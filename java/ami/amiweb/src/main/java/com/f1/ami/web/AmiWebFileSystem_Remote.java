package com.f1.ami.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.msg.AmiFileMessage;
import com.f1.ami.amicommon.msg.AmiWebManagerGetFileRequest;
import com.f1.ami.amicommon.msg.AmiWebManagerGetFileResponse;
import com.f1.ami.amicommon.msg.AmiWebManagerPutFileRequest;
import com.f1.ami.amicommon.msg.AmiWebManagerPutFileResponse;
import com.f1.ami.web.amiscript.AmiWebFileSystem;
import com.f1.base.Table;

public class AmiWebFileSystem_Remote implements AmiWebFileSystem {

	private AmiWebService service;
	private AmiWebManagerClient webManagerClient;

	public AmiWebFileSystem_Remote(AmiWebManagerClient webManagerClient) {
		this.webManagerClient = webManagerClient;
	}
	@Override
	public AmiWebFile getFile(String string) {
		return toFile(webManagerClient.getFile(getUserName(), string));
	}

	@Override
	public AmiWebSafeFile getSafeFile(AmiWebFile string) throws IOException {
		AmiFileMessage fm = webManagerClient.getSafeFile(getUserName(), string);
		byte[] data = fm.getData();
		fm.setData(null);
		AmiWebFile_Remote f = toFile(fm);
		return new AmiWebSafeFile_Remote(f, data);
	}

	@Override
	public AmiWebFile getFile(AmiWebFile parent, String string) {
		return toFile(webManagerClient.getFile(getUserName(), parent, string));
	}

	@Override
	public AmiWebFile getFile(String parent, String string) {
		return toFile(webManagerClient.getFile(getUserName(), parent, string));
	}

	@Override
	public AmiWebFile[] listRoots() {
		return toFiles(webManagerClient.listRoots(getUserName()));
	}
	@Override
	public void init(AmiWebService service) {
		this.service = service;
	}
	public AmiWebFile[] listFiles(AmiWebFile_Remote file) {
		return toFiles(this.webManagerClient.listFiles(getUserName(), file));
	}
	public AmiWebManagerPutFileRequest newPutFileRequest(AmiWebFile_Remote file, short action) {
		return this.webManagerClient.newPutFileRequest(getUserName(), file, action);
	}
	public AmiWebManagerGetFileRequest newGetFileRequest(String parentFile, String fileName, short options) {
		return this.webManagerClient.newGetFileRequest(getUserName(), parentFile, fileName, options);
	}
	public AmiWebManagerPutFileResponse send(AmiWebManagerPutFileRequest req) {
		return this.webManagerClient.send(req);
	}
	public AmiWebManagerGetFileResponse send(AmiWebManagerGetFileRequest req) {
		return this.webManagerClient.send(req);
	}
	public byte[] readData(AmiWebFile_Remote file) {
		return this.webManagerClient.readData(getUserName(), file);
	}
	public String[] list(AmiWebFile_Remote file) {
		return this.webManagerClient.list(getUserName(), file);
	}
	private String getUserName() {
		return service == null ? null : service.getUserName();
	}
	@Override
	public String getHostName() {
		return this.webManagerClient.getHostName();
	}

	private AmiWebFile_Remote toFile(AmiFileMessage file) {
		return new AmiWebFile_Remote(this, file);
	}
	private AmiWebFile[] toFiles(List<AmiFileMessage> t) {
		AmiWebFile[] r = new AmiWebFile[t.size()];
		for (int i = 0; i < t.size(); i++)
			r[i] = toFile(t.get(i));
		return r;
	}
	@Override
	public Table getSpecial(AmiWebFile file, String instruction, Map<String, ?> params) {
		return this.webManagerClient.getSpecial(getUserName(), file, instruction, params);
	}
	@Override
	public boolean isWindows() {
		return false;
	}
	@Override
	public boolean isLocal() {
		return false;
	}
}
