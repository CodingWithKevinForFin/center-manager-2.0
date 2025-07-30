package com.f1.utils.bytecode;

public class ByteCodeConstLong implements ByteCodeConstValued {

	final private long value;
	final private int index;

	public ByteCodeConstLong(int index, long value) {
		this.value = value;
		this.index = index;
	}

	@Override
	public byte getConstType() {
		return ByteCodeConstants.CONSTANT_LONG;
	}

	@Override
	public void bind(ByteCodeClass owner) {
	}

	@Override
	public Long getValue() {
		return value;
	}

	public long getLongValue() {
		return value;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public StringBuilder toJavaString(StringBuilder sb) {
		return sb.append(value);
	}

}
