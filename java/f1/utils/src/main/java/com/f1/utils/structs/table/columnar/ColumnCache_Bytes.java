package com.f1.utils.structs.table.columnar;

import java.io.File;
import java.io.IOException;

import com.f1.base.Bytes;

public class ColumnCache_Bytes extends ColumnCache<Bytes> {

	public ColumnCache_Bytes(File data, long maxCacheSize) throws IOException {
		super(data, maxCacheSize);
	}

	@Override
	protected byte[] valueFrom(Bytes value) {
		return value.getBytes();
	}

	@Override
	protected Bytes valueOf(byte[] r) {
		return Bytes.valueOf(r);
	}

}
