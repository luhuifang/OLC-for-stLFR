package alignment;

import graph.Vertex;

/**
 * 
 * @author luhuifang
 *
 */
public class PairNodeOverlap {
	
	private Vertex first;
	private Vertex second;
	
	private String firstOri;
	private String secondOri;
	
	private double rate_mismatch;
	private int align_start ;
	private int overlap_len;
	private int mismatch_num;	
	
	public PairNodeOverlap(Vertex node1, Vertex node2) {
		this.first = node1;
		this.second = node2;
		this.firstOri = "+";
		this.secondOri = "+";
		this.rate_mismatch = 0;
		init();
		findBestOverlap(node1, node2);
	}
	
	public PairNodeOverlap(Vertex node1, Vertex node2, double num_mismatch) {
		this.first = node1;
		this.second = node2;
		this.firstOri = "+";
		this.secondOri = "+";
		this.rate_mismatch = num_mismatch;
		init();
		findBestOverlap(node1, node2);
	}
	
	private void init() {
		this.align_start = -1;
		this.overlap_len = 0;
		this.mismatch_num = 0;
	}
	
	public int getAlign_start() {
		return align_start;
	}

	public void setAlign_start(int align_start) {
		this.align_start = align_start;
	}

	public int getOverlap_len() {
		return overlap_len;
	}

	public void setOverlap_len(int overlap_len) {
		this.overlap_len = overlap_len;
	}

	public int getMismatch_num() {
		return mismatch_num;
	}

	public void setMismatch_num(int mismatch_num) {
		this.mismatch_num = mismatch_num;
	}
	
	public Vertex getFirst() {
		return first;
	}

	public void setFirst(Vertex first) {
		this.first = first;
	}

	public Vertex getSecond() {
		return second;
	}

	public void setSecond(Vertex second) {
		this.second = second;
	}

	public String getFirstOri() {
		return firstOri;
	}

	public void setFirstOri(String firstOri) {
		this.firstOri = firstOri;
	}

	public String getSecondOri() {
		return secondOri;
	}

	public void setSecondOri(String secondOri) {
		this.secondOri = secondOri;
	}

	private void findBestOverlap(Vertex node1, Vertex node2) {
		StringBuffer seq1 = node1.getSeq();
		StringBuffer seq2 = node2.getSeq();
		StringBuffer rev_seq2 = node2.revComSeq();
		
		if(commonSubString(seq1, seq2)) {
			set(node1, node2, "+", "+");
		}
		if(commonSubString(seq2, seq1)) {
			set(node2, node1, "+", "+");
		}
		if(commonSubString(seq1, rev_seq2)) {
			set(node1, node2, "+", "-");
		}
		if(commonSubString(rev_seq2, seq1)) {
			set(node2, node1, "-", "+");
		}
	}
	
	private void set(Vertex f, Vertex r, String orif, String orir) {
		setFirst(f);
		setSecond(r);
		setFirstOri(orif);
		setSecondOri(orir);
	}
	
	private boolean commonSubString(StringBuffer seqM, StringBuffer seqN) {
		int lenSeqM = seqM.length();
		int lenSeqN = seqN.length();
		
		int cutoff_mismatch = 0;
		if(rate_mismatch != 0) {
			cutoff_mismatch = (int) ((lenSeqM + lenSeqN)*rate_mismatch + 1);
		}
		
		for(int i =0 ; i<lenSeqM; i++) {
			int j = 0;
			int start = i;
			int len_overlap = 0;
			int mismatch = 0;
			while(i < lenSeqM && j < lenSeqN) {
				if(seqM.charAt(i) != seqN.charAt(j)) {
					mismatch += 1;
					if (mismatch > cutoff_mismatch) {
						break;
					}
				}
				len_overlap += 1;					
				i += 1;
				j += 1;
			}
			
			if (i == lenSeqM){
				if (this.overlap_len < len_overlap) {
					setAlign_start(start);
					setOverlap_len(len_overlap);
					setMismatch_num(mismatch);
					return true;
				}
				break;
			}
		}
		
		return false;
	}
	
	public int compareTo(PairNodeOverlap other) {
		return this.overlap_len - other.getOverlap_len();	
	}

	@Override
	public String toString() {
		return this.first.getNode_id() + "\t" + this.firstOri + "\t" +this.second.getNode_id() + "\t" + this.secondOri 
				+ "\t" + this.align_start + "\t" + this.overlap_len;
	}
	
	
}
