package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.math.PrimitiveMathManager;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionAbs extends AbstractMethodDerivedCellCalculator1 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("abs", Number.class, "Number value");
	static {
		VERIFIER.addDesc("Takes an input Number object and calculates the absolute value. "
				+ "<ul>"
				+ "<li> Negative numbers are multiplied by -1 </li>"
				+ "<li> Positive numbers are unchanged </li>"
				+ "</ul>");
		VERIFIER.addParamDesc(0, "Number to get absolute value for");
		VERIFIER.addExample(-3.32);
		VERIFIER.addExample(0);
		VERIFIER.addExample(4);
	}

	private Class<?> returnType;
	private PrimitiveMath<?> pm;

	public AmiWebFunctionAbs(int position, DerivedCellCalculator params) {
		super(position, params);
		this.returnType = params.getReturnType();
		this.pm = PrimitiveMathManager.INSTANCE.get(this.returnType);
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}
	@Override
	public Class<?> getReturnType() {
		return returnType;
	}

	@Override
	public Object eval(Object t) {
		if (t == null)
			return null;
		return this.pm.abs((Number) t);

	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionAbs(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionAbs(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
