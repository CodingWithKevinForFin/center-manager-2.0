package com.f1.vortexcommon.msg.eye.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.RSCR")
public interface VortexEyeRunShellCommandResponse extends VortexEyeResponse {

	int EXIT_CODE_AUTH_FAILED = -2;
	int EXIT_CODE_GENERAL_ERROR = -1;
	int EXIT_CODE_UNKNOWN_HOST = -3;

	@PID(10)
	public void setExitCodes(int[] commands);
	public int[] getExitCodes();

	@PID(11)
	public void setStdouts(List<byte[]> stdouts);
	public List<byte[]> getStdouts();

	@PID(12)
	public void setStderrs(List<byte[]> stderrs);
	public List<byte[]> getStderrs();

}
