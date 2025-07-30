package com.f1.utils.bytecode;

import java.io.DataInput;
import java.io.IOException;

import com.f1.utils.SH;

public class ByteCodeField extends ByteCodeAttributable {

	private ByteCodeConstValued constValue;
	private ByteCodeConstUtf descriptor;
	private String typeText;

	public ByteCodeField() {
	}

	public ByteCodeConstUtf getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(ByteCodeConstUtf descriptor) {
		this.descriptor = descriptor;
		this.typeText = ByteCodeUtils.parseClassDescriptor(this.descriptor.getUtf());
	}

	public Object getConstValue() {
		return constValue == null ? null : constValue.getValue();
	}

	public String getTypeText() {
		return typeText;
	}
	public String getDescriptorText() {
		return descriptor.getUtf();
	}

	public void setTypeText(String typeText) {
		this.typeText = typeText;
	}

	public StringBuilder toJavaString(String indent, StringBuilder sb) {
		super.toJavaString(indent, sb);
		sb.append(indent);
		ByteCodeUtils.modifiersMaskToString(getAccessFlags(), sb, false);
		sb.append(' ');
		sb.append(typeText);
		sb.append(' ');
		sb.append(getName().getUtf());
		if (this.constValue != null)
			sb.append(" = ").append(this.constValue.getValue());
		sb.append(';');
		sb.append(SH.NEWLINE);
		return sb;
	}

	@Override
	public void addAttribute(ByteCodeConstUtf key, DataInput dis, int length) throws IOException {
		if (key.getUtf().equals(ByteCodeConstants.ATTR_CONSTANT_VALUE)) {
			this.constValue = getOwner().getConstValued(dis.readUnsignedShort());
		} else
			super.addAttribute(key, dis, length);
	}
}
