package com.f1.suite.web.portal.impl.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.TableList;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Short;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.BasicDataList;
import com.f1.utils.structs.BasicDataView;
import com.f1.utils.structs.ComparableComparator;
import com.f1.utils.structs.DataList;
import com.f1.utils.structs.ReverseComparator;

public class FormPortletFieldAutoCompleteExtension<T extends FormPortletTextEditField> implements FormPortletFieldExtension {
	public static final short SORTING_NONE = 0;
	public static final short SORTING_ASCENDING = 1;
	public static final short SORTING_DESCENDING = 2;
	//arrow up/down position indicators
	public static final short START_OF_SUGGESTION = 1;
	public static final short END_OF_SUGGESTION = 2;
	public static final short MIDDLE_OF_SUGGESTION = 3;

	private static final String DEFAULT_PREFIX = "(?i)^";
	private static final String DEFAULT_POSTFIX = "";
	private static final int BOUNDS_NONE = -1;
	private static final int BOUNDS_LOWER_DEFAULT = 0;
	private static final int BOUNDS_UPPER_DEFAULT = 19;
	public static int DEFAULT_LIMIT = 20;
	private T textEditField;
	private static final String CALLBACK_ONBOUNDS_CHANGE = "onBoundsChange";
	private static final String CALLBACK_ON_OPEN_SUGGESTIONS = "onOpen";
	private static final String CALLBACK_ON_CLOSE_SUGGESTIONS = "onClose";
	private static final String CALLBACK_ON_SELECT = "onSelect";
	private BasicDataList<String> suggestions2 = new BasicDataList<String>();
	private BasicDataList<String> suggestionsSorted = new BasicDataList<String>();
	private BasicDataList<String> suggestionsFiltered = new BasicDataList<String>();
	private boolean flagSuggestionsChanged = false;
	private boolean flagShowSuggestions = false;
	private boolean showOptionsImmediately = false;
	private boolean performSubstringMatching = false;
	private int limit;
	private String prefix;
	private String prefix2;
	private String postfix;
	private int extensionIndex;
	private short sorting;
	private String delimiter;
	private final String jsObjectName = "e";
	private StringBuilder updatePendingJs = new StringBuilder();
	private boolean flagPendingJsUpdate = false;
	private boolean flagSizeChanged = false;
	private JsSelectBox jsSelectBoxView;
	private int lowerPos = BOUNDS_NONE;
	private int upperPos = BOUNDS_NONE;

	public FormPortletFieldAutoCompleteExtension(T textEditField) {
		this.textEditField = textEditField;
		this.extensionIndex = this.textEditField.addExtension(this);
		this.jsSelectBoxView = new JsSelectBox();

		this.setPrefix(DEFAULT_PREFIX);
		this.setPostfix(DEFAULT_POSTFIX);
		this.setLimit(DEFAULT_LIMIT);
	}

	/*
	 * Handles Js Callbacks
	 * 
	 * - onSelect( int sel )
	 * - onBoundsChange( int lower, int upper )
	 * - onOpen( int lower, int upper )
	 * - onClose( )
	 */
	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if (CALLBACK_ON_SELECT.equals(callback)) {
			final int sel = CH.getOr(Caster_Integer.INSTANCE, attributes, "sel", -1);
			if (sel == -1)
				throw new IllegalStateException("Selected unknown index");
			String val = this.suggestionsFiltered.get(sel);
			this.textEditField.setValue(val);
		} else if (CALLBACK_ONBOUNDS_CHANGE.equals(callback)) {
			final int lower = CH.getOr(Caster_Integer.INSTANCE, attributes, "s", -1);
			final int upper = CH.getOr(Caster_Integer.INSTANCE, attributes, "e", -1);
			final boolean updateSelection = CH.getOr(Caster_Boolean.INSTANCE, attributes, "updateSelection", false);
			final short startOrEndOfSuggestion = CH.getOr(Caster_Short.INSTANCE, attributes, "startOrEndOfSuggestion", (short) -1);

			this.lowerPos = lower;
			this.upperPos = upper;

			this.jsSelectBoxView.onViewBoundsChanged(suggestionsFiltered, lower, upper);
			this.callJs_sizeChanged(this.suggestionsFiltered.size());
			this.callJs_updateSuggestions();
			this.callJs_updateMenu(updateSelection, startOrEndOfSuggestion);

		} else if (CALLBACK_ON_OPEN_SUGGESTIONS.equals(callback)) {
			final int lower = CH.getOr(Caster_Integer.INSTANCE, attributes, "s", BOUNDS_NONE);
			final int upper = CH.getOr(Caster_Integer.INSTANCE, attributes, "e", BOUNDS_NONE);

			this.lowerPos = lower == BOUNDS_NONE ? BOUNDS_LOWER_DEFAULT : lower;
			this.upperPos = upper == BOUNDS_NONE ? BOUNDS_UPPER_DEFAULT : upper;
			this.resetSuggestions();
			this.onSuggestionsChanged();
			this.showSuggestions(lowerPos, upperPos);
		} else if (CALLBACK_ON_CLOSE_SUGGESTIONS.equals(callback)) {
			this.resetSuggestions();
			this.callJs_hide();
		}
	}

	@Override
	public void onUserValueChanged(Map<String, String> attributes) {
		String type = CH.getOr(attributes, "type", "");
		boolean setValueEvent = CH.getOrNoThrow(Caster_Boolean.INSTANCE, attributes, "sv", false);
		if (!setValueEvent && "onchange".equals(type)) {
			this.filterSuggestions();
			//TODO:this is not performant,best do delta

			if (this.suggestionsFiltered.hasView())
				this.suggestionsFiltered.removeView(this.jsSelectBoxView);
			this.showSuggestions(BOUNDS_LOWER_DEFAULT, BOUNDS_UPPER_DEFAULT);
		}
	}

	private void flagJsUpdate() {
		if (this.flagPendingJsUpdate == false) {
			this.flagPendingJsUpdate = true;
			this.textEditField.flagFieldExtensionUpdate();

		}
	}

	private void callJs_setSuggestions(StringBuilder pendingJs) {
		Map<String, String> visibleSuggestions = this.getVisibleSuggestions();

		new JsFunction(pendingJs, jsObjectName, "setSuggestions").addParamJson(visibleSuggestions).end();
		// Dont flag, this is for rebuildJs
	}

	private void callJs_updateSuggestions() {
		new JsFunction(updatePendingJs, jsObjectName, "updateSuggestions").end();
		this.flagJsUpdate();
	}

	private void callJs_updateMenu(boolean updateSelection, short startOrEndOfSuggestion) {
		new JsFunction(updatePendingJs, jsObjectName, "updateMenu").addParam(updateSelection).addParam(startOrEndOfSuggestion).end();
		this.flagJsUpdate();
	}

	private void callJs_setRowSelected(int index) {
		new JsFunction(updatePendingJs, jsObjectName, "setRowSelected").addParam(index).end();
		this.flagJsUpdate();
	}

	private void callJs_show(int lower, int upper) {
		new JsFunction(updatePendingJs, this.jsObjectName, "show").addParam(lower).addParam(upper).end();
		this.flagJsUpdate();
	}

	private void callJs_hide() {
		new JsFunction(updatePendingJs, this.jsObjectName, "hide").end();
		this.flagJsUpdate();
	}

	private void callJs_clear() {
		new JsFunction(updatePendingJs, this.jsObjectName, "clear").end();
		this.flagJsUpdate();
	}

	public void callJs_add(int index, String newValue) {
		String fval = formatVal(this.prefix2, newValue);
		new JsFunction(updatePendingJs, this.jsObjectName, "a").addParam(index).addParamQuoted(fval).end();
		this.flagJsUpdate();
	}
	public void callJs_update(int index, String newValue) {
		String fval = formatVal(this.prefix2, newValue);
		new JsFunction(updatePendingJs, this.jsObjectName, "u").addParam(index).addParamQuoted(fval).end();
		this.flagJsUpdate();
	}
	public void callJs_remove(int index, String value) {
		new JsFunction(updatePendingJs, this.jsObjectName, "r").addParam(index).end();
		this.flagJsUpdate();
	}

	public void callJs_sizeChanged(int newSize) {
		new JsFunction(updatePendingJs, this.jsObjectName, "s").addParam(newSize).end();
	}

	protected void showSuggestions(int lower, int upper) {
		if (this.flagShowSuggestions == true)
			return;
		this.flagShowSuggestions = true;

		if (!this.suggestionsFiltered.hasView()) {
			this.jsSelectBoxView.onViewBoundsChanged(suggestionsFiltered, lower, upper);
			this.suggestionsFiltered.addView(this.jsSelectBoxView);
		} else {
			// Already has view
		}

		this.callJs_sizeChanged(this.suggestionsFiltered.size());
		this.callJs_show(lower, upper);
	}

	private void resetSuggestions() {
		if (this.suggestionsFiltered.hasView())
			this.suggestionsFiltered.removeView(this.jsSelectBoxView);
	}

	protected class JsSelectBox extends BasicDataView<String> {
		@Override
		public void onDataInit(DataList<String> dataSyncer) {
			super.onDataInit(dataSyncer);
			FormPortletFieldAutoCompleteExtension.this.callJs_sizeChanged(FormPortletFieldAutoCompleteExtension.this.suggestions2.size());
		}
		@Override
		public void onDataCleared() {
			super.onDataCleared();
			FormPortletFieldAutoCompleteExtension.this.callJs_clear();
		}
		@Override
		public void onDataAdded(int index, String newValue) {
			super.onDataAdded(index, newValue);
			FormPortletFieldAutoCompleteExtension.this.callJs_add(index, newValue);
		}
		@Override
		public void onDataUpdated(int index, String newValue, String oldValue) {
			super.onDataUpdated(index, newValue, oldValue);
			FormPortletFieldAutoCompleteExtension.this.callJs_update(index, newValue);
		}
		@Override
		public void onDataRemoved(int index, String oldValue) {
			super.onDataRemoved(index, oldValue);
			FormPortletFieldAutoCompleteExtension.this.callJs_remove(index, oldValue);
		}
		@Override
		public void onDataRequested(int position, String value) {
			super.onDataRequested(position, value);

			//This is already called no need to call
			//			FormPortletFieldAutoCompleteExtension.this.callJs_update(position, value);
		}
		@Override
		public void onDataSizeChanged(int newSize) {
			FormPortletFieldAutoCompleteExtension.this.flagSizeChanged = true;
			FormPortletFieldAutoCompleteExtension.this.flagJsUpdate();

			//Don't call this here, will fire too many times.
			//FormPortletFieldAutoCompleteExtension.this.callJs_sizeChanged(newSize);
		}

	}

	@Override
	public String getJsObjectName() {
		return jsObjectName;
	}

	@Override
	public int getExtensionIndex() {
		return this.extensionIndex;
	}

	public void clearSuggestions() {
		this.suggestions2.clear();
		this.suggestionsFiltered.clear();
	}
	public List<String> getSuggestions() {
		return this.suggestions2;
	}

	public void putSuggestion(String option) {
		if (option == null)
			return;
		this.suggestions2.add(option);
	}

	public void removeSuggestion(String option) {
		this.suggestions2.remove(option);
	}

	public void addSuggestions(Collection<String> newSuggestions) {
		boolean changed = false;
		for (String i : newSuggestions)
			if (i != null && this.suggestions2.add(i))
				changed = true;
	}
	public void linkSuggestionsToTable(TableList rows, Column idCl, Column vlCl) {
		int vlCol = vlCl.getLocation();
		List<String> added = new ArrayList<String>();
		for (Row row : rows) {
			String suggestion = row.getAt(vlCol, Caster_String.INSTANCE);
			added.add(suggestion);
	}

	}
	public void setSuggestions(Collection<String> newSuggestions) {
		BasicDataList<String> t = new BasicDataList<String>();
		for (String i : newSuggestions)
			if (i != null)
				t.add(i);
		if (OH.ne(this.suggestions2, t)) {
			this.suggestions2 = t;
			this.onSuggestionsChanged();
		}
	}

	protected void onSuggestionsChanged() {
		if (this.flagSuggestionsChanged == true)
			return;
		this.flagSuggestionsChanged = true;
		Map<String, String> visibleSuggestions = this.getVisibleSuggestions();
		this.flagJsUpdate();
	}

	//	/* Returns a list of suggestions, key is what user sees, value is what gets replaced with */
	//	private Map<String, String> getVisibleSuggestions() {
	//	}
	protected void filterSuggestions() {
		String val = this.textEditField.getValue();

		this.suggestionsFiltered.clear();
		if (!this.isShowOptionsImmediately() && (val == null || val.length() == 0)) {
			return;
		} else {
			// Step 1 : Sort
			if (this.sorting == SORTING_NONE) {
				suggestionsSorted.clear();
			} else {
				suggestionsSorted.clear();
				suggestionsSorted.addAll(this.suggestions2);
				if (this.sorting == SORTING_ASCENDING)
					suggestionsSorted.sort((Comparator) ComparableComparator.INSTANCE);
				else if (this.sorting == SORTING_DESCENDING)
					suggestionsSorted.sort((Comparator) ReverseComparator.INSTANCE);
	}
			// Step 2 Filter
			BasicDataList<String> tgtSuggestions = this.sorting == SORTING_NONE ? suggestions2 : suggestionsSorted;
			if (this.getPerformSubstringMatching()) {
				for (String option : tgtSuggestions)
					if (SH.isSubstringIgnoreCase(option, val))
						this.suggestionsFiltered.add(option);
			} else {
				for (String option : tgtSuggestions)
					if (SH.startsWithIgnoreCase(option, val))
						this.suggestionsFiltered.add(option);
			}

		}

	}

	public void rebuildJs(StringBuilder pendingJs) {
		String jsFieldObjectName = this.textEditField.getJsObjectName();
		pendingJs.append("var ").append(jsObjectName).append("= new FieldAutocompleteExtension(").append(jsFieldObjectName).append(',').append(extensionIndex).append(");");
		//First lets send all suggestions over to the front end all the time

		if (flagShowSuggestions)
			new JsFunction(pendingJs, jsObjectName, "show").end();

		//Set flags to false
		this.flagSuggestionsChanged = false;
		this.flagShowSuggestions = false;
	}

	@Override
	public void updateJs(StringBuilder pendingJs) {
		if (this.flagPendingJsUpdate) {
			if (this.flagSizeChanged)
				this.callJs_sizeChanged(this.suggestionsFiltered.size());
			//			SH.p(updatePendingJs.toString());

			pendingJs.append(updatePendingJs);
			SH.clear(updatePendingJs);
			this.flagPendingJsUpdate = false;
		}
		this.flagSuggestionsChanged = false;
		this.flagShowSuggestions = false;
	}

	/* Returns a list of suggestions, key is what user sees, value is what gets replaced with */
	private Map<String, String> getVisibleSuggestions() {
		filterSuggestions();
		String val = this.textEditField.getValue();
		if (!this.isShowOptionsImmediately() && (val == null || val.length() == 0)) {
			return null;
		} else {
			Map<String, String> acList = new LinkedHashMap<String, String>();
			String prefix;
			if (delimiter != null && val.indexOf(delimiter) != -1) {
				prefix = SH.beforeLast(val, delimiter) + delimiter;
				val = SH.afterLast(val, delimiter);
			} else
				prefix = null;
			this.prefix2 = prefix;
			int count = 0;
			for (String option : this.suggestionsFiltered) {
						put(acList, prefix, option);
				//				if (++count == this.limit)
				//					break;
					}
			return acList;
					}
			}
	static private String formatVal(String prefix, String val) {
		String val2 = val == null ? "<i>&lt;null&gt;</i>" : ("".equals(val) ? "<i>&lt;Empty String&gt;</i>" : WebHelper.escapeHtml(val));
		if (prefix != null)
			val = prefix + val;
		return val2;

	}
	static private void put(Map<String, String> r, String prefix, String val) {
		String val2 = val == null ? "<i>&lt;null&gt;</i>" : ("".equals(val) ? "<i>&lt;Empty String&gt;</i>" : WebHelper.escapeHtml(val));
		if (prefix != null)
			val = prefix + val;
		r.put(val2, val);
	}
	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPostfix() {
		return postfix;
	}

	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}

	public boolean isShowOptionsImmediately() {
		return showOptionsImmediately;
	}

	public void setShowOptionsImmediately(boolean showOptionsImmediately) {
		this.showOptionsImmediately = showOptionsImmediately;
	}

	public boolean getPerformSubstringMatching() {
		return performSubstringMatching;
	}

	public void setPerformSubstringMatching(boolean performSubstringMatching) {
		this.performSubstringMatching = performSubstringMatching;
	}

	public short getSorting() {
		return sorting;
	}

	public void setSorting(short sorting) {
		if (sorting == this.sorting)
			return;
		this.sorting = sorting;
		if (this.suggestions2.size() > 1)
			this.onSuggestionsChanged();
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	@Override
	public boolean hasUpdate() {
		return this.flagPendingJsUpdate;
	}
}
