package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.RRDBQ")
public interface AmiRelayRunDbResponse extends AmiRelayResponse {

	@PID(2)
	public AmiCenterResponse getClientResponse();
	public void setClientResponse(AmiCenterResponse response);
}
