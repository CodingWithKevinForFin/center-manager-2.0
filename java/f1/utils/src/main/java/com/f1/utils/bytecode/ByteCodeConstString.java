package com.f1.utils.bytecode;

public class ByteCodeConstString implements ByteCodeConstValued {

	final private int utfIndex;
	private ByteCodeConstUtf value;
	final private int index;

	public ByteCodeConstString(int index, int utfIndex) {
		this.utfIndex = utfIndex;
		this.index = index;
	}

	@Override
	public byte getConstType() {
		return ByteCodeConstants.CONSTANT_STRING;
	}

	@Override
	public void bind(ByteCodeClass owner) {
		value = owner.getConstUtf(utfIndex);
	}

	@Override
	public String getValue() {
		return value.getUtf();
	}

	public String getStringValue() {
		return value.getUtf();
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public StringBuilder toJavaString(StringBuilder sb) {
		sb.append('"');
		value.toJavaString(sb);
		return sb.append('"');
	}

}
