package com.f1.ami.web;

import java.util.Map;

public class AmiWebRpcResponse {
	final private String responseUid;
	final private String errorMessage;
	final private Map<String, Object> returnValues;
	final private AmiWebRpcRequest origRequest;

	public AmiWebRpcResponse(AmiWebRpcRequest origRequest, String responseUid, Map<String, Object> arguments) {
		this.origRequest = origRequest;
		this.responseUid = responseUid;
		this.errorMessage = null;
		this.returnValues = arguments;
	}

	public AmiWebRpcResponse(AmiWebRpcRequest origRequest, String responseUid, String errorMessage) {
		this.origRequest = origRequest;
		this.responseUid = responseUid;
		this.errorMessage = errorMessage;
		this.returnValues = null;
	}

	public AmiWebRpcRequest getOrigRequest() {
		return origRequest;
	}

	public String getResponseUid() {
		return responseUid;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public Map<String, Object> getReturnValues() {
		return returnValues;
	}

}
