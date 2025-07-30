package com.f1.ami.web;

import java.util.Set;

import com.f1.ami.web.dm.AmiWebDmListener;

public interface AmiWebDmPortlet extends AmiWebPortlet, AmiWebDmListener {
	public Set<String> getUsedDmVariables(String dmAliasDotDmName, String dmTable, Set<String> sink);
	public Set<String> getUsedDmAliasDotNames();//For compiling situations, the first entry in the set is the dm to use (Use a LinkedHashSet obviously)
	public Set<String> getUsedDmTables(String aliasDotName);

}
