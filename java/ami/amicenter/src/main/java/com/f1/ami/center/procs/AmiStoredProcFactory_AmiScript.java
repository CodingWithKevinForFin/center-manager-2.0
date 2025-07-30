package com.f1.ami.center.procs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class AmiStoredProcFactory_AmiScript implements AmiStoredProcFactory {

	private List<AmiFactoryOption> options = new ArrayList<AmiFactoryOption>();

	public AmiStoredProcFactory_AmiScript() {
		options.add(new AmiFactoryOption("script", String.class, true, ""));
		options.add(new AmiFactoryOption("arguments", String.class, true, ""));
		options.add(new AmiFactoryOption("vars", String.class, false, ""));
		options.add(new AmiFactoryOption("onStartupScript", String.class, false, ""));
		options.add(new AmiFactoryOption("logging", String.class, false, ""));
	}

	@Override
	public void init(ContainerTools tools, PropertyController props) {
	}

	@Override
	public AmiStoredProc newStoredProc() {
		return new AmiStoredProc_AmiScript();
	}

	@Override
	public String getPluginId() {
		return "AMISCRIPT";
	}

	@Override
	public Collection<AmiFactoryOption> getAllowedOptions() {
		return this.options;
	}

}
