package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.PasswordQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.base.Password;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormPasswordField extends AmiWebScriptBaseMemberMethods<PasswordQueryField> {

	private AmiWebScriptMemberMethods_FormPasswordField() {
		super();
		addMethod(GET_VALUE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
	}

	private static final AmiAbstractMemberMethod<PasswordQueryField> GET_VALUE = new AmiAbstractMemberMethod<PasswordQueryField>(PasswordQueryField.class, "getValue",
			Password.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, PasswordQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Password r = targetObject.getField().getValue();
			targetObject.setValue(null);
			return r;
		}

		@Override
		protected String getHelp() {
			return "Returns the password object";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	@Override
	public String getVarTypeName() {
		return "FormPasswordField";
	}

	@Override
	public String getVarTypeDescription() {
		return "Form field for password.";
	}

	@Override
	public Class<PasswordQueryField> getVarType() {
		return PasswordQueryField.class;
	}

	@Override
	public Class<? extends PasswordQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormPasswordField INSTANCE = new AmiWebScriptMemberMethods_FormPasswordField();

}
