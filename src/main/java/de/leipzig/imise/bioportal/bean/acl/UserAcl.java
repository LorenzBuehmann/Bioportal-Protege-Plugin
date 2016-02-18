package de.leipzig.imise.bioportal.bean.acl;

import java.util.List;

public class UserAcl {
	
	private List<UserEntry> userEntry;
	
	public List<UserEntry> getUserEntries() {
		return userEntry;
	}
	
	public void getUserEntries(List<UserEntry> userEntries) {
		this.userEntry = userEntries;
	}

}
