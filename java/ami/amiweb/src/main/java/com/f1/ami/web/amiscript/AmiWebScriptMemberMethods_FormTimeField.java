package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.TimeQueryField;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormTimeField extends AmiWebScriptBaseMemberMethods<TimeQueryField> {

	private AmiWebScriptMemberMethods_FormTimeField() {
		super();
		addMethod(GET_MINUTE, "minute");
		addMethod(GET_MINUTE_TZ);
		addMethod(GET_HOUR, "hour");
		addMethod(GET_HOUR_TZ);
		addMethod(GET_HOUR_OF_DAY, "hourOfDay");
		addMethod(GET_HOUR_OF_DAY_TZ);
		addMethod(GET_IS_PM, "pm");
		addMethod(GET_IS_PM_TZ);
		addMethod(GET_SECOND, "second");
		addMethod(GET_SECOND_TZ);
		addMethod(GET_MILLIS, "millis");
		addMethod(GET_MILLIS_TZ);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
	}

	public static final AmiAbstractMemberMethod<TimeQueryField> GET_MINUTE = new AmiAbstractMemberMethod<TimeQueryField>(TimeQueryField.class, "getMinute", Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMinute();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the minute of the current value in UTC.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeQueryField> GET_MINUTE_TZ = new AmiAbstractMemberMethod<TimeQueryField>(TimeQueryField.class, "getMinute", Number.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMinute(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the minute of the current value in a specific timezone using the timeZoneId.";
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "timeZoneId" };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeQueryField> GET_HOUR = new AmiAbstractMemberMethod<TimeQueryField>(TimeQueryField.class, "getHour", Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHour();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the hour (1-12) of the current value in UTC.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeQueryField> GET_HOUR_TZ = new AmiAbstractMemberMethod<TimeQueryField>(TimeQueryField.class, "getHour", Number.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHour(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the hour (1-12) of the current value in a specific timezone using the timeZoneId.";
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "timeZoneId" };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeQueryField> GET_HOUR_OF_DAY = new AmiAbstractMemberMethod<TimeQueryField>(TimeQueryField.class, "getHourOfDay", Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHourOfDay();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the hour (0-23) of the current value in UTC.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeQueryField> GET_HOUR_OF_DAY_TZ = new AmiAbstractMemberMethod<TimeQueryField>(TimeQueryField.class, "getHourOfDay", Number.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHourOfDay(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the hour (0-23) of the current value in a specific timezone using the timeZoneId.";
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "timeZoneId" };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeQueryField> GET_SECOND = new AmiAbstractMemberMethod<TimeQueryField>(TimeQueryField.class, "getSecond", Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSecond();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the second of the current value in UTC.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeQueryField> GET_SECOND_TZ = new AmiAbstractMemberMethod<TimeQueryField>(TimeQueryField.class, "getSecond", Number.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSecond(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the second of the current value in a specific timezone using the timeZoneId.";
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "timeZoneId" };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	public static final AmiAbstractMemberMethod<TimeQueryField> GET_MILLIS = new AmiAbstractMemberMethod<TimeQueryField>(TimeQueryField.class, "getMillis", Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMillis();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the millisecond of the current value in UTC.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeQueryField> GET_MILLIS_TZ = new AmiAbstractMemberMethod<TimeQueryField>(TimeQueryField.class, "getMillis", Number.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMillis(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the millisecond of the current value in a specific timezone using the timeZoneId.";
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "timeZoneId" };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeQueryField> GET_IS_PM = new AmiAbstractMemberMethod<TimeQueryField>(TimeQueryField.class, "getIsPm", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getIsPm();
		}
		@Override
		protected String getHelp() {
			return "Returns true if the hour is in PM";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeQueryField> GET_IS_PM_TZ = new AmiAbstractMemberMethod<TimeQueryField>(TimeQueryField.class, "getIsPm", Boolean.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getIsPm(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns true if the hour is in PM in a specific timezone using the timeZoneId.";
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "timeZoneId" };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "FormTimeField";
	}

	@Override
	public String getVarTypeDescription() {
		return "Time field";
	}

	@Override
	public Class<TimeQueryField> getVarType() {
		return TimeQueryField.class;
	}

	@Override
	public Class<TimeQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormTimeField INSTANCE = new AmiWebScriptMemberMethods_FormTimeField();

}
