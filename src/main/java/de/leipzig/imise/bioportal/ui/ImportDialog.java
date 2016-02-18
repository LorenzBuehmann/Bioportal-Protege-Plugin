package de.leipzig.imise.bioportal.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.IconUIResource;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.util.BioPortalUtil;
import org.ncbo.stanford.util.HTMLUtil;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.progress.BackgroundTask;
import org.protege.editor.core.ui.util.NativeBrowserLauncher;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import de.leipzig.imise.bioportal.BioportalRESTServices;
import de.leipzig.imise.bioportal.bean.concept.ClassBean;

public class ImportDialog extends JDialog {

	private OWLEditorKit editorKit;
	private JTree classTree;
	private JEditorPane detailsPane;
	private SearchBean searchBean;
	
	private Map<ClassBean, Set<String>> node2SelectedRelations = new HashMap<ClassBean, Set<String>>();
	
	private Map<ClassBean, DetailsPanel> node2Details = new HashMap<ClassBean, DetailsPanel>();
	
	private ClassBean lastSelected = null;
	private DetailsPanel detailsPanel;
	private JScrollPane scroll;

	public ImportDialog(ClassBean cb, SearchBean searchBean, final OWLEditorKit editorKit) {
		this.searchBean = searchBean;
		this.editorKit = editorKit;

		setLayout(new BorderLayout());
		setTitle("Import classes from " + searchBean.getOntologyDisplayLabel());
		setModal(true);
		setSize(600, 600);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		classTree = createClassTree(cb);
		add(classTree, BorderLayout.WEST);

//		JPanel detailsPanel = createDetailsPanel();
//		add(detailsHolder, BorderLayout.EAST);

		JButton importButton = new JButton("Import");
		importButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				onImportClasses();
			}
		});
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(importButton, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
	}

	private JTree createClassTree(final ClassBean root) {
		UIManager.put("Tree.collapsedIcon", new IconUIResource(new NodeIcon('+')));
		UIManager.put("Tree.expandedIcon", new IconUIResource(new NodeIcon('-')));
		Collection<ClassBean> children = (Collection<ClassBean>) root.getRelations().get(ClassBean.SUB_CLASS_PROPERTY);
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);
		if(children != null){
			for (ClassBean child : children) {
				Integer childCount = (Integer) child.getRelations().get("ChildCount");
				boolean allowChildren = childCount > 0;
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
				if (allowChildren) {
					childNode.add(new DefaultMutableTreeNode("Loading children..."));
				}
				rootNode.add(childNode);
			}
		}
		final JTree tree = new JTree(rootNode);
		tree.addTreeWillExpandListener(new TreeWillExpandListener() {

			@Override
			public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
				TreePath path = e.getPath();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
				ExpandTreeWorker expandTreeTask = new ExpandTreeWorker(node, searchBean.getOntologyVersionId(),
						(DefaultTreeModel) tree.getModel());
				ProtegeApplication.getBackgroundTaskManager().startTask(expandTreeTask);
				editorKit.getWorkspace().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				expandTreeTask.execute();
			}

			@Override
			public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {
				// TODO Auto-generated method stub

			}
		});
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath[] paths = e.getPaths();
				for (int i=0; i<paths.length; i++) {
					if (e.isAddedPath(i)) {tree.repaint();
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
						ClassBean cb = (ClassBean)node.getUserObject();
						if(!cb.getRelations().containsKey(ClassBean.SUB_CLASS_PROPERTY)){
							editorKit.getWorkspace().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							new LoadPropertiesWorker(node, searchBean.getOntologyVersionId()).execute();
						} else {
							onShowDetails(cb);
						}
						
						break;
					} else {
						// This node has been deselected
						break;
					}
				}
				
			}
		});
//		tree.setCellRenderer(new DefaultTreeCellRenderer(){
//			@Override
//			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
//					boolean leaf, int row, boolean hasFocus) {
//				Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
//				
//				TreePath path = tree.getSelectionPath();
//				if(path != null){
//					System.out.println("PATH: " + Arrays.asList(path.getPath()) + " VALUE: " + ((DefaultMutableTreeNode) value));
//					if(Arrays.asList(path.getPath()).contains(value)){
//						System.out.println("Contains");
//						((JLabel)c).setBackground(Color.RED);
//						c.setBackground(Color.blue);//getBackgroundSelectionColor());
//					} else {
//						c.setBackground(getBackgroundNonSelectionColor());
//					}
//				}
//				
//				return c;
//			}
//		});
		return tree;
	}

	private JPanel createDetailsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		detailsPane = new JEditorPane();
		detailsPane.setEditable(false);
		detailsPane.setEditorKit(new HTMLEditorKit());

		Font font = UIManager.getFont("Label.font");
		String bodyRule = "body { font-family: " + font.getFamily() + "; " + "font-size: " + font.getSize() + "pt; }";
		((HTMLDocument) detailsPane.getDocument()).getStyleSheet().addRule(bodyRule);
		// ((HTMLDocument)detailsPane.getDocument()).getStyleSheet().addRule(TableCss.CSS);

		detailsPane.setBorder(BorderUIResource.getEtchedBorderUIResource());
		detailsPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					NativeBrowserLauncher.openURL(e.getURL().toString());
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(detailsPane);
		scrollPane.setAutoscrolls(true);
		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}

	private void onShowDetails(ClassBean cb) {
//		if(lastSelected != null){
//			node2SelectedRelations.put(cb, getSelectedRelations(detailsPane));
//		}
		
		
//		detailsPane.scrollRectToVisible(new Rectangle(0, 0, 100, 10));
//
//		if (cb == null) {
//			detailsPane.setText("No class selected in BioPortal tree.");
//			return;
//		}
//
//		detailsPane.setText("Fetching properties from BioPortal...");
//
//		StringBuffer buffer = new StringBuffer();
//		buffer.append("<html><body>");
//		buffer.append("<table width=\"100%\" class=\"servicesT\" style=\"border-collapse:collapse;border-width:0px;padding:5px\"><tr>");
//
//		buffer.append("<td class=\"servHd\" style=\"background-color:#8E798D;color:#FFFFFF;\">Property</td>");
//		buffer.append("<td class=\"servHd\" style=\"background-color:#8E798D;color:#FFFFFF;\">Value</td>");
//		buffer.append("<td class=\"servHd\" style=\"background-color:#8E798D;color:#FFFFFF;\"></td>");
//
//		int i = 0;
//		i = setBufferText(buffer, "ID", cb.getId(), i);
//		i = setBufferText(buffer, "Full Id", "<a href=\"" + cb.getFullId() + "\">" + cb.getFullId(), i);
//		i = setBufferText(buffer, "Label", HTMLUtil.replaceEOF(getDisplayText(cb.getLabel())), i);
//		i = setBufferText(buffer, "Definitions", HTMLUtil.replaceEOF(getDisplayText(cb.getDefinitions())), i);
//		i = setBufferText(buffer, "Synonyms", HTMLUtil.replaceEOF(getDisplayText(cb.getSynonyms())), i);
//		Map<Object, Object> relationsMap = cb.getRelations();
//		
//		System.out.println(relationsMap);
//
//		TreeMap<Object, Object> t = new TreeMap<Object, Object>(new NameComparator());
//		t.putAll(relationsMap);
//		for (Object obj : t.keySet()) {
//			Object value = relationsMap.get(obj);
//			if (value != null) {
//				String text = "";
//				if (value instanceof List && ((List<Object>) value).size() != 0) {
//					Iterator<Object> itr = ((List<Object>) value).iterator();
//					while (itr.hasNext()) {
//						Object obj2 = itr.next();
//						if (obj2 instanceof ClassBean) {
//							text += "<a href=\"" + ((ClassBean) obj2).getFullId() + "\">"
//									+ ((ClassBean) obj2).getLabel() + "</a><br/>";
//						} else {
//							text += HTMLUtil.replaceEOF(getDisplayText(obj2)) + "<br/>";
//						}
//					}
//				} else {
//					text = HTMLUtil.replaceEOF(getDisplayText(value));
//				}
//				i = setBufferText(buffer, obj, text, i);
//			}
//		}
//		buffer.append("</table>");
//
//		String directLink = BioportalRESTServices.getUrlWithDefaultSuffix(BioPortalUtil.getVisualizationURL(BioportalRESTServices.DEFAULT_BASE_URL, String.valueOf(searchBean.getOntologyId()),
//				cb.getId()));
//
//		if (directLink != null && directLink.length() > 0) {
//			buffer.append("<div style=\"padding:5px;\"><br><b>Direct link in BioPortal:</b> ");
//			buffer.append("<a href=\"");
//			buffer.append(directLink);
//			buffer.append("\">");
//			buffer.append(directLink);
//			buffer.append("</a></div>");
//			buffer.append("<br>"); // important in order to avoid automatic
//									// horizontal scrolling to the right end of
//									// the page when displaying very long URLs
//		}
//		buffer.append("</body></html>");
//		detailsPane.setText(buffer.toString());
//
//		detailsPane.scrollRectToVisible(new Rectangle(0, 0, 100, 10));
//		detailsPane.setCaretPosition(0);
		if(scroll != null){
			remove(scroll);
		}
		detailsPanel = node2Details.get(cb);
		if(detailsPanel == null){
			detailsPanel = new DetailsPanel(cb, editorKit);
			node2Details.put(cb, detailsPanel);
		}
		
		scroll = new JScrollPane(detailsPanel);
		add(scroll, BorderLayout.CENTER);
		lastSelected = cb;
		revalidate();
	}
	
//	private Set<String> getSelectedRelations(JEditorPane editor){
//		Set<String> selectedRelations = new HashSet<String>();
//		HTMLDocument doc = (HTMLDocument)editor.getDocument();
//        ElementIterator it = new ElementIterator(doc);
//        Element element;
//
//        while ( (element = it.next()) != null )
//        {
//            System.out.println();
//
//            AttributeSet as = element.getAttributes();
//            Enumeration enumm = as.getAttributeNames();
//
//            while( enumm.hasMoreElements() )
//            {
//                Object name = enumm.nextElement();
//                Object value = as.getAttribute( name );
//                
//                if(value instanceof JToggleButton.ToggleButtonModel){
//                	if(((JToggleButton.ToggleButtonModel)value).isSelected()){
//                		Enumeration e = as.getAttributeNames();
//                        while( e.hasMoreElements() ){
//                        	Object o = e.nextElement();
//                        	if(o instanceof HTML.Attribute && o.toString().equals("name")){
//                        		selectedRelations.add(as.getAttribute(o).toString());
//                        	}
//                        }
//                	}
//                	
//                }
//
//            }
//        }
//        return selectedRelations;
//	}

	private String getDisplayText(Object value) {
		return HTMLUtil.makeHTMLLinks(value.toString());
	}

	private String getDisplayText(Object value, int maxLength) {
		return HTMLUtil.makeHTMLLinks(value.toString(), maxLength);
	}

	private String getDisplayText(Collection<?> values) {
		StringBuffer buffer = new StringBuffer();
		if (values == null) {
			return buffer.toString();
		}
		for (Iterator<?> iterator = values.iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			buffer.append(getDisplayText(object));
			buffer.append(", ");
		}
		if (buffer.length() > 2) {
			return buffer.substring(0, buffer.length() - 2);
		}
		return buffer.toString();
	}

	private String getDisplayText(Collection<?> values, int maxLength) {
		StringBuffer buffer = new StringBuffer();
		if (values == null) {
			return buffer.toString();
		}
		for (Iterator<?> iterator = values.iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			buffer.append(getDisplayText(object, maxLength));
			buffer.append(", ");
		}
		if (buffer.length() > 2) {
			return buffer.substring(0, buffer.length() - 2);
		}
		return buffer.toString();
	}

	private int setBufferText(StringBuffer buffer, Object obj, String text, int i) {
		String oddColor = "#F4F2F3";
		String evenColor = "#E6E6E5";
		String color = i % 2 == 0 ? evenColor : oddColor;
		if (text.startsWith("[")) {
			text = text.substring(1, text.length() - 1);
		}
		if (text.length() > 0) {
			buffer.append("<tr>");
			
			buffer.append("<td class=\"servBodL\" style=\"background-color:" + color
					+ ";padding:7px;font-weight: bold;\" >");
			buffer.append(getDisplayText(obj));
			buffer.append("</td>");
			
			buffer.append("<td class=\"servBodL\" style=\"background-color:" + color + ";padding:7px;\" >");
			buffer.append(text);
			buffer.append("</td>");
			
			buffer.append("<td class=\"servBodL\" style=\"background-color:" + color + ";padding:7px;\" >");
			buffer.append("<input type=\"checkbox\" name=\"" + obj.toString() +  "\" value=\"value\" />");
			buffer.append("</td>");
			
			buffer.append("</tr>");
			i++;
		}
		return i;
	}

	private void onImportClasses() {
//		Set<Object> selectedValues = detailsPanel.getSelectedValues();
		TreePath[] selection = classTree.getSelectionPaths();
		if (selection == null) {

		} else {
			OWLClass superClass = editorKit.getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
			OWLOntologyManager ontMan = editorKit.getOWLModelManager().getOWLOntologyManager();
			OWLDataFactory dataFactory = ontMan.getOWLDataFactory();
			OWLOntology ontology = editorKit.getOWLModelManager().getActiveOntology();
			System.out.println(superClass);
			for (TreePath treePath : selection) {
				System.out.println("Path: " + treePath);
				Object[] nodes = treePath.getPath();
				OWLClass sup = convert((ClassBean) ((DefaultMutableTreeNode) nodes[0]).getUserObject());
				OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(sup, superClass);
				ontMan.addAxiom(ontology, axiom);
				for (int i = 1; i < nodes.length; i++) {
					Object node = nodes[i];
					OWLClass sub = convert((ClassBean) ((DefaultMutableTreeNode) node).getUserObject());
					System.out.println("Node: " + node);
					axiom = dataFactory.getOWLSubClassOfAxiom(sub, sup);
					ontMan.addAxiom(ontology, axiom);
					sup = sub;
				}
			}
		}
	}

	private OWLClass convert(ClassBean cb) {
		OWLDataFactory dataFactory = editorKit.getOWLModelManager().getOWLOntologyManager().getOWLDataFactory();
		OWLClass cls = dataFactory.getOWLClass(IRI.create(cb.getFullId()));
		addMetaData(cb);
		return cls;
	}

	private void addMetaData(ClassBean cb) {
		OWLDataFactory dataFactory = editorKit.getOWLModelManager().getOWLOntologyManager().getOWLDataFactory();
		OWLClass cls = dataFactory.getOWLClass(IRI.create(cb.getFullId()));
		OWLOntology ontology = editorKit.getOWLModelManager().getActiveOntology();
		String label = cb.getLabel();
		OWLAnnotation labelAnno = dataFactory.getOWLAnnotation(
				dataFactory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()),
				dataFactory.getOWLLiteral(label));
		OWLAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(cls.getIRI(), labelAnno);
		editorKit.getOWLModelManager().getOWLOntologyManager().addAxiom(ontology, ax);
		
		Set<String> selectedRelations = node2SelectedRelations.get(cb);
		if(selectedRelations == null){
//			selectedRelations = getSelectedRelations(detailsPane);
		}
		DetailsPanel details = node2Details.get(cb);
		if(details != null){
			Map<String, Set<Object>> selectedValues = details.getSelectedValues();
			System.out.println("Additional meta data: " + selectedValues);
			for(Entry<String, Set<Object>> entry : selectedValues.entrySet()){
				String relation = entry.getKey();
				Set<Object> values = entry.getValue();
				for (Object value : values) {
					OWLAnnotation anno = dataFactory.getOWLAnnotation(
							dataFactory.getOWLAnnotationProperty(IRI.create(relation)),
							dataFactory.getOWLLiteral(value.toString()));
					ax = dataFactory.getOWLAnnotationAssertionAxiom(cls.getIRI(), anno);
					editorKit.getOWLModelManager().getOWLOntologyManager().addAxiom(ontology, ax);
				}
			}
		}
	}

	class ExpandTreeWorker extends SwingWorker<Collection<ClassBean>, Void> implements BackgroundTask {

		private DefaultMutableTreeNode nodeToExpand;
		private int ontologyVersionId;
		private DefaultTreeModel model;

		public ExpandTreeWorker(DefaultMutableTreeNode nodeToExpand, int ontologyVersionId, DefaultTreeModel model) {
			this.nodeToExpand = nodeToExpand;
			this.ontologyVersionId = ontologyVersionId;
			this.model = model;
		}
		
		@Override
		protected Collection<ClassBean> doInBackground() throws Exception {
			ClassBean cb = (ClassBean) nodeToExpand.getUserObject();
			Collection<ClassBean> children = (Collection<ClassBean>) cb.getRelations().get(ClassBean.SUB_CLASS_PROPERTY);
			if(children == null){
				cb = BioportalRESTServices.getConceptProperties(ontologyVersionId, cb.getId());
				children = (Collection<ClassBean>) cb.getRelations().get(ClassBean.SUB_CLASS_PROPERTY);
				nodeToExpand.setUserObject(cb);
			}
			return children;
		}

		@Override
		protected void done() {
			ProtegeApplication.getBackgroundTaskManager().endTask(this);
			try {
				Collection<ClassBean> children = get();
				// nodeToExpand.removeAllChildren();

				for (ClassBean child : children) {
					Integer childCount = (Integer) child.getRelations().get("ChildCount");
					boolean allowChildren = childCount > 0;
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
					if (allowChildren) {
						childNode.add(new DefaultMutableTreeNode("Loading children..."));
					}
					// nodeToExpand.add(childNode);
					model.insertNodeInto(childNode, nodeToExpand, model.getChildCount(nodeToExpand));

				}
				// model.reload();
				model.removeNodeFromParent((MutableTreeNode) model.getChild(nodeToExpand, 0));
				editorKit.getWorkspace().setCursor(Cursor.getDefaultCursor());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		

	}
	
	class LoadPropertiesWorker extends SwingWorker<Void, Void> implements BackgroundTask {

		private DefaultMutableTreeNode nodeToExpand;
		private int ontologyVersionId;

		public LoadPropertiesWorker(DefaultMutableTreeNode nodeToExpand, int ontologyVersionId) {
			this.nodeToExpand = nodeToExpand;
			this.ontologyVersionId = ontologyVersionId;
		}

		@Override
		protected Void doInBackground() throws Exception {
			ClassBean cb = BioportalRESTServices.getConceptProperties(ontologyVersionId, ((ClassBean)nodeToExpand.getUserObject()).getId());
			Collection<ClassBean> children = (Collection<ClassBean>) cb.getRelations().get(ClassBean.SUB_CLASS_PROPERTY);
			if(children == null){
				cb.getRelations().put(ClassBean.SUB_CLASS_PROPERTY, Collections.<ClassBean>emptySet());
			}
			nodeToExpand.setUserObject(cb);
			return null;
		}

		@Override
		protected void done() {
			ProtegeApplication.getBackgroundTaskManager().endTask(this);
			editorKit.getWorkspace().setCursor(Cursor.getDefaultCursor());
			onShowDetails((ClassBean) nodeToExpand.getUserObject());
		}

	}

	class NameComparator implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			String s1 = getDisplayText(o1);
			String s2 = getDisplayText(o2);
			return s1.compareToIgnoreCase(s2);
		}
	}

}
