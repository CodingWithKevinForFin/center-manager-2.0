package com.f1.ami.center.replication;

import com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessageImpl;
import com.f1.base.Action;

public interface AmiCenterReplicationObjectsMessage extends Action {

	public void setHead(AmiCenterClientObjectMessageImpl head);
	public AmiCenterClientObjectMessageImpl getHead();
	public void setSchemaHead(AmiCenterClientObjectMessageImpl head);
	public AmiCenterClientObjectMessageImpl getSchemaHead();
	public void setCenterId(byte centerId);
	public byte getCenterId();
}
