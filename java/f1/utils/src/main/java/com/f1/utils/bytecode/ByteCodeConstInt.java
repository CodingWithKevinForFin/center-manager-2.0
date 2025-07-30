package com.f1.utils.bytecode;

public class ByteCodeConstInt implements ByteCodeConstValued {

	final private int value;
	final private int index;

	public ByteCodeConstInt(int index, int value) {
		this.value = value;
		this.index = index;
	}

	@Override
	public byte getConstType() {
		return ByteCodeConstants.CONSTANT_INT;
	}

	@Override
	public void bind(ByteCodeClass owner) {
	}

	@Override
	public Integer getValue() {
		return value;
	}

	public int getLongValue() {
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
