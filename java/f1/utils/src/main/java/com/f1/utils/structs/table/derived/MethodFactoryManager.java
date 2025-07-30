package com.f1.utils.structs.table.derived;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.f1.base.Caster;
import com.f1.utils.structs.table.stack.CalcTypesStack;

public interface MethodFactoryManager {

	//This recursivly gets all method factories.. If this method factory manager contains other method factory managers those are included
	void getAllMethodFactories(List<MethodFactory> sink);

	DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, CalcTypesStack context);

	<T> DerivedCellMemberMethod<T> findMemberMethod(Class<? extends T> targetType, String methodName, Class<?>[] arguments);
	<T> void getMemberMethods(Class<? extends T> targetType, String methodName, List<DerivedCellMemberMethod<T>> sink);

	Class<?> forName(String vartype) throws ClassNotFoundException;
	Class<?> forNameNoThrow(String vartype);
	String forType(Class<?> clazz);
	void getTypes(Set<Class<?>> sink);
	Class<?> getDefaultImplementation(Class<?> vartype);

	String getVarTypeDescription(String name);

	<T> List<ClassDebugInspector<?>> getClassDebugInepectors(Class<T> c);

	MethodFactory getMethodFactory(String name, Class[] args);

	Caster getCaster(Class type);

	Class findType(Class clazz);

	//Only returns the method factories owned by this method factory manager (not inner method factory managers)
	void getMethodFactories(Collection<MethodFactory> sink);

	MethodFactoryManager getFactoryForVirtuals();

}
