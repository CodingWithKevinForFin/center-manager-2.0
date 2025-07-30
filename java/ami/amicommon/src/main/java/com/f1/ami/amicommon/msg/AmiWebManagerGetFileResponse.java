package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.GFR")
public interface AmiWebManagerGetFileResponse extends AmiCenterResponse {

	byte STATUS_NO_CHANGE = 2;
	byte STATUS_OKAY = 1;

	@PID(1)
	AmiFileMessage getFile();
	public void setFile(AmiFileMessage file);

	@PID(2)
	public byte getStatus();
	public void setStatus(byte status);

}
