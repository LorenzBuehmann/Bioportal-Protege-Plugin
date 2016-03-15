package de.leipzig.imise.bioportal.ui;

import de.leipzig.imise.bioportal.rest.Entity;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.*;

/**
 * @author Lorenz Buehmann
 */
public class JTreeUtils {

	public static Comparator< DefaultMutableTreeNode> tnc = new Comparator< DefaultMutableTreeNode>() {
		@Override public int compare(DefaultMutableTreeNode a, DefaultMutableTreeNode b) {
			//Sort the parent and child nodes separately:
			if (a.isLeaf() && !b.isLeaf()) {
				return 1;
			} else if (!a.isLeaf() && b.isLeaf()) {
				return -1;
			} else {
				String sa = a.getUserObject().toString();
				String sb = b.getUserObject().toString();
				return sa.compareToIgnoreCase(sb);
			}
		}
	};

	public static DefaultMutableTreeNode searchNode(DefaultMutableTreeNode root, String nodeStr) {
		DefaultMutableTreeNode node = null;
		Enumeration e = root.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			node = (DefaultMutableTreeNode) e.nextElement();
			if ((node.getUserObject().toString()).contains(nodeStr)) {
				return node;
			}
		}
		return null;
	}

	public static DefaultMutableTreeNode searchNode(DefaultMutableTreeNode root, Entity entity) {
		DefaultMutableTreeNode node = null;
		Enumeration e = root.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			node = (DefaultMutableTreeNode) e.nextElement();
			if (node.getUserObject().equals(entity)) {
				return node;
			}
		}
		return null;
	}

	public static void sortTree(DefaultMutableTreeNode root) {
		Enumeration e = root.depthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			if (!node.isLeaf()) {
				sort3(node);   //selection sort
				//sort3(node); //JDK 1.6.0: iterative merge sort
				//sort3(node); //JDK 1.7.0: TimSort
			}
		}
	}

	public static void sort3(DefaultMutableTreeNode parent) {
		int n = parent.getChildCount();
		//@SuppressWarnings("unchecked")
		//Enumeration< DefaultMutableTreeNode> e = parent.children();
		//ArrayList< DefaultMutableTreeNode> children = Collections.list(e);
		List< DefaultMutableTreeNode> children = new ArrayList< DefaultMutableTreeNode>(n);
		for (int i = 0; i < n; i++) {
			children.add((DefaultMutableTreeNode) parent.getChildAt(i));
		}
		Collections.sort(children, tnc); //iterative merge sort
		parent.removeAllChildren();
		for (MutableTreeNode node: children) {
			parent.add(node);
		}
	}

	public static void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
		for (int i = startingIndex; i < rowCount; ++i) {
			tree.expandRow(i);
		}

		if (tree.getRowCount() != rowCount) {
			expandAllNodes(tree, rowCount, tree.getRowCount());
		}
	}
}
