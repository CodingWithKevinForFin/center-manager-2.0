package com.f1.ami.web;

import java.util.Set;

public interface AmiWebAmiObjectsVariablesListener {
	public void onVariableAdded(String variableName, String ari);
	public void onVariableRemoved(String variableName, String ari);
	public void onVariableUpdated(String oldVariableName, String newVariableName, String ari);

	//	public void onVariableAdded(String variableName, AmiWebDomObject variable);
	//	public void onVariableRemoved(String variableName, AmiWebDomObject variable);
	//	public void onVariableUpdated(String oldVariableName, String newVariableName, AmiWebDomObject variable);
	public void onVariableUpdateOption(String variableName, String optionType, Object value);

	public String getNextVariableName(String suggestedName);
	public Set<String> getUsedVariableNames(Set<String> sink);
}
