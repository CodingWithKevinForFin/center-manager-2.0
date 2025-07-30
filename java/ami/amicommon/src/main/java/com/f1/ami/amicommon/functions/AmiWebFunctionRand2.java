package com.f1.ami.amicommon.functions;

import java.util.Random;
import java.util.logging.Logger;

import com.f1.base.Caster;
import com.f1.base.Mapping;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionRand2 extends AbstractMethodDerivedCellCalculator1 {

	private static final Logger log = LH.get();
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("rand", Number.class, "Number range");
	static {
		VERIFIER.addDesc(
				"Returns a random number between 0.0 (inclusive) and the supplied range (exculsive), if range is negative the returned value will also be <=0, note: this random is not secure.");
		VERIFIER.addExample(2);
		VERIFIER.addExample(12.2);
		VERIFIER.addExample(-19);
	}

	private final Random random;
	private final boolean isWholeNumber;
	private final Caster<? extends Number> caster;

	public AmiWebFunctionRand2(int position, DerivedCellCalculator param, Random random) {
		super(position, param);
		this.caster = OH.getCaster((Class<Number>) param.getReturnType());
		this.isWholeNumber = OH.isWholeNumber(this.caster.getCastToClass());
		this.random = random;
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public Class<?> getReturnType() {
		return this.caster == null ? super.getReturnType() : this.caster.getCastToClass();
	}
	@Override
	public Object eval(Object key) {
		Number n = (Number) key;
		if (n == null)
			return null;
		return this.caster.cast(isWholeNumber ? MH.nextLongSigned(random, n.longValue()) : MH.nextDoubleSigned(random, n.doubleValue()));
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionRand2(getPosition(), params2, random);
	}

	public static class Factory implements AmiWebFunctionFactory {

		public Factory() {
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionRand2(position, calcs[0], MH.RANDOM);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
