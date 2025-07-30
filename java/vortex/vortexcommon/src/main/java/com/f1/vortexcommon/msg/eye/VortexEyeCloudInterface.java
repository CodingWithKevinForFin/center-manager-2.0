package com.f1.vortexcommon.msg.eye;

import java.util.Map;

import com.f1.base.PID;

public interface VortexEyeCloudInterface extends VortexEyeEntity {

	byte KEY_PUBLIC = 1;
	byte KEY_PRIVATE = 2;
	short VENDOR_AMAZON_AWS = 1;
	short VENDOR_RACKSPACE = 2;

	@PID(1)
	public String getUserName();
	public void setUserName(String userName);

	@PID(2)
	public byte[] getPassword();
	public void setPassword(byte password[]);

	@PID(3)
	public byte[] getKeyContents();
	public void setKeyContents(byte[] keyContents);

	@PID(4)
	public byte getKeyType();
	public void setKeyType(byte keyType);

	@PID(5)
	public short getCloudVendorType();
	public void setCloudVendorType(short type);

	@PID(6)
	public String getDescription();
	public void setDescription(String description);

	@PID(7)
	public Map<String, String> getParameters();
	public void setParameters(Map<String, String> description);

	@Override
	public VortexEyeCloudInterface clone();
}
