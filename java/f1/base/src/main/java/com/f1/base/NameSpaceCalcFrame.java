package com.f1.base;

public interface NameSpaceCalcFrame extends CalcFrame, NameSpaceCalcTypes {

	Object getValue(NameSpaceIdentifier id);
	public Object getAt(int n);
}
