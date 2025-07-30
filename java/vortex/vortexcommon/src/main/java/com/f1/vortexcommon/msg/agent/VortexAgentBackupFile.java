package com.f1.vortexcommon.msg.agent;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.ABF")
public interface VortexAgentBackupFile extends VortexAgentFile {

	byte STATUS_NONE = 0;
	byte STATUS_UNSTABLE = 1;
	byte STATUS_JUST_UPDATED = 2;
	byte STATUS_OFFLINE = 3;

	byte PID_BACKUP_ID = 31;
	@PID(PID_BACKUP_ID)
	public long getBackupId();
	public void setBackupId(long backupId);

	@Override
	VortexAgentBackupFile clone();

	@PID(32)
	public long getDataVvid();
	public void setDataVvid(long dataVvid);

	byte PID_STATUS = 33;
	@PID(PID_STATUS)
	public byte getStatus();
	public void setStatus(byte status);
}
