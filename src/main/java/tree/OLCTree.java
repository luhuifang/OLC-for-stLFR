package tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import graph.WeightDiEdge;

import java.util.TreeMap;

public class OLCTree {
	private List<TreeNode> roots;
	Map<Integer, TreeNode> nodeMap;
	private boolean isValidTree;
	
	private void initNodes() {
		if (this.roots == null) 
			this.roots = new ArrayList<TreeNode>();
		
		if (this.nodeMap == null)
			this.nodeMap = new TreeMap<Integer, TreeNode>();
	}
	
	
	public OLCTree() {
		initNodes();
		this.isValidTree = true;
	}
	
	public OLCTree(List<TreeNode> nodes) {
		initNodes();
		for(TreeNode node : nodes) {
			this.nodeMap.put(node.getSelfId(), node);
		}
		this.isValidTree = true;
	}
	
	public OLCTree(Map<Integer, TreeNode> nodeMap) {
		this.nodeMap = nodeMap;
		initNodes();
		this.isValidTree = true;
	}
	public static TreeNode getTreeNodeById(TreeNode tree, int id) {  
        if (tree == null)  
            return null;  
        TreeNode treeNode = tree.findTreeNodeById(id);  
        return treeNode;  
    } 
	
	
	public void createTree() {
		//Map<Integer, TreeNode> nodeMap = putNodeToMap();
		addNodeToParent(nodeMap);
	}
	
	protected void addNodeToParent(Map<Integer, TreeNode> nodemap) {
		if(nodemap.isEmpty()) return;
		
		Iterator<TreeNode> nodes = nodemap.values().iterator();
		while(nodes.hasNext()) {
			TreeNode node = nodes.next();
			int parentId = node.getParentId();
			
			if (parentId < 0) {  // not parentNode, rootNode
				roots.add(node);
				continue;
			}
			
			if(nodemap.containsKey(parentId)) {
				TreeNode parentNode = nodemap.get(parentId);
				if(parentNode == null) {
					this.isValidTree = false;
					return;
				}else {
					parentNode.addChildNode(node);
				}
			}
			
		}
		
	}
	
	/*
	protected Map<Integer, TreeNode> putNodeToMap() {
		Map<Integer, TreeNode> nodeMap = new TreeMap<Integer, TreeNode>();
		if(this.allNodes == null || this.allNodes.isEmpty())
			return nodeMap;
		for(TreeNode node : this.allNodes) {
			int selfId = node.getSelfId();
			nodeMap.put(selfId, node);
		}
		return nodeMap;
	}
	*/
	
	public void addTreeNodesFromEdge(Map<String, WeightDiEdge> allEdges) {
		Map<String, Integer> nameIdMap = new TreeMap<String, Integer>();
		
		int treeNodeCount = 1;
		for(Entry<String, WeightDiEdge> entry : allEdges.entrySet()) {
			System.out.println(entry.getKey());
			String[] names = entry.getKey().split(" ");
			String parentName = names[0];
			String childName = names[1];
			
			//add parentNode
			TreeNode parentnode = null;
			if(!nameIdMap.containsKey(parentName)) {
				nameIdMap.put(parentName, treeNodeCount);
				parentnode = new TreeNode(treeNodeCount, parentName);
				this.nodeMap.put(treeNodeCount, parentnode);
				treeNodeCount ++ ;
			}else {
				parentnode = nodeMap.get(nameIdMap.get(parentName));
			}
			
			//add childNode
			TreeNode childnode = null;
			if(nameIdMap.containsKey(childName)) { //exists this node
				childnode = nodeMap.get(nameIdMap.get(childName));
				if(childnode.getParentId() == -1) { //no parent
					childnode.setParentNode(parentnode);
				}else { //has parent
					WeightDiEdge childObj = (WeightDiEdge) childnode.getObj();
					if(childObj.getWeight() < entry.getValue().getWeight()) {
						childnode.setObj(entry.getValue());
					}
				}
			}else { // not exists this node , add new one
				nameIdMap.put(childName, treeNodeCount);
				childnode = new TreeNode(treeNodeCount, childName, parentnode);
				childnode.setObj(entry.getValue());
				nodeMap.put(treeNodeCount, childnode);
				treeNodeCount ++ ;
			}
			
			System.out.println("parentNode: " + parentnode.toString());
			System.out.println("childNode: " + childnode.toString());
		}
		
	}

	public boolean addTreeNode(TreeNode node) {
		initNodes();
		if(this.nodeMap.containsKey(node.getSelfId())) {
			return false;
		}
		this.nodeMap.put(node.getSelfId(), node);
		return true;
	}
	

	public boolean insertTreeNode(TreeNode node) {
		for(TreeNode root : this.roots) {
			if(root.insertJuniorNode(node)) {
				return true;
			}
		}
		return false;
	}
	
	public List<TreeNode> getRoots() {
		return roots;
	}


	public void setRoots(List<TreeNode> roots) {
		this.roots = roots;
	}


	public boolean isValidTree() {
		return isValidTree;
	}


	public void setValidTree(boolean isValidTree) {
		this.isValidTree = isValidTree;
	}


	public Map<Integer, TreeNode> getNodeMap() {
		return nodeMap;
	}


	public void setNodeMap(Map<Integer, TreeNode> nodeMap) {
		this.nodeMap = nodeMap;
	}
	
	
	
}
