package com.f1.ami.web.filter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.f1.ami.web.filter.AmiWebFilterPortlet.Option;
import com.f1.base.Row;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.RowHasher;

public class AmiWebFilterFormPortlet_Search extends AmiWebFilterFormPortlet implements FormPortletListener {

	private AmiWebFilterPortlet filterPortlet;
	private FormPortletTextField searchField;
	private HasherSet<Row> selectedRows = null;
	private List<Option> options = Collections.EMPTY_LIST;

	public AmiWebFilterFormPortlet_Search(PortletConfig config, AmiWebFilterPortlet filterPortlet) {
		super(config);
		this.filterPortlet = filterPortlet;
		this.searchField = this.addField(new FormPortletTextField(""));
		this.addFormPortletListener(this);
		this.getFormPortletStyle().setLabelsWidth(10);
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.searchField) {
			String s = this.searchField.getValue();
			HasherSet<Row> sr;
			if (SH.is(s)) {
				sr = null;
				int found = 0;
				TextMatcher matcher = SH.m(this.searchField.getValue());
				for (int i = 0, l = this.options.size(); i < l; i++) {
					Option option = this.options.get(i);
					if (matcher.matches(option.getDisplay())) {
						found++;
						switch (found) {
							case 1:
								sr = option.getRows();
								break;
							case 2:
								sr = new HasherSet<Row>(RowHasher.INSTANCE, sr);
							default:
								sr.addAll(option.getRows());
						}
					}
				}
				if (sr == null)
					sr = new HasherSet<Row>(RowHasher.INSTANCE);
			} else
				sr = null;
			if (OH.eq(sr, this.selectedRows))
				return;
			this.selectedRows = sr;
			this.filterPortlet.onValuesChanged();
		}
	}
	@Override
	public void setOptions(String title, List<Option> options) {
		super.setOptions(title, options);
		this.options = options;
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
		this.searchField.setValue(null);
		//		this.filterPortlet.onValuesChanged();
	}

}
