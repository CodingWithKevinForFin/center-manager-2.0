package com.f1.ami.amicommon.msg;

import java.util.List;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.GRER")
public interface AmiCenterGetResourceResponse extends Message {

	@PID(1)
	public List<AmiCenterResource> getResources();
	public void setResources(List<AmiCenterResource> name);

}
