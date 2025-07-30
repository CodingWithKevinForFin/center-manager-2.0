package com.f1.ami.amicommon.msg;

import java.util.List;
import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.RAMIQ")
public interface AmiRelayRunAmiCommandRequest extends AmiRelayRequest {

	@PID(1)
	public Map<String, Object> getArguments();
	public void setArguments(Map<String, Object> arguments);

	@PID(2)
	public String getCommandDefinitionId();
	public void setCommandDefinitionId(String command);

	@PID(4)
	public List<Map<String, Object>> getFields();
	public void setFields(List<Map<String, Object>> fields);

	@PID(5)
	public String getHostIp();
	public void setHostIp(String timeoutMs);

	@PID(6)
	public int getTimeoutMs();
	public void setTimeoutMs(int timeoutMs);

	@PID(7)
	public String getCommandUid();
	public void setCommandUid(String commandUid);

	@PID(8)
	public String[] getObjectTypes();
	public void setObjectTypes(String[] alertId);

	@PID(9)
	public String[] getObjectIds();
	public void setObjectIds(String[] objectIds);

	@PID(10)
	public int getRelayConnectionId();
	public void setRelayConnectionId(int timeoutMs);

	@PID(11)
	public void setCommandId(long id);
	public long getCommandId();

	@PID(12)
	public long[] getAmiObjectIds();
	public void setAmiObjectIds(long[] amiObjectId);

	@PID(13)
	public String getAppId();
	public void setAppId(String timeoutMs);

	@PID(14)
	public boolean getIsManySelect();
	public void setIsManySelect(boolean isMulti);

	@PID(15)
	public String getSessionId();
	public void setSessionId(String sessionId);
}
