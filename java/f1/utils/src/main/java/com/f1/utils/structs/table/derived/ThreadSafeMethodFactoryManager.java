package com.f1.utils.structs.table.derived;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.f1.base.Caster;
import com.f1.utils.structs.table.stack.CalcTypesStack;

public class ThreadSafeMethodFactoryManager extends BasicMethodFactory {

	public ThreadSafeMethodFactoryManager() {
	}

	synchronized public void getAllMethodFactories(List<MethodFactory> sink) {
		super.getAllMethodFactories(sink);
	}

	synchronized public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, CalcTypesStack context) {
		return super.toMethod(position, methodName, calcs, context);
	}

	synchronized public <T> DerivedCellMemberMethod<T> findMemberMethod(Class<? extends T> targetType, String methodName, Class<?>[] arguments) {
		return super.findMemberMethod(targetType, methodName, arguments);
	}

	synchronized public <T> void getMemberMethods(Class<? extends T> targetType, String methodName, List<DerivedCellMemberMethod<T>> sink) {
		super.getMemberMethods(targetType, methodName, sink);
	}

	//	synchronized public com.f1.base.Types getVariables(int position, String methodName, com.f1.base.Types variables) {
	//		return super.getVariables(position, methodName, variables);
	//	}

	synchronized public Class<?> forName(String vartype) throws ClassNotFoundException {
		return super.forName(vartype);
	}

	synchronized public Class<?> forNameNoThrow(String vartype) {
		return super.forNameNoThrow(vartype);
	}

	synchronized public String forType(Class<?> clazz) {
		return super.forType(clazz);
	}

	synchronized public void getTypes(Set<Class<?>> sink) {
		super.getTypes(sink);
	}

	synchronized public Class<?> getDefaultImplementation(Class<?> vartype) {
		return super.getDefaultImplementation(vartype);
	}

	synchronized public String getVarTypeDescription(String name) {
		return super.getVarTypeDescription(name);
	}

	synchronized public <T> List<ClassDebugInspector<?>> getClassDebugInepectors(Class<T> c) {
		return super.getClassDebugInepectors(c);
	}

	synchronized public MethodFactory getMethodFactory(String name, Class[] args) {
		return super.getMethodFactory(name, args);
	}

	synchronized public Caster getCaster(Class type) {
		return super.getCaster(type);
	}

	@Override
	synchronized public Class findType(Class clazz) {
		return super.findType(clazz);
	}

	@Override
	synchronized public void addCaster(Caster<?> c) {
		super.addCaster(c);
	}

	@Override
	synchronized public void getMethodFactories(Collection<MethodFactory> sink) {
		super.getMethodFactories(sink);
	}
	synchronized public void removeFactoryManager(MethodFactoryManager mf) {
		super.removeFactoryManager(mf);
	}
	synchronized public void addFactoryManager(MethodFactoryManager mf) {
		super.addFactoryManager(mf);
	}
	synchronized public void clearFactoryManagers() {
		super.clearFactoryManagers();
	}
	synchronized public <T> void addMemberMethod(DerivedCellMemberMethod<T> method) {
		super.addMemberMethod(method);
	}
	synchronized public void addVarType(String name, Class<?> type) {
		super.addVarType(name, type);
	}
	synchronized public void addVarTypeDescription(String name, String description) {
		super.addVarTypeDescription(name, description);
	}
	synchronized public void addVarType(String name, Class<?> type, Class<?> dfltImpl) {
		super.addVarType(name, type, dfltImpl);
	}
	@Override
	synchronized public void addFactory(MethodFactory factory) {
		super.addFactory(factory);
	}

	@Override
	synchronized public void removeFactory(MethodFactory factory) {
		super.removeFactory(factory);
	}

}
