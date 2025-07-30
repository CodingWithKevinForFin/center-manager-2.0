package com.f1.ami.amicommon.centerclient;

import java.util.List;
import java.util.Set;

import com.f1.base.Message;

public interface AmiCenterClientSnapshot extends Message {

	public List getCached();
	public void setCached(List messages);

	public long getSeqNum();
	public void setSeqNum(long seqnum);

	public byte getCenterId();
	public void setCenterId(byte centerId);

	public String getSessionUid();
	public void setSessionUid(String centerId);

	public Set<String> getTypes();
	public void setTypes(Set<String> types);

	public String getProcessUid();
	public void setProcessUid(String centerId);

	public void setOrigRequest(AmiCenterClientGetSnapshotRequest getSnapshotReqOrig);
	public AmiCenterClientGetSnapshotRequest getOrigRequest();

	public void setInvokedBy(String a);
	public String getInvokedBy();
}
