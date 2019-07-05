package graph;

import alignment.PairNodeOverlap;
import alignment.PairSeqOverlap;

/**
 * 
 * @author luhuifang
 *
 */
public class WeightDiEdge extends Edge{
	
	protected Vertex source; //Source Vertex
	protected Vertex target; //Target Vertex
	protected String source_ori; //Orientation of source
	protected String target_ori; //Orientation of target
	protected int source_start;
	protected int overlap_len;
	private double weight; //alignment score or (0.6 * (this.overlap_len) + 0.4 * (this.target.seqLen() - this.overlap_len))
	
	public WeightDiEdge(Vertex source, Vertex target) {
		initWeightDiEdge(source, target, "+", "+", 0, 0, 0);
	}
	
	public WeightDiEdge(Vertex source, Vertex target, String source_ori, String target_ori) {
		initWeightDiEdge(source, target, source_ori, target_ori, 0, 0, 0);
	}
	
	public WeightDiEdge(Vertex source, Vertex target, String source_ori, String target_ori, int source_start, int overlap_len) {
		initWeightDiEdge(source, target, source_ori, target_ori, source_start, overlap_len, 0);
	}
	
	public WeightDiEdge(Vertex source, Vertex target, String source_ori, String target_ori, int source_start, int overlap_len, double weight) {
		initWeightDiEdge(source, target, source_ori, target_ori, source_start, overlap_len, weight);
	}
	
	public WeightDiEdge(Vertex source, Vertex target, boolean first_forward, boolean second_forward, PairSeqOverlap pair) {
		String first_ori = first_forward ? "+" : "-";
		String second_ori = second_forward ? "+" : "-";
		initWeightDiEdge(source, target, first_ori, second_ori, pair.getAlign_start(), pair.getOverlap_len(), pair.getAlignment_score());
	}

	public WeightDiEdge(PairNodeOverlap pair) {
		initWeightDiEdge(
				pair.getFirst(),
				pair.getSecond(),
				pair.getFirstOri(),
				pair.getSecondOri(),
				pair.getAlign_start(),
				pair.getOverlap_len(),
				0);
	}
	
	private void initWeightDiEdge(Vertex source, Vertex target, 
								String source_ori, String target_ori, 
								int source_start, int overlap_len, double weight) {
		this.source = source;
		this.target = target;
		this.source_ori = source_ori;
		this.target_ori = target_ori;
		this.source_start = source_start;
		this.overlap_len = overlap_len;
		this.weight = weight;
	}
	
	public Vertex getSource() {
		return source;
	}

	public void setSource(Vertex source) {
		this.source = source;
	}

	public Vertex getTarget() {
		return target;
	}

	public void setTarget(Vertex target) {
		this.target = target;
	}

	public String getSource_ori() {
		return source_ori;
	}

	public void setSource_ori(String source_ori) {
		this.source_ori = source_ori;
	}

	public String getTarget_ori() {
		return target_ori;
	}

	public void setTarget_ori(String target_ori) {
		this.target_ori = target_ori;
	}

	public int getSource_start() {
		return source_start;
	}

	public void setSource_start(int source_start) {
		this.source_start = source_start;
	}

	public int getOverlap_len() {
		return overlap_len;
	}

	public void setOverlap_len(int overlap_len) {
		this.overlap_len = overlap_len;
	}
	
	public String From() {
		return this.source.getNode_id() + this.source_ori;
	}
	
	public StringBuffer getFromSeq() {
		if("+".equals(this.source_ori)) {
			return this.source.getSeq();
		}else {
			return this.source.revComSeq();
		}
	}
	
	public String To() {
		return this.target.getNode_id() + this.target_ori;
	}
	
	public StringBuffer getToSeq() {
		if("+".equals(this.target_ori)) {
			return this.target.getSeq();
		}else {
			return this.target.revComSeq();
		}
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public double getWeight() {
		if (this.weight == 0) {
			return 0.6 * (this.overlap_len) + 0.4 * (this.target.seqLen() - this.overlap_len);
		}
		return this.weight;
	}
	
	/**
	 * The direction of this edge is reversed.
	 * @return a WeightDiEdge object
	 */
	public WeightDiEdge reverse() {
		int source_start1 = this.target.seqLen()-this.overlap_len;
		return new WeightDiEdge(this.target, this.source, this.target_ori, this.source_ori, source_start1, this.overlap_len, this.weight);
	}
	
	/**
	 * Convert to equivalent overlap
	 * @return a WeightDiEdge object
	 */
	public WeightDiEdge revEdge() {
		int source_start1 = this.target.seqLen()-this.overlap_len;
		return new WeightDiEdge(this.target, this.source, 
								revOri(this.target_ori), revOri(this.source_ori), 
								source_start1, this.overlap_len, this.weight);
	}

	
	public String getOverlapSeq() {
		if (this.source_ori.equals("+")) {
			return this.source.getSeq().substring(this.source_start, this.source_start+this.overlap_len).toString();
		}else{
			return this.source.revComSeq().substring(this.source_start, this.source_start+this.overlap_len);
		}
	}
	
	private String revOri(String ori) {
		if ("+".equals(ori)){
			return "-";
		}else{
			return "+";
		}
	}
	
	@Override
	public String toString() {
		String str = this.From() + "\t" 
					+ this.To() + "\t"
					+ this.source_start + "\t"
					+ this.overlap_len + "\t"
					+ this.getWeight();
		return str;
	}
}

