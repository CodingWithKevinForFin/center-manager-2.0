package com.f1.vortexcommon.msg.agent.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.FDQ")
public interface VortexAgentFileDeleteRequest extends VortexAgentRequest {

	@PID(10)
	public List<String> getFiles();
	public void setFiles(List<String> files);

	@PID(11)
	public boolean getIsPermanent();
	public void setIsPermanent(boolean isPermanent);
}
