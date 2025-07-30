package com.f1.povo.standard;

import com.f1.base.Lockable;
import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.ST.SEC")
public interface SecureMessage<M extends Message> extends Message, Lockable {

	@PID(1)
	public byte[] getPayload();
	public void setPayload(byte[] data);

	//name of system sending message
	@PID(2)
	public String getSender();
	public void setSender(String message);

	@PID(3)
	public long getNow();
	public void setNow(long message);

	@PID(4)
	public String getSignature();
	public void setSignature(String certificate);

	@PID(5)
	public String getEncodingMode();
	public void setEncodingMode(String mode);

}
