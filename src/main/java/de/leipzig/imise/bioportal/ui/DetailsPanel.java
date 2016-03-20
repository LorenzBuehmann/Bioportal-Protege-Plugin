package de.leipzig.imise.bioportal.ui;

import de.leipzig.imise.bioportal.rest.Entity;
import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DetailsPanel extends JPanel{
	
	private int entryNumber = 0;
	private int rowNumber = 0;
	private OWLEditorKit editorKit;
	
	private GridBagConstraints gbc = new GridBagConstraints();
	
	Map<Entity, EntityDetailsTableModel> entity2Model = new HashMap<>();

	JLabel label;
	JXTable table;
	
	public DetailsPanel(OWLEditorKit editorKit) {
		this.editorKit = editorKit;
		setLayout(new BorderLayout());

		// entity ID
		label = new JLabel("Entity: ");
		add(label, BorderLayout.NORTH);

		// table with properties
		createTable();
	}

	public void showDetails(Entity entity) {
		label.setText("Entity: " + entity.getId());

		EntityDetailsTableModel model = entity2Model.get(entity);
		if(model == null) {
			model = new EntityDetailsTableModel(entity);
			entity2Model.put(entity, model);
		}
		table.setModel(model);

		TableColumn mapToColumn = table.getColumnModel().getColumn(3);
		JXComboBox comboBox = new JXComboBox();
//		comboBox.setRenderer(new DefaultListCellRenderer() {
////			Icon primitiveClassIcon = new OWLClassIcon(OWLClassIcon.Type.PRIMITIVE);
//
//			@Override
//			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
//														  boolean cellHasFocus) {
//				JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
////				label.setIcon(primitiveClassIcon);
//				return label;
//			}
//		});
		for(OWLProperty p : getApplicableProperties()){
			comboBox.addItem(p);
		}
		AutoCompleteDecorator.decorate(comboBox);
		mapToColumn.setCellEditor(new DefaultCellEditor(comboBox));

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		resizeColumnWidth(table);
		table.getColumn(0).setMinWidth(25);
		table.getColumn(0).setMaxWidth(25);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	}

	private void createTable() {
		table = new JXTable();
		table.setShowGrid(false);
		table.setModel(new EntityDetailsTableModel());

		TableColumn mapToColumn = table.getColumnModel().getColumn(3);
		JXComboBox comboBox = new JXComboBox();
//		comboBox.setRenderer(new DefaultListCellRenderer() {
////			Icon primitiveClassIcon = new OWLClassIcon(OWLClassIcon.Type.PRIMITIVE);
//
//			@Override
//			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
//														  boolean cellHasFocus) {
//				JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
////				label.setIcon(primitiveClassIcon);
//				return label;
//			}
//		});
		for(OWLProperty p : getApplicableProperties()){
			comboBox.addItem(p);
		}
		AutoCompleteDecorator.decorate(comboBox);
		mapToColumn.setCellEditor(new DefaultCellEditor(comboBox));

		final JPopupMenu popupMenu = createPropertiesMenu();
//		JMenuItem deleteItem = new JMenuItem("Delete");
//		deleteItem.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				JOptionPane.showMessageDialog(DetailsPanel.this.getParent(), "Right-click performed on table and choose DELETE");
//			}
//		});
//		popupMenu.add(deleteItem);
		popupMenu.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						int rowAtPoint = table.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), table));
						table.setValueAt(e.getSource(), rowAtPoint, 3);
						int colAtPoint = table.columnAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), table));
						if (rowAtPoint > -1) {
							table.setRowSelectionInterval(rowAtPoint, rowAtPoint);
						}
					}
				});
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});
//		table.setComponentPopupMenu(popupMenu);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		resizeColumnWidth(table);
		table.getColumn(0).setMinWidth(25);
		table.getColumn(0).setMaxWidth(25);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);

		add(scrollPane);
	}

	private void resizeColumnWidth(JTable table) {
		final TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {
			int width = 50; // Min width
			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer renderer = table.getCellRenderer(row, column);
				Component comp = table.prepareRenderer(renderer, row, column);
				width = Math.max(comp.getPreferredSize().width +1 , width);
			}
			columnModel.getColumn(column).setPreferredWidth(width);
		}
	}

	private Set<OWLProperty> getApplicableProperties() {
		Set<OWLProperty> properties = new TreeSet<>();

		OWLOntology ont = editorKit.getOWLModelManager().getActiveOntology();
		OWLDataFactory df = editorKit.getOWLModelManager().getOWLDataFactory();

		properties.addAll(ont.getObjectPropertiesInSignature(Imports.INCLUDED));
		properties.addAll(ont.getDataPropertiesInSignature(Imports.INCLUDED));
		properties.addAll(ont.getAnnotationPropertiesInSignature(Imports.INCLUDED));
		for(IRI iri : OWLRDFVocabulary.BUILT_IN_ANNOTATION_PROPERTY_IRIS){
			properties.add(df.getOWLAnnotationProperty(iri));
		}
		
		return properties;
	}

	private JPopupMenu createPropertiesMenu() {
		JPopupMenu menu = new JPopupMenu();

		OWLOntology ont = editorKit.getOWLModelManager().getActiveOntology();

		JMenu m1 = new JMenu(EntityType.OBJECT_PROPERTY.getPrintName());
		for (OWLObjectProperty p : ont.getObjectPropertiesInSignature(Imports.INCLUDED)) {
			m1.add(new JMenuItem(p.toStringID()));
		}
		menu.add(m1);

		JMenu m2 = new JMenu(EntityType.DATA_PROPERTY.getPrintName());
		for (OWLDataProperty p : ont.getDataPropertiesInSignature(Imports.INCLUDED)) {
			m2.add(new JMenuItem(p.toStringID()));
		}
		menu.add(m2);

		JMenu m3 = new JMenu(EntityType.ANNOTATION_PROPERTY.getPrintName());
		for (OWLAnnotationProperty p : ont.getAnnotationPropertiesInSignature(Imports.INCLUDED)) {
			m3.add(new JMenuItem(p.toStringID()));
		}
		for(IRI iri : OWLRDFVocabulary.BUILT_IN_ANNOTATION_PROPERTY_IRIS){
			m3.add(new JMenuItem(iri.toString()));
		}
		menu.add(m3);

		return menu;
	}
	
	public Map<String, Set<Object>> getSelectedValues(){
		Map<String, Set<Object>> selectedValues = new HashMap<String, Set<Object>>();

		return selectedValues;
	}

	public Map<OWLProperty, String> getSelectedValues(Entity entity){
		Map<OWLProperty, String> selectedValues = new HashMap<>();
		EntityDetailsTableModel model = entity2Model.get(entity);
		for(int i = 0; i < model.getRowCount(); i++) {
			Boolean selectedCol = (Boolean) model.getValueAt(i, 0);
			if(selectedCol) {
				// get property
				OWLProperty property = (OWLProperty) model.getValueAt(i, 3);
				// get value
				String value = (String) model.getValueAt(i, 2);

				selectedValues.put(property, value);
			}
		}
		return selectedValues;
	}

	public static void main(String[] args) {
		GridBagConstraints gbc = new GridBagConstraints();
		
		JPanel main  = new JPanel();
		main.setLayout(new GridBagLayout());
		
		gbc.gridy = 0;
		gbc.gridx = 1;
		main.add(new JLabel("Koala"), gbc);
		gbc.gridx = 2;
		main.add(new JCheckBox(), gbc);
		gbc.gridy = 1;
		gbc.gridx = 1;
		main.add(new JLabel("Koala"), gbc);
		gbc.gridx = 2;
		main.add(new JCheckBox(), gbc);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 2;
		main.add(new JLabel("SubClassOf"), gbc);
		
		gbc.gridy = 2;
		gbc.gridx = 1;
		main.add(new JLabel("Koala"), gbc);
		gbc.gridx = 2;
		main.add(new JCheckBox(), gbc);
		gbc.gridy = 3;
		gbc.gridx = 1;
		main.add(new JLabel("Koala"), gbc);
		gbc.gridx = 2;
		main.add(new JCheckBox(), gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridheight = 2;
		main.add(new JLabel("SubClassOf"), gbc);
		
		
		
		
		
		
		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.add(main, BorderLayout.CENTER);
		frame.setSize(new Dimension(400, 400));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}

}
