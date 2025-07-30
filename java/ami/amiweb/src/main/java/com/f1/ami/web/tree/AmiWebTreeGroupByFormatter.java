package com.f1.ami.web.tree;

import java.util.Set;

import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulas;
import com.f1.ami.web.AmiWebFormulasImpl;
import com.f1.ami.web.AmiWebFormulasListener;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.tree.WebTreeAggregateNodeFormatter;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebTreeGroupByFormatter implements WebTreeAggregateNodeFormatter, AmiWebFormulasListener {
	private WebCellFormatter inner;
	private AmiWebTreePortlet tree;
	private int columnId;
	private DerivedCellCalculator descriptionCalc;
	private DerivedCellCalculator styleCalc;
	private DerivedCellCalculator colorCalc;
	private DerivedCellCalculator bgCalc;
	private DerivedCellCalculator orderByCalc;
	private AmiWebFormula descriptionFormula;
	private AmiWebFormula styleFormula;
	private AmiWebFormula colorFormula;
	private AmiWebFormula bgColorFormula;
	private AmiWebFormula orderByFormula;
	private AmiWebFormulasImpl formulas;

	/* Think of this as AmiWebTreeColumnFormatter, and AmiWebTreeGroupBy as the formatter for the Grouping's which is a type of ColumnFormatter
	 * 
	 * If we were to rename it:
	 * AmiWebTreeGroupBy -> AmiWebTreeGroupingFormatter (contains AmiWebTreeColumnFormatter)
	 * AmiWebTreeGroupByFormatter -> AmiWebTreeColumnFormatter
	 * */
	public AmiWebTreeGroupByFormatter(AmiWebTreePortlet tree, WebCellFormatter inner, AmiWebFormulasImpl formulas) {
		this.formulas = formulas;
		this.descriptionFormula = this.formulas.addFormulaAgg("display", Object.class);
		this.styleFormula = this.formulas.addFormulaAgg("style", String.class);
		this.colorFormula = this.formulas.addFormulaAgg("color", String.class);
		this.bgColorFormula = this.formulas.addFormulaAgg("backgroundColor", String.class);
		this.orderByFormula = this.formulas.addFormulaAgg("orderBy", Comparable.class);
		this.formulas.addFormulasListener(this);
		this.tree = tree;
		this.inner = inner;
	}
	public int getColumnId() {
		return this.columnId;
	}
	public void setColumnId(int columnId) {
		this.columnId = columnId;
	}
	public void setInnerFormatter(WebCellFormatter inner) {
		this.inner = inner;
	}

	public DerivedCellCalculator getCalcNoThrow(String description) {
		try {
			return this.descriptionFormula.toCalc(description, null);
		} catch (Exception e) {
			return null;
		}
	}
	public void setFormula(String description, String orderBy, String style, String color, String bgColor) {
		this.descriptionFormula.setFormula(description, false);
		this.orderByFormula.setFormula(orderBy, false);
		this.styleFormula.setFormula(style, false);
		this.colorFormula.setFormula(color, false);
		this.bgColorFormula.setFormula(bgColor, false);
	}
	public String getDescription(boolean override) {
		return this.descriptionFormula.getFormula(override);
	}

	public void formatToText(AggregateFactory af, StringBuilder descriptionSink, CalcFrameStack rsf) {
		if (descriptionCalc != null) {
			Object val = descriptionCalc.get(rsf);
			inner.formatCellToText(val, descriptionSink);
		}
	}
	public void formatToHtml(AggregateFactory af, StringBuilder descriptionSink, StringBuilder styleSink, CalcFrameStack sf) {
		if (descriptionCalc != null) {
			Object val = descriptionCalc.get(sf);
			inner.formatCellToHtml(val, descriptionSink, styleSink);
		}
		if (styleSink != null) {
			if (styleCalc != null) {
				styleSink.append("_fm=");
				SH.s(styleCalc.get(sf), styleSink, "");
			}
			if (colorCalc != null) {
				styleSink.append("|_fg=");
				SH.s(colorCalc.get(sf), styleSink, "");
			}
			if (bgCalc != null) {
				styleSink.append("|_bg=");
				SH.s(bgCalc.get(sf), styleSink, "");
			}
		}
	}
	public String getStyle(boolean override) {
		return styleFormula.getFormula(override);
	}

	public String getColor(boolean override) {
		return colorFormula.getFormula(override);
	}

	public String getBackgroundColor(boolean override) {
		return bgColorFormula.getFormula(override);
	}

	public boolean hasDescription(boolean override) {
		return this.descriptionFormula.getFormula(override) != null;
	}
	public String getOrderBy(boolean override) {
		return this.orderByFormula.getFormula(override);
	}

	@Override
	public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
		this.tree.getTreePortletFormatter().formatColumnToHtml(this, node, sink, style);
	}
	@Override
	public void formatToText(WebTreeNode node, StringBuilder sink) {
		this.tree.getTreePortletFormatter().formatColumnToText(this, node, sink);
	}
	@Override
	public int compare(WebTreeNode o1, WebTreeNode o2) {
		return this.tree.getTreePortletFormatter().compareColumn(this, o1, o2);
	}
	public Comparable<?> getOrdinalValue(AggregateFactory aggregateFactory, WebTreeNode node, CalcFrameStack rsf) {
		final Object r;
		if (orderByCalc != null)
			r = orderByCalc.get(rsf);
		else if (descriptionCalc != null)
			r = descriptionCalc.get(rsf);
		else
			r = node.getKeyOrNull();
		return (r instanceof Comparable) ? (Comparable) r : null;
	}
	public Object getDisplayValue(AggregateFactory aggregateFactory, WebTreeNode node, CalcFrameStack rsf) {
		if (descriptionCalc != null) {
			node.setShouldShowInQuickFilter(true);
			return descriptionCalc.get(rsf);
		} else {//if leaf formatting is not configured
			node.setShouldShowInQuickFilter(false);
			return node.getKeyOrNull();
		}
	}
	public void getDependencies(Set<Object> r) {
		DerivedHelper.getDependencyIds(this.bgCalc, r);
		DerivedHelper.getDependencyIds(this.descriptionCalc, r);
		DerivedHelper.getDependencyIds(this.styleCalc, r);
		DerivedHelper.getDependencyIds(this.colorCalc, r);
		DerivedHelper.getDependencyIds(this.bgCalc, r);
		DerivedHelper.getDependencyIds(this.orderByCalc, r);
	}
	@Override
	public Object getValue(WebTreeNode node) {
		return this.tree.getTreePortletFormatter().getValue(this, node);
	}
	@Override
	public Object getValueDisplay(WebTreeNode node) {
		return this.tree.getTreePortletFormatter().getValueDisplay(this, node);
	}
	@Override
	public String formatToText(Object data) {
		return OH.toString(data);
	}
	@Override
	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		if (formula == descriptionFormula)
			descriptionCalc = nuw;
		else if (formula == styleFormula)
			styleCalc = nuw;
		else if (formula == colorFormula)
			colorCalc = nuw;
		else if (formula == bgColorFormula)
			bgCalc = nuw;
		else if (formula == orderByFormula)
			orderByCalc = nuw;
		else
			return;
		//add,test
		this.onRebuildCalcs();
	}

	public void onRebuildCalcs() {
		this.tree.getTree().onColumnChanged(this.tree.getTree().getColumn(this.columnId));
	}
	public AmiWebFormulas getFormulas() {
		return this.formulas;
	}

}
