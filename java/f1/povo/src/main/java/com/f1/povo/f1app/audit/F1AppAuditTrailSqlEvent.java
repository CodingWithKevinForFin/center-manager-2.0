package com.f1.povo.f1app.audit;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.ATSE")
public interface F1AppAuditTrailSqlEvent extends F1AppAuditTrailEvent {

	byte PID_PARAMS = 11;

	@PID(PID_PARAMS)
	public List<String> getParams();
	public void setParams(List<String> message);

}
