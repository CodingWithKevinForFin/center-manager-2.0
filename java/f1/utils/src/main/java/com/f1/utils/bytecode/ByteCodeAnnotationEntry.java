package com.f1.utils.bytecode;

import com.f1.utils.SH;

public class ByteCodeAnnotationEntry {
	final private Object name;
	final private Object value;
	final private char type;

	public ByteCodeAnnotationEntry(Object ename, char etype, Object eval) {
		this.name = ename;
		this.value = eval;
		this.type = etype;
	}

	public Object getName() {
		return name;
	}

	public char getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public StringBuilder toJavaString(String indent, StringBuilder sb) {
		sb.append(name).append('=');
		if (type == '[')
			SH.join(',', (Object[]) value, sb.append('[')).append(']');
		else
			sb.append(value);
		return sb;
	}

}
