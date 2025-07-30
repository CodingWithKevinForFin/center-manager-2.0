package com.vortex.agent.messages;

import java.util.Map;

import com.f1.base.Message;

public interface VortexAgentOsAdapterDeploymentRequest extends Message {

	public byte TYPE_DEPLOY = 1;
	public byte TYPE_VERIFY = 2;
	public byte TYPE_UNDEPLOY = 3;

	String getTargetDirectory();
	void setTargetDirectory(String targetDirectory);

	byte[] getData();
	void setData(byte data[]);

	String getInvokedBy();
	void setInvokedBy(String invokedBy);

	String getOwner();
	void setOwner(String targetOwner);

	byte getType();
	void setType(byte type);

	public Map<String, String> getPropertyFiles();
	public void setPropertyFiles(Map<String, String> propertyFiles);

	void setAutoDeleteFiles(String autoDeleteFiles);
	String getAutoDeleteFiles();

}
