package de.leipzig.imise.bioportal.ui;

import de.leipzig.imise.bioportal.rest.BioportalRESTService;
import de.leipzig.imise.bioportal.rest.Entity;
import de.leipzig.imise.bioportal.util.POJO2HTML;
import org.protege.editor.core.ui.util.NativeBrowserLauncher;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.util.Map;

public class ConceptDetailsDialog extends DetailsDialog {

	public ConceptDetailsDialog(Entity entity){
		setTitle("Details for " + entity.getId() + " (" + entity.getPrefLabel() + ")");
		setModal(true);

		String buffer = POJO2HTML.makeHTML(BioportalRESTService.getEntityDetails(entity)) +
				"<p><b>" + asHTMLLink(entity.getEntityLinks().getUi(), "Show entity in BioPortal") +
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
