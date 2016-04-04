package de.leipzig.imise.bioportal.ui;

import de.leipzig.imise.bioportal.rest.Entity;
import org.jdesktop.swingx.JXComboBox;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.semanticweb.owlapi.vocab.OWLRDFVocabulary.*;

public class DetailsPanel extends JPanel{
	
	private OWLEditorKit editorKit;
	
	Map<Entity, EntityDetailsTableModel> entity2Model = new HashMap<>();

	JLabel label;
	JTable table;
	
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
			model = new EntityDetailsTableModel(editorKit, entity);
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
		getApplicableProperties().forEach(comboBox::addItem);
		AutoCompleteDecorator.decorate(comboBox);
		mapToColumn.setCellEditor(new DefaultCellEditor(comboBox));

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		resizeColumnWidth(table);
		table.getColumnModel().getColumn(0).setMinWidth(25);
		table.getColumnModel().getColumn(0).setMaxWidth(25);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		table.getColumn(table.getColumnName(1)).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
														   boolean hasFocus,
														   int row, int column) {
				return super.getTableCellRendererComponent(table, ((OWLProperty)value).toStringID(), isSelected, hasFocus, row, column);
			}
		});

		table.getColumn(table.getColumnName(3)).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
														   boolean hasFocus,
														   int row, int column) {
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		});
	}

	private void createTable() {
		String popupLocation = "table.popupLocation";
		table = new JTable() {
			@Override
			public Point getPopupLocation(MouseEvent event) {
				((JComponent) event.getComponent()).putClientProperty(
						popupLocation, event != null ? event.getPoint() : null);
				return super.getPopupLocation(event);
			}
		};
		table.setShowGrid(false);
		table.setModel(new EntityDetailsTableModel(editorKit));

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
		getApplicableProperties().forEach(comboBox::addItem);
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
				SwingUtilities.invokeLater(() -> {
					int rowAtPoint = table.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), table));
					table.setValueAt(e.getSource(), rowAtPoint, 3);
					int colAtPoint = table.columnAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), table));
					if (rowAtPoint > -1) {
						table.setRowSelectionInterval(rowAtPoint, rowAtPoint);
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
		table.getColumnModel().getColumn(0).setMinWidth(25);
		table.getColumnModel().getColumn(0).setMaxWidth(25);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);

		table.getColumnModel().getColumn(2).setCellRenderer(new MultilineCellRenderer());

		add(scrollPane);


		JPopupMenu popup = new JPopupMenu();
		Action printLocation = new AbstractAction("Copy to clipboard") {

			@Override
			public void actionPerformed(ActionEvent e) {
				Point p = (Point) table.getClientProperty(popupLocation);
				System.out.println(p);
				if (p != null) { // popup triggered by mouse
					int row = table.rowAtPoint(p);
					int column = table.columnAtPoint(p);
					StringSelection entry = new StringSelection(table.getValueAt(table.convertRowIndexToView(row),
																				 table.convertRowIndexToView(column)).toString());
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(entry, entry);
				} else { // popup triggered otherwise
					// could choose f.i. by leadRow/ColumnSelection
				}
			}

		};
		popup.add(printLocation);
		table.setComponentPopupMenu(popup);


		table.setAutoCreateRowSorter(true);
		((TableRowSorter)table.getRowSorter()).sort();
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

//		properties.addAll(ont.getObjectPropertiesInSignature(Imports.INCLUDED));
//		properties.addAll(ont.getDataPropertiesInSignature(Imports.INCLUDED));
		properties.addAll(ont.getAnnotationPropertiesInSignature(Imports.INCLUDED));
		properties.addAll(
				OWLRDFVocabulary.asIRISet(RDFS_LABEL, RDFS_COMMENT, RDFS_SEE_ALSO, RDFS_IS_DEFINED_BY).stream().map(
						df::getOWLAnnotationProperty).collect(Collectors.toList()));
		
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
	
	public Map<OWLProperty, String> getSelectedValues(Entity entity) {
		Map<OWLProperty, String> selectedValues = new HashMap<>();
		EntityDetailsTableModel model = entity2Model.get(entity);
		for(int row = 0; row < model.getRowCount(); row++) {
			Boolean selectedCol = (Boolean) model.getValueAt(row, 0);
			if(selectedCol) {
				// get property
				OWLProperty property = (OWLProperty) model.getValueAt(row, 3);

				// if user did not select a different property, we use the one from the data
				if(property == null) {
					property = (OWLProperty) model.getValueAt(row, 1);
				}

				// get value
				String value = (String) model.getValueAt(row, 2);

				selectedValues.put(property, value);
			}
		}
		return selectedValues;
	}

	public boolean checkTable(Entity entity) {
		EntityDetailsTableModel model = entity2Model.get(entity);
		for(int row = 0; row < model.getRowCount(); row++) {
			Boolean selectedCol = (Boolean) model.getValueAt(row, 0);
			if(selectedCol) {
				// get property
				OWLProperty property = (OWLProperty) model.getValueAt(row, 3);

				if(property == null) {
					// highlight cell



					return false;
				}
			}
		}
		return true;
	}
}
