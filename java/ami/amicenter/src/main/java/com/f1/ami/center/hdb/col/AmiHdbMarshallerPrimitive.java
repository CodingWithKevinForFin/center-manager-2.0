package com.f1.ami.center.hdb.col;

import java.io.IOException;

import com.f1.utils.FastDataOutput;

public interface AmiHdbMarshallerPrimitive<C extends Comparable> extends AmiHdbMarshallerFixedSize<C> {

	void writePrimitiveLong(FastDataOutput out, long v) throws IOException;
	void writePrimitiveDouble(FastDataOutput out, double v) throws IOException;
	void writeMinValue(FastDataOutput out) throws IOException;
	boolean isMin(long v);
	boolean isMin(double v);

}
