package com.f1.ami.amicommon.msg;

import com.f1.base.Lockable;
import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.AMI.DSC")
public interface AmiDatasourceColumn extends Message, Lockable {

	byte TYPE_UNKNOWN = -1;
	byte TYPE_NONE = 0;
	byte TYPE_BOOLEAN = AmiDataEntity.PARAM_TYPE_BOOLEAN;
	byte TYPE_INT = AmiDataEntity.PARAM_TYPE_INT4;
	byte TYPE_SHORT = AmiDataEntity.PARAM_TYPE_INT2;
	byte TYPE_BYTE = AmiDataEntity.PARAM_TYPE_INT1;
	byte TYPE_FLOAT = AmiDataEntity.PARAM_TYPE_FLOAT;
	byte TYPE_DOUBLE = AmiDataEntity.PARAM_TYPE_DOUBLE;
	byte TYPE_LONG = AmiDataEntity.PARAM_TYPE_LONG8;
	byte TYPE_STRING = AmiDataEntity.PARAM_TYPE_STRING;
	byte TYPE_BINARY = AmiDataEntity.PARAM_TYPE_BINARY;
	byte TYPE_UTC = AmiDataEntity.PARAM_TYPE_UTC6;
	byte TYPE_UTCN = AmiDataEntity.PARAM_TYPE_UTCN;
	byte TYPE_CHAR = AmiDataEntity.PARAM_TYPE_CHAR;
	byte TYPE_BIGINT = AmiDataEntity.PARAM_TYPE_BIGINT;
	byte TYPE_BIGDEC = AmiDataEntity.PARAM_TYPE_BIGDEC;
	byte TYPE_COMPLEX = AmiDataEntity.PARAM_TYPE_COMPLEX;
	byte TYPE_UUID = AmiDataEntity.PARAM_TYPE_UUID;

	byte HINT_JSON = 1;
	byte HINT_NONE = 0;

	@PID(1)
	public String getName();
	public void setName(String name);

	@PID(2)
	public byte getType();
	public void setType(byte type);

	@PID(3)
	public byte getHint();
	public void setHint(byte type);

	@Override
	public int hashCode();

}
