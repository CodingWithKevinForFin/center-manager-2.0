/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs.table;

import com.f1.base.Row;
import com.f1.utils.LocalToolkit;

public interface RowFilter {

	boolean shouldKeep(Row row, LocalToolkit localToolkit);

}
