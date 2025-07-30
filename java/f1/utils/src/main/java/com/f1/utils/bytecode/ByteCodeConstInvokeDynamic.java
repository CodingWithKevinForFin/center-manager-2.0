package com.f1.utils.bytecode;

public class ByteCodeConstInvokeDynamic implements ByteCodeConst {

	final private int index;
	final private int referenceKind;
	final private int referencedIndex;

	public ByteCodeConstInvokeDynamic(int index, int referenceKind, int referencedIndex) {
		this.index = index;
		this.referenceKind = referenceKind;
		this.referencedIndex = referencedIndex;
	}

	@Override
	public void bind(ByteCodeClass owner) {
	}

	@Override
	public byte getConstType() {
		return ByteCodeConstants.CONSTANT_INVOKE_DYNAMIC;
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
