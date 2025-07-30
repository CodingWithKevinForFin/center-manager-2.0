package com.f1.ami.amicommon.functions;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiStartup;
import com.f1.base.Mapping;
import com.f1.utils.LH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator0;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionVersionMap extends AbstractMethodDerivedCellCalculator0 {

	private static final Logger log = LH.get();
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("versionMap", String.class, "");
	static {
		VERIFIER.addDesc("Returns AMI version details as a map if configured");
		VERIFIER.addExample();
	}
	private static Map<String, String> AMI_LIBRARY_VERSION = null;
	static {
		Map<String, String> properties = new LinkedHashMap<String, String>();
		for (String key : AmiStartup.getBuildProperties()) {
			String value = AmiStartup.getBuildProperty(key);
			properties.put(key, value);
		}

		AMI_LIBRARY_VERSION = Collections.unmodifiableMap(properties);
	}

	public AmiWebFunctionVersionMap(int position) {
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
		return new AmiWebFunctionVersionMap(getPosition());
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionVersionMap(position);
		}
	}
}
