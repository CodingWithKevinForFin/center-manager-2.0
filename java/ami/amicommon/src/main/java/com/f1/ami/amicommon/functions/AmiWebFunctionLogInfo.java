package com.f1.ami.amicommon.functions;

import java.util.logging.Logger;

import com.f1.utils.LH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionLogInfo extends AbstractMethodDerivedCellCalculator1 {
	private static final Logger amiScripLog = Logger.getLogger("AMISCRIPT.LOGINFO");
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("logInfo", String.class, "Object message");
	static {
		VERIFIER.addDesc("Writes and returns the message at INF level to the AMISCRIPT.LOGINFO stream in the Ami log file (usually AmiOne.log).");
		VERIFIER.addParamDesc(0, "Message to log");
	}

	public AmiWebFunctionLogInfo(int position, DerivedCellCalculator params) {
		super(position, params);
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o) {
		String msg = DerivedHelper.toString(o);
		LH.info(amiScripLog, msg);
		return msg;
	}
	@Override
	public boolean isConst() {
		return false;
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionLogInfo(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionLogInfo(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}