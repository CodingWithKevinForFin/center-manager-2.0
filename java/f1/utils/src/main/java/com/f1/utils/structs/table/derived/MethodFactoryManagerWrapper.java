package com.f1.utils.structs.table.derived;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.f1.base.Caster;
import com.f1.utils.structs.table.stack.CalcTypesStack;

public class MethodFactoryManagerWrapper implements MethodFactoryManager {

	private final MethodFactoryManager inner;

	public MethodFactoryManagerWrapper(MethodFactoryManager inner) {
		this.inner = inner;
	}

	public void getAllMethodFactories(List<MethodFactory> sink) {
		inner.getAllMethodFactories(sink);
	}

	public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, CalcTypesStack context) {
		return inner.toMethod(position, methodName, calcs, context);
	}

	public <T> DerivedCellMemberMethod<T> findMemberMethod(Class<? extends T> targetType, String methodName, Class<?>[] arguments) {
		return inner.findMemberMethod(targetType, methodName, arguments);
	}

	public <T> void getMemberMethods(Class<? extends T> targetType, String methodName, List<DerivedCellMemberMethod<T>> sink) {
		inner.getMemberMethods(targetType, methodName, sink);
	}

	public Class<?> forName(String vartype) throws ClassNotFoundException {
		return inner.forName(vartype);
	}

	public Class<?> forNameNoThrow(String vartype) {
		return inner.forNameNoThrow(vartype);
	}

	public String forType(Class<?> clazz) {
		return inner.forType(clazz);
	}

	public void getTypes(Set<Class<?>> sink) {
		inner.getTypes(sink);
	}

	public Class<?> getDefaultImplementation(Class<?> vartype) {
		return inner.getDefaultImplementation(vartype);
	}

	public String getVarTypeDescription(String name) {
		return inner.getVarTypeDescription(name);
	}

	public <T> List<ClassDebugInspector<?>> getClassDebugInepectors(Class<T> c) {
		return inner.getClassDebugInepectors(c);
	}

	public MethodFactory getMethodFactory(String name, Class[] args) {
		return inner.getMethodFactory(name, args);
	}

	public Caster getCaster(Class type) {
		return inner.getCaster(type);
	}

	public Class findType(Class clazz) {
		return inner.findType(clazz);
	}

	public void getMethodFactories(Collection<MethodFactory> sink) {
		inner.getMethodFactories(sink);
	}

	public MethodFactoryManager getFactoryForVirtuals() {
		return inner.getFactoryForVirtuals();
	}

}
