package com.f1.vortexcommon.msg.agent;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VA.TFES")
public interface VortexAgentTailFileEvents extends PartialMessage {

	byte PID_EVENTS = 1;
	byte PID_MACHINE_UID = 2;

	@PID(PID_EVENTS)
	public List<VortexAgentTailFileEvent> getEvents();
	public void setEvents(List<VortexAgentTailFileEvent> events);

	@PID(PID_MACHINE_UID)
	public void setMachineUid(String machineUid);
	public String getMachineUid();
}
