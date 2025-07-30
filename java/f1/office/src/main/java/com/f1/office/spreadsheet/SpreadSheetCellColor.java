package com.f1.office.spreadsheet;

import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.xml.XmlElement;

//Helper class for handling excel's color variants
public class SpreadSheetCellColor {
	
	private XmlElement xml;
	
	//color cache

	//Could be color, bgColor, fgColor,...
	private String name; 
	//Could be theme, indexed, rgb
	private String attributeName;
	
	public SpreadSheetCellColor(final String name) {
		this.name = name;
		this.xml = new XmlElement(name);
	}
	
	public SpreadSheetCellColor(final String name, final String attributeName, final String value) {
		this.name = name;
		this.attributeName = attributeName;
		this.xml = new XmlElement(name);
		String parsedColor = SH.equals(attributeName, "rgb") ? cleanColor(value) : value;
		if (SH.isnt(parsedColor))
			parsedColor = "#000000FF";
		this.xml.addAttribute(attributeName, parsedColor);
	}
	
	public void setValue(final String value) {
		this.xml.addAttributeNotStrict(this.attributeName, SH.equals(attributeName, "rgb") ? cleanColor(value) : value);
	}
	
	//Index offset if there are already existing themes
	public void readFromXml(final XmlElement e, int themeIndexOffset) {
		String value = e.getAttribute("theme");
		this.xml = e;
		if (value != null) {
			int intVal = SH.parseInt(value);
			value = SH.toString(intVal + themeIndexOffset);
			this.xml.addAttributeNotStrict("theme", value);
			this.attributeName = "theme";
			return;
		}
		value = e.getAttribute("indexed");
		if (value != null) {
			this.attributeName = "indexed";
			return;
		}
		value = e.getAttribute("rgb");
		if (value != null) {
			this.attributeName = "rgb";
			return;
		}
		throw new RuntimeException("Could not detect " + name + " attribute: " + e.toString());
	}
	
	public XmlElement toXmlElement() {
		return this.xml;
	}

	private String cleanColor(String color) {
		if (SH.isnt(color) || color.length() < 6)
			return null;
		if (color.length() == 8 && color.charAt(0) != ' ' && color.charAt(7) != ' ')
			return color.toUpperCase();
		return cleanColor2(color);
	}

	private String cleanColor2(String color) {
		StringBuilder tmp = new StringBuilder();
		SH.clear(tmp);
		tmp.append("FF");
		for (int i = 0, l = color.length(); i < l; i++) {
			char c = Character.toUpperCase(color.charAt(i));
			if (OH.isBetween(c, '0', '9') || OH.isBetween(c, 'A', 'F')) {
				tmp.append(c);
				if (tmp.length() == 8)
					break;
			}
		}
		while (tmp.length() < 8)
			tmp.append('F');
		return tmp.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpreadSheetCellColor other = (SpreadSheetCellColor) obj;
		return OH.eq(attributeName, other.attributeName) && OH.eq(name, other.name) && OH.eq(xml.toString(), other.xml.toString());
	}
	
	@Override
	public String toString() {
		return name + "_" + attributeName + ":" + xml.toString();
	}


}
