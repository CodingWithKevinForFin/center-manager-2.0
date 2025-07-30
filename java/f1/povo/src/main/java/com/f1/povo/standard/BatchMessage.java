/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.povo.standard;

import java.util.List;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.ST.BA")
public interface BatchMessage extends Message {

	
	byte PID_MESSAGES=1;
	
	@PID(PID_MESSAGES)
	public List<? extends Message> getMessages();
	public void setMessages(List<? extends Message> messages);

}
