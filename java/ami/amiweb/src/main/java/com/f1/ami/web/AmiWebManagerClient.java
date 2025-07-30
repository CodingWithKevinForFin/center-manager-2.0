package com.f1.ami.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.msg.AmiFileMessage;
import com.f1.ami.amicommon.msg.AmiWebManagerGetFileRequest;
import com.f1.ami.amicommon.msg.AmiWebManagerGetFileResponse;
import com.f1.ami.amicommon.msg.AmiWebManagerListRootsRequest;
import com.f1.ami.amicommon.msg.AmiWebManagerListRootsResponse;
import com.f1.ami.amicommon.msg.AmiWebManagerProcessSpecialFileRequest;
import com.f1.ami.amicommon.msg.AmiWebManagerProcessSpecialFileResponse;
import com.f1.ami.amicommon.msg.AmiWebManagerPutFileRequest;
import com.f1.ami.amicommon.msg.AmiWebManagerPutFileResponse;
import com.f1.base.Message;
import com.f1.base.Table;
import com.f1.container.RequestOutputPort;
import com.f1.container.exceptions.ContainerTimeoutException;

public class AmiWebManagerClient {

	private RequestOutputPort<Message, Message> webManagerPort;
	private String hostname;
	private long timeout;

	public AmiWebManagerClient(RequestOutputPort<Message, Message> wmport, String hostname, long timeout) {
		webManagerPort = wmport;
		this.hostname = hostname;
		this.timeout = timeout;
	}

	public List<AmiFileMessage> listRoots(String username) {
		AmiWebManagerListRootsRequest req = webManagerPort.nw(AmiWebManagerListRootsRequest.class);
		req.setInvokedBy(username);
		AmiWebManagerListRootsResponse res = (AmiWebManagerListRootsResponse) webManagerPort.requestWithFuture(req, null).getResult(timeout).getAction();
		return res.getFiles();
	}

	public List<AmiFileMessage> listFiles(String username, AmiWebFile file) {
		AmiWebManagerGetFileRequest req = newGetFileRequest(username, null, file.getAbsolutePath(), AmiWebManagerGetFileRequest.INCLUDE_FILES);
		AmiWebManagerGetFileResponse res = send(req);
		return res.getFile().getFiles();
	}

	public AmiWebManagerGetFileResponse send(AmiWebManagerGetFileRequest req) {
		AmiWebManagerGetFileResponse res;
		try {
			res = (AmiWebManagerGetFileResponse) webManagerPort.requestWithFuture(req, null).getResult(timeout).getAction();
		} catch (ContainerTimeoutException e) {
			throw new RuntimeException("Timeout while getting file details from webmanager at: " + this.hostname, e);
		}
		if (res.getException() != null)
			throw new RuntimeException("Error getting file '" + req.getFileName() + "' on host " + this.hostname, res.getException());
		return res;
	}

	public String[] list(String username, AmiWebFile file) {
		AmiWebManagerGetFileRequest req = newGetFileRequest(username, null, file.getAbsolutePath(), AmiWebManagerGetFileRequest.INCLUDE_FILE_NAMES);
		AmiWebManagerGetFileResponse res;
		try {
			res = send(req);
		} catch (ContainerTimeoutException e) {
			throw new RuntimeException("Timeout while getting file details from webmanager at: " + this.hostname, e);
		}
		return res.getFile().getFileNames();
	}

	public AmiWebManagerGetFileRequest newGetFileRequest(String username, String parentFileName, String filename, short options) {
		AmiWebManagerGetFileRequest req = webManagerPort.nw(AmiWebManagerGetFileRequest.class);
		req.setInvokedBy(username);
		req.setParentFileName(parentFileName);
		req.setFileName(filename);
		req.setOptions(options);
		return req;
	}

	public AmiWebManagerPutFileResponse send(AmiWebManagerPutFileRequest req) {
		try {
			AmiWebManagerPutFileResponse res = (AmiWebManagerPutFileResponse) webManagerPort.requestWithFuture(req, null).getResult(timeout).getAction();
			return res;
		} catch (ContainerTimeoutException e) {
			throw new RuntimeException("Timeout while getting file details from webmanager at: " + this.hostname, e);
		}
	}

	public AmiWebManagerPutFileRequest newPutFileRequest(String username, AmiWebFile file, short action) {
		AmiWebManagerPutFileRequest req = webManagerPort.nw(AmiWebManagerPutFileRequest.class);
		req.setInvokedBy(username);
		req.setFileName(file.getAbsolutePath());
		req.setAction(action);
		return req;
	}

	public byte[] readData(String username, AmiWebFile file) {
		AmiWebManagerGetFileRequest req = newGetFileRequest(username, null, file.getAbsolutePath(), AmiWebManagerGetFileRequest.INCLUDE_DATA);
		AmiWebManagerGetFileResponse res = send(req);
		return res.getFile().getData();
	}

	public AmiFileMessage getFile(String username, String filename) {
		return send(newGetFileRequest(username, null, filename, (short) 0)).getFile();
	}

	public AmiFileMessage getFile(String username, AmiWebFile parent, String filename) {
		return send(newGetFileRequest(username, parent.getAbsolutePath(), filename, (short) 0)).getFile();
	}

	public AmiFileMessage getFile(String username, String parent, String filename) {
		return send(newGetFileRequest(username, parent, filename, (short) 0)).getFile();
	}

	public AmiFileMessage getSafeFile(String username, AmiWebFile file) throws IOException {
		AmiWebManagerGetFileRequest req = newGetFileRequest(username, null, file.getAbsolutePath(), AmiWebManagerGetFileRequest.INCLUDE_DATA);
		AmiWebManagerGetFileResponse res = send(req);
		return res.getFile();
	}

	public String getHostName() {
		return hostname;
	}

	public Table getSpecial(String username, AmiWebFile file, String instruction, Map<String, ?> params) {
		AmiWebManagerProcessSpecialFileRequest req = webManagerPort.nw(AmiWebManagerProcessSpecialFileRequest.class);
		req.setInvokedBy(username);
		req.setFileName(file.getAbsolutePath());
		req.setInstruction(instruction);
		req.setOptions(params);
		AmiWebManagerProcessSpecialFileResponse res;
		try {
			res = (AmiWebManagerProcessSpecialFileResponse) webManagerPort.requestWithFuture(req, null).getResult(timeout).getAction();
		} catch (ContainerTimeoutException e) {
			throw new RuntimeException("Timeout while getting file details from webmanager at: " + this.hostname, e);
		}
		if (res.getException() != null)
			throw new RuntimeException("Error getting file on host " + this.hostname, res.getException());
		return res.getResults();
	}

}
