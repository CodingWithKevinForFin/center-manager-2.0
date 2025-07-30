package com.f1.suite.web.portal.impl.form;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.suite.web.JsFunction;
import com.f1.utils.CH;
import com.f1.utils.ColorHelper;
import com.f1.utils.SH;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.ComparableComparator;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.ReverseComparator;

public class FormPortletMultiCheckboxField<KEY> extends FormPortletField<Set<KEY>> {
	private static final String JSNAME = "MultiCheckboxField";

	private IndexedList<String, Option<KEY>> options = new BasicIndexedList<String, Option<KEY>>();
	private Map<KEY, Option<KEY>> optionsByKey = new HashMap<KEY, Option<KEY>>();
	private LinkedHashSet<KEY> selected = new LinkedHashSet<KEY>();
	private int nextId = 1;
	private final Class<KEY> optionsType;;

	public FormPortletMultiCheckboxField(Class type, String title) {
		super((Class) Set.class, title);
		this.optionsType = type;
	}

	@Override
	public String getjsClassName() {
		return JSNAME;
	}

	public LinkedHashSet<KEY> getValue() {
		return selected;
	}

	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {
		final String value = CH.getOrThrow(attributes, "value");
		this.selected.clear();
		if (SH.is(value)) {
			for (String key : SH.split(',', value)) {
				Option<KEY> option = this.options.getNoThrow(key);
				if (option != null)
					this.selected.add(option.getKey());
			}
		}
		return true;
	}

	@Override
	public String getJsValue() {
		if (selected.isEmpty())
			return "";
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (KEY sel : selected) {
			Option<KEY> option = optionsByKey.get(sel);
			if (!first)
				sb.append(',');
			else
				first = false;
			if (option != null)
				sb.append(option.getId());
		}
		return sb.toString();
	}
	@Override
	public void updateJs(StringBuilder pendingJs) {
		if (hasChanged(MASK_OPTIONS)) {
			new JsFunction(pendingJs, jsObjectName, "clear").end();
			for (Map.Entry<String, Option<KEY>> e : this.options) {
				final Option<KEY> option = e.getValue();
				new JsFunction(pendingJs, jsObjectName, "addOption").addParamQuoted(option.getId()).addParamQuoted(option.getName()).addParam(getValue().contains(option.getKey()))
						.end();
			}
		}
		if (hasChanged(MASK_STYLE)) {
			if (this.getBgColor() != null) {
				Color origColor = ColorHelper.parseColor(this.getBgColor());
				int contrastColor = ColorHelper.colorDodgeRgb(origColor.getRGB());
				String rgbaString = ColorHelper.toRgbaString(contrastColor);
				new JsFunction(pendingJs, jsObjectName, "setClearElementColor").addParamQuoted(rgbaString).end();
			} else
				new JsFunction(pendingJs, jsObjectName, "setClearElementColor").addParamQuoted(null).end();
		}
		super.updateJs(pendingJs);
	}
	public void clear() {
		if (!selected.isEmpty()) {
			this.selected.clear();
			flagChange(MASK_VALUE);
		}
		options.clear();
		optionsByKey.clear();
		flagChange(MASK_OPTIONS);
	}

	public void clearSelected() {
		if (this.selected.isEmpty())
			return;
		this.selected.clear();
		flagChange(MASK_VALUE);
	}

	public Option<KEY> addOption3(String id, KEY key, String name) {
		int position = options.getSize();
		Option<KEY> option = new Option<KEY>(id, key, name);
		CH.putOrThrow(optionsByKey, key, option);
		options.add(id, option, position);
		flagChange(MASK_OPTIONS);
		return option;
	}

	public Option<KEY> addOption2(KEY key, String name) {
		int position = options.getSize();
		String id = generateNextOptionId();
		Option<KEY> option = new Option<KEY>(id, key, name);
		CH.putOrThrow(optionsByKey, key, option);
		options.add(id, option, position);
		flagChange(MASK_OPTIONS);
		return option;
	}

	public FormPortletMultiCheckboxField<KEY> addOption(KEY key, String name) {
		addOption2(key, name);
		return this;
	}

	public FormPortletMultiCheckboxField<KEY> addOptions(Map<KEY, String> values) {
		for (Entry<KEY, String> e : values.entrySet())
			addOption(e.getKey(), e.getValue());
		return this;
	}

	@Override
	public FormPortletMultiCheckboxField<KEY> setValue(Set<KEY> keys) {
		if (keys == null)
			keys = Collections.EMPTY_SET;
		selected.clear();
		for (KEY key : keys)
			if (!optionsByKey.containsKey(key))
				throw new RuntimeException("unknown key: " + key);
		selected.clear();
		selected.addAll(keys);
		super.setValue(keys);
		return this;
	}

	@Override
	public boolean setValueNoThrow(Set<KEY> keys) {
		if (keys == null)
			keys = Collections.EMPTY_SET;
		selected.clear();

		HashSet<KEY> keys2 = new HashSet<KEY>(keys.size());

		boolean r = false;
		for (KEY key : keys)
			if (optionsByKey.containsKey(key)) {
				keys2.add(key);
				r = true;
			}
		selected.addAll(keys2);
		super.setValue(keys2);
		return r;
	}
	public Set<KEY> getUnselected() {
		Set<KEY> output = new LinkedHashSet<KEY>();
		KEY k;
		for (Option<KEY> op : getOptions())
			if (!this.selected.contains(op.key))
				output.add(op.key);
		return output;
	}
	public Set<KEY> getSelectedValueKeys() {
		return selected;
	}
	public void sortOptionsByName() {
		sortOptions((Comparator<Option<KEY>>) ComparableComparator.INSTANCE);
	}
	public void sortOptionsByNameDesc() {
		sortOptions((Comparator<Option<KEY>>) ReverseComparator.INSTANCE);
	}
	private void sortOptions(Comparator<Option<KEY>> c) {
		this.options.sortByValues(c);
		this.flagConfigChanged();
	}

	public Iterable<Option<KEY>> getOptions() {
		return this.options.values();
	}

	public Collection<Option> getSelectedOptions() {
		HashSet<Option> selectedOptions = new HashSet<FormPortletMultiCheckboxField.Option>();
		for (KEY key : this.selected)
			selectedOptions.add(getOption(key));
		return selectedOptions;
	}

	public Class<KEY> getOptionsType() {
		return this.optionsType;
	}
	private String generateNextOptionId() {
		return SH.toString(nextId++);
	}

	public Option<KEY> getOption(KEY id) {
		return optionsByKey.get(id);
	}
	public FormPortletMultiCheckboxField<KEY> removeOptionNoThrow(KEY id) {
		Option<KEY> r = optionsByKey.remove(id);
		if (r == null)
			return this;
		options.remove(r.getId());
		if (selected.remove(id))
			flagChange(MASK_VALUE);
		flagConfigChanged();
		return this;
	}

	public FormPortletMultiCheckboxField<KEY> removeOption(KEY id) {
		Option<KEY> r = CH.removeOrThrow(optionsByKey, id);
		options.remove(r.getId());
		if (selected.remove(id))
			flagChange(MASK_VALUE);
		flagConfigChanged();
		return this;
	}

	public static class Option<T> implements Comparable<Option<T>> {
		public String name, id;
		private Object correlationData;
		private T key;

		public Option(String id, T key, String name) {
			this.id = id;
			this.key = key;
			this.name = name;
			this.correlationData = correlationData;
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id;
		}

		public T getKey() {
			return key;
		}

		public String toString() {
			return name + "=" + key;
		}

		@Override
		public int compareTo(Option<T> o) {
			return SH.COMPARATOR_CASEINSENSITIVE_STRING.compare(getName(), o.getName());
		}

		public Object getCorrelationData() {
			return correlationData;
		}

		public Option<T> setCorrelationData(Object correlationData) {
			this.correlationData = correlationData;
			return this;
		}
	}

}
