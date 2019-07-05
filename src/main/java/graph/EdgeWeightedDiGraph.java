package graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 
 * @author luhuifang
 *
 * @param <T> Class which extends Edge
 */
public class EdgeWeightedDiGraph <T extends Edge > extends DiGraph{
	
	private Map<String, List<T>> adj;

	public EdgeWeightedDiGraph(Set<String> nodes) {
		super(nodes);
		initNodes();
	}
	
	public EdgeWeightedDiGraph(Set<String> nodes, int e) {
		super(nodes, e);
		initNodes();
	}
	
	private void initNodes() {
		this.adj = new TreeMap<String, List<T>>();
		this.indegree = new TreeMap<String, Integer>();
		this.outdegree = new TreeMap<String, Integer>();
		for(String node : this.nodes) {
			this.adj.put(node, new ArrayList<T>());
			this.indegree.put(node, 0);
			this.outdegree.put(node, 0);
		}
	}
	
	
	public void addEdge(T edge) {
		this.adj.get(edge.From()).add(edge);
		this.E += 1;
		
		Integer preod = this.outdegree.get(edge.From());
		preod += 1;
		this.outdegree.put(edge.From(), preod);
		
		Integer preid = this.indegree.get(edge.To());
		preid += 1;
		this.indegree.put(edge.To(), preid);
	
	}
	
	public List<T> edges() {
		List<T> edges = new ArrayList<T>();
		for(List<T> e : this.adj.values()) {
			edges.addAll(e);
		}
		return edges;
	}
	
	public List<T> adjEdges(String v) {
		return this.adj.get(v);
	}

	@Override
	public void printNetwork() {
		for(List<T> e : this.adj.values()) {
			for(T v : e) {
				System.out.println(v.From() + "\t" + v.To());
			}
		}
	}
	
	
	
}
