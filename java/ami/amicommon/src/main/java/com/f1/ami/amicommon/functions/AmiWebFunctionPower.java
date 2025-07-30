package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionPower extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("power", Double.class, "Number value,Number exponent");
	static {
		VERIFIER.addDesc("Returns a Double value of the first argument raised to the power of the exponent.");
		VERIFIER.addParamDesc(0, "value to raise to exponent", "exponent to raise value to");
		VERIFIER.addExample(10, 1);
		VERIFIER.addExample(10, 0);
		VERIFIER.addExample(10, 3);
		VERIFIER.addExample(5.3, 4.2);
	}

	public AmiWebFunctionPower(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}
	private static Double power(Number numberVal, Number power) {
		return Math.pow(numberVal.doubleValue(), power.doubleValue());
	}

	@Override
	public Object eval(Object o0, Object o1) {
		return power((Number) o0, (Number) o1);
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionPower(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionPower(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
