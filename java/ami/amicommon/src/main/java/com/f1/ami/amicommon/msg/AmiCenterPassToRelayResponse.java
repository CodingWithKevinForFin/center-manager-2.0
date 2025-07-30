package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.PTAR")
public interface AmiCenterPassToRelayResponse extends AmiCenterResponse {

	@PID(1)
	public void setAgentResponse(AmiRelayResponse agentResponse);
	public AmiRelayResponse getAgentResponse();

}
