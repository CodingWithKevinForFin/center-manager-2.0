package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexEyeMetadataField;

@VID("F1.VE.MDFR")
public interface VortexEyeManageMetadataFieldResponse extends VortexEyeResponse {

	@PID(10)
	public VortexEyeMetadataField getMetadataField();
	public void setMetadataField(VortexEyeMetadataField metadataField);

}
