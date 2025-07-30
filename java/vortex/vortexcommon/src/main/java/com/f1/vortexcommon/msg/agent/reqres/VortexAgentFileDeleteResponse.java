package com.f1.vortexcommon.msg.agent.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.FDR")
public interface VortexAgentFileDeleteResponse extends VortexAgentResponse {

	@PID(1)
	public List<String> getFilesDeleted();
	public void setFilesDeleted(List<String> files);

}
