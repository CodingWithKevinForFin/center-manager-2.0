package com.f1.povo.f1app.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.SSQ")
public interface F1AppSnapshotRequest extends F1AppRequest {

	@PID(2)
	public String getAgentProcessUid();
	public void setAgentProcessUid(String processUid);
}
