/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.structs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.f1.utils.IterableIterator;
import com.f1.utils.MapWrapper;
import com.f1.utils.OH;

public abstract class BasicMultiMap<KEY, VAL, COL extends Collection<VAL>> extends MapWrapper<KEY, COL> implements MultiMap<KEY, VAL, COL> {

	private static final Object[] ARGS = new Object[] { 1 };// default size
	// of
	// collections
	//	private ObjectGeneratorForClass<COL> generator;

	public BasicMultiMap(Map<KEY, COL> inner) {
		setInnerMap(inner);
	}

	public BasicMultiMap() {
		this(new HashMap<KEY, COL>());
	}

	@Override
	public void putMulti(Map<KEY, VAL> map) {
		for (Map.Entry<KEY, VAL> entry : map.entrySet())
			putMulti(entry.getKey(), entry.getValue());
	}
	@Override
	public void putAllMulti(KEY key, Collection<VAL> vals) {
		COL r = get(key);
		if (r == null)
			put(key, r = newCollection());// TODO:donot_use_nondefault_constructor,make_specialized_factory!
		r.addAll(vals);
	}

	@Override
	public COL putMulti(KEY key, VAL val) {
		COL r = get(key);
		if (r == null)
			put(key, r = newCollection());// TODO:donot_use_nondefault_constructor,make_specialized_factory!
		r.add(val);
		return r;
	}

	public COL getOrCreate(KEY key) {
		COL r = get(key);
		if (r == null)
			put(key, r = newCollection());// TODO:donot_use_nondefault_constructor,make_specialized_factory!
		return r;
	}

	abstract protected COL newCollection();

	@Override
	public Iterable<VAL> valuesMulti() {
		return IterableIterator.create(values());
	}

	@Override
	public void putAllMulti(MultiMap<KEY, VAL, COL> val) {
		for (java.util.Map.Entry<KEY, COL> e : val.entrySet())
			putAllMulti(e.getKey(), e.getValue());
	}

	@Override
	public boolean removeMulti(KEY key, VAL val) {
		Iterable<VAL> i = get(key);
		if (i == null)
			return false;
		Iterator<VAL> it = i.iterator();
		boolean r = false;
		while (it.hasNext())
			if (OH.eq(val, it.next())) {
				it.remove();
				r = true;
			}
		return r;
	}

	/**
	 * returns true if the map changed as a result of this call
	 */
	public boolean removeMultiAndKeyIfEmpty(KEY key, VAL val) {
		if (!removeMulti(key, val))
			return false;
		if (get(key).isEmpty())
			remove(key);
		return true;
	}

	@Override
	public VAL getMulti(KEY key) {
		COL c = get(key);
		if (c == null || c.size() == 0)
			return null;
		else if (c instanceof List)
			return ((java.util.List<VAL>) c).get(0);
		else
			return c.iterator().next();
	}

	public static class List<KEY, VAL> extends BasicMultiMap<KEY, VAL, java.util.List<VAL>> {
		public List(Map<KEY, java.util.List<VAL>> map1) {
			super(map1);
		}
		public List() {
			super();
		}

		@Override
		protected java.util.List<VAL> newCollection() {
			return new ArrayList<VAL>(1);
		}
		@Override
		public BasicMultiMap.List<KEY, VAL> deepClone() {
			BasicMultiMap.List<KEY, VAL> r = new BasicMultiMap.List<KEY, VAL>();
			for (Entry<KEY, java.util.List<VAL>> e : getInnerMap().entrySet()) {
				java.util.List<VAL> col2 = newCollection();
				col2.addAll(e.getValue());
				e.setValue(col2);
			}
			return r;
		}
	}

	public boolean containsMulti(KEY k, VAL v) {
		COL c = this.get(k);
		return c != null && c.contains(v);
	}

	public static class Set<KEY, VAL> extends BasicMultiMap<KEY, VAL, java.util.Set<VAL>> {
		@Override
		protected java.util.Set<VAL> newCollection() {
			return new HashSet<VAL>(1);
		}
		@Override
		public boolean removeMulti(KEY key, VAL val) {
			java.util.Set<VAL> i = get(key);
			if (i == null)
				return false;
			return i.remove(val);
		}
		@Override
		public BasicMultiMap.Set<KEY, VAL> deepClone() {
			BasicMultiMap.Set<KEY, VAL> r = new BasicMultiMap.Set<KEY, VAL>();
			for (Entry<KEY, java.util.Set<VAL>> e : getInnerMap().entrySet()) {
				java.util.Set<VAL> col2 = newCollection();
				col2.addAll(e.getValue());
				e.setValue(col2);
			}
			return r;
		}
	}

	public static class IntSet<KEY> extends BasicMultiMap<KEY, Integer, com.f1.utils.structs.IntSet> {
		@Override
		protected com.f1.utils.structs.IntSet newCollection() {
			return new com.f1.utils.structs.IntSet();
		}
		public com.f1.utils.structs.IntSet putMulti(KEY key, int val) {
			com.f1.utils.structs.IntSet r = get(key);
			if (r == null)
				put(key, r = newCollection());
			r.add(val);
			return r;
		}
		public boolean removeMulti(KEY key, int val) {
			com.f1.utils.structs.IntSet i = get(key);
			return i != null && i.remove(val);
		}
		@Override
		public BasicMultiMap.IntSet<KEY> deepClone() {
			BasicMultiMap.IntSet<KEY> r = new BasicMultiMap.IntSet<KEY>();
			for (Entry<KEY, com.f1.utils.structs.IntSet> e : getInnerMap().entrySet()) {
				com.f1.utils.structs.IntSet col2 = newCollection();
				col2.addAll(e.getValue());
				e.setValue(col2);
			}
			return r;
		}
	}

}
