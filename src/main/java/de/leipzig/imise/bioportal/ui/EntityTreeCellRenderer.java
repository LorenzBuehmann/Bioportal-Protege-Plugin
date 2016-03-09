package de.leipzig.imise.bioportal.ui;

import de.leipzig.imise.bioportal.rest.Entity;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * @author Lorenz Buehmann
 */
public class EntityTreeCellRenderer implements TreeCellRenderer {
	private JLabel label = new JLabel();

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
												  boolean leaf, int row, boolean hasFocus) {
		if (value != null && value instanceof DefaultMutableTreeNode) {
			Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
			if(userObject instanceof Entity) {
				label.setText(((Entity) userObject).getPrefLabel());
			} else {
				label.setText(userObject.toString());
			}
		}
		return label;
	}
}
