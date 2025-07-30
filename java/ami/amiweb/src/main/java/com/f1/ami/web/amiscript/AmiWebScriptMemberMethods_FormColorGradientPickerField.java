package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.ColorGradientPickerQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.utils.ColorGradient;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormColorGradientPickerField extends AmiWebScriptBaseMemberMethods<ColorGradientPickerQueryField> {

	private AmiWebScriptMemberMethods_FormColorGradientPickerField() {
		super();
		addMethod(SET_VALUE);
		addMethod(GET_VALUE, "value");
		addMethod(SET_ALPHA_ENABLED);
		addMethod(GET_ALPHA_ENABLED, "alphaEnabled");
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);

	}

	private static final AmiAbstractMemberMethod<ColorGradientPickerQueryField> GET_VALUE = new AmiAbstractMemberMethod<ColorGradientPickerQueryField>(
			ColorGradientPickerQueryField.class, "getValue", ColorGradient.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, ColorGradientPickerQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return (ColorGradient) targetObject.getValue();
		}

		@Override
		protected String getHelp() {
			return "Returns this field's value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<ColorGradientPickerQueryField> SET_VALUE = new AmiAbstractMemberMethod<ColorGradientPickerQueryField>(
			ColorGradientPickerQueryField.class, "setValue", Boolean.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, ColorGradientPickerQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Object o = Caster_String.INSTANCE.cast(params[0]);
			return targetObject.setValue(o);
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "value" };
		}

		@Override
		protected String getHelp() {
			return "Set this field's value.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<ColorGradientPickerQueryField> GET_ALPHA_ENABLED = new AmiAbstractMemberMethod<ColorGradientPickerQueryField>(
			ColorGradientPickerQueryField.class, "getAlphaEnabled", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, ColorGradientPickerQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getValue();
		}

		@Override
		protected String getHelp() {
			return "Returns true if the alpha channel is enabled.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<ColorGradientPickerQueryField> SET_ALPHA_ENABLED = new AmiAbstractMemberMethod<ColorGradientPickerQueryField>(
			ColorGradientPickerQueryField.class, "setAlphaEnabled", Boolean.class, Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, ColorGradientPickerQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Boolean o = Caster_Boolean.INSTANCE.cast(params[0]);
			if (o == null)
				targetObject.setAlaphaEnabled(targetObject.getAlaphaEnabled(false), false);
			else
				targetObject.setAlaphaEnabled(o.booleanValue(), true);
			return null;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "value, null to go back to default" };
		}

		@Override
		protected String getHelp() {
			return "Enable the alpha channel.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "FormGradientColorPickerField";
	}

	@Override
	public String getVarTypeDescription() {
		return "AMI Script Class to represent the color gradient picker Field";
	}

	@Override
	public Class<ColorGradientPickerQueryField> getVarType() {
		return ColorGradientPickerQueryField.class;
	}

	@Override
	public Class<? extends ColorGradientPickerQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormColorGradientPickerField INSTANCE = new AmiWebScriptMemberMethods_FormColorGradientPickerField();
}
