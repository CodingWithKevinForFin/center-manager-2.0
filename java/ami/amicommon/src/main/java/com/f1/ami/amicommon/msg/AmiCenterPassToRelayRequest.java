package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.PTAQ")
public interface AmiCenterPassToRelayRequest extends AmiCenterRequest {

	@PID(1)
	public void setAgentRequest(AmiRelayRequest agentRequest);
	public AmiRelayRequest getAgentRequest();

	@PID(4)
	public void setRelayMiid(long miid);
	public long getRelayMiid();

}
