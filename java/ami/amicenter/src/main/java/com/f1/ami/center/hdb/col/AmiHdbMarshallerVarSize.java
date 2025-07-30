package com.f1.ami.center.hdb.col;

import java.io.IOException;

import com.f1.utils.FastDataInput;

public interface AmiHdbMarshallerVarSize<T extends Comparable> extends AmiHdbMarshaller<T> {
	void skip(FastDataInput datIn) throws IOException;
}
