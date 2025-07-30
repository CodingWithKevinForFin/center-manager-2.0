package com.f1.povo.f1app.audit;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.ATME")
public interface F1AppAuditTrailMsgEvent extends F1AppAuditTrailEvent {

	byte PID_IS_INCOMING = 12;
	byte PID_MSG_TYPE = 13;
	byte PID_TOPIC = 14;

	@PID(PID_IS_INCOMING)
	public boolean getIsIncoming();
	public void setIsIncoming(boolean isIncoming);

	@PID(PID_MSG_TYPE)
	public void setMsgType(byte type);
	public byte getMsgType();

	@PID(PID_TOPIC)
	public String getTopic();
	public void setTopic(String topic);

}
