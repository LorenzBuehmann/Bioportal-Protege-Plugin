package de.leipzig.imise.bioportal.ui;

import java.awt.Dimension;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.util.HTMLUtil;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.core.ui.util.NativeBrowserLauncher;

import de.leipzig.imise.bioportal.BioportalRESTServices;
import de.leipzig.imise.bioportal.bean.concept.ClassBean;
import de.leipzig.imise.bioportal.util.PrivateOntologyException;

public class ConceptDetailsDialog extends DetailsDialog {

	public ConceptDetailsDialog(SearchBean searchBean){
		super();
		StringBuffer buffer = new StringBuffer();
		buffer.append("<html><body>");
		buffer.append("<table width=\"100%\" class=\"servicesT\" style=\"border-collapse:collapse;border-width:0px;padding:5px\"><tr>");

		buffer.append("<td class=\"servHd\" style=\"background-color:#8E798D;color:#FFFFFF;\">Property</td>");
		buffer.append("<td class=\"servHd\" style=\"background-color:#8E798D;color:#FFFFFF;\">Value</td>");

		String oddColor = "#F4F2F3";
		String evenColor = "#E6E6E5";

		ClassBean cb = null;
		try {
			cb = BioportalRESTServices.getConceptProperties(searchBean.getOntologyVersionId(), searchBean.getConceptId());
			if (cb == null) {
				detailsPane.setText("<html><body><i>No search results.</i></body></html>");
				return;
			}
		} catch (PrivateOntologyException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this,e1.getMessage(),"Access Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		
		String label = cb.getLabel();
		Map<Object, Object> relationsMap = cb.getRelations();
		
		buffer.append(getDetailsProperty("ID", cb.getId(), evenColor));
		buffer.append(getDetailsProperty("Name", label, oddColor));
		if(relationsMap.get("ChildCount") != null){
			buffer.append(getDetailsProperty("Children", relationsMap.get("ChildCount").toString(), evenColor));
			relationsMap.remove("ChildCount");
		}
		if(relationsMap.get("InstanceCount") != null){
			buffer.append(getDetailsProperty("Instances", relationsMap.get("InstanceCount").toString(), oddColor));
			relationsMap.remove("InstanceCount");
		}
		
		
		int i = 0;
		for (Object obj : relationsMap.keySet()) {
			Object value = relationsMap.get(obj);
			String color = i % 2 == 0 ? evenColor : oddColor;
			if (value != null) {
				String text = HTMLUtil.replaceEOF(value.toString());
				if (text.startsWith("[")) {
					text = text.substring(1, text.length() - 1);
				}
				if (text.length() > 0) {
					buffer.append("<tr>");
					buffer.append("<td class=\"servBodL\" style=\"background-color:" + color
							+ ";padding:7px;font-weight: bold;\" >");
					buffer.append(obj.toString());
					buffer.append("</td>");
					buffer.append("<td class=\"servBodL\" style=\"background-color:" + color + ";padding:7px;\" >");
					buffer.append(text);
					buffer.append("</td>");
					buffer.append("</tr>");
					i++;
				}
			}
		}
		buffer.append("</table>");

		String directLink = BioportalRESTServices.getConceptPropertiesURL(searchBean.getOntologyVersionId(),
				searchBean.getConceptId()).toString();
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

		setTitle("Details for " + label + " [" + searchBean.getOntologyDisplayLabel() + "]");
		setModal(true);
		add(new JScrollPane(detailsPane));
		pack();
		setVisible(true);
	}
	
}
