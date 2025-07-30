package com.f1.base;

public interface NameSpaceCalcTypes extends CalcTypes {

	Class<?> getType(NameSpaceIdentifier key);//returns null if does not contain
	Class<?> getTypeAt(int n);
	public int getPosition(String key);
	public int getPosition(NameSpaceIdentifier key);
}
