package com.vortex.web.portlet.grids;

import java.util.ArrayList;
import java.util.List;

import com.f1.suite.web.JsFunction;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.text.FastTextPortlet;
import com.f1.suite.web.portal.impl.text.TextModel;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.utils.diff.SequenceDiffer;
import com.f1.utils.diff.SequenceDiffer.Block;
import com.f1.utils.structs.Tuple2;

public class VortexFileComparePortlet extends GridPortlet implements TextModel {

	final private FastTextPortlet leftTextPortlet;
	final private FastTextPortlet rightTextPortlet;
	final private DividerPortlet divPortlet;
	final private HtmlPortlet leftTitlePortlet;
	final private HtmlPortlet rightTitlePortlet;
	final private List<Block<String>> blocks = new ArrayList<SequenceDiffer.Block<String>>();
	final private List<Tuple2<Block<String>, Integer>> blocksAndOffsets = new ArrayList<Tuple2<Block<String>, Integer>>();
	private boolean first;

	public VortexFileComparePortlet(PortletConfig config) {
		super(config);
		divPortlet = addChild(new DividerPortlet(generateConfig(), true), 0, 0);

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
	}
	public void setText(String left, String right) {
		String[] parts1 = SH.splitLines(left);
		String[] parts2 = SH.splitLines(right);
		SequenceDiffer<String> seq = new SequenceDiffer<String>(parts1, parts2);
		this.blocks.clear();
		this.blocks.addAll(seq.getBlocks());
		this.blocksAndOffsets.clear();
		int pos = 0;
		rightTextPortlet.clearScrollMarks();
		for (Block<String> block : this.blocks) {
			for (int i = 0; i < block.getMaxCount(); i++) {
				if (!block.areSame()) {
					if (i >= block.getLeftCount()) {
						rightTextPortlet.addScrollbarMark(pos, "#00AAAA");
					} else if (i >= block.getRightCount()) {
						rightTextPortlet.addScrollbarMark(pos, "#b2AC00");
					} else {
						rightTextPortlet.addScrollbarMark(pos, "#00AAAA");
						rightTextPortlet.addScrollbarMark(pos, "#b2AC00");
					}
				}
				blocksAndOffsets.add(new Tuple2<SequenceDiffer.Block<String>, Integer>(block, i));
				pos++;
			}
		}
		leftTextPortlet.forceRefresh();
		rightTextPortlet.forceRefresh();
		leftTextPortlet.moveToLineTop(0);
		rightTextPortlet.moveToLineTop(0);
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
	public void formatText(FastTextPortlet portlet, int lineNumber, StringBuilder sink) {
		Tuple2<Block<String>, Integer> t = blocksAndOffsets.get(lineNumber);
		Block<String> block = t.getA();
		int offset = t.getB();
		if (portlet == this.leftTextPortlet) {
			if (offset < block.getLeftCount()) {
				String left = block.getLeft(offset);
				WebHelper.escapeHtml(left, sink);
			}
		} else {
			if (offset < block.getRightCount()) {
				String left = block.getRight(offset);
				WebHelper.escapeHtml(left, sink);
			}
		}

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
		if (this.leftTextPortlet.getVisible() && first) {
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
				sink.append("style.background=#dbffff");
		} else {
			if (!block.areSame())
				sink.append("style.background=#f4f2c6");
		}

	}
}
