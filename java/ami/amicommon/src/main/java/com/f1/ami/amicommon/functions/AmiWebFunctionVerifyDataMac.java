package com.f1.ami.amicommon.functions;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.f1.base.Bytes;
import com.f1.base.Mapping;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculatorN;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionVerifyDataMac extends AbstractMethodDerivedCellCalculatorN {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("verifyMac", Boolean.class,
			"com.f1.base.Bytes data,com.f1.base.Bytes key,String algorithm,com.f1.base.Bytes mac");
	static {
		VERIFIER.addDesc(
				"Verifies the Message Authentication Code (MAC) of the specified binary data with the specified key and algorithm and returns true if the MAC generated from the data using the algorithm is the same as the given MAC, false otherwise.");
		VERIFIER.addParamDesc(0, "Binary data to be signed");
		VERIFIER.addParamDesc(1, "Binary value of key to be used for signing");
		VERIFIER.addParamDesc(2, "Algorithm to be used for signing, e.g. HMAC-SHA256");
		VERIFIER.addParamDesc(3, "Binary mac to be verified");
	}

	public AmiWebFunctionVerifyDataMac(int position, DerivedCellCalculator[] params) {
		super(position, params);
		evalConsts();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o[]) {
		// Does the same work as AmiWebFunctionSignDataMac but then compares the mac's
		Bytes data = (Bytes) o[0];
		Bytes key = (Bytes) o[1];
		String algorithm = (String) o[2];
		Bytes omac = (Bytes) o[3];
		if (data == null || key == null || algorithm == null)
			return null;
		if (omac == null)
			return false;
		Bytes nmac = null;
		try {
			Mac mac = Mac.getInstance(algorithm);
			mac.init(new SecretKeySpec(key.getBytes(), algorithm));
			nmac = new Bytes(mac.doFinal(data.getBytes()));
			return OH.eq(nmac, omac);
		} catch (Exception e) {
			return null;
		}

	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator[] params2) {
		return new AmiWebFunctionVerifyDataMac(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionVerifyDataMac(position, calcs);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
