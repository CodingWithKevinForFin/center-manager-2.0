package com.f1.ami.amicommon.msg;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.CBA")
public interface AmiRelayAckMessage extends Message {

	@PID(9)
	public String getAgentProcessUid();
	public void setAgentProcessUid(String agentProcessUid);

	@PID(8)
	public long getSeqNum();
	public void setSeqNum(long seqNum);

	@PID(7)
	public byte getCenterId();
	public void setCenterId(byte seqNum);
}
