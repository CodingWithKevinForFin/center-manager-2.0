package com.f1.ami.web.form.queryfield;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.f1.ami.web.AmiWebOverrideValue;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDmListener;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormTextFieldFactory;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.TableList;
import com.f1.suite.web.portal.impl.form.FormPortletFieldAutoCompleteExtension;
import com.f1.suite.web.portal.impl.form.FormPortletTextEditField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;

public class TextQueryField extends AbstractDmQueryField<FormPortletTextField> implements AmiWebDmListener {
	final public static int AUTOCOMPLETE_DELIM_MAXLEN = 5;
	private FormPortletFieldAutoCompleteExtension<FormPortletTextEditField> autocompleteExtension;
	private AmiWebOverrideValue<String> autocompleteDelim = new AmiWebOverrideValue<String>(null);
	private AmiWebOverrideValue<Boolean> isShowOptionsImmediately = new AmiWebOverrideValue<Boolean>(false);
	private AmiWebOverrideValue<Boolean> performSubstringMatching = new AmiWebOverrideValue<Boolean>(false);

	public TextQueryField(AmiWebFormTextFieldFactory factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletTextField("").setValue(""));
		this.autocompleteExtension = new FormPortletFieldAutoCompleteExtension<FormPortletTextEditField>(this.getField());
	}
	@Override
	public void init(Map<String, Object> initArgs) {
		super.init(initArgs);
		if (getDmName() == null) {
			LinkedHashMap<String, String> options = toValues(CH.getOr(Caster_Simple.OBJECT, initArgs, "v", null));
			if (options != null)
				setCustomOptions(options);
		}
		boolean showOptionsImmediately = CH.getOrNoThrow(Caster_Boolean.PRIMITIVE, initArgs, "soi", false);
		setShowOptionsImmediately(showOptionsImmediately, false);
		boolean performSubstringMatching = CH.getOrNoThrow(Caster_Boolean.PRIMITIVE, initArgs, "substrmatch", false);
		setPerformSubstringMatching(performSubstringMatching, false);
		String acDelim = CH.getOrNoThrow(Caster_String.INSTANCE, initArgs, "acdelim", null);
		setAutocompleteDelimiter(acDelim, false);
	}

	@Override
	public Map<String, Object> getJson(Map<String, Object> sink) {
		if (this.getDmName() == null)
			AmiWebUtils.putSkipEmpty(sink, "v", fromValues(getCustomOptions()));
		if (this.isShowOptionsImmediately(false))
			sink.put("soi", this.isShowOptionsImmediately(false));
		if (this.getPerformSubstringMatching(false))
			sink.put("substrmatch", this.getPerformSubstringMatching(false));
		if (SH.is(getAutocompleteDelimeter(false)))
			sink.put("acdelim", getAutocompleteDelimeter(false));
		return super.getJson(sink);
	}

	@Override
	public void onDataChanged(TableList rows, Column idCl, Column vlCl) {
		int vlCol = vlCl.getLocation();
		List<String> added = new ArrayList<String>();
		for (Row row : rows) {
			String suggestion = row.getAt(vlCol, Caster_String.INSTANCE);
			added.add(suggestion);
		}

		this.getAutocompleteExtension().setSuggestions(added);
	}
	public FormPortletFieldAutoCompleteExtension<FormPortletTextEditField> getAutocompleteExtension() {
		return autocompleteExtension;
	}

	public boolean isShowOptionsImmediately(boolean isOverride) {
		return this.isShowOptionsImmediately.getValue(isOverride);
	}
	public void setShowOptionsImmediately(boolean showOptionsImmediately, boolean isOverride) {
		if (this.isShowOptionsImmediately.setValue(showOptionsImmediately, isOverride))
			this.getAutocompleteExtension().setShowOptionsImmediately(showOptionsImmediately);
	}

	public boolean getPerformSubstringMatching(boolean isOverride) {
		return this.performSubstringMatching.getValue(isOverride);
	}
	public void setPerformSubstringMatching(boolean performSubstringMatching, boolean isOverride) {
		if (this.performSubstringMatching.setValue(performSubstringMatching, isOverride))
			this.getAutocompleteExtension().setPerformSubstringMatching(performSubstringMatching);
	}
	public static short getShortValForDisplaySort(String displayValSortOption) {
		short sorting;
		if (AbstractDmQueryField.DISPLAY_VAL_SORT_ASC.equals(displayValSortOption))
			sorting = FormPortletFieldAutoCompleteExtension.SORTING_ASCENDING;
		else if (AbstractDmQueryField.DISPLAY_VAL_SORT_DESC.equals(displayValSortOption))
			sorting = FormPortletFieldAutoCompleteExtension.SORTING_DESCENDING;
		else
			sorting = FormPortletFieldAutoCompleteExtension.SORTING_NONE;
		return sorting;
	}
	@Override
	public void setDisplaySortOption(String displaySort, boolean isOverride) {
		if (getDisplayValSortOverrideObj().setValue(displaySort, isOverride)) {
			super.setDisplaySortOption(displaySort, isOverride);
			this.getAutocompleteExtension().setSorting(TextQueryField.getShortValForDisplaySort(displaySort));
		}
	}
	public String getAutocompleteDelimeter(boolean isOverride) {
		return this.autocompleteDelim.getValue(isOverride);
	}
	public void setAutocompleteDelimiter(String delim, boolean isOverride) {
		if (autocompleteDelim.setValue(delim, isOverride)) {
			delim = SH.isnt(delim) ? null : delim;
			this.autocompleteExtension.setDelimiter(delim);
		}
	}
}
