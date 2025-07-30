package com.f1.ami.web.tree;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebDomObjectsManager;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulas;
import com.f1.ami.web.AmiWebFormulasImpl;
import com.f1.ami.web.AmiWebFormulasListener;
import com.f1.ami.web.AmiWebScriptManagerForLayout;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.base.CalcTypes;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesTuple3;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiWebTreeGroupBy implements AmiWebDomObject, AmiWebFormulasListener {

	private DerivedCellCalculator groupbyCalc;
	private DerivedCellCalculator parentGroupCalc;
	private AmiWebTreePortlet tree;
	private DerivedCellCalculator rowStyleCalc;
	private DerivedCellCalculator rowColorCalc;
	private DerivedCellCalculator rowBgCalc;
	private boolean isRecursive;

	private AmiWebFormula groupbyFormula;
	private AmiWebFormula parentGroupFormula;
	private AmiWebFormula rowStyleFormula;
	private AmiWebFormula rowColorFormula;
	private AmiWebFormula rowBgFormula;

	final private AmiWebTreeGroupByFormatter formatter;
	private boolean isLeaf;
	private boolean isTransient;
	private String ari;
	private String amiId;

	public AmiWebTreeGroupBy(String amiId, AmiWebTreePortlet tree) {
		this.formulas = new AmiWebFormulasImpl(this);
		this.formulas.setAggregateFactory(tree.aggregateFactory);
		this.groupbyFormula = this.formulas.addFormulaAgg("groupby", Object.class);
		this.parentGroupFormula = this.formulas.addFormulaAgg("parent", Object.class);
		this.rowStyleFormula = this.formulas.addFormula("rowStyle", Object.class);
		this.rowColorFormula = this.formulas.addFormula("rowColor", Object.class);
		this.rowBgFormula = this.formulas.addFormula("rowBackgroundColor", Object.class);
		this.formulas.addFormulasListener(this);
		this.tree = tree;
		this.amiId = amiId;
		this.formatter = new AmiWebTreeGroupByFormatter(tree, tree.getService().getFormatterManager().getBasicFormatter(), this.formulas);
		updateAri();
	}

	public boolean setFormula(boolean isLeaf, String formula, String parentGroup, String description, String orderBy, String style, String color, String bgColor, String rowStyle,
			String rowColor, String rowBgColor) {
		DerivedCellCalculator gbCalc;
		CalcTypes types = new CalcTypesTuple3(tree.getClassTypes(), tree.getUserDefinedVariables(), AmiWebTreePortlet.VAR_TYPES);
		AmiWebScriptManagerForLayout scriptManager = tree.getScriptManager();
		this.isRecursive = false;
		DerivedCellCalculator pgCalc;
		if (!isLeaf && SH.is(parentGroup)) {
			pgCalc = scriptManager.toCalc(parentGroup, types, this, null);

			this.isRecursive = true;
			if (pgCalc == null)
				return false;
		}

		AggregateFactory af;

		this.formatter.setFormula(description, orderBy, style, color, bgColor);

		this.isLeaf = isLeaf;
		this.groupbyFormula.setFormula(formula, false);
		this.parentGroupFormula.setFormula(parentGroup, false);
		this.rowStyleFormula.setFormula(rowStyle, false);
		this.rowStyleFormula.setFormula(rowStyle, false);
		this.rowColorFormula.setFormula(rowColor, false);
		this.rowBgFormula.setFormula(rowBgColor, false);
		return true;
	}
	public String getGroupby(boolean override) {
		return this.groupbyFormula.getFormula(override);
	}
	public AmiWebTreeGroupByFormatter getFormatter() {
		return this.formatter;
	}

	public String getParentGroup(boolean override) {
		return this.parentGroupFormula.getFormula(override);
	}

	public DerivedCellCalculator getParentGroupCalc() {
		return this.parentGroupCalc;
	}

	public String getValue(ReusableCalcFrameStack rsf) {
		if (groupbyCalc == null)
			return null;
		return AmiUtils.s(groupbyCalc.get(rsf));
	}

	public void formatTreeCellToHtml(AggregateFactory af, WebTreeNode node, StringBuilder descriptionSink, StringBuilder styleSink, CalcFrameStack sf) {
		if (!this.formatter.hasDescription(true) && !isLeaf)
			SH.s(node.getKeyOrNull(), descriptionSink, "");
		this.formatter.formatToHtml(af, descriptionSink, styleSink, sf);
	}
	public void formatColumnCellToHtml(AggregateFactory af, WebTreeNode node, StringBuilder descriptionSink, StringBuilder styleSink, AmiWebTreeGroupByFormatter formatter,
			CalcFrameStack sf) {
		formatter.formatToHtml(af, descriptionSink, styleSink, sf);
	}
	public void formatTreeCellToText(AggregateFactory af, WebTreeNode node, StringBuilder descriptionSink, CalcFrameStack sf) {
		if (!this.formatter.hasDescription(true) && !isLeaf)
			SH.s(node.getKeyOrNull(), descriptionSink, "");
		this.formatter.formatToText(af, descriptionSink, sf);
	}
	public void formatColumnCellToText(AggregateFactory af, WebTreeNode node, StringBuilder descriptionSink, AmiWebTreeGroupByFormatter formatter, CalcFrameStack sf) {
		formatter.formatToText(af, descriptionSink, sf);
	}

	public String getRowStyle(boolean override) {
		return this.rowStyleFormula.getFormula(override);
	}
	public String getRowColor(boolean override) {
		return this.rowColorFormula.getFormula(override);
	}
	public String getRowBackgroundColor(boolean override) {
		return this.rowBgFormula.getFormula(override);
	}

	public void formatRowStyle(AggregateFactory aggregateFactory, WebTreeNode node, StringBuilder styleSink, CalcFrameStack rsf) {
		if (rowStyleCalc != null) {
			styleSink.append("_fm=");
			SH.s(rowStyleCalc.get(rsf), styleSink, "");
		}
		if (rowColorCalc != null) {
			styleSink.append("|_fg=");
			SH.s(rowColorCalc.get(rsf), styleSink, "");
		}
		if (rowBgCalc != null) {
			styleSink.append("|_bg=");
			SH.s(rowBgCalc.get(rsf), styleSink, "");
		}
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public void getDependencies(Set<Object> r) {
		getDependencies(this.groupbyCalc, r);
		getDependencies(this.rowBgCalc, r);
		getDependencies(this.rowColorCalc, r);
		getDependencies(this.rowStyleCalc, r);
		if (this.isRecursive)
			getDependencies(this.parentGroupCalc, r);
		this.formatter.getDependencies(r);
	}
	static private void getDependencies(DerivedCellCalculator calc, Set<Object> sink) {
		DerivedHelper.getDependencyIds(calc, sink);
	}

	public boolean getIsRecursive() {
		return this.isRecursive;
	}
	@Override
	public String toString() {
		return toDerivedString();
	}
	@Override
	public String toDerivedString() {
		return getAri();
	}

	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		return sb.append(getAri());
	}
	@Override
	public String getAri() {
		return this.ari;
	}
	@Override
	public void updateAri() {
		String oldAri = this.ari;
		this.amiLayoutFullAlias = this.tree.getAmiLayoutFullAlias();
		this.amiLayoutFullAliasDotId = this.tree.getAmiLayoutFullAliasDotId();
		this.ari = AmiWebDomObject.ARI_TYPE_GROUPING + ":" + this.amiLayoutFullAliasDotId + "?" + getDomLabel();
		if (OH.ne(this.ari, oldAri)) {
			this.tree.getService().getDomObjectsManager().fireAriChanged(this, oldAri);
		}
		FastWebTree fwt = this.tree.getTree();
		String selector = AmiWebUtils.toHtmlIdSelector(this);
		//Keep grouping id consistent - AMI:GROUPING:PNLID?groupingID -> AMI:GROUPING:PNLID
		selector = SH.beforeLast(selector, '?');
		fwt.setHtmlIdSelectorForColumn(SH.toString(FastWebTree.GROUPING_COLUMN_ID), selector);
	}
	@Override
	public String getAriType() {
		return ARI_TYPE_GROUPING;
	}
	@Override
	public String getDomLabel() {
		return getAmiId();
	}
	public String getAmiId() {
		return amiId;
	}
	public void setAmiId(String amiId) {
		if (OH.eq(this.amiId, amiId))
			return;
		this.amiId = amiId;
		updateAri();
	}

	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		return Collections.EMPTY_LIST;
	}
	@Override
	public AmiWebDomObject getParentDomObject() {
		return this.tree;
	}
	@Override
	public Class<?> getDomClassType() {
		return this.getClass();
	}
	@Override
	public Object getDomValue() {
		return this;
	}
	@Override
	public boolean isTransient() {
		return isTransient;
	}
	@Override
	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}
	@Override
	public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
		return null;
	}

	private boolean isManagedByDomManager = false;
	private String amiLayoutFullAlias;
	private String amiLayoutFullAliasDotId;
	final private AmiWebFormulasImpl formulas;

	@Override
	public void addToDomManager() {
		if (this.isManagedByDomManager == false) {
			AmiWebService service = this.tree.getService();
			service.getDomObjectsManager().addManagedDomObject(this);
			service.getDomObjectsManager().fireAdded(this);
			this.isManagedByDomManager = true;

		}
	}

	@Override
	public void removeFromDomManager() {
		AmiWebDomObjectsManager domObjectsManager = this.tree.getService().getDomObjectsManager();
		for (AmiWebDomObject i : this.getChildDomObjects())
			domObjectsManager.fireRemoved(i);
		domObjectsManager.fireRemoved(this);

		if (this.isManagedByDomManager == true) {
			//Remove DomValues First

			//Remove Self
			AmiWebService service = this.tree.getService();
			service.getDomObjectsManager().removeManagedDomObject(this);
			this.isManagedByDomManager = false;
		}
	}

	@Override
	public String getAmiLayoutFullAlias() {
		return amiLayoutFullAlias;
	}

	@Override
	public String getAmiLayoutFullAliasDotId() {
		return amiLayoutFullAliasDotId;
	}

	@Override
	public AmiWebFormulas getFormulas() {
		return this.formulas;
	}

	@Override
	public AmiWebService getService() {
		return tree.getService();
	}

	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return this.tree.getFormulaVarTypes(f);
	}

	public AmiWebTreePortlet getTree() {
		return this.tree;
	}

	@Override
	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		if (formula == groupbyFormula)
			groupbyCalc = nuw;
		else if (formula == parentGroupFormula)
			parentGroupCalc = nuw;
		else if (formula == rowStyleFormula)
			rowStyleCalc = nuw;
		else if (formula == rowColorFormula)
			rowColorCalc = nuw;
		else if (formula == rowBgFormula)
			rowBgCalc = nuw;
		else
			return;

	}
	//This function should only be used carefully only call it after you need to rebuild the tree visualization
	public void flagRebuildCalcs() {
		this.tree.flagRebuildCalcs();
	}
	public void onRebuildCalcs() {
		this.formatter.onRebuildCalcs();
		this.tree.getTree().onColumnChanged(this.tree.getTree().getTreeColumn());
	}

	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

}
