/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.Lockable;
import com.f1.base.Message;
import com.f1.base.PartialMessage;
import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.impl.ValuedHasher;
import com.f1.utils.structs.table.BasicTable;

/**
 * Valued Helper. See {@link Valued}
 */
public class VH {

	static public Object getNestedValue(Object o, String value, boolean throwOnError) {
		return RootAssister.INSTANCE.getNestedValue(o, value, throwOnError);
	}

	static public String toString(Object o) {
		return RootAssister.INSTANCE.toString(o);
	}

	static public StringBuilder toString(Object o, StringBuilder sb) {
		RootAssister.INSTANCE.toString(o, sb, null);
		return sb;
	}

	static public <T> T clone(T o) {
		return RootAssister.INSTANCE.clone(o);
	}

	public static String toLegibleString(Object o, int maxlength) {
		return RootAssister.INSTANCE.toLegibleString(o, maxlength);
	}

	public static String toLegibleStringNoCircref(Object o, int maxlength) {
		IndentedStringBuildable r = new IndentedStringBuildable(4);
		RootAssister.INSTANCE.toLegibleString(o, r, null, maxlength);
		return r.toString();
	}

	public static StringBuilder diff(ValuedSchema l, ValuedSchema r, StringBuilder sb) {
		Set<String> ls = CH.s(l.askParams());
		Set<String> rs = CH.s(r.askParams());
		BasicTable table = new BasicTable(new Class[] { String.class, String.class, String.class }, new String[] { "Cond.", l.askOriginalType().getSimpleName(),
				r.askOriginalType().getSimpleName() });
		for (String s : CH.comm(ls, rs, true, false, false))
			table.getRows().addRow("<<", l.askClass(s).getName() + " " + s, "");
		for (String s : CH.comm(ls, rs, false, true, false))
			table.getRows().addRow(">>", "", r.askClass(s).getName() + " " + s);
		for (String s : CH.comm(ls, rs, false, false, true))
			if (l.askClass(s) != r.askClass(s))
				table.getRows().addRow("!=", l.askClass(s).getName() + " " + s, r.askClass(s).getName() + " " + s);
		for (String s : CH.comm(ls, rs, false, false, true))
			if (l.askClass(s) == r.askClass(s))
				table.getRows().addRow("==", l.askClass(s).getName() + " " + s, r.askClass(s).getName() + " " + s);
		TableHelper.toString(table, "", TableHelper.SHOW_ALL, sb);
		return sb;
	}

	public static <T extends Valued> void copyFields(T source, T dest) {
		for (ValuedParam vp : dest.askSchema().askValuedParams())
			vp.copy(source, dest);
	}

	public static <T extends PartialMessage> void copyPartialFields(T source, T dest) {
		for (ValuedParam vp : source.askExistingValuedParams())
			vp.copy(source, dest);
	}

	public static Map<String, Object> toMap(Valued valued, boolean storeNulls, String keyForClassNameOrNull) {
		return (Map<String, Object>) RootAssister.INSTANCE.toMapList(valued, storeNulls, keyForClassNameOrNull);
	}

	public static <T extends Valued> ValuedSchema<T> getSchema(T obj) {
		return (ValuedSchema<T>) obj.askSchema();
	}

	public static boolean eq(Valued l, Valued r) {
		return ValuedHasher.INSTANCE.areEqual(l, r);
	}

	public static <T extends Message> ArrayList<T> cloneListEntries(Collection<T> list) {
		if (list == null)
			return null;
		ArrayList<T> r = new ArrayList<T>(list.size());
		for (T t : list)
			r.add((T) t.clone());
		return r;
	}
	public static <K, T extends Message> Map<K, T> cloneMapEntries(Map<K, T> map) {
		if (map == null)
			return null;
		HashMap<K, T> r = new HashMap<K, T>(map);
		for (Entry<K, T> e : r.entrySet())
			e.setValue(cloneShallow(e.getValue()));
		return r;
	}

	static public <T extends Message> T cloneShallow(T m) {
		return m == null ? null : (T) m.clone();
	}
	public static void sort(List<? extends Valued> list, byte pid) {
		if (list.size() > 1) {
			ValuedParam<Valued> vp = list.get(0).askSchema().askValuedParam(pid);
			sort(list, vp);
		}
	}
	public static void sort(List<? extends Valued> list, String name) {
		if (list.size() > 1) {
			ValuedParam<Valued> vp = list.get(0).askSchema().askValuedParam(name);
			sort(list, vp);
		}
	}

	public static void sort(List<? extends Valued> list, ValuedParam<Valued> vp) {
		Collections.sort(list, new ValuedParamComparator(vp));
	}

	public static <V extends Valued> ValuedParam<V>[] getValuedParams(V valued) {
		return (ValuedParam<V>[]) valued.askSchema().askValuedParams();
	}

	public static <T extends Message> T cloneIfUnlocked(T value) {
		if (value instanceof Lockable && ((Lockable) value).isLocked())
			return value;
		return cloneShallow(value);
	}

	public void lockAll(Iterable<? extends Lockable> values) {
		for (Lockable v : values)
			v.lock();
	}

	public static Class getClass(Valued v) {
		if (v == null)
			return null;
		return v.askSchema().askOriginalType();
	}
	public static String getSimpleClassName(Valued action) {
		return OH.getSimpleName(getClass(action));
	}
	public static String getClassName(Valued action) {
		return OH.getName(getClass(action));
	}
}
