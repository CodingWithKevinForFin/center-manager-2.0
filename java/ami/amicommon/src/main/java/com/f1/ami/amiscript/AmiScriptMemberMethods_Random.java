package com.f1.ami.amiscript;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.f1.base.CalcFrame;
import com.f1.base.UUID;
import com.f1.utils.AH;
import com.f1.utils.ByteHelper;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Random extends AmiScriptBaseMemberMethods<Random> {

	private AmiScriptMemberMethods_Random() {
		super();

		addMethod(INIT);
		addMethod(NEXT_DOUBLE);
		addMethod(NEXT_INT);
		addMethod(NEXT_BOOLEAN);
		addMethod(NEXT_GAUSSIAN);
		addMethod(NEXT_GUID);
		addMethod(NEXT_UUID);
		addMethod(SHUFFLE);
	}

	private static final AmiAbstractMemberMethod<Random> INIT = new AmiAbstractMemberMethod<Random>(Random.class, null, Random.class, Number.class, Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Random targetObject, Object[] params, DerivedCellCalculator caller) {
			Number seed = (Number) params[0];
			boolean secure = Boolean.TRUE.equals((Boolean) params[1]);
			if (seed == null)
				return secure ? new SecureRandom() : new Random();
			long value;
			if (seed instanceof Double || seed instanceof Float) {
				value = (long) (Long.MAX_VALUE * seed.doubleValue());
			} else
				value = seed.longValue();
			return secure ? new SecureRandom(ByteHelper.asBytes(value)) : new Random(seed.longValue());
		}
		protected String[] buildParamNames() {
			return new String[] { "seed", "secure" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "seed for repeatability. If nullm then the seed is random", "if true, then secure. if null or false, then not secure" };
		}
		@Override
		protected String getHelp() {
			return "Creates a new Randomizer, used for generating random numbers. If a seed is provided, then the numbers generated are the same across runs.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Rand r = new Rand();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "r" }));
		
			example = new StringBuilder();
			example.append("Rand r = new Rand(1,true);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "r" })); 

			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<Random> NEXT_DOUBLE = new AmiAbstractMemberMethod<Random>(Random.class, "nextDouble", Double.class, Number.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Random targetObject, Object[] params, DerivedCellCalculator caller) {
			Number sn = (Number) params[0];
			Number en = (Number) params[1];
			double s = sn == null ? 0 : sn.doubleValue();
			double e = en == null ? 1 : en.doubleValue();
			if (s < e) {
				double t = s;
				s = e;
				e = t;
			}
			return MH.scale(targetObject.nextDouble(), 0d, 1d, s, e);
		}
		protected String[] buildParamNames() {
			return new String[] { "min", "max" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "minimum number to be returned (inclusive). If null, then 0", "maximum number to be returned (exclusive). If null, then 1" };
		}
		@Override
		protected String getHelp() {
			return "Returns a random Double between min and max values.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Rand r = new Rand();").append("\n");
			example.append("r.nextDouble(0,10);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "r.nextDouble(0,10)"}));
		
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<Random> NEXT_INT = new AmiAbstractMemberMethod<Random>(Random.class, "nextInt", Integer.class, Number.class, Number.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Random targetObject, Object[] params, DerivedCellCalculator caller) {
			Number sn = (Number) params[0];
			Number en = (Number) params[1];
			int s = sn == null ? 0 : sn.intValue();
			int e = en == null ? 1 : en.intValue();
			if (e < s) {
				int t = s;
				s = e;
				e = t;
			}
			return MH.rand(targetObject, s, e);
		}
		protected String[] buildParamNames() {
			return new String[] { "min", "max" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "minimum number to be returned (inclusive). If null, then 0", "maximum number to be returned (exclusive). If null, then 1" };
		}
		@Override
		protected String getHelp() {
			return "Returns a random Integer between min and max values.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Rand r = new Rand();").append("\n");
			example.append("r.nextInt(0,10);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "r.nextInt(0,10)"}));
		
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<Random> NEXT_BOOLEAN = new AmiAbstractMemberMethod<Random>(Random.class, "nextBoolean", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Random targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.nextBoolean();
		}
		protected String[] buildParamNames() {
			return OH.EMPTY_STRING_ARRAY;
		}
		@Override
		protected String[] buildParamDescriptions() {
			return OH.EMPTY_STRING_ARRAY;
		}
		@Override
		protected String getHelp() {
			return "Returns a random Boolean value.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Rand r = new Rand();").append("\n");
			example.append("r.nextBoolean();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "r.nextBoolean()"}));
		
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<Random> SHUFFLE = new AmiAbstractMemberMethod<Random>(Random.class, "shuffle", List.class, List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Random targetObject, Object[] params, DerivedCellCalculator caller) {
			List l = (List) params[0];
			if (l == null)
				return null;
			l = new ArrayList(l);
			Collections.shuffle(l, targetObject);
			return l;
		}
		protected String[] buildParamNames() {
			return new String[] { "list" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "list of values to shuffle" };
		}
		@Override
		protected String getHelp() {
			return "Returns a new list with the values' positions randomly shuffled.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Rand r = new Rand();").append("\n");
			example.append("List l = new List(\"a\",\"b\",\"c\");").append("\n");
			example.append("r.shuffle(l);").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "r.shuffle(l)"}));
		
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<Random> NEXT_GAUSSIAN = new AmiAbstractMemberMethod<Random>(Random.class, "nextGaussian", Double.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Random targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.nextGaussian();
		}
		protected String[] buildParamNames() {
			return OH.EMPTY_STRING_ARRAY;
		}
		@Override
		protected String[] buildParamDescriptions() {
			return OH.EMPTY_STRING_ARRAY;
		}
		@Override
		protected String getHelp() {
			return "Return a random signed Double with a mean of 0 and standard deviation of 1.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Rand r = new Rand();").append("\n");
			example.append("r.nextGaussian();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "r.nextGaussian()"}));
		
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<Random> NEXT_GUID = new AmiAbstractMemberMethod<Random>(Random.class, "nextGuid", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Random targetObject, Object[] params, DerivedCellCalculator caller) {
			return MH.nextGuid(targetObject, new StringBuilder(36)).toString();
		}
		protected String[] buildParamNames() {
			return OH.EMPTY_STRING_ARRAY;
		}
		@Override
		protected String[] buildParamDescriptions() {
			return OH.EMPTY_STRING_ARRAY;
		}
		@Override
		protected String getHelp() {
			return "Returns a random string.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Rand r = new Rand();").append("\n");
			example.append("r.nextGuid();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "r.nextGuid()"}));
		
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	private static final AmiAbstractMemberMethod<Random> NEXT_UUID = new AmiAbstractMemberMethod<Random>(Random.class, "nextUUID", UUID.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, Random targetObject, Object[] params, DerivedCellCalculator caller) {
			return new UUID(targetObject);
		}
		protected String[] buildParamNames() {
			return OH.EMPTY_STRING_ARRAY;
		}
		@Override
		protected String[] buildParamDescriptions() {
			return OH.EMPTY_STRING_ARRAY;
		}
		@Override
		protected String getHelp() {
			return "Returns a randomly generated version 4 UUID object.";
		}
		@Override
		public MethodExample[] getExamples() {
			MethodExample examples[] = new MethodExample[0];
			StringBuilder example = new StringBuilder();
			example.append("Rand r = new Rand();").append("\n");
			example.append("r.nextUUID();").append("\n");
			examples = AH.append(examples, new MethodExample(example.toString(), new String[] { "r.nextUUID()"}));
		
			return examples;
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	@Override
	public String getVarTypeName() {
		return "Rand";
	}
	@Override
	public String getVarTypeDescription() {
		return null;
	}
	@Override
	public Class<Random> getVarType() {
		return Random.class;
	}
	@Override
	public Class<Random> getVarDefaultImpl() {
		return null;
	}

	public static AmiScriptMemberMethods_Random INSTANCE = new AmiScriptMemberMethods_Random();
}
