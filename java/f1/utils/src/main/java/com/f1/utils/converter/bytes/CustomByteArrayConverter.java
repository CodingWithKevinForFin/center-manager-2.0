package com.f1.utils.converter.bytes;

import java.io.IOException;

/**
 * Handles the custom marshalling of data to and from a byte stream. Please note that reading of data and writing of data <B>must</B> be in the same order and format.
 * 
 * @author rcooke
 * 
 * @param <TYPE>
 */
public interface CustomByteArrayConverter<TYPE> {

	/**
	 * converts an object to a stream of bytes. See {@link ToByteArrayConverterSession#getStream()}
	 * 
	 * @param o
	 *            the object to convert
	 * @param session
	 *            contains the stream to write to
	 * @throws IOException
	 *             if an error occurs
	 */
	public void write(TYPE o, ToByteArrayConverterSession session) throws IOException;

	/**
	 * Converts a stream of bytes to an object. See {@link FromByteArrayConverterSession#getStream()}
	 * 
	 * @param session
	 *            contains the stream of bytes to read from
	 * @return the newly created object, must not be null!
	 * @throws IOException
	 */
	public TYPE read(FromByteArrayConverterSession session) throws IOException;

	/**
	 * @return The exact class type that this converter can convert
	 */
	public Class<TYPE> getType();

	/**
	 * @return A globally unique id that identifies objects of this type. For example, getType().getName() would work
	 */
	public Object getCustomId();

	/**
	 * @param o
	 *            the class type of the object to potentially convert
	 * @return true if this converter can convert instances of the supplied class type. Otherwise false
	 */
	public boolean isCompatible(Class<?> o);
}
