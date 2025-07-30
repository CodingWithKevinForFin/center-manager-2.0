package com.f1.ami.amicommon.functions;

import java.text.DecimalFormat;

import com.f1.base.Mapping;
import com.f1.utils.formatter.BasicNumberFormatter;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator3;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionFormatNumber extends AbstractMethodDerivedCellCalculator3 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatNumber", String.class, "Number value,String format,String options");

	static {
		VERIFIER.addDesc("Converts a numeric value to string based on the supplied format.");
		VERIFIER.addParamDesc(0, "value to convert to string");
		VERIFIER.addParamDesc(1, "pattern for converting value. See java DecimalFormat for details");
		VERIFIER.addParamDesc(2, "Place holder, leave as empty string");
		VERIFIER.addExample(123.456, "#", "");
		VERIFIER.addExample(123.456, "#,###", "");
		VERIFIER.addExample(123.456, "#,###.000", "");
		VERIFIER.addExample(123.456, "#,###.E0", "");
	}

	public AmiWebFunctionFormatNumber(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		super(position, p0, p1, p2);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	protected BasicNumberFormatter get1(Object o1) {
		try {
			if (o1 == null)
				return null;
			String format = (String) o1;
			return new BasicNumberFormatter(new DecimalFormat(format));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected boolean shortCircuitNull() {
		return false;
	}

	@Override
	public Object eval(Object o0, Object o1, Object o2) {
		if (o0 == null)
			return null;
		final BasicNumberFormatter sdf = (BasicNumberFormatter) o1;
		if (sdf == null)
			return "<BAD_PATTERN>";
		Number value = (Number) o0;
		return sdf.format(value);
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionFormatNumber(getPosition(), p0, p1, p2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionFormatNumber(position, calcs[0], calcs[1], calcs[2]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
