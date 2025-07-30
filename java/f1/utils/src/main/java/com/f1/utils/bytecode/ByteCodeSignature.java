package com.f1.utils.bytecode;

import java.io.DataInput;
import java.io.IOException;

import com.f1.utils.SH;

public class ByteCodeSignature {

	private ByteCodeConstUtf text;

	public ByteCodeSignature(DataInput dis, ByteCodeClass owner) throws IOException {
		this.text = owner.getConstUtf(dis.readUnsignedShort());
	}

	public void toJavaString(String indent, StringBuilder sb) {
		sb.append("//Signature: ").append(text.getUtf()).append(SH.NEWLINE);
	}

}
