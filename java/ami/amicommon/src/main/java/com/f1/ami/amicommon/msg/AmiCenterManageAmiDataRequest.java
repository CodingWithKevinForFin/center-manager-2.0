package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.MAMDQ")
public interface AmiCenterManageAmiDataRequest extends AmiCenterRequest {

	byte ACTION_DELETE_ALL = 1;

	@PID(1)
	public byte getAction();
	public void setAction(byte alert);

}
