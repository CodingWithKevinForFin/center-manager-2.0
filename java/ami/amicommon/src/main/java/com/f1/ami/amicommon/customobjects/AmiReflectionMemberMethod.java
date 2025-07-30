package com.f1.ami.amicommon.customobjects;

import java.lang.reflect.Method;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiReflectionMemberMethod<T> extends AmiAbstractMemberMethod<T> {

	private String[] paramNames;
	private String help;
	private Method method;
	private boolean isReadonly;

	public AmiReflectionMemberMethod(Method method, String methodName, String[] paramNames, String help, boolean isReadonly) {
		super((Class) method.getDeclaringClass(), methodName, OH.getBoxed(method.getReturnType()), method.isVarArgs(), getBoxed(method.getParameterTypes()));
		this.paramNames = paramNames;
		this.isReadonly = isReadonly;
		this.help = help;
		this.method = method;
	}
	public static Class<?>[] getBoxed(Class<?>[] classes) {
		for (int i = 0; i < classes.length; i++)
			classes[i] = OH.getBoxed(classes[i]);
		return classes;

	}
	protected String[] buildParamNames() {
		return null;
	}
	@Override
	protected String getHelp() {
		return help;
	}
	public String[] getParamNames() {
		return this.paramNames;
	}
	public String[] getParamDescriptions() {
		return this.paramNames;
	}

	@Override
	public Object invokeMethod2(CalcFrameStack sf, T targetObject, Object[] params, DerivedCellCalculator caller) {
		try {
			return method.invoke(targetObject, params);
		} catch (Exception e) {
			throw new RuntimeException("Error calling method " + getMethodName(), e);
		}
	}

	@Override
	public boolean isReadOnly() {
		return this.isReadonly;
	}

}
