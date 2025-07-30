package com.f1.ami.web;

import java.io.IOException;

import com.f1.ami.amicommon.msg.AmiWebManagerGetFileRequest;
import com.f1.ami.amicommon.msg.AmiWebManagerGetFileResponse;
import com.f1.ami.amicommon.msg.AmiWebManagerPutFileRequest;
import com.f1.ami.amicommon.msg.AmiWebManagerPutFileResponse;
import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.SafeFile;

public class AmiWebSafeFile_Remote implements AmiWebSafeFile {

	private AmiWebFile_Remote inner;
	private String text;
	private byte[] data;
	private long asOfModifiedTime;

	public AmiWebSafeFile_Remote(AmiWebFile_Remote inner, byte[] data) {
		this.inner = inner;
		this.data = data;
		this.updateAsOfModifiedTime();
	}
	private void updateAsOfModifiedTime() {
		this.asOfModifiedTime = inner.lastModified();
	}
	@Override
	public boolean exists() {
		return inner.exists();
	}

	@Override
	public AmiWebFile getFile() {
		return inner;
	}

	@Override
	public String getText() {
		checkForUpdatedFile();
		if (text == null)
			text = (data == null) ? null : new String(data);
		return text;
	}

	@Override
	public void setText(String text) throws IOException {
		if (text == null)
			throw new NullPointerException("text");
		if (OH.eq(text, this.text))
			return;
		AmiWebManagerPutFileRequest req = inner.getClient().newPutFileRequest(this.inner, AmiWebManagerPutFileRequest.ACTION_WRITE_DATA_SAFE);
		byte[] data = text.getBytes();
		req.setData(data);
		req.setSafeFileExtension(SafeFile.DEFAULT_TMP_EXTENSION);
		AmiWebManagerPutFileResponse res = inner.getClient().send(req);
		inner.processResponse(res);
		this.text = text;
		this.data = data;
		updateAsOfModifiedTime();
	}

	@Override
	public void deleteFile() throws IOException {
		AmiWebManagerPutFileRequest req = inner.getClient().newPutFileRequest(this.inner, AmiWebManagerPutFileRequest.ACTION_DELETE_SAFE);
		req.setSafeFileExtension(SafeFile.DEFAULT_TMP_EXTENSION);
		AmiWebManagerPutFileResponse res = inner.getClient().send(req);
		inner.processResponse(res);
		this.text = null;
		this.data = null;
		updateAsOfModifiedTime();
	}
	@Override
	public long getAsOfModifiedTime() {
		return this.asOfModifiedTime;
	}
	@Override
	public byte[] getData() {
		checkForUpdatedFile();
		return data;
	}
	private void checkForUpdatedFile() {
		AmiWebManagerGetFileRequest req = this.inner.getClient().newGetFileRequest(null, this.inner.getAbsolutePath(), AmiWebManagerGetFileRequest.INCLUDE_DATA);
		req.setAsOfModifiedTime(this.asOfModifiedTime);
		AmiWebManagerGetFileResponse res = this.inner.getClient().send(req);
		byte status = res.getStatus();
		if (status == AmiWebManagerGetFileResponse.STATUS_OKAY) {
			inner.processResponseNoThrowIO(res);
			updateAsOfModifiedTime();
			this.data = res.getFile().getData();
			this.text = null;
		} else if (status != AmiWebManagerGetFileResponse.STATUS_NO_CHANGE) {
			throw new RuntimeException("unknown status: " + status);
		}
	}
	@Override
	public void setData(byte[] data) throws IOException {
		if (data == null)
			throw new NullPointerException("data");
		if (AH.eq(data, this.data))
			return;
		AmiWebManagerPutFileRequest req = inner.getClient().newPutFileRequest(this.inner, AmiWebManagerPutFileRequest.ACTION_WRITE_DATA_SAFE);
		req.setData(data);
		req.setSafeFileExtension(SafeFile.DEFAULT_TMP_EXTENSION);
		AmiWebManagerPutFileResponse res = inner.getClient().send(req);
		inner.processResponse(res);
		this.text = null;
		this.data = data;
		updateAsOfModifiedTime();
	}

}
