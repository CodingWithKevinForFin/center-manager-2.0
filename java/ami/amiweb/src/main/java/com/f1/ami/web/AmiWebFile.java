package com.f1.ami.web;

import java.io.File;
import java.io.IOException;

public interface AmiWebFile {

	public long length();
	public String getName();
	public long lastModified();

	public String getFullPath();
	public String getAbsolutePath();
	public String getPath();

	public boolean exists();
	public boolean canWrite();
	public boolean canRead();
	public boolean isDirectory();
	public boolean isFile();
	public boolean isHidden();
	public boolean canExecute();

	public AmiWebFile[] listFiles();
	public String[] list();
	public AmiWebFile getParentFile();
	public String getParent();

	public boolean mkdir();
	public void mkdirForce() throws IOException;

	public void appendText(String text) throws IOException;
	public void writeText(String text) throws IOException;
	public void appendBytes(byte[] data) throws IOException;
	public void writeBytes(byte[] data) throws IOException;

	public String readTextForce() throws IOException;
	public String readText() throws IOException;
	public byte[] readBytes() throws IOException;

	public boolean setWritable(boolean b);

	public boolean delete();
	public void deleteForce() throws IOException;
	public void deleteForceRecursive() throws IOException;

	public boolean move(AmiWebFile nuw);
	public void moveForce(AmiWebFile targetFile) throws IOException;

	public File getLocalFile();
	public void refreshFlags() throws IOException;
}
