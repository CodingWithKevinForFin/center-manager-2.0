package com.f1.vortexcommon.msg.eye.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;

@VID("F1.VE.QHR")
public interface VortexEyeQueryHistoryResponse extends PartialMessage {

	byte PID_HISTORY = 23;
	//byte PID_MACHINES = 24;

	@PID(PID_HISTORY)
	public List<VortexAgentEntity> getHistory();
	public void setHistory(List<VortexAgentEntity> history);

	//@PID(PID_MACHINES)
	//public Map<AgentMachine, List<AgentMachineStats>> getMachines();
	//public void setMachines(Map<AgentMachine, List<AgentMachineStats>> queryMachineHistory);

}
