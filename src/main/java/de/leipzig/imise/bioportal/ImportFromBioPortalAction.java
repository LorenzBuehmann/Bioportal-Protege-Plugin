package de.leipzig.imise.bioportal;

import de.leipzig.imise.bioportal.ui.SearchPanel;
import org.protege.editor.owl.ui.action.SelectedOWLClassAction;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * The context menu action plugin for the class hierarchy view component.
 *
 * @author Lorenz Buehmann
 */
public class ImportFromBioPortalAction extends SelectedOWLClassAction{


	@Override
	protected void initialiseAction() throws Exception {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		OWLClass selectedClass = getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();

		if (selectedClass != null){
			onImportFromBioPortal();
		}
	}

	private void onImportFromBioPortal() {
		JDialog dialog = new JDialog();
		dialog.setTitle("Import data from BioPortal");
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.add(new SearchPanel(getOWLEditorKit()));
		dialog.setSize(new Dimension(1000, 800));
		dialog.setModal(true);
		dialog.setVisible(true);
	}
}
