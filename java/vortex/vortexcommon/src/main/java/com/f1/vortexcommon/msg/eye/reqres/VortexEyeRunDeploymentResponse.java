package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.RDR")
public interface VortexEyeRunDeploymentResponse extends VortexEyeResponse {

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
