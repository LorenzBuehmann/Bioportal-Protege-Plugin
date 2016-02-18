package de.leipzig.imise.bioportal.bean.hierarchy;

import java.util.ArrayList;
import java.util.List;

public class Data {
	List<ClassBean> list = new ArrayList<ClassBean>();

	public void setClassBeans(List<ClassBean> list) {
		this.list = list;
	}

	public List<ClassBean> getClassBeans() {
		return list;
	}
}
