package com.f1.base;

public interface CalcTypes {
	Class<?> getType(String var);//returns null if does not contain
	boolean isVarsEmpty();
	Iterable<String> getVarKeys();
	int getVarsCount();
}
