package de.leipzig.imise.bioportal.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import de.leipzig.imise.bioportal.bean.concept.ClassBean;
import de.leipzig.imise.bioportal.util.BioportalConcept;
import de.leipzig.imise.bioportal.util.BioportalOntology;
import de.leipzig.imise.bioportal.util.PrivateOntologyException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class BioportalRESTService {
	public static final String DEFAULT_BASE_URL = "http://data.bioontology.org";
	public static final String STAGE_BASE_URL 	= "http://stagerest.bioontology.org/bioportal/";
	public static final String SUFFIX_ONTOLOGIES = "ontologies/";
	public static final String SUFFIX_ONTOLOGY_VIRTUAL = "virtual/ontology/";
	public static final String SUFFIX_ONTOLOGY_VERSIONS = "ontologies/versions/";
	public static final String SUFFIX_ONTOLOGY_METRICS = "ontologies/metrics/";
	public static final String SUFFIX_CONCEPTS = "concepts/";
	public static final String SUFFIX_SEARCH = "search/";
	public static final String SUFFIX_CATEGORIES = "categories/";
	public static final String SUFFIX_GROUPS = "groups/";
	public static final String SEARCH_PROPERTY_ONTOLOGY_IDS = "ontologyids=";
	public static final String SEARCH_PROPERTY_EXACT_MATCH = "isexactmatch";
	public static final String SEARCH_PROPERTY_INCLUDE_PROPERTIES = "includeproperties";
	public static final String SEARCH_PROPERTY_OBJECT_TYPES = "objecttypes";
	public static final String SEARCH_PROPERTY_OBJECT_TYPE_CLASS = "class";
	public static final String SEARCH_PROPERTY_OBJECT_TYPE_PROPERTY = "property";
	public static final String SEARCH_PROPERTY_OBJECT_TYPE_INDIVIDUAL = "individual";
	public static final String PROPERTY_CONCEPT_ID = "conceptid=";
	public static final String DEFAULT_EMAIL = "email=example@example.org";

	public final static String API_KEY_PARAM = "apikey";	
	public static final String BP_PRODUCTION_PROTEGE_API_KEY = API_KEY_PARAM + "=8fadfa2c-47de-4487-a1f5-b7af7378d693";
	public static final String API_KEY = "8fadfa2c-47de-4487-a1f5-b7af7378d693";

	static final ObjectMapper mapper = new ObjectMapper();

	private static final Map<String, String> serviceLinks = new HashMap<>();


	static {
		init();
	}

	public static void init() {
		// Get the available resources
		String resourcesString = get(DEFAULT_BASE_URL + "/");
		JsonNode resources = jsonToNode(resourcesString);

		// Follow the link by looking for available services in the list of links
		JsonNode links = resources.get("links");
		for(Iterator<Map.Entry<String, JsonNode>> it = links.fields(); it.hasNext();) {
			Map.Entry<String, JsonNode> entry = it.next();
			serviceLinks.put(entry.getKey(), entry.getValue().asText());
		}
	}

	private static String get(String urlToGet) {
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		try {
			url = new URL(urlToGet);
			System.out.println(url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "apikey token=" + API_KEY);
			conn.setRequestProperty("Accept", "application/json");
			rd = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static URL getBioportalCategoriesURL(){
		URL url = null;
		try {
			url = new URL(getUrlWithDefaultSuffix(DEFAULT_BASE_URL + SUFFIX_CATEGORIES));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	private static URL getBioportalGroupsURL(){
		URL url = null;
		try {
			url = new URL(getUrlWithDefaultSuffix(DEFAULT_BASE_URL + SUFFIX_GROUPS));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	private static URL getOntologiesURL(){
		URL url = null;
		try {
			url = new URL(getUrlWithDefaultSuffix(DEFAULT_BASE_URL + SUFFIX_ONTOLOGIES));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	public static URL getOntologyPropertiesURL(int ontologyId){
		URL url = null;
		try {
			url = new URL(getUrlWithDefaultSuffix(DEFAULT_BASE_URL + SUFFIX_ONTOLOGY_VIRTUAL + ontologyId + "?" + DEFAULT_EMAIL));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	public static URL getConceptPropertiesURL(int ontologyVersionId, String conceptId){
		URL url = null;
		StringBuffer sb = new StringBuffer();
		sb.append(DEFAULT_BASE_URL);
		sb.append(SUFFIX_CONCEPTS);
		sb.append(ontologyVersionId);
		sb.append("?");
		sb.append(PROPERTY_CONCEPT_ID);
		try {
			sb.append(encodeURI(conceptId));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		sb.append("&");
		sb.append(DEFAULT_EMAIL);
		try {
			url = new URL(getUrlWithDefaultSuffix(sb.toString()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	public static URL getConceptPropertiesVirtualURL(int ontologyVirtualId, String conceptId){
		URL url = null;
		StringBuffer sb = new StringBuffer();
		sb.append(DEFAULT_BASE_URL);
		sb.append(SUFFIX_ONTOLOGY_VIRTUAL);
		sb.append(ontologyVirtualId);
		sb.append("?");
		sb.append(PROPERTY_CONCEPT_ID);
		try {
			sb.append(encodeURI(conceptId));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			url = new URL(getUrlWithDefaultSuffix(sb.toString()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	private static String getSearchTermString(String searchTerm, List<String> ontologyIds, boolean isExactMatch, boolean includeProperties){
		StringBuffer sb = new StringBuffer("?");
		sb.append("q=").append(searchTerm).append("&");
		if(!ontologyIds.isEmpty()){
			sb.append(SEARCH_PROPERTY_ONTOLOGY_IDS);
			for(String ontologyId : ontologyIds){
				sb.append(ontologyId);
				sb.append(",");
			}
			sb.append("&");
		}
		sb.append(SEARCH_PROPERTY_EXACT_MATCH);
		sb.append(isExactMatch ? "=1" : "=0");
		sb.append("&");
		sb.append(SEARCH_PROPERTY_INCLUDE_PROPERTIES);
		sb.append(includeProperties ? "=1" : "=0");
		return sb.toString();
	}
	
	private static URL getSearchURL(String searchTerm, List<String> ontologyIds, boolean isExactMatch, boolean includeProperties){
		URL url = null;
		try {
			url = new URL(getUrlWithDefaultSuffix(getSearchTermString(searchTerm, ontologyIds, isExactMatch, includeProperties)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	private static URL getSearchClassesURL(String searchTerm, List<String> ontologyIds, boolean isExactMatch, boolean includeProperties){
		URL url = null;
		StringBuffer sb = new StringBuffer(getSearchTermString(searchTerm, ontologyIds, isExactMatch, includeProperties));
		sb.append("&");
		sb.append(SEARCH_PROPERTY_OBJECT_TYPES);
		sb.append("=").append(SEARCH_PROPERTY_OBJECT_TYPE_CLASS);
		try {
			url = new URL(getUrlWithDefaultSuffix(sb.toString()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	private static URL getSearchPropertiesURL(String searchTerm, List<String> ontologyIds, boolean isExactMatch, boolean includeProperties){
		URL url = null;
		StringBuffer sb = new StringBuffer(getSearchTermString(searchTerm, ontologyIds, isExactMatch, includeProperties));
		sb.append("&");
		sb.append(SEARCH_PROPERTY_OBJECT_TYPES);
		sb.append("=").append(SEARCH_PROPERTY_OBJECT_TYPE_PROPERTY);
		try {
			url = new URL(getUrlWithDefaultSuffix(sb.toString()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}

	private static JsonNode jsonToNode(String json) {
		JsonNode root = null;
		try {
			root = mapper.readTree(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return root;
	}

	/**
	 * @return all categories
	 */
	public static List<Category> getCategories(){
		String link = serviceLinks.get("categories");

		// Get the categories from the link we found
		JsonNode rootNode = jsonToNode(get(link));

		List<Category> result = new ArrayList<>();
		for (JsonNode node : rootNode) {
			try {
				result.add(mapper.readValue(node.toString(), Category.class));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * @return all groups
	 */
	public static List<Group> getGroups(){
		String link = serviceLinks.get("groups");

		// Get the groups from the link we found
		JsonNode rootNode = jsonToNode(get(link));

		List<Group> result = new ArrayList<>();
		for (JsonNode node : rootNode) {
			try {
				result.add(mapper.readValue(node.toString(), Group.class));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * @return all ontologies
	 */
	public static List<Ontology> getOntologies(){
		String link = serviceLinks.get("ontologies");

		// Get the ontologies from the link we found
		JsonNode rootNode = jsonToNode(get(link));

		List<Ontology> result = new ArrayList<>();
		for (JsonNode node : rootNode) {
			try {
				result.add(mapper.readValue(node.toString(), Ontology.class));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public static JsonNode getOntologyMetrics(String ontologyAcronym){
		String link = serviceLinks.get("ontologies") + "/" + ontologyAcronym + "/metrics";

		// Get the metrics from the link we found
		JsonNode metrics = jsonToNode(get(link));

		for (Iterator<Map.Entry<String, JsonNode>> entries = metrics.fields(); entries.hasNext();) {
			Map.Entry<String, JsonNode> entry = entries.next();
			System.out.println(entry.getKey() + ":" + entry.getValue().asText());
		}

		return metrics;
	}

	public static Page getSearchResult(String searchTerm, List<String> ontologyIds, boolean isExactMatch, boolean includeProperties) {
		String link = serviceLinks.get("search");

		// Get the groups from the link we found
		JsonNode rootNode = jsonToNode(get(link + getSearchTermString(searchTerm, ontologyIds, isExactMatch, includeProperties)));
		Page rootPage = null;
		try {
			rootPage = mapper.readValue(rootNode.toString(), Page.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return rootPage;
	}
	
	public static de.leipzig.imise.bioportal.bean.ontology.OntologyBean getOntologyProperties(int ontologyId){
		BioportalOntology c = new BioportalOntology();
		return c.getOntologyProperties(getOntologyPropertiesURL(ontologyId));
	}
	
	public static ClassBean getConceptProperties(int ontologyVersionId, String conceptId) throws PrivateOntologyException{
		BioportalConcept c = new BioportalConcept();
		return c.getConceptProperties(getConceptPropertiesURL(ontologyVersionId, conceptId));
	}
	
	public static ClassBean getConceptPropertiesVirtual(int ontologyVirtualId, String conceptId) throws PrivateOntologyException{
		BioportalConcept c = new BioportalConcept();
		return c.getConceptProperties(getConceptPropertiesVirtualURL(ontologyVirtualId, conceptId));
	}

	public static Collection<Entity> getChildren(Entity entity) {
		Set<Entity> entities = new HashSet<>();

		ArrayNode rootNode = (ArrayNode) jsonToNode(get(entity.getEntityLinks().getTree()));

		Iterator<JsonNode> iterator = rootNode.iterator();
		while (iterator.hasNext()) {
			try {
				entities.add(mapper.readValue(iterator.next().toString(), Entity.class));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return entities;
	}

	private static String encodeURI(String text) throws UnsupportedEncodingException {
		return URLEncoder.encode(text, "UTF-8").toString().replaceAll("\\+", "%20");
	}

	public static String getDefaultRestSuffix() {
	        return BP_PRODUCTION_PROTEGE_API_KEY;
	}
	
	public static String getUrlWithDefaultSuffix(String url) {
	        if (!url.contains("?")) {
	            url = url + "?";
	        } else {
	            url = url + "&";
	        }
	        return url + getDefaultRestSuffix();
	}
	
	public static void main(String[] args) throws Exception{
		List<Ontology> ontologies = BioportalRESTService.getOntologies();
		List<Group> groups = BioportalRESTService.getGroups();
		List<Category> categories = BioportalRESTService.getCategories();
		Page page = BioportalRESTService.getSearchResult("heart", Collections.<String>emptyList(), false, false);
		List<Entity> entities = page.getEntities();
		for (Entity entity : entities) {
			System.out.println(entity.getId());
		}
//		System.out.println(BioportalRESTServices.getConceptPropertiesVirtual(1516, "O80-O84.9").getRelations().get(ClassBean.SUB_CLASS_PROPERTY));
	}

}
