package com.f1.ami.center.hdb.col;

import java.io.IOException;

import com.f1.ami.center.hdb.AmiHdbPartitionColumn;
import com.f1.ami.center.hdb.AmiHdbUtils;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.FastRandomAccessFile;
import com.f1.utils.OH;

public class AmiHdbColumnMarshaller_Flat<T extends Comparable> implements AmiHdbColumnMarshaller {

	private FastRandomAccessFile colIO;
	private int rowCount;
	final private long rowSize;
	final private long headerSize;
	final private AmiHdbMarshallerFixedSize<T> marshaller;
	final private AmiHdbMarshallerPrimitive pMarshaller;

	public AmiHdbColumnMarshaller_Flat(AmiHdbPartitionColumn owner, AmiHdbMarshallerFixedSize<T> marshaller) throws IOException {
		this.marshaller = marshaller;
		if (this.marshaller instanceof AmiHdbMarshallerPrimitive)
			this.pMarshaller = (AmiHdbMarshallerPrimitive) this.marshaller;
		else
			this.pMarshaller = null;
		this.rowSize = marshaller.getFixedSize() + 1;
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
		return AmiHdbUtils.MODE_FLAT;
	}

	@Override
	public byte getClassType() {
		return AmiTable.TYPE_INT;
	}

	@Override
	public void readValues(int start, int count, int sinkStart, Comparable[] sink) throws IOException {
		this.colIO.seek(headerSize + start * rowSize);
		FastDataInput in = this.colIO.getInput();
		while (count-- > 0) {
			boolean b = in.readBoolean();
			T v = marshaller.read(in);
			sink[sinkStart++] = b ? null : (Comparable) v;
		}
	}
	@Override
	public void readValues(int[] rows, int rowsStart, int rowsCount, int sinkStart, Comparable[] sink) throws IOException {
		FastDataInput in = this.colIO.getInput();
		int last = Integer.MIN_VALUE;
		for (int i = 0; i < rowsCount; i++) {
			int row = rows[rowsStart + i];
			if (row != last + 1)
				this.colIO.seek(headerSize + row * rowSize);
			boolean b = in.readBoolean();
			T v = marshaller.read(in);
			sink[sinkStart++] = b ? null : (Comparable) v;
			last = row;
		}
	}

	@Override
	public void updateRows(int[] toUpdate, Comparable[] values, int count) throws IOException {
		int last = Integer.MIN_VALUE;
		FastDataOutput out = this.colIO.getOutput();
		for (int i = 0; i < count; i++) {
			int row = toUpdate[i];
			if (row != last + 1)
				this.colIO.seek(headerSize + row * rowSize);
			T v = marshaller.cast(values[i]);
			if (v == null) {
				out.writeBoolean(true);
				v = marshaller.minValue();
			} else
				out.writeBoolean(false);
			marshaller.write(out, v);
			last = row;
		}

	}

	@Override
	public void appendNulls(int count) throws IOException {
		FastDataOutput out = this.colIO.getOutput();
		this.colIO.seek(this.colIO.length());
		for (int i = 0; i < count; i++) {
			out.writeBoolean(true);
			marshaller.write(out, marshaller.minValue());
		}
		this.rowCount += count;
	}

	@Override
	public void appendValues(Comparable[] values, int start, int count) throws IOException {
		FastDataOutput out = this.colIO.getOutput();
		this.colIO.seek(this.colIO.length());
		for (int i = 0; i < count; i++) {
			T v = marshaller.cast(values[i + start]);
			if (v == null) {
				out.writeBoolean(true);
				v = marshaller.minValue();
			} else
				out.writeBoolean(false);
			marshaller.write(out, v);
		}
		this.rowCount += count;
	}
	@Override
	public void appendValuesPrimitive(long[] values, boolean[] isNulls, int start, int count) throws IOException {
		FastDataOutput out = this.colIO.getOutput();
		this.colIO.seek(this.colIO.length());
		for (int i = 0; i < count; i++) {
			long v;
			if (isNulls[i + start]) {
				out.writeBoolean(true);
				pMarshaller.writeMinValue(out);
			} else {
				out.writeBoolean(false);
				pMarshaller.writePrimitiveLong(out, values[i + start]);
			}
		}
		this.rowCount += count;
	}
	@Override
	public boolean canAppendValuesPrimitive(long[] values, boolean[] isNulls, int start, int count) {
		return true;
	}
	@Override
	public void appendValuesPrimitive(double[] values, boolean[] isNulls, int start, int count) throws IOException {
		FastDataOutput out = this.colIO.getOutput();
		this.colIO.seek(this.colIO.length());
		for (int i = 0; i < count; i++) {
			long v;
			if (isNulls[i + start]) {
				out.writeBoolean(true);
				pMarshaller.writeMinValue(out);
			} else {
				out.writeBoolean(false);
				pMarshaller.writePrimitiveDouble(out, values[i + start]);
			}
		}
		this.rowCount += count;
	}
	@Override
	public boolean canAppendValuesPrimitive(double[] values, boolean[] isNulls, int start, int count) {
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
		double bestCaseNewSizePercentChange = 1d - ((rowSize - 1d) / (rowSize));
		if (cutoff > bestCaseNewSizePercentChange)
			return OPTIMIZED_TO_THRESHOLD;
		this.colIO.seek(headerSize);
		FastDataInput in = this.colIO.getInput();
		boolean hasNull = false, hasMin = false;
		T minValue = marshaller.minValue();
		for (int i = 0; i < this.rowCount; i++) {
			boolean b = in.readBoolean();
			T v = marshaller.read(in);
			if (b) {
				hasNull = true;
				if (hasMin)
					break;
			} else if (OH.eq(minValue, v)) {
				hasMin = true;
				if (hasNull)
					break;
			}
		}

		final byte r;
		if (!hasMin)
			return AmiHdbUtils.MODE_FLAT_NOMIN;
		else if (!hasNull)
			return AmiHdbUtils.MODE_FLAT_NONULL;
		else
			return -1;
	}
	@Override
	public int getRowCount() {
		return this.rowCount;
	}

	@Override
	public boolean canAppendValues(Comparable[] values, int start, int count) throws IOException {
		return true;
	}
	@Override
	public boolean canAppendNulls(int count) {
		return true;
	}
}
