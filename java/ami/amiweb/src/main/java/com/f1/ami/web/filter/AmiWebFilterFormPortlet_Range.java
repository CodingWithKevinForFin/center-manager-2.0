package com.f1.ami.web.filter;

import java.util.List;
import java.util.Map;

import com.f1.ami.web.filter.AmiWebFilterPortlet.Option;
import com.f1.base.Row;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.RowHasher;

public class AmiWebFilterFormPortlet_Range extends AmiWebFilterFormPortlet implements FormPortletListener {

	private AmiWebFilterPortlet filterPortlet;
	private FormPortletSelectField<Integer> selectFieldMin;
	private FormPortletSelectField<Integer> selectFieldMax;
	private HasherSet<Row> selectedRows = null;

	public AmiWebFilterFormPortlet_Range(PortletConfig config, AmiWebFilterPortlet filterPortlet) {
		super(config);
		this.filterPortlet = filterPortlet;
		this.selectFieldMin = this.addField(new FormPortletSelectField<Integer>(Integer.class, "Min:"));
		this.selectFieldMax = this.addField(new FormPortletSelectField<Integer>(Integer.class, "Max:"));
		this.addFormPortletListener(this);
		this.getFormPortletStyle().setLabelsWidth(35);
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.selectFieldMin || field == this.selectFieldMax)
			updateSelected();
	}
	@Override
	public void setOptions(String title, List<Option> options) {
		super.setOptions(title, options);
		updateOptions(this.selectFieldMin, options, "  <no minimum>");
		updateOptions(this.selectFieldMax, options, "  <no maximum>");
		updateSelected();
		setFieldAbsPositioning();
	}
	private void updateOptions(FormPortletSelectField<Integer> selectField, List<Option> options, String emptyText) {
		FormPortletSelectField.Option<Integer> o2 = selectField.getValueOption();
		Option keepSelected = o2 == null ? null : (Option) o2.getCorrelationData();
		selectField.clearOptions();
		selectField.addOption(-1, emptyText, null);
		selectField.setValue(-1);
		int id = 0;
		for (Option o : (options)) {
			selectField.addOption(id, o.getDisplay(), null).setCorrelationData(o);
			if (keepSelected != null && OH.eq(keepSelected.getKey(), o.getKey())) {
				selectField.setValue(id);
				this.selectedRows = o.getRows();
			}
			id++;
		}
	}

	private void updateSelected() {
		int min = this.selectFieldMin.getValue();
		int max = this.selectFieldMax.getValue();
		if (min == -1 && max == -1) {
			this.selectedRows = null;
		} else {
			if (min != -1 && max != -1) {
				if (min > max) {
					int t = min;
					min = max;
					max = t;
					this.selectFieldMin.setValue(min);
					this.selectFieldMax.setValue(max);
				}
			} else if (min == -1)
				min = 0;
			else if (max == -1)
				max = this.selectFieldMax.getOptionsCount() - 2;

			if (min == max) {
				this.selectedRows = ((Option) this.selectFieldMin.getOption(min).getCorrelationData()).getRows();
			} else {
				this.selectedRows = new HasherSet<Row>(RowHasher.INSTANCE);
				for (int i = min; i <= max; i++)
					this.selectedRows.addAll(((Option) this.selectFieldMin.getOption(i).getCorrelationData()).getRows());
			}
		}
		this.filterPortlet.onValuesChanged();

	}

	@Override
	public HasherSet<Row> getSelectedRows() {
		return this.selectedRows;
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
	}

	@Override
	void clearSelectedRows() {
		this.selectedRows = null;
		this.selectFieldMin.setValue(-1);
		this.selectFieldMax.setValue(-1);
		//		this.filterPortlet.onValuesChanged();
	}

}
