package com.f1.suite.web.portal.impl.form;

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
import com.f1.utils.SH;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.ComparableComparator;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.ReverseComparator;

public class FormPortletMultiSelectField<KEY> extends FormPortletField<Set<KEY>> {

	private IndexedList<String, Option<KEY>> options = new BasicIndexedList<String, Option<KEY>>();
	private Map<KEY, Option<KEY>> optionsByKey = new HashMap<KEY, Option<KEY>>();
	private int nextId = 1;

	public LinkedHashSet<KEY> selected = new LinkedHashSet<KEY>();
	private boolean ensureSelectedVisibleFlag;
	private final Class<KEY> optionsType;
	//	private boolean rightAlign = false; // not used
	private boolean useCustomScrollbar = false;

	public FormPortletMultiSelectField(Class<KEY> type, String title) {
		super((Class) Set.class, title);
		this.optionsType = type;
	}

	/**
	 * clones option
	 */
	public FormPortletMultiSelectField<KEY> addOption(int position, Option<KEY> option) {
		return this.addOption(position, option.getKey(), option.getName(), option.getStyle());
	}
	public FormPortletMultiSelectField<KEY> addOption(Option<KEY> option) {
		return this.addOption(option.getKey(), option.getName(), option.getStyle());
	}
	public FormPortletMultiSelectField<KEY> addOption(KEY id, String name) {
		return this.addOption(options.getSize(), id, name, null);
	}
	public FormPortletMultiSelectField<KEY> addOption(KEY id, String name, String style) {
		return this.addOption(options.getSize(), id, name, style);
	}
	public FormPortletMultiSelectField<KEY> addOption(int position, KEY id, String name) {
		return this.addOption(position, id, name, null);
	}
	public FormPortletMultiSelectField<KEY> addOption(int position, KEY key, String name, String style) {
		String id = generateNextOptionId();
		Option<KEY> option = new Option<KEY>(id, key, name, style);
		CH.putOrThrow(optionsByKey, key, option);
		options.add(id, option, position);
		flagChange(MASK_OPTIONS);
		return this;
	}
	public FormPortletMultiSelectField<KEY> addOptions(Map<KEY, String> m) {
		for (Entry<KEY, String> e : m.entrySet()) {
			addOption(e.getKey(), e.getValue());
		}
		return this;
	}

	public boolean containsOption(KEY key) {
		return this.optionsByKey.containsKey(key);

	}
	public Option<KEY> getOptionNoThrow(KEY key) {
		return this.optionsByKey.get(key);
	}
	private String generateNextOptionId() {
		return SH.toString(nextId++);
	}

	public void removeOptionByKey(String key) {
		options.remove(optionsByKey.get(key).id);
		optionsByKey.remove(key);
		selected.remove(key);
		flagChange((short) (MASK_OPTIONS | MASK_VALUE));
	}
	public Option<KEY> getOption(String id) {
		return options.get(id);
	}
	public Option<KEY> getOptionByKey(KEY key) {
		return optionsByKey.get(key);
	}
	public Option<KEY> getOptionAt(int i) {
		return this.options.getAt(i);
	}
	@Override
	public String getjsClassName() {
		return "SelectField";
	}

	public void setUseCustomScrollbar(boolean b) {
		if (this.useCustomScrollbar == b)
			return;
		this.useCustomScrollbar = b;
		flagStyleChanged();
	}

	public boolean getUseCustomScrollbar() {
		return this.useCustomScrollbar;
	}

	@Override
	public void updateJs(StringBuilder pendingJs) {
		if (hasChanged(MASK_REBUILD))
			new JsFunction(pendingJs, jsObjectName, "setMulti").addParam(true).end();
		if (hasChanged(MASK_STYLE)) {
			if (useCustomScrollbar) {
				String gripColor = super.getForm().getStyleManager().getFormButtonBackgroundColor();
				String trackColor = super.getForm().getStyleManager().getFormButtonFontColor();
				new JsFunction(pendingJs, jsObjectName, "setAttr").end();
				new JsFunction(pendingJs, jsObjectName, "setScrollbarGripColor").addParamQuoted(gripColor).end();
				new JsFunction(pendingJs, jsObjectName, "setScrollbarTrackColor").addParamQuoted(trackColor).end();
				new JsFunction(pendingJs, jsObjectName, "setScrollbarRadius").end();
			}
		}

		if (hasChanged(MASK_OPTIONS)) {
			new JsFunction(pendingJs, jsObjectName, "clear").end();
			String fs = getFieldStyles(new StringBuilder()).toString();
			for (Map.Entry<String, Option<KEY>> e : this.options) {
				final Option<KEY> option = e.getValue();
				String style = option.getStyle();
				if (SH.is(fs)) {
					if (SH.isnt(style))
						style = fs;
					else
						style += "|" + fs;
				}
				new JsFunction(pendingJs, jsObjectName, "addOption").addParamQuoted(option.getId()).addParamQuoted(option.getName()).addParamQuoted(style)
						.addParam(getValue().contains(option.getKey())).end();
			}
		}
		super.updateJs(pendingJs);
		if (hasChanged(MASK_CUSTOM)) {
			if (this.ensureSelectedVisibleFlag) {
				this.ensureSelectedVisibleFlag = false;
				new JsFunction(pendingJs, jsObjectName, "ensureSelectedVisible").end();
			}
		}
	}

	public static class Option<T> implements Comparable<Option<T>> {
		public String name, id, style;
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

		public void setStyle(String style) {
			this.style = style;
		}
		public T getKey() {
			return key;
		}

		@Override
		public int compareTo(Option<T> o) {
			return SH.COMPARATOR_CASEINSENSITIVE_STRING.compare(getName(), o.getName());
		}
	}

	@Override
	public FormPortletMultiSelectField<KEY> setValue(Set<KEY> keys) {
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
	public LinkedHashSet<KEY> getValue() {
		return selected;
	}
	public Set<KEY> getValues() {
		return selected;
	}
	public LinkedHashSet<KEY> getUnselected() {
		LinkedHashSet<KEY> output = new LinkedHashSet<KEY>();
		KEY k;
		for (int i = 0; i < this.options.getSize(); i++) {
			k = getOptionAt(i).getKey();
			if (!this.selected.contains(k)) {
				output.add(k);
			}
		}
		return output;
	}

	public KEY getValueKey() {
		int size = selected.size();
		if (size == 0)
			return null;
		if (size == 1)
			return CH.first(selected);
		throw new RuntimeException("multiple keys, use getKeys()");
	}

	public Set<KEY> getSelectedValueKeys() {
		return selected;
	}
	public Set<KEY> getOptionKeys() {
		return this.optionsByKey.keySet();
	}

	public Iterable<Option<KEY>> getOptions() {
		return this.options.values();
	}
	public FormPortletMultiSelectField<KEY> setValueByKey(KEY key) {
		Option<KEY> option = this.optionsByKey.get(key);
		if (option == null)
			throw new RuntimeException("unknown key: " + key);
		selected.clear();
		selected.add(key);
		return this;
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
	public boolean setValueNoThrow(Set<KEY> keys) {
		if (keys == null)
			keys = Collections.EMPTY_SET;
		selected.clear();

		HashSet<KEY> keys2 = new HashSet<KEY>(keys.size());

		boolean r = false;
		for (KEY key : keys)
			if (optionsByKey.containsKey(key)) {
				r = true;
				keys2.add(key);
			}
		selected.addAll(keys2);
		super.setValue(keys2);
		return r;
	}

	public void clear() {
		this.selected.clear();
		options.clear();
		optionsByKey.clear();
		flagConfigChanged();
	}
	public void setOptions(BasicIndexedList<String, Option<KEY>> selectedColumns) {
		this.options = selectedColumns;
	}

	public void clearNoFire() {
		this.selected.clear();
		options.clear();
		optionsByKey.clear();
	}

	@Override
	public FormPortletMultiSelectField<KEY> setName(String name) {
		super.setName(name);
		return this;
	}

	//use  sertHeight(...) instead
	@Deprecated
	public FormPortletMultiSelectField<KEY> setSize(int i) {
		super.setHeight(i);
		return this;
	}

	@Override
	public FormPortletMultiSelectField<KEY> setWidth(int width) {
		super.setWidth(width);
		return this;
	}
	@Override
	public FormPortletMultiSelectField<KEY> setHeight(int height) {
		super.setHeight(height);
		return this;
	}
	public void moveSelectedTo(Integer position) {
		if (position == null)
			return;
		int selectedSize = this.selected.size();
		int optionsSize = this.options.getSize();
		// Check that options are selected and not all of them are selected
		if (selectedSize == 0 || selectedSize == optionsSize) {
			return;
		}

		// If position is less than 0 or greater than the number of options
		if (position < 0 || position >= optionsSize)
			return;

		if (position > optionsSize - selectedSize) {
			position = optionsSize - selectedSize;
		}

		Set<KEY> selectedKeys = new LinkedHashSet<KEY>(getValue());
		IndexedList<KEY, Option<KEY>> reordered = new BasicIndexedList<KEY, Option<KEY>>();

		Option<KEY> a = null;
		//Add options before 
		int i;
		for (i = 0; i < position;) {
			a = this.options.getAt(i);
			if (this.selected.contains(a.getKey()))
				continue;
			reordered.add(a.getKey(), a);
			i++;
		}
		//Add selected options
		for (KEY key : selectedKeys) {
			reordered.add(key, this.getOptionByKey(key));
		}
		//Add remaining
		for (; i < optionsSize; i++) {
			a = this.options.getAt(i);
			if (this.selected.contains(a.getKey()))
				continue;
			reordered.add(a.getKey(), a);
			i++;
		}
		clear();
		for (i = 0; i < reordered.getSize(); i++) {
			addOption(reordered.getKeyAt(i), reordered.getAt(i).getName());
		}
		setValue(selectedKeys);

	}
	public void moveSelectedUp() {
		int selectedSize = this.selected.size();
		int optionsSize = this.options.getSize();
		if (selectedSize == 0 || selectedSize == optionsSize) {
			return;
		}
		Set<KEY> selectedKeys = new HashSet<KEY>(getValue());
		IndexedList<KEY, Option<KEY>> reordered = new BasicIndexedList<KEY, Option<KEY>>();
		Option<KEY> a = null;
		Option<KEY> b = null;
		boolean flagOrderChanged = false;
		for (int i = 0; i < optionsSize - 1; i++) {
			if (!flagOrderChanged) {
				a = this.options.getAt(i);
			}
			b = this.options.getAt(i + 1);
			if (!this.selected.contains(a.getKey()) && this.selected.contains(b.getKey())) {
				reordered.add(b.getKey(), b);
				flagOrderChanged = true;
			} else {
				reordered.add(a.getKey(), a);
				flagOrderChanged = false;
			}
			if (i + 1 == optionsSize - 1) {
				if (flagOrderChanged) {
					reordered.add(a.getKey(), a);
				} else {
					reordered.add(b.getKey(), b);
				}
			}
		}
		clear();
		for (int i = 0; i < reordered.getSize(); i++) {
			addOption(reordered.getKeyAt(i), reordered.getAt(i).getName());
		}
		setValue(selectedKeys);
	}
	public void moveSelectedDown() {
		int selectedSize = this.selected.size();
		int optionsSize = this.options.getSize();
		if (selectedSize == 0 || selectedSize == optionsSize) {
			return;
		}
		Set<KEY> selectedKeys = new HashSet<KEY>(getValue());
		IndexedList<KEY, Option<KEY>> reordered = new BasicIndexedList<KEY, Option<KEY>>();
		Option<KEY> a = null;
		Option<KEY> b = null;
		boolean flagOrderChanged = false;
		for (int i = optionsSize - 1; i > 0; i--) {
			if (!flagOrderChanged) {
				a = this.options.getAt(i);
			}
			b = this.options.getAt(i - 1);
			if (!this.selected.contains(a.getKey()) && this.selected.contains(b.getKey())) {
				reordered.add(b.getKey(), b);
				flagOrderChanged = true;
			} else {
				reordered.add(a.getKey(), a);
				flagOrderChanged = false;
			}
			if (i - 1 == 0) {
				if (flagOrderChanged) {
					reordered.add(a.getKey(), a);
				} else {
					reordered.add(b.getKey(), b);
				}
			}
		}
		clear();
		for (int i = optionsSize - 1; i >= 0; i--) {
			addOption(reordered.getKeyAt(i), reordered.getAt(i).getName());
		}
		setValue(selectedKeys);
	}
	public void moveSelectedTop() {
		int selectedSize = this.selected.size();
		int optionsSize = this.options.getSize();
		if (selectedSize == 0 || selectedSize == optionsSize) {
			return;
		}
		Set<KEY> selectedKeys = new HashSet<KEY>(getValue());
		IndexedList<KEY, Option<KEY>> reordered = new BasicIndexedList<KEY, Option<KEY>>();
		Option<KEY> o;
		for (int i = 0; i < optionsSize; i++) {
			o = this.options.getAt(i);
			if (this.selected.contains(o.getKey())) {
				reordered.add(o.getKey(), o);
			}
		}
		for (int i = 0; i < optionsSize; i++) {
			o = this.options.getAt(i);
			if (!this.selected.contains(o.getKey())) {
				reordered.add(o.getKey(), o);
			}
		}
		clear();
		for (int i = 0; i < optionsSize; i++) {
			addOption(reordered.getKeyAt(i), reordered.getAt(i).getName());
		}
		setValue(selectedKeys);
	}
	public void moveSelectedBottom() {
		int selectedSize = this.selected.size();
		int optionsSize = this.options.getSize();
		if (selectedSize == 0 || selectedSize == optionsSize) {
			return;
		}
		Set<KEY> selectedKeys = new HashSet<KEY>(getValue());
		IndexedList<KEY, Option<KEY>> reordered = new BasicIndexedList<KEY, Option<KEY>>();
		Option<KEY> o;
		for (int i = 0; i < optionsSize; i++) {
			o = this.options.getAt(i);
			if (!this.selected.contains(o.getKey())) {
				reordered.add(o.getKey(), o);
			}
		}
		for (int i = 0; i < optionsSize; i++) {
			o = this.options.getAt(i);
			if (this.selected.contains(o.getKey())) {
				reordered.add(o.getKey(), o);
			}
		}
		clear();
		for (int i = 0; i < optionsSize; i++) {
			addOption(reordered.getKeyAt(i), reordered.getAt(i).getName());
		}
		setValue(selectedKeys);
	}
	public int getSize() {
		return this.options.getSize();
	}

	public void ensureSelectedVisible() {
		this.ensureSelectedVisibleFlag = true;
		flagChange(MASK_CUSTOM);
	}
	public Class<KEY> getOptionsType() {
		return this.optionsType;
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

	public void clearOptions() {
		if (options.getSize() == 0)
			return;
		options.clear();
		optionsByKey.clear();
		selected.clear();
		flagConfigChanged();
	}

}
