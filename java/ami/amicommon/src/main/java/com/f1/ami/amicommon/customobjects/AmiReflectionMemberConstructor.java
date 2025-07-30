package com.f1.ami.amicommon.customobjects;

import java.lang.reflect.Constructor;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.base.CalcFrame;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiReflectionMemberConstructor<T> extends AmiAbstractMemberMethod<T> {

	final private String[] paramNames;
	final private String help;
	final private Constructor constructor;
	final private boolean isReadonly;

	public AmiReflectionMemberConstructor(Constructor constructor, String methodName, String[] paramNames, String help, boolean isReadonly) {
		super((Class) constructor.getDeclaringClass(), methodName, constructor.getDeclaringClass(), constructor.isVarArgs(),
				AmiReflectionMemberMethod.getBoxed(constructor.getParameterTypes()));
		this.paramNames = paramNames;
		this.isReadonly = isReadonly;
		this.help = help;
		this.constructor = constructor;
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
			return constructor.newInstance(params);
		} catch (Exception e) {
			throw new RuntimeException("Error calling method " + getMethodName(), e);
		}
	}
	@Override
	public boolean isReadOnly() {
		return this.isReadonly;
	}

}
