package com.f1.povo.f1app.reqres;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.FA.Q")
public interface F1AppRequest extends PartialMessage {

	@PID(3)
	public String getInvokedBy();
	public void setInvokedBy(String buildProcedureId);

	@PID(4)
	public String getTargetF1AppProcessUid();
	public void setTargetF1AppProcessUid(String processUid);
}
