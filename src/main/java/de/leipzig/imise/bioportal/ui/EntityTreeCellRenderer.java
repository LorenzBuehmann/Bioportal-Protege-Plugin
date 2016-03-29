package de.leipzig.imise.bioportal.ui;

import de.leipzig.imise.bioportal.rest.Entity;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.tree.OWLObjectTreeCellRenderer;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.SKOSVocabulary;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * @author Lorenz Buehmann
 */
public class EntityTreeCellRenderer extends DefaultTreeCellRenderer {

	private final OWLObjectTreeCellRenderer renderer;
	private OWLEditorKit editorKit;

	public EntityTreeCellRenderer(OWLEditorKit editorKit) {
		this.editorKit = editorKit;
		renderer = new OWLObjectTreeCellRenderer(editorKit);
	}

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
				Entity entity = (Entity) userObject;
				label.setText(entity.getPrefLabel());
				OWLDataFactory df = editorKit.getOWLModelManager().getOWLDataFactory();
				OWLClass cls = df.getOWLClass(IRI.create(entity.getId()));

				return renderer.getTreeCellRendererComponent(tree, cls, selected, expanded, leaf, row, hasFocus);
			} else {
				label.setText(userObject.toString());
			}
		}
		return label;
	}
}
