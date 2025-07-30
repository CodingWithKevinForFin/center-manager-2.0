package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.ColorHelper;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculatorN;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionClrSetHsl extends AbstractMethodDerivedCellCalculatorN {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("clrSetHsl", String.class, "String color,Number hue,Number saturation,Number luminance");
	private static final int VARIANT = -2;
	private static final int IS_NULL = -1;
	static {
		VERIFIER.addDesc(
				"Sets the hue, saturation or luminance of the supplied color. Creates a new color if all three are supplied. Returns the resulting color in rrggbb format, or null if not a valid color.");
		VERIFIER.addParamDesc(0, "Default Color to update");
		VERIFIER.addParamDesc(1, "Hue componenet (0-65535) or null to leave default, not the number will role, ex: 65536 -> 0, -1 => 65535");
		VERIFIER.addParamDesc(2, "Saturation componenet (0-65535) or null to leave default");
		VERIFIER.addParamDesc(3, "Luminance componenet (0-65535) or null to leave default");
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
	final private int hConst, sConst, lConst;

	public AmiWebFunctionClrSetHsl(int position, String methodName, DerivedCellCalculator[] params) {
		super(position, params);
		evalConsts();
		cConst = params[0].isConst() ? getColor((String) getBuf()[0]) : VARIANT;
		hConst = params[1].isConst() ? get360((Number) getBuf()[1]) : VARIANT;
		sConst = params[2].isConst() ? get100((Number) getBuf()[2]) : VARIANT;
		lConst = params[3].isConst() ? get100((Number) getBuf()[3]) : VARIANT;
		if (hConst >= 0 && sConst >= 0 && lConst >= 0)
			this.notConsts = OH.EMPTY_INT_ARRAY;//special case, if, HSL are all specified, then its a const, regardless of color passed in
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	private String get(long c, int h, int s, int l) {
		if (c != IS_NULL) {
			if (h == IS_NULL)
				h = ColorHelper.getH(c);
			if (s == IS_NULL)
				s = ColorHelper.getS(c);
			if (l == IS_NULL)
				l = ColorHelper.getL(c);
		} else if (h == IS_NULL || s == IS_NULL || l == IS_NULL)
			return null;
		return ColorHelper.toRgbString(ColorHelper.hsl2Rgb(h, s, l));
	}

	static private long getColor(String n) {
		if (n == null)
			return IS_NULL;
		long v = ColorHelper.parseRgbNoThrow(n);
		return v == ColorHelper.NO_COLOR ? IS_NULL : ColorHelper.rgb2Hsl((int) v);
	}
	static private int get100(Number n) {
		if (n == null)
			return IS_NULL;
		return MH.clip((int) (n.doubleValue() * (65535d / 100d)), 0, 65535);
	}
	static private int get360(Number n) {
		if (n == null)
			return IS_NULL;
		return MH.mod((int) (n.doubleValue() * (65535d / 360d)), 65536);
	}

	@Override
	public Object eval(Object values[]) {
		long c = cConst == VARIANT ? getColor((String) values[0]) : cConst;
		int h = hConst == VARIANT ? get360((Number) values[1]) : hConst;
		int s = sConst == VARIANT ? get100((Number) values[2]) : sConst;
		int l = lConst == VARIANT ? get100((Number) values[3]) : lConst;
		return get(c, h, s, l);
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator[] params2) {
		return new AmiWebFunctionClrSetHsl(getPosition(), getMethodName(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionClrSetHsl(position, methodName, calcs);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}