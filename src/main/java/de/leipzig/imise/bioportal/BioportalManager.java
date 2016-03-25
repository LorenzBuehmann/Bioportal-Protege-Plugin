package de.leipzig.imise.bioportal;

import de.leipzig.imise.bioportal.bean.ontologies.OntologyBean;
import de.leipzig.imise.bioportal.cache.SearchCache;
import de.leipzig.imise.bioportal.rest.BioportalRESTService;
import de.leipzig.imise.bioportal.rest.Ontology;
import de.leipzig.imise.bioportal.rest.Page;
import org.ncbo.stanford.bean.search.SearchBean;

import java.util.*;

public class BioportalManager {
	
	public static BioportalManager instance = null;
	
	private Set<Ontology> selectedOntologies = new HashSet<>();
	private Map<Ontology, Boolean> ontologies = new HashMap<>();
	private SearchCache searchCache;

	private BioportalManager(){


		searchCache = new SearchCache();
	}
	
	public static synchronized BioportalManager getInstance(){
		if(instance == null){
			instance = new BioportalManager();
		}
		return instance;
	}
	
	public void setOntologySelected(Ontology ontology, boolean b) {
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
	
	public Set<Ontology> getOntologies(){
		Set<Ontology> o = ontologies.keySet();
		if(o.isEmpty()){
			for(Ontology ontology : BioportalRESTService.getOntologies()){
				ontologies.put(ontology, Boolean.valueOf(false));
			}
		}
		return Collections.unmodifiableSet(ontologies.keySet());
	}
	
	public Set<Ontology> getSelectedOntologies() {
        return Collections.unmodifiableSet(selectedOntologies);
    }
	
	public List<SearchBean> getSearchResults(String searchTerm, List<Ontology> ontologies, boolean isExactMatch, boolean includeProperties){
		List<String> ontIds = new ArrayList<>();
		for(Ontology ontology : ontologies){
			ontIds.add(ontology.getAcronym());
		}
		return searchCache.getSearchResults(searchTerm, ontIds, isExactMatch, includeProperties);
//		return searchCache.getSearchResults(searchTerm, ontologyIds, isExactMatch, includeProperties);
	}
	
	public Page getSearchClassesResults(String searchTerm, List<Ontology> ontologies, boolean isExactMatch,
										boolean includeProperties){
		List<String> ontIds = new ArrayList<>();
		for(Ontology ontology : ontologies){
			ontIds.add(ontology.getAcronym());
		}
		return BioportalRESTService.getSearchResult(searchTerm, ontIds, isExactMatch, includeProperties);
	}
	
	public List<SearchBean> getSearchPropertiesResults(String searchTerm, List<Ontology> ontologies, boolean isExactMatch, boolean includeProperties){
		List<String> ontIds = new ArrayList<>();
		for(Ontology ontology : ontologies){
			ontIds.add(ontology.getAcronym());
		}
		return searchCache.getSearchPropertiesResults(searchTerm, ontIds, isExactMatch, includeProperties);
	}
}
