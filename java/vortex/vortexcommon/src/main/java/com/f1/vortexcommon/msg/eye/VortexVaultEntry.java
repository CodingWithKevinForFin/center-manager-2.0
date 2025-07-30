package com.f1.vortexcommon.msg.eye;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VO.VVLE")
public interface VortexVaultEntry extends VortexEyeEntity, Lockable {

	@PID(30)
	public byte[] getData();
	public void setData(byte[] data);

	@PID(31)
	public long getChecksum();
	public void setChecksum(long checksum);

	@PID(32)
	public long getSoftlinkVvid();
	public void setSoftlinkVvid(long id);

	@PID(33)
	public int getDataLength();
	public void setDataLength(int length);

}
