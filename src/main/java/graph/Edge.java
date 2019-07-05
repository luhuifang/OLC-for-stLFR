package graph;

public class Edge {
	
	protected Vertex source;
	protected Vertex target;
	protected String source_ori;
	protected String target_ori;
	
	public Edge() {
		initWeightDiEdge(new Vertex(), new Vertex(), "+", "+");
	}
	
	public Edge(Vertex source, Vertex target) {
		initWeightDiEdge(source, target, "+", "+");
	}
	
	public Edge(Vertex source, Vertex target, String source_ori, String target_ori) {
		initWeightDiEdge(source, target, source_ori, target_ori);
	}
	
	private void initWeightDiEdge(Vertex source, Vertex target, 
			String source_ori, String target_ori) {
		
		this.source = source;
		this.target = target;
		this.source_ori = source_ori;
		this.target_ori = target_ori;
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
	
	public String To() {
		return this.target.getNode_id() + this.target_ori;
	}
	
	public String From() {
		return this.source.getNode_id() + this.source_ori;
	}

	@Override
	public String toString() {
		return this.From() + "\t" + this.To();
	}
	
	

}
