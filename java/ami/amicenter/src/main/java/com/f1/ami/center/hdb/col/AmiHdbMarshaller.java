package com.f1.ami.center.hdb.col;

import java.io.IOException;

import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public interface AmiHdbMarshaller<T extends Comparable> {

	void write(FastDataOutput out, T v) throws IOException;
	T read(FastDataInput input) throws IOException;
	T cast(Object o);
	int getSize(T value);
	byte getType();

}
