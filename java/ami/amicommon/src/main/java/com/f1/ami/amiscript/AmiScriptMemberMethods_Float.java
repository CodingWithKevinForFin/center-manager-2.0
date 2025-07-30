package com.f1.ami.amiscript;

import com.f1.utils.AH;
import com.f1.utils.MH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Float extends AmiScriptBaseMemberMethods<Float> {

	private AmiScriptMemberMethods_Float() {
		super();
		addMethod(BYTE_VALUE);
		//		addMethod(this.compareTo);
		//		addMethod(this.equals);
		addMethod(INIT);
		addMethod(IS_FINITE);
		addMethod(IS_INFINITE);
		addMethod(IS_NAN);
		//		addMethod(AmiScriptMemberMethods_Float.toString);
	}

	private final static AmiAbstractMemberMethod<Float> INIT = new AmiAbstractMemberMethod<Float>(Float.class, null, Object.class, false, Float.class) {
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
			return new String[] { "a float value" };
		}

		@Override
		protected String getHelp() {
			return "Initialize a 32 bit Float object.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Float f = 0.5f;").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "f" }));
			return examples;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, Float targetObject, Object[] params, DerivedCellCalculator caller) {
			return (Float) params[0];
		}

	};

	private final static AmiAbstractMemberMethod<Float> BYTE_VALUE = new AmiAbstractMemberMethod<Float>(Float.class, "byteValue", Byte.class) {
		@Override
		protected String getHelp() {
			return "Returns the value of this Float as a Byte after a narrowing primitive conversion.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Float f = 0.5;").append("\n");
			example.append("f.byteValue();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "f.byteValue()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Float targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.byteValue();
		};
	};

	private final static AmiAbstractMemberMethod<Float> IS_FINITE = new AmiAbstractMemberMethod<Float>(Float.class, "isFinite", Boolean.class) {

		@Override
		protected String getHelp() {
			return "Returns \"true\" if this Float has a finite value; returns \"false\" otherwise (for NaN and infinity arguments).";
		}
		
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Float f = 0.5;").append("\n");
			example.append("f.isFinite();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "f.isFinite()" }));
			
			return examples;
		}
		
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Float targetObject, Object[] params, DerivedCellCalculator caller) {
			return MH.isFinite(targetObject);
		};
	};

	private final static AmiAbstractMemberMethod<Float> IS_INFINITE = new AmiAbstractMemberMethod<Float>(Float.class, "isInfinite", Boolean.class) {

		@Override
		protected String getHelp() {
			return "Returns \"true\" if this Float is infinitely large in magnitude, \"false\" otherwise.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Float f = 1f/0f;").append("\n");
			example.append("f.isInfinite();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "f.isInfinite()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Float targetObject, Object[] params, DerivedCellCalculator caller) {
			return Float.isInfinite(targetObject);
		};
	};

	private final static AmiAbstractMemberMethod<Float> IS_NAN = new AmiAbstractMemberMethod<Float>(Float.class, "isNaN", Boolean.class) {

		@Override
		protected String getHelp() {
			return "Returns \"true\" if this Float instance is Not-a-Number (NaN) value, \"false\" otherwise.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Float f = power(-1,0.5f);").append("\n");
			example.append("f.isNaN();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "f.isNaN()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Float targetObject, Object[] params, DerivedCellCalculator caller) {
			return Float.isNaN(targetObject);
		};
	};

	@Override
	public String getVarTypeName() {
		return "Float";
	}

	@Override
	public String getVarTypeDescription() {
		return "A number of type Float.";
	}

	@Override
	public Class<Float> getVarType() {
		return Float.class;
	}

	@Override
	public Class<? extends Float> getVarDefaultImpl() {
		return Float.class;
	}

	public static AmiScriptMemberMethods_Float INSTANCE = new AmiScriptMemberMethods_Float();
}
