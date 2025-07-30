package com.f1.utils.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class XmlElement implements XmlNode {

	private String name;
	private List<XmlNode> children;
	private Map<String, String> attributes = new LinkedHashMap<String, String>();

	public XmlElement(String name) {
		this.name = name;
	}

	public XmlElement addChild(XmlNode text) {
		if (text == this)
			throw new IllegalArgumentException("Can not add child to itself");
		if (children == null)
			children = new ArrayList<XmlNode>();
		children.add(text);
		return this;
	}

	public XmlElement addTextNode(XmlText text) {
		if (text.toString() == this.toString())
			throw new IllegalArgumentException("Can not add child to itself");
		if (children == null)
			children = new ArrayList<XmlNode>();
		children.add(text);
		return this;
	}

	@SuppressWarnings("unchecked")
	public List<XmlNode> getChildren() {
		return children == null ? Collections.EMPTY_LIST : children;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public String getAttribute(String name) {
		return attributes.get(name);
	}

	public boolean hasAttribute(String name) {
		return attributes.containsKey(name);
	}

	public String removeAttribute(String name) {
		return attributes.remove(name);
	}

	public XmlElement addAttribute(String key, long number) {
		return addAttribute(key, SH.toString(number));
	}
	public XmlElement addAttribute(String key, double number) {
		return addAttribute(key, SH.toString(number));
	}
	public XmlElement addAttribute(String key, String value) {
		if (value == null)
			throw new NullPointerException("null value in '" + this.getName() + "' for attribute: " + key);
		if (key.length() == 0)
			throw new RuntimeException("invalid attribute name:" + key);
		final char first = key.charAt(0);
		if (!(OH.isBetween(first, 'a', 'z') || OH.isBetween(first, 'A', 'Z') || first == '_') || "xml".equalsIgnoreCase(key) || key.indexOf(' ', 0) != -1)
			throw new RuntimeException("invalid attribute name: '" + key + "'");
		CH.putOrThrow(attributes, key, value);
		return this;
	}
	public XmlElement addAttributeNotStrict(String key, String value) {
		if (value == null)
			throw new NullPointerException("null value in '" + this.getName() + "' for attribute: " + key);
		attributes.put(key, value);
		return this;
	}

	public String getName() {
		return name;
	}

	public XmlElement getFirstElement(String name) {
		if (CH.isntEmpty(children))
			for (XmlNode child : children)
				if (child instanceof XmlElement) {
					XmlElement element = (XmlElement) child;
					if (name.equals(element.getName()))
						return element;
				}
		return null;
	}

	public boolean deleteFirstElement(String name) {
		if (CH.isntEmpty(children))
			for (XmlNode child : children)
				if (child instanceof XmlElement) {
					XmlElement element = (XmlElement) child;
					if (name.equals(element.getName()))
						return children.remove(child);
				}
		return false;
	}
	public XmlElement setFirstElement(XmlElement e) {
		if (CH.isntEmpty(children))
			for (int i = 0; i < children.size(); ++i) {
				final XmlNode child = children.get(i);
				if (child instanceof XmlElement) {
					XmlElement element = (XmlElement) child;
					if (e.name.equals(element.getName())) {
						children.set(i, e);
						return this;
					}
				}
			}
		return addChild(e);
	}

	public List<XmlElement> getElements(String name) {
		List<XmlElement> r = new ArrayList<XmlElement>();
		if (CH.isntEmpty(children))
			for (XmlNode child : children)
				if (child instanceof XmlElement) {
					XmlElement element = (XmlElement) child;
					if (name.equals(element.getName()))
						r.add(element);
				}
		return r;
	}
	public List<XmlElement> getElements() {
		List<XmlElement> r = new ArrayList<XmlElement>();
		if (CH.isntEmpty(children))
			for (XmlNode child : children)
				if (child instanceof XmlElement) {
					r.add((XmlElement) child);
				}
		return r;
	}

	public String getInnerAsString() {
		if (children == null)
			return "";
		if (children.size() == 1)
			return children.get(0).toString();
		else {
			StringBuilder r = new StringBuilder();
			for (XmlNode child : children)
				child.toString(r);
			return r.toString();
		}
	}

	private void append(StringBuilder sink, String value) {
		sink.append(value);
	}

	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	public String toLegibleString() {
		return toLegibleString(new StringBuilder(), 0).toString();
	}

	@Override
	public StringBuilder toLegibleString(StringBuilder sink, int i, byte version) {
		SH.repeat(' ', i, sink);
		if (!isRootDocument()) {
			sink.append('<');
			sink.append(name);
			for (Entry<String, String> e : attributes.entrySet()) {
				sink.append(' ').append(e.getKey()).append('=').append('"');
				appendAttribute(sink, e.getValue(), version);
				sink.append('"');
			}
		}
		if (!CH.isEmpty(children)) {
			if (!isRootDocument())
				sink.append('>').append(SH.NEWLINE);
			for (XmlNode child : children)
				child.toLegibleString(sink, i + 2);
			SH.repeat(' ', i, sink);
			if (!isRootDocument()) {
				sink.append("</").append(name).append('>').append(SH.NEWLINE);
			}
		} else if (!isRootDocument())
			sink.append("/>").append(SH.NEWLINE);
		return sink;
	}

	static public void appendAttribute(StringBuilder sink, CharSequence value, byte version) {
		if (version == VERSION_XML10) {
			for (int i = 0, l = value.length(); i < l; i++) {
				char c = value.charAt(i);
				switch (value.charAt(i)) {
					case '&':
						sink.append("&amp;");
						break;
					case '<':
						sink.append("&lt;");
						break;
					case '"':
						sink.append("&quot;");
						break;
					case 0x0009:
					case 0x000A:
					case 0x000D:
						sink.append(c);
						break;
					default:
						if (OH.isBetween(c, 0x0020, 0xd7ff) || OH.isBetween(c, 0xe000, 0xfffd))
							sink.append(c);
						else
							sink.append('\u00bf');
				}
			}
		} else {
			for (int i = 0, l = value.length(); i < l; i++) {
				char c = value.charAt(i);
				switch (value.charAt(i)) {
					case '&':
						sink.append("&amp;");
						break;
					case '<':
						sink.append("&lt;");
						break;
					case '"':
						sink.append("&quot;");
						break;
					case 0x0009:
					case 0x000A:
					case 0x000D:
						sink.append(c);
						break;
					default:
						if (OH.isBetween(c, 0x0020, 0xd7ff) || OH.isBetween(c, 0xe000, 0xfffd))
							sink.append(c);
						else {
							sink.append("&#");
							sink.append((int) c);
							sink.append(';');
						}
				}
			}
		}
	}
	@Override
	public StringBuilder toString(StringBuilder sink, byte version) {
		if (!isRootDocument()) {
			sink.append('<');
			sink.append(name);
			for (Entry<String, String> e : attributes.entrySet()) {
				sink.append(' ').append(e.getKey()).append('=').append('"');
				appendAttribute(sink, e.getValue(), version);
				sink.append('"');
			}
		}
		if (!CH.isEmpty(children)) {
			if (!isRootDocument())
				sink.append('>');
			for (XmlNode child : children)
				child.toString(sink);
			if (!isRootDocument()) {
				sink.append("</").append(name).append('>');
			}
		} else if (!isRootDocument())
			sink.append("/>");
		return sink;
	}

	private boolean isRootDocument() {
		return "".equals(name) && attributes.isEmpty();
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
		return OH.hashCode(this.name, this.children, this.attributes);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XmlElement other = (XmlElement) obj;
		return OH.eq(this.name, other.name) && OH.eq(this.children, other.children) && OH.eq(this.attributes, other.attributes);
	}
}
