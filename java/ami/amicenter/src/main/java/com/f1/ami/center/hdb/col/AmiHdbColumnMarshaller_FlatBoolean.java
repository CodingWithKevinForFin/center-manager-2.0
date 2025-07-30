package com.f1.ami.center.hdb.col;

import java.io.IOException;

import com.f1.ami.center.hdb.AmiHdbPartitionColumn;
import com.f1.ami.center.hdb.AmiHdbUtils;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.FastRandomAccessFile;
import com.f1.utils.casters.Caster_Boolean;

public class AmiHdbColumnMarshaller_FlatBoolean implements AmiHdbColumnMarshaller {

	private long ROW_SIZE = 1;
	private FastRandomAccessFile colIO;
	private int rowCount;
	final private long headerSize;

	public AmiHdbColumnMarshaller_FlatBoolean(AmiHdbPartitionColumn owner) throws IOException {
		this.headerSize = owner.getHeaderSize();
	}
	@Override
	public void init(FastRandomAccessFile colIO, FastRandomAccessFile datIO) throws IOException {
		this.colIO = colIO;
		this.colIO.seek(this.headerSize);
		this.rowCount = (int) ((this.colIO.length() - headerSize) / ROW_SIZE);
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
		this.colIO.seek(headerSize + start * ROW_SIZE);
		FastDataInput in = this.colIO.getInput();
		while (count-- > 0) {
			byte b = in.readByte();
			sink[sinkStart++] = b == -2 ? null : b == 1;
		}
	}
	@Override
	public void readValues(int[] rows, int rowsStart, int rowsCount, int sinkStart, Comparable[] sink) throws IOException {
		FastDataInput in = this.colIO.getInput();
		int last = Integer.MIN_VALUE;
		for (int i = 0; i < rowsCount; i++) {
			int row = rows[rowsStart + i];
			if (row != last + 1)
				this.colIO.seek(headerSize + row * ROW_SIZE);
			byte b = in.readByte();
			sink[sinkStart++] = b == -1 ? null : b == 1;
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
				this.colIO.seek(headerSize + row * ROW_SIZE);
			Boolean v = Caster_Boolean.INSTANCE.cast(values[i]);
			out.writeByte(v == null ? -1 : v ? 1 : 0);
			last = row;
		}

	}

	@Override
	public void appendNulls(int count) throws IOException {
		FastDataOutput out = this.colIO.getOutput();
		this.colIO.seek(this.colIO.length());
		for (int i = 0; i < count; i++) {
			out.writeByte(-1);
		}
		this.rowCount += count;
	}

	@Override
	public void appendValues(Comparable[] values, int start, int count) throws IOException {
		FastDataOutput out = this.colIO.getOutput();
		this.colIO.seek(this.colIO.length());
		for (int i = 0; i < count; i++) {
			Boolean v = Caster_Boolean.INSTANCE.cast(values[i + start]);
			out.writeByte(v == null ? -1 : v ? 1 : 0);
		}
		this.rowCount += count;
	}
	@Override
	public void appendValuesPrimitive(long[] values, boolean[] isNulls, int start, int count) throws IOException {
		FastDataOutput out = this.colIO.getOutput();
		this.colIO.seek(this.colIO.length());
		for (int i = 0; i < count; i++) {
			if (isNulls[i + start])
				out.writeByte(-1);
			else
				out.writeByte(values[i + start] == 0 ? 0 : 1);
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
			if (isNulls[i + start])
				out.writeByte(-1);
			else
				out.writeByte(values[i + start] == 0D ? 0 : 1);
		}
		this.rowCount += count;
	}
	@Override
	public boolean canAppendValuesPrimitive(double[] values, boolean[] isNulls, int start, int count) {
		return true;
	}
	@Override
	public boolean supportsPrimitive() {
		return true;
	}

	@Override
	public int getBytesPerRow() {
		return (int) ROW_SIZE;
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

	@Override
	public boolean canAppendValues(Comparable[] values, int start, int count) throws IOException {
		return true;
	}
	@Override
	public boolean canAppendNulls(int count) {
		return true;
	}
}
