package com.f1.ami.center.hdb.col;

import java.io.IOException;

import com.f1.utils.FastRandomAccessFile;

public interface AmiHdbColumnMarshaller {
	byte ALREADY_OPTIMIZED = -1;
	byte OPTIMIZED_TO_THRESHOLD = -2;

	void init(FastRandomAccessFile colIO, FastRandomAccessFile datIO) throws IOException;
	byte getMode();
	byte getClassType();
	int getBytesPerRow();
	void readValues(int start, int count, int sinkStart, Comparable[] sink) throws IOException;
	void readValues(int[] rows, int rowsStart, int rowsCount, int sinkStart, Comparable[] sink) throws IOException;
	void removeRows(int[] toRemove) throws IOException;
	void appendValues(Comparable[] values, int start, int count) throws IOException;
	void appendNulls(int count) throws IOException;
	void updateRows(int[] toUpdate, Comparable[] values, int count) throws IOException;

	boolean canAppendValues(Comparable[] values, int start, int count) throws IOException;
	boolean canAppendNulls(int count);

	//returns ALREADY_OPTIMIZED or OPTIMIZED_TO_THRESHOLD or new mode
	//0=always, 1=never, .2=if the file will be reduced by 20 percent or more
	byte determineOptimizedMode(double cufoff) throws IOException;

	boolean hasDataFile();
	int getRowCount();

	void appendValuesPrimitive(long[] values, boolean[] isNulls, int start, int count) throws IOException;
	boolean canAppendValuesPrimitive(long[] values, boolean[] isNulls, int start, int count);
	void appendValuesPrimitive(double[] values, boolean[] isNulls, int start, int count) throws IOException;
	boolean canAppendValuesPrimitive(double[] values, boolean[] isNulls, int start, int count);
	public boolean supportsPrimitive();
}
