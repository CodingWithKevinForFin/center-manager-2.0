package com.f1.ami.center.hdb.col;

import java.io.IOException;

import com.f1.ami.center.hdb.AmiHdbPartitionColumn;
import com.f1.ami.center.hdb.AmiHdbUtils;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.FastRandomAccessFile;

public class AmiHdbColumnMarshaller_FlatNoNull<T extends Comparable> implements AmiHdbColumnMarshaller {

	private FastRandomAccessFile colIO;
	private int rowCount;
	final private long rowSize;
	final private long headerSize;
	final private AmiHdbMarshallerFixedSize<T> marshaller;
	final private AmiHdbMarshallerPrimitive pMarshaller;

	public AmiHdbColumnMarshaller_FlatNoNull(AmiHdbPartitionColumn owner, AmiHdbMarshallerFixedSize<T> marshaller) throws IOException {
		this.marshaller = marshaller;
		if (this.marshaller instanceof AmiHdbMarshallerPrimitive)
			this.pMarshaller = (AmiHdbMarshallerPrimitive) this.marshaller;
		else
			this.pMarshaller = null;
		this.rowSize = marshaller.getFixedSize();
		this.headerSize = owner.getHeaderSize();
	}
	@Override
	public void init(FastRandomAccessFile colIO, FastRandomAccessFile datIO) throws IOException {
		this.colIO = colIO;
		this.colIO.seek(this.headerSize);
		this.rowCount = (int) ((this.colIO.length() - headerSize) / rowSize);
	}

	@Override
	public byte getMode() {
		return AmiHdbUtils.MODE_FLAT_NONULL;
	}

	@Override
	public byte getClassType() {
		return AmiTable.TYPE_INT;
	}

	/*
	 * Sequential read n rows 
	 */
	@Override
	public void readValues(int start, int count, int sinkStart, Comparable[] sink) throws IOException {
		this.colIO.seek(headerSize + start * rowSize);
		FastDataInput in = this.colIO.getInput();
		while (count-- > 0)
			sink[sinkStart++] = (Comparable) marshaller.read(in);
	}
	/*
	 * Random read n rows
	 */
	@Override
	public void readValues(int[] rows, int rowsStart, int rowsCount, int sinkStart, Comparable[] sink) throws IOException {
		FastDataInput in = this.colIO.getInput();
		int last = Integer.MIN_VALUE;
		for (int i = 0; i < rowsCount; i++) {
			int row = rows[rowsStart + i];
			if (row != last + 1) // Optimization: if position is next byte in file no need to seek to a new position
				this.colIO.seek(headerSize + row * rowSize);
			sink[sinkStart] = (Comparable) marshaller.read(in);
			sinkStart++;
			last = row;
		}
	}

	/*
	 * Random update
	 */
	@Override
	public void updateRows(int[] toUpdate, Comparable[] values, int count) throws IOException {
		int last = Integer.MIN_VALUE;
		FastDataOutput out = this.colIO.getOutput();
		for (int i = 0; i < count; i++) {
			int row = toUpdate[i];
			if (row != last + 1)
				this.colIO.seek(headerSize + row * rowSize);
			T v = marshaller.cast(values[i]);
			if (v == null) // If null use min value (hence no null)
				v = marshaller.minValue();
			marshaller.write(out, v);
			last = row;
		}

	}

	/* 
	 * Write sequential nulls
	 */
	@Override
	public void appendNulls(int count) throws IOException {
		FastDataOutput out = this.colIO.getOutput();
		this.colIO.seek(this.colIO.length());
		for (int i = 0; i < count; i++)
			marshaller.write(out, marshaller.minValue());
		this.rowCount += count;
	}

	/*
	 * Write sequential new values
	 */
	@Override
	public void appendValues(Comparable[] values, int start, int count) throws IOException {
		FastDataOutput out = this.colIO.getOutput();
		this.colIO.seek(this.colIO.length());
		for (int i = 0; i < count; i++) {
			T v = marshaller.cast(values[i + start]);
			if (v == null)
				v = marshaller.minValue();
			marshaller.write(out, v);
		}
		this.rowCount += count;
	}

	/*
	 * Write sequential primitive long values, no need to cast and ignore isNulls 
	 */
	@Override
	public void appendValuesPrimitive(long[] values, boolean[] isNulls, int start, int count) throws IOException {
		FastDataOutput out = this.colIO.getOutput();
		this.colIO.seek(this.colIO.length());
		for (int i = 0; i < count; i++)
			pMarshaller.writePrimitiveLong(out, values[i + start]);
		this.rowCount += count;
	}
	/*
	 * Check if any long values are null, if any are null, we cant append values
	 */
	@Override
	public boolean canAppendValuesPrimitive(long[] values, boolean[] isNulls, int start, int count) {
		for (int i = 0; i < count; i++)
			if (isNulls[i + start])
				return false;
		return true;
	}

	/*
	 * Write sequential primitive double values, no need to cast and ignore isNulls 
	 */
	@Override
	public void appendValuesPrimitive(double[] values, boolean[] isNulls, int start, int count) throws IOException {
		FastDataOutput out = this.colIO.getOutput();
		this.colIO.seek(this.colIO.length());
		for (int i = 0; i < count; i++)
			pMarshaller.writePrimitiveDouble(out, values[i + start]);
		this.rowCount += count;
	}

	/*
	 * Check if any double values are null, if any are null, we cant append values
	 */
	@Override
	public boolean canAppendValuesPrimitive(double[] values, boolean[] isNulls, int start, int count) {
		for (int i = 0; i < count; i++)
			if (isNulls[i + start])
				return false;
		return true;
	}
	@Override
	public boolean supportsPrimitive() {
		return this.pMarshaller != null;
	}
	@Override
	public int getBytesPerRow() {
		return (int) rowSize;
	}

	@Override
	public void removeRows(int[] toRemove) throws IOException {
		AmiHdbUtils.removeRows(this.colIO, toRemove, this.getBytesPerRow(), rowCount, headerSize);
		this.rowCount -= toRemove.length;
	}

	@Override
	public boolean hasDataFile() {
		return false;
	}

	@Override
	public byte determineOptimizedMode(double cutoff) throws IOException {
		return ALREADY_OPTIMIZED;
	}
	@Override
	public int getRowCount() {
		return this.rowCount;
	}

	/*
	 * Check if any comparable values are null, if any are null, we cant append values
	 */
	@Override
	public boolean canAppendValues(Comparable[] values, int start, int count) throws IOException {
		for (int i = 0; i < count; i++)
			if (marshaller.cast(values[i + start]) == null)
				return false;
		return true;
	}

	/*
	 * Check if we can append nulls, if there are any nulls (count >0) return false
	 */
	@Override
	public boolean canAppendNulls(int count) {
		return count == 0;
	}
}
