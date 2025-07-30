package com.f1.vortexcommon.msg.eye;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VE.VUDSFA")
public interface VortexUpdateDeploymentStatusesFromAgent extends PartialMessage {

	@PID(2)
	public List<VortexDeployment> getUpdated();
	public void setUpdated(List<VortexDeployment> statuses);

}
