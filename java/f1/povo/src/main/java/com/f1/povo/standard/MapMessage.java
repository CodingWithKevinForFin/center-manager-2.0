/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.povo.standard;

import java.util.Map;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.ST.MA")
public interface MapMessage extends Message {

	byte PID_MAP = 1;
	@PID(PID_MAP)
	public Map<Object, Object> getMap();
	public void setMap(Map<Object, Object> map);

}
