package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.math.PrimitiveBitwiseMath;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionBitShiftRight extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("bitShiftRight", Number.class, "Number n,Number shiftAmount");
	static {
		VERIFIER.addDesc("Returns the bitwise right shift of the first number.");
		VERIFIER.addParamDesc(0, "number to shift");
		VERIFIER.addParamDesc(1, "bits to shift by");
		VERIFIER.addExample(5, 8);
		VERIFIER.addExample(13, 22);
		VERIFIER.addExample(null, 17);
	}

	final private PrimitiveBitwiseMath math;

	public AmiWebFunctionBitShiftRight(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		math = AmiWebFunctionBitAnd.getMath(p0);
		evalConsts();
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}
	@Override
	public Class<?> getReturnType() {
		return math == null ? Number.class : math.getReturnType();
	}

	@Override
	public Object eval(Object o0, Object o1) {
		if (math == null)
			return null;
		return math.shiftRight((Number) o0, ((Number) o1).intValue());
	}
	@Override
	public boolean isConst() {
		return math == null || super.isConst();
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionBitShiftRight(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionBitShiftRight(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
