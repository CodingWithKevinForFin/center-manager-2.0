package com.f1.pdf;

import java.awt.Color;

import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;

public class PdfFont extends Font implements Lockable {

	private boolean isLocked;

	public PdfFont() {
		super();
	}

	public PdfFont(BaseFont bf, float size, int style, Color color) {
		super(bf, size, style, color);
	}

	public PdfFont(BaseFont bf, float size, int style) {
		super(bf, size, style);
	}

	public PdfFont(BaseFont bf, float size) {
		super(bf, size);
	}

	public PdfFont(BaseFont bf) {
		super(bf);
	}

	public PdfFont(Font other) {
		super(other);
	}

	public PdfFont(int family, float size, int style, Color color) {
		super(family, size, style, color);
	}

	public PdfFont(int family, float size, int style) {
		super(family, size, style);
	}

	public PdfFont(int family, float size) {
		super(family, size);
	}

	public PdfFont(int family) {
		super(family);
	}

	@Override
	public void setColor(Color color) {
		super.setColor(color);
	}

	@Override
	public void setFamily(String family) {
		LockedException.assertNotLocked(this);
		super.setFamily(family);
	}

	@Override
	public void setSize(float size) {
		LockedException.assertNotLocked(this);
		super.setSize(size);
	}

	@Override
	public void setColor(int red, int green, int blue) {
		LockedException.assertNotLocked(this);
		super.setColor(red, green, blue);
	}

	@Override
	public void setStyle(String style) {
		LockedException.assertNotLocked(this);
		super.setStyle(style);
	}

	@Override
	public void setStyle(int style) {
		LockedException.assertNotLocked(this);
		super.setStyle(style);
	}

	public PdfFont lockMe() {
		this.isLocked = true;
		return this;
	}

	@Override
	public void lock() {
		this.isLocked = true;
	}

	@Override
	public boolean isLocked() {
		return this.isLocked;
	}

}
