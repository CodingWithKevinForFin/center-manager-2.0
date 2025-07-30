package com.f1.ami.amiscript;

import com.f1.utils.AH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Error extends AmiScriptBaseMemberMethods<FlowControlThrow> {

	private AmiScriptMemberMethods_Error() {
		addMethod(GET_CAUSE, "cause");
		addMethod(GET_MESSAGE, "message");
		addMethod(GET_STACK_TRACE);
		addMethod(INIT);
		addMethod(INIT2);
	}

	private final static AmiAbstractMemberMethod<FlowControlThrow> INIT = new AmiAbstractMemberMethod<FlowControlThrow>(FlowControlThrow.class, null, Object.class, String.class,
			FlowControlThrow.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, FlowControlThrow targetObject, Object[] params, DerivedCellCalculator caller) {
			return new FlowControlThrow(caller, (String) params[0], (FlowControlThrow) params[1]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "msg", "cause" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "error message", "cause of error" };
		}
		@Override
		protected String getHelp() {
			return "Constructs a new error with the specified detail message and cause.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<FlowControlThrow> INIT2 = new AmiAbstractMemberMethod<FlowControlThrow>(FlowControlThrow.class, null, Object.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, FlowControlThrow targetObject, Object[] params, DerivedCellCalculator caller) {
			return new FlowControlThrow(caller, (String) params[0]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "msg" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "error message" };
		}
		@Override
		protected String getHelp() {
			return "Constructs a new error with the specified detail message.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	private final static AmiAbstractMemberMethod<FlowControlThrow> GET_MESSAGE = new AmiAbstractMemberMethod<FlowControlThrow>(FlowControlThrow.class, "getMessage", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, FlowControlThrow targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMessage();
		}
		@Override
		protected String getHelp() {
			return "Returns the detail message string of this error.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<FlowControlThrow> GET_CAUSE = new AmiAbstractMemberMethod<FlowControlThrow>(FlowControlThrow.class, "getCause",
			FlowControlThrow.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, FlowControlThrow targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getCause();
		}
		@Override
		protected String getHelp() {
			return "Returns the cause of this error or null if the cause is nonexistent or unknown.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	private final static AmiAbstractMemberMethod<FlowControlThrow> GET_STACK_TRACE = new AmiAbstractMemberMethod<FlowControlThrow>(FlowControlThrow.class, "getStackTrace",
			FlowControlThrow.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, FlowControlThrow targetObject, Object[] params, DerivedCellCalculator caller) {
			return SH.printStackTrace(targetObject);
		}
		@Override
		protected String getHelp() {
			return "Returns the raw java stack trace of this error.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	@Override
	public String getVarTypeName() {
		return "Error";
	}
	@Override
	public String getVarTypeDescription() {
		return null;
	}
	@Override
	public Class<FlowControlThrow> getVarType() {
		return FlowControlThrow.class;
	}
	@Override
	public Class<FlowControlThrow> getVarDefaultImpl() {
		return null;
	}

	public static AmiScriptMemberMethods_Error INSTANCE = new AmiScriptMemberMethods_Error();
}
