package de.leipzig.imise.bioportal.bean.category;

public class CategoryBean implements Comparable<CategoryBean>{

	protected Integer id;
	protected String parentId;
	protected String name;
	
	public CategoryBean(String name){
		this.id = Integer.valueOf(0000);
		this.name = name;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(CategoryBean o) {
		return this.name.compareTo(o.getName());
	}
}
