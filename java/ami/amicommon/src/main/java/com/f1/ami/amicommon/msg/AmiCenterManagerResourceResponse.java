package com.f1.ami.amicommon.msg;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.MRER")
public interface AmiCenterManagerResourceResponse extends AmiCenterResponse {

	@PID(1)
	public List<AmiCenterResource> getResources();
	public void setResources(List<AmiCenterResource> name);

}
