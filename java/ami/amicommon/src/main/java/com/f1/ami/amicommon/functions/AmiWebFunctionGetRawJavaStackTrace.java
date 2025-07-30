package com.f1.ami.amicommon.functions;

import java.util.logging.Logger;

import com.f1.base.Mapping;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator0;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionGetRawJavaStackTrace extends AbstractMethodDerivedCellCalculator0 {
	private static final Logger amiScripLog = Logger.getLogger("AMISCRIPT.LOGWARN");
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("getRawJavaStackTrace", String.class, "");
	static {
		VERIFIER.addDesc("Returns the raw java stacktrace as a string.");
	}

	public AmiWebFunctionGetRawJavaStackTrace(int position) {
		super(position);
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval() {
		try {
			return SH.printStackTraceElements(Thread.currentThread().getStackTrace(), "", "\n", new StringBuilder(), null).toString();
		} catch (Exception e) {
			return null;
		}
	}
	@Override
	public boolean isConst() {
		return false;
	}
	@Override
	public DerivedCellCalculator copy() {
		return new AmiWebFunctionGetRawJavaStackTrace(getPosition());
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionGetRawJavaStackTrace(position);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}