package com.f1.utils.bytecode;

public interface ByteCodeConst {

	public int getIndex();

	public byte getConstType();

	void bind(ByteCodeClass owner);

	public StringBuilder toJavaString(StringBuilder sb);

}
