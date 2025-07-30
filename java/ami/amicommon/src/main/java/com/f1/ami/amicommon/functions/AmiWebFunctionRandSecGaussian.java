package com.f1.ami.amicommon.functions;

import java.util.Random;
import java.util.logging.Logger;

import com.f1.base.Mapping;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator0;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionRandSecGaussian extends AbstractMethodDerivedCellCalculator0 {

	private static final Logger log = LH.get();
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("randSecGaussian", Double.class, "");
	private Random random;
	static {
		VERIFIER.addDesc("Returns a random, Gaussian-distributed Double with a mean of 0.0 and a standard deviation of 1, this is secure.");
		VERIFIER.addExample();
		VERIFIER.addExample();
		VERIFIER.addExample();
	}

	public AmiWebFunctionRandSecGaussian(int position, Random random) {
		super(position);
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
	public Object eval() {
		return random.nextGaussian();
	}

	@Override
	public DerivedCellCalculator copy() {
		return new AmiWebFunctionRandSecGaussian(getPosition(), random);
	}

	public static class Factory implements AmiWebFunctionFactory {

		public Factory() {
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionRandSecGaussian(position, MH.RANDOM_SECURE);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
