
package com.f1.ami.web.tree;

public class FormulaWrapper {

	private AmiWebTreeGroupBy formula;
	public String description, color, bgColor, font, style, orderBy;
	public String rowBgColor, rowStyle, rowColor;
	public String groupby;
	public String amiId;
	public String parentGroup;
	public int position;

	public FormulaWrapper(AmiWebTreeGroupBy formula, int position) {
		this.formula = formula;
		if (formula != null) {
			this.amiId = formula.getAmiId();
			this.parentGroup = formula.getParentGroup(false);
			this.description = formula.getFormatter().getDescription(false);
			this.orderBy = formula.getFormatter().getOrderBy(false);
			this.style = formula.getFormatter().getStyle(false);
			this.bgColor = formula.getFormatter().getBackgroundColor(false);
			this.color = formula.getFormatter().getColor(false);
			this.groupby = formula.getGroupby(false);
			this.rowStyle = formula.getRowStyle(false);
			this.rowBgColor = formula.getRowBackgroundColor(false);
			this.rowColor = formula.getRowColor(false);
		}
		this.position = position;
	}
	public AmiWebTreeGroupBy getFormula() {
		return this.formula;
	}
	public boolean setFormula(AmiWebTreeGroupBy f, StringBuilder sb) {
		return f.setFormula(position == -1, groupby, parentGroup, description, orderBy, style, color, bgColor, rowStyle, rowColor, rowBgColor);
	}
	public void setAmiId(String amiId) {
		this.amiId = amiId;
	}
}