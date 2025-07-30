package com.f1.ami.center.replication;

import com.f1.base.Action;

public interface AmiCenterReplicationConnectedMessage extends Action {

	public void setCenterId(byte centerId);
	public byte getCenterId();

}
