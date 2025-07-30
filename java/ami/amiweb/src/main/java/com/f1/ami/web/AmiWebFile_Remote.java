package com.f1.ami.web;

import java.io.File;
import java.io.IOException;

import com.f1.ami.amicommon.msg.AmiFileMessage;
import com.f1.ami.amicommon.msg.AmiWebManagerGetFileResponse;
import com.f1.ami.amicommon.msg.AmiWebManagerPutFileRequest;
import com.f1.ami.amicommon.msg.AmiWebManagerPutFileResponse;
import com.f1.utils.MH;

public class AmiWebFile_Remote implements AmiWebFile {

	private AmiWebFileSystem_Remote client;
	private AmiFileMessage inner;

	public AmiWebFile_Remote(AmiWebFileSystem_Remote client, AmiFileMessage inner) {
		this.client = client;
		this.inner = inner;

	}

	@Override
	public long length() {
		return inner.getLength();
	}

	@Override
	public String getName() {
		return inner.getName();
	}

	@Override
	public long lastModified() {
		return inner.getLastModified();
	}

	@Override
	public String getFullPath() {
		return inner.getFullPath();
	}

	@Override
	public boolean exists() {
		return MH.anyBits(inner.getFlags(), AmiFileMessage.FLAG_EXISTS);
	}

	@Override
	public boolean canWrite() {
		return MH.anyBits(inner.getFlags(), AmiFileMessage.FLAG_CAN_WRITE);
	}

	@Override
	public boolean canRead() {
		return MH.anyBits(inner.getFlags(), AmiFileMessage.FLAG_CAN_READ);
	}

	@Override
	public boolean isDirectory() {
		return MH.anyBits(inner.getFlags(), AmiFileMessage.FLAG_IS_DIRECTORY);
	}

	@Override
	public AmiWebFile[] listFiles() {
		return client.listFiles(this);
	}

	@Override
	public boolean isFile() {
		return MH.anyBits(inner.getFlags(), AmiFileMessage.FLAG_IS_FILE);
	}

	@Override
	public boolean isHidden() {
		return MH.anyBits(inner.getFlags(), AmiFileMessage.FLAG_IS_HIDDEN);
	}

	@Override
	public boolean canExecute() {
		return MH.anyBits(inner.getFlags(), AmiFileMessage.FLAG_CAN_EXECUTE);
	}

	@Override
	public AmiWebFile getParentFile() {
		return client.getFile(this.getParent());
	}

	@Override
	public String getParent() {
		return inner.getParentPath();
	}

	@Override
	public void mkdirForce() throws IOException {
		AmiWebManagerPutFileRequest req = client.newPutFileRequest(this, AmiWebManagerPutFileRequest.ACTION_MKDIR_FORCE);
		AmiWebManagerPutFileResponse res = client.send(req);
		processResponse(res);
		if (isFile())
			throw new IOException("Could not Directory, exists as file: " + this.getFullPath());
		if (!isDirectory())
			throw new IOException("Could not create directory: " + this.getFullPath());
	}

	@Override
	public void appendText(String text) throws IOException {
		AmiWebManagerPutFileRequest req = client.newPutFileRequest(this, AmiWebManagerPutFileRequest.ACTION_APPEND_DATA);
		req.setData(text.getBytes());
		AmiWebManagerPutFileResponse res = client.send(req);
		processResponse(res);
	}

	@Override
	public void writeText(String text) throws IOException {
		AmiWebManagerPutFileRequest req = client.newPutFileRequest(this, AmiWebManagerPutFileRequest.ACTION_WRITE_DATA);
		req.setData(text.getBytes());
		AmiWebManagerPutFileResponse res = client.send(req);
		processResponse(res);
	}

	@Override
	public String readTextForce() throws IOException {
		byte[] r = client.readData(this);
		if (r == null)
			throw new IOException("File missing: " + this.getFullPath());
		return new String(r);
	}
	@Override
	public void appendBytes(byte[] bytes) throws IOException {
		AmiWebManagerPutFileRequest req = client.newPutFileRequest(this, AmiWebManagerPutFileRequest.ACTION_APPEND_DATA);
		req.setData(bytes);
		AmiWebManagerPutFileResponse res = client.send(req);
		processResponse(res);
	}

	@Override
	public void writeBytes(byte[] bytes) throws IOException {
		AmiWebManagerPutFileRequest req = client.newPutFileRequest(this, AmiWebManagerPutFileRequest.ACTION_WRITE_DATA);
		req.setData(bytes);
		AmiWebManagerPutFileResponse res = client.send(req);
		processResponse(res);
	}

	@Override
	public byte[] readBytes() throws IOException {
		byte[] data = client.readData(this);
		return data;
	}

	@Override
	public String getAbsolutePath() {
		return inner.getAbsolutePath();
	}

	@Override
	public String getPath() {
		return inner.getPath();
	}

	@Override
	public boolean delete() {
		AmiWebManagerPutFileRequest req = client.newPutFileRequest(this, AmiWebManagerPutFileRequest.ACTION_DELETE);
		AmiWebManagerPutFileResponse res = client.send(req);
		processResponseNoThrowIO(res);
		return Boolean.TRUE.equals(res.getReturnFlag());
	}

	@Override
	public boolean move(AmiWebFile nuw) {
		AmiWebManagerPutFileRequest req = client.newPutFileRequest(this, AmiWebManagerPutFileRequest.ACTION_MOVE);
		req.setTargetFileName(nuw.getAbsolutePath());
		AmiWebManagerPutFileResponse res = client.send(req);
		processResponseNoThrowIO(res);
		return Boolean.TRUE.equals(res.getReturnFlag());
	}

	@Override
	public boolean mkdir() {
		AmiWebManagerPutFileRequest req = client.newPutFileRequest(this, AmiWebManagerPutFileRequest.ACTION_MKDIR);
		AmiWebManagerPutFileResponse res = client.send(req);
		processResponseNoThrowIO(res);
		return Boolean.TRUE.equals(res.getReturnFlag());
	}

	@Override
	public String readText() throws IOException {
		byte[] data = client.readData(this);
		return data == null ? null : new String(data);
	}

	@Override
	public boolean setWritable(boolean b) {
		AmiWebManagerPutFileRequest req = client.newPutFileRequest(this, AmiWebManagerPutFileRequest.ACTION_DELETE);
		req.setWritable(b);
		AmiWebManagerPutFileResponse res = client.send(req);
		processResponseNoThrowIO(res);
		return canWrite();
	}

	public void processResponseNoThrowIO(AmiWebManagerPutFileResponse res) {
		if (res.getFile() != null)
			this.inner = res.getFile();
	}
	public void processResponseNoThrowIO(AmiWebManagerGetFileResponse res) {
		if (res.getFile() != null)
			this.inner = res.getFile();
	}
	void processResponse(AmiWebManagerPutFileResponse res) throws IOException {
		if (res.getException() != null)
			throw new IOException("From " + this.client.getHostName(), res.getException());
		if (res.getFile() != null)
			this.inner = res.getFile();
	}
	void processResponse(AmiWebManagerGetFileResponse res) throws IOException {
		if (res.getException() != null)
			throw new IOException("From " + this.client.getHostName(), res.getException());
		if (res.getFile() != null)
			this.inner = res.getFile();
	}

	@Override
	public void deleteForceRecursive() throws IOException {
		AmiWebManagerPutFileRequest req = client.newPutFileRequest(this, AmiWebManagerPutFileRequest.ACTION_DELETE_FORCE_RECURSIVE);
		AmiWebManagerPutFileResponse res = client.send(req);
		processResponse(res);
		if (this.exists())
			throw new IOException("Could not delete: " + getFullPath());
	}

	@Override
	public void moveForce(AmiWebFile targetFile) throws IOException {
		AmiWebManagerPutFileRequest req = client.newPutFileRequest(this, AmiWebManagerPutFileRequest.ACTION_MOVE_FORCE);
		req.setTargetFileName(targetFile.getAbsolutePath());
		AmiWebManagerPutFileResponse res = client.send(req);
		processResponse(res);
		targetFile.refreshFlags();
	}

	@Override
	public File getLocalFile() {
		throw new UnsupportedOperationException("Is Remote File");
	}

	@Override
	public String[] list() {
		return client.list(this);
	}

	public AmiWebFileSystem_Remote getClient() {
		return this.client;
	}

	@Override
	public void deleteForce() throws IOException {
		AmiWebManagerPutFileRequest req = client.newPutFileRequest(this, AmiWebManagerPutFileRequest.ACTION_DELETE);
		AmiWebManagerPutFileResponse res = client.send(req);
		processResponse(res);
		if (this.exists())
			throw new IOException("Could not delete: " + getFullPath());
	}

	@Override
	public void refreshFlags() throws IOException {
		AmiWebManagerPutFileRequest req = client.newPutFileRequest(this, (byte) 0);
		req.setTargetFileName(getAbsolutePath());
		AmiWebManagerPutFileResponse res = client.send(req);
		processResponse(res);
	}

}
