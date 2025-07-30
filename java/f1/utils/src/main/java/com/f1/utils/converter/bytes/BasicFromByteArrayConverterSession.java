/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.utils.FastDataInput;
import com.f1.utils.converter.ConverterException;
import com.f1.utils.structs.IntKeyMap;

public class BasicFromByteArrayConverterSession implements FromByteArrayConverterSession {

	private FastDataInput stream;
	private ObjectToByteArrayConverter converter;
	private IntKeyMap ids;
	private boolean supportCircRefs;
	private boolean legacyMode = false;

	public BasicFromByteArrayConverterSession(ObjectToByteArrayConverter converter, FastDataInput stream) {
		this.converter = converter;
		this.stream = stream;
		ids = new IntKeyMap();
	}

	@Override
	public FastDataInput getStream() {
		return stream;
	}

	@Override
	public ObjectToByteArrayConverter getConverter() {
		return converter;
	}

	@Override
	public int handleIfAlreadyConverted() throws IOException {
		if (!supportCircRefs && !legacyMode)
			return 0;
		return stream.readInt();
	}

	@Override
	public Object get(int i) {
		if (!supportCircRefs)
			throw new ConverterException("this should never be called with cir-refs off");
		return ids.get(-i);
	}

	@Override
	public void store(int i, Object obj) {
		if (!supportCircRefs)
			return;
		ids.put(i, obj);
	}

	@Override
	public void resetCircRefs(boolean supportCircRefs) {
		if (this.supportCircRefs == supportCircRefs)
			return;
		this.supportCircRefs = supportCircRefs;
		this.ids.clear();
	}

	public void setLegacyMode() {
		this.legacyMode = true;
	}

}
