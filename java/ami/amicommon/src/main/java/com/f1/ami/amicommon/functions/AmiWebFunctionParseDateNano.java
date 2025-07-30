package com.f1.ami.amicommon.functions;

import com.f1.base.DateNanos;
import com.f1.base.Mapping;
import com.f1.utils.DateFormatNano;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionParseDateNano extends AbstractMethodDerivedCellCalculator2 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("parseDateNano", DateNanos.class, "String text,String format");
	static {
		VERIFIER.addDesc("Deprecated, use parseDate(text,format,timezone) instead");
		VERIFIER.addRetDesc("UTCN or null");
		VERIFIER.addParamDesc(0, "String to parse");
		VERIFIER.addParamDesc(1, "Pattern to expect string to be in, see java SimpleDateFormat for options");
	}

	public AmiWebFunctionParseDateNano(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	protected Object get1(Object o1) {
		try {
			return new DateFormatNano((String) o1);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Object eval(Object o1, Object o2) {
		String value = (String) o1;
		if (o2 == null)
			return null;
		return parse(value, (DateFormatNano) o2);
	}

	private DateNanos parse(final String value, final DateFormatNano sdf) {
		try {
			return sdf.parseToNanos(value);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionParseDateNano(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionParseDateNano(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
