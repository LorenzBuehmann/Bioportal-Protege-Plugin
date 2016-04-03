package de.leipzig.imise.bioportal.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import de.leipzig.imise.bioportal.BioportalPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class BioportalRESTService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BioportalRESTService.class);

	public static final String SEARCH_PROPERTY_ONTOLOGY_IDS = "ontologies=";
	public static final String SEARCH_PROPERTY_EXACT_MATCH = "isexactmatch";
	public static final String SEARCH_PROPERTY_INCLUDE_PROPERTIES = "includeproperties";

	public final static String API_KEY_PARAM = "apikey";	
	public static final String BP_PRODUCTION_PROTEGE_API_KEY = API_KEY_PARAM + "=8fadfa2c-47de-4487-a1f5-b7af7378d693";

	public static final Set<String> META_PROPERTIES = Sets.newHashSet("links", "@context", "hasChildren", "children");
	public static final String META_PROPERTY_NS = "http://data.bioontology.org/metadata/";



	static final ObjectMapper mapper = new ObjectMapper();

	private static final Map<String, String> serviceLinks = new HashMap<>();

	private static boolean useCache = true;
	private static File cacheDir;

	static {
		init();
	}

	public static void init() {
		// setup cache
		String property = "java.io.tmpdir";
		String tempDir = System.getProperty(property);
		cacheDir = new File(tempDir, "bioportal-cache");
		cacheDir.mkdirs();

		LOGGER.info("Initializing BioPortal REST services...");
		// Get the available resources
		String resourcesString = get(BioportalPreferences.getInstance().getRestBaseURL() + "/");
		JsonNode resources = jsonToNode(resourcesString);

		// Follow the link by looking for available services in the list of links
		JsonNode links = resources.get("links");
		for(Iterator<Map.Entry<String, JsonNode>> it = links.fields(); it.hasNext();) {
			Map.Entry<String, JsonNode> entry = it.next();
			serviceLinks.put(entry.getKey(), entry.getValue().asText());
		}
	}

	/**
	 * Adds API key param to url.
	 * @param url the URL
	 * @return the enriched URL
	 */
	public static String asBioportalLink(String url) {
		return url + "?apikey=" + BioportalPreferences.getInstance().getRestAPIKey();
	}

	private static String get(String urlToGet) {
		LOGGER.info("BioPortal request:" + urlToGet);
		String result = "";

		HashFunction hf = Hashing.md5();
		HashCode hc = hf.newHasher()
				.putString(urlToGet, Charsets.UTF_8)
				.hash();

		File cacheFile = new File(cacheDir, hc.toString() + ".cache");

		if (useCache) {
			if (cacheFile.exists()) {
				try {
					LOGGER.info("loading from cache directory...");
					result = Joiner.on("\n").join(Files.readLines(cacheFile, Charsets.UTF_8));
					return result;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		InputStream is;
		URL url;
		HttpURLConnection conn;
		try {
			url = new URL(urlToGet);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "apikey token=" + BioportalPreferences.getInstance().getRestAPIKey());
			conn.setRequestProperty("Accept", "application/json");
			is = conn.getInputStream();
			try (BufferedReader rd = new BufferedReader(new InputStreamReader(is))) {
				String line;
				while ((line = rd.readLine()) != null) {
					result += line;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// write to disk
		if(useCache) {
			try {
				Files.write(result, cacheFile, Charsets.UTF_8);
			} catch (IOException e) {
				LOGGER.error("caching failed", e);
			}
		}

		return result;
	}

	private static String getSearchTermString(String searchTerm, List<String> ontologyIds, boolean isExactMatch, boolean includeProperties){
		StringBuilder sb = new StringBuilder("?");

		sb.append("q=").append(searchTerm);

		if(!ontologyIds.isEmpty()){
			sb.append("&").append(SEARCH_PROPERTY_ONTOLOGY_IDS);
			sb.append(Joiner.on(",").join(ontologyIds));

		}

		sb.append("&").append(SEARCH_PROPERTY_EXACT_MATCH);
		sb.append(isExactMatch ? "=1" : "=0");

		sb.append("&").append(SEARCH_PROPERTY_INCLUDE_PROPERTIES);
		sb.append(includeProperties ? "=1" : "=0");
		return sb.toString();
	}
	
	private static JsonNode jsonToNode(String json) {
		JsonNode root = null;
		try {
			root = mapper.readTree(json);
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
		String link = serviceLinks.get("ontologies") + "?include=name,acronym,group,hasDomain";

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

	public static Ontology getOntology(Entity entity) {
		JsonNode node = jsonToNode(get(entity.getEntityLinks().getOntology()));

		try {
			Ontology ontology = mapper.readValue(node.toString(), Ontology.class);
			return ontology;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static OntologySubmission getOntologySubmission(Ontology ontology) {
		JsonNode node = jsonToNode(get(ontology.getLinks().getLatestSubmission()));

		try {
			OntologySubmission ontologySubmission = mapper.readValue(node.toString(), OntologySubmission.class);
			return ontologySubmission;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Entity getEntityDetails(Entity entity) {
		JsonNode node = jsonToNode(get(entity.getEntityLinks().getSelf() + "?include=all"));//hasChildren,prefLabel,synonym,definition"));

		try {
			Entity detailedEntity = mapper.readValue(node.toString(), Entity.class);
			return detailedEntity;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static Collection<Entity> getChildren(Entity entity) {
		Set<Entity> entities = new HashSet<>();

		ArrayNode rootNode = (ArrayNode) jsonToNode(get(entity.getEntityLinks().getChildren() + "?include=hasChildren,prefLabel,synonym,definition")).get("collection");

		for (JsonNode aRootNode : rootNode) {
			try {
				Entity child = mapper.readValue(aRootNode.toString(), Entity.class);
				entities.add(child);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return entities;
	}

	public static DefaultMutableTreeNode getTree(Entity entity) {
		DefaultMutableTreeNode tree = new DefaultMutableTreeNode();
		tree.setUserObject(Entity.TOP_ENTITY);

		ArrayNode rootNode = (ArrayNode) jsonToNode(get(entity.getEntityLinks().getTree()));

		createTree(tree, rootNode);

		return tree;
	}

	private static void createTree(DefaultMutableTreeNode tree, ArrayNode childrenNode) {
		for (JsonNode aChildrenNode : childrenNode) {
			try {
				JsonNode childNode = aChildrenNode;
				Entity child = mapper.readValue(childNode.toString(), Entity.class);
				DefaultMutableTreeNode subTree = new DefaultMutableTreeNode();
				subTree.setUserObject(child);
				tree.add(subTree);

				if (childNode.has("children")) {
					createTree(subTree, (ArrayNode) childNode.get("children"));
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	public static Collection<Entity> getRoots(Entity entity) {
		Set<Entity> entities = new HashSet<>();

		ArrayNode rootNode = (ArrayNode) jsonToNode(get(entity.getEntityLinks().getTree()));

		for (JsonNode aRootNode : rootNode) {
			try {
				Entity child = mapper.readValue(aRootNode.toString(), Entity.class);
				entities.add(child);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return entities;
	}

	public static Collection<List<Entity>> getPathsToRoot(Entity entity) {
		List<List<Entity>> paths = new ArrayList<>();

		// [p1[n1_1, ..., entity], p2[], ...]
		ArrayNode pathsNode = (ArrayNode) jsonToNode(get(entity.getEntityLinks().getSelf() + "/paths_to_root"));

		for (JsonNode aPathsNode : pathsNode) {
			ArrayNode pathNode = (ArrayNode) aPathsNode;
			List<Entity> path = new ArrayList<>();
			for (JsonNode aPathNode : pathNode) {
				try {
					Entity child = mapper.readValue(aPathNode.toString(), Entity.class);
					path.add(child);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			paths.add(path);
		}

		return paths;
	}

	private static String encodeURI(String text) throws UnsupportedEncodingException {
		return URLEncoder.encode(text, "UTF-8").replaceAll("\\+", "%20");
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
		Entity e = new Entity();
		e.setId("http://purl.bioontology.org/ontology/SNOMEDCT/235351004");
		EntityLinks el = new EntityLinks();
		el.setTree("http://data.bioontology.org/ontologies/SNOMEDCT/classes/http%3A%2F%2Fpurl.bioontology.org%2Fontology%2FSNOMEDCT%2F235351004/tree");
		e.setEntityLinks(el);

		MutableTreeNode tree = BioportalRESTService.getTree(e);
		System.out.println(tree);

		List<Ontology> ontologies = BioportalRESTService.getOntologies();
		List<Group> groups = BioportalRESTService.getGroups();
		List<Category> categories = BioportalRESTService.getCategories();
		Page page = BioportalRESTService.getSearchResult("heart", Collections.emptyList(), false, false);
		List<Entity> entities = page.getEntities();
		for (Entity entity : entities) {
			System.out.println(entity.getId());
		}
//		System.out.println(BioportalRESTServices.getConceptPropertiesVirtual(1516, "O80-O84.9").getRelations().get(ClassBean.SUB_CLASS_PROPERTY));
	}

}
