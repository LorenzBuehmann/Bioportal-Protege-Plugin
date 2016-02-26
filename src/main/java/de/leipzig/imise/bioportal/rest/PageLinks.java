package de.leipzig.imise.bioportal.rest;

import java.util.HashMap;
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
"nextPage",
"prevPage"
})
public class PageLinks {

@JsonProperty("nextPage")
private String nextPage;
@JsonProperty("prevPage")
private Object prevPage;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
* 
* @return
* The nextPage
*/
@JsonProperty("nextPage")
public String getNextPage() {
return nextPage;
}

/**
* 
* @param nextPage
* The nextPage
*/
@JsonProperty("nextPage")
public void setNextPage(String nextPage) {
this.nextPage = nextPage;
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

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}