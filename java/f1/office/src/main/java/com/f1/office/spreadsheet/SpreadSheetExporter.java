package com.f1.office.spreadsheet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.office.spreadsheet.SpreadSheetFlexsheet.CellContent;
import com.f1.office.spreadsheet.SpreadSheetFlexsheet.ExcelDimensions;
import com.f1.utils.CH;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.XlsxHelper;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.xml.XmlElement;
import com.f1.utils.xml.XmlNode;
import com.f1.utils.xml.XmlParser;
import com.f1.utils.xml.XmlRawText;
import com.f1.utils.xml.XmlText;

public class SpreadSheetExporter {
	//	final static private Logger log = LH.get();

	//	private static final String FILL_STYLE_SOLID = "solid";
	private static final String TYPE_THEME = "officeDocument/2006/relationships/theme";
	private static final String TYPE_STYLES = "officeDocument/2006/relationships/styles";
	private static final String TYPE_SHARED_STRINGS = "officeDocument/2006/relationships/sharedStrings";
	private static final String TYPE_WORKSHEET = "officeDocument/2006/relationships/worksheet";
	private static final String TYPE_EXTERNAL = "officeDocument/2006/relationships/externalLink";
	private static final String TYPE_PIVOTCACHE = "officeDocument/2006/relationships/pivotCacheDefinition";
	private static final String TYPE_TABLE = "officeDocument/2006/relationships/table";
	private static final String TYPE_OFFICE_DOCUMENT = "officeDocument/2006/relationships/officeDocument";
	private static final String TYPE_CORE_PROPERTIES = "package/2006/relationships/metadata/core-properties";
	private static final String TYPE_EXTENDED_PROPERTIES = "officeDocument/2006/relationships/extended-properties";
	private static final String TYPE_CUSTOM_PROPERTIES = "officeDocument/2006/relationships/custom-properties";
	private static final String TYPE_CALCCHAIN = "officeDocument/2006/relationships/calcChain";
	private static final String TYPE_SHEET_METADATA = "officeDocument/2006/relationships/sheetMetadata";
	private static final String TYPE_CUSTOM_XML = "officeDocument/2006/relationships/customXml";

	private static final String EXTENSION_XML = "xml";
	private static final String EXTENSION_RELS = "rels";

	private static final String URL_CONTENT_TYPES = "http://schemas.openxmlformats.org/package/2006/content-types";
	private static final String URL_OFFICE_EXTENDED_PROPERTIES = "http://schemas.openxmlformats.org/officeDocument/2006/extended-properties";
	private static final String URL_OFFICE_DOCUMENT_YPES = "http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes";
	private static final String URL_MAIN = "http://schemas.openxmlformats.org/spreadsheetml/2006/main";
	private static final String URL_X14AC = "http://schemas.microsoft.com/office/spreadsheetml/2009/9/ac";
	private static final String URL_X16R2 = "http://schemas.microsoft.com/office/spreadsheetml/2015/02/main";
	private static final String URL_XR = "http://schemas.microsoft.com/office/spreadsheetml/2014/revision";
	private static final String URL_MARKUP_COMPATIBILITY = "http://schemas.openxmlformats.org/markup-compatibility/2006";
	private static final String URL_OFFICE_RELATIONSHIPS = "http://schemas.openxmlformats.org/officeDocument/2006/relationships";
	private static final String URL_RELATIONSHIPS = "http://schemas.openxmlformats.org/package/2006/relationships";
	private static final String URL_OPEN_XML_FORMATS = "http://schemas.openxmlformats.org/";
	private static final String URL_CORE_PROPERTIES = "http://schemas.openxmlformats.org/package/2006/metadata/core-properties";
	private static final String URL_DC_ELEMENTS_1_1 = "http://purl.org/dc/elements/1.1/";
	private static final String URL_DC_TERMS = "http://purl.org/dc/terms/";
	private static final String URL_DC_DCMITYPE = "http://purl.org/dc/dcmitype/";
	private static final String URL_XML_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";

	private static final String XMLNS_X14AC = "xmlns:x14ac";
	private static final String XMLNS_X16R2 = "xmlns:x16r2";
	private static final String XMLNS_XR = "xmlns:xr";
	private static final String XMLNS = "xmlns";
	private static final String XMLNS_R = "xmlns:r";
	private static final String XMLNS_MC = "xmlns:mc";
	private static final String XMLNS_VT = "xmlns:vt";

	private static final String CP_CORE_PROPERTIES = "cp:coreProperties";
	private static final String XMLNS_CP = "xmlns:cp";
	private static final String XMLNS_DC = "xmlns:dc";
	private static final String XMLNS_DCTERMS = "xmlns:dcterms";
	private static final String XMLNS_DCMITYPE = "xmlns:dcmitype";
	private static final String XMLNS_XSI = "xmlns:xsi";
	private static final String DC_CREATOR = "dc:creator";
	private static final String CP_LAST_MODIFIED_BY = "cp:lastModifiedBy";
	private static final String DCTERMS_CREATED = "dcterms:created";
	private static final String XSI_TYPE = "xsi:type";
	private static final String DCTERMS_MODIFIED = "dcterms:modified";
	//	private static final String SI = "si";
	private static final String SST = "sst";
	private static final String RELATIONSHIPS = "Relationships";
	private static final String TYPES = "Types";
	private static final String RELATIONSHIP = "Relationship";
	private static final String EXTENSION = "Extension";
	private static final String PARTNAME = "PartName";
	private static final String CONTENTTYPE = "ContentType";
	private static final String OVERRIDE = "Override";
	private static final String DEFAULT = "Default";
	private static final String TARGET = "Target";
	private static final String TYPE = "Type";
	private static final String SHEET_VIEWS = "sheetViews";
	private static final String MC_IGNORABLE = "mc:Ignorable";
	//	private static final String X14AC_DY_DESCENT = "x14ac:dyDescent";
	private static final String SHEET_VIEW = "sheetView";
	//	private static final String SHEET_FORMAT_PR = "sheetFormatPr";
	private static final String SHEET_DATA = "sheetData";
	//	private static final String DIMENSION = "dimension";
	private static final String CALC_PR = "calcPr";
	private static final String CALC_LOAD = "fullCalcOnLoad";
	private static final String SHEET = "sheet";
	//	private static final String RIGHT = "right";
	//	private static final String TOP = "top";
	//	private static final String BOTTOM = "bottom";
	//	private static final String HEADER = "header";
	//	private static final String FOOTER = "footer";
	//	private static final String PAGE_MARGINS = "pageMargins";
	private static final String BOOK_VIEWS = "bookViews";
	private static final String WORKBOOK_VIEW = "workbookView";
	private static final String WORKBOOK_PR = "workbookPr";
	private static final String ROW2 = "row";
	//	private static final String SELECTION = "selection";
	//	private static final String TAB_SELECTED = "tabSelected";
	private static final String WORKSHEET = "worksheet";
	private static final String PROPERTIES = "Properties";
	private static final String R_ID = "r:id";
	private static final String SHEET_ID = "sheetId";
	private static final String NAME = "name";
	//	private static final String SQREF = "sqref";
	//	private static final String LEFT = "left";
	//	private static final String REF = "ref";
	//	private static final String SPANS = "spans";
	//	private static final String ACTIVE_CELL = "activeCell";
	//	private static final String DEFAULT_ROW_HEIGHT = "defaultRowHeight";
	private static final String WORKBOOK = "workbook";
	private static final String FILE_VERSION = "fileVersion";
	private static final String APP_NAME = "appName";
	private static final String DEFAULT_THEME_VERSION = "defaultThemeVersion";
	private static final String RUP_BUILD = "rupBuild";
	private static final String LOWEST_EDITED = "lowestEdited";
	private static final String LAST_EDITED = "lastEdited";
	private static final String Y_WINDOW = "yWindow";
	private static final String WINDOW_WIDTH = "windowWidth";
	private static final String WINDOW_HEIGHT = "windowHeight";
	private static final String X_WINDOW = "xWindow";
	private static final String SHEETS = "sheets";
	private static final String CALC_ID = "calcId";
	//	private static final String DOC_SECURITY = "DocSecurity";
	//	private static final String APPLICATION = "Application";
	//	private static final String SCALE_CROP = "ScaleCrop";
	//	private static final String LINKS_UP_TO_DATE = "LinksUpToDate";
	//	private static final String SHARED_DOC = "SharedDoc";
	//	private static final String HYPERLINKS_CHANGED = "HyperlinksChanged";
	//	private static final String APP_VERSION = "AppVersion";
	//	private static final String TITLES_OF_PARTS = "TitlesOfParts";
	//	private static final String HEADING_PAIRS = "HeadingPairs";
	//	private static final String VT_LPSTR = "vt:lpstr";
	//	private static final String BASE_TYPE = "baseType";
	//	private static final String VT_VECTOR = "vt:vector";
	//	private static final String VT_VARIANT = "vt:variant";
	private static final String UNIQUE_COUNT = "uniqueCount";
	private static final String COUNT = "count";

	private static final String CONTENTTYPE_XML = "application/xml";
	private static final String CONTENTTYPE_RELS = "application/vnd.openxmlformats-package.relationships+xml";
	private static final String CONTENTTYPE_WORKBOOK = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml";
	private static final String CONTENTTYPE_EXTENDED_PROPERTIES = "application/vnd.openxmlformats-officedocument.extended-properties+xml";
	private static final String CONTENTTYPE_CORE_PROPERTIES = "application/vnd.openxmlformats-package.core-properties+xml";
	private static final String CONTENTTYPE_SHARED_STRINGS = "application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml";
	private static final String CONTENTTYPE_STYLES = "application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml";
	//	private static final String CONTENTTYPE_THEME = "application/vnd.openxmlformats-officedocument.theme+xml";
	private static final String CONTENTTYPE_WORKSHEET = "application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml";
	private static final String CONTENTTYPE_TABLE = "application/vnd.openxmlformats-officedocument.spreadsheetml.table+xml";

	public static final byte FORMAT_PERCENT = 1;
	public static final byte FORMAT_NUMBER = 2;
	public static final byte FORMAT_STRING = 3;
	private static final double COL_WIDTH_RATIO = 215 / 30d;

	private SpreadSheetWorkbook workbook;

	public SpreadSheetExporter(SpreadSheetWorkbook wb) {
		this.workbook = wb;
	}

	public void write(File fname) throws IOException {
		IOH.writeData(fname, toXlsx());
	}

	public byte[] toXlsx() {
		Map<String, XmlElement> files = new LinkedHashMap<String, XmlElement>();
		files.put("[Content_Types].xml", toContentTypes());
		for (SpreadSheetWorksheetBase s : this.workbook.getSheets().values()) {
			if (s instanceof SpreadSheetWorksheet) {
				SpreadSheetWorksheet ws = (SpreadSheetWorksheet) s;
				files.put("xl/" + ws.getSheetFileName(), toSheet(ws));
				files.put("xl/" + ws.getSheetRelFileName(), toSheetRels(ws));
				files.put("xl/" + ws.getTableFileName(), toTable(ws));
			} else if (s instanceof SpreadSheetFlexsheet) {
				SpreadSheetFlexsheet fs = (SpreadSheetFlexsheet) s;
				files.put("xl/" + fs.getSheetFileName(), toSheet(fs));
			} else {
				throw new UnsupportedOperationException("Unsupported worksheet type detected");
			}
		}

		for (final Map.Entry<String, XmlElement> e : this.workbook.getWorksheetRelationships().entrySet()) {
			files.put("xl/worksheets/_rels/" + e.getKey(), e.getValue());
		}

		files.put("xl/_rels/workbook.xml.rels", toWorkbookRels());
		files.put("xl/workbook.xml", toWorkBook());
		files.put("xl/calcChain.xml", toCalcChain());
		files.put("docProps/core.xml", toCore());
		files.put("docProps/app.xml", toApp());

		if (this.workbook.getCustomXML() != null)
			files.put("docProps/custom.xml", this.workbook.getCustomXML());

		if (this.workbook.getThemeCount() != 0) {
			for (Map.Entry<Integer, XmlElement> theme : this.workbook.getThemes().entrySet())
				files.put("xl/theme/theme" + theme.getKey() + ".xml", theme.getValue());
		}

		files.put("_rels/.rels", toRels());
		files.put("xl/sharedStrings.xml", toSharedStrings());
		files.put("xl/styles.xml", toStyles());
		FastByteArrayDataOutputStream r = new FastByteArrayDataOutputStream();
		ZipOutputStream out = new ZipOutputStream(r);
		FastBufferedOutputStream buf = new FastBufferedOutputStream(out, 10000);
		final byte[] bytes;
		StringBuilder t = new StringBuilder();
		try {
			for (Entry<String, XmlElement> e : files.entrySet()) {
				out.putNextEntry(new ZipEntry(e.getKey()));

				t.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
				e.getValue().toString(t, XmlNode.VERSION_XML10);
				SH.writeUTF(t, buf);
				buf.flush();
				out.closeEntry();
				if (t.length() > 10000)
					t = new StringBuilder();
				else
					t.setLength(0);
			}
			//Add in extra files
			for (final Tuple2<String, byte[]> misc : this.workbook.getMiscFileList()) {
				out.putNextEntry(new ZipEntry(misc.getKey()));
				buf.write(misc.getValue());
				buf.flush();
				out.closeEntry();
			}

			out.flush();
			out.close();
			bytes = r.toByteArray();
		} catch (IOException e1) {
			throw OH.toRuntime(e1);
		}
		return bytes;
	}

	private XmlElement toCalcChain() {
		XmlElement existing = this.workbook.getCalcChainXML();
		if (existing != null)
			return existing;
		XmlElement element = new XmlElement("calcChain").addAttribute(XMLNS, URL_MAIN);
		return element;
	}

	private XmlElement toStyles() {
		XmlElement element = new XmlElement("");
		XmlElement styleSheet = new XmlElement("styleSheet");
		element.addChild(styleSheet);
		styleSheet.addAttribute(XMLNS, URL_MAIN);
		styleSheet.addAttribute(XMLNS_MC, URL_MARKUP_COMPATIBILITY);
		styleSheet.addAttribute(MC_IGNORABLE, "x14ac x16r2 xr");
		styleSheet.addAttribute(XMLNS_X14AC, URL_X14AC);
		styleSheet.addAttribute(XMLNS_X16R2, URL_X16R2);
		styleSheet.addAttribute(XMLNS_XR, URL_XR);
		this.workbook.getWorkbookStyle().toStyles(styleSheet);
		return element;
	}

	private XmlElement toContentTypes() {
		XmlElement existing = this.workbook.getContentTypeXML();
		if (existing != null)
			return existing;

		XmlElement element = new XmlElement("");
		XmlElement types = new XmlElement(TYPES).addAttribute(XMLNS, URL_CONTENT_TYPES);
		element.addChild(types);
		types.addChild(new XmlElement(DEFAULT).addAttribute(CONTENTTYPE, CONTENTTYPE_RELS).addAttribute(EXTENSION, EXTENSION_RELS));
		types.addChild(new XmlElement(DEFAULT).addAttribute(CONTENTTYPE, CONTENTTYPE_XML).addAttribute(EXTENSION, EXTENSION_XML));
		types.addChild(new XmlElement(OVERRIDE).addAttribute(CONTENTTYPE, CONTENTTYPE_WORKBOOK).addAttribute(PARTNAME, "/xl/workbook.xml"));
		types.addChild(new XmlElement(OVERRIDE).addAttribute(CONTENTTYPE, CONTENTTYPE_STYLES).addAttribute(PARTNAME, "/xl/styles.xml"));
		types.addChild(new XmlElement(OVERRIDE).addAttribute(CONTENTTYPE, CONTENTTYPE_SHARED_STRINGS).addAttribute(PARTNAME, "/xl/sharedStrings.xml"));
		types.addChild(new XmlElement(OVERRIDE).addAttribute(CONTENTTYPE, CONTENTTYPE_CORE_PROPERTIES).addAttribute(PARTNAME, "/docProps/core.xml"));
		types.addChild(new XmlElement(OVERRIDE).addAttribute(CONTENTTYPE, CONTENTTYPE_EXTENDED_PROPERTIES).addAttribute(PARTNAME, "/docProps/app.xml"));
		for (SpreadSheetWorksheetBase sheet : workbook.getSheets().values()) {
			types.addChild(new XmlElement(OVERRIDE).addAttribute(CONTENTTYPE, CONTENTTYPE_WORKSHEET).addAttribute(PARTNAME, "/xl/" + sheet.getSheetFileName()));
			if (sheet instanceof SpreadSheetWorksheet) {
				SpreadSheetWorksheet ws = (SpreadSheetWorksheet) sheet;
				types.addChild(new XmlElement(OVERRIDE).addAttribute(CONTENTTYPE, CONTENTTYPE_TABLE).addAttribute(PARTNAME, "/xl/" + ws.getTableFileName()));
			}
		}
		return element;
	}

	private XmlElement toRels() {
		List<Tuple3<String, String, String>> m = new ArrayList<Tuple3<String, String, String>>();
		m.add(new Tuple3<String, String, String>("docProps/app.xml", TYPE_EXTENDED_PROPERTIES, "rId3"));
		m.add(new Tuple3<String, String, String>("docProps/core.xml", TYPE_CORE_PROPERTIES, "rId2"));
		m.add(new Tuple3<String, String, String>("xl/workbook.xml", TYPE_OFFICE_DOCUMENT, "rId1"));
		if (this.workbook.getCustomXML() != null)
			m.add(new Tuple3<String, String, String>("docProps/custom.xml", TYPE_CUSTOM_PROPERTIES, "rId4"));
		return toRels(m);
	}

	private XmlElement toWorkbookRels() {
		int id = 0;
		List<Tuple3<String, String, String>> m = new ArrayList<Tuple3<String, String, String>>();
		for (SpreadSheetWorksheetBase sheet : workbook.getSheets().values()) {
			m.add(new Tuple3<String, String, String>(sheet.getSheetFileName(), TYPE_WORKSHEET, sheet.getSheetRid()));
			int idVal = SH.parseInt(SH.afterFirst(sheet.getSheetRid(), "rId"));
			id = idVal > id ? idVal : id;
		}

		for (Integer key : this.workbook.getPivotTables().keySet()) {
			++id;
			m.add(new Tuple3<String, String, String>("pivotCache/pivotCacheDefinition" + key + ".xml", TYPE_PIVOTCACHE, "rId" + id));
		}

		for (Integer key : this.workbook.getExternalLinks().keySet()) {
			++id;
			m.add(new Tuple3<String, String, String>("externalLinks/externalLink" + key + ".xml", TYPE_EXTERNAL, "rId" + id));
		}

		LinkedHashMap<Integer, XmlElement> themes = this.workbook.getThemes();
		for (final Map.Entry<Integer, XmlElement> e : themes.entrySet()) {
			++id;
			m.add(new Tuple3<String, String, String>("theme/theme" + e.getKey() + ".xml", TYPE_THEME, "rId" + id));
		}
		m.add(new Tuple3<String, String, String>("sharedStrings.xml", TYPE_SHARED_STRINGS, "rId" + ++id));
		m.add(new Tuple3<String, String, String>("styles.xml", TYPE_STYLES, "rId" + ++id));
		if (this.workbook.getCalcChainXML() != null)
			m.add(new Tuple3<String, String, String>("calcChain.xml", TYPE_CALCCHAIN, "rId" + ++id));
		if (this.workbook.getSheetMetadataXML() != null)
			m.add(new Tuple3<String, String, String>("sheetMetadata.xml", TYPE_SHEET_METADATA, "rId" + ++id));

		for (Integer key : CH.sort(this.workbook.getCustomXMLRelationships().keySet())) {
			++id;
			m.add(new Tuple3<String, String, String>("../customXml/item" + key + ".xml", TYPE_CUSTOM_XML, "rId" + id));
		}

		return toRels(m);
	}
	private XmlElement toSheetRels(SpreadSheetWorksheet sheet) {
		List<Tuple3<String, String, String>> m = new ArrayList<Tuple3<String, String, String>>();
		m.add(new Tuple3<String, String, String>("../" + sheet.getTableFileName(), TYPE_TABLE, sheet.getTableRid()));
		return toRels(m);
	}
	private XmlElement toRels(List<Tuple3<String, String, String>> namesAndTypesIds) {
		XmlElement element = new XmlElement("");
		XmlElement types = new XmlElement(RELATIONSHIPS).addAttributeNotStrict(XMLNS, URL_RELATIONSHIPS);
		element.addChild(types);
		for (Tuple3<String, String, String> e : namesAndTypesIds) {
			types.addChild(new XmlElement(RELATIONSHIP).addAttribute(TARGET, e.getA()).addAttribute(TYPE, URL_OPEN_XML_FORMATS + e.getB()).addAttribute("Id", e.getC()));
		}
		return element;
	}

	private XmlElement toCore() {
		XmlElement existing = this.workbook.getCoreXML();
		if (existing != null)
			return existing;
		XmlElement element = new XmlElement("");
		XmlElement types = new XmlElement(CP_CORE_PROPERTIES);
		element.addChild(types);
		types.addAttribute(XMLNS_CP, URL_CORE_PROPERTIES);
		types.addAttribute(XMLNS_DC, URL_DC_ELEMENTS_1_1);
		types.addAttribute(XMLNS_DCTERMS, URL_DC_TERMS);
		types.addAttribute(XMLNS_DCMITYPE, URL_DC_DCMITYPE);
		types.addAttribute(XMLNS_XSI, URL_XML_SCHEMA_INSTANCE);
		types.addChild(new XmlElement(DC_CREATOR).addChild(new XmlText(this.workbook.getUserName())));
		types.addChild(new XmlElement(CP_LAST_MODIFIED_BY).addChild(new XmlText(this.workbook.getUserName())));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String timestamp = sdf.format(new Date());
		types.addChild(new XmlElement(DCTERMS_CREATED).addAttribute(XSI_TYPE, "dcterms:W3CDTF").addChild(new XmlText(timestamp)));
		types.addChild(new XmlElement(DCTERMS_MODIFIED).addAttribute(XSI_TYPE, "dcterms:W3CDTF").addChild(new XmlText(timestamp)));
		return element;
	}

	//If there is an existing app.xml, use it
	private XmlElement toApp() {
		XmlElement existing = this.workbook.getAppXML();
		if (existing != null)
			return existing;
		XmlElement element = new XmlElement("");
		XmlElement props = new XmlElement(PROPERTIES);
		element.addChild(props);
		props.addAttribute(XMLNS, URL_OFFICE_EXTENDED_PROPERTIES);
		props.addAttribute(XMLNS_VT, URL_OFFICE_DOCUMENT_YPES);
		return element;
	}

	public XmlElement toSharedStrings() {
		XmlElement element = new XmlElement("");
		XmlRawText sst = new XmlRawText(SST, XmlRawText.VERSION_XML10);
		sst.addAttribute(UNIQUE_COUNT, this.workbook.getSharedStrings().getResourcesCount());
		sst.addAttribute(COUNT, this.workbook.getSharedStrings().getResourcesCount());
		sst.addAttribute(XMLNS, URL_MAIN);
		for (SpreadSheetResource_SharedString s : this.workbook.getSharedStrings().getResourcesInOrder()) {
			sst.addChild(s.getXml());
		}
		element.addChild(sst);
		return element;
	}
	public XmlElement toWorkBook() {
		XmlElement existing = this.workbook.getWorkbookXML();
		if (existing != null) {
			//Regenerate sheets elements
			XmlElement sheets = new XmlElement(SHEETS);
			for (SpreadSheetWorksheetBase s : this.workbook.getSheets().values()) {
				XmlElement sheet = new XmlElement(SHEET).addAttribute(R_ID, s.getSheetRid()).addAttribute(SHEET_ID, s.getSheetId()).addAttribute(NAME, s.getTitle());
				if (this.workbook.isSheetHidden(s.getTitle()))
					sheet.addAttribute("state", "hidden");
				sheets.addChild(sheet);
			}
			existing.setFirstElement(sheets);

			//Replace and refresh workbook external links
			if (!this.workbook.getExternalLinks().isEmpty()) {
				XmlElement externalReferences = new XmlElement("externalReferences");
				int count = this.workbook.getSheets().size() + 1;
				for (int i = 0; i < this.workbook.getExternalLinks().size(); ++i) {
					XmlElement element = new XmlElement("externalReference");
					element.addAttribute("r:id", "rId" + count++);
					externalReferences.addChild(element);
				}
				existing.setFirstElement(externalReferences);
			}

			return existing;
		} else {
			XmlElement element = new XmlElement("");
			XmlElement workbook = new XmlElement(WORKBOOK);
			element.addChild(workbook);
			workbook.addAttribute(XMLNS_R, URL_OFFICE_RELATIONSHIPS);
			workbook.addAttribute(XMLNS, URL_MAIN);
			workbook.addChild(
					new XmlElement(FILE_VERSION).addAttribute(RUP_BUILD, "9303").addAttribute(LOWEST_EDITED, "10").addAttribute(LAST_EDITED, "10").addAttribute(APP_NAME, "xl"));
			workbook.addChild(new XmlElement(WORKBOOK_PR).addAttribute(DEFAULT_THEME_VERSION, "124226"));
			XmlElement bookViews = new XmlElement(BOOK_VIEWS);
			bookViews.addChild(new XmlElement(WORKBOOK_VIEW).addAttribute(WINDOW_HEIGHT, "18705").addAttribute(WINDOW_WIDTH, "37395").addAttribute(Y_WINDOW, "45")
					.addAttribute(X_WINDOW, "480"));
			workbook.addChild(bookViews);
			XmlElement sheets = new XmlElement(SHEETS);
			workbook.addChild(sheets);

			for (SpreadSheetWorksheetBase s : this.workbook.getSheets().values()) {
				XmlElement sheet = new XmlElement(SHEET).addAttribute(R_ID, s.getSheetRid()).addAttribute(SHEET_ID, s.getSheetId()).addAttribute(NAME, s.getTitle());
				if (this.workbook.isSheetHidden(s.getTitle()))
					sheet.addAttribute("state", "hidden");
				sheets.addChild(sheet);
			}
			workbook.addChild(new XmlElement(CALC_PR).addAttribute(CALC_ID, "145621").addAttribute(CALC_LOAD, "1"));
			return element;
		}

	}
	private XmlElement toTable(SpreadSheetWorksheet s) {
		List<Column> columns = s.getTable().getColumns();
		XmlElement element = new XmlElement("");
		XmlElement table = new XmlElement("table");
		element.addChild(table);
		table.addAttribute(XMLNS, URL_MAIN);
		table.addAttribute("totalsRowShown", "0");
		String ref = toCell(0, 0) + ":" + toCell(columns.size() - 1, Math.max(1, s.getTable().getSize()));
		table.addAttribute("ref", ref);
		table.addAttribute("displayName", "Table" + s.getTableId());
		table.addAttribute("name", "Table" + s.getTableId());
		table.addAttribute("id", s.getTableId());
		table.addChild(new XmlElement("autoFilter").addAttribute("ref", ref));
		XmlElement tableColumns = new XmlElement("tableColumns").addAttribute("count", SH.toString(columns.size()));
		int id = 0;
		for (Column column : columns)
			tableColumns.addChild(new XmlElement("tableColumn").addAttribute("name", (String) column.getId()).addAttribute("id", SH.toString(++id)));
		table.addChild(tableColumns);
		table.addChild(new XmlElement("tableStyleInfo").addAttribute("name", "simple").addAttribute("showColumnStripes", "0").addAttribute("showRowStripes", "1")
				.addAttribute("showLastColumn", "0").addAttribute("showFirstColumn", "0"));
		return element;
	}
	private XmlElement toSheet(SpreadSheetWorksheet s) {
		s.parseFormulas();
		XmlElement element = new XmlElement("");
		XmlElement worksheet = new XmlElement(WORKSHEET);
		element.addChild(worksheet);
		worksheet.addAttribute(XMLNS_X14AC, URL_X14AC);
		worksheet.addAttribute(MC_IGNORABLE, "x14ac");
		worksheet.addAttribute(XMLNS_MC, URL_MARKUP_COMPATIBILITY);
		worksheet.addAttribute(XMLNS_R, URL_OFFICE_RELATIONSHIPS);
		worksheet.addAttribute(XMLNS, URL_MAIN);
		XmlElement cols = new XmlElement("cols");

		XmlElement sheetViews = new XmlElement(SHEET_VIEWS);
		XmlElement sheetView = new XmlElement(SHEET_VIEW);
		sheetView.addAttribute("workbookViewId", 0).addAttribute("tabSelected", 1);
		if (s.getPinnedColumnsCount() > 0) {
			sheetView.addChild(new XmlElement("pane").addAttribute("state", "frozen").addAttribute("activePane", "topRight")
					.addAttribute("topLeftCell", toCell(s.getPinnedColumnsCount(), 1)).addAttribute("xSplit", s.getPinnedColumnsCount()).addAttribute("ySplit", 1));
		} else {
			sheetView.addChild(new XmlElement("pane").addAttribute("state", "frozen").addAttribute("activePane", "topRight").addAttribute("topLeftCell", toCell(0, 1))
					.addAttribute("ySplit", 1));
		}
		sheetViews.addChild(sheetView);
		worksheet.addChild(sheetViews);

		worksheet.addChild(cols);
		XmlRawText sheetData = new XmlRawText(SHEET_DATA, XmlRawText.VERSION_XML10);
		Table table = s.getTable();
		int colsCount = table.getColumnsCount();
		int y = 0;
		{
			SpreadSheetResource_CellXfs style = s.getHeaderStyle();
			XmlElement row = new XmlElement(ROW2).addAttribute("r", y + 1);
			for (int x = 0; x < colsCount; x++) {
				XmlElement c = new XmlElement("c").addAttribute("r", toCell(x, y)).addAttribute("t", "s");
				if (style != null && style.getId() > 0)
					c.addAttribute("s", SH.toString(style.getId()));
				String value = (String) table.getColumnAt(x).getId();
				if (value != null) {
					c.addChild(new XmlElement("v").addChild(new XmlText(SH.toString(this.workbook.getSharedString(value).getId()))));
				}
				row.addChild(c);
			}
			sheetData.addChild(row);
			y++;
		}
		for (int x = 0; x < colsCount; x++) {
			int w = s.getColumnWidthPx(x);
			if (w != -1)
				cols.addChild(toColumn(x, w));
		}
		for (Row rw : table.getRows()) {
			sheetData.startChildElement(ROW2);
			sheetData.addAttribute("r", y + 1);
			for (int x = 0; x < colsCount; x++) {
				sheetData.startChildElement("c");
				tmp.setLength(0);
				toCell(x, y, tmp);
				sheetData.addAttribute("r", tmp);
				tmp.setLength(0);
				SpreadSheetResource_CellXfs style = s.getStyle(x, y - 1);
				if (style != null && style.getId() > 0) {
					sheetData.addAttribute("s", SH.toString(style.getId()));
				}

				Object value = rw.getAt(x);
				if (value != null) {
					if (value instanceof String) {
						sheetData.addAttribute("t", "s");
						if (s.parsedFormulas.containsKey(x)) {
							String formula = s.parsedFormulas.get(x);
							formula = SH.replaceAll(formula, '#', (char) ('0' + (y + 1)));
							sheetData.startChildElement("f");
							sheetData.addChild(formula);
							sheetData.endChild();
						}
						sheetData.startChildElement("v");
						sheetData.addChild(this.workbook.getSharedString((String) value).getId());
						sheetData.endChild();
					} else {
						if (value instanceof Number) {
							Number d;
							if (style != null) {
								switch (style.getNumFmtId().shortValue()) {
									case SpreadSheetResource_NumberFormat.TYPE_DATE:
									case SpreadSheetResource_NumberFormat.TYPE_TIME:
									case SpreadSheetResource_NumberFormat.TYPE_TIME_MILLI:
									case SpreadSheetResource_NumberFormat.TYPE_TIME_MICRO:
									case SpreadSheetResource_NumberFormat.TYPE_TIME_NANO:
									case SpreadSheetResource_NumberFormat.TYPE_DATETIME:
									case SpreadSheetResource_NumberFormat.TYPE_DATETIME_MILLI:
									case SpreadSheetResource_NumberFormat.TYPE_DATETIME_MICRO:
									case SpreadSheetResource_NumberFormat.TYPE_DATETIME_NANO:
										d = XlsxHelper.convertUnixToShreadSheetTimestamp(((Number) value).longValue(), this.workbook.getTimezoneOffset());
										break;
									default:
										d = ((Number) value).doubleValue();
								}
							} else {
								d = ((Number) value).doubleValue();
							}

							if (d instanceof Double || d instanceof Float) {
								if (s.parsedFormulas.containsKey(x)) {
									String formula = s.parsedFormulas.get(x);
									formula = SH.replaceAll(formula, '#', Integer.toString(y + 1));
									sheetData.startChildElement("f");
									sheetData.addChild(formula);
									sheetData.endChild();
								}
								double dv = d.doubleValue();
								if (MH.isNumber(dv)) {
									sheetData.startChildElement("v");
									sheetData.addChild(dv);
									sheetData.endChild();
								}
							} else {
								sheetData.startChildElement("v");
								sheetData.addChild(((Number) d).longValue());
								sheetData.endChild();
							}
						} else {
							sheetData.addAttribute("t", "s");
							if (s.parsedFormulas.containsKey(x)) {
								String formula = s.parsedFormulas.get(x);
								formula = SH.replaceAll(formula, '#', (char) ('0' + (y + 1)));
								sheetData.startChildElement("f");
								sheetData.addChild(formula);
								sheetData.endChild();
							}
							sheetData.startChildElement("v");
							sheetData.addChild(this.workbook.getSharedString(SH.toString(value)).getId());
							sheetData.endChild();
						}
					}
				}
				sheetData.endChild();
			}
			sheetData.endChild();
			y++;
		}
		XmlElement tableParts = new XmlElement("tableParts");
		tableParts.addAttribute("count", "1");
		tableParts.addChild(new XmlElement("tablePart").addAttribute("r:id", s.getTableRid()));
		worksheet.addChild(sheetData);
		worksheet.addChild(tableParts);
		return element;
	}
	private XmlElement toSheet(SpreadSheetFlexsheet s) {
		XmlElement element = new XmlElement("");
		String header = s.getHeader();
		//Needed for col style
		if (SH.is(header))
			header = header.replaceAll("style=\"\\d+\"", " style=\"0\"");
		String footer = s.getFooter();
		//All possible row params https://learn.microsoft.com/en-us/dotnet/api/documentformat.openxml.spreadsheet.row?view=openxml-3.0.1#properties
		final List<String> rowStyles = CH.l("outlineLevel", "hidden", "spans", "customFormat", "ht", "customHeight", "collapsed", "thickTop", "thickBot", "ph");

		XmlRawText sheetData = new XmlRawText(SHEET_DATA, XmlRawText.VERSION_XML10);

		List<Map.Entry<ExcelDimensions, CellContent>> sorted = new ArrayList<Map.Entry<ExcelDimensions, CellContent>>(s.getCells().entrySet());
		Collections.sort(sorted, new Comparator<Map.Entry<ExcelDimensions, CellContent>>() {
			public int compare(Map.Entry<ExcelDimensions, CellContent> a, Map.Entry<ExcelDimensions, CellContent> b) {
				return Integer.compare(a.getKey().getSortingIndex(), b.getKey().getSortingIndex());
			}
		});

		HashSet<Integer> rowsWithVals = new HashSet<Integer>();
		for (int i = 0; i < sorted.size(); i++) {
			int Y = sorted.get(i).getKey().getY();
			rowsWithVals.add(Y);
		}

		int currY = -1;
		int tempY = -1;
		for (final Map.Entry<ExcelDimensions, CellContent> e : sorted) {
			final String dimString = e.getKey().dimString;
			Tuple2<Integer, Integer> dim = SpreadSheetUtils.getPositionFromExcelDim(dimString);
			final CellContent cell = e.getValue();
			//New row handling
			if (currY != dim.getB()) {
				if (currY != -1)
					sheetData.endChild();
				currY = dim.getB();

				//if empty rows with grouping exists
				if (currY - tempY > 1) {
					for (int i = tempY; i < currY; i++) {
						if (i == -1) {
							break;
						}
						if (rowsWithVals.contains(i))
							continue;
						try {
							sheetData.startChildElement(ROW2);
							sheetData.addAttribute("r", i);
							for (String attr : rowStyles) {
								String value = s.getRow(String.valueOf(i)).getAttribute(attr);
								if (SH.is(value)) {
									sheetData.addAttribute(attr, value);
								}
							}
							sheetData.endChild();
						} catch (Exception e1) {
						}
					}
				}

				sheetData.startChildElement(ROW2);
				sheetData.addAttribute("r", currY);
				try {
					for (String attr : rowStyles) {
						String value = s.getRow(String.valueOf(currY)).getAttribute(attr);
						if (SH.is(value)) {
							sheetData.addAttribute(attr, value);
						}
					}
				} catch (Exception e1) {
				}
			}

			tempY = currY;
			cell.writeToXmlRawText(sheetData, dimString);
		}

		if (sorted != null && !sorted.isEmpty())
			sheetData.endChild();

		if (SH.is(header) && SH.is(footer)) {
			//Use existing header and footer
			StringBuilder sink = new StringBuilder();
			sink.append(header);
			//Reconstruct XML
			sheetData.toString(sink);
			sink.append(footer);
			XmlParser parser = new XmlParser();
			XmlElement xmlParsed = parser.parseDocument(sink.toString());
			XmlElement dims = new XmlElement("dimension");
			dims.addAttribute("ref", s.getDimensions());
			xmlParsed.setFirstElement(dims);
			element.addChild(xmlParsed);
		} else {
			//Reconstruct sheet
			XmlElement worksheet = new XmlElement(WORKSHEET);
			element.addChild(worksheet);
			worksheet.addAttribute(XMLNS_X14AC, URL_X14AC);
			worksheet.addAttribute(MC_IGNORABLE, "x14ac");
			worksheet.addAttribute(XMLNS_MC, URL_MARKUP_COMPATIBILITY);
			worksheet.addAttribute(XMLNS_R, URL_OFFICE_RELATIONSHIPS);
			worksheet.addAttribute(XMLNS, URL_MAIN);
			XmlElement sheetViews = new XmlElement(SHEET_VIEWS);
			XmlElement sheetView = new XmlElement(SHEET_VIEW);
			sheetView.addAttribute("workbookViewId", 0).addAttribute("tabSelected", 1);
			sheetViews.addChild(sheetView);
			worksheet.addChild(sheetViews);
			worksheet.addChild(sheetData);
		}

		return element;
	}

	private XmlNode toColumn(int location, int width) {
		return new XmlElement("col").addAttribute("customWidth", "1").addAttribute("width", SH.toString((float) (width / COL_WIDTH_RATIO)))
				.addAttribute("min", SH.toString(location + 1)).addAttribute("max", SH.toString(location + 1));
	}

	StringBuilder tmp = new StringBuilder();

	private String toCell(int x, int y) {
		tmp.setLength(0);
		toCell(x, y, tmp);
		return tmp.toString();
	}
	private void toCell(int x, int y, StringBuilder sink) {
		if (x >= 26) {
			if (x >= 702)
				appendLetter((x / 676) - 1, sink);
			appendLetter(((x / 26) - 1), sink);
		}
		appendLetter(x, sink);
		sink.append(y + 1);
	}
	private void appendLetter(int x, StringBuilder sink) {
		sink.append((char) ('A' + (x % 26)));
	}
}
