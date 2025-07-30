package com.f1.office.spreadsheet;

import com.f1.utils.OH;
import com.f1.utils.xml.XmlElement;

public class SpreadSheetResource_Fill extends SpreadSheetResource {

	private static final String XML_NAME = "fill";

	public SpreadSheetResource_Fill() {
		this.setXml(createDefaultXml());
	}

	public SpreadSheetResource_Fill(XmlElement e) {
		this.setXml(e);
	}

	private XmlElement createDefaultXml() {
		XmlElement r = new XmlElement(XML_NAME);
		XmlElement pt = new XmlElement("patternFill").addAttribute("patternType", "none");
		r.addChild(pt);
		return r;
	}

	public String getStyle() {
		try {
			return this.getXml().getFirstElement("patternFill").getAttribute("patternType");
		} catch (Exception e) {
		}
		return null;
	}

	public void setStyle(String style) {
		assertNotLocked();
		try {
			XmlElement existing = this.getXml().getFirstElement("patternFill");
			if (existing != null)
				existing.addAttributeNotStrict("patternType", style);
			else
				this.getXml().setFirstElement(new XmlElement("patternFill").addAttribute("patternType", style));
		} catch (Exception e) {
		}
	}

	public SpreadSheetCellColor getFgColor() {
		try {
			XmlElement e = this.getXml().getFirstElement("patternFill");
			SpreadSheetCellColor color = new SpreadSheetCellColor("fgColor");
			color.readFromXml(e, 0);
			return color;
		} catch (Exception e) {
		}
		return null;
	}

	public void setFgColor(SpreadSheetCellColor fgColor) {
		assertNotLocked();
		try {
			this.getXml().getFirstElement("patternFill").setFirstElement(fgColor.toXmlElement());
		} catch (Exception e) {
		}
	}

	public SpreadSheetCellColor getBgColor() {
		try {
			XmlElement e = this.getXml().getFirstElement("patternFill");
			SpreadSheetCellColor color = new SpreadSheetCellColor("bgColor");
			color.readFromXml(e, 0);
			return color;
		} catch (Exception e) {
		}
		return null;
	}

	public void setBgColor(SpreadSheetCellColor bgColor) {
		assertNotLocked();
		try {
			this.getXml().getFirstElement("patternFill").setFirstElement(bgColor.toXmlElement());
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
		SpreadSheetResource_Fill other = (SpreadSheetResource_Fill) obj;
		return OH.eq(this.getXml(), other.getXml());
	}

	@Override
	public void clear() {
		this.setXml(createDefaultXml());
	}

}
