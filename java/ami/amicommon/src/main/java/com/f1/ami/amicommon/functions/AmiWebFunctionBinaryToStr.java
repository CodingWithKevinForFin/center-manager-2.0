package com.f1.ami.amicommon.functions;

import com.f1.base.Bytes;
import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionBinaryToStr extends AbstractMethodDerivedCellCalculator1 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("binaryToStr", String.class, "com.f1.base.Bytes data");
	static {
		VERIFIER.addDesc("Converts binary to a UTF string.");
		VERIFIER.addParamDesc(0, "The string to convert");
	}

	public AmiWebFunctionBinaryToStr(int position, DerivedCellCalculator params) {
		super(position, params);

	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o) {
		Bytes value = (Bytes) o;
		if (value == null)
			return null;
		return new String(value.getBytes());
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiWebFunctionBinaryToStr(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionBinaryToStr(position, calcs[0]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
