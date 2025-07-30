package com.f1.ami.web.filter;

import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.filter.AmiWebFilterPortlet.Option;
import com.f1.base.Row;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeSubRangeField;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.RowHasher;
import com.f1.utils.structs.Tuple2;

public class AmiWebFilterFormPortlet_RangeSlider extends AmiWebFilterFormPortlet implements FormPortletListener {

	private AmiWebFilterPortlet filterPortlet;
	private FormPortletNumericRangeSubRangeField sliderField;
	private FormPortletCheckboxField checkBoxField;
	private HasherSet<Row> selectedRows = null;
	private Number max;
	private Number min;
	private List<Option> options;

	public AmiWebFilterFormPortlet_RangeSlider(PortletConfig config, AmiWebFilterPortlet filterPortlet) {
		super(config);
		this.filterPortlet = filterPortlet;
		this.sliderField = this.addField(new FormPortletNumericRangeSubRangeField(""));
		this.sliderField.setLeftPosPx(0);
		this.sliderField.setRightPosPx(0);
		this.checkBoxField = this.addField(new FormPortletCheckboxField("Include&nbsp;nulls", true));
		this.checkBoxField.setTopPosPx(30).setLeftPosPx(10).setWidthPx(20).setHeightPx(20);
		this.checkBoxField.setLabelSide(FormPortletField.LABEL_SIDE_RIGHT);
		this.checkBoxField.setLabelWidthPx(200);
		this.addFormPortletListener(this);
		this.getFormPortletStyle().setLabelsWidth(10);
		this.setFilterOutNulls(!this.checkBoxField.getValue());
	}

	@Override
	public boolean isValidDataType(Class<?> type) {
		return AmiUtils.isNumericType(type);
	}
	@Override
	protected boolean updateFieldAbsPositioning(FormPortletField f) {
		if (f == this.checkBoxField) {
			//			this.checkBoxField.setLeftPosPx(116);
			return true;
		} else
			return false;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.sliderField) {
			this.updateSelected();
		}
		if (field == this.checkBoxField) {
			this.setFilterOutNulls(!this.checkBoxField.getValue());
			this.updateSelected();
		}
	}

	@Override
	protected void setOptions(String title, List<Option> options) {
		super.setOptions(title, options);
		this.options = options;
		this.updateOptions();
		this.updateSelected();
	}

	private void updateOptions() {
		if (CH.isntEmpty(this.options) && (this.options.get(0).getSort() == null || CH.last(this.options).getSort() == null)) {
			this.addFieldNoThrow(this.checkBoxField);
		} else
			this.removeFieldNoThrow(this.checkBoxField);
		this.min = getMinOption(this.options);
		this.max = getMaxOption(this.options);

		if (this.min == null && this.max == null) {
			sliderField.setMin(0d);
			sliderField.setMax(0d);
			sliderField.setStep(1d);
		} else {
			sliderField.setMin(min.doubleValue());
			sliderField.setMax(max.doubleValue());
			sliderField.setStep(1d);
		}
	}

	private Number getMaxOption(List<Option> sortedOptions) {
		if (options == null || options.size() == 0)
			return null;
		for (int i = 0, l = sortedOptions.size() - 1; i <= l; l--) {
			Option o = sortedOptions.get(l);
			if (o.getSort() != null) {
				return (Number) o.getSort();
			}
		}
		return null;
	}

	private Number getMinOption(List<Option> sortedOptions) {
		if (options == null || options.size() == 0)
			return null;
		for (int i = 0, l = sortedOptions.size() - 1; i <= l; i++) {
			Option o = sortedOptions.get(i);
			if (o.getSort() != null) {
				return (Number) o.getSort();
			}
		}
		return null;
	}

	private void updateSelected() {
		Tuple2<Double, Double> val = this.sliderField.getValue();
		Number lo = val == null ? null : val.getA();
		Number up = val == null ? null : val.getB();

		this.selectedRows = new HasherSet<Row>(RowHasher.INSTANCE);

		if (lo != null && up != null && this.options != null) {
			double loval = lo.doubleValue();
			double upval = up.doubleValue();
			if (MH.isNumber(loval) && MH.isNumber(upval))
				for (int i = 0, l = this.options.size(); i < l; i++) {
					Option option = this.options.get(i);
					Number value = (Number) option.getSort();
					if (value == null) {
						if (this.getFilerOutNulls() == false)
							this.selectedRows.addAll(option.getRows());
						continue;
					}
					double valueval = value.doubleValue();
					if (MH.isNumber(valueval) && OH.isBetween(valueval, loval, upval))
						this.selectedRows.addAll(option.getRows());
				}
		}

		this.filterPortlet.onValuesChanged();
	}
	@Override
	HasherSet<Row> getSelectedRows() {
		return this.selectedRows;
	}

	@Override
	void clearSelectedRows() {
		this.selectedRows = null;
		if (this.min == null && this.max == null) {
			sliderField.setValue(0d, 0d);
			sliderField.setStep(1d);
		} else {
			sliderField.setValue(min.doubleValue(), max.doubleValue());
			sliderField.setStep(1d);
		}
		this.checkBoxField.setValue(Boolean.TRUE);
	}

}
