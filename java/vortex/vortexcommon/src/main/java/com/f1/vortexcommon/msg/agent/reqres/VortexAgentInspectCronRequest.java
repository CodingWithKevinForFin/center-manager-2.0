package com.f1.vortexcommon.msg.agent.reqres;

import java.util.TimeZone;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.ICQ")
public interface VortexAgentInspectCronRequest extends VortexAgentRequest {

	byte PID_TIME_ZONE = 1;

	@PID(PID_TIME_ZONE)
	public TimeZone getTimeZone();
	public void setTimeZone(TimeZone zone);
}
