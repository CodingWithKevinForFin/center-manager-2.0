package com.f1.utils.structs.table.derived;

import java.util.Map;

import com.f1.utils.structs.table.stack.CalcFrameStack;

public interface ClassDebugInspector<T> {

	Class<T> getVarType();
	String getVarTypeName();
	Object getDebugProperty(String name, T value, CalcFrameStack sf);
	Map<String, Class<?>> getDebugProperties();

}
