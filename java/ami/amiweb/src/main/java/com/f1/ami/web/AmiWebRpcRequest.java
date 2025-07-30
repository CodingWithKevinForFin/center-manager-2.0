package com.f1.ami.web;

import java.util.Map;

import com.f1.utils.CH;

public class AmiWebRpcRequest {
	final private String methodName;
	final private String sourceUrl;
	final private String targetUrl;
	final private String sourceLayout;
	final private String sourceUsername;
	final private String sourceSessionId;
	final private String requiredTargetLayout;
	final private String requiredTargetUsername;
	final private String requiredTargetSessionId;
	final private Map<String, Object> arguments;
	final private String requestUid;

	public AmiWebRpcRequest(String requestUid, String sourceLayout, String sourceUrl, String sourceUsername, String sourceSessionId, String targetUrl, String methodName,
			Map<String, Object> arguments, String requiredTargetLayout, String requiredTargetUsername, String requiredTargetSessionId) {
		this.requestUid = requestUid;
		this.methodName = methodName;
		this.sourceLayout = sourceLayout;
		this.sourceUrl = sourceUrl;
		this.targetUrl = targetUrl;
		this.sourceUsername = sourceUsername;
		this.sourceSessionId = sourceSessionId;
		this.arguments = CH.unmodifiableMapNoNull(arguments);
		this.requiredTargetLayout = requiredTargetLayout;
		this.requiredTargetUsername = requiredTargetUsername;
		this.requiredTargetSessionId = requiredTargetSessionId;
	}

	public String getMethodName() {
		return methodName;
	}
	public String getRequestUid() {
		return requestUid;
	}
	public String getSourceLayout() {
		return sourceLayout;
	}
	public String getSourceUrl() {
		return sourceUrl;
	}
	public String getTargetUrl() {
		return targetUrl;
	}
	public String getSourceUsername() {
		return sourceUsername;
	}
	public String getSourceSessionId() {
		return sourceSessionId;
	}
	public Map<String, Object> getArguments() {
		return arguments;
	}

	public String getRequiredTargetLayout() {
		return requiredTargetLayout;
	}

	public String getRequiredTargetUsername() {
		return requiredTargetUsername;
	}

	public String getRequiredTargetSessionId() {
		return requiredTargetSessionId;
	}

}
