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
		"prefLabel",
		"synonym",
		"definition",
		"obsolete",
		"matchType",
		"ontologyType",
		"provisional",
		"@id",
		"@type",
		"entityLinks"
})
public class Entity {

	public static Entity TOP_ENTITY = new Entity();
	static {
		TOP_ENTITY.setId("TOP");
		TOP_ENTITY.setPrefLabel("TOP");
	}

	@JsonProperty("prefLabel")
	private String prefLabel;
	@JsonProperty("synonym")
	private List<String> synonym = new ArrayList<String>();
	@JsonProperty("definition")
	private List<String> definition = new ArrayList<String>();
	@JsonProperty("obsolete")
	private Boolean obsolete;
	@JsonProperty("matchType")
	private String matchType;
	@JsonProperty("ontologyType")
	private String ontologyType;
	@JsonProperty("provisional")
	private Boolean provisional;
	@JsonProperty("@id")
	private String Id;
	@JsonProperty("@type")
	private String Type;
	@JsonProperty("entityLinks")
	private EntityLinks entityLinks;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * @return The prefLabel
	 */
	@JsonProperty("prefLabel")
	public String getPrefLabel() {
		return prefLabel;
	}

	/**
	 * @param prefLabel The prefLabel
	 */
	@JsonProperty("prefLabel")
	public void setPrefLabel(String prefLabel) {
		this.prefLabel = prefLabel;
	}

	/**
	 * @return The synonym
	 */
	@JsonProperty("synonym")
	public List<String> getSynonym() {
		return synonym;
	}

	/**
	 * @param synonym The synonym
	 */
	@JsonProperty("synonym")
	public void setSynonym(List<String> synonym) {
		this.synonym = synonym;
	}

	/**
	 * @return The definition
	 */
	@JsonProperty("definition")
	public List<String> getDefinition() {
		return definition;
	}

	/**
	 * @param definition The definition
	 */
	@JsonProperty("definition")
	public void setDefinition(List<String> definition) {
		this.definition = definition;
	}

	/**
	 * @return The obsolete
	 */
	@JsonProperty("obsolete")
	public Boolean getObsolete() {
		return obsolete;
	}

	/**
	 * @param obsolete The obsolete
	 */
	@JsonProperty("obsolete")
	public void setObsolete(Boolean obsolete) {
		this.obsolete = obsolete;
	}

	/**
	 * @return The matchType
	 */
	@JsonProperty("matchType")
	public String getMatchType() {
		return matchType;
	}

	/**
	 * @param matchType The matchType
	 */
	@JsonProperty("matchType")
	public void setMatchType(String matchType) {
		this.matchType = matchType;
	}

	/**
	 * @return The ontologyType
	 */
	@JsonProperty("ontologyType")
	public String getOntologyType() {
		return ontologyType;
	}

	/**
	 * @param ontologyType The ontologyType
	 */
	@JsonProperty("ontologyType")
	public void setOntologyType(String ontologyType) {
		this.ontologyType = ontologyType;
	}

	/**
	 * @return The provisional
	 */
	@JsonProperty("provisional")
	public Boolean getProvisional() {
		return provisional;
	}

	/**
	 * @param provisional The provisional
	 */
	@JsonProperty("provisional")
	public void setProvisional(Boolean provisional) {
		this.provisional = provisional;
	}

	/**
	 * @return The Id
	 */
	@JsonProperty("@id")
	public String getId() {
		return Id;
	}

	/**
	 * @param Id The @id
	 */
	@JsonProperty("@id")
	public void setId(String Id) {
		this.Id = Id;
	}

	/**
	 * @return The Type
	 */
	@JsonProperty("@type")
	public String getType() {
		return Type;
	}

	/**
	 * @param Type The @type
	 */
	@JsonProperty("@type")
	public void setType(String Type) {
		this.Type = Type;
	}

	/**
	 * @return The entityLinks
	 */
	@JsonProperty("links")
	public EntityLinks getEntityLinks() {
		return entityLinks;
	}

	/**
	 * @param entityLinks The entityLinks
	 */
	@JsonProperty("links")
	public void setEntityLinks(EntityLinks entityLinks) {
		this.entityLinks = entityLinks;
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
		return getId();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Entity)) return false;

		Entity entity = (Entity) o;

		if (!Id.equals(entity.Id)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Id.hashCode();
	}
}