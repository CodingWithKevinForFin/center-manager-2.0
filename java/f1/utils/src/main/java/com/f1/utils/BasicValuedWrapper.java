/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import com.f1.base.Valued;
import com.f1.base.ValuedSchema;
import com.f1.base.ValuedWrapper;

public class BasicValuedWrapper<V extends Valued> implements ValuedWrapper<V> {

	protected V inner;
	private Class<? extends Valued> innerType;

	public BasicValuedWrapper(Class<V> innerType) {
		this.innerType = innerType;
	}

	@Override
	public V getInner() {
		return inner;
	}

	@Override
	public Class<Valued> askType() {
		return inner.askType();
	}

	@Override
	public Valued nw() {
		return inner.nw();
	}

	@Override
	public Valued nw(Object[] args) {
		return inner.nw(args);
	}

	@Override
	public Valued nwCast(Class[] types, Object[] args) {
		return inner.nwCast(types, args);
	}

	@Override
	public void put(String name, Object value) {
		inner.put(name, value);
	}

	@Override
	public boolean putNoThrow(String name, Object value) {
		return inner.putNoThrow(name, value);
	}

	@Override
	public Object ask(String name) {
		return inner.ask(name);
	}

	@Override
	public void put(byte pid, Object value) {
		inner.put(pid, value);
	}

	@Override
	public boolean putNoThrow(byte pid, Object value) {
		return inner.putNoThrow(pid, value);
	}

	@Override
	public Object ask(byte pid) {
		return inner.ask(pid);
	}

	@Override
	public boolean askBoolean(byte pid) {
		return inner.askBoolean(pid);
	}

	@Override
	public byte askByte(byte pid) {
		return inner.askByte(pid);
	}

	@Override
	public short askShort(byte pid) {
		return inner.askShort(pid);
	}

	@Override
	public char askChar(byte pid) {
		return inner.askChar(pid);
	}

	@Override
	public int askInt(byte pid) {
		return inner.askInt(pid);
	}

	@Override
	public float askFloat(byte pid) {
		return inner.askFloat(pid);
	}

	@Override
	public long askLong(byte pid) {
		return inner.askLong(pid);
	}

	@Override
	public double askDouble(byte pid) {
		return inner.askDouble(pid);
	}

	@Override
	public void putBoolean(byte pid, boolean value) {
		inner.putBoolean(pid, value);
	}

	@Override
	public void putByte(byte pid, byte value) {
		inner.putByte(pid, value);
	}

	@Override
	public void putShort(byte pid, short value) {
		inner.putShort(pid, value);
	}

	@Override
	public void putChar(byte pid, char value) {
		inner.putChar(pid, value);
	}

	@Override
	public void putInt(byte pid, int value) {
		inner.putInt(pid, value);
	}

	@Override
	public void putFloat(byte pid, float value) {
		inner.putFloat(pid, value);
	}

	@Override
	public void putLong(byte pid, long value) {
		inner.putLong(pid, value);
	}

	@Override
	public void putDouble(byte pid, double value) {
		inner.putDouble(pid, value);
	}

	@Override
	public void init(V valued) {
		this.inner = valued;
	}

	@Override
	public ValuedSchema<Valued> askSchema() {
		return inner.askSchema();
	}

	@Override
	public Class<? extends Valued> getInnerType() {
		return innerType;
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		return inner.toString(sb);
	}

	@Override
	public Valued clone() {
		try {
			return (Valued) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object askAtPosition(int name) {
		return inner.askAtPosition(name);
	}

}
