package com.f1.vortexcommon.msg.eye;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexMetadatable;

@VID("F1.VO.BKUP")
public interface VortexEyeBackup extends VortexEyeEntity, VortexMetadatable {

	long NO_DEPLOYMENT = -1;

	byte STATUS_NEVER_RUN = 0;
	byte STATUS_RUNNING = 2;
	byte STATUS_OKAY = 3;
	byte STATUS_PARTIAL_OKAY = 4;
	byte STATUS_FAILURE = 5;

	//only supplied if not associated w/ a deployment (mutually exclusive w/ deploymentId)
	@PID(11)
	public String getSourceMachineUid();
	public void setSourceMachineUid(String machineId);

	@PID(12)
	public String getSourcePath();
	public void setSourcePath(String targetDirectory);

	//NO_DEPLOYEMNT(-1) --> not associated with deployment, only machine
	@PID(13)
	public long getDeploymentId();
	public void setDeploymentId(long deploymentSetId);

	@PID(14)
	public byte getStatus();
	public void setStatus(byte status);

	@PID(15)
	public String getMessage();
	public void setMessage(String message);

	@PID(16)
	public long getBackupDestinationId();
	public void setBackupDestinationId(long backupDestination);

	@PID(17)
	public int getOptions();
	public void setOptions(int options);

	@PID(18)
	public String getInvokedBy();
	public void setInvokedBy(String currentBuildInvokedBy);

	@PID(19)
	public String getDescription();
	public void setDescription(String description);

	byte PID_IGNORE_EXPRESSION = 30;
	@PID(PID_IGNORE_EXPRESSION)
	public String getIgnoreExpression();
	public void setIgnoreExpression(String ignoreExpression);

	byte PID_FILE_COUNT = 31;
	@PID(PID_FILE_COUNT)
	public int getFileCount();
	public void setFileCount(int fileCount);

	byte PID_IGNORED_FILE_COUNT = 32;
	@PID(PID_IGNORED_FILE_COUNT)
	public int getIgnoredFileCount();
	public void setIgnoredFileCount(int ignoredFileCount);

	byte PID_BYTES_COUNT = 33;
	@PID(PID_BYTES_COUNT)
	public long getBytesCount();
	public void setBytesCount(long bytesCount);

	byte PID_LATEST_MODIFIED_TIME = 34;
	@PID(PID_LATEST_MODIFIED_TIME)
	public long getLatestModifiedTime();
	public void setLatestModifiedTime(long fileCount);

	byte PID_MANIFEST_LENGTH = 35;
	@PID(PID_MANIFEST_LENGTH)
	public int getManifestLength();
	public void setManifestLength(int manifestLength);

	byte PID_MANIFEST = 36;
	@PID(PID_MANIFEST)
	public String getManifest();
	public void setManifest(String manifest);

	byte PID_MANIFEST_VVID = 37;
	@PID(PID_MANIFEST_VVID)
	public long getManifestVvid();
	public void setManifestVvid(long manifestVvid);

	byte PID_MANIFEST_TIME = 38;
	@PID(PID_MANIFEST_TIME)
	public long getManifestTime();
	public void setManifestTime(long manifestTime);

	public VortexEyeBackup clone();
}
