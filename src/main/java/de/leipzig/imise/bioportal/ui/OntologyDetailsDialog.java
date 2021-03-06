package de.leipzig.imise.bioportal.ui;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;

import de.leipzig.imise.bioportal.rest.BioportalRESTService;
import de.leipzig.imise.bioportal.rest.Ontology;
import de.leipzig.imise.bioportal.util.POJO2HTML;
import org.protege.editor.core.ui.util.NativeBrowserLauncher;

public class OntologyDetailsDialog extends DetailsDialog {
	
	public OntologyDetailsDialog(Ontology ontology){
		setTitle("Details for " + ontology.getName() + " (" + ontology.getAcronym() + ")");
		setModal(true);

		String buffer = POJO2HTML.makeHTML(BioportalRESTService.getOntologySubmission(ontology)) +
				"<p><b>" + asHTMLLink(ontology.getLinks().getUi(), "Open ontology in Bioportal") +
				"</b></p>" +
				"</body></html>";
//		buffer.append("<html><body>");
//		buffer.append("<table width=\"100%\" class=\"servicesT\" style=\"border-collapse:collapse;border-width:0px;padding:5px\"><tr>");
//
//		buffer.append("<td class=\"servHd\" style=\"background-color:#8E798D;color:#FFFFFF;\">Property</td>");
//		buffer.append("<td class=\"servHd\" style=\"background-color:#8E798D;color:#FFFFFF;\">Value</td>");

		String oddColor = "#F4F2F3";
		String evenColor = "#E6E6E5";

		detailsPane.setText(buffer);
		detailsPane.setPreferredSize(new Dimension(700, 500));

		detailsPane.addHyperlinkListener(e -> {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				NativeBrowserLauncher.openURL(e.getURL().toString());
			}
		});

		add(new JScrollPane(detailsPane));
		pack();
		setVisible(true);
	}
}
