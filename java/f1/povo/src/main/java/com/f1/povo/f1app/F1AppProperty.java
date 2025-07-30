package com.f1.povo.f1app;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.PTY")
public interface F1AppProperty extends F1AppEntity {
	int NO_LINE_NUMBER = -1;
	byte TYPE_FILE = 1;
	byte TYPE_SYSTEM_PROPERTY = 2;
	byte TYPE_SYSTEM_ENV = 3;
	byte TYPE_CODE = 4;
	byte TYPE_PREFERENCE = 5;
	byte TYPE_COLLECTION = 6;
	byte TYPE_UNKNOWN = -1;

	byte PID_KEY = 1;
	byte PID_VALUE = 2;
	byte PID_SOURCE_LINE_NUMBER = 3;
	byte PID_SOURCE = 4;
	byte PID_SOURCE_TYPE = 5;
	byte PID_POSITION = 6;
	byte PID_IS_SECURE = 7;

	@PID(PID_KEY)
	public String getKey();
	public void setKey(String key);

	@PID(PID_VALUE)
	public String getValue();
	public void setValue(String value);

	@PID(PID_SOURCE_LINE_NUMBER)
	public int getSourceLineNumber();
	public void setSourceLineNumber(int lineNumber);

	@PID(PID_SOURCE)
	public String getSource();
	public void setSource(String source);

	@PID(PID_SOURCE_TYPE)
	public byte getSourceType();
	public void setSourceType(byte sourceType);

	//0 = effective, greater the number the more times it was overridden
	@PID(PID_POSITION)
	public short getPosition();
	public void setPosition(short sourceType);

	@PID(PID_IS_SECURE)
	public void setIsSecure(boolean b);
	public boolean getIsSecure();

}
