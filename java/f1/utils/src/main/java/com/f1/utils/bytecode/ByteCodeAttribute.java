package com.f1.utils.bytecode;

public interface ByteCodeAttribute {

	public ByteCodeConstUtf getName();

	public byte[] getData();

	public void toJavaString(String indent, StringBuilder sb);

}
