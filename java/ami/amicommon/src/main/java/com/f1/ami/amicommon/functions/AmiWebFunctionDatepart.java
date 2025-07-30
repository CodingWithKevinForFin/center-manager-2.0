package com.f1.ami.amicommon.functions;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.f1.base.Mapping;
import com.f1.utils.MH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator3;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionDatepart extends AbstractMethodDerivedCellCalculator3 {
	public static final int MASK_ERROR = 65536;
	public static final int MASK_YEAR = 1;
	public static final int MASK_MONTH = 2;
	public static final int MASK_DAY = 4;
	public static final int MASK_HOUR = 8;
	public static final int MASK_MINUTE = 16;
	public static final int MASK_SECOND = 32;
	public static final int MASK_MILLISECOND = 64;

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("datePart", Long.class, "Number unixEpochMillis,String yMdHmsS,String timezone");
	static {
		VERIFIER.addDesc(
				"Converts a datetime to a time as number of milliseconds past midnight in the given timezone. Returns a Long that is the number of milliseonds since midnight in supplied timezone. Will be between 0 and 86,400,000");
		VERIFIER.addParamDesc(0, "Number of milliseconds since 1/1/1970 in UTC timezone");
		VERIFIER.addParamDesc(1, "Fields to return: y-year, M-month, d-day,H-hour,m-minute,s-Second,S-millisecond");
		VERIFIER.addParamDesc(2, "TimeZone to return extracted fields in");
		VERIFIER.addExample(100000000, "y", "EST5EDT");
		VERIFIER.addExample(100000000, "HmsS", "UTC");
	}

	private static final Object CONST_NULL = new Object();
	private Calendar calendar;
	private TimeZone tz;

	public AmiWebFunctionDatepart(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		super(position, p0, p1, p2);
		this.calendar = GregorianCalendar.getInstance();
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	protected Integer get1(Object o1) {
		int r = getFlags((String) o1);
		if (MH.allBits(r, MASK_ERROR))
			return null;
		return r;
	}
	@Override
	protected TimeZone get2(Object o1) {
		return TimeZone.getTimeZone((String) o1);
	}
	static private int getFlags(String string) {
		int r = 0;
		for (int i = 0, l = string.length(); i < l; i++) {
			r |= getFlag(string.charAt(i));
		}

		return r;
	}
	static private int getFlag(char c) {
		switch (c) {
			case 'y':
				return MASK_YEAR;
			case 'M':
				return MASK_MONTH;
			case 'd':
				return MASK_DAY;
			case 'H':
				return MASK_HOUR;
			case 'm':
				return MASK_MINUTE;
			case 's':
				return MASK_SECOND;
			case 'S':
				return MASK_MILLISECOND;
			default:
				return MASK_ERROR;
		}
	}
	static private long getTime(Calendar calendar, long now, int parts) {
		calendar.setTimeInMillis(now);
		if (MH.allBits(parts, MASK_ERROR))
			return Long.MIN_VALUE;
		calendar.clear(Calendar.DAY_OF_WEEK_IN_MONTH);
		calendar.clear(Calendar.WEEK_OF_MONTH);
		calendar.clear(Calendar.WEEK_OF_YEAR);
		calendar.clear(Calendar.DAY_OF_YEAR);
		calendar.clear(Calendar.DAY_OF_WEEK);
		calendar.clear(Calendar.AM_PM);
		calendar.clear(Calendar.HOUR);

		if (!MH.allBits(parts, MASK_MILLISECOND))
			calendar.clear(Calendar.MILLISECOND);
		if (!MH.allBits(parts, MASK_SECOND))
			calendar.clear(Calendar.SECOND);
		if (!MH.allBits(parts, MASK_MINUTE))
			calendar.clear(Calendar.MINUTE);
		if (!MH.allBits(parts, MASK_HOUR))
			calendar.clear(Calendar.HOUR_OF_DAY);
		if (!MH.allBits(parts, MASK_DAY))
			calendar.clear(Calendar.DAY_OF_MONTH);
		if (!MH.allBits(parts, MASK_MONTH))
			calendar.clear(Calendar.MONTH);
		if (!MH.allBits(parts, MASK_YEAR))
			calendar.clear(Calendar.YEAR);
		return calendar.getTimeInMillis();
	}

	@Override
	public Object eval(Object o0, Object o1, Object o2) {
		if (o1 == null || o2 == null)
			return null;
		final Number value = (Number) o0;
		try {
			TimeZone tz = (TimeZone) o2;
			if (tz != this.tz) {
				this.tz = tz;
				this.calendar.setTimeZone(tz);
			}
		} catch (Exception e) {
			return null;
		}
		int flags = (Integer) o1;
		return getTime(this.calendar, value.longValue(), flags);
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionDatepart(getPosition(), p0, p1, p2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionDatepart(position, calcs[0], calcs[1], calcs[2]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
