package com.f1.ami.web.guiplugin;

import com.f1.ami.web.AmiWebGuiServiceAdapter;
import com.f1.ami.web.AmiWebGuiServicePlugin;
import com.f1.ami.web.AmiWebService;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class AmiWebGuiServicePlugin_GuiTools implements AmiWebGuiServicePlugin {
	public static final String PLUGINID = "GUITOOLS";

	@Override
	public void init(ContainerTools tools, PropertyController props) {

	}

	@Override
	public String getPluginId() {
		return PLUGINID;
	}

	@Override
	public AmiWebGuiServiceAdapter createGuiIntegrationAdapter(AmiWebService service) {
		return new AmiWebGuiServiceAdapter_GuiTools();
	}

}
