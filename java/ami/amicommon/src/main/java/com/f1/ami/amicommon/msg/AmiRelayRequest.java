package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VA.Q")
public interface AmiRelayRequest extends PartialMessage {

	@PID(40)
	public String getTargetAgentProcessUid();
	public void setTargetAgentProcessUid(String processUid);

	@PID(41)
	public String getInvokedBy();
	public void setInvokedBy(String buildProcedureId);

	@PID(42)
	public byte getCenterId();
	public void setCenterId(byte buildProcedureId);
}
