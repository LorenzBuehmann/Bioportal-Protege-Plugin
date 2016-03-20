package de.leipzig.imise.bioportal.ui;

import de.leipzig.imise.bioportal.BioportalRESTServices;
import de.leipzig.imise.bioportal.bean.concept.ClassBean;
import de.leipzig.imise.bioportal.rest.BioportalRESTService;
import de.leipzig.imise.bioportal.rest.Entity;
import org.ncbo.stanford.bean.search.SearchBean;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.progress.BackgroundTask;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.plaf.IconUIResource;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

public class ImportDialog extends JDialog {

	private OWLEditorKit editorKit;
	private JEditorPane detailsPane;
	private SearchBean searchBean;
	
	private Map<Entity, Set<String>> node2SelectedRelations = new HashMap<>();
	
	private Map<Entity, DetailsPanel> node2Details = new HashMap<>();
	
	private Entity lastSelected = null;
	private DetailsPanel detailsPanel;
	private JScrollPane scroll;

	private final JTree classTree;

	private final WaitLayerUI layerUI = new WaitLayerUI("Loading");

	public ImportDialog(Window parent, Entity entity, final OWLEditorKit editorKit) {
		super(parent);
		this.editorKit = editorKit;

		setLayout(new BorderLayout());
		setTitle("Import classes from " + entity.getEntityLinks().getOntology());
		setModal(true);
		setSize(1000, 600);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(parent);


		// the class tree on the left
		classTree = new JTree();
//		classTree.setPreferredSize(new Dimension(200, 0));
		classTree.setRootVisible(false);
		classTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane treeScrollPane = new JScrollPane(classTree);
		treeScrollPane.setPreferredSize(new Dimension(200, 0));
		add(new JLayer<JComponent>(treeScrollPane, layerUI), BorderLayout.WEST);

		// populate the tree
		ClassTreeLoadingTask task = new ClassTreeLoadingTask(entity);
		ProtegeApplication.getBackgroundTaskManager().startTask(task);
		layerUI.start();
		task.execute();

		// the details panel in the right
		detailsPanel = new DetailsPanel(editorKit);
		add(detailsPanel, BorderLayout.CENTER);

		// the import button at the bottom
		JButton importButton = new JButton("Import");
		importButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onImportClasses();
			}
		});
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(importButton, BorderLayout.EAST);
		add(southPanel, BorderLayout.SOUTH);
	}

	private JTree createClassTree(Entity entity, DefaultMutableTreeNode root) {
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

		((DefaultTreeModel) classTree.getModel()).setRoot(root);

//		JTreeUtils.sortTree(root);

		// expand all nodes
//		JTreeUtils.expandAllNodes(tree, 0, tree.getRowCount());

		// select the entity node
		DefaultMutableTreeNode entityNode = JTreeUtils.searchNode((DefaultMutableTreeNode) classTree.getModel().getRoot(), entity);
		TreePath path = new TreePath(((DefaultTreeModel) classTree.getModel()).getPathToRoot(entityNode));
		classTree.setExpandsSelectedPaths(true);
		classTree.setSelectionPath(path);
		classTree.scrollPathToVisible(path);

		// set cell renderer to display entity label
		classTree.setCellRenderer(new EntityTreeCellRenderer());

		// expand node action listener
		classTree.addTreeWillExpandListener(new TreeWillExpandListener() {

			@Override
			public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
				TreePath path = e.getPath();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
				ExpandTreeWorker expandTreeTask = new ExpandTreeWorker(node, (DefaultTreeModel) classTree.getModel());
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

		// the selection listener
		classTree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath[] paths = e.getPaths();
				for (int i=0; i<paths.length; i++) {
					if (e.isAddedPath(i)) {
						classTree.repaint();
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
						Entity entity = (Entity)node.getUserObject();
//						if(!cb.getRelations().containsKey(ClassBean.SUB_CLASS_PROPERTY)){
//							setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
////							editorKit.getWorkspace().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//							new LoadPropertiesWorker(node, searchBean.getOntologyVersionId()).execute();
//						} else {
//							onShowDetails(entity);
//						}
						onShowDetails(entity);

						break;
					} else {
						// This node has been deselected
						break;
					}
				}

			}
		});
		EntityTreeCellRenderer renderer =
				(EntityTreeCellRenderer) classTree.getCellRenderer();
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
		return classTree;
	}


	private void onShowDetails(Entity entity) {
		// get details for entity
		entity = BioportalRESTService.getEntityDetails(entity);

		detailsPanel.showDetails(entity);
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

	private OWLClass convert(Entity entity) {
		OWLDataFactory dataFactory = editorKit.getOWLModelManager().getOWLOntologyManager().getOWLDataFactory();
		OWLClass cls = dataFactory.getOWLClass(IRI.create(entity.getId()));
		addMetaData(entity);
		return cls;
	}

	private void addMetaData(Entity entity) {
		OWLDataFactory dataFactory = editorKit.getOWLModelManager().getOWLOntologyManager().getOWLDataFactory();
		
		OWLClass cls = dataFactory.getOWLClass(IRI.create(entity.getId()));
		
		OWLOntology ontology = editorKit.getOWLModelManager().getActiveOntology();

		Map<OWLProperty, String> selectedValues = detailsPanel.getSelectedValues(entity);

		for(Entry<OWLProperty, String> entry : selectedValues.entrySet()) {
			OWLProperty property = entry.getKey();
			String value = entry.getValue();

			OWLAxiom ax;
			if(property.isOWLAnnotationProperty()) {
				OWLAnnotation anno = dataFactory.getOWLAnnotation(
						property.asOWLAnnotationProperty(),
						dataFactory.getOWLLiteral(value));

				ax = dataFactory.getOWLAnnotationAssertionAxiom(cls.getIRI(), anno);

				editorKit.getOWLModelManager().getOWLOntologyManager().addAxiom(ontology, ax);
			} else if(property.isOWLDataProperty()) {

			} else {

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

	class ClassTreeLoadingTask extends SwingWorker<DefaultMutableTreeNode, Void> implements BackgroundTask {

		Entity entity;

		public ClassTreeLoadingTask(Entity entity) {
			this.entity = entity;
		}

		@Override
		protected DefaultMutableTreeNode doInBackground() throws Exception {
			return BioportalRESTService.getTree(entity);
		}

		@Override
		protected void done() {
			layerUI.stop();
			ProtegeApplication.getBackgroundTaskManager().endTask(this);
			DefaultMutableTreeNode result;
			try {
				result = get();
				createClassTree(entity, result);
//				editorKit.getWorkspace().setCursor(Cursor.getDefaultCursor());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

	}

}
