package com.f1.utils.xml;

import com.f1.base.Clearable;
import com.f1.utils.AH;
import com.f1.utils.ByteHelper;
import com.f1.utils.OH;

public class XmlRawText implements XmlNode, Clearable {
	final private static byte STATE_INIT = 0;
	final private static byte STATE_HAS_NAME = 1;
	final private static byte STATE_HAS_ATTRIBUTES = 2;
	final private static byte STATE_HAS_CHILDREN = 3;
	final private static byte STATE_DONE = 4;

	private StringBuilder sink = new StringBuilder();
	int nameStart = -1;
	int nameEnd = -1;
	private byte state = STATE_INIT;

	int stackPos = 0;
	byte stack[] = new byte[27];
	private byte xmlVersion;

	public XmlRawText(CharSequence name, byte xmlVersion) {
		setName(name);
		this.xmlVersion = xmlVersion;
	}

	private void setName(CharSequence name) {
		assertState(STATE_INIT);
		sink.append('<');
		nameStart = sink.length();
		sink.append(name);
		nameEnd = sink.length();
		state = STATE_HAS_NAME;
	}
	private void assertState(byte state) {
		if (this.state != state)
			throw new IllegalStateException("Expecting state " + state + ", but in " + this.state);
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		finish();
		return sink.append(this.sink);
	}

	private void finish() {
		while (this.stackPos > 0)
			this.endChild();
		done();
	}

	@Override
	public StringBuilder toLegibleString(StringBuilder sink, int i) {
		finish();
		return sink.append(sink);
	}

	public XmlRawText addAttribute(String key, long number) {
		appendAttributeKey(key);
		sink.append(number).append('"');
		return this;
	}
	public XmlRawText addAttribute(String key, double number) {
		appendAttributeKey(key);
		sink.append(number).append('"');
		return this;
	}
	public XmlRawText addAttribute(String key, CharSequence value) {
		if (value == null)
			throw new NullPointerException("null value for attribute: " + key);
		appendAttributeKey(key);
		XmlElement.appendAttribute(sink, value, this.xmlVersion);
		sink.append('"');
		return this;
	}

	private void appendAttributeKey(String key) {
		if (this.state != STATE_HAS_ATTRIBUTES)
			assertState(STATE_HAS_NAME);
		if (key.length() == 0)
			throw new RuntimeException("invalid attribute name:" + key);
		final char first = key.charAt(0);
		if (!(OH.isBetween(first, 'a', 'z') || OH.isBetween(first, 'A', 'Z') || first == '_') || "xml".equalsIgnoreCase(key) || key.indexOf(' ', 0) != -1)
			throw new RuntimeException("invalid attribute name: '" + key + "'");
		sink.append(' ').append(key).append('=').append('"');
		state = STATE_HAS_ATTRIBUTES;
	}

	public XmlRawText addChild(XmlNode text) {
		if (text == this)
			throw new IllegalArgumentException("Can not add child to itself");
		startChild();
		text.toString(sink);
		return this;
	}
	public XmlRawText addChild(CharSequence text) {
		startChild();
		XmlElement.appendAttribute(sink, text, this.xmlVersion);
		return this;
	}
	public XmlRawText addChild(double value) {
		startChild();
		sink.append(value);
		return this;
	}
	public XmlRawText addChild(long value) {
		startChild();
		sink.append(value);
		return this;
	}
	private void startChild() {
		switch (state) {
			case STATE_HAS_NAME:
			case STATE_HAS_ATTRIBUTES:
				sink.append('>');
				state = STATE_HAS_CHILDREN;
				break;
			default:
				assertState(STATE_HAS_CHILDREN);
		}
	}

	@Override
	public void clear() {
		this.state = STATE_INIT;
		this.stackPos = 0;
		this.nameStart = -1;
		this.nameEnd = -1;
		this.sink.setLength(0);
	}
	private void done() {
		switch (state) {
			case STATE_DONE:
				return;
			case STATE_HAS_NAME:
			case STATE_HAS_ATTRIBUTES:
				sink.append("/>");
				break;
			case STATE_HAS_CHILDREN:
				sink.append("</");
				sink.append(sink, this.nameStart, this.nameEnd);
				sink.append('>');
				break;
			default:
				assertState(STATE_HAS_NAME);

		}
		state = STATE_DONE;
	}

	public void startChildElement(String si) {
		switch (state) {
			case STATE_HAS_NAME:
			case STATE_HAS_ATTRIBUTES:
				sink.append('>');
				state = STATE_HAS_CHILDREN;
				break;
			default:
				assertState(STATE_HAS_CHILDREN);
		}
		pushStack();
		setName(si);
	}
	public void endChild() {
		if (this.stackPos == 0)
			throw new IllegalStateException("call startChildElement(...) first");
		done();
		popStack();
	}
	private void pushStack() {
		this.stack = AH.enssureCapacity(this.stack, this.stackPos + 9, 2);
		ByteHelper.writeByte(state, this.stack, this.stackPos);
		this.stackPos++;
		ByteHelper.writeInt(nameStart, this.stack, this.stackPos);
		this.stackPos += 4;
		ByteHelper.writeInt(nameEnd, this.stack, this.stackPos);
		this.stackPos += 4;

		this.state = 0;
		this.nameStart = -1;
		this.nameEnd = -1;
	}
	private void popStack() {
		this.stackPos -= 4;
		this.nameEnd = ByteHelper.readInt(this.stack, this.stackPos);
		this.stackPos -= 4;
		this.nameStart = ByteHelper.readInt(this.stack, this.stackPos);
		this.stackPos--;
		this.state = ByteHelper.readByte(this.stack, this.stackPos);
	}

	@Override
	public StringBuilder toLegibleString(StringBuilder sink, int i, byte version) {
		if (this.xmlVersion != version)
			throw new IllegalStateException("XML Version mismatch: " + this.xmlVersion + " vs " + version);
		return toLegibleString(sink, i);
	}

	@Override
	public StringBuilder toString(StringBuilder sink, byte version) {
		if (this.xmlVersion != version)
			throw new IllegalStateException("XML Version mismatch: " + this.xmlVersion + " vs " + version);
		return toString(sink);
	}

	@Override
	public int hashCode() {
		return OH.hashCode((Object) this.xmlVersion, this.sink.toString());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XmlRawText other = (XmlRawText) obj;
		return OH.eq(this.xmlVersion, other.xmlVersion) && OH.eq(this.sink.toString(), other.sink.toString());
	}

}
