package com.f1.utils.structs.table.derived;

import java.util.Set;

import com.f1.utils.structs.table.stack.CalcFrameStack;

public interface ExternCompiled {

	public Object execute(CalcFrameStack lcvs);
	public void getDependencies(Set<String> ids);
	public Class getReturnType();
}
