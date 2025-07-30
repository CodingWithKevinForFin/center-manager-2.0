package com.f1.ami.center.timers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class AmiTimerFactory_Debug implements AmiTimerFactory {

	private List<AmiFactoryOption> options = new ArrayList<AmiFactoryOption>();

	public AmiTimerFactory_Debug() {
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
		return new AmiTimer_Debug();
	}

	@Override
	public String getPluginId() {
		return "DEBUG";
	}

}
