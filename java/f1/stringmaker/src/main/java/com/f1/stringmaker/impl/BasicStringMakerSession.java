package com.f1.stringmaker.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import com.f1.base.Valued;
import com.f1.stringmaker.StringMaker;
import com.f1.stringmaker.StringMakerSession;
import com.f1.utils.CH;
import com.f1.utils.EmptyIterator;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.structs.ArrayIterator;

public class BasicStringMakerSession implements StringMakerSession {

	final private StringBuilder sink;
	final private Map<String, StringMaker> stringMakers = new HashMap<String, StringMaker>();
	final private Map<String, Stack<Object>> objectsStack = new HashMap<String, Stack<Object>>();
	private Map<String, Object> objects;
	private boolean supportDotsInVarNames;

	public BasicStringMakerSession(Map<String, Object> objects) {
		sink = new StringBuilder();
		this.objects = objects;
	}

	@Override
	public StringBuilder getSink() {
		return sink;
	}

	@Override
	public Object dereference(Object source, String key) {
		if (source == null)
			return null;
		if (source instanceof Map)
			return ((Map) source).get(key);
		if (source instanceof Valued)
			return ((Valued) source).ask(key);
		try {
			return RH.invokeMethod(source, "get" + SH.uppercaseFirstChar(key), OH.EMPTY_OBJECT_ARRAY);
		} catch (RuntimeException e) {
			try {
				return RH.invokeMethod(source, "is" + SH.uppercaseFirstChar(key), OH.EMPTY_OBJECT_ARRAY);
			} catch (RuntimeException e2) {
				throw e;
			}
		}
	}

	@Override
	public void append(Object obj) {
		if (obj != null)
			sink.append(obj);
	}

	@Override
	public Iterator<?> toIterator(Object object) {
		if (object == null)
			return EmptyIterator.INSTANCE;
		else if (object instanceof Iterable)
			return ((Iterable<?>) object).iterator();
		else if (object.getClass().isArray())
			return new ArrayIterator((Object[]) object);
		else
			throw new StringMakerException("can not iterate over target").setWithType("target", object);
	}

	@Override
	public int toLength(Object object) {
		if (object == null)
			return 0;
		else if (object instanceof Iterable)
			return Math.max(CH.size(((Iterable<?>) object)), 0);
		else if (object.getClass().isArray())
			return ((Object[]) object).length;
		else
			throw new StringMakerException("can not iterate over target").setWithType("target", object);
	}

	@Override
	public StringMaker getStringMaker(String name) {
		return CH.getOrThrow(stringMakers, name, "string maker not found: ");
	}

	@Override
	public boolean toBoolean(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof Boolean)
			return ((Boolean) obj).booleanValue();
		return !obj.toString().isEmpty();
	}

	@Override
	public Object getValue(String key) {
		return objects.get(key);
	}

	@Override
	public Object getValue(String[] keyPath, String origKey) {
		if (supportDotsInVarNames && objects.containsKey(origKey))
			return objects.get(origKey);

		Object r = objects.get(keyPath[0]);
		for (int i = 1; i < keyPath.length && r != null; i++)
			r = dereference(r, keyPath[i]);
		return r;

	}

	@Override
	public void pushValue(String key, Object obj) {
		Object existing = objects.put(key, obj);
		if (existing != null) {
			Stack<Object> stack = objectsStack.get(key);
			if (stack == null)
				objectsStack.put(key, stack = new Stack<Object>());
			stack.push(obj);
		}
	}

	@Override
	public Object popValue(String key) {
		Stack<Object> stack = objectsStack.get(key);
		if (stack != null && !stack.isEmpty())
			return objects.put(key, stack.pop());
		else
			return objects.get(key);
	}

	public void reset(Map<String, Object> vars) {
		this.objects = vars;
		this.sink.setLength(0);
		this.objectsStack.clear();
	}

	@Override
	public boolean containsKey(String key) {
		return objects.containsKey(key);
	}
	public boolean getSupportDotsInVarNames() {
		return supportDotsInVarNames;
	}

	public void setSupportDotsInVarNames(boolean supportDotsInVarNames) {
		this.supportDotsInVarNames = supportDotsInVarNames;
	}

}
