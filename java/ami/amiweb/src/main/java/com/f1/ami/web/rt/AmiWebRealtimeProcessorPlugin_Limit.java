package com.f1.ami.web.rt;

import java.util.Set;

import com.f1.ami.web.AmiWebRealtimeProcessor;
import com.f1.ami.web.AmiWebRealtimeProcessorPlugin;
import com.f1.ami.web.AmiWebService;
import com.f1.container.ContainerTools;
import com.f1.suite.web.portal.Portlet;
import com.f1.utils.CH;
import com.f1.utils.PropertyController;

public class AmiWebRealtimeProcessorPlugin_Limit implements AmiWebRealtimeProcessorPlugin {

	public static final String PLUGIN_ID = "LIMIT";
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
		return new AmiWebRealtimeProcessor_Limit(service);
	}
	@Override
	public AmiWebRealtimeProcessor create(AmiWebService service, String alias) {
		return new AmiWebRealtimeProcessor_Limit(service, alias);
	}

	@Override
	public String getDescription() {
		return "Limit";
	}

	@Override
	public void startWizard(AmiWebService service, Set<String> entries) {
		if (entries.size() != 1) {
			service.getPortletManager().showAlert("Plesae select exactly one realtime object");
			return;
		}
		Portlet p = new AmiWebRealtimeProcessorWizard_Limit(service.getPortletManager().generateConfig(), CH.first(entries));
		service.getPortletManager().showDialog("Create Realtime Limit Processor ", p);
	}

	@Override
	public boolean canSupportCount(int size) {
		return size == 1;
	}
	@Override
	public void starEditWizard(AmiWebService service, AmiWebRealtimeProcessor target) {
		Portlet p = new AmiWebRealtimeProcessorWizard_Limit(service.getPortletManager().generateConfig(), target);
		service.getPortletManager().showDialog("Edit Realtime Processor ", p);
	}

}
