package com.vortex.client;

import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;

public class VortexClientBackupFile extends VortexClientMachineEntity<VortexAgentBackupFile> {

	private String pathWithoutRoot;
	private VortexClientBackup backup;

	public VortexClientBackupFile(VortexAgentBackupFile data) {
		super(VortexAgentEntity.TYPE_BACKUP_FILE, data);
		update(data);
	}

	public void update(VortexAgentBackupFile data) {
		super.update(data);
	}

	public String getPathWithoutRoot(VortexClientManager manager) {
		if (pathWithoutRoot == null) {
			VortexClientBackup backup = getBackup(manager);//manager.getBackup(getData().getBackupId());
			if (backup == null)
				this.pathWithoutRoot = getData().getPath();
			else
				this.pathWithoutRoot = SH.stripPrefix(getData().getPath(), backup.getFullSourcePath(), false);
		}
		return pathWithoutRoot;
	}

	public VortexClientBackup getBackup(VortexClientManager manager) {
		if (backup == null)
			backup = manager.getBackup(getData().getBackupId());
		return backup;
	}

}
