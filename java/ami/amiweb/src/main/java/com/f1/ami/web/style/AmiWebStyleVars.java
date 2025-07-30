package com.f1.ami.web.style;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.web.AmiWebUtils;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.BasicIndexedList;

public class AmiWebStyleVars {

	final private AmiWebStyleImpl owner;
	private BasicIndexedList<String, String> colors = new BasicIndexedList<String, String>();

	public AmiWebStyleVars(AmiWebStyleImpl owner) {
		this.owner = owner;
	}
	public Set<String> getColorKeys() {
		return this.colors.keySet();
	}
	public Iterator<Entry<String, String>> getColorIterator() {
		return this.colors.iterator();
	}
	public String getColor(String key) {
		return this.colors.get(key);
	}
	public String removeColor(String key) {
		String r = this.colors.remove(key);
		if (r != null)
			this.owner.onVarColorRemoved(key);
		return r;
	}
	public String addColor(String key, String color) {
		String old = this.colors.getNoThrow(key);

		if (OH.ne(old, color)) {
			if (old == null) {
				this.colors.add(key, color);
				this.owner.onVarColorAdded(key, color);
			} else if (color == null) {//new color being null means "No Color", we should unregister the variable
				this.removeColor(key);
			} else {
				this.colors.update(key, color);
				this.owner.onVarColorUpdated(key, old, color);
			}
		}
		return old;
	}

	public void arrange(List<String> newOrder) {
		this.colors.sortByKeys(new ColorComparator(newOrder));
	}

	private class ColorComparator implements Comparator<String> {
		List<String> keyOrder;

		public ColorComparator(List<String> keyOrder) {
			this.keyOrder = keyOrder;
		}
		@Override
		public int compare(String o1, String o2) {
			return this.keyOrder.indexOf(o1) - this.keyOrder.indexOf(o2);
		}
	}

	public Map getConfiguration() {
		HashMap<String, Object> r = new HashMap<String, Object>();
		if (CH.isntEmpty(this.colors)) {
			List l = new ArrayList(this.colors.getSize());
			Iterator<Entry<String, String>> iter = this.colors.iterator();
			while (iter.hasNext()) {
				Entry<String, String> i = iter.next();
				l.add(CH.m("name", i.getKey(), "value", i.getValue()));
			}
			AmiWebUtils.putSkipEmpty(r, "colors", l);
		}
		return r;
	}

	public void init(Map map) {
		this.colors.clear();
		List<Map> c = CH.getOr(List.class, map, "colors", null);
		if (CH.isntEmpty(c)) {
			for (Map o : c) {
				String name = CH.getOrThrow(Caster_String.INSTANCE, o, "name");
				String value = CH.getOrThrow(Caster_String.INSTANCE, o, "value");
				this.colors.add(name, value);
			}
		}
	}
	public boolean isColorUsed(String s) {
		return owner.isVarColorUsed(s);
	}
}
