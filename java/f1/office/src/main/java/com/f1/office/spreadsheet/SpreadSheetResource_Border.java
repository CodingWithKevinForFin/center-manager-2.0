package com.f1.office.spreadsheet;

import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.xml.XmlElement;

public class SpreadSheetResource_Border extends SpreadSheetResource {

	private static final String XML_NAME = "border";

	public SpreadSheetResource_Border() {
		this.setXml(createDefaultXml());
	}

	public SpreadSheetResource_Border(XmlElement e) {
		this.setXml(e);
	}

	private XmlElement createDefaultXml() {
		XmlElement r = new XmlElement(XML_NAME);
		r.addChild(new XmlElement("left"));
		r.addChild(new XmlElement("right"));
		r.addChild(new XmlElement("top"));
		r.addChild(new XmlElement("bottom"));
		r.addChild(new XmlElement("diagonal"));
		return r;
	}

	public String getStyle(String position) {
		position = SH.toLowerCase(position);
		try {
			return this.getXml().getFirstElement(XML_NAME).getFirstElement(position).getAttribute("style");
		} catch (Exception e) {
		}
		return null;
	}

	public void setStyle(String style, String position) {
		if (!SH.is(style) || !SH.is(position))
			return;
		assertNotLocked();
		position = SH.toLowerCase(position);
		try {
			this.getXml().getFirstElement(XML_NAME).getFirstElement(position).addAttributeNotStrict("style", style);
		} catch (Exception e) {
		}
	}

	public SpreadSheetCellColor getColor(String position) {
		position = SH.toLowerCase(position);
		try {
			XmlElement e = this.getXml().getFirstElement(XML_NAME).getFirstElement(position);
			SpreadSheetCellColor color = new SpreadSheetCellColor("color");
			color.readFromXml(e, 0);
			return color;
		} catch (Exception e) {
		}
		return null;
	}

	public void setColor(SpreadSheetCellColor color, String position) {
		if (!SH.is(color) || !SH.is(position))
			return;
		assertNotLocked();
		position = SH.toLowerCase(position);
		try {
			this.getXml().getFirstElement(XML_NAME).getFirstElement(position).setFirstElement(color.toXmlElement());
		} catch (Exception e) {
		}
	}

	@Override
	public int hashCode() {
		return OH.hashCode(this.getXml());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpreadSheetResource_Border other = (SpreadSheetResource_Border) obj;
		return OH.eq(this.getXml(), other.getXml());
	}

	@Override
	public void clear() {
		this.setXml(createDefaultXml());
	}

}
