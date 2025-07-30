package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.ColorHelper;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionClrGetHue extends AbstractMethodDerivedCellCalculator1 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("clrGetHue", Double.class, "String color");
	static {
		VERIFIER.addDesc("RReturns an Integer between 0-255 that is the hue of the supplied color. Returns null if supplied color is null or invalid format.");
		VERIFIER.addParamDesc(0, "Color to inspect in rrggbb format");
		VERIFIER.addExample("#FF0000");
		VERIFIER.addExample("#FFFF00");
		VERIFIER.addExample("#FFFFFF");
		VERIFIER.addExample("#ABC");
		VERIFIER.addExample("#ABCD");
		VERIFIER.addExample("invalid");
		VERIFIER.addExample(null);
	}

	public AmiWebFunctionClrGetHue(int position, DerivedCellCalculator params) {
		super(position, params);
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o) {
		long c = ColorHelper.parseRgbNoThrow((CharSequence) o);
		return c == ColorHelper.NO_COLOR ? null : ColorHelper.getRgbHue((int) c) / (65535d / 360d);
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionClrGetHue(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionClrGetHue(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}