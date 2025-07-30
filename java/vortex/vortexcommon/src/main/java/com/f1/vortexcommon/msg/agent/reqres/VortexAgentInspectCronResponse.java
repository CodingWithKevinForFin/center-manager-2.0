package com.f1.vortexcommon.msg.agent.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexAgentCron;

@VID("F1.VA.ICR")
public interface VortexAgentInspectCronResponse extends VortexAgentResponse {

	byte PID_CRON = 1;

	@PID(PID_CRON)
	public List<VortexAgentCron> getCron();
	public void setCron(List<VortexAgentCron> chron);
}
