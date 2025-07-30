package com.f1.utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.formatter.BasicNumberFormatter;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.xml.XmlElement;
import com.f1.utils.xml.XmlParser;

public class XlsxHelper {

	private static final Logger log = Logger.getLogger(XlsxHelper.class.getName());

	public static final String SHARED_STRINGS_PATH = "xl/sharedStrings.xml";
	public static final String STYLES_PATH = "xl/styles.xml";
	public static final String WORKBOOK_PATH = "xl/workbook.xml";
	public static final String SHEET_HEADER_PATH = "xl/";
	public static final String RELATIONSHIPS_PATH = "xl/_rels/workbook.xml.rels";
	public static final String CALCCHAIN_PATH = "xl/calcChain.xml";
	public static final String SHEET_METADATA_PATH = "xl/metadata.xml";
	public static final String APP_XML_PATH = "docProps/app.xml";
	public static final String CORE_XML_PATH = "docProps/core.xml";
	public static final String CUSTOM_XML_PATH = "docProps/custom.xml";
	public static final String CONTENT_TYPE_PATH = "[Content_Types].xml";
	public static final String WORKSHEETS_PATH = "xl/worksheets/";
	public static final String EXTERNAL_PATH = "xl/externalLinks/";
	public static final String CUSTOM_SHEET_XML_RELATIONSHIP_PATH = "customXml/_rels/";
	public static final String WORKSHEETS_RELATIONSHIP_PATH = "xl/worksheets/_rels/";
	public static final String EXTERNAL_RELATIONSHIP_PATH = "xl/externalLinks/_rels/";
	public static final String WORKBOOK_XML_PATH = "xl/workbook.xml";
	public static final String PIVOT_TABLE_PATH = "xl/pivotCache/";
	public static final String THEMES_PATH = "xl/theme/";
	public static final String MISC_FILES = "misc";

	private static final XmlParser parser = new XmlParser();

	private static class XlsxFormat {

		private static final List<String> TIME_FORMAT = CH.l("h", "s", "aa");
		private static final List<String> DATE_FORMAT = CH.l("yy", "d", "E", "M");
		private static final char AMIBIGUOUS_M = 'm';
		private final List<String> formats = new ArrayList<String>();
		private String strFormat; //Optional string formatting
		public Boolean isDate;
		public Boolean isTime;

		//See for format code specification: https://support.microsoft.com/en-us/office/number-format-codes-5026bbd6-04bc-48cd-bf33-80f18b4eae68
		public XlsxFormat(String format) {

			//Convert excel format to java friendly format
			format = SH.replaceAll(format, "am/pm", "aa");
			format = SH.replaceAll(format, "AM/PM", "aa");
			format = SH.replaceAll(format, "\\", "");
			format = SH.replaceAll(format, "dddd", "EEEEE");
			format = SH.replaceAll(format, "ddd", "EEE");
			format = SH.replaceAll(format, "mmmm", "MMMMM");

			boolean hasAmbiguousChar = SH.indexOf(format, AMIBIGUOUS_M, 0) != -1;
			if (SH.indexOf(format, "[$-F400]", 0) != -1) { //Time format
				format = SH.substring(format, 8, format.length());
				this.isTime = true;
				this.isDate = false;
			} else if (SH.indexOf(format, "[$-F800]", 0) != -1) { //Datetime format
				format = SH.substring(format, 8, format.length());
				this.isTime = false;
				this.isDate = true;
			} else { //Custom format
				boolean isTime = false;
				for (String s : TIME_FORMAT) {
					if (SH.indexOf(format, s, 0) != -1) {
						isTime = true;
						break;
					}
				}
				this.isTime = isTime;
				boolean isDate = false;
				for (String s : DATE_FORMAT) {
					if (SH.indexOf(format, s, 0) != -1) {
						isDate = true;
						break;
					}
				}
				this.isDate = isDate;
			}

			//Handle excel month vs minute:  If you use the "m" or "mm" code immediately after the "h" or "hh" code (for hours) 
			//or immediately before the "ss" code (for seconds), Excel displays minutes instead of the month.
			if (!isTime && hasAmbiguousChar) {
				format = SH.replaceAll(format, AMIBIGUOUS_M, "M");
				this.isDate = true;
			} else if (hasAmbiguousChar) {
				StringBuilder sb = new StringBuilder(format);
				boolean isTime = false;
				for (int i = 0; i < sb.length(); ++i) {
					final char c = sb.charAt(i);

					if (c == 'm' && !isTime) {
						sb.setCharAt(i, 'M');
						this.isDate = true;
						continue;
					}

					//Only check for letters (ignore other chars like :,/,...)
					if (Character.isAlphabetic(c)) {
						if (c == 'h' || c == 's' || c == 'm')
							isTime = true;
						else
							isTime = false;
					}
				}
				format = sb.toString();
			}

			//Clean up color related format information
			format = SH.replaceAll(format, "\"", "");
			while (SH.indexOf(format, '[', 0) != -1) {
				int startIdx = SH.indexOf(format, '[', 0);
				int endIdx = SH.indexOf(format, ']', 0);
				if (endIdx == -1)
					break;
				format = SH.substring(format, 0, startIdx) + SH.substring(format, endIdx + 1, format.length());
			}

			//Clean up alignment related information
			StringBuilder sb = new StringBuilder(format);
			for (int i = 0; i < sb.length(); ++i) {
				final char c = sb.charAt(i);

				if (c == '_' || c == '*') {
					sb.deleteCharAt(i);
					sb.deleteCharAt(i);
					continue;
				}
			}
			format = sb.toString();

			List<String> formatLists = SH.splitToList(";", format);
			for (String s : formatLists) {
				if (s.contains("@")) {
					this.strFormat = s;
				} else {
					this.formats.add(s);
				}
			}
		}

		public String getFormat(Number val) {
			if (formats.isEmpty())
				return "";
			else if (formats.size() == 1)
				return formats.get(0);
			else if (formats.size() == 2) {
				if (val == null || val.longValue() >= 0)
					return formats.get(0);
				else
					return formats.get(1);
			} else if (formats.size() == 3) {
				if (val == null || val.longValue() > 0)
					return formats.get(0);
				else if (val.longValue() < 0)
					return formats.get(1);
				else
					return formats.get(2);
			} else {
				throw new RuntimeException("Invalid number of formats detected (max 3): " + this.formats);
			}
		}

		public String format(Number value) {
			String formatted = "";
			if (this.isTime && !this.isDate) { //Time format
				formatted = getExcelFormattedTime(this.getFormat(value), SH.toString(value));
			} else if (!this.isTime && this.isDate) { //Date format
				formatted = getExcelFormattedDate(this.getFormat(value), SH.toString(value));
			} else if (this.isTime && this.isDate) {
				formatted = getExcelFormattedDateTime(this.getFormat(value), SH.toString(value));
			} else { //Custom format, try to clean it up
				formatted = getExcelFormattedNumber(this.getFormat(value), SH.toString(value));
			}

			if (SH.is(this.strFormat)) {
				return SH.replaceAll(this.strFormat, "@", formatted);
			}
			return formatted;
		}
	}

	private static class XlsxSheet {
		public final String name;
		public final String directory;

		public XlsxSheet(final String _name, final String _directory) {
			name = _name;
			directory = _directory;
		}
	}

	//Returns an excel position (A, BD, CE) from an int
	public static String getExcelPosition(int pos) {
		int first = pos % 26;
		String result = "" + (char) ('A' + first);
		pos -= first;
		long base = 26;
		while (pos > 0) {
			int num = pos / 26;
			result = (char) ('A' + num - 1) + result;
			pos -= num * base;
			base *= 26;
		}
		return result;
	}

	//Specify types for performance - otherwise type needs to be deduced from Strings
	//Specify tableName to only get a specific table
	public static Tableset parseXlsx(byte[] csvFile, boolean firstRowIsHeader, Class<?>[] types, String tableName) {

		Map<String, Tuple2<ZipEntry, byte[]>> contents;
		try {
			contents = IOH.unzip(csvFile);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		parser.setSkipEmptyText(false);

		//Get strings
		final List<String> sharedStrings_list = parseSharedStrings(toXml(contents, SHARED_STRINGS_PATH));

		//Get styles
		XmlElement style_element = toXml(contents, STYLES_PATH);
		final List<Integer> style_list = parseStyleSheet(style_element);
		final Map<Integer, String> custom_styles = parseCustomStyleSheet(style_element);
		final Map<String, String> relationships = parseRelationships(toXml(contents, RELATIONSHIPS_PATH));

		//Get individual sheets (name + id)
		final List<XlsxSheet> sheets = getSheets(toXml(contents, WORKBOOK_PATH), relationships);
		Tableset result = new TablesetImpl();
		//Parse and store result
		for (final XlsxSheet sheet : sheets) {
			if (tableName != null && !sheet.name.equals(tableName))
				continue;
			final Table t = parseWorksheet(toXml(contents, SHEET_HEADER_PATH + sheet.directory), firstRowIsHeader, sharedStrings_list, style_list, custom_styles, types);
			t.setTitle(sheet.name);
			result.putTable(t);
		}
		return result;
	}

	public static Table parseXlsxRange(byte[] csvFile, boolean firstRowIsHeader, Class<?>[] types, String tableName, String dimensions) {
		Map<String, Tuple2<ZipEntry, byte[]>> contents;
		try {
			contents = IOH.unzip(csvFile);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		parser.setSkipEmptyText(false);

		//Get strings
		final List<String> sharedStrings_list = parseSharedStrings(toXml(contents, SHARED_STRINGS_PATH));

		//Get styles
		XmlElement style_element = toXml(contents, STYLES_PATH);
		final List<Integer> style_list = parseStyleSheet(style_element);
		final Map<Integer, String> custom_styles = parseCustomStyleSheet(style_element);
		final Map<String, String> relationships = parseRelationships(toXml(contents, RELATIONSHIPS_PATH));

		//Get individual sheets (name + id)
		final List<XlsxSheet> sheets = getSheets(toXml(contents, WORKBOOK_PATH), relationships);
		//Parse and store result
		for (final XlsxSheet sheet : sheets) {
			if (tableName != null && !sheet.name.equals(tableName))
				continue;
			List<XmlElement> rows = toXml(contents, SHEET_HEADER_PATH + sheet.directory).getFirstElement("sheetData").getElements();
			final Table t = parseElementsInner(dimensions, rows, firstRowIsHeader, types, sharedStrings_list, style_list, custom_styles, false);
			t.setTitle(sheet.name);
			return t;
		}
		return null;
	}

	public static Map<String, Object> parseRawXlsx(byte[] csvFile) {
		return XlsxHelper.parseRawXlsx(csvFile, null);
	}
	//Returns a raw map of xlsx files as xml elements in a map
	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseRawXlsx(byte[] csvFile, final List<String> worksheetNames) {

		Map<String, Tuple2<ZipEntry, byte[]>> contents;
		try {
			contents = IOH.unzip(csvFile);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		parser.setSkipEmptyText(false);

		Map<String, Object> parsedFiles = CH.m();
		if (contents.containsKey(SHARED_STRINGS_PATH))
			parsedFiles.put(SHARED_STRINGS_PATH, toXml(contents, SHARED_STRINGS_PATH));
		if (contents.containsKey(STYLES_PATH))
			parsedFiles.put(STYLES_PATH, toXml(contents, STYLES_PATH));
		if (contents.containsKey(CALCCHAIN_PATH))
			parsedFiles.put(CALCCHAIN_PATH, toXml(contents, CALCCHAIN_PATH));
		if (contents.containsKey(APP_XML_PATH))
			parsedFiles.put(APP_XML_PATH, toXml(contents, APP_XML_PATH));
		if (contents.containsKey(CORE_XML_PATH))
			parsedFiles.put(CORE_XML_PATH, toXml(contents, CORE_XML_PATH));
		if (contents.containsKey(CONTENT_TYPE_PATH))
			parsedFiles.put(CONTENT_TYPE_PATH, toXml(contents, CONTENT_TYPE_PATH));
		if (contents.containsKey(SHEET_METADATA_PATH))
			parsedFiles.put(SHEET_METADATA_PATH, toXml(contents, SHEET_METADATA_PATH));
		if (contents.containsKey(CUSTOM_XML_PATH))
			parsedFiles.put(CUSTOM_XML_PATH, toXml(contents, CUSTOM_XML_PATH));
		if (contents.containsKey(WORKBOOK_XML_PATH))
			parsedFiles.put(WORKBOOK_XML_PATH, toXml(contents, WORKBOOK_XML_PATH));

		Map<String, XmlElement> sheetRelationships = CH.m();
		List<Tuple2<String, XmlElement>> sheetsList = CH.l();
		if (contents.containsKey(RELATIONSHIPS_PATH)) {
			final XmlElement relationshipXml = toXml(contents, RELATIONSHIPS_PATH);
			parsedFiles.put(RELATIONSHIPS_PATH, relationshipXml);
			final Map<String, String> relationships = parseRelationships(relationshipXml);
			//Get individual sheets (name + id)
			final List<XlsxSheet> sheets = getSheets(toXml(contents, WORKBOOK_PATH), relationships);

			//Parse and store result
			for (final XlsxSheet sheet : sheets) {
				if (worksheetNames != null && !worksheetNames.contains(sheet.name))
					continue;
				sheetsList.add(new Tuple2<String, XmlElement>(sheet.name, toXml(contents, SHEET_HEADER_PATH + sheet.directory)));
				String relationshipPath = WORKSHEETS_RELATIONSHIP_PATH + SH.afterLast(sheet.directory, "/") + ".rels";
				if (contents.containsKey(relationshipPath)) {
					sheetRelationships.put(sheet.name, toXml(contents, relationshipPath));
				}
			}
		}
		parsedFiles.put(WORKSHEETS_PATH, sheetsList);
		parsedFiles.put(WORKSHEETS_RELATIONSHIP_PATH, sheetRelationships);

		Map<String, XmlElement> externalRel = CH.m();
		List<Tuple2<String, XmlElement>> externalList = CH.l();
		Map<String, XmlElement> pivotTables = CH.m();
		List<Tuple2<String, XmlElement>> customXMLRels = CH.l();

		for (final Map.Entry<String, Tuple2<ZipEntry, byte[]>> entry : contents.entrySet()) {
			String filename = entry.getKey();
			if (filename.startsWith(EXTERNAL_PATH)) {
				if (filename.endsWith(".xml.rels")) {
					externalRel.put(filename, toXml(contents, filename));
				} else {
					externalList.add(new Tuple2<String, XmlElement>(filename, toXml(contents, filename)));
				}
			} else if (filename.startsWith(PIVOT_TABLE_PATH)) {
				if (filename.contains("pivotCacheDefinition")) {
					pivotTables.put(filename, toXml(contents, filename));
				}
			} else if (filename.startsWith(CUSTOM_SHEET_XML_RELATIONSHIP_PATH)) {
				if (filename.endsWith(".xml.rels"))
					customXMLRels.add(new Tuple2<String, XmlElement>(filename, toXml(contents, filename)));
			}
		}

		parsedFiles.put(CUSTOM_SHEET_XML_RELATIONSHIP_PATH, customXMLRels);
		parsedFiles.put(PIVOT_TABLE_PATH, pivotTables);
		parsedFiles.put(EXTERNAL_PATH, externalList);
		parsedFiles.put(EXTERNAL_RELATIONSHIP_PATH, externalRel);

		List<Tuple2<String, XmlElement>> themesList = CH.l();
		for (final Map.Entry<String, Tuple2<ZipEntry, byte[]>> e : contents.entrySet()) {
			String filename = e.getKey();
			if (filename.startsWith(THEMES_PATH)) {
				themesList.add(new Tuple2<String, XmlElement>(SH.afterFirst(filename, THEMES_PATH), toXml(contents, filename)));
			}
		}
		parsedFiles.put(THEMES_PATH, themesList);

		//Copy all other misc files inside an excel (will likely break when loading multiple files)
		//List of files/paths to ignore
		final List<String> matching_files = CH.l(SHARED_STRINGS_PATH, STYLES_PATH, WORKBOOK_PATH, RELATIONSHIPS_PATH, CALCCHAIN_PATH, APP_XML_PATH, CORE_XML_PATH, CUSTOM_XML_PATH,
				CONTENT_TYPE_PATH, THEMES_PATH, "_rels/.rels", WORKSHEETS_PATH);
		List<Tuple2<String, byte[]>> miscFileList = CH.l();
		List<Object> filtered = contents.entrySet().stream().filter(new Predicate<Entry<String, Tuple2<ZipEntry, byte[]>>>() {
			@Override
			public boolean test(Entry<String, Tuple2<ZipEntry, byte[]>> entry) {
				for (final String file_prefix : matching_files)
					if (SH.startsWith(entry.getKey(), file_prefix))
						return false;
				return true;
			}
		}).collect(Collectors.toList());

		for (final Object o : filtered) {
			final Map.Entry<String, Tuple2<ZipEntry, byte[]>> e = (Map.Entry<String, Tuple2<ZipEntry, byte[]>>) o;
			miscFileList.add(new Tuple2<String, byte[]>(e.getKey(), e.getValue().getValue()));
		}
		parsedFiles.put(MISC_FILES, miscFileList);
		return parsedFiles;
	}

	public static Table parseExcelRange(final List<XmlElement> rows, boolean firstRowIsHeader, final List<String> sharedStrings, final List<Integer> styleList, Class<?>[] types,
			String tableDimension, final Map<Integer, String> customStyles, boolean getRaw) {
		return parseElementsInner(tableDimension, rows, firstRowIsHeader, types, sharedStrings, styleList, customStyles, getRaw);
	}

	private static XmlElement toXml(final Map<String, Tuple2<ZipEntry, byte[]>> data, final String path) {
		final Tuple2<ZipEntry, byte[]> val = data.get(path);
		String val_str = new String(val.getB());
		return parser.parseDocument(val_str);
	}

	//https://support.microsoft.com/en-gb/office/timevalue-function-0b615c12-33d8-4431-bf3d-f3eb6d186645
	private static String getExcelFormattedTime(String format, String val) {
		val = "0." + SH.afterFirst(val, '.');
		double timeval = SH.parseDouble(val);
		final int seconds = 86400;
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR, 0);
		c.add(Calendar.SECOND, (int) Math.round(seconds * timeval));
		return new DateFormatNano(format).format(c.getTime());
	}

	//https://support.microsoft.com/en-us/office/datevalue-function-df8b07d4-7761-4a93-bc33-b7471bbff252
	private static String getExcelFormattedDate(String format, String val) {
		Calendar c = Calendar.getInstance();
		c.set(1899, 11, 30, 0, 0, 0);
		val = SH.beforeFirst(val, '.');
		c.add(Calendar.DAY_OF_YEAR, SH.parseInt(val));
		return new DateFormatNano(format).format(c.getTime());
	}

	private static String getExcelFormattedDateTime(String format, String val) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(1899, 11, 30, 0, 0, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR, 0);
		String dateVal = SH.beforeFirst(val, '.');
		c.add(Calendar.DAY_OF_YEAR, SH.parseInt(dateVal));
		double timeVal = SH.parseDouble(SH.afterFirst(val, '.'));
		final int seconds = 86400;
		c.add(Calendar.SECOND, (int) Math.round(seconds * timeVal));
		return new DateFormatNano(format).format(c.getTime());
	}

	private static String getExcelFormattedNumber(String format, String val) {
		final BasicNumberFormatter formatter = new BasicNumberFormatter(new DecimalFormat(format));
		return formatter.format(SH.parseDouble(val));
	}

	public static double convertUnixToShreadSheetTimestamp(final long unix, final long workbookOffset) {
		return (unix + workbookOffset) / 86400000d + 25569d;

	}

	public static String setValueFromXmlElement(final Object value, final boolean isString, final Integer numberFormatting, Long workbookOffset, String format) {
		if (value == null)
			return null;

		if (workbookOffset == null)
			workbookOffset = 0L;

		if (isString) {
			//This should be the shared string id
			return SH.toString(value);
		} else if (value instanceof Number) {
			String datetimeStr = "";
			try {
				datetimeStr = SH.toString(convertUnixToShreadSheetTimestamp(((Number) value).longValue(), workbookOffset));
			} catch (Exception e) {
			}

			Number d;
			if (SH.is(format)) {
				XlsxFormat parsedFormat = new XlsxFormat(format);
				if (parsedFormat.isDate && parsedFormat.isTime) {
					return datetimeStr;
				} else if (parsedFormat.isDate) {
					return SH.beforeFirst(datetimeStr, ".");
				} else if (parsedFormat.isTime) {
					return SH.afterFirst(datetimeStr, ".");
				} else {
					d = (Number) value;
				}
			} else if (numberFormatting != null && numberFormatting < 50) {
				switch (numberFormatting) {
					case 14: //mm-dd-yy
					case 15: //d-mmm-yy
					case 16: //d-mmm
					case 17:
						//Take only first half
						return SH.beforeFirst(datetimeStr, ".");
					case 18: //h:mm AM/PM
					case 19: //h:mm:ss AM/PM
					case 20: //h:mm
					case 21: //h:mm:ss 	
						return SH.afterFirst(datetimeStr, ".");
					case 22: //m/d/yy h:mm
						return datetimeStr;
					default:
						d = (Number) value;
						break;
				}
			} else {
				d = ((Number) value).doubleValue();
			}

			if (d instanceof Double || d instanceof Float) {
				return SH.toString(d.doubleValue());
			} else {
				return SH.toString(((Number) d).longValue());
			}
		} else {
			throw new RuntimeException("Failed to set value for " + SH.toString(value));
		}
	}

	public static String getValueFromXmlElement(final XmlElement e, final String sharedString, int numberFormatting, final String format, boolean getRaw) {
		if (e == null)
			return null;

		final XmlElement value = e.getFirstElement("v");
		final String t = e.getAttribute("t");

		if (value == null && !SH.equals(t, "inlineStr"))
			return null;

		String val = "";
		if (SH.equals(t, "s")) {
			val = sharedString;
		} else if (SH.equals(t, "inlineStr")) {
			try {
				XmlElement inlineString = e.getFirstElement("is");
				val = inlineString.getFirstElement("t").getInnerAsString();
			} catch (Exception ex) {
				log.warning("Failed to parse inline string, element: " + e.toString() + ", exception: " + ex.toString());
			}
		} else {
			val = value.getInnerAsString();
		}

		if (getRaw) {
			return val;
		}

		//See: https://learn.microsoft.com/en-us/dotnet/api/documentformat.openxml.spreadsheet.numberingformat?view=openxml-2.8.1
		try {
			switch (numberFormatting) {
				case 0: //general
				case 1: //whole numbers
				case 2: //decimals (0.00)
				case 3: //#,##0
				case 4: //#,##0.00
				case 9: //percentage 0%
				case 10://percentage 0.00%
				case 11://0.00E+00
				case 12://fractions
					return val;
				case 14: //mm-dd-yy
					return getExcelFormattedDate("MM-dd-yyyy", val);
				case 15: //d-mmm-yy
					return getExcelFormattedDate("dd-MMM-yyyy", val);
				case 16: //d-mmm
					return getExcelFormattedDate("dd-MMM", val);
				case 17:
					return getExcelFormattedDate("MMM-yyyy", val);
				case 18: //h:mm AM/PM
					return getExcelFormattedTime("hh:mm aa", val);
				case 19: //h:mm:ss AM/PM
					return getExcelFormattedTime("hh:mm:ss aa", val);
				case 20: //h:mm
					return getExcelFormattedTime("hh:mm", val);
				case 21: //h:mm:ss 
					return getExcelFormattedTime("hh:mm:ss", val);
				case 22: //m/d/yy h:mm
					return getExcelFormattedDate("MM-dd-yyyy", val) + " " + getExcelFormattedTime("hh:mm", val);
				case 45: //mm:ss
					return getExcelFormattedTime("mm:ss", val);
				case 46: //[h]:mm:ss
					return getExcelFormattedTime("H:mm:ss", val);
				case 47: //mmss.0
					String tempval = "0." + SH.afterFirst(val, '.');
					final int SECONDS = 86400;
					return getExcelFormattedTime("mm:ss", val) + "." + SH.afterLast(getExcelFormattedNumber(".#", SH.toString(SECONDS * SH.parseDouble(tempval))), '.');
			}

			if (SH.is(format)) {
				XlsxFormat parsedFormat = new XlsxFormat(format);
				return parsedFormat.format(SH.parseDouble(val));
			}

			return getExcelFormattedTime("", val);
		} catch (Exception exception) {
			LH.warning(log, "Error with parsing cell, returning original string: " + exception);
			return val;
		}
	}

	//Get data either as is, or from the shared strings list
	private static String getValue(final XmlElement e, final List<String> sharedStrings, final List<Integer> styleList, final Map<Integer, String> customStyles, boolean getRaw) {
		String t = e.getAttribute("t");

		String sharedString = null;
		//Get from String
		if (t != null && t.equals("s")) {
			final XmlElement value = e.getFirstElement("v");
			if (value == null)
				return null;
			int index = SH.parseInt(value.getInnerAsString());
			sharedString = sharedStrings.get(index);
		}

		//Handle specific numeric formatting
		int styleFormat;
		try {
			int styleIdx = SH.parseInt(e.getAttribute("s"));
			styleFormat = styleList.get(styleIdx);
		} catch (Exception ex) {
			styleFormat = 0;
		}

		final String format = customStyles != null ? customStyles.get(styleFormat) : null;
		return getValueFromXmlElement(e, sharedString, styleFormat, format, getRaw);
	}

	//Attempt to deduce most suitable type from column values
	private static Class<?> tryGetType(Class<?> current, String obj) {

		//Do nothing for null objects
		if (current == null && obj == null)
			return null;
		//If type is already known and null object passed in, used existing
		else if (current != null && obj == null)
			return current;

		//Nothing further to be done
		if (current == String.class)
			return String.class;

		//Start with strictest type (only 0 or 1s)
		if (current == null || current == Boolean.class)
			if (obj.equals("0") || obj.equals("1"))
				return Boolean.class;

		//Try to deduce numerical type, if already known to contain floats, then skip
		if (current != Double.class && current != Float.class) {
			if (current != Long.class) {
				try {
					SH.parseInt(obj);
					return Integer.class;
				} catch (Exception e) {
				}
			}

			try {
				SH.parseLong(obj);
				return Long.class;
			} catch (Exception e) {
			}
		}

		//Deduce floating points
		if (current == Double.class || current == Float.class || SH.indexOf(obj, '.', 0) != -1) {

			if (current != Double.class) {
				try {
					SH.parseFloat(obj);
					return Float.class;
				} catch (Exception e) {
				}
			}

			try {
				SH.parseDouble(obj);
				return Double.class;
			} catch (Exception e) {
			}

		}

		//No further types, default to string
		return String.class;
	}

	private static Class<?>[] parseTypes(final ArrayList<String[]> values, int colSize) {
		//O(N^2) iteration to deduce type - can be avoided by specifying class
		Class<?>[] types = new Class<?>[colSize];
		for (final String[] innervals : values)
			for (int i = 0; i < colSize; ++i)
				types[i] = tryGetType(types[i], innervals[i]);

		//Set remaining unknown types to string
		for (int i = 0; i < types.length; ++i)
			if (types[i] == null)
				types[i] = String.class;

		return types;
	}

	private static Object castValue(final Class<?> target, final String val) {
		if (val == null)
			return null;

		if (target == Boolean.class)
			return val.equals("1") ? true : false;
		else if (target == Integer.class)
			return SH.parseInt(val);
		else if (target == Long.class)
			return SH.parseLong(val);
		else if (target == Float.class)
			return SH.parseFloat(val);
		else if (target == Double.class)
			return SH.parseDouble(val);
		return val;
	}

	private static class ExcelDimension {
		public int width;
		public int height;
		public int start_x_idx; //Starting x val (could be 3)
		@SuppressWarnings("unused")
		public int start_y_idx; //Starting y val (could be B (2))
	}

	//Converts Alphabetical ordering to int
	public static int excelStringDimToInt(String dimension) {
		int val = 0;
		for (int i = dimension.length() - 1, cnt = 0; i >= 0; --i, ++cnt) {
			val += (int) (dimension.charAt(i) - ('A' - 1)) * (int) Math.pow(26, cnt);
		}
		return val;
	}

	//Expects dimensions in "A3:G10" format
	private static ExcelDimension parseDimensions(String dimension) {
		ExcelDimension parsedDimensions = new ExcelDimension();

		if (dimension.isEmpty()) {
			parsedDimensions.height = 0;
			parsedDimensions.width = 0;
			parsedDimensions.start_x_idx = 0;
			parsedDimensions.start_y_idx = 0;
			return parsedDimensions;
		}

		String firstDim = SH.beforeFirst(dimension, ':');
		String secondDim = SH.afterFirst(dimension, ':');

		Matcher matcher = Pattern.compile("\\d+").matcher(firstDim);
		matcher.find();
		int start_y = SH.parseInt(matcher.group());
		matcher = Pattern.compile("[A-Z]+").matcher(firstDim);
		matcher.find();
		String start_x_str = matcher.group();
		int start_x = excelStringDimToInt(start_x_str);

		matcher = Pattern.compile("\\d+").matcher(secondDim);
		matcher.find();
		int end_y = SH.parseInt(matcher.group());
		matcher = Pattern.compile("[A-Z]+").matcher(secondDim);
		matcher.find();
		String end_x_str = matcher.group();
		int end_x = excelStringDimToInt(end_x_str);

		parsedDimensions.height = end_y - start_y + 1;
		parsedDimensions.start_y_idx = start_y;
		parsedDimensions.width = end_x - start_x + 1;
		parsedDimensions.start_x_idx = start_x;

		return parsedDimensions;
	}

	private static Table parseElementsInner(String dimensionString, final List<XmlElement> rows, final boolean firstRowIsHeader, Class<?>[] types, final List<String> sharedStrings,
			final List<Integer> styleList, final Map<Integer, String> customStyles, boolean getRaw) {
		if (rows.isEmpty())
			return new ColumnarTable();
		String[] colNames = null;
		ArrayList<String[]> values = new ArrayList<String[]>(firstRowIsHeader ? rows.size() - 1 : rows.size());
		boolean first = true;
		boolean deduceTypes = types == null;
		ExcelDimension dimensions = parseDimensions(dimensionString);

		for (int i = 0; i < rows.size() && i < dimensions.height; ++i) {
			final XmlElement row = rows.get(i);
			final List<XmlElement> columns = row.getElements();
			if (first) {
				//Get colname and types
				first = false;
				colNames = new String[dimensions.width];
				XmlElement current = null;
				int current_col_pos = -1;

				for (int j = 0, col_idx = 0; j < dimensions.width; ++j) {
					if (firstRowIsHeader) {
						if (current == null && col_idx < columns.size()) {
							current = columns.get(col_idx);
							String s = current.getAttribute("r");
							Matcher matcher = Pattern.compile("[A-Z]+").matcher(s);
							matcher.find();
							current_col_pos = excelStringDimToInt(matcher.group()) - dimensions.start_x_idx;
						}
						//Match position to current index
						if (current_col_pos == j) {
							String col_name = getValue(columns.get(col_idx), sharedStrings, styleList, customStyles, getRaw);
							if (col_name != null && !col_name.isEmpty())
								colNames[j] = col_name;
							else
								colNames[j] = "col" + j;

							current = null;
							++col_idx;
							continue;
						}

						colNames[j] = "col" + j;

					} else
						colNames[j] = "col" + j;
				}

				if (firstRowIsHeader)
					continue;
			}

			String[] row_vals = new String[dimensions.width];
			XmlElement current = null;
			int current_col_pos = -1;
			for (int j = 0, col_idx = 0; j < dimensions.width && col_idx < columns.size(); ++j) {
				if (current == null) {
					current = columns.get(col_idx);
					String s = current.getAttribute("r");
					Matcher matcher = Pattern.compile("[A-Z]+").matcher(s);
					matcher.find();
					current_col_pos = excelStringDimToInt(matcher.group()) - dimensions.start_x_idx;
				}

				if (current_col_pos == j) {
					row_vals[j] = getValue(current, sharedStrings, styleList, customStyles, getRaw);
					current = null;
					++col_idx;
				} else
					row_vals[j] = null;
			}
			values.add(row_vals);

		}

		if (deduceTypes) {
			types = parseTypes(values, dimensions.width);
		}

		Table result = new ColumnarTable(types, colNames);
		//Only add empty rows if there are subsequent values
		List<Row> emptyRows = new ArrayList<Row>();
		for (final String[] vals : values) {
			Row r = result.newEmptyRow();
			boolean isEmpty = true;
			for (int i = 0; i < dimensions.width; ++i) {
				final Object o = castValue(types[i], vals[i]);
				isEmpty &= o == null;
				r.putAt(i, o);
			}
			if (isEmpty)
				emptyRows.add(r);
			else {
				if (!emptyRows.isEmpty())
					for (final Row emptyRow : emptyRows)
						result.getRows().add(emptyRow);
				emptyRows.clear();
				result.getRows().add(r);
			}
		}

		return result;
	}

	private static Table parseWorksheet(final XmlElement e, boolean firstRowIsHeader, final List<String> sharedStrings, final List<Integer> styleList,
			final Map<Integer, String> customStyles, Class<?>[] types) {
		//Parse dimensions
		String tableDimension = "";
		try {
			tableDimension = e.getFirstElement("dimension").getAttribute("ref");
		} catch (Exception _e) { //Excel sheet doesn't contain dimension information - manually parse
			List<XmlElement> columns = e.getFirstElement("sheetData").getElements("row");
			if (!columns.isEmpty()) {
				List<XmlElement> rows = columns.get(0).getElements("c");
				if (!rows.isEmpty())
					tableDimension += rows.get(0).getAttribute("r") + ":";
				rows = columns.get(columns.size() - 1).getElements("c");
				if (!rows.isEmpty())
					tableDimension += rows.get(rows.size() - 1).getAttribute("r");
			}
		}

		List<XmlElement> rows = e.getFirstElement("sheetData").getElements();
		return parseElementsInner(tableDimension, rows, firstRowIsHeader, types, sharedStrings, styleList, customStyles, false);
	}

	private static String stripXmlTags(String s) {
		StringBuilder sb = new StringBuilder();
		boolean findStartTag = false;
		for (int i = 0; i < s.length(); ++i) {
			final char c = s.charAt(i);
			if (!findStartTag) {
				if (c == '<')
					findStartTag = true;
				else
					sb.append(c);
			} else {
				if (c == '>')
					findStartTag = false;
			}
		}
		return sb.toString();
	}

	private static List<String> parseSharedStrings(final XmlElement e) {
		int string_count = 0;
		try {
			string_count = SH.parseInt(e.getAttribute("uniqueCount"));
		} catch (Exception except) {
			return new ArrayList<String>();
		}

		ArrayList<String> shared_string_set = new ArrayList<String>(string_count);
		for (final XmlElement element : e.getElements()) {
			try {
				shared_string_set.add(element.getFirstElement("t").getInnerAsString());
			} catch (Exception ex) {
				log.warning("Failed to parse shared string, attempting to resolve " + element.toString() + ",exception: " + ex.toString());
				String stripped = stripXmlTags(element.toString());
				shared_string_set.add(stripped);
			}
		}
		return shared_string_set;
	}

	private static List<Integer> parseStyleSheet(final XmlElement e) {
		final XmlElement cells = e.getFirstElement("cellXfs");
		int count = 0;
		try {
			count = SH.parseInt(cells.getAttribute("count"));
		} catch (Exception exception) {
			return new ArrayList<Integer>();
		}

		ArrayList<Integer> stylesheet = new ArrayList<Integer>(count);
		for (final XmlElement element : cells.getElements()) {
			stylesheet.add(SH.parseInt(element.getAttribute("numFmtId")));
		}
		return stylesheet;
	}

	private static Map<Integer, String> parseCustomStyleSheet(final XmlElement e) {
		final XmlElement cells = e.getFirstElement("numFmts");
		if (cells == null)
			return Collections.emptyMap();
		int count = SH.parseInt(cells.getAttribute("count"));

		HashMap<Integer, String> stylesheet = new HashMap<Integer, String>(count);
		for (final XmlElement element : cells.getElements()) {
			final int id = SH.parseInt(element.getAttribute("numFmtId"));
			String formatCode = element.getAttribute("formatCode");
			formatCode = new XlsxFormat(formatCode).getFormat(null);
			stylesheet.put(id, formatCode);
		}
		return stylesheet;
	}

	private static Map<String, String> parseRelationships(final XmlElement e) {
		HashMap<String, String> relationships = new HashMap<String, String>();
		for (final XmlElement element : e.getElements()) {
			final String id = element.getAttribute("Id");
			final String target = element.getAttribute("Target");
			if (id != null && target != null)
				relationships.put(id, target);
		}
		return relationships;
	}

	private static List<XlsxSheet> getSheets(final XmlElement e, final Map<String, String> relationships) {
		final List<XmlElement> elements = e.getFirstElement("sheets").getElements();
		ArrayList<XlsxSheet> results = new ArrayList<XlsxSheet>(elements.size());
		for (final XmlElement element : elements) {
			String relationshipId = element.getAttribute("r:id");
			results.add(new XlsxSheet(element.getAttribute("name"), relationships.get(relationshipId)));
		}
		return results;
	}
}
