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
"self",
"ontology",
"children",
"parents",
"descendants",
"ancestors",
"instances",
"tree",
"notes",
"mappings",
"ui"
})
public class EntityLinks {

@JsonProperty("self")
private String self;
@JsonProperty("ontology")
private String ontology;
@JsonProperty("children")
private String children;
@JsonProperty("parents")
private String parents;
@JsonProperty("descendants")
private String descendants;
@JsonProperty("ancestors")
private String ancestors;
@JsonProperty("instances")
private String instances;
@JsonProperty("tree")
private String tree;
@JsonProperty("notes")
private String notes;
@JsonProperty("mappings")
private String mappings;
@JsonProperty("ui")
private String ui;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
* 
* @return
* The self
*/
@JsonProperty("self")
public String getSelf() {
return self;
}

/**
* 
* @param self
* The self
*/
@JsonProperty("self")
public void setSelf(String self) {
this.self = self;
}

/**
* 
* @return
* The ontology
*/
@JsonProperty("ontology")
public String getOntology() {
return ontology;
}

/**
* 
* @param ontology
* The ontology
*/
@JsonProperty("ontology")
public void setOntology(String ontology) {
this.ontology = ontology;
}

/**
* 
* @return
* The children
*/
@JsonProperty("children")
public String getChildren() {
return children;
}

/**
* 
* @param children
* The children
*/
@JsonProperty("children")
public void setChildren(String children) {
this.children = children;
}

/**
* 
* @return
* The parents
*/
@JsonProperty("parents")
public String getParents() {
return parents;
}

/**
* 
* @param parents
* The parents
*/
@JsonProperty("parents")
public void setParents(String parents) {
this.parents = parents;
}

/**
* 
* @return
* The descendants
*/
@JsonProperty("descendants")
public String getDescendants() {
return descendants;
}

/**
* 
* @param descendants
* The descendants
*/
@JsonProperty("descendants")
public void setDescendants(String descendants) {
this.descendants = descendants;
}

/**
* 
* @return
* The ancestors
*/
@JsonProperty("ancestors")
public String getAncestors() {
return ancestors;
}

/**
* 
* @param ancestors
* The ancestors
*/
@JsonProperty("ancestors")
public void setAncestors(String ancestors) {
this.ancestors = ancestors;
}

/**
* 
* @return
* The instances
*/
@JsonProperty("instances")
public String getInstances() {
return instances;
}

/**
* 
* @param instances
* The instances
*/
@JsonProperty("instances")
public void setInstances(String instances) {
this.instances = instances;
}

/**
* 
* @return
* The tree
*/
@JsonProperty("tree")
public String getTree() {
return tree;
}

/**
* 
* @param tree
* The tree
*/
@JsonProperty("tree")
public void setTree(String tree) {
this.tree = tree;
}

/**
* 
* @return
* The notes
*/
@JsonProperty("notes")
public String getNotes() {
return notes;
}

/**
* 
* @param notes
* The notes
*/
@JsonProperty("notes")
public void setNotes(String notes) {
this.notes = notes;
}

/**
* 
* @return
* The mappings
*/
@JsonProperty("mappings")
public String getMappings() {
return mappings;
}

/**
* 
* @param mappings
* The mappings
*/
@JsonProperty("mappings")
public void setMappings(String mappings) {
this.mappings = mappings;
}

/**
* 
* @return
* The ui
*/
@JsonProperty("ui")
public String getUi() {
return ui;
}

/**
* 
* @param ui
* The ui
*/
@JsonProperty("ui")
public void setUi(String ui) {
this.ui = ui;
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