package com.f1.vortexcommon.msg.agent.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;

@VID("F1.VA.SBFDQ")
public interface VortexAgentSendBackupFilesToDestinationRequest extends VortexAgentRequest {

	@PID(10)
	public List<VortexAgentFile> getFiles();
	public void setFiles(List<VortexAgentFile> destinationManifest);

	@PID(11)
	public String getBackupPath();
	public void setBackupPath(String backupPath);

	@PID(12)
	public String getDestinationPath();
	public void setDestinationPath(String destinationPath);

	@PID(13)
	public void setSourceMuid(String muid);
	public String getSourceMuid();

	@PID(14)
	public void setSourceHostName(String muid);
	public String getSourceHostName();

	@PID(15)
	public boolean getIsComplete();
	public void setIsComplete(boolean isComplete);
}
