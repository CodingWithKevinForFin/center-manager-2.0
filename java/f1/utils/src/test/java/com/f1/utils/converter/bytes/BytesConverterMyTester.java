package com.f1.utils.converter.bytes;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("TS.BA.C1")
public interface BytesConverterMyTester extends Message {
	@PID(1)
	public byte[] getData();

	public void setData(byte[] data);

	@PID(2)
	public int getId();

	public void setId(int id);

	@PID(3)
	public int getWidth();

	public void setWidth(int id);

	@PID(4)
	public int getHeight();

	public void setHeight(int id);
}
