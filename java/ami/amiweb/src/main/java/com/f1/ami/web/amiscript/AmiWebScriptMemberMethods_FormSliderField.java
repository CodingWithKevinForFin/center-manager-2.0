package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.RangeQueryField;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormSliderField extends AmiWebScriptBaseMemberMethods<RangeQueryField> {

	private AmiWebScriptMemberMethods_FormSliderField() {
		super();
		addMethod(SET_VALUE);
		addMethod(GET_MIN, "min");
		addMethod(GET_MAX, "max");
		addMethod(SET_RANGE);
		addMethod(SET_STEP);
		addMethod(GET_STEP);
		addMethod(RESET_RANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
	}

	public static final AmiAbstractMemberMethod<RangeQueryField> RESET_RANGE = new AmiAbstractMemberMethod<RangeQueryField>(RangeQueryField.class, "resetRange", Object.class) {
		@Override
		protected String getHelp() {
			return "If the min or max values have been set via amiscript, this resets to default config";
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, RangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.getField().setRange(targetObject.getMin(), targetObject.getMax());
			return null;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	public static final AmiAbstractMemberMethod<RangeQueryField> SET_VALUE = new AmiAbstractMemberMethod<RangeQueryField>(RangeQueryField.class, "setValue", Object.class,
			Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, RangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Double val = Caster_Double.INSTANCE.cast(params[0]);
			targetObject.getField().setValue(val);
			return null;
		}
		@Override
		protected String getHelp() {
			return "Set low and high values of field";
		}
		protected String[] buildParamNames() {
			return new String[] { "val" };
		};
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	public static final AmiAbstractMemberMethod<RangeQueryField> SET_RANGE = new AmiAbstractMemberMethod<RangeQueryField>(RangeQueryField.class, "setRange", Object.class,
			Number.class, Number.class) {
		@Override
		protected String getHelp() {
			return "Set min and max values of field";
		}
		protected String[] buildParamNames() {
			return new String[] { "min", "max" };
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, RangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Double min = Caster_Double.INSTANCE.cast(params[0]);
			Double max = Caster_Double.INSTANCE.cast(params[1]);
			if (min == null || max == null)
				return null;
			if (max < min) {
				Double t = min;
				min = max;
				max = t;
				return null;
			}
			targetObject.getField().setRange(min, max);
			return null;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	public static final AmiAbstractMemberMethod<RangeQueryField> GET_MIN = new AmiAbstractMemberMethod<RangeQueryField>(RangeQueryField.class, "getMin", Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, RangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMin(); // srqf has it's own min now, get it from there.
		}
		@Override
		protected String getHelp() {
			return "Get min value of field";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public static final AmiAbstractMemberMethod<RangeQueryField> GET_MAX = new AmiAbstractMemberMethod<RangeQueryField>(RangeQueryField.class, "getMax", Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, RangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMax(); // srqf has its own max now, get it from there.
		}
		@Override
		protected String getHelp() {
			return "Get max value of field";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public static final AmiAbstractMemberMethod<RangeQueryField> GET_STEP = new AmiAbstractMemberMethod<RangeQueryField>(RangeQueryField.class, "getStep", Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, RangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getField().getStep();
		}
		@Override
		protected String getHelp() {
			return "Get step of range field";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public static final AmiAbstractMemberMethod<RangeQueryField> SET_STEP = new AmiAbstractMemberMethod<RangeQueryField>(RangeQueryField.class, "setRange", Object.class,
			Number.class) {
		@Override
		protected String getHelp() {
			return "Set step of range field";
		}
		protected String[] buildParamNames() {
			return new String[] { "step" };
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, RangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			double step = Caster_Double.PRIMITIVE.cast(params[0]);
			targetObject.getField().setStep(step);
			return null;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "FormSliderField";
	}

	@Override
	public String getVarTypeDescription() {
		return "Field for defining a range with min and max values";
	}

	@Override
	public Class<RangeQueryField> getVarType() {
		return RangeQueryField.class;
	}

	@Override
	public Class<RangeQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormSliderField INSTANCE = new AmiWebScriptMemberMethods_FormSliderField();
}
