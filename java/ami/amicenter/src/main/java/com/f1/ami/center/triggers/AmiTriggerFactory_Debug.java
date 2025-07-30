package com.f1.ami.center.triggers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class AmiTriggerFactory_Debug implements AmiTriggerFactory {

	private List<AmiFactoryOption> options = new ArrayList<AmiFactoryOption>();

	public AmiTriggerFactory_Debug() {
		options.add(new AmiFactoryOption("returnOnDelete", Boolean.class, false, ""));
		options.add(new AmiFactoryOption("returnOnInsert", Boolean.class, false, ""));
		options.add(new AmiFactoryOption("returnOnUpdate", Boolean.class, false, ""));
	}

	@Override
	public void init(ContainerTools tools, PropertyController props) {

	}

	@Override
	public AmiTrigger newTrigger() {
		return new AmiTrigger_Debug();
	}

	@Override
	public String getPluginId() {
		return "DEBUG";
	}

	@Override
	public Collection<AmiFactoryOption> getAllowedOptions() {
		return options;
	}

}
