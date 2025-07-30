package com.f1.ami.web;

public interface AmiWebDomObjectDependency {

	void initLinkedVariables();
	void onDomObjectAriChanged(AmiWebDomObject target, String oldAri);
	void onDomObjectEvent(AmiWebDomObject object, byte eventType);
	void onDomObjectRemoved(AmiWebDomObject object);
	void onDomObjectAdded(AmiWebDomObject object);
}
