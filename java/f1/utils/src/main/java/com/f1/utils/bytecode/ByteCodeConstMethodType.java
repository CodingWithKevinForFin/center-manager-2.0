package com.f1.utils.bytecode;

public class ByteCodeConstMethodType implements ByteCodeConstValued {

	final private int index;
	final private int descriptorIndex;
	private ByteCodeConstUtf descriptor;
	private String descriptorText;

	public ByteCodeConstMethodType(int index, int nameIndex) {
		this.index = index;
		this.descriptorIndex = nameIndex;
	}

	public int getDescriptorIndex() {
		return descriptorIndex;
	}
	@Override
	public void bind(ByteCodeClass owner) {
		if (descriptor == null) {
			descriptor = owner.getConstUtf(descriptorIndex);
			descriptor.bind(owner);
			descriptorText = descriptor.getUtf();
		}
	}

	@Override
	public byte getConstType() {
		return ByteCodeConstants.CONSTANT_METHOD_TYPE;
	}

	@Override
	public int getIndex() {
		return index;
	}

	public String getClassNameText() {
		return descriptorText;
	}

	@Override
	public StringBuilder toJavaString(StringBuilder sb) {
		return sb.append(descriptorText);
	}

	@Override
	public Object getValue() {
		return descriptorText;
	}

}
