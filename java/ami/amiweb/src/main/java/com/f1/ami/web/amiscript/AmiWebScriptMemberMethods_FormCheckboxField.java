package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.CheckboxQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormCheckboxField extends AmiWebScriptBaseMemberMethods<CheckboxQueryField> {

	private AmiWebScriptMemberMethods_FormCheckboxField() {
		super();
		addMethod(GET_VALUE, "value");
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
	}

	@Override
	public String getVarTypeName() {
		return "FormCheckboxField";
	}

	@Override
	public String getVarTypeDescription() {
		return "AMI Script Class to represent Checkbox Field";
	}

	@Override
	public Class<CheckboxQueryField> getVarType() {
		return CheckboxQueryField.class;
	}

	@Override
	public Class<CheckboxQueryField> getVarDefaultImpl() {
		return null;
	}

	private static final AmiAbstractMemberMethod<CheckboxQueryField> GET_VALUE = new AmiAbstractMemberMethod<CheckboxQueryField>(CheckboxQueryField.class, "getValue",
			Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, CheckboxQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getField().getValue();
		}

		@Override
		protected String getHelp() {
			return "Returns true if checkbox is checked, false otherwise.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};

	public final static AmiWebScriptMemberMethods_FormCheckboxField INSTANCE = new AmiWebScriptMemberMethods_FormCheckboxField();
}
