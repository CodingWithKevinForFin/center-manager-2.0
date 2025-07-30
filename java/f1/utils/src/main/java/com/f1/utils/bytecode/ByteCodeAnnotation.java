package com.f1.utils.bytecode;

import com.f1.utils.SH;

public class ByteCodeAnnotation {

	private ByteCodeConstUtf type;
	final private ByteCodeAnnotationEntry[] entries;
	private String annotationTypeText;

	public ByteCodeAnnotation(ByteCodeConstUtf type, ByteCodeAnnotationEntry[] entries) {
		this.type = type;
		this.entries = entries;
		this.annotationTypeText = ByteCodeUtils.parseClassDescriptor(type.getUtf());
	}
	public ByteCodeConstUtf getType() {
		return type;
	}

	public ByteCodeAnnotationEntry[] getEntries() {
		return entries;
	}

	public StringBuilder toJavaString(String indent, StringBuilder sb) {
		sb.append(indent);
		sb.append('@');
		sb.append(annotationTypeText);
		sb.append('(');
		boolean first = true;
		for (ByteCodeAnnotationEntry entry : entries) {
			if (first)
				first = false;
			else
				sb.append(',');
			entry.toJavaString(indent, sb);
		}
		sb.append(')').append(SH.NEWLINE);
		return sb;
	}
}
