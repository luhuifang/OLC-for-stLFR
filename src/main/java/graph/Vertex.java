package graph;

public class Vertex {
	
	private int Node_id;
	private String Node_name;
	private StringBuffer seq;
	
	public Vertex() {
		this.Node_id = 0;
		this.Node_name = "";
		this.seq = new StringBuffer();
	}
	
	public Vertex(int nodeId) {
		this.Node_id = nodeId;
		this.Node_name = "";
		this.seq = new StringBuffer();
	}
	
	public Vertex(int nodeId, String nodeName){
		this.Node_id = nodeId;
		this.Node_name = nodeName;
		this.seq = new StringBuffer();
	}
	
	public Vertex(int nodeId, String nodeName, String sequence) {
		this.Node_id = nodeId;
		this.Node_name = nodeName;
		this.seq = new StringBuffer(sequence.toUpperCase());
	}
	
	public int getNode_id() {
		return Node_id;
	}

	public void setNode_id(int node_id) {
		Node_id = node_id;
	}

	public String getNode_name() {
		return Node_name;
	}

	public void setNode_name(String node_name) {
		Node_name = node_name;
	}

	public StringBuffer getSeq() {
		return seq;
	}

	public void setSeq(StringBuffer seq) {
		this.seq = seq;
	}

	public void extendSeq(String s) {
		this.seq.append(s.toUpperCase());
	}
	
	public int seqLen() {
		return this.seq.length();
	}
	
	private char transChar(char c) {
		switch(c) {
		case 'A':
			return 'T';
		case 'T':
			return 'A';
		case 'C':
			return 'G';
		case 'G':
			return 'C';
		case 'a':
			return 'T';
		case 't':
			return 'A';
		case 'c':
			return 'G';
		case 'g':
			return 'C';
		case 'n':
			return 'N';
		default:
			return c;
		}
	}
	
	
	public StringBuffer revComSeq() {
		StringBuffer comSeq = new StringBuffer();
		for (int i = 0; i<this.seqLen(); i++) {
			comSeq.append(transChar(this.seq.charAt(i)));
		}
		return comSeq.reverse();
	}
	
	/**
	 * Get a sequence whose ori is forward or not 
	 * @param forward true: getSeq; false: getRevComSeq  
	 * @return Return StringBuffer of sequence
	 */
	public StringBuffer getSequence(boolean forward) {
		if(forward) {
			return this.getSeq();
		}else {
			return this.revComSeq();
		}
	}

	@Override
	public String toString() {
		return this.Node_id + "\t" + this.Node_name + "\t" + this.seq.toString();
	}

	@Override
	public boolean equals(Object obj) {
		Vertex otherObj = (Vertex) obj;
		return this.hashCode() == otherObj.hashCode();
	}

	@Override
	public int hashCode() {
		return this.getSeq().toString().hashCode();
	}
	
	
	
}
