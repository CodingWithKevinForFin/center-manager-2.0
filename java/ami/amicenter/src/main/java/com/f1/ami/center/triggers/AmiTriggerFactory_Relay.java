package com.f1.ami.center.triggers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class AmiTriggerFactory_Relay implements AmiTriggerFactory {

	private List<AmiFactoryOption> options = new ArrayList<AmiFactoryOption>();

	public AmiTriggerFactory_Relay() {
		options.add(new AmiFactoryOption("host", String.class, true, ""));
		options.add(new AmiFactoryOption("port", Integer.class, true, ""));
		options.add(new AmiFactoryOption("login", String.class, true, ""));
		options.add(new AmiFactoryOption("keystoreFile", String.class, false, ""));
		options.add(new AmiFactoryOption("keystorePass", String.class, false, ""));
		options.add(new AmiFactoryOption("derivedValues", String.class, false, ""));
		options.add(new AmiFactoryOption("inserts", String.class, false, ""));
		options.add(new AmiFactoryOption("updates", String.class, false, ""));
		options.add(new AmiFactoryOption("deletes", String.class, false, ""));
		options.add(new AmiFactoryOption("target", String.class, false, ""));
		options.add(new AmiFactoryOption("where", String.class, false, ""));
	}

	@Override
	public Collection<AmiFactoryOption> getAllowedOptions() {
		return options;
	}

	@Override
	public void init(ContainerTools tools, PropertyController props) {

	}

	@Override
	public String getPluginId() {
		return "RELAY";
	}

	@Override
	public AmiTrigger newTrigger() {
		return new AmiTrigger_Relay();
	}

}
