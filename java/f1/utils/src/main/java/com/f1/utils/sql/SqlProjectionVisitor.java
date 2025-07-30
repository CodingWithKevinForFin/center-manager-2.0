package com.f1.utils.sql;

import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.TablesCalcFrame;

public interface SqlProjectionVisitor {

	void visit(TablesCalcFrame tg, int firstTablePos, ReusableCalcFrameStack sf);

	void trimTable(int limitOffset, int limit);

}
