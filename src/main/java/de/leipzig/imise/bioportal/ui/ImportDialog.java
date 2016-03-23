package de.leipzig.imise.bioportal.ui;

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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.awt.GraphicsDevice.WindowTranslucency.TRANSLUCENT;

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
		classTree = new JTree(new DefaultMutableTreeNode());
//		classTree.setPreferredSize(new Dimension(200, 0));
		classTree.setRootVisible(false);
		classTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane treeScrollPane = new JScrollPane(classTree);
		treeScrollPane.setPreferredSize(new Dimension(200, 0));
		add(new JLayer<JComponent>(treeScrollPane, layerUI), BorderLayout.WEST);

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

		// populate the tree
		ClassTreeLoadingTask task = new ClassTreeLoadingTask(entity);
		ProtegeApplication.getBackgroundTaskManager().startTask(task);
		layerUI.start();
		task.execute();
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

		classTree.setExpandsSelectedPaths(true);
		classTree.setSelectionPath(path);
		classTree.scrollPathToVisible(path);

		return classTree;
	}


	private void onShowDetails(Entity entity) {
		// get details for entity
		entity = BioportalRESTService.getEntityDetails(entity);

		detailsPanel.showDetails(entity);
	}
	
	private void onImportClasses() {
		// Determine if the GraphicsDevice supports translucency.
		GraphicsEnvironment ge =
				GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();

		//If translucent windows aren't supported, exit.
		if (gd.isWindowTranslucencySupported(TRANSLUCENT)) {
			final JDialog d = new JDialog(this);
			d.setUndecorated(true);
			d.setOpacity(0.7f);
			d.add(new JLabel("<html>Axioms successfully added.</html>"));
//			d.setModal(true);
			d.setPreferredSize(new Dimension(200, 200));
			d.pack();
			d.setAlwaysOnTop(true);
			d.setLocationRelativeTo(this);

			Dimension scrSize = this.getSize();//Toolkit.getDefaultToolkit().getScreenSize();
			Insets toolHeight = d.getInsets();//Toolkit.getDefaultToolkit().getScreenInsets(d.getGraphicsConfiguration());
			d.setLocation(scrSize.width - d.getWidth(), scrSize.height - toolHeight.bottom - d.getHeight());

			ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();
			s.schedule(new Runnable() {
				public void run() {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							d.setVisible(false);
							d.dispose();
						}
					});
				}
			}, 3, TimeUnit.SECONDS);

			d.setVisible(true);
		}


//		Set<Object> selectedValues = detailsPanel.getSelectedValues();
		TreePath[] selection = classTree.getSelectionPaths();
		if (selection == null) {

		} else {
			OWLOntologyManager ontMan = editorKit.getOWLModelManager().getOWLOntologyManager();
			OWLDataFactory dataFactory = ontMan.getOWLDataFactory();
			OWLOntology ontology = editorKit.getOWLModelManager().getActiveOntology();

			Set<OWLAxiom> axioms2Add = new HashSet<>();

			// get the selected class in Protege -> this will be the super class
			OWLClass superClass = editorKit.getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
			System.out.println(superClass);

			// convert the selected entity in the tree ->A will be the subclass
			Entity selectedEntity = (Entity) ((DefaultMutableTreeNode) classTree.getLastSelectedPathComponent()).getUserObject();
			OWLClass subClass = convert(selectedEntity);

			// get axioms for the selected metadata
			Set<OWLAxiom> metaDataAxioms = getMetaData(selectedEntity);
			axioms2Add.addAll(metaDataAxioms);

			// create and add the subclass axiom
			OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(subClass, superClass);
			axioms2Add.add(axiom);

			// add the axioms
			ontMan.addAxioms(ontology, axioms2Add);
			System.out.println("Adding axioms:" + axioms2Add);

//			for (TreePath treePath : selection) {
//				System.out.println("Path: " + treePath);
//				Object[] nodes = treePath.getPath();
//
//				// convert the selected entity in the tree
//				OWLClass sup = convert((Entity) ((DefaultMutableTreeNode) nodes[0]).getUserObject());
//
//				// create and add the subclass axiom
//				OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(sup, superClass);
//				ontMan.addAxiom(ontology, axiom);
//
//				for (int i = 1; i < nodes.length; i++) {
//					Object node = nodes[i];
//					OWLClass sub = convert((Entity) ((DefaultMutableTreeNode) node).getUserObject());
//					System.out.println("Node: " + node);
//					axiom = dataFactory.getOWLSubClassOfAxiom(sub, sup);
//					ontMan.addAxiom(ontology, axiom);
//					sup = sub;
//				}
//			}
		}
	}

	private OWLClass convert(Entity entity) {
		OWLDataFactory dataFactory = editorKit.getOWLModelManager().getOWLOntologyManager().getOWLDataFactory();
		OWLClass cls = dataFactory.getOWLClass(IRI.create(entity.getId()));
		return cls;
	}

	private Set<OWLAxiom> getMetaData(Entity entity) {
		Set<OWLAxiom> axioms = new HashSet<>();

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

				axioms.add(ax);
			} else if(property.isOWLDataProperty()) {

			} else {

			}
		}
		return axioms;
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
