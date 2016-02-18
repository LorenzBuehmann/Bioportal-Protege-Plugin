package de.leipzig.imise.bioportal.ui;

import de.leipzig.imise.bioportal.BioportalConstants;
import de.leipzig.imise.bioportal.BioportalViewComponent;
import de.leipzig.imise.bioportal.bean.concept.ClassBean;
import de.leipzig.imise.bioportal.util.BioportalConcept;
import de.leipzig.imise.bioportal.util.PrivateOntologyException;
import org.apache.log4j.Logger;
import org.ncbo.stanford.bean.search.Page;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.bean.search.SearchResultListBean;
import org.ncbo.stanford.util.BioportalSearch;
import org.ncbo.stanford.util.HTMLUtil;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.progress.BackgroundTask;
import org.protege.editor.core.ui.util.NativeBrowserLauncher;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.list.OWLAxiomList;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.io.OWLParser;
import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.io.UnparsableOntologyException;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.BorderUIResource;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CopyOfSearchPanel extends JPanel {

	private static transient Logger log = Logger.getLogger(CopyOfSearchPanel.class);

	public static String DETAILS_ICON_URL = BioportalViewComponent.class.getResource("details.png").toString();
	
	private static int INDENT = 4;

	private JXTextField searchField;
	private JButton searchButton;
	private JCheckBox searchInAttributesBox;
	private JRadioButton exactMatchRadioButton;
	private JRadioButton containsRadioButton;
	private SearchResultTable searchResultTable;

	private List<SearchBean> searchResults;

	private OWLEditorKit editorKit;


	public CopyOfSearchPanel(OWLEditorKit editorKit) {
		this.editorKit = editorKit;
		searchResults = new ArrayList<SearchBean>();

		createUI();
	}

	private void createUI() {
		setLayout(new BorderLayout());
		
		

		////////////////////////////////////////////////////////////////////////////////////
		// the search options panel
		//
		
		JPanel searchFieldPanel = new JPanel();
		searchFieldPanel.setBorder(new TitledBorder("Search in Bioportal ontologies"));
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
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				search();
			}
		});
		searchField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					search();
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
		JComboBox categoryBox = new JComboBox();
		holderPanel.add(new JLabel("Categories"), gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		holderPanel.add(categoryBox, gbc);
		JComboBox groupBox = new JComboBox();
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = 1;
		holderPanel.add(new JLabel("Groups"), gbc);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		holderPanel.add(groupBox, gbc);
		
		//////////////
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(searchFieldPanel);
		splitPane.setRightComponent(holderPanel);
		
		
		///////////////////////////////////////////////////////////////////////////////////////
		// the search result panel
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
						case 0:try {
							NativeBrowserLauncher.openURL(getShowConceptInBPString(searchResultTable.getSearchBean(row)));
						} catch (UnsupportedEncodingException e2) {
							e2.printStackTrace();
						}break;
						case 2:NativeBrowserLauncher.openURL(getShowOntologyInBPString(searchResultTable.getSearchBean(row)));break;
						case 4:showDetails(searchResultTable.getSearchBean(row));break;
						case 5:printHierarchy(searchResultTable.getSearchBean(row)); showModuleAxiomsDialog(searchResultTable.getSearchBean(row));break;
					}
					
					setCursor(null);
				}
			}
		});
		
		JPanel searchResultPanel = new JPanel(new BorderLayout());
		searchResultPanel.setBorder(new TitledBorder("Matching terms"));
		searchResultPanel.add(new JScrollPane(searchResultTable), BorderLayout.CENTER);
		
		add(splitPane, BorderLayout.NORTH);
		add(searchResultPanel, BorderLayout.CENTER);
		validateSearchButton();
	}

	private void search() {
		try {
			searchResults.clear();
			SearchTask searchTask = new SearchTask(new URL(getSearchString()));
			ProtegeApplication.getBackgroundTaskManager().startTask(searchTask);
			editorKit.getWorkspace().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			searchTask.execute();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}


	private void validateSearchButton() {
		searchButton.setEnabled(!searchField.getText().isEmpty());
	}

	private void showDetails(SearchBean searchBean) {
		BioportalConcept bpc = new BioportalConcept();
		String urlString = "";
		try {
			urlString = getShowDetailsString(searchBean);
		} catch (UnsupportedEncodingException e) {
			log.warn("Encode URI failed: " + e.getMessage(), e);
		}
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			log.warn("Invalid URL: " + urlString, e);
		}
		if (url == null) {
			return;
		}

		JEditorPane detailsPane = new JEditorPane();
		detailsPane.setEditable(false);
		detailsPane.setEditorKit(new HTMLEditorKit());
		// add a CSS rule to force body tags to use the default label font
		// instead of the value in javax.swing.text.html.default.csss
		Font font = UIManager.getFont("Label.font");
		String bodyRule = "body { font-family: " + font.getFamily() + "; " + "font-size: " + font.getSize() + "pt; }";
		((HTMLDocument) detailsPane.getDocument()).getStyleSheet().addRule(bodyRule);
		// ((HTMLDocument)editorPane.getDocument()).getStyleSheet().addRule(TableCss.CSS);
		detailsPane.setBorder(BorderUIResource.getEtchedBorderUIResource());

		StringBuffer buffer = new StringBuffer();
		buffer.append("<html><body>");
		buffer
				.append("<table width=\"100%\" class=\"servicesT\" style=\"border-collapse:collapse;border-width:0px;padding:5px\"><tr>");

		buffer.append("<td class=\"servHd\" style=\"background-color:#8E798D;color:#FFFFFF;\">Property</td>");
		buffer.append("<td class=\"servHd\" style=\"background-color:#8E798D;color:#FFFFFF;\">Value</td>");

		String oddColor = "#F4F2F3";
		String evenColor = "#E6E6E5";

		ClassBean cb = null;
		try {
			cb = bpc.getConceptProperties(url);
		} catch (PrivateOntologyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (cb == null) {
			detailsPane.setText("<html><body><i>No search results.</i></body></html>");
			return;
		}
		
		
		String label = cb.getLabel();
		Map<Object, Object> relationsMap = cb.getRelations();
		
		buffer.append(getDetailsProperty("ID", cb.getId(), evenColor));
		buffer.append(getDetailsProperty("Name", label, oddColor));
		if(relationsMap.get("ChildCount") != null){
			buffer.append(getDetailsProperty("Children", relationsMap.get("ChildCount").toString(), evenColor));
			relationsMap.remove("ChildCount");
		}
		if(relationsMap.get("InstanceCount") != null){
			buffer.append(getDetailsProperty("Instances", relationsMap.get("InstanceCount").toString(), oddColor));
			relationsMap.remove("InstanceCount");
		}
		
		
		int i = 0;
		for (Object obj : relationsMap.keySet()) {
			Object value = relationsMap.get(obj);
			String color = i % 2 == 0 ? evenColor : oddColor;
			if (value != null) {
				String text = HTMLUtil.replaceEOF(value.toString());
				if (text.startsWith("[")) {
					text = text.substring(1, text.length() - 1);
				}
				if (text.length() > 0) {
					buffer.append("<tr>");
					buffer.append("<td class=\"servBodL\" style=\"background-color:" + color
							+ ";padding:7px;font-weight: bold;\" >");
					buffer.append(obj.toString());
					buffer.append("</td>");
					buffer.append("<td class=\"servBodL\" style=\"background-color:" + color + ";padding:7px;\" >");
					buffer.append(text);
					buffer.append("</td>");
					buffer.append("</tr>");
					i++;
				}
			}
		}
		buffer.append("</table>");

		String directLink;
		try {
			directLink = getShowConceptInBPString(searchBean);
		} catch (UnsupportedEncodingException e) {
			directLink = "???";
		}
		if (directLink != null && directLink.length() > 0) {
			buffer.append("<div style=\"padding:5px;\"><br><b>Direct link in BioPortal:</b> ");
			buffer.append("<a href=\"");
			buffer.append(directLink);
			buffer.append("\">");
			buffer.append(directLink);
			buffer.append("</a></div>");
			buffer.append("<br>"); // important in order to avoid automatic
			// horizontal scrolling to the right end of
			// the page when displaying very long URLs
		}
		buffer.append("</body></html>");
		detailsPane.setText(buffer.toString());
		detailsPane.setPreferredSize(new Dimension(700, 500));

		detailsPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					NativeBrowserLauncher.openURL(e.getURL().toString());
				}
			}
		});

		JDialog dialog = new JDialog();
		dialog.setTitle("Properties for " + label + " from BioPortal");
		dialog.setModal(true);
		dialog.add(new JScrollPane(detailsPane));
		dialog.pack();
		dialog.setVisible(true);

	}
	
	private String getDetailsProperty(String name, String value, String color){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<tr>");
		buffer.append("<td class=\"servBodL\" style=\"background-color:" + color
				+ ";padding:7px;font-weight: bold;\" >");
		buffer.append(name);
		buffer.append("</td>");
		buffer.append("<td class=\"servBodL\" style=\"background-color:" + color + ";padding:7px;\" >");
		buffer.append(value);
		buffer.append("</td>");
		buffer.append("</tr>");
		
		return buffer.toString();
	}
	
	private void extractChildren(SearchBean searchBean){
		String urlString = getExtractChildrenString(searchBean);
		try {
			URL url = new URL(urlString);
			System.out.println(url.toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void printHierarchy(SearchBean searchBean){
		OWLOntology ont = downloadOntology(searchBean.getOntologyVersionId());
		OWLReasoner reasoner = new StructuralReasonerFactory().createNonBufferingReasoner(ont);
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		OWLDataFactory factory = OWLManager.getOWLDataFactory();
		OWLClass cl = factory.getOWLClass(IRI.create(searchBean.getConceptId()));
		printHierarchy(reasoner, cl, 0);
	}

	private void showModuleAxiomsDialog(SearchBean searchBean) {
		OWLOntology ontology = downloadOntology(searchBean.getOntologyVersionId());
		System.out.println(ontology);
		OWLOntology module = extractModule(ontology, searchBean.getConceptId());
		System.out.println("Module size: " + module.getLogicalAxiomCount());
		System.out.println(module.getLogicalAxioms());
//		System.out.println(searchBean.getConceptId());

		OWLAxiomList list = new OWLAxiomList(editorKit);
//		list.setAxioms(Collections.singleton(module.getAxioms()), ontology.getImportsClosure());
		list.setPreferredSize(new Dimension(700, 400));
		JDialog dialog = new JDialog();
		dialog.setPreferredSize(new Dimension(700, 400));
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setTitle("Extracted axioms for concept " + searchBean.getContents());
		dialog.setModal(true);
		dialog.add(new JScrollPane(list));
		dialog.pack();
		dialog.setVisible(true);

	}

	private OWLOntology downloadOntology(int ontologyVersionID) {
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
		} catch (MalformedURLException e) {
			e.printStackTrace();
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

	private String getShowConceptInBPString(SearchBean searchBean) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		sb.append(BioportalConstants.BP_REPOSITORY_BASE_STRING);
		sb.append(BioportalConstants.BP_VISUALIZE_STR);
		sb.append("/");
		sb.append(searchBean.getOntologyVersionId());
		sb.append("/?conceptid=");
		sb.append(encodeURI(searchBean.getConceptId()));

		return sb.toString();
	}
	
	private String getShowOntologyInBPString(SearchBean searchBean){
		StringBuffer sb = new StringBuffer();
		sb.append(BioportalConstants.BP_REPOSITORY_BASE_STRING);
		sb.append(BioportalConstants.BP_ONTOLOGIES_STRING);
		sb.append("/");
		sb.append(searchBean.getOntologyVersionId());

		return sb.toString();
	}

	private String getShowDetailsString(SearchBean searchBean) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		sb.append(BioportalConstants.BP_REST_BASE_URL_STRING);
		sb.append(BioportalConstants.BP_CONCEPTS_STR);
		sb.append("/");
		sb.append(searchBean.getOntologyVersionId());
		sb.append("/?conceptid=");
		sb.append(encodeURI(searchBean.getConceptId()));

		return sb.toString();
	}
	
	private String getExtractParentsString(SearchBean searchBean) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		sb.append(BioportalConstants.BP_REST_BASE_URL_STRING);
		sb.append(BioportalConstants.BP_CONCEPTS_STR);
		sb.append("/");
		sb.append(BioportalConstants.BP_PARENTS_STR);
		sb.append("/");
		sb.append(searchBean.getOntologyVersionId());
		sb.append("/");
		sb.append(searchBean.getConceptId());

		return sb.toString();
	}
	
	private String getExtractChildrenString(SearchBean searchBean) {
		StringBuffer sb = new StringBuffer();
		sb.append(BioportalConstants.BP_REST_BASE_URL_STRING);
		sb.append(BioportalConstants.BP_CONCEPTS_STR);
		sb.append("/");
		sb.append(BioportalConstants.BP_CHILDREN_STR);
		sb.append("/");
		sb.append(searchBean.getOntologyVersionId());
		sb.append("/");
		sb.append(searchBean.getConceptId());
		System.out.println(sb);

		return sb.toString();
	}
	

	private String getSearchString() {
		StringBuffer sb = new StringBuffer();
		sb.append(BioportalConstants.BP_REST_BASE_URL_STRING);
		sb.append(BioportalConstants.BP_SEARCH_STRING);
		sb.append("/");
		sb.append(searchField.getText());
		sb.append("?");
		sb.append(BioportalConstants.BP_EXACT_MATCH_STRING + "=");
		sb.append(exactMatchRadioButton.isSelected() ? 1 : 0);
		sb.append("&");
		sb.append(BioportalConstants.BP_INCLUDE_PROPERTIES_STRING + "=");
		sb.append(searchInAttributesBox.isSelected() ? 1 : 0);
		sb.append("&");
		sb.append(BioportalConstants.BP_EMAIL_DUMMY);
		return sb.toString();
	}

	private String encodeURI(String text) throws UnsupportedEncodingException {
		return URLEncoder.encode(text, "UTF-8").toString().replaceAll("\\+", "%20");
	}
	
	public static void main(String[] args){
		
		JDialog dialog = new JDialog();
		dialog.add(new CopyOfSearchPanel(null));
		dialog.setPreferredSize(new Dimension(600, 600));
		dialog.pack();
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}
	
	private void printHierarchy(OWLReasoner reasoner, OWLClass clazz, int level){
		if(!clazz.isOWLNothing()){
			for(int i = 0; i < level * INDENT; i++) {
				System.out.println(" ");
			}
			System.out.println(clazz);
			for (OWLClass child : reasoner.getSubClasses(clazz, true).getFlattened()) {
				if (!child.equals(clazz) && !child.isOWLNothing()) {
					printHierarchy(reasoner, clazz, level++);
				}
			}
		}
		
	}
	
	class SearchTask extends SwingWorker<List<SearchBean>, Void> implements BackgroundTask{

		
		private URL searchURL;
	
		public SearchTask(URL searchURL){
			this.searchURL = searchURL;
		}

		@Override
		protected List<SearchBean> doInBackground() throws Exception {
			BioportalSearch bps = new BioportalSearch();

			Page page = null;
			try {
				page = bps.getSearchResults(searchURL);
			} catch (IOException e) {
				log.warn("Error accessing BioPortal: " + searchURL, e);
			}
			SearchResultListBean data = page.getContents();
			
			return data.getSearchResultList();
		}
		
		@Override
		protected void done() {
			ProtegeApplication.getBackgroundTaskManager().endTask(this);
			List<SearchBean> result;
			try {
				result = get();
				searchResultTable.setSearchResults(result);
				editorKit.getWorkspace().setCursor(Cursor.getDefaultCursor());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
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
}