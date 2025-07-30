package com.f1.ami.amicommon.functions;

import com.f1.base.Complex;
import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionExponential extends AbstractMethodDerivedCellCalculator1 {

	private final static ParamsDefinition VERIFIER = new ParamsDefinition("exp", Number.class, "Number x");
	static {
		VERIFIER.addDesc("Returns Euler's number raised to the power of x.");
		VERIFIER.addParamDesc(0, "Input value");
		VERIFIER.addRetDesc("Exponential");
		VERIFIER.addExample(38);
		VERIFIER.addExample(0.5);
		VERIFIER.addExample(-2.99);
	}

	public AmiWebFunctionExponential(int position, DerivedCellCalculator params) {
		super(position, params);
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o) {
		Number value = (Number) o;
		if (value == null)
			return null;
		if (value instanceof Complex)
			return ((Complex) value).exponential();
		return Math.exp(value.doubleValue());
	}

	@Override
	public Class<?> getReturnType() {
		return getParamAt(0).getReturnType() == Complex.class ? Complex.class : Double.class;
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionExponential(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionExponential(position, calcs[0]);
		}

	}
}
