package com.f1.ami.web.charts;

import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.SH;

public class AmiWebChartEditFormula_Simple extends AmiWebChartEditFormula<AmiWebChartFormula_Simple> {

	private String label;//, value;
	//	private AmiWebChartFormula formulaValue;
	private FormPortletTextField field;
	private AmiWebChartFormula_Simple copyFromFormula;
	//	private String defaultValue;

	public AmiWebChartEditFormula_Simple(int pos, AmiWebChartEditSeriesPortlet<?> target, AmiWebChartFormula_Simple formula) {
		super(pos, target, formula);
		this.field = new FormPortletTextField(formula.getLabel());
		field.setMaxChars(4048);
		field.setHasButton(true);
		field.setCssStyle("_fm=courier");
		field.setTopPosPx(pos).setRightPosPx(15).setLeftPosPx(120).setHeightPx(20);
		field.setDefaultValue(formula.getValue());
		field.setCorrelationData(this);
		if (pos == -1)
			return;
		target.getForm().addField(field);

	}
	public String getLabel() {
		return label;
	}
	public String getValue() {
		if (this.copyFromFormula != null)
			return copyFromFormula.getValue();
		if (this.field == null)
			return null;
		return this.field.getValue();
	}

	public FormPortletField<String> getField() {
		return this.field;
	}
	@Override
	public boolean test(StringBuilder sb) {
		return getFormula().testValue(getValue(), sb);
	}
	@Override
	public void applyValue() {
		formula.setValue(getValue());
	}
	@Override
	public void resetFromFormula() {
		FormPortletField<String> field2 = getField();
		if (field2 != null) {
			AmiWebChartFormula_Simple formula2 = getFormula();
			String value = formula2.getValue();
			field2.setValue(value);
		}
	}
	@Override
	public void setTitle(String title) {
		this.field.setTitle(title);
	}
	public void setCopyFromFormula(AmiWebChartFormula_Simple formula) {
		this.copyFromFormula = formula;
	}
	public AmiWebChartEditFormula_Simple setValueIfNotPopulated(String value) {
		if (isPopulated())
			return this;
		//		this.defaultValue = value;
		this.field.setValue(value);
		return this;
	}
	public void overrideValue(String value) {
		this.field.setValue(value);
	}
	@Override
	public String getTitle() {
		return this.field.getTitle();
	}
	@Override
	public boolean isPopulated() {
		return SH.is(this.getValue());
	}

}
