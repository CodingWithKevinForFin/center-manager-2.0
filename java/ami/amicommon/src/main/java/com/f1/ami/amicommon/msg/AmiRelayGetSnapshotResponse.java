package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.GSR")
public interface AmiRelayGetSnapshotResponse extends AmiRelayResponse {

	@PID(1)
	public void setSnapshot(AmiRelayChangesMessage changes);
	public AmiRelayChangesMessage getSnapshot();

	@PID(7)
	public String getProcessUid();
	public void setProcessUid(String processUid);
}
