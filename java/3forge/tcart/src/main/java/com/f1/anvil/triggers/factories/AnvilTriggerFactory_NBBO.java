package com.f1.anvil.triggers.factories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.center.triggers.AmiTrigger;
import com.f1.ami.center.triggers.AmiTriggerFactory;
import com.f1.anvil.triggers.AnvilTriggerNBBO;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class AnvilTriggerFactory_NBBO implements AmiTriggerFactory {

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
		return "NBBO";
	}

	@Override
	public AmiTrigger newTrigger() {
		return new AnvilTriggerNBBO();
	}

}
