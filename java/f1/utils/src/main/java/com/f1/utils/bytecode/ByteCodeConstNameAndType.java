package com.f1.utils.bytecode;

public class ByteCodeConstNameAndType implements ByteCodeConst {

	final private int nameIndex;
	final private int descriptorIndex;
	final private int index;
	private ByteCodeConstUtf name;
	private ByteCodeConstUtf descriptor;
	private String descriptorText;

	public ByteCodeConstNameAndType(int index, int nameIndex, int descriptorIndex) {
		this.index = index;
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
	}

	public int getNameIndex() {
		return nameIndex;
	}

	public int getDesciptorIndex() {
		return descriptorIndex;
	}

	@Override
	public void bind(ByteCodeClass owner) {
		if (name == null) {
			name = owner.getConstUtf(nameIndex);
			descriptor = owner.getConstUtf(descriptorIndex);
		}
	}

	@Override
	public byte getConstType() {
		return ByteCodeConstants.CONSTANT_NAME_AND_TYPE;
	}

	@Override
	public int getIndex() {
		return index;
	}
	@Override
	public StringBuilder toJavaString(StringBuilder sb) {
		descriptor.toJavaString(sb);
		sb.append(' ');
		return name.toJavaString(sb);
	}

	public ByteCodeConstUtf getDesciptor() {
		return descriptor;
	}

	public ByteCodeConstUtf getName() {
		return name;
	}
	public String getDesciptorText() {
		return descriptor.getUtf();
	}

	public String getNameText() {
		return name.getUtf();
	}

}
