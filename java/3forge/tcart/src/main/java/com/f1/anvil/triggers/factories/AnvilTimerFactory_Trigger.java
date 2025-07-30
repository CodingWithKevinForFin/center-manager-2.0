package com.f1.anvil.triggers.factories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.center.timers.AmiTimer;
import com.f1.ami.center.timers.AmiTimerFactory;
import com.f1.anvil.triggers.AnvilTriggerTimer;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class AnvilTimerFactory_Trigger implements AmiTimerFactory {
	private List<AmiFactoryOption> options = new ArrayList<AmiFactoryOption>();

	@Override
	public Collection<AmiFactoryOption> getAllowedOptions() {
		return options;
	}

	@Override
	public void init(ContainerTools tools, PropertyController props) {

	}

	@Override
	public String getPluginId() {
		return "TRIGGER";
	}

	@Override
	public AmiTimer newTimer() {
		return new AnvilTriggerTimer();
	}

}
