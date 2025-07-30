package com.f1.vortexcommon.msg.eye;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexMetadatable;

@VID("F1.VO.BR")
public interface VortexBuildResult extends VortexEyeEntity, VortexMetadatable {

	byte STATE_INIT = 1;
	byte STATE_RUNNING = 2;
	byte STATE_TRANSFERRING = 3;
	byte STATE_SUCCCESS = 4;
	byte STATE_FAILURE = 5;
	byte STATE_ERROR = 6;
	byte STATE_FILE_NOT_FOUND = 7;

	@PID(11)
	public String getName();
	public void setName(String name);

	@PID(12)
	public String getVersion();
	public void setVersion(String name);

	@PID(13)
	public String getFile();
	public void setFile(String filename);

	@PID(14)
	public long getProcedureId();
	public void setProcedureId(long buildProcedureId);

	@PID(15)
	public int getProcedureRevision();
	public void setProcedureRevision(int buildProcedureId);

	@PID(16)
	public String getProcedureName();
	public void setProcedureName(String buildProcedureId);

	@PID(17)
	public String getBuildMachineUid();
	public void setBuildMachineUid(String buildMachineUid);

	@PID(18)
	public String getBuildUser();
	public void setBuildUser(String buildUser);

	@PID(19)
	public String getBuildCommand();
	public void setBuildCommand(String buildCommand);

	@PID(30)
	public String getBuildStdin();
	public void setBuildStdin(String stdout);

	@PID(31)
	public byte[] getBuildStdout();
	public void setBuildStdout(byte[] stderr);

	@PID(32)
	public byte[] getBuildStderr();
	public void setBuildStderr(byte[] stderr);

	@PID(33)
	public long getBuildStdoutLength();
	public void setBuildStdoutLength(long stderrLength);

	@PID(34)
	public long getBuildStderrLength();
	public void setBuildStderrLength(long stderrLength);

	@PID(35)
	public String getInvokedBy();
	public void setInvokedBy(String buildProcedureId);

	@PID(36)
	public byte[] getData();
	public void setData(byte[] buildOutput);

	@PID(37)
	public long getDataLength();
	public void setDataLength(long buildOutput);

	@PID(38)
	public long getStartTime();
	public void setStartTime(long buildProcedureId);

	@PID(39)
	public byte getState();
	public void setState(byte state);

	@PID(40)
	public Integer getBuildExitcode();
	public void setBuildExitcode(Integer exitcode);

	@PID(41)
	public long getDataChecksum();
	public void setDataChecksum(long buildOutput);

	@PID(42)
	public void setBuildVariables(Map<String, String> variables);
	public Map<String, String> getBuildVariables();

	@PID(43)
	public long getVerifyDataChecksum();
	public void setVerifyDataChecksum(long buildOutput);

	@PID(44)
	public byte[] getVerifyData();
	public void setVerifyData(byte[] buildOutput);

	@PID(45)
	public long getVerifyDataLength();
	public void setVerifyDataLength(long buildOutput);

	@PID(46)
	public String getVerifyFile();
	public void setVerifyFile(String filename);

	@PID(47)
	public long getVerifyDataVvid();
	public void setVerifyDataVvid(long verifyDataVvid);

	@PID(48)
	public long getDataVvid();
	public void setDataVvid(long dataVvid);

	@PID(49)
	public long getBuildStderrVvid();
	public void setBuildStderrVvid(long stdinVvid);

	@PID(50)
	public long getBuildStdoutVvid();
	public void setBuildStdoutVvid(long stdoutVvid);

	public VortexBuildResult clone();

}
