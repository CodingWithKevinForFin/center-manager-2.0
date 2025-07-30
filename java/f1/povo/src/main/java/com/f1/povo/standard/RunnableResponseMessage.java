package com.f1.povo.standard;

import com.f1.base.PID;
import com.f1.base.Prioritized;
import com.f1.base.VID;

@VID("F1.ST.RR")
public interface RunnableResponseMessage extends TextMessage, Prioritized {

	static byte RESULT_CODE_COMPLETE = 1;
	static byte RESULT_CODE_TIMEOUT = 2;
	static byte RESULT_CODE_THROWN = 3;

	@PID(3)
	public byte getResultCode();
	public void setResultCode(byte resultCode);

	@PID(4)
	public Throwable getThrowable();
	public void setThrowable(Throwable throwable);

	@PID(5)
	public int getPriority();
	public void setPriority(int priority);
}
