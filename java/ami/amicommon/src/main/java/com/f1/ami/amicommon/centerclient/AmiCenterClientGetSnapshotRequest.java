package com.f1.ami.amicommon.centerclient;

import java.util.Set;

import com.f1.base.Message;

public interface AmiCenterClientGetSnapshotRequest extends Message {

	public Set<String> getAmiObjectTypesToSend();
	void setAmiObjectTypesToSend(Set<String> types);

	public Set<String> getAmiObjectTypesToStopSend();
	void setAmiObjectTypesToStopSend(Set<String> types);

	public String getInvokedBy();
	public void setInvokedBy(String invokedBy);

	public String getSessionUid();
	public void setSessionUid(String sessionUid);

	public void setRequestTime(long currentTimeMillis);
	public long getRequestTime();

}
