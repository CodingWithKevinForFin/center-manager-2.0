package com.f1.pdf;

import com.f1.utils.ColorHelper;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;

public class PdfHelper {

	public static void showTextAligned(PdfContentByte canvas, int alignment, Phrase phrase, float x, float y) {
		showTextAligned(canvas, alignment, phrase, x, y, 1, 0);
	}
	public static void showTextAligned(PdfContentByte canvas, int alignment, Phrase phrase, float x, float y, int runDirection, int arabicOptions) {
		if (alignment != 0 && alignment != 1 && alignment != 2)
			alignment = 0;
		float w = canvas.getPdfDocument().getPageSize().getWidth();
		float h = canvas.getPdfDocument().getPageSize().getHeight();
		ColumnText ct = new ColumnText(canvas);
		float lly = y - h;
		float ury = y;
		float llx;
		float urx;
		switch (alignment) {
			case Element.ALIGN_LEFT:
				llx = x;
				urx = x + w;
				break;

			case Element.ALIGN_RIGHT:
				llx = x - w;
				urx = x;
				break;

			case Element.ALIGN_CENTER:
			default:
				llx = x - w;
				urx = x + w;
				break;
		}
		ct.setSimpleColumn(llx, lly, urx, ury, 2.0F, alignment);
		ct.addText(phrase);
		if (runDirection == 3)
			if (alignment == 0)
				alignment = 2;
			else if (alignment == 2)
				alignment = 0;
		ct.setAlignment(alignment);
		ct.setArabicOptions(arabicOptions);
		ct.setRunDirection(runDirection);
		try {
			ct.go();
		} catch (DocumentException e) {
			throw new ExceptionConverter(e);
		}
	}
	public static PdfFont setFont(PdfFont sink, String text) {
		if (SH.isnt(text))
			return sink;
		String[] parts = SH.splitContinous(' ', text);
		return setFont(sink, parts);
	}
	public static PdfFont setFont(PdfFont font, String parts[]) {
		PdfFont r = new PdfFont();
		int style = 0;
		boolean resetStyle = false;
		for (String p : parts) {
			if (FontFactory.getRegisteredFonts().contains(p.toLowerCase()) && Font.getFamilyIndex(p) == -1)
				r = new PdfFont(FontFactory.getFont(p, BaseFont.IDENTITY_H, BaseFont.EMBEDDED));
		}

		for (String part : parts) {
			if (isNumber(part)) {
				if (r == null)
					r = new PdfFont(font);
				r.setSize(MH.clip(PdfBuilder.fromInches(SH.parseFloat(part)), 4f, 200f));
			} else if (ColorHelper.isColor(part)) {
				if (r == null)
					r = new PdfFont(font);
				r.setColor(ColorHelper.parseColor(part));
			} else if (Font.getFamilyIndex(part) != -1) {
				if (r == null)
					r = new PdfFont(font);
				r.setFamily(part);
			} else {
				int s = getStyleValue(part);
				if (s == -1)
					continue;
				else if (s == Font.NORMAL)
					resetStyle = true;
				else
					style |= s;
			}
		}
		if (style != 0 || resetStyle) {
			if (r == null)
				r = new PdfFont(font);
			if (resetStyle || font.getStyle() == -1)
				r.setStyle(style);
			else
				r.setStyle(font.getStyle() | style);
		}
		return r == null ? font : r.lockMe();
	}
	public static int getStyleValue(String style) {
		if (style.equalsIgnoreCase("normal"))
			return 0;
		else if (style.equalsIgnoreCase("bold"))
			return Font.BOLD;
		else if (style.equalsIgnoreCase("italic"))
			return Font.ITALIC;
		else if (style.equalsIgnoreCase("oblique"))
			return Font.ITALIC;
		else if (style.equalsIgnoreCase("underline"))
			return Font.UNDERLINE;
		else if (style.equalsIgnoreCase("line-through"))
			return 8;
		else
			return -1;
	}
	public static int parseAlignment(String i, int current) {
		if (SH.startsWithIgnoreCase("left", i))
			return Element.ALIGN_LEFT;
		if (SH.startsWithIgnoreCase("right", i))
			return Element.ALIGN_RIGHT;
		if (SH.startsWithIgnoreCase("center", i))
			return Element.ALIGN_CENTER;
		return current;
	}
	public static int parseAlignmentV(String i, int current) {
		if (SH.startsWithIgnoreCase("top", i))
			return Element.ALIGN_TOP;
		if (SH.startsWithIgnoreCase("bottom", i))
			return Element.ALIGN_BOTTOM;
		if (SH.startsWithIgnoreCase("middle", i))
			return Element.ALIGN_MIDDLE;
		return current;
	}
	public static int parseAlignmentFrom(String[] parts, int current) {
		for (String i : parts) {
			if (SH.startsWithIgnoreCase("left", i))
				return Element.ALIGN_LEFT;
			if (SH.startsWithIgnoreCase("right", i))
				return Element.ALIGN_RIGHT;
			if (SH.startsWithIgnoreCase("center", i))
				return Element.ALIGN_CENTER;
		}
		return current;
	}
	public static int parseAlignmentVFrom(String[] parts, int current) {
		for (String i : parts) {
			if (SH.startsWithIgnoreCase("top", i))
				return Element.ALIGN_TOP;
			if (SH.startsWithIgnoreCase("bottom", i))
				return Element.ALIGN_BOTTOM;
			if (SH.startsWithIgnoreCase("middle", i))
				return Element.ALIGN_MIDDLE;
		}
		return current;
	}
	public static String parseAlignmentToStr(int alignmentCode) {
		switch (alignmentCode) {
			case Element.ALIGN_LEFT:
				return "LEFT";
			case Element.ALIGN_RIGHT:
				return "RIGHT";
			case Element.ALIGN_CENTER:
				return "CENTER";
			default:
				return "DEFAULT";
		}
	}
	public static boolean isNumber(String text) {
		int length = text.length();
		if (length == 0)
			return false;
		boolean hasDecimal = false;
		for (int i = 0; i < length; i++) {
			char c = text.charAt(i);
			if (c == '.') {
				if (hasDecimal || i == length - 1)
					return false;
				hasDecimal = true;
			} else if (c < '0' || c > '9')
				return false;
		}
		return true;
	}
	public static String getFont(PdfFont font) {
		StringBuilder sb = new StringBuilder();
		sb.append(font.getFamilyname()).append(' ');
		sb.append(PdfBuilder.toInches(font.getSize())).append(' ');
		int style = font.getStyle();
		if (style == 0)
			sb.append("normal ");
		else {
			if (MH.areAnyBitsSet(style, Font.BOLD))
				sb.append("bold ");
			if (MH.areAnyBitsSet(style, Font.ITALIC))
				sb.append("italic ");
			if (MH.areAnyBitsSet(style, Font.UNDERLINE))
				sb.append("underline ");
			if (MH.areAnyBitsSet(style, Font.STRIKETHRU))
				sb.append("line-through ");
		}
		sb.append(ColorHelper.toString(font.getColor())).append(' ');
		return sb.toString();
	}
}
