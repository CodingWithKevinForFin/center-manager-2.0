package com.f1.ami.amicommon.msg;

import java.util.List;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.GUPR")
public interface AmiFileMessage extends Message {

	short FLAG_EXISTS = 1;
	short FLAG_IS_FILE = 2;
	short FLAG_IS_DIRECTORY = 4;
	short FLAG_IS_HIDDEN = 8;
	short FLAG_CAN_READ = 16;
	short FLAG_CAN_WRITE = 32;
	short FLAG_CAN_EXECUTE = 64;

	@PID(1)
	long getLength();
	void setLength(long length);

	@PID(2)
	String getName();
	void setName(String name);

	@PID(3)
	long getLastModified();
	void setLastModified(long lastModified);

	@PID(4)
	String getFullPath();
	void setFullPath(String fullPath);

	@PID(5)
	public String getAbsolutePath();
	void setAbsolutePath(String absolutePath);

	@PID(6)
	public String getPath();
	public void setPath(String path);

	@PID(7)
	public byte[] getData();
	public void setData(byte[] text);

	@PID(8)
	public String[] getFileNames();
	public void setFileNames(String[] files);

	@PID(9)
	public List<AmiFileMessage> getFiles();
	public void setFiles(List<AmiFileMessage> files);

	@PID(10)
	public short getFlags();
	public void setFlags(short flags);

	@PID(11)
	public String getParentPath();
	public void setParentPath(String path);
}
