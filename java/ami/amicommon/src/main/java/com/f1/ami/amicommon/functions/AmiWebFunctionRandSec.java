package com.f1.ami.amicommon.functions;

import java.util.Random;
import java.util.logging.Logger;

import com.f1.base.Mapping;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator0;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionRandSec extends AbstractMethodDerivedCellCalculator0 {

	private static final Logger log = LH.get();
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("randSec", Double.class, "");
	static {
		VERIFIER.addDesc("Returns a random Double between 0.0 (inclusive) and 1.0 (exclusive), this is secure.");
		VERIFIER.addRetDesc("double");
		VERIFIER.addExample();
		VERIFIER.addExample();
		VERIFIER.addExample();
	}
	private Random random;

	public AmiWebFunctionRandSec(int position, Random random) {
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
		return random.nextDouble();
	}

	@Override
	public DerivedCellCalculator copy() {
		return new AmiWebFunctionRandSec(getPosition(), random);
	}

	public static class Factory implements AmiWebFunctionFactory {

		public Factory() {
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionRandSec(position, MH.RANDOM_SECURE);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
