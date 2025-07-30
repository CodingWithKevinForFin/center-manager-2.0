package com.f1.ami.amicommon.msg;

import com.f1.base.Lockable;
import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.RES")
public interface AmiCenterResource extends Message, Lockable {
	int NO_SIZE = -1;

	@PID(1)
	public String getPath();
	public void setPath(String name);

	@PID(2)
	public long getSize();
	public void setSize(long size);

	@PID(3)
	public byte[] getData();
	public void setData(byte[] data);

	@PID(4)
	public long getChecksum();
	public void setChecksum(long data);

	@PID(5)
	public long getModifiedOn();
	public void setModifiedOn(long data);

	@PID(6)
	public void setImageWidth(int width);
	public int getImageWidth();

	@PID(7)
	public void setImageHeight(int height);
	public int getImageHeight();
}
