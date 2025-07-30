package com.f1.ami.relay;

import com.f1.container.impl.BasicState;

public class AmiRelayMonitorFilesState extends BasicState {

	final private AmiRelayTransformManager transformManager;
	final private AmiRelayRoutes routes;

	public AmiRelayMonitorFilesState(AmiRelayTransformManager transformManager, AmiRelayRoutes routes) {
		super();
		this.transformManager = transformManager;
		this.routes = routes;
	}

	public AmiRelayTransformManager getTransformManager() {
		return transformManager;
	}

	public AmiRelayRoutes getRoutes() {
		return routes;
	}

}
