package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.MH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionColorCycle extends AbstractMethodDerivedCellCalculator1 {
	private static final String[] DISTINCT_COLORS = { "#011aff", "#ff001b", "#00a600", "#730081", "#005067", "#873d00", "#00009d", "#00ff02", "#7200f1", "#1b5306", "#d4007c",
			"#0050c0", "#5d0025", "#5f3f55", "#e24400", "#648a00", "#e000f6", "#00a462", "#5833b9", "#4cd300", "#00ea51", "#b73a51", "#007fff", "#0095b2", "#b00000", "#bd8400",
			"#b12bbe", "#568656", "#0e0e4e", "#00dba1", "#545cff", "#ff3d88", "#00c9f5", "#9eff00", "#5778ab", "#58ff40", "#ffb100", "#ff773c", "#96bb2e", "#ff43ff", "#b0815f",
			"#f5fa00", "#56c87c", "#ad5dff", "#4da2ec", "#2fffd8", "#cc69ad", "#8d528e", "#40ff91", "#daaf47", "#8aa094", "#007b32", "#3cb83c", "#c7e533", "#a10449", "#50cfc1",
			"#8a86db", "#85682f" };

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("colorCycle", String.class, "Number offset");
	static {
		VERIFIER.addDesc(
				"Returns the nth color from choices where n is the offset + 1. If the offset is greater than or equal to the number of choices, n is calculated as (offset % number of choices) + 1.");
		VERIFIER.addParamDesc(0, "index into choices");
		VERIFIER.addExample(0);
		VERIFIER.addExample(5);
		VERIFIER.addExample(-1);
	}

	public AmiWebFunctionColorCycle(int position, DerivedCellCalculator params) {
		super(position, params);
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object p1) {
		final Number n = (Number) p1;
		if (n == null)
			return null;
		return DISTINCT_COLORS[MH.mod(n.intValue(), DISTINCT_COLORS.length)];
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionColorCycle(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionColorCycle(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
