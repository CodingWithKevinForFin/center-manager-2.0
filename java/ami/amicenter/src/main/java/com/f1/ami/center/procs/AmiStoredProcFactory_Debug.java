package com.f1.ami.center.procs;

import java.util.Collection;
import java.util.Collections;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class AmiStoredProcFactory_Debug implements AmiStoredProcFactory {

	@Override
	public void init(ContainerTools tools, PropertyController props) {
	}

	@Override
	public AmiStoredProc newStoredProc() {
		return new AmiStoredProc_Debug();
	}

	@Override
	public String getPluginId() {
		return "DEBUG";
	}

	@Override
	public Collection<AmiFactoryOption> getAllowedOptions() {
		return Collections.EMPTY_LIST;
	}

}
