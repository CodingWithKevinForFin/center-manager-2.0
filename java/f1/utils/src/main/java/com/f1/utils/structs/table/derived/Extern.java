package com.f1.utils.structs.table.derived;

import com.f1.utils.structs.table.stack.CalcTypesStack;

public interface Extern {

	public ExternCompiled compile(CalcTypesStack variableTypes, int codePosition, String code);

}
