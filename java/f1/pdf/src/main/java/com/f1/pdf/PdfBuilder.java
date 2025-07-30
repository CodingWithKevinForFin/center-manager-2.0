package com.f1.pdf;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.base.TableList;
import com.f1.utils.AH;
import com.f1.utils.ColorHelper;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.WebRectangle;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.IntKeyMap;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.MultiColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class PdfBuilder {
	private static final Logger log = LH.get();

	private static final PdfFont DEFAULT_HEADER_FONT = new PdfFont(Font.HELVETICA, 8, Font.NORMAL, Color.WHITE).lockMe();
	public static final PdfFont DEFAULT_FONT = new PdfFont(Font.HELVETICA, 8, Font.NORMAL, Color.BLACK).lockMe();
	private MultiColumnText col = new MultiColumnText();
	public Boolean columnInProcessFlag = false;

	public static class CellStyle {
		public CellStyle(Line tb, Line bb, Line lb, Line rb, PdfFont ft, Color bg, int halign, int valign) {
			this.tBorder = tb;
			this.bBorder = bb;
			this.lBorder = lb;
			this.rBorder = rb;
			this.halign = halign;
			this.valign = valign;
			this.font = ft;
			this.background = bg;
		}

		public final Line tBorder;
		public final Line bBorder;
		public final Line lBorder;
		public final Line rBorder;
		public final PdfFont font;
		public final Color background;
		public final int halign;
		public final int valign;

	}

	static private class Line {

		public Line(Color color, float width) {
			this.color = color;
			this.width = width;
		}
		public Line(String color, float width) {
			this.color = ColorHelper.parseColor(color);
			this.width = width;
		}

		public Color color;
		public float width;

		public Line parse(String t) {
			if (t == null)
				return this;
			for (String part : SH.splitContinous(' ', t)) {
				if (PdfHelper.isNumber(part)) {
					width = Math.max(SH.parseFloat(part), 0f);
				} else if (ColorHelper.isColor(part)) {
					color = ColorHelper.parseColor(part);
				}
			}
			return this;
		}
	}

	final public static byte POSITION_UL = 0;
	final public static byte POSITION_UC = 1;
	final public static byte POSITION_UR = 2;
	final public static byte POSITION_LL = 3;
	final public static byte POSITION_LC = 4;
	final public static byte POSITION_LR = 5;
	private OuterElement outerElements[] = new OuterElement[6];
	private OuterElement outerElementsInOrder[] = new OuterElement[6];

	private class OuterElement implements Comparable<OuterElement> {
		final float height;
		final float width;
		final Image image;
		final String text;
		final String url;
		final byte position;
		private PdfFont font;
		private int priority;

		public OuterElement(int prioirity, byte position, float width, float height, Image image, String text, String url, String font) {
			super();
			this.priority = prioirity;
			this.url = url;
			this.position = position;
			this.width = width;
			this.height = height;
			this.image = image;
			this.text = text;
			float heightPt = fromInches(height);
			this.font = new PdfFont(Font.HELVETICA, heightPt, Font.NORMAL, Color.BLACK).lockMe();
			this.font = PdfHelper.setFont(this.font, font);
			if (this.font.getSize() > heightPt)
				this.font.setSize(heightPt);
			if (image != null) {
				image.scaleAbsoluteWidth(fromInches(width));
				image.scaleAbsoluteHeight(heightPt);
			}
		}
		public void draw(PdfWriter writer, Document d, int page) throws Exception {
			Rectangle p = d.getPageSize();
			if (!doc.isOpen())
				return;
			Phrase phrase = new Phrase();
			if (text != null) {
				phrase.setFont(this.font);
				Chunk chunk = new Chunk(SH.replaceAll(text, "${page}", SH.toString(page)));
				if (url != null)
					chunk.setAnchor(url);
				phrase.add(chunk);
			}
			if (image != null) {
				Chunk chunk = new Chunk(image, 0, 0);
				if (url != null)
					chunk.setAnchor(url);
				phrase.add(chunk);
			}

			float top = 0;
			if (image == null && text != null)
				top = height;
			switch (position) {
				case POSITION_UL:
					PdfHelper.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, phrase, d.leftMargin(), p.getHeight() - fromInches(marginT + top));
					break;
				case POSITION_UC:
					PdfHelper.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, phrase, (p.getWidth() - d.leftMargin() - d.rightMargin()) / 2 + d.leftMargin(),
							p.getHeight() - fromInches(marginT + top));
					break;
				case POSITION_UR:
					PdfHelper.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, phrase, p.getWidth() - d.rightMargin(), p.getHeight() - fromInches(marginT + top));
					break;
				case POSITION_LL:
					PdfHelper.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, phrase, d.leftMargin(), fromInches(marginB + height));
					break;
				case POSITION_LC:
					PdfHelper.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, phrase, (p.getWidth() - d.leftMargin() - d.rightMargin()) / 2 + d.leftMargin(),
							fromInches(marginB + height));
					break;
				case POSITION_LR:
					PdfHelper.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, phrase, p.getWidth() - d.rightMargin(), fromInches(marginB + height));
					break;
			}
		}
		@Override
		public int compareTo(OuterElement o) {
			return OH.compare(this.priority, o.priority);
		}
	}

	private class EventHandler extends PdfPageEventHelper {

		public void onEndPage(PdfWriter writer, Document document) {
			PdfBuilder.this.onEndPage();
		}
	}

	public static final float fromInches(float n) {
		return n * 72;
	}
	public static final float toInches(float n) {
		return n / 72;
	}

	private int page = 0;
	private Document doc;
	private ByteArrayOutputStream buf;
	private PdfWriter writer;
	private PdfFont tableCellFont;
	private PdfFont tableHeaderFont;
	private float marginL;
	private float marginR;
	private float marginT;
	private float marginB;
	private float marginBH;
	private float marginAF;
	private Color tableHeaderBackgroundColor = ColorHelper.parseColor("#555555");
	private Color tableBackgroundColor = Color.white;
	private Color tableAltRowBackgroundColor = ColorHelper.parseColor("#EEEEEE");
	private int tableCellAlignment = Element.ALIGN_CENTER;
	private float cellPaddingT = 2f;
	private float cellPaddingR = 2f;
	private float cellPaddingB = 2f;
	private float cellPaddingL = 2f;
	private int alignmentH = Element.ALIGN_LEFT;
	private float tableWidth = -1;
	private PdfText richText = new PdfText();
	private float pageW;
	private float pageH;
	private Line imageBorder = new Line("#000000", .005f);
	private Line tableBorder = new Line("#000000", .005f);
	private Color imageBackground = null;

	private Line cellBorderH = new Line("#DDDDDD", .0005f);
	private Line cellBorderV = new Line("#333333", .001f);
	private float spacingBefore = .01f;
	private float spacingAfter = .01f;
	private EventHandler eventHandler;
	private Color pageBackground = Color.white;

	public PdfBuilder() {
		tableCellFont = DEFAULT_FONT;
		tableHeaderFont = DEFAULT_HEADER_FONT;
		this.doc = new Document();
		setPageSize(8.5f, 11f);
		setPageMargin(.25f, .25f, .25f, .25f);
		try {
			this.writer = PdfWriter.getInstance(doc, this.buf = new ByteArrayOutputStream());
			this.writer.setPageEvent(this.eventHandler = new EventHandler());
			this.writer.setStrictImageSequence(false);
		} catch (DocumentException e) {
			throw OH.toRuntime(e);
		}
	}

	public void onEndPage() {
		page++;
		try {
			for (OuterElement i : outerElementsInOrder)
				i.draw(writer, doc, page);
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}
	public void setPageSize(float w, float h) {
		this.doc.setPageSize(new Rectangle(fromInches(w), fromInches(h)));
		this.pageW = w;
		this.pageH = h;
	}

	public void setMarginBelowHeader(float belowHeaderMargin) {
		this.marginBH = belowHeaderMargin;
	}
	public void setMarginAboveFooter(float aboveFooterMargin) {
		this.marginAF = aboveFooterMargin;
	}
	public void setPageMargin(float left, float right, float top, float bottom) {
		this.marginL = left;
		this.marginR = right;
		this.marginT = top;
		this.marginB = bottom;
	}

	private void applyMargin() {
		float top = 0, bot = 0;
		for (byte b : new byte[] { POSITION_UL, POSITION_UC, POSITION_UR })
			if (outerElements[b] != null)
				top = Math.max(top, outerElements[b].height);
		for (byte b : new byte[] { POSITION_LL, POSITION_LC, POSITION_LR })
			if (outerElements[b] != null)
				bot = Math.max(bot, outerElements[b].height);
		this.doc.setMargins(fromInches(this.marginL), fromInches(this.marginR), fromInches(this.marginT + top + (top > 0 ? this.marginBH : 0)),
				fromInches(this.marginB + bot + (bot > 0 ? this.marginAF : 0f)));
	}
	private void builderOuterElementsInOrder() {
		this.outerElementsInOrder = AH.removeAll(this.outerElements, null);
		Arrays.sort(outerElementsInOrder);
	}
	public void addCornerIcon(int priority, byte[] image, String text, String url, byte position, float height, String font) {
		Image img = null;
		float width = Float.NaN;
		if (image != null)
			try {
				img = Image.getInstance(image);
				width = height * (img.getWidth() / img.getHeight());
			} catch (Exception e) {
				throw OH.toRuntime(e);
			}
		this.outerElements[position] = new OuterElement(priority, position, width, height, img, text, url, font);
		this.builderOuterElementsInOrder();
		applyMargin();
	}

	public boolean getIsCornerIconOccupied(byte position) {
		return this.outerElements[position] != null;
	}

	public String getCornerIconUrl(byte position) {
		return this.outerElements[position].url;
	}

	public boolean isStarted() {
		return this.doc.isOpen();
	}
	public void start() {
		applyMargin();
		this.doc.open();
	}
	public void appendImage(byte[] image) {
		Image img;
		try {
			img = Image.getInstance(image);
			appendImage(img, img.getWidth());
		} catch (Exception e) {
			LH.warning(log, "PdfBuilder encountered error: " + e);
			throw OH.toRuntime(e);
		}
	}
	public void appendImage(byte[] image, float width) {
		Image img;
		try {
			img = Image.getInstance(image);
			appendImage(img, width);
		} catch (Exception e) {
			LH.warning(log, "PdfBuilder encountered error: " + e);
			throw OH.toRuntime(e);
		}
	}
	public void appendImage(Image img, float width) {
		flushText();

		width = Math.min(width, this.getBodyWidth());
		if (width <= 0)
			return;

		float ratio = img.getHeight() / img.getWidth();
		float origWidth = img.getWidth();
		float newHeight = fromInches(width * ratio);
		img.scaleAbsoluteWidth(fromInches(width));
		img.scaleAbsoluteHeight(newHeight);

		if (this.imageBorder.width > 0) {
			img.setBorder(Rectangle.BOX);
			img.setBorderColor(this.imageBorder.color);
			float w = fromInches(this.imageBorder.width);
			img.setBorderWidth(w * origWidth / fromInches(width));
		}

		Paragraph p = new Paragraph(0f);
		p.setAlignment(this.alignmentH);
		p.setSpacingBefore(fromInches(this.spacingBefore));
		p.setSpacingAfter(fromInches(this.spacingAfter));
		Chunk chunk = new Chunk(img, 0, 0, true);
		img.setSpacingBefore(0);
		img.setSpacingAfter(0);
		if (this.imageBackground != null)
			chunk.setBackground(this.imageBackground, 0, newHeight * -.208f, 0, newHeight * .28f);
		p.add(chunk);
		if (newHeight + p.getSpacingAfter() + p.getSpacingBefore() > (writer.getVerticalPosition(false) - doc.bottomMargin()))
			this.appendPageBreak();
		try {
			doc.add(p);
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}
	private float getBodyWidth() {
		return this.pageW - this.marginL - this.marginR;
	}

	public void appendTable(Table table) {
		try {
			flushText();
			PdfPTable t = new PdfPTable(table.getColumnsCount());
			if (this.tableWidth > 0) {
				float bodyW = this.pageW - this.marginL - marginR;
				t.setWidthPercentage(MH.clip(100f * this.tableWidth / bodyW, .01f, 100f));
			}
			t.setHorizontalAlignment(this.alignmentH);
			PdfPCell cell = new PdfPCell();
			if (table.getRows().size() > 0)
				t.setHeaderRows(1);
			t.setSpacingBefore(fromInches(this.spacingBefore));
			t.setSpacingAfter(fromInches(this.spacingAfter));
			float cellBorderWidthHpts = fromInches(this.cellBorderH.width);
			float cellBorderWidthVpts = fromInches(this.cellBorderV.width);
			float tableBorderWidthPts = fromInches(this.tableBorder.width);

			cell.setHorizontalAlignment(tableCellAlignment);
			cell.setBackgroundColor(this.tableHeaderBackgroundColor);
			cell.setPaddingTop(this.cellPaddingT + tableBorderWidthPts);
			cell.setPaddingBottom(this.cellPaddingB);
			cell.setPaddingLeft(this.cellPaddingL);
			cell.setPaddingRight(this.cellPaddingR);

			cell.setBorderColorTop(this.tableBorder.color);
			cell.setBorderWidthTop(tableBorderWidthPts);
			cell.setBorderColorBottom(this.cellBorderH.color);
			cell.setBorderWidthBottom(cellBorderWidthHpts);
			cell.setBorderColorLeft(this.cellBorderV.color);
			cell.setBorderWidthLeft(cellBorderWidthVpts);
			cell.setBorderColorRight(this.cellBorderV.color);
			cell.setBorderWidthRight(cellBorderWidthVpts);
			for (int i = 0; i < table.getColumnsCount(); i++) {
				if (i == 0) {
					cell.setBorderColorLeft(this.tableBorder.color);
					cell.setBorderWidthLeft(tableBorderWidthPts);
					cell.setBorderColorRight(this.cellBorderV.color);
					cell.setBorderWidthRight(0);
					cell.setPaddingRight(this.cellPaddingR);
					cell.setPaddingLeft(this.cellPaddingL + tableBorderWidthPts);
				} else if (i == 1) {
					cell.setBorderColorLeft(this.cellBorderV.color);
					cell.setBorderWidthLeft(cellBorderWidthVpts);
					cell.setPaddingLeft(this.cellPaddingL);
				}
				if (i == table.getColumnsCount() - 1) {
					cell.setBorderColorRight(this.tableBorder.color);
					cell.setBorderWidthRight(tableBorderWidthPts);
					cell.setPaddingRight(this.cellPaddingR + tableBorderWidthPts);
				}
				Phrase phrase = new Phrase((String) table.getColumnAt(i).getId(), this.tableHeaderFont);
				cell.setPhrase(phrase);
				t.addCell(cell);
			}

			cell.setPaddingTop(this.cellPaddingT);
			cell.setBorderColorTop(this.cellBorderH.color);
			cell.setBorderWidthTop(0);
			cell.setBorderColorBottom(this.cellBorderH.color);
			cell.setBorderWidthBottom(cellBorderWidthHpts);
			cell.setBorderColorLeft(this.cellBorderV.color);
			cell.setBorderWidthLeft(cellBorderWidthVpts);
			cell.setBorderColorRight(this.cellBorderV.color);
			cell.setBorderWidthRight(0);

			TableList rows = table.getRows();
			for (int y = 0; y < rows.size(); y++) {
				Row row = rows.get(y);
				for (int x = 0; x < table.getColumnsCount(); x++) {
					Color bg = (y & 1) == 0 ? this.tableBackgroundColor : this.tableAltRowBackgroundColor;
					if (x == 0) {
						cell.setBorderColorLeft(this.tableBorder.color);
						cell.setBorderWidthLeft(tableBorderWidthPts);
						cell.setBorderColorRight(this.cellBorderV.color);
						cell.setBorderWidthRight(0);
						cell.setPaddingRight(this.cellPaddingR);
						cell.setPaddingLeft(this.cellPaddingL + tableBorderWidthPts);
					} else if (x == 1) {
						cell.setBorderColorLeft(this.cellBorderV.color);
						cell.setBorderWidthLeft(cellBorderWidthVpts);
						cell.setPaddingLeft(this.cellPaddingL);
					}
					if (x == table.getColumnsCount() - 1) {
						cell.setBorderColorRight(this.tableBorder.color);
						cell.setBorderWidthRight(tableBorderWidthPts);
						cell.setPaddingRight(this.cellPaddingR + tableBorderWidthPts);
					}
					if (y == rows.size() - 1) {
						cell.setBorderColorBottom(this.tableBorder.color);
						cell.setBorderWidthBottom(tableBorderWidthPts);
						cell.setPaddingBottom(this.cellPaddingB + tableBorderWidthPts);
					}
					PdfFont font = this.tableCellFont;
					boolean clearBorders = false;
					cell.setHorizontalAlignment(tableCellAlignment);
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					for (Entry<WebRectangle, CellStyle> e : this.cellFormattingOverrides.entrySet()) {
						WebRectangle pos = e.getKey();
						if (pos.containsPoint(x, y)) {
							CellStyle cs = e.getValue();
							if (pos.getLeft() == x && cs.lBorder != null) {
								cell.setBorderWidthLeft(cs.lBorder.width);
								cell.setBorderColorLeft(cs.lBorder.color);
								clearBorders = true;
							}
							if (pos.getRight() == x + 1 && cs.rBorder != null) {
								cell.setBorderWidthRight(cs.rBorder.width);
								cell.setBorderColorRight(cs.rBorder.color);
								clearBorders = true;
							}
							if (pos.getTop() == y && cs.tBorder != null) {
								cell.setBorderWidthTop(cs.tBorder.width);
								cell.setBorderColorTop(cs.tBorder.color);
								clearBorders = true;
							}
							if (pos.getBottom() == y + 1 && cs.bBorder != null) {
								cell.setBorderWidthBottom(cs.bBorder.width);
								cell.setBorderColorBottom(cs.bBorder.color);
								clearBorders = true;
							}
							if (cs.halign != Element.ALIGN_UNDEFINED)
								cell.setHorizontalAlignment(cs.halign);
							if (cs.valign != Element.ALIGN_UNDEFINED)
								cell.setVerticalAlignment(cs.valign);
							if (cs.font != null)
								font = cs.font;
							if (cs.background != null)
								bg = cs.background;
						}
					}
					cell.setBackgroundColor(bg);
					Object at = row.getAt(x);
					if (at instanceof PdfText) {
						cell.addElement(((PdfText) at).getParagraph());
						t.addCell(cell);
						cell.setColumn(new ColumnText(null));
					} else {
						cell.setPhrase(new Phrase(Caster_String.INSTANCE.cast(at), font));
						t.addCell(cell);
					}
					if (clearBorders) {
						cell.setBorderColorTop(this.cellBorderH.color);
						cell.setBorderWidthTop(0);
						cell.setBorderColorBottom(this.cellBorderH.color);
						cell.setBorderWidthBottom(cellBorderWidthHpts);
						cell.setBorderColorLeft(this.cellBorderV.color);
						cell.setBorderWidthLeft(cellBorderWidthVpts);
						cell.setBorderColorRight(this.cellBorderV.color);
						cell.setBorderWidthRight(0);
					}
				}
			}

			final float[] weights = new float[table.getColumnsCount()];
			for (int i = 0; i < weights.length; i++)
				weights[i] = (float) getColumnWidthWeight(i);
			t.setWidths(weights);
			doc.add(t);

		} catch (

		Exception e) {
			LH.warning(log, "PdfBuilder encountered error: " + e);
			throw OH.toRuntime(e);
		}
	}

	private void flushText() {
		this.richText.flushText(this.doc);
	}

	public byte[] build() {
		flushText();
		this.onEndPage();
		doc.close();
		writer.close();
		return buf.toByteArray();
	}
	public void appendPageBreak() {
		flushText();
		this.doc.newPage();
	}
	public void appendLineBreak() {
		flushText();
	}

	public void setTableCellFont(String i) {
		this.tableCellFont = PdfHelper.setFont(this.tableCellFont, i);
	}
	public void setTableHeaderFont(String i) {
		this.tableHeaderFont = SH.isnt(i) ? DEFAULT_HEADER_FONT : PdfHelper.setFont(this.tableHeaderFont, i);
	}
	public void setTableHeaderBackground(String i) {
		this.tableHeaderBackgroundColor = ColorHelper.parseColorNoThrow(i);
	}
	public String getTableHeaderBackground() {
		return ColorHelper.toString(this.tableHeaderBackgroundColor);
	}
	public void setTableBackground(String i) {
		this.tableBackgroundColor = ColorHelper.parseColorNoThrow(i);
	}
	public String getTableBackground() {
		return ColorHelper.toString(this.tableBackgroundColor);
	}
	public void setTableAltRowBackground(String i) {
		this.tableAltRowBackgroundColor = ColorHelper.parseColorNoThrow(i);
	}
	public String getTableAltRowBackground() {
		return ColorHelper.toString(this.tableAltRowBackgroundColor);
	}
	public void setTableCellHorizontalAlignment(String i) {
		this.tableCellAlignment = PdfHelper.parseAlignment(i, this.tableCellAlignment);
	}
	public String getTableCellHorizontalAlignment() {
		return PdfHelper.parseAlignmentToStr(this.tableCellAlignment);
	}
	public void setHorizontalAlignment(String i) {
		flushText();
		this.alignmentH = PdfHelper.parseAlignment(i, this.alignmentH);
		this.richText.setHorizontalAlignment(i);
	}
	public String getHorizontalAlignment() {
		return PdfHelper.parseAlignmentToStr(this.alignmentH);
	}
	public void setTableCellPadding(float t, float r, float b, float l) {
		this.cellPaddingT = t;
		this.cellPaddingR = r;
		this.cellPaddingB = b;
		this.cellPaddingL = l;
	}
	public float getTableCellPaddingTop() {
		return this.cellPaddingT;
	}
	public float getTableCellPaddingRight() {
		return this.cellPaddingR;
	}
	public float getTableCellPaddingBottom() {
		return this.cellPaddingB;
	}
	public float getTableCellPaddingLeft() {
		return this.cellPaddingL;
	}
	public void setTableWidth(float t) {
		this.tableWidth = t;
	}
	public float getTableWidth() {
		return this.tableWidth;
	}
	public float getPageWidth() {
		return this.pageW;
	}
	public float getPageHeight() {
		return this.pageH;
	}

	public float getPageMarginT() {
		return this.marginT;
	}
	public float getPageMarginB() {
		return this.marginB;
	}
	public float getPageMarginL() {
		return this.marginL;
	}
	public float getPageMarginR() {
		return this.marginR;
	}
	public float getPageMarginAF() {
		return this.marginAF;
	}
	public float getPageMarginBH() {
		return this.marginBH;
	}

	public float getCellPaddingT() {
		return this.cellPaddingT;
	}
	public float getCellPaddingB() {
		return this.cellPaddingB;
	}
	public float getCellPaddingL() {
		return this.cellPaddingL;
	}
	public float getCellPaddingR() {
		return this.cellPaddingR;
	}

	public void setImageBorder(String t) {
		this.imageBorder.parse(t);
	}
	public void setTableBorder(String t) {
		this.tableBorder.parse(t);
	}
	public String getTableBorderColor() {
		return ColorHelper.toString(this.tableBorder.color);
	}
	public float getTableBorderWidth() {
		return this.tableBorder.width;
	}
	public void setCellBorderV(String t) {
		this.cellBorderV.parse(t);
	}
	public String getTableColumnBorderColor() {
		return ColorHelper.toString(cellBorderV.color);
	}
	public float getTableColumnBorderWidth() {
		return this.cellBorderV.width;
	}
	public void setCellBorderH(String t) {
		this.cellBorderH.parse(t);
	}
	public String getTableRowBorderColor() {
		return ColorHelper.toString(cellBorderH.color);
	}
	public float getTableRowBorderWidth() {
		return this.cellBorderH.width;
	}
	public void removeCornerIcon(byte pos) {
		this.outerElements[pos] = null;
		this.builderOuterElementsInOrder();
		applyMargin();
	}
	public void setSpacingBefore(float t) {
		this.spacingBefore = t;
		this.richText.setSpacingBefore(t);
	}
	public float getSpacingBefore() {
		return this.spacingBefore;
	}
	public void setSpacingAfter(float t) {
		this.spacingAfter = t;
		this.richText.setSpacingAfter(t);
	}
	public float getSpacingAfter() {
		return this.spacingAfter;
	}
	public String getTableCellFont(String i) {
		return PdfHelper.getFont(this.tableCellFont);
	}
	public String getTableCellFont() {
		return PdfHelper.getFont(this.tableCellFont);
	}
	public String getTableHeaderFont() {
		return PdfHelper.getFont(this.tableHeaderFont);
	}

	private IntKeyMap<Double> columnWidthWeights = new IntKeyMap<Double>();
	private LinkedHashMap<WebRectangle, CellStyle> cellFormattingOverrides = new LinkedHashMap<WebRectangle, CellStyle>();

	public void addCellStyle(WebRectangle position, String tborder, String rborder, String bborder, String lborder, String font, String backgroundColor) {
		if (position.getWidth() <= 0 || position.getHeight() <= 0)
			return;
		Line tb = parseLine(tborder, this.cellBorderH);
		Line bb = parseLine(bborder, this.cellBorderH);
		Line lb = parseLine(lborder, this.cellBorderV);
		Line rb = parseLine(rborder, this.cellBorderV);
		String[] fontParts = SH.splitContinous(' ', font);
		int h = PdfHelper.parseAlignmentFrom(fontParts, Element.ALIGN_UNDEFINED);
		int v = PdfHelper.parseAlignmentVFrom(fontParts, Element.ALIGN_UNDEFINED);
		PdfFont ft = SH.is(font) ? PdfHelper.setFont(this.tableCellFont, fontParts) : null;
		Color bg = ColorHelper.parseColorNoThrow(backgroundColor);
		if (tb == null && bb == null && lb == null && rb == null && ft == null)
			return;
		cellFormattingOverrides.put(position.clone(), new CellStyle(tb, bb, lb, rb, ft, bg, h, v));
	}
	private Line parseLine(String s, Line template) {
		if (SH.isnt(s))
			return null;
		final Line l = template == null ? new Line(Color.BLACK, 0) : new Line(template.color, template.width);
		return l.parse(s);
	}
	public void setColumnWidthWeight(int col, double weight) {
		if (col >= 0 && weight > 0)
			this.columnWidthWeights.put(col, weight);
	}
	public double getColumnWidthWeight(int col) {
		Double r = this.columnWidthWeights.get(col);
		return r == null ? 1d : r.doubleValue();
	}

	public void clearCellStyles() {
		this.tableCellFont = DEFAULT_FONT;
		this.cellFormattingOverrides.clear();
	}
	public void clearColumnWidthWeights() {
		this.columnWidthWeights.clear();
	}

	public void setFont(String string) {
		this.richText.setFont(string);
	}
	public String getFont() {
		return this.richText.getFont();
	}
	public void setIndent(float indent) {
		flushText();
		this.richText.setIndent(indent);
	}
	public void appendText(String string) {
		this.richText.appendText(string);
	}
	public void setLineSpacing(float f) {
		this.richText.setLineSpacing(f);
	}
	public float getLineSpacing() {
		return this.richText.getLineSpacing();
	}
	public void setTextRise(float f) {
		this.richText.setTextRise(f);
	}
	public float getTextRise() {
		return this.richText.getTextRise();
	}
	public void setPageBackground(String i) {
		this.pageBackground = ColorHelper.parseColorNoThrow(i);
		Rectangle pageSize = this.doc.getPageSize();
		pageSize.setBackgroundColor(pageBackground);
		this.doc.setPageSize(pageSize);
	}
	public String getPageBackground() {
		return ColorHelper.toString(this.pageBackground);
	}

	public void spanImage(byte[] image, float width, Number offset) {
		Image img;
		try {
			img = Image.getInstance(image);
			spanImage(img, width, offset);
		} catch (Exception e) {
			LH.warning(log, "PdfBuilder encountered error: " + e);
			throw OH.toRuntime(e);
		}
	}

	public void spanImage(byte[] image, Number offset) {
		Image img;
		try {
			img = Image.getInstance(image);
			spanImage(img, img.getWidth(), offset);
		} catch (Exception e) {
			LH.warning(log, "PdfBuilder encountered error: " + e);
			throw OH.toRuntime(e);
		}
	}

	public void spanImage(Image img, float width, Number offset) {
		flushText();
		width = Math.min(width, this.getBodyWidth());
		if (width <= 0)
			return;
		float ratio = img.getHeight() / img.getWidth();
		float origWidth = img.getWidth();
		float newHeight = fromInches(width * ratio);
		img.scaleAbsoluteWidth(fromInches(width));
		img.scaleAbsoluteHeight(newHeight);
		if (this.imageBorder.width > 0) {
			img.setBorder(Rectangle.BOX);
			img.setBorderColor(this.imageBorder.color);
			float w = fromInches(this.imageBorder.width);
			img.setBorderWidth(w * origWidth / fromInches(width));
		}
		Chunk chunk = new Chunk(img, fromInches(offset.floatValue()), 0, true);
		if (this.imageBackground != null)
			chunk.setBackground(this.imageBackground, 0, newHeight * -.208f, 0, newHeight * .28f);
		if (newHeight > (writer.getVerticalPosition(false) - doc.bottomMargin()))
			this.appendPageBreak();
		try {
			doc.add(chunk);
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}

	public void createColumn(String p, float lLeftX, float lLeftY, float uRightX, float uRightY, String style) {
		if (doc.isOpen() != true) {
			doc.open();
		}
		PdfFont pf = PdfHelper.setFont(new PdfFont(1), style);
		Chunk text = new Chunk(p, pf);
		Phrase ph = new Phrase(text);
		PdfContentByte cb = writer.getDirectContent();
		ColumnText ct = new ColumnText(cb);
		ct.setSimpleColumn(lLeftX, lLeftY, uRightX, uRightY);
		ct.addText(ph);
		ct.go();
	}

	public void addFont(String path, String name) {
		FontFactory.register(path, name);
	}

	public void addColumn(int numColumns, float gutter) {
		if (doc.isOpen() != true) {
			doc.open();
		}
		this.columnInProcessFlag = true;
		col = new MultiColumnText();
		float pageWidth = this.doc.getPageSize().getWidth();
		col.addRegularColumns(pageWidth * 0.10f, pageWidth * 0.9f, pageWidth * gutter, numColumns);
	}

	public void appendColumnText(String content, String style) {
		PdfFont pf = PdfHelper.setFont(new PdfFont(1), style);
		Chunk text = new Chunk(content, pf);
		Phrase ph = new Phrase(text);
		col.addText(ph);
	}

	public void endColumn() {
		if (columnInProcessFlag) {
			this.doc.add(col);
			this.columnInProcessFlag = false;
		}
	}
}