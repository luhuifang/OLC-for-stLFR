package kmer;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import graph.Vertex;



/*
 * Split reads to kmers for finding overlap
 */
public class OlcKmer <V extends Collection<Vertex>>{
	
	protected V list;
	
	public OlcKmer(V list) {
		this.list = list;
	}
	
	/*
	 * split kmer for finding overlap in OLC assembly
	 */
	/**
	 * Get all kmers from reads(contigs) 
	 * @param K Length of kmer
	 * @return {kmer, <object Kmer>}
	 */
	public Map<String, Kmer> getOLCKmers(int K) {
		Map<String, Kmer> olc_kmers = new TreeMap<String, Kmer>();
		for (Vertex l : list) {
			addKmers(l.getSeq(), l.getNode_id(), true, K, olc_kmers);
			addKmers(l.revComSeq(), l.getNode_id(), false, K, olc_kmers);
		}
		return olc_kmers;
	}
	
	/**
	 * Split sequence to kmers and add to olc_kmers
	 * @param seq Sequence to split
	 * @param source Contig_id(read_id) the sequence come from  
	 * @param K Length of kmer
	 * @param olc_kmers Save result {kmer, <object Kmer>}
	 */
	private void addKmers(StringBuffer seq , int source, boolean forward, int K, Map<String, Kmer> olc_kmers) {
		if(seq.length() == 0) throw new IllegalArgumentException("seq is empty");
		if(K < 0) throw new IllegalArgumentException("K must be >=0 but got " + K);
		if(K > seq.length()) throw new IllegalArgumentException("K must be <= seq_len, but K : " +  K + " > seqlen: " + seq.length());
		
		for(int i = 0; i <= seq.length()-K; i++) {
			String kmer = seq.substring(i, i+K);
			if(!olc_kmers.containsKey(kmer)) {
				olc_kmers.put(kmer, new Kmer(kmer, K));
			}
			olc_kmers.get(kmer).addPos(source, forward, i);
		}
	}

}
