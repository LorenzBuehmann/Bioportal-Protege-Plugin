package de.leipzig.imise.bioportal.ui;

import de.leipzig.imise.bioportal.BioportalViewComponent;
import de.leipzig.imise.bioportal.rest.Entity;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SearchResultTableModel extends AbstractTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1006389200163898235L;
	private static final Icon showDetailsIcon = new ImageIcon(BioportalViewComponent.class.getClassLoader().getResource("details.png"));
	private static final Icon extractIcon = new ImageIcon(BioportalViewComponent.class.getClassLoader().getResource("extract.png"));
	private List<Entity> searchResults = new ArrayList<>();

	public SearchResultTableModel() {

	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public int getRowCount() {
		return searchResults.size();
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	@Override
	public Class<?> getColumnClass(int column) {
		if(column > 3){
			return Icon.class;
		} else {
			return super.getColumnClass(column);
		}
	}
	
	@Override
	public String getColumnName(int column) {
		switch(column){
			case 0: return "Term name";
			case 1: return "Term ID";
			case 2: return "Ontology";
			case 3: return "Found in";
			case 4: return "";
			case 5: return "";
			default: return super.getColumnName(column);
		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		Entity searchBean = searchResults.get(row);
		switch(column){
			case 0: return searchBean.getPrefLabel();
			case 1: return  getURLString(searchBean.getId());
			case 2: return getURLString(searchBean.getEntityLinks().getOntology());
			case 3: return searchBean.getMatchType();
			case 4: return showDetailsIcon;
			case 5: return extractIcon;
			default: return null;
		}
	}
	
	public void setSearchResults(List<Entity> searchResults){
		this.searchResults.clear();
		this.searchResults.addAll(searchResults);
		fireTableDataChanged();
	}
	
	public Entity getSearchBean(int row){
		return searchResults.get(row);
	}
	
	private String getURLString(String url){
		return "<html><u><font color='blue'>" + url + "</u></html>";
	}
	
	private String getRecordTypeString(String recordType){
		StringBuffer sb = new StringBuffer();
		String[] temp = recordType.substring(12).split("_");
		for(String split : temp){
			sb.append(split.charAt(0));
			sb.append(split.substring(1).toLowerCase());
			sb.append(" ");
		}
		
		return sb.toString().trim();
	}

}