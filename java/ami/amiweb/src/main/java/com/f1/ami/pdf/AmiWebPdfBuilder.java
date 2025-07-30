package com.f1.ami.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.base.Bytes;
import com.f1.base.Table;
import com.f1.pdf.PdfBuilder;
import com.f1.pdf.PdfHelper;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.WebRectangle;
import com.lowagie.text.Element;

public class AmiWebPdfBuilder {

	private PdfBuilder builder;
	private byte[] logo;

	public AmiWebPdfBuilder() {
		reset();
	}

	public void reset() {
		this.builder = new PdfBuilder();
		try {
			this.logo = IOH.readDataFromResource("amiweb/3forgeLogoBlueTransparent.png");
			if (logo == null)
				throw new RuntimeException("resource error");
			this.builder.addCornerIcon(1, logo, null, "https://3forge.com", PdfBuilder.POSITION_UL, .25f, null);
			this.builder.addCornerIcon(1, null, "Page ${page}", null, PdfBuilder.POSITION_UR, .2f, ".12");
			this.builder.setFont(null);
			this.builder.setTableHeaderFont(null);
			this.builder.setHorizontalAlignment("left");
			this.builder.setMarginBelowHeader(.1f);
			this.builder.setMarginAboveFooter(.1f);
			this.builder.setPageMargin(.5f, .5f, .5f, .5f);
			this.builder.setPageSize(8.5f, 11f);
			this.builder.setSpacingBefore(.03f);
			this.builder.setSpacingAfter(.015f);
			this.builder.setLineSpacing(.01f);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	public Bytes build() {
		if (logo == null)
			throw new RuntimeException("resource error");
		ensureStarted();
		Bytes r = new Bytes(this.builder.build());
		reset();
		return r;
	}
	public void setPageSize(Number w, Number h) {
		if (w == null || w.floatValue() < .5)
			w = this.builder.getPageWidth();
		if (h == null || h.floatValue() < .5)
			h = this.builder.getPageHeight();
		this.builder.setPageSize(w.floatValue(), h.floatValue());
	}
	public float getPageHeight() {
		return this.builder.getPageHeight();
	}
	public float getPageWidth() {
		return this.builder.getPageWidth();
	}
	public void setPageMargin(Number t, Number r, Number b, Number l, Number bh, Number af) {
		if (t == null || t.floatValue() < 0)
			t = this.builder.getPageMarginT();
		if (r == null || r.floatValue() < 0)
			r = this.builder.getPageMarginR();
		if (b == null || b.floatValue() < 0)
			b = this.builder.getPageMarginB();
		if (l == null || l.floatValue() < 0)
			l = this.builder.getPageMarginL();
		if (bh == null || bh.floatValue() < 0)
			bh = this.builder.getPageMarginBH();
		if (af == null || af.floatValue() < 0)
			af = this.builder.getPageMarginAF();
		this.builder.setPageMargin(l.floatValue(), r.floatValue(), t.floatValue(), b.floatValue());
		this.builder.setMarginAboveFooter(af.floatValue());
		this.builder.setMarginBelowHeader(bh.floatValue());
	}
	public float getPageMarginTop() {
		return this.builder.getPageMarginT();
	}
	public float getPageMarginRight() {
		return this.builder.getPageMarginR();
	}
	public float getPageMarginBottom() {
		return this.builder.getPageMarginB();
	}
	public float getPageMarginLeft() {
		return this.builder.getPageMarginL();
	}
	public float getPageMarginBelowHeader() {
		return this.builder.getPageMarginBH();
	}
	public float getPageMarginAboveFooter() {
		return this.builder.getPageMarginAF();
	}
	public void appendTable(Table t) {
		ensureStarted();
		if (t == null || t.getColumnsCount() == 0)
			return;
		builder.appendTable(t);
	}
	private void ensureStarted() {
		if (!this.builder.isStarted())
			this.builder.start();
	}

	public void appendPageBreak() {
		ensureStarted();
		this.builder.appendPageBreak();
	}
	public void appendLineBreak() {
		ensureStarted();
		this.builder.appendLineBreak();
	}
	public void appendText(String text) {
		ensureStarted();
		if (text != null && this.builder.columnInProcessFlag == false)
			this.builder.appendText(text);
		if (text != null && this.builder.columnInProcessFlag == true)
			if (this.builder.getFont() == null) {
				this.builder.appendColumnText(text, "Helvetica 0.12 normal #000000");
			} else {
				this.builder.appendColumnText(text, this.builder.getFont());
			}
	}
	public void appendImage(byte[] data, Number width) {
		ensureStarted();
		if (data != null) {
			if (width == null)
				this.builder.appendImage(data);
			else
				this.builder.appendImage(data, width.floatValue());
		}
	}
	public void setFont(String i) {
		this.builder.setFont(i);
	}
	public void setTableCellFont(String i) {
		if (i != null)
			this.builder.setTableCellFont(i);
	}
	public void setHorizontalAlignment(String i) {
		if (i != null)
			this.builder.setHorizontalAlignment(i);
	}
	public String getHorizontalAlignment() {
		return this.builder.getHorizontalAlignment();
	}
	public void setTableHeaderFont(String i) {
		if (i != null)
			this.builder.setTableHeaderFont(i);
	}
	public void setTableHeaderBackground(String i) {
		if (i != null)
			this.builder.setTableHeaderBackground(i);
	}
	public String getTableHeaderBackground() {
		return this.builder.getTableHeaderBackground();
	}
	public void setTableBackground(String i) {
		if (i != null)
			this.builder.setTableBackground(i);
	}
	public String getTableBackground() {
		return this.builder.getTableBackground();
	}
	public void setTableAltRowBackground(String i) {
		if (i != null)
			this.builder.setTableAltRowBackground(i);
	}
	public String getTableAltRowBackground() {
		return this.builder.getTableAltRowBackground();
	}
	public void setTableCellHorizontalAlignment(String i) {
		if (i != null)
			this.builder.setTableCellHorizontalAlignment(i);
	}
	public String getTableCellHorizontalAlignment() {
		return this.builder.getTableCellHorizontalAlignment();
	}
	public void setTableCellPadding(Number t, Number r, Number b, Number l) {
		if (t == null || t.floatValue() < 0)
			t = this.builder.getCellPaddingT();
		if (r == null || r.floatValue() < 0)
			r = this.builder.getCellPaddingR();
		if (b == null || b.floatValue() < 0)
			b = this.builder.getCellPaddingB();
		if (l == null || l.floatValue() < 0)
			l = this.builder.getCellPaddingL();
		this.builder.setTableCellPadding(t.floatValue(), r.floatValue(), b.floatValue(), l.floatValue());
	}
	public float getTableCellPaddingTop() {
		return this.builder.getTableCellPaddingTop();
	}
	public float getTableCellPaddingRight() {
		return this.builder.getTableCellPaddingRight();
	}
	public float getTableCellPaddingBottom() {
		return this.builder.getTableCellPaddingBottom();
	}
	public float getTableCellPaddingLeft() {
		return this.builder.getTableCellPaddingLeft();
	}
	public void setTableWidth(Number t) {
		if (t == null)
			t = -1;
		this.builder.setTableWidth(t.floatValue());
	}
	public float getTableWidth() {
		return this.builder.getTableWidth();
	}
	public void setImageBorder(String t) {
		this.builder.setImageBorder(t);
	}
	public void setTableBorder(String t) {
		this.builder.setTableBorder(t);
	}
	public String getTableBorderColor() {
		return this.builder.getTableBorderColor();
	}
	public float getTableBorderWidth() {
		return this.builder.getTableBorderWidth();
	}
	public void setCellBorderV(String t) {
		this.builder.setCellBorderV(t);
	}
	public String getTableColumnBorderColor() {
		return this.builder.getTableColumnBorderColor();
	}
	public float getTableColumnBorderWidth() {
		return this.builder.getTableColumnBorderWidth();
	}
	public void setCellBorderH(String t) {
		this.builder.setCellBorderH(t);
	}
	public String getTableRowBorderColor() {
		return this.builder.getTableRowBorderColor();
	}
	public float getTableRowBorderWidth() {
		return this.builder.getTableRowBorderWidth();
	}

	public void setSpacing(Number beforeParagraph, Number afterParagraph, Number line) {
		if (beforeParagraph != null)
			this.builder.setSpacingBefore(Math.max(0f, beforeParagraph.floatValue()));
		if (afterParagraph != null)
			this.builder.setSpacingAfter(Math.max(0f, afterParagraph.floatValue()));
		if (line != null)
			this.builder.setLineSpacing(Math.max(0f, line.floatValue()));
	}
	public float getSpacingBefore() {
		return this.builder.getSpacingBefore();
	}
	public float getSpacingAfter() {
		return this.builder.getSpacingAfter();
	}
	public float getLineSpacing() {
		return this.builder.getLineSpacing();
	}
	public void setImageBackground(String string) {
		this.builder.setImageBorder(string);
	}
	public void setHeader(String string, String alignment) {
		this.builder.setImageBorder(string);
	}
	public void addFooter(byte[] image, String string, String alignment, String font, String url, Number heightInches) {
		byte pos;
		switch (PdfHelper.parseAlignment(alignment, Element.ALIGN_CENTER)) {
			case Element.ALIGN_LEFT:
				pos = PdfBuilder.POSITION_LL;
				//this.builder.addCornerIcon(1, logo, null, "https://3forge.com", PdfBuilder.POSITION_UL, .25f, null);
				break;
			case Element.ALIGN_CENTER:
				pos = PdfBuilder.POSITION_LC;
				break;
			case Element.ALIGN_RIGHT:
				pos = PdfBuilder.POSITION_LR;
				break;
			default:
				return;
		}

		if (this.builder.getIsCornerIconOccupied(pos) && (this.builder.getCornerIconUrl(pos) != null) && (this.builder.getCornerIconUrl(pos).equals("https://3forge.com")))
			return;

		if (string != null || image != null)
			this.builder.addCornerIcon(0, image, string, url, pos, heightInches == null ? .25f : heightInches.floatValue(), font);
		else
			this.builder.removeCornerIcon(pos);
	}
	public void addHeader(byte[] image, String string, String alignment, String font, String url, Number heightInches) {
		byte pos;
		switch (PdfHelper.parseAlignment(alignment, Element.ALIGN_CENTER)) {
			case Element.ALIGN_LEFT:
				pos = PdfBuilder.POSITION_UL;
				//this.builder.addCornerIcon(1, logo, null, "https://3forge.com", PdfBuilder.POSITION_LL, .25f, null);
				break;
			case Element.ALIGN_CENTER:
				pos = PdfBuilder.POSITION_UC;
				break;
			case Element.ALIGN_RIGHT:
				pos = PdfBuilder.POSITION_UR;
				break;
			default:
				return;
		}

		if (this.builder.getIsCornerIconOccupied(pos) && (this.builder.getCornerIconUrl(pos) != null) && (this.builder.getCornerIconUrl(pos).equals("https://3forge.com")))
			return;

		if (string != null || image != null)
			this.builder.addCornerIcon(0, image, string, url, pos, heightInches == null ? .25f : heightInches.floatValue(), font);
		else
			this.builder.removeCornerIcon(pos);
	}
	public String getFont() {
		return builder.getFont();
	}
	public String getTableCellFont() {
		return builder.getTableCellFont();
	}
	public String getTableHeaderFont() {
		return builder.getTableHeaderFont();
	}
	public void addCellsStyle(WebRectangle position, String tborder, String rborder, String bborder, String lborder, String font, String backgroundColor) {
		builder.addCellStyle(position, tborder, rborder, bborder, lborder, font, backgroundColor);
	}

	public void clearCellStyles() {
		this.builder.clearCellStyles();
		this.builder.clearColumnWidthWeights();
	}

	public void setColumnWidthWeight(int col, double weight) {
		this.builder.setColumnWidthWeight(col, weight);
	}

	public double getColumnWidthWeight(int col) {
		return this.builder.getColumnWidthWeight(col);
	}
	public void setTextRise(float f) {
		this.builder.setTextRise(f);
	}
	public float getTextRise() {
		return this.builder.getTextRise();
	}
	public void setPageBackground(String i) {
		if (i != null)
			this.builder.setPageBackground(i);
		try {
			byte whichLogo2 = AmiUtils.getBestImageType(this.getPageBackground());
			if (whichLogo2 == AmiUtils.WHITE) {
				this.logo = IOH.readDataFromResource("amiweb/logo-white.png");
			} else if (whichLogo2 == AmiUtils.BLACK) {
				this.logo = IOH.readDataFromResource("amiweb/logo-black.png");
			} else if (whichLogo2 == AmiUtils.COLOR1) {
				this.logo = IOH.readDataFromResource("amiweb/3forgeLogoBlueTransparent.png");
			} else if (whichLogo2 == AmiUtils.COLOR2) {
				this.logo = IOH.readDataFromResource("amiweb/3forgeLogoWhiteTransparent.png");
			} else {
				this.logo = IOH.readDataFromResource("amiweb/3forgeLogoBlueTransparent.png");
			}

			if (logo == null) {
				throw new RuntimeException("resource error");
			}
			this.builder.addCornerIcon(1, logo, null, "https://3forge.com", PdfBuilder.POSITION_UL, .25f, null);

		} catch (

		IOException e) {
			throw OH.toRuntime(e);
		}
	}

	public String getPageBackground() {
		return this.builder.getPageBackground();
	}

	public void spanImage(byte[] data, Number width, Number offset) {
		ensureStarted();
		if (data != null) {
			if (width == null)
				this.builder.spanImage(data, offset);
			else
				this.builder.spanImage(data, width.floatValue(), offset);
		}
	}

	public void createColumn(String content, Number lLX, Number lLY, Number uRX, Number uRY, String style) {
		this.builder.createColumn(content, lLX.floatValue(), lLY.floatValue(), uRX.floatValue(), uRY.floatValue(), style);
	}
	public void addFont(String path, String name) {
		this.builder.addFont(path, name);
	}

	public void addColumn(int numColumns, float gutter) {
		this.appendLineBreak();
		this.builder.addColumn(numColumns, gutter);
	}

	public void endColumn() {
		this.builder.endColumn();
	}
	public void setIndent(Number indent) {
		this.builder.setIndent(indent.floatValue());
	}
	public void move3forgeLogo(String alignment) {
		alignment = alignment.toUpperCase();
		byte pos;
		switch (alignment) {
			case "UPPER LEFT":
				pos = PdfBuilder.POSITION_UL;
				break;
			case "UPPER RIGHT":
				pos = PdfBuilder.POSITION_UR;
				break;
			case "LOWER LEFT":
				pos = PdfBuilder.POSITION_LL;
				break;
			case "LOWER RIGHT":
				pos = PdfBuilder.POSITION_LR;
				break;
			case "UPPER CENTER":
				pos = PdfBuilder.POSITION_UC;
				break;
			case "LOWER CENTER":
				pos = PdfBuilder.POSITION_LC;
				break;
			default:
				pos = PdfBuilder.POSITION_UL;
		}

		//Checks for existing 3forge logos so no duplicates
		List<Byte> list = new ArrayList<>(
				Arrays.asList(PdfBuilder.POSITION_UL, PdfBuilder.POSITION_LL, PdfBuilder.POSITION_UC, PdfBuilder.POSITION_LC, PdfBuilder.POSITION_UR, PdfBuilder.POSITION_LR));

		for (Byte b : list) {
			if (this.builder.getIsCornerIconOccupied(b) && (this.builder.getCornerIconUrl(b) != null) && (this.builder.getCornerIconUrl(b).equals("https://3forge.com"))) {
				this.builder.removeCornerIcon(b);
			}
		}

		this.builder.addCornerIcon(1, logo, null, "https://3forge.com", pos, .25f, null);

	}

}
