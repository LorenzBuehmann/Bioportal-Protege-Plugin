package de.leipzig.imise.bioportal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.leipzig.imise.bioportal.bean.category.CategoryBean;
import de.leipzig.imise.bioportal.bean.concept.ClassBean;
import de.leipzig.imise.bioportal.bean.group.GroupBean;
import de.leipzig.imise.bioportal.bean.ontologies.OntologyBean;
import de.leipzig.imise.bioportal.util.*;
import org.ncbo.stanford.bean.search.Page;
import org.ncbo.stanford.bean.search.SearchBean;
import org.ncbo.stanford.bean.search.SearchResultListBean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

public class BioportalRESTServices {
	public static final String DEFAULT_BASE_URL = "http://data.bioontology.org/";
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

	static final ObjectMapper mapper = new ObjectMapper();
	
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
	
	private static String getSearchTermString(String searchTerm, List<Integer> ontologyIds, boolean isExactMatch, boolean includeProperties){
		StringBuffer sb = new StringBuffer();
		sb.append(DEFAULT_BASE_URL);
		sb.append(SUFFIX_SEARCH);
		try {
			sb.append(encodeURI(searchTerm));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		sb.append("?");
		if(!ontologyIds.isEmpty()){
			sb.append(SEARCH_PROPERTY_ONTOLOGY_IDS);
			for(Integer ontologyId : ontologyIds){
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
		sb.append("&");
		sb.append(DEFAULT_EMAIL);
		return sb.toString();
	}
	
	private static URL getSearchURL(String searchTerm, List<Integer> ontologyIds, boolean isExactMatch, boolean includeProperties){
		URL url = null;
		try {
			url = new URL(getUrlWithDefaultSuffix(getSearchTermString(searchTerm, ontologyIds, isExactMatch, includeProperties)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	private static URL getSearchClassesURL(String searchTerm, List<Integer> ontologyIds, boolean isExactMatch, boolean includeProperties){
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
	
	private static URL getSearchPropertiesURL(String searchTerm, List<Integer> ontologyIds, boolean isExactMatch, boolean includeProperties){
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
	
	public static List<CategoryBean> getCategories(){
		BioportalCategories c = new BioportalCategories();
		return c.getCategories(getBioportalCategoriesURL());
	}
	
	public static List<GroupBean> getGroups(){
		BioportalGroups c = new BioportalGroups();
		return c.getGroups(getBioportalGroupsURL());
	}
	
	public static List<OntologyBean> getOntologies(){
		BioportalOntologies c = new BioportalOntologies();
		return c.getOntologies(getOntologiesURL());
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
	
	public static List<SearchBean> getSearchResults(String searchTerm, List<Integer> ontologyIds, boolean isExactMatch, boolean includeProperties){
		try {
			BioportalSearch c = new BioportalSearch();
			Page page = c.getSearchResults(getSearchURL(searchTerm, ontologyIds, isExactMatch, includeProperties));
			if(page == null){
				return Collections.emptyList();
			}
			SearchResultListBean data = page.getContents();
			if(data == null){
				return Collections.emptyList();
			}
			return data.getSearchResultList();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<SearchBean> getSearchClassesResults(String searchTerm, List<Integer> ontologyIds, boolean isExactMatch, boolean includeProperties){
		try {
			BioportalSearch c = new BioportalSearch();
			Page page = c.getSearchResults(getSearchClassesURL(searchTerm, ontologyIds, isExactMatch, includeProperties));
			if(page == null){
				return Collections.emptyList();
			}
			SearchResultListBean data = page.getContents();
			if(data == null){
				return Collections.emptyList();
			}
			return data.getSearchResultList();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<SearchBean> getSearchPropertiesResults(String searchTerm, List<Integer> ontologyIds, boolean isExactMatch, boolean includeProperties){
		try {
			BioportalSearch c = new BioportalSearch();
			Page page = c.getSearchResults(getSearchPropertiesURL(searchTerm, ontologyIds, isExactMatch, includeProperties));
			if(page == null){
				return Collections.emptyList();
			}
			SearchResultListBean data = page.getContents();
			if(data == null){
				return Collections.emptyList();
			}
			return data.getSearchResultList();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
	        }System.out.println(url + getDefaultRestSuffix());
	        return url + getDefaultRestSuffix();
	}
	

}
