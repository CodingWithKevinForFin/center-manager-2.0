package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.PFR")
public interface AmiWebManagerPutFileResponse extends AmiCenterResponse {

	@PID(1)
	AmiFileMessage getFile();
	public void setFile(AmiFileMessage userName);

	@PID(2)
	void setReturnFlag(Boolean b);
	Boolean getReturnFlag();

}
