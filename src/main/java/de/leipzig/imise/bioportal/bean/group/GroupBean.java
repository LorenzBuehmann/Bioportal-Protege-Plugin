package de.leipzig.imise.bioportal.bean.group;

public class GroupBean implements Comparable<GroupBean>{

	protected Integer id;
	protected String acronym;
	protected String name;
	protected String description;
	
	public GroupBean(String name){
		this.id = Integer.valueOf(0000);
		this.name = name;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getAcronym() {
		return acronym;
	}
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		if(acronym != null){
			sb.append(" (").append(acronym).append(")");
		}
		return sb.toString();
	}

	@Override
	public int compareTo(GroupBean o) {
		return this.name.compareTo(o.getName());
	}
}
