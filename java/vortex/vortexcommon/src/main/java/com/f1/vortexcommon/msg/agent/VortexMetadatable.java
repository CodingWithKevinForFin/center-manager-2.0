package com.f1.vortexcommon.msg.agent;

import java.util.Map;

import com.f1.base.PID;
import com.f1.vortexcommon.msg.VortexEntity;

public interface VortexMetadatable extends VortexEntity {

	byte PID_METADATA = 75;
	@PID(PID_METADATA)
	public Map<String, String> getMetadata();
	public void setMetadata(Map<String, String> metaData);

}
