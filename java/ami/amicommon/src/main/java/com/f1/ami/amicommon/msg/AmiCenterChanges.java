package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.CH")
public interface AmiCenterChanges extends AmiCenterChangesMessage {

	@PID(21)
	public String getEyeProcessUid();
	public void setEyeProcessUid(String agentProcessUid);

	@PID(20)
	public long getSeqNum();
	public void setSeqNum(long seqNum);

	@PID(24)
	public byte[] getAmiEntitiesAdded();
	public void setAmiEntitiesAdded(byte[] entityAdds);

	@PID(25)
	public void setAmiEntitiesUpdated(byte[] updated);
	public byte[] getAmiEntitiesUpdated();

	@PID(26)
	public void setAmiEntitiesRemoved(byte[] removed);
	public byte[] getAmiEntitiesRemoved();

	@PID(28)
	public void setAmiValuesStringPoolMap(byte[] value);
	public byte[] getAmiValuesStringPoolMap();

	@PID(29)
	public void setResponseNum(int responseInt);
	public int getResponseNum();

}
