package com.f1.ami.center.dbo;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellMemberMethod;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiDboMethodWrapper implements DerivedCellMemberMethod {

	private DerivedCellMemberMethod inner;
	private int methodIndex;

	public AmiDboMethodWrapper(DerivedCellMemberMethod inner, int methodIndex) {
		this.inner = inner;
		this.methodIndex = methodIndex;
	}

	public int getMethodIndex() {
		return this.methodIndex;
	}

	public Object invokeMethod(CalcFrameStack lcvs, Object targetObject, Object[] params, DerivedCellCalculator caller) {
		AmiDbo dbo = (AmiDbo) targetObject;
		AmiDboBindingImpl binding = (AmiDboBindingImpl) dbo.getAmiDboBinding();
		if (!binding.isRunning())
			return null;
		binding.lock();
		long start = System.nanoTime();
		try {
			Object o = inner.invokeMethod(lcvs, targetObject, params, caller);
			binding.onMethodStat(this, System.nanoTime() - start);
			return o;
		} catch (Throwable t) {
			binding.onMethodErrorStat(this, t);
			throw t;
		} finally {
			binding.unlock();
		}
	}

	public Class getReturnType() {
		return inner.getReturnType();
	}

	public Class getTargetType() {
		return inner.getTargetType();
	}

	public Class[] getParamTypes() {
		return inner.getParamTypes();
	}

	public String getMethodName() {
		return inner.getMethodName();
	}

	public Class getVarArgType() {
		return inner.getVarArgType();
	}

	public StringBuilder toString(StringBuilder sb, MethodFactoryManager mFactory) {
		return inner.toString(sb, mFactory);
	}

	public String[] getParamNames() {
		return inner.getParamNames();
	}

	public ParamsDefinition getParamsDefinition() {
		return inner.getParamsDefinition();
	}

	public StringBuilder toString(StringBuilder sink) {
		return inner.toString(sink);
	}

	public boolean isReadOnly() {
		return inner.isReadOnly();
	}

	public Object resumeMethod(CalcFrameStack lcvs, Object target, Object[] params, PauseStack paused, FlowControlPause fp, DerivedCellCalculator caller) {
		throw new UnsupportedOperationException();
	}

	public boolean isPausable() {
		return inner.isPausable();
	}

}
