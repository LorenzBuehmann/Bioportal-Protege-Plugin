package de.leipzig.imise.bioportal.ui;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.util.List;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.ncbo.stanford.bean.search.SearchBean;

public class SearchResultTable extends JXTable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7673941614799822286L;

	public SearchResultTable(){
		super(new SearchResultTableModel());
		addHighlighter(HighlighterFactory.createAlternateStriping());
		getColumn(4).setMaxWidth(30);
//		getColumn(5).setMaxWidth(30);
	}
	
	public void setSearchResults(List<SearchBean> searchResults){
		((SearchResultTableModel)getModel()).setSearchResults(searchResults);
	}
	
	public SearchBean getSearchBean(int row){
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
	        SearchBean searchBean = getSearchBean(rowIndex);
	        
	        switch(realColumnIndex){
				case 0: tip = searchBean.getContents() + " (click to view in Bioportal)"; break;
				case 1: tip = searchBean.getConceptIdShort(); break;
				case 2: tip = searchBean.getOntologyDisplayLabel() + " (click to see ontology details)"; break;
				case 3: tip = "Found term " + searchBean.getContents() + " in " + getValueAt(rowIndex, realColumnIndex); break;
				case 4: tip = "Open the details window for " + searchBean.getContents(); break;
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
