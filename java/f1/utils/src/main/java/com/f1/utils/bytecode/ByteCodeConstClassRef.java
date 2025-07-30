package com.f1.utils.bytecode;

public class ByteCodeConstClassRef implements ByteCodeConstValued {

	final private int index;
	final private int nameIndex;
	private ByteCodeConstUtf name;
	private String classNameText;

	public ByteCodeConstClassRef(int index, int nameIndex) {
		this.index = index;
		this.nameIndex = nameIndex;
	}

	public int getNameIndex() {
		return nameIndex;
	}
	@Override
	public void bind(ByteCodeClass owner) {
		if (name == null) {
			name = owner.getConstUtf(nameIndex);
			name.bind(owner);
			classNameText = ByteCodeUtils.parseClassName(name.getUtf());
		}
	}

	@Override
	public byte getConstType() {
		return ByteCodeConstants.CONSTANT_CLASS_REF;
	}

	@Override
	public int getIndex() {
		return index;
	}

	public String getClassNameText() {
		return classNameText;
	}

	@Override
	public StringBuilder toJavaString(StringBuilder sb) {
		return sb.append(classNameText);
	}

	@Override
	public Object getValue() {
		return classNameText;
	}
}
