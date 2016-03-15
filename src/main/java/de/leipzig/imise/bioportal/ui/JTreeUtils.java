package de.leipzig.imise.bioportal.ui;

import de.leipzig.imise.bioportal.rest.Entity;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

/**
 * @author Lorenz Buehmann
 */
public class JTreeUtils {

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

	public static void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
		for (int i = startingIndex; i < rowCount; ++i) {
			tree.expandRow(i);
		}

		if (tree.getRowCount() != rowCount) {
			expandAllNodes(tree, rowCount, tree.getRowCount());
		}
	}
}
