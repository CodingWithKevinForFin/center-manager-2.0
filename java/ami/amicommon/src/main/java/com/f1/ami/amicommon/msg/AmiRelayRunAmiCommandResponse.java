package com.f1.ami.amicommon.msg;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.RAMIR")
public interface AmiRelayRunAmiCommandResponse extends AmiRelayResponse {

	byte STATUS_DONT_CLOSE_DIALOG = 2;
	byte STATUS_UPDATE_RECORD = 3;
	byte STATUS_OKAY = 0;
	int STATUS_COMMAND_NOT_REGISTERED = -2;
	int STATUS_GENERAL_ERROR = -1;
	int STATUS_TIMEOUT = -3;

	@PID(2)
	public int getStatusCode();
	public void setStatusCode(int statusCode);

	@PID(3)
	public String getAmiMessage();
	public void setAmiMessage(String message);

	@PID(5)
	public int getConnectionId();
	public void setConnectionId(int connectionId);

	@PID(7)
	public String getAmiScript();
	public void setAmiScript(String connectionId);

	@PID(8)
	public Map<String, Object> getParams();
	public void setParams(Map<String, Object> params);

	@PID(9)
	public String getCommandUid();
	public void setCommandUid(String commandUid);
}
