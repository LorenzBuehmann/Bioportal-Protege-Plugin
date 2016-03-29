package de.leipzig.imise.bioportal.ui;

import de.leipzig.imise.bioportal.BioportalManager;
import de.leipzig.imise.bioportal.rest.*;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.progress.BackgroundTask;
import org.protege.editor.core.ui.util.CheckTable;
import org.protege.editor.core.ui.util.CheckTableModel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.OWLEditorKitFactory;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.error.OntologyLoadErrorHandlerUI;
import org.protege.editor.owl.ui.list.OWLAxiomList;
import org.protege.editor.owl.ui.ontology.imports.missing.MissingImportHandlerUI;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.io.OWLParser;
import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.io.UnparsableOntologyException;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchPanel extends JPanel {

	private static transient Logger log = Logger.getLogger(SearchPanel.class);

	public static String DETAILS_ICON_URL;
	
	private static int INDENT = 4;

	private JXTextField searchField;
	private JButton searchButton;
	private JCheckBox searchInAttributesBox;
	private JRadioButton exactMatchRadioButton;
	private JRadioButton containsRadioButton;
	private SearchResultTable searchResultTable;
	private JComboBox categoriesBox;
	private JComboBox groupsBox;
	private OntologiesTable ontologiesTable;
	private CheckTable<Ontology> ontologiesTable1;
	private JTree classTree;

	private final WaitLayerUI layerUI = new WaitLayerUI("Searching");

	private OWLEditorKit editorKit;

	public SearchPanel(OWLEditorKit editorKit) {
		this.editorKit = editorKit;

		// create the UI
		createUI();

		// fill comboboxes and table with data that can be used for filtering
		initFilterData();

		DETAILS_ICON_URL = this.getClass().getClassLoader().getResource("details.png").toString();
	}
	
	private void initFilterData(){
		OntologiesRetrievingTask task1 = new OntologiesRetrievingTask();
		ProtegeApplication.getBackgroundTaskManager().startTask(task1);
		task1.execute();

		CategoriesRetrievingTask task2 = new CategoriesRetrievingTask();
		ProtegeApplication.getBackgroundTaskManager().startTask(task2);
		task2.execute();

		GroupsRetrievingTask task3 = new GroupsRetrievingTask();
		ProtegeApplication.getBackgroundTaskManager().startTask(task3);
		task3.execute();
	}

	private void createUI() {
		setLayout(new BorderLayout());
		
		

		////////////////////////////////////////////////////////////////////////////////////
		// the onSearch options panel
		//
		
		JPanel searchFieldPanel = new JPanel();
		searchFieldPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		searchField = new JXTextField("Enter search term (e.g. Melanoma)", Color.GRAY);
		searchField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				validateSearchButton();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				validateSearchButton();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				validateSearchButton();
			}
		});
		searchFieldPanel.add(searchField, c);
		
		searchButton = new JButton("Search");
		c.weightx = 0.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		searchFieldPanel.add(searchButton, c);

		searchButton.addActionListener(e -> onSearch());

		searchField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					onSearch();
				}
			}
		});
		
		c.anchor = GridBagConstraints.WEST;
		searchInAttributesBox = new JCheckBox("Include attributes in search");
		searchFieldPanel.add(searchInAttributesBox, c);
		
		ButtonGroup bg = new ButtonGroup();
		JPanel radionButtonHolder = new JPanel(new FlowLayout(FlowLayout.LEFT));
		searchFieldPanel.add(radionButtonHolder, c);
		
		containsRadioButton = new JRadioButton("Contains");
		containsRadioButton.setSelected(true);
		bg.add(containsRadioButton);
		radionButtonHolder.add(containsRadioButton);
		
		exactMatchRadioButton = new JRadioButton("Exact match");
		bg.add(exactMatchRadioButton);
		radionButtonHolder.add(exactMatchRadioButton);
		
		///////////////////////////////////////////////////////////////////////////////////////
		// the categories and groups panel
		//
		
		JPanel holderPanel = new JPanel();
		holderPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		categoriesBox = new JComboBox();
		DefaultComboBoxModel<Object> loadingCatsModel = new DefaultComboBoxModel<>();
		loadingCatsModel.addElement("Loading categories...");
		categoriesBox.setModel(loadingCatsModel);
		categoriesBox.addActionListener(e -> updateOntologiesList());
		holderPanel.add(new JLabel("Categories"), gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		holderPanel.add(categoriesBox, gbc);
		groupsBox = new JComboBox();
		DefaultComboBoxModel<Object> loadingGroupsModel = new DefaultComboBoxModel<>();
		loadingGroupsModel.addElement("Loading groups...");
		groupsBox.setModel(loadingGroupsModel);
		groupsBox.addActionListener(e -> updateOntologiesList());
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = 1;
		holderPanel.add(new JLabel("Groups"), gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		holderPanel.add(groupsBox, gbc);
		
		
		ontologiesTable1 = new CheckTable<>("Ontologies");
		ontologiesTable1.setDefaultRenderer(new DefaultTableCellRenderer(){
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if(column == 1){
					JLabel l = (JLabel) c;
					l.setText(((Ontology)value).getName());
				}
				return c;
			}
		});
		ontologiesTable = new OntologiesTable();
		JScrollPane scroll = new JScrollPane(ontologiesTable1);
		scroll.setPreferredSize(new Dimension(200, 120));
//		JXTitledPanel p = new JXTitledPanel("Ontologies:");
//		p.add(scroll);
		holderPanel.add(scroll, gbc);
		
		//////////////
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setBorder(new TitledBorder("Search in Bioportal ontologies"));
		splitPane.setLeftComponent(searchFieldPanel);
		splitPane.setRightComponent(holderPanel);
		splitPane.setDividerLocation(0.5);
		
		
		///////////////////////////////////////////////////////////////////////////////////////
		// the onSearch result panel
		//
		searchResultTable = new SearchResultTable();
		searchResultTable.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				int row = searchResultTable.rowAtPoint(e.getPoint());
				int column = searchResultTable.columnAtPoint(e.getPoint());

				if (column == 4 && row <= searchResultTable.getRowCount() && row >= 0) {
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else {
					setCursor(null);
				}
			}

		});

		searchResultTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = searchResultTable.rowAtPoint(e.getPoint());
				int column = searchResultTable.columnAtPoint(e.getPoint());

				if (row >= 0 && row <= searchResultTable.getRowCount()){
					switch(column){
						case 0:
//							try {
//							NativeBrowserLauncher.openURL(getShowConceptInBPString(searchResultTable.getSearchBean(row)));
//						} catch (UnsupportedEncodingException e2) {
//							e2.printStackTrace();
//						}
							createLazyClassHierarchyTree(searchResultTable.getEntity(row));
						break;
						case 2:
							showOntologyDetailsDialog(searchResultTable.getEntity(row));
							break;//NativeBrowserLauncher.openURL(getShowOntologyInBPString(searchResultTable.getSearchBean(row)));break;
						case 4:
							showConceptDetailsDialog(searchResultTable.getEntity(row));
							break;
						case 5:
//							showModuleAxiomsDialog(searchResultTable.getSearchBean(row));
							break;
					}
					
					setCursor(null);
				}
			}
		});
		
		JPanel searchResultPanel = new JPanel(new BorderLayout());
		searchResultPanel.setBorder(new TitledBorder("Matching terms"));
		searchResultPanel.add(new JLayer<>(new JScrollPane(searchResultTable), layerUI), BorderLayout.CENTER);
		
		add(splitPane, BorderLayout.NORTH);
		add(searchResultPanel, BorderLayout.CENTER);
		validateSearchButton();
	}

	private void onSearch() {
		SearchTask searchTask = new SearchTask(
				searchField.getText(),
				ontologiesTable1.getFilteredValues(),
				exactMatchRadioButton.isSelected(),
				searchInAttributesBox.isSelected());
		ProtegeApplication.getBackgroundTaskManager().startTask(searchTask);
//		editorKit.getWorkspace().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		searchButton.setEnabled(false);
		layerUI.start();
		searchTask.execute();
	}


	private void validateSearchButton() {
		searchButton.setEnabled(!searchField.getText().isEmpty());
	}

	private void showConceptDetailsDialog(Entity searchBean) {
		new ConceptDetailsDialog(searchBean);
	}
	
	private void showOntologyDetailsDialog(Entity entity) {
		new OntologyDetailsDialog(BioportalRESTService.getOntology(entity));
	}
	
	private void createLazyClassHierarchyTree(final Entity entity){
//		ClassBean cb = null;
//		try {
//			cb = BioportalRESTService.getConceptProperties(entity.getEntityLinks().getUi());
//		} catch (PrivateOntologyException e) {
//			e.printStackTrace();
//			String link = "http://bioportal.bioontology.org/ontologies/" + searchBean.getOntologyId() + "?p=terms";
//			String message = "This ontology is either private or licensed. " +
//					"Please go to <a href='" + link + "'>" + link + "</a> to get access to the ontology.";
//			JEditorPane pane = new JEditorPane("text/html", message);
//			pane.setEditable(false);
//			pane.setOpaque(false);
//			pane.addHyperlinkListener(new HyperlinkListener() {
//
//				@Override
//				public void hyperlinkUpdate(HyperlinkEvent e) {
//					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
//						NativeBrowserLauncher.openURL(e.getURL().toString());
//					}
//				}
//			});
//			JOptionPane optionPane = new JOptionPane(pane, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
//			JDialog dlg = createDialog(this, "Access Error", optionPane, pane);
//	        dlg.setVisible(true);
//			return;
//		}
		ImportDialog dialog = new ImportDialog(SwingUtilities.getWindowAncestor(this), entity, editorKit);
		dialog.setVisible(true);
	}
	
	private static JDialog createDialog(Component parent, String title, JOptionPane optionPane, final JComponent defaultFocusedComponent) {
        JDialog dlg = optionPane.createDialog(parent, title);
        dlg.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                if (defaultFocusedComponent != null) {
                    defaultFocusedComponent.requestFocusInWindow();
                }
            }
        });
        dlg.setLocationRelativeTo(parent);
        dlg.setResizable(true);
        dlg.setModal(true);
        dlg.pack();
        return dlg;
    }
	
	
	
	private void showModuleAxiomsDialog(Entity searchBean) {
		OWLOntology ontology = downloadOntology(searchBean.getEntityLinks().getOntology());
		System.out.println(ontology);
		OWLOntology module = extractModule(ontology, searchBean.getId());
		System.out.println("Module size: " + module.getLogicalAxiomCount());
		System.out.println(module.getLogicalAxioms());
//		System.out.println(searchBean.getConceptId());

		OWLAxiomList list = new OWLAxiomList(editorKit);
//		list.setAxioms(Collections.singleton(module.getAxioms()), ontology.getImportsClosure());
		list.setPreferredSize(new Dimension(700, 400));
		JDialog dialog = new JDialog();
		dialog.setPreferredSize(new Dimension(700, 400));
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setTitle("Extracted axioms for concept " + searchBean.getPrefLabel());
		dialog.setModal(true);
		dialog.add(new JScrollPane(list));
		dialog.pack();
		dialog.setVisible(true);

	}

	private OWLOntology downloadOntology(String ontologyVersionID) {
		final BackgroundTask task = ProtegeApplication.getBackgroundTaskManager().startTask(
				"downloading " + ontologyVersionID);
		System.out.println("http://rest.bioontology.org/bioportal/ontologies/download/" + ontologyVersionID
				+ "?email=example@example.org");
		try {
			InputStream in = new URL("http://rest.bioontology.org/bioportal/ontologies/download/" + ontologyVersionID
					+ "?email=example@example.org").openStream();
			OWLOntologyManager man = OWLManager.createOWLOntologyManager();
//			man.setSilentMissingImportsHandling(true);
//			man.addMissingImportListener(new MissingImportListener() {
//				
//				@Override
//				public void importMissing(MissingImportEvent event) {
//					System.out.println(event.getImportedOntologyURI());
//					
//				}
//			});
//			
			OWLOntology ontology = man.loadOntologyFromOntologyDocument(in);
			ProtegeApplication.getBackgroundTaskManager().endTask(task);
			return ontology;
		} catch (OWLOntologyCreationIOException e) {
			// IOExceptions during loading get wrapped in an
			// OWLOntologyCreationIOException
			Throwable ioException = e.getCause();
			if (ioException instanceof FileNotFoundException) {
				System.out.println("Could not load ontology. File not found: " + ioException.getMessage());
			} else if (ioException instanceof UnknownHostException) {
				System.out.println("Could not load ontology. Unknown host: " + ioException.getMessage());
			} else {
				System.out.println("Could not load ontology: " + ioException.getClass().getSimpleName() + " "
						+ ioException.getMessage());
			}
		} catch (UnparsableOntologyException e) {
			// If there was a problem loading an ontology because there are
			// syntax errors in the document (file) that
			// represents the ontology then an UnparsableOntologyException is
			// thrown
			System.out.println("Could not parse the ontology: " + e.getMessage());
			// A map of errors can be obtained from the exception
			Map<OWLParser, OWLParserException> exceptions = e.getExceptions();
			// The map describes which parsers were tried and what the errors
			// were
			for (OWLParser parser : exceptions.keySet()) {
				System.out.println("Tried to parse the ontology with the " + parser.getClass().getSimpleName()
						+ " parser");
				System.out.println("Failed because: " + exceptions.get(parser).getMessage());
			}
		} catch (UnloadableImportException e) {
			// If our ontology contains imports and one or more of the imports
			// could not be loaded then an
			// UnloadableImportException will be thrown (depending on the
			// missing imports handling policy)
			System.out.println("Could not load import: " + e.getImportsDeclaration());
			// The reason for this is specified and an
			// OWLOntologyCreationException
			OWLOntologyCreationException cause = e.getOntologyCreationException();
			System.out.println("Reason: " + cause.getMessage());
		} catch (OWLOntologyCreationException e) {
			System.out.println("Could not load ontology: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		ProtegeApplication.getBackgroundTaskManager().endTask(task);
		return null;
	}

	private OWLOntology extractModule(OWLOntology ontology, String conceptID) {
		try {
			OWLOntologyManager man = OWLManager.createOWLOntologyManager();

			OWLClass cl = man.getOWLDataFactory().getOWLClass(IRI.create(conceptID));

			SyntacticLocalityModuleExtractor moduleExtractor = new SyntacticLocalityModuleExtractor(man, ontology, ModuleType.STAR);

			OWLOntology moduleOntology = moduleExtractor.extractAsOntology(cl.getSignature(), IRI.create("http://dl-learner.org/module" + conceptID));

			return moduleOntology;
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		return null;

	}

	private static BundleContext createDummyContext() {
		return (BundleContext) Proxy.newProxyInstance(BundleContext.class.getClassLoader(),
													  new Class[] { BundleContext.class }, new InvocationHandler() {
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return null;
					}
				});
	}

	private static OWLEditorKit createDummyEditorKit() {
		OWLEditorKit editorKit = new OWLEditorKit(new OWLEditorKitFactory()) {

			@Override
			public void setOWLModelManager(OWLModelManager modelManager) {
				Field f = null;
				try {
					f = OWLEditorKit.class.getDeclaredField("modelManager");
					f.setAccessible(true);
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				}
				try {
					f.set(this, modelManager);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void initialise() {
				OWLModelManager modelManager = new de.leipzig.imise.bioportal.util.OWLModelManagerImpl();
				setOWLModelManager(modelManager);

//				modelManager.setExplanationManager(new ExplanationManager(this));
				modelManager.setMissingImportHandler(new MissingImportHandlerUI(this));
				modelManager.setSaveErrorHandler((ont, physicalURIForOntology, e) -> {
				});

//				ontologyChangeListener = owlOntologyChanges -> modifiedDocument = true;
//				this.modelManager.addOntologyChangeListener(ontologyChangeListener);
//
//				searchManagerSelector = new SearchManagerSelector(this);

				OntologyLoadErrorHandlerUI loadErrorHandler = new OntologyLoadErrorHandlerUI(this);

				modelManager.setLoadErrorHandler(loadErrorHandler);

//				getWorkspace().refreshComponents();
			}

			@Override
			protected void initialiseCompleted() {

			}
		};
		return editorKit;
	}

	public static void main(String[] args) throws Exception{
		OWLEditorKit editorKit = createDummyEditorKit();
		editorKit.getOWLModelManager().createNewOntology(new OWLOntologyID(IRI.create("http://test.org")), URI.create("http://test.org"));
		SwingUtilities.invokeLater(() -> {
			JFrame dialog = new JFrame("Bioportal");
			dialog.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			dialog.setType(JFrame.Type.UTILITY);
			dialog.add(new SearchPanel(editorKit));
			dialog.setPreferredSize(new Dimension(1200, 600));

			dialog.pack();
			dialog.setVisible(true);
		});




	}

	private void updateOntologiesList() {
		Category category = (Category) categoriesBox.getSelectedItem();
		Group group = (Group) groupsBox.getSelectedItem();
		List<Ontology> ontologies = new ArrayList<>();
		for (Ontology ontology : BioportalManager.getInstance().getOntologies()) {
			if ((category.getName().equals("ALL CATEGORIES") || ontology.getCategories().contains(category.getId()))
					&& (group.getName().equals("ALL GROUPS") || ontology.getGroups().contains(group.getId()))) {
				ontologies.add(ontology);
			}
		}
		Collections.sort(ontologies, (o1, o2) -> o1.getName().compareTo(o2.getName()));
		CheckTableModel<Ontology> model = ontologiesTable1.getModel();
		model.setData(ontologies, false);
//		ontologiesTable.setOntologies(ontologies);
	}
	
	class SearchTask extends SwingWorker<Page, Void> implements BackgroundTask{

		
		private String searchTerm;
		private List<Ontology> ontologies;
		private boolean isExactMatch;
		private boolean includeProperties;
		
		public SearchTask(String searchTerm, List<Ontology> ontologies, boolean isExactMatch, boolean includeProperties){
			this.searchTerm = searchTerm;
			this.ontologies = ontologies;
			this.isExactMatch = isExactMatch;
			this.includeProperties = includeProperties;
		}

		@Override
		protected Page doInBackground() throws Exception {
			return BioportalManager.getInstance().getSearchClassesResults(searchTerm, ontologies, isExactMatch, includeProperties);
		}
		
		@Override
		protected void done() {
			layerUI.stop();
			ProtegeApplication.getBackgroundTaskManager().endTask(this);
			searchButton.setEnabled(true);
			Page result;
			try {
				result = get();
				searchResultTable.setSearchResults(searchTerm, result.getEntities());
//				editorKit.getWorkspace().setCursor(Cursor.getDefaultCursor());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	class OntologyDownloadTask extends SwingWorker implements BackgroundTask {

		@Override
		protected Object doInBackground() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

	}
	
	class OntologiesRetrievingTask extends SwingWorker<List<Ontology>, Void> implements BackgroundTask {

		@Override
		protected List<Ontology> doInBackground() throws Exception {
			return new ArrayList<>(BioportalManager.getInstance().getOntologies());
		}
		
		@Override
		protected void done() {
			ProtegeApplication.getBackgroundTaskManager().endTask(this);
			List<Ontology> result;
			try {
				result = get();
				Collections.sort(result, (o1, o2) -> o1.getName().compareTo(o2.getName()));
				CheckTableModel<Ontology> model = ontologiesTable1.getModel();
				model.setData(result, false);
				ontologiesTable.setOntologies(result);
//				editorKit.getWorkspace().setCursor(Cursor.getDefaultCursor());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

	}
	
	class CategoriesRetrievingTask extends SwingWorker<List<Category>, Void> implements BackgroundTask {

		@Override
		protected List<Category> doInBackground() throws Exception {
			return BioportalRESTService.getCategories();
		}
		
		@Override
		protected void done() {
			ProtegeApplication.getBackgroundTaskManager().endTask(this);
			List<Category> result;
			try {
				result = get();
				Collections.sort(result, (o1, o2) -> o1.getName().compareTo(o2.getName()));
				Category allCategory = new Category();
				allCategory.setName("ALL CATEGORIES");
				result.add(0, allCategory);
				categoriesBox.setModel(new DefaultComboBoxModel(result.toArray()));
//				editorKit.getWorkspace().setCursor(Cursor.getDefaultCursor());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

	}
	
	class GroupsRetrievingTask extends SwingWorker<List<Group>, Void> implements BackgroundTask {

		@Override
		protected List<Group> doInBackground() throws Exception {
			return BioportalRESTService.getGroups();
		}
		
		@Override
		protected void done() {
			ProtegeApplication.getBackgroundTaskManager().endTask(this);
			List<Group> result;
			try {
				result = get();
				Collections.sort(result, (o1, o2) -> o1.getName().compareTo(o2.getName()));
				Group allGroup = new Group();
				allGroup.setName("ALL GROUPS");
				result.add(0, allGroup);
				groupsBox.setModel(new DefaultComboBoxModel(result.toArray()));
//				editorKit.getWorkspace().setCursor(Cursor.getDefaultCursor());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

	}
	
}