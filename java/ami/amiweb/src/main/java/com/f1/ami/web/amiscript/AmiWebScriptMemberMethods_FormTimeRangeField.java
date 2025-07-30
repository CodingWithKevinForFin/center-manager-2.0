package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.TimeRangeQueryField;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormTimeRangeField extends AmiWebScriptBaseMemberMethods<TimeRangeQueryField> {

	private AmiWebScriptMemberMethods_FormTimeRangeField() {
		super();
		addMethod(GET_MINUTE_START, "minuteStart");
		addMethod(GET_MINUTE_START_TZ);
		addMethod(GET_HOUR_START, "hourStart");
		addMethod(GET_HOUR_START_TZ);
		addMethod(GET_HOUR_OF_DAY, "hourOfDayStart");
		addMethod(GET_HOUR_OF_DAY_START_TZ);
		addMethod(GET_IS_PM_START, "pmStart");
		addMethod(GET_IS_PM_START_TZ);
		addMethod(GET_SECOND_START, "secondStart");
		addMethod(GET_SECOND_START_TZ);
		addMethod(GET_MILLIS_START, "millisStart");
		addMethod(GET_MILLIS_START_TZ);

		addMethod(GET_MINUTE_END, "minuteEnd");
		addMethod(GET_MINUTE_END_TZ);
		addMethod(GET_HOUR_END, "hourEnd");
		addMethod(GET_HOUR_END_TZ);
		addMethod(GET_HOUR_OF_DAY_END, "hourOfDayEnd");
		addMethod(GET_HOUR_OF_DAY_END_TZ);
		addMethod(GET_IS_PM_END, "pmEnd");
		addMethod(GET_IS_PM_END_TZ);
		addMethod(GET_SECOND_END, "secondEnd");
		addMethod(GET_SECOND_END_TZ);
		addMethod(GET_MILLIS_END, "millisEnd");
		addMethod(GET_MILLIS_END_TZ);
		addMethod(SET_RANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
	}

	public static final AmiAbstractMemberMethod<TimeRangeQueryField> SET_RANGE = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class, "setRange",
			Boolean.class, Number.class, Number.class) {
		@Override
		public Boolean invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Long start = Caster_Long.INSTANCE.cast(params[0]);
			Long end = Caster_Long.INSTANCE.cast(params[1]);

			targetObject.getField().setValue(new Tuple2<Long, Long>(start, end));
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "startValue", "endValue" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "startValue", "endValue" };
		}
		@Override
		protected String getHelp() {
			return "Sets the time range's start and end value.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_MINUTE_START = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class, "getMinuteStart",
			Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMinuteStart();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the minute on the LEFT hand side of the time range in UTC.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_MINUTE_START_TZ = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class,
			"getMinuteStart", Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMinuteStart(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the minute on the LEFT hand side of the time range in specified time zone.";
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
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_HOUR_START = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class, "getHourStart",
			Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHourStart();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the hour (1-12) on the LEFT hand side of the time range in UTC.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_HOUR_START_TZ = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class, "getHourStart",
			Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHourStart(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the hour (1-12) on the LEFT hand side of the time range in specified time zone.";
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
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_HOUR_OF_DAY = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class,
			"getHourOfDayStart", Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHourOfDayStart();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the hour (0-23) on the LEFT hand side of the time range in UTC.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_HOUR_OF_DAY_START_TZ = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class,
			"getHourOfDayStart", Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHourOfDayStart(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the hour (0-23) on the LEFT hand side of the time range in specified time zone.";
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
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_SECOND_START = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class,
			"getSecondStart", Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSecondStart();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the second on the LEFT hand side of the time range in UTC.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_SECOND_START_TZ = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class,
			"getSecondStart", Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSecondStart(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the second on the LEFT hand side of the time range in specified time zone.";
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
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_MILLIS_START = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class,
			"getMillisStart", Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMillisStart();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the millisecond on the LEFT hand side of the time range in UTC.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_MILLIS_START_TZ = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class,
			"getMillisStart", Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMillisStart(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the millisecond on the LEFT hand side of the time range in specified time zone.";
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

	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_MINUTE_END = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class, "getMinuteEnd",
			Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMinuteEnd();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the minute on the RIGHT hand side of the time range in UTC.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_MINUTE_END_TZ = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class, "getMinuteEnd",
			Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMinuteEnd(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the minute on the RIGHT hand side of the time range in specified time zone.";
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
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_HOUR_END = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class, "getHourEnd",
			Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHourEnd();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the hour (1-12) on the RIGHT hand side of the time range in UTC.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_HOUR_END_TZ = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class, "getHourEnd",
			Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHourEnd(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the hour (1-12) on the RIGHT hand side of the time range in specified time zone.";
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
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_HOUR_OF_DAY_END = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class,
			"getHourOfDayEnd", Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHourOfDayEnd();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the hour (0-23) on the RIGHT hand side of the time range in UTC.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_HOUR_OF_DAY_END_TZ = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class,
			"getHourOfDayEnd", Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHourOfDayEnd(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the hour (0-23) on the RIGHT hand side of the time range in specified time zone.";
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
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_SECOND_END = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class, "getSecondEnd",
			Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSecondEnd();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the second on the RIGHT hand side of the time range in UTC.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_SECOND_END_TZ = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class, "getSecondEnd",
			Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSecondEnd(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the second on the RIGHT hand side of the time range in specified time zone.";
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
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_MILLIS_END = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class, "getMillisEnd",
			Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMillisEnd();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the millisecond on the RIGHT hand side of the time range in UTC.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_MILLIS_END_TZ = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class, "getMillisEnd",
			Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMillisEnd(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the millisecond on the RIGHT hand side of the time range in specified time zone.";
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

	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_IS_PM_END = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class, "getIsPmEnd",
			Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getIsPmEnd();
		}
		@Override
		protected String getHelp() {
			return "Returns true if the hour is in PM on the RIGHT hand side of the time range in UTC.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_IS_PM_END_TZ = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class, "getIsPmEnd",
			Boolean.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getIsPmEnd(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns true if the hour is in PM on the RIGHT hand side of the time range in specified time zone.";
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
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_IS_PM_START = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class, "getIsPmStart",
			Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getIsPmStart();
		}
		@Override
		protected String getHelp() {
			return "Returns true if the hour is in PM on the LEFT hand side of the time range in UTC.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	public static final AmiAbstractMemberMethod<TimeRangeQueryField> GET_IS_PM_START_TZ = new AmiAbstractMemberMethod<TimeRangeQueryField>(TimeRangeQueryField.class,
			"getIsPmStart", Boolean.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, TimeRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getIsPmStart(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns true if the hour is in PM on the LEFT hand side of the time range in specified time zone.";
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
		return "FormTimeRangeField";
	}

	@Override
	public String getVarTypeDescription() {
		return "Time Range field";
	}

	@Override
	public Class<TimeRangeQueryField> getVarType() {
		return TimeRangeQueryField.class;
	}

	@Override
	public Class<TimeRangeQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormTimeRangeField INSTANCE = new AmiWebScriptMemberMethods_FormTimeRangeField();
}
