package de.leipzig.imise.bioportal.ui;

import de.leipzig.imise.bioportal.rest.Entity;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;
import java.util.Map.Entry;

public class DetailsPanel extends JPanel{
	
	private int entryNumber = 0;
	private int rowNumber = 0;
	private OWLEditorKit editorKit;
	
	private GridBagConstraints gbc = new GridBagConstraints();
	
	private Set<DetailsEntry> entries = new HashSet<DetailsEntry>();
	
	public DetailsPanel(Entity cb, OWLEditorKit editorKit) {
		this.editorKit = editorKit;
		Map<String, Object> relationsMap = cb.getAdditionalProperties();
		setLayout(new GridBagLayout());
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = 1;
		gbc.ipady = 5;
		
		addEntry("ID", cb.getId());
		addEntry("Preferred Name", cb.getPrefLabel());
		addEntry("Definitions", cb.getDefinition());
		addEntry("Synonyms", cb.getSynonym());
		
		
		for(Entry<String, Object> entry : relationsMap.entrySet()){
			if(entry.getValue() != null){
				addEntry(entry.getKey(), entry.getValue());
			}
		}
	}
	
	public Map<String, Set<Object>> getSelectedValues(){
		Map<String, Set<Object>> selectedValues = new HashMap<String, Set<Object>>();
		for (DetailsEntry e : entries) {
			selectedValues.putAll(e.getSelectedValues());
		}
		return selectedValues;
	}
	
	private void addEntry(String name, Object value){
		if(value != null && !(value instanceof Collection && ((Collection)value).isEmpty())){
			Color color = (entryNumber++ % 2 == 0) ? Color.WHITE : Color.LIGHT_GRAY;
			
			DetailsEntry entry = new DetailsEntry(name, value, color, editorKit, this);
			entries.add(entry);
			
			int numberOfValues = entry.getNumberOfValues();
			gbc.gridx = 0;
			gbc.gridy = rowNumber-numberOfValues;
			gbc.gridheight = numberOfValues;
			if(numberOfValues > 0){
				JLabel nameLabel = new JLabel(name);
				nameLabel.setOpaque(true);
				nameLabel.setBackground(color);
				Font font = new Font(nameLabel.getFont().getName(), Font.BOLD, nameLabel.getFont().getSize());
				nameLabel.setFont(font);
				add(nameLabel, gbc);
			}
			gbc.gridheight = 1;
			
		}
	}
	
	
	class DetailsEntry {
		
		private String name;
		private Map<Object, JCheckBox> value2CheckBox = new HashMap<Object, JCheckBox>();
		private Map<Object, JComboBox<OWLEntity>> value2Combobox = new HashMap<Object, JComboBox<OWLEntity>>();
		private OWLEditorKit editorKit;
		private Collection<Object> values;
		
		private Map<Object, OWLAnnotationProperty> value2Property = new HashMap<Object, OWLAnnotationProperty>();
		
		public DetailsEntry(String name, Object value, Color color, OWLEditorKit editorKit, JPanel parent) {
			this.name = name;
			this.editorKit = editorKit;
			
			if(value instanceof Collection) {
				values = (Collection)value;
			} else {
				values = Collections.singleton(value);
			}
			for (Object val : values) {
				addRow(parent, val, color);
			}
		}
		
		public Map<String, Set<Object>> getSelectedValues(){
			
			Map<String, Set<Object>> selected = new HashMap<String, Set<Object>>();
			Set<Object> selectedValues = new HashSet<Object>();
			for(Entry<Object, JCheckBox> entry : value2CheckBox.entrySet()){
				if(entry.getValue().isSelected()){
					selectedValues.add(entry.getKey());
					System.out.println(value2Combobox.get(entry.getKey()).getSelectedItem());
				}
			}
			System.out.println("Get selected values of " + name + ": " + selectedValues);
			selected.put(name, selectedValues);
			return selected;
		}
		
		public int getNumberOfValues(){
			return values.size();
		}
		
		private void addRow(JPanel parent, Object value, final Color color){
			
			gbc.gridx = 1;
			gbc.gridy = rowNumber++;
			JLabel valueLabel = new JLabel(value.toString());
			valueLabel.setOpaque(true);
			valueLabel.setBackground(color);
			parent.add(valueLabel, gbc);
			
			gbc.gridx = 2;
			gbc.weightx = 1;
			final JCheckBox checkbox = new JCheckBox();
			checkbox.setBackground(color);
			final JLabel dummy = new JLabel();
			JPanel wrapper = new JPanel();
			wrapper.setBackground(color);
			wrapper.setLayout(new GridBagLayout());
			parent.add(wrapper, gbc);
			
			final JPanel propertySelectionPanel = new JPanel();
			propertySelectionPanel.setBackground(color);
			propertySelectionPanel.setOpaque(false);
			propertySelectionPanel.add(new JLabel(" as "));
			final JComboBox<String> propertyTypeBox = new JComboBox<String>(new String[]{"Object Property", "Data Property", "Annotation Property"});
			
			propertyTypeBox.setBackground(color);
			propertyTypeBox.setRenderer(new DefaultListCellRenderer(){
				@Override
				public Component getListCellRendererComponent(JList<?> list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					Component c = super.getListCellRendererComponent(list, value, index, isSelected,
							cellHasFocus);
					if(!isSelected){
						c.setBackground(color);
					}
					return c;
				}
			});
			propertySelectionPanel.add(propertyTypeBox);
			propertySelectionPanel.setVisible(false);
			
			final JComboBox<OWLEntity> propertiesBox = new JComboBox<OWLEntity>();
			value2Combobox.put(value, propertiesBox);
			propertiesBox.setBackground(color);
			OWLOntology ont = editorKit.getOWLModelManager().getActiveOntology();
				for(OWLObjectProperty op : ont.getObjectPropertiesInSignature(true)){
					propertiesBox.addItem(op);
				}
			propertySelectionPanel.add(propertiesBox);
			
			checkbox.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					dummy.setVisible(!checkbox.isSelected());
					propertySelectionPanel.setVisible(checkbox.isSelected());
//					propertySelectionPanel.setEnabled(checkbox.isSelected());
					revalidate();
				}
			});
			
			propertyTypeBox.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent e) {
					String item = e.getItem().toString();
					propertiesBox.removeAllItems();
					OWLOntology ont = editorKit.getOWLModelManager().getActiveOntology();
					if(item.equals("Object Property")){
						for(OWLObjectProperty op : ont.getObjectPropertiesInSignature(true)){
							propertiesBox.addItem(op);
						}
					} else if(item.equals("Data Property")){
						for(OWLDataProperty dp : ont.getDataPropertiesInSignature(true)){
							propertiesBox.addItem(dp);
						}
					} else {
						for(OWLAnnotationProperty ap : ont.getAnnotationPropertiesInSignature()){
							propertiesBox.addItem(ap);
						}
						for(IRI iri : OWLRDFVocabulary.BUILT_IN_ANNOTATION_PROPERTY_IRIS){
							propertiesBox.addItem(editorKit.getOWLModelManager().getOWLDataFactory().getOWLAnnotationProperty(iri));
						}
					}
				}
			});
			gbc.weightx = 0;
			gbc.gridx = 0;
			gbc.gridy = 0;
			wrapper.add(checkbox, gbc);
			gbc.weightx = 1;
			gbc.gridx = 1;
			wrapper.add(dummy, gbc);
			gbc.gridx = 2;
			gbc.fill = GridBagConstraints.NONE;
			wrapper.add(propertySelectionPanel, gbc);
			
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 0;
			gbc.gridx = 0;
			gbc.gridy = 0;
			
			value2CheckBox.put(value, checkbox);
		}
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
