package com.f1.ami.web;

import java.util.Set;

import com.f1.ami.amicommon.AmiPlugin;

public interface AmiWebRealtimeProcessorPlugin extends AmiPlugin {

	AmiWebRealtimeProcessor create(AmiWebService service);
	AmiWebRealtimeProcessor create(AmiWebService service, String fullAlias);

	public String getDescription();

	void startWizard(AmiWebService service, Set<String> selectedRealtimeIds);
	void starEditWizard(AmiWebService service, AmiWebRealtimeProcessor target);

	boolean canSupportCount(int size);

}
