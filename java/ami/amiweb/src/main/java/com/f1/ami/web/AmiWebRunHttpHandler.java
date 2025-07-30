package com.f1.ami.web;

import java.io.IOException;
import java.util.Map;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.amiscript.AmiCallRestFlowControlPause;
import com.f1.container.ContainerTools;
import com.f1.http.HttpRequestResponse;
import com.f1.http.impl.BasicHttpRequestResponse;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.HttpStateCreator;
import com.f1.suite.web.HttpStateHandler;
import com.f1.suite.web.WebState;
import com.f1.suite.web.portal.PortletManager;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorConst;

public class AmiWebRunHttpHandler extends HttpStateHandler {

	public static final String ALLOW_ORIGIN_ALL = "*";
	public static final String ALLOW_ORIGIN_NULL = "null";
	final private boolean allowAll;

	final private TextMatcher allowedOrigins;
	final private String allowedOriginsText;

	public AmiWebRunHttpHandler(HttpStateCreator stateCreator, ContainerTools tools) {
		super(stateCreator);

		String allowedOrigins = tools.getOptional(AmiWebProperties.PROPERTY_AMI_PERMITTED_CORS_ORIGINS, "*");
		this.allowAll = SH.equals(ALLOW_ORIGIN_ALL, allowedOrigins);
		if (this.allowAll)
			AmiUtils.logSecurityWarning("AMI WEB has been configured with an overly permissive CORS policy of '*', it is recommended to update the property `"
					+ AmiWebProperties.PROPERTY_AMI_PERMITTED_CORS_ORIGINS + "` to a list of permitted origins delimited by `|`: origin|origin2...");
		this.allowedOriginsText = allowedOrigins;
		this.allowedOrigins = SH.m(allowedOrigins);
	}

	@Override
	public void handle(HttpRequestResponse req) throws IOException {
		assertInit();
		String origin = req.getHeader().get("Origin");
		if ("OPTIONS".equals(req.getMethod())) {
			req.putResponseHeader("Allow", "OPTIONS, GET, HEAD, POST");
			req.putResponseHeader("Access-Control-Request-Method", "POST");
			req.putResponseHeader("Access-Control-Allow-Credentials", "true");
			req.putResponseHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");
			req.setResponseType(HttpRequestResponse.HTTP_204_NO_CONTENT);
			return;
		} else if (SH.isnt(origin)) {
			req.setResponseType(BasicHttpRequestResponse.HTTP_401_UNAUTHORIZED);
			return;
		}
		// Access Control Allow Origin
		boolean matchesOrigin = allowedOrigins.matches(origin);
		if (allowAll)
			req.putResponseHeader("Access-Control-Allow-Origin", ALLOW_ORIGIN_ALL);
		else {
			if (!matchesOrigin)
				req.putResponseHeader("Access-Control-Allow-Origin", ALLOW_ORIGIN_NULL);
			else {
				req.putResponseHeader("Access-Control-Allow-Origin", origin);
				req.putResponseHeader("Vary", "Origin");
			}
		}
		req.putResponseHeader("Access-Control-Allow-Credentials", "true");
		if (!matchesOrigin) {
			AmiUtils.logSecurityWarning("Requested CORS origin is not permitted: " + origin + " (Permitted mask is: " + allowedOriginsText + ")");
			StringBuilder sb = new StringBuilder();
			AmiCallRestFlowControlPause.append(AmiCallRestFlowControlPause.RESPONSE_PARAM_ERROR, "ORIGIN_NOT_PERMITTED: " + origin, sb);
			req.getOutputStream().print(sb);
			return;
		} else if (req.getSession(false) == null) {
			StringBuilder sb = new StringBuilder();
			AmiCallRestFlowControlPause.append(AmiCallRestFlowControlPause.RESPONSE_PARAM_ERROR, "USER_NOT_LOGGED_IN", sb);
			req.getOutputStream().print(sb);
			return;
		} else
			super.handle(req);
	}
	@Override
	public Object handle(HttpRequestAction request, WebState state) {
		PortletManager portletManager = state.getPortletManager();
		if (portletManager == null) {
			request.getRequest().setResponseType(BasicHttpRequestResponse.BYTES_HTTP_401_UNAUTHORIZED);
			request.getRequest().getOutputStream().print("Session not found");
			return null;
		}
		Map<String, String> params = request.getRequest().getParams();
		String targetLayout = params.get(AmiCallRestFlowControlPause.REQUEST_PARAM_REQUIRED_TARGET_LAYOUT);
		String targetUsername = params.get(AmiCallRestFlowControlPause.REQUEST_PARAM_REQUIRED_TARGET_USERNAME);
		String targetSessionId = params.get(AmiCallRestFlowControlPause.REQUEST_PARAM_REQUIRED_TARGET_SESSIONID);
		String sourceLayout = params.get(AmiCallRestFlowControlPause.REQUEST_PARAM_SOURCE_LAYOUT);
		String sourceUsername = params.get(AmiCallRestFlowControlPause.REQUEST_PARAM_SOURCE_USERNAME);
		String sourceSessionId = params.get(AmiCallRestFlowControlPause.REQUEST_PARAM_SOURCE_SESSIONID);
		String requestUid = params.get(AmiCallRestFlowControlPause.REQUEST_PARAM_UID);
		String sourceUrl = params.get(AmiCallRestFlowControlPause.REQUEST_PARAM_SOURCE_URL);
		String method = params.get(AmiCallRestFlowControlPause.REQUEST_PARAM_METHOD);
		String arguments = params.get(AmiCallRestFlowControlPause.REQUEST_PARAM_ARGUMENTS);
		String targetUrl = request.getRequest().getRequestUrl();
		StringBuilder sb = new StringBuilder();
		AmiWebService service = (AmiWebService) portletManager.getService(AmiWebService.ID);
		if (SH.is(targetUsername) && OH.ne(targetUsername, service.getUserName())) {
			AmiCallRestFlowControlPause.append(AmiCallRestFlowControlPause.RESPONSE_PARAM_ERROR, "USERNAME_MISMATCH", sb);
		} else if (SH.is(targetLayout) && OH.ne(targetLayout, service.getLayoutFilesManager().getLayoutName())) {
			AmiCallRestFlowControlPause.append(AmiCallRestFlowControlPause.RESPONSE_PARAM_ERROR, "LAYOUT_NAME_MISMATCH", sb);
		} else if (SH.is(targetSessionId) && OH.ne(targetSessionId, service.getPortletManager().getState().getWebStatesManager().getSession().getSessionId())) {
			AmiCallRestFlowControlPause.append(AmiCallRestFlowControlPause.RESPONSE_PARAM_ERROR, "SESSIONID_MISMATCH", sb);
		} else {
			Map<String, Object> argumentsMap = (Map<String, Object>) ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(arguments);
			AmiWebRpcRequest restRequest = new AmiWebRpcRequest(requestUid, sourceLayout, sourceUrl, sourceUsername, sourceSessionId, targetUrl, method, argumentsMap, targetLayout,
					targetUsername, targetSessionId);
			AmiWebScriptManagerForLayout sm = service.getScriptManager("");
			BasicMethodFactory declared = sm.getMethodFactory();
			DerivedCellCalculatorConst args = new DerivedCellCalculatorConst(0, restRequest);
			DerivedCellCalculator methodDcc = declared == null ? null : declared.toMethod(0, method, new DerivedCellCalculator[] { args }, null);
			if (methodDcc == null || !Map.class.isAssignableFrom(methodDcc.getReturnType())) {
				AmiCallRestFlowControlPause.append(AmiCallRestFlowControlPause.RESPONSE_PARAM_ERROR, "NO_SUCH_METHOD: Map " + method + "(RestRequest request)", sb);
			} else {
				try {
					Map result = (Map) methodDcc.get(service.createStackFrame(service));
					AmiCallRestFlowControlPause.append(AmiCallRestFlowControlPause.RESPONSE_PARAM_RETURN_VALUE, ObjectToJsonConverter.INSTANCE_CLEAN.objectToString(result), sb);
				} catch (Exception e) {
					LH.warning(log, "Method ", methodDcc, " threw exception: ", e);
					AmiCallRestFlowControlPause.append(AmiCallRestFlowControlPause.RESPONSE_PARAM_ERROR, "METHOD_EXCEPTION: " + e.toString(), sb);
				}
			}
		}
		request.getRequest().getOutputStream().print(sb);
		return null;
	}
	@Override
	public void handleAfterUnlock(HttpRequestResponse req, Object data) {

	}

}
