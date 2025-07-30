package com.f1.ami.web.functions;

import java.util.TimeZone;

import com.f1.ami.amicommon.functions.AmiWebFunctionFactory;
import com.f1.ami.amicommon.functions.AmiWebFunctionFormatDate;
import com.f1.ami.web.AmiWebService;
import com.f1.base.Mapping;
import com.f1.utils.DateFormatNano;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionFormatDate2 extends AbstractMethodDerivedCellCalculator2 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatDate", String.class, "Number unixEpoch,String pattern");
	private final TimeZone timezone;
	static {
		VERIFIER.addDesc(
				"Formats the supplied unix epoch time. Returns a legible string date/time based on the supplied pattern. The pattern is constructed by combining words listed below "
						+ AmiWebFunctionFormatDate.DATE_FORMAT_HELP + ".");
		VERIFIER.addParamDesc(0,
				"Number of milli,micro or nanoseconds since 1/1/1970 in UTC timezone, depending on pattern. If the pattern contains n then nanoseconds, If the pattern contains u then microseconds, otherwise milliseconds.");
		VERIFIER.addParamDesc(1, "Pattern string. See Letters above for details.");
		VERIFIER.addExample(1464215270786L, "yyyyMMdd-HH:mm:ss.SSS");
		VERIFIER.addExample(1464215270786L, "MMMM/dd/yyyy-HH:mm:ss zzz");
		VERIFIER.addExample(1540407446123456L, "MMMM/dd/yyyy-HH:mm:ss.SSS.rrr zzz");
		VERIFIER.addExample(1540407446123456789L, "MMMM/dd/yyyy-HH:mm:ss.SSS.rrr.RRR zzz");
		VERIFIER.addExample(113232L, "HH:mm:ss");
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	public AmiWebFunctionFormatDate2(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, TimeZone timezone) {
		super(position, p0, p1);
		this.timezone = timezone;
		evalConsts();
	}

	@Override
	protected Object get1(Object o1) {
		try {
			DateFormatNano r = new DateFormatNano((String) o1);
			r.setTimeZone(timezone);
			return r;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Object eval(Object o0, Object o1) {
		Number value = (Number) o0;
		DateFormatNano sdf = (DateFormatNano) o1;
		if (sdf == null)
			return "<BAD_PATTERN>";
		return sdf.format(value);
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionFormatDate2(getPosition(), p0, p1, timezone);
	}

	public static class Factory implements AmiWebFunctionFactory {

		private AmiWebService service;

		public Factory(AmiWebService service) {
			this.service = service;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionFormatDate2(position, calcs[0], calcs[1], service.getVarsManager().getTimeZone());
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
