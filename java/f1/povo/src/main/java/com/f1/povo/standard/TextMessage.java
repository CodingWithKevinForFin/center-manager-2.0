/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.povo.standard;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.ST.TE")
public interface TextMessage extends Message {

	byte PID_TEXT = 1;

	@PID(PID_TEXT)
	public String getText();
	public void setText(String text);

}
