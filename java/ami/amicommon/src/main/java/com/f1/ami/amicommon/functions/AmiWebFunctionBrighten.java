package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.ColorHelper;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionBrighten extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("brighten", String.class, "String color,Number pctChange");
	static {
		VERIFIER.addDesc(
				"Returns a new hexcode color that is a percentage brighter or darker than the supplied color. Input and output colors should be in #RRGGBB format. Returns null if argument is null.");
		VERIFIER.addParamDesc(0, "Color to brighten/darken, in rrggbb format");
		VERIFIER.addParamDesc(1, "Pct to brighten or darken by, such that 0 is unchanged, 1 is 100% brighter and -1 is 100% darker");
		VERIFIER.addExample("#FF0000", 0);
		VERIFIER.addExample("#FF0000", .5);
		VERIFIER.addExample("#FF0000", -.5);
		VERIFIER.addExample("#880088", 0);
		VERIFIER.addExample("#880088", .1);
		VERIFIER.addExample("#880880", -1);
	}

	public AmiWebFunctionBrighten(int position, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		super(position, p1, p2);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}
	@Override
	protected Long get0(Object o) {
		long r = ColorHelper.parseRgbNoThrow((String) o);
		return r == ColorHelper.NO_COLOR ? null : (Long) r;
	}

	@Override
	public Object eval(Object param1, Object param2) {
		final Long color = (Long) param1;
		final Number pct = (Number) param2;
		return ColorHelper.toString(ColorHelper.brighten(color.intValue(), pct.doubleValue()));

	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionBrighten(getPosition(), p1, p2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionBrighten(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}