package com.f1.ami.center.dialects;

import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class AmiDbDialectPlugin_Tableau implements AmiDbDialectPlugin {

	@Override
	public void init(ContainerTools tools, PropertyController props) {
	}

	@Override
	public String getPluginId() {
		return "TABLEAU";
	}

	@Override
	public AmiDbDialect createDialectInstance() {
		return new AmiDbDialect_Tableau();
	}

}
