package com.f1.utils.bytecode;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.f1.utils.IOH;

public class ByteCodeAttributable {

	private ByteCodeClass owner;
	private ByteCodeConstUtf name;
	private int accessFlags;
	private List<ByteCodeAttribute> unknownAttributes = new ArrayList<ByteCodeAttribute>(2);
	private ByteCodeAnnotations visibleAnnotations = null;
	private ByteCodeAnnotations invisibleAnnotations;
	private ByteCodeSignature signature;

	public ByteCodeAttributable() {
	}

	public void setOwner(ByteCodeClass owner) {
		this.owner = owner;
	}

	public Iterable<ByteCodeAttribute> getUnknownAttributes() {
		return unknownAttributes;
	}
	public ByteCodeAnnotations getVisibleAnnotations() {
		return visibleAnnotations;
	}
	public ByteCodeAnnotations getInvisibleAnnotations() {
		return invisibleAnnotations;
	}
	public ByteCodeSignature getSignature() {
		return signature;
	}

	public void addAttribute(ByteCodeConstUtf key, DataInput dis, int length) throws IOException {
		String att = key.getUtf();
		if (ByteCodeConstants.ATTR_SIGNATURE.equals(att)) {
			signature = new ByteCodeSignature(dis, getOwner());
		} else if (ByteCodeConstants.ATTR_RUNTIME_INVISIBLE_ANNOTATIONS.equals(att)) {
			invisibleAnnotations = new ByteCodeAnnotations(dis, getOwner());
		} else if (ByteCodeConstants.ATTR_RUNTIME_VISIBLE_ANNOTATIONS.equals(att)) {
			invisibleAnnotations = new ByteCodeAnnotations(dis, getOwner());
		} else
			unknownAttributes.add(new ByteCodeUnknownAttribute(key, IOH.readData(dis, length)));
	}
	public ByteCodeConstUtf getName() {
		return name;
	}

	public void setName(ByteCodeConstUtf name) {
		this.name = name;
	}

	public int getAccessFlags() {
		return accessFlags;
	}

	public void setAccessFlags(int accessFlags) {
		this.accessFlags = accessFlags;
	}

	public ByteCodeClass getOwner() {
		return owner;
	}

	public StringBuilder toJavaString(String indent, StringBuilder sb) {
		for (ByteCodeAttribute unk : this.unknownAttributes)
			unk.toJavaString(indent, sb);
		if (this.visibleAnnotations != null)
			this.visibleAnnotations.toJavaString(indent, sb);
		if (this.invisibleAnnotations != null)
			this.invisibleAnnotations.toJavaString(indent, sb);
		if (this.signature != null)
			this.signature.toJavaString(indent, sb);
		return sb;
	}

	public String toJavaString() {
		return toJavaString("", new StringBuilder()).toString();
	}

	public String getNameText() {
		return name.getUtf();
	}

}
