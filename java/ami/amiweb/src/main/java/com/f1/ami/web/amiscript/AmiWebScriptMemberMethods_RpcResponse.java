package com.f1.ami.web.amiscript;

import java.util.Map;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.AmiWebRpcRequest;
import com.f1.ami.web.AmiWebRpcResponse;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_RpcResponse extends AmiWebScriptBaseMemberMethods<AmiWebRpcResponse> {

	private AmiWebScriptMemberMethods_RpcResponse() {
		super();
		addMethod(GET_ORIG_REQUEST, "origRequest");
		addMethod(GET_RESPONSE_UID, "responseUid");
		addMethod(GET_ERROR_MESSAGE, "errorMessgae");
		addMethod(GET_ERROR, "error");
		addMethod(GET_ARGUMENTS, "arguments");
	}

	private static final AmiAbstractMemberMethod<AmiWebRpcResponse> GET_ORIG_REQUEST = new AmiAbstractMemberMethod<AmiWebRpcResponse>(AmiWebRpcResponse.class, "getOrigRequest",
			AmiWebRpcRequest.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRpcResponse targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getOrigRequest();
		}

		@Override
		protected String getHelp() {
			return "Returns the original request that this a response to.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebRpcResponse> GET_RESPONSE_UID = new AmiAbstractMemberMethod<AmiWebRpcResponse>(AmiWebRpcResponse.class, "getResponseUid",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRpcResponse targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getResponseUid();
		}

		@Override
		protected String getHelp() {
			return "Returns the Response Uid.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebRpcResponse> GET_ERROR_MESSAGE = new AmiAbstractMemberMethod<AmiWebRpcResponse>(AmiWebRpcResponse.class, "getErrorMessage",
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRpcResponse targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getErrorMessage();
		}

		@Override
		protected String getHelp() {
			return "Returns the Error Message.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebRpcResponse> GET_ERROR = new AmiAbstractMemberMethod<AmiWebRpcResponse>(AmiWebRpcResponse.class, "getError", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRpcResponse targetObject, Object[] params, DerivedCellCalculator caller) {
			String em = targetObject.getErrorMessage();
			String r = em == null ? null : SH.trim(SH.beforeFirst(em, ':'));
			if ("ERROR_FROM_SERVER".equals(r)) {
				String remaining = SH.afterFirst(em, ':');
				r = SH.trim(SH.beforeFirst(remaining, ':', remaining));
				return r == null ? "ERROR_FROM_SERVER" : ("ERROR_FROM_SERVER_" + r);
			} else
				return r;
		}

		@Override
		protected String getHelp() {
			return "Returns the Error as a String.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebRpcResponse> GET_ARGUMENTS = new AmiAbstractMemberMethod<AmiWebRpcResponse>(AmiWebRpcResponse.class, "getReturnValues",
			Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRpcResponse targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getReturnValues();
		}

		@Override
		protected String getHelp() {
			return "Returns a map of the arguments of this RPC Response.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "RpcResponse";
	}

	@Override
	public String getVarTypeDescription() {
		return "Represents a response from an RPC call";
	}
	@Override
	public Class<AmiWebRpcResponse> getVarType() {
		return AmiWebRpcResponse.class;
	}
	@Override
	public Class<AmiWebRpcResponse> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_RpcResponse INSTANCE = new AmiWebScriptMemberMethods_RpcResponse();

}
