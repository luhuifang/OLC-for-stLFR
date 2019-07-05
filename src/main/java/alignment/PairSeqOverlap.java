package alignment;

/**
 * 
 * @author luhuifang
 * @version v1.0
 *
 */
public class PairSeqOverlap {
	
	private StringBuffer seqA;
	private StringBuffer seqB;
	private StringBuffer overlapSeq;
	
	private int align_start;
	private int overlap_len;
	private int mismatch_num;
	
	/*
	 * alignment score of overlap,
	 * as = match_num*match_penalty + mismatch_num*mismatch_penalty + gap_num*gap_penalty
	 */
	private int alignment_score; // deafult = 0
	//private int match_penalty; // default = 5
	//private int mismatch_penalty; // default = -5
	//private int gap_penalty; //default = 0
	
	private boolean isoverlap; //is available overlap
	
	public PairSeqOverlap(StringBuffer seqA, StringBuffer seqB) {
		this.findOverlap(seqA, seqB, 0, 0, 0, 5, -5, 0);
	}
	
	public PairSeqOverlap(StringBuffer seqA, StringBuffer seqB, int startA) {
		this.findOverlap(seqA, seqB, startA, 0, 0, 5, -5, 0);
	}
	
	public PairSeqOverlap(StringBuffer seqA, StringBuffer seqB, int startA, int startB) {
		this.findOverlap(seqA, seqB, startA, startB, 0, 5, -5, 0);
	}
	
	public PairSeqOverlap(StringBuffer seqA, StringBuffer seqB, double rate) {
		this.findOverlap(seqA, seqB, 0, 0, rate, 5, -5, 0);
	}
	
	public PairSeqOverlap(StringBuffer seqA, StringBuffer seqB, int startA, int startB, double rate) {
		this.findOverlap(seqA, seqB, startA, startB, rate, 5, -5, 0);
	}
	
	public PairSeqOverlap(StringBuffer seqA, StringBuffer seqB,
							int match_penalty, int mismatch_penalty, int gap_penalty) {
		this.findOverlap(seqA, seqB, 0, 0, 0, match_penalty, mismatch_penalty, gap_penalty);
	}
	
	public PairSeqOverlap(StringBuffer seqA, StringBuffer seqB, int startA, 
							int match_penalty, int mismatch_penalty, int gap_penalty) {
		this.findOverlap(seqA, seqB, startA, 0, 0, match_penalty, mismatch_penalty, gap_penalty);
	}
	
	public PairSeqOverlap(StringBuffer seqA, StringBuffer seqB, int startA, int startB, 
							int match_penalty, int mismatch_penalty, int gap_penalty) {
		this.findOverlap(seqA, seqB, startA, startB, 0, match_penalty, mismatch_penalty, gap_penalty);
	}
	
	public PairSeqOverlap(StringBuffer seqA, StringBuffer seqB, double rate, 
							int match_penalty, int mismatch_penalty, int gap_penalty) {
		this.findOverlap(seqA, seqB, 0, 0, rate, match_penalty, mismatch_penalty, gap_penalty);
	}
	
	public PairSeqOverlap(StringBuffer seqA, StringBuffer seqB, int startA, int startB, double rate, 
							int match_penalty, int mismatch_penalty, int gap_penalty) {
		this.findOverlap(seqA, seqB, startA, startB, rate, match_penalty, mismatch_penalty, gap_penalty);
	}
	
	/**
	 * Initialize PairSeqOverlap object
	 * 
	 * @param seqA First sequence for overlap
	 * @param seqB Second sequence for overlap
	 * @param A_start Start of seqA for finding overlap
	 * @param B_start Start of seqB for finding overlap
	 * @param rate_mismatch Maxmum rate of mismatchs in finding overlap
	 */
	private void findOverlap(final StringBuffer seqA, final StringBuffer seqB, int A_start, int B_start, double rate_mismatch,
								int match_penalty, int mismatch_penalty, int gap_penalty) {
		if(seqA==null || seqB==null) throw new IllegalArgumentException("Sequnece must not be null");
		if(A_start < 0 || B_start < 0) 
			throw new IllegalArgumentException("Start of seqA or seqB must be >=0, but A_start = " + A_start + "; B_start = " + B_start);
		if(rate_mismatch < 0)
			throw new IllegalArgumentException("Rate of mismatchs must be >=0, but rate_mismatch = " + rate_mismatch);
		
		this.seqA = seqA;
		this.seqB = seqB;
		this.overlapSeq = new StringBuffer();
		
		this.alignment_score = 0;
		//this.match_penalty = match_penalty;
		//this.mismatch_penalty = mismatch_penalty;
		//this.gap_penalty = gap_penalty;
		
		if(findCommonSubString(seqA, seqB, A_start, B_start, rate_mismatch, match_penalty, mismatch_penalty, gap_penalty)) 
			setIsoverlap(true);
		else
			setIsoverlap(false);
		
	}

	public StringBuffer getSeqA() {
		return seqA;
	}

	public void setSeqA(StringBuffer seqA) {
		this.seqA = seqA;
	}

	public StringBuffer getSeqB() {
		return seqB;
	}

	public void setSeqB(StringBuffer seqB) {
		this.seqB = seqB;
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

	public void setIsoverlap(boolean isoverlap) {
		this.isoverlap = isoverlap;
	}

	public int getMismatch_num() {
		return mismatch_num;
	}

	public void setMismatch_num(int mismatch_num) {
		this.mismatch_num = mismatch_num;
	}
	
	public int getAlignment_score() {
		return alignment_score;
	}

	public void setAlignment_score(int alignment_score) {
		this.alignment_score = alignment_score;
	}

	public boolean isOlcOverlap() {
		return this.isoverlap;
	}
	
	public String OverlapSeq() {
		return this.overlapSeq.toString();
	}
	
	/**
	 * Find common string of pair sequences(seqA and seqB)
	 * 
	 * @param seqM	First sequence
	 * @param seqN	second sequence
	 * @param A_start Start of first sequence for finding overlap
	 * @param B_start Start of second sequence for finding overlap
	 * @param rate_mismatch Maxmum rate of mismatches in finding overlap
	 * @param match_penalty 
	 * @param mismatch_penalty
	 * @param gap_penalty
	 * @return Return is OLC overlap(true) or not(false)
	 */
	private boolean findCommonSubString(StringBuffer seqM, StringBuffer seqN, 
										int A_start, int B_start, double rate_mismatch,
										int match_penalty, int mismatch_penalty, int gap_penalty) {
		int lenSeqM = seqM.length();
		int lenSeqN = seqN.length();
		
		int cutoff_mismatch = 0;
		if(rate_mismatch != 0) {
			cutoff_mismatch = (int) ((lenSeqM + lenSeqN)*rate_mismatch + 1);
		}
		
		int i = A_start;
		int j = B_start;
		
		this.align_start = A_start;
		this.overlap_len = 0;
		this.mismatch_num = 0;
		while(i < lenSeqM && j < lenSeqN && this.mismatch_num <= cutoff_mismatch) {
			this.overlapSeq.append(seqM.charAt(i));
			if(seqM.charAt(i) != seqN.charAt(j)) { // mismatch
				this.mismatch_num += 1;
				this.alignment_score += mismatch_penalty;
			}else { // match
				this.alignment_score += match_penalty;
			}
			this.overlap_len += 1;					
			i += 1;
			j += 1;
		}
		
		if (i == lenSeqM){
			//if(!this.overlapSeq.toString().contains("AAAAAAA") && !this.overlapSeq.toString().contains("TTTTTTT")) // filter all polyA or polyT
				return true;
		}
		
		return false;
	}

	@Override
	public String toString() {
		String str = this.seqA.toString() + "\t" 
					+ this.seqB.toString() + "\t"
					+ this.align_start + "\t"
					+ this.overlap_len + "\t"
					+ this.mismatch_num + "\t"
					+ this.overlapSeq.toString();
		return str;
	}
	
	

}
