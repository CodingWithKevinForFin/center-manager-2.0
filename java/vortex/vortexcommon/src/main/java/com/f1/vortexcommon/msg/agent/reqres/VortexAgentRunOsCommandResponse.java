package com.f1.vortexcommon.msg.agent.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.ROCR")
public interface VortexAgentRunOsCommandResponse extends VortexAgentResponse {

	@PID(1)
	public String getCommand();
	public void setCommand(String rule);

	@PID(2)
	public byte[] getStdin();
	public void setStdin(byte[] rule);

	@PID(3)
	public String getOwner();
	public void setOwner(String owner);

	@PID(31)
	public byte[] getStdout();
	public void setStdout(byte[] stderr);

	@PID(32)
	public byte[] getStderr();
	public void setStderr(byte[] stderr);

	@PID(33)
	public long getStdoutLength();
	public void setStdoutLength(long stderrLength);

	@PID(34)
	public long getStderrLength();
	public void setStderrLength(long stderrLength);

	@PID(38)
	public long getStartTime();
	public void setStartTime(long buildProcedureId);

	@PID(39)
	public long getEndTime();
	public void setEndTime(long buildProcedureId);

	@PID(40)
	public Integer getExitcode();
	public void setExitcode(Integer exitcode);

	public VortexAgentRunOsCommandResponse clone();
}
