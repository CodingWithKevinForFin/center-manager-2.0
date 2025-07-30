package com.f1.ami.extern;

import com.f1.utils.structs.table.derived.Extern;
import com.f1.utils.structs.table.derived.ExternCompiled;
import com.f1.utils.structs.table.stack.CalcTypesStack;

public class PythonExtern implements Extern {

	@Override
	public ExternCompiled compile(CalcTypesStack context, int codePosition, String code) {
		return new PythonExternCompiled(context, code);
	}

}
