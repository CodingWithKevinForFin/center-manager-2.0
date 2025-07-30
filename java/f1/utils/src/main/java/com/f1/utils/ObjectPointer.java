package com.f1.utils;

import com.f1.base.BasicTypes;
import com.f1.base.Pointer;

public class ObjectPointer implements Pointer<Object>, Cloneable {

	private static final byte TYPE_OBJECT = BasicTypes.OBJECT;
	private static final byte TYPE_BYTE = BasicTypes.PRIMITIVE_BYTE;
	private static final byte TYPE_SHORT = BasicTypes.PRIMITIVE_SHORT;
	private static final byte TYPE_INT = BasicTypes.PRIMITIVE_INT;
	private static final byte TYPE_LONG = BasicTypes.PRIMITIVE_LONG;
	private static final byte TYPE_FLOAT = BasicTypes.PRIMITIVE_FLOAT;
	private static final byte TYPE_DOUBLE = BasicTypes.PRIMITIVE_DOUBLE;
	private static final byte TYPE_BOOLEAN = BasicTypes.PRIMITIVE_BOOLEAN;
	private static final byte TYPE_CHAR = BasicTypes.PRIMITIVE_CHAR;
	private Object value;
	private long primitiveValue;
	private byte type;

	public byte getType() {
		return type;
	}

	@Override
	public Object get() {
		switch (type) {
			case TYPE_OBJECT :
				return value;
			case TYPE_BYTE :
				return getByte();
			case TYPE_SHORT :
				return getShort();
			case TYPE_INT :
				return getInt();
			case TYPE_LONG :
				return getLong();
			case TYPE_FLOAT :
				return getFloat();
			case TYPE_DOUBLE :
				return getDouble();
			case TYPE_BOOLEAN :
				return getBoolean();
			case TYPE_CHAR :
				return getChar();
			default :
				throw new RuntimeException("");
		}
	}

	@Override
	public Object put(Object value) {
		Object old = value;
		this.value = value;
		this.type = TYPE_OBJECT;
		return old;
	}

	public void putByte(byte value) {
		this.value = null;
		this.type = TYPE_BYTE;
		this.primitiveValue = value;
	}

	public void putShort(short value) {
		this.value = null;
		this.type = TYPE_SHORT;
		this.primitiveValue = value;
	}

	public void putInt(int value) {
		this.value = null;
		this.type = TYPE_INT;
		this.primitiveValue = value;
	}

	public void putLong(long value) {
		this.primitiveValue = value;
	}

	public void putFloat(float value) {
		this.value = null;
		this.type = TYPE_FLOAT;
		this.primitiveValue = Float.floatToRawIntBits(value);
	}

	public void putDouble(double value) {
		this.value = null;
		this.type = TYPE_DOUBLE;
		this.primitiveValue = Double.doubleToRawLongBits(value);
	}

	public void putChar(char value) {
		this.value = null;
		this.type = TYPE_CHAR;
		this.primitiveValue = value;
	}

	public void putBoolean(boolean value) {
		this.value = null;
		this.type = TYPE_BOOLEAN;
		this.primitiveValue = value ? 1L : 0L;
	}

	public byte getByte() {
		if (this.type != TYPE_BYTE)
			throw new ClassCastException();
		return (byte) this.primitiveValue;
	}

	public short getShort() {
		if (this.type != TYPE_SHORT)
			throw new ClassCastException();
		return (short) this.primitiveValue;
	}

	public int getInt() {
		if (this.type != TYPE_INT)
			throw new ClassCastException();
		return (int) this.primitiveValue;
	}

	public long getLong() {
		return this.primitiveValue;
	}

	public float getFloat() {
		if (this.type != TYPE_FLOAT)
			throw new ClassCastException();
		return Float.intBitsToFloat((int) this.primitiveValue);
	}

	public double getDouble() {
		if (this.type != TYPE_DOUBLE)
			throw new ClassCastException();
		return Double.longBitsToDouble(this.primitiveValue);
	}

	public char getChar() {
		if (this.type != TYPE_CHAR)
			throw new ClassCastException();
		return (char) this.primitiveValue;
	}

	public boolean getBoolean() {
		if (this.type != TYPE_BOOLEAN)
			throw new ClassCastException();
		return this.primitiveValue == 1L;
	}

	@Override
	public ObjectPointer clone() {
		try {
			return (ObjectPointer) super.clone();
		} catch (CloneNotSupportedException e) {
			throw OH.toRuntime(e);
		}
	}

	public void copyTo(ObjectPointer dest) {
		dest.primitiveValue = this.primitiveValue;
		dest.type = this.type;
		dest.value = this.value;
	}

}
