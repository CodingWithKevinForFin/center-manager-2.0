package com.f1.office.spreadsheet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.Table;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.xml.XmlElement;

public class SpreadSheetWorkbook {

	final private SpreadSheetWorkbookStyle style = new SpreadSheetWorkbookStyle();
	final private Map<String, SpreadSheetWorksheetBase> sheets = new LinkedHashMap<String, SpreadSheetWorksheetBase>();
	final private String username;

	final private SpreadSheetResourcePool<SpreadSheetResource_SharedString> sharedStrings = new SpreadSheetResourcePool<SpreadSheetResource_SharedString>() {
		@Override
		public SpreadSheetResource_SharedString nw() {
			return new SpreadSheetResource_SharedString();
		}
	};

	final private HashMap<String, String> namedRanges = new HashMap<String, String>();

	final private HashSet<String> hiddenSheets = new HashSet<String>();

	private XmlElement appXML, coreXML, customXML, contentTypeXML, calcChainXML, workbookXML, sheetMetadataXML;

	public void setWorkbookXML(final XmlElement e) {
		this.workbookXML = e;
	}

	public XmlElement getWorkbookXML() {
		return this.workbookXML;
	}

	public Map<Integer, Integer> parseExistingStyleXML(final XmlElement e) {
		return this.style.parseExistingStyleXML(e);
	}

	public void setAppXML(final XmlElement e) {
		this.appXML = e;
	}
	public XmlElement getAppXML() {
		return this.appXML;
	}

	public void setCoreXML(final XmlElement e) {
		this.coreXML = e;
	}
	public XmlElement getCoreXML() {
		return this.coreXML;
	}

	public void setCustomXML(final XmlElement e) {
		this.customXML = e;
	}
	public XmlElement getCustomXML() {
		return this.customXML;
	}

	public void setContentTypeXML(final XmlElement e) {
		this.contentTypeXML = e;
	}
	public XmlElement getContentTypeXML() {
		return this.contentTypeXML;
	}

	public void setCalcChainXML(final XmlElement e) {
		this.calcChainXML = e;
	}

	public XmlElement getCalcChainXML() {
		return this.calcChainXML;
	}

	public void setSheetMetadataXML(final XmlElement e) {
		this.sheetMetadataXML = e;
	}

	public XmlElement getSheetMetadataXML() {
		return this.sheetMetadataXML;
	}

	final private LinkedHashMap<Integer, XmlElement> themes = new LinkedHashMap<Integer, XmlElement>();

	final private LinkedHashMap<String, XmlElement> worksheetRelationships = new LinkedHashMap<String, XmlElement>();

	final private LinkedHashMap<Integer, Tuple2<XmlElement, XmlElement>> externalLinks = new LinkedHashMap<Integer, Tuple2<XmlElement, XmlElement>>();

	final private LinkedHashMap<Integer, XmlElement> customXMLRelationships = new LinkedHashMap<Integer, XmlElement>();

	final private LinkedHashMap<Integer, XmlElement> pivotTables = new LinkedHashMap<Integer, XmlElement>();

	final private List<Tuple2<String, byte[]>> miscFileList = new ArrayList<Tuple2<String, byte[]>>();

	public LinkedHashMap<Integer, XmlElement> getThemes() {
		return this.themes;
	}

	public int getThemeCount() {
		return themes.size();
	}

	public int addTheme(final XmlElement theme) {
		int id = themes.size() + 1;
		themes.put(id, theme);
		return id;
	}

	public LinkedHashMap<String, XmlElement> getWorksheetRelationships() {
		return this.worksheetRelationships;
	}

	public void addWorksheetRelationships(String sheetName, XmlElement e) {
		this.worksheetRelationships.put(sheetName, e);
	}

	public LinkedHashMap<Integer, XmlElement> getPivotTables() {
		return this.pivotTables;
	}

	public void addPivotTable(Integer id, XmlElement file) {
		this.pivotTables.put(id, file);
	}

	public LinkedHashMap<Integer, Tuple2<XmlElement, XmlElement>> getExternalLinks() {
		return this.externalLinks;
	}

	public void addExternalLink(Integer sheetNumber, XmlElement file, XmlElement relationship) {
		this.externalLinks.put(sheetNumber, new Tuple2<XmlElement, XmlElement>(file, relationship));
	}

	public LinkedHashMap<Integer, XmlElement> getCustomXMLRelationships() {
		return this.customXMLRelationships;
	}

	public void addCustomXMLRelationship(Integer relNumber, XmlElement file) {
		this.customXMLRelationships.put(relNumber, file);
	}

	public SpreadSheetResource_SharedString getSharedString(String s) {
		SpreadSheetResource_SharedString sharedString = sharedStrings.borrowTmp();
		sharedString.setString(s);
		return sharedStrings.share(sharedString);
	}

	public SpreadSheetResource_SharedString getSharedString(XmlElement e) {
		SpreadSheetResource_SharedString sharedString = sharedStrings.borrowTmp();
		sharedString.setXml(e);
		return sharedStrings.share(sharedString);
	}

	public SpreadSheetResourcePool<SpreadSheetResource_SharedString> getSharedStrings() {
		return this.sharedStrings;
	}

	public String getSharedString(Integer id) {
		final SpreadSheetResource_SharedString ss = this.sharedStrings.getById(id);
		return ss != null ? ss.getString() : null;
	}

	public void addMiscFile(String filename, byte[] bytes) {
		this.miscFileList.add(new Tuple2<String, byte[]>(filename, bytes));
	}

	public List<Tuple2<String, byte[]>> getMiscFileList() {
		return this.miscFileList;
	}

	public SpreadSheetWorkbook(String string) {
		this.username = string;
	}

	private int nextId = 1;
	private SpreadSheetExporter exporter = new SpreadSheetExporter(this);
	private long timezoneOffset;

	public SpreadSheetWorksheet addSheet(String name, Table table, final BasicCalcTypes underlyingTypes) {
		name = SH.substring(name, 0, 28);//leave 2 for digits
		SpreadSheetWorksheet r = new SpreadSheetWorksheet(this, nextId, nextId, name, table, underlyingTypes);
		sheets.put(r.getTitle(), r);
		nextId++;
		return r;
	}

	public SpreadSheetFlexsheet addFlexSheet(String name) {
		SpreadSheetFlexsheet fs = new SpreadSheetFlexsheet(this, nextId, name);
		sheets.put(fs.getTitle(), fs);
		nextId++;
		return fs;
	}

	public SpreadSheetFlexsheet addFlexSheet(String name, final XmlElement e, final Map<Integer, Integer> sharedStringsMapping, final Map<Integer, Integer> stylesMapping) {
		SpreadSheetFlexsheet fs = new SpreadSheetFlexsheet(this, nextId, name, e, sharedStringsMapping, stylesMapping);
		sheets.put(fs.getTitle(), fs);
		nextId++;
		return fs;
	}

	public SpreadSheetFlexsheet copyFlexSheet(String name, final SpreadSheetFlexsheet copy) {
		SpreadSheetFlexsheet fs = new SpreadSheetFlexsheet(this, nextId, name, copy);
		sheets.put(fs.getTitle(), fs);
		nextId++;
		return fs;
	}

	public SpreadSheetWorkbookStyle getWorkbookStyle() {
		return this.style;
	}

	public String getUniqueSheetName(String name) {
		return SH.getNextId(name, sheets.keySet());
	}

	public SpreadSheetExporter getExporter() {
		return this.exporter;
	}

	public Map<String, SpreadSheetWorksheetBase> getSheets() {
		return this.sheets;
	}

	public String getUserName() {
		return this.username;
	}

	public static void main(String[] table) throws IOException {

		SpreadSheetWorkbook sse = new SpreadSheetWorkbook("roberto");
		Table t = new BasicTable(new String[] { "test", "this" });
		t.setTitle("My Title");
		t.getRows().addRow("Test", "This");
		t.getRows().addRow("Test", "That");

		sse.addSheet("sheet1", t, new BasicCalcTypes());
		sse.addSheet("sheet2", t, new BasicCalcTypes());
		sse.addSheet("sheet3", t, new BasicCalcTypes());
		sse.getExporter().write(new File("/home/share/temp/xlstest/f1.xlsx"));

	}

	public long getTimezoneOffset() {
		return this.timezoneOffset;
	}

	public void setTimezoneOffset(long timezoneOffset) {
		this.timezoneOffset = timezoneOffset;
	}

	public void addNamedRange(final String rangeName, final String rangeValues) {
		this.namedRanges.put(rangeName, rangeValues);
	}

	public String getNamedRange(final String namedRange) {
		return this.namedRanges.get(namedRange);
	}

	public void hideSpreadSheet(String sheetName) {
		this.hiddenSheets.add(sheetName);
	}

	public void showSpreadSheet(String sheetName) {
		this.hiddenSheets.remove(sheetName);
	}

	public void deleteSpreadSheet(String sheetName) {
		this.sheets.remove(sheetName);
	}

	public boolean isSheetHidden(String sheetName) {
		return this.hiddenSheets.contains(sheetName);
	}

	public void renameSpreadSheet(String sheetName, String newSheetName) {
		newSheetName = SpreadSheetUtils.getSheetName(this, newSheetName);
		if (this.hiddenSheets.contains(sheetName)) {
			this.hiddenSheets.remove(sheetName);
			this.hiddenSheets.add(newSheetName);
		}

		if (this.sheets.containsKey(sheetName)) {
			final SpreadSheetWorksheetBase base = this.sheets.get(sheetName);
			base.setTitle(newSheetName);
			this.sheets.remove(sheetName);
			this.sheets.put(newSheetName, base);
		}
	}
}
