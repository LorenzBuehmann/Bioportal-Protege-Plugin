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
		"id",
		"acronym",
		"name",
		"description",
		"created",
		"parentCategory"
})
public class Category {

	@JsonProperty("id")
	private String id;
	@JsonProperty("acronym")
	private String acronym;
	@JsonProperty("name")
	private String name;
	@JsonProperty("description")
	private Object description;
	@JsonProperty("created")
	private String created;
	@JsonProperty("parentCategory")
	private String parentCategory;
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
	 * @return The acronym
	 */
	@JsonProperty("acronym")
	public String getAcronym() {
		return acronym;
	}

	/**
	 * @param acronym The acronym
	 */
	@JsonProperty("acronym")
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	/**
	 * @return The name
	 */
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	/**
	 * @param name The name
	 */
	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The description
	 */
	@JsonProperty("description")
	public Object getDescription() {
		return description;
	}

	/**
	 * @param description The description
	 */
	@JsonProperty("description")
	public void setDescription(Object description) {
		this.description = description;
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
	 * @return The parentCategory
	 */
	@JsonProperty("parentCategory")
	public String getParentCategory() {
		return parentCategory;
	}

	/**
	 * @param parentCategory The parentCategory
	 */
	@JsonProperty("parentCategory")
	public void setParentCategory(String parentCategory) {
		this.parentCategory = parentCategory;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@Override
	public String toString() {
		return getName();
	}
}