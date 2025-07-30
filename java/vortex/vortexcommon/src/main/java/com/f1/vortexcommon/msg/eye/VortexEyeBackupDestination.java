package com.f1.vortexcommon.msg.eye;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexMetadatable;

@VID("F1.VO.BD")
public interface VortexEyeBackupDestination extends VortexEyeEntity, VortexMetadatable {

	@PID(1)
	public String getDestinationMachineUid();
	public void setDestinationMachineUid(String targetMachineUid);

	@PID(2)
	public String getDestinationPath();
	public void setDestinationPath(String path);

	@PID(3)
	public String getName();
	public void setName(String path);

	public VortexEyeBackupDestination clone();
}
