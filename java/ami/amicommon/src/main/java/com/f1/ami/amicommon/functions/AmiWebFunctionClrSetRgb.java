package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.ColorHelper;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculatorN;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionClrSetRgb extends AbstractMethodDerivedCellCalculatorN {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("clrSetRgb", String.class, "String color,Number red,Number green,Number blue");
	private static final int VARIANT = -2;
	private static final int IS_NULL = -1;
	static {
		VERIFIER.addDesc(
				"Sets the red, green or blue of the supplied color. Creates a new color if all three are supplied. Returns the resulting color in rrggbb format, or null if not a valid color.");
		VERIFIER.addParamDesc(0, "Default Color to update");
		VERIFIER.addParamDesc(1, "Red componenet (0-255) or null to leave default");
		VERIFIER.addParamDesc(2, "Green componenet (0-255) or null to leave default");
		VERIFIER.addParamDesc(3, "Blue componenet (0-255) or null to leave default");
		VERIFIER.addExample(null, 255, 0, 0);
		VERIFIER.addExample(null, 255, 0, 0);
		VERIFIER.addExample(null, 255, 128, 64);
		VERIFIER.addExample(null, 255, 128, 64);
		VERIFIER.addExample("#ABC", null, null, null);
		VERIFIER.addExample("#ABC", null, 33, null);
		VERIFIER.addExample("#112233", 255, null, null);
		VERIFIER.addExample("#112233", null, 255, null);
		VERIFIER.addExample("#112233", null, 255, 255);
		VERIFIER.addExample("#112233", null, null, 255);
		VERIFIER.addExample(null, null, null, null);
		VERIFIER.addExample(null, null, 128, null);
		VERIFIER.addExample(null, 0, 128, 0);
	}
	final private long cConst;
	final private int rConst, gConst, bConst;

	public AmiWebFunctionClrSetRgb(int position, DerivedCellCalculator[] params) {
		super(position, params);
		evalConsts();
		cConst = params[0].isConst() ? getColor((String) getBuf()[0]) : VARIANT;
		rConst = params[1].isConst() ? get255((Number) getBuf()[1]) : VARIANT;
		gConst = params[2].isConst() ? get255((Number) getBuf()[2]) : VARIANT;
		bConst = params[3].isConst() ? get255((Number) getBuf()[3]) : VARIANT;
		if (rConst >= 0 && gConst >= 0 && bConst >= 0)
			this.notConsts = OH.EMPTY_INT_ARRAY;//special case, if, RGB are all specified, then its a const, regardless of color passed in
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	private String get(long c, int r, int g, int b) {
		if (c == IS_NULL) {
			if (r == IS_NULL || g == IS_NULL || b == IS_NULL)
				return null;
			else
				return ColorHelper.toString(ColorHelper.toRgb(r, g, b));
		} else {
			int ret = (int) c;
			if (r != IS_NULL)
				ret = ColorHelper.setR(ret, r);
			if (g != IS_NULL)
				ret = ColorHelper.setG(ret, g);
			if (b != IS_NULL)
				ret = ColorHelper.setB(ret, b);
			return ColorHelper.toString(ret);
		}
	}

	private long getColor(String n) {
		if (n == null)
			return IS_NULL;
		long v = ColorHelper.parseRgbNoThrow(n);
		return v == ColorHelper.NO_COLOR ? IS_NULL : v;
	}
	private int get255(Number n) {
		if (n == null)
			return IS_NULL;
		return MH.clip(n.intValue(), 0, 255);
	}

	@Override
	public Object eval(Object values[]) {
		long c = cConst == VARIANT ? getColor((String) values[0]) : cConst;
		int h = rConst == VARIANT ? get255((Number) values[1]) : rConst;
		int s = gConst == VARIANT ? get255((Number) values[2]) : gConst;
		int l = bConst == VARIANT ? get255((Number) values[3]) : bConst;
		return get(c, h, s, l);
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator[] params2) {
		return new AmiWebFunctionClrSetRgb(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionClrSetRgb(position, calcs);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}