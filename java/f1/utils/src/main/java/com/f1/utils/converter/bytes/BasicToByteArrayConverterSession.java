/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;
import java.util.IdentityHashMap;

import com.f1.utils.FastDataOutput;

public class BasicToByteArrayConverterSession implements ToByteArrayConverterSession {

	private FastDataOutput stream;
	private boolean supportCircRefs;
	private ObjectToByteArrayConverter converter;
	private IdentityHashMap<Object, Integer> ids;
	private boolean legacyMode = false;

	public BasicToByteArrayConverterSession(ObjectToByteArrayConverter converter, FastDataOutput dataOutputStream, boolean supportCircRefs) {
		this.converter = converter;
		this.stream = dataOutputStream;
		this.supportCircRefs = supportCircRefs;
		this.ids = new IdentityHashMap<Object, Integer>();
	}

	@Override
	public FastDataOutput getStream() {
		return stream;
	}

	@Override
	public ObjectToByteArrayConverter getConverter() {
		return converter;
	}

	@Override
	public boolean handleIfAlreadyConverted(Object obj) throws IOException {
		if (!supportCircRefs) {
			if (legacyMode)
				stream.writeInt(0);
			return false;
		}
		Integer id = ids.get(obj);
		if (id != null) {
			stream.writeInt(-id);
			return true;
		} else {
			stream.writeInt(ids.size() + 1);
			ids.put(obj, ids.size() + 1);
		}
		return false;
	}

	@Override
	public void resetCircRefs(boolean support) {
		if (!support) {
			if (supportCircRefs) {
				supportCircRefs = false;
				ids.clear();
			}
			return;
		}
		if (ids.size() > 0)
			ids.clear();
	}

	public void setLegacyMode() {
		this.legacyMode = true;
	}

}
