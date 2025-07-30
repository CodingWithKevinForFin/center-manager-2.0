package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.GSQ")
public interface AmiRelayGetSnapshotRequest extends AmiRelayRequest {

	@PID(1)
	void setLastSeqnumReceivedByCenter(long seqnum);
	long getLastSeqnumReceivedByCenter();

	@PID(2)
	public byte getCenterId();
	public void setCenterId(byte centerId);

}
