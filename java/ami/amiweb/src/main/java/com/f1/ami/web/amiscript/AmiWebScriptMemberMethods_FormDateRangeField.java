package com.f1.ami.web.amiscript;

import java.util.Date;
import java.util.TimeZone;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.form.queryfield.DateRangeQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.base.Day;
import com.f1.utils.BasicDay;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_FormDateRangeField extends AmiWebScriptBaseMemberMethods<DateRangeQueryField> {

	private AmiWebScriptMemberMethods_FormDateRangeField() {
		super();
		addMethod(GET_START_DAY, "startDay");
		addMethod(GET_START_MONTH, "startMonth");
		addMethod(GET_START_YEAR, "startYear");
		addMethod(GET_END_DAY, "endDay");
		addMethod(GET_END_MONTH, "endMonth");
		addMethod(GET_END_YEAR, "endYear");
		addMethod(SET_RANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONCHANGE);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONENTERKEY);
		registerCallbackDefinition(QueryField.CALLBACK_DEF_ONFOCUS);
	}

	private static final AmiAbstractMemberMethod<DateRangeQueryField> SET_RANGE = new AmiAbstractMemberMethod<DateRangeQueryField>(DateRangeQueryField.class, "setRange",
			Boolean.class, Number.class, Number.class) {
		@Override
		public Boolean invokeMethod2(CalcFrameStack sf, DateRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			TimeZone tz = targetObject.getField().getTimeZone();
			Long start = Caster_Long.INSTANCE.cast(params[0]);
			Long end = Caster_Long.INSTANCE.cast(params[1]);
			BasicDay bds = start == null ? null : new BasicDay(tz, new Date(start));
			BasicDay bde = end == null ? null : new BasicDay(tz, new Date(end));

			targetObject.getField().setValue(new Tuple2<Day, Day>(bds, bde));
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "value1", "value2" };
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
	private static final AmiAbstractMemberMethod<DateRangeQueryField> GET_START_DAY = new AmiAbstractMemberMethod<DateRangeQueryField>(DateRangeQueryField.class, "getStartDay",
			Byte.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getStartDay();
		}
		@Override
		protected String getHelp() {
			return "Returns a Byte that is the start day of the current value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<DateRangeQueryField> GET_END_DAY = new AmiAbstractMemberMethod<DateRangeQueryField>(DateRangeQueryField.class, "getEndDay",
			Byte.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getEndDay();
		}
		@Override
		protected String getHelp() {
			return "Returns a Byte that is the end day of the current value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<DateRangeQueryField> GET_START_MONTH = new AmiAbstractMemberMethod<DateRangeQueryField>(DateRangeQueryField.class, "getStartMonth",
			Byte.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getStartMonth();
		}
		@Override
		protected String getHelp() {
			return "Returns a Byte that is the start month of the current value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<DateRangeQueryField> GET_END_MONTH = new AmiAbstractMemberMethod<DateRangeQueryField>(DateRangeQueryField.class, "getEndMonth",
			Byte.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getEndMonth();
		}
		@Override
		protected String getHelp() {
			return "Returns a Byte that is the end month of the current value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<DateRangeQueryField> GET_START_YEAR = new AmiAbstractMemberMethod<DateRangeQueryField>(DateRangeQueryField.class, "getStartYear",
			Short.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getStartYear();
		}
		@Override
		protected String getHelp() {
			return "Returns a Short that is the start year of the current value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<DateRangeQueryField> GET_END_YEAR = new AmiAbstractMemberMethod<DateRangeQueryField>(DateRangeQueryField.class, "getEndYear",
			Short.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, DateRangeQueryField targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getEndYear();
		}
		@Override
		protected String getHelp() {
			return "Returns a Short that is the end year of the current value.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "FormDateRangeField";
	}

	@Override
	public String getVarTypeDescription() {
		return "Date range field";
	}

	@Override
	public Class<DateRangeQueryField> getVarType() {
		return DateRangeQueryField.class;
	}

	@Override
	public Class<DateRangeQueryField> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_FormDateRangeField INSTANCE = new AmiWebScriptMemberMethods_FormDateRangeField();
}
