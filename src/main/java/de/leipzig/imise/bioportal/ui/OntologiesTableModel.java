package de.leipzig.imise.bioportal.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.leipzig.imise.bioportal.BioportalManager;
import de.leipzig.imise.bioportal.bean.ontologies.OntologyBean;

public class OntologiesTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 6494904083408386403L;
	private List<OntologyBean> ontologies = new ArrayList<OntologyBean>();

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return ontologies.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		if(column == 0){
			return BioportalManager.getInstance().getSelectedOntologies().contains(ontologies.get(row));
		} else {
			return ontologies.get(row);
		}
	}
	
	@Override
	public void setValueAt(Object value, int row, int column) {
		if(column == 0){
			BioportalManager.getInstance().setOntologySelected(ontologies.get(row), (Boolean)value);
			fireTableDataChanged();
		}
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0;
	}
	
	@Override
	public Class<?> getColumnClass(int column) {
		if(column == 0){
			return Boolean.class;
		} else {
			return OntologyBean.class;
		}
	}
	
	@Override
	public String getColumnName(int column) {
		if(column == 0){
			return "selected";
		}
		return null;
	}
	
	public void setOntologies(List<OntologyBean> ontologies){
		this.ontologies.clear();
		this.ontologies.addAll(ontologies);
		fireTableDataChanged();
	}

}
