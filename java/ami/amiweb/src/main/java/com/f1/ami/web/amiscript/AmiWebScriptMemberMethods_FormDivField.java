package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.DivQueryField;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormDivField extends AmiWebScriptBaseMemberMethods<DivQueryField> {

	private AmiWebScriptMemberMethods_FormDivField() {
		super();
		addMethod(SET_VALUE);
		addMethod(RESET_VALUE);
		addMethod(GET_VALUE, "value");

	}

	private static final AmiAbstractMemberMethod<DivQueryField> GET_VALUE = new AmiAbstractMemberMethod<DivQueryField>(DivQueryField.class, "getValue", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, DivQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHtmlValue(true);
		}

		@Override
		protected String getHelp() {
			return "Returns this div field's value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<DivQueryField> SET_VALUE = new AmiAbstractMemberMethod<DivQueryField>(DivQueryField.class, "setValue", Boolean.class,
			Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DivQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Object o = Caster_String.INSTANCE.cast(params[0]);
			return targetObject.setHtmlValue(o, true);
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "value (can be html)" };
		}

		@Override
		protected String getHelp() {
			return "Set this div field's html.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<DivQueryField> RESET_VALUE = new AmiAbstractMemberMethod<DivQueryField>(DivQueryField.class, "resetValue", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DivQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.setHtmlValue(targetObject.getHtmlValue(false), false);
			return true;
		}

		@Override
		protected String getHelp() {
			return "If setValue has been called (effectively overriding the div's html), then this will set the div's html back to the original default value.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "FormDivField";
	}

	@Override
	public String getVarTypeDescription() {
		return "AMI Script Class to represent the Div Field";
	}

	@Override
	public Class<DivQueryField> getVarType() {
		return DivQueryField.class;
	}

	@Override
	public Class<? extends DivQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormDivField INSTANCE = new AmiWebScriptMemberMethods_FormDivField();
}
