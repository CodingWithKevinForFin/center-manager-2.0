package com.f1.suite.web.portal.impl.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.suite.web.JsFunction;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuLink;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.impl.AbstractPortlet;
import com.f1.suite.web.portal.impl.BasicPortletSchema;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.structs.IntSet;

public class FastTextPortlet extends AbstractPortlet {
	public static final Logger log = LH.get();

	private static final int DEFAULT_MAX_LINE_LENGTH = 10000;
	private int rowHeight = 16;
	private boolean needsRefresh;
	private TextModel model;
	private int maxCharsPerLine = DEFAULT_MAX_LINE_LENGTH;
	private IntSet linesSent = new IntSet();
	private List<TextPortletListener> listeners = new ArrayList<TextPortletListener>();

	public FastTextPortlet(PortletConfig portletConfig, TextModel model) {
		super(portletConfig);
		setTextModel(model);
	}
	protected void setTextModel(TextModel model) {
		needsRefresh = true;
		flagPendingAjax();
		this.model = model;
	}

	@Override
	public void setVisible(boolean isVisible) {
		if (isVisible) {
			needsRefresh = true;
			scrollMarksChanged = true;
			flagPendingAjax();
		}
		super.setVisible(isVisible);
	}

	private int currentTop = -1;
	private int currentBottom = -1;
	private int visibleTop = -1;
	private int visibleBottom = -1;
	private String selectedText;
	private int[] selectedLines;

	private int columnsVisible;

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("clip".equals(callback)) {
			visibleTop = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "top");
			visibleBottom = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "bot");
			flagPendingAjax();
		} else if ("colsVisible".equals(callback)) {
			int cols = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "cols");
			this.columnsVisible = cols;
			this.model.setColumnsVisible(cols);
		} else if ("select".equals(callback)) {
			this.selectedText = CH.getOrThrow(Caster_String.INSTANCE, attributes, "selected");
			this.selectedLines = null;
		} else if ("showMenu".equals(callback)) {
			WebMenu menu = model.createMenu(this);
			if (menu != null) {
				Map<String, Object> menuModel = PortletHelper.menuToJson(getManager(), menu);
				this.callJsFunction("showContextMenu").addParamJson(menuModel).end();
			}
		} else if ("menuitem".equals(callback)) {
			WebMenuLink action = getManager().getMenuManager().fireLinkForId(CH.getOrThrow(attributes, "action"));
			if (action != null)
				for (TextPortletListener i : this.listeners)
					i.onTextContextMenu(this, action.getAction());
		} else {
			super.handleCallback(callback, attributes);
		}
	}

	private int moveToLine = -1;
	private int padding = 50;

	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (!getVisible())
			return;
		int lines = model.getNumberOfLines(this);
		if (needsRefresh && getVisible()) {
			this.moveToLine = this.currentTop;
			this.currentTop = this.currentBottom = -1;
			this.linesSent.clear();
			needsRefresh = false;
			callJsFunction("init").addParam(lines).addParam(rowHeight).addParam(maxCharsPerLine).addParam(model.getLabelWidth(this)).addParamQuoted(labelStyle)
					.addParamQuoted(style).end();
			callJsFunction("setSelectedLines").addParamQuoted(SH.noNull(this.selectedText)).end();
		}
		generateScrollMarks();
		if (this.moveToLine != -1) {
			if (this.moveToLine >= lines) {
				this.moveToLine = lines - 1;
				this.visibleBottom = (this.visibleBottom - this.visibleTop) + moveToLine;
				this.visibleTop = moveToLine;
			}
			callJsFunction("setTopLine").addParam(this.moveToLine).end();
			this.moveToLine = -1;
		}
		if (this.visibleTop != this.currentTop || this.visibleBottom != this.currentBottom) {
			int start = Math.max(this.visibleTop - this.padding, 0);
			int end = Math.min(this.visibleBottom + this.padding, lines);
			this.currentTop = this.visibleTop;
			this.currentBottom = this.visibleBottom;

			while (start < end)
				if (!this.linesSent.contains(start))
					break;
				else
					start++;
			if (start < end) {
				int reqStart = -1, reqEnd = -1;
				for (int i = start; i < end; i++) {
					if (this.linesSent.contains(i))
						continue;
					if (reqStart == -1)
						reqStart = i;
					reqEnd = i;
				}
				if (reqStart != -1) {
					this.model.prepareLines(this, reqStart, 1 + reqEnd - reqStart);
					JsFunction func = callJsFunction("setData");
					JsonBuilder json = func.startJson();
					json.startList();
					StringBuilder sb = json.getStringBuilder();
					for (int pos = reqStart; pos < 1 + reqEnd; pos++) {
						if (pos >= start && pos <= end && this.linesSent.add(pos)) {
							json.startMap();
							json.addKeyValue("p", pos);

							json.addKey("l");
							json.startQuote();
							try {
								model.formatLabel(this, pos, sb);
							} catch (Exception e) {
								LH.warning(log, "Error formatting label at line ", pos, e);
							}
							json.endQuote();

							json.addKey("v");
							json.startQuote();
							try {
								model.formatHtml(this, pos, sb);
							} catch (Exception e) {
								LH.warning(log, "Error formatting text at line ", pos, e);
							}
							json.endQuote();

							json.addKey("s");
							json.startQuote();
							try {
								model.formatStyle(this, pos, sb);
							} catch (Exception e) {
								LH.warning(log, "Error formatting style at line ", pos, e);
							}
							json.endQuote();
							json.endMap();
						}
					}
					json.endList();
					json.close();
					func.end();
				}
			}
		}
	}
	public void resetLinesAt(int position) {
		boolean in = false;
		in = in || OH.isBetween(position, this.visibleTop, this.visibleBottom);
		this.linesSent.remove(position);
		if (in)
			flagPendingAjax();
	}
	public void resetLinesAt(int... positions) {
		boolean in = false;
		for (int position : positions) {
			in = in || OH.isBetween(position, this.visibleTop, this.visibleBottom);
			this.linesSent.remove(position);
		}
		if (in)
			flagPendingAjax();
	}

	@Override
	public void initJs() {
		super.initJs();
		flagPendingAjax();
	}

	public static final PortletSchema<FastTreePortlet> SCHEMA = new BasicPortletSchema<FastTreePortlet>("Text", "TextPortlet", FastTreePortlet.class, false, true);
	private static final char[] DASH_OR_COMMA = new char[] { '-', ',' };

	@Override
	public PortletSchema<?> getPortletSchema() {
		return SCHEMA;
	}

	public int getRowHeight() {
		return rowHeight;
	}

	public void forceRefresh() {
		needsRefresh = true;
		flagPendingAjax();
	}
	public void setRowHeight(int rowHeight) {
		needsRefresh = true;
		flagPendingAjax();
		this.rowHeight = rowHeight;
	}
	public int getMaxCharsPerLine() {
		return maxCharsPerLine;
	}
	public void setMaxCharsPerLine(int maxLength) {
		if (this.maxCharsPerLine == maxLength)
			return;
		needsRefresh = true;
		flagPendingAjax();
		this.maxCharsPerLine = maxLength;
	}

	public int getLinesVisible() {
		return this.visibleBottom - this.visibleTop - 2;
	}
	public void ensureLineVisible(int lineNumber) {
		ensureLineVisible(lineNumber, 1);
	}
	public void ensureLineVisible(int lineNumber, int padding) {
		if (padding > getLinesVisible() / 2)
			padding = getLinesVisible() / 2;
		if (lineNumber - padding < this.visibleTop) {
			moveToLineTop(lineNumber - padding);
		} else if (lineNumber + padding >= this.visibleBottom - 3) {
			moveToLineTop(lineNumber + padding - getLinesVisible() + 2);
		}
	}
	public void moveToLineTop(int lineNumber) {
		int topLine = lineNumber;
		if (topLine >= model.getNumberOfLines(this))
			topLine = model.getNumberOfLines(this) - 1;
		if (topLine < 0)
			topLine = 0;
		this.moveToLine = topLine;
		flagPendingAjax();
	}
	public int getTopLineVisible() {
		return this.visibleTop;
	}
	public int getBottomLineVisible() {
		return this.visibleBottom;
	}

	public int getTopLineSelected() {
		if (AH.isntEmpty(this.selectedLines))
			return selectedLines[0];
		if (SH.isnt(this.selectedText))
			return -1;
		int i = SH.indexOfFirst(this.selectedText, 0, DASH_OR_COMMA);
		return Integer.parseInt(i == -1 ? this.selectedText : this.selectedText.substring(0, i));
	}

	public int getBottomLineSelected() {
		if (AH.isntEmpty(this.selectedLines))
			return AH.last(selectedLines, -1);
		if (SH.isnt(this.selectedText))
			return -1;
		int i = SH.indexOfLast(this.selectedText, this.selectedText.length(), DASH_OR_COMMA);
		return Integer.parseInt(i == -1 ? this.selectedText : this.selectedText.substring(i + 1));
	}

	public int[] getSelectedLines() {
		if (this.selectedLines != null)
			return this.selectedLines;
		if (SH.isnt(this.selectedText))
			return this.selectedLines = OH.EMPTY_INT_ARRAY;
		String[] parts = SH.split(',', selectedText);
		int ranges[] = new int[parts.length * 2];
		int cnt = 0, i = 0;
		for (String s : parts) {
			int start, end;
			if (s.indexOf('-') == -1) {
				start = end = Integer.parseInt(s);
			} else {
				start = Integer.parseInt(SH.beforeFirst(s, '-'));
				end = Integer.parseInt(SH.afterFirst(s, '-'));
			}
			cnt += end - start + 1;
			ranges[i++] = start;
			ranges[i++] = end;
		}
		this.selectedLines = new int[cnt];
		int rowsCount = this.model.getNumberOfLines(this);
		int j = 0;
		for (i = 0; i < ranges.length; i += 2) {
			int loc = ranges[i], end = ranges[i + 1];
			while (loc <= end && loc < rowsCount)
				this.selectedLines[j++] = loc++;
		}
		return this.selectedLines;
	}
	public void selectLineEnsureVisible(int line) {
		selectLines(line, line);
		if (line != -1)
			ensureLineVisible(line);
	}
	public void selectLine(int line) {
		selectLines(line, line);
	}
	public void selectLines(int start, int end) {
		if (start == -1 || end == -1)
			this.selectedText = null;
		else
			this.selectedText = start + "-" + end;
		this.selectedLines = null;
		if (getVisible())
			callJsFunction("setSelectedLines").addParamQuoted(SH.noNull(this.selectedText)).end();
	}

	public void addListener(TextPortletListener listener) {
		this.listeners.add(listener);
	}
	public void removeListener(TextPortletListener listener) {
		this.listeners.remove(listener);
	}
	public int getColumnsVisible() {
		return columnsVisible;
	}

	private Map<String, IntSet> scrollbarMarks = new LinkedHashMap<String, IntSet>();

	private boolean scrollMarksChanged;

	private String style = "";
	private String labelStyle = "";

	public void addScrollbarMark(int pos, String color) {
		IntSet positions = scrollbarMarks.get(color);
		if (positions == null) {
			if (scrollbarMarks.size() > 15)
				throw new IndexOutOfBoundsException("can only represent up to 15 categories");
			scrollbarMarks.put(color, positions = new IntSet());
		}
		if (!positions.add(pos))
			return;
		scrollMarksChanged = true;
		flagPendingAjax();
	}

	public boolean removeScrollbarMark(int pos, String color) {
		IntSet positions = scrollbarMarks.get(color);
		if (positions == null)
			return false;
		if (!positions.remove(pos))
			return false;
		if (positions.isEmpty())
			scrollbarMarks.remove(color);
		scrollMarksChanged = true;
		flagPendingAjax();
		return true;
	}

	public void clearScrollMarks() {
		if (scrollbarMarks.isEmpty())
			return;
		scrollMarksChanged = true;
		flagPendingAjax();
		this.scrollbarMarks.clear();
	}
	public void generateScrollMarks() {
		if (!scrollMarksChanged)
			return;
		scrollMarksChanged = false;
		JsFunction func = callJsFunction("setScrollTicks");
		JsonBuilder json = func.startJson();
		int linesCount = model.getNumberOfLines(this);
		int pixelCount = getHeight();
		double linesPerPixel = ((double) linesCount) / (pixelCount / 2);
		int jump;
		if (linesPerPixel < 1)
			jump = 1;
		else
			jump = (int) linesPerPixel;

		json.startList();
		int cnt = 0;
		for (Entry<String, IntSet> s : this.scrollbarMarks.entrySet()) {
			json.startMap();
			json.addKeyValueQuoted("style", s.getKey());
			json.addKey("ticks");
			json.startList();
			int[] positions = s.getValue().toIntArray();
			if (!AH.isSorted(positions))
				Arrays.sort(positions);
			int top = positions[0];
			int bot = top;
			for (int i = 1; i < positions.length; i++) {
				final int pos = positions[i];
				if (pos > bot + jump) {
					json.addEntry(top);
					json.addEntry(bot - top + 1);
					top = pos;
					cnt++;
				}
				bot = pos;
			}
			cnt++;
			json.addEntry(top);
			json.addEntry(bot - top + 1);
			json.endList();
			json.endMap();
		}
		json.endList();
		json.end();
		func.end();

	}

	@Override
	public void setSize(int width, int height) {
		if (height != this.getHeight())
			scrollMarksChanged = true;
		super.setSize(width, height);
	}

	public void setStyle(String style) {
		if (OH.eq(this.style, style))
			return;
		this.style = style;
		forceRefresh();
		flagPendingAjax();
	}
	public void setLabelStyle(String labelStyle) {
		if (OH.eq(this.labelStyle, labelStyle))
			return;
		this.labelStyle = labelStyle;
		forceRefresh();
		flagPendingAjax();
	}

	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (keyEvent.isJustCtrlKey() && OH.eq("a", keyEvent.getKey())) {
			this.selectLines(0, this.model.getNumberOfLines(this));
			return true;
		} else if (keyEvent.isJustCtrlKey() && OH.eq("c", keyEvent.getKey())) {
			BasicWebMenu r = new BasicWebMenu();
			String text = this.model.toString();
			StringBuilder sink = new StringBuilder();
			int[] t = getSelectedLines();
			if (AH.isEmpty(t)) {
				for (int i = 0, l = this.model.getNumberOfLines(this); i < l; i++) {
					this.model.formatText(this, i, sink);
					sink.append('\n');
					if (sink.length() > 100000) {
						sink.append("<Too Large for Clipboard>");
						break;
					}
				}
			} else {
				for (int i : t) {
					this.model.formatText(this, i, sink);
					sink.append('\n');
					if (sink.length() > 100000) {
						sink.append("<Too Large for Clipboard>");
						break;
					}
				}
			}
			r.addChild(new BasicWebMenuLink("<U>C</U>opy to clipboard", true, "copy").setOnClickJavascript(PortletHelper.createJsCopyToClipboard(sink.toString())));
			getManager().showContextMenu(r, null);
			return true;
		} else {
			if (!this.listeners.isEmpty())
				for (TextPortletListener i : this.listeners)
					if (i.onTextUserKeyEvent(this, keyEvent))
						return true;
		}
		return super.onUserKeyEvent(keyEvent);
	}
}
