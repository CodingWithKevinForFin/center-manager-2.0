package com.f1.ami.center;

public class AmiCenterApplication {

	final private short appId;
	final private String appName;
	final private AmiCenterState state;

	public AmiCenterApplication(AmiCenterState state, short appId, String appName) {
		this.state = state;
		this.appId = appId;
		this.appName = appName;
	}
	public short getAppId() {
		return appId;
	}
	public String getAppName() {
		return this.appName;
	}
}
