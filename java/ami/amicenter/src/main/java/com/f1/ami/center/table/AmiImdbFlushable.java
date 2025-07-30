package com.f1.ami.center.table;

import com.f1.utils.structs.table.stack.CalcFrameStack;

public interface AmiImdbFlushable {

	void flushPersister(CalcFrameStack sf);
	String getFlushableName();

}
