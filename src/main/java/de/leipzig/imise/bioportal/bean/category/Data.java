package de.leipzig.imise.bioportal.bean.category;

import java.util.ArrayList;
import java.util.List;

public class Data {
	List<CategoryBean> list = new ArrayList<CategoryBean>();

	public void setCategoryBeans(List<CategoryBean> list) {
		this.list = list;
	}

	public List<CategoryBean> getCategoryBeans() {
		return list;
	}
}
