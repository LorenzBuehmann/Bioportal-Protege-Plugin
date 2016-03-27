package de.leipzig.imise.bioportal.cache;

import de.leipzig.imise.bioportal.SearchRequest;
import de.leipzig.imise.bioportal.rest.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchCache {
	
	private Map<SearchRequest, List<Entity>> searchCache = new HashMap<>();
	
	public List<Entity> getSearchResults(String searchTerm, List<String> ontologyIds, boolean isExactMatch, boolean includeProperties){
		SearchRequest request = new SearchRequest(searchTerm, ontologyIds, isExactMatch, includeProperties);
		List<Entity> result = searchCache.get(request);
		if(result == null){

			for(SearchRequest r : searchCache.keySet()){
				if(r.getSearchTerm().equals(searchTerm)){
					if(r.getOntologyIds().contains(request.getOntologyIds())){
						result = new ArrayList<Entity>();
						for(Entity bean : searchCache.get(r)){
							if(request.getOntologyIds().contains(bean.getEntityLinks().getOntology())){
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
}
