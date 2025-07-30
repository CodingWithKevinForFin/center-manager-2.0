package com.f1.office.spreadsheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.xml.XmlElement;
import com.f1.utils.xml.XmlNode;

//Wrapper class for managing the styles.xml file for each workbook
public class SpreadSheetWorkbookStyle {

	private static final List<String> SUPPORTED_ATTRIBUTES = CH.l("colors", "dxfs", "tableStyles", "extLst");
	private final Map<String, XmlElement> loadedAttributes = CH.m();

	private SpreadSheetCellColor getColor(String name, String attribute, String value) {
		return SH.is(value) ? new SpreadSheetCellColor(name, attribute, value) : null;
	}

	final private SpreadSheetResourcePool<SpreadSheetResource_CellXfs> cellXfs = new SpreadSheetResourcePool<SpreadSheetResource_CellXfs>() {
		@Override
		public SpreadSheetResource_CellXfs nw() {
			return new SpreadSheetResource_CellXfs();
		}
	};
	final private ArrayList<SpreadSheetResource_CellStyle> cellStyles = new ArrayList<SpreadSheetResource_CellStyle>();
	final private SpreadSheetResourcePool<SpreadSheetResource_CellStyleXfs> cellStyleXfs = new SpreadSheetResourcePool<SpreadSheetResource_CellStyleXfs>() {
		@Override
		public SpreadSheetResource_CellStyleXfs nw() {
			return new SpreadSheetResource_CellStyleXfs();
		}
	};
	final private SpreadSheetResourcePool<SpreadSheetResource_Fill> fills = new SpreadSheetResourcePool<SpreadSheetResource_Fill>() {
		@Override
		public SpreadSheetResource_Fill nw() {
			return new SpreadSheetResource_Fill();
		}
	};
	final private SpreadSheetResourcePool<SpreadSheetResource_Font> fonts = new SpreadSheetResourcePool<SpreadSheetResource_Font>() {
		@Override
		public SpreadSheetResource_Font nw() {
			return new SpreadSheetResource_Font();
		}
	};

	final private SpreadSheetResourcePool<SpreadSheetResource_Border> borders = new SpreadSheetResourcePool<SpreadSheetResource_Border>() {
		@Override
		public SpreadSheetResource_Border nw() {
			return new SpreadSheetResource_Border();
		}
	};

	final private SpreadSheetResourcePool<SpreadSheetResource_NumberFormat> numberFormats = new SpreadSheetResourcePool<SpreadSheetResource_NumberFormat>() {
		@Override
		public SpreadSheetResource_NumberFormat nw() {
			return new SpreadSheetResource_NumberFormat();
		}
	};

	//Handle remapping of ids
	private SpreadSheetResource_CellStyleXfs getCellStyleXfs(final XmlElement e, final Map<Integer, Integer> fillMapping, final Map<Integer, Integer> bordersMapping,
			final Map<Integer, Integer> fontsMapping) {
		SpreadSheetResource_CellStyleXfs format = cellStyleXfs.borrowTmp();
		format.setXml(e);
		//Remap any ids (for loading in multiple files)
		if (bordersMapping != null)
			format.setBorder(bordersMapping.get(format.getBorderId()), false);
		if (fontsMapping != null)
			format.setFont(fontsMapping.get(format.getFontId()), false);
		if (fillMapping != null)
			format.setFill(fillMapping.get(format.getFillId()), false);
		format = cellStyleXfs.share(format);
		return format;
	}

	//Handle remapping of ids
	private SpreadSheetResource_CellXfs addCellXfs(final XmlElement e, final Map<Integer, Integer> fillMapping, final Map<Integer, Integer> bordersMapping,
			final Map<Integer, Integer> fontsMapping) {
		SpreadSheetResource_CellXfs format = cellXfs.borrowTmp();
		format.setXml(e);
		//Remap any ids (for loading in multiple files)
		if (bordersMapping != null)
			format.setBorder(bordersMapping.get(format.getBorderId()), false);
		if (fontsMapping != null)
			format.setFont(fontsMapping.get(format.getFontId()), false);
		if (fillMapping != null)
			format.setFill(fillMapping.get(format.getFillId()), false);
		return cellXfs.share(format);
	}

	public SpreadSheetResource_CellXfs getCellXfs(String fillStyle, String bgColor, String fgColor, String fontColor, String fontName, Integer numberFormat, String formatCode) {
		SpreadSheetResource_CellXfs r = cellXfs.borrowTmp();
		SpreadSheetCellColor bgCellColor = getColor("bgColor", "rgb", bgColor);
		SpreadSheetCellColor fgCellColor = getColor("fgColor", "rgb", fgColor);
		SpreadSheetCellColor fontCellColor = getColor("color", "rgb", fontColor);

		SpreadSheetResource_Fill fill = getFill(fillStyle, bgCellColor, fgCellColor);
		SpreadSheetResource_Font font = getFont(fontCellColor, fontName);

		if (numberFormat != null && SH.is(formatCode)) {
			SpreadSheetResource_NumberFormat numFormat = getNumberFormat(SH.toString(numberFormat), formatCode);
			r.setNumFmt(numFormat.getNumberFormatId());
		} else {
			r.setNumFmt(0);
		}

		r.setBorder(0);
		r.setFill(fill.getId());
		r.setFont(font.getId());
		return cellXfs.share(r);
	}

	private SpreadSheetResource_Font getFont(SpreadSheetCellColor fontColor, String fontName) {
		if (fontName == null)
			fontName = "Calibri";
		if (fontColor == null)
			fontColor = new SpreadSheetCellColor("color", "rgb", "FF000000");
		SpreadSheetResource_Font font = fonts.borrowTmp();
		font.setColor(fontColor);
		font.setName(fontName);
		font = fonts.share(font);
		return font;
	}

	private SpreadSheetResource_Fill getFill(String fillStyle, SpreadSheetCellColor bgColor, SpreadSheetCellColor fgColor) {
		SpreadSheetResource_Fill fill = fills.borrowTmp();
		if (bgColor != null)
			fill.setBgColor(bgColor);
		if (fgColor != null)
			fill.setFgColor(fgColor);
		if (fillStyle != null)
			fill.setStyle(fillStyle);
		fill = fills.share(fill);
		return fill;
	}

	private SpreadSheetResource_NumberFormat getNumberFormat(String numberFormat, String formatCode) {
		SpreadSheetResource_NumberFormat format = numberFormats.borrowTmp();
		format.setFormatCode(formatCode);
		format.setNumberFormatId(numberFormat);
		format = numberFormats.share(format);
		return format;
	}

	public SpreadSheetResource_NumberFormat getNumberFormatByFormatId(int id) {
		for (SpreadSheetResource_NumberFormat format : numberFormats.getResourcesInOrder()) {
			if (format.getNumberFormatId() == id)
				return format;
		}
		return null;
	}

	private SpreadSheetResource_CellStyleXfs getCellStyleXfs(final int numFmtId, final int borderId, final int fillId, final int fontId) {
		SpreadSheetResource_CellStyleXfs format = cellStyleXfs.borrowTmp();
		format.setNumFmt(numFmtId);
		format.setBorder(borderId);
		format.setFill(fillId);
		format.setFont(fontId);
		format = cellStyleXfs.share(format);
		return format;
	}

	private SpreadSheetResource_Border getBorder(String leftStyle, SpreadSheetCellColor leftColor, String rightStyle, SpreadSheetCellColor rightColor, String topStyle,
			SpreadSheetCellColor topColor, String bottomStyle, SpreadSheetCellColor bottomColor, String diagonalStyle, SpreadSheetCellColor diagonalColor) {
		SpreadSheetResource_Border border = borders.borrowTmp();
		border.setStyle(leftStyle, "left");
		border.setColor(leftColor, "left");
		border.setStyle(rightStyle, "right");
		border.setColor(rightColor, "right");
		border.setStyle(topStyle, "top");
		border.setColor(topColor, "top");
		border.setStyle(bottomStyle, "bottom");
		border.setColor(bottomColor, "bottom");
		border.setStyle(diagonalStyle, "diagonal");
		border.setColor(diagonalColor, "diagonal");
		border = borders.share(border);
		return border;
	}

	private SpreadSheetResource_CellStyle addCellStyle(String name, int xfId, String builtinId, String xruid) {
		SpreadSheetResource_CellStyle cellStyle = new SpreadSheetResource_CellStyle();
		cellStyle.setName(name);
		cellStyle.setXFId(xfId);
		if (SH.is(builtinId))
			cellStyle.setBuiltInId(builtinId);
		if (SH.is(xruid))
			cellStyle.setUID(xruid);
		cellStyles.add(cellStyle);
		return cellStyle;
	}

	//Add default values
	public void setDefaults() {
		if (this.fonts.getResourcesCount() == 0)
			this.getFont(new SpreadSheetCellColor("color", "rgb", "FF000000"), "Calibri");

		if (this.fills.getResourcesCount() == 0) {
			this.getFill("none", null, null);
			this.getFill("gray125", null, null);
		}

		if (this.borders.getResourcesCount() == 0)
			this.getBorder("", null, "", null, "", null, "", null, "", null);

		if (this.cellStyleXfs.getResourcesCount() == 0)
			this.getCellStyleXfs(0, 0, 0, 0);

		if (this.cellStyles.isEmpty())
			this.addCellStyle("Normal", 0, "0", null);

		if (this.cellXfs.getResourcesCount() == 0)
			this.getCellXfs("none", null, null, "FF000000", "Calibri", -1, null);
	}

	public Integer getNumberFormattingByStyle(Integer styleId) {
		try {
			return this.cellXfs.getById(styleId).getNumFmtId();
		} catch (Exception e) {
			return null;
		}
	}

	public void populateStyles(final List<Integer> styleList, final Map<Integer, String> customStyles) {
		for (final SpreadSheetResource_CellXfs cellXfs : this.cellXfs.getResourcesInOrder()) {
			styleList.add(cellXfs.getNumFmtId());
		}

		for (final SpreadSheetResource_NumberFormat numFmt : this.numberFormats.getResourcesInOrder()) {
			if (SH.is(numFmt.getFormatCode()))
				customStyles.put(numFmt.getNumberFormatId(), numFmt.getFormatCode());
		}
	}

	public Map<Integer, Integer> parseExistingStyleXML(final XmlElement styles) {
		Map<Integer, Integer> fillMapping = new HashMap<Integer, Integer>();
		Map<Integer, Integer> bordersMapping = new HashMap<Integer, Integer>();
		Map<Integer, Integer> fontsMapping = new HashMap<Integer, Integer>();
		Map<Integer, Integer> cellXfsMapping = new HashMap<Integer, Integer>();

		int i = 1;
		try {
			for (XmlNode node : styles.getFirstElement("numFmts").getChildren()) {
				SpreadSheetResource_NumberFormat format = numberFormats.borrowTmp();
				format.setXml((XmlElement) node);
				format = numberFormats.share(format);
			}
		} catch (Exception ex) {
		}

		try {
			i = 0;
			for (XmlNode node : styles.getFirstElement("fonts").getChildren()) {
				SpreadSheetResource_Font font = fonts.borrowTmp();
				font.setXml((XmlElement) node);
				font = fonts.share(font);
				fontsMapping.put(i++, font.getId());
			}
		} catch (Exception ex) {
		}

		try {
			i = 0;
			for (XmlNode node : styles.getFirstElement("fills").getChildren()) {
				SpreadSheetResource_Fill fill = fills.borrowTmp();
				fill.setXml((XmlElement) node);
				fill = fills.share(fill);
				fillMapping.put(i++, fill.getId());
			}
		} catch (Exception ex) {
		}

		try {
			i = 0;
			for (XmlNode node : styles.getFirstElement("borders").getChildren()) {
				SpreadSheetResource_Border border = borders.borrowTmp();
				border.setXml((XmlElement) node);
				border = borders.share(border);
				bordersMapping.put(i++, border.getId());
			}
		} catch (Exception ex) {
		}

		try {
			for (XmlNode node : styles.getFirstElement("cellStyleXfs").getChildren()) {
				XmlElement e = (XmlElement) node;
				this.getCellStyleXfs(e, fillMapping, bordersMapping, fontsMapping);
			}
		} catch (Exception ex) {
		}

		try {
			i = 0;
			for (XmlNode node : styles.getFirstElement("cellXfs").getChildren()) {
				XmlElement e = (XmlElement) node;
				int new_id = this.addCellXfs(e, fillMapping, bordersMapping, fontsMapping).getId();
				cellXfsMapping.put(i++, new_id);
			}
		} catch (Exception ex) {
		}

		try {
			for (XmlNode node : styles.getFirstElement("cellStyles").getChildren()) {
				SpreadSheetResource_CellStyle cellStyle = new SpreadSheetResource_CellStyle();
				cellStyle.setXml((XmlElement) node);
				cellStyles.add(cellStyle);
			}
		} catch (Exception ex) {
		}

		//Attributes which are loaded as-is
		for (String attr : SUPPORTED_ATTRIBUTES) {
			try {
				XmlElement e = styles.getFirstElement(attr);
				if (e != null)
					loadedAttributes.put(attr, e);
			} catch (Exception ex) {
			}
		}

		return cellXfsMapping;
	}

	//Populates the styles.xml file
	public void toStyles(final XmlElement e) {
		this.setDefaults();
		XmlElement fonts = new XmlElement("fonts").addAttribute("count", this.fonts.getResourcesCount());
		XmlElement fills = new XmlElement("fills").addAttribute("count", this.fills.getResourcesCount());
		XmlElement borders = new XmlElement("borders").addAttribute("count", this.borders.getResourcesCount());
		XmlElement cellStyleXfs = new XmlElement("cellStyleXfs").addAttribute("count", this.cellStyleXfs.getResourcesCount());
		XmlElement cellStyles = new XmlElement("cellStyles").addAttribute("count", this.cellStyles.size());
		XmlElement cellXfs = new XmlElement("cellXfs").addAttribute("count", this.cellXfs.getResourcesCount());
		XmlElement dxfs = this.loadedAttributes.containsKey("dxfs") ? this.loadedAttributes.get("dxfs") : new XmlElement("dxfs").addAttribute("count", "0");
		XmlElement tableStyles = this.loadedAttributes.containsKey("tableStyles") ? this.loadedAttributes.get("tableStyles")
				: new XmlElement("tableStyles").addAttribute("count", "1").addAttribute("defaultPivotStyle", "PivotStyleLight16")
						.addAttribute("defaultTableStyle", "TableStyleMedium2")
						.addChild(new XmlElement("tableStyle").addAttribute("count", "0").addAttribute("name", "simple").addAttribute("pivot", "0"));

		Set<String> knownFonts = new HashSet<String>();
		for (final SpreadSheetResource_Font font : this.fonts.getResourcesInOrder()) {
			fonts.addChild(font.getXml());
			knownFonts.add(font.getName());
		}
		fonts.addAttribute("x14ac:knownFonts", knownFonts.size());

		for (final SpreadSheetResource_Fill fill : this.fills.getResourcesInOrder())
			fills.addChild(fill.getXml());

		for (final SpreadSheetResource_Border border : this.borders.getResourcesInOrder())
			borders.addChild(border.getXml());

		final SpreadSheetResourcePool<SpreadSheetResource_NumberFormat> numberFormats = this.numberFormats;
		XmlElement numFmts = new XmlElement("numFmts").addAttribute("count", numberFormats.getResourcesCount());
		for (final SpreadSheetResource_NumberFormat format : numberFormats.getResourcesInOrder())
			numFmts.addChild(format.getXml());

		for (final SpreadSheetResource_CellStyleXfs i : this.cellStyleXfs.getResourcesInOrder())
			cellStyleXfs.addChild(i.getXml());

		for (final SpreadSheetResource_CellXfs i : this.cellXfs.getResourcesInOrder())
			cellXfs.addChild(i.getXml());

		for (final SpreadSheetResource_CellStyle i : this.cellStyles)
			cellStyles.addChild(i.getXml());

		XmlElement extLst = this.loadedAttributes.containsKey("extLst") ? this.loadedAttributes.get("extLst") : new XmlElement("extLst");

		if (this.numberFormats.getResourcesCount() > 0)
			e.addChild(numFmts);
		e.addChild(fonts);
		e.addChild(fills);
		e.addChild(borders);
		e.addChild(cellStyleXfs);
		if (this.cellXfs.getResourcesCount() > 0)
			e.addChild(cellXfs);
		e.addChild(cellStyles);
		e.addChild(dxfs);
		e.addChild(tableStyles);
		if (this.loadedAttributes.containsKey("colors"))
			e.addChild(this.loadedAttributes.get("colors"));
		e.addChild(extLst);

	}
}
