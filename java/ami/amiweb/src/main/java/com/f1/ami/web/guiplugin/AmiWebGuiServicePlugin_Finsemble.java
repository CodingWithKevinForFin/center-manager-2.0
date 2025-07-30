package com.f1.ami.web.guiplugin;

import com.f1.ami.web.AmiWebGuiServiceAdapter;
import com.f1.ami.web.AmiWebGuiServicePlugin;
import com.f1.ami.web.AmiWebService;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class AmiWebGuiServicePlugin_Finsemble implements AmiWebGuiServicePlugin {

	@Override
	public void init(ContainerTools tools, PropertyController props) {
	}

	@Override
	public String getPluginId() {
		return "FINSEMBLE";
	}

	@Override
	public AmiWebGuiServiceAdapter createGuiIntegrationAdapter(AmiWebService service) {
		return new AmiWebGuiServiceAdapter_Finsemble();
	}

}
