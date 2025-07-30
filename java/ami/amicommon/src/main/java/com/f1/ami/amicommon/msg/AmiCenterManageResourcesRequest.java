package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.MREQ")
public interface AmiCenterManageResourcesRequest extends AmiCenterRequest {

	//f resource is null, returns list of all resource, if data is null, resource is deleted.
	@PID(1)
	public AmiCenterResource getResource();
	public void setResource(AmiCenterResource resource);

}
