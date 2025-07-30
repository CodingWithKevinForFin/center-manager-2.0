package com.f1.ami.web.dm;

import com.f1.ami.web.AmiWebDmPortlet;

public interface AmiWebDmManagerListener {

	void onDmUpdated(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm);
	void onDmAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm);
	void onDmRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm);
	void onDmNameChanged(AmiWebDmManager amiWebDmManagerImpl, String oldAliasDotName, AmiWebDm dm);

	void onDmDependencyAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm upper, AmiWebDm lower);
	void onDmDependencyRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm upper, AmiWebDm lower);

	void onDmLinkAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDmLink link);
	void onDmLinkRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDmLink link);

	public void onDmManagerInitDone();
	void onDmDependencyAdded(AmiWebDmManager manager, AmiWebDmPortlet target, String dmName, String tableName);
	void onDmDependencyRemoved(AmiWebDmManagerImpl amiWebDmManagerImpl, AmiWebDmPortlet target, String dmName, String tableName);

	void onFilterDependencyAdded(AmiWebDmManagerImpl amiWebDmManagerImpl, AmiWebDmFilter target, String dmName, String tableName);
	void onFilterDependencyRemoved(AmiWebDmManagerImpl amiWebDmManagerImpl, AmiWebDmFilter target, String dmName, String tableName);

}
