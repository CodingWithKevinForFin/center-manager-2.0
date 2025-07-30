package com.f1.utils.structs.table.columnar;

import java.io.File;
import java.io.IOException;

public class ColumnCache_String extends ColumnCache<String> {

	public ColumnCache_String(File data, long maxCacheSize) throws IOException {
		super(data, maxCacheSize);
	}

	@Override
	protected byte[] valueFrom(String value) {
		return value.getBytes();
	}

	@Override
	protected String valueOf(byte[] r) {
		return new String(r);
	}

}
