package com.f1.ami.amiscript;

import java.math.BigDecimal;

import com.f1.base.CalcFrame;
import com.f1.utils.AH;
import com.f1.utils.casters.Caster_BigDecimal;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_BigDecimal extends AmiScriptBaseMemberMethods<BigDecimal> {

	private AmiScriptMemberMethods_BigDecimal() {
		super();
		addMethod(INIT);
		addMethod(INIT2);
	}

	private final static AmiAbstractMemberMethod<BigDecimal> INIT = new AmiAbstractMemberMethod<BigDecimal>(BigDecimal.class, null, BigDecimal.class, Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BigDecimal target, Object[] params, DerivedCellCalculator caller) {
			Number n = (Number) params[0];
			if (n == null)
				return null;
			return Caster_BigDecimal.INSTANCE.cast(n);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "a big decimal value" };
		}
		@Override
		protected String getHelp() {
			return "Initialize a BigDecimal object with a numerical value.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("").append("\n");
			example.append("BigDecimal bd = new BigDecimal(1.23456789);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "bd","bd.getClassName()" }
					));
			
			return examples;
		}

		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	private final static AmiAbstractMemberMethod<BigDecimal> INIT2 = new AmiAbstractMemberMethod<BigDecimal>(BigDecimal.class, null, BigDecimal.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BigDecimal target, Object[] params, DerivedCellCalculator caller) {
			String n = (String) params[0];
			if (n == null)
				return null;
			return Caster_BigDecimal.INSTANCE.cast(n);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "a big decimal value supplied as a string" };
		}
		@Override
		protected String getHelp() {
			return "Initialize a BigDecimal object by casting a string to a BigDecimal. String must contain only numerical values.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("").append("\n");
			example.append("BigDecimal bd = new BigDecimal(\"1.23456789\");").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "bd","bd.getClassName()" }
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
		return "BigDecimal";
	}
	@Override
	public String getVarTypeDescription() {
		return "An unbounded, signed decimal";
	}
	@Override
	public Class<BigDecimal> getVarType() {
		return BigDecimal.class;
	}
	@Override
	public Class<BigDecimal> getVarDefaultImpl() {
		return BigDecimal.class;
	}

	public static AmiScriptMemberMethods_BigDecimal INSTANCE = new AmiScriptMemberMethods_BigDecimal();
}
