package com.f1.ami.center.table;

import com.f1.utils.structs.table.columnar.ColumnarRow;

public interface PreparedRowCopier {

	void copy(ColumnarRow srcRow, AmiPreparedRow tgtRow);
}
