package com.f1.ami.amicommon.centerclient;

import com.f1.base.Message;

public interface AmiCenterClientGetSnapshotResponse extends Message {

	//	public List<AmiWebObjectMessage> getMessages();
	//	public void setMessages(List<AmiWebObjectMessage> messages);
	//
	//	public List<AmiWebObject_Feed> getCached();
	//	public void setCached(List<AmiWebObject_Feed> messages);

	public long getSeqNum();
	public void setSeqNum(long seqnum);

	public byte getCenterId();
	public void setCenterId(byte centerId);

	public String getProcessUid();
	public void setProcessUid(String centerId);

}
