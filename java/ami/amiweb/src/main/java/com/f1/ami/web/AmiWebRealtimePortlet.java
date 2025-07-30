package com.f1.ami.web;

//RENAME TO AmiWebRealtimePortlet
public interface AmiWebRealtimePortlet extends AmiWebLinkableVarsPortlet, AmiWebRealtimeObjectListener, AmiWebRealtimeObjectManager {

	public byte DOWN_STREAM_MODE_OFF = 0;
	public byte DOWN_STREAM_MODE_SELECTED_OR_ALL = 1;

	public AmiWebOverrideValue<Byte> getDownstreamRealtimeMode();
	public void setDownstreamRealtimeMode(byte mode);
	public void setDownstreamRealtimeModeOverride(byte mode);

}
