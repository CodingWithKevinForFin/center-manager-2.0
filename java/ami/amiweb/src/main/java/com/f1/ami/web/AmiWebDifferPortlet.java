package com.f1.ami.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.suite.web.JsFunction;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.form.Form;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.text.FastTextPortlet;
import com.f1.suite.web.portal.impl.text.TextModel;
import com.f1.suite.web.portal.impl.text.TextPortletListener;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.AH;
import com.f1.utils.IntArrayList;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.diff.SequenceDiffer;
import com.f1.utils.diff.SequenceDiffer.Block;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.structs.Tuple2;

public class AmiWebDifferPortlet extends GridPortlet implements TextModel, TextPortletListener, FormPortletListener, ConfirmDialogListener {

	final private FastTextPortlet leftTextPortlet;
	final private FastTextPortlet rightTextPortlet;
	final private DividerPortlet divPortlet;
	final private HtmlPortlet leftTitlePortlet;
	final private HtmlPortlet rightTitlePortlet;
	final private List<Block<String>> blocks = new ArrayList<SequenceDiffer.Block<String>>();
	final private List<Tuple2<Block<String>, Integer>> blocksAndOffsets = new ArrayList<Tuple2<Block<String>, Integer>>();
	private boolean first;
	private HtmlPortlet titlePortlet;
	private FormPortlet optionsPortlet;
	private FormPortletCheckboxField expandJsonField;
	private String leftText;
	private String rightText;
	private FormPortletTextField searchField;
	private String searchMatcher;

	public AmiWebDifferPortlet(PortletConfig config) {
		super(config);
		this.titlePortlet = addChild(new HtmlPortlet(generateConfig()), 0, 0);
		this.optionsPortlet = addChild(new FormPortlet(generateConfig()), 1, 0);
		divPortlet = (DividerPortlet) addChild(new DividerPortlet(generateConfig(), true), 0, 1, 2, 1).getPortlet();
		titlePortlet.setCssClass("diff_title");
		setRowSize(0, 26);

		leftTextPortlet = new FastTextPortlet(generateConfig(), this);
		GridPortlet leftGrid = new GridPortlet(generateConfig());
		leftGrid.addChild(leftTitlePortlet = new HtmlPortlet(generateConfig(), "", "diff_title"), 0, 0);
		leftGrid.addChild(leftTextPortlet, 0, 1);
		leftGrid.setRowSize(0, 20);

		rightTextPortlet = new FastTextPortlet(generateConfig(), this);
		GridPortlet rightGrid = new GridPortlet(generateConfig());
		rightGrid.addChild(rightTitlePortlet = new HtmlPortlet(generateConfig(), "", "diff_title"), 0, 0);
		rightGrid.setRowSize(0, 20);
		rightGrid.addChild(rightTextPortlet, 0, 1);
		divPortlet.addChild(leftGrid);
		divPortlet.addChild(rightGrid);
		this.leftTextPortlet.addListener(this);
		this.rightTextPortlet.addListener(this);
		this.searchField = this.optionsPortlet.addField(new FormPortletTextField("Search:"));
		this.searchField.setValue("");
		this.searchField.setTopPosPx(5).setRightPosPx(20).setWidthPx(100).setHeightPx(20);
		this.searchField.setLabelWidthPx(150);
		this.expandJsonField = this.optionsPortlet.addField(new FormPortletCheckboxField("Display&nbsp;diffable&nbsp;JSON"));
		this.expandJsonField.setValue(true);
		this.expandJsonField.setTopPosPx(5).setRightPosPx(400).setWidthPx(20).setHeightPx(20);
		this.expandJsonField.setLabelWidthPx(100);
		this.optionsPortlet.addFormPortletListener(this);
	}
	public void setText(String left, String right) {
		this.leftText = left;
		this.rightText = right;
		try {
			ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(this.leftText);
			ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(this.rightText);
			this.optionsPortlet.addFieldNoThrow(this.expandJsonField);
		} catch (Exception e) {
			this.optionsPortlet.removeFieldNoThrow(this.expandJsonField);
		}
		doDiff();
	}

	private int[] leftSearchMatchRows = OH.EMPTY_INT_ARRAY;
	private int[] rightSearchMatchRows = OH.EMPTY_INT_ARRAY;

	private void doDiff() {
		String left, right;
		if (this.expandJsonField.getForm() != null && this.expandJsonField.getBooleanValue()) {
			ObjectToJsonConverter converter = new ObjectToJsonConverter();
			converter.setCompactMode(converter.MODE_SEMI);
			Object t = converter.stringToObject(leftText);
			String left2 = converter.objectToString(t);
			t = converter.stringToObject(rightText);
			String right2 = converter.objectToString(t);
			left = left2;
			right = right2;
		} else {
			left = this.leftText;
			right = this.rightText;
		}
		String[] parts1 = SH.splitLines(left);
		String[] parts2 = SH.splitLines(right);
		int lMax = SH.length(SH.getLongest(parts1));
		int rMax = SH.length(SH.getLongest(parts2));
		SequenceDiffer<String> seq = new SequenceDiffer<String>(parts1, parts2);
		this.blocks.clear();
		this.blocks.addAll(seq.getBlocks());
		this.blocksAndOffsets.clear();
		for (Block<String> block : this.blocks)
			for (int i = 0; i < block.getMaxCount(); i++)
				blocksAndOffsets.add(new Tuple2<SequenceDiffer.Block<String>, Integer>(block, i));
		leftTextPortlet.setMaxCharsPerLine(lMax);
		rightTextPortlet.setMaxCharsPerLine(rMax);
		doScrollbarMarks();
		int diffs = 0;
		for (Block<String> i : seq.getBlocks())
			if (!i.areSame())
				diffs++;
		if (diffs < 1) {
			titlePortlet.setHtml("Contents are Identical");
			titlePortlet.setCssStyle("_fg=#000000");
			leftTextPortlet.moveToLineTop(0);
			rightTextPortlet.moveToLineTop(0);
		} else {
			if (diffs == 1)
				titlePortlet.setHtml("1 Difference");
			else
				titlePortlet.setHtml(diffs + " Differences <span style='color:#666666'>(Spacebar to navigate through differences)</span>");
			titlePortlet.setCssStyle("_fg=#990000");
			int top = Math.max(0, seq.getBlocks().get(0).getLeftCount() - 10);
			leftTextPortlet.moveToLineTop(top);
			rightTextPortlet.moveToLineTop(top);
		}
	}
	private void doScrollbarMarks() {
		int pos = 0;
		IntArrayList leftSearchMatchRows = new IntArrayList();
		IntArrayList rightSearchMatchRows = new IntArrayList();
		rightTextPortlet.clearScrollMarks();
		leftTextPortlet.clearScrollMarks();
		for (Block<String> block : this.blocks) {
			for (int i = 0; i < block.getMaxCount(); i++) {
				if (i < block.getLeftCount()) {
					if (searchMatcher != null && SH.indexOfIgnoreCase(block.get(SequenceDiffer.LEFT, i), searchMatcher, 0) != -1) {
						leftSearchMatchRows.add(pos);
						leftTextPortlet.addScrollbarMark(pos, "#fffa00");
					}
					if (!block.areSame())
						leftTextPortlet.addScrollbarMark(pos, "#00AA00");
				}
				if (i < block.getRightCount()) {
					if (searchMatcher != null && SH.indexOfIgnoreCase(block.get(SequenceDiffer.RIGHT, i), searchMatcher, 0) != -1) {
						rightTextPortlet.addScrollbarMark(pos, "#fffa00");
						rightSearchMatchRows.add(pos);
					}
					if (!block.areSame())
						rightTextPortlet.addScrollbarMark(pos, "#b2AC00");
				}
				pos++;
			}
		}
		leftTextPortlet.forceRefresh();
		rightTextPortlet.forceRefresh();
		this.leftSearchMatchRows = leftSearchMatchRows.toIntArray();
		this.rightSearchMatchRows = rightSearchMatchRows.toIntArray();
	}
	@Override
	public WebMenu createMenu(FastTextPortlet fastTextPortlet) {
		return null;
	}

	@Override
	public int getNumberOfLines(FastTextPortlet portlet) {
		return blocksAndOffsets.size();
	}
	@Override
	public int getLabelWidth(FastTextPortlet portlet) {
		return Math.max(3, MH.getDigitsCount(getNumberOfLines(null), 10)) * 10;
	}

	public void setTitles(String leftHtml, String rightHtml) {
		this.leftTitlePortlet.setHtml(SH.noNull(leftHtml));
		this.rightTitlePortlet.setHtml(SH.noNull(rightHtml));
	}
	@Override
	public void prepareLines(FastTextPortlet portlet, int start, int count) {
	}
	@Override
	public void formatHtml(FastTextPortlet portlet, int lineNumber, StringBuilder sink) {
		Tuple2<Block<String>, Integer> t = blocksAndOffsets.get(lineNumber);
		Block<String> block = t.getA();
		int offset = t.getB();
		boolean side = portlet == this.leftTextPortlet ? SequenceDiffer.LEFT : SequenceDiffer.RIGHT;
		if (offset < block.getCount(side)) {
			String left = block.getLeftOrNull(offset);
			String right = block.getRightOrNull(offset);
			if (OH.eq(left, right)) {
				append(sink, left);
			} else if (SH.isnt(left) || SH.isnt(right)) {
				sink.append("<span style='color:#AA0000;'>");
				String txt = block.getOrNull(side, offset);
				append(sink, txt);
				sink.append("</span>");
			} else {
				SequenceDiffer<String> seq = new SequenceDiffer<String>(toTokens(left), toTokens(right));
				for (Block<String> b : seq.getBlocks()) {
					if (!b.areSame())
						sink.append("<span style='color:#AA0000'>");
					for (int i = 0; i < b.getCount(side); i++) {
						String txt = b.getOrNull(side, i);
						append(sink, txt);
					}
					if (!b.areSame())
						sink.append("</span>");
				}
			}
		}
	}
	@Override
	public void formatText(FastTextPortlet portlet, int lineNumber, StringBuilder sink) {
		Tuple2<Block<String>, Integer> t = blocksAndOffsets.get(lineNumber);
		Block<String> block = t.getA();
		int offset = t.getB();
		boolean side = portlet == this.leftTextPortlet ? SequenceDiffer.LEFT : SequenceDiffer.RIGHT;
		if (offset < block.getCount(side)) {
			String left = block.getLeftOrNull(offset);
			String right = block.getRightOrNull(offset);
			if (OH.eq(left, right)) {
				sink.append(left);
			} else if (SH.isnt(left) || SH.isnt(right)) {
				String txt = block.getOrNull(side, offset);
				sink.append(txt);
			} else {
				SequenceDiffer<String> seq = new SequenceDiffer<String>(toTokens(left), toTokens(right));
				for (Block<String> b : seq.getBlocks()) {
					//					if (!b.areSame())
					//						sink.append("<span style='color:#AA0000'>");
					for (int i = 0; i < b.getCount(side); i++) {
						String txt = b.getOrNull(side, i);
						sink.append(txt);
					}
					//					if (!b.areSame())
					//						sink.append("</span>");
				}
			}
		}
	}
	private void append(StringBuilder sink, String txt) {
		if (searchMatcher == null) {
			WebHelper.escapeHtml(txt, sink);
			return;
		}
		int start = 0;
		for (;;) {
			int end = SH.indexOfIgnoreCase(txt, searchMatcher, start);
			if (end == -1) {
				WebHelper.escapeHtml(txt.substring(start), sink);
				break;
			}
			WebHelper.escapeHtml(txt.substring(start, end), sink);
			sink.append("<span style='background-color:yellow'>");
			start = end + searchMatcher.length();
			WebHelper.escapeHtml(txt.substring(end, start), sink);
			sink.append("</span>");
		}
	}
	private String[] toTokens(String s) {
		ArrayList<String> r = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		StringCharReader sr = new StringCharReader(s);
		for (;;) {
			sr.readWhileAny(StringCharReader.ALPHA_NUM_UNDERBAR, sb);
			if (sb.length() != 0)
				r.add(SH.toStringAndClear(sb));
			if (sr.isEof())
				break;
			r.add(SH.toString(sr.readChar()));
		}
		return AH.toArray(r, String.class);
	}
	@Override
	protected void initJs() {
		super.initJs();
		this.first = true;
		flagPendingAjax();

	}

	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (this.leftTextPortlet.getVisible() && this.rightTextPortlet.getVisible() && first) {
			first = false;
			StringBuilder pendingJs = getManager().getPendingJs();
			pendingJs.append("{\n");
			pendingJs.append("  var t=");
			new JsFunction(pendingJs, this.leftTextPortlet.getJsObjectName(), "getText").close().call("getScrollPane").end();
			new JsFunction(pendingJs, this.rightTextPortlet.getJsObjectName(), "getText").close().call("getScrollPane").close().call("linkToScrollBar").addParam("t").end();
			pendingJs.append("}\n");
		} else
			flagPendingAjax();
	}

	@Override
	public void formatLabel(FastTextPortlet portlet, int lineNumber, StringBuilder sink) {
		Tuple2<Block<String>, Integer> t = blocksAndOffsets.get(lineNumber);
		Block<String> block = t.getA();
		int offset = t.getB();
		if (portlet == this.leftTextPortlet) {
			if (offset < block.getLeftCount()) {
				sink.append(block.getLeftLineNumber(offset));
			} else {
				sink.append("<span style=\\'color:#00AAAA\\'>");
				WebHelper.escapeHtml(">>>", sink);
				sink.append("</span>");
			}
		} else {
			if (offset < block.getRightCount()) {
				sink.append(block.getRightLineNumber(offset));
			} else {
				sink.append("<span style=\\'color:#b2AC00\\'>");
				WebHelper.escapeHtml("<<<", sink);
				sink.append("</span>");
			}
		}

	}
	@Override
	public void setColumnsVisible(int columns) {
	}
	@Override
	public void formatStyle(FastTextPortlet portlet, int lineNumber, StringBuilder sink) {
		Tuple2<Block<String>, Integer> t = blocksAndOffsets.get(lineNumber);
		Block<String> block = t.getA();
		int offset = t.getB();
		if (portlet == this.leftTextPortlet) {
			if (!block.areSame())
				sink.append("style.backgroundColor=#dbffdb");
		} else {
			if (!block.areSame())
				sink.append("style.backgroundColor=#f4f2c6");
		}

	}
	@Override
	public void onTextContextMenu(FastTextPortlet portlet, String id) {
	}
	@Override
	public boolean onTextUserKeyEvent(FastTextPortlet portlet, KeyEvent keyEvent) {
		if (" ".equals(keyEvent.getKey())) {
			int row = AH.last(portlet.getSelectedLines(), -1);
			if (row == -1)
				row = portlet.getTopLineVisible();
			int start = row;
			Block<String> startBlock = this.blocksAndOffsets.get(row).getA();
			int direction = keyEvent.isShiftKey() ? -1 : 1;
			for (;;) {
				row += direction;
				if (row == -1)
					row = this.blocksAndOffsets.size() - 1;
				else if (row == this.blocksAndOffsets.size())
					row = 0;
				if (row == start)
					break;

				Block<String> currentBlock = this.blocksAndOffsets.get(row).getA();
				if (startBlock != currentBlock && !currentBlock.areSame()) {
					leftTextPortlet.selectLines(row, row);
					leftTextPortlet.ensureLineVisible(row);
					rightTextPortlet.selectLines(row, row);
					rightTextPortlet.ensureLineVisible(row);
					break;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.expandJsonField)
			doDiff();
		if (field == this.searchField) {
			String value = this.searchField.getValue();
			if (SH.isnt(value))
				value = null;
			if (OH.ne(this.searchMatcher, value)) {
				this.searchMatcher = value;
				doScrollbarMarks();
			}
		}
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (keycode == Form.KEYCODE_ENTER) {
			if (this.leftSearchMatchRows.length > 0 || this.rightSearchMatchRows.length > 0) {
				int l = this.leftTextPortlet.getBottomLineSelected();
				int r = this.rightTextPortlet.getBottomLineSelected();
				if (MH.anyBits(mask, Form.KEY_SHIFT)) {
					int min = l == -1 ? r : (r == -1 ? l : Math.min(l, r));
					int leftNext = getPrevPosition(leftSearchMatchRows, min);
					int rightNext = getPrevPosition(rightSearchMatchRows, min);
					if (leftNext == -1 && rightNext == -1) {
						leftNext = getPrevPosition(leftSearchMatchRows, Integer.MAX_VALUE);
						rightNext = getPrevPosition(rightSearchMatchRows, Integer.MAX_VALUE);
					}
					if (leftNext == rightNext) {
						this.leftTextPortlet.selectLineEnsureVisible(leftNext);
						this.rightTextPortlet.selectLineEnsureVisible(rightNext);
					} else if (leftNext != -1 && leftNext > rightNext) {
						this.leftTextPortlet.selectLineEnsureVisible(leftNext);
						this.rightTextPortlet.selectLine(-1);
					} else if (rightNext != -1 && rightNext > leftNext) {
						this.rightTextPortlet.selectLineEnsureVisible(rightNext);
						this.leftTextPortlet.selectLine(-1);
					}
				} else {
					int max = l == -1 ? r : (r == -1 ? l : Math.max(l, r));
					int leftNext = getNextPosition(leftSearchMatchRows, max);
					int rightNext = getNextPosition(rightSearchMatchRows, max);
					if (leftNext == -1 && rightNext == -1) {
						leftNext = getNextPosition(leftSearchMatchRows, -1);
						rightNext = getNextPosition(rightSearchMatchRows, -1);
					}
					if (leftNext == rightNext) {
						this.leftTextPortlet.selectLineEnsureVisible(leftNext);
						this.rightTextPortlet.selectLineEnsureVisible(rightNext);
					} else if (leftNext != -1 && leftNext < rightNext) {
						this.leftTextPortlet.selectLineEnsureVisible(leftNext);
						this.rightTextPortlet.selectLine(-1);
					} else if (rightNext != -1 && rightNext < leftNext) {
						this.rightTextPortlet.selectLineEnsureVisible(rightNext);
						this.leftTextPortlet.selectLine(-1);
					}
				}
			}

		}
	}
	private int getNextPosition(int[] vals, int n) {
		final int idx = AH.indexOfSortedGreaterThanEqualTo(n + 1, vals);
		return idx == -1 ? -1 : vals[idx];
	}
	private int getPrevPosition(int[] vals, int n) {
		final int idx = AH.indexOfSortedLessThanEqualTo(n - 1, vals);
		return idx == -1 ? -1 : vals[idx];
	}
	public void showDialog(String string) {
		if (OH.eq(leftText, rightText))
			getManager().showDialog(string, new ConfirmDialogPortlet(generateConfig(), "Contents are Identical, Show anyways?", ConfirmDialogPortlet.TYPE_YES_NO, this)
					.setCallback("SHOW").setCorrelationData(string));
		else
			getManager().showDialog(string, this);
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("SHOW".equals(source.getCallback()) && ConfirmDialogPortlet.ID_YES.equals(id))
			getManager().showDialog((String) source.getCorrelationData(), this);
		return true;
	}
	public void disableSearchField() {
		this.searchField.setDisabled(true);
	}
	public void setSearch(String searchText) {
		this.searchMatcher = searchText;
		this.searchField.setValue(searchText);
		doScrollbarMarks();
	}
	public void showOnlyLeft() {
		this.divPortlet.setOffset(1);
		this.divPortlet.setThickness(0);
		this.titlePortlet.setHtml("");
	}
	public void showOnlyRight() {
		this.divPortlet.setOffset(0.0);
		this.divPortlet.setThickness(0);
		this.titlePortlet.setHtml("");
	}
}
