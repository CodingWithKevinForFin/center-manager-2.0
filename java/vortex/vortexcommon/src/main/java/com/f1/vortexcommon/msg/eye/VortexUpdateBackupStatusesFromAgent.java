package com.f1.vortexcommon.msg.eye;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;

@VID("F1.VE.VUBSFA")
public interface VortexUpdateBackupStatusesFromAgent extends PartialMessage {

	@PID(1)
	public List<VortexAgentBackupFile> getAddedFiles();
	public void setAddedFiles(List<VortexAgentBackupFile> files);

	@PID(2)
	public List<VortexAgentBackupFile> getUpdatedFiles();
	public void setUpdatedFiles(List<VortexAgentBackupFile> files);

	@PID(3)
	public List<VortexAgentBackupFile> getRemovedFiles();
	public void setRemovedFiles(List<VortexAgentBackupFile> files);

	@PID(4)
	public List<VortexEyeBackup> getUpdated();
	public void setUpdated(List<VortexEyeBackup> backups);
}
