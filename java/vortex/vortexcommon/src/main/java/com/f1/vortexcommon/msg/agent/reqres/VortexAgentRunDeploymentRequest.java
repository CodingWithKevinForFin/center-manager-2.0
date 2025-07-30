package com.f1.vortexcommon.msg.agent.reqres;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.RDR")
public interface VortexAgentRunDeploymentRequest extends VortexAgentRequest {

	byte TYPE_DEPLOY = 3;
	byte TYPE_START_SCRIPT = 4;
	byte TYPE_STOP_SCRIPT = 5;
	//byte TYPE_GET_FILE_STRUCTURE = 6;
	byte TYPE_RUN_SCRIPT = 7;
	//byte TYPE_GET_FILE = 8;
	byte TYPE_DELETE_ALL_FILES = 9;
	byte TYPE_VERIFY = 10;
	byte TYPE_DEPLOY_CONFIG = 11;

	byte PID_DATA = 2;
	byte PID_JOB_ID = 10;
	byte PID_DATA_FILE_NAME = 11;
	byte PID_PROPERTIES = 12;
	byte PID_GENERATED_PROPERTIES_FILE = 17;
	byte PID_DEPLOYMENT_ID = 21;
	byte PID_DEPLOYED_INSTANCE_ID = 24;
	byte PID_BUILD_RESULT_ID = 22;
	byte PID_COMMAND_TYPE = 23;
	byte PID_TARGET_FILE = 34;
	byte PID_VERIFY_DATA = 35;
	byte PID_VERIFY_DATA_FILE_NAME = 36;
	byte PID_PROPERTY_FILES = 37;

	@PID(PID_DATA)
	public byte[] getData();
	public void setData(byte[] stdin);

	@PID(PID_JOB_ID)
	public long getJobId();
	public void setJobId(long jobId);

	@PID(PID_DATA_FILE_NAME)
	public void setDataFileName(String filename);
	public String getDataFileName();

	@Deprecated
	@PID(PID_PROPERTIES)
	public void setProperties(String properties);
	@Deprecated
	public String getProperties();

	@Deprecated
	@PID(PID_GENERATED_PROPERTIES_FILE)
	public String getGeneratedPropertiesFile();
	@Deprecated
	public void setGeneratedPropertiesFile(String generatedPropertiesFile);

	@PID(PID_DEPLOYMENT_ID)
	public void setDeploymentId(long id);
	public long getDeploymentId();

	@PID(PID_DEPLOYED_INSTANCE_ID)
	public void setDeployedInstanceId(long id);
	public long getDeployedInstanceId();

	@PID(PID_BUILD_RESULT_ID)
	public long getBuildResultId();
	public void setBuildResultId(long buildResultId);

	@PID(PID_COMMAND_TYPE)
	public byte getCommandType();
	public void setCommandType(byte commandType);

	@PID(PID_TARGET_FILE)
	public void setTargetFile(String script);
	public String getTargetFile();

	@PID(PID_VERIFY_DATA)
	public byte[] getVerifyData();
	public void setVerifyData(byte[] stdin);

	@PID(PID_VERIFY_DATA_FILE_NAME)
	public void setVerifyDataFileName(String verifyFilename);
	public String getVerifyDataFileName();

	@PID(PID_PROPERTY_FILES)
	public Map<String, String> getPropertyFiles();
	public void setPropertyFiles(Map<String, String> propertyFiles);
}
