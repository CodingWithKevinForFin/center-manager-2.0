package com.vortex.eye.messages;

import java.util.Map;
import java.util.Set;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;

@VID("F1.VE.VVSQ")
public interface VortexVaultRequest extends VortexEyeRequest {

	@PID(20)
	public Map<Long, byte[]> getDataToStore();
	public void setDataToStore(Map<Long, byte[]> data);

	@PID(21)
	public Set<Long> getVvidsToRetrieve();
	public void setVvidsToRetrieve(Set<Long> ids);

}
