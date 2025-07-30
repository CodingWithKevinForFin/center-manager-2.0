package com.f1.utils.bytecode;

import com.f1.utils.SH;

//based on http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.2
public class ByteCodeClass extends ByteCodeAttributable {

	private int minorVersion;
	private int majorVersion;
	private ByteCodeConst[] consts;
	private ByteCodeConstClassRef thisClass;
	private ByteCodeConstClassRef superClass;
	private ByteCodeConstClassRef[] interfaces;

	private ByteCodeField[] fields;
	private ByteCodeMethod[] methods;

	public ByteCodeClass() {
		super.setOwner(this);
	}

	public ByteCodeConstClassRef getSuperClass() {
		return superClass;
	}

	public void setSuperClass(ByteCodeConstClassRef superClass) {
		this.superClass = superClass;
	}

	public void setThisClass(ByteCodeConstClassRef thisClass) {
		this.thisClass = thisClass;
	}

	public ByteCodeConstClassRef getThisClass() {
		return thisClass;
	}

	public ByteCodeConst getConst(int i) {
		return consts[i - 1];
	}

	public ByteCodeConstUtf getConstUtf(int i) {
		return (ByteCodeConstUtf) getConst(i);
	}
	public ByteCodeConstValued getConstValued(int i) {
		return (ByteCodeConstValued) getConst(i);
	}
	public ByteCodeConstClassRef getClassRef(int i) {
		return (ByteCodeConstClassRef) getConst(i);
	}
	public ByteCodeConstMethodRef getMethodRef(int i) {
		return (ByteCodeConstMethodRef) getConst(i);
	}
	public ByteCodeConstFieldRef getFieldRef(int i) {
		return (ByteCodeConstFieldRef) getConst(i);
	}

	public ByteCodeConstNameAndType getNameAndType(int i) {
		return (ByteCodeConstNameAndType) getConst(i);
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}

	public void setConsts(ByteCodeConst[] consts) {
		this.consts = consts;
	}

	public void setInterfaces(ByteCodeConstClassRef[] interfaces) {
		this.interfaces = interfaces;
	}

	public ByteCodeField[] getFields() {
		return fields;
	}

	public void setFields(ByteCodeField[] fields) {
		this.fields = fields;
	}

	public ByteCodeMethod[] getMethods() {
		return methods;
	}

	public void setMethods(ByteCodeMethod[] methods) {
		this.methods = methods;
	}

	public ByteCodeConstClassRef[] getInterfaces() {
		return interfaces;
	}

	@Override
	public StringBuilder toJavaString(String indent, StringBuilder sb) {
		super.toJavaString(indent, sb);
		sb.append(indent);
		ByteCodeUtils.modifiersMaskToString(getAccessFlags(), sb, true);
		sb.append("class ");
		thisClass.toJavaString(sb);
		if (superClass.getClassNameText().equals(Object.class.getName())) {
			sb.append(" extends ");
			superClass.toJavaString(sb);
		}
		if (this.interfaces.length > 0) {
			sb.append(" implements ");
			boolean first = true;
			for (ByteCodeConstClassRef intrf : interfaces) {
				if (first)
					first = false;
				else
					sb.append(", ");
				intrf.toJavaString(sb);
			}
		}
		sb.append("{");
		sb.append(SH.NEWLINE);
		for (ByteCodeField field : fields) {
			field.toJavaString(indent + "  ", sb);
			sb.append(SH.NEWLINE);
		}
		for (ByteCodeMethod method : methods) {
			method.toJavaString(indent + "  ", sb);
			sb.append(SH.NEWLINE);
		}
		sb.append(indent).append("}");
		sb.append(SH.NEWLINE);
		return sb;
	}

}
