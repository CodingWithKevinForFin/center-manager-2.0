package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.ColorHelper;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionClrGetAlpha extends AbstractMethodDerivedCellCalculator1 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("clrGetAlpha", Integer.class, "String color");
	static {
		VERIFIER.addDesc(
				"Returns an Integer between 0-255 the alpha component of the supplied color. Returns null if supplied color is null or invalid format. Returns 255 if there is no alpha component.");
		VERIFIER.addParamDesc(0, "Color to inspect in rrggbb format");
		VERIFIER.addExample("#AABBCC");
		VERIFIER.addExample("#AABBCCDD");
		VERIFIER.addExample("#AABBCCFF");
		VERIFIER.addExample("#AABBCC00");
		VERIFIER.addExample("#ABC");
		VERIFIER.addExample("#ABCD");
		VERIFIER.addExample("invalid");
		VERIFIER.addExample(null);
	}

	public AmiWebFunctionClrGetAlpha(int position, DerivedCellCalculator params) {
		super(position, params);
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o) {
		long c = ColorHelper.parseRgbNoThrow((CharSequence) o);
		if (c == ColorHelper.NO_COLOR)
			return null;
		return ColorHelper.hasAlpha((int) c) ? ColorHelper.getA((int) c) : 0xff;
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionClrGetAlpha(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionClrGetAlpha(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}