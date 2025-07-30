package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.IAGQ")
public interface VortexEyeInstallAgentRequest extends VortexEyeRequest {

	@PID(12)
	public void setUsername(String userName);
	public String getUsername();

	@PID(13)
	public void setPassword(byte[] userName);
	public byte[] getPassword();

	@PID(14)
	public void setHostName(String userName);
	public String getHostName();

	@PID(15)
	public void setTargetPath(String target);
	public String getTargetPath();

	@PID(16)
	public String getAgentVersion();
	public void setAgentVersion(String agentVersion);

	@PID(17)
	public String getAgentInterface();
	public void setAgentInterface(String agentVersion);

	@PID(18)
	public void setPublicKeyData(byte[] userName);
	public byte[] getPublicKeyData();

	@PID(19)
	public String getDeployUid();
	public void setDeployUid(String uid);

	@PID(20)
	public int getPort();
	public void setPort(int port);

}
