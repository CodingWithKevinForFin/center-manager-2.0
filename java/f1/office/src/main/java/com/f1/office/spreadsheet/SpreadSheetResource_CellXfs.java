package com.f1.office.spreadsheet;

import com.f1.base.LockedException;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.xml.XmlElement;

public class SpreadSheetResource_CellXfs extends SpreadSheetResource {

	private static final String XML_NAME = "xf";

	public SpreadSheetResource_CellXfs() {
		this.setXml(createDefaultXml());
	}

	public SpreadSheetResource_CellXfs(XmlElement e) {
		this.setXml(e);
	}

	private XmlElement createDefaultXml() {
		XmlElement r = new XmlElement(XML_NAME);
		r.addAttribute("numFmtId", "0").addAttribute("fontId", "0").addAttribute("fillId", "0").addAttribute("borderId", "0");
		return r;
	}

	public Integer getFillId() {
		try {
			return SH.parseInt(this.getXml().getAttribute("fillId"));
		} catch (Exception e) {
		}
		return null;
	}
	public void setFill(int id) {
		assertNotLocked();
		try {
			this.getXml().addAttributeNotStrict("fillId", SH.toString(id));
			if (id == 0)
				this.getXml().removeAttribute("applyFill");
			else
				this.getXml().addAttributeNotStrict("applyFill", "1");
		} catch (Exception e) {
		}
	}

	public void setFill(int id, boolean handleApply) {
		assertNotLocked();
		try {
			this.getXml().addAttributeNotStrict("fillId", SH.toString(id));
			if (handleApply) {
				if (id == 0)
					this.getXml().removeAttribute("applyFill");
				else
					this.getXml().addAttributeNotStrict("applyFill", "1");
			}
		} catch (Exception e) {
		}
	}

	public Integer getNumFmtId() {
		try {
			return SH.parseInt(this.getXml().getAttribute("numFmtId"));
		} catch (Exception e) {
		}
		return null;
	}

	public void setNumFmt(int numFmt) {
		assertNotLocked();
		try {
			this.getXml().addAttributeNotStrict("numFmtId", SH.toString(numFmt));
			if (numFmt == 0)
				this.getXml().removeAttribute("applyNumberFormat");
			else
				this.getXml().addAttributeNotStrict("applyNumberFormat", "1");
		} catch (Exception e) {
		}
	}

	public void setNumFmt(int numFmt, boolean handleApply) {
		assertNotLocked();
		try {
			this.getXml().addAttributeNotStrict("numFmtId", SH.toString(numFmt));
			if (handleApply) {
				if (numFmt == 0)
					this.getXml().removeAttribute("applyNumberFormat");
				else
					this.getXml().addAttributeNotStrict("applyNumberFormat", "1");
			}
		} catch (Exception e) {
		}
	}

	public Integer getFontId() {
		try {
			return SH.parseInt(this.getXml().getAttribute("fontId"));
		} catch (Exception e) {
		}
		return null;
	}

	public void setFont(int fontId) {
		assertNotLocked();
		try {
			this.getXml().addAttributeNotStrict("fontId", SH.toString(fontId));
			if (fontId == 0)
				this.getXml().removeAttribute("applyFont");
			else
				this.getXml().addAttributeNotStrict("applyFont", "1");
		} catch (Exception e) {
		}
	}

	public void setFont(int fontId, boolean handleApply) {
		assertNotLocked();
		try {
			this.getXml().addAttributeNotStrict("fontId", SH.toString(fontId));
			if (handleApply) {
				if (fontId == 0)
					this.getXml().removeAttribute("applyFont");
				else
					this.getXml().addAttributeNotStrict("applyFont", "1");
			}
		} catch (Exception e) {
		}
	}

	public Integer getBorderId() {
		try {
			return SH.parseInt(this.getXml().getAttribute("borderId"));
		} catch (Exception e) {
		}
		return null;
	}

	public void setBorder(int borderId) {
		assertNotLocked();
		try {
			this.getXml().addAttributeNotStrict("borderId", SH.toString(borderId));
			if (borderId == 0)
				this.getXml().removeAttribute("applyBorder");
			else
				this.getXml().addAttributeNotStrict("applyBorder", "1");
		} catch (Exception e) {
		}
	}

	public void setBorder(int borderId, boolean handleApply) {
		assertNotLocked();
		try {
			this.getXml().addAttributeNotStrict("borderId", SH.toString(borderId));
			if (handleApply) {
				if (borderId == 0)
					this.getXml().removeAttribute("applyBorder");
				else
					this.getXml().addAttributeNotStrict("applyBorder", "1");
			}
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
		SpreadSheetResource_CellXfs other = (SpreadSheetResource_CellXfs) obj;
		return OH.eq(this.getXml(), other.getXml());
	}

}
