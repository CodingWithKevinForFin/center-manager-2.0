package com.f1.suite.web.portal.impl.text;

import java.util.ArrayList;
import java.util.List;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.SH;

public class SimpleFastTextPortlet extends FastTextPortlet implements TextModel, TextPortletListener {

	private List<Row> lines = new ArrayList<Row>();
	private int labelWidth = 0;

	public SimpleFastTextPortlet(PortletConfig portletConfig) {
		super(portletConfig, null);
		setTextModel(this);
		this.addListener(this);
	}

	public Row getLine(int line) {
		return lines.get(line);
	}

	@Override
	public int getNumberOfLines(FastTextPortlet portlet) {
		return lines.size();
	}

	@Override
	public void prepareLines(FastTextPortlet portlet, int start, int count) {
	}

	@Override
	public int getLabelWidth(FastTextPortlet portlet) {
		return labelWidth * 8;
	}

	@Override
	public void formatHtml(FastTextPortlet portlet, int lineNumber, StringBuilder sink) {
		WebHelper.escapeHtmlIncludeBackslash(lines.get(lineNumber).text, sink);
	}
	@Override
	public void formatText(FastTextPortlet portlet, int lineNumber, StringBuilder sink) {
		sink.append(lines.get(lineNumber).text);
	}

	@Override
	public void formatLabel(FastTextPortlet portlet, int lineNumber, StringBuilder sink) {
		WebHelper.escapeHtmlIncludeBackslash(lines.get(lineNumber).label, sink);
	}

	@Override
	public void formatStyle(FastTextPortlet portlet, int lineNumber, StringBuilder sink) {
		String style = lines.get(lineNumber).style;
		if (style != null)
			sink.append(style);
	}

	@Override
	public void setColumnsVisible(int columns) {
	}

	public void appendLine(String label, String textstyle) {
		appendLine(label, textstyle, null);
	}
	public void appendLine(String label, String text, String style) {
		if (text.length() > getMaxCharsPerLine())
			setMaxCharsPerLine(text.length());
		this.labelWidth = Math.max(SH.length(label), labelWidth);
		this.lines.add(new Row(label, text, style));
		forceRefresh();
		this.ensureLineVisible(this.lines.size() - 1);
	}

	public class Row {

		public final String style, label, text;

		public Row(String label, String text, String style) {
			this.style = style;
			this.label = label;
			this.text = text;
		}

		public String getStyle() {
			return style;
		}

		public String getLabel() {
			return label;
		}

		public String getText() {
			return text;
		}

	}

	public void clearLines() {
		this.lines.clear();
		this.setMaxCharsPerLine(1);
		this.labelWidth = 0;
		forceRefresh();
	}

	public void setLines(String string) {
		clearLines();
		int cnt = 0;
		for (String line : SH.splitLines(string)) {
			appendLine(SH.toString(++cnt), line);
		}
	}
	@Override
	public void onTextContextMenu(FastTextPortlet portlet, String id) {
		if ("copy".equals(id)) {
			FormPortlet fp = new FormPortlet(generateConfig());
			boolean missing = false;
			StringBuilder buf = new StringBuilder();
			for (int lineNum : getSelectedLines()) {
				buf.append(getLine(lineNum).getText()).append('\n');
			}
			int w = (int) (.8 * getManager().getRoot().getWidth());
			int h = (int) (.8 * getManager().getRoot().getHeight());
			fp.addField(new FormPortletTextAreaField("")).setValue(SH.toStringAndClear(buf)).setHeight(h - 50);
			fp.getFormPortletStyle().setLabelsWidth(0);
			getManager().showDialog("copy", fp, w, h);
			if (missing)
				getManager().showAlert("Not all selected data was available");
		}
	}
	@Override
	public WebMenu createMenu(FastTextPortlet fastTextPortlet) {
		BasicWebMenu r = new BasicWebMenu();
		r.addChild(new BasicWebMenuLink("View for Copy to clipboard", true, "copy"));
		return r;
	}

	@Override
	public boolean onTextUserKeyEvent(FastTextPortlet portlet, KeyEvent keyEvent) {
		return false;
	}

}
