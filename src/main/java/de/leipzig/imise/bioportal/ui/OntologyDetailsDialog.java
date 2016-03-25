package de.leipzig.imise.bioportal.ui;

import java.awt.Dimension;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import de.leipzig.imise.bioportal.rest.BioportalRESTService;
import de.leipzig.imise.bioportal.rest.Ontology;
import de.leipzig.imise.bioportal.util.POJO2HTML;
import org.ncbo.stanford.bean.search.SearchBean;
import org.protege.editor.core.ui.util.NativeBrowserLauncher;

import de.leipzig.imise.bioportal.BioportalConstants;
import de.leipzig.imise.bioportal.BioportalRESTServices;

public class OntologyDetailsDialog extends DetailsDialog {
	
	public OntologyDetailsDialog(Ontology ontology){

		StringBuffer buffer = new StringBuffer();
//		buffer.append("<html><body>");
//		buffer.append("<table width=\"100%\" class=\"servicesT\" style=\"border-collapse:collapse;border-width:0px;padding:5px\"><tr>");
//
//		buffer.append("<td class=\"servHd\" style=\"background-color:#8E798D;color:#FFFFFF;\">Property</td>");
//		buffer.append("<td class=\"servHd\" style=\"background-color:#8E798D;color:#FFFFFF;\">Value</td>");

		String oddColor = "#F4F2F3";
		String evenColor = "#E6E6E5";

		buffer.append(POJO2HTML.makeHTML(BioportalRESTService.getOntologySubmission(ontology)));

		buffer.append("<p><b>" + asHTMLLink(ontology.getLinks().getUi(), "Open ontology in Bioportal") + "</b></p>");

		buffer.append("</body></html>");
		detailsPane.setText(buffer.toString());
		detailsPane.setPreferredSize(new Dimension(700, 500));

		detailsPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					NativeBrowserLauncher.openURL(e.getURL().toString());
				}
			}
		});

		JDialog dialog = new JDialog();
		dialog.setTitle("Details for " + ontology.getName() + " (" + ontology.getAcronym() + ")");
		dialog.setModal(true);
		dialog.add(new JScrollPane(detailsPane));
		dialog.pack();
		dialog.setVisible(true);
	}
}
