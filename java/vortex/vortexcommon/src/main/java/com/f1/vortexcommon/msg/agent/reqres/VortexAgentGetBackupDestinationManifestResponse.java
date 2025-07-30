package com.f1.vortexcommon.msg.agent.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;

@VID("F1.VA.GBDMR")
public interface VortexAgentGetBackupDestinationManifestResponse extends VortexAgentResponse {

	@PID(12)
	public List<VortexAgentFile> getBackupFiles();
	public void setBackupFiles(List<VortexAgentFile> backupFiles);

}
