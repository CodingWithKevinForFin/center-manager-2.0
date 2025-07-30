package com.f1.ami.web;

import java.io.IOException;

public interface AmiWebSafeFile {
	public boolean exists();
	public AmiWebFile getFile();
	//	public byte[] getData();
	public long getAsOfModifiedTime();//Returns the the Modified Time of the File when it was loaded
	public String getText();
	public void setText(String text) throws IOException;
	public byte[] getData();
	public void setData(byte[] data) throws IOException;
	//	public void setData(byte[] data) throws IOException;
	public void deleteFile() throws IOException;
}
