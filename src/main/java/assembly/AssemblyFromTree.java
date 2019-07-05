package assembly;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.hadoop.io.Text;
import alignment.PairSeqOverlap;
import graph.Vertex;
import graph.WeightDiEdge;
import io.Input;
import kmer.Kmer;
import kmer.OlcKmer;
import tree.OLCTree;
import tree.TreeNode;

public class AssemblyFromTree {
	private List<Vertex> raw_seq;
	
	public AssemblyFromTree(String inputfile) {
		try {
			this.raw_seq = Input.readFastaFile(new File(inputfile));
		} catch (IOException e) {
			throw new RuntimeException("Error: read file , " + inputfile);
		}
	}
	
	public <T extends Collection<String>> AssemblyFromTree(T seqs) {
		this.raw_seq = Input.readFastaStream(seqs);
	}
	
	public AssemblyFromTree(File input) {
		try {
			this.raw_seq = Input.readFastaFile(input);
		} catch (IOException e) {
			throw new RuntimeException("Error: read file , " + input.toString());
		}
	}
	
	public AssemblyFromTree(String[] seqs) {
		this.raw_seq = Input.readFastaStream(seqs);
	}
	
	public AssemblyFromTree(Iterable<Text> seqs) {
		this.raw_seq = Input.readFastqStream(seqs);
	}
	
	
	public List<Vertex> getRaw_seq() {
		return raw_seq;
	}

	public void setRaw_seq(List<Vertex> raw_seq) {
		this.raw_seq = raw_seq;
	}


	/**
	 * Run OLC Assembly 
	 * @param cutoff_overlap_len Minimum number of overlap of pair sequences
	 * @param rate_mismatch	Rate of mismatch for finding overlap
	 * @param seq_type Type of sequences, "long": write out single sequences after assembly; "short": remove single sequences after assembly.
	 * @return MutablePair: left->contigs; right->paths
	 * @throws IOException
	 */
	public  MutablePair<Map<Integer,String>,Map<Integer,String>> 
		runAssembly(int cutoff_overlap_len, double rate_mismatch, String seq_type) throws IOException {
		
		/*
		 * find all overlap
		 */
		//MutablePair<Map<String, WeightDiEdge>, Map<String, Integer>> all = findAllOverlap(raw_seq, cutoff_overlap_len, rate_mismatch);
		MutablePair<Map<String, WeightDiEdge>, Map<String, Integer>> all = KmerToAllOverlapForTree(raw_seq, cutoff_overlap_len, rate_mismatch);
		Map<String, WeightDiEdge> allEdges = all.left;
		Map<String, Integer> allNodes = all.right;
		
		OLCTree tree = createOLCTree(allEdges, allNodes);
		List<TreeNode> roots = tree.getRoots();
		System.out.println("roots num: " + roots.size());
		for(TreeNode root : roots) {
			System.out.println("root id : " + root.getSelfId());
			for(TreeNode junior : root.getJuniors()) {
				System.out.println(junior.getSelfId());
			}
			root.traverse();
		}
	
		return new MutablePair<Map<Integer, String>, Map<Integer, String>>();
	}


	/**
	 * Find best one overlap of each seqB(has shared kmer start position is 0) from shared kmer
	 * 
	 * @param raw_seq All sequences
	 * @param cutoff_overlap_len Minimum length of overlap
	 * @param rate_mismatch Maxmum rate of mismatches
	 * @return All edges and nodes for tree
	 */
	public MutablePair<Map<String,WeightDiEdge>,Map<String,Integer>>
		KmerToAllOverlapForTree(List<Vertex> raw_seq, int cutoff_overlap_len, double rate_mismatch) {
		
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
			
				/*
				 * for each seqB, save best alignment one (max alignment_score)
				 */
				WeightDiEdge best_new_edge = null;
				int best_alinment_score = 0;
				
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
							String filterA = String.join("", Collections.nCopies((cutoff_overlap_len-1),"A"));
							String filterT = String.join("", Collections.nCopies((cutoff_overlap_len-1),"T"));
							if(pairoverlap.OverlapSeq().endsWith(filterT) || 
									pairoverlap.OverlapSeq().endsWith(filterA) ||
									pairoverlap.OverlapSeq().startsWith(filterT) ||
									pairoverlap.OverlapSeq().startsWith(filterA) ) //filter polyA or polyT
								continue; 
							
							WeightDiEdge new_edge = new WeightDiEdge(raw_seq.get(A), raw_seq.get(B), pos.right, B_one.right, pairoverlap);
							if(pairoverlap.getAlignment_score() >= best_alinment_score) {
								best_alinment_score = pairoverlap.getAlignment_score();
								best_new_edge = new_edge;
							}
						
							break;  // next sequence A
						}
					}
				}
				
				if(best_new_edge == null) { // have not any seqA can overlap this seqB
					continue;
				}
				String edge_id = best_new_edge.From() + " " + best_new_edge.To();
				overlapEdges.put(edge_id, best_new_edge);
				allNodes.put(best_new_edge.From(), 1);
				allNodes.put(best_new_edge.To(), 1);
				
			}
		}
		MutablePair<Map<String, WeightDiEdge>, Map<String, Integer>> 
			res = new MutablePair<Map<String, WeightDiEdge>, Map<String, Integer>>(overlapEdges, allNodes);
		return res;
	}

	public OLCTree createOLCTree(Map<String, WeightDiEdge> allEdges, Map<String, Integer> allNodes) {
		OLCTree olcTree = new OLCTree();
		olcTree.addTreeNodesFromEdge(allEdges);
		olcTree.createTree();
		return olcTree;
	}
}
