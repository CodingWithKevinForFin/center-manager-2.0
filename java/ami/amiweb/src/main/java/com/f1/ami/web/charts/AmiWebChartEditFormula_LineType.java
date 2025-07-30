package com.f1.ami.web.charts;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.utils.SH;

public class AmiWebChartEditFormula_LineType extends AmiWebChartEditFormula<AmiWebChartFormula_Simple> {

	//	private int pos;
	private FormPortletSelectField<String> field;
	private AmiWebChartFormula_Simple copyFromFormula;
	private String defaultValue;

	public AmiWebChartEditFormula_LineType(int pos, AmiWebChartEditSeriesPortlet target, AmiWebChartFormula_Simple formula) {
		super(pos, target, formula);
		field = new FormPortletSelectField<String>(String.class, formula.getLabel()).setName(AmiWebChartSeries.PARAM_LN_DIR);
		field.addOption(null, "Direct - Angle Line");
		field.addOption('"' + AmiWebChartSeries_Graph.LINE_HORZ + '"', "Corner - First Horizontal, then Vertical");
		field.addOption('"' + AmiWebChartSeries_Graph.LINE_VERT + '"', "Corner - First Vertical, then Horizontal");
		field.addOption('"' + AmiWebChartSeries_Graph.LINE_HORZ_QUAD_BEZIER + '"', "Curved - Horizontal Quadratic Bezier");
		field.addOption('"' + AmiWebChartSeries_Graph.LINE_VERT_QUAD_BEZIER + '"', "Curved - Vertical Quadratic Bezier");
		field.addOption('"' + AmiWebChartSeries_Graph.LINE_HORZ_CUBIC_BEZIER + '"', "Curved - Horizontal Cubic Bezier");
		field.addOption('"' + AmiWebChartSeries_Graph.LINE_VERT_CUBIC_BEZIER + '"', "Curved - Vertical Cubic Bezier");

		field.setDefaultValue(formula.getValue());
		if (pos != -1) {
			field.setTopPosPx(pos).setRightPosPx(15).setLeftPosPx(120).setHeightPx(20);
			this.target.getForm().addField(field);
		}
		field.setCorrelationData(formula);
	}

	@Override
	public FormPortletField<?> getField() {
		return this.field;
	}

	@Override
	public boolean test(StringBuilder sb) {
		return true;
	}

	@Override
	public void applyValue() {
		this.formula.setValue(field.getValue());
	}

	@Override
	public void resetFromFormula() {
		field.setValue(this.formula.getValue());
	}
	@Override
	public void setTitle(String title) {
		this.field.setTitle(title);
	}

	public AmiWebChartEditFormula_LineType setCopyFromFormula(AmiWebChartFormula_Simple formula) {
		this.copyFromFormula = formula;
		return this;
	}
	public AmiWebChartEditFormula_LineType setDefaultValue(String value) {
		this.defaultValue = value;
		return this;
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		return null;
	}

	@Override
	public String getTitle() {
		return this.field.getTitle();
	}
	@Override
	public boolean isPopulated() {
		String val = this.getValue();
		if ("null".equals(val))
			return false;
		return SH.is(val);
	}
	public String getValue() {
		if (isVisible())
			return this.field.getValue();
		if (this.copyFromFormula != null)
			return copyFromFormula.getValue();
		if (this.defaultValue != null)
			return this.defaultValue;
		return null;
	}
}
