package com.f1.ami.amicommon.functions;

import java.util.TimeZone;

import com.f1.base.Mapping;
import com.f1.utils.DateFormatNano;
import com.f1.utils.EH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator3;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionParseDateTz extends AbstractMethodDerivedCellCalculator3 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("parseDate", Long.class, "String text,String format,String timezone");
	static {
		VERIFIER.addDesc("Converts a string to a unix epoch time. Returns a Long or null. ");
		VERIFIER.addParamDesc(0, "String to parse");
		VERIFIER.addParamDesc(1, "Pattern to expect string to be in, see java SimpleDateFormat for options");
		VERIFIER.addParamDesc(2, "Timezone to expect string is in");
		VERIFIER.addExample("201505030-10:30:40.123", "yyyyMMdd-HH:mm:ss.SSS", "EST5EDT");
		VERIFIER.addExample("Jan/03/1982-10:30:40", "MMMM/dd/yyyy-HH:mm:ss", "UTC");
	}

	public AmiWebFunctionParseDateTz(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		super(position, p0, p1, p2);
		evalConsts();
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	protected DateFormatNano get1(Object o1) {
		try {
			String format = (String) o1;
			return new DateFormatNano(format);
		} catch (Exception e) {
			return null;
		}
	}
	@Override
	protected Object get2(Object o2) {
		return EH.getTimeZoneOrGMT((String) o2);
	}

	@Override
	public Object eval(Object o0, Object o1, Object o2) {
		String val = (String) o0;
		DateFormatNano sdf = (DateFormatNano) o1;
		if (sdf == null)
			return null;
		TimeZone tz = (TimeZone) o2;
		sdf.setTimeZone(tz);
		return parse(val, sdf);
	}

	private Object parse(final String value, final DateFormatNano sdf) {
		try {
			return sdf.parse(value).getTime();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionParseDateTz(getPosition(), p0, p1, p2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionParseDateTz(position, calcs[0], calcs[1], calcs[2]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
