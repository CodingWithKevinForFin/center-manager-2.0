package com.f1.utils.xml;

import com.f1.utils.OH;
import com.f1.utils.SH;

public class XmlText implements XmlNode {

	private final String text;

	public XmlText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return text;
	}

	@Override
	public StringBuilder toString(StringBuilder sink, byte version) {
		XmlElement.appendAttribute(sink, text, version);
		return sink;
	}

	@Override
	public StringBuilder toLegibleString(StringBuilder sink, int i, byte version) {
		SH.repeat(' ', i, sink);
		StringBuilder buf = new StringBuilder(text.length() + 10);
		XmlElement.appendAttribute(buf, text, version);
		SH.replaceAll(buf.toString(), '\n', '\n' + SH.repeat(' ', i), sink);
		sink.append(SH.NEWLINE);
		return sink;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return toString(sink, VERSION_DEFAULT);
	}

	@Override
	public StringBuilder toLegibleString(StringBuilder sink, int i) {
		return toLegibleString(sink, i, VERSION_DEFAULT);
	}

	@Override
	public int hashCode() {
		return OH.hashCode(this.text);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XmlText other = (XmlText) obj;
		return OH.eq(this.text, other.text);
	}
}
