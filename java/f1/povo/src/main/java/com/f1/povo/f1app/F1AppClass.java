package com.f1.povo.f1app;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.C")
public interface F1AppClass extends F1AppEntity {

	byte PID_CLASS_NAME = 1;
	@PID(PID_CLASS_NAME)
	public String getClassName();
	public void setClassName(String className);

	@PID(2)
	public void setClassInstanceId(long classInstanceId);
	public long getClassInstanceId();

}
