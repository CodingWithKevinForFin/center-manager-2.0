package com.f1.ami.amiscript;

import com.f1.base.Complex;
import com.f1.base.CalcFrame;
import com.f1.utils.AH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Complex extends AmiScriptBaseMemberMethods<Complex> {

	private AmiScriptMemberMethods_Complex() {
		super();
		addMethod(INIT);
		addMethod(GET_REAL, "real");
		addMethod(GET_IMAGINARY, "imaginary");
		addMethod(GET_ABSOLUTE);
		addMethod(GET_CONJUGATE);
		addMethod(GET_EXPONENTIAL);
		addMethod(GET_LOGARITHM);
		addMethod(GET_SQRT);
		addMethod(GET_SIN);
		addMethod(GET_COS);
		addMethod(GET_SINH);
		addMethod(GET_COSH);
		addMethod(GET_TAN);
		addMethod(GET_NEGATIVE);
	}

	private final static AmiAbstractMemberMethod<Complex> INIT = new AmiAbstractMemberMethod<Complex>(Complex.class, null, Complex.class, Number.class, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			Number r = (Number) params[0];
			if (r == null)
				return null;
			Number i = (Number) params[1];
			if (i == null)
				return null;
			return new Complex(r.doubleValue(), i.doubleValue());
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "real", "imaginary" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "value of real part", "value of imaginary part" };
		}
		@Override
		protected String getHelp() {
			return "Initialize a complex number as a Complex object. Must provide the real and imaginary components.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Complex c1 = new Complex(1,2);").append("\n");
			example.append("Complex c2 = 1+2i;").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "c1.getClassName()","c2.getClassName()" }, "These two statements are equivalent."));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};

	private final static AmiAbstractMemberMethod<Complex> GET_REAL = new AmiAbstractMemberMethod<Complex>(Complex.class, "getReal", Double.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.real();
		}
		@Override
		protected String getHelp() {
			return "Returns the real (non <i>i</i>) component of the complex number.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Complex c = 1+2i;").append("\n");
			example.append("c.getReal();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "c.getReal()" }));
			
			return examples;
		}

		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_IMAGINARY = new AmiAbstractMemberMethod<Complex>(Complex.class, "getImaginary", Double.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.imaginary();
		}
		@Override
		protected String getHelp() {
			return "Returns the imaginary (<i>i</i>) component of the complex number.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Complex c = 1+2i;").append("\n");
			example.append("c.getImaginary();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "c.getImaginary()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_ABSOLUTE = new AmiAbstractMemberMethod<Complex>(Complex.class, "getAbsolute", Double.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.modulus();
		}
		@Override
		protected String getHelp() {
			return "Returns the absolute value, or modulus, of this complex number.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Complex c = 3+4i;").append("\n");
			example.append("c.getAbsolute();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "c.getAbsolute()" }));
			
			return examples;
		}
		
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_CONJUGATE = new AmiAbstractMemberMethod<Complex>(Complex.class, "getConjugate", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.conjugate();
		}
		@Override
		protected String getHelp() {
			return "Returns the complex conjugate value of the complex number.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Complex c = 3+4i;").append("\n");
			example.append("c.getConjugate();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "c.getConjugate()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_EXPONENTIAL = new AmiAbstractMemberMethod<Complex>(Complex.class, "getExponential", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.exponential();
		}
		@Override
		protected String getHelp() {
			return "Returns <i>e</i> raised to the power of the supplied complex number.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Complex c = 3+4i;").append("\n");
			example.append("c.getExponential();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "c.getExponential()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	private final static AmiAbstractMemberMethod<Complex> GET_LOGARITHM = new AmiAbstractMemberMethod<Complex>(Complex.class, "getLogarithm", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.logarithm();
		}
		@Override
		protected String getHelp() {
			return "Returns the natural log of the supplied complex value.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Complex c = 3+4i;").append("\n");
			example.append("c.getLogarithm();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "c.getLogarithm()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_NEGATIVE = new AmiAbstractMemberMethod<Complex>(Complex.class, "getNegative", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.negative();
		}
		@Override
		protected String getHelp() {
			return "Returns the negative value of the supplied complex number.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Complex c = 3+4i;").append("\n");
			example.append("c.getNegative();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "c.getNegative()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_SQRT = new AmiAbstractMemberMethod<Complex>(Complex.class, "getSqrt", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.sqrt();
		}
		@Override
		protected String getHelp() {
			return "Returns the square root of the supplied complex number.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Complex c = 3+4i;").append("\n");
			example.append("c.getSqrt();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "c.getSqrt()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_SIN = new AmiAbstractMemberMethod<Complex>(Complex.class, "getSin", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.sin();
		}
		@Override
		protected String getHelp() {
			return "Returns the sine of the supplied complex number.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Complex c = 3+4i;").append("\n");
			example.append("c.getSin();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "c.getSin()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_COS = new AmiAbstractMemberMethod<Complex>(Complex.class, "getCos", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.cos();
		}
		@Override
		protected String getHelp() {
			return "Returns the cosine of the supplied complex number.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Complex c = 3+4i;").append("\n");
			example.append("c.getCos();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "c.getCos()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_SINH = new AmiAbstractMemberMethod<Complex>(Complex.class, "getSinh", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.sinh();
		}
		@Override
		protected String getHelp() {
			return "Returns the hyperbolic sine of the supplied complex number.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Complex c = 3+4i;").append("\n");
			example.append("c.getSinh();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "c.getSinh()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_COSH = new AmiAbstractMemberMethod<Complex>(Complex.class, "getCosh", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.cosh();
		}
		@Override
		protected String getHelp() {
			return "Returns the hyperbolic cosine of the supplied complex number.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Complex c = 3+4i;").append("\n");
			example.append("c.getCosh();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "c.getCosh()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private final static AmiAbstractMemberMethod<Complex> GET_TAN = new AmiAbstractMemberMethod<Complex>(Complex.class, "getTan", Complex.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Complex target, Object[] params, DerivedCellCalculator caller) {
			return target.tan();
		}
		@Override
		protected String getHelp() {
			return "Returns the tangent of the supplied complex number.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Complex c = 3+4i;").append("\n");
			example.append("c.getTan();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "c.getTan()" }));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	@Override
	public String getVarTypeName() {
		return "Complex";
	}
	@Override
	public String getVarTypeDescription() {
		return "A number with a real and imaginary component.";
	}
	@Override
	public Class<Complex> getVarType() {
		return Complex.class;
	}
	@Override
	public Class<Complex> getVarDefaultImpl() {
		return Complex.class;
	}

	public static AmiScriptMemberMethods_Complex INSTANCE = new AmiScriptMemberMethods_Complex();
}
