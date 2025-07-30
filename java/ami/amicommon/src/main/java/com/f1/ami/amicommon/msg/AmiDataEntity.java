package com.f1.ami.amicommon.msg;

//TODO: rename to AmiDataConsts

public interface AmiDataEntity {

	public byte MASK_CREATED_ON = 1;
	public byte MASK_MODIFIED_ON = 2;
	public byte MASK_REVISION = 4;
	public byte MASK_EXPIRES_IN_MILLIS = 8;
	public byte MASK_APPLICATION_ID = 16;
	public byte MASK_OBJECT_ID = 32;
	public byte MASK_PARAMS = 64;

	long MAX_UTC_VALUE = (1L << 47) - 1;

	byte PARAM_TYPE_NULL = 1;
	byte PARAM_TYPE_BOOLEAN = 2;
	byte PARAM_TYPE_FLOAT = 5;
	byte PARAM_TYPE_DOUBLE = 6;
	byte PARAM_TYPE_STRING = 7;
	byte PARAM_TYPE_ASCII = 8;
	byte PARAM_TYPE_ASCII_SMALL = 9;
	byte PARAM_TYPE_ASCII_ENUM = 25;

	byte PARAM_TYPE_INT1 = 10;
	byte PARAM_TYPE_INT2 = 11;
	byte PARAM_TYPE_INT3 = 12;
	byte PARAM_TYPE_INT4 = 13;

	byte PARAM_TYPE_LONG1 = 14;
	byte PARAM_TYPE_LONG2 = 15;
	byte PARAM_TYPE_LONG3 = 16;
	byte PARAM_TYPE_LONG4 = 17;
	byte PARAM_TYPE_LONG5 = 18;
	byte PARAM_TYPE_LONG6 = 19;
	byte PARAM_TYPE_LONG7 = 20;
	byte PARAM_TYPE_LONG8 = 21;

	byte PARAM_TYPE_ENUM1 = 22;
	byte PARAM_TYPE_ENUM2 = 23;
	byte PARAM_TYPE_ENUM3 = 24;
	byte PARAM_TYPE_UTC6 = 30;//6 bytes
	byte PARAM_TYPE_UTCN = 31;//8 bytes
	byte PARAM_TYPE_CHAR = 26;

	byte PARAM_TYPE_BINARY = 40;
	byte PARAM_TYPE_BIGINT = 41;
	byte PARAM_TYPE_BIGDEC = 42;
	byte PARAM_TYPE_COMPLEX = 43;
	byte PARAM_TYPE_UUID = 44;

	byte PARAM_TYPE_RESERVED = 127;

}
