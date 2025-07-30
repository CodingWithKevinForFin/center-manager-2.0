package com.f1.ami.web.rt;

import java.util.Set;

import com.f1.ami.web.AmiWebRealtimeProcessor;
import com.f1.ami.web.AmiWebRealtimeProcessorPlugin;
import com.f1.ami.web.AmiWebService;
import com.f1.container.ContainerTools;
import com.f1.suite.web.portal.Portlet;
import com.f1.utils.PropertyController;

public class AmiWebRealtimeProcessorPlugin_GRPC implements AmiWebRealtimeProcessorPlugin {

	public static final String PLUGIN_ID = "GRPC";

	@Override
	public void init(ContainerTools tools, PropertyController props) {
	}

	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}

	@Override
	public AmiWebRealtimeProcessor create(AmiWebService service) {
		return new AmiWebRealtimeProcessor_GRPC(service);
	}

	@Override
	public String getDescription() {
		return "GRPC";
	}

	public void startWizard(AmiWebService service) {
		Portlet p = new AmiWebRealtimeProcessorWizard_GRPC(service.getPortletManager().generateConfig());
		service.getPortletManager().showDialog("Create Realtime GRPC Processor ", p);
	}

	@Override
	public boolean canSupportCount(int size) {
		return size == 0;
	}
	@Override
	public void starEditWizard(AmiWebService service, AmiWebRealtimeProcessor target) {
		Portlet p = new AmiWebRealtimeProcessorWizard_GRPC(service.getPortletManager().generateConfig(), target);
		service.getPortletManager().showDialog("Edit Realtime Processor ", p);
	}

	@Override
	public AmiWebRealtimeProcessor create(AmiWebService service, String fullAlias) {
		return new AmiWebRealtimeProcessor_GRPC(service, fullAlias);
	}

	@Override
	public void startWizard(AmiWebService service, Set<String> selectedRealtimeIds) {
		Portlet p = new AmiWebRealtimeProcessorWizard_GRPC(service.getPortletManager().generateConfig());
		service.getPortletManager().showDialog("Create Realtime GRPC Processor ", p);
	}
}
