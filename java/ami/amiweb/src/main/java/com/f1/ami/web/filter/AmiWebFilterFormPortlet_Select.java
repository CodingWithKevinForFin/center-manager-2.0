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

public class AmiWebFilterFormPortlet_Select extends AmiWebFilterFormPortlet implements FormPortletListener {

	private static final int OPTION_NO_FILTER = -1;
	private AmiWebFilterPortlet filterPortlet;
	private FormPortletSelectField<Integer> selectField;
	private HasherSet<Row> selectedRows = null;

	public AmiWebFilterFormPortlet_Select(PortletConfig config, AmiWebFilterPortlet filterPortlet) {
		super(config);
		this.filterPortlet = filterPortlet;
		this.selectField = this.addField(new FormPortletSelectField<Integer>(Integer.class, ""));
		this.addFormPortletListener(this);
		this.getFormPortletStyle().setLabelsWidth(10);
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.selectField) {
			FormPortletSelectField.Option<Integer> o = this.selectField.getValueOption();
			Option op = (Option) o.getCorrelationData();
			this.selectedRows = op == null ? null : op.getRows();
			this.filterPortlet.onValuesChanged();
		}
	}

	@Override
	public void setOptions(String title, List<Option> options) {
		super.setOptions(title, options);
		FormPortletSelectField.Option<Integer> o2 = this.selectField.getValueOption();
		selectedRows = null;
		Option keepSelected = o2 == null ? null : (Option) o2.getCorrelationData();
		this.selectField.clearOptions();
		this.selectField.addOption(OPTION_NO_FILTER, "  <no filter>  ", null);
		int id = 0;
		for (Option o : (options)) {
			this.selectField.addOption(id, o.getDisplay(), null).setCorrelationData(o);
			if (keepSelected != null && OH.eq(keepSelected.getKey(), o.getKey())) {
				this.selectField.setValue(id);
				this.selectedRows = o.getRows();
			}
			id++;
		}
		setFieldAbsPositioning();
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
		this.selectField.setValue(OPTION_NO_FILTER);
		//		this.filterPortlet.onValuesChanged();
	}

}
