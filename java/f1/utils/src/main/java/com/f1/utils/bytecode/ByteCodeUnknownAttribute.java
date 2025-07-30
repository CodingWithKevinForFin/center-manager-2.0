package com.f1.utils.bytecode;

import com.f1.utils.SH;

public class ByteCodeUnknownAttribute implements ByteCodeAttribute {

	private ByteCodeConstUtf name;
	private byte data[];

	public ByteCodeUnknownAttribute(ByteCodeConstUtf name, byte data[]) {
		this.name = name;
		this.data = data;
	}

	public ByteCodeConstUtf getName() {
		return name;
	}

	public void setName(ByteCodeConstUtf name) {
		this.name = name;
	}

	public byte[] getData() {
		return data;
	}
	public void setData(byte data[]) {
		this.data = data;
	}

	@Override
	public void toJavaString(String indent, StringBuilder sb) {
		sb.append(indent).append("// unknown attribute: ").append(getName().getUtf()).append(" ==> ").append(getData().length).append(" byte(s)").append(SH.NEWLINE);

	}
}
