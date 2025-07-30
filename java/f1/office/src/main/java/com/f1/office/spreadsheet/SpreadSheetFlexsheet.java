package com.f1.office.spreadsheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.SH;
import com.f1.utils.XlsxHelper;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.xml.XmlElement;
import com.f1.utils.xml.XmlNode;
import com.f1.utils.xml.XmlParser;
import com.f1.utils.xml.XmlRawText;
import com.f1.utils.xml.XmlText;

//This is for a raw excel workflow
public class SpreadSheetFlexsheet extends SpreadSheetWorksheetBase {

	//Raw XML strings only used when loading an existing spreadsheet
	private String header;
	private String footer;

	private HashMap<String, XmlElement> rows = new HashMap<String, XmlElement>();

	public static class ExcelDimensions {
		private final SpreadSheetFlexsheet fs;
		public String dimString;
		Tuple2<Integer, Integer> coords;

		public ExcelDimensions(final SpreadSheetFlexsheet fs, final String dimString) {
			this.fs = fs;
			this.dimString = SH.trim(SH.toUpperCase(dimString));
			this.coords = SpreadSheetUtils.getPositionFromExcelDim(dimString);
			this.dimString = SH.trim(SH.toUpperCase(dimString));
		}

		public int getSortingIndex() {
			final Tuple2<Integer, Integer> coords = SpreadSheetUtils.getPositionFromExcelDim(dimString);
			return coords.getA() + coords.getB() * fs.endDimensions.getX();
		}

		public int getX() {
			return this.coords.getA();
		}

		public int getY() {
			return this.coords.getB();
		}

		@Override
		public int hashCode() {
			return dimString.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null)
				return false;
			if (this.getClass() != o.getClass())
				return false;
			ExcelDimensions d = (ExcelDimensions) o;
			return SH.equals(dimString, d.dimString);
		}
	}

	public static class CellContent {

		private XmlElement contents;

		private final SpreadSheetWorkbook workbook;

		public CellContent(final CellContent copy) {
			this.workbook = copy.workbook;
			XmlParser parser = new XmlParser();
			this.contents = parser.parseDocument(copy.contents.toString());
		}

		public CellContent(final SpreadSheetWorkbook workbook) {
			this.workbook = workbook;
			this.contents = new XmlElement("c");
		}

		public void setContent(final XmlElement e) {
			this.contents = e;
		}

		public Integer getStyle() {
			try {
				return SH.parseInt(this.contents.getAttribute("s"));
			} catch (Exception e) {
			}
			return null;
		}

		public void setStyle(Integer id) {
			if (id != null && id >= 0)
				contents.addAttributeNotStrict("s", SH.toString(id));
			else
				contents.removeAttribute("s");
		}

		public String getDimension() {
			try {
				return this.contents.getAttribute("r");
			} catch (Exception e) {
			}
			return null;
		}

		public void setDimension(final String dim) {
			contents.addAttributeNotStrict("r", dim);
		}

		public String getValue(boolean getRaw) {
			try {
				int numFmt = 0;
				try {
					numFmt = this.workbook.getWorkbookStyle().getNumberFormattingByStyle(this.getStyle());
				} catch (Exception e2) {
				}

				String format = null;
				if (numFmt != 0) {
					try {
						format = this.workbook.getWorkbookStyle().getNumberFormatByFormatId(numFmt).getFormatCode();
					} catch (Exception e) {
					}
				}
				String sharedString = null;
				try {
					final String content = this.contents.getFirstElement("v").getInnerAsString();
					sharedString = this.workbook.getSharedString(SH.parseInt(content));
				} catch (Exception e2) {
				}
				return XlsxHelper.getValueFromXmlElement(this.contents, sharedString, numFmt, format, getRaw);
			} catch (Exception e) {
			}
			return null;
		}

		public void setValue(Object value) {
			if (value != null) {
				setFormula(null);
				String displayValue = "";
				Integer numFmt = null;
				String format = null;
				if (value instanceof String) {
					displayValue = SH.toString(this.workbook.getSharedString((String) value).getId());
					this.contents.addAttributeNotStrict("t", "s");
				} else {
					//Ignore number formatting for strings - only relevant for numbers
					try {
						numFmt = this.workbook.getWorkbookStyle().getNumberFormattingByStyle(this.getStyle());
					} catch (Exception e2) {
					}
					if (numFmt != null) {
						try {
							format = this.workbook.getWorkbookStyle().getNumberFormatByFormatId(numFmt).getFormatCode();
						} catch (Exception e) {
						}
					} else {
						numFmt = 0;
					}
					displayValue = SH.toString(value);
					if ("s".equals(this.contents.getAttribute("t")))
						this.contents.removeAttribute("t");
					displayValue = XlsxHelper.setValueFromXmlElement(value, false, numFmt, this.workbook.getTimezoneOffset(), format);
				}
				this.contents.setFirstElement(new XmlElement("v").addChild(new XmlText(displayValue)));
			} else {
				this.contents.deleteFirstElement("v");
			}
		}

		public String getFormula() {
			try {
				return this.contents.getFirstElement("f").getInnerAsString();
			} catch (Exception e) {
			}
			return null;
		}

		public void setFormula(final String formula) {
			if (formula != null) {
				this.contents.setFirstElement(new XmlElement("f").addChild(new XmlText(formula)));
			} else {
				this.contents.deleteFirstElement("f");
			}
		}

		public void writeToXmlRawText(final XmlRawText rawText, String dim) {
			this.setDimension(dim);
			rawText.addChild(this.contents);
		}
	}

	private HashMap<ExcelDimensions, CellContent> cells = new HashMap<ExcelDimensions, CellContent>();

	final SpreadSheetWorkbook workbook;

	private ExcelDimensions startDimensions;
	private ExcelDimensions endDimensions;

	public SpreadSheetFlexsheet(SpreadSheetWorkbook workbook, int sheetId, String name) {
		super(workbook, sheetId, name);
		this.workbook = workbook;
		this.startDimensions = new ExcelDimensions(this, "A1");
		this.endDimensions = new ExcelDimensions(this, "A1");
	}

	public SpreadSheetFlexsheet(SpreadSheetWorkbook workbook, int sheetId, String name, final SpreadSheetFlexsheet copy) {
		super(workbook, sheetId, name);
		this.workbook = workbook;
		for (final Map.Entry<ExcelDimensions, CellContent> e : copy.cells.entrySet()) {
			this.cells.put(e.getKey(), new CellContent(e.getValue()));
		}

		this.startDimensions = copy.startDimensions;
		this.endDimensions = copy.endDimensions;
		this.footer = copy.footer;
		this.header = copy.header;
	}

	public SpreadSheetFlexsheet(SpreadSheetWorkbook workbook, int sheetId, String name, final XmlElement e, final Map<Integer, Integer> sharedStringsMapping,
			final Map<Integer, Integer> stylesMapping) {
		super(workbook, sheetId, name);
		this.workbook = workbook;
		rows.clear();
		//Pull everything but sheetData
		String xmlString = e.toString();
		this.header = SH.beforeFirst(xmlString, "<sheetData>");
		this.footer = SH.afterLast(xmlString, "</sheetData>");

		try {
			String initial_dimensions = e.getFirstElement("dimension").getAttribute("ref");
			int index = SH.indexOf(initial_dimensions, ":", 0);
			//Single cell edit
			if (index == -1) {
				this.startDimensions = new ExcelDimensions(this, initial_dimensions);
				this.endDimensions = new ExcelDimensions(this, initial_dimensions);
			} else {
				String startDim = SH.substring(initial_dimensions, 0, index);
				String endDim = SH.substring(initial_dimensions, index + 1, initial_dimensions.length());
				this.startDimensions = new ExcelDimensions(this, startDim);
				this.endDimensions = new ExcelDimensions(this, endDim);
			}
		} catch (Exception ex) {
			this.startDimensions = new ExcelDimensions(this, "A1");
			this.endDimensions = new ExcelDimensions(this, "A1");
		}

		//Get all cells and convert them into managed format
		for (final XmlNode node : e.getFirstElement("sheetData").getChildren()) {
			if (!(node instanceof XmlElement))
				continue;
			XmlElement row = (XmlElement) node;
			rows.put(row.getAttribute("r"), row);
			for (final XmlNode n : row.getChildren()) {
				if (!(n instanceof XmlElement))
					continue;
				XmlElement cellElement = (XmlElement) n;
				String dim = null, type = null, val = null;
				Integer style = null;

				dim = cellElement.getAttribute("r");
				try {
					int s = SH.parseInt(cellElement.getAttribute("s"));
					style = stylesMapping.get(s);
					cellElement.addAttributeNotStrict("s", SH.toString(style));

				} catch (Exception exception) {
				}

				try {
					type = cellElement.getAttribute("t");
				} catch (Exception exception) {
				}

				//Using shared strings
				if ("s".equals(type)) {
					int idx = SH.parseInt(cellElement.getFirstElement("v").getInnerAsString());
					val = SH.toString(sharedStringsMapping.get(idx));
					XmlElement newVal = new XmlElement("v");
					newVal.addChild(new XmlText(val));
					cellElement.setFirstElement(newVal);
				}

				ExcelDimensions dimensions = new ExcelDimensions(this, dim);
				setDimensions(dimensions);
				CellContent content = new CellContent(this.workbook);
				content.setContent(cellElement);
				this.cells.put(dimensions, content);
			}
		}

	}

	//Used to set one single cell
	private void setValueInner(final ExcelDimensions dim, Object value) {
		if (cells.containsKey(dim)) {
			final CellContent content = cells.get(dim);
			content.setValue(value);
		} else {
			setDimensions(dim);
			CellContent content = new CellContent(this.workbook);
			content.setValue(value);
			cells.put(dim, content);
		}
	}

	private void clearValueInner(final ExcelDimensions dim) {
		if (cells.containsKey(dim)) {
			cells.remove(dim);
		}
	}

	public void setValue(String dimension, Object value) {
		int index = SH.indexOf(dimension, ":", 0);
		//Single cell edit
		if (index == -1) {
			setValueInner(new ExcelDimensions(this, dimension), value);
		} else {
			String startDim = SH.substring(dimension, 0, index);
			String endDim = SH.substring(dimension, index + 1, dimension.length());
			ExcelDimensions startDimension = new ExcelDimensions(this, startDim);
			ExcelDimensions endDimension = new ExcelDimensions(this, endDim);
			if (startDimension.getX() > endDimension.getX() || startDimension.getY() > endDimension.getY()) {
				endDimension = new ExcelDimensions(this, startDim);
				startDimension = new ExcelDimensions(this, endDim);
			}

			for (int i = startDimension.getX(); i <= endDimension.getX(); ++i) {
				for (int j = startDimension.getY(); j <= endDimension.getY(); ++j) {
					String excelPosition = XlsxHelper.getExcelPosition(i - 1) + j;
					setValueInner(new ExcelDimensions(this, excelPosition), value);
				}
			}
		}
	}

	public void setValue(String dimension, Table value, boolean useHeader) {
		final Tuple2<Integer, Integer> startDim = SpreadSheetUtils.getPositionFromExcelDim(dimension);
		int y = startDim.getB();
		final int width = value.getColumnsCount();

		if (useHeader) {
			for (int x = 0; x < width; ++x) {
				String currDim = XlsxHelper.getExcelPosition(x + startDim.getA() - 1) + SH.toString(y);
				setValueInner(new ExcelDimensions(this, currDim), value.getColumnAt(x).getId());
			}
			++y;
		}

		for (final Row r : value.getRows()) {
			for (int x = 0; x < width; ++x) {
				String currDim = XlsxHelper.getExcelPosition(x + startDim.getA() - 1) + SH.toString(y);
				setValueInner(new ExcelDimensions(this, currDim), r.getAt(x));
			}
			++y;
		}
	}

	private void setDimensions(final ExcelDimensions dim) {
		if (dim.getX() > this.endDimensions.getX())
			this.endDimensions = new ExcelDimensions(this, XlsxHelper.getExcelPosition(dim.getX() - 1) + this.endDimensions.getY());
		if (dim.getX() < this.startDimensions.getX())
			this.startDimensions = new ExcelDimensions(this, XlsxHelper.getExcelPosition(dim.getX() - 1) + this.startDimensions.getY());
		if (dim.getY() > this.endDimensions.getY())
			this.endDimensions = new ExcelDimensions(this, XlsxHelper.getExcelPosition(this.endDimensions.getX() - 1) + dim.getY());
		if (dim.getY() < this.startDimensions.getY())
			this.startDimensions = new ExcelDimensions(this, XlsxHelper.getExcelPosition(this.startDimensions.getX() - 1) + dim.getY());
	}

	public void setValueNamedRange(final String namedRange, final Object value) {
		String range = this.workbook.getNamedRange(namedRange);
		if (SH.isnt(range))
			throw new RuntimeException("Failed to parse given named range: " + namedRange);
		String parsedRange = parseNamedRange(range);
		if (SpreadSheetUtils.isValidExcelDimensionRange(parsedRange) || SpreadSheetUtils.isValidExcelDimension(parsedRange))
			setValue(parsedRange, value);
	}

	public Object getValue(String dimension, boolean getRaw) {
		ExcelDimensions dim = new ExcelDimensions(this, dimension);
		if (cells.containsKey(dim)) {
			return cells.get(dim).getValue(getRaw);
		}
		return null;
	}

	public Table getValues(final String dimension, final Boolean hasHeader, final Class<?>[] types, boolean getRaw) {
		int index = SH.indexOf(dimension, ":", 0);
		//Single cell edit
		if (index == -1) {
			return null;
		} else {
			String startDim = SH.substring(dimension, 0, index);
			String endDim = SH.substring(dimension, index + 1, dimension.length());
			ExcelDimensions startDimension = new ExcelDimensions(this, startDim);
			ExcelDimensions endDimension = new ExcelDimensions(this, endDim);
			if (startDimension.getX() > endDimension.getX() || startDimension.getY() > endDimension.getY()) {
				endDimension = new ExcelDimensions(this, startDim);
				startDimension = new ExcelDimensions(this, endDim);
			}
			final int width = endDimension.getX() - startDimension.getX();
			final int height = endDimension.getY() - startDimension.getY();

			if (types != null && types.length != width + 1)
				throw new RuntimeException("Invalid types count, expecting: " + (width + 1) + ", got: " + types.length);

			ArrayList<XmlElement> elements = new ArrayList<XmlElement>(width * height);
			for (int j = startDimension.getY(); j <= endDimension.getY(); ++j) {
				XmlElement row = new XmlElement("row");
				for (int i = startDimension.getX(); i <= endDimension.getX(); ++i) {
					String excelPosition = XlsxHelper.getExcelPosition(i - 1) + j;
					final ExcelDimensions dim = new ExcelDimensions(this, excelPosition);
					if (this.cells.containsKey(dim))
						row.addChild(this.cells.get(dim).contents);

				}
				elements.add(row);
			}

			List<String> sharedStrings = new ArrayList<String>(this.workbook.getSharedStrings().getResourcesCount());
			for (final SpreadSheetResource_SharedString sharedString : this.workbook.getSharedStrings().getResourcesInOrder()) {
				sharedStrings.add(sharedString.getString());
			}
			List<Integer> styleList = new ArrayList<Integer>();
			Map<Integer, String> customStyles = new HashMap<Integer, String>();
			this.workbook.getWorkbookStyle().populateStyles(styleList, customStyles);

			return XlsxHelper.parseExcelRange(elements, hasHeader, sharedStrings, styleList, types, dimension, customStyles, getRaw);
		}
	}

	public Object getValueNamedRange(String namedRange, boolean getRaw) {
		String range = this.workbook.getNamedRange(namedRange);
		if (SH.isnt(range))
			throw new RuntimeException("Failed to parse given named range: " + namedRange);
		String parsedRange = parseNamedRange(range);
		if (SpreadSheetUtils.isValidExcelDimension(parsedRange))
			return getValue(parsedRange, getRaw);
		return null;
	}

	public Table getValuesNamedRange(String namedRange, Boolean hasHeader, final Class<?>[] types, boolean getRaw) {
		String range = this.workbook.getNamedRange(namedRange);
		if (SH.isnt(range))
			throw new RuntimeException("Failed to parse given named range: " + namedRange);
		String parsedRange = parseNamedRange(range);
		if (SpreadSheetUtils.isValidExcelDimensionRange(parsedRange))
			return getValues(parsedRange, hasHeader, types, getRaw);
		return null;
	}

	public Table getRawValuesNamedRange(String namedRange, Boolean hasHeader, final Class<?>[] types) {
		String range = this.workbook.getNamedRange(namedRange);
		if (SH.isnt(range))
			throw new RuntimeException("Failed to parse given named range: " + namedRange);
		String parsedRange = parseNamedRange(range);
		if (SpreadSheetUtils.isValidExcelDimensionRange(parsedRange))
			return getValues(parsedRange, hasHeader, types, false);
		return null;
	}

	public void clearCell(String dimension) {
		int index = SH.indexOf(dimension, ":", 0);
		//Single cell edit
		if (index == -1) {
			clearValueInner(new ExcelDimensions(this, dimension));
		} else {
			String startDim = SH.substring(dimension, 0, index);
			String endDim = SH.substring(dimension, index + 1, dimension.length());
			ExcelDimensions startDimension = new ExcelDimensions(this, startDim);
			ExcelDimensions endDimension = new ExcelDimensions(this, endDim);
			if (startDimension.getX() > endDimension.getX() || startDimension.getY() > endDimension.getY()) {
				endDimension = new ExcelDimensions(this, startDim);
				startDimension = new ExcelDimensions(this, endDim);
			}

			for (int i = startDimension.getX(); i <= endDimension.getX(); ++i) {
				for (int j = startDimension.getY(); j <= endDimension.getY(); ++j) {
					String excelPosition = XlsxHelper.getExcelPosition(i - 1) + j;
					clearValueInner(new ExcelDimensions(this, excelPosition));
				}
			}
		}

		//Recompute dimensions

		List<Map.Entry<ExcelDimensions, CellContent>> sorted = new ArrayList<Map.Entry<ExcelDimensions, CellContent>>(this.cells.entrySet());
		Collections.sort(sorted, new Comparator<Map.Entry<ExcelDimensions, CellContent>>() {
			public int compare(Map.Entry<ExcelDimensions, CellContent> a, Map.Entry<ExcelDimensions, CellContent> b) {
				return Integer.compare(a.getKey().getX(), b.getKey().getX());
			}
		});
		//Take first and last element for x-bounds
		if (!sorted.isEmpty()) {
			this.startDimensions = new ExcelDimensions(this, XlsxHelper.getExcelPosition(sorted.get(0).getKey().getX() - 1) + this.startDimensions.getY());
			this.endDimensions = new ExcelDimensions(this, XlsxHelper.getExcelPosition(sorted.get(sorted.size() - 1).getKey().getX() - 1) + this.endDimensions.getY());
		}
		Collections.sort(sorted, new Comparator<Map.Entry<ExcelDimensions, CellContent>>() {
			public int compare(Map.Entry<ExcelDimensions, CellContent> a, Map.Entry<ExcelDimensions, CellContent> b) {
				return Integer.compare(a.getKey().getY(), b.getKey().getY());
			}
		});
		//Take first and last element for y-bounds
		if (!sorted.isEmpty()) {
			this.startDimensions = new ExcelDimensions(this, XlsxHelper.getExcelPosition(this.startDimensions.getX() - 1) + sorted.get(0).getKey().getY());
			this.endDimensions = new ExcelDimensions(this, XlsxHelper.getExcelPosition(this.endDimensions.getX() - 1) + sorted.get(sorted.size() - 1).getKey().getY());
		}
	}

	private String parseNamedRange(String namedRange) {
		if (!SH.is(namedRange))
			return null;

		int index = SH.indexOf(namedRange, '!', 0);
		//Validate sheet name
		if (index != -1) {
			String sheetName = SH.substring(namedRange, 0, index);
			if (!SH.equals(this.name, sheetName))
				return null;
			namedRange = SH.substring(namedRange, index + 1, namedRange.length());
		}

		namedRange = SH.replaceAll(namedRange, '$', "");
		return namedRange;
	}

	public void clearCellNamedRange(String namedRange) {
		String range = this.workbook.getNamedRange(namedRange);
		if (SH.isnt(range))
			throw new RuntimeException("Failed to parse given named range: " + namedRange);
		String parsedRange = parseNamedRange(range);
		if (SpreadSheetUtils.isValidExcelDimensionRange(parsedRange) || SpreadSheetUtils.isValidExcelDimension(parsedRange))
			clearCell(parsedRange);
	}

	//Used to set one single cell
	private void setStyleInner(final ExcelDimensions dim, Integer id) {
		if (cells.containsKey(dim)) {
			final CellContent content = cells.get(dim);
			content.setStyle(id);
		} else {
			if (dim.getX() > this.endDimensions.getX())
				this.endDimensions = new ExcelDimensions(this, XlsxHelper.getExcelPosition(dim.getX() - 1) + this.endDimensions.getY());
			if (dim.getY() > this.endDimensions.getY())
				this.endDimensions = new ExcelDimensions(this, XlsxHelper.getExcelPosition(this.endDimensions.getX() - 1) + dim.getY());
			CellContent content = new CellContent(this.workbook);
			content.setStyle(id);
			cells.put(dim, content);
		}
	}

	public void setStyle(String dimension, Integer id) {
		int index = SH.indexOf(dimension, ":", 0);
		//Single cell edit
		if (index == -1) {
			setStyleInner(new ExcelDimensions(this, dimension), id);
		} else {
			String startDim = SH.substring(dimension, 0, index);
			String endDim = SH.substring(dimension, index + 1, dimension.length());
			ExcelDimensions startDimension = new ExcelDimensions(this, startDim);
			ExcelDimensions endDimension = new ExcelDimensions(this, endDim);
			if (startDimension.getX() > endDimension.getX() || startDimension.getY() > endDimension.getY()) {
				endDimension = new ExcelDimensions(this, startDim);
				startDimension = new ExcelDimensions(this, endDim);
			}

			for (int i = startDimension.getX(); i <= endDimension.getX(); ++i) {
				for (int j = startDimension.getY(); j <= endDimension.getY(); ++j) {
					String excelPosition = XlsxHelper.getExcelPosition(i - 1) + j;
					setStyleInner(new ExcelDimensions(this, excelPosition), id);
				}
			}
		}
	}

	public Integer getStyle(String dimension) {
		ExcelDimensions dim = new ExcelDimensions(this, dimension);
		if (this.cells.containsKey(dim)) {
			final CellContent content = cells.get(dim);
			return content.getStyle();
		}
		return null;
	}

	//Used to set one single cell
	private void setFormulaInner(final ExcelDimensions dim, String formula) {
		if (cells.containsKey(dim)) {
			final CellContent content = cells.get(dim);
			content.setFormula(formula);
		} else {
			if (dim.getX() > this.endDimensions.getX())
				this.endDimensions = new ExcelDimensions(this, XlsxHelper.getExcelPosition(dim.getX() - 1) + this.endDimensions.getY());
			if (dim.getY() > this.endDimensions.getY())
				this.endDimensions = new ExcelDimensions(this, XlsxHelper.getExcelPosition(this.endDimensions.getX() - 1) + dim.getY());
			CellContent content = new CellContent(this.workbook);
			content.setFormula(formula);
			cells.put(dim, content);
		}
	}

	public void setFormula(String dimension, String formula) {
		int index = SH.indexOf(dimension, ":", 0);
		//Single cell edit
		if (index == -1) {
			setFormulaInner(new ExcelDimensions(this, dimension), formula);
		} else {
			String startDim = SH.substring(dimension, 0, index);
			String endDim = SH.substring(dimension, index + 1, dimension.length());
			ExcelDimensions startDimension = new ExcelDimensions(this, startDim);
			ExcelDimensions endDimension = new ExcelDimensions(this, endDim);
			if (startDimension.getX() > endDimension.getX() || startDimension.getY() > endDimension.getY()) {
				endDimension = new ExcelDimensions(this, startDim);
				startDimension = new ExcelDimensions(this, endDim);
			}

			for (int i = startDimension.getX(); i <= endDimension.getX(); ++i) {
				for (int j = startDimension.getY(); j <= endDimension.getY(); ++j) {
					String excelPosition = XlsxHelper.getExcelPosition(i - 1) + j;
					setFormulaInner(new ExcelDimensions(this, excelPosition), formula);
				}
			}
		}
	}

	public String getFormula(String dimension) {
		ExcelDimensions dim = new ExcelDimensions(this, dimension);
		if (this.cells.containsKey(dim)) {
			final CellContent content = cells.get(dim);
			return content.getFormula();
		}
		return null;
	}

	public HashMap<ExcelDimensions, CellContent> getCells() {
		return this.cells;
	}

	public String getHeader() {
		return this.header;
	}

	public String getFooter() {
		return this.footer;
	}

	public XmlElement getRow(String r) {
		return rows.get(r);
	}

	public String getDimensions() {
		return this.startDimensions.dimString + ":" + this.endDimensions.dimString;
	}
}
