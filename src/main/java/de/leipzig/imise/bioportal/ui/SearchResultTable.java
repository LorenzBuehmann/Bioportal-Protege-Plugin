package de.leipzig.imise.bioportal.ui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

import de.leipzig.imise.bioportal.rest.Entity;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class SearchResultTable extends JXTable {
	
	private static final long serialVersionUID = -7673941614799822286L;
	private String searchTerm;

	public SearchResultTable(){
		super(new SearchResultTableModel());
		addHighlighter(HighlighterFactory.createAlternateStriping());
		getColumn(4).setMaxWidth(30);
//		getColumn(5).setMaxWidth(30);

		// renderer to highlight the search term in the entity label
		getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
														   boolean hasFocus,
														   int row, int column) {
				Component c =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if(column == 0) {
					JLabel label = (JLabel) c;
					label.setText("<html>" + label.getText().replaceAll("(?i)(" + searchTerm + ")", "<b>$1</b>") + "</html>");
				}
				return c;
			}
		});
	}

	/**
	 * Set the entities found for the given search term.
	 * @param searchTerm the search term
	 * @param searchResults the entities
	 */
	public void setSearchResults(String searchTerm, List<Entity> searchResults){
		this.searchTerm = searchTerm;
		((SearchResultTableModel)getModel()).setSearchResults(searchResults);
	}

	/**
	 * Get the entity for the given row.
	 * @param row the row
	 * @return the entity
	 */
	public Entity getEntity(int row){
		return ((SearchResultTableModel)getModel()).getSearchBean(row);
	}
	
	@Override
	public String getToolTipText(MouseEvent e) {
		String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);
        int realColumnIndex = convertColumnIndexToModel(colIndex);
        if(rowIndex >= 0){
			Entity entity = getEntity(rowIndex);
	        
	        switch(realColumnIndex){
				case 0: tip = "ID:" + entity.getId() + " (click to see entity details)"; break;
				case 1: tip = "Name:" + entity.getPrefLabel() + " (click to see entity details)"; break;
				case 2: tip = entity.getEntityLinks().getOntology() + " (click to see ontology details)"; break;
				case 3: tip = "Found term " + entity.getId() + " in " + getValueAt(rowIndex, realColumnIndex); break;
				case 4: tip = "Go to Import dialog."; break;
				case 5: tip = ""; break;
				default: return super.getToolTipText(e);
	        }
        }
        return tip;
	}
	
	@Override
	protected void processMouseMotionEvent(MouseEvent e) {
		int column = columnAtPoint(e.getPoint());
		if(column == 0 ||  column == 2 || column >= 4){
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			setCursor(Cursor.getDefaultCursor());
		}
		super.processMouseMotionEvent(e);
	}
}
