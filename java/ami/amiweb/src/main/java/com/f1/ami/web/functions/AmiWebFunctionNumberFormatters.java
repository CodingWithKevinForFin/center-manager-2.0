package com.f1.ami.web.functions;

import com.f1.ami.amicommon.functions.AmiWebFunctionFactory;
import com.f1.ami.web.AmiWebService;
import com.f1.base.Mapping;
import com.f1.utils.Formatter;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public abstract class AmiWebFunctionNumberFormatters extends AbstractMethodDerivedCellCalculator1 {

	private Formatter formatter;

	public AmiWebFunctionNumberFormatters(int position, DerivedCellCalculator param, Formatter formatter) {
		super(position, param);
		this.formatter = formatter;
	}

	@Override
	public Object eval(Object o0) {
		Number value = (Number) o0;
		return formatter.format(value);
	}

	protected Formatter getFormatter() {
		return this.formatter;
	}

	public static class IntegerFormatter extends AmiWebFunctionNumberFormatters {

		public IntegerFormatter(int position, DerivedCellCalculator param, Formatter formatter) {
			super(position, param, formatter);
		}

		@Override
		public DerivedCellCalculator copy(DerivedCellCalculator p) {
			return new IntegerFormatter(getPosition(), p, getFormatter());
		}

		@Override
		public ParamsDefinition getDefinition() {
			return IntegerFactory.VERIFIER;
		}
	}

	public static class IntegerFactory implements AmiWebFunctionFactory {

		private AmiWebService service;
		private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatInteger", String.class, "Number value");
		static {
			VERIFIER.addDesc("Formats a number (rounded to an integer) to a legible string, returns that string.");
			VERIFIER.addParamDesc(0, "Number to format as string");
			VERIFIER.addExample(60);
			VERIFIER.addExample(12.3);
			VERIFIER.addExample(-3.5);
		}

		public IntegerFactory(AmiWebService service) {
			this.service = service;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			AmiWebFunctionNumberFormatters r = new IntegerFormatter(position, calcs[0], service.getFormatterManager().getIntegerFormatter());
			return r;
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	public static class DecimalFormatter extends AmiWebFunctionNumberFormatters {

		public DecimalFormatter(int position, DerivedCellCalculator param, Formatter formatter) {
			super(position, param, formatter);
		}

		@Override
		public DerivedCellCalculator copy(DerivedCellCalculator p) {
			return new DecimalFormatter(getPosition(), p, getFormatter());
		}

		@Override
		public ParamsDefinition getDefinition() {
			return DecimalFactory.VERIFIER;
		}
	}

	public static class DecimalFactory implements AmiWebFunctionFactory {

		private AmiWebService service;
		private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatDecimal", String.class, "Number value");
		static {
			VERIFIER.addDesc("Formats a number to a legible string");
			VERIFIER.addParamDesc(0, "Number to format as string");
			VERIFIER.addRetDesc("String legible string representation of number (with decimals of precision specified by user settings)");
			VERIFIER.addExample(345);
		}

		public DecimalFactory(AmiWebService service) {
			this.service = service;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			AmiWebFunctionNumberFormatters r = new DecimalFormatter(position, calcs[0], service.getFormatterManager().getDecimalFormatter());
			return r;
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	public static class ScientificFormatter extends AmiWebFunctionNumberFormatters {

		public ScientificFormatter(int position, DerivedCellCalculator param, Formatter formatter) {
			super(position, param, formatter);
		}

		@Override
		public DerivedCellCalculator copy(DerivedCellCalculator p) {
			return new ScientificFormatter(getPosition(), p, getFormatter());
		}

		@Override
		public ParamsDefinition getDefinition() {
			return ScientificFactory.VERIFIER;
		}
	}

	public static class ScientificFactory implements AmiWebFunctionFactory {

		private AmiWebService service;
		private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatScientific", String.class, "Number value");
		static {
			VERIFIER.addDesc("Formats a number to a scientific notation string");
			VERIFIER.addParamDesc(0, "Number to format as string");
			VERIFIER.addRetDesc("String scientific notiational representation of number");
			VERIFIER.addExample(345);
			VERIFIER.addExample(-2.2);
			VERIFIER.addExample(3.530);
		}

		public ScientificFactory(AmiWebService service) {
			this.service = service;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			AmiWebFunctionNumberFormatters r = new ScientificFormatter(position, calcs[0], service.getFormatterManager().getScientificFormatter());
			return r;
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
