/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;
import java.io.Serializable;

public interface ByteArrayConverter<TYPE> extends Serializable {

	public void write(TYPE o, ToByteArrayConverterSession session) throws IOException;

	public TYPE read(FromByteArrayConverterSession session) throws IOException;

	/**
	 * When writing (converting to bytes from an object) this value is prefixed before the payload. <BR>
	 * When reading (converting to an object from bytes) this value is used in a lookup table to determine the appropriate converter.
	 * 
	 * @return
	 */
	public byte getBasicType();

	/**
	 * when writing (converting to bytes from an object) this method is visited for objects in need of converting to determine if it this can be used for converting the object.
	 * 
	 * @param o
	 * @return
	 */
	public boolean isCompatible(Class<?> o);
}
