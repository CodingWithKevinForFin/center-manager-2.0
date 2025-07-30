package com.f1.vortexcommon.msg.eye;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexMetadatable;

@VID("F1.VO.BP")
public interface VortexBuildProcedure extends VortexEyeEntity, VortexMetadatable {

	@PID(10)
	public String getName();
	public void setName(String name);

	@PID(11)
	public String getBuildMachineUid();
	public void setBuildMachineUid(String machineId);

	@PID(12)
	public String getTemplateUser();
	public void setTemplateUser(String buildUser);

	@PID(13)
	public String getTemplateCommand();
	public void setTemplateCommand(String buildCommand);

	@PID(14)
	public String getTemplateStdin();
	public void setTemplateStdin(String stdin);

	@PID(15)
	public String getTemplateResultFile();
	public void setTemplateResultFile(String buildOutput);

	@PID(16)
	public String getTemplateResultName();
	public void setTemplateResultName(String buildResultName);

	@PID(17)
	public String getTemplateResultVersion();
	public void setTemplateResultVersion(String templateResulttVersion);

	@PID(18)
	public String getTemplateResultVerifyFile();
	public void setTemplateResultVerifyFile(String verifyResultFileName);

	public VortexBuildProcedure clone();

}
