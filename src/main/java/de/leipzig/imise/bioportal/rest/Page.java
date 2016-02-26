package de.leipzig.imise.bioportal.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
"page",
"pageCount",
"prevPage",
"nextPage",
"pageLinks",
"entity"
})
public class Page {

@JsonProperty("page")
private Integer page;
@JsonProperty("pageCount")
private Integer pageCount;
@JsonProperty("prevPage")
private Object prevPage;
@JsonProperty("nextPage")
private Integer nextPage;
@JsonProperty("pageLinks")
private PageLinks pageLinks;
@JsonProperty("collection")
private List<Entity> entity = new ArrayList<Entity>();
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
* 
* @return
* The page
*/
@JsonProperty("page")
public Integer getPage() {
return page;
}

/**
* 
* @param page
* The page
*/
@JsonProperty("page")
public void setPage(Integer page) {
this.page = page;
}

/**
* 
* @return
* The pageCount
*/
@JsonProperty("pageCount")
public Integer getPageCount() {
return pageCount;
}

/**
* 
* @param pageCount
* The pageCount
*/
@JsonProperty("pageCount")
public void setPageCount(Integer pageCount) {
this.pageCount = pageCount;
}

/**
* 
* @return
* The prevPage
*/
@JsonProperty("prevPage")
public Object getPrevPage() {
return prevPage;
}

/**
* 
* @param prevPage
* The prevPage
*/
@JsonProperty("prevPage")
public void setPrevPage(Object prevPage) {
this.prevPage = prevPage;
}

/**
* 
* @return
* The nextPage
*/
@JsonProperty("nextPage")
public Integer getNextPage() {
return nextPage;
}

/**
* 
* @param nextPage
* The nextPage
*/
@JsonProperty("nextPage")
public void setNextPage(Integer nextPage) {
this.nextPage = nextPage;
}

/**
* 
* @return
* The pageLinks
*/
@JsonProperty("pageLinks")
public PageLinks getPageLinks() {
return pageLinks;
}

/**
* 
* @param pageLinks
* The pageLinks
*/
@JsonProperty("pageLinks")
public void setPageLinks(PageLinks pageLinks) {
this.pageLinks = pageLinks;
}

/**
* 
* @return
* The entity
*/
@JsonProperty("collection")
public List<Entity> getEntity() {
return entity;
}

/**
* 
* @param entity
* The entity
*/
@JsonProperty("collection")
public void setEntity(List<Entity> entity) {
this.entity = entity;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}