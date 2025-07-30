package com.f1.vortexcommon.msg.agent.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;

@VID("F1.VA.RDQ")
public interface VortexAgentRunDeploymentResponse extends VortexAgentResponse {

	@PID(1)
	void setFiles(List<VortexAgentFile> files);
	List<VortexAgentFile> getFiles();

	@PID(2)
	public int getVerifyExitCode();
	public void setVerifyExitCode(int exitCode);

	@PID(3)
	public byte[] getVerifyStderr();
	public void setVerifyStderr(byte[] stdin);

	@PID(4)
	public byte[] getVerifyStdout();
	public void setVerifyStdout(byte[] stdout);

	@PID(5)
	public int getInstallExitCode();
	public void setInstallExitCode(int exitCode);

	@PID(6)
	public byte[] getInstallStderr();
	public void setInstallStderr(byte[] stdin);

	@PID(7)
	public byte[] getInstallStdout();
	public void setInstallStdout(byte[] stdout);

	@PID(8)
	public int getUninstallExitCode();
	public void setUninstallExitCode(int exitCode);

	@PID(9)
	public byte[] getUninstallStderr();
	public void setUninstallStderr(byte[] stdin);

	@PID(10)
	public byte[] getUninstallStdout();
	public void setUninstallStdout(byte[] stdout);
}
