package com.f1.ami.amicommon.functions;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.f1.base.Mapping;
import com.f1.utils.EH;
import com.f1.utils.MH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator3;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionDatepartNum extends AbstractMethodDerivedCellCalculator3 {
	public static final int CODE_ERROR = 65536;
	public static final int CODE_YEAR = 1;
	public static final int CODE_MONTH = 2;
	public static final int CODE_QUARTER = 3;
	public static final int CODE_DAY_OF_YEAR = 4;
	public static final int CODE_HOUR = 5;
	public static final int CODE_MINUTE = 6;
	public static final int CODE_SECOND = 7;
	public static final int CODE_MILLISECOND = 8;
	public static final int CODE_DAY_IN_MONTH = 9;
	public static final int CODE_DAY_IN_WEEK = 10;
	public static final int CODE_DAY_OF_WEEK_IN_MONTH = 11;
	public static final int CODE_WEEK_IN_YEAR = 12;
	public static final int CODE_WEEK_IN_MONTH = 13;

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("datePartNum", Integer.class, "Number unixEpochMillis,String part,String timezone");
	static {
		VERIFIER.addDesc("Converts a datetime to an Integer. Returns an Integer that is the number of milliseconds since 1/1/1970 in UTC timezone.");
		VERIFIER.addParamDesc(0, "Number of milliseconds since 1/1/1970 in UTC timezone");
		VERIFIER.addParamDesc(1,
				"Field to return: y-year, M-month, d-day of month,,E-day of week,D-day of year,H-hour,m-minute,s-Second,S-millisecond,q-quarter,w-week in year,W-week in month,F-day of week in month");
		VERIFIER.addParamDesc(2, "TimeZone to return extracted fields in");
		VERIFIER.addExample(100000000, "y", "EST5EDT");
		VERIFIER.addExample(100000000, "HmsS", "UTC");
		VERIFIER.addExample(100000000, "E", "UTC");
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	private final Calendar calendar;
	private TimeZone tz;

	public AmiWebFunctionDatepartNum(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		super(position, p0, p1, p2);
		this.calendar = GregorianCalendar.getInstance();
		evalConsts();
	}

	@Override
	protected Integer get1(Object o1) {
		int r = getFlags((String) o1);
		if (MH.allBits(r, CODE_ERROR))
			return null;
		return r;
	}

	@Override
	protected TimeZone get2(Object o2) {
		return EH.getTimeZoneOrGMT((String) o2);
	}

	static private int getFlags(String string) {
		if (string == null || string.length() != 1)
			return CODE_ERROR;
		return getFlag(string.charAt(0));
	}
	static private int getFlag(char c) {
		switch (c) {
			case 'y':
				return CODE_YEAR;
			case 'M':
				return CODE_MONTH;
			case 'd':
				return CODE_DAY_IN_MONTH;
			case 'E':
				return CODE_DAY_IN_WEEK;
			case 'D':
				return CODE_DAY_OF_YEAR;
			case 'q':
				return CODE_QUARTER;
			case 'H':
				return CODE_HOUR;
			case 'm':
				return CODE_MINUTE;
			case 's':
				return CODE_SECOND;
			case 'S':
				return CODE_MILLISECOND;
			case 'w':
				return CODE_WEEK_IN_YEAR;
			case 'W':
				return CODE_WEEK_IN_MONTH;
			case 'F':
				return CODE_DAY_OF_WEEK_IN_MONTH;
			default:
				return CODE_ERROR;
		}
	}
	static private Integer getTime(Calendar calendar, long now, int parts) {
		calendar.setTimeInMillis(now);
		long r = 0;
		if (MH.allBits(parts, CODE_ERROR))
			return Integer.MIN_VALUE;
		calendar.clear(Calendar.DAY_OF_WEEK_IN_MONTH);
		calendar.clear(Calendar.WEEK_OF_MONTH);
		calendar.clear(Calendar.WEEK_OF_YEAR);
		calendar.clear(Calendar.DAY_OF_YEAR);
		calendar.clear(Calendar.DAY_OF_WEEK);
		calendar.clear(Calendar.AM_PM);
		calendar.clear(Calendar.HOUR);
		switch (parts) {
			case CODE_YEAR:
				return calendar.get(Calendar.YEAR);
			case CODE_MONTH:
				return calendar.get(Calendar.MONTH) + 1;
			case CODE_DAY_OF_YEAR:
				return calendar.get(Calendar.DAY_OF_YEAR);
			case CODE_DAY_IN_WEEK:
				return calendar.get(Calendar.DAY_OF_WEEK);
			case CODE_QUARTER:
				return (calendar.get(Calendar.MONTH) / 3) + 1;
			case CODE_DAY_IN_MONTH:
				return calendar.get(Calendar.DAY_OF_MONTH);
			case CODE_HOUR:
				return calendar.get(Calendar.HOUR_OF_DAY);
			case CODE_MINUTE:
				return calendar.get(Calendar.MINUTE);
			case CODE_SECOND:
				return calendar.get(Calendar.SECOND);
			case CODE_MILLISECOND:
				return calendar.get(Calendar.MILLISECOND);
			case CODE_WEEK_IN_YEAR:
				return calendar.get(Calendar.WEEK_OF_YEAR);
			case CODE_WEEK_IN_MONTH:
				return calendar.get(Calendar.WEEK_OF_MONTH);
			case CODE_DAY_OF_WEEK_IN_MONTH:
				return calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);
		}
		return null;
	}

	@Override
	public Object eval(Object o0, Object o1, Object o2) {
		Number value = (Number) o0;
		Integer flags = (Integer) o1;
		if (flags == null)
			return null;
		TimeZone tz = (TimeZone) o2;
		if (tz == null)
			return null;
		try {
			if (tz != this.tz) {
				this.tz = tz;
				this.calendar.setTimeZone(tz);
			}
		} catch (Exception e) {
			return null;
		}
		return getTime(this.calendar, value.longValue(), flags);
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionDatepartNum(getPosition(), p0, p1, p2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionDatepartNum(position, calcs[0], calcs[1], calcs[2]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
