/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

public interface ByteArraySelfConverter {

	public void read(FromByteArrayConverterSession session) throws IOException;

	public void write(ToByteArrayConverterSession session) throws IOException;

}
