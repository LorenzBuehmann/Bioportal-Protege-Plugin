package de.leipzig.imise.bioportal.ui;

import java.awt.Dimension;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.ncbo.stanford.bean.search.SearchBean;
import org.protege.editor.core.ui.util.NativeBrowserLauncher;

import de.leipzig.imise.bioportal.BioportalConstants;
import de.leipzig.imise.bioportal.BioportalRESTServices;

public class OntologyDetailsDialog extends DetailsDialog {
	
	public OntologyDetailsDialog(SearchBean searchBean){

		StringBuffer buffer = new StringBuffer();
		buffer.append("<html><body>");
		buffer.append("<table width=\"100%\" class=\"servicesT\" style=\"border-collapse:collapse;border-width:0px;padding:5px\"><tr>");

		buffer.append("<td class=\"servHd\" style=\"background-color:#8E798D;color:#FFFFFF;\">Property</td>");
		buffer.append("<td class=\"servHd\" style=\"background-color:#8E798D;color:#FFFFFF;\">Value</td>");

		String oddColor = "#F4F2F3";
		String evenColor = "#E6E6E5";

		de.leipzig.imise.bioportal.bean.ontology.OntologyBean cb = BioportalRESTServices.getOntologyProperties(searchBean.getOntologyId());
		if (cb == null) {
			detailsPane.setText("<html><body><i>No search results.</i></body></html>");
			return;
		}
		
		
		String label = cb.getDisplayLabel();
		
		buffer.append(getDetailsProperty("Ontology Name", label, evenColor));
		buffer.append(getDetailsProperty("Ontology ID", cb.getOntologyId().toString(), oddColor));
		buffer.append(getDetailsProperty("Format", cb.getFormat(), evenColor));
		buffer.append(getDetailsProperty("Categories", cb.getCategoryIds().isEmpty() ? "not assigned" : cb.getCategoryIds().toString(), oddColor));
		buffer.append(getDetailsProperty("Groups", cb.getGroupIds().isEmpty() ? "not assigned" : cb.getGroupIds().toString(), evenColor));
		buffer.append(getDetailsProperty("Contact", cb.getContactName(), oddColor));
		buffer.append(getDetailsProperty("Home Page", cb.getHomepage(), evenColor));
		buffer.append(getDetailsProperty("Publications Page", cb.getPublication(), oddColor));
		buffer.append(getDetailsProperty("Documentation Page", cb.getDocumentation(), evenColor));
		buffer.append(getDetailsProperty("Description", cb.getDescription(), oddColor));
		buffer.append(getDetailsProperty("Created At", new SimpleDateFormat().format(cb.getDateCreated()), oddColor));
		buffer.append(getDetailsProperty("Released At", new SimpleDateFormat().format(cb.getDateReleased()), oddColor));
		buffer.append("</table>");

		String directLink = getShowOntologyInBPString(searchBean);
		if (directLink != null && directLink.length() > 0) {
			buffer.append("<div style=\"padding:5px;\"><br><b>Direct link in BioPortal:</b> ");
			buffer.append("<a href=\"");
			buffer.append(directLink);
			buffer.append("\">");
			buffer.append(directLink);
			buffer.append("</a></div>");
			buffer.append("<br>"); // important in order to avoid automatic
			// horizontal scrolling to the right end of
			// the page when displaying very long URLs
		}
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
		dialog.setTitle("Details for " + label);
		dialog.setModal(true);
		dialog.add(new JScrollPane(detailsPane));
		dialog.pack();
		dialog.setVisible(true);
	}
	
	private String getShowOntologyInBPString(SearchBean searchBean){
		StringBuffer sb = new StringBuffer();
		sb.append(BioportalConstants.BP_REPOSITORY_BASE_STRING);
		sb.append(BioportalConstants.BP_ONTOLOGIES_STRING);
		sb.append("/");
		sb.append(searchBean.getOntologyId());

		return sb.toString();
	}

}
