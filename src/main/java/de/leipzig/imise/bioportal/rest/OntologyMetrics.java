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
		"id",
		"created",
		"classes",
		"individuals",
		"properties",
		"maxDepth",
		"maxChildCount",
		"averageChildCount",
		"classesWithOneChild",
		"classesWithMoreThan25Children",
		"classesWithNoDefinition",
		"submission"
})
public class OntologyMetrics {

	@JsonProperty("id")
	private String id;
	@JsonProperty("created")
	private String created;
	@JsonProperty("classes")
	private Integer classes;
	@JsonProperty("individuals")
	private Integer individuals;
	@JsonProperty("properties")
	private Integer properties;
	@JsonProperty("maxDepth")
	private Integer maxDepth;
	@JsonProperty("maxChildCount")
	private Integer maxChildCount;
	@JsonProperty("averageChildCount")
	private Integer averageChildCount;
	@JsonProperty("classesWithOneChild")
	private Integer classesWithOneChild;
	@JsonProperty("classesWithMoreThan25Children")
	private Integer classesWithMoreThan25Children;
	@JsonProperty("classesWithNoDefinition")
	private Integer classesWithNoDefinition;
	@JsonProperty("submission")
	private List<String> submission = new ArrayList<String>();
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * @return The id
	 */
	@JsonProperty("id")
	public String getId() {
		return id;
	}

	/**
	 * @param id The id
	 */
	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return The created
	 */
	@JsonProperty("created")
	public String getCreated() {
		return created;
	}

	/**
	 * @param created The created
	 */
	@JsonProperty("created")
	public void setCreated(String created) {
		this.created = created;
	}

	/**
	 * @return The classes
	 */
	@JsonProperty("classes")
	public Integer getClasses() {
		return classes;
	}

	/**
	 * @param classes The classes
	 */
	@JsonProperty("classes")
	public void setClasses(Integer classes) {
		this.classes = classes;
	}

	/**
	 * @return The individuals
	 */
	@JsonProperty("individuals")
	public Integer getIndividuals() {
		return individuals;
	}

	/**
	 * @param individuals The individuals
	 */
	@JsonProperty("individuals")
	public void setIndividuals(Integer individuals) {
		this.individuals = individuals;
	}

	/**
	 * @return The properties
	 */
	@JsonProperty("properties")
	public Integer getProperties() {
		return properties;
	}

	/**
	 * @param properties The properties
	 */
	@JsonProperty("properties")
	public void setProperties(Integer properties) {
		this.properties = properties;
	}

	/**
	 * @return The maxDepth
	 */
	@JsonProperty("maxDepth")
	public Integer getMaxDepth() {
		return maxDepth;
	}

	/**
	 * @param maxDepth The maxDepth
	 */
	@JsonProperty("maxDepth")
	public void setMaxDepth(Integer maxDepth) {
		this.maxDepth = maxDepth;
	}

	/**
	 * @return The maxChildCount
	 */
	@JsonProperty("maxChildCount")
	public Integer getMaxChildCount() {
		return maxChildCount;
	}

	/**
	 * @param maxChildCount The maxChildCount
	 */
	@JsonProperty("maxChildCount")
	public void setMaxChildCount(Integer maxChildCount) {
		this.maxChildCount = maxChildCount;
	}

	/**
	 * @return The averageChildCount
	 */
	@JsonProperty("averageChildCount")
	public Integer getAverageChildCount() {
		return averageChildCount;
	}

	/**
	 * @param averageChildCount The averageChildCount
	 */
	@JsonProperty("averageChildCount")
	public void setAverageChildCount(Integer averageChildCount) {
		this.averageChildCount = averageChildCount;
	}

	/**
	 * @return The classesWithOneChild
	 */
	@JsonProperty("classesWithOneChild")
	public Integer getClassesWithOneChild() {
		return classesWithOneChild;
	}

	/**
	 * @param classesWithOneChild The classesWithOneChild
	 */
	@JsonProperty("classesWithOneChild")
	public void setClassesWithOneChild(Integer classesWithOneChild) {
		this.classesWithOneChild = classesWithOneChild;
	}

	/**
	 * @return The classesWithMoreThan25Children
	 */
	@JsonProperty("classesWithMoreThan25Children")
	public Integer getClassesWithMoreThan25Children() {
		return classesWithMoreThan25Children;
	}

	/**
	 * @param classesWithMoreThan25Children The classesWithMoreThan25Children
	 */
	@JsonProperty("classesWithMoreThan25Children")
	public void setClassesWithMoreThan25Children(Integer classesWithMoreThan25Children) {
		this.classesWithMoreThan25Children = classesWithMoreThan25Children;
	}

	/**
	 * @return The classesWithNoDefinition
	 */
	@JsonProperty("classesWithNoDefinition")
	public Integer getClassesWithNoDefinition() {
		return classesWithNoDefinition;
	}

	/**
	 * @param classesWithNoDefinition The classesWithNoDefinition
	 */
	@JsonProperty("classesWithNoDefinition")
	public void setClassesWithNoDefinition(Integer classesWithNoDefinition) {
		this.classesWithNoDefinition = classesWithNoDefinition;
	}

	/**
	 * @return The submission
	 */
	@JsonProperty("submission")
	public List<String> getSubmission() {
		return submission;
	}

	/**
	 * @param submission The submission
	 */
	@JsonProperty("submission")
	public void setSubmission(List<String> submission) {
		this.submission = submission;
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