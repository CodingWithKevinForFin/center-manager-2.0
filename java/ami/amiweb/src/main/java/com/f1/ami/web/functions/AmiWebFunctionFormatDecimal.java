package com.f1.ami.web.functions;

import com.f1.ami.amicommon.functions.AmiWebFunctionFactory;
import com.f1.ami.web.AmiWebFormatterManager;
import com.f1.ami.web.AmiWebService;
import com.f1.base.Mapping;
import com.f1.utils.Formatter;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionFormatDecimal extends AbstractMethodDerivedCellCalculator2 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatDecimal", String.class, "Number value,Integer decimalPrecision");
	private AmiWebFormatterManager formatManager;
	static {
		VERIFIER.addDesc("Formats a number to a legible string based on the users settings, with the given number of digits after the decimal. Returns that string.");
		VERIFIER.addParamDesc(0, "Number to format as string");
		VERIFIER.addParamDesc(1, "Number of digits after the decimal, note zero means no digits, null will use user default number of digits");
		VERIFIER.addExample(14642.150786, 8);
		VERIFIER.addExample(14642.150786, 2);
		VERIFIER.addExample(14642.150786, 0);
		VERIFIER.addExample(14642.150786, null);
	}

	public AmiWebFunctionFormatDecimal(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, AmiWebFormatterManager formatManager) {
		super(position, p0, p1);
		this.formatManager = formatManager;
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	protected Formatter get1(Object o1) {
		return this.formatManager.getDecimalFormatter((Integer) o1);
	}

	@Override
	public Object eval(Object o0, Object o1) {
		Number value = (Number) o0;
		final Formatter sdf = (Formatter) o1;
		return sdf.format(value);
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionFormatDecimal(getPosition(), p0, p1, this.formatManager);
	}

	public static class Factory implements AmiWebFunctionFactory {

		private AmiWebService service;

		public Factory(AmiWebService service) {
			this.service = service;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionFormatDecimal(position, calcs[0], calcs[1], service.getFormatterManager());
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
