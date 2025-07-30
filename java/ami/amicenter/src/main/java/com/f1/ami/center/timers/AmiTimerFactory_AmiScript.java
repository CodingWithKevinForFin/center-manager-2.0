package com.f1.ami.center.timers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class AmiTimerFactory_AmiScript implements AmiTimerFactory {

	private List<AmiFactoryOption> options = new ArrayList<AmiFactoryOption>();

	public AmiTimerFactory_AmiScript() {
		options.add(new AmiFactoryOption("script", String.class, false, ""));
		options.add(new AmiFactoryOption("vars", String.class, false, ""));
		options.add(new AmiFactoryOption("onStartupScript", String.class, false, ""));
		options.add(new AmiFactoryOption("logging", String.class, false, ""));
		options.add(new AmiFactoryOption("timeout", Integer.class, false, ""));
		options.add(new AmiFactoryOption("limit", Integer.class, false, ""));

	}

	@Override
	public void init(ContainerTools tools, PropertyController props) {

	}

	@Override
	public Collection<AmiFactoryOption> getAllowedOptions() {
		return options;
	}

	@Override
	public AmiTimer newTimer() {
		return new AmiTimer_AmiScript();
	}

	@Override
	public String getPluginId() {
		return "AMISCRIPT";
	}

}
