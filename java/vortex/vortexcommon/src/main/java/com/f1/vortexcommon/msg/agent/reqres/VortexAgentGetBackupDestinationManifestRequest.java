package com.f1.vortexcommon.msg.agent.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.GBDMQ")
public interface VortexAgentGetBackupDestinationManifestRequest extends VortexAgentRequest {

	@PID(10)
	public String getDestinationPath();
	public void setDestinationPath(String destinationPath);

	@PID(12)
	public String getBackupPath();
	public void setBackupPath(String backupPath);

	@PID(13)
	public void setSourceMuid(String muid);
	public String getSourceMuid();

	@PID(14)
	public void setSourceHostName(String muid);
	public String getSourceHostName();
}
