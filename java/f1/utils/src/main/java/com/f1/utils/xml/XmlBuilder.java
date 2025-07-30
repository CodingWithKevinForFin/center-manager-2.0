package com.f1.utils.xml;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import com.f1.utils.SH;

public class XmlBuilder {
	private XmlNode rootElement;
	private Object currentElement;
	private XmlNode lastAdd;
	private Stack<Object> positions;
	private static final int SIZE_NOT_DEFINED = -1;
	private static final int FIRST = 0;

	public static void main(String[] args) {

		XmlBuilder b = new XmlBuilder();
		b.init(new XmlElement("ETF_Creation_Request"));
		b.addAttribute("time", System.currentTimeMillis());
		b.addElement(new XmlElement("ETF_Details"));
		b.elements().walk(0); // goes into etf details
		b.addTextElement("ETF_Name", "Example Etf");
		b.addTextElement("ETF_Ticker", "EXM");
		b.addTextElement("ETF_Issuer", "Issuer Etf");
		b.popNode();
		b.addElement("Authorized_Participant_Details");

		b.enter();
		//		b.elements().walk(1); // goes into etf details same as previous line
		b.addTextElement("AP_Name", "Example ap");
		b.addTextElement("AP_ID", SH.toString(1234567));
		b.pop();

		b.addElement("Creation_Details");
		b.enter();
		b.addElement("Creation_Basket");
		b.enter();
		b.addElement("Security");
		b.enter();
		b.addTextElement("Ticker", "ABC");
		b.addTextElement("Quantity", SH.toString(1000));
		b.popNode();

		b.addElement("Security");
		b.enter();
		b.addTextElement("Ticker", "DEF");
		b.addTextElement("Quantity", SH.toString(2000));

		b.popNode();
		b.popNode();
		b.addTextElement("Creation_Unit_Size", SH.toString(70000));

		//		b.nodes();
		//		System.out.println(b.csize());
		//		b.pop();
		//		b.toLastAdd();

		//
		XmlNode build = b.build();
		String s = build.toLegibleString(new StringBuilder(), 4).toString();
		System.out.println(s);

		b.walk("Authorized_Participant_Details");

		String s2 = XmlBuilder.toString(b.current());
		System.out.println(s2);

	}
	public XmlBuilder() {
		this.positions = new Stack<Object>();

	}

	public XmlBuilder(XmlNode e) {
		this.positions = new Stack<Object>();
		this.init(e);
	}

	public XmlBuilder clear() {
		this.rootElement = null;
		this.currentElement = null;
		this.lastAdd = null;
		this.positions.clear();
		return this;
	}

	public XmlBuilder init(XmlNode e) {
		this.positions.clear();
		this.lastAdd = null;
		this.rootElement = e;
		this.currentElement = e;
		return this;
	}
	public XmlBuilder reset() {
		this.positions.clear();
		this.lastAdd = null;
		this.currentElement = this.rootElement;
		return this;
	}

	public XmlBuilder pop() {
		if (this.positions.isEmpty()) {
			throw new IllegalStateException("Xml Builder is already at the root element");
		}
		this.currentElement = this.positions.pop();
		return this;
	}

	public XmlBuilder popNode() {
		this.pop();
		while (!(this.currentElement instanceof XmlNode)) {
			this.pop();
		}
		return this;
	}

	/*
	 * enter into the element that was last added
	 */
	public XmlBuilder enter() {
		if (this.lastAdd == null)
			throw new IllegalStateException("No element was previously added");
		this.positions.push(this.currentElement);
		this.currentElement = this.lastAdd;
		return this;
	}

	/*
	 * Short hand for current size
	 */
	public int csize() {
		if (this.currentElement instanceof List) {
			List l = (List) this.currentElement;
			return l.size();
		} else if (this.currentElement instanceof XmlElement) {
			return SIZE_NOT_DEFINED;

		}
		return SIZE_NOT_DEFINED;

	}

	/*
	 * Shorthand for current type
	 */
	public Class<?> ctype() {
		return this.currentElement.getClass();
	}

	/*
	 * Walk Nodes
	 */
	public XmlBuilder nodes() {
		if (this.currentElement instanceof XmlElement) {
			XmlElement x = (XmlElement) this.currentElement;
			this.positions.push(x);
			this.currentElement = x.getChildren();
		} else
			throw new IllegalStateException("Current node isn't an Xml Element");

		return this;
	}

	/*
	 * Walk Elements
	 */
	public XmlBuilder elements() {
		if (this.currentElement instanceof XmlElement) {
			XmlElement x = (XmlElement) this.currentElement;
			this.positions.push(x);
			this.currentElement = x.getElements();
		} else
			throw new IllegalStateException("Current node isn't an Xml Element");
		return this;
	}
	/*
	 * Walk Elements with name
	 */
	public XmlBuilder elements(String name) {
		if (this.currentElement instanceof XmlElement) {
			XmlElement x = (XmlElement) this.currentElement;
			this.positions.push(x);
			this.currentElement = x.getElements(name);
		} else
			throw new IllegalStateException("Current node isn't an Xml Element");
		return this;
	}

	/*
	 * alias for elements
	 */
	public XmlBuilder walk(String name) {
		return this.elements(name);
	}

	public XmlBuilder walk(Object... stops) {
		Object prevStop = null;
		for (int i = 0; i < stops.length; i++) {
			Object stop = stops[i];
			if (stop instanceof Integer) {
				this.walk((int) stop);
			} else if (stop instanceof String) {
				if (prevStop instanceof String)
					this.walk(FIRST);

				this.elements((String) stop);
			} else {
				throw new IllegalStateException("Expecting stops to be strings or integers");
			}
			prevStop = stop;
		}

		return this;
	}

	public XmlBuilder first() {
		return this.walk(FIRST);
	}
	/*
	 * walk 
	 */
	public XmlBuilder walk(int pos) {
		if (this.currentElement instanceof List) {
			List l = (List) this.currentElement;
			this.positions.push(this.currentElement);
			this.currentElement = l.get(pos);
		} else
			throw new IllegalStateException("Current node isn't an List");
		return this;
	}

	public XmlBuilder addText(String text) {
		if (this.currentElement instanceof XmlElement) {
			XmlElement x = (XmlElement) this.currentElement;
			this.lastAdd = new XmlText(text);
			x.addChild(this.lastAdd);
		} else
			throw new IllegalStateException("Current node isn't an Xml Element");
		return this;
	}

	public XmlBuilder addText(Object o) {
		if (this.currentElement instanceof XmlElement) {
			XmlElement x = (XmlElement) this.currentElement;
			this.lastAdd = new XmlText(SH.s(o));
			x.addChild(this.lastAdd);
		} else
			throw new IllegalStateException("Current node isn't an Xml Element");
		return this;
	}

	public XmlBuilder addTextElement(String name, String text) {
		if (this.currentElement instanceof XmlElement) {
			XmlElement x = (XmlElement) this.currentElement;
			XmlElement p = new XmlElement(name);
			this.lastAdd = new XmlText(text);
			x.addChild(p);
			p.addChild(this.lastAdd);
		} else if (this.rootElement == null) {
			XmlElement p = new XmlElement(name);
			this.init(p);
			this.lastAdd = new XmlText(text);
			p.addChild(this.lastAdd);
		} else
			throw new IllegalStateException("Current node isn't an Xml Element");
		return this;
	}

	public XmlBuilder addTextElement(String name, Object o) {
		if (this.currentElement instanceof XmlElement) {
			XmlElement x = (XmlElement) this.currentElement;
			XmlElement p = new XmlElement(name);
			this.lastAdd = new XmlText(SH.s(o));
			x.addChild(p);
			p.addChild(this.lastAdd);
		} else if (this.rootElement == null) {
			XmlElement p = new XmlElement(name);
			this.init(p);
			this.lastAdd = new XmlText(SH.s(o));
			p.addChild(this.lastAdd);
		} else
			throw new IllegalStateException("Current node isn't an Xml Element");
		return this;
	}

	public XmlBuilder addElement(XmlElement e) {
		if (this.currentElement instanceof XmlElement) {
			XmlElement x = (XmlElement) this.currentElement;
			this.lastAdd = e;
			x.addChild(e);
		} else if (this.rootElement == null) {
			this.init(e);
		} else
			throw new IllegalStateException("Current node isn't an Xml Element");
		return this;
	}

	public XmlBuilder addNode(XmlNode e) {
		if (this.currentElement instanceof XmlElement) {
			XmlElement x = (XmlElement) this.currentElement;
			this.lastAdd = e;
			x.addChild(e);
		} else if (this.rootElement == null) {
			this.init(e);
		} else
			throw new IllegalStateException("Current node isn't an Xml Element");
		return this;
	}

	public XmlBuilder addElement(String name) {
		if (this.currentElement instanceof XmlElement) {
			XmlElement x = (XmlElement) this.currentElement;
			this.lastAdd = new XmlElement(name);
			x.addChild(this.lastAdd);
		} else if (this.rootElement == null) {
			XmlElement x = new XmlElement(name);
			this.init(x);
		} else
			throw new IllegalStateException("Current node isn't an Xml Element");
		return this;
	}
	public XmlBuilder addAttributes(Map<String, String> attributes) {
		if (this.currentElement instanceof XmlElement) {
			XmlElement x = (XmlElement) this.currentElement;
			for (Entry<String, String> e : attributes.entrySet()) {
				x.addAttribute(e.getKey(), e.getValue());
			}
		} else
			throw new IllegalStateException("Current node isn't an Xml Element");
		return this;
	}
	public XmlBuilder addAttribute(String key, String value) {
		if (this.currentElement instanceof XmlElement) {
			XmlElement x = (XmlElement) this.currentElement;
			x.addAttribute(key, value);
		} else
			throw new IllegalStateException("Current node isn't an Xml Element");
		return this;
	}

	public XmlBuilder addAttribute(String key, long value) {
		if (this.currentElement instanceof XmlElement) {
			XmlElement x = (XmlElement) this.currentElement;
			x.addAttribute(key, value);
		} else
			throw new IllegalStateException("Current node isn't an Xml Element");
		return this;
	}
	public XmlBuilder addAttribute(String key, double value) {
		if (this.currentElement instanceof XmlElement) {
			XmlElement x = (XmlElement) this.currentElement;
			x.addAttribute(key, value);
		} else
			throw new IllegalStateException("Current node isn't an Xml Element");
		return this;
	}
	public XmlNode build() {
		return this.rootElement;
	}
	public Object current() {
		return this.currentElement;
	}
	public XmlNode lastAdd() {
		return this.lastAdd;
	}
	public XmlNode buildAndClear() {
		XmlNode r = this.rootElement;
		this.clear();
		return r;
	}

	public static String toString(Object o) {
		return XmlBuilder.toString(new StringBuilder(), o).toString();
	}
	public static StringBuilder toString(StringBuilder sb, Object o) {
		if (o instanceof List) {
			List l = (List) o;
			for (Object e : l) {
				if (e instanceof XmlNode) {
					XmlNode n = (XmlNode) e;
					n.toLegibleString(sb, 0, XmlNode.VERSION_DEFAULT);
				} else
					throw new IllegalStateException("Object in current list isn't an Xml Node or List");

			}
		} else if (o instanceof XmlNode) {
			XmlNode n = (XmlNode) o;
			n.toLegibleString(sb, 0, XmlNode.VERSION_DEFAULT);
		} else
			throw new IllegalStateException("Current node isn't an Xml Node or List");
		return sb;
	}

}
