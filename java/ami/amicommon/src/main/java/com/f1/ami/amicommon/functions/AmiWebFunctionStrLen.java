package com.f1.ami.amicommon.functions;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.base.Mapping;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrLen extends AbstractMethodDerivedCellCalculator1 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strLen", Integer.class, "Object text");
	static {
		VERIFIER.addDesc("Returns the number of chars in the supplied string.");
		VERIFIER.addParamDesc(0, "String to get length for");
		VERIFIER.addExample("");
		VERIFIER.addExample("AMI Rocks");
		VERIFIER.addExample("\n\n\n");
	}

	public AmiWebFunctionStrLen(int position, DerivedCellCalculator params) {
		super(position, params);
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object value) {
		if (value == null)
			return null;
		return OH.valueOf(AmiUtils.s(value).length());
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionStrLen(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrLen(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
