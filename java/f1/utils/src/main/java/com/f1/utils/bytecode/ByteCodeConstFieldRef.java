package com.f1.utils.bytecode;

public class ByteCodeConstFieldRef implements ByteCodeConstValued {

	final private int index;
	final private int classIndex;
	final private int nameAndTypeIndex;
	private ByteCodeConstNameAndType nameAndType;
	private ByteCodeConstClassRef clazz;
	private String nameText;
	private String descriptorText;

	public ByteCodeConstFieldRef(int index, int classIndex, int nameAndTypeIndex) {
		this.index = index;
		this.classIndex = classIndex;
		this.nameAndTypeIndex = nameAndTypeIndex;
	}

	public int getClassIndex() {
		return classIndex;
	}
	public int getNameAndTypeIndex() {
		return nameAndTypeIndex;
	}

	@Override
	public void bind(ByteCodeClass owner) {
		if (clazz == null) {
			clazz = owner.getClassRef(classIndex);
			clazz.bind(owner);
			nameAndType = owner.getNameAndType(nameAndTypeIndex);
			nameAndType.bind(owner);
			nameText = nameAndType.getNameText();
			descriptorText = ByteCodeUtils.parseClassDescriptor(nameAndType.getDesciptorText());
		}
	}

	@Override
	public byte getConstType() {
		return ByteCodeConstants.CONSTANT_FIELD_REF;
	}

	@Override
	public int getIndex() {
		return index;
	}
	@Override
	public StringBuilder toJavaString(StringBuilder sb) {
		sb.append('(');
		sb.append(descriptorText);
		sb.append(')');
		clazz.toJavaString(sb);
		sb.append('.');
		sb.append(nameText);
		return sb;
	}

	@Override
	public Object getValue() {
		return "TODO:DONT KNOW WHAT A FIELD REFERENCE VALUE SHOULD RETURN!";
	}

}
