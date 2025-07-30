package com.f1.utils.bytecode;

public class ByteCodeConstUtf implements ByteCodeConstValued {

	private final String utf;
	private int index;

	@Override
	public byte getConstType() {
		return ByteCodeConstants.CONSTANT_UTF8;
	}

	public ByteCodeConstUtf(int index, String utf) {
		this.index = index;
		this.utf = utf;
	}

	@Override
	public void bind(ByteCodeClass owner) {
	}

	public String getUtf() {
		return utf;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public StringBuilder toJavaString(StringBuilder sb) {
		return sb.append(utf);
	}

	@Override
	public Object getValue() {
		return utf;
	}

}
