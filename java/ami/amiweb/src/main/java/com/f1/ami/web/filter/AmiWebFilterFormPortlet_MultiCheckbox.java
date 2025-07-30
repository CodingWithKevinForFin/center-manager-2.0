package com.f1.ami.web.filter;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.filter.AmiWebFilterPortlet.Option;
import com.f1.base.Row;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletMultiCheckboxField;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.RowHasher;

public class AmiWebFilterFormPortlet_MultiCheckbox extends AmiWebFilterFormPortlet implements FormPortletListener {

	private AmiWebFilterPortlet filterPortlet;
	private FormPortletMultiCheckboxField<String> multiCheckboxField = new FormPortletMultiCheckboxField(String.class, "");
	private HasherSet<Row> selectedRows = new HasherSet<Row>(RowHasher.INSTANCE);

	public AmiWebFilterFormPortlet_MultiCheckbox(PortletConfig config, AmiWebFilterPortlet filterPortlet) {
		super(config);
		this.filterPortlet = filterPortlet;
		this.addField(multiCheckboxField);
		multiCheckboxField.setLabelSide(FormPortletField.LABEL_SIDE_RIGHT);
		multiCheckboxField.setLabelSideAlignment(FormPortletField.LABEL_SIDE_ALIGN_CENTER);
		this.addFormPortletListener(this);
		this.getFormPortletStyle().setLabelsWidth(250);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field instanceof FormPortletMultiCheckboxField) {
			Collection<FormPortletMultiCheckboxField.Option> selectedOptions = this.multiCheckboxField.getSelectedOptions();
			this.selectedRows.clear();
			for (FormPortletMultiCheckboxField.Option o : selectedOptions) {
				Option op = (Option) o.getCorrelationData();
				this.selectedRows.addAll(op.getRows());
			}
			filterPortlet.onValuesChanged();
		}
	}

	@Override
	public void setOptions(String title, List<Option> options) {
		super.setOptions(title, options);
		int cnt = 0;
		int maxCheckboxes = this.filterPortlet.getMaxOptions(true);
		FormPortletMultiCheckboxField<String> field = this.multiCheckboxField;
		Set<String> keepSelected = new HashSet<String>(field.getSelectedValueKeys());
		HashMap<String,String> persist = new HashMap<String,String>();
		// keep options that didn't change
		this.keepSame(persist, options);
		// clear all existing options/selections
		field.clear();
		// add new options
		for (Option o : options) {
			String key = o.getKey().getA();
			// reuse old id if new option == old option
			if (persist.containsKey(key)) {
				field.addOption3(persist.get(key), key, o.getDisplay()).setCorrelationData(o);
			} else
				field.addOption2(key, o.getDisplay()).setCorrelationData(o);
			if (++cnt >= maxCheckboxes)
				break;
		}
		// update this.selectedRows, this gets used later when we reprocess filter
		this.updateSelection(keepSelected);
		if (!keepSelected.isEmpty()) {
			// no need to call setValue because changing option will call it
			field.getSelectedValueKeys().addAll(keepSelected);
		}
		setFieldAbsPositioning();
	}

	@Override
	public HasherSet<Row> getSelectedRows() {
		return selectedRows.size() == 0 ? null : this.selectedRows;
	}

	@Override
	void clearSelectedRows() {
		this.selectedRows.clear();
		this.multiCheckboxField.clearSelected();
		//		this.filterPortlet.onValuesChanged();
	}
	
	void keepSame(HashMap<String,String> sink, List<Option> options) {
		// loop over new options, check if each option exists already
		for (Option newOp:options) {
			FormPortletMultiCheckboxField.Option<String> opt =this.multiCheckboxField.getOption(newOp.getKey().getA());
			if (opt != null) {
				sink.put(newOp.getKey().getA(), opt.getId());
			}
		}
	}
	
	// ensures previous selection still exists in current options
	public void updateSelection(Set<String> selection) {
		this.selectedRows.clear();
		Iterator<String> it = selection.iterator();
		while (it.hasNext()) {
			String key = it.next();
			FormPortletMultiCheckboxField.Option<String> option = this.multiCheckboxField.getOption(key);
			if (option != null) {
				Option o = (Option)option.getCorrelationData();
				// update selectedRows since value might have changed
				this.selectedRows.addAll(o.getRows());
			} else {
				it.remove();
			}
		}
	}
}
