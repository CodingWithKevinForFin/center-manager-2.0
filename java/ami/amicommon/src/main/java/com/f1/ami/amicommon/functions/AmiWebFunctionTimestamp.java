package com.f1.ami.amicommon.functions;

import java.util.logging.Logger;

import com.f1.base.Clock;
import com.f1.base.Mapping;
import com.f1.utils.LH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator0;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionTimestamp extends AbstractMethodDerivedCellCalculator0 {

	private static final Logger log = LH.get();
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("timestamp", Long.class, "");
	static {
		VERIFIER.addDesc("Returns the current wall clock time in unix epoch in milliseconds.");
		VERIFIER.addExample();
	}
	private Clock clock;

	public AmiWebFunctionTimestamp(int position, Clock clock) {
		super(position);
		this.clock = clock;
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
		return clock.getNow();
	}

	@Override
	public DerivedCellCalculator copy() {
		return new AmiWebFunctionTimestamp(getPosition(), clock);
	}

	public static class Factory implements AmiWebFunctionFactory {

		private Clock clock;

		public Factory(Clock clock) {
			this.clock = clock;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionTimestamp(position, this.clock);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
