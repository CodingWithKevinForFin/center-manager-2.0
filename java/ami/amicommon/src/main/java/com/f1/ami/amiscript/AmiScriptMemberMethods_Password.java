package com.f1.ami.amiscript;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.base.Password;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Password extends AmiScriptBaseMemberMethods<Password> {
	private static final String VAR_TYPE_NAME = "Password";
	private static final String VAR_TYPE_DESC = "A data holder for a password";

	private AmiScriptMemberMethods_Password() {
		super();

		addMethod(INIT);
		addMethod(CLEAR);
		addMethod(PEEK_AND_CLEAR);
	}

	@Override
	public String getVarTypeName() {
		return VAR_TYPE_NAME;
	}

	@Override
	public String getVarTypeDescription() {
		return VAR_TYPE_DESC;
	}

	@Override
	public Class<Password> getVarType() {
		return Password.class;
	}

	@Override
	public Class<? extends Password> getVarDefaultImpl() {
		return Password.class;
	}

	private final static AmiAbstractMemberMethod<Password> INIT = new AmiAbstractMemberMethod<Password>(Password.class, null, Password.class, CharSequence.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Password targetObject, Object[] params, DerivedCellCalculator caller) {
			return new Password((CharSequence) params[0]);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "password" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "password string" };
		}
		@Override
		protected String getHelp() {
			return "Creates a new password holder, note empty string is not a recommended password";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	private final static AmiAbstractMemberMethod<Password> CLEAR = new AmiAbstractMemberMethod<Password>(Password.class, "clear", Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Password targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.clear();
			return null;
		}
		@Override
		protected String getHelp() {
			return "Clears the password from the Password holder, its the developer's responsibility to clear out the password after it is not needed. When cleared the password is set to empty string";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	private final static AmiAbstractMemberMethod<Password> PEEK_AND_CLEAR = new AmiAbstractMemberMethod<Password>(Password.class, "peekAndClear", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Password targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiCalcFrameStack ei = AmiUtils.getExecuteInstance2(sf);
			AmiUtils.logSecurityWarning("Peek was called on Password object for User: " + SH.quote(ei.getUserName()) + " and callback: " + SH.quote(ei.getSourceAri())
					+ ". Please ensure this was intended, as Password::peekAndClear() is not intended for production use cases.");
			return targetObject.peekAndClear();
		}
		@Override
		protected String getHelp() {
			return "Warning, this is intended to be used for debugging. Peek into the password holder and returns the password. This will clear out the password holder. Logs a security warning when this method is called.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	public static AmiScriptMemberMethods_Password INSTANCE = new AmiScriptMemberMethods_Password();
}
