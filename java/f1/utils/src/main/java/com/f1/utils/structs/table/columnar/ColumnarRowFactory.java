package com.f1.utils.structs.table.columnar;

public interface ColumnarRowFactory {

	ColumnarRow newColumnarRow(ColumnarTable columnarTable, int i, int index);

}
