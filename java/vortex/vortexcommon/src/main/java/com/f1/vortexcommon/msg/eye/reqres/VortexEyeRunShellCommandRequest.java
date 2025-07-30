package com.f1.vortexcommon.msg.eye.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.RSCQ")
public interface VortexEyeRunShellCommandRequest extends VortexEyeRequest {

	@PID(10)
	public void setCommands(List<String> commands);
	public List<String> getCommands();

	@PID(11)
	public void setStdins(List<byte[]> commands);
	public List<byte[]> getStdins();

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
	public int getTimeoutMs();
	public void setTimeoutMs(int timeoutMs);

	@PID(16)
	public void setPublicKeyData(byte[] userName);
	public byte[] getPublicKeyData();

	@PID(17)
	public void setUseTTY(boolean b);
	public boolean getUseTTY();

	@PID(18)
	public void setPort(int port);
	public int getPort();

}
