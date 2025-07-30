package com.f1.ami.web;

import java.io.IOException;

import com.f1.utils.SafeFile;

public class AmiWebSafeFile_Local implements AmiWebSafeFile {

	private long asOfModifiedTime = 0;
	private SafeFile inner;
	private AmiWebFile_Local file;

	public AmiWebSafeFile_Local(AmiWebFile_Local file) throws IOException {
		this.inner = new SafeFile(file.getLocalFile());
		this.file = file;
		updateAsOfTime();
	}
	@Override
	public boolean exists() {
		return inner.exists();
	}

	@Override
	public AmiWebFile getFile() {
		return file;
	}

	@Override
	public String getText() {
		return inner.getText();
	}

	@Override
	public void setText(String text) throws IOException {
		inner.setText(text);
		updateAsOfTime();
	}
	private void updateAsOfTime() {
		this.asOfModifiedTime = this.file.lastModified();
	}

	@Override
	public void deleteFile() throws IOException {
		inner.deleteFile();
		updateAsOfTime();
	}
	@Override
	public long getAsOfModifiedTime() {
		return this.asOfModifiedTime;
	}
	@Override
	public byte[] getData() {
		return inner.getData();
	}
	@Override
	public void setData(byte[] data) throws IOException {
		this.inner.setData(data);
		updateAsOfTime();
	}

}
