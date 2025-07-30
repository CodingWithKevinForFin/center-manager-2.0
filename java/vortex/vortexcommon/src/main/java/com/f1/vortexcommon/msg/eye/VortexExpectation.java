package com.f1.vortexcommon.msg.eye;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexMetadatable;

@VID("F1.VO.EX")
public interface VortexExpectation extends VortexEyeEntity, VortexMetadatable {
	byte MASK_TYPE_NAME = 1;
	byte MASK_TYPE_USER = 2;
	byte MASK_TYPE_COMMAND = 3;
	byte MASK_TYPE_TYPE = 4;
	byte MASK_TYPE_STATE = 5;
	byte MASK_TYPE_FOREIGN_HOST = 6;
	byte MASK_TYPE_FOREIGN_PORT = 7;
	byte MASK_TYPE_LOCAL_HOST = 8;
	byte MASK_TYPE_LOCAL_PORT = 9;
	byte MASK_TYPE_SCHEDULE = 10;
	byte MASK_TYPE_ID = 11;

	@PID(1)
	public byte getTargetType();
	public void setTargetType(byte type);

	@PID(9)
	public String getMachineUid();
	public void setMachineUid(String machineId);

	@PID(2)
	public Map<Byte, String> getFieldMasks();
	public void setFieldMasks(Map<Byte, String> VortexFieldMasks);

	@PID(3)
	public Map<Byte, String> getTolerances();
	public void setTolerances(Map<Byte, String> tolerances);

	@PID(4)
	public Map<Byte, String> getTargetMetadata();
	public void setTargetMetadata(Map<Byte, String> metaData);

	@PID(8)
	public String getName();
	public void setName(String name);

	@Override
	VortexExpectation clone();

}
