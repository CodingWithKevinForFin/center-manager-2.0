package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.WM.GFQ")
public interface AmiWebManagerGetFileRequest extends AmiCenterRequest {

	short INCLUDE_DATA = 1;
	short INCLUDE_FILES = 2;
	short INCLUDE_FILE_NAMES = 3;
	short INCLUDE_DATA_SAFE = 4;

	@PID(1)
	String getFileName();
	public void setFileName(String fileName);

	@PID(2)
	String getParentFileName();
	public void setParentFileName(String parentFileName);

	@PID(3)
	short getOptions();
	public void setOptions(short fileName);

	@PID(4)
	String getSafeFileExtension();
	public void setSafeFileExtension(String fileName);

	@PID(5)
	void setAsOfModifiedTime(long asOfModifiedTime);
	long getAsOfModifiedTime();
}
