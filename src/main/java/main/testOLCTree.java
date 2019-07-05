package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import assembly.AssemblyFromTree;
import graph.Vertex;
import io.Input;

import tree.OLCTree;
import tree.TreeNode;

public class testOLCTree {
	
	public static void main(String[] args) throws IOException {
	
		/*
		List<TreeNode> nodes = new ArrayList<TreeNode>();
		TreeNode node1 = new TreeNode(1, "A");
		TreeNode node2 = new TreeNode(2, "B", node1);
		TreeNode node3 = new TreeNode(3, "C", node1);
		TreeNode node4 = new TreeNode(4, "D", node3);
		TreeNode node5 = new TreeNode(5, "E", node3);
		TreeNode node6 = new TreeNode(6, "F", node3);
		TreeNode node7 = new TreeNode(7, "G", node5);
		
		nodes.add(node1);
		nodes.add(node2);
		nodes.add(node3);
		nodes.add(node4);
		nodes.add(node5);
		nodes.add(node6);
		nodes.add(node7);
		
		OLCTree olctree = new OLCTree(nodes);
		olctree.createTree();
		List<TreeNode> roots = olctree.getRoots();
		for(TreeNode root : roots) {
			System.out.println("root id : " + root.getSelfId());
			for(TreeNode junior : root.getJuniors()) {
				System.out.println(junior.getSelfId());
			}
			//root.traverse();
		}
		*/
		
		/*
		String fa = "E:\\01.program\\04.stLFR\\02.assembly\\05.test_java_olc\\00.data\\AGGTTGCGATCGAGGG.fa";
		
		AssemblyFromTree ass = new AssemblyFromTree(new File(fa));
		try {
			ass.runAssembly(11, 0, "short");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		List<Vertex> l = new ArrayList<Vertex>();
		l.add(new Vertex(1, "a", "ATGC"));
		l.add(new Vertex(2, "b", "ATGC"));
		Vertex a = new Vertex(3, "c", "ATGC");
		System.out.println(l.contains(a));
		
		Set<Vertex> s = new HashSet<>(l);
		List<Vertex> l2 = new ArrayList<>(s);
		System.out.println(l2);
		
		l.stream().distinct();
		System.out.println(l);
		
		String fa = "E:\\01.program\\04.stLFR\\02.assembly\\05.test_java_olc\\00.data\\test2.fa";
		
		List<Vertex> seqs = Input.readFastaFile(new File(fa));
		for(Vertex s1:seqs) {
			System.out.println(s1);
		}
		
		System.out.println("filter");
		/*
		Set<Vertex> s2 = new HashSet<>(seqs);
		seqs = new ArrayList<>(s2);
		//seqs = seq;
		System.out.println(seqs);
	
		
		List<Vertex> raws = seqs;
		seqs = new ArrayList<Vertex>();
		for(Vertex v : raws) {
			if(! seqs.contains(v)) {
				v.setNode_id(seqs.size());
				seqs.add(v);
			}
		}
		
		System.out.println(seqs);
		
		String str = "TGCACTCCTCACTAGT";
		System.out.println(str.hashCode());
		*/
		
		String stat = "E:\\01.program\\04.stLFR\\02.assembly\\05.test_java_olc\\00.data\\barcode_umi_relation.stat";
		Map<String, String[]> map = Input.readUmilist(new File(stat));
		for(Entry<String, String[]> v : map.entrySet()) {
			System.out.println(v.getKey() + ": " + v.getValue());
		}
	}

}
