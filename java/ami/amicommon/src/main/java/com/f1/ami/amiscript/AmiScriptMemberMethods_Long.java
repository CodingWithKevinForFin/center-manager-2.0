package com.f1.ami.amiscript;

import com.f1.utils.AH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Long extends AmiScriptBaseMemberMethods<Long> {

	private AmiScriptMemberMethods_Long() {
		super();
		addMethod(BYTE_VALUE);
		//		addMethod(this.compareTo);
		//		addMethod(this.equals);
		addMethod(INIT);
		addMethod(AmiScriptMemberMethods_Long.HIGHEST_ONE_BIT);
		addMethod(AmiScriptMemberMethods_Long.LOWEST_ONE_BIT);
		addMethod(AmiScriptMemberMethods_Long.NUMBER_OF_TRAILING_ZEROES);
		addMethod(AmiScriptMemberMethods_Long.NUMBER_OF_LEADING_ZEROS);
		//		addMethod(AmiScriptMemberMethods_Long.toString);
	}

	private static final AmiAbstractMemberMethod<Long> INIT = new AmiAbstractMemberMethod<Long>(Long.class, null, Object.class, false, Long.class) {
		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "a long value" };
		}
		

		@Override
		protected String getHelp() {
			return "Initialize a Long object.";
		}
		
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Long l = 100l;").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l" }));
			
			return examples;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Long targetObject, Object[] params, DerivedCellCalculator caller) {
			return (Long) params[0];
		}
		

	};

	private static final AmiAbstractMemberMethod<Long> BYTE_VALUE = new AmiAbstractMemberMethod<Long>(Long.class, "byteValue", Byte.class) {
		@Override
		protected String getHelp() {
			return "Returns the value of this Long as a Byte after a narrowing primitive conversion.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Long l = 100;").append("\n");
			example.append("l.byteValue();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.byteValue()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Long targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.byteValue();
		};
	};

	private static final AmiAbstractMemberMethod<Long> HIGHEST_ONE_BIT = new AmiAbstractMemberMethod<Long>(Long.class, "highestOneBit", Long.class) {
		@Override
		protected String getHelp() {
			return "Returns a Long value with a single one-bit in the position of the highest-order (\"leftmost\") one-bit of this Long, or 0 if the value supplied is 0.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Long l = 100;").append("\n");
			example.append("l.highestOneBit();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.highestOneBit()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Long targetObject, Object[] params, DerivedCellCalculator caller) {
			return Long.highestOneBit(targetObject);
		};
	};

	private static final AmiAbstractMemberMethod<Long> LOWEST_ONE_BIT = new AmiAbstractMemberMethod<Long>(Long.class, "lowestOneBit", Long.class) {
		@Override
		protected String getHelp() {
			return "Returns a Long value with a single one-bit in the position of the lowest-order (\"rightmost\") one-bit of the supplied long value, or 0 if the value supplied is 0.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Long l = 100;").append("\n");
			example.append("l.lowestOneBit();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.lowestOneBit()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Long targetObject, Object[] params, DerivedCellCalculator caller) {
			return Long.lowestOneBit(targetObject);
		};
	};

	private static final AmiAbstractMemberMethod<Long> NUMBER_OF_LEADING_ZEROS = new AmiAbstractMemberMethod<Long>(Long.class, "numberOfLeadingZeros", Integer.class) {
		@Override
		protected String getHelp() {
			return "Returns the number of zero bits preceding the highest-order (\"leftmost\") one-bit in the two's complement binary representation of this Long.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Long l = 100;").append("\n");
			example.append("l.numberOfLeadingZeros();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.numberOfLeadingZeros()" }));
			
			return examples;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Long targetObject, Object[] params, DerivedCellCalculator caller) {
			return Long.numberOfLeadingZeros(targetObject);
		};
	};

	private static final AmiAbstractMemberMethod<Long> NUMBER_OF_TRAILING_ZEROES = new AmiAbstractMemberMethod<Long>(Long.class, "numberOfTrailingZeros", Integer.class) {

		@Override
		protected String getHelp() {
			return "Returns the number of zero bits following the lowest-order (\"rightmost\") one-bit in the two's complement binary representation of this Long.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Long l= 100;").append("\n");
			example.append("l.numberOfTrailingZeros();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "l.numberOfTrailingZeros()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Long targetObject, Object[] params, DerivedCellCalculator caller) {
			return Long.numberOfTrailingZeros(targetObject);
		};
	};

	@Override
	public String getVarTypeName() {
		return "Long";
	}

	@Override
	public String getVarTypeDescription() {
		return "A number of type Long.";
	}

	@Override
	public Class<Long> getVarType() {
		return Long.class;
	}

	@Override
	public Class<? extends Long> getVarDefaultImpl() {
		return Long.class;
	}

	public static AmiScriptMemberMethods_Long INSTANCE = new AmiScriptMemberMethods_Long();
}
