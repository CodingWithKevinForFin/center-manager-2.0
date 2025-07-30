package com.f1.ami.center.table.index;

import com.f1.ami.center.table.AmiRowImpl;

public interface AmiQueryScanner {

	public boolean matches(AmiRowImpl row);
}
