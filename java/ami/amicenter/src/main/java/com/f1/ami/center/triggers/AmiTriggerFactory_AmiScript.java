package com.f1.ami.center.triggers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class AmiTriggerFactory_AmiScript implements AmiTriggerFactory {

	private List<AmiFactoryOption> options = new ArrayList<AmiFactoryOption>();

	public AmiTriggerFactory_AmiScript() {
		options.add(new AmiFactoryOption("canMutateRow", Boolean.class, false, ""));
		options.add(new AmiFactoryOption("runOnStartup", Boolean.class, false, ""));
		options.add(new AmiFactoryOption("onInsertingScript", String.class, false, ""));
		options.add(new AmiFactoryOption("onUpdatingScript", String.class, false, ""));
		options.add(new AmiFactoryOption("onDeletingScript", String.class, false, ""));
		options.add(new AmiFactoryOption("onInsertedScript", String.class, false, ""));
		options.add(new AmiFactoryOption("onUpdatedScript", String.class, false, ""));
		options.add(new AmiFactoryOption("rowVar", String.class, false, ""));
		options.add(new AmiFactoryOption("vars", String.class, false, ""));
		options.add(new AmiFactoryOption("onStartupScript", String.class, false, ""));
	}

	@Override
	public void init(ContainerTools tools, PropertyController props) {
	}

	@Override
	public AmiTrigger newTrigger() {
		return new AmiTrigger_AmiScript();
	}

	@Override
	public String getPluginId() {
		return "AMISCRIPT";
	}

	@Override
	public Collection<AmiFactoryOption> getAllowedOptions() {
		return options;
	}

}
