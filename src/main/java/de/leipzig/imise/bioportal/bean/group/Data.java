package de.leipzig.imise.bioportal.bean.group;

import java.util.ArrayList;
import java.util.List;

public class Data {
	List<GroupBean> list = new ArrayList<GroupBean>();

	public void setGroupBeans(List<GroupBean> list) {
		this.list = list;
	}

	public List<GroupBean> getGroupBeans() {
		return list;
	}
}
