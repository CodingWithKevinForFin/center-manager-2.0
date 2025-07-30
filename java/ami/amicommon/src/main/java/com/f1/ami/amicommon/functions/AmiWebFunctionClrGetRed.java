package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.ColorHelper;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionClrGetRed extends AbstractMethodDerivedCellCalculator1 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("clrGetRed", Integer.class, "String color");
	static {
		VERIFIER.addDesc("Returns an Integer between 0-255 that is the red component of the supplied color. Returns null if supplied color is null or invalid format.");
		VERIFIER.addParamDesc(0, "Color to inspect in rrggbb format");
		VERIFIER.addExample("#AABBCC");
		VERIFIER.addExample("#AABBCCDD");
		VERIFIER.addExample("#ABC");
		VERIFIER.addExample("#ABCD");
		VERIFIER.addExample("invalid");
		VERIFIER.addExample(null);
	}

	public AmiWebFunctionClrGetRed(int position, DerivedCellCalculator params) {
		super(position, params);
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o) {
		long c = ColorHelper.parseRgbNoThrow((CharSequence) o);
		return c == ColorHelper.NO_COLOR ? null : ColorHelper.getR((int) c);
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionClrGetRed(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionClrGetRed(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}