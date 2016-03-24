
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
    "contact",
    "ontology",
    "hasOntologyLanguage",
    "released",
    "creationDate",
    "homepage",
    "publication",
    "documentation",
    "version",
    "description",
    "status",
    "submissionId",
    "@id",
    "@type",
    "links"
})
public class OntologySubmission {

    @JsonProperty("contact")
    private List<Contact> contact = new ArrayList<Contact>();
    @JsonProperty("ontology")
    private Ontology ontology;
    @JsonProperty("hasOntologyLanguage")
    private String hasOntologyLanguage;
    @JsonProperty("released")
    private String released;
    @JsonProperty("creationDate")
    private String creationDate;
    @JsonProperty("homepage")
    private String homepage;
    @JsonProperty("publication")
    private Object publication;
    @JsonProperty("documentation")
    private String documentation;
    @JsonProperty("version")
    private String version;
    @JsonProperty("description")
    private String description;
    @JsonProperty("status")
    private Object status;
    @JsonProperty("submissionId")
    private Integer submissionId;
    @JsonProperty("@id")
    private String Id;
    @JsonProperty("@type")
    private String Type;
    @JsonProperty("links")
    private OntologySubmissionLinks links;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The contact
     */
    @JsonProperty("contact")
    public List<Contact> getContact() {
        return contact;
    }

    /**
     * 
     * @param contact
     *     The contact
     */
    @JsonProperty("contact")
    public void setContact(List<Contact> contact) {
        this.contact = contact;
    }

    /**
     * 
     * @return
     *     The ontology
     */
    @JsonProperty("ontology")
    public Ontology getOntology() {
        return ontology;
    }

    /**
     * 
     * @param ontology
     *     The ontology
     */
    @JsonProperty("ontology")
    public void setOntology(Ontology ontology) {
        this.ontology = ontology;
    }

    /**
     * 
     * @return
     *     The hasOntologyLanguage
     */
    @JsonProperty("hasOntologyLanguage")
    public String getHasOntologyLanguage() {
        return hasOntologyLanguage;
    }

    /**
     * 
     * @param hasOntologyLanguage
     *     The hasOntologyLanguage
     */
    @JsonProperty("hasOntologyLanguage")
    public void setHasOntologyLanguage(String hasOntologyLanguage) {
        this.hasOntologyLanguage = hasOntologyLanguage;
    }

    /**
     * 
     * @return
     *     The released
     */
    @JsonProperty("released")
    public String getReleased() {
        return released;
    }

    /**
     * 
     * @param released
     *     The released
     */
    @JsonProperty("released")
    public void setReleased(String released) {
        this.released = released;
    }

    /**
     * 
     * @return
     *     The creationDate
     */
    @JsonProperty("creationDate")
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * 
     * @param creationDate
     *     The creationDate
     */
    @JsonProperty("creationDate")
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * 
     * @return
     *     The homepage
     */
    @JsonProperty("homepage")
    public String getHomepage() {
        return homepage;
    }

    /**
     * 
     * @param homepage
     *     The homepage
     */
    @JsonProperty("homepage")
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    /**
     * 
     * @return
     *     The publication
     */
    @JsonProperty("publication")
    public Object getPublication() {
        return publication;
    }

    /**
     * 
     * @param publication
     *     The publication
     */
    @JsonProperty("publication")
    public void setPublication(Object publication) {
        this.publication = publication;
    }

    /**
     * 
     * @return
     *     The documentation
     */
    @JsonProperty("documentation")
    public String getDocumentation() {
        return documentation;
    }

    /**
     * 
     * @param documentation
     *     The documentation
     */
    @JsonProperty("documentation")
    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    /**
     * 
     * @return
     *     The version
     */
    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    /**
     * 
     * @param version
     *     The version
     */
    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 
     * @return
     *     The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *     The description
     */
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     * @return
     *     The status
     */
    @JsonProperty("status")
    public Object getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    @JsonProperty("status")
    public void setStatus(Object status) {
        this.status = status;
    }

    /**
     * 
     * @return
     *     The submissionId
     */
    @JsonProperty("submissionId")
    public Integer getSubmissionId() {
        return submissionId;
    }

    /**
     * 
     * @param submissionId
     *     The submissionId
     */
    @JsonProperty("submissionId")
    public void setSubmissionId(Integer submissionId) {
        this.submissionId = submissionId;
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
    public OntologySubmissionLinks getLinks() {
        return links;
    }

    /**
     * 
     * @param links
     *     The links
     */
    @JsonProperty("links")
    public void setLinks(OntologySubmissionLinks links) {
        this.links = links;
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
