package de.leipzig.imise.bioportal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ncbo.stanford.bean.search.SearchBean;

import de.leipzig.imise.bioportal.bean.ontologies.OntologyBean;
import de.leipzig.imise.bioportal.cache.SearchCache;

public class BioportalManager {
	
	public static BioportalManager instance = null;
	
	private Set<OntologyBean> selectedOntologies;
	private Map<OntologyBean, Boolean> ontologies;
	private SearchCache searchCache;

	private BioportalManager(){
		selectedOntologies = new HashSet<OntologyBean>();
		ontologies = new HashMap<OntologyBean, Boolean>();
		searchCache = new SearchCache();
	}
	
	public static synchronized BioportalManager getInstance(){
		if(instance == null){
			instance = new BioportalManager();
		}
		return instance;
	}
	
	public void setOntologySelected(OntologyBean ontology, boolean b) {
        if(b) {
            if(!selectedOntologies.contains(ontology)) {
            	selectedOntologies.add(ontology);
            }
        }
        else {
            if(selectedOntologies.contains(ontology)) {
            	selectedOntologies.remove(ontology);
            }
        }
        ontologies.put(ontology, b);
    }
	
	public boolean isSelectedOntology(OntologyBean ontologyBean){
		ontologies.get(ontologyBean).booleanValue();
		return selectedOntologies.contains(ontologyBean);
	}
	
	public Set<OntologyBean> getOntologies(){
		Set<OntologyBean> o = ontologies.keySet();
		if(o.isEmpty()){
			for(OntologyBean bean : BioportalRESTServices.getOntologies()){
				ontologies.put(bean, Boolean.valueOf(false));
			}
		}
		return Collections.unmodifiableSet(ontologies.keySet());
	}
	
	public Set<OntologyBean> getSelectedOntologies() {
        return Collections.unmodifiableSet(selectedOntologies);
    }
	
	public List<SearchBean> getSearchResults(String searchTerm, List<Integer> ontologyIds, boolean isExactMatch, boolean includeProperties){
		List<Integer> ontIds = new ArrayList<Integer>();
		for(OntologyBean bean : selectedOntologies){
			ontIds.add(bean.getOntologyId());
		}System.out.println(ontIds);
		return searchCache.getSearchResults(searchTerm, ontIds, isExactMatch, includeProperties);
//		return searchCache.getSearchResults(searchTerm, ontologyIds, isExactMatch, includeProperties);
	}
	
	public List<SearchBean> getSearchClassesResults(String searchTerm, List<Integer> ontologyIds, boolean isExactMatch, boolean includeProperties){
		List<Integer> ontIds = new ArrayList<Integer>();
		for(OntologyBean bean : selectedOntologies){
			ontIds.add(bean.getOntologyId());
		}
		return searchCache.getSearchClassesResults(searchTerm, ontIds, isExactMatch, includeProperties);
	}
	
	public List<SearchBean> getSearchPropertiesResults(String searchTerm, List<Integer> ontologyIds, boolean isExactMatch, boolean includeProperties){
		List<Integer> ontIds = new ArrayList<Integer>();
		for(OntologyBean bean : selectedOntologies){
			ontIds.add(bean.getOntologyId());
		}
		return searchCache.getSearchPropertiesResults(searchTerm, ontIds, isExactMatch, includeProperties);
	}
}
