package com.f1.ami.amicommon.functions;

import com.f1.utils.EH;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebFunctionSleepMillis extends AbstractMethodDerivedCellCalculator1 {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("sleepMillis", Long.class, "Number millisToSleep");
	static {
		VERIFIER.addDesc("Returns the number of millisecond slept as a Long. This is primarily used for testing. WARNING: This is a BLOCKING call.");
		VERIFIER.addParamDesc(0, "The number of milliseconds to sleep.");
		VERIFIER.addExample(0);
		VERIFIER.addExample(50);
	}

	private TimeoutController tc;

	public AmiWebFunctionSleepMillis(int position, DerivedCellCalculator params) {
		super(position, params);
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
	public Object get(CalcFrameStack key) {
		this.tc = key.getTimeoutController();
		return super.get(key);
	}

	@Override
	public Object eval(Object t) {
		if (t == null)
			return null;
		long millis = ((Number) t).longValue();
		if (millis <= 0)
			return 0L;
		long now = EH.currentTimeMillis();
		if (this.tc != null) {
			long to = tc.getTimeoutMillisRemainingOrZero();
			if (to < millis) {
				for (;;) {
					OH.sleep(tc.getTimeoutMillisRemaining());
					tc.throwIfTimedout(this);
				}
			}
		}
		OH.sleep(millis);
		return EH.currentTimeMillis() - now;
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionSleepMillis(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionSleepMillis(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}