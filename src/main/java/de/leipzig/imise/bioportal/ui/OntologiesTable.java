package de.leipzig.imise.bioportal.ui;

import de.leipzig.imise.bioportal.BioportalManager;
import de.leipzig.imise.bioportal.rest.Ontology;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.NotHighlightPredicate;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.StringValue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class OntologiesTable extends JXTable {
	
	private static final long serialVersionUID = 5269788903940060647L;

	public OntologiesTable(){
		super(new OntologiesTableModel());
		setTableHeader(null);
//		setRolloverEnabled(true);
//		addHighlighter(HighlighterFactory.createAlternateStriping());
//		addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, 
//			      Color.CYAN, Color.BLACK)); 
		HighlightPredicate selectedPredicate = (renderer, adapter) -> {
			int modelColumn = adapter.getColumnIndex("selected");
			return (Boolean) adapter.getValue(modelColumn);
		};
        setHighlighters(new CompoundHighlighter(
        		new ColorHighlighter(selectedPredicate, Color.RED, Color.WHITE), 
        		new CompoundHighlighter(
        				new NotHighlightPredicate(selectedPredicate), new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, Color.CYAN, Color.BLACK))));
		getColumn(0).setMinWidth(20);
		getColumn(0).setMaxWidth(20);
		org.jdesktop.swingx.renderer.StringValue sv = (StringValue) value -> ((Ontology)value).getName();
		 setDefaultRenderer(Ontology.class, new DefaultTableRenderer(sv));
//		getColumn(1).setCellRenderer(new DefaultTableCellRenderer(){
//			@Override
//			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
//					boolean hasFocus, int row, int column) {
//				JLabel c = (JLabel) super.getTableCellRendererComponent(table,
//                        value,
//                        isSelected,
//                        hasFocus,
//                        row,
//                        column);
//				c.setBackground(null);
//				c.setText(((OntologyBean)value).getDisplayLabel());
//				if(BioportalManager.getInstance().isSelectedOntology((OntologyBean)value)){
//					c.setBackground(Color.RED);
//				}
//				return c;
//			}
//		});
//		setDefaultRenderer(OntologyBean.class, new DefaultTableCellRenderer(){
//			@Override
//			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
//					boolean hasFocus, int row, int column) {
//				JLabel c = (JLabel) super.getTableCellRendererComponent(table,
//                        value,
//                        isSelected,
//                        hasFocus,
//                        row,
//                        column);
//				c.setBackground(null);
//				c.setText(((OntologyBean)value).getDisplayLabel());
//				if(BioportalManager.getInstance().isSelectedOntology((OntologyBean)value)){
//					c.setBackground(Color.RED);
//				}
//				return c;
//			}
//		});
		
	}
	
	public void setOntologies(List<Ontology> ontologies){
		((OntologiesTableModel)getModel()).setOntologies(ontologies);
	}
	
	@Override
	public String getToolTipText(MouseEvent e) {
		String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        if(rowIndex >= 0){
	        tip = getValueAt(rowIndex, 1).toString();
        }
        return tip;
	}

	public static void main(String[] args) {
		JFrame test = new JFrame();
		test.setSize(800, 800);
		test.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
		
		OntologiesTable t = new OntologiesTable();
		JScrollPane scroll = new JScrollPane(t);
		scroll.setPreferredSize(new Dimension(200, 120));
		
//		JPanel holderPanel = new JPanel();
//		holderPanel.setLayout(new GridBagLayout());
//		GridBagConstraints gbc = new GridBagConstraints();
//		holderPanel.add(scroll);
//		
//		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//		splitPane.setBorder(new TitledBorder("Search in Bioportal ontologies"));
//		splitPane.setRightComponent(holderPanel);
//		splitPane.setDividerLocation(0.5);
		
		test.add(scroll);
		
		List<Ontology> result = new ArrayList<>(BioportalManager.getInstance().getOntologies());
		t.setOntologies(result);
		
		test.setVisible(true);
		
	}

}
