package de.leipzig.imise.bioportal.ui;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Sets;
import de.leipzig.imise.bioportal.rest.BioportalRESTService;
import de.leipzig.imise.bioportal.rest.Entity;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.SKOS;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLProperty;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * @author Lorenz Buehmann
 */
public class EntityDetailsTableModel extends AbstractTableModel{

	private Set<String> toIgnore = Sets.newHashSet(
			"id",
			"subClassOf",
			"parents",
			"prefixIRI",
			"http://www.w3.org/2000/01/rdf-schema#subClassOf",
			"http://www.w3.org/1999/02/22-rdf-syntax-ns#type");

	private String[] columnNames = { "", "Property", "Value", "Map To" };

	private List<List<Object>> data = new ArrayList<>();
	private OWLEditorKit editorKit;

	public EntityDetailsTableModel(OWLEditorKit editorKit) {
		this.editorKit = editorKit;
	}

	public EntityDetailsTableModel(OWLEditorKit editorKit, Entity entity) {
		this(editorKit);

//		Map<String, Object> properties = entity.getAdditionalProperties();
		Map<String, Object> properties = (Map<String, Object>) entity.getAdditionalProperties().get("properties");

		Map<String, Object> properties2 = new TreeMap<>(new NameSpacePreferenceComparator());
		properties2.putAll(properties);
		properties = properties2;

		for(Map.Entry<String, Object> entry : properties.entrySet()){
			String relation = entry.getKey();
			Object value = entry.getValue();

			if (!canBeIgnored(relation) && entry.getValue() != null) {

				if (value instanceof Collection) {
					Collection coll = (Collection) value;

					for (Object val : coll) {
						addRow(relation, val);
					}
				} else if(value instanceof Map){ // properties are nested in a map, thus, we have to process it here
					Map<Object, Object> map = (Map) value;

					for (Map.Entry<Object, Object> entry2 : map.entrySet()) {
						String relation2 = (String) entry2.getKey();
						Object value2 = entry2.getValue();

						if(!canBeIgnored(relation2)) {
							if (value2 instanceof Collection) {
								Collection coll2 = (Collection) value2;

								for (Object val2 : coll2) {
									addRow(relation2, val2);
								}
							} else {
								addRow(relation2, value2);
							}
						}
					}
				} else {
					addRow(relation, value);
				}
			}
		}
	}

	private boolean canBeIgnored(String relation) {
		return BioportalRESTService.META_PROPERTIES.contains(relation) ||
				relation.startsWith(BioportalRESTService.META_PROPERTY_NS) ||
				toIgnore.contains(relation);
	}

	private void addRow(Object ... values) {
		List<Object> row = new ArrayList<>(4);
		row.add(false);
		OWLAnnotationProperty property = editorKit.getOWLModelManager().getOWLDataFactory().getOWLAnnotationProperty(
				IRI.create(values[0].toString()));
		row.add(1, property);
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
		return columnNames.length;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

//	@Override
//	public Class<?> getColumnClass(int columnIndex) {
//		if (data.isEmpty()) {
//			return Object.class;
//		}
//		return getValueAt(0, columnIndex).getClass();
//	}

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
			case 1:return OWLProperty.class;
			case 3:return OWLProperty.class;
		}
		return super.getColumnClass(columnIndex);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0 || columnIndex == 3;
	}

	class NameSpacePreferenceComparator implements Comparator<String> {

		@Override
		public int compare(String o1, String o2) {
			return ComparisonChain.start()
					.compareTrueFirst(o1.startsWith(RDFS.NAMESPACE), o2.startsWith(RDFS.NAMESPACE))
					.compareTrueFirst(o1.startsWith(SKOS.NAMESPACE), o2.startsWith(SKOS.NAMESPACE))
					.compare(o1, o2)
					.result();
		}
	}
}
