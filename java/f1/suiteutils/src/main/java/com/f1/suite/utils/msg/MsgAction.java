/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.utils.msg;

import com.f1.base.Message;
import com.f1.msg.MsgEvent;

public interface MsgAction extends Message {

	public MsgEvent getMsgEvent();
	public void setMsgEvent(MsgEvent msg);

	public String getTopic();
	public void setTopic(String topic);

	public String getSource();
	public void setSource(String source);

}
