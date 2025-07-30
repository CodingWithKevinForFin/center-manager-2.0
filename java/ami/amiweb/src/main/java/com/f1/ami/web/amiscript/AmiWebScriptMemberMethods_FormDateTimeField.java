package com.f1.ami.web.amiscript;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.DateTimeQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormDateTimeField extends AmiWebScriptBaseMemberMethods<DateTimeQueryField> {

	private AmiWebScriptMemberMethods_FormDateTimeField() {
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
		addMethod(GET_DAY, "day");
		addMethod(GET_DAY_TZ);
		addMethod(GET_MONTH, "month");
		addMethod(GET_MONTH_TZ);
		addMethod(GET_YEAR, "year");
		addMethod(GET_YEAR_TZ);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
	}

	private static final AmiAbstractMemberMethod<DateTimeQueryField> GET_DAY = new AmiAbstractMemberMethod<DateTimeQueryField>(DateTimeQueryField.class, "getDay", Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateTimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getDay();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the day of the current value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<DateTimeQueryField> GET_DAY_TZ = new AmiAbstractMemberMethod<DateTimeQueryField>(DateTimeQueryField.class, "getDay", Number.class,
			String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateTimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getDay(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the day of the current value in a specific timezone using the timeZoneId.";
		}
		protected String[] buildParamNames() {
			return new String[] { "timeZoneId" };
		};
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<DateTimeQueryField> GET_MONTH = new AmiAbstractMemberMethod<DateTimeQueryField>(DateTimeQueryField.class, "getMonth",
			Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateTimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMonth();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the month of the current value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<DateTimeQueryField> GET_MONTH_TZ = new AmiAbstractMemberMethod<DateTimeQueryField>(DateTimeQueryField.class, "getMonth",
			Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateTimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMonth(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the month of the current value in a specific timezone using the timeZoneId.";
		}
		protected String[] buildParamNames() {
			return new String[] { "timeZoneId" };
		};
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<DateTimeQueryField> GET_YEAR = new AmiAbstractMemberMethod<DateTimeQueryField>(DateTimeQueryField.class, "getYear", Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateTimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getYear();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the year of the current value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<DateTimeQueryField> GET_YEAR_TZ = new AmiAbstractMemberMethod<DateTimeQueryField>(DateTimeQueryField.class, "getYear",
			Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateTimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getYear(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the year of the current value in a specific timezone using the timeZoneId.";
		}
		protected String[] buildParamNames() {
			return new String[] { "timeZoneId" };
		};
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<DateTimeQueryField> GET_MINUTE = new AmiAbstractMemberMethod<DateTimeQueryField>(DateTimeQueryField.class, "getMinute",
			Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateTimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMinute();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the minute of the current value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<DateTimeQueryField> GET_MINUTE_TZ = new AmiAbstractMemberMethod<DateTimeQueryField>(DateTimeQueryField.class, "getMinute",
			Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateTimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
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
	private static final AmiAbstractMemberMethod<DateTimeQueryField> GET_HOUR = new AmiAbstractMemberMethod<DateTimeQueryField>(DateTimeQueryField.class, "getHour", Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateTimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHour();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the hour (1-12) of the current value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<DateTimeQueryField> GET_HOUR_TZ = new AmiAbstractMemberMethod<DateTimeQueryField>(DateTimeQueryField.class, "getHour",
			Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateTimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHour(Caster_String.INSTANCE.cast(params[0]));
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the hour  (1-12) of the current value in a specific timezone using the timeZoneId.";
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
	private static final AmiAbstractMemberMethod<DateTimeQueryField> GET_HOUR_OF_DAY = new AmiAbstractMemberMethod<DateTimeQueryField>(DateTimeQueryField.class, "getHourOfDay",
			Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateTimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHourOfDay();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the hour (0-23) of the current value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<DateTimeQueryField> GET_HOUR_OF_DAY_TZ = new AmiAbstractMemberMethod<DateTimeQueryField>(DateTimeQueryField.class, "getHourOfDay",
			Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateTimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
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
	private static final AmiAbstractMemberMethod<DateTimeQueryField> GET_IS_PM = new AmiAbstractMemberMethod<DateTimeQueryField>(DateTimeQueryField.class, "getIsPm",
			Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateTimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getIsPM();
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
	private static final AmiAbstractMemberMethod<DateTimeQueryField> GET_IS_PM_TZ = new AmiAbstractMemberMethod<DateTimeQueryField>(DateTimeQueryField.class, "getIsPm",
			Boolean.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateTimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getIsPM(Caster_String.INSTANCE.cast(params[0]));
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
	private static final AmiAbstractMemberMethod<DateTimeQueryField> GET_SECOND = new AmiAbstractMemberMethod<DateTimeQueryField>(DateTimeQueryField.class, "getSecond",
			Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateTimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSecond();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the second of the current value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<DateTimeQueryField> GET_SECOND_TZ = new AmiAbstractMemberMethod<DateTimeQueryField>(DateTimeQueryField.class, "getSecond",
			Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateTimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
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
	private static final AmiAbstractMemberMethod<DateTimeQueryField> GET_MILLIS = new AmiAbstractMemberMethod<DateTimeQueryField>(DateTimeQueryField.class, "getMillis",
			Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateTimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMillis();
		}
		@Override
		protected String getHelp() {
			return "Returns a Number that is the millisecond of the current value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<DateTimeQueryField> GET_MILLIS_TZ = new AmiAbstractMemberMethod<DateTimeQueryField>(DateTimeQueryField.class, "getMillis",
			Number.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateTimeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
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

	@Override
	public String getVarTypeName() {
		return "FormDateTimeField";
	}

	@Override
	public String getVarTypeDescription() {
		return "Date-time field";
	}

	@Override
	public Class<DateTimeQueryField> getVarType() {
		return DateTimeQueryField.class;
	}

	@Override
	public Class<DateTimeQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormDateTimeField INSTANCE = new AmiWebScriptMemberMethods_FormDateTimeField();

}
