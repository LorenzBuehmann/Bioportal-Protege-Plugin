package de.leipzig.imise.bioportal.ui;

import de.leipzig.imise.bioportal.rest.BioportalRESTService;
import de.leipzig.imise.bioportal.rest.Entity;
import org.semanticweb.owlapi.model.OWLProperty;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Lorenz Buehmann
 */
public class EntityDetailsTableModel extends AbstractTableModel{

	List<List<Object>> data = new ArrayList<>();

	public EntityDetailsTableModel() {}

	public EntityDetailsTableModel(Entity entity) {
//		addRow("ID", entity.getId());
		addRow("Preferred Label", entity.getPrefLabel());
		if(entity.getDefinition() != null) {
			List<String> definitions = entity.getDefinition();
			if(!definitions.isEmpty()) {
				int i = 1;
				for (String definition : definitions) {
					addRow("Definition(" + i++ + ")", definition);
				}
			}
		}

		for(Map.Entry<String, Object> entry : entity.getAdditionalProperties().entrySet()){
			String relation = entry.getKey();

			if(!BioportalRESTService.META_PROPERTIES.contains(relation) && entry.getValue() != null){
				addRow(relation, entry.getValue());
			}
		}
	}

	private void addRow(Object ... values) {
		List<Object> row = new ArrayList<>(4);
		row.add(false);
		row.add(1, values[0]);
		row.add(2, values[1]);
		row.add(null);
		row.add(null);
		data.add(row);
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data.get(rowIndex).get(columnIndex);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		data.get(rowIndex).set(columnIndex, aValue);
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
			case 0:return Boolean.class;
			case 3:return OWLProperty.class;
		}
		return super.getColumnClass(columnIndex);
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0: return "";
			case 1: return "Property";
			case 2: return "Value";
			case 3: return "Map To";
		}
		return super.getColumnName(column);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0 || columnIndex == 3;
	}
}