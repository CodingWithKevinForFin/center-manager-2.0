package com.f1.utils.structs.table.derived;

import java.util.Arrays;
import java.util.Map;

import com.f1.base.CalcFrame;
import com.f1.base.Caster;
import com.f1.utils.AH;
import com.f1.utils.Hasher;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.node.MethodDeclarationNode;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class ParamsDefinition {

	public static final byte MODIFIER_VIRTUAL = MethodDeclarationNode.MODIFIER_VIRTUAL;
	final private String[] names;
	final private Class<?>[] types;
	final private Caster<?>[] casters;
	final private String format;
	final private String methodName;
	final private boolean isVarArg;
	final private com.f1.base.CalcTypes paramTypesMapping;
	private String description = "";
	private String[] paramDefinitions;
	private String returnDescription;
	private Object[][] examples = new Object[0][];
	private MethodExample[] advancedExamples = new MethodExample[0];
	final private String toString;
	final private Class returnType;
	final private byte modifiers;
	final private int hashcode;//includes all final fields
	final private int hashcodeDef;//this only includes fields that uniquely define this definition, ex: param names is excluded
	final private int hashcodeDefIgnoreReturnType;

	@Deprecated
	public ParamsDefinition(String methodName, String[] names, Class<?>[] types, boolean isVarArg) {
		this(methodName, null, names, types, isVarArg, (byte) 0);
	}
	public ParamsDefinition(String methodName, Class returnType, String[] names, Class<?>[] types, boolean isVarArg, byte modifiers) {
		this.methodName = methodName;
		this.names = names;
		this.types = types;
		this.casters = OH.getAllCasters(this.types);
		this.modifiers = modifiers;
		this.returnType = returnType;
		this.paramDefinitions = new String[names.length];
		OH.assertEq(names.length, types.length);
		StringBuilder format = new StringBuilder();
		for (int i = 0; i < names.length; i++) {
			if (i > 0)
				format.append(',');
			SH.getSimpleName(types[i], format).append(' ');
			if (isVarArg && i == names.length - 1)
				format.append("... ");
			format.append(names[i]);
		}
		this.format = format.toString();
		this.isVarArg = isVarArg;
		this.toString = toModifiersString(modifiers) + methodName + "(" + format + ")";
		if (types.length == 0)
			this.paramTypesMapping = EmptyCalcTypes.INSTANCE;
		else {
			com.f1.utils.structs.table.stack.BasicCalcTypes hm = new com.f1.utils.structs.table.stack.BasicCalcTypes();
			for (int i = 0; i < names.length; i++)
				hm.putType(names[i], types[i]);
			this.paramTypesMapping = hm;
		}
		this.hashcodeDef = OH.hashCode(OH.hashCode(methodName), OH.hashCode(returnType), Arrays.hashCode(types), OH.hashCode(isVarArg));
		this.hashcodeDefIgnoreReturnType = OH.hashCode(OH.hashCode(methodName), Arrays.hashCode(types), OH.hashCode(isVarArg));
		this.hashcode = OH.hashCode(this.hashcodeDef, Arrays.hashCode(this.names), this.modifiers);
	}
	private static String toModifiersString(byte modifiers) {
		if (modifiers == MODIFIER_VIRTUAL)
			return "Virtual ";
		return "";
	}
	public String toString() {
		return this.toString;
	}
	public String toString(MethodFactoryManager mfm) {
		return toString(mfm, new StringBuilder()).toString();
	}
	public StringBuilder toString(MethodFactoryManager mfm, StringBuilder sink) {
		sink.append(toModifiersString(modifiers));
		if (returnType != null)
			sink.append(mfm.forType(returnType)).append(' ');
		sink.append(methodName).append('(');
		for (int i = 0; i < names.length; i++) {
			if (i > 0)
				sink.append(',');
			sink.append(mfm.forType(types[i]));
			if (isVarArg && i == names.length - 1)
				sink.append(" ...");
			sink.append(' ').append(names[i]);
		}
		sink.append(')');
		return sink;
	}
	public StringBuilder toString(MethodFactoryManager mfm, StringBuilder sink, boolean includeModifiers, boolean includeReturnType, boolean includeTypes, boolean includeNames) {
		if (includeModifiers)
			sink.append(toModifiersString(modifiers));
		if (includeReturnType)
			if (returnType != null)
				sink.append(mfm.forType(returnType)).append(' ');
		sink.append(methodName).append('(');
		if (includeTypes || includeNames)
			for (int i = 0; i < names.length; i++) {
				if (i > 0)
					sink.append(',');
				if (includeTypes) {
					sink.append(mfm.forType(types[i]));
					if (isVarArg && i == names.length - 1)
						sink.append(" ...");
				}
				if (includeNames)
					sink.append(' ').append(names[i]);
			}
		sink.append(')');
		return sink;
	}

	//	@Deprecated
	//	public ParamsDefinition(String methodName, String format) {jjj
	//		this(methodName, null, format);
	//	}
	public ParamsDefinition(String methodName, Class returnType, String format) {
		this.modifiers = 0;
		this.returnType = returnType;
		this.methodName = methodName;
		this.format = format;
		String[] parts = SH.split(',', format);
		this.paramDefinitions = new String[parts.length];
		this.names = new String[parts.length];
		this.types = new Class[parts.length];
		boolean isVarArg = false;
		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			String type;
			String name;
			if (i == parts.length - 1 && part.contains("...")) {
				type = SH.trim(SH.beforeFirst(part, "..."));
				name = SH.trim(SH.afterFirst(part, "..."));
				isVarArg = true;
			} else {
				type = SH.trim(SH.beforeFirst(part, ' '));
				name = SH.trim(SH.afterFirst(part, ' '));
			}
			name = SH.trim(name);
			assertValuedVar(name);
			this.names[i] = name;
			this.types[i] = RH.getClass(type.indexOf('.') == -1 ? ("java.lang." + type) : type);
		}
		this.casters = OH.getAllCasters(this.types);
		this.isVarArg = isVarArg;
		this.toString = methodName + "(" + format + ")";
		if (types.length == 0)
			this.paramTypesMapping = EmptyCalcTypes.INSTANCE;
		else {
			com.f1.utils.structs.table.stack.BasicCalcTypes hm = new com.f1.utils.structs.table.stack.BasicCalcTypes();
			for (int i = 0; i < names.length; i++)
				hm.putType(names[i], types[i]);
			this.paramTypesMapping = hm;
		}
		this.hashcodeDef = OH.hashCode(OH.hashCode(methodName), OH.hashCode(returnType), Arrays.hashCode(types), OH.hashCode(isVarArg));
		this.hashcodeDefIgnoreReturnType = OH.hashCode(OH.hashCode(methodName), Arrays.hashCode(types), OH.hashCode(isVarArg));
		this.hashcode = OH.hashCode(this.hashcodeDef, Arrays.hashCode(this.names), this.modifiers);
	}

	private void assertValuedVar(String name) {
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (OH.isBetween(c, 'A', 'Z') || OH.isBetween(c, 'a', 'z') || c == '_')
				continue;
			if (OH.isBetween(c, '0', '9') && i != 0)
				continue;
			throw new RuntimeException("Bad var name at char " + i + ": " + name);
		}

	}
	public void verify(MethodDerivedCellCalculator calc) {
		verify(calc, true);
	}
	public void verify(MethodDerivedCellCalculator calc, boolean checkReturnType) {
		int paramsLength = calc.getParamsCount();
		//		final DerivedCellCalculator[] params = calc.getParams();
		if (OH.ne(methodName, calc.getMethodName()))
			throw new ExpressionParserException(calc.getPosition(), "Bad method name for method " + calc.getMethodName());
		if (checkReturnType && this.returnType != null && OH.ne(this.returnType, calc.getReturnType()))
			throw new ExpressionParserException(calc.getPosition(), "Bad return type for method " + calc.getMethodName() + ": " + this.returnType + " vs " + calc.getReturnType());
		if (isVarArg) {
			if (names.length - 1 > paramsLength)
				throw new ExpressionParserException(calc.getPosition(),
						"Expecting at least " + (names.length - 1) + " argument(s) not " + paramsLength + " for method " + calc.getMethodName() + "(" + format + ")");
			for (int i = 0; i < paramsLength; i++) {
				if (!isAssignableFrom(types[Math.min(i, types.length - 1)], calc.getParamAt(i)))
					throw new ExpressionParserException(calc.getPosition(), "Argument " + (i + 1) + " for method should not be "
							+ calc.getParamAt(i).getReturnType().getSimpleName() + ": " + calc.getMethodName() + "(" + format + ")");
			}
		} else {
			if (names.length != paramsLength)
				throw new ExpressionParserException(calc.getPosition(),
						"Expecting " + names.length + " argument(s) not " + paramsLength + " for method " + calc.getMethodName() + "(" + format + ")");
			for (int i = 0; i < paramsLength; i++) {
				if (!isAssignableFrom(types[i], calc.getParamAt(i)))
					throw new ExpressionParserException(calc.getPosition(), "Argument " + (i + 1) + " for method should not be "
							+ calc.getParamAt(i).getReturnType().getSimpleName() + ": " + calc.getMethodName() + "(" + format + ")");
			}
		}
	}
	public boolean canAccept(DerivedCellCalculator params[]) {
		if (isVarArg) {
			if (names.length - 1 > params.length)
				return false;
			for (int i = 0; i < params.length; i++) {
				if (!isAssignableFrom(types[Math.min(i, types.length - 1)], params[i]))
					return false;
			}
		} else {
			if (names.length != params.length)
				return false;
			for (int i = 0; i < params.length; i++) {
				if (!isAssignableFrom(types[i], params[i]))
					return false;
			}
		}
		return true;
	}
	public boolean canAcceptArguments(Object params[]) {
		if (isVarArg) {
			if (names.length - 1 > params.length)
				return false;
			for (int i = 0; i < params.length; i++) {
				if (!isAssignableFromObject(types[Math.min(i, types.length - 1)], params[i]))
					return false;
			}
		} else {
			if (names.length != params.length)
				return false;
			for (int i = 0; i < params.length; i++) {
				if (!isAssignableFromObject(types[i], params[i]))
					return false;
			}
		}
		return true;
	}
	public Object[] castArguments(Object params[]) {
		if (params.length == 0)
			return params;
		params = params.clone();
		if (isVarArg) {
			if (names.length - 1 > params.length)
				return null;
			for (int i = 0; i < params.length; i++) {
				Class<?> clazz = types[Math.min(i, types.length - 1)];
				Object obj = params[i];
				if (obj != null) {
					Object val = OH.cast(obj, clazz);
					if (val == null)
						return null;
					params[i] = val;
				}
			}
		} else {
			if (names.length != params.length)
				return null;
			for (int i = 0; i < params.length; i++) {
				Class<?> clazz = types[i];
				Object obj = params[i];
				if (obj != null) {
					Object val = OH.cast(obj, clazz);
					if (val == null)
						return null;
					params[i] = val;
				}
			}
		}
		return params;
	}

	private static boolean isAssignableFromObject(Class<?> class1, Object obj) {
		return obj == null || OH.isAssignableFrom(class1, obj.getClass());
	}
	private static boolean isAssignableFrom(Class<?> class1, DerivedCellCalculator dcc) {
		return OH.isAssignableFrom(class1, dcc.getReturnType()) || (dcc.isConst() && dcc.getReturnType() == Object.class && dcc.get(null) == null);
	}
	public String getMethodName() {
		return methodName;
	}
	public int getParamsCount() {
		return names.length;
	}

	public String getParamName(int position) {
		return names[position];
	}
	public Class<?> getParamType(int position) {
		return types[position];
	}

	public boolean isVarArg() {
		return isVarArg;
	}

	public String getDescriptionHtml() {
		return this.description;
	}

	public ParamsDefinition addDesc(String description) {
		this.description += description;
		return this;
	}
	public ParamsDefinition addRetDesc(String description) {
		this.returnDescription = description;
		return this;
	}

	public ParamsDefinition addParamDesc(int start, String... string) {
		for (int i = 0; i < string.length; i++)
			this.paramDefinitions[start + i] = string[i];
		return this;
	}

	public String getParamDescriptionHtml(int i) {
		return this.paramDefinitions[i];
	}

	public String getReturnDescriptionHtml() {
		return this.returnDescription;
	}

	public void addAdvancedExample(String script) {
		this.advancedExamples = AH.append(this.advancedExamples, new MethodExample(script));
	}
	public void addAdvancedExample(String script, String[] returns) {
		this.advancedExamples = AH.append(this.advancedExamples, new MethodExample(script, returns));
	}
	public void addAdvancedExample(String script, String[] returns, String description) {
		this.advancedExamples = AH.append(this.advancedExamples, new MethodExample(script, returns, description));
	}
	public MethodExample[] getAdvancedExamples() {
		return advancedExamples;
	}

	public void addExample() {
		this.examples = AH.append(this.examples, OH.EMPTY_OBJECT_ARRAY);
	}
	public void addExample(Object p0) {
		this.examples = AH.append(this.examples, new Object[] { p0 });
	}
	public void addExample(Object p0, Object p1) {
		this.examples = AH.append(this.examples, new Object[] { p0, p1 });
	}
	public void addExample(Object p0, Object p1, Object p2) {
		this.examples = AH.append(this.examples, new Object[] { p0, p1, p2 });
	}
	public void addExample(Object p0, Object p1, Object p2, Object p3) {
		this.examples = AH.append(this.examples, new Object[] { p0, p1, p2, p3 });
	}
	public void addExample(Object p0, Object p1, Object p2, Object p3, Object p4) {
		this.examples = AH.append(this.examples, new Object[] { p0, p1, p2, p3, p4 });
	}
	public void addExample(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
		this.examples = AH.append(this.examples, new Object[] { p0, p1, p2, p3, p4, p5 });
	}
	public void addExample(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
		this.examples = AH.append(this.examples, new Object[] { p0, p1, p2, p3, p4, p5, p6 });
	}
	public void addExample(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
		this.examples = AH.append(this.examples, new Object[] { p0, p1, p2, p3, p4, p5, p6, p7 });
	}
	public void addExample(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
		this.examples = AH.append(this.examples, new Object[] { p0, p1, p2, p3, p4, p5, p6, p7, p8 });
	}
	public void addExample(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
		this.examples = AH.append(this.examples, new Object[] { p0, p1, p2, p3, p4, p5, p6, p7, p8, p9 });
	}

	public Object[][] getExamples() {
		return this.examples;
	}

	public Class<?>[] getParamTypes() {
		return types;
	}
	public com.f1.base.CalcTypes getParamTypesMapping() {
		return this.paramTypesMapping;
	}

	public Class getReturnType() {
		return this.returnType;
	}
	public String[] getParamDescriptions() {
		return this.names;
	}
	public String[] getParamNames() {
		return this.paramDefinitions;
	}

	public byte getModifiers() {
		return this.modifiers;
	}
	public boolean isVirtual() {
		return MH.anyBits(this.modifiers, MODIFIER_VIRTUAL);
	}
	public boolean equalsDef(ParamsDefinition def) {
		return this.isVarArg == def.isVarArg //
				&& OH.eq(this.methodName, def.methodName) //
				&& OH.eq(this.returnType, def.returnType)//
				&& Arrays.equals(this.types, def.types);
	}
	public boolean equalsDefIgnoreReturnType(ParamsDefinition def) {
		return this.isVarArg == def.isVarArg //
				&& OH.eq(this.methodName, def.methodName) //
				&& Arrays.equals(this.types, def.types);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != ParamsDefinition.class)
			return false;
		if (obj == this)
			return true;
		ParamsDefinition o = (ParamsDefinition) obj;
		return equalsDef(o) && this.modifiers == o.modifiers && OH.eq(names, o.names);
	}
	public int hashCodeDef() {
		return this.hashcodeDef;
	}
	public int hashCodeDefIngoreReturnType() {
		return this.hashcodeDefIgnoreReturnType;
	}
	@Override
	public int hashCode() {
		return this.hashcode;
	}
	public CalcFrame mapToInputs(Map<String, Object> variables, boolean throwOnError) {
		if (names.length == 0)
			return EmptyCalcFrame.INSTANCE;
		CalcFrame r = new BasicCalcFrame(this.getParamTypesMapping());
		for (int i = 0; i < names.length; i++) {
			String name = this.names[i];
			r.putValue(name, this.casters[i].cast(variables.get(name), false, throwOnError));
		}
		return r;
	}

	public final static Hasher<ParamsDefinition> HASHER_DEF_IGNORE_RETURNTYPE = new Hasher<ParamsDefinition>() {

		@Override
		public int hashcode(ParamsDefinition o) {
			return o.hashCodeDefIngoreReturnType();
		}

		@Override
		public boolean areEqual(ParamsDefinition l, ParamsDefinition r) {
			return l.equalsDefIgnoreReturnType(r);
		}
	};
	public static Hasher<ParamsDefinition> HASHER_DEF = new Hasher<ParamsDefinition>() {

		@Override
		public int hashcode(ParamsDefinition o) {
			return o.hashCodeDef();
		}

		@Override
		public boolean areEqual(ParamsDefinition l, ParamsDefinition r) {
			return l.equalsDef(r);
		}
	};

	public Caster<?> getParamCaster(int i) {
		return this.casters[i];
	}
}
