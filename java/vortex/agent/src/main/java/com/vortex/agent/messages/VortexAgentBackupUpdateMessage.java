package com.vortex.agent.messages;

import java.util.List;

import com.f1.base.Message;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;

public interface VortexAgentBackupUpdateMessage extends Message {

	public VortexEyeBackup getBackup();
	public void setBackup(VortexEyeBackup backup);

	public String getPartitionId();
	public void setPartitionId(String partitionId);

	public void setFiles(List<VortexAgentBackupFile> backupFiles);
	public List<VortexAgentBackupFile> getFiles();

}
