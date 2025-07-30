package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionCos extends AbstractMethodDerivedCellCalculator1 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("cos", Double.class, "Number radians");
	static {
		VERIFIER.addDesc("Returns a Double between -1 and 1 that is the trigonometric cosine of an angle.");
		VERIFIER.addParamDesc(0, "Angle in radians");
		VERIFIER.addExample(0);
		VERIFIER.addExample(10);
		VERIFIER.addExample(3);
	}

	public AmiWebFunctionCos(int position, DerivedCellCalculator params) {
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
		return Math.cos(value.doubleValue());
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionCos(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionCos(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}