package com.f1.anvil.triggers.factories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.center.procs.AmiStoredProc;
import com.f1.ami.center.procs.AmiStoredProcFactory;
import com.f1.anvil.triggers.AnvilCommandTriggerImpl;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class AnvilStoredProcFactory_HandleAlert implements AmiStoredProcFactory {

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
		return "spHandleAlert";
	}
	@Override
	public AmiStoredProc newStoredProc() {
		return new AnvilCommandTriggerImpl();
	}

}
