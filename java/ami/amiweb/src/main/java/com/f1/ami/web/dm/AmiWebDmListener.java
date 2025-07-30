package com.f1.ami.web.dm;

public interface AmiWebDmListener {

	public void onDmDataBeforeFilterChanged(AmiWebDm datamodel);
	public void onDmDataChanged(AmiWebDm datamodel);
	public void onDmError(AmiWebDm datamodel, AmiWebDmError error);
	public void onDmRunningQuery(AmiWebDm datamodel, boolean isRequery);

	boolean hasVisiblePortletForDm(AmiWebDm datamodel);
	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm);

}
