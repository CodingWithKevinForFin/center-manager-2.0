package com.f1.povo.f1app.inspect;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.IN.IE")
public interface F1AppInspectionEntity extends Message {

	byte TYPE_BOOL = 1;
	byte TYPE_BYTE = 2;
	byte TYPE_CHAR = 3;
	byte TYPE_SHORT = 4;
	byte TYPE_INT = 5;
	byte TYPE_FLOAT = 6;
	byte TYPE_LONG = 7;
	byte TYPE_DOUBLE = 8;
	byte TYPE_OBJECT = 9;
	@PID(1)
	public int getId();
	public void setId(int id);
	@PID(5)
	public int getIdentityHashCode();
	public void setIdentityHashCode(int identityHashCode);

}
