package com.f1.utils.structs.table.derived;

import com.f1.base.ToStringable;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public interface DerivedCellMemberMethod<T> extends ToStringable {

	Object invokeMethod(CalcFrameStack lcvs, T targetObject, Object[] params, DerivedCellCalculator caller);

	Class<?> getReturnType();

	Class<T> getTargetType();

	Class[] getParamTypes();

	String getMethodName();

	Class getVarArgType();

	StringBuilder toString(StringBuilder sb, MethodFactoryManager mFactory);

	String[] getParamNames();

	ParamsDefinition getParamsDefinition();

	boolean isReadOnly();

	Object resumeMethod(CalcFrameStack lcvs, T target, Object[] params, PauseStack paused, FlowControlPause fp, DerivedCellCalculator caller);

	boolean isPausable();
}
