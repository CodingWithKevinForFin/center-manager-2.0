package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.MH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionLnGamma extends AbstractMethodDerivedCellCalculator1 {

	private final static ParamsDefinition VERIFIER = new ParamsDefinition("lnGamma", Double.class, "Number x");
	static {
		VERIFIER.addDesc("Returns the natural logarithm of the Gamma function applied to x.");
		VERIFIER.addParamDesc(0, "Input value");
		VERIFIER.addExample(3.5);
		VERIFIER.addExample(-50.3);
		VERIFIER.addExample(777);
	}

	public AmiWebFunctionLnGamma(int position, DerivedCellCalculator params) {
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
		return MH.lnGamma(value.doubleValue());
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionLnGamma(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionLnGamma(position, calcs[0]);
		}

	}

}
