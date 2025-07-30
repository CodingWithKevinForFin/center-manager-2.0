package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.SubRangeQueryField;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormRangeSliderField extends AmiWebScriptBaseMemberMethods<SubRangeQueryField> {

	private AmiWebScriptMemberMethods_FormRangeSliderField() {
		super();
		addMethod(GET_LOW_VALUE);
		addMethod(GET_HIGH_VALUE);
		addMethod(SET_VALUE);
		addMethod(GET_MIN, "min");
		addMethod(GET_MAX, "max");
		addMethod(SET_RANGE);
		addMethod(RESET_RANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
	}

	private static final AmiAbstractMemberMethod<SubRangeQueryField> GET_LOW_VALUE = new AmiAbstractMemberMethod<SubRangeQueryField>(SubRangeQueryField.class, "getLowValue",
			Double.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, SubRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getField().getValue().getA();
		}
		@Override
		protected String getHelp() {
			return "Returns a Double that is the current low value of the field.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<SubRangeQueryField> SET_VALUE = new AmiAbstractMemberMethod<SubRangeQueryField>(SubRangeQueryField.class, "setValue", Object.class,
			Number.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, SubRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Double low = Caster_Double.INSTANCE.cast(params[0]);
			Double high = Caster_Double.INSTANCE.cast(params[1]);
			if (low == null || high == null)
				return null;
			if (high < low) {
				Double t = low;
				low = high;
				high = t;
			}
			targetObject.getField().setValue(low, high);
			return null;
		}
		@Override
		protected String getHelp() {
			return "Sets the low and high values of the field.";
		}
		protected String[] buildParamNames() {
			return new String[] { "low", "high" };
		};
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<SubRangeQueryField> RESET_RANGE = new AmiAbstractMemberMethod<SubRangeQueryField>(SubRangeQueryField.class, "resetRange",
			Object.class) {
		@Override
		protected String getHelp() {
			return "If the min or max values have been set via amiscript, this resets the range to the default configuration.";
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, SubRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.getField().setRange(targetObject.getMin(), targetObject.getMax());
			return null;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<SubRangeQueryField> SET_RANGE = new AmiAbstractMemberMethod<SubRangeQueryField>(SubRangeQueryField.class, "setRange", Object.class,
			Number.class, Number.class) {
		@Override
		protected String getHelp() {
			return "Sets the min and max values of the field.";
		}
		protected String[] buildParamNames() {
			return new String[] { "min", "max" };
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, SubRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Double min = Caster_Double.INSTANCE.cast(params[0]);
			Double max = Caster_Double.INSTANCE.cast(params[1]);
			if (min == null || max == null)
				return null;
			if (max < min) {
				Double t = min;
				min = max;
				max = t;
			}
			targetObject.getField().setRange(min, max);
			return null;
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<SubRangeQueryField> GET_HIGH_VALUE = new AmiAbstractMemberMethod<SubRangeQueryField>(SubRangeQueryField.class, "getHighValue",
			Double.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, SubRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getField().getValue().getB();
		}
		@Override
		protected String getHelp() {
			return "Returns a Double that is the current high value of the field.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<SubRangeQueryField> GET_MIN = new AmiAbstractMemberMethod<SubRangeQueryField>(SubRangeQueryField.class, "getMin", Double.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, SubRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMin(); // srqf has it's own min now, get it from there.
		}
		@Override
		protected String getHelp() {
			return "Returns a Double that is the min value of the field.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<SubRangeQueryField> GET_MAX = new AmiAbstractMemberMethod<SubRangeQueryField>(SubRangeQueryField.class, "getMax", Double.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, SubRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMax(); // srqf has its own max now, get it from there.
		}
		@Override
		protected String getHelp() {
			return "Return a Double that is the max value of the field.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<SubRangeQueryField> getStep = new AmiAbstractMemberMethod<SubRangeQueryField>(SubRangeQueryField.class, "getStep", Double.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, SubRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getField().getStep();
		}
		@Override
		protected String getHelp() {
			return "Returns a Double that is the step of the range field.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<SubRangeQueryField> setStep = new AmiAbstractMemberMethod<SubRangeQueryField>(SubRangeQueryField.class, "setRange", Object.class,
			Number.class) {
		@Override
		protected String getHelp() {
			return "Sets the step of the range field.";
		}
		protected String[] buildParamNames() {
			return new String[] { "step" };
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, SubRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
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
		return "FormRangeSliderField";
	}

	@Override
	public String getVarTypeDescription() {
		return "Field for defining a range with min and max values";
	}

	@Override
	public Class<SubRangeQueryField> getVarType() {
		return SubRangeQueryField.class;
	}

	@Override
	public Class<SubRangeQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormRangeSliderField INSTANCE = new AmiWebScriptMemberMethods_FormRangeSliderField();
}
