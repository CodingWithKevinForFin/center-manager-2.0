package com.vortex.eye.cloud;

import java.util.List;

import com.f1.container.impl.AbstractContainerScope;
import com.f1.vortexcommon.msg.eye.VortexEyeCloudInterface;
import com.f1.vortexcommon.msg.eye.VortextEyeCloudMachineInfo;

public interface CloudAdapter {

	public List<String> getMachinesInCloud(VortexEyeCloudInterface ci) throws Exception;
	public List<VortextEyeCloudMachineInfo> getMachineInfoList(AbstractContainerScope scope, VortexEyeCloudInterface ci) throws Exception;
	public void stopMachine(VortextEyeCloudMachineInfo mi, VortexEyeCloudInterface ci) throws Exception;
	public void startMachine(VortextEyeCloudMachineInfo mi, VortexEyeCloudInterface ci) throws Exception;
	public void startMoreLikeThis(VortextEyeCloudMachineInfo mi, VortexEyeCloudInterface ci, String name, int numberOfInstances) throws Exception;
	public void terminateMachine(VortextEyeCloudMachineInfo mi, VortexEyeCloudInterface ci) throws Exception;
}
