package com.vortex.client;

import com.f1.utils.OH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongKeyMapSource;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeBackupDestination;

public class VortexClientBackupDestination extends VortexClientEntity<VortexEyeBackupDestination> {

	final private LongKeyMap<VortexClientBackup> backups = new LongKeyMap<VortexClientBackup>();

	public VortexClientBackupDestination(VortexEyeBackupDestination data) {
		super(VortexAgentEntity.TYPE_BACKUP_DESTINATION, data);
		update(data);
	}

	public void addBackup(VortexClientBackup backup) {
		OH.assertEq(backup.getData().getBackupDestinationId(), getId());
		backups.put(backup.getId(), backup);
	}
	public VortexClientBackup removeBackup(long backupId) {
		return backups.remove(backupId);
	}

	public LongKeyMapSource<VortexClientBackup> getBackups() {
		return backups;
	}

	public void removeBackups() {
		this.backups.clear();
	}

	public String getDescription() {
		return getData().getName() + " [" + getHostName() + ":" + getData().getDestinationPath() + "]";
	}

}
