package com.f1.ami.amicommon.functions;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.f1.base.Bytes;
import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator3;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionSignDataMac extends AbstractMethodDerivedCellCalculator3 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("signDataMac", Bytes.class, "com.f1.base.Bytes data,com.f1.base.Bytes key,String algorithm");
	static {
		VERIFIER.addDesc("Returns a signed value as a Byte object given key and algorithm.");
		VERIFIER.addParamDesc(0, "Binary data to be signed");
		VERIFIER.addParamDesc(1, "Binary value of key to be used for signing");
		VERIFIER.addParamDesc(2, "Algorithm to be used for signing, e.g. HMAC-SHA256");
	}

	public AmiWebFunctionSignDataMac(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		super(position, p0, p1, p2);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Bytes eval(Object o0, Object o1, Object o2) {
		Bytes data = (Bytes) o0;
		Bytes key = (Bytes) o1;
		String algorithm = (String) o2;
		if (data == null || key == null || algorithm == null)
			return null;
		try {
			Mac mac = Mac.getInstance(algorithm);
			mac.init(new SecretKeySpec(key.getBytes(), algorithm));
			return new Bytes(mac.doFinal(data.getBytes()));
		} catch (Exception e) {
			return null;
		}
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionSignDataMac(getPosition(), p0, p1, p2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionSignDataMac(position, calcs[0], calcs[1], calcs[2]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
