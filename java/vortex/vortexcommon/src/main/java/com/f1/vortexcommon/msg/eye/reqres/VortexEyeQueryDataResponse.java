package com.f1.vortexcommon.msg.eye.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.VortexEntity;

@VID("F1.VE.QDR")
public interface VortexEyeQueryDataResponse extends VortexEyeResponse {

	@PID(1)
	public List<VortexEntity> getData();
	public void setData(List<? extends VortexEntity> data);
}
