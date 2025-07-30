package com.f1.ami.center.dialects;

import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class AmiDbDialectPlugin_Hibernate implements AmiDbDialectPlugin {

	private PropertyController props;

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		this.props = props;
	}

	@Override
	public String getPluginId() {
		return "HIBERNATE";
	}

	@Override
	public AmiDbDialect createDialectInstance() {
		return new AmiDbDialect_HibernateSql(this.props);
	}

}
