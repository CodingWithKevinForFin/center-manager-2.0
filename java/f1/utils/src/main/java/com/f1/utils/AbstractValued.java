package com.f1.utils;

import com.f1.base.Valued;
import com.f1.base.ValuedSchema;

public abstract class AbstractValued implements Valued {

	@Override
	abstract public ValuedSchema<Valued> askSchema();

	@Override
	abstract public boolean putNoThrow(String name, Object value);

	@Override
	abstract public Object ask(String name);

	@Override
	abstract public boolean putNoThrow(byte pid, Object value);

	@Override
	abstract public Object ask(byte pid);

	@Override
	public Class<Valued> askType() {
		return (Class) askSchema().askOriginalType();
	}

	@Override
	public Valued nw() {
		return nw(OH.EMPTY_OBJECT_ARRAY);
	}

	@Override
	public Valued nw(Object[] args) {
		return nwCast(OH.EMPTY_CLASS_ARRAY, OH.EMPTY_OBJECT_ARRAY);
	}

	@Override
	public Valued nwCast(Class[] types, Object[] args) {
		return RH.invokeConstructor(getClass(), args);
	}

	@Override
	public StringBuilder toString(StringBuilder sb) {
		return sb.append(toString());
	}

	@Override
	public void put(String name, Object value) {
		if (!putNoThrow(name, value))
			throw new RuntimeException("invalid key: " + name);
	}

	@Override
	public void put(byte pid, Object value) {
		if (!putNoThrow(pid, value))
			throw new RuntimeException("invalid pid: " + pid);
	}

	@Override
	public boolean askBoolean(byte pid) {
		return (Boolean) ask(pid);
	}

	@Override
	public byte askByte(byte pid) {
		return (Byte) ask(pid);
	}

	@Override
	public short askShort(byte pid) {
		return (Short) ask(pid);
	}

	@Override
	public char askChar(byte pid) {
		return (Character) ask(pid);
	}

	@Override
	public int askInt(byte pid) {
		return (Integer) ask(pid);
	}

	@Override
	public float askFloat(byte pid) {
		return (Float) ask(pid);
	}

	@Override
	public long askLong(byte pid) {
		return (Long) ask(pid);
	}

	@Override
	public double askDouble(byte pid) {
		return (Double) ask(pid);
	}

	@Override
	public void putBoolean(byte pid, boolean value) {
		put(pid, value);
	}

	@Override
	public void putByte(byte pid, byte value) {
		put(pid, value);
	}

	@Override
	public void putShort(byte pid, short value) {
		put(pid, value);
	}

	@Override
	public void putChar(byte pid, char value) {
		put(pid, value);
	}

	@Override
	public void putInt(byte pid, int value) {
		put(pid, value);
	}

	@Override
	public void putFloat(byte pid, float value) {
		put(pid, value);
	}

	@Override
	public void putLong(byte pid, long value) {
		put(pid, value);
	}

	@Override
	public void putDouble(byte pid, double value) {
		put(pid, value);
	}

	@Override
	public Valued clone() {
		try {
			return (Valued) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}

