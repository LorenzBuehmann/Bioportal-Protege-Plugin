package de.leipzig.imise.bioportal.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import de.leipzig.imise.bioportal.bean.concept.ClassBean;

public class DetailsPanel2 extends JPanel{
	
	private int row = 0;
	private OWLEditorKit editorKit;
	
	public DetailsPanel2(ClassBean cb, OWLEditorKit editorKit) {
		this.editorKit = editorKit;
		Map<Object, Object> relationsMap = cb.getRelations();
//		setLayout(new GridLayout(relationsMap.size()+5, 2));
		setLayout(new GridBagLayout());
		
		addEntry("Id", cb.getId());
		addEntry("Full Id", cb.getFullId());
		addEntry("Label", cb.getLabel());
		addEntry("Definitions", cb.getDefinitions());
		addEntry("Synonyms", cb.getSynonyms());
		
		for(Entry<Object, Object> entry : relationsMap.entrySet()){
			if(entry.getValue() != null){
				addEntry(entry.getKey().toString(), entry.getValue());
			}
		}
		
	}
	
	private void addEntry(String name, Object value){
		if(value != null && !(value instanceof Collection && ((Collection)value).isEmpty())){
			Color color = (row++ % 2 == 0) ? Color.WHITE : Color.LIGHT_GRAY;
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 1f;
			gbc.weighty = 1f;
			
			JLabel nameLabel = new JLabel(name);
			nameLabel.setOpaque(true);
			nameLabel.setBackground(color);
			Font font = new Font(nameLabel.getFont().getName(), Font.BOLD, nameLabel.getFont().getSize());
			nameLabel.setFont(font);
			add(nameLabel, gbc);
			
			DetailsEntry entry = new DetailsEntry(name, value, color, editorKit);
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			add(entry, gbc);
		}
	}
	
	
	class DetailsEntry extends JPanel{
		
		private String name;
		private Map<Object, JCheckBox> value2CheckBox = new HashMap<Object, JCheckBox>();
		private OWLEditorKit editorKit;
		
		public DetailsEntry(String name, Object value, Color color, OWLEditorKit editorKit) {
			this.editorKit = editorKit;
			setBackground(color);
			
			Collection<Object> values;
			if(value instanceof Collection) {
				values = (Collection)value;
			} else {
				values = Collections.singleton(value);
			}
			setLayout(new GridLayout(values.size(), 2));
			for (Object val : values) {
				addRow(val, color);
			}
		}
		
		private void addRow(Object value, Color color){
			addRow(this, value, color);
		}
		
		private void addRow(JPanel parent, Object value, final Color color){
			GridBagConstraints gbc = new GridBagConstraints();
			JLabel valueLabel = new JLabel(value.toString());
//			valueLabel.setOpaque(true);
			valueLabel.setBackground(Color.RED);
			parent.add(valueLabel);
			
			JPanel p = new JPanel(new GridBagLayout());
			p.setOpaque(false);
			parent.add(p);
			
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.anchor = GridBagConstraints.LINE_START;
			
			final JCheckBox checkbox = new JCheckBox();
			checkbox.setOpaque(false);
			checkbox.setBackground(Color.GREEN);
			p.add(checkbox, gbc);
			
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.BOTH;
			final JPanel propertySelectionPanel = new JPanel();
			propertySelectionPanel.setPreferredSize(new Dimension(600,50));
			propertySelectionPanel.setOpaque(false);
			propertySelectionPanel.setEnabled(false);
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
			OWLOntology ont = editorKit.getOWLModelManager().getActiveOntology();
				for(OWLObjectProperty op : ont.getObjectPropertiesInSignature(true)){
					propertiesBox.addItem(op);
				}
			propertySelectionPanel.add(propertiesBox);
			
			checkbox.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
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
			
			p.add(propertySelectionPanel, gbc);
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
