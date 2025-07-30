package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexEyeMetadataField;

@VID("F1.VE.MDFQ")
public interface VortexEyeManageMetadataFieldRequest extends VortexEyeRequest {

	@PID(10)
	public VortexEyeMetadataField getMetadataField();
	public void setMetadataField(VortexEyeMetadataField metadataField);

	@PID(11)
	public void setDefaultValue(String value);
	public String getDefaultValue();
}
