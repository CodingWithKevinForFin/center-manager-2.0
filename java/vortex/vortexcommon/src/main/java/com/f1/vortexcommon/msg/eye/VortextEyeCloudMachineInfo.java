package com.f1.vortexcommon.msg.eye;

import com.f1.base.PID;

public interface VortextEyeCloudMachineInfo extends VortexEyeEntity {

	@PID(1)
	public void setCIId(long id);
	public long getCIId();

	@PID(2)
	public void setName(String name);
	public String getName();

	@PID(3)
	public void setInstanceId(String instanceId);
	public String getInstanceId();

	@PID(4)
	public void setCIName(String CIName);
	public String getCIName();

	@PID(5)
	public void setStatus(String Status);
	public String getStatus();

	@PID(6)
	public void setPrivateIP(String PrivateIP);
	public String getPrivateIP();

	@PID(7)
	public void setPublicIP(String PublicIP);
	public String getPublicIP();

	@PID(8)
	public void setAsOf(long AsOf);
	public long getAsOf();

	@PID(9)
	public void setInstanceType(String InstanceType);
	public String getInstanceType();

	@PID(10)
	public void setKeyName(String KeyName);
	public String getKeyName();

	@PID(11)
	public void setCreateTime(long CreateTime);
	public long getCreateTime();

	@PID(12)
	public void setOS(String OS);
	public String getOS();

	@PID(13)
	public void setLastOP(short op);
	public short getLastOP();

	@PID(14)
	public void setLastOPStatus(String status);
	public String getLastOPStatus();

}
