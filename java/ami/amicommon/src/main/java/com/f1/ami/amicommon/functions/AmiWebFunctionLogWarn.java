package com.f1.ami.amicommon.functions;

import java.util.logging.Logger;

import com.f1.base.Mapping;
import com.f1.utils.LH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionLogWarn extends AbstractMethodDerivedCellCalculator1 {
	private static final Logger amiScripLog = Logger.getLogger("AMISCRIPT.LOGWARN");
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("logWarn", String.class, "Object message");
	static {
		VERIFIER.addDesc("Writes and returns the message at WRN level to the AMISCRIPT.LOGWARN stream in the Ami log file (usually AmiOne.log).");
		VERIFIER.addRetDesc("message printed");
		VERIFIER.addParamDesc(0, "Message to log");
	}

	public AmiWebFunctionLogWarn(int position, DerivedCellCalculator params) {
		super(position, params);
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o) {
		String msg = DerivedHelper.toString(o);
		LH.warning(amiScripLog, msg);
		return msg;
	}
	@Override
	public boolean isConst() {
		return false;
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionLogWarn(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionLogWarn(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}