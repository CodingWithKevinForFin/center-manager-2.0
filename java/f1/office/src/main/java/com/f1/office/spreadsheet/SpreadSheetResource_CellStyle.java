package com.f1.office.spreadsheet;

import com.f1.base.LockedException;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.xml.XmlElement;

public class SpreadSheetResource_CellStyle extends SpreadSheetResource {

	private static final String XML_NAME = "cellStyle";

	public SpreadSheetResource_CellStyle() {
		this.setXml(createDefaultXml());
	}

	public SpreadSheetResource_CellStyle(XmlElement e) {
		this.setXml(e);
	}

	private XmlElement createDefaultXml() {
		XmlElement r = new XmlElement(XML_NAME);
		r.addAttribute("name", "").addAttribute("xfId", "0");
		return r;
	}

	public String getName() {
		try {
			return this.getXml().getAttribute("name");
		} catch (Exception e) {
		}
		return null;
	}
	public void setName(String name) {
		assertNotLocked();
		try {
			this.getXml().addAttributeNotStrict("name", name);
		} catch (Exception e) {
		}
	}

	public Integer getXFId() {
		try {
			return SH.parseInt(this.getXml().getAttribute("xfId"));
		} catch (Exception e) {
		}
		return null;
	}

	public void setXFId(int xfId) {
		assertNotLocked();
		try {
			this.getXml().addAttributeNotStrict("xfId", SH.toString(xfId));
		} catch (Exception e) {
		}
	}

	public String getUID() {
		try {
			return this.getXml().getAttribute("xr:uid");
		} catch (Exception e) {
		}
		return null;
	}

	public void setUID(String uid) {
		assertNotLocked();
		try {
			this.getXml().addAttributeNotStrict("xr:uid", uid);
		} catch (Exception e) {
		}
	}

	public String getBuiltInId() {
		try {
			return this.getXml().getAttribute("builtinId");
		} catch (Exception e) {
		}
		return null;
	}

	public void setBuiltInId(String id) {
		assertNotLocked();
		try {
			this.getXml().addAttributeNotStrict("builtinId", id);
		} catch (Exception e) {
		}
	}

	@Override
	public void clear() {
		LockedException.assertNotLocked(this);
		this.setXml(createDefaultXml());
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
		SpreadSheetResource_CellStyle other = (SpreadSheetResource_CellStyle) obj;
		return OH.eq(this.getXml(), other.getXml());
	}

}
