package searchGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import graph.DiGraph;

public class DepthFirstPaths {
	private String start;
	private Map<String, String> endTo;
	private Map<String, Boolean> marked;
	
	public DepthFirstPaths(DiGraph graph, String start) {
		this.start = start;
		this.marked = new TreeMap<String, Boolean>();
		this.endTo = new TreeMap<String, String>();
		dfs(graph, start);
	}
	
	private void dfs(DiGraph graph, String s) {
		this.marked.put(s, true);
		if(graph.hasAdj(s)) {
			for(String w :graph.adjNodes(s)) {  //adj: node -> [nodes]
				if(!this.marked.containsKey(w)) {
					this.endTo.put(w, s);
					dfs(graph, w);
				}
			}
		}
	}
	
	public boolean hasPathTo(String endv) {
		if (this.marked.containsKey(endv)){
			return true;
		}else {
			return false;
		}
	}
	
	public List<String> pathTo(String endv) {
		List<String> path = new ArrayList<String>();
		if(hasPathTo(endv)) {
			String v = endv;
			while( ! v.equals(this.start)) {
				path.add(v);
				v = this.endTo.get(v);
			}
			path.add(this.start);
			Collections.reverse(path);
		}
		return path;
	}
}
