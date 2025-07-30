package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionArcSin extends AbstractMethodDerivedCellCalculator1 {
	private final static ParamsDefinition VERIFIER = new ParamsDefinition("asin", Double.class, "Number value");
	static {
		VERIFIER.addDesc("Returns a Double that is the arc sine of a value; the returned angle is expressed in radians in the range of -pi/2 to pi/2.");
		VERIFIER.addParamDesc(0, "Input value");
		VERIFIER.addExample(1);
		VERIFIER.addExample(0.5);
	}

	public AmiWebFunctionArcSin(int position, DerivedCellCalculator params) {
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
		return Math.asin(value.doubleValue());
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionArcSin(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionArcSin(position, calcs[0]);
		}

	}
}
