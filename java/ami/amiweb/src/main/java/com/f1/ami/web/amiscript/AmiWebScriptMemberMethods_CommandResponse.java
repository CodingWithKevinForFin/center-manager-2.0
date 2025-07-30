package com.f1.ami.web.amiscript;

import java.util.Map;

import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandResponse;
import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_CommandResponse extends AmiWebScriptBaseMemberMethods<AmiWebCommandResponse> {

	private AmiWebScriptMemberMethods_CommandResponse() {
		super();
		addMethod(GET_RETURN_VALUE, "returnValues");
		addMethod(GET_MESSAGE, "message");
		addMethod(GET_REQUEST_ID, "requestId");
		addMethod(GET_AMISCRIPT, "amiScript");
		addMethod(GET_STATUS, "status");
		addMethod(GET_STATUS_CODE, "statusCode");
	}

	private static final AmiAbstractMemberMethod<AmiWebCommandResponse> GET_RETURN_VALUE = new AmiAbstractMemberMethod<AmiWebCommandResponse>(AmiWebCommandResponse.class,
			"getReturnValues", Map.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCommandResponse targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getValues();
		}

		@Override
		protected String getHelp() {
			return "get values supplied in the response";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebCommandResponse> GET_MESSAGE = new AmiAbstractMemberMethod<AmiWebCommandResponse>(AmiWebCommandResponse.class, "getMessage",
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCommandResponse targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getResponse().getAmiMessage();
		}

		@Override
		protected String getHelp() {
			return "get message supplied in the response (M=...)";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebCommandResponse> GET_AMISCRIPT = new AmiAbstractMemberMethod<AmiWebCommandResponse>(AmiWebCommandResponse.class,
			"getAmiScript", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCommandResponse targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getResponse().getAmiScript();
		}

		@Override
		protected String getHelp() {
			return "get ami script supplied in the response";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebCommandResponse> GET_STATUS = new AmiAbstractMemberMethod<AmiWebCommandResponse>(AmiWebCommandResponse.class, "getStatus",
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCommandResponse targetObject, Object[] params, DerivedCellCalculator caller) {
			int code = targetObject.getResponse().getStatusCode();
			switch (code) {
				case AmiRelayRunAmiCommandResponse.STATUS_COMMAND_NOT_REGISTERED:
					return "COMMAND_NOT_REGISTERED";
				case AmiRelayRunAmiCommandResponse.STATUS_DONT_CLOSE_DIALOG:
					return "DONT_CLOSE_DIALOG";
				case AmiRelayRunAmiCommandResponse.STATUS_OKAY:
					return "OKAY";
				case AmiRelayRunAmiCommandResponse.STATUS_GENERAL_ERROR:
					return "GENERAL_ERROR";
				case AmiRelayRunAmiCommandResponse.STATUS_UPDATE_RECORD:
					return "UPDATE_RECORD";
				case AmiRelayRunAmiCommandResponse.STATUS_TIMEOUT:
					return "TIMEOUT";
				default:
					return "STATUS CODE " + code;
			}
		}

		@Override
		protected String getHelp() {
			return "get status code supplied in the response, either COMMAND_NOT_REGISTERED, DONT_CLOSE_DIALOG, OKAY, GENERAL_ERROR, UPDATE_RECORD, TIMEOUT";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebCommandResponse> GET_STATUS_CODE = new AmiAbstractMemberMethod<AmiWebCommandResponse>(AmiWebCommandResponse.class,
			"getStatusCode", Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCommandResponse targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getResponse().getStatusCode();
		}

		@Override
		protected String getHelp() {
			return "get status code supplied in the response as an integer.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebCommandResponse> GET_REQUEST_ID = new AmiAbstractMemberMethod<AmiWebCommandResponse>(AmiWebCommandResponse.class,
			"getRequestId", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebCommandResponse targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getRequest().getCommandUid();
		}

		@Override
		protected String getHelp() {
			return "get status code supplied in the response(S=...)";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "CommandResponse";
	}

	@Override
	public String getVarTypeDescription() {
		return "Represents the response to calling a command";
	}

	@Override
	public Class<AmiWebCommandResponse> getVarType() {
		return AmiWebCommandResponse.class;
	}

	@Override
	public Class<AmiWebCommandResponse> getVarDefaultImpl() {
		return null;
	}

	public static final AmiWebScriptMemberMethods_CommandResponse INSTANCE = new AmiWebScriptMemberMethods_CommandResponse();
}
