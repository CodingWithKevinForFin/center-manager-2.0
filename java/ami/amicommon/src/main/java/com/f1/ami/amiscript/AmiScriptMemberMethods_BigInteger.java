package com.f1.ami.amiscript;

import java.math.BigInteger;

import com.f1.base.CalcFrame;
import com.f1.utils.AH;
import com.f1.utils.casters.Caster_BigInteger;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_BigInteger extends AmiScriptBaseMemberMethods<BigInteger> {

	private AmiScriptMemberMethods_BigInteger() {
		super();
		addMethod(INIT);
		addMethod(INIT2);
	}

	private final static AmiAbstractMemberMethod<BigInteger> INIT = new AmiAbstractMemberMethod<BigInteger>(BigInteger.class, null, BigInteger.class, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BigInteger target, Object[] params, DerivedCellCalculator caller) {
			Number n = (Number) params[0];
			if (n == null)
				return null;
			return Caster_BigInteger.INSTANCE.cast(n);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "a big integer value" };
		}
		@Override
		protected String getHelp() {
			return "Initialize a BigInteger object with a numerical value.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("").append("\n");
			example.append("BigInteger bi = new BigInteger(123456789);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "bi","bi.getClassName()" }
					));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private final static AmiAbstractMemberMethod<BigInteger> INIT2 = new AmiAbstractMemberMethod<BigInteger>(BigInteger.class, null, BigInteger.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BigInteger target, Object[] params, DerivedCellCalculator caller) {
			String n = (String) params[0];
			if (n == null)
				return null;
			return Caster_BigInteger.INSTANCE.cast(n);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "a big integer value supplied as a string" };
		}
		@Override
		protected String getHelp() {
			return "Initialize a BigInteger object by casting a string to a BigInteger. String must contain only numerical values.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("").append("\n");
			example.append("BigInteger bi = new BigInteger(\"123456789\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "bi","bi.getClassName()" }
					));
			
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};

	@Override
	public String getVarTypeName() {
		return "BigInteger";
	}
	@Override
	public String getVarTypeDescription() {
		return "An unbounded, signed integer";
	}
	@Override
	public Class<BigInteger> getVarType() {
		return BigInteger.class;
	}
	@Override
	public Class<BigInteger> getVarDefaultImpl() {
		return BigInteger.class;
	}

	public static AmiScriptMemberMethods_BigInteger INSTANCE = new AmiScriptMemberMethods_BigInteger();
}
