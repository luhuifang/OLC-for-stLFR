package graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * 
 * @author luhuifang
 *
 */
public class DiGraph {
	
	protected final int V; //Number of vertex of graph
	protected int E; //Number of edges of graph
	protected Set<String> nodes; //All vertex of graph
	
	/*
	 * Adjacency List
	 * key: Name of vertex
	 * value: A list of vertex which is neighbourhood for key 
	 */
	private Map<String, List<String>> adj ; 
	
	/*
	 * key: Name of vertex
	 * value: indegree or outdegree
	 */
	protected Map<String, Integer> indegree;
	protected Map<String, Integer> outdegree;
	
	public DiGraph(Set<String> nodes) {
		this.V = nodes.size();
		this.E = 0;
		this.nodes = nodes;
		initNodes();
	}

	public DiGraph(Set<String> nodes, int e) {
		this.V = nodes.size();
		this.E = e;
		this.nodes = nodes;
		initNodes();
	}
	
	private void initNodes() {
		this.adj = new TreeMap<String, List<String>>();
		this.indegree = new TreeMap<String, Integer>();
		this.outdegree = new TreeMap<String, Integer>();
		for(String node : this.nodes) {
			this.adj.put(node, new ArrayList<String>());
			this.indegree.put(node, 0);
			this.outdegree.put(node, 0);
		}
	}
	
	public int getE() {
		return E;
	}

	public void setE(int e) {
		E = e;
	}

	public Map<String, List<String>> getAdj() {
		return adj;
	}

	public void setAdj(Map<String, List<String>> adj) {
		this.adj = adj;
	}

	public Map<String, Integer> getIndegree() {
		return indegree;
	}

	public void setIndegree(TreeMap<String, Integer> indegree) {
		this.indegree = indegree;
	}

	public Map<String, Integer> getOutdegree() {
		return outdegree;
	}

	public void setOutdegree(TreeMap<String, Integer> outdegree) {
		this.outdegree = outdegree;
	}

	public int getV() {
		return V;
	}
	
	public void addEdge(String source_id, String target_id) {
		this.adj.get(source_id).add(target_id);
		this.E += 1;
		Integer preod = this.outdegree.get(source_id);
		preod += 1;
		this.outdegree.put(source_id, preod);
		
		Integer preid = this.indegree.get(target_id);
		preid += 1;
		this.outdegree.put(target_id, preid);
	}
	
	public boolean hasAdj(String v) {
		if(this.adj.get(v).size() == 0) {
			return false;
		}else {
			return true;
		}
	}
	
	public List<String> adjNodes(String v) {
		return this.adj.get(v);
	}
	
	public DiGraph reverse() {
		DiGraph R = new DiGraph(this.nodes);
		for(Entry<String, List<String>> entry : this.adj.entrySet()) {
			for(String w : entry.getValue()) {
				R.addEdge(w, entry.getKey());
			}
		}
		return R;
	}
	
	public Integer getIndegree(String v) {
		return this.indegree.get(v);
	}
	
	public Integer getOutdrgree(String v) {
		return this.outdegree.get(v);
	}
	
	public boolean isSingleNode(String v) {
		if (getIndegree(v) == 0 && getOutdrgree(v) == 0) {
			return true;
		}
		return false;
	}
	
	public boolean isRoot(String v) {
		if(getIndegree(v) == 0) {
			return true;
		}
		return false;
	}
	
	public boolean isLeaf(String v) {
		if(getOutdrgree(v) == 0) {
			return true;
		}
		return false;
	}
	
	public List<String> findAllSingleNodes() {
		List<String> singlenodes = new ArrayList<String>();
		for(String v : this.indegree.keySet()) {
			if (isRoot(v) && isLeaf(v)) {
				singlenodes.add(v);
			}
		}
		return singlenodes;
	}
	
	public List<String> findNodesWithIndegree(int indegree) {
		List<String> nodes = new ArrayList<String>();
		for(String v:this.indegree.keySet()) {
			if (this.indegree.get(v) == indegree){
				nodes.add(v);
			}
		}
		return nodes;
	}
	
	public List<String> findNodesWithOutdegree(int outdegree) {
		List<String> nodes = new ArrayList<String>();
		for(String v:this.outdegree.keySet()) {
			if (this.outdegree.get(v) == outdegree){
				nodes.add(v);
			}
		}
		return nodes;
	}
	
	public void printNetwork() {
		for (Entry<String, List<String>> entry : this.adj.entrySet()) {
			for (String w : entry.getValue()) {
				System.out.println(entry.getKey() + "\t" + w);
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer graph_str = new StringBuffer(this.V + "\t" + this.E + "\n");
		for (Entry<String, List<String>> entry : this.adj.entrySet()) {
			graph_str.append(entry.getKey() + " : " + entry.getValue().toString() + "\n");
		}
		return graph_str.toString();
	}
	
	
}
