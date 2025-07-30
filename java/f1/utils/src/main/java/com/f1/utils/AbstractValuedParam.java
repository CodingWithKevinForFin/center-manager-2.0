/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import com.f1.base.Valued;
import com.f1.base.ValuedParam;

public abstract class AbstractValuedParam<V extends Valued> implements ValuedParam<V> {

	@Override
	public boolean getBoolean(V valued) {
		throw new ClassCastException(getName() + ": " + valued);
	}

	@Override
	public void setBoolean(V valued, boolean value) {
		throw new ClassCastException(getName() + ": " + valued);

	}

	@Override
	public byte getByte(V valued) {
		throw new ClassCastException(getName() + ": " + valued);
	}

	@Override
	public void setByte(V valued, byte value) {
		throw new ClassCastException(getName() + ": " + valued);
	}

	@Override
	public char getChar(V valued) {
		throw new ClassCastException(getName() + ": " + valued);
	}

	@Override
	public void setChar(V valued, char value) {
		throw new ClassCastException(getName() + ": " + valued);
	}

	@Override
	public short getShort(V valued) {
		throw new ClassCastException(getName() + ": " + valued);
	}

	@Override
	public void setShort(V valued, short value) {
		throw new ClassCastException(getName() + ": " + valued);
	}

	@Override
	public int getInt(V valued) {
		throw new ClassCastException(getName() + ": " + valued);
	}

	@Override
	public void setInt(V valued, int value) {
		throw new ClassCastException(getName() + ": " + valued);
	}

	@Override
	public long getLong(V valued) {
		throw new ClassCastException(getName() + ": " + valued);
	}

	@Override
	public void setLong(V valued, long value) {
		throw new ClassCastException(getName() + ": " + valued);
	}

	@Override
	public double getDouble(V valued) {
		throw new ClassCastException(getName() + ": " + valued);
	}

	@Override
	public void setDouble(V valued, double value) {
		throw new ClassCastException(getName() + ": " + valued);
	}

	@Override
	public float getFloat(V valued) {
		throw new ClassCastException(getName() + ": " + valued);
	}

	@Override
	public void setFloat(V valued, float value) {
		throw new ClassCastException(getName() + ": " + valued);
	}

}
