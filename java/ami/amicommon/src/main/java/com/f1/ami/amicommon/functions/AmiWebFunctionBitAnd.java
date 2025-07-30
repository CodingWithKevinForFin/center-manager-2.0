package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.math.PrimitiveBitwiseMath;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.math.PrimitiveMathManager;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionBitAnd extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("bitAnd", Number.class, "Number a,Number b");
	static {
		VERIFIER.addDesc("Returns the bitwise AND (&) of two numbers.");
		VERIFIER.addParamDesc(0, "param1");
		VERIFIER.addParamDesc(1, "param2");
		VERIFIER.addExample(5, 8);
		VERIFIER.addExample(13, 22);
		VERIFIER.addExample(null, 17);
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	final private PrimitiveBitwiseMath math;

	public AmiWebFunctionBitAnd(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		math = AmiWebFunctionBitAnd.getMath(p0, p1);
		evalConsts();
	}

	public static PrimitiveBitwiseMath getMath(DerivedCellCalculator p1, DerivedCellCalculator p2) {
		PrimitiveMath r = PrimitiveMathManager.INSTANCE.getNoThrow((Class) p1.getReturnType(), (Class) p2.getReturnType());
		return r instanceof PrimitiveBitwiseMath ? (PrimitiveBitwiseMath) r : null;
	}
	public static PrimitiveBitwiseMath getMath(DerivedCellCalculator p1) {
		PrimitiveMath r = PrimitiveMathManager.INSTANCE.getNoThrow((Class) p1.getReturnType());
		return r instanceof PrimitiveBitwiseMath ? (PrimitiveBitwiseMath) r : null;
	}

	@Override
	public Object eval(Object p0, Object p1) {
		if (math == null)
			return null;
		return math.and((Number) p0, (Number) p1);
	}
	@Override
	public boolean isConst() {
		return math == null || super.isConst();
	}

	public java.lang.Class<?> getReturnType() {
		return math == null ? Number.class : math.getReturnType();
	};

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator param0, DerivedCellCalculator param1) {
		return new AmiWebFunctionBitAnd(getPosition(), param0, param1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionBitAnd(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
