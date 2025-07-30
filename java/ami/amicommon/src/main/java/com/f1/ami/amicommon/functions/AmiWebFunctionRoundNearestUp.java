package com.f1.ami.amicommon.functions;

import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.math.PrimitiveMathManager;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionRoundNearestUp extends AbstractMethodDerivedCellCalculator2 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("roundNearestUp", Number.class, "Number value,Number bucketSize");
	static {
		VERIFIER.addDesc("Given the value and the bucket size, returns a Long that rounds UP to the closest multiple of the bucket	size.");
		VERIFIER.addParamDesc(0, "Number to round");
		VERIFIER.addParamDesc(1, "multiple, aka, bucket size to round to");
		VERIFIER.addExample(0, 10);
		VERIFIER.addExample(3, 10);
		VERIFIER.addExample(7, 10);
		VERIFIER.addExample(14, 10);
		VERIFIER.addExample(19, 10);
		VERIFIER.addExample(20, 10);
		VERIFIER.addExample(21, 10);
		VERIFIER.addExample(239, 60);
		VERIFIER.addExample(240, 60);
		VERIFIER.addExample(241, 60);
	}

	private PrimitiveMath<?> math;

	public AmiWebFunctionRoundNearestUp(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		math = PrimitiveMathManager.INSTANCE.getNoThrow((Class) p0.getReturnType(), (Class) p1.getReturnType());
		if (math == null)
			throw new ExpressionParserException(p0.getPosition(), "Could not determine return type for given arguments");
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Class<?> getReturnType() {
		return math.getReturnType();
	}

	@Override
	public Object eval(Object o0, Object o1) {
		Number v = (Number) o0;
		Number b = (Number) o1;
		b = math.abs(b);
		Number divide = math.divide(v, b);
		if (divide == null)
			return null;
		Number r = math.multiply(b, divide.longValue());
		if (math.compare(r, v) >= 0)
			return r;
		return math.add(r, b);
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionRoundNearestUp(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionRoundNearestUp(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}
	}

}
