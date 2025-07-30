package com.f1.bootstrap.appmonitor;

public class AppMonitorClientState {

	final private AppMonitorState appMonitorState;
	final private String puid;
	final private long connectedTime;

	public AppMonitorClientState(AppMonitorState appMonitorState, String puid, long connectedTime) {
		this.appMonitorState = appMonitorState;
		this.puid = puid;
		this.connectedTime = connectedTime;
	}

	public AppMonitorState getAppMonitorState() {
		return appMonitorState;
	}

	public String getPuid() {
		return puid;
	}

	public long getConnectedTime() {
		return connectedTime;
	}

}
