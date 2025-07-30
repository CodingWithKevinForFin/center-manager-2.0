package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.ColorHelper;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionClrGetSat extends AbstractMethodDerivedCellCalculator1 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("clrGetSat", Integer.class, "String color");
	static {
		VERIFIER.addDesc("Returns an Integer between 0-255 that is the saturation of the supplied color. Returns null if supplied color is null or invalid format.");
		VERIFIER.addParamDesc(0, "Color to inspect in rrggbb format");
		VERIFIER.addExample("#FF0000");
		VERIFIER.addExample("#FFFF00");
		VERIFIER.addExample("#FFFFFF");
		VERIFIER.addExample("#ABC");
		VERIFIER.addExample("#ABCD");
		VERIFIER.addExample("invalid");
		VERIFIER.addExample(null);
	}

	public AmiWebFunctionClrGetSat(int position, DerivedCellCalculator params) {
		super(position, params);
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o) {
		long c = ColorHelper.parseRgbNoThrow((CharSequence) o);
		return c == ColorHelper.NO_COLOR ? null : ColorHelper.getRgbSat((int) c);
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionClrGetSat(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionClrGetSat(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}