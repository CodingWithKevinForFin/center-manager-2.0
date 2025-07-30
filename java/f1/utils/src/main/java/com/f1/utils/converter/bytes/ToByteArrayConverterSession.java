/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.utils.FastDataOutput;

public interface ToByteArrayConverterSession {

	public FastDataOutput getStream();

	public ObjectToByteArrayConverter getConverter();

	// Returns true if the object had already been converted to bytes, hence
	// circ-ref
	public boolean handleIfAlreadyConverted(Object to) throws IOException;

	public void resetCircRefs(boolean b);
}
