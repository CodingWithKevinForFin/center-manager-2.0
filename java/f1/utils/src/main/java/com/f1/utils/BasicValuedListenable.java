/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.ArrayList;
import java.util.List;

import com.f1.base.Valued;
import com.f1.base.ValuedListenable;
import com.f1.base.ValuedListener;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;
import com.f1.utils.structs.ByteKeyMap;
import com.f1.utils.structs.ByteKeyMap.Node;

public class BasicValuedListenable<V extends Valued> extends BasicValuedWrapper<V> implements ValuedListenable {
	private ByteKeyMap<List<ValuedListener>> listeners;
	private List<ValuedListener> listeners2;
	private boolean askSupportsPids;
	private ValuedSchema<V> schema;

	public BasicValuedListenable(Class<V> innerType) {
		super(innerType);
		this.listeners = new ByteKeyMap<List<ValuedListener>>();
	}

	@Override
	public void init(V v) {
		super.init(v);
		schema = (ValuedSchema<V>) v.askSchema();
		askSupportsPids = schema.askSupportsPids();
	}

	@Override
	public void put(String name, Object value) {
		List<ValuedListener> l = getListeners(name);
		if (l != null || listeners2 != null) {
			ValuedParam<V> vp = schema.askValuedParam(name);
			final Object old = vp.getValue(inner);
			vp.setValue(inner, value);
			if (listeners2 != null)
				for (ValuedListener listener : listeners2)
					listener.onValued(this, name, vp.getPid(), old, value);
			if (listeners2 != null)
				for (ValuedListener listener : listeners2)
					listener.onValued(this, name, vp.getPid(), old, value);
		} else
			inner.put(name, value);
	}

	@Override
	public boolean putNoThrow(String name, Object value) {
		if (!schema.askParamValid(name))
			return false;
		put(name, value);
		return true;
	}

	@Override
	public void put(byte pid, Object value) {
		List<ValuedListener> l = getListeners(pid);
		if (l != null || listeners2 != null) {
			ValuedParam<V> vp = schema.askValuedParam(pid);
			final Object old = vp.getValue(inner);
			vp.setValue(inner, value);
			if (l != null)
				for (ValuedListener listener : l)
					listener.onValued(this, vp.getName(), pid, old, value);
			if (listeners2 != null)
				for (ValuedListener listener : listeners2)
					listener.onValued(this, vp.getName(), pid, old, value);
		} else
			inner.put(pid, value);
	}

	@Override
	public boolean putNoThrow(byte pid, Object value) {
		if (!schema.askPidValid(pid))
			return false;
		put(pid, value);
		return true;
	}

	@Override
	public void putBoolean(byte pid, boolean value) {
		List<ValuedListener> l = getListeners(pid);
		if (l != null || listeners2 != null) {
			ValuedParam<V> vp = schema.askValuedParam(pid);
			final boolean old = vp.getBoolean(inner);
			vp.setBoolean(inner, value);
			if (l != null)
				for (ValuedListener listener : l)
					listener.onValuedBoolean(this, vp.getName(), pid, old, value);
			if (listeners2 != null)
				for (ValuedListener listener : listeners2)
					listener.onValuedBoolean(this, vp.getName(), pid, old, value);
		} else
			inner.putBoolean(pid, value);
	}

	@Override
	public void putByte(byte pid, byte value) {
		List<ValuedListener> l = getListeners(pid);
		if (l != null || listeners2 != null) {
			ValuedParam<V> vp = schema.askValuedParam(pid);
			final byte old = vp.getByte(inner);
			vp.setByte(inner, value);
			if (l != null)
				for (ValuedListener listener : l)
					listener.onValuedByte(this, vp.getName(), pid, old, value);
			if (listeners2 != null)
				for (ValuedListener listener : listeners2)
					listener.onValuedByte(this, vp.getName(), pid, old, value);
		} else
			inner.putByte(pid, value);
	}

	@Override
	public void putChar(byte pid, char value) {
		List<ValuedListener> l = getListeners(pid);
		if (l != null || listeners2 != null) {
			ValuedParam<V> vp = schema.askValuedParam(pid);
			final Character old = vp.getChar(inner);
			vp.setChar(inner, value);
			if (l != null)
				for (ValuedListener listener : l)
					listener.onValuedChar(this, vp.getName(), pid, old, value);
			if (listeners2 != null)
				for (ValuedListener listener : listeners2)
					listener.onValuedChar(this, vp.getName(), pid, old, value);
		} else
			inner.putChar(pid, value);
	}

	@Override
	public void putShort(byte pid, short value) {
		List<ValuedListener> l = getListeners(pid);
		if (l != null || listeners2 != null) {
			ValuedParam<V> vp = schema.askValuedParam(pid);
			final Short old = vp.getShort(inner);
			vp.setShort(inner, value);
			if (l != null)
				for (ValuedListener listener : l)
					listener.onValuedShort(this, vp.getName(), pid, old, value);
			if (listeners2 != null)
				for (ValuedListener listener : listeners2)
					listener.onValuedShort(this, vp.getName(), pid, old, value);
		} else
			inner.putShort(pid, value);
	}

	@Override
	public void putInt(byte pid, int value) {
		List<ValuedListener> l = getListeners(pid);
		if (l != null || listeners2 != null) {
			ValuedParam<V> vp = schema.askValuedParam(pid);
			final Integer old = vp.getInt(inner);
			vp.setInt(inner, value);
			if (l != null)
				for (ValuedListener listener : l)
					listener.onValuedInt(this, vp.getName(), pid, old, value);
			if (listeners2 != null)
				for (ValuedListener listener : listeners2)
					listener.onValuedInt(this, vp.getName(), pid, old, value);
		} else
			inner.putInt(pid, value);
	}

	@Override
	public void putLong(byte pid, long value) {
		List<ValuedListener> l = getListeners(pid);
		if (l != null || listeners2 != null) {
			ValuedParam<V> vp = schema.askValuedParam(pid);
			final Long old = vp.getLong(inner);
			vp.setLong(inner, value);
			if (l != null)
				for (ValuedListener listener : l)
					listener.onValuedLong(this, vp.getName(), pid, old, value);
			if (listeners2 != null)
				for (ValuedListener listener : listeners2)
					listener.onValuedLong(this, vp.getName(), pid, old, value);
		} else
			inner.putLong(pid, value);
	}

	@Override
	public void putFloat(byte pid, float value) {
		List<ValuedListener> l = getListeners(pid);
		if (l != null || listeners2 != null) {
			ValuedParam<V> vp = schema.askValuedParam(pid);
			final Float old = vp.getFloat(inner);
			vp.setFloat(inner, value);
			if (l != null)
				for (ValuedListener listener : l)
					listener.onValuedFloat(this, vp.getName(), pid, old, value);
			if (listeners2 != null)
				for (ValuedListener listener : listeners2)
					listener.onValuedFloat(this, vp.getName(), pid, old, value);
		} else
			inner.putFloat(pid, value);
	}

	@Override
	public void putDouble(byte pid, double value) {
		List<ValuedListener> l = getListeners(pid);
		if (l != null || listeners2 != null) {
			ValuedParam<V> vp = schema.askValuedParam(pid);
			final Double old = vp.getDouble(inner);
			vp.setDouble(inner, value);
			if (l != null)
				for (ValuedListener listener : l)
					listener.onValuedDouble(this, vp.getName(), pid, old, value);
			if (listeners2 != null)
				for (ValuedListener listener : listeners2)
					listener.onValuedDouble(this, vp.getName(), pid, old, value);
		} else
			inner.putDouble(pid, value);
	}

	@Override
	public void addListener(byte pid, ValuedListener listener) {
		Node<List<ValuedListener>> n = getListenersNode(pid);
		List<ValuedListener> l = n.getValue();
		if (l == null) {
			n.setValue(l = new ArrayList<ValuedListener>(1));
			l.add(listener);
		} else if (!l.contains(listener))
			l.add(listener);
	}

	@Override
	public void removeListener(byte pid, ValuedListener listener) {
		List<ValuedListener> l = getListeners(pid);
		if (l == null)
			return;
		l.remove(listener);
		if (l.size() == 0)
			l.remove(pid);
	}

	final private List<ValuedListener> getListeners(byte pid) {
		return listeners.get(askSupportsPids ? pid : (byte) schema.askPosition(pid));
	}

	final private Node<List<ValuedListener>> getListenersNode(byte pid) {
		return listeners.getNode(askSupportsPids ? pid : (byte) schema.askPosition(pid));
	}

	final private List<ValuedListener> getListeners(String name) {
		return listeners.get(askSupportsPids ? schema.askPid(name) : (byte) schema.askPosition(name));
	}

	final private Node<List<ValuedListener>> getListenersNode(String name) {
		return listeners.getNode(askSupportsPids ? schema.askPid(name) : (byte) schema.askPosition(name));
	}

	@Override
	public void addListener(String field, ValuedListener listener) {
		Node<List<ValuedListener>> n = getListenersNode(field);
		List<ValuedListener> l = n.getValue();
		if (l == null) {
			n.setValue(l = new ArrayList<ValuedListener>(1));
			l.add(listener);
		} else if (!l.contains(listener))
			l.add(listener);
	}

	@Override
	public void removeListener(String field, ValuedListener listener) {
		List<ValuedListener> l = getListeners(field);
		if (l == null)
			return;
		l.remove(listener);
	}

	@Override
	public boolean addListener(ValuedListener listener) {
		if (listeners2 == null)
			listeners2 = new ArrayList<ValuedListener>(1);
		if (this.listeners2.contains(listener))
			return false;
		this.listeners2.add(listener);
		return true;
	}

	@Override
	public boolean removeListener(ValuedListener listener) {
		if (listeners2 == null)
			return false;
		boolean r = this.listeners2.remove(listener);
		if (listeners2.size() == 0)
			this.listeners2 = null;
		return r;
	}

	@Override
	public Iterable<ValuedListener> getValuedListeners() {
		return listeners2 == null ? EmptyCollection.INSTANCE : listeners2; // TODO
																			// :consider
																			// field
																			// level
																			// listeners
	}

	@Override
	public void askChildValuedListenables(List<ValuedListenable> sink) {
		for (ValuedParam p : this.schema.askValuedParams()) {
			if (!p.isImmutable()) {
				Object o = p.getValue(inner);
				if (o instanceof ValuedListenable)
					sink.add((ValuedListenable) o);
			}
		}
	}

}
