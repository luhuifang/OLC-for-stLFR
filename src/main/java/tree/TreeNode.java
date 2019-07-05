package tree;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
	
	private int parentId; //has parent: >0, otherwise: <0
	private int selfId; // must > 0
	private String nodeName;
	protected Object obj;  
	private TreeNode parentNode;
	private List<TreeNode> childList;
	
	public TreeNode() {
		initChildList();
	}
	
	public TreeNode(int id) {
		initTreeNode(-1, id, null, null);
	}
	
	public TreeNode(int id, String nodename) {
		initTreeNode(-1, id, nodename, null);
	}

	public TreeNode(int id, TreeNode parentNode) {
		initTreeNode(parentNode.getSelfId(), id, null, parentNode);
	}
	
	public TreeNode(int id, String nodename, TreeNode parentNode) {
		initTreeNode(parentNode.getSelfId(), id, nodename, parentNode);
	}
	
	public void initChildList() {
		if(childList == null)
			this.childList = new ArrayList<TreeNode>();
	}
	
	private void initTreeNode(int parentId, int selfId, String nodeName, TreeNode parentNode) {
		if(selfId < 0) {
			throw new RuntimeException("id of TreeNode must more than 0");
		}
		this.parentId = parentId;
		this.selfId = selfId;
		this.nodeName = nodeName;
		this.parentNode = parentNode;
		initChildList();
	}
	
	public boolean isLeaf() {
		if(childList == null) {
			return true;
		}else {
			if(childList.isEmpty())
				return true;
			else
				return false;
		}
	}
	
	/**
	 * Add a child node
	 * @param new_node New child node
	 * @return True: add successful; False: add failed
	 */
	public boolean addChildNode(TreeNode new_node) {
		initChildList();
		return this.childList.add(new_node);
	}
	
	public boolean isValidTree() {
		return true;
	}
	
	/**
	 * Get all elder Nodes
	 * @return A list of elders
	 */
	public List<TreeNode> getElders(){
		List<TreeNode>elders = new ArrayList<TreeNode>();
		TreeNode parentNode = this.getParentNode();
		if(parentNode == null) {
			return elders;
		}else {
			elders.add(parentNode);
			elders.addAll(parentNode.getElders());
			return elders;
		}
	}

	/**
	 * Get all junior nodes
	 * @return A list of juniors
	 */
	public List<TreeNode> getJuniors(){
		List<TreeNode> juniors = new ArrayList<TreeNode>();
		
		List<TreeNode> children = this.getChildList();
		if(children == null) {
			return juniors;
		}else {
			int childNumber = children.size();
			System.out.println("Node id : " + this.selfId + ", child number : " + children.size()); 
			for(int i=0; i<childNumber; i++) {
				TreeNode child = children.get(i);
				juniors.add(child);
				juniors.addAll(child.getJuniors());
			}
		return juniors;
		}
		
	}
	
	/**
	 * Delete child of this node
	 * @param childId
	 */
	public void deleteChildNode(int childId) {
		List<TreeNode> children = this.getChildList();
		int childNumber = children.size();
		for(int i=0; i<childNumber; i++) {
			TreeNode child = children.get(i);
			if(child.getSelfId() == childId) {
				children.remove(i);
				return;
			}
		}
	}
	
	/**
	 * 
	 */
	public void deleteNode() {
		TreeNode parent = this.getParentNode();
		int id = this.getSelfId();
		
		if(parent != null) {
			parent.deleteChildNode(id);
		}
	}
	
	/**
	 * Insert a junior node random
	 * @param treenode A TreeNode will insert
	 * @return true: insert successful; false: insert failed
	 */
	public boolean insertJuniorNode(TreeNode treenode) {
		int juniorParentId = treenode.getParentId();
		if(this.parentId == juniorParentId) {
			addChildNode(treenode);
			return true;
		}else {
			List<TreeNode> children = this.getChildList();
			int childNumber = children.size();
			boolean insertFlag;
			
			for(int i = 0; i < childNumber; i++) {
				TreeNode child = children.get(i);
				insertFlag = child.insertJuniorNode(treenode);
				if(insertFlag == true) {
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * Find one node from tree
	 * @param id
	 * @return
	 */
	public TreeNode findTreeNodeById(int id) {
		if(this.selfId == id)
			return this;
		if(childList.isEmpty() || childList == null) {
			return null;
		}else {
			int childNumber = childList.size();
			for(int i =0; i<childNumber; i++) {
				TreeNode child = childList.get(i);
				TreeNode resultNode = child.findTreeNodeById(id);
				if(resultNode != null)
					return resultNode;
			}
		}
		return null;
	}
	
	/**
	 * Traveling a tree, traversing hierarchically
	 */
	public void traverse() {
		if(this.selfId < 0)
			return;
		print(this.selfId);
		if(this.childList == null || this.childList.isEmpty())
			return;
		int childNumber = this.childList.size();
		for(int i=0; i<childNumber; i++) {
			TreeNode child = this.childList.get(i);
			child.traverse();
		}
		
	}
	
	public int indegree() {
		if (this.getParentNode() == null) {
			return 0;
		}else {
			return 1;
		}
	}
	
	public int outdegree() {
		if(this.childList == null || this.childList.isEmpty()) {
			return 0;
		}else {
			return this.childList.size();
		}
	}
	
	public void print(String content) {
		System.out.println(content);
	}
	
	public void print(int content) {
		System.out.println(String.valueOf(content));  
	} 
	 
	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}


	public int getSelfId() {
		return selfId;
	}

	public void setSelfId(int selfId) {
		this.selfId = selfId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String selfName) {
		this.nodeName = selfName;
	}

	public TreeNode getParentNode() {
		return parentNode;
	}

	public void setParentNode(TreeNode parentNode) {
		this.parentNode = parentNode;
		this.parentId = parentNode.getSelfId();
	}

	public List<TreeNode> getChildList() {
		return childList;
	}

	public void setChildList(List<TreeNode> childList) {
		this.childList = childList;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	@Override
	public String toString() {
		String str = this.selfId + "\t" + this.nodeName + "\t" + this.parentId + "\tchild num: " + this.childList.size();
		return str;
	}
	
	

}
