package assembly;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.hadoop.io.Text;
import alignment.PairNodeOverlap;
import alignment.PairSeqOverlap;
import graph.EdgeWeightedDiGraph;
import graph.Vertex;
import graph.WeightDiEdge;
import io.Input;
import kmer.Kmer;
import kmer.OlcKmer;
import searchGraph.DijkstraSP;


public class UmiAssembly {
	
	private List<Vertex> raw_seq;
	
	public UmiAssembly(String inputfile) {
		try {
			this.raw_seq = Input.readFastaFile(new File(inputfile));
		} catch (IOException e) {
			throw new RuntimeException("Error: read file , " + inputfile);
		}
	}
	
	public <T extends Collection<String>> UmiAssembly(T seqs) {
		this.raw_seq = Input.readFastaStream(seqs);
	}
	
	public UmiAssembly(File input) {
		try {
			this.raw_seq = Input.readFastaFile(input);
		} catch (IOException e) {
			throw new RuntimeException("Error: read file , " + input.toString());
		}
	}
	
	public UmiAssembly(String[] seqs) {
		this.raw_seq = Input.readFastaStream(seqs);
	}
	
	public UmiAssembly(Iterable<Text> seqs) {
		this.raw_seq = Input.readFastqStream(seqs);
	}
	
	
	public List<Vertex> getRaw_seq() {
		return raw_seq;
	}

	public void setRaw_seq(List<Vertex> raw_seq) {
		this.raw_seq = raw_seq;
	}
	
	private void filterRawSeq() {
		
		List<Vertex> raws = this.raw_seq;
		this.raw_seq = new ArrayList<Vertex>();
		for(Vertex v : raws) {
			if(! raw_seq.contains(v)) {
				v.setNode_id(raw_seq.size());
				raw_seq.add(v);
			}
		}
		
		/*
		Set<Vertex> seqSet = new HashSet<>(this.getRaw_seq());
		this.raw_seq = new ArrayList<>(seqSet);
		*/
	}
	
	public MutablePair<Map<Integer, String>, Map<Integer, String>> run(int cutoff_overlap_len, double rate_mismatch, String seq_type) throws IOException {
		
		if ("short".equals(seq_type)) {
			Map<Integer, String> contigs = runAssembly(cutoff_overlap_len, 0, seq_type).left;
			
			Collection<String> set_contig = contigs.values();
			UmiAssembly new_ass = new UmiAssembly(set_contig); 
			return new_ass.runAssembly(cutoff_overlap_len, rate_mismatch, "long");
		}else {
			return runAssembly(cutoff_overlap_len, rate_mismatch, seq_type);
		}
	}
	
	
	/**
	 * Run OLC Assembly after filter
	 * @param cutoff_overlap_len Minimum number of overlap of pair sequences
	 * @param rate_mismatch	Rate of mismatch for finding overlap
	 * @param seq_type Type of sequences, "long": write out single sequences after assembly; "short": remove single sequences after assembly.
	 * @return MutablePair: left->contigs; right->paths
	 */
	public MutablePair<Map<Integer, String>, Map<Integer, String>> runAssemblyAfterFilter
			(int cutoff_overlap_len, double rate_mismatch, String seq_type) {
		return assembly(cutoff_overlap_len, rate_mismatch, seq_type, true);
	}
	
	/**
	 * Run OLC Assembly not filter
	 * @param cutoff_overlap_len Minimum number of overlap of pair sequences
	 * @param rate_mismatch	Rate of mismatch for finding overlap
	 * @param seq_type Type of sequences, "long": write out single sequences after assembly; "short": remove single sequences after assembly.
	 * @return MutablePair: left->contigs; right->paths
	 */
	public  MutablePair<Map<Integer,String>,Map<Integer,String>> 
			runAssembly(int cutoff_overlap_len, double rate_mismatch, String seq_type) {
		return assembly(cutoff_overlap_len, rate_mismatch, seq_type, false);
	}
	
	
	/**
	 *Run OLC Assembly 
	 * @param cutoff_overlap_len Minimum number of overlap of pair sequences
	 * @param rate_mismatch	Rate of mismatch for finding overlap
	 * @param seq_type Type of sequences, "long": write out single sequences after assembly; "short": remove single sequences after assembly.
	 * @param filter Filter duplication(true) or not(false)
	 * @return MutablePair: left->contigs; right->paths
	 */
	protected MutablePair<Map<Integer,String>,Map<Integer,String>> assembly
			(int cutoff_overlap_len, double rate_mismatch, String seq_type, boolean filter) {
		
		if(filter) filterRawSeq();
		
		/*
		 * find all overlap
		 */
		//MutablePair<Map<String, WeightDiEdge>, Map<String, Integer>> all = findAllOverlap(raw_seq, cutoff_overlap_len, rate_mismatch);
		MutablePair<Map<String, WeightDiEdge>, Map<String, Integer>> all = KmerToAllOverlap(raw_seq, cutoff_overlap_len, rate_mismatch);
		Map<String, WeightDiEdge> allEdges = all.left;
		Map<String, Integer> allNodes = all.right;
		Set<String> nodes = allNodes.keySet();
		
		/*
		 * construct EdgeWeightedDiGraph
		 */
		EdgeWeightedDiGraph<WeightDiEdge> graph = constructEdgeWeightedDiGraph(nodes, allEdges);
	
		/*
		 * find all paths from graph
		 */
		List<List<WeightDiEdge>> allPaths = findAllPaths(graph, "DijkstraSP");
		
		/*
		 * paths to contings
		 */
		MutableTriple<Map<Integer,String>,Map<Integer,String>,List<Integer>> res = constructContig(allPaths);
		Map<Integer, String> contigs = res.left;
		Map<Integer, String> contig_path = res.middle;
		List<Integer> contig_node = res.right;
		
		/*
		for(Entry<Integer, String> contig : contigs.entrySet()) {
			System.out.println(">" + contig.getKey() + "\t len: " + contig.getValue().length());
			System.out.println(contig.getValue());
		}
		/*
		 * if long sequences for assembly, save single path 
		 */
		if("long".equals(seq_type)) {
			singleNodeToContig(contig_node, contigs, contig_path);
		}else if (! "short".equals(seq_type)){
			throw new IllegalArgumentException("Seq_type must be [short | long]");
		}
		
		return new MutablePair<Map<Integer, String>, Map<Integer, String>>(contigs, contig_path);
	}
	
	
	/**
	 * Find all overlaps from shared kmers
	 * @param raw_seq All sequences
	 * @param cutoff_overlap_len Minimum length of overlap
	 * @param rate_mismatch Maxmum rate of mismatches
	 * @return All edges and nodes for graph
	 */
	public MutablePair<Map<String,WeightDiEdge>,Map<String,Integer>> 
		KmerToAllOverlap(List<Vertex> raw_seq, int cutoff_overlap_len, double rate_mismatch) {
		
		Map<String, WeightDiEdge> overlapEdges = new TreeMap<String, WeightDiEdge>();
		Map<String, Integer> allNodes = new TreeMap<String, Integer>();
		
		OlcKmer<List<Vertex>> olcKmer = new OlcKmer<List<Vertex>>(raw_seq);
		Map<String, Kmer> olcKmers = olcKmer.getOLCKmers(cutoff_overlap_len);
		
		//each kmer
		for(Kmer kmer : olcKmers.values()) { 
			if(!kmer.isPotentialOverlap()) continue;
			if(kmer.getKmer_pos().size() <= 0) continue;
			
			/*
			 * Potential seqB which kmer start == 0
			 * MutablePair:
			 * 	left: read_id
			 * 	right: true(forward) or false(reverse)
			 */
			List<MutablePair<Integer,Boolean>> reads_B = kmer.firstKmerReads(); 	//list of seq_B
			for(MutablePair<Integer, Boolean> B_one : reads_B) { //for each seqB, which start == 0
			
				Integer B = B_one.left; //read_id of B
				StringBuffer seqB = raw_seq.get(B).getSequence(B_one.right); // seq of B
				
				/*
				 * Positions of this kmer in echo sequences
				 * 
				 * Entry: (one sequence)
				 * 	key = read_id
				 * 	value = [(start_pos, ori), (start_pos, ori), ...]
				 */
				for(Entry<Integer, List<MutablePair<Integer, Boolean>>> pos_entry : kmer.getKmer_pos().entrySet()) { 
					Integer A = pos_entry.getKey(); //id of seqA
					if(A == B) continue; // same read id of seqB
					
					/*
					 * echo position for one kmer
					 * MutablePair:
					 * 	left: start_pos
					 * 	right: true(forward) or false(reverse)
					 */
					for(MutablePair<Integer, Boolean> pos : pos_entry.getValue()) { 
						StringBuffer seqA = raw_seq.get(A).getSequence(pos.right); //seq of A
						
						//find overlap of seqA and seqB
						PairSeqOverlap pairoverlap = new PairSeqOverlap(seqA, seqB, pos.left, 0, rate_mismatch);
		
						if(pairoverlap.isOlcOverlap()) {
							String filterA = String.join("", Collections.nCopies((cutoff_overlap_len-2),"A"));
							String filterT = String.join("", Collections.nCopies((cutoff_overlap_len-2),"T"));
							if(pairoverlap.OverlapSeq().endsWith(filterT) || 
									pairoverlap.OverlapSeq().endsWith(filterA) ||
									pairoverlap.OverlapSeq().startsWith(filterT) ||
									pairoverlap.OverlapSeq().startsWith(filterA) ) //filter polyA or polyT
								continue; 
							
							WeightDiEdge new_edge = new WeightDiEdge(raw_seq.get(A), raw_seq.get(B), pos.right, B_one.right, pairoverlap);
							
							String edge_id = new_edge.From() + " " + new_edge.To();
							overlapEdges.put(edge_id, new_edge);
							allNodes.put(new_edge.From(), 1);
							allNodes.put(new_edge.To(), 1);
													
							break;  // next sequence A
						}
					}
				}
			}
		}
		MutablePair<Map<String, WeightDiEdge>, Map<String, Integer>> 
			res = new MutablePair<Map<String, WeightDiEdge>, Map<String, Integer>>(overlapEdges, allNodes);
		return res;
	}
	
	/**
	 * Find overlap for each pair sequences.
	 * @param raw_seq All sequences
	 * @param cutoff_overlap_len Minimum length of overlap
	 * @param rate_mismatch Maxmum rate of mismatchs
	 * @return All edges and nodes for graph
	 */
	public MutablePair<Map<String, WeightDiEdge>, Map<String, Integer>> 
			findAllOverlap(List<Vertex> raw_seq, int cutoff_overlap_len, double rate_mismatch) {
		
		Map<String, WeightDiEdge> overlapEdges = new TreeMap<String, WeightDiEdge>();
		Map<String, Integer> allNodes = new TreeMap<String, Integer>();
		
		for(int i = 0; i < raw_seq.size(); i++) {
			for(int j = i+1; j < raw_seq.size(); j++) {
				Vertex nodeM = raw_seq.get(i);
				Vertex nodeN = raw_seq.get(j);
				
				PairNodeOverlap pairOverlap = new PairNodeOverlap(nodeM, nodeN, rate_mismatch);
				
				if(pairOverlap.getOverlap_len() >= cutoff_overlap_len) {
					WeightDiEdge new_edge = new WeightDiEdge(pairOverlap);
					String edge_id = new_edge.From() + " "  + new_edge.To();
					overlapEdges.put(edge_id, new_edge);
					allNodes.put(new_edge.From(), 1);
					allNodes.put(new_edge.To(), 1);
					
					WeightDiEdge rev_new_edge = new_edge.revEdge();
					String rev_edge_id = rev_new_edge.From() + " " + rev_new_edge.To();
					overlapEdges.put(rev_edge_id, rev_new_edge);
					allNodes.put(rev_new_edge.From(), 1);
					allNodes.put(rev_new_edge.To(), 1);
				}	
				
			}
		}
		MutablePair<Map<String, WeightDiEdge>, Map<String, Integer>> 
			res = new MutablePair<Map<String, WeightDiEdge>, Map<String, Integer>>(overlapEdges, allNodes);
		return res;
	}
	
	/**
	 * Construct edge weighted digraph
	 * @param allNodes Vertexs of graph
	 * @param allEdges Edges of graph
	 * @return EdgeWeightedDiGraph object
	 */
	public EdgeWeightedDiGraph<WeightDiEdge> 
			constructEdgeWeightedDiGraph(Set<String> allNodes, Map<String, WeightDiEdge> allEdges) {
		
		EdgeWeightedDiGraph<WeightDiEdge> graph = new EdgeWeightedDiGraph<WeightDiEdge>(allNodes);
		for(WeightDiEdge edge : allEdges.values()) {
			graph.addEdge(edge);
		}
		return graph;
	}
	
	/**
	 * Find all paths from graph
	 * @param graph EdgeWeightedDiGraph object
	 * @param method Method of search graph "DijkstraSP" or "DepthFirst"
	 * @return A array list of paths
	 */
	public List<List<WeightDiEdge>> 
			findAllPaths(EdgeWeightedDiGraph<WeightDiEdge> graph, String method) {
		
		List<String> rootNodes = graph.findNodesWithIndegree(0);
		
		List<List<WeightDiEdge>> allPaths = new ArrayList<List<WeightDiEdge>>();
		if("DijkstraSP".equals(method)) {
			DijkstraSP<WeightDiEdge> search = new DijkstraSP<WeightDiEdge>(graph, rootNodes);
			allPaths = search.allPaths();
		}
		
		return allPaths;
	}
	
	/**
	 * Construct contigs from paths
	 * @param allpaths A list of all paths
	 * @return Contigs : sequences of contigs (Map: contigs_id => contig_seq);
	 *			Contig_path : name of reads contain in each contig (Map: contigs_id => reads_name_list);
	 *			Contig_node : id of node used in construct contigs, nonredundant (List: node_id).
	 */
	public  MutableTriple<Map<Integer,String>,Map<Integer,String>,List<Integer>> 
				constructContig(List<List<WeightDiEdge>> allpaths) {
		
		int count = 0;
		Map<Integer, String> contig_path = new TreeMap<Integer, String>();
		List<Integer> contig_node = new ArrayList<Integer>();
		Map<Integer, String> contigs = new TreeMap<Integer, String>();
		
		/*
		 * List: [WeightDiEdge, WeightDiEdge, ...]
		 */
		for(List<WeightDiEdge> path : allpaths) {
			/*
			 * consensus for saving contig seq
			 */
			Map<Integer, Map<Character, Integer>> consensus = new TreeMap<Integer, Map<Character,Integer>>();
			List<Integer> refArray = new ArrayList<Integer>();
			int start = -1;
			
			StringBuffer contigPath = new StringBuffer();
			
			for(WeightDiEdge edge : path) {
				StringBuffer fromSeq = edge.getFromSeq();
				StringBuffer toSeq = edge.getToSeq();
				
				String from_name = edge.getSource().getNode_name() + edge.getSource_ori();
				String to_name = edge.getTarget().getNode_name() + edge.getTarget_ori();
				
				int source_start = edge.getSource_start();
				
				if(start == -1) { //start of contig
					refArray = appendConsensus(consensus, fromSeq.toString(), 0, refArray);
					contigPath.append(from_name + "\n");
					contig_node.add(edge.getSource().getNode_id());
				}
				start = source_start;
				refArray = appendConsensus(consensus, toSeq.toString(), start, refArray);
				contigPath.append(to_name + "\n");
				contig_node.add(edge.getTarget().getNode_id());
			}
			
			contig_path.put(count, contigPath.toString());
			String contig = getConsensus(consensus);
			contigs.put(count, contig);
			count += 1;
		}
		
		return new MutableTriple<Map<Integer, String>, Map<Integer, String>, List<Integer>>(contigs, contig_path, contig_node);
	}
	
	private String getConsensus(Map<Integer, Map<Character, Integer>> consensus) {
		StringBuffer consensus_list = new StringBuffer();
		for(Map<Character, Integer> v : consensus.values()) {
			int maxBaseValue = 0 ;
			char maxBase = 0 ;
			//maxbase
			for( Entry<Character, Integer> entry : v.entrySet()) {
				if(entry.getValue() >= maxBaseValue) {
					maxBaseValue = entry.getValue();
					maxBase = entry.getKey();
				}
			}
			consensus_list.append(maxBase);
		}
		return consensus_list.toString();
	}

	private List<Integer> appendConsensus(
			Map<Integer, Map<Character, Integer>> consensus, String seq, int start, List<Integer> refArray) {
		
		List<Integer> contig = new ArrayList<Integer>();
		int i = start;
		for(char base : seq.toCharArray()) { //each base
			if(base != '-') {
				int pos;
				if(i < refArray.size()) {
					pos = refArray.get(i);
				}else {
					if(contig.size() == 0) {
						pos = 0;
					}else {
						pos = contig.get(contig.size()-1) + 1;
					}
				}
				contig.add(pos);
				if (!consensus.containsKey(pos)) {
					consensus.put(pos, new TreeMap<Character, Integer>());
					consensus.get(pos).put('A', 0);
					consensus.get(pos).put('T', 0);
					consensus.get(pos).put('C', 0);
					consensus.get(pos).put('G', 0);
					consensus.get(pos).put('N', 0);
				}
				
				Integer num = consensus.get(pos).get(base) + 1;
				consensus.get(pos).put(base, num);
			}
			i += 1;
		}
		
		return contig;
	}

	private void singleNodeToContig(List<Integer> contig_node, Map<Integer, String> contigs, Map<Integer, String> contig_path) {
		int count = contigs.size();
		
		for(int id =0; id < raw_seq.size(); id ++) {
			if(contig_node.contains(id)) {
				continue;
			}
			contigs.put(count, raw_seq.get(id).getSeq().toString());
			contig_path.put(count, raw_seq.get(id).getNode_name());
			count += 1;
		}
	}
}
