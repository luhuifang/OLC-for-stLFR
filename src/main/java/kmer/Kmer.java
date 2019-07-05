package kmer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.MutablePair;

public class Kmer {
	
	protected String kmer;
	protected int length;
	
	/*
	 * save kmers whose start == 0
	 * Map<String, List<Mutable<boolean, int>>>
	 * key: read_name
	 * value: [(start, ori), (start, ori) ... ]
	 */
	protected Map<Integer,List<MutablePair<Integer,Boolean>>> kmer_pos;
	
	/*
	 * save kmers whose start > 0
	 * Map<String, List<Mutable<boolean, int>>>
	 * key: read_name
	 * value: [(start, ori), (start, ori) ... ] 
	protected Map<String, List<MutablePair<Integer, Boolean>>> other_pos; 
	*/
	
	/*
	 * true if start==0
	 */
	protected boolean potential_overlap;
	
	public Kmer(String kmer) {
		this(kmer, kmer.length());
	}
	
	/**
	 * Create a new kmer backed by the bases in bases, spanning start -> start + length
	 * 
	 * Under no circumstances can bases be modified anywhere in the client code. This does not make a copy of bases for performance reasons
	 * 
	 * @param bases an array of bases
	 * @param length the length of the kmer. Must be >= 0 and start + length < bases.length
	 */
	public Kmer(final String bases,final int length) {
		if(bases == null) throw new IllegalArgumentException("bases cannot be null");
		if(length <0) throw new IllegalArgumentException("length must be >=0 but got " + length);
		this.kmer = bases;
		this.length = length;
		this.potential_overlap = false;
		this.kmer_pos = new TreeMap<Integer, List<MutablePair<Integer, Boolean>>>();
		//this.other_pos = new TreeMap<String, List<MutablePair<Integer, Boolean>>>();
	}
	
	public boolean addPos(int source, int start) {
		return addPos(source, true, start);
	}
	
	/**
	 * Save each kmers
	 * @param source Seq_name or seq_id (int)
	 * @param forward Ori of kmer
	 * @param start Start position of kmer from source
	 * @return True: save successful; False: save failed.
	 */
	public boolean addPos(int source, Boolean forward, int start) {
		if(start == 0) { //kmer start 0 from source
			this.potential_overlap = true;
		}
		
		if(!kmer_pos.containsKey(source)) {
			ArrayList<MutablePair<Integer, Boolean>> list = new ArrayList<MutablePair<Integer, Boolean>>();
			kmer_pos.put(source, list);
		}
		MutablePair<Integer, Boolean> new_pair = new MutablePair<Integer, Boolean>(start, forward);
		return kmer_pos.get(source).add(new_pair);
	}

	public String getKmer() {
		return kmer;
	}

	public void setKmer(String kmer) {
		this.kmer = kmer;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	public boolean isPotentialOverlap() {
		return this.potential_overlap;
	}
	
	public Map<Integer, List<MutablePair<Integer, Boolean>>> getKmer_pos() {
		return kmer_pos;
	}

	public void setKmer_pos(Map<Integer, List<MutablePair<Integer, Boolean>>> kmer_pos) {
		this.kmer_pos = kmer_pos;
	}
	
	/**
	 * Get list of reads and ori which have shared kmers start position is 0 (seq_B) 
	 * @return ArrayList of reads and ori, [(read_id, ori), (read_id, ori), ...]
	 */
	public List<MutablePair<Integer, Boolean>> firstKmerReads() {
		List<MutablePair<Integer, Boolean>> reads = new ArrayList<MutablePair<Integer, Boolean>>();
		for(Entry<Integer, List<MutablePair<Integer, Boolean>>> entry : this.kmer_pos.entrySet()) {
			if(entry.getValue().get(0).left == 0) {
				MutablePair<Integer, Boolean> new_one = new MutablePair<Integer, Boolean>(entry.getKey(), entry.getValue().get(0).right);
				reads.add(new_one);
			}
		}
		return reads;
	}
}
