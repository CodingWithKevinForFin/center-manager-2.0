package com.f1.utils.bytecode;

public class ByteCodeConstFloat implements ByteCodeConstValued {

	final private float value;
	final private int index;

	public ByteCodeConstFloat(int index, float value) {
		this.value = value;
		this.index = index;
	}

	@Override
	public byte getConstType() {
		return ByteCodeConstants.CONSTANT_FLOAT;
	}

	@Override
	public void bind(ByteCodeClass owner) {
	}

	@Override
	public Float getValue() {
		return value;
	}

	public float getLongValue() {
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
