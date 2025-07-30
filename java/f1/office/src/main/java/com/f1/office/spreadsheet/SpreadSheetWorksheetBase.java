package com.f1.office.spreadsheet;

import com.f1.utils.SH;

public class SpreadSheetWorksheetBase {

	String name;
	private int sheetId;
	private String sheetRid;

	public SpreadSheetWorksheetBase(SpreadSheetWorkbook workbook, int sheetId, String name) {
		this.name = SpreadSheetUtils.getSheetName(workbook, name); 
		this.sheetId = sheetId;
		this.sheetRid = "rId" + sheetId;
	}
	
	public String getSheetFileName() {
		return "worksheets/sheet" + sheetId + ".xml";
	}
	public String getSheetRelFileName() {
		return "worksheets/_rels/sheet" + sheetId + ".xml.rels";
	}
	
	public String getSheetRid() {
		return sheetRid;
	}
	
	public String getTitle() {
		return name;
	}
	
	public void setTitle(final String title) {
		this.name = title;
	}
	public String getSheetId() {
		return SH.toString(sheetId);
	}
}
