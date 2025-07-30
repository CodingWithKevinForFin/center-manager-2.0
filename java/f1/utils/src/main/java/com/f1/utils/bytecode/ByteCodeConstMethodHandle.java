package com.f1.utils.bytecode;

public class ByteCodeConstMethodHandle implements ByteCodeConst {

	final private int index;
	final private int bootstrapMethodAttrIndex;
	private int nameAndTypeIndex;

	public ByteCodeConstMethodHandle(int index, int bootstrapMethodAttrIndex, int nameAndTypeIndex) {
		this.index = index;
		this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}

	@Override
	public void bind(ByteCodeClass owner) {
	}

	@Override
	public byte getConstType() {
		return ByteCodeConstants.CONSTANT_METHOD_HANDLE;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public StringBuilder toJavaString(StringBuilder sb) {
		return sb;
	}

}
