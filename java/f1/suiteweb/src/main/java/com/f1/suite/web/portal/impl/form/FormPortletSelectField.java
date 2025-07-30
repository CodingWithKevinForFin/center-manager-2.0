package com.f1.suite.web.portal.impl.form;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.suite.web.JsFunction;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.ComparableComparator;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.ReverseComparator;

public class FormPortletSelectField<KEY> extends FormPortletField<KEY> {

	public static final int DEFAULT_WIDTH = -1;
	public static final String JSNAME = "SelectField";
	private IndexedList<String, Option<KEY>> options = new BasicIndexedList<String, Option<KEY>>();
	private Map<KEY, Option<KEY>> optionsByKey = new HashMap<KEY, Option<KEY>>();
	private boolean disabled;
	private int nextId = 1;

	private boolean hasButton = false;
	private boolean rightAlign = false;

	public FormPortletSelectField(Class<KEY> type, String title) {
		super(type, title);
	}

	public FormPortletSelectField<KEY> addOption(KEY key, String name) {
		this.addOption(key, name, null);
		return this;
	}
	public FormPortletSelectField<KEY> addOptionNoFire(KEY key, String name) {
		this.addOption(key, name, null, false);
		return this;
	}
	public FormPortletSelectField<KEY> addOptions(Map<KEY, String> values) {
		for (Entry<KEY, String> e : values.entrySet())
			addOption(e.getKey(), e.getValue());
		return this;
	}
	public FormPortletSelectField<KEY> addOptionsNoThrow(Map<KEY, String> values) {
		for (Entry<KEY, String> e : values.entrySet())
			addOptionNoThrow(e.getKey(), e.getValue());
		return this;
	}
	public Option<KEY> addOption(KEY key, String name, String style) {
		return addOption(key, name, style, true);
	}
	public Option<KEY> addOption(KEY key, String name, String style, boolean fire) {
		String id = generateNextOptionId();
		Option<KEY> option = new Option<KEY>(id, key, name, style);
		CH.putOrThrow(optionsByKey, key, option);
		options.add(id, option);
		if (options.getSize() == 1) {
			if (fire) {
				setValue(key);
			} else {
				setValueNoFire(key);
			}
			setDefaultValue(key);
		}
		flagChange(MASK_OPTIONS);
		return option;
	}
	public FormPortletSelectField<KEY> addOptionNoThrow(KEY id, String name) {
		return this.addOptionNoThrow(id, name, null);
	}
	public FormPortletSelectField<KEY> addOptionNoThrow(KEY key, String name, String style) {
		String id = generateNextOptionId();
		Option<KEY> option = new Option<KEY>(id, key, name, style);
		if (optionsByKey.containsKey(key))
			return this;
		CH.putOrThrow(optionsByKey, key, option);
		options.add(id, option);
		if (options.getSize() == 1) {
			setValue(key);
			setDefaultValue(key);
		}
		flagChange(MASK_OPTIONS);
		return this;
	}
	private String generateNextOptionId() {
		return SH.toString(nextId++);
	}

	public FormPortletSelectField<KEY> removeOption(KEY id) {
		Option<KEY> r = optionsByKey.remove(id);
		options.remove(r.getId());
		flagChange(MASK_OPTIONS);
		return this;
	}
	public FormPortletSelectField<KEY> removeOptionNoThrow(KEY id) {
		Option<KEY> r = optionsByKey.remove(id);
		if (r == null)
			return this;
		if (OH.eq(getValue(), id))
			setValue(getDefaultValue());
		options.remove(r.getId());
		flagChange(MASK_OPTIONS);
		return this;
	}

	public Option<KEY> getOption(KEY id) {
		return optionsByKey.get(id);
	}
	@Override
	public String getjsClassName() {
		return JSNAME;
	}

	@Override
	public void updateJs(StringBuilder pendingJs) {
		if (hasChanged(MASK_REBUILD))
			new JsFunction(pendingJs, jsObjectName, "setMulti").addParam(false).end();
		if (hasChanged(MASK_CONFIG))
			new JsFunction(pendingJs, jsObjectName, "init").addParam(hasButton).end();
		if (hasChanged(MASK_OPTIONS)) {
			new JsFunction(pendingJs, jsObjectName, "clear").end();
			for (Map.Entry<String, Option<KEY>> e : this.options) {
				final Option<KEY> option = e.getValue();
				new JsFunction(pendingJs, jsObjectName, "addOption").addParamQuoted(option.getId()).addParamQuoted(option.getName()).addParamQuoted(option.getStyle())
						.addParam(false).end();
			}
		}
		super.updateJs(pendingJs);
		if (hasChanged(MASK_STYLE)) {
			new JsFunction(pendingJs, jsObjectName, "setTextAlign").addParamQuoted(rightAlign ? "rtl" : "ltr").end();
		}
	}

	public static class Option<T> implements Comparable<Option<T>> {
		public String name, id, style;
		private Object correlationData;
		private T key;

		public Option(String id, T key, String name, String style) {
			this.id = id;
			this.key = key;
			this.name = name;
			this.style = style;
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id;
		}

		public String getStyle() {
			return style;
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

	@Override
	public FormPortletSelectField<KEY> setValueNoFire(KEY key) {
		if (!optionsByKey.containsKey(key))
			throw new RuntimeException("unknown key: " + key);
		super.setValueNoFire(key);
		return this;
	}
	@Override
	public FormPortletSelectField<KEY> setValue(KEY key) {
		if (!optionsByKey.containsKey(key)) {
			if (!optionsByKey.isEmpty() || key != null)
				throw new RuntimeException("unknown key: " + key + " (options are: " + optionsByKey.keySet() + ")");
		}
		super.setValue(key);
		return this;
	}

	public Iterable<Option<KEY>> getOptions() {
		return this.options.values();
	}
	public int getOptionsCount() {
		return this.options.getSize();
	}

	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {
		final String value = CH.getOrThrow(attributes, "value");
		if (SH.is(value))
			setValueNoFire(options.get(value).getKey());
		return true;
	}

	@Override
	public String getJsValue() {
		if (optionsByKey.isEmpty())
			return null;
		return optionsByKey.get(getValue()).getId();
	}

	@Override
	public boolean setValueNoThrow(KEY key) {
		if (!optionsByKey.containsKey(key))
			return false;
		setValue(key);
		return true;
	}
	public FormPortletSelectField<KEY> setValueNoThrow2(KEY key) {
		if (!optionsByKey.containsKey(key))
			return this;
		setValue(key);
		return this;
	}

	@Override
	public FormPortletSelectField<KEY> setId(String id) {
		super.setId(id);
		return this;
	}

	public void sortOptionsByName() {
		sortOptions((Comparator<Option<KEY>>) ComparableComparator.INSTANCE);
	}

	public void sortOptionsByNameDesc() {
		sortOptions((Comparator<Option<KEY>>) ReverseComparator.INSTANCE);
	}
	public void sortOptions(Comparator<Option<KEY>> c) {
		this.options.sortByValues(c);
		this.flagConfigChanged();
	}

	public FormPortletSelectField<KEY> clearOptions() {
		if (optionsByKey.isEmpty())
			return this;
		optionsByKey.clear();
		options.clear();
		setDefaultValue(null);
		flagChange(MASK_OPTIONS);
		this.setValue(getDefaultValue());
		return this;

	}

	public FormPortletSelectField setDisabled(Boolean b) {
		if (b == null || this.disabled == b)
			return this;
		this.disabled = b;
		flagConfigChanged();
		return this;
	}
	public boolean isDisabled() {
		return disabled;
	}

	@Override
	public FormPortletSelectField<KEY> setCorrelationData(Object correlationData) {
		super.setCorrelationData(correlationData);
		return this;
	}

	public boolean containsOption(KEY key) {
		return this.optionsByKey.containsKey(key);

	}
	public Option<KEY> getOptionNoThrow(KEY key) {
		return this.optionsByKey.get(key);
	}

	public void setOptionName(KEY key, String name) {
		this.getOption(key).name = name;
		flagChange(MASK_OPTIONS);
	}

	@Override
	public FormPortletSelectField<KEY> setTitleIsClickable(boolean tic) {
		super.setTitleIsClickable(tic);
		return this;
	}

	@Override
	public FormPortletSelectField<KEY> setName(String name) {
		super.setName(name);
		return this;
	}

	public FormPortletSelectField<KEY> addDefaultOption() {
		this.addOption(null, "Inherited");
		return this;
	}

	public FormPortletSelectField<KEY> setHasButton(boolean hasButton) {
		if (hasButton == this.hasButton)
			return this;
		this.hasButton = hasButton;
		flagConfigChanged();
		return this;
	}
	public Option<KEY> getValueOption() {
		return this.getOptionNoThrow(getValue());
	}

	public void setIsRightAlign(boolean rightAlign) {
		if (this.rightAlign == rightAlign)
			return;
		this.rightAlign = rightAlign;
		flagStyleChanged();
	}
	public boolean getIsRightAlign() {
		return this.rightAlign;
	}

}
