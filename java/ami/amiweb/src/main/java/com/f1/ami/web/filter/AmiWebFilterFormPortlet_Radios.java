package com.f1.ami.web.filter;

import java.util.List;
import java.util.Map;

import com.f1.ami.web.filter.AmiWebFilterPortlet.Option;
import com.f1.base.Row;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletRadioButtonField;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.RowHasher;

public class AmiWebFilterFormPortlet_Radios extends AmiWebFilterFormPortlet implements FormPortletListener {
	private AmiWebFilterPortlet filterPortlet;
	private HasherSet<Row> selectedRows = new HasherSet<Row>(RowHasher.INSTANCE);
	private FormPortletRadioButtonField checkedField;
	private FormPortletButtonField resetField;
	private int suggestedLabelsWidth = 0;

	public AmiWebFilterFormPortlet_Radios(PortletConfig config, AmiWebFilterPortlet filterPortlet) {
		super(config);
		this.filterPortlet = filterPortlet;
		this.addFormPortletListener(this);
		this.getFormPortletStyle().setLabelsWidth(50);

	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field instanceof FormPortletRadioButtonField) {
			clearSelectedRows();
			FormPortletRadioButtonField rbf = (FormPortletRadioButtonField) field;
			Option o = (Option) rbf.getCorrelationData();
			this.checkedField = rbf;
			this.selectedRows.addAll(o.getRows());
			filterPortlet.onValuesChanged();
		} else if (field == this.resetField) {
			clearSelectedRows();
			if (this.checkedField != null) {
				this.checkedField.setValue(false);
			}
			filterPortlet.getDm().getDm().reprocessFilters(filterPortlet.getTargetTableName());
		}
	}

	@Override
	public void setOptions(String title, List<Option> options) {
		super.setOptions(title, options);
		clearFields();
		addField(getTitleField());
		this.resetField = this.addFieldAfter(getTitleField(), new FormPortletButtonField("").setValue("Reset"));
		int cnt = 0;
		int maxRadioButtons = this.filterPortlet.getMaxOptions(true);
		int maxLength = 0;
		String fontMetricsText = null;
		for (Option o : options) {
			FormPortletRadioButtonField field = (FormPortletRadioButtonField) new FormPortletRadioButtonField(o.getDisplay()).setCorrelationData(o);
			field.setLabelSide(FormPortletField.LABEL_SIDE_RIGHT);
			field.setLabelSideAlignment(FormPortletField.LABEL_SIDE_ALIGN_CENTER);
			if (o.getStyle() != null)
				field.setLabelCssStyle(o.getStyle());
			if (SH.length(o.getDisplay()) > maxLength) {
				maxLength = SH.length(o.getDisplay());
				fontMetricsText = o.getDisplay();
			}
			addField(field);
			if (++cnt >= maxRadioButtons) {
				break;
			}
		}
		this.suggestedLabelsWidth = this.getManager().getPortletMetrics().getWidth(fontMetricsText, this.getFontFamily() != null ? "_fm=" + this.getFontFamily() : null,
				this.getFontSize());
		this.selectedRows.clear();
		setFieldAbsPositioning();
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	HasherSet<Row> getSelectedRows() {
		if (this.selectedRows.size() == 0)
			return null;
		return this.selectedRows;
	}

	@Override
	void clearSelectedRows() {
		this.selectedRows.clear();
	}

	@Override
	protected boolean updateFieldAbsPositioning(FormPortletField f) {
		if (f == getTitleField())
			f.setWidthPx(f.getDefaultWidth());
		else if (f == this.resetField)
			f.setWidthPx(50);
		else
			f.setWidthPx(19);
		f.setLeftPosPx(10);
		f.setLabelWidthPx(this.suggestedLabelsWidth);
		return true;
	}
}
