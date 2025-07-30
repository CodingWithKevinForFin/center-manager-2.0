package com.f1.vortexcommon.msg.agent.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;

@VID("F1.VA.GBCFQ")
public interface VortexAgentGetBackupChangedFilesRequest extends VortexAgentRequest {

	@PID(10)
	public List<VortexAgentFile> getDestinationManifest();
	public void setDestinationManifest(List<VortexAgentFile> destinationManifest);

	@PID(12)
	public String getBackupPath();
	public void setBackupPath(String backupPath);

}
