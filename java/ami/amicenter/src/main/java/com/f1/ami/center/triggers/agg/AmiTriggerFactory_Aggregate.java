package com.f1.ami.center.triggers.agg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.center.triggers.AmiTrigger;
import com.f1.ami.center.triggers.AmiTriggerFactory;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class AmiTriggerFactory_Aggregate implements AmiTriggerFactory {

	private List<AmiFactoryOption> options = new ArrayList<AmiFactoryOption>();

	public AmiTriggerFactory_Aggregate() {
		options.add(new AmiFactoryOption("groupBys", String.class, true, ""));
		options.add(new AmiFactoryOption("selects", String.class, true, ""));
		options.add(new AmiFactoryOption("allowExternalUpdates", Boolean.class, false, ""));
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
		return "AGGREGATE";
	}

	@Override
	public AmiTrigger newTrigger() {
		return new AmiTrigger_Aggregate();
	}

}
