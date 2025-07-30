package com.f1.ami.amicommon.functions;

import com.f1.base.Mapping;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionStrEqualsIgnoreCase extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("strEqualsIgnoreCase", Boolean.class, "String left,String right");

	static {
		VERIFIER.addDesc("Returns true if the two supplied strings are equal ignoring case or if both are null, false otherwise.");
		VERIFIER.addParamDesc(0, "The First string to text");
		VERIFIER.addParamDesc(1, "The Second string to text");
		VERIFIER.addExample("me", "Me");
		VERIFIER.addExample("You", "YOU");
		VERIFIER.addExample("YouTo", "YOU2");
		VERIFIER.addExample(null, null);
		VERIFIER.addExample(null, "I");
	}

	public AmiWebFunctionStrEqualsIgnoreCase(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object eval(Object o0, Object o1) {
		String value1 = (String) o0;
		String value2 = (String) o1;
		return SH.equalsIgnoreCase(value1, value2);
	}

	@Override
	protected boolean shortCircuitNull() {
		return false;
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionStrEqualsIgnoreCase(getPosition(), p0, p1);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionStrEqualsIgnoreCase(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
