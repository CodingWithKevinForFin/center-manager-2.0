package com.f1.ami.amicommon.functions;

import com.f1.base.Bytes;
import com.f1.base.Mapping;
import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrToBinary extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strToBinary", Bytes.class, "String text,Integer base");
	static {
		VERIFIER.addDesc("Given a string in the specified base, returns it as binary data. E.g. if specified 64, must provide a string in base 64.");
		VERIFIER.addParamDesc(0, "The string to encode");
		VERIFIER.addParamDesc(1, "The base of the provided string. Must be either 16, 64 or 256");
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	public AmiWebFunctionStrToBinary(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}

	@Override
	public Bytes eval(Object o0, Object o1) {
		String value = (String) o0;
		Integer base = (Integer) o1;
		try {
			switch (base) {
				case 16:
					return new Bytes(EncoderUtils.decode16(value));
				case 64:
					return new Bytes(EncoderUtils.decode64(value));
				case 256:
					return new Bytes(value.getBytes());
				default:
					return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params0, DerivedCellCalculator params1) {
		return new AmiWebFunctionStrToBinary(getPosition(), param0, params1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrToBinary(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
