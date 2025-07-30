package com.f1.ami.amiscript;

import com.f1.utils.AH;
import com.f1.utils.MH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Double extends AmiScriptBaseMemberMethods<Double> {

	private AmiScriptMemberMethods_Double() {
		super();
		addMethod(BYTE_VALUE);
		//		addMethod(this.compareTo);
		//		addMethod(this.equals);
		addMethod(INIT);
		addMethod(AmiScriptMemberMethods_Double.IS_FINITE);
		addMethod(AmiScriptMemberMethods_Double.IS_INFINITE);
		addMethod(AmiScriptMemberMethods_Double.IS_NAN);
		//		addMethod(AmiScriptMemberMethods_Double.toString);
	}

	private final static AmiAbstractMemberMethod<Double> INIT = new AmiAbstractMemberMethod<Double>(Double.class, null, Object.class, false, Double.class) {
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
			return new String[] { "a double value" };
		}

		@Override
		protected String getHelp() {
			return "Initialize a 64 bit Double object.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Double d = 0.5d;").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "d" }));
			return examples;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, Double targetObject, Object[] params, DerivedCellCalculator caller) {
			return (Double) params[0];
		}

	};

	private final static AmiAbstractMemberMethod<Double> BYTE_VALUE = new AmiAbstractMemberMethod<Double>(Double.class, "byteValue", Byte.class) {
		@Override
		protected String getHelp() {
			return "Returns the value of this Double as a Byte after a narrowing primitive conversion.";
		}
		
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Double d = 0.5;").append("\n");
			example.append("d.byteValue();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "d.byteValue()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Double targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.byteValue();
		};
	};

	//	public final AmiAbstractMemberMethod<Double> compareTo = new AmiAbstractMemberMethod<Double>(Double.class, "compareTo", Integer.class, Double.class) {
	//		@Override
	//		protected String[] buildParamNames() {
	//			return new String[] { "arg" };
	//		}
	//		@Override
	//		protected String[] buildParamDescriptions() {
	//			return new String[] { "Another double" };
	//		}
	//		@Override
	//		protected String getHelp() {
	//			return "Compares and returns the value 0 if the argument is numerically equal to this Double; a value less than 0 if this Double is numerically less than the argument; and a value greater than 0 if this Double is numerically greater than the argument.";
	//		}
	//		@Override
	//		public boolean isReadOnly() {
	//			return true;
	//		}
	//		@Override
	//		public Object invokeMethod(Map<String,Object> ei, Double targetObject, Object[] params, DerivedCellCalculator caller) {
	//			return targetObject.compareTo((Double) params[0]);
	//		};
	//	};
	//
	//	public final AmiAbstractMemberMethod<Double> equals = new AmiAbstractMemberMethod<Double>(Double.class, "equals", Boolean.class, Object.class) {
	//		@Override
	//		protected String[] buildParamNames() {
	//			return new String[] { "Object" };
	//		}
	//		@Override
	//		protected String[] buildParamDescriptions() {
	//			return new String[] { "an Object argument" };
	//		}
	//		@Override
	//		protected String getHelp() {
	//			return "Compares this object against the specified object. Returns true if the objects are the same; false otherwise.";
	//		}
	//		@Override
	//		public boolean isReadOnly() {
	//			return true;
	//		}
	//		@Override
	//		public Object invokeMethod(Map<String,Object> ei, Double targetObject, Object[] params, DerivedCellCalculator caller) {
	//			return targetObject.equals(params[0]);
	//		};
	//	};

	private static final AmiAbstractMemberMethod<Double> IS_FINITE = new AmiAbstractMemberMethod<Double>(Double.class, "isFinite", Boolean.class) {

		@Override
		protected String getHelp() {
			return "Returns \"true\" if this Double has a finite value; returns \"false\" otherwise (for NaN and infinity arguments).";
		}
		
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Double d = 0.5;").append("\n");
			example.append("d.isFinite();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "d.isFinite()" }));
			
			return examples;
		}
		
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Double targetObject, Object[] params, DerivedCellCalculator caller) {
			return MH.isFinite(targetObject);
		};
	};

	private static final AmiAbstractMemberMethod<Double> IS_INFINITE = new AmiAbstractMemberMethod<Double>(Double.class, "isInfinite", Boolean.class) {

		@Override
		protected String getHelp() {
			return "Returns \"true\" if this Double is infinitely large in magnitude, \"false\" otherwise.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Double d = 0.5;").append("\n");
			example.append("d.isInfinite();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "d.isInfinite()" }));
			
			return examples;
		}
		
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Double targetObject, Object[] params, DerivedCellCalculator caller) {
			return Double.isInfinite(targetObject);
		};
	};

	private static final AmiAbstractMemberMethod<Double> IS_NAN = new AmiAbstractMemberMethod<Double>(Double.class, "isNaN", Boolean.class) {

		@Override
		protected String getHelp() {
			return "Returns \"true\" if this Double instance is a Not-a-Number (NaN) value, \"false\" otherwise.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Double d = power(-1,0.5d);").append("\n");
			example.append("d.isNaN();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "d.isNaN()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Double targetObject, Object[] params, DerivedCellCalculator caller) {
			return Double.isNaN(targetObject);
		};
	};

	//	public final AmiAbstractMemberMethod<Double> longValue = new AmiAbstractMemberMethod<Double>(Double.class, "longValue", Long.class) {
	//
	//		@Override
	//		protected String getHelp() {
	//			return "Returns the value of this Double as a long after a narrowing primitive conversion.";
	//		}
	//		@Override
	//		public boolean isReadOnly() {
	//			return true;
	//		}
	//		@Override
	//		public Object invokeMethod(Map<String,Object> ei, Double targetObject, Object[] params, DerivedCellCalculator caller) {
	//			return targetObject.longValue();
	//		};
	//	};

	//	public final AmiAbstractMemberMethod<Double> shortValue = new AmiAbstractMemberMethod<Double>(Double.class, "shortValue", Short.class) {
	//
	//		@Override
	//		protected String getHelp() {
	//			return "Returns the value of this Double as a short after a narrowing primitive conversion.";
	//		}
	//		@Override
	//		public boolean isReadOnly() {
	//			return true;
	//		}
	//		@Override
	//		public Object invokeMethod(Map<String,Object> ei, Double targetObject, Object[] params, DerivedCellCalculator caller) {
	//			return targetObject.shortValue();
	//		};
	//	};

	//	public static final AmiAbstractMemberMethod<Double> toString = new AmiAbstractMemberMethod<Double>(Double.class, "toString", String.class) {
	//
	//		@Override
	//		protected String getHelp() {
	//			return "Returns a string representation of this Double .";
	//		}
	//		@Override
	//		public boolean isReadOnly() {
	//			return true;
	//		}
	//		@Override
	//		public Object invokeMethod(Map<String,Object> ei, Double targetObject, Object[] params, DerivedCellCalculator caller) {
	//			return Double.toString(targetObject);
	//		};
	//	};

	@Override
	public String getVarTypeName() {
		return "Double";
	}

	@Override
	public String getVarTypeDescription() {
		return "A number of type Double.";
	}

	@Override
	public Class<Double> getVarType() {
		return Double.class;
	}

	@Override
	public Class<? extends Double> getVarDefaultImpl() {
		return Double.class;
	}

	public static AmiScriptMemberMethods_Double INSTANCE = new AmiScriptMemberMethods_Double();
}
