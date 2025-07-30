package com.f1.utils.bytecode;

public class ByteCodeConstDouble implements ByteCodeConstValued {

	final private double value;
	final private int index;

	public ByteCodeConstDouble(int index, double value) {
		this.value = value;
		this.index = index;
	}

	@Override
	public byte getConstType() {
		return ByteCodeConstants.CONSTANT_DOUBLE;
	}

	@Override
	public void bind(ByteCodeClass owner) {
	}

	@Override
	public Double getValue() {
		return value;
	}

	public double getLongValue() {
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
