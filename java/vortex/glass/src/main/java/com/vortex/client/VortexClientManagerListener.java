package com.vortex.client;

import com.vortex.web.VortexWebEyeService;

public interface VortexClientManagerListener {

	void onVortexEyeDisconnected();
	public void onVortexEyeSnapshotProcessed();
	void onVortexClientListenerAdded(Object listener);
	void onVortexConnectionStateChanged(VortexClientManager vortexClientManager, VortexWebEyeService vortexWebEyeService);

}
