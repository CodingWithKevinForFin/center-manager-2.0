package com.f1.ami.web.tree;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.AmiWebScriptManagerForLayout;
import com.f1.ami.web.AmiWebUtils;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.suite.web.tree.WebTreeGroupingNodeFormatter;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.WebTreeRowFormatter;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.sql.aggs.AggCalculator;
import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesTuple3;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiWebTreePortletFormatter implements WebTreeGroupingNodeFormatter, WebTreeRowFormatter {

	final private AmiWebTreePortlet treePortlet;
	final private FastWebTree tree;
	private String rowBackgroundColor;
	private String rowTextColor;
	protected DerivedCellCalculator rowBgFormulaCalc;
	protected DerivedCellCalculator rowFgFormulaCalc;
	final private Set<String> usedConstVars = new HashSet<String>();

	public AmiWebTreePortletFormatter(AmiWebTreePortlet treePortlet) {
		this.treePortlet = treePortlet;
		this.tree = this.treePortlet.getTree();
		//		this.parser = this.treePortlet.getService().getScriptManager().getParser(this.treePortlet.getAmiLayoutFullAlias());
	}

	public Map<String, Object> getConfiguration(Map<String, Object> sink) {
		AmiWebUtils.putSkipEmpty(sink, "rtxc", this.rowTextColor);
		AmiWebUtils.putSkipEmpty(sink, "rbgc", this.rowBackgroundColor);
		return sink;
	}

	public void init(Map<String, Object> configuration, StringBuilder errorSink) {
		this.rowTextColor = CH.getOr(Caster_String.INSTANCE, configuration, "rtxc", this.rowTextColor);
		this.rowBackgroundColor = CH.getOr(Caster_String.INSTANCE, configuration, "rbgc", this.rowBackgroundColor);
		// The below gets called in portlet's rebuildCalcs
		this.rebuildCalcs(errorSink);
	}

	public void getDependencies(Set<Object> r) {
		DerivedHelper.getDependencyIds(this.rowBgFormulaCalc, r);
		DerivedHelper.getDependencyIds(this.rowFgFormulaCalc, r);
	}

	public boolean rebuildCalcs(StringBuilder errorSink) {
		return this.setFormula(this.rowTextColor, this.rowBackgroundColor, errorSink);
	}
	public boolean setFormula(String rowColor, String rowBgColor, StringBuilder errorSink) {
		AggregateFactory af = treePortlet.aggregateFactory;
		CalcTypes types = new CalcTypesTuple3(treePortlet.getClassTypes(), treePortlet.getUserDefinedVariables(), AmiWebTreePortlet.VAR_TYPES);
		AmiWebScriptManagerForLayout parser = treePortlet.getService().getScriptManager(this.treePortlet.getAmiLayoutFullAlias());
		DerivedCellCalculator rowBgCalc = null;
		DerivedCellCalculator rowTxCalc = null;
		Set<String> usedConstVars = new HashSet<String>();
		try {
			// If the formula is string create calc, otherwise formula null
			// Row Background Color
			if (SH.is(rowBgColor)) {
				rowBgCalc = parser.toAggCalc(rowBgColor, types, af, treePortlet, usedConstVars);
			} else
				rowBgColor = null;
			// Row Text Color
			if (SH.is(rowColor)) {
				rowTxCalc = parser.toAggCalc(rowColor, types, af, treePortlet, usedConstVars);
			} else
				rowColor = null;
		} catch (Exception e) {
			errorSink.append(e.getMessage());
			return false;
		}

		//Aggregate factory get dependencies and check them- set of all dependencies
		Set<Object> sink = new HashSet<Object>();
		for (AggCalculator i : af.getAggregates())
			DerivedHelper.getDependencyIds(i, sink);
		for (Object i : sink) {
			if (AmiWebTreePortlet.VAR_TYPES.getType((String) i) != null) {
				af.clearAggregates();
				errorSink.append("This variable can not by used inside an aggregate method: " + i);
				return false;
			}
		}

		// Finally set the calc and formula
		this.setRowBackgroundColor(rowBgColor, rowBgCalc);
		this.setRowTextColor(rowColor, rowTxCalc);
		this.usedConstVars.clear();
		this.usedConstVars.addAll(usedConstVars);
		return true;
	}

	public boolean validateFormula(String formula, StringBuilder errorSink) {
		AggregateFactory af = treePortlet.getScriptManager().createAggregateFactory();
		com.f1.base.CalcTypes types = new CalcTypesTuple3(treePortlet.getClassTypes(), treePortlet.getUserDefinedVariables(), AmiWebTreePortlet.VAR_TYPES);
		AmiWebScriptManagerForLayout sm = treePortlet.getScriptManager();
		try {
			sm.toAggCalc(formula, types, af, treePortlet, null);
		} catch (Exception e) {
			errorSink.append(e.getMessage());
			return false;
		}

		//Aggregate factory get dependencies and check them- set of all dependencies
		Set<Object> sink = new HashSet<Object>();
		for (AggCalculator i : af.getAggregates())
			DerivedHelper.getDependencyIds(i, sink);
		for (Object i : sink) {
			if (AmiWebTreePortlet.VAR_TYPES.getType((String) i) != null) {
				errorSink.append("This variable can not by used inside an aggregate method: " + i);
				return false;
			}
		}
		return true;

	}

	private void setRowBackgroundColor(String formula, DerivedCellCalculator calc) {
		this.rowBgFormulaCalc = calc;
		this.rowBackgroundColor = formula;
	}

	private void setRowTextColor(String formula, DerivedCellCalculator calc) {
		this.rowFgFormulaCalc = calc;
		this.rowTextColor = formula;
	}

	public String getRowTextColor() {
		return this.rowTextColor;
	}
	public String getRowBackgroundColor() {
		return this.rowBackgroundColor;
	}

	@Override
	final public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
		int groupIndex = node.getGroupIndex();
		if (groupIndex == -1) {
			int depth = node.getDepth();
			if (depth == 0) {
				sink.append(this.treePortlet.rootName);
				return;
			}
			groupIndex = depth - 1;
		}
		CalcFrameStack sf = treePortlet.getTmpValuesStackFrame();
		AmiWebTreeGroupBy gb = this.treePortlet.groupbyFormulas.getAt(groupIndex);
		this.treePortlet.setCurrentNode(node);
		gb.formatTreeCellToHtml(this.treePortlet.aggregateFactory, node, sink, style, sf);
	}

	@Override
	final public void formatToText(WebTreeNode node, StringBuilder sink) {
		int groupIndex = node.getGroupIndex();
		if (groupIndex == -1) {
			int depth = node.getDepth();
			if (depth == 0) {
				sink.append(this.treePortlet.rootName);
				return;
			}
			groupIndex = depth - 1;
		}
		CalcFrameStack sf = treePortlet.getTmpValuesStackFrame();
		AmiWebTreeGroupBy gb = this.treePortlet.groupbyFormulas.getAt(groupIndex);
		this.treePortlet.setCurrentNode(node);
		gb.formatTreeCellToText(this.treePortlet.aggregateFactory, node, sink, sf);

	}

	@Override
	public int compare(WebTreeNode node, WebTreeNode node2) {
		int groupIndex = node.getGroupIndex();
		if (groupIndex == -1) {
			int depth = node.getDepth();
			groupIndex = depth - 1;
		}
		CalcFrameStack sf = treePortlet.getTmpValuesStackFrame();
		AmiWebTreeGroupBy gb = this.treePortlet.groupbyFormulas.getAt(groupIndex);
		this.treePortlet.setCurrentNode(node);
		Comparable<?> o1 = gb.getFormatter().getOrdinalValue(this.treePortlet.aggregateFactory, node, sf);
		this.treePortlet.setCurrentNode(node2);
		sf = treePortlet.getTmpValuesStackFrame();
		Comparable<?> o2 = gb.getFormatter().getOrdinalValue(this.treePortlet.aggregateFactory, node2, sf);
		if (node.getGroupIndex() != node2.getGroupIndex())
			return OH.compare(node.getGroupIndex(), node2.getGroupIndex());
		return OH.compare(o1, o2);
	}

	@Override
	final public void format(WebTreeNode node, StringBuilder style) {
		ReusableCalcFrameStack rsf = treePortlet.getStackFrame();
		int groupIndex = node.getGroupIndex();
		if (groupIndex == -1) {
			int depth = node.getDepth();
			if (depth == 0)
				return;
			groupIndex = depth - 1;
		}
		AmiWebTreeGroupBy gb = this.treePortlet.groupbyFormulas.getAt(groupIndex);
		this.treePortlet.setCurrentNode(node);

		CalcFrameStack sf = treePortlet.getTmpValuesStackFrame();
		gb.formatRowStyle(this.treePortlet.aggregateFactory, node, style, sf);

		if (style != null) {
			// If the groupbyformatter doesn't have a row color
			String rwfg = gb.getRowColor(true);
			if (SH.isnt(rwfg) && this.rowFgFormulaCalc != null) {
				style.append("|_fg=");
				SH.s(this.rowFgFormulaCalc.get(rsf), style, "");

			}
			// If the groupbyformatter doesn't have a row background color
			String rwbg = gb.getRowBackgroundColor(true);
			if (SH.isnt(rwbg) && this.rowBgFormulaCalc != null) {
				style.append("|_bg=");
				SH.s(this.rowBgFormulaCalc.get(rsf), style, "");
			}
		}
	}

	public void formatColumnToHtml(AmiWebTreeGroupByFormatter formatter, WebTreeNode node, StringBuilder sink, StringBuilder style) {
		CalcFrameStack sf = this.treePortlet.getTmpValuesStackFrame();
		this.treePortlet.setCurrentNode(node);
		formatter.formatToHtml(this.treePortlet.aggregateFactory, sink, style, sf);
	}

	public void formatColumnToText(AmiWebTreeGroupByFormatter formatter, WebTreeNode node, StringBuilder sink) {
		CalcFrameStack sf = treePortlet.getTmpValuesStackFrame();
		this.treePortlet.setCurrentNode(node);
		formatter.formatToText(this.treePortlet.aggregateFactory, sink, sf);
	}
	public int compareColumn(AmiWebTreeGroupByFormatter formatter, WebTreeNode node, WebTreeNode node2) {
		this.treePortlet.setCurrentNode(node);
		CalcFrameStack sf = treePortlet.getTmpValuesStackFrame();
		Comparable<?> o1 = formatter.getOrdinalValue(this.treePortlet.aggregateFactory, node, sf);
		this.treePortlet.setCurrentNode(node2);
		sf = treePortlet.getTmpValuesStackFrame();
		Comparable<?> o2 = formatter.getOrdinalValue(this.treePortlet.aggregateFactory, node2, sf);
		return OH.compare(o1, o2);
	}
	public Object getValue(AmiWebTreeGroupByFormatter formatter, WebTreeNode node) {
		this.treePortlet.setCurrentNode(node);
		CalcFrameStack sf = treePortlet.getTmpValuesStackFrame();
		return formatter.getOrdinalValue(this.treePortlet.aggregateFactory, node, sf);
	}
	public Object getValueDisplay(AmiWebTreeGroupByFormatter formatter, WebTreeNode node) {
		this.treePortlet.setCurrentNode(node);
		CalcFrameStack sf = treePortlet.getTmpValuesStackFrame();
		return formatter.getDisplayValue(this.treePortlet.aggregateFactory, node, sf);
	}

	private FastWebTree getTree() {
		return this.tree;
	}
	@Override
	public Object getValue(WebTreeNode node) {
		int groupIndex = node.getGroupIndex();
		if (groupIndex == -1) {
			int depth = node.getDepth();
			groupIndex = depth;// - 1;
		}
		AmiWebTreeGroupBy gb = this.treePortlet.groupbyFormulas.getAt(groupIndex);
		this.treePortlet.setCurrentNode(node);
		CalcFrameStack sf = treePortlet.getTmpValuesStackFrame();
		return gb.getFormatter().getOrdinalValue(this.treePortlet.aggregateFactory, node, sf);
	}
	@Override
	public Object getValueDisplay(WebTreeNode node) {
		int groupIndex = node.getGroupIndex();
		if (groupIndex == -1) {
			int depth = node.getDepth();
			groupIndex = depth;// - 1;
		}
		CalcFrame tmpValuesMap = this.treePortlet.getTmpValuesMap();
		AmiWebTreeGroupBy gb = this.treePortlet.groupbyFormulas.getAt(groupIndex);
		this.treePortlet.setCurrentNode(node);
		CalcFrameStack sf = treePortlet.getTmpValuesStackFrame();
		return gb.getFormatter().getDisplayValue(this.treePortlet.aggregateFactory, node, sf);
	}
	@Override
	public String formatToText(Object data) {
		return OH.toString(data);
	}

	public Set<String> getUsedConstVars() {
		return usedConstVars;
	}

}
