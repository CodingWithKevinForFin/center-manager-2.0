package com.f1.ami.amicommon.centerclient;

import com.f1.base.Message;
import com.f1.povo.msg.MsgStatusMessage;

public interface AmiCenterClientMsgStatusMessage extends Message {

	public byte getCenterId();
	public void setCenterId(byte centerId);

	public void setMsgStatusMessage(MsgStatusMessage msg);
	public MsgStatusMessage getMsgStatusMessage();

}
