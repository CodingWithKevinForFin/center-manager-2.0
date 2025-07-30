package com.f1.ami.web;

public class AmiWebHtmlFormatter extends AmiWebBasicWebCellFormatter {

	private AmiWebService service;

	public AmiWebHtmlFormatter(AmiWebService service) {
		this.service = service;
	}
	@Override
	public void formatCellToHtml(Object value, StringBuilder sb, StringBuilder cellStyle) {
		if (value instanceof CharSequence)
			sb.append(service.cleanHtml((CharSequence) value));
		else
			super.formatCellToHtml(value, sb, cellStyle);
	}
	@Override
	public String formatCellToHtml(Object value) {
		String r;
		if (value instanceof CharSequence)
			r = value.toString();
		else
			r = super.formatCellToHtml(value);
		return service.cleanHtml(r);
	}

}
