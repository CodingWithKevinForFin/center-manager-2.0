package com.f1.utils.bytecode;

import java.util.List;

import com.f1.utils.SH;

public class ByteCodeConstMethodRef implements ByteCodeConstValued {

	final private int index;
	final private int classIndex;
	final private int nameAndTypeIndex;
	private ByteCodeConstClassRef clazz;
	private ByteCodeConstNameAndType nameAndType;
	private String nameText;
	private List<String> argumentsText;
	private String returnTypeText;

	public ByteCodeConstMethodRef(int index, int classIndex, int nameAndTypeIndex) {
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
			List<String> arguments = ByteCodeUtils.parseArguments(nameAndType.getDesciptorText());
			this.argumentsText = arguments.subList(0, arguments.size() - 1);
			this.returnTypeText = arguments.get(arguments.size() - 1);
		}
	}

	@Override
	public byte getConstType() {
		return ByteCodeConstants.CONSTANT_INTERFACE_METHOD_REF;
	}

	@Override
	public int getIndex() {
		return index;
	}

	public ByteCodeConstClassRef getClassRef() {
		return clazz;
	}
	public ByteCodeConstNameAndType getNameAndType() {
		return nameAndType;
	}

	@Override
	public StringBuilder toJavaString(StringBuilder sb) {
		sb.append(returnTypeText);
		sb.append(' ');
		clazz.toJavaString(sb);
		if (!nameText.equals("<init>")) {
			sb.append('.');
			sb.append(nameText);
		}
		sb.append('(');
		SH.join(',', argumentsText, sb);
		return sb.append(')');
	}

	@Override
	public Object getValue() {
		return "TODO:DONT KNOW WHAT A METHOD REFERENCE VALUE SHOULD RETURN!";
	}

}
