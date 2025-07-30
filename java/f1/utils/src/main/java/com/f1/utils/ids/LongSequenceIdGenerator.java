package com.f1.utils.ids;


public interface LongSequenceIdGenerator extends IdGenerator<Long> {
	public long createNextLongId();
	public long createNextLongIds(int count);
}
