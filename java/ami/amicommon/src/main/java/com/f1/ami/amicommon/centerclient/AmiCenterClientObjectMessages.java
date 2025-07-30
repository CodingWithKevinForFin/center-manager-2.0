package com.f1.ami.amicommon.centerclient;

import java.util.List;

import com.f1.base.Message;

public interface AmiCenterClientObjectMessages extends Message {

	public List<AmiCenterClientObjectMessage> getMessages();
	public void setMessages(List<AmiCenterClientObjectMessage> messages);

	public long getSeqNum();
	public void setSeqNum(long seqnum);

	public byte getCenterId();
	public void setCenterId(byte centerId);

}
