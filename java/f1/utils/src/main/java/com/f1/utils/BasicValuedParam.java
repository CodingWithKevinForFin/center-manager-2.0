package com.f1.utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.base.Caster;
import com.f1.base.StringBuildable;
import com.f1.base.Valued;
import com.f1.base.ValuedParam;

public class BasicValuedParam<V extends Valued> implements ValuedParam<V> {

	final private String name;
	final private Class<?> returnType;
	final private Caster<?> caster;
	final private byte transience;
	final private byte id;
	final private int position;
	final private byte basicType;

	public BasicValuedParam(String name, Class<?> returnType, byte transience, byte id, int position) {
		this.name = name;
		this.returnType = returnType;
		this.caster = OH.getCaster(returnType);
		this.transience = transience;
		this.id = id;
		this.position = position;
		this.basicType = BasicTypeHelper.toType(this.returnType);
	}

	public BasicValuedParam(ValuedParam param) {
		this(param.getName(), param.getReturnType(), param.getTransience(), param.getPid(), param.askPosition());
	}

	@Override
	public byte getTransience() {
		return transience;
	}

	@Override
	public boolean isPrimitive() {
		return returnType.isPrimitive();
	}

	@Override
	public boolean isBoxed() {
		return OH.isBoxed(returnType);
	}

	@Override
	public boolean isPrimitiveOrBoxed() {
		return false;
	}

	@Override
	public boolean isValued() {
		return Valued.class.isAssignableFrom(returnType);
	}

	@Override
	public byte getPid() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getValue(V valued) {
		return valued.ask(name);
	}

	@Override
	public void setValue(V valued, Object value) {
		valued.put(name, value);
	}

	@Override
	public void copy(V source, V dest) {
		setValue(dest, getValue(source));
	}

	@Override
	public Class<?> getReturnType() {
		return returnType;
	}

	@Override
	public Caster<?> getCaster() {
		return caster;
	}

	@Override
	public boolean isImmutable() {
		return OH.isImmutableClass(returnType);
	}

	@Override
	public int askPosition() {
		return position;
	}

	@Override
	public void append(V o, StringBuilder sb) {
		sb.append(o);
	}

	@Override
	public void append(V o, StringBuildable sb) {
		sb.append(o);
	}

	@Override
	public boolean getBoolean(V valued) {
		return (Boolean) getValue(valued);
	}

	@Override
	public void setBoolean(V valued, boolean value) {
		setValue(valued, value);
	}

	@Override
	public byte getByte(V valued) {
		return (Byte) getValue(valued);
	}

	@Override
	public void setByte(V valued, byte value) {
		setValue(valued, value);
	}

	@Override
	public char getChar(V valued) {
		return (Character) getValue(valued);
	}

	@Override
	public void setChar(V valued, char value) {
		setValue(valued, value);
	}

	@Override
	public short getShort(V valued) {
		return (Short) getValue(valued);
	}

	@Override
	public void setShort(V valued, short value) {
		setValue(valued, value);
	}

	@Override
	public int getInt(V valued) {
		return (Integer) getValue(valued);
	}

	@Override
	public void setInt(V valued, int value) {
		setValue(valued, value);
	}

	@Override
	public long getLong(V valued) {
		return (Long) getValue(valued);
	}

	@Override
	public void setLong(V valued, long value) {
		setValue(valued, value);
	}

	@Override
	public double getDouble(V valued) {
		return (Double) getValue(valued);
	}

	@Override
	public void setDouble(V valued, double value) {
		setValue(valued, value);
	}

	@Override
	public float getFloat(V valued) {
		return (Float) getValue(valued);
	}

	@Override
	public void setFloat(V valued, float value) {
		setValue(valued, value);
	}

	@Override
	public String toString() {
		return OH.getSimpleName(returnType) + " " + name;
	}

	@Override
	public void clear(V valued) {
		setValue(valued, OH.getDefaultValue(returnType));
	}

	@Override
	public boolean areEqual(V valued1, V valued2) {
		return OH.eq(getValue(valued1), getValue(valued2));
	}

	@Override
	public byte getBasicType() {
		return basicType;
	}

	@Override
	public void read(V valued, DataInput stream) throws IOException {
		switch (basicType) {
			case BasicTypes.PRIMITIVE_BOOLEAN:
				setBoolean(valued, stream.readBoolean());
				return;
			case BasicTypes.PRIMITIVE_CHAR:
				setChar(valued, stream.readChar());
				return;
			case BasicTypes.PRIMITIVE_BYTE:
				setByte(valued, stream.readByte());
				return;
			case BasicTypes.PRIMITIVE_SHORT:
				setShort(valued, stream.readShort());
				return;
			case BasicTypes.PRIMITIVE_INT:
				setInt(valued, stream.readInt());
				return;
			case BasicTypes.PRIMITIVE_LONG:
				setLong(valued, stream.readLong());
				return;
			case BasicTypes.PRIMITIVE_FLOAT:
				setFloat(valued, stream.readFloat());
				return;
			case BasicTypes.PRIMITIVE_DOUBLE:
				setDouble(valued, stream.readDouble());
				return;
			default:
				throw new IOException("Can not read type from stream: " + BasicTypeHelper.toString(basicType));
		}
	}
	@Override
	public void write(V valued, DataOutput stream) throws IOException {
		switch (basicType) {
			case BasicTypes.PRIMITIVE_BOOLEAN:
				stream.writeBoolean(getBoolean(valued));
				return;
			case BasicTypes.PRIMITIVE_CHAR:
				stream.writeChar(getChar(valued));
				return;
			case BasicTypes.PRIMITIVE_BYTE:
				stream.writeByte(getByte(valued));
				return;
			case BasicTypes.PRIMITIVE_SHORT:
				stream.writeShort(getShort(valued));
				return;
			case BasicTypes.PRIMITIVE_INT:
				stream.writeInt(getInt(valued));
				return;
			case BasicTypes.PRIMITIVE_LONG:
				stream.writeLong(getLong(valued));
				return;
			case BasicTypes.PRIMITIVE_FLOAT:
				stream.writeFloat(getFloat(valued));
				return;
			case BasicTypes.PRIMITIVE_DOUBLE:
				stream.writeDouble(getDouble(valued));
				return;
			default:
				throw new IOException("Can not write type to stream: " + BasicTypeHelper.toString(basicType));
		}
	}

}
