package com.f1.ami.center.hdb.col;

public interface AmiHdbMarshallerFixedSize<T extends Comparable> extends AmiHdbMarshaller<T> {

	T minValue();
	int getFixedSize();
}
