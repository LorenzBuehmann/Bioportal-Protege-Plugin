package de.leipzig.imise.bioportal;

import java.util.List;

public class SearchRequest {
	
	private String searchTerm;
	private List<String> ontologyIds;
	private boolean isExactMatch;
	private boolean includeProperties;
	
	public SearchRequest(String searchTerm, List<String> ontologyIds, boolean isExactMatch, boolean includeProperties){
		this.searchTerm = searchTerm;
		this.ontologyIds = ontologyIds;
		this.isExactMatch = isExactMatch;
		this.includeProperties = includeProperties;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public List<String> getOntologyIds() {
		return ontologyIds;
	}

	public boolean isExactMatch() {
		return isExactMatch;
	}

	public boolean isIncludeProperties() {
		return includeProperties;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this){
			return true;
		}
		if(!(obj instanceof SearchRequest)){
			return false;
		}
		SearchRequest r = (SearchRequest)obj;
		return r.getSearchTerm().equals(searchTerm) && 
			r.getOntologyIds().equals(ontologyIds) && 
			r.isExactMatch == isExactMatch &&
			r.includeProperties == includeProperties;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + searchTerm.hashCode();
		result = 37 * result + ontologyIds.hashCode();
		result = 37 * result + (isExactMatch ? 0 : 1);
		result = 37 * result + (includeProperties ? 0 : 1);
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SEARCH REQUEST:\n");
		sb.append("Search term: ");
		sb.append(searchTerm);
		sb.append("\n");
		sb.append("Ontology IDs: ");
		sb.append(ontologyIds);
		sb.append("\n");
		sb.append("Exact match: ");
		sb.append(isExactMatch);
		sb.append("\n");
		sb.append("Include properties: ");
		sb.append(includeProperties);
		sb.append("\n");
		return sb.toString();
	}

}
