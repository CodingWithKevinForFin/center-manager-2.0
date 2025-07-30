package com.f1.ami.amicommon.functions;

import java.util.Date;
import java.util.TimeZone;

import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionTimezoneOffset extends AbstractMethodDerivedCellCalculator2 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("timezoneOffset", Long.class, "Number unixEpochMillis,String timezone");

	static {
		VERIFIER.addDesc(
				"Returns the milliseconds offset of the supplied time zone from UTC at the specified date/time. If Daylight Saving Time is in effect at the specified date, the offset value is adjusted with the amount of daylight saving.");
		VERIFIER.addParamDesc(0, "Number of milliseconds since 1/1/1970 in UTC timezone");
		VERIFIER.addParamDesc(1, "TimeZone to get offset for");
		VERIFIER.addExample(0, "EST5EDT");
		VERIFIER.addExample(1152590400000L, "EST5EDT");
		VERIFIER.addExample(1165640400000L, "EST5EDT");
		VERIFIER.addExample(1234567L, "UTC");
	}

	public AmiWebFunctionTimezoneOffset(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	protected TimeZone get1(Object o1) {
		return TimeZone.getTimeZone((String) o1);
	}

	@Override
	public Object eval(Object o0, Object o1) {
		Number value = (Number) o0;
		TimeZone tz = (TimeZone) o1;
		if (tz == null)
			return null;
		return getOffset(tz, value);
	}

	private final Date tmp = new Date(0);

	private long getOffset(TimeZone tz2, Number longValue) {
		tmp.setTime(longValue.longValue());
		if (tz2.inDaylightTime(tmp))
			return tz2.getRawOffset() + tz2.getDSTSavings();
		return tz2.getRawOffset();
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionTimezoneOffset(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionTimezoneOffset(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
