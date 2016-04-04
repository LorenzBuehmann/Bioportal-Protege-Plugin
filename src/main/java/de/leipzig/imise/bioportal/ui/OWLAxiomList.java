package de.leipzig.imise.bioportal.ui;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.*;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.protege.editor.owl.ui.renderer.InlineAnnotationRendering.DO_NOT_RENDER_COMPOUND_ANNOTATIONS_INLINE;
import static org.protege.editor.owl.ui.renderer.InlineAnnotationRendering.RENDER_COMPOUND_ANNOTATIONS_INLINE;

/**
 * A list of OWL axioms that have been selected for addition to the current ontology.
 *
 * @author Lorenz Buehmann
 */
public class OWLAxiomList extends MList{

	private OWLEditorKit editorKit;


	public OWLAxiomList(OWLEditorKit editorKit) {
		this.editorKit = editorKit;
		setCellRenderer(new AxiomListItemRenderer());
	}

	/**
	 * Set the currently shown axioms.
	 *
	 * @param axioms the axioms
	 */
	public void setAxioms(Set<OWLAxiom> axioms) {
		List<Object> items = new ArrayList<>();
		for (OWLAxiom axiom : axioms) {
			items.add(new AxiomListItem(axiom));
		}
		setListData(items.toArray());
		setFixedCellHeight(24);
	}

	@Override
	protected void handleDelete() {
		super.handleDelete();
	}

	private class AxiomListItem implements MListItem {

		private OWLAxiom axiom;

		public AxiomListItem(OWLAxiom axiom) {
			this.axiom = axiom;
		}

		public boolean isEditable() {
			return false;
		}


		public void handleEdit() {}

		public boolean isDeleteable() {
			return true;
		}


		public boolean handleDelete() {
			return true;
		}

		public String getTooltip() {
			return "To be added.";
		}
	}

	private class AxiomListItemRenderer implements ListCellRenderer {

		private OWLCellRenderer ren = new OWLCellRenderer(editorKit);
		private OWLAnnotationCellRenderer3 annotationRenderer = new OWLAnnotationCellRenderer3(editorKit);

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
													  boolean cellHasFocus) {
			if (value instanceof AxiomListItem) {
				AxiomListItem item = ((AxiomListItem) value);
				ren.setOntology(editorKit.getOWLModelManager().getActiveOntology());
				ren.setHighlightKeywords(true);
				ren.setWrap(false);

				OWLAxiom axiom = item.axiom;
				if (axiom instanceof OWLAnnotationAssertionAxiom) {
					OWLAnnotationAssertionAxiom annotationAssertionAxiom = (OWLAnnotationAssertionAxiom) axiom;
					annotationRenderer.setReferenceOntology(editorKit.getOWLModelManager().getActiveOntology());
					annotationRenderer.setInlineAnnotationRendering(InlineAnnotationRendering.DO_NOT_RENDER_COMPOUND_ANNOTATIONS_INLINE);//getRenderAnnotationAnnotationsInline());
					annotationRenderer.setInlineDatatypeRendering(getAnnotationLiteralDatatypeRendering());
					annotationRenderer.setThumbnailRendering(getInlineThumbnailRendering());
					return annotationRenderer.getListCellRendererComponent(list,
																		   annotationAssertionAxiom,
																		   index,
																		   isSelected,
																		   cellHasFocus);
				}
				return ren.getListCellRendererComponent(list, item.axiom, index, isSelected, cellHasFocus);
			}
			else {
				return ren.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			}
		}

		private InlineDatatypeRendering getAnnotationLiteralDatatypeRendering() {
			return OWLRendererPreferences.getInstance().isDisplayLiteralDatatypesInline() ? InlineDatatypeRendering.RENDER_DATATYPE_INLINE : InlineDatatypeRendering.DO_NOT_RENDER_DATATYPE_INLINE;
		}

		private InlineAnnotationRendering getRenderAnnotationAnnotationsInline() {
			return OWLRendererPreferences.getInstance().isDisplayAnnotationAnnotationsInline() ? RENDER_COMPOUND_ANNOTATIONS_INLINE : DO_NOT_RENDER_COMPOUND_ANNOTATIONS_INLINE;
		}

		private InlineThumbnailRendering getInlineThumbnailRendering() {
			return OWLRendererPreferences.getInstance().isDisplayThumbnailsInline() ? InlineThumbnailRendering.DISPLAY_THUMBNAILS_INLINE : InlineThumbnailRendering.DO_NOT_DISPLAY_THUMBNAILS_INLINE;
		}
	}
}
