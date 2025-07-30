package com.f1.ami.amicommon.functions;

import com.f1.utils.DateFormatNano;
import com.f1.utils.math.PrimitiveMathManager;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionParseInt extends AbstractMethodDerivedCellCalculator2 {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("parseInt", Integer.class, "String num,Number radix");
	static {
		VERIFIER.addDesc("Converts num of base radix into base 10");
		VERIFIER.addParamDesc(0, "String representation of the number to be convereted to base 10");
		VERIFIER.addParamDesc(1, "Base of num. Supported bases are 2, 8, 10, 16.");
		VERIFIER.addExample("0xab", 16);
		VERIFIER.addExample("-70ab", 16);
		VERIFIER.addExample("171", 8);
		VERIFIER.addExample("10001011", 2);

	}
	
	public AmiWebFunctionParseInt(int position, DerivedCellCalculator p0, DerivedCellCalculator p1) {
		super(position, p0, p1);
		evalConsts();
	}
	
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1) {
		return new AmiWebFunctionParseInt(getPosition(), p0, p1);
	}
	
	@Override
	public Object eval(Object o1, Object o2) {	
		if (o1 == null || o2 == null)
			return null;
				
		String value = (String) o1;
		int radix = (Integer)o2;
		
		if(radix != 2 && radix != 8 && radix != 10 && radix != 16) {
			throw new RuntimeException("Radix must be 2, 8, 10, or 16");
		}
		
		if (value.length() == 0)
			return null;
		
		if(radix == 16 && value.contains("0x")) {
			value = value.replace("0x", "");
		}

		try {
			return Integer.parseInt(value, radix);
		} catch(NumberFormatException e) {
			throw new RuntimeException("Invalid number format");
		}
	}
	
	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionParseInt(position, calcs[0], calcs[1]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}
}
