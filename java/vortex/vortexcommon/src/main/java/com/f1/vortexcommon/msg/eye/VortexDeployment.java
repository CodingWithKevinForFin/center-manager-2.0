package com.f1.vortexcommon.msg.eye;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexMetadatable;

@VID("F1.VO.DP")
public interface VortexDeployment extends VortexEyeEntity, VortexMetadatable {

	int MASK_ACTIONS = 0x000000FF; //0-7 bits: user actions
	int STATUS_ACTION_INSTALLING____ = 0x00000001;
	int STATUS_ACTION_STOPPING______ = 0x00000002;
	int STATUS_ACTION_STARTING______ = 0x00000004;
	int STATUS_ACTION_RUNNING_SCRIPT = 0x00000008;
	int STATUS_ACTION_UNINSTALLING__ = 0x00000010;
	int STATUS_ACTION_RETRIEVING____ = 0x00000020;
	int STATUS_ACTION_VERIFYING_____ = 0x00000040;

	int MASK_PROCESS = 0x0000FF00; //8-15 bits: process status
	int STATUS_PROCESS_PID_FOUND____ = 0x00000100;
	int STATUS_PROCESS_PUID_MATCH___ = 0x00000200;
	int STATUS_PROCESS_AGENT_DOWN___ = 0x00000400;
	int STATUS_PROCESS_BAD_DIID_____ = 0x00000800;

	int MASK_DEPLOY = 0x00FF0000; //16-23 bits: deployment status
	int STATUS_DEPLOY_NO_MANIFEST___ = 0x00010000;
	int STATUS_DEPLOY_BAD_ROOT_PATH_ = 0x00020000;
	int STATUS_DEPLOY_GENERAL_ERROR_ = 0x00040000;
	int STATUS_DEPLOY_SCRIPTS_BAD___ = 0x00080000;
	int STATUS_DEPLOY_FILE_MISMATCH_ = 0x00100000;
	int STATUS_DEPLOY_MANIFEST_BAD__ = 0x00200000;
	int STATUS_DEPLOY_NOT_FOUND_____ = 0x00400000;

	int MASK_RESERVED = 0xFF000000; //24-31 bits: reserved

	int OPTION_AGENT__ = 0x00000001;
	int OPTION_EYE____ = 0x00000002;
	int OPTION_GLASS__ = 0x00000004;
	int OPTION_3FORGE_ = 0x00000008;

	byte PID_PROCEDURE_ID = 10;
	@PID(PID_PROCEDURE_ID)
	public long getProcedureId();
	public void setProcedureId(long buildId);

	@PID(11)
	public String getTargetMachineUid();
	public void setTargetMachineUid(String machineId);

	byte PID_CURRENT_BUILD_RESULT_ID = 12;
	@PID(PID_CURRENT_BUILD_RESULT_ID)
	public Long getCurrentBuildResultId();
	public void setCurrentBuildResultId(Long currentBuildResultId);

	byte PID_DEPLOYMENT_SET_ID = 13;
	@PID(PID_DEPLOYMENT_SET_ID)
	public long getDeploymentSetId();
	public void setDeploymentSetId(long deploymentSetId);

	@PID(14)
	public String getTargetDirectory();
	public void setTargetDirectory(String targetDirectory);

	@PID(15)
	public String getTargetUser();
	public void setTargetUser(String targetUser);

	byte PID_CURRENT_BUILD_INVOKED_BY = 16;
	@PID(PID_CURRENT_BUILD_INVOKED_BY)
	public String getCurrentBuildInvokedBy();
	public void setCurrentBuildInvokedBy(String currentBuildInvokedBy);

	@PID(17)
	public String getGeneratedPropertiesFile();
	public void setGeneratedPropertiesFile(String generatedPropertiesFile);

	@PID(18)
	public String getStartScriptFile();
	public void setStartScriptFile(String startScript);

	@PID(19)
	public String getStopScriptFile();
	public void setStopScriptFile(String stopScript);

	@PID(40)
	public String getRollbackDirectory();
	public void setRollbackDirectory(String stopScript);

	@PID(41)
	public int getRollbackCount();
	public void setRollbackCount(int stopScript);

	byte PID_STATUS = 42;
	@PID(PID_STATUS)
	public int getStatus();
	public void setStatus(int status);

	byte PID_RUNNING_PID = 44;
	@PID(PID_RUNNING_PID)
	public Integer getRunningPid();
	public void setRunningPid(Integer pid);

	byte PID_RUNNING_PROCESS_UID = 45;
	@PID(PID_RUNNING_PROCESS_UID)
	public String getRunningProcessUid();
	public void setRunningProcessUid(String pid);

	byte PID_MESSAGE = 46;
	@PID(PID_MESSAGE)
	public String getMessage();
	public void setMessage(String message);

	@Deprecated
	@PID(47)
	public String getProperties();
	@Deprecated
	public void setProperties(String properties);

	byte PID_DEPLOYED_INSTANCE_ID = 48;
	@PID(PID_DEPLOYED_INSTANCE_ID)
	public Long getDeployedInstanceId();
	public void setDeployedInstanceId(Long diid);

	@PID(49)
	public String getScriptsDirectory();
	public void setScriptsDirectory(String startScript);

	@PID(50)
	public String getScriptsFound();
	public void setScriptsFound(String stopScript);

	@PID(51)
	public String getDescription();
	public void setDescription(String description);

	@PID(52)
	public int getOptions();
	public void setOptions(int description);

	@PID(53)
	public String getInstallScriptFile();
	public void setInstallScriptFile(String startScript);

	@PID(54)
	public String getUninstallScriptFile();
	public void setUninstallScriptFile(String stopScript);

	@PID(55)
	public String getVerifyScriptFile();
	public void setVerifyScriptFile(String verifyScriptCommand);

	@PID(56)
	public Map<String, String> getGeneratedFiles();
	public void setGeneratedFiles(Map<String, String> generatedFiles);

	@PID(57)
	public String getEnvVars();
	public void setEnvVars(String envVars);

	//comma delimited
	@PID(58)
	public String getAutoDeleteFiles();
	public void setAutoDeleteFiles(String startScript);

	//comma delimited
	@PID(59)
	public String getLogDirectories();
	public void setLogDirectories(String logDirectories);

	public VortexDeployment clone();

}
