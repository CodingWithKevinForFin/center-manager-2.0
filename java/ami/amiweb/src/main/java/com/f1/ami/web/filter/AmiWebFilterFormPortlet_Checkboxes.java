package com.f1.ami.web.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.ami.web.filter.AmiWebFilterPortlet.Option;
import com.f1.base.Row;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.RowHasher;
import com.f1.utils.structs.Tuple2;

public class AmiWebFilterFormPortlet_Checkboxes extends AmiWebFilterFormPortlet implements FormPortletListener {

	private AmiWebFilterPortlet filterPortlet;
	private HashMap<Tuple2<String, String>, FormPortletCheckboxField> selectedFields = new HashMap<Tuple2<String, String>, FormPortletCheckboxField>();
	private HasherSet<Row> selectedRows = new HasherSet<Row>(RowHasher.INSTANCE);
	private int suggestedLabelsWidth = 0;

	public AmiWebFilterFormPortlet_Checkboxes(PortletConfig config, AmiWebFilterPortlet filterPortlet) {
		super(config);
		this.filterPortlet = filterPortlet;
		this.addFormPortletListener(this);
		this.getFormPortletStyle().setLabelsWidth(250);
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field instanceof FormPortletCheckboxField) {
			FormPortletCheckboxField cb = (FormPortletCheckboxField) field;
			Option o = (Option) cb.getCorrelationData();
			if (cb.getBooleanValue()) {
				this.selectedFields.put(o.getKey(), cb);
				this.selectedRows.addAll(o.getRows());
				filterPortlet.onValuesChanged();
			} else {
				this.selectedFields.remove(o.getKey());
				this.selectedRows.removeAll(o.getRows());
				filterPortlet.onValuesChanged();
			}
		}
	}

	@Override
	public void setOptions(String title, List<Option> options) {
		super.setOptions(title, options);
		clearFields();
		addField(getTitleField());
		List<FormPortletCheckboxField> keepSelected = new ArrayList<FormPortletCheckboxField>();
		int cnt = 0;
		int maxCheckboxes = this.filterPortlet.getMaxOptions(true);
		int maxLength = 0;
		String fontMetricsText = null;
		for (Option o : options) {
			FormPortletCheckboxField field = new FormPortletCheckboxField(o.getDisplay()).setCorrelationData(o);
			field.setBgColor(getFieldsBackgroundColor());
			field.setFontColor(getFieldsFontColor());
			field.setBorderColor(getFieldBorderColor());
			field.setLabelSide(FormPortletField.LABEL_SIDE_RIGHT);
			field.setLabelSideAlignment(FormPortletField.LABEL_SIDE_ALIGN_CENTER);
			field.setLabelPaddingPx(10);
			if (this.selectedFields.containsKey(o.getKey())) {
				keepSelected.add(field);
				field.setValue(true);
			}

			if (o.getStyle() != null)
				field.setLabelCssStyle(o.getStyle());
			if (SH.length(o.getDisplay()) > maxLength) {
				maxLength = SH.length(o.getDisplay());
				fontMetricsText = o.getDisplay();
			}
			// at this point, everything in the field needs to be set. Anything set to the field after this point has no effect on JS rendering.
			addField(field);

			if (++cnt >= maxCheckboxes) {
				break;
			}

		}
		PortletMetrics portletMetrics = this.getManager().getPortletMetrics();
		String style = this.getFontFamily() != null ? "_fm=" + this.getFontFamily() : null;
		//		Integer fontSize = this.getFontSize();

		this.suggestedLabelsWidth = portletMetrics.getWidth(fontMetricsText, style, this.getTitleFontSize() + 2); // 2 is safety factor
		// need to clear selected fields because it is possible that previously selected fields do not exist any more in the most recent set of options
		this.selectedFields.clear();
		this.selectedRows.clear();
		for (FormPortletCheckboxField cb : keepSelected) {
			Option o = (Option) cb.getCorrelationData();
			this.selectedFields.put(o.getKey(), cb);
			this.selectedRows.addAll(o.getRows());
		}
		setFieldAbsPositioning();
	}

	@Override
	public HasherSet<Row> getSelectedRows() {
		if (this.selectedRows.size() == 0)
			return null;
		return this.selectedRows;
	}

	@Override
	void clearSelectedRows() {
		this.selectedRows.clear();
		for (FormPortletCheckboxField checkbox : this.selectedFields.values()) {
			checkbox.setValue(false);
		}
		//		this.filterPortlet.onValuesChanged();
	}
	@Override
	protected boolean updateFieldAbsPositioning(FormPortletField f) {
		f.setWidthPx(f.getDefaultWidth());
		f.setLeftPosPx(10);
		f.setLabelWidthPx(this.suggestedLabelsWidth);
		return true;
	}

}
