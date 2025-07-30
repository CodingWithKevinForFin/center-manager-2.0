package com.f1.povo.f1app.audit;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.ATLE")
public interface F1AppAuditTrailLoggerEvent extends F1AppAuditTrailEvent {

	byte PID_LOG_LEVEL = 10;
	byte PID_CLASS_NAME = 13;
	byte PID_FILE_NAME = 14;
	byte PID_LINE_NUMBER = 15;
	byte PID_METHOD_NAME = 16;

	@PID(PID_LOG_LEVEL)
	public int getLogLevel();
	public void setLogLevel(int level);

	@PID(PID_CLASS_NAME)
	public void setClassName(String className);
	public String getClassName();

	@PID(PID_FILE_NAME)
	public void setFileName(String fileName);
	public String getFileName();

	@PID(PID_LINE_NUMBER)
	public void setLineNumber(int lineNumber);
	public int getLineNumber();

	@PID(PID_METHOD_NAME)
	public void setMethodName(String methodName);
	public String getMethodName();
}
