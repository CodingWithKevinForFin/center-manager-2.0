package com.f1.base;

/**
 * 
 * An enum which is one-to-one matched such that each enumeration can be associated with a real value, such as a char, byte or string. This is useful for messaging and long-term
 * storage of enums. This is important for messaging and persistence.
 * 
 * @see ValuedEnumCache
 * 
 * @param <T>
 *            the type of id which identifies each value
 */
public interface ValuedEnum<T> {

	/**
	 * @return underlying value uniquely identifying each value within an enum.
	 */
	T getEnumValue();
}

