package com.f1.ami.center.table;

import com.f1.ami.amicommon.functions.AmiWebFunctionFactory;
import com.f1.ami.center.AmiCenterState;
import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiCenterFunctionStrEncrypt extends AbstractMethodDerivedCellCalculator1 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strEncrypt", String.class, "String decryptedText");
	static {
		VERIFIER.addDesc("Encode a string");
		VERIFIER.addRetDesc("Strings or null if any param is null");
		VERIFIER.addParamDesc(0, "The plaintext string to encode");
	}
	final private AmiCenterState state;

	public AmiCenterFunctionStrEncrypt(int position, DerivedCellCalculator params, AmiCenterState state) {
		super(position, params);
		this.state = state;
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o) {
		try {
			return this.state.encrypt((String) o);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator params2) {
		return new AmiCenterFunctionStrEncrypt(getPosition(), params2, state);
	}

	public static class Factory implements AmiWebFunctionFactory {

		private AmiCenterState state;

		public Factory(AmiCenterState state) {
			this.state = state;
		}

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiCenterFunctionStrEncrypt(position, calcs[0], state);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
