package com.f1.ami.web.amiscript;

import java.util.Map;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebRpcRequest;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpUtils;
import com.f1.utils.GuidHelper;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_RpcRequest extends AmiWebScriptBaseMemberMethods<AmiWebRpcRequest> {

	private AmiWebScriptMemberMethods_RpcRequest() {
		super();
		addMethod(INIT);
		addMethod(INIT2);
		addMethod(GET_METHOD_NAME, "methodName");
		addMethod(GET_SOURCE_URL, "sourceUrl");
		addMethod(GET_TARGET_URL, "targetUrl");
		addMethod(GET_SOURCE_USERNAME, "sourceUsername");
		addMethod(GET_SOURCE_SESSION_ID, "sourceSessionId");
		addMethod(GET_REQUIRED_TARGET_LAYOUT, "requiredTargetLayout");
		addMethod(GET_REQUIRED_TARGET_USERNAME, "requiredTargetusername");
		addMethod(GET_REQUIRED_TARGET_SESSIONID, "requiredTargetSessionId");
		addMethod(GET_REQUEST_UID, "requestUid");
		addMethod(GET_ARGUMENTS, "arguments");
	}

	private static String getSourceUrl(AmiWebService service) {
		HttpRequestResponse request = service == null ? null : service.getPortletManager().getCurrentRequestAction();
		if (request == null)
			return null;
		else {
			return HttpUtils.buildUrl(request.getIsSecure(), request.getHost(), request.getPort(), "", "");
		}
	}

	private static final AmiAbstractMemberMethod<AmiWebRpcRequest> INIT = new AmiAbstractMemberMethod<AmiWebRpcRequest>(AmiWebRpcRequest.class, null, AmiWebRpcRequest.class,
			String.class, String.class, Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRpcRequest targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebService service = AmiWebUtils.getService(sf);
			if (service == null)
				return null;//HMMM not sure what to do in a constructor
			String username = service.getUserName();
			String sessionId = (String) service.getPortletManager().getState().getSessionId();
			String targetUrl = (String) params[0];
			String methodName = (String) params[1];
			Map arguments = (Map) params[2];
			String sourceLayout = service.getLayoutFilesManager().getLayoutName();
			return new AmiWebRpcRequest("AMIRPC_" + GuidHelper.getGuid(), sourceLayout, getSourceUrl(service), username, sessionId, targetUrl, methodName, arguments, null, null,
					null);
		}

		@Override
		protected String getHelp() {
			return "Initializes a RPC Request object with the given arguments.";
		}
		protected String[] buildParamNames() {
			return new String[] { "targetUrl", "methodName", "arguments" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "In the form http[s]://host[:port]", "method name to call on target", "arguments to pass into method" };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	// not seeing this constructor in the doc
	private static final AmiAbstractMemberMethod<AmiWebRpcRequest> INIT2 = new AmiAbstractMemberMethod<AmiWebRpcRequest>(AmiWebRpcRequest.class, null, AmiWebRpcRequest.class,
			String.class, String.class, Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRpcRequest targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebService service = AmiWebUtils.getService(sf);
			if (service == null)
				return null;//HMMM not sure what to do in a constructor
			String username = service.getUserName();
			String sessionId = (String) service.getPortletManager().getState().getSessionId();
			String targetUrl = (String) params[0];
			String methodName = (String) params[1];
			Map arguments = (Map) params[2];
			String sourceLayout = service.getLayoutFilesManager().getLayoutName();
			String reqTargetLayout = (String) params[3];
			String reqTargetUsername = (String) params[4];
			String reqTargetSessionId = (String) params[5];
			return new AmiWebRpcRequest("AMIRPC_" + GuidHelper.getGuid(), sourceLayout, getSourceUrl(service), username, sessionId, targetUrl, methodName, arguments,
					reqTargetLayout, reqTargetUsername, reqTargetSessionId);
		}

		@Override
		protected String getHelp() {
			return "Initializes a RPC Request object with the given arguments.";
		}
		protected String[] buildParamNames() {
			return new String[] { "targetUrl", "methodName", "arguments" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "In the form http[s]://host[:port]", "method name to call on target", "arguments to pass into method" };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebRpcRequest> GET_METHOD_NAME = new AmiAbstractMemberMethod<AmiWebRpcRequest>(AmiWebRpcRequest.class, "getMethodName",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRpcRequest targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMethodName();
		}

		@Override
		protected String getHelp() {
			return "Returns the method name of this RPC request.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebRpcRequest> GET_SOURCE_URL = new AmiAbstractMemberMethod<AmiWebRpcRequest>(AmiWebRpcRequest.class, "getSourceUrl",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRpcRequest targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSourceUrl();
		}

		@Override
		protected String getHelp() {
			return "Returns the source URL of this RPC request.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebRpcRequest> GET_TARGET_URL = new AmiAbstractMemberMethod<AmiWebRpcRequest>(AmiWebRpcRequest.class, "getTargetUrl",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRpcRequest targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTargetUrl();
		}

		@Override
		protected String getHelp() {
			return "Returns the taget URL of this RPC request.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebRpcRequest> GET_SOURCE_USERNAME = new AmiAbstractMemberMethod<AmiWebRpcRequest>(AmiWebRpcRequest.class, "getSourceUsername",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRpcRequest targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSourceUsername();
		}

		@Override
		protected String getHelp() {
			return "Returns the source username of this RPC request.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebRpcRequest> GET_SOURCE_SESSION_ID = new AmiAbstractMemberMethod<AmiWebRpcRequest>(AmiWebRpcRequest.class,
			"getSourceSessionId", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRpcRequest targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSourceSessionId();
		}

		@Override
		protected String getHelp() {
			return "Returns the source SessionId of this RPC request.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebRpcRequest> GET_ARGUMENTS = new AmiAbstractMemberMethod<AmiWebRpcRequest>(AmiWebRpcRequest.class, "getArguments",
			Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRpcRequest targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getArguments();
		}

		@Override
		protected String getHelp() {
			return "Returns a map of the arguments of this RPC request.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebRpcRequest> GET_REQUIRED_TARGET_LAYOUT = new AmiAbstractMemberMethod<AmiWebRpcRequest>(AmiWebRpcRequest.class,
			"getRequiredTargetLayout", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRpcRequest targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getRequiredTargetLayout();
		}

		@Override
		protected String getHelp() {
			return "Returns the required target layout of this RPC request.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebRpcRequest> GET_REQUIRED_TARGET_USERNAME = new AmiAbstractMemberMethod<AmiWebRpcRequest>(AmiWebRpcRequest.class,
			"getRequiredTargetUsername", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRpcRequest targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getRequiredTargetUsername();
		}

		@Override
		protected String getHelp() {
			return "Returns the required target username of this RPC request.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebRpcRequest> GET_REQUIRED_TARGET_SESSIONID = new AmiAbstractMemberMethod<AmiWebRpcRequest>(AmiWebRpcRequest.class,
			"getRequiredTargetSessionId", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRpcRequest targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getRequiredTargetSessionId();
		}

		@Override
		protected String getHelp() {
			return "Returns the required target sessionId of this RPC request.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebRpcRequest> GET_REQUEST_UID = new AmiAbstractMemberMethod<AmiWebRpcRequest>(AmiWebRpcRequest.class, "getRequestUid",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRpcRequest targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getRequestUid();
		}

		@Override
		protected String getHelp() {
			return "Returns the request uid.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "RpcRequest";
	}

	@Override
	public String getVarTypeDescription() {
		return "Represents a call to/from a RPC API";
	}
	@Override
	public Class<AmiWebRpcRequest> getVarType() {
		return AmiWebRpcRequest.class;
	}
	@Override
	public Class<AmiWebRpcRequest> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_RpcRequest INSTANCE = new AmiWebScriptMemberMethods_RpcRequest();

}
