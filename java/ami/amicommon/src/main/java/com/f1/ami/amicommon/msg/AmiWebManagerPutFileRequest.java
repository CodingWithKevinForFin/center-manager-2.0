package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.WM.PFQ")
public interface AmiWebManagerPutFileRequest extends AmiCenterRequest {

	short ACTION_WRITE_DATA = 6;
	short ACTION_WRITE_DATA_SAFE = 7;
	short ACTION_APPEND_DATA = 5;

	short ACTION_MKDIR = 10;
	short ACTION_MKDIR_FORCE = 11;

	short ACTION_MOVE = 8;
	short ACTION_MOVE_FORCE = 9;

	short ACTION_DELETE = 1;
	short ACTION_DELETE_SAFE = 2;
	short ACTION_DELETE_FORCE_RECURSIVE = 4;

	@PID(1)
	String getFileName();
	public void setFileName(String fileName);

	@PID(2)
	short getAction();
	public void setAction(short action);

	@PID(3)
	byte[] getData();//For APPEND_TEXT, WRITE_TEXT
	public void setData(byte[] data);

	@PID(4)
	String getTargetFileName();//For ACTION_MOVE, ACTION_MOVE_FORCE
	public void setTargetFileName(String fileName);

	@PID(5)
	Boolean getWritable();//null means unchanged
	public void setWritable(Boolean fileName);

	@PID(6)
	Boolean getReadable();//null means unchanged
	public void setReadable(Boolean fileName);

	@PID(7)
	Boolean getExecutable();//null means unchanged
	public void setExecutable(Boolean fileName);

	@PID(8)
	short getOptions();
	public void setOptions(short fileName);

	@PID(9)
	String getSafeFileExtension();
	public void setSafeFileExtension(String fileName);
}
