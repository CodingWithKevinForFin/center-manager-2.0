package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.SR")
public interface AmiCenterGetSnapshotResponse extends AmiCenterResponse {

	@PID(1)
	public AmiCenterChanges getSnapshot();
	public void setSnapshot(AmiCenterChanges changes);

	@PID(4)
	public String getProcessUid();
	public void setProcessUid(String processUid);

}
