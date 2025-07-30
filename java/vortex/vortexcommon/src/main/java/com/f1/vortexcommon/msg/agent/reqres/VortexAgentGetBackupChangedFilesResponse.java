package com.f1.vortexcommon.msg.agent.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;

@VID("F1.VA.GBCFR")
public interface VortexAgentGetBackupChangedFilesResponse extends VortexAgentResponse {

	@PID(12)
	public List<VortexAgentFile> getBackupFiles();
	public void setBackupFiles(List<VortexAgentFile> backupFiles);

	@PID(13)
	public boolean getIsComplete();
	public void setIsComplete(boolean isComplete);

}
