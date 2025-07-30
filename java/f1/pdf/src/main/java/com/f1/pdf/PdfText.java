package com.f1.pdf;

import com.f1.utils.OH;
import com.f1.utils.SH;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;

public class PdfText {

	private PdfFont font = PdfBuilder.DEFAULT_FONT;
	private float textRise = 0;
	private Paragraph textBuffer;
	private float spacingBefore = .01f;
	private float spacingAfter = .01f;
	private float lineSpacing = .02f;
	private int alignmentH = Element.ALIGN_LEFT;
	private float indent = 0f;

	private StringBuilder toString = new StringBuilder();

	public PdfText(String text) {
		appendText(text);
	}
	public PdfText() {
	}
	public void setTextRise(float textRise) {
		if (textRise == this.textRise)
			return;
		this.textRise = textRise;
	}
	public float getTextRise() {
		return this.textRise;
	}
	private void initTextBuffer() {
		if (this.textBuffer == null) {
			this.textBuffer = new Paragraph(this.font.getSize() + PdfBuilder.fromInches(this.lineSpacing));
			this.textBuffer.setAlignment(this.alignmentH);
			this.textBuffer.setSpacingBefore(PdfBuilder.fromInches(this.spacingBefore));
			this.textBuffer.setSpacingAfter(PdfBuilder.fromInches(this.spacingAfter));
			this.textBuffer.setIndentationLeft(PdfBuilder.fromInches(this.indent));
		}
	}
	public void appendText(String text) {
		initTextBuffer();
		Phrase phrase;
		if (this.textRise != 0) {
			if (textRise <= -1f || textRise >= 1f)
				return;
			float origSize = this.font.getSize();
			float tr = origSize * textRise;
			PdfFont f = new PdfFont(this.font);
			f.setSize(this.font.getSize() - Math.abs(tr));
			f.lockMe();
			Chunk chunk = new Chunk(text).setTextRise(tr);
			chunk.setFont(f);
			phrase = new Phrase(chunk);
		} else
			phrase = new Phrase(text, font);
		textBuffer.add(phrase);
		toString.append(text);
	}
	public void setFont(String i) {
		float oldSize = this.font.getSize();
		this.font = SH.isnt(i) ? PdfBuilder.DEFAULT_FONT : PdfHelper.setFont(this.font, i);
		float nuwSize = this.font.getSize();
		if (this.textBuffer != null && nuwSize > oldSize) {
			this.textBuffer.setLeading(Math.max(this.textBuffer.getLeading(), nuwSize + PdfBuilder.fromInches(this.lineSpacing)));
		}
	}
	public String getFont() {
		return PdfHelper.getFont(this.font);
	}
	public void flushText(Document sink) {
		if (this.textBuffer != null) {
			try {
				sink.add(textBuffer);
				textBuffer = null;
			} catch (DocumentException e) {
				throw OH.toRuntime(e);
			}
		}
	}
	public void setSpacingBefore(float t) {
		this.spacingBefore = t;
	}
	public float getSpacingBefore() {
		return this.spacingBefore;
	}
	public void setSpacingAfter(float t) {
		this.spacingAfter = t;
	}
	public float getSpacingAfter() {
		return this.spacingAfter;
	}
	public void setLineSpacing(float t) {
		this.lineSpacing = t;
	}
	public float getLineSpacing() {
		return this.lineSpacing;
	}
	public void setHorizontalAlignment(String i) {
		this.alignmentH = PdfHelper.parseAlignment(i, this.alignmentH);
	}
	public String getHorizontalAlignment() {
		return PdfHelper.parseAlignmentToStr(this.alignmentH);
	}
	public Paragraph getParagraph() {
		initTextBuffer();
		return this.textBuffer;
	}
	public void setIndent(float indent) {
		this.indent = indent;
	}
	public float getIndent() {
		return this.indent;
	}
	@Override
	public String toString() {
		return this.toString.toString();
	}
}
