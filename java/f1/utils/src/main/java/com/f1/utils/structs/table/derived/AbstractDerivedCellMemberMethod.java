package com.f1.utils.structs.table.derived;

import com.f1.base.ToStringable;
import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public abstract class AbstractDerivedCellMemberMethod<T> implements DerivedCellMemberMethod<T>, ToStringable {

	private static final String[] CACHED_PARAM_NAMES = new String[] { "p1", "p2", "p3", "p4", "p5", "p6", "p7", "p8", "p9", "p10" };
	final private Class<T> targetType;
	final private String methodName;
	final private Class<?> returnType;
	final private Class[] paramTypes;
	final private Class<?> varArgType;
	private String[] paramNames;
	private ParamsDefinition params;

	public AbstractDerivedCellMemberMethod(Class<T> targetType, String methodName, Class<?> returnType, Class... paramTypes) {
		OH.assertNotNull(returnType);
		this.targetType = targetType;
		this.methodName = methodName;
		this.returnType = returnType;
		this.paramTypes = paramTypes;
		this.varArgType = null;
		this.params = new ParamsDefinition(methodName, returnType, paramNames, paramTypes, false, (byte) 0);
	}
	public AbstractDerivedCellMemberMethod(Class<T> targetType, String methodName, Class<?> returnType, boolean isVarArg, Class... paramTypes) {
		OH.assertNotNull(returnType);
		this.targetType = targetType;
		this.methodName = methodName;
		this.returnType = returnType;
		if (isVarArg) {
			this.paramTypes = AH.subarray(paramTypes, 0, paramTypes.length - 1);
			this.varArgType = paramTypes[paramTypes.length - 1];
			paramNames = new String[this.paramTypes.length + 1];
		} else {
			this.paramTypes = paramTypes;
			paramNames = new String[this.paramTypes.length];
			this.varArgType = null;
		}
		for (int i = 0; i < this.paramNames.length; i++)
			paramNames[i] = getParamName(i);
		this.params = new ParamsDefinition(methodName, returnType, paramNames, paramTypes, isVarArg, (byte) 0);
	}

	private static String getParamName(int i) {
		if (i < CACHED_PARAM_NAMES.length)
			return CACHED_PARAM_NAMES[i];
		return "p" + (i + 1);
	}

	@Override
	abstract public Object invokeMethod(CalcFrameStack sf, T targetObject, Object[] params, DerivedCellCalculator caller);

	@Override
	public Class<?> getReturnType() {
		return returnType;
	}

	@Override
	public Class<T> getTargetType() {
		return targetType;
	}

	@Override
	public Class[] getParamTypes() {
		return paramTypes;
	}

	@Override
	public String getMethodName() {
		return methodName;
	}

	@Override
	public Class<?> getVarArgType() {
		return varArgType;
	}
	@Override
	final public String toString() {
		return toString(new StringBuilder()).toString();
	}

	final public StringBuilder toString(StringBuilder sb) {
		return toString(sb, null);
	}

	@Override
	public String[] getParamNames() {
		return paramNames;
	}

	@Override
	public StringBuilder toString(StringBuilder sb, MethodFactoryManager mFactory) {
		if (methodName == null) {
			sb.append("new ");
			getSimpleName(this.targetType, mFactory, sb);
			sb.append('(');

		} else {
			getSimpleName(getReturnType(), mFactory, sb);
			sb.append(' ');
			getSimpleName(targetType, mFactory, sb);
			sb.append('.').append(methodName).append('(');
		}
		for (int i = 0; i < paramTypes.length; i++) {
			if (i > 0)
				sb.append(", ");
			getSimpleName(paramTypes[i], mFactory, sb);
		}
		if (varArgType != null) {
			if (paramTypes.length > 0)
				sb.append(", ");
			getSimpleName(varArgType, mFactory, sb);
			sb.append("...");
		}
		sb.append(")");
		return sb;
	}
	private void getSimpleName(Class<?> clz, MethodFactoryManager mFactory, StringBuilder sb) {
		if (mFactory == null)
			sb.append(OH.getSimpleName(clz));
		else {
			String r = mFactory.forType(clz);
			sb.append(SH.isnt(r) ? OH.getSimpleName(clz) : r);
		}
	}

	@Override
	public ParamsDefinition getParamsDefinition() {
		return this.params;
	}
}
