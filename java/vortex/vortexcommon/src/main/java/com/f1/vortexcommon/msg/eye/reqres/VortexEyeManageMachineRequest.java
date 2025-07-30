package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;

@VID("F1.VE.MMCQ")
public interface VortexEyeManageMachineRequest extends VortexEyeRequest {

	byte PID_MACHINE = 1;

	@PID(PID_MACHINE)
	public VortexAgentMachine getMachine();
	public void setMachine(VortexAgentMachine machine);

}
