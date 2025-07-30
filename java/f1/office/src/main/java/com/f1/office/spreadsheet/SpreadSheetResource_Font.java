package com.f1.office.spreadsheet;

import com.f1.utils.OH;
import com.f1.utils.xml.XmlElement;

public class SpreadSheetResource_Font extends SpreadSheetResource {

	private static final String XML_NAME = "font";

	public SpreadSheetResource_Font() {
		this.setXml(createDefaultXml());
	}

	public SpreadSheetResource_Font(XmlElement e) {
		this.setXml(e);
	}

	private XmlElement createDefaultXml() {
		XmlElement r = new XmlElement(XML_NAME);
		r.addChild(new XmlElement("sz").addAttribute("val", "10"));
		r.addChild(new XmlElement("family").addAttribute("val", "2"));
		r.addChild(new XmlElement("scheme").addAttribute("val", "minor"));
		return r;
	}

	public SpreadSheetCellColor getColor() {
		try {
			XmlElement e = this.getXml();
			SpreadSheetCellColor color = new SpreadSheetCellColor("color");
			color.readFromXml(e, 0);
			return color;
		} catch (Exception e) {
		}
		return null;
	}

	public void setColor(SpreadSheetCellColor color) {
		assertNotLocked();
		try {
			this.getXml().setFirstElement(color.toXmlElement());
		} catch (Exception e) {
		}
	}

	public String getSize() {
		try {
			return this.getXml().getFirstElement("sz").getAttribute("val");
		} catch (Exception e) {
		}
		return null;
	}

	public void setSize(String size) {
		assertNotLocked();
		try {
			this.getXml().getFirstElement("sz").addAttributeNotStrict("val", size);
		} catch (Exception e) {
		}
	}

	public String getCharset() {
		try {
			return this.getXml().getFirstElement("charset").getAttribute("val");
		} catch (Exception e) {
		}
		return null;
	}

	public void setCharset(String charset) {
		assertNotLocked();
		try {
			XmlElement e = new XmlElement("charset").addAttribute("val", charset);
			this.getXml().setFirstElement(e);
		} catch (Exception e) {
		}
	}

	public String getScheme() {
		try {
			return this.getXml().getFirstElement("scheme").getAttribute("val");
		} catch (Exception e) {
		}
		return null;
	}

	public void setScheme(String scheme) {
		assertNotLocked();
		try {
			this.getXml().getFirstElement("scheme").addAttributeNotStrict("val", scheme);
		} catch (Exception e) {
		}
	}

	public String getName() {
		try {
			return this.getXml().getFirstElement("name").getAttribute("val");
		} catch (Exception e) {
		}
		return null;
	}

	public void setName(String name) {
		assertNotLocked();
		try {
			XmlElement e = new XmlElement("name").addAttribute("val", name);
			this.getXml().setFirstElement(e);
		} catch (Exception e) {
		}
	}

	public String getFamily() {
		try {
			return this.getXml().getFirstElement("family").getAttribute("val");
		} catch (Exception e) {
		}
		return null;
	}

	public void setFamily(String family) {
		assertNotLocked();
		try {
			this.getXml().getFirstElement("family").addAttributeNotStrict("val", family);
		} catch (Exception e) {
		}
	}

	public void setBold(boolean isBold) {
		if (isBold) {
			this.getXml().setFirstElement(new XmlElement("b"));
		} else {
			this.getXml().deleteFirstElement("b");
		}
	}

	public boolean getBold() {
		try {
			return this.getXml().getFirstElement("b") != null;
		} catch (Exception e) {
		}
		return false;
	}

	public void setItalic(boolean isItalic) {
		if (isItalic) {
			this.getXml().setFirstElement(new XmlElement("i"));
		} else {
			this.getXml().deleteFirstElement("i");
		}
	}

	public boolean getItalic() {
		try {
			return this.getXml().getFirstElement("i") != null;
		} catch (Exception e) {
		}
		return false;
	}

	public void setUnderline(boolean isUnderlined) {
		if (isUnderlined) {
			this.getXml().setFirstElement(new XmlElement("u"));
		} else {
			this.getXml().deleteFirstElement("u");
		}
	}

	public boolean getUnderlined() {
		try {
			return this.getXml().getFirstElement("u") != null;
		} catch (Exception e) {
		}
		return false;
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
		SpreadSheetResource_Font other = (SpreadSheetResource_Font) obj;
		return OH.eq(this.getXml(), other.getXml());
	}

	@Override
	public void clear() {
		this.setXml(createDefaultXml());
	}

}
