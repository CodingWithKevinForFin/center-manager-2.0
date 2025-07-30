/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Represents a particular getter/setter on a {@link ValuedSchema}'s {@link Valued} class. As an analogy, think of it like the {@link java.lang.reflect.Method} but specially built
 * for getter/setters
 * 
 * @param <V>
 */
public interface ValuedParam<V extends Valued> {

	/**
	 * @see Transient
	 */
	byte getTransience();

	/**
	 * @return does the getter return one of the 8 java primitives,ex: double.class
	 */
	boolean isPrimitive();

	/**
	 * @return does the getter return one of the 8 java primitives autoboxed versions, ex: Double.class
	 */
	boolean isBoxed();

	/**
	 * 
	 * @return does the getter return one of the 8 java primitives of there autoboxed versions
	 */
	boolean isPrimitiveOrBoxed();

	/**
	 * @return does the getter return a {@link Valued} object (this is useful for nesting.
	 */
	boolean isValued();

	/**
	 * @return the pid that represents this param
	 */
	byte getPid();

	/**
	 * @return the name that represents this param. For example getOrder()/setOrder(...) has a name of "order"
	 */
	String getName();

	/**
	 * @param valued
	 *            the object to visit and get the value that this represents
	 * @return the value represented by the param from the supplied {@link Valued}
	 */
	Object getValue(V valued);

	/**
	 * @param valued
	 *            the object to visit and set the value that this represents
	 * @param value
	 *            the value to set
	 * 
	 */
	void setValue(V valued, Object value);

	/**
	 * 
	 * Copies the value represented by this param from the src to the dest. Note for objects, its just copying the reference to the object (not a clone, etc)
	 * 
	 * @param source
	 *            where to take value from
	 * @param dest
	 *            where to put value to.
	 */
	void copy(V source, V dest);

	Class<?> getReturnType();

	Caster<?> getCaster();

	/**
	 * @return does the getter return a value that can not be mutated
	 */
	boolean isImmutable();

	/**
	 * @return this params position withing the {@link ValuedSchema}. Remember, params are ordered in the schema and can be referenced by position
	 */
	int askPosition();

	/**
	 * Call the toString (or {@link ToStringable#toString(StringBuilder)} of the value represented by this param and append to the supplied string builder
	 * 
	 * @param sb
	 *            sink
	 */
	void append(V o, StringBuilder sb);

	/**
	 * Call the toString (or {@link ToStringable#toString(StringBuilder)} of the value represented by this param and append to the supplied string builder
	 * 
	 * @param sb
	 *            sink
	 */
	void append(V o, StringBuildable sb);

	/**
	 * avoids autoboxing. Will throw Exception if the param does not represent the correct type.
	 */
	boolean getBoolean(V valued);

	/**
	 * avoids autoboxing. Will throw Exception if the param does not represent the correct type.
	 */
	void setBoolean(V valued, boolean value);

	/**
	 * avoids autoboxing. Will throw Exception if the param does not represent the correct type.
	 */
	byte getByte(V valued);

	/**
	 * avoids autoboxing. Will throw Exception if the param does not represent the correct type.
	 */
	void setByte(V valued, byte value);

	/**
	 * avoids autoboxing. Will throw Exception if the param does not represent the correct type.
	 */
	char getChar(V valued);

	/**
	 * avoids autoboxing. Will throw Exception if the param does not represent the correct type.
	 */
	void setChar(V valued, char value);

	/**
	 * avoids autoboxing. Will throw Exception if the param does not represent the correct type.
	 */
	short getShort(V valued);

	/**
	 * avoids autoboxing. Will throw Exception if the param does not represent the correct type.
	 */
	void setShort(V valued, short value);

	/**
	 * avoids autoboxing. Will throw Exception if the param does not represent the correct type.
	 */
	int getInt(V valued);

	/**
	 * avoids autoboxing. Will throw Exception if the param does not represent the correct type.
	 */
	void setInt(V valued, int value);

	/**
	 * avoids autoboxing. Will throw Exception if the param does not represent the correct type.
	 */
	long getLong(V valued);

	/**
	 * avoids autoboxing. Will throw Exception if the param does not represent the correct type.
	 */
	void setLong(V valued, long value);

	/**
	 * avoids autoboxing. Will throw Exception if the param does not represent the correct type.
	 */
	double getDouble(V valued);

	void setDouble(V valued, double value);

	float getFloat(V valued);

	void setFloat(V valued, float value);

	void clear(V valued);

	/**
	 * Are the values represented by this param from the two valued objects the same, as defined by {@link Object#equals(Object)}. For primitives this will be a simple comparison
	 * 
	 * @param valued1
	 *            left
	 * @param valued2
	 *            right
	 * @return true if equal
	 */
	boolean areEqual(V valued1, V valued2);

	/**
	 * 
	 * @return the basic type that the getter would return, see {@link BasicTypes}
	 */
	byte getBasicType();

	/**
	 * Write the value represented by this param from the source to the sink
	 */
	void write(V source, DataOutput sink) throws IOException;
	/**
	 * read the value represented by this param from the source and store to sink
	 */
	void read(V sink, DataInput source) throws IOException;
}
