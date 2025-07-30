package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.MH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionRound2 extends AbstractMethodDerivedCellCalculator2 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("round", Double.class, "Number value,Number precision");
	static {
		VERIFIER.addDesc("Returns the closest Long given the value and the number of decimals to keep.");
		VERIFIER.addParamDesc(0, "Value to round");
		VERIFIER.addExample(123.14159, 0);
		VERIFIER.addExample(123.14159, 1);
		VERIFIER.addExample(123.14159, 2);
		VERIFIER.addExample(123.14159, 3);
		VERIFIER.addExample(123.14159, -1);
		VERIFIER.addExample(123.14159, -2);
		VERIFIER.addExample(123.14159, null);
		VERIFIER.addExample(null, null);
	}

	public AmiWebFunctionRound2(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	protected Integer get1(Object o1) {
		return MH.clip(((Number) o1).intValue(), -17, 17);
	}

	@Override
	public Object eval(Object o0, Object o1) {
		Number value = (Number) o0;
		final int prec = (Integer) o1;
		return MH.round(value.doubleValue(), MH.ROUND_HALF_EVEN, prec);
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionRound2(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionRound2(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}
	}

}
