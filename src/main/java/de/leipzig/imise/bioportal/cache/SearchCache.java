package de.leipzig.imise.bioportal.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ncbo.stanford.bean.search.SearchBean;

import de.leipzig.imise.bioportal.BioportalRESTServices;
import de.leipzig.imise.bioportal.SearchRequest;

public class SearchCache {
	
	private Map<SearchRequest, List<SearchBean>> searchCache = new HashMap<SearchRequest, List<SearchBean>>();
	
	public List<SearchBean> getSearchResults(String searchTerm, List<String> ontologyIds, boolean isExactMatch, boolean includeProperties){
		SearchRequest request = new SearchRequest(searchTerm, ontologyIds, isExactMatch, includeProperties);
		List<SearchBean> result = searchCache.get(request);
		if(result == null){

			for(SearchRequest r : searchCache.keySet()){
				if(r.getSearchTerm().equals(searchTerm)){
					if(r.getOntologyIds().contains(request.getOntologyIds())){
						result = new ArrayList<SearchBean>();
						for(SearchBean bean : searchCache.get(r)){
							if(request.getOntologyIds().contains(bean.getOntologyId())){
								result.add(bean);
							}
						}
						return result;
					}
				}
			}
			
			
//			result = BioportalRESTServices.getSearchResults(searchTerm, ontologyIds, isExactMatch, includeProperties);
			searchCache.put(request, result);
		}
		return result;
	}
	
	public List<SearchBean> getSearchClassesResults(String searchTerm, List<String> ontologyIds, boolean isExactMatch, boolean includeProperties){
		SearchRequest request = new SearchRequest(searchTerm, ontologyIds, isExactMatch, includeProperties);
		List<SearchBean> result = searchCache.get(request);
		if(result == null){
			
			for(SearchRequest r : searchCache.keySet()){
				if(r.getSearchTerm().equals(searchTerm)){
					if(r.getOntologyIds().contains(request.getOntologyIds())){
						result = new ArrayList<SearchBean>();
						for(SearchBean bean : searchCache.get(r)){
							if(request.getOntologyIds().contains(bean.getOntologyId())){
								result.add(bean);
							}
						}
						return result;
					}
				}
			}
			
			
//			result = BioportalRESTServices.getSearchClassesResults(searchTerm, ontologyIds, isExactMatch, includeProperties);
			searchCache.put(request, result);
		}
		return result;
	}
	
	public List<SearchBean> getSearchPropertiesResults(String searchTerm, List<String> ontologyIds, boolean isExactMatch, boolean includeProperties){
		SearchRequest request = new SearchRequest(searchTerm, ontologyIds, isExactMatch, includeProperties);
		List<SearchBean> result = searchCache.get(request);
		if(result == null){
			for(SearchRequest r : searchCache.keySet()){
				if(r.getSearchTerm().equals(searchTerm)){
					if(r.getOntologyIds().contains(request.getOntologyIds())){
						result = new ArrayList<SearchBean>();
						for(SearchBean bean : searchCache.get(r)){
							if(request.getOntologyIds().contains(bean.getOntologyId())){
								result.add(bean);
							}
						}
						return result;
					}
				}
			}
			
//			result = BioportalRESTServices.getSearchPropertiesResults(searchTerm, ontologyIds, isExactMatch, includeProperties);
			searchCache.put(request, result);
		}
		return result;
	}

}
