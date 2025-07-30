package com.f1.vortexcommon.msg.agent.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.ROCQ")
public interface VortexAgentRunOsCommandRequest extends VortexAgentRequest {

	@PID(1)
	public String getCommand();
	public void setCommand(String ommandrule);

	@PID(2)
	public byte[] getStdin();
	public void setStdin(byte[] stdin);

	@PID(3)
	public String getOwner();
	public void setOwner(String owner);

	@PID(6)
	public long getMaxRuntimeMs();
	public void setMaxRuntimeMs(long maxRuntime);

	@PID(7)
	public int getMaxCaptureStderr();
	public void setMaxCaptureStderr(int maxCaptureStderr);

	@PID(8)
	public int getMaxCaptureStdout();
	public void setMaxCaptureStdout(int maxCaptureStdout);

	@PID(9)
	public String getPwd();
	public void setPwd(String pwd);

	@PID(10)
	public void setEnvVars(String[] envVars);
	public String[] getEnvVars();

	//@PID(10)
	//public long getJobId();
	//public void setJobId(long jobId);
}
