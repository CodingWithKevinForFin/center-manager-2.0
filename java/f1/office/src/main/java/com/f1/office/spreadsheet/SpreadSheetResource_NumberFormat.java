package com.f1.office.spreadsheet;

import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.xml.XmlElement;

public class SpreadSheetResource_NumberFormat extends SpreadSheetResource {

	final public static short TYPE_STRING = 0;
	final public static short TYPE_NUMBER = 201;
	final public static short TYPE_DATE = 203;
	final public static short TYPE_TIME = 204;
	final public static short TYPE_DATETIME = 205;
	final public static short TYPE_DATETIME_MILLI = 206;
	final public static short TYPE_DATETIME_MICRO = 207;
	final public static short TYPE_DATETIME_NANO = 208;
	final public static short TYPE_TIME_MILLI = 209;
	final public static short TYPE_TIME_MICRO = 210;
	final public static short TYPE_TIME_NANO = 211;
	final public static short TYPE_NUMBER_DEC1 = 212;
	final public static short TYPE_NUMBER_DEC2 = 213;
	final public static short TYPE_NUMBER_DEC3 = 214;
	final public static short TYPE_NUMBER_DEC4 = 215;
	final public static short TYPE_NUMBER_DEC5 = 216;
	final public static short TYPE_NUMBER_DEC6 = 217;
	final public static short TYPE_NUMBER_DEC7 = 218;
	final public static short TYPE_NUMBER_DEC8 = 219;

	private static final String XML_NAME = "numFmt";

	public SpreadSheetResource_NumberFormat() {
		this.setXml(createDefaultXml());
	}

	public SpreadSheetResource_NumberFormat(XmlElement e) {
		this.setXml(e);
	}

	private XmlElement createDefaultXml() {
		return new XmlElement(XML_NAME).addAttribute("formatCode", "").addAttribute("numFmtId", "0");
	}

	public Integer getNumberFormatId() {
		try {
			return SH.parseInt(this.getXml().getAttribute("numFmtId"));
		} catch (Exception e) {
		}
		return null;
	}

	public void setNumberFormatId(String numFmtId) {
		assertNotLocked();
		try {
			this.getXml().addAttributeNotStrict("numFmtId", numFmtId);
		} catch (Exception e) {
		}
	}

	public String getFormatCode() {
		try {
			return this.getXml().getAttribute("formatCode");
		} catch (Exception e) {
		}
		return null;
	}

	public void setFormatCode(String formatCode) {
		assertNotLocked();
		try {
			this.getXml().addAttributeNotStrict("formatCode", formatCode);
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
		SpreadSheetResource_NumberFormat other = (SpreadSheetResource_NumberFormat) obj;
		return OH.eq(this.getXml(), other.getXml());
	}

	@Override
	public void clear() {
		this.setXml(createDefaultXml());
	}

	public static short getDecimalFormatType(int precision) {
		switch (precision) {
			case 0:
				return TYPE_NUMBER;
			case 1:
				return TYPE_NUMBER_DEC1;
			case 2:
				return TYPE_NUMBER_DEC2;
			case 3:
				return TYPE_NUMBER_DEC3;
			case 4:
				return TYPE_NUMBER_DEC4;
			case 5:
				return TYPE_NUMBER_DEC5;
			case 6:
				return TYPE_NUMBER_DEC6;
			case 7:
				return TYPE_NUMBER_DEC7;
			case 8:
				return TYPE_NUMBER_DEC8;
			default:
				return TYPE_NUMBER;
		}
	}

	public static String getFormatCode(short format) {
		switch (format) {
			case TYPE_NUMBER:
				return "#,##0";
			case TYPE_NUMBER_DEC1:
				return "#,##0.0";
			case TYPE_NUMBER_DEC2:
				return "#,##0.00";
			case TYPE_NUMBER_DEC3:
				return "#,##0.000";
			case TYPE_NUMBER_DEC4:
				return "#,##0.0000";
			case TYPE_NUMBER_DEC5:
				return "#,##0.00000";
			case TYPE_NUMBER_DEC6:
				return "#,##0.000000";
			case TYPE_NUMBER_DEC7:
				return "#,##0.0000000";
			case TYPE_NUMBER_DEC8:
				return "#,##0.00000000";
			case TYPE_DATE:
				return "m/d/yyyy";
			case TYPE_TIME:
				return "hh:mm:ss";
			case TYPE_TIME_MILLI:
				return "hh:mm:ss.000";
			case TYPE_TIME_MICRO:
				return "hh:mm:ss.000";
			case TYPE_TIME_NANO:
				return "hh:mm:ss.000";
			case TYPE_DATETIME:
				return "m/d/yyyy hh:mm:ss";
			case TYPE_DATETIME_MILLI:
				return "m/d/yyyy hh:mm:ss.000";
			case TYPE_DATETIME_MICRO:
				return "m/d/yyyy hh:mm:ss.000";
			case TYPE_DATETIME_NANO:
				return "m/d/yyyy hh:mm:ss.000";
			case TYPE_STRING:
				return null;
			default:
				throw new UnsupportedOperationException("Unknown format: " + format);
		}
	}

}
