package com.f1.suite.web.table.impl;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.f1.base.Bytes;
import com.f1.base.Row;
import com.f1.office.spreadsheet.SpreadSheetFlexsheet;
import com.f1.office.spreadsheet.SpreadSheetResource_NumberFormat;
import com.f1.office.spreadsheet.SpreadSheetWorkbook;
import com.f1.office.spreadsheet.SpreadSheetWorksheet;
import com.f1.office.spreadsheet.SpreadSheetWorksheetBase;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.Formatter;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.XlsxHelper;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.formatter.BasicDateFormatter;
import com.f1.utils.formatter.BasicNumberFormatter;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.xml.XmlElement;
import com.f1.utils.xml.XmlNode;

public class SpreadSheetBuilder {
	public static final String DEFAULT_TITLE = "3forge-SpreadSheet";
	private SpreadSheetWorkbook workbook;
	private String title;

	public SpreadSheetBuilder() {
		this.workbook = new SpreadSheetWorkbook(DEFAULT_TITLE);
	}
	public SpreadSheetWorkbook getWorkBook() {
		return workbook;
	}
	public String getTitle() {
		return SH.is(this.title) ? this.title : DEFAULT_TITLE;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public byte[] build() {
		return this.workbook.getExporter().toXlsx();
	}
	public int getSheetsCount() {
		return this.workbook.getSheets().size();
	}
	public Set<String> getSheetNames() {
		return this.workbook.getSheets().keySet();
	}
	public boolean isRowCountExceeded(FastTablePortlet ftp) {
		return ftp.getTable().getRows().size() > SpreadSheetWorksheet.MAX_ROW_COUNT;
	}
	public boolean setTimezoneOffset(String timezone) {
		final TimeZone tz = EH.getTimeZoneOrGMT(timezone);
		if (tz == null)
			return false;
		try {
			this.workbook.setTimezoneOffset(tz.getOffset(EH.now().toInstant().toEpochMilli()));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean setTimezoneOffset(Long offset) {
		this.workbook.setTimezoneOffset(offset);
		return true;
	}
	public void addSheet(String sheetName, byte[] imageData) {
	}
	public void addSheet(FastTablePortlet t, String sheetName, boolean onlySelectedRows, boolean shouldFormat) {
		if (isRowCountExceeded(t)) {
			t.getManager().showAlert("Max spreadsheet row of " + SpreadSheetWorksheet.MAX_ROW_COUNT + " count exceeded: " + t.getTable().getRows().size());
			return;
		}
		addSheetInner(t, sheetName, onlySelectedRows, shouldFormat);
	}
	
	public void copySheet(String targetSheet, String newSheet) {
		final SpreadSheetWorksheetBase base = this.workbook.getSheets().get(targetSheet);
		if (base instanceof SpreadSheetFlexsheet) {
			workbook.copyFlexSheet(newSheet, (SpreadSheetFlexsheet)base);
		} else if (base instanceof SpreadSheetWorksheet) {
			this.getWorkBook().getSheets().put(newSheet, this.getWorksheet(targetSheet));
		} else {
			throw new RuntimeException("Can not copy: Unknown Sheet Type");
		}
	}
	public void addFlexSheet(String sheetName) {
		this.workbook.addFlexSheet(sheetName);
	}
	public SpreadSheetWorksheetBase getWorksheet(String name) {
		return this.workbook.getSheets().get(name);
	}
	
	public void loadExistingSheets(Bytes data) {
		this.loadExistingSheets(data, null);
	}
	
	@SuppressWarnings("unchecked")
	public void loadExistingSheets(Bytes data, final List<String> worksheetNames) {
		Map<String, Object> files = XlsxHelper.parseRawXlsx(data.getBytes(), worksheetNames);
		XmlElement appXML = (XmlElement)files.get(XlsxHelper.APP_XML_PATH);
		this.workbook.setAppXML(appXML);
		XmlElement coreXML = (XmlElement)files.get(XlsxHelper.CORE_XML_PATH);
		this.workbook.setCoreXML(coreXML);
		if (files.containsKey(XlsxHelper.CUSTOM_XML_PATH)) {
			XmlElement customXML = (XmlElement)files.get(XlsxHelper.CUSTOM_XML_PATH);
			this.workbook.setCustomXML(customXML);
		}
		
		XmlElement contentTypeXML = (XmlElement)files.get(XlsxHelper.CONTENT_TYPE_PATH);
		this.workbook.setContentTypeXML(contentTypeXML);
		
		XmlElement workbookXML = (XmlElement)files.get(XlsxHelper.WORKBOOK_XML_PATH);
		if (workbookXML != null) {
			//Clean up hidden definedNames and add visible ones
			XmlElement definedNames = workbookXML.getFirstElement("definedNames");
			if (definedNames != null) {
				XmlElement newDefinedNames = new XmlElement("definedNames");
				for (XmlNode node: definedNames.getChildren()) {
					XmlElement e = (XmlElement)node;
					String hidden = e.getAttribute("hidden");
					if (SH.is(hidden) && SH.equals(hidden, "1"))
						continue;
					else {
						try {
							final String name = e.getAttribute("name");
							final String value = e.getInnerAsString();
							this.workbook.addNamedRange(name, value);
						} catch (Exception ex) {			
						}
					}
					newDefinedNames.addChild(e);
				}
				workbookXML.setFirstElement(newDefinedNames);
			}
		}
		
		//Recreate sheet mapping
		Map<String, Integer> workbookSheetMapping = CH.m();
		if (workbookXML != null) {
			XmlElement sheets = workbookXML.getFirstElement("sheets");
			try {
				for (XmlNode node : sheets.getChildren()) {
					XmlElement e = (XmlElement)node;
					workbookSheetMapping.put(e.getAttribute("name"), SH.parseInt(e.getAttribute("sheetId")));
					//Check if sheet is hidden
					try {
						if (SH.equals(e.getAttribute("state"), "hidden"))
							this.workbook.hideSpreadSheet(e.getAttribute("name"));
					} catch (Exception ex) {}
					
				}
			} catch (Exception e) {}
		}
		
		//Handle sharedStrings
		Map<Integer, Integer> sharedStringsMapping = loadSharedStrings(files);
		
		//Load themes
		List<Tuple2<String, XmlElement>> themesList = (List<Tuple2<String, XmlElement>>) files.get(XlsxHelper.THEMES_PATH);
		for (final Tuple2<String, XmlElement> t: themesList) {
			this.workbook.addTheme(t.getValue());
		}
		
		//Load styles
		final XmlElement styles = (XmlElement)files.get(XlsxHelper.STYLES_PATH);
		final Map<Integer, Integer> styleMapping = this.workbook.parseExistingStyleXML(styles);
		
		Map<Integer, Integer> sheetMapping = CH.m();
		
		//Load worksheet
		List<Tuple2<String, XmlElement>> worksheets = (List<Tuple2<String, XmlElement>>)files.get(XlsxHelper.WORKSHEETS_PATH);
		Map<String, XmlElement> sheetRelationships = (Map<String, XmlElement>)files.get(XlsxHelper.WORKSHEETS_RELATIONSHIP_PATH);
		for (final Tuple2<String, XmlElement> worksheet: worksheets) {
			String worksheetName = worksheet.getKey();
			SpreadSheetFlexsheet fs = workbook.addFlexSheet(worksheet.getKey(), worksheet.getValue(), 
					sharedStringsMapping, styleMapping);
			//Handle remapping of relationship id
			if (sheetRelationships.containsKey(worksheetName))
				workbook.addWorksheetRelationships("sheet" + fs.getSheetId() + ".xml.rels", sheetRelationships.get(worksheet.getKey()));
			
			if (workbookSheetMapping.containsKey(worksheetName))
				sheetMapping.put(workbookSheetMapping.get(worksheetName), SH.parseInt(fs.getSheetId()));
		}
		
		//Load pivot tables
		if (files.containsKey(XlsxHelper.PIVOT_TABLE_PATH)) {
			Map<String, XmlElement> pivotTables = (Map<String, XmlElement>)files.get(XlsxHelper.PIVOT_TABLE_PATH);
			for (Map.Entry<String, XmlElement> e: pivotTables.entrySet()) {
				String key = SH.beforeFirst(SH.afterLast(e.getKey(), "pivotCacheDefinition"), ".xml");
				workbook.addPivotTable(SH.parseInt(key), e.getValue());
			}
			
		}
		
		//Load external references
		if (files.containsKey(XlsxHelper.EXTERNAL_PATH)) {
			List<Tuple2<String, XmlElement>> externalLinks = (List<Tuple2<String, XmlElement>>)files.get(XlsxHelper.EXTERNAL_PATH);
			Map<String, XmlElement> externalRelationships = (Map<String, XmlElement>)files.get(XlsxHelper.EXTERNAL_RELATIONSHIP_PATH);
			for (final Tuple2<String, XmlElement> external: externalLinks) {
				String externalName = external.getKey();
				String externalKey = SH.beforeFirst(SH.afterLast(external.getKey(), "externalLink"), ".xml");
				String relName = externalName + ".rels";
				XmlElement relationship = null;
				if (externalRelationships.containsKey(relName))
					relationship = externalRelationships.get(relName);
				workbook.addExternalLink(SH.parseInt(externalKey), sheetRelationships.get(externalName), relationship);
			}			
		}
		
		//Load custom xmls (only store relationship references)
		if (files.containsKey(XlsxHelper.CUSTOM_SHEET_XML_RELATIONSHIP_PATH)) {
			List<Tuple2<String, XmlElement>> customXmls = (List<Tuple2<String, XmlElement>>)files.get(XlsxHelper.CUSTOM_SHEET_XML_RELATIONSHIP_PATH);
			for (final Tuple2<String, XmlElement> customXml: customXmls) {
				String customXmlKey = SH.beforeFirst(SH.afterLast(customXml.getKey(), "item"), ".xml");
				workbook.addCustomXMLRelationship(SH.parseInt(customXmlKey), customXml.getValue());
			}
		}
		
		this.workbook.setWorkbookXML(workbookXML);
		
		XmlElement calcChainXML = (XmlElement)files.get(XlsxHelper.CALCCHAIN_PATH);
		if (calcChainXML != null && !sheetMapping.isEmpty()) {
				for (XmlNode node : calcChainXML.getChildren()) {
					try {
						XmlElement e = (XmlElement)node;
						int id = SH.parseInt(e.getAttribute("i"));
						if (sheetMapping.containsKey(id))
							e.addAttributeNotStrict("i", SH.toString(sheetMapping.get(id)));
					} catch (Exception e) {}
				}
		}
		this.workbook.setCalcChainXML(calcChainXML);
		
		//Sheet metadata
		XmlElement sheetMetadataXML = (XmlElement)files.get(XlsxHelper.SHEET_METADATA_PATH);
		if (sheetMetadataXML != null)
			this.workbook.setSheetMetadataXML(sheetMetadataXML);
		
		//Load misc files
		List<Tuple2<String, byte[]>> miscFileList = (List<Tuple2<String, byte[]>>) files.get(XlsxHelper.MISC_FILES);
		for (final Tuple2<String, byte[]> t: miscFileList) {
			this.workbook.addMiscFile(t.getKey(), t.getValue());
		}
	}
	
	private Map<Integer, Integer> loadSharedStrings(final Map<String, Object> files) {
		Map<Integer, Integer> sharedStringsMapping = new HashMap<Integer, Integer>();
		if (!files.containsKey(XlsxHelper.SHARED_STRINGS_PATH))
			return sharedStringsMapping;
		XmlElement sharedStrings = (XmlElement)files.get(XlsxHelper.SHARED_STRINGS_PATH);
		int i = 0;
		for (XmlElement e: sharedStrings.getElements()) {
			int new_id = this.workbook.getSharedString(e).getId();
			sharedStringsMapping.put(i++, new_id);
		}
		return sharedStringsMapping;
	}
	
	private void addSheetInner(FastTablePortlet t, String sheetName, boolean onlySelectedRows, boolean shouldFormat) {
		this.getWorkBook().getWorkbookStyle().setDefaults();
		
		if (this.workbook.getTimezoneOffset() == 0)
			this.workbook.setTimezoneOffset(t.getManager().getLocaleFormatter().getTimeZone().getOffset(t.getManager().getNow()));
		final String headerBgColor = t.getOption(FastTablePortlet.OPTION_MENU_BAR_COLOR, "#535353");
		final String headerFontColor = t.getOption(FastTablePortlet.OPTION_MENU_FONT_COLOR, "#FFFFFF");
		String defaultFontColor = t.getOption(FastTablePortlet.OPTION_DEFAULT_FONT_COLOR, "#000000");
		String bgColor = t.getOption(FastTablePortlet.OPTION_BACKGROUND_STYLE, "#FFFFFF");
		final String greybarColor = t.getOption(FastTablePortlet.OPTION_GREY_BAR_COLOR, "#EEEEEE");
		bgColor = SH.stripPrefix(bgColor, "_bg=", false);
		final BasicTable r = new BasicTable();
		final FastWebTable fastTable = t.getTable();
		r.setTitle(sheetName);
		final List<Row> rows = onlySelectedRows ? t.getTable().getSelectedRows() : t.getTable().getRows();
		BasicCalcTypes underlyingTypes = new BasicCalcTypes();
		final int cnt = fastTable.getVisibleColumnsCount();
		short[] numberFormatTypes = new short[cnt];

		boolean hasSpecial = false;
		for (int i = 0; i < cnt; i++) {
			WebColumn col = fastTable.getVisibleColumn(i);
			final Class<?> c = fastTable.getTable().getColumnAt(col.getTableColumnLocations()[0]).getType();
			underlyingTypes.putType(col.getColumnName(), c);
			WebCellFormatter cellFormatter = col.getCellFormatter();
			NumberWebCellFormatter numberFormatter;
			if (cellFormatter instanceof WebCellStyleWrapperFormatter) {
				numberFormatter = OH.castIfInstance(((WebCellStyleWrapperFormatter) cellFormatter).getInner(), NumberWebCellFormatter.class);
			} else if (cellFormatter instanceof WebCellStyleAdvancedWrapperFormatter) {
				numberFormatter = OH.castIfInstance(((WebCellStyleAdvancedWrapperFormatter) cellFormatter).getInner(), NumberWebCellFormatter.class);
			} else
				numberFormatter = OH.castIfInstance(cellFormatter, NumberWebCellFormatter.class);

			final short numberFormatType;
			if (numberFormatter != null) {
				Formatter formatter = numberFormatter.getFormatter();
				if (formatter instanceof BasicDateFormatter) {
					boolean isDate = formatter.getPattern().contains("yy");//year
					boolean isTime = formatter.getPattern().contains("mm");//minute
					boolean isNano = formatter.getPattern().contains("RRR");
					boolean isMicro = !isNano && formatter.getPattern().contains("rrr");
					boolean isMilli = formatter.getPattern().contains("SSS") && !isMicro && !isNano;

					if (isTime && !isDate) {
						if (isMilli) {
							numberFormatType = SpreadSheetResource_NumberFormat.TYPE_TIME_MILLI;
							hasSpecial = true;
						} else if (isNano) {
							numberFormatType = SpreadSheetResource_NumberFormat.TYPE_TIME_NANO;
							hasSpecial = true;
						} else if (isMicro) {
							numberFormatType = SpreadSheetResource_NumberFormat.TYPE_TIME_MICRO;
							hasSpecial = true;
						} else
							numberFormatType = SpreadSheetResource_NumberFormat.TYPE_TIME;
					} else if (isDate && !isTime)
						numberFormatType = SpreadSheetResource_NumberFormat.TYPE_DATE;
					else {
						// force type to TYPE_DATETIME if format is Nano or Micro (Excel only supports up to Milis).
						if (isMilli) {
							numberFormatType = SpreadSheetResource_NumberFormat.TYPE_DATETIME_MILLI;
							hasSpecial = true;
						} else if (isNano) {
							numberFormatType = SpreadSheetResource_NumberFormat.TYPE_DATETIME_NANO;
							hasSpecial = true;
						} else if (isMicro) {
							numberFormatType = SpreadSheetResource_NumberFormat.TYPE_DATETIME_MICRO;
							hasSpecial = true;
						} else
							numberFormatType = SpreadSheetResource_NumberFormat.TYPE_DATETIME;
					}
				} else if (formatter instanceof BasicNumberFormatter) {
					NumberFormat numberFormat = ((BasicNumberFormatter) formatter).getNumberFormat();
					int minimumFractionDigits = numberFormat.getMinimumFractionDigits();
					numberFormatType = SpreadSheetResource_NumberFormat.getDecimalFormatType(minimumFractionDigits);
				} else {
					numberFormatType = SpreadSheetResource_NumberFormat.TYPE_NUMBER;
				}
			} else
				numberFormatType = SpreadSheetResource_NumberFormat.TYPE_STRING;

			numberFormatTypes[i] = numberFormatType;
			r.addColumn(String.class, WebHelper.htmlToText(fastTable.getVisibleColumn(i).getColumnName(), false));
		}

		for (Row row : rows) {
			Object values[] = new Object[cnt];
			for (int i = 0; i < cnt; i++) {
				WebColumn col = fastTable.getVisibleColumn(i);
				Object value = row.getAt(col.getTableColumnLocations()[0]);
				if (hasSpecial) {
					switch (numberFormatTypes[i]) {
						case SpreadSheetResource_NumberFormat.TYPE_TIME_NANO:
						case SpreadSheetResource_NumberFormat.TYPE_DATETIME_NANO:
							if (value instanceof Number)
								value = ((Number) value).longValue() / 1000000d;
							break;
						case SpreadSheetResource_NumberFormat.TYPE_TIME_MICRO:
						case SpreadSheetResource_NumberFormat.TYPE_DATETIME_MICRO:
							if (value instanceof Number)
								value = ((Number) value).longValue() / 1000d;
							break;
						case SpreadSheetResource_NumberFormat.TYPE_TIME_MILLI:
						case SpreadSheetResource_NumberFormat.TYPE_DATETIME_MILLI:
							if (value instanceof Number)
								value = ((Number) value).doubleValue();
							break;
					}
				}
				values[i] = value;
			}
			r.getRows().addRow(values);
		}

		SpreadSheetWorksheet sheet = this.workbook.addSheet(sheetName, r, underlyingTypes);
		sheet.setPinnedColumnsCount(fastTable.getPinnedColumnsCount());
		for (int j = 0; j < cnt; j++) {
			WebColumn col = fastTable.getVisibleColumn(j);
			sheet.setColumnWidthPx(j, col.getWidth());
			StringBuilder sb = new StringBuilder();
			StringBuilder cellStyle = new StringBuilder();
			Map<String, String> styles = new HasherMap<String, String>();
			WebCellFormatter cellFormatter = col.getCellFormatter();
			for (int y = 0; y < r.getSize(); y++) {
				try {
					if (shouldFormat) {
						cellFormatter.formatCellToHtml(col.getData(rows.get(y)), sb, cellStyle);
						String conditionalCellColor = null;
						String conditionalFontColor = null;
						if (cellStyle.length() > 0) {
							styles.clear();
							SH.splitToMapNoThrow(styles, '|', '=', cellStyle, 0, cellStyle.length());
							cellStyle.setLength(0);
							conditionalCellColor = styles.get("_bg");
							conditionalFontColor = styles.get("_fg");
						}
						sb.setLength(0);
						String cellColor = conditionalCellColor != null ? conditionalCellColor : ((y & 1) == 0 ? greybarColor : bgColor);
						sheet.setStyle(j, y, this.workbook.getWorkbookStyle().getCellXfs("solid", null, cellColor, 
								conditionalFontColor != null ? conditionalFontColor : defaultFontColor, null,
								((Short)numberFormatTypes[j]).intValue(), SpreadSheetResource_NumberFormat.getFormatCode(numberFormatTypes[j])));
					} else
						sheet.setStyle(j, y, null);
				} catch (Exception e) {
					throw new RuntimeException("Error with column " + col.getColumnName() + " at row " + y, e);
				}
			}

		}
		if (shouldFormat)
			sheet.setHeaderStyle(this.workbook.getWorkbookStyle().getCellXfs("solid", null, headerBgColor, headerFontColor, null, null, null));
		else
			sheet.setHeaderStyle(null);
	}
	
	public void hideSpreadSheet(String sheetName) {
		this.workbook.hideSpreadSheet(sheetName);
	}
	
	public void showSpreadSheet(String sheetName) {
		this.workbook.showSpreadSheet(sheetName);
	}
	
	public void deleteSpreadSheet(String sheetName) {
		this.workbook.deleteSpreadSheet(sheetName);
	}
	
	public void renameSpreadSheet(String sheetName, String newSheetName) {
		this.workbook.renameSpreadSheet(sheetName, newSheetName);
	}
}
