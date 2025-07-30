/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.utils.FastDataInput;

public interface FromByteArrayConverterSession {

	public FastDataInput getStream();

	public ObjectToByteArrayConverter getConverter();

	// if the value is zero or positive, then the object needs to be read and
	// stored
	public int handleIfAlreadyConverted() throws IOException;

	public Object get(int i);

	public void store(int i, Object obj);

	public void resetCircRefs(boolean supportCircRefs);
}
