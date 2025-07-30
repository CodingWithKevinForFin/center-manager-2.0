/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.povo.standard;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.ST.OB")
public interface ObjectMessage extends Message {

	byte PID_OBJECT=1;
	
	@PID(PID_OBJECT)
	public Object getObject();
	public void setObject(Object object);

}
