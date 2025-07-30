package com.f1.ami.web.amiscript;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebRpcRequest;
import com.f1.ami.web.AmiWebRpcResponse;
import com.f1.http.HttpRequestResponse;
import com.f1.http.impl.BasicHttpRequestResponse;
import com.f1.povo.standard.RunnableRequestMessage;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletManagerRestCallResponseListener;
import com.f1.utils.GuidHelper;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlPause;

public class AmiCallRestFlowControlPause extends FlowControlPause implements PortletManagerRestCallResponseListener {

	public static final String REQUEST_PARAM_REQUIRED_TARGET_LAYOUT = "tLayout";
	public static final String REQUEST_PARAM_REQUIRED_TARGET_USERNAME = "tUsername";
	public static final String REQUEST_PARAM_REQUIRED_TARGET_SESSIONID = "tSessionId";
	public static final String REQUEST_PARAM_SOURCE_LAYOUT = "sLayout";
	public static final String REQUEST_PARAM_SOURCE_USERNAME = "sUsername";
	public static final String REQUEST_PARAM_SOURCE_SESSIONID = "sSessionId";
	public static final String REQUEST_PARAM_UID = "reqUid";
	public static final String REQUEST_PARAM_SOURCE_URL = "sUrl";
	public static final String REQUEST_PARAM_METHOD = "method";
	public static final String REQUEST_PARAM_ARGUMENTS = "args";
	public static final String RESPONSE_PARAM_ERROR = "err";
	public static final String RESPONSE_PARAM_RETURN_VALUE = "rval";
	private AmiWebRpcRequest restRequest;
	private int timeout;
	private RunnableRequestMessage request;
	private Logger log = LH.get();
	private Exception exception;
	private AmiWebRpcResponse restResponse;
	private AmiWebScriptRunner runner;

	public AmiCallRestFlowControlPause(AmiWebRpcRequest restRequest, int timeout, DerivedCellCalculator position) {
		super(position);
		this.restRequest = restRequest;
		this.timeout = timeout;
	}

	public void setRequest(RunnableRequestMessage request) {
		this.request = request;
	}

	public void run(AmiWebScriptRunner runner, PortletManager portletManager) {
		this.runner = runner;
		StringBuilder sb = new StringBuilder();
		append(REQUEST_PARAM_REQUIRED_TARGET_LAYOUT, restRequest.getRequiredTargetLayout(), sb);
		append(REQUEST_PARAM_REQUIRED_TARGET_USERNAME, restRequest.getRequiredTargetUsername(), sb);
		append(REQUEST_PARAM_REQUIRED_TARGET_SESSIONID, restRequest.getRequiredTargetSessionId(), sb);
		append(REQUEST_PARAM_SOURCE_LAYOUT, restRequest.getSourceLayout(), sb);
		append(REQUEST_PARAM_SOURCE_USERNAME, restRequest.getSourceUsername(), sb);
		append(REQUEST_PARAM_SOURCE_SESSIONID, restRequest.getSourceSessionId(), sb);
		append(REQUEST_PARAM_SOURCE_URL, restRequest.getSourceUrl(), sb);
		append(REQUEST_PARAM_METHOD, restRequest.getMethodName(), sb);
		append(REQUEST_PARAM_UID, restRequest.getRequestUid(), sb);
		append(REQUEST_PARAM_ARGUMENTS, ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(restRequest.getArguments()), sb);

		byte[] out;
		URL target;
		try {
			target = new URL(restRequest.getTargetUrl() + "/run");
		} catch (Exception e) {
			LH.info(log, "For request ", sb, e);
			this.exception = e;
			String guid = GuidHelper.getGuid();
			this.restResponse = new AmiWebRpcResponse(this.restRequest, guid, "INVALID_URL: " + restRequest.getTargetUrl() + "/run");
			resume();
			return;
		}
		try {
			portletManager.sendRestRequest(true, target, sb.toString(), timeout, this);
		} catch (Exception e) {
			LH.info(log, "For request ", sb, e);
			this.exception = e;
			String guid = GuidHelper.getGuid();
			this.restResponse = new AmiWebRpcResponse(this.restRequest, guid, e.toString());
			resume();
			return;
		}

	}

	public AmiWebRpcResponse getResponse() {
		return this.restResponse;
	}
	public static void append(String key, String value, StringBuilder sb) {
		if (value == null)
			return;
		if (sb.length() > 0)
			sb.append('&');
		sb.append(key).append('=');
		SH.encodeUrl(value, sb);

	}

	@Override
	public void onRestCallResponse(int statusCode, String response) {

		String guid = GuidHelper.getGuid();
		switch (statusCode) {
			case HttpRequestResponse.HTTP_200_OK: {
				Map<String, String> sink = new HashMap<String, String>();
				BasicHttpRequestResponse.parseParams(response, 0, response.length(), sink, null, new StringBuilder());
				String error = (String) sink.get(RESPONSE_PARAM_ERROR);
				if (error != null)
					this.restResponse = new AmiWebRpcResponse(this.restRequest, guid, "ERROR_FROM_SERVER: " + error);
				else {
					String rvaltext = (String) sink.get(RESPONSE_PARAM_RETURN_VALUE);
					Map<String, Object> responseArguments = (Map<String, Object>) ObjectToJsonConverter.INSTANCE_COMPACT.stringToObject(rvaltext);
					this.restResponse = new AmiWebRpcResponse(this.restRequest, guid, responseArguments);
				}
				break;
			}
			case PortletManagerRestCallResponseListener.CODE_NO_RESPONSE:
				this.restResponse = new AmiWebRpcResponse(this.restRequest, guid, "NO_RESPONSE");
				break;
			case HttpRequestResponse.HTTP_401_UNAUTHORIZED:
				this.restResponse = new AmiWebRpcResponse(this.restRequest, guid, "UNAUTHORIZED");
				break;
			case 0:
				this.restResponse = new AmiWebRpcResponse(this.restRequest, guid, "CONNECTION_FAILED: " + restRequest.getTargetUrl() + "/run");
				break;
			default:
				this.restResponse = new AmiWebRpcResponse(this.restRequest, guid, "HTTP_STATUS_ERROR: " + statusCode);
				break;
		}
		this.runner.onRestResponse(this);
	}
}
