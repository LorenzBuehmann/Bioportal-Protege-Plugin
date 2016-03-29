package de.leipzig.imise.bioportal.ui;

import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public abstract class DetailsDialog extends JDialog {
	
	protected JEditorPane detailsPane;
	
	public DetailsDialog(){
		detailsPane = new JEditorPane();
		detailsPane.setEditable(false);
		detailsPane.setEditorKit(new HTMLEditorKit());
		// add a CSS rule to force body tags to use the default label font
		// instead of the value in javax.swing.text.html.default.csss
		Font font = UIManager.getFont("Label.font");
		String bodyRule = "body { font-family: " + font.getFamily() + "; " + "font-size: " + font.getSize() + "pt; }";
		((HTMLDocument) detailsPane.getDocument()).getStyleSheet().addRule(bodyRule);
		// ((HTMLDocument)editorPane.getDocument()).getStyleSheet().addRule(TableCss.CSS);
		detailsPane.setBorder(BorderUIResource.getEtchedBorderUIResource());
	}

	protected String asHTMLLink(String url, String text) {
		return "<a href='" + url + "'>" + text + "</a>";
	}
	
	protected String getDetailsProperty(String name, String value, String color){
		StringBuilder buffer = new StringBuilder();
		if(value != null){
			buffer.append("<tr>");
			buffer.append("<td class=\"servBodL\" style=\"background-color:").append(color).append(
					";padding:7px;font-weight: bold;\" >");
			buffer.append(name);
			buffer.append("</td>");
			buffer.append("<td class=\"servBodL\" style=\"background-color:").append(color).append(";padding:7px;\" >");
			buffer.append(value);
			buffer.append("</td>");
			buffer.append("</tr>");
		}
		
		return buffer.toString();
	}

}
