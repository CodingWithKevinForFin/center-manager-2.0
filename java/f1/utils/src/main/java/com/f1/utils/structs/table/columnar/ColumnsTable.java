package com.f1.utils.structs.table.columnar;

import com.f1.base.Table;

public interface ColumnsTable extends Table {

	int mapRowNumToIndex(int location);

	int mapRowNumToIndex(ColumnarRow location);

}
