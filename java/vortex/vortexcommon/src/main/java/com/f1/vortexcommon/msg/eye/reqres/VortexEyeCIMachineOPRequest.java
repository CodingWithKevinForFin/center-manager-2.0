package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.CIMOPQ")
public interface VortexEyeCIMachineOPRequest extends VortexEyeRequest {

	public static short OP_START = 1;
	public static short OP_STOP = 2;
	public static short OP_DUP = 3;
	public static short OP_DEPLOY = 4;
	public static short OP_TERMINATE = 5;
	public static short OP_START_AGENT = 6;

	@PID(1)
	public void setId(long id);
	public long getId();

	@PID(2)
	public void setOp(short op);
	public short getOp();

	@PID(3)
	public void setName(String name);
	public String getName();

	@PID(4)
	public void setNumberOfInstances(int n);
	public int getNumberOfInstances();

}
