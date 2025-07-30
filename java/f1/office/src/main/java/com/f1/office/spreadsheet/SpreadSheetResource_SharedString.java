package com.f1.office.spreadsheet;

import com.f1.utils.OH;
import com.f1.utils.xml.XmlElement;
import com.f1.utils.xml.XmlText;

public class SpreadSheetResource_SharedString extends SpreadSheetResource {

	private static final String XML_NAME = "si";

	public SpreadSheetResource_SharedString() {
		this.setXml(createDefaultXml());
	}

	public SpreadSheetResource_SharedString(XmlElement e) {
		this.setXml(e);
	}

	private XmlElement createDefaultXml() {
		return new XmlElement(XML_NAME);
	}

	public String getString() {
		try {
			return this.getXml().getFirstElement("t").getInnerAsString();
		} catch (Exception e) {
		}
		return null;
	}

	public void setString(String value) {
		assertNotLocked();
		try {
			XmlElement t = new XmlElement("t");
			t.addChild(new XmlText(value));
			this.getXml().setFirstElement(t);
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
		SpreadSheetResource_SharedString other = (SpreadSheetResource_SharedString) obj;
		return OH.eq(this.getXml(), other.getXml());
	}

	@Override
	public void clear() {
		this.setXml(createDefaultXml());
	}

}
