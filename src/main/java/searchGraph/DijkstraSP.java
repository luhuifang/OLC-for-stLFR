package searchGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import graph.EdgeWeightedDiGraph;
import graph.WeightDiEdge;
import queue.IndexMaxPQ;

/**
 * 
 * @author luhuifang
 *
 * @param <T> Object extends WeightDiEdge
 */
public class DijkstraSP <T extends WeightDiEdge> {
	
	private List<String> hasStarted;
	private Map<String, WeightDiEdge> edgeTo;
	private Map<String, Double> distTo;
	private List<String> nodes; // 点的编号与点的名字对应表
	private IndexMaxPQ<Double> pq;
	
	public DijkstraSP(EdgeWeightedDiGraph<T> graph, List<String> startNodes) {
		this.edgeTo = new TreeMap<String, WeightDiEdge>();
		this.distTo = new TreeMap<String, Double>();
		this.pq = new IndexMaxPQ<Double>(graph.getV());
		this.hasStarted = new ArrayList<String>();
		this.nodes = new ArrayList<String>();
		
		if (startNodes.size() > 0) {
			for(String v : startNodes) {
				hasStarted.add(v);
				this.distTo.put(v, 0.0);
				
				int index = indexOfNode(v);
				if(index < nodes.size()) {
					nodes.set(index, v);
				}else {
					nodes.add(v);
				}
				
				pq.insert(index, 0.0);
				
				while(!pq.isEmpty()) {
					relax(graph, pq.delMax());
				}
			}
		}
		
	}
	
	private int indexOfNode(String v) {
		int index;
		if (nodes.contains(v)) {
			index = nodes.indexOf(v); 
		}else {
			index = nodes.size();
		}
		return index;
	}
	
	
	private void relax(EdgeWeightedDiGraph<T> graph, int maxIndex) {		
		String start_v = nodes.get(maxIndex);
		hasStarted.add(start_v);
		for (T adj_edge : graph.adjEdges(start_v)) {
			String w = adj_edge.To();
			int index = indexOfNode(w);
			
			if(index < nodes.size()) {
				nodes.set(index, w);
			}else {
				nodes.add(w);
			}
			
			if(hasStarted.contains(w)) {
				continue;
			}
			
			if(!this.distTo.containsKey(w) || (this.distTo.get(w) < this.distTo.get(start_v) + adj_edge.getWeight())) {
				
				this.distTo.put(w, this.distTo.get(start_v) + adj_edge.getWeight()); //change weight of this node
				edgeTo.put(w, adj_edge);
				
				if(pq.contains(index)) {
					pq.changeKey(index, this.distTo.get(w));
				}else {
					pq.insert(index, this.distTo.get(w));
				}
			}
		}
		
	}
	
	public Double getDistTo(String v) {
		return distTo.get(v);
	}
	
	public boolean hasPathTo(String v) {
		if(distTo.containsKey(v)) {
			return true;
		}else {
			return false;
		}
	}
	
	public List<WeightDiEdge> pathTo(String endv) {
		List<WeightDiEdge> path = new ArrayList<WeightDiEdge>();
		if(hasPathTo(endv)) {
			String v = endv;
			while(edgeTo.containsKey(v)) {
				path.add(edgeTo.get(v));
				v = this.edgeTo.get(v).From();
			}
			Collections.reverse(path);
		}
		return path;
	}
	
	public Entry<String, Double> maxDistItem(Map<String, Double> dists) {
		double maxdist = -1;
		Entry<String, Double> maxItem = null;
		
		for(Entry<String, Double> entry : distTo.entrySet()) {
			if(entry.getValue() > maxdist) {
				maxdist = entry.getValue();
				maxItem = entry;
			}
		}
		return maxItem;
		
	}
	
	public List<List<WeightDiEdge>> allPaths() {
		
		
		List<List<WeightDiEdge>> paths = new ArrayList<List<WeightDiEdge>>();
		Map<String, Double> dists = distTo;
		Map<String, WeightDiEdge> edges = edgeTo;
		
		while(dists.size() > 0) {
			Entry<String, Double> maxDistEntry = maxDistItem(dists);
			if (maxDistEntry == null) {
				continue;
			}
			String e = maxDistEntry.getKey();
			List<WeightDiEdge> path = new ArrayList<WeightDiEdge>();
			
			if(maxDistEntry.getValue() > 0) {
				while(edges.containsKey(e)) {
					String w = edges.get(e).From();
					
					if(dists.containsKey(w)) {
						path.add(edges.get(e));
					}
					
					//delete from dists
					dists.remove(e);
					if(dists.containsKey(revV(e))) {
						dists.remove(revV(e));
					}
					
					//delete from edges
					edges.remove(e);
					if(edges.containsKey(revV(e))) {
						edges.remove(revV(e));
					}
					
					e = w;
				}
				
				//delete from dists
				if(dists.containsKey(e)) {
					dists.remove(e);
				}
				if(dists.containsKey(revV(e))) {
					dists.remove(revV(e));
				}
				
				//if the size of path greater than 0, save
				if (path.size() > 0) {
					Collections.reverse(path);
					paths.add(path);
				}
			}else {
				dists.remove(e);
			}
		}
		
		return paths;
	}
	
	private String revV(String v) {
		if(v.endsWith("+")) {
			return v.replace("+", "-");
		}else if(v.endsWith("-")) {
			return v.replace("-", "+");
		}
		return v;
	}

}
