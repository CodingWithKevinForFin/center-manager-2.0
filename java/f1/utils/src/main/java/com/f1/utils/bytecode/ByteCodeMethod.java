package com.f1.utils.bytecode;

import java.io.DataInput;
import java.io.IOException;
import java.util.List;

import com.f1.utils.SH;

public class ByteCodeMethod extends ByteCodeAttributable {

	private String returnTypeText;
	private List<String> argumentsText;
	private ByteCodeConstUtf descriptor;
	private ByteCodeCode code;

	public ByteCodeMethod() {
	}

	public ByteCodeConstUtf getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(ByteCodeConstUtf descriptor) {
		this.descriptor = descriptor;
		List<String> arguments = ByteCodeUtils.parseArguments(descriptor.getUtf());
		this.argumentsText = arguments.subList(0, arguments.size() - 1);
		this.returnTypeText = arguments.get(arguments.size() - 1);
	}

	public List<String> getArgumentsText() {
		return argumentsText;
	}

	public StringBuilder toJavaString(String indent, StringBuilder sb) {
		super.toJavaString(indent, sb);
		sb.append(indent);
		ByteCodeUtils.modifiersMaskToString(getAccessFlags(), sb, false);
		sb.append(' ');
		String name = getNameText();
		if (!name.equals("<init>") && !name.equals("<clinit>")) {
			sb.append(returnTypeText);
			sb.append(' ');
			sb.append(getName().getUtf());
		} else {
			sb.append(getOwner().getThisClass().getClassNameText());
		}
		sb.append('(');
		for (int i = 0; i < argumentsText.size(); i++) {
			if (i > 0)
				sb.append(",");
			sb.append(argumentsText.get(i));
			sb.append(" arg").append(i);
		}
		sb.append(')');
		if (code != null) {
			sb.append("{");
			sb.append(SH.NEWLINE);
			code.toJavaString(indent + "  ", sb);
			sb.append(indent).append("}");
		} else
			sb.append(";");
		sb.append(SH.NEWLINE);
		return sb;
	}

	@Override
	public void addAttribute(ByteCodeConstUtf key, DataInput dis, int length) throws IOException {
		if (key.getUtf().equals(ByteCodeConstants.ATTR_CODE)) {
			this.code = new ByteCodeCode(dis, this);
		} else
			super.addAttribute(key, dis, length);
	}

	public Object getDescriptorText() {
		return descriptor.getUtf();
	}

	public byte[] getCode() {
		return this.code == null ? null : this.code.getCode();
	}
	public String getCodeText() {
		return this.code == null ? null : this.code.toJavaString("", new StringBuilder()).toString();
	}

	public Object getReturnTypeText() {
		return returnTypeText;
	}

}
