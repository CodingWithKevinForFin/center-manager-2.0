package com.f1.ami.amicommon.msg;

import java.util.List;
import java.util.Map;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.CBC")
public interface AmiRelayChangesMessage extends Message {

	@PID(9)
	public String getAgentProcessUid();
	public void setAgentProcessUid(String agentProcessUid);

	@PID(8)
	public long getSeqNum();
	public void setSeqNum(long seqNum);

	@PID(10)
	public void setAmiEvents(List<AmiRelayMessage> action);
	public List<AmiRelayMessage> getAmiEvents();

	@PID(11)
	public void setAmiStringPoolMap(Map<Short, String> map);
	public Map<Short, String> getAmiStringPoolMap();

}
