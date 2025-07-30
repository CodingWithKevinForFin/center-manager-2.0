package com.f1.vortexcommon.msg.agent.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;

@VID("F1.VA.UBUQ")
public interface VortexAgentUpdateBackupRequest extends VortexAgentRequest {

	@PID(1)
	public List<VortexEyeBackup> getUpdated();
	public void setUpdated(List<VortexEyeBackup> updated);

	@PID(3)
	public long[] getRemoved();
	public void setRemoved(long[] removed);

	@PID(4)
	public boolean getIsSnapshot();
	public void setIsSnapshot(boolean removed);

	@PID(5)
	public void setFiles(List<VortexAgentBackupFile> backupFiles);
	public List<VortexAgentBackupFile> getFiles();

}
