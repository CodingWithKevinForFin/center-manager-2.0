package com.f1.utils;

import com.f1.base.ValuedListenable;
import com.f1.base.ValuedListener;

public abstract class AbstractValuedListener implements ValuedListener {

	@Override
	abstract public void onValued(ValuedListenable target, String name, byte pid, Object old, Object value);

	@Override
	public void onValuedBoolean(ValuedListenable target, String name, byte pid, boolean old, boolean value) {
		onValued(target, name, pid, old, value);
	}

	@Override
	public void onValuedByte(ValuedListenable target, String name, byte pid, byte old, byte value) {
		onValued(target, name, pid, old, value);
	}

	@Override
	public void onValuedChar(ValuedListenable target, String name, byte pid, char old, char value) {
		onValued(target, name, pid, old, value);
	}

	@Override
	public void onValuedShort(ValuedListenable target, String name, byte pid, short old, short value) {
		onValued(target, name, pid, old, value);
	}

	@Override
	public void onValuedInt(ValuedListenable target, String name, byte pid, int old, int value) {
		onValued(target, name, pid, old, value);
	}

	@Override
	public void onValuedLong(ValuedListenable target, String name, byte pid, long old, long value) {
		onValued(target, name, pid, old, value);
	}

	@Override
	public void onValuedFloat(ValuedListenable target, String name, byte pid, float old, float value) {
		onValued(target, name, pid, old, value);
	}

	@Override
	public void onValuedDouble(ValuedListenable target, String name, byte pid, double old, double value) {
		onValued(target, name, pid, old, value);
	}

}

