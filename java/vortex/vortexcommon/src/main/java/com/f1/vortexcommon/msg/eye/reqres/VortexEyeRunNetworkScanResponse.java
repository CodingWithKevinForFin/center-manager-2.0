package com.f1.vortexcommon.msg.eye.reqres;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.VortexEyeNetworkScan;

@VID("F1.VE.RNSR")
public interface VortexEyeRunNetworkScanResponse extends VortexEyeResponse {

	@PID(1)
	public List<VortexEyeNetworkScan> getResults();
	public void setResults(List<VortexEyeNetworkScan> results);
}
