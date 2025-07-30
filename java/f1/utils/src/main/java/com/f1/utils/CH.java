/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.base.Caster;
import com.f1.base.Getter;
import com.f1.base.IterableAndSize;
import com.f1.base.Mapping;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.impl.BasicHasher;
import com.f1.utils.structs.ArrayIterator;
import com.f1.utils.structs.ComparableComparator;
import com.f1.utils.structs.Tuple2;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CH {
	public static final String AVAILABLE_KEYS = "available keys";
	public static final String SUPPLIED_KEY = "supplied key";
	public static final String REQUIRED_KEY = "required key";

	/**
	 * reverses the comparison of a comparator (as passed into the contstructor)
	 */
	public static final Comparator<Comparable> REVERSE_COMPARATOR = new Comparator<Comparable>() {

		@Override
		public int compare(Comparable o1, Comparable o2) {
			return OH.compare(o2, o1);
		}

	};

	/**
	 * A default comparator for comparing {@link Comparable} objects. See {@link OH#compare(Comparable, Comparable)} for details about how nulls are handled
	 */
	public static final Comparator<Comparable> COMPARATOR = new Comparator<Comparable>() {

		@Override
		public int compare(Comparable o1, Comparable o2) {
			return OH.compare(o1, o2);
		}

	};
	public static final Comparator<Map.Entry> VALUE_COMPARATOR = new Comparator<Map.Entry>() {

		@Override
		public int compare(Map.Entry o1, Map.Entry o2) {
			return OH.compare((Comparable) o1.getValue(), (Comparable) o2.getValue());
		}

	};
	public static final Comparator<Map.Entry> KEY_COMPARATOR = new Comparator<Map.Entry>() {

		@Override
		public int compare(Map.Entry o1, Map.Entry o2) {
			return OH.compare((Comparable) o1.getKey(), (Comparable) o2.getKey());
		}

	};

	/**
	 * convenient for quickly building / populating maps. Items are expected to be in key1, value1, key2,value2 ordering. Please note that duplicate keys will cause the later value
	 * to overwrite the prior value
	 * 
	 * @param <K>
	 *            type of key
	 * @param <V>
	 *            type of value
	 * @param in
	 *            sink to be populated
	 * @param items
	 *            list of items(in key,value,key2,value2,... order). Note that the list must be an event size
	 * @return the in param for convenience.
	 */
	static public <K extends Object, V extends Object, M extends Map<K, V>> M m(M in, Object... items) {
		int l = items.length;
		if (l % 2 != 0)
			throw new IndexOutOfBoundsException("odd number of items: " + items.length);
		for (int i = 0; i < l;)
			in.put((K) items[i++], (V) items[i++]);
		return in;
	}
	static public <K extends Object, V extends Object, M extends Map<K, V>> M mSkipNull(M in, Object... items) {
		int l = items.length;
		if (l % 2 != 0)
			throw new IndexOutOfBoundsException("odd number of items: " + items.length);
		for (int i = 0; i < l; i += 2) {
			if (items[i + 1] != null)
				in.put((K) items[i], (V) items[i + 1]);
		}
		return in;
	}

	/**
	 * convenient for quickly building / populating maps from a collection by some property.
	 * 
	 * 
	 * @param <K>
	 *            type of key
	 * @param <V>
	 *            type of value
	 * @param keyGetter
	 *            <V, K> returns the key from V which is used as a key in a map
	 * @param c
	 *            Collection<V>
	 * @return the Map<K,V>
	 */

	static public <K, V> Map<K, V> m(Iterable<V> c, Getter<V, K> keyGetter) {
		if (isEmpty(c))
			return new HashMap<K, V>();

		HashMap<K, V> m = new HashMap<K, V>();
		for (V v : c)
			m.put(keyGetter.get(v), v);

		return m;
	}

	/**
	 * convenient for quickly building / populating maps from a collection by some property.
	 * 
	 * 
	 * @param <K>
	 *            type of key
	 * @param <V>
	 *            type of value
	 * @param <T>
	 *            type of object in collection
	 * @param keyGetter
	 *            <V, K> returns the key from T which is used as a key in a map
	 * @param valuleGetter
	 *            <V, K> returns the value from T which is used as a key in a map
	 * 
	 * @param c
	 *            Collection<V>
	 * @return the Map<K,V>
	 */

	static public <K, V, T> Map<K, V> m(Iterable<T> c, Getter<T, K> keyGetter, Getter<T, V> valuleGetter) {
		if (isEmpty(c))
			return new HashMap<K, V>();

		HashMap<K, V> m = new HashMap<K, V>();
		for (T t : c)
			m.put(keyGetter.get(t), valuleGetter.get(t));

		return m;
	}

	/**
	 * see {@link #m(Map, Object...)} except it does not take a sink, simply returns a newly created and populated {@link HashMap}
	 */
	static public <K extends Object, V extends Object> Map<K, V> m(Object... items) {
		return m(new HashMap<K, V>(items.length / 2), items);
	}
	static public <K extends Object, V extends Object> Map<K, V> mSkipNull(Object... items) {
		return mSkipNull(new HashMap<K, V>(items.length / 2), items);
	}

	/**
	 * populates a list with items, in the order that those items appear
	 * 
	 * @param <T>
	 *            the list to be populated
	 * @param in
	 *            the sink to populate
	 * @param items
	 *            the items to be added.
	 * @return the in param for convenience
	 */
	static public <T extends Object, C extends Collection<T>> C l(C in, T... items) {
		for (int i = 0, l = items.length; i < l; i++)
			in.add(items[i]);
		return in;
	}
	static public <T extends Object, C extends ArrayList<T>> C l(C in, T... items) {
		in.ensureCapacity(in.size() + items.length);
		for (int i = 0, l = items.length; i < l; i++)
			in.add(items[i]);
		return in;
	}

	/**
	 * populates a set with items. note that duplicate items will follow the {@link Set#add(Object)} behaviour
	 * 
	 * @param <T>
	 *            the type of items
	 * @param <S>
	 *            the type of set
	 * @param in
	 *            the params to add(will be added in order of the list)
	 * @param items
	 *            sink to populate
	 * @return the in param for convenience
	 */
	static public <T extends Object, S extends Set<T>> S s(S in, T... items) {
		for (int i = 0, l = items.length; i < l; i++)
			in.add(items[i]);
		return in;
	}

	/**
	 * see {@link #l(List, Object...)} except that it doesnt take a sink. simply returns a newly created and populated {@link ArrayList}
	 */
	static public <T extends Object> List<T> l(T... items) {
		return l(new ArrayList<T>(items.length), items);
	}

	static public <T extends Object> List<T> l(Iterable<T> items) {
		if (items instanceof Collection)
			return new ArrayList<T>((Collection<T>) items);
		return l(new ArrayList<T>(), items);
	}
	static public <T extends Object> List<T> l(IterableAndSize<T> items) {
		return l(new ArrayList<T>(items.size()), items);
	}
	static public <T extends Object> Set<T> s(IterableAndSize<T> items) {
		return s(new HashSet<T>(items.size()), items);
	}

	static public <T extends Object> List<T> l(Iterator<T> items) {
		return l(new ArrayList<T>(), items);
	}
	static public <T extends Object> List<T> l(Collection<T> items) {
		return new ArrayList<T>(items);
	}

	static public <T extends Object> List<T> l(List<T> sink, Iterable<T> items) {
		if (items instanceof Collection)
			sink.addAll((Collection) items);
		else
			for (T item : items)
				sink.add(item);
		return sink;
	}
	static public <T extends Object> Set<T> s(Set<T> sink, Iterable<T> items) {
		if (items instanceof Collection)
			sink.addAll((Collection) items);
		else
			for (T item : items)
				sink.add(item);
		return sink;
	}

	static public <T extends Object> List<T> l(List<T> sink, Iterator<T> items) {
		while (items.hasNext())
			sink.add(items.next());
		return sink;
	}

	static public <T extends Object> List<T> l(Enumeration<T> items) {
		return l(new ArrayList<T>(), items);
	}

	static public <T extends Object> List<T> l(List<T> sink, Enumeration<T> items) {
		while (items.hasMoreElements())
			sink.add(items.nextElement());
		return sink;
	}

	/**
	 * convenient for quickly building / populating maps from a collection by some property.
	 * 
	 * 
	 * @param <T>
	 *            type of extracted value
	 * @param <V>
	 *            type of value
	 * @param getter
	 *            <V, T> returns the value from V which is add to the list
	 * @param c
	 *            Collection<V>
	 * @return the List<T>
	 */

	static public <V, T> List<T> l(Collection<V> c, Getter<V, T> getter) {
		if (isEmpty(c))
			return Collections.EMPTY_LIST;

		List<T> l = new ArrayList<T>(c.size());
		for (V v : c)
			l.add(getter.get(v));

		return l;
	}

	/**
	 * set {@link #s(Object...)} except that it doesnt take a sink. simply returns a newly created and populated {@link HashSet}
	 */
	static public <T extends Object> Set<T> s(T... items) {
		return s(new HashSet<T>(), items);
	}

	/**
	 * given a collection of objects, if all objects have the exact same class type (see {@link Class#getClass()}) then that class type is returned, otherwise null. any empty
	 * collection will also return null
	 * 
	 * @param <T>
	 *            type of class to return
	 * @param it
	 *            the collection to inspect
	 * @return class type of elements in collection or null
	 */
	static public <T> Class<? extends T> getClassIfSame(Iterable<? extends T> it) {
		final Iterator<? extends T> iterator = it.iterator();
		if (!iterator.hasNext())
			return null;
		T t = iterator.next();
		if (t == null)
			return null;
		final Class r = t.getClass();
		while (iterator.hasNext())
			if ((t = iterator.next()) == null || t.getClass() != r)
				return null;
		return r;
	}

	/**
	 * similar to the unix comm tool(but ordering of the inputs is not important). <BR>
	 * Given the venn diagram for two sets, can return a combination of:<BR>
	 * * all of the elements unique to the <B>left</B> set<BR>
	 * * all of the elements unique to the <B>right</B> set<BR>
	 * * all of the elements found in the <B>both</B> sets
	 * <P>
	 * 
	 * 
	 * @param <V>
	 *            type of elements in the set
	 * @param l
	 *            the left set
	 * @param r
	 *            the right set
	 * @param incLeft
	 *            if true, include those elements unique to the left set
	 * @param incRight
	 *            if true, include those elements unique to the right set
	 * @param both
	 *            if true, include those elements found in both sets
	 * @return a set containing elements from the left and right set base on flags
	 */
	public static <V> Set<V> comm(Set<? extends V> l, Set<? extends V> r, boolean incLeft, boolean incRight, boolean both) {
		return comm(l, r, incLeft, incRight, both, new HashSet<V>());
	}

	public static final Boolean ONLY_LEFT = Boolean.TRUE;
	public static final Boolean ONLY_RIGHT = Boolean.FALSE;
	public static final Boolean BOTH = null;

	/**
	 * Compares the contents of two sets and builds a map mapping which values are unique to which set.
	 * 
	 * @param left
	 * @param right
	 * @param representCommonAsNull
	 *            if true, keys common to both sets will also be included in the map with a value of null.
	 * @return values of <b>true</b> indicate the corresponding key only exists on the <b>left</b> set while <b>false</b> indicates the key only exists on the <b>right</b> set. If
	 *         the representCommaAsNull is present then values of null indicate the key exists in both the left and right set.
	 */
	public static <V> Map<V, Boolean> diff(Set<V> left, Set<V> right, boolean representCommonAsNull) {
		Map<V, Boolean> r = new HashMap<V, Boolean>(Math.max(10, 2 * MH.diff(left.size(), right.size())));
		for (V i : CH.comm(left, right, true, false, false))
			r.put(i, ONLY_LEFT);
		for (V i : CH.comm(left, right, false, true, false))
			r.put(i, ONLY_RIGHT);
		if (representCommonAsNull)
			for (V i : CH.comm(left, right, false, false, true))
				r.put(i, BOTH);
		return r;
	}
	/**
	 * see {@link #comm(Set, Set, boolean, boolean, boolean)} except the items meeting the flagged criteria are added to the sink
	 */
	static public <V> Set<V> comm(Set<? extends V> l, Set<? extends V> r, boolean incLeft, boolean incRight, boolean both, Set<V> sink) {
		if (both) {
			if (!incLeft && !incRight) {
				if (r.size() > l.size()) {
					for (V v : l)
						if (r.contains(v))
							sink.add(v);
				} else {
					for (V v : r)
						if (l.contains(v))
							sink.add(v);
				}
			} else {
				if (incLeft)
					sink.addAll(l);
				if (incRight)
					sink.addAll(r);
			}
		} else {
			if (incLeft)
				for (V v : l)
					if (!r.contains(v))
						sink.add(v);
			if (incRight)
				for (V v : r)
					if (!l.contains(v))
						sink.add(v);
		}
		return sink;
	}

	/**
	 * returns the value in the map associated with a particular key. if the key is not present in the map, then a RuntimeException is thrown. Please note: if the map permits null
	 * values then null will be returned if the specied key is associated with a null value. <BR>
	 * This methods is prefered over simply calling {@link Map#get(Object)} when ever the map <i>must</i> contain the key. Instead of simply raising a {@link NullPointerException}
	 * when the missing value is dereferenced, this method will fail fast & tries to provide good user feed back as to what went wrong.
	 * 
	 * 
	 * @param <K>
	 *            key type
	 * @param <V>
	 *            value type
	 * @param map
	 *            map containing the key
	 * @param key
	 *            the key to return the value for
	 * @return the associated value. (may return null if map can contain null values)
	 * @throws RuntimeException
	 *             describing the key not found. A list of permissible keys is also described as long as the size of the map is less then {@link #MAX_COLLECTION_TO_DELINEATE}
	 */
	public static <K, V> V getOrThrow(Map<K, V> map, K key) {
		return getOrThrow(map, key, "key not found");
	}

	/**
	 * returns the value in the map associated with a particular key cast to the requested type (see {@link OH#cast(Object, Class)}. if the key is not present in the map, then a
	 * RuntimeException is thrown. if the map does not contain the key, or the associated value is null, the dflt will be returned
	 * 
	 * @param <K>
	 *            key type
	 * @param <V>
	 *            value type
	 * @param cast
	 *            the type of class the return should be cast to. see {@link OH#cast(Object, Class)}
	 * @param map
	 *            map containing the key
	 * @param key
	 *            the key to return the value for
	 * @param dflt
	 *            default value to return if map does not contain key, or key is associated with null value
	 * @return the associated value or dflt
	 * @throws RuntimeException
	 *             if casting failed
	 */
	public static <K, V> V getOr(Class<V> cast, Map<K, ?> map, K key, V dflt) {
		if (map == null)
			return dflt;
		Object r = map.get(key);
		if (r == null && !map.containsKey(key))
			return dflt;
		try {
			return OH.cast(r, cast);
		} catch (Exception e) {
			throw new DetailedException("error casting", e).set("key", key).set("value to cast", r).set("value to cast type", OH.getClass(r).getName()).set("attempting to cast to",
					cast);
		}

	}

	public static <K, V> void putExcept(Map<K, V> map, K key, V value, V except) {
		if (OH.ne(value, except))
			map.put(key, value);
	}
	public static <K, V> V getOr(Caster<V> caster, Map<K, ?> map, K key, V dflt) {
		if (map == null)
			return dflt;
		Object r = map.get(key);
		if (r == null && !map.containsKey(key))
			return dflt;
		try {
			return caster.cast(r);
		} catch (Exception e) {
			throw new DetailedException("error casting", e).set("key", key).set("value to cast", r).set("value to cast type", OH.getClass(r).getName()).set("attempting to cast to",
					caster);
		}

	}

	public static <K, V> V getOrNoThrow(Class<V> cast, Map<K, ?> map, K key, V dflt) {
		try {
			return getOr(cast, map, key, dflt);
		} catch (Exception ex) {
			return dflt;
		}
	}
	public static <K, V> V getOrNoThrow(Caster<V> caster, Map<K, ?> map, K key, V dflt) {
		try {
			return getOr(caster, map, key, dflt);
		} catch (Exception ex) {
			return dflt;
		}
	}

	/**
	 * returns the value in the map associated with a particular key. if the key is not present in the map or the key is associated with a null value then dflt is returned .
	 * 
	 * @param <K>
	 *            key type
	 * @param <V>
	 *            value type
	 * @param map
	 *            map containing the key
	 * @param key
	 *            the key to return the value for
	 * @param dflt
	 *            default value to return if map does not contain key, or key is associated with null value
	 * @return the associated value. (may return null if map can contain null values)
	 */
	public static <K, V> V getOr(Map<K, V> map, K key, V dflt) {
		if (map == null)
			return dflt;
		V r = map.get(key);
		if (r == null && !map.containsKey(key))
			return dflt;
		return r;
	}

	/**
	 * returns the element from the list at index (see {@link List#get(int)}) unless the index is out of bounds for the list in which case dflt is returned.
	 * 
	 * @param <V>
	 * @param list
	 *            the list to dereference
	 * @param index
	 *            the index of the element to return
	 * @param dflt
	 *            value to return if index < 0 or >=size of the list
	 * @return element at list[index] or dflt
	 */
	public static <V> V getOr(List<V> list, int index, V dflt) {
		return list != null && OH.isBetween(index, 0, list.size() - 1) ? list.get(index) : dflt;
	}

	/**
	 * returns the value in the map associated with a particular key. if the key is not present in the map, then a RuntimeException is thrown using the description provided to
	 * describe the key type. Please note: if the map permits null values then null will be returned if the specied key is associated with a null value.<BR>
	 * This methods is prefered over simply calling {@link Map#get(Object)} when ever the map <i>must</i> contain the key. Instead of simply raising a {@link NullPointerException}
	 * when the missing value is dereferenced, this method will fail fast & tries to provide good user feed back as to what went wrong.
	 * 
	 * @param <K>
	 *            key type
	 * @param <V>
	 *            value type
	 * @param map
	 *            map containing the key
	 * @param key
	 *            the key to return the value for
	 * @param description
	 *            a string description of the value's purpose (for exampl, a map of ids to users might use "user" as the description)
	 * @return the associated value. (may return null if map can contain null values)
	 * @throws RuntimeException
	 *             describing the key not found. A list of permissible keys is also described as long as the size of the map is less then {@link #MAX_COLLECTION_TO_DELINEATE}
	 */
	public static <K, V> V getOrThrow(Map<K, V> map, K key, String description) {
		V r = map.get(key);
		if (r == null && !map.containsKey(key))
			throw new DetailedException(description + ": " + key).set(SUPPLIED_KEY, key).set(AVAILABLE_KEYS, map.keySet());
		return r;
	}
	public static <K, V> V getOrThrow(Map<K, V> map, K key, String description, String description2) {
		V r = map.get(key);
		if (r == null && !map.containsKey(key))
			throw new DetailedException(description + description2 + ": " + key).set(SUPPLIED_KEY, key).set(AVAILABLE_KEYS, map.keySet());
		return r;
	}

	/**
	 * removes the key and value in the map associated with a particular key (see {@link Map#remove(Object)}). if the key is not present in the map, then a RuntimeException is
	 * thrown using the description provided to describe the key type. Please note: if the map permits null values then null will be removed and returned if the specied key is
	 * associated with a null value.<BR>
	 * This methods is prefered over simply calling {@link Map#remove(Object)} when ever the map <i>must</i> contain the key. Instead of simply raising a
	 * {@link NullPointerException} when the missing value is dereferenced, this method will fail fast & tries to provide good user feed back as to what went wrong.
	 * 
	 * @param <K>
	 *            key type
	 * @param <V>
	 *            value type
	 * @param map
	 *            map containing the key
	 * @param key
	 *            the key to return the value for
	 * @param description
	 *            a string description of the value's purpose (for example, a map of ids to users might use "user" as the description)
	 * @return the associated value. (may return null if map can contain null values)
	 * @throws RuntimeException
	 *             describing the key not found. A list of permissible keys is also described as long as the size of the map is less then {@link #MAX_COLLECTION_TO_DELINEATE}
	 */
	public static <K, V> V removeOrThrow(Map<K, V> map, K key, String description) {
		if (!map.containsKey(key)) {
			throw new DetailedException(description + " not found").set(REQUIRED_KEY, key).set(AVAILABLE_KEYS, map.keySet());
		}
		return map.remove(key);
	}

	/**
	 * removes the key and value in the map associated with a particular key (see {@link Map#remove(Object)}). If the key is not present in the map, then a RuntimeException is
	 * thrown using the description provided to describe the key type. Please note: if the map permits null values then null will be removed and returned if the specied key is
	 * associated with a null value.<BR>
	 * This methods is prefered over simply calling {@link Map#remove(Object)} when ever the map <i>must</i> contain the key. Instead of simply raising a
	 * {@link NullPointerException} when the missing value is dereferenced, this method will fail fast & tries to provide good user feed back as to what went wrong.
	 * 
	 * @param <K>
	 *            key type
	 * @param <V>
	 *            value type
	 * @param map
	 *            map containing the key
	 * @param key
	 *            the key to return the value for
	 * @return the associated value. (may return null if map can contain null values)
	 * @throws RuntimeException
	 *             describing the key not found. A list of permissible keys is also described as long as the size of the map is less then {@link #MAX_COLLECTION_TO_DELINEATE}
	 */
	public static <K, V> V removeOrThrow(Map<K, V> map, K key) {
		V r = map.remove(key);
		if (r == null) {
			throw new DetailedException("key not found").set(REQUIRED_KEY, key).set(AVAILABLE_KEYS, map.keySet());
		}
		return r;
	}

	/**
	 * returns the value in the map associated with a particular key, cast to the requested type. if the key is not present in the map, then a RuntimeException is thrown using the
	 * description provided to describe the key type. Please note: if the map permits null values then null will be returned if the specied key is associated with a null value.<BR>
	 * This methods is prefered over simply calling {@link Map#get(Object)} when ever the map <i>must</i> contain the key. Instead of simply raising a {@link NullPointerException}
	 * when the missing value is dereferenced, this method will fail fast & tries to provide good user feed back as to what went wrong.
	 * 
	 * @param <K>
	 *            key type
	 * @param <V>
	 *            value type
	 * @param cast
	 *            the type of class the return should be cast to. see {@link OH#cast(Object, Class)}
	 * @param map
	 *            map containing the key
	 * @param key
	 *            the key to return the value for
	 * @param description
	 *            a string description of the value's purpose (for exampl, a map of ids to users might use "user" as the description)
	 * @return the associated value. (may return null if map can contain null values)
	 * @throws RuntimeException
	 *             describing the key not found. A list of permissible keys is also described as long as the size of the map is less then {@link #MAX_COLLECTION_TO_DELINEATE}
	 */
	public static <K, V> V getOrThrow(Class<V> cast, Map<K, ?> map, K key, String description) {
		Object r = map.get(key);
		if (r == null && !map.containsKey(key)) {
			throw new DetailedException(description + " not found: " + key).set(REQUIRED_KEY, key).set(AVAILABLE_KEYS, map.keySet()).set("cast to", cast);
		}
		try {
			return OH.cast(r, cast);
		} catch (Exception e) {
			throw new DetailedException("error casting value", e).set(REQUIRED_KEY, key).set("cast to", cast).set("value to cast", r);
		}
	}
	public static <K, V> V getOrThrow(Caster<V> caster, Map<K, ?> map, K key, String description) {
		Object r = map.get(key);
		if (r == null && !map.containsKey(key)) {
			throw new DetailedException(description + " not found: " + key).set(REQUIRED_KEY, key).set(AVAILABLE_KEYS, map.keySet()).set("cast to", caster);
		}
		try {
			return caster.cast(r);
		} catch (Exception e) {
			throw new DetailedException("error casting value", e).set(REQUIRED_KEY, key).set("cast to", caster).set("value to cast", r);
		}
	}

	/**
	 * returns the value in the map associated with a particular key, cast to the requested type. if the key is not present in the map, then a RuntimeException is thrown using the
	 * description provided to describe the key type. Please note: if the map permits null values then null will be returned if the specied key is associated with a null value.<BR>
	 * This methods is prefered over simply calling {@link Map#get(Object)} when ever the map <i>must</i> contain the key. Instead of simply raising a {@link NullPointerException}
	 * when the missing value is dereferenced, this method will fail fast & tries to provide good user feed back as to what went wrong.
	 * 
	 * @param <K>
	 *            key type
	 * @param <V>
	 *            value type
	 * @param cast
	 *            the type of class the return should be cast to. see {@link OH#cast(Object, Class)}
	 * @param map
	 *            map containing the key
	 * @param key
	 *            the key to return the value for
	 * @return the associated value. (may return null if map can contain null values)
	 * @throws RuntimeException
	 *             describing the key not found. A list of permissible keys is also described as long as the size of the map is less then {@link #MAX_COLLECTION_TO_DELINEATE}
	 */
	public static <K, V> V getOrThrow(Class<V> cast, Map<K, ?> map, K key) {
		Object r = map.get(key);
		if (r == null && !map.containsKey(key)) {
			throw new DetailedException("key not found: " + key).set(REQUIRED_KEY, key).set(AVAILABLE_KEYS, map.keySet()).set("cast to", cast);
		}
		try {
			return OH.cast(r, cast);
		} catch (Exception e) {
			throw new DetailedException("error casting value", e).set(REQUIRED_KEY, key).set("cast to", cast).set("value to cast", r);
		}

	}
	public static <K, V> V getOrThrow(Caster<V> caster, Map<K, ?> map, K key) {
		Object r = map.get(key);
		if (r == null && !map.containsKey(key)) {
			throw new DetailedException("key not found: " + key).set(REQUIRED_KEY, key).set(AVAILABLE_KEYS, map.keySet()).set("cast to", caster);
		}
		try {
			return caster.cast(r);
		} catch (Exception e) {
			throw new DetailedException("error casting value", e).set(REQUIRED_KEY, key).set("cast to", caster).set("value to cast", r);
		}

	}

	/**
	 * Puts the key and value into the map (similar to {@link Map#put(Object, Object)}) but ensures that the key did not exist prior. If the key does exist & the associated value
	 * is different then the value supplied an exception is raised explaining the issue and the map is left unmodified
	 * 
	 * @param <K>
	 *            the keys type of the map
	 * @param <V>
	 *            the values type of the map
	 * @param map
	 *            map to have the key and value added to.
	 * @param key
	 *            the key to add
	 * @param value
	 *            the value to add
	 * @return true if the map was mutated. false if not(because the key and value supplied already exist in the map)
	 * @throws RuntimeException
	 *             if the key already exists in the map, but is associated with a different value
	 */
	public static <K, V> boolean putOrThrow(Map<K, V> map, K key, V value) {
		if (map.containsKey(key)) {
			V existing = map.get(key);
			if (OH.eq(existing, value))
				return false;
			throw new DetailedException("key already exists in map and associated with different value").set(SUPPLIED_KEY, key).set("supplied value", value).set("existing value",
					existing);
		}
		map.put(key, value);
		return true;
	}

	public static <K, V> boolean putOrThrow(Map<K, V> map, K key, V value, String message) {
		if (map.containsKey(key)) {
			V existing = map.get(key);
			if (OH.eq(existing, value))
				return false;
			throw new DetailedException(message + ": " + key).set(SUPPLIED_KEY, key).set("supplied value", value).set("existing value", existing);
		}
		map.put(key, value);
		return true;
	}

	public static <T> T addOrThrow(Set<T> set, T value) {
		if (!set.add(value))
			throw new DetailedException("value already exists in set").set("supplied value", value);
		return value;
	}

	public static <T> T addIdentityOrThrow(List<T> list, T value) {
		if (!list.isEmpty())
			for (T i : list)
				if (i == value)
					throw new DetailedException("value already exists in list").set("supplied value", value);
		list.add(value);
		return value;
	}

	public static <T> T removeOrThrow(Set<T> set, T value) {
		if (!set.remove(value))
			throw new DetailedException("value not found for remove in set").set("supplied value", value).set("existing values", set);
		return value;
	}

	/**
	 * tests to see if the collection is not null and not empty (see {@link Collection#isEmpty()}).
	 * 
	 * @param c
	 *            collection to inspect
	 * @return true iff c is null and is not empty
	 */
	public static boolean isntEmpty(Collection<?> c) {
		return !isEmpty(c);
	}

	/**
	 * tests to see if the collection is null or is empty (see {@link Collection#isEmpty()}).
	 * 
	 * @param c
	 *            collection to inspect
	 * @return true iff c is empty or null
	 */
	public static boolean isEmpty(Collection<?> c) {
		return c == null || c.isEmpty();
	}

	public static boolean isntEmpty(Iterable<?> c) {
		return !isEmpty(c);
	}

	public static boolean isEmpty(IterableAndSize<?> c) {
		return c == null || c.size() == 0;
	}
	public static boolean isntEmpty(IterableAndSize<?> c) {
		return !isEmpty(c);
	}

	public static boolean isEmpty(Iterable<?> c) {
		if (c == null)
			return true;
		else if (c instanceof Collection)
			return ((Collection) c).isEmpty();
		else if (c instanceof Map)
			return ((Map) c).isEmpty();
		else if (c instanceof IterableAndSize<?>)
			return ((IterableAndSize) c).size() == 0;
		return !c.iterator().hasNext();
	}

	/**
	 * tests to see if the map is not null and not empty (see {@link Map#isEmpty()}).
	 * 
	 * @param map
	 *            map to inspect
	 * @return true iff c is null and is not empty
	 */
	public static boolean isntEmpty(Map<?, ?> map) {
		return !isEmpty(map);
	}

	/**
	 * tests to see if the map is null or is empty (see {@link Map#isEmpty()}).
	 * 
	 * @param map
	 *            map to inspect
	 * @return true iff map is empty or null
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	/**
	 * insert an element to a sorted list with sorting maintained.
	 * 
	 * @param <T>
	 *            the type of elements in the list
	 * @param list
	 *            the list of sorted elements which will be mutated. <B>elements must be sorted, based on the comparator</B>. If the list is not properly sorted, results are
	 *            unexpected.
	 * @param value
	 *            the vlaue to add to the list
	 * @param comparator
	 *            used to compare the elements in the list for ordering
	 * @return the list param for conveniecnce.
	 */
	static public <T> int insertSorted(List<T> list, T value, Comparator<? super T> comparator, boolean replaceIfSame) {
		int min = 0, max = list.size();
		while (min < max) {
			int pivot = (min + max) / 2;
			int c = comparator.compare(list.get(pivot), value);
			if (c == 0) {
				if (replaceIfSame)
					list.set(pivot, value);
				else
					list.add(pivot, value);
				return pivot;
			} else if (c < 0) {
				min = pivot + 1;
			} else {
				max = pivot - 1;
			}
		}
		if (max < min)
			list.add(min, value);
		else if (min == list.size())
			list.add(value);
		else {
			int c = comparator.compare(list.get(min), value);
			if (c < 0) {
				list.add(min + 1, value);
				return min + 1;
			} else if (c == 0 && replaceIfSame) {
				list.set(min, value);
			} else
				list.add(min, value);
		}
		return min;
	}
	static public <T> int getPositionSorted(List<T> list, T value, Comparator<? super T> comparator) {
		int min = 0, max = list.size();
		while (min < max) {
			int pivot = (min + max) / 2;
			int c = comparator.compare(list.get(pivot), value);
			if (c == 0) {
				return pivot;
			} else if (c < 0) {
				min = pivot + 1;
			} else {
				max = pivot - 1;
			}
		}
		if (max < min)
			return min;
		else if (min == list.size())
			return list.size();
		else if (comparator.compare(list.get(min), value) < 0)
			return min + 1;
		else
			return min;
	}

	/**
	 * insert an element to a sorted list with natural sorting maintained. See {@link OH#compare(Comparable, Comparable)} for details on sorting null values
	 * 
	 * @param <T>
	 *            the type of elements in the list
	 * @param list
	 *            the list of sorted elements which will be mutated. <B>elements must be sorted, based on the comparator</B>. If the list is not properly sorted, results are
	 *            unexpected.
	 * @param value
	 *            the vlaue to add to the list
	 * @return the list param for conveniecnce.
	 */
	static public <T extends Comparable<?>> int insertSorted(List<T> list, T value) {
		return insertSorted(list, value, (Comparator<T>) ComparableComparator.INSTANCE, false);
	}

	/**
	 * tests to see if the list is sorted(based on the order of values returned from {@link Collection#iterator()}) returns true iff at any point value[N] is less <B>or equal</B>
	 * to value[N+1].
	 * 
	 * @param <T>
	 *            the type of elements in the list
	 * @param list
	 *            the collection of items to test for order
	 * @param comparator
	 *            used to determine ordinal relationships
	 * @return true iff the list is sorted or empty
	 */
	static public <T> boolean isSorted(Collection<T> list, Comparator<T> comparator) {
		if (list.size() < 2)
			return true;
		Iterator<T> i = list.iterator();
		for (T last = i.next(); i.hasNext();)
			if (comparator.compare(last, last = i.next()) > 0)
				return false;
		return true;
	}

	/**
	 * tests to see if the list is naturally sorted(based on the order of values returned from {@link Collection#iterator()}) returns true iff at any point value[N] is less <B>or
	 * equal</B> to value[N+1].
	 * 
	 * @param <T>
	 *            the type of elements in the list
	 * @param list
	 *            the collection of items to test for order
	 * @return true iff the list is sorted or empty
	 */
	static public <T extends Comparable<?>> boolean isSorted(Collection<T> list) {
		return isSorted(list, (Comparator<T>) ComparableComparator.INSTANCE);
	}

	/**
	 * return the index of the value with the maximum value (based on {@link Comparable#compareTo(Object)}). Note: if multiple elements are tied for maximum value, then the lowest
	 * index is returned. See {@link OH#compare(Comparable, Comparable)} for details on sorting null values
	 * <P>
	 * see {@link OH#compare(Comparable, Comparable)} for details on handlign null values.
	 * 
	 * @param <T>
	 *            the type of elements in the list
	 * @param values
	 *            the values to iterator over.
	 * @return the index of the highest value, or -1 if empty list.
	 */
	public static <T extends Comparable<?>> int maxIndex(List<T> values) {
		return maxIndex(values, (Comparator<T>) ComparableComparator.INSTANCE);
	}

	/**
	 * return the index of the value with the minimum value (based on {@link Comparable#compareTo(Object)}). Note: if multiple elements are tied for minimum value, then the lowest
	 * index is returned. see {@link OH#compare(Comparable, Comparable)} for details on handlign null values.
	 * 
	 * 
	 * @param <T>
	 *            the type of elements in the list
	 * @param values
	 *            the values to iterator over.
	 * @return the index of the lowest value, or -1 if empty list.
	 */
	public static <T> int minIndex(List<? extends T> values) {
		return minIndex(values, (Comparator<T>) ComparableComparator.INSTANCE);
	}

	/**
	 * return the index of the value with the maximum value (based on {@link Comparator#compare(Object, Object)}). Note: if multiple elements are tied for maximum value, then the
	 * lowest index is returned.
	 * 
	 * @param <T>
	 *            the type of elements in the list
	 * @param values
	 *            the values to iterator over.
	 * @param t
	 *            the comparator used for comparing which values have higher / lower ordinal values
	 * @return the index of the highest value, or -1 if empty list.
	 */
	public static <T> int maxIndex(List<? extends T> values, Comparator<T> t) {
		int r = -1, j = 0;
		T max = null;
		for (T i : values) {
			if (max == null || t.compare(i, max) > 0) {
				max = i;
				r = j;
			}
			j++;
		}
		return r;
	}

	/**
	 * return the index of the value with the minimum value (based on {@link Comparator#compare(Object, Object)}). Note: if multiple elements are tied for minimum value, then the
	 * lowest index is returned.
	 * 
	 * @param <T>
	 *            the type of elements in the list
	 * @param values
	 *            the values to iterator over.
	 * @param t
	 *            the comparator used for comparing which values have higher / lower ordinal values
	 * @return the index of the lowest value, or -1 if empty list.
	 */
	public static <T> int minIndex(List<? extends T> values, Comparator<T> t) {
		int r = -1, j = 0;
		T min = null;
		for (T i : values) {
			if (min == null || t.compare(i, min) < 0) {
				min = i;
				r = j;
			}
			j++;
		}
		return r;
	}

	public static <T extends Comparable<T>> List<T> sort(Iterable<? extends T> elements) {
		List<T> list = (List<T>) l(elements);
		Collections.sort((List) list, (Comparator) ComparableComparator.INSTANCE);
		return list;
	}

	public static <T> List<T> sort(Iterable<? extends T> elements, Comparator<? super T> comparator) {
		List<T> list = (List<T>) l(elements);
		Collections.sort((List) list, comparator);
		return list;
	}
	public static <T> List<T> sort(Collection<? extends T> elements, Comparator<? super T> comparator) {
		List<T> list = (List<T>) l(elements);
		Collections.sort((List) list, comparator);
		return list;
	}
	public static <T extends Comparable<T>> List<T> sortUniq(Iterable<? extends T> elements) {
		List<T> list = (List<T>) l(elements);
		Collections.sort((List) list, (Comparator) ComparableComparator.INSTANCE);
		uniqInplace(list);
		return list;
	}
	public static <T> List<T> sortUniq(Iterable<? extends T> elements, Comparator<? super T> comparator) {
		List<T> list = (List<T>) l(elements);
		Collections.sort((List) list, comparator);
		uniqInplace(list, (Comparator) comparator);
		return list;
	}

	//assumes input list is sorted 
	public static <T> List<T> uniqInplace(List<T> list) {
		if (list.size() < 2)
			return list;
		int pos = 0;
		T last = list.get(0);
		for (int i = 1, l = list.size(); i < l; i++) {
			T t = list.get(i);
			if (OH.ne(last, t)) {
				if (++pos != i)
					list.set(pos, t);
				last = t;
			}
		}
		removeAll(list, ++pos, list.size() - pos);
		return list;
	}
	public static <T> List<T> uniqInplace(List<T> list, Comparator<T> comp) {
		if (list.size() < 2)
			return list;
		int pos = 0;
		T last = list.get(0);
		for (int i = 1, l = list.size(); i < l; i++) {
			T t = list.get(i);
			if (comp.compare(last, t) != 0) {
				if (++pos != i)
					list.set(pos, t);
				last = t;
			}
		}
		removeAll(list, ++pos, list.size() - pos);
		return list;
	}
	/**
	 * replaces an existing value within a list (see {@link List#indexOf(Object)} with a replacement value. If the supplied existing value is not found in the supplied list then -1
	 * is returned and the list is left unmodified
	 * 
	 * @param <T>
	 *            type of values in list
	 * @param list
	 *            list containing existing value to be replaced
	 * @param existing
	 *            existing value
	 * @param replacement
	 *            value to replace existing value
	 * @return -1 if the existing value is not found.
	 */
	public static <T> int replace(List<T> list, T existing, T replacement) {
		int r = list.indexOf(existing);
		if (r != -1)
			list.set(r, replacement);
		return r;
	}

	public static <T> int replaceOrThrow(List<T> list, T existing, T replacement) {
		int r = replace(list, existing, replacement);
		if (r == -1)
			throw new DetailedException("key already exists and associated with different value").set("supplied existing", existing).set("supplied replacement", replacement)
					.set("list", list);
		else
			return r;
	}

	public static <T> void removeOrThrow(List<T> list, T obj) {
		if (!list.remove(obj))
			throw new DetailedException("value not found in list").set("list", list).set("value to remove", obj);
	}

	public static <T> Iterable<T> iterate(Iterable<? extends Iterable<T>> i) {
		return IterableIterator.create(i);
	}

	public static <T> Iterable<T> iterate(Iterable<T>... i) {
		return IterableIterator.create(i);
	}

	/**
	 * passes through the iterator unless it's null in which case an empty iterator is returned
	 * 
	 * @param i
	 * @return i
	 */
	public static <T> Iterable<T> i(Iterable<T> i) {
		return i == null ? EmptyIterable.INSTANCE : i;
	}

	public static <T> Iterable<T> iterate(T[]... a) {
		final ArrayIterator<T>[] ai = new ArrayIterator[a.length];
		for (int i = 0; i < a.length; i++)
			ai[i] = new ArrayIterator<T>(a[i]);
		return IterableIterator.create(ai);
	}

	public static int size(Collection<?> c) {
		return c == null ? 0 : c.size();
	}

	public static int size(Map c) {
		return c == null ? 0 : c.size();
	}

	public static <T> List<T> emptyList(Class<T> clazz) {
		return Collections.EMPTY_LIST;
	}

	public static <K, V> Map<K, V> emptyMap(Class<K> kClazz, Class<V> vClazz) {
		return Collections.EMPTY_MAP;
	}

	public static <K> Set<K> emptySet(Class<K> kClazz) {
		return Collections.EMPTY_SET;
	}

	public static <K, V> void swapKeyValue(Collection<? extends Map.Entry<V, K>> in, Map<K, V> sink) {
		for (Map.Entry<V, K> m : in)
			sink.put(m.getValue(), m.getKey());
	}
	public static <K, V> Map<K, V> swapKeyValue(Collection<? extends Map.Entry<V, K>> in) {
		Map<K, V> r = new HashMap<K, V>();
		swapKeyValue(in, r);
		return r;
	}

	public static int size(Iterable<?> i) {
		if (i == null)
			return 0;
		if (i instanceof Collection)
			return size((Collection<?>) i);
		if (i instanceof IterableAndSize)
			return ((IterableAndSize) i).size();
		if (i instanceof Map)
			return ((Map<?, ?>) i).size();
		int r = 0;
		for (Object o : i)
			r++;
		return r;
	}

	/**
	 * joins two maps of the same key type. The keys of the returned map is a superset of both keys from the left and right maps. For each entry in the returned map, the
	 * corresponding value will be tuple populated with the values from the left(getA) and right(getB) supplied maps. A null value inside the tuple indicates that the key did not
	 * exist(or the supplied map had a null value)
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	public static <K, V1, V2> Map<K, Tuple2<V1, V2>> join(Map<K, V1> left, Map<K, V2> right) {

		int lsize = left.size();
		int rsize = right.size();
		if (lsize == 0 || rsize == 0) {
			if (lsize == rsize)
				return new HashMap<K, Tuple2<V1, V2>>();
			else if (lsize == 0) {
				final Map<K, Object> r = new HashMap<K, Object>(right);
				for (Entry<K, Object> e : r.entrySet())
					e.setValue(new Tuple2(null, e.getValue()));
				return (Map) r;
			} else {
				final Map<K, Object> r = new HashMap<K, Object>(left);
				for (Entry<K, Object> e : r.entrySet())
					e.setValue(new Tuple2(e.getValue(), null));
				return (Map) r;
			}
		} else if (lsize >= rsize) {
			int outerRemaining = rsize;
			Map<K, Object> r = new HashMap<K, Object>(left);
			//loop through the left side and find matching keys in the right side
			for (Entry<K, Object> e : r.entrySet()) {
				V2 rightValue = right.get(e.getKey());
				e.setValue(new Tuple2(e.getValue(), rightValue));
				if (rightValue != null)
					outerRemaining--;
			}
			if (outerRemaining > 0) {
				for (Entry<K, V2> e : right.entrySet()) {
					Object existing = r.get(e.getKey());
					if (existing != null)//already added
						continue;
					r.put(e.getKey(), new Tuple2(null, e.getValue()));
					if (--outerRemaining == 0)
						break;
				}
			}
			return (Map) r;
		} else {
			int outerRemaining = lsize;
			Map<K, Object> r = new HashMap<K, Object>(right);
			//loop through the left side and find matching keys in the right side
			for (Entry<K, Object> e : r.entrySet()) {
				V1 leftValue = left.get(e.getKey());
				e.setValue(new Tuple2(leftValue, e.getValue()));
				if (leftValue != null)
					outerRemaining--;
			}
			if (outerRemaining > 0) {
				for (Entry<K, V1> e : left.entrySet()) {
					Object existing = r.get(e.getKey());
					if (existing != null)//already added
						continue;
					r.put(e.getKey(), new Tuple2(e.getValue(), null));
					if (--outerRemaining == 0)
						break;
				}
			}
			return (Map) r;
		}
	}

	public static <K, V> Map<K, V> getAll(Mapping<K, V> right, Iterable<K> left) {
		HashMap<K, V> r = new HashMap<K, V>();
		for (K k : left) {
			V value = right.get(k);
			if (value != null || right.containsKey(k))
				r.put(k, value);
		}
		return r;
	}
	public static <K, V> Map<K, V> getAll(Map<K, V> right, Iterable<K> left) {
		HashMap<K, V> r = new HashMap<K, V>();
		for (K k : left) {
			V value = right.get(k);
			if (value != null || right.containsKey(k))
				r.put(k, value);
		}
		return r;
	}
	public static <K, V> boolean putOrLog(Map<K, V> map, K key, V value, Logger log, Level level) {
		if (map.containsKey(key)) {
			V existing = map.get(key);
			if (existing == value || existing.equals(value))
				return false;
			LH.log(log, level, "Key already exists in map and associated with different value",
					new DetailedException().set(SUPPLIED_KEY, key).set("supplied value", value).set("existing value", existing));
		} else
			map.put(key, value);
		return true;
	}

	public static <T, C extends Collection<T>> C addSkipNulls(C sink, T... valuesOrNulls) {
		if (valuesOrNulls != null)
			for (T v : valuesOrNulls)
				if (v != null)
					sink.add(v);
		return sink;
	}

	public static <T, C extends Collection<T>> C addSkipNull(C sink, T valueOrNull) {
		if (valueOrNull != null)
			sink.add(valueOrNull);
		return sink;
	}

	static public <T> List<List<T>> batchSublists(List<T> data, int maxBatchSize, boolean keepSizesSimilar) {
		int l = data.size();
		if (l == 0)
			return new ArrayList<List<T>>(1);
		if (l <= maxBatchSize) {
			List<List<T>> r = new ArrayList<List<T>>(1);
			r.add(new ArrayList(data));
			return r;
		}
		int batches = (maxBatchSize - 1 + l) / maxBatchSize;
		int size = keepSizesSimilar ? 1 + (l + batches - 1) / batches : maxBatchSize;
		List<List<T>> r = new ArrayList<List<T>>(batches);
		for (int i = 0; i < l; i += size)
			r.add(data.subList(i, Math.min(i + size, l)));
		return r;

	}

	public static <T> T first(Iterable<T> collection) {
		if (collection instanceof List)
			return first((List<T>) collection);
		if (collection == null)
			return null;
		Iterator<T> i = collection.iterator();
		return i.hasNext() ? i.next() : null;
	}
	public static <T> T last(Iterable<T> collection) {
		if (collection instanceof List)
			return last((List<T>) collection);
		Iterator<T> i = collection.iterator();
		T r = null;
		while (i.hasNext())
			r = i.next();
		return r;
	}
	public static <T> T firstOr(Iterable<T> collection, T dfault) {
		if (collection == null)
			return dfault;
		Iterator<T> i = collection.iterator();
		return i.hasNext() ? i.next() : dfault;
	}

	public static <T extends Iterable<?>> T noEmpty(T collection, T onEmpty) {
		return isEmpty(collection) ? onEmpty : collection;
	}

	public static <T> T lastOr(List<T> l, T dfault) {
		if (l == null)
			return dfault;
		int size = l.size();
		return size == 0 ? dfault : l.get(size - 1);
	}
	public static <T> T last(List<T> l) {
		if (l == null)
			return null;
		int size = l.size();
		return size == 0 ? null : l.get(size - 1);
	}
	public static <T> T first(List<T> l) {
		return l == null || l.size() == 0 ? null : l.get(0);
	}

	public static Set<Long> asSet(long... values) {
		final Set<Long> r = new HashSet<Long>(values.length);
		for (final long value : values)
			r.add(value);
		return r;
	}
	public static Set<Integer> asSet(int... values) {
		final Set<Integer> r = new HashSet<Integer>(values.length);
		for (final int value : values)
			r.add(value);
		return r;
	}
	public static Set<Short> asSet(short... values) {
		final Set<Short> r = new HashSet<Short>(values.length);
		for (final short value : values)
			r.add(value);
		return r;
	}
	public static Set<Byte> asSet(byte... values) {
		final Set<Byte> r = new HashSet<Byte>(values.length);
		for (final byte value : values)
			r.add(value);
		return r;
	}
	public static Set<Double> asSet(double... values) {
		final Set<Double> r = new HashSet<Double>(values.length);
		for (final double value : values)
			r.add(value);
		return r;
	}
	public static Set<Float> asSet(float... values) {
		final Set<Float> r = new HashSet<Float>(values.length);
		for (final float value : values)
			r.add(value);
		return r;
	}
	public static Set<Character> asSet(char... values) {
		final Set<Character> r = new HashSet<Character>(values.length);
		for (final char value : values)
			r.add(value);
		return r;
	}
	public static Set<Boolean> asSet(boolean... values) {
		final Set<Boolean> r = new HashSet<Boolean>(values.length);
		for (final boolean value : values)
			r.add(value);
		return r;
	}

	public static <T> Collection<T> values(Map<?, T> map) {
		return map == null ? EmptyCollection.INSTANCE : map.values();
	}

	public static <K, V> Set<Entry<K, V>> entrySet(Map<K, V> m) {
		return m == null ? Collections.EMPTY_SET : m.entrySet();
	}

	public static <T extends Collection<?>> T clear(T collection) {
		collection.clear();
		return collection;
	}

	public static <T> List<T> sublistStartingAt(List<T> list, int i) {
		return list.subList(i, list.size());
	}

	public static <T> Collection<T> addAll(Collection<T> sink, Iterable<? extends T> source) {
		for (T t : source)
			sink.add(t);
		return sink;
	}
	public static <T> Collection<T> addAll(Collection<T> sink, T[] source) {
		for (T t : source)
			sink.add(t);
		return sink;
	}
	public static <T> Collection<T> addAll(Collection<T> sink, Iterable<? extends T> source, int maxToAdd) {
		if (maxToAdd > 0)
			for (T t : source) {
				sink.add(t);
				if (--maxToAdd == 0)
					break;
			}
		return sink;
	}

	public static <T> T nextOr(Iterator<T> i, T ifNoNext) {
		return i.hasNext() ? i.next() : ifNoNext;
	}

	public static <T> int incrementValue(Map<T, Integer> map, T key) {
		Integer v = map.get(key);
		int value = v == null ? 1 : (v.intValue() + 1);
		map.put(key, value);
		return value;
	}

	public static <T extends Map<?, ?>> T noNull(T map) {
		return map == null ? (T) Collections.EMPTY_MAP : map;
	}

	public static <K, V> Map<K, V> castMap(Map<?, ?> source, Caster<K> keyCaster, Caster<V> valCaster) {
		Map<K, V> map = new HashMap<K, V>();
		castMap(source, keyCaster, valCaster, map);
		return map;
	}
	public static <K, V> void castMap(Map<?, ?> source, Caster<K> keyCaster, Caster<V> valueCaster, Map<K, V> sink) {
		for (Entry<?, ?> e : source.entrySet()) {
			try {
				sink.put(keyCaster.cast(e.getKey()), valueCaster.cast(e.getValue()));
			} catch (Exception ex) {
				throw OH.setCause(new ClassCastException("Error casting map for entry: " + e), ex);
			}
		}
	}

	public static <K, V> Map<K, V> putNoKeyNull(Map<K, V> sink, K key, V value) {
		if (key != null)
			sink.put(key, value);
		return sink;
	}
	public static <K, V> Map<K, V> putNoNull(Map<K, V> sink, K key, V value) {
		if (value != null)
			sink.put(key, value);
		return sink;
	}

	public static <T> void removeAll(List<T> list, int start, int length) {
		//TODO: be smarter about this, copy end to start and remove end only
		if (start < 0 || start + length > list.size())
			throw new IndexOutOfBoundsException("for size " + list.size() + " start: " + start + " length: " + length);
		else if (length == 0)
			return;
		else if (length == list.size()) {
			list.clear();
			return;
		}
		int moveCount = list.size() - (length + start);
		while (moveCount-- > 0)
			list.set(start, list.get((start++) + length));

		for (int i = start + length - 1; i >= start; i--) {
			list.remove(i);
		}
	}

	public static BitSet fillBitSet(BitSet bitSet, int startInclusive, int endInclusive, boolean value) {
		for (int i = startInclusive; i <= endInclusive; i++)
			bitSet.set(i, value);
		return bitSet;

	}

	public static <T> boolean containsAny(Set<T> l, Collection<T> r) {
		if (l.size() > 0 && r.size() > 0)
			for (T t : r)
				if (l.contains(t))
					return true;
		return false;
	}

	public static <T, C extends Collection<T>> C castAll(Iterable<?> source, Class<T> castTo, boolean required, C sink) {
		Caster<T> caster = OH.getCaster(castTo);
		for (Object o : source)
			sink.add(caster.cast(o, required));
		return sink;
	}
	public static <T> List<T> castAll(Collection<?> source, Class<T> castTo, boolean required) {
		List<T> r = new ArrayList<T>(source.size());
		return castAll(source, castTo, required, r);
	}
	public static <T> List<T> castAll(IterableAndSize<?> source, Class<T> castTo, boolean required) {
		List<T> r = new ArrayList<T>(source.size());
		return castAll(source, castTo, required, r);
	}

	//will wrap... Also, -1=last element, -2=snd last element.  If list is empyt, null
	public static <T> T getAtMod(List<T> list, int x) {
		int size = list.size();
		return size == 0 ? null : list.get(MH.mod(x, size));
	}

	public static <T> boolean toggle(Set<T> set, T value) {
		return set.add(value) || !set.remove(value);
	}

	public static boolean areSame(Set<?> s1, Set<?> s2) {
		return s1 == s2 || (s1 != null && s2 != null && s1.size() == s2.size() && (s1.isEmpty() || s1.containsAll(s2)));
	}
	public static boolean areSame(Map<?, ?> s1, Map<?, ?> s2) {
		if (s1 == s2)
			return true;
		if (s1 == null || s2 == null)
			return false;
		if (s1.size() != s2.size())
			return false;
		if (s1.isEmpty())
			return true;
		for (Entry<? extends Object, ? extends Object> i : s1.entrySet()) {
			Object v2 = s2.get(i.getKey());
			if (OH.ne(i.getValue(), v2))
				return false;
			if (v2 == null && !s2.containsKey(i.getKey()))
				return false;
		}
		return true;
	}
	public static boolean areSame(Map<?, ?> s1, Mapping s2) {
		if (s1 == s2)
			return true;
		if (s1 == null || s2 == null)
			return false;
		if (s1.size() != s2.size())
			return false;
		if (s1.isEmpty())
			return true;
		for (Entry<? extends Object, ? extends Object> i : s1.entrySet()) {
			Object v2 = s2.get(i.getKey());
			if (OH.ne(i.getValue(), v2))
				return false;
			if (v2 == null && !s2.containsKey(i.getKey()))
				return false;
		}
		return true;
	}

	public static <T> T getRandom(List<T> list, Random r) {
		final int size = list.size();
		if (size < 2)
			return size == 0 ? null : list.get(0);
		return list.get(r.nextInt(size));
	}

	//Optimized for array lists
	public static <T> List<T> splice(List<T> list, int index, int howMany, T[] params, int start, int end) {
		if (index < 0) {
			index = list.size() + 1 + index;
			if (index < 0)
				index = 0;
		}
		if (index >= list.size()) {
			for (int i = index - list.size(); i > 0; i--)
				list.add(null);
			for (int i = start; i < end; i++)
				list.add(params[i]);
			return list;
		}
		int sizeIncrease = (end - start) - howMany;
		if (sizeIncrease > 0)
			insertMany(list, index + howMany, sizeIncrease, null);
		else if (sizeIncrease < 0)
			removeMany(list, index + howMany + sizeIncrease, -sizeIncrease);
		for (int i = start; i < end; i++)
			list.set(index++, params[i]);
		return list;
	}

	public static <T> List<T> splice(List<T> list, int index, int howMany, List<T> params) {
		return splice(list, index, howMany, params, 0, params.size());
	}
	public static <T> List<T> splice(List<T> list, int index, int howMany, List<T> params, int start, int end) {
		if (index < 0) {
			index = list.size() + 1 + index;
			if (index < 0)
				index = 0;
		}
		if (index >= list.size()) {
			for (int i = index - list.size(); i > 0; i--)
				list.add(null);
			for (int i = start; i < end; i++)
				list.add(params.get(i));
			return list;
		}
		int sizeIncrease = (end - start) - howMany;
		if (sizeIncrease > 0)
			insertMany(list, index + howMany, sizeIncrease, null);
		else if (sizeIncrease < 0)
			removeMany(list, index + howMany + sizeIncrease, -sizeIncrease);
		for (int i = start; i < end; i++)
			list.set(index++, params.get(i));
		return list;
	}

	public static <T> List<T> insertMany(List<T> list, int index, int count, T value) {
		if (count == 0)
			return list;
		if (count == 1) {
			list.add(index, value);
			return list;
		}
		final int len = list.size();
		for (int pos = len - count; pos < len; pos++)
			list.add(pos < index ? value : list.get(pos));
		for (int pos = len - 1; pos >= index; pos--)
			list.set(pos, pos - count < index ? value : list.get(pos - count));
		return list;
	}
	public static <T> List<T> removeMany(List<T> list, int index, int count) {
		if (count == 0)
			return list;
		final int len = list.size();
		OH.assertLe(count + index, len, "index within bounds");
		if (count == len) {
			list.clear();
			return list;
		}
		while (index + count < len) {
			list.set(index, list.get(index + count));
			index++;
		}
		for (int i = len - 1; i >= index; i--)
			list.remove(i);
		return list;
	}
	public static boolean areSame(Iterator<?> i1, Iterator<?> i2) {
		while (i1.hasNext())
			if (!i2.hasNext() || OH.ne(i1.next(), i2.next()))
				return false;
		return !i2.hasNext();
	}
	public static <T> T firstDup(Collection<T> values) {
		if (values == null)
			return null;
		int size = values.size();
		if (size < 2)
			return null;
		Iterator<T> it = values.iterator();
		if (size < 6) {
			Object[] a = new Object[size];
			for (int i = 0; i < size; i++) {
				T val = it.next();
				for (int n = 0; n < i; n++)
					if (OH.eq(a[n], val))
						return val;
				a[i] = val;
			}
		} else {
			Set<T> s = new HasherSet<T>(BasicHasher.INSTANCE, size);
			for (int i = 0; i < size; i++) {
				T val = it.next();
				if (!s.add(val))
					return val;
			}
		}
		return null;
	}
	public static void setSize(ArrayList<?> sink, int size) {
		if (size < 0)
			throw new IndexOutOfBoundsException(SH.toString(size));
		if (size == 0) {
			sink.clear();
			return;
		}
		int n = sink.size();
		if (n < size)
			while (n-- < size)
				sink.add(null);
		else
			while (n > size)
				sink.remove(--n);

	}

	private static final Class UnmodifiableClass = Collections.unmodifiableMap(new HashMap()).getClass();

	public static <K, V> Map<K, V> unmodifiableMapNoNull(Map<K, V> map) {
		if (map == null)
			return Collections.EMPTY_MAP;
		if (UnmodifiableClass.isAssignableFrom(map.getClass()) || map == Collections.EMPTY_MAP)
			return map;
		return Collections.unmodifiableMap(map);
	}
	public static <K, V> void putAllMissing(Map<K, V> target, Map<K, V> toAdd) {
		for (Entry<K, V> e : toAdd.entrySet())
			if (!target.containsKey(e.getKey()))
				target.put(e.getKey(), e.getValue());

	}
	public static <T> Collection<T> getAllImplementing(Collection<?> c, Class<T> class1) {
		List r = null;
		int skipped = 0;
		for (Object e : c)
			if (class1.isInstance(e)) {
				if (r == null)
					r = new ArrayList<T>(c.size() - skipped);
				r.add(class1.cast(e));
			} else
				skipped++;
		return r == null ? Collections.emptyList() : r;
	}

	//0 = sorted, -1 should move up, 1 should move down
	public static <T> int isSorted(List<T> list, int i, Comparator<T> c) {
		final int size = list.size();
		if (size >= 2) {
			T val = list.get(i);
			if (i > 0 && c.compare(list.get(i - 1), val) > 0)
				return -1;
			if (i < size - 1 && c.compare(val, list.get(i + 1)) > 0)
				return 1;
		}
		return 0;
	}
	public static <T> ArrayList<T> copyAndAdd(Collection<T> l, T o) {
		ArrayList<T> r = new ArrayList<T>(l);
		r.add(o);
		return r;
	}
	public static <K, V> IdentityHashMap<K, V> copyAndPut(IdentityHashMap<K, V> m, K key, V value) {
		m = new IdentityHashMap<K, V>(m);
		m.put(key, value);
		return m;
	}
	public static <K, V> HashMap<K, V> copyAndPut(HashMap<K, V> m, K key, V value) {
		m = new HashMap<K, V>(m);
		m.put(key, value);
		return m;
	}
	public static <T> int indexOfIdentity(List<T> l, Object v) {
		if (l != null)
			for (int i = 0, n = l.size(); i < n; i++)
				if (l.get(i) == v)
					return i;
		return -1;
	}
}
