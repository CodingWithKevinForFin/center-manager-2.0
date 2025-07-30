package com.f1.ami.web.amiscript;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.DateQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.base.DateMillis;
import com.f1.utils.BasicDay;
import com.f1.utils.DateFormatNano;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormDateField extends AmiWebScriptBaseMemberMethods<DateQueryField> {

	private AmiWebScriptMemberMethods_FormDateField() {
		super();
		addMethod(GET_DAY, "day");
		addMethod(GET_MONTH, "month");
		addMethod(GET_YEAR, "year");
		addMethod(SET_YYYYMMDD);
		addMethod(GET_YYYYMMDD);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
	}

	private static final AmiAbstractMemberMethod<DateQueryField> GET_DAY = new AmiAbstractMemberMethod<DateQueryField>(DateQueryField.class, "getDay", Byte.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getDay();
		}
		@Override
		protected String getHelp() {
			return "Get day for current value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<DateQueryField> GET_MONTH = new AmiAbstractMemberMethod<DateQueryField>(DateQueryField.class, "getMonth", Byte.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getMonth();
		}
		@Override
		protected String getHelp() {
			return "Returns a Byte that is the month of the current value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<DateQueryField> GET_YEAR = new AmiAbstractMemberMethod<DateQueryField>(DateQueryField.class, "getYear", Short.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getYear();
		}
		@Override
		protected String getHelp() {
			return "Returns a Byte that is the year of the current value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<DateQueryField> SET_YYYYMMDD = new AmiAbstractMemberMethod<DateQueryField>(DateQueryField.class, "setYYYYMMDD", Object.class,
			Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			Long date = Caster_Long.INSTANCE.cast(params[0]);
			TimeZone tz = targetObject.getField().getTimeZone();
			try {
				Long dateInMilliseconds = validateAndReturnDateInMilliseconds(date, tz);
				targetObject.setValue(dateInMilliseconds);
			} catch (ParseException e) {
				return null;
			}
			return null;
		}
		@Override
		protected String getHelp() {
			return "Sets YYYYMMDD as the value.";
		}
		protected String[] buildParamNames() {
			return new String[] { "newDate" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}

	};

	private static Long validateAndReturnDateInMilliseconds(Long date, TimeZone tz) throws ParseException {
		String yyyymmdd = Caster_String.INSTANCE.cast(date);
		if (SH.isnt(yyyymmdd))
			throw new ParseException("Error Parsing Date. Please verify format", 10);
		yyyymmdd = yyyymmdd.trim();
		if (yyyymmdd.length() != 8 || !SH.areBetween(yyyymmdd, '0', '9')) {
			throw new ParseException("Error Parsing Date. Please verify format", 10);
		}
		DateFormatNano dateFormatter = new DateFormatNano("yyyyMMdd");
		dateFormatter.setTimeZone(tz);
		long unixTimestamp = dateFormatter.parse(SH.s(date)).getTime();
		return unixTimestamp;
	}

	private static final AmiAbstractMemberMethod<DateQueryField> GET_YYYYMMDD = new AmiAbstractMemberMethod<DateQueryField>(DateQueryField.class, "getYYYYMMDD", Long.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return parseDateFromDateQueryField(targetObject);
		}
		@Override
		protected String getHelp() {
			return "Returns a long that represents YYYYMMDD from the field.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static Long parseDateFromDateQueryField(DateQueryField targetObject) {
		try {
			DateMillis dms = (DateMillis) targetObject.getValue();
			TimeZone tz = targetObject.getField().getTimeZone();
			BasicDay bd = new BasicDay(tz, new Date(dms.longValue()));
			return Caster_Long.INSTANCE.cast("" + bd.getYear() + appendZeroIfSingleDigit(SH.toString(bd.getMonth())) + appendZeroIfSingleDigit(SH.toString(bd.getDay())));
		} catch (Exception e) {
			return null;
		}
	}
	private static String appendZeroIfSingleDigit(String dayOrMonth) {
		return SH.length(dayOrMonth) == 1 ? "0" + dayOrMonth : dayOrMonth;
	}
	@Override
	public String getVarTypeName() {
		return "FormDateField";
	}

	@Override
	public String getVarTypeDescription() {
		return "Date field";
	}

	@Override
	public Class<DateQueryField> getVarType() {
		return DateQueryField.class;
	}

	@Override
	public Class<DateQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormDateField INSTANCE = new AmiWebScriptMemberMethods_FormDateField();
}
