/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * 
 * A class that is defined via interface and then autocoded (by inspecing the interface). Each getter/setter pair is considered a "param".
 * <P>
 * An analogy to reflections would be: "Classes declare Methods and have object instances" is like "ValuedSchemas declare ValueParams and have Valued instances"
 * <P>
 * From an instance persepective, valued objects can also be thought of as Maps where each key has a predefined type, ex: <i>{"quantity" -> int, "name" -> String, "px" -> Double}
 * </i> much like a c style struct.
 * 
 * @see ValuedSchema
 * @see ValuedParam
 * 
 */
public interface Valued extends ObjectGeneratorForClass<Valued>, ToStringable, Cloneable {

	/**
	 * total number of supported params
	 */
	int MAX_PARAMS_COUNT = 255;

	byte NO_PID = -1;

	/**
	 * apply a value for a field by name
	 * 
	 * @param name
	 *            name of the field (first char should be lowercase)
	 * @param value
	 *            the value
	 * @throws RuntimeException
	 *             if the name is not supported for this type
	 */
	void put(String name, Object value);

	/**
	 * apply a value for a field by name
	 * 
	 * @param name
	 *            name of the field (first char should be lowercase)
	 * @param value
	 *            the value
	 * @throws RuntimeException
	 *             if the name is not supported for this type
	 * @return true iff it succeeded
	 */
	boolean putNoThrow(String name, Object value);

	/**
	 * get the value for a particular field
	 * 
	 * @param name
	 *            name of the field
	 * @return value
	 */
	Object ask(String name);

	/**
	 * apply a value for a field by pid. see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field (first char should be lowercase)
	 * @param value
	 *            the value
	 * @throws RuntimeException
	 *             if the name is not supported for this type
	 */
	void put(byte pid, Object value);

	/**
	 * apply a value for a field by pid. see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field (first char should be lowercase)
	 * @param value
	 *            the value
	 * @return true iff succeeded
	 */
	boolean putNoThrow(byte pid, Object value);

	/**
	 * get the value for a particular field.see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field
	 * @return value
	 */
	Object ask(byte pid);

	/**
	 * get the value for a particular field, field must be a boolean type.see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field
	 * @return value
	 */
	boolean askBoolean(byte pid);

	/**
	 * get the value for a particular field, field must be a byte type.see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field
	 * @return value
	 */
	byte askByte(byte pid);

	/**
	 * get the value for a particular field, field must be a short type.see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field
	 * @return value
	 */
	short askShort(byte pid);

	/**
	 * get the value for a particular field, field must be a char type.see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field
	 * @return value
	 */
	char askChar(byte pid);

	/**
	 * get the value for a particular field, field must be a int type.see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field
	 * @return value
	 */
	int askInt(byte pid);

	/**
	 * get the value for a particular field, field must be a float type.see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field
	 * @return value
	 */
	float askFloat(byte pid);

	/**
	 * get the value for a particular field, field must be a long type.see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field
	 * @return value
	 */
	long askLong(byte pid);

	/**
	 * get the value for a particular field, field must be a double type.see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field
	 * @return value
	 */
	double askDouble(byte pid);

	/**
	 * apply a value for a field by pid. field must a boolean type. see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field (first char should be lowercase)
	 * @param value
	 *            the value
	 * @throws RuntimeException
	 *             if the name is not supported for this type
	 */
	void putBoolean(byte pid, boolean value);

	/**
	 * apply a value for a field by pid. field must a byte type. see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field (first char should be lowercase)
	 * @param value
	 *            the value
	 * @throws RuntimeException
	 *             if the name is not supported for this type
	 */
	void putByte(byte pid, byte value);

	/**
	 * apply a value for a field by pid. field must a short type. see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field (first char should be lowercase)
	 * @param value
	 *            the value
	 * @throws RuntimeException
	 *             if the name is not supported for this type
	 */
	void putShort(byte pid, short value);

	/**
	 * apply a value for a field by pid. field must a char type. see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field (first char should be lowercase)
	 * @param value
	 *            the value
	 * @throws RuntimeException
	 *             if the name is not supported for this type
	 */
	void putChar(byte pid, char value);

	/**
	 * apply a value for a field by pid. field must a int type. see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field (first char should be lowercase)
	 * @param value
	 *            the value
	 * @throws RuntimeException
	 *             if the name is not supported for this type
	 */
	void putInt(byte pid, int value);

	/**
	 * apply a value for a field by pid. field must a float type. see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field (first char should be lowercase)
	 * @param value
	 *            the value
	 * @throws RuntimeException
	 *             if the name is not supported for this type
	 */
	void putFloat(byte pid, float value);

	/**
	 * apply a value for a field by pid. field must a long type. see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field (first char should be lowercase)
	 * @param value
	 *            the value
	 * @throws RuntimeException
	 *             if the name is not supported for this type
	 */
	void putLong(byte pid, long value);

	/**
	 * apply a value for a field by pid. field must a double type. see {@link PID}
	 * 
	 * @param pid
	 *            pid of the field (first char should be lowercase)
	 * @param value
	 *            the value
	 * @throws RuntimeException
	 *             if the name is not supported for this type
	 */
	void putDouble(byte pid, double value);

	/**
	 * @return get the schema for this class type
	 */
	ValuedSchema<Valued> askSchema();

	/**
	 * get value by position.
	 * 
	 * @param position
	 *            the position of the field. See {@link ValuedSchema#askPosition(String)}
	 * @return the value at that position
	 */
	Object askAtPosition(int position);
}
