package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.math.PrimitiveBitwiseMath;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionBitOr extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("bitOr", Number.class, "Number a,Number b");
	static {
		VERIFIER.addDesc("Returns the bitwise OR (|) of two numbers.");
		VERIFIER.addParamDesc(0, "param1");
		VERIFIER.addParamDesc(1, "param2");
		VERIFIER.addExample(5, 8);
		VERIFIER.addExample(13, 22);
		VERIFIER.addExample(null, 17);
	}

	final private PrimitiveBitwiseMath math;

	public AmiWebFunctionBitOr(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		math = AmiWebFunctionBitAnd.getMath(p0, p1);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o0, Object o1) {
		if (math == null)
			return null;
		return math.or((Number) o0, (Number) o1);
	}

	@Override
	public boolean isConst() {
		return math == null || super.isConst();
	}
	public java.lang.Class<?> getReturnType() {
		return math == null ? Number.class : math.getReturnType();
	};

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionBitOr(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionBitOr(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
