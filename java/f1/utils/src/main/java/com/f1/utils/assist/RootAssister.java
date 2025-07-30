/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.assist;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.base.Table;
import com.f1.base.Valued;
import com.f1.utils.IndentedStringBuildable;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.BasicToJsonConverterSession;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.Tuple;

public class RootAssister implements Assister<Object>, Lockable {

	public static final RootAssister INSTANCE = new RootAssister();
	static {
		INSTANCE.lock();
	}

	private Assister<Object> nullAssister = new NullAssister();
	private ValuedAssister valuedAssister = new ValuedAssister(this);
	private Assister<Collection<?>> collectionAssister = new CollectionAssister(this);
	private Assister<Tuple> tupleAssister = new TupleAssister(this);
	private Assister<Object[]> arrayAssister = new ArrayAssister(this);
	private Assister<Object> primitiveArrayAssister = new PrimitiveArrayAssister(this);
	private Assister<Object> objectAssister = new ObjectAssister();
	private Assister<Table> tableAssister = new TableAssister(this);
	private Assister<Map<?, ?>> mapAssister = new MapAssister(this);

	private Assister<Object> immutableAssister = new ImmutableAssister();

	private boolean locked;

	public void setValuedAssister(ValuedAssister assister) {
		LockedException.assertNotLocked(this);
		this.valuedAssister = assister;
	}

	@Override
	public Object getNestedValue(Object o, String path, boolean throwOnError) {
		if (path == null) {
			if (throwOnError)
				throw new NullPointerException("key is null");
			return null;
		}
		if (path.indexOf('.') == -1)
			return getAssister(o).getNestedValue(o, path, throwOnError);
		else {
			for (String part : SH.split('.', path)) {
				if (o == null) {
					if (throwOnError)
						throw new NullPointerException("path not found: " + path + " (at " + part + ")");
					return null;
				}
				o = getAssister(o).getNestedValue(o, part, throwOnError);
			}
			return o;
		}
	}

	public String toString(Object o) {
		StringBuilder sb = new StringBuilder();
		toString(o, sb, new IdentityHashMap<Object, Object>());
		return sb.toString();
	}

	@Override
	public void toString(Object o, StringBuilder sb, IdentityHashMap<Object, Object> visited) {
		Assister<Object> assister = getAssister(o);
		if (visited != null && assister != immutableAssister && assister != objectAssister) {
			if (visited.put(o, o) != null)
				sb.append("<circ-ref>");
			else {
				assister.toString(o, sb, visited);
				visited.put(0, null);
			}
		} else
			assister.toString(o, sb, visited);
	}

	public <T> T clone(T o) {
		return (T) clone(o, new IdentityHashMap<Object, Object>());
	}

	@Override
	public Object clone(Object o, IdentityHashMap<Object, Object> visited) {
		Object r = visited.get(o);
		if (r != null)
			return r;
		r = getAssister(o).clone(o, visited);
		visited.put(o, r);
		return r;
	}

	public <T> Assister<T> getAssister(T object) {
		if (object instanceof Map)
			return (Assister<T>) mapAssister;
		if (object == null)
			return (Assister<T>) nullAssister;
		if (object instanceof Valued)
			return (Assister<T>) valuedAssister;
		if (OH.isImmutable(object))
			return (Assister<T>) immutableAssister;
		if (object instanceof Table)
			return (Assister<T>) tableAssister;
		if (object instanceof Collection)
			return (Assister<T>) collectionAssister;
		if (object instanceof Tuple)
			return (Assister<T>) tupleAssister;
		if (object.getClass().isArray()) {
			if (object.getClass().getComponentType().isPrimitive())
				return (Assister<T>) primitiveArrayAssister;
			else
				return (Assister<T>) arrayAssister;
		}
		return (Assister<T>) objectAssister;
	}

	public String toLegibleString(Object o, int maxlength) {
		IndentedStringBuildable sb = new IndentedStringBuildable(4);
		toLegibleString(o, sb, new IdentityHashMap<Object, Object>(), maxlength);
		return sb.toString();
	}

	@Override
	public void toLegibleString(Object o, IndentedStringBuildable sb, IdentityHashMap<Object, Object> visited, int maxlength) {
		if (o != null && visited != null && visited.put(o, o) != null)
			sb.append("<circ-ref>");
		else {
			getAssister(o).toLegibleString(o, sb, visited == null ? new IdentityHashMap<Object, Object>() : new IdentityHashMap<Object, Object>(visited), maxlength);
			if (o != null && visited != null)
				visited.remove(o);
		}
		if (sb.length() >= maxlength)
			sb.append("<truncated after " + maxlength + " chars>");
	}

	@Override
	public void toJson(Object o, StringBuilder sb) {
		ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(o, new BasicToJsonConverterSession(ObjectToJsonConverter.INSTANCE_COMPACT, sb));
	}

	public String toJson(Object o) {
		return ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(o);
	}

	@Override
	public void lock() {
		this.locked = true;
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	@Override
	public Object toMapList(Object o, boolean storeNulls, String keyForClassNameOrNull) {
		return getAssister(o).toMapList(o, storeNulls, keyForClassNameOrNull);
	}

	public ValuedAssister getValuedAssister() {
		return valuedAssister;
	}

}
