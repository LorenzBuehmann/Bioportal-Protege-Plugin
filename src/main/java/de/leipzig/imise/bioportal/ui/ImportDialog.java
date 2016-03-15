package de.leipzig.imise.bioportal.ui;

import de.leipzig.imise.bioportal.BioportalRESTServices;
import de.leipzig.imise.bioportal.bean.concept.ClassBean;
import de.leipzig.imise.bioportal.rest.BioportalRESTService;
import de.leipzig.imise.bioportal.rest.Entity;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.util.HTMLUtil;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.progress.BackgroundTask;
import org.protege.editor.core.ui.util.NativeBrowserLauncher;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.IconUIResource;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

public class ImportDialog extends JDialog {

	private OWLEditorKit editorKit;
	private JTree classTree;
	private JEditorPane detailsPane;
	private SearchBean searchBean;
	
	private Map<Entity, Set<String>> node2SelectedRelations = new HashMap<>();
	
	private Map<Entity, DetailsPanel> node2Details = new HashMap<>();
	
	private Entity lastSelected = null;
	private DetailsPanel detailsPanel;
	private JScrollPane scroll;

	public ImportDialog(Window parent, Entity entity, final OWLEditorKit editorKit) {
		super(parent);
		this.searchBean = searchBean;
		this.editorKit = editorKit;

		setLayout(new BorderLayout());
		setTitle("Import classes from " + entity.getEntityLinks().getOntology());
		setModal(true);
		setSize(600, 600);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(parent);

		classTree = createClassTree(entity);
		add(new JScrollPane(classTree), BorderLayout.WEST);

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

	private JTree createClassTree(final Entity entity) {
		UIManager.put("Tree.collapsedIcon", new IconUIResource(new NodeIcon('+')));
		UIManager.put("Tree.expandedIcon", new IconUIResource(new NodeIcon('-')));

//		Collection<Entity> children = BioportalRESTService.getRoots(root);
//		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);
//		if(children != null){
//			for (Entity child : children) {
//				Boolean hasChildren = (Boolean) child.getAdditionalProperties().get("hasChildren");
//				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
//				if (hasChildren) {
//					childNode.add(new DefaultMutableTreeNode("Loading children..."));
//				}
//				rootNode.add(childNode);
//			}
//		}
		// create the tree
		DefaultMutableTreeNode root = BioportalRESTService.getTree(entity);
//		JTreeUtils.sortTree(root);
		final JTree tree = new JTree(root);
		tree.setRootVisible(false);

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		// expand all nodes
//		JTreeUtils.expandAllNodes(tree, 0, tree.getRowCount());

		// select the entity node
		DefaultMutableTreeNode entityNode = JTreeUtils.searchNode((DefaultMutableTreeNode) tree.getModel().getRoot(), entity);
		TreePath path = new TreePath(((DefaultTreeModel) tree.getModel()).getPathToRoot(entityNode));
		tree.setExpandsSelectedPaths(true);
		tree.setSelectionPath(path);
		tree.scrollPathToVisible(path);

		// set cell renderer to display entity label
		tree.setCellRenderer(new EntityTreeCellRenderer());

		// expand node action listener
		tree.addTreeWillExpandListener(new TreeWillExpandListener() {

			@Override
			public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
				TreePath path = e.getPath();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
				ExpandTreeWorker expandTreeTask = new ExpandTreeWorker(node, (DefaultTreeModel) tree.getModel());
				ProtegeApplication.getBackgroundTaskManager().startTask(expandTreeTask);
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//				editorKit.getWorkspace().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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
					if (e.isAddedPath(i)) {
						tree.repaint();
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
						Entity entity = (Entity)node.getUserObject();
//						if(!cb.getRelations().containsKey(ClassBean.SUB_CLASS_PROPERTY)){
//							setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
////							editorKit.getWorkspace().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//							new LoadPropertiesWorker(node, searchBean.getOntologyVersionId()).execute();
//						} else {
//							onShowDetails(entity);
//						}
//						onShowDetails(entity);

						break;
					} else {
						// This node has been deselected
						break;
					}
				}

			}
		});
		EntityTreeCellRenderer renderer =
				(EntityTreeCellRenderer) tree.getCellRenderer();
		renderer.setTextSelectionColor(Color.white);
		renderer.setBackgroundSelectionColor(Color.blue);
		renderer.setBorderSelectionColor(Color.black);
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

	private void onShowDetails(Entity entity) {
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
		detailsPanel = node2Details.get(entity);
		if(detailsPanel == null){
			detailsPanel = new DetailsPanel(entity, editorKit);
			node2Details.put(entity, detailsPanel);
		}
		
		scroll = new JScrollPane(detailsPanel);
		add(scroll, BorderLayout.CENTER);
		lastSelected = entity;
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
				OWLClass sup = convert((Entity) ((DefaultMutableTreeNode) nodes[0]).getUserObject());
				OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(sup, superClass);
				ontMan.addAxiom(ontology, axiom);
				for (int i = 1; i < nodes.length; i++) {
					Object node = nodes[i];
					OWLClass sub = convert((Entity) ((DefaultMutableTreeNode) node).getUserObject());
					System.out.println("Node: " + node);
					axiom = dataFactory.getOWLSubClassOfAxiom(sub, sup);
					ontMan.addAxiom(ontology, axiom);
					sup = sub;
				}
			}
		}
	}

	private OWLClass convert(Entity cb) {
		OWLDataFactory dataFactory = editorKit.getOWLModelManager().getOWLOntologyManager().getOWLDataFactory();
		OWLClass cls = dataFactory.getOWLClass(IRI.create(cb.getId()));
		addMetaData(cb);
		return cls;
	}

	private void addMetaData(Entity cb) {
		OWLDataFactory dataFactory = editorKit.getOWLModelManager().getOWLOntologyManager().getOWLDataFactory();
		OWLClass cls = dataFactory.getOWLClass(IRI.create(cb.getId()));
		OWLOntology ontology = editorKit.getOWLModelManager().getActiveOntology();
		String label = cb.getPrefLabel();
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

	class ExpandTreeWorker extends SwingWorker<Collection<Entity>, Void> implements BackgroundTask {

		private DefaultMutableTreeNode nodeToExpand;
		private DefaultTreeModel model;

		public ExpandTreeWorker(DefaultMutableTreeNode nodeToExpand, DefaultTreeModel model) {
			this.nodeToExpand = nodeToExpand;
			this.model = model;
		}
		
		@Override
		protected Collection<Entity> doInBackground() throws Exception {
			Entity entity = (Entity) nodeToExpand.getUserObject();
			Collection<Entity> children = BioportalRESTService.getChildren(entity);
			return children;
		}

		@Override
		protected void done() {
			ProtegeApplication.getBackgroundTaskManager().endTask(this);
			try {
				Collection<Entity> children = get();
				// nodeToExpand.removeAllChildren();

				for (Entity child : children) {
					Boolean hasChildren = (Boolean) child.getAdditionalProperties().get("hasChildren");
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
					if (hasChildren) {
						childNode.add(new DefaultMutableTreeNode("Loading children..."));
					}
					// nodeToExpand.add(childNode);
					model.insertNodeInto(childNode, nodeToExpand, model.getChildCount(nodeToExpand));

				}
				// model.reload();
				model.removeNodeFromParent((MutableTreeNode) model.getChild(nodeToExpand, 0));
				setCursor(Cursor.getDefaultCursor());
//				editorKit.getWorkspace().setCursor(Cursor.getDefaultCursor());
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
			onShowDetails((Entity) nodeToExpand.getUserObject());
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
