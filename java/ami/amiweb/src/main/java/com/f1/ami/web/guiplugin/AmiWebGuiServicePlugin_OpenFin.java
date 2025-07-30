package com.f1.ami.web.guiplugin;

import java.util.logging.Logger;

import com.f1.ami.web.AmiWebGuiServiceAdapter;
import com.f1.ami.web.AmiWebGuiServicePlugin;
import com.f1.ami.web.AmiWebService;
import com.f1.container.ContainerTools;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

public class AmiWebGuiServicePlugin_OpenFin implements AmiWebGuiServicePlugin {
	private static final Logger log = LH.get();

	@Override
	public void init(ContainerTools tools, PropertyController props) {
	}

	@Override
	public String getPluginId() {
		return "OPENFIN";
	}

	@Override
	public AmiWebGuiServiceAdapter createGuiIntegrationAdapter(AmiWebService service) {
		return new AmiWebGuiServiceAdapter_OpenFin();
	}

}
