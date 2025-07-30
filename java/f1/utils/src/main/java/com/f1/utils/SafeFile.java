package com.f1.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

public class SafeFile {
	private static final Logger log = LH.get();
	public static final String DEFAULT_TMP_EXTENSION = ".safe";
	final private File file;
	private byte[] data;
	private File tmpFile;
	private String text;

	public SafeFile(File location) throws IOException {
		this(location, DEFAULT_TMP_EXTENSION);
	}
	public SafeFile(File location, String tmpExtension) throws IOException {
		this.file = location;
		this.tmpFile = new File(file.getParent(), file.getName() + tmpExtension);
		if (file.exists()) {
			data = IOH.readData(file);
			// we could log this if we can't delete the file
			tmpFile.delete();
		} else if (tmpFile.exists()) {//hit edge condition where system went down after deleting file, but before move complete
			tmpFile.renameTo(file);
			data = IOH.readData(file);
		}
	}

	public boolean exists() {
		return file.exists();
	}

	public File getFile() {
		return this.file;
	}

	public byte[] getData() {
		return data;
	}

	public String getText() {
		if (text == null && data != null)
			text = new String(data);
		return text;
	}

	public void setText(String text) throws IOException {
		if (text == null)
			throw new NullPointerException("text");
		if (OH.eq(text, this.text))
			return;
		setData(text.getBytes());
		this.text = text;
	}
	public void setData(byte[] data) throws IOException {
		if (data == null)
			throw new NullPointerException("data");
		if (Arrays.equals(this.data, data))
			return;
		if (!file.exists())
			IOH.writeData(file, OH.EMPTY_BYTE_ARRAY);
		IOH.writeData(tmpFile, data);
		if (!file.delete()) {
			LH.warning(log, "Failed to delete file " + file.getName());
		}
		if (!tmpFile.renameTo(file)) {
			LH.warning(log, "Failed to rename file from " + tmpFile.getName() + " to " + file.getName());
		}
		this.data = data.clone();
		text = null;
	}
	public void deleteFile() throws IOException {
		IOH.delete(this.file);
		IOH.delete(this.tmpFile);
		this.text = null;
		this.data = null;
	}

}
