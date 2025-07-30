package com.f1.ami.web.rt;

import java.util.Set;

import com.f1.ami.web.AmiWebRealtimeProcessor;
import com.f1.ami.web.AmiWebRealtimeProcessorPlugin;
import com.f1.ami.web.AmiWebService;
import com.f1.container.ContainerTools;
import com.f1.suite.web.portal.Portlet;
import com.f1.utils.PropertyController;

public class AmiWebRealtimeProcessorPlugin_Decorate implements AmiWebRealtimeProcessorPlugin {

	public static final String PLUGIN_ID = "DECORATE";
	private ContainerTools tools;

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		this.tools = tools;
	}

	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}

	@Override
	public AmiWebRealtimeProcessor create(AmiWebService service) {
		return new AmiWebRealtimeProcessor_Decorate(service);
	}
	@Override
	public AmiWebRealtimeProcessor create(AmiWebService service, String alias) {
		return new AmiWebRealtimeProcessor_Decorate(service, alias);
	}
	@Override
	public String getDescription() {
		return "Decorate";
	}

	@Override
	public void startWizard(AmiWebService service, Set<String> entries) {
		if (entries.size() < 2) {
			service.getPortletManager().showAlert("Plesae select at least two realtime objects");
			return;
		}
		Portlet p = new AmiWebRealtimeProcessorWizard_Decorate(service.getPortletManager().generateConfig(), entries);
		service.getPortletManager().showDialog("Create Realtime Decorate Processor", p);

	}

	@Override
	public boolean canSupportCount(int size) {
		return size > 1;
	}

	@Override
	public void starEditWizard(AmiWebService service, AmiWebRealtimeProcessor target) {
		service.getPortletManager().showAlert("The DECORATE realtime processor does not support editing");
	}

}
