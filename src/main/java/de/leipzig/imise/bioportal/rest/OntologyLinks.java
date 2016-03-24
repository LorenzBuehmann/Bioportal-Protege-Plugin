
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
    "submissions",
    "properties",
    "classes",
    "single_class",
    "roots",
    "instances",
    "metrics",
    "reviews",
    "notes",
    "groups",
    "categories",
    "latest_submission",
    "projects",
    "download",
    "views",
    "analytics",
    "ui"
})
public class OntologyLinks {

    @JsonProperty("submissions")
    private String submissions;
    @JsonProperty("properties")
    private String properties;
    @JsonProperty("classes")
    private String classes;
    @JsonProperty("single_class")
    private String singleClass;
    @JsonProperty("roots")
    private String roots;
    @JsonProperty("instances")
    private String instances;
    @JsonProperty("metrics")
    private String metrics;
    @JsonProperty("reviews")
    private String reviews;
    @JsonProperty("notes")
    private String notes;
    @JsonProperty("groups")
    private String groups;
    @JsonProperty("categories")
    private String categories;
    @JsonProperty("latest_submission")
    private String latestSubmission;
    @JsonProperty("projects")
    private String projects;
    @JsonProperty("download")
    private String download;
    @JsonProperty("views")
    private String views;
    @JsonProperty("analytics")
    private String analytics;
    @JsonProperty("ui")
    private String ui;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The submissions
     */
    @JsonProperty("submissions")
    public String getSubmissions() {
        return submissions;
    }

    /**
     * 
     * @param submissions
     *     The submissions
     */
    @JsonProperty("submissions")
    public void setSubmissions(String submissions) {
        this.submissions = submissions;
    }

    /**
     * 
     * @return
     *     The properties
     */
    @JsonProperty("properties")
    public String getProperties() {
        return properties;
    }

    /**
     * 
     * @param properties
     *     The properties
     */
    @JsonProperty("properties")
    public void setProperties(String properties) {
        this.properties = properties;
    }

    /**
     * 
     * @return
     *     The classes
     */
    @JsonProperty("classes")
    public String getClasses() {
        return classes;
    }

    /**
     * 
     * @param classes
     *     The classes
     */
    @JsonProperty("classes")
    public void setClasses(String classes) {
        this.classes = classes;
    }

    /**
     * 
     * @return
     *     The singleClass
     */
    @JsonProperty("single_class")
    public String getSingleClass() {
        return singleClass;
    }

    /**
     * 
     * @param singleClass
     *     The single_class
     */
    @JsonProperty("single_class")
    public void setSingleClass(String singleClass) {
        this.singleClass = singleClass;
    }

    /**
     * 
     * @return
     *     The roots
     */
    @JsonProperty("roots")
    public String getRoots() {
        return roots;
    }

    /**
     * 
     * @param roots
     *     The roots
     */
    @JsonProperty("roots")
    public void setRoots(String roots) {
        this.roots = roots;
    }

    /**
     * 
     * @return
     *     The instances
     */
    @JsonProperty("instances")
    public String getInstances() {
        return instances;
    }

    /**
     * 
     * @param instances
     *     The instances
     */
    @JsonProperty("instances")
    public void setInstances(String instances) {
        this.instances = instances;
    }

    /**
     * 
     * @return
     *     The metrics
     */
    @JsonProperty("metrics")
    public String getMetrics() {
        return metrics;
    }

    /**
     * 
     * @param metrics
     *     The metrics
     */
    @JsonProperty("metrics")
    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    /**
     * 
     * @return
     *     The reviews
     */
    @JsonProperty("reviews")
    public String getReviews() {
        return reviews;
    }

    /**
     * 
     * @param reviews
     *     The reviews
     */
    @JsonProperty("reviews")
    public void setReviews(String reviews) {
        this.reviews = reviews;
    }

    /**
     * 
     * @return
     *     The notes
     */
    @JsonProperty("notes")
    public String getNotes() {
        return notes;
    }

    /**
     * 
     * @param notes
     *     The notes
     */
    @JsonProperty("notes")
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * 
     * @return
     *     The groups
     */
    @JsonProperty("groups")
    public String getGroups() {
        return groups;
    }

    /**
     * 
     * @param groups
     *     The groups
     */
    @JsonProperty("groups")
    public void setGroups(String groups) {
        this.groups = groups;
    }

    /**
     * 
     * @return
     *     The categories
     */
    @JsonProperty("categories")
    public String getCategories() {
        return categories;
    }

    /**
     * 
     * @param categories
     *     The categories
     */
    @JsonProperty("categories")
    public void setCategories(String categories) {
        this.categories = categories;
    }

    /**
     * 
     * @return
     *     The latestSubmission
     */
    @JsonProperty("latest_submission")
    public String getLatestSubmission() {
        return latestSubmission;
    }

    /**
     * 
     * @param latestSubmission
     *     The latest_submission
     */
    @JsonProperty("latest_submission")
    public void setLatestSubmission(String latestSubmission) {
        this.latestSubmission = latestSubmission;
    }

    /**
     * 
     * @return
     *     The projects
     */
    @JsonProperty("projects")
    public String getProjects() {
        return projects;
    }

    /**
     * 
     * @param projects
     *     The projects
     */
    @JsonProperty("projects")
    public void setProjects(String projects) {
        this.projects = projects;
    }

    /**
     * 
     * @return
     *     The download
     */
    @JsonProperty("download")
    public String getDownload() {
        return download;
    }

    /**
     * 
     * @param download
     *     The download
     */
    @JsonProperty("download")
    public void setDownload(String download) {
        this.download = download;
    }

    /**
     * 
     * @return
     *     The views
     */
    @JsonProperty("views")
    public String getViews() {
        return views;
    }

    /**
     * 
     * @param views
     *     The views
     */
    @JsonProperty("views")
    public void setViews(String views) {
        this.views = views;
    }

    /**
     * 
     * @return
     *     The analytics
     */
    @JsonProperty("analytics")
    public String getAnalytics() {
        return analytics;
    }

    /**
     * 
     * @param analytics
     *     The analytics
     */
    @JsonProperty("analytics")
    public void setAnalytics(String analytics) {
        this.analytics = analytics;
    }

    /**
     * 
     * @return
     *     The ui
     */
    @JsonProperty("ui")
    public String getUi() {
        return ui;
    }

    /**
     * 
     * @param ui
     *     The ui
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
