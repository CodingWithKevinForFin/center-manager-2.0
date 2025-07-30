package com.f1.ami.amicommon.functions;

import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionToHex extends AbstractMethodDerivedCellCalculator1 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("toHex", Integer.class, "Number num");
	static {
		VERIFIER.addDesc("Converts number to base 16 representation as a String");
		VERIFIER.addParamDesc(0, "Number to be converted to base 16");
		VERIFIER.addExample(16);
		VERIFIER.addExample(-100);
		VERIFIER.addExample(3);
		VERIFIER.addExample(257);

	}
	
	public AmiWebFunctionToHex(int position, DerivedCellCalculator params) {
		super(position, params);
	}
	
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}
	
	@Override
	public Object eval(Object o) {
		Integer value = (Integer) o;
		if (value == null)
			return null;
		return Integer.toHexString(value);
	}
	
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionToHex(getPosition(), params2);
	}
	
	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionToHex(position, calcs[0]);
		}

	}
	
}
