package de.leipzig.imise.bioportal.ui;

import java.awt.Dimension;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import de.leipzig.imise.bioportal.rest.Entity;
import org.ncbo.stanford.util.HTMLUtil;
import org.protege.editor.core.ui.util.NativeBrowserLauncher;

public class ConceptDetailsDialog extends DetailsDialog {

	public ConceptDetailsDialog(Entity entity){
		super();
		StringBuffer buffer = new StringBuffer();
		buffer.append("<html><body>");
		buffer.append("<table width=\"100%\" class=\"servicesT\" style=\"border-collapse:collapse;border-width:0px;padding:5px\"><tr>");

		buffer.append("<td class=\"servHd\" style=\"background-color:#8E798D;color:#FFFFFF;\">Property</td>");
		buffer.append("<td class=\"servHd\" style=\"background-color:#8E798D;color:#FFFFFF;\">Value</td>");

		String oddColor = "#F4F2F3";
		String evenColor = "#E6E6E5";

		String label = entity.getPrefLabel();
		Map<String, Object> relationsMap = entity.getAdditionalProperties();
		
		buffer.append(getDetailsProperty("ID", entity.getId(), evenColor));
		buffer.append(getDetailsProperty("Name", label, oddColor));

		boolean odd = false;
		for(String def : entity.getDefinition()) {
			buffer.append(getDetailsProperty("Definitions", def, odd ? oddColor : evenColor));
			odd = !odd;
		}

		if(relationsMap.get("ChildCount") != null){
			buffer.append(getDetailsProperty("Children", relationsMap.get("ChildCount").toString(), evenColor));
			relationsMap.remove("ChildCount");
		}

		if(relationsMap.get("ChildCount") != null){
			buffer.append(getDetailsProperty("Children", relationsMap.get("ChildCount").toString(), evenColor));
			relationsMap.remove("ChildCount");
		}

		if(relationsMap.get("InstanceCount") != null){
			buffer.append(getDetailsProperty("Instances", relationsMap.get("InstanceCount").toString(), oddColor));
			relationsMap.remove("InstanceCount");
		}
		
		
		int i = 0;
		for (Map.Entry<String, Object> entry : relationsMap.entrySet()) {

			String property = entry.getKey();
			Object value = entry.getValue();

			if(property.equals("@context")) {
				continue;
			}

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
					buffer.append(property);
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

		String directLink = entity.getEntityLinks().getUi();
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

		setTitle("Details for " + label + " [" + entity.getPrefLabel() + "]");
		setModal(true);
		add(new JScrollPane(detailsPane));
		pack();
		setVisible(true);
	}
	
}
