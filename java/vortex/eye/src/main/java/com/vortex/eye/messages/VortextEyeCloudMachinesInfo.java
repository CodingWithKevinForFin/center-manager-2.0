package com.vortex.eye.messages;

import java.util.List;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.vortexcommon.msg.eye.VortextEyeCloudMachineInfo;

public interface VortextEyeCloudMachinesInfo extends Message {

	@PID(1)
	public void setMachineInfoList(List<VortextEyeCloudMachineInfo> miList);
	public List<VortextEyeCloudMachineInfo> getMachineInfoList();
}
