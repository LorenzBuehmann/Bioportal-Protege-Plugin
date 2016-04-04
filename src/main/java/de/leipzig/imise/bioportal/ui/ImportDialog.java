package de.leipzig.imise.bioportal.ui;

import de.leipzig.imise.bioportal.rest.BioportalRESTService;
import de.leipzig.imise.bioportal.rest.Entity;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.progress.BackgroundTask;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.UIHelper;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.SKOSVocabulary;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.plaf.IconUIResource;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

	private Map<Entity, Set<String>> node2SelectedRelations = new HashMap<>();
	
	private Map<Entity, DetailsPanel> node2Details = new HashMap<>();
	
	private Entity lastSelected = null;
	private DetailsPanel detailsPanel;
	private JScrollPane scroll;

	private final JTree classTree;

	private final WaitLayerUI layerUI = new WaitLayerUI("Loading");
	private final WaitLayerUI layerUIDetails = new WaitLayerUI("Loading details");

	private Set<OWLAxiom> renderingAxioms = new HashSet<>();
	private Set<OWLAxiom> addedAxioms = new HashSet<>();

	public ImportDialog(Window parent, Entity entity, final OWLEditorKit editorKit) {
		super(parent);
		this.editorKit = editorKit;

		setLayout(new BorderLayout());
		setTitle("Import data from " + entity.getEntityLinks().getOntology());
		setModal(true);
		setSize(1000, 600);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(parent);


		// the class tree on the left
		UIManager.put("Tree.collapsedIcon", new IconUIResource(new NodeIcon('+')));
		UIManager.put("Tree.expandedIcon", new IconUIResource(new NodeIcon('-')));
		classTree = new JTree(new DefaultMutableTreeNode());
//		classTree.setPreferredSize(new Dimension(200, 0));
		classTree.setRootVisible(false);
		classTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		// set cell renderer to display entity label
		classTree.setCellRenderer(new EntityTreeCellRenderer(editorKit));

		JScrollPane treeScrollPane = new JScrollPane(classTree);
		treeScrollPane.setPreferredSize(new Dimension(200, 0));

		// the details panel on the right
		detailsPanel = new DetailsPanel(editorKit);

		JSplitPane splitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT,
				new JLayer<>(treeScrollPane, layerUI), // left
				new JLayer<>(detailsPanel, layerUIDetails) // right
				);
		splitPane.setDividerLocation(0.3);
		add(splitPane, BorderLayout.CENTER);

		// the import button at the bottom
		JButton importButton = new JButton("Import");
		importButton.addActionListener(e -> onImportClasses());
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(importButton, BorderLayout.EAST);
		add(southPanel, BorderLayout.SOUTH);


		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				onDialogClosed();
			}
		});

		// populate the tree
		populateClassTree(entity);
	}

	private void populateClassTree(Entity entity) {
		ClassTreeLoadingTask task = new ClassTreeLoadingTask(entity);
		ProtegeApplication.getBackgroundTaskManager().startTask(task);
		layerUI.start();
		task.execute();
	}

	private void onDialogClosed() {
		renderingAxioms.removeAll(addedAxioms);
		editorKit.getOWLModelManager().getOWLOntologyManager().removeAxioms(editorKit.getOWLModelManager().getActiveOntology(), renderingAxioms);
	}

	private void showAxiomsToAdd(Set<OWLAxiom> axioms) {
		OWLAxiomList list = new OWLAxiomList(editorKit);
		list.setAxioms(axioms);

		JDialog dialog = new JDialog(this);
		dialog.add(list);
		dialog.setPreferredSize(new Dimension(600, 300));
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setAlwaysOnTop(true);
		dialog.setModal(true);
		dialog.setVisible(true);
	}

	private Set<OWLAxiom> createOntologyFromTree(DefaultMutableTreeNode root) {
		OWLDataFactory df = editorKit.getOWLModelManager().getOWLDataFactory();

		Set<OWLAxiom> axioms = new HashSet<>();
		Enumeration<DefaultMutableTreeNode> en = root.depthFirstEnumeration();
		while (en.hasMoreElements()) {
			DefaultMutableTreeNode node = en.nextElement();

			Entity entity = (Entity) node.getUserObject();

			OWLClass cls = df.getOWLClass(IRI.create(entity.getId()));
			axioms.add(df.getOWLDeclarationAxiom(cls));

			OWLAnnotation ann = df.getOWLAnnotation(
					df.getOWLAnnotationProperty(SKOSVocabulary.PREFLABEL.getIRI()),
					df.getOWLLiteral(entity.getPrefLabel()));
			OWLAnnotationAssertionAxiom annAxiom = df.getOWLAnnotationAssertionAxiom(cls.getIRI(), ann);
			axioms.add(annAxiom);
		}
		return axioms;
	}

	private void fillClassTree(Entity entity, DefaultMutableTreeNode root) {
		renderingAxioms.addAll(createOntologyFromTree(root));
//		editorKit.getOWLModelManager().getOWLOntologyManager().addAxioms(editorKit.getOWLModelManager().getActiveOntology(), renderingAxioms);
		((DefaultTreeModel) classTree.getModel()).setRoot(root);

//		JTreeUtils.sortTree(root);

		// expand all nodes
//		JTreeUtils.expandAllNodes(tree, 0, tree.getRowCount());

		// select the entity node
		DefaultMutableTreeNode entityNode = JTreeUtils.searchNode((DefaultMutableTreeNode) classTree.getModel().getRoot(), entity);
		TreePath path = new TreePath(((DefaultTreeModel) classTree.getModel()).getPathToRoot(entityNode));

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
			public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {}
		});

		// the selection listener
		classTree.addTreeSelectionListener(e -> {
			TreePath[] paths = e.getPaths();
			for (int i = 0; i < paths.length; i++) {
				if (e.isAddedPath(i)) {
					classTree.repaint();
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
					Entity entity1 = (Entity)node.getUserObject();
					onShowDetails(entity1);
					break;
				} else {
					// This node has been deselected
					break;
				}
			}
		});

		classTree.setExpandsSelectedPaths(true);
		classTree.setSelectionPath(path);
		classTree.scrollPathToVisible(path);
	}

	private void onShowDetails(final Entity entity) {
		Thread t = new Thread(() -> {
			try {
				// get details for entity
				Entity detailedEntity = BioportalRESTService.getEntityDetails(entity);
				// show details in UI
				SwingUtilities.invokeLater(() -> detailsPanel.showDetails(detailedEntity));
			} catch (Exception e) {

			} finally {
				// stop the loading indicator
				SwingUtilities.invokeLater(layerUIDetails::stop);
			}
		});
		// show the loading indicator
		layerUIDetails.start();

		// start the thread
		t.start();
	}

	private void showAxiomsAddedNotification() {
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
			s.schedule((Runnable) () -> SwingUtilities.invokeLater(() -> {
				d.setVisible(false);
				d.dispose();
			}), 3, TimeUnit.SECONDS);

			d.setVisible(true);
		}
	}
	
	private void onImportClasses() {
//		Set<Object> selectedValues = detailsPanel.getSelectedValues();
		TreePath[] selection = classTree.getSelectionPaths();
		if (selection != null) {
			OWLOntologyManager ontMan = editorKit.getOWLModelManager().getOWLOntologyManager();
			OWLDataFactory dataFactory = ontMan.getOWLDataFactory();
			OWLOntology ontology = editorKit.getOWLModelManager().getActiveOntology();

			Set<OWLAxiom> axioms2Add = new HashSet<>();

			// convert the selected entity in the tree ->A will be the subclass
			Entity selectedEntity = (Entity) ((DefaultMutableTreeNode) classTree.getLastSelectedPathComponent()).getUserObject();
			OWLClass subClass = convert(selectedEntity);

//			detailsPanel.checkTable(selectedEntity);

			// get axioms for the selected metadata
			Set<OWLAxiom> metaDataAxioms = getMetaData(selectedEntity);
			axioms2Add.addAll(metaDataAxioms);

			// get the selected class in Protege -> this will be the super class
			OWLClass superClass = editorKit.getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();

			// create and add the subclass axiom
			OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(subClass, superClass);
			axioms2Add.add(axiom);

			OWLAxiomList list = new OWLAxiomList(editorKit);
			list.setAxioms(axioms2Add);
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.add(list, BorderLayout.CENTER);
			panel.setPreferredSize(new Dimension(400, 200));

			if(new UIHelper(editorKit).showValidatingDialog(
					"Add axioms to ontology",
					panel,
					list) == JOptionPane.OK_OPTION) {
				// add the axioms
				ontMan.addAxioms(ontology, axioms2Add);
				System.out.println("Added axioms:" + axioms2Add);

				addedAxioms.addAll(axioms2Add);
			}
//			showAxiomsToAdd(axioms2Add);



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
		return dataFactory.getOWLClass(IRI.create(entity.getId()));
	}

	private Set<OWLAxiom> getMetaData(Entity entity) {
		Set<OWLAxiom> axioms = new HashSet<>();

		OWLDataFactory dataFactory = editorKit.getOWLModelManager().getOWLOntologyManager().getOWLDataFactory();

		OWLClass cls = dataFactory.getOWLClass(IRI.create(entity.getId()));
		
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
			}
		}
		return axioms;
	}

	/**
	 * Load the children for the selected entity.
	 */
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
			return BioportalRESTService.getChildren(entity);
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
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * The task of loading the class tree.
	 */
	class ClassTreeLoadingTask extends SwingWorker<DefaultMutableTreeNode, Void> implements BackgroundTask {

		private Entity entity;

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

			try {
				DefaultMutableTreeNode result = get();
				fillClassTree(entity, result);
//				editorKit.getWorkspace().setCursor(Cursor.getDefaultCursor());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

		@Override
		public String toString() {
			return "Loading class hierarchy";
		}
	}
}
