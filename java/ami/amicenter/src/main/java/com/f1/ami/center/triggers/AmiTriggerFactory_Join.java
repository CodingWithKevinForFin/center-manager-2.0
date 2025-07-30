package com.f1.ami.center.triggers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class AmiTriggerFactory_Join implements AmiTriggerFactory {

	private List<AmiFactoryOption> options = new ArrayList<AmiFactoryOption>();
	public AmiTriggerFactory_Join() {
		options.add(new AmiFactoryOption("type", String.class, false, ""));
		options.add(new AmiFactoryOption("on", String.class, true, ""));
		options.add(new AmiFactoryOption("wheres", String.class, false, ""));
		options.add(new AmiFactoryOption("selects", String.class, true, ""));
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
		return "JOIN";
	}

	@Override
	public AmiTrigger newTrigger() {
		return new AmiTrigger_Join();
	}

}
