package com.f1.ami.amicommon.functions;

import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiStartup;
import com.f1.base.Mapping;
import com.f1.utils.LH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator0;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionVersionDetails extends AbstractMethodDerivedCellCalculator0 {

	private static final Logger log = LH.get();
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("versionDetails", String.class, "");
	static {
		VERIFIER.addDesc("Returns AMI version details if configured");
		VERIFIER.addExample();
	}
	private static String AMI_LIBRARY_VERSION = null;
	static {
		AMI_LIBRARY_VERSION = "=== Ami Library Version ===\n\n";
		for (String key : AmiStartup.getBuildProperties()) {
			String value = AmiStartup.getBuildProperty(key);
			AMI_LIBRARY_VERSION += key + " = " + value + "\n";
		}
	}

	public AmiWebFunctionVersionDetails(int position) {
		super(position);
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public boolean isConst() {
		return true;
	}

	@Override
	public Object eval() {
		return AMI_LIBRARY_VERSION;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new AmiWebFunctionVersionDetails(getPosition());
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionVersionDetails(position);
		}
	}
}
