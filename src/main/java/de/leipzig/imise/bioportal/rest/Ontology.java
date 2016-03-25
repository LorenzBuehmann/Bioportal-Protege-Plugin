
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
    "administeredBy",
    "acronym",
    "name",
    "summaryOnly",
    "ontologyType",
    "@id",
    "@type",
    "links"
})
public class Ontology {

    @JsonProperty("administeredBy")
    private List<String> administeredBy = new ArrayList<String>();
    @JsonProperty("acronym")
    private String acronym;
    @JsonProperty("name")
    private String name;
    @JsonProperty("summaryOnly")
    private Boolean summaryOnly;
    @JsonProperty("ontologyType")
    private String ontologyType;
    @JsonProperty("@id")
    private String Id;
    @JsonProperty("@type")
    private String Type;
    @JsonProperty("links")
    private OntologyLinks links;
    @JsonProperty("group")
    private List<String> groups = new ArrayList<String>();
    @JsonProperty("hasDomain")
    private List<String> categories = new ArrayList<String>();

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The administeredBy
     */
    @JsonProperty("administeredBy")
    public List<String> getAdministeredBy() {
        return administeredBy;
    }

    /**
     * 
     * @param administeredBy
     *     The administeredBy
     */
    @JsonProperty("administeredBy")
    public void setAdministeredBy(List<String> administeredBy) {
        this.administeredBy = administeredBy;
    }

    /**
     * 
     * @return
     *     The acronym
     */
    @JsonProperty("acronym")
    public String getAcronym() {
        return acronym;
    }

    /**
     * 
     * @param acronym
     *     The acronym
     */
    @JsonProperty("acronym")
    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    /**
     * 
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The summaryOnly
     */
    @JsonProperty("summaryOnly")
    public Boolean getSummaryOnly() {
        return summaryOnly;
    }

    /**
     * 
     * @param summaryOnly
     *     The summaryOnly
     */
    @JsonProperty("summaryOnly")
    public void setSummaryOnly(Boolean summaryOnly) {
        this.summaryOnly = summaryOnly;
    }

    /**
     * 
     * @return
     *     The ontologyType
     */
    @JsonProperty("ontologyType")
    public String getOntologyType() {
        return ontologyType;
    }

    /**
     * 
     * @param ontologyType
     *     The ontologyType
     */
    @JsonProperty("ontologyType")
    public void setOntologyType(String ontologyType) {
        this.ontologyType = ontologyType;
    }

    /**
     * 
     * @return
     *     The Id
     */
    @JsonProperty("@id")
    public String getId() {
        return Id;
    }

    /**
     * 
     * @param Id
     *     The @id
     */
    @JsonProperty("@id")
    public void setId(String Id) {
        this.Id = Id;
    }

    /**
     * 
     * @return
     *     The Type
     */
    @JsonProperty("@type")
    public String getType() {
        return Type;
    }

    /**
     * 
     * @param Type
     *     The @type
     */
    @JsonProperty("@type")
    public void setType(String Type) {
        this.Type = Type;
    }

    /**
     * 
     * @return
     *     The links
     */
    @JsonProperty("links")
    public OntologyLinks getLinks() {
        return links;
    }

    /**
     * 
     * @param links
     *     The links
     */
    @JsonProperty("links")
    public void setLinks(OntologyLinks links) {
        this.links = links;
    }

    /**
     *
     * @return
     *     The group
     */
    @JsonProperty("group")
    public List<String> getGroups() {
        return groups;
    }

    /**
     *
     * @param group
     *     The group
     */
    @JsonProperty("group")
    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    /**
     *
     * @return
     *     The hasDomain
     */
    @JsonProperty("hasDomain")
    public List<String> getCategories() {
        return categories;
    }

    /**
     *
     * @param hasDomain
     *     The hasDomain
     */
    @JsonProperty("hasDomain")
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        if(!name.equals("@context"))
            this.additionalProperties.put(name, value);
    }

}
