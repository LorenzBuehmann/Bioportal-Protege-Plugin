package de.leipzig.imise.bioportal.ui;

import de.leipzig.imise.bioportal.rest.Entity;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * @author Lorenz Buehmann
 */
public class EntityTreeCellRenderer extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
												  boolean leaf, int row, boolean hasFocus) {
		JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		// remove default folder icons
		label.setIcon(null);

		// use the entity label
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
