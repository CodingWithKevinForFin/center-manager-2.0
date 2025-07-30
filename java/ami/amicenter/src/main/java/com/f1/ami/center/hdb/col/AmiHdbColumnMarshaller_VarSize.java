package com.f1.ami.center.hdb.col;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.center.hdb.AmiHdbPartitionColumn;
import com.f1.ami.center.hdb.AmiHdbUtils;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.FastRandomAccessFile;
import com.f1.utils.LH;

public class AmiHdbColumnMarshaller_VarSize<T extends Comparable> implements AmiHdbColumnMarshaller {
	private static final Logger log = LH.get();
	private FastRandomAccessFile colIO;
	private FastRandomAccessFile datIO;
	private int rowCount;
	final private long headerSize;
	final private byte mode;
	final private AmiHdbMarshallerVarSize<T> marshaller;
	final private int rowSize;
	private AmiHdbPartitionColumn owner;

	public AmiHdbColumnMarshaller_VarSize(AmiHdbPartitionColumn owner, AmiHdbMarshallerVarSize<T> marshaller, byte mode) throws IOException {
		this.marshaller = marshaller;
		this.headerSize = owner.getHeaderSize();
		this.owner = owner;
		this.mode = mode;
		this.rowSize = getRowSizeForMode(mode);
	}

	static private int getRowSizeForMode(byte mode) {
		switch (mode) {
			case AmiHdbUtils.MODE_VARSIZE3:
				return 3;
			case AmiHdbUtils.MODE_VARSIZE4:
				return 4;
			case AmiHdbUtils.MODE_VARSIZE5:
				return 5;
			default:
				throw new RuntimeException("Invalid mode: " + mode);
		}
	}

	@Override
	public void init(FastRandomAccessFile colIO, FastRandomAccessFile datIO) throws IOException {
		this.colIO = colIO;
		this.colIO.seek(headerSize);
		this.datIO = datIO;
		this.rowCount = (int) ((this.colIO.length() - headerSize) / rowSize);
	}

	@Override
	public void appendValues(Comparable values[], int start, int count) throws IOException {
		datIO.seek(datIO.length());
		FastDataOutput plodOut = datIO.getOutput();
		long positions[] = new long[count];
		for (int i = 0; i < count; i++) {
			T v = marshaller.cast(values[start + i]);
			if (v == null) {
				positions[i] = -1;
			} else {
				positions[i] = datIO.getPosition();
				marshaller.write(plodOut, v);
			}
		}
		colIO.seek(colIO.length());
		FastDataOutput colOut = this.colIO.getOutput();
		for (long p : positions)
			writePos(colOut, p);
		this.rowCount += count;
	}

	private void writePos(FastDataOutput dataOut, long p) throws IOException {
		switch (mode) {
			case AmiHdbUtils.MODE_VARSIZE3:
				dataOut.writeInt3((int) p);
				break;
			case AmiHdbUtils.MODE_VARSIZE4:
				dataOut.writeInt((int) p);
				break;
			case AmiHdbUtils.MODE_VARSIZE5:
				dataOut.writeLong5(p);
				break;
			default:
				throw new RuntimeException("Invalid mode: " + mode);
		}
	}

	private long readPos(FastDataInput colIn) throws IOException {
		switch (mode) {
			case AmiHdbUtils.MODE_VARSIZE3:
				return colIn.readInt3();
			case AmiHdbUtils.MODE_VARSIZE4:
				return colIn.readInt();
			case AmiHdbUtils.MODE_VARSIZE5:
				return colIn.readLong5();
			default:
				throw new RuntimeException("Invalid mode: " + mode);
		}
	}

	@Override
	public void appendNulls(int count) throws IOException {
		colIO.seek(colIO.length());
		FastDataOutput dataOut = this.colIO.getOutput();
		for (int i = 0; i < count; i++)
			writePos(dataOut, -1);
		this.rowCount += count;
	}

	@Override
	public byte getMode() {
		return mode;
	}

	@Override
	public byte getClassType() {
		return AmiTable.TYPE_STRING;
	}

	@Override
	public int getBytesPerRow() {
		return rowSize;
	}

	@Override
	public void updateRows(int[] toUpdate, Comparable[] values, int count) throws IOException {
		datIO.seek(datIO.length());
		FastDataOutput plodOut = datIO.getOutput();
		long positions[] = new long[count];
		for (int i = 0; i < count; i++) {
			T v = marshaller.cast(values[i]);
			if (v == null) {
				positions[i] = -1;
			} else {
				positions[i] = datIO.getPosition();
				marshaller.write(plodOut, v);
			}
		}
		colIO.seek(colIO.length());
		FastDataOutput dataOut = this.colIO.getOutput();
		int last = Integer.MIN_VALUE;
		for (int i = 0; i < count; i++) {
			int row = toUpdate[i];
			if (row != last + 1)
				this.colIO.seek(headerSize + row * rowSize);
			long p = positions[i];
			writePos(dataOut, p);
			last = row;
		}
	}

	@Override
	public void readValues(int start, int count, int sinkStart, Comparable[] sink) throws IOException {
		colIO.seek(headerSize + start * rowSize);
		FastDataInput colIn = this.colIO.getInput();
		FastDataInput datIn = this.datIO.getInput();
		long[] t = new long[count];
		for (int i = 0; i < count; i++)
			t[i] = readPos(colIn);
		for (int i = 0; i < count; i++) {
			long pos = t[i];
			if (pos == -1L) {
				sink[sinkStart] = null;
			} else {
				datIO.seek(pos);
				sink[sinkStart] = marshaller.read(datIn);
			}
			sinkStart++;
		}
	}

	@Override
	public void readValues(int[] rows, int rowsStart, int rowsCount, int sinkStart, Comparable[] sink) throws IOException {
		FastDataInput colIn = this.colIO.getInput();
		FastDataInput datIn = this.datIO.getInput();
		long[] t = new long[rowsCount];
		int last = Integer.MIN_VALUE;
		for (int i = 0; i < rowsCount; i++) {
			int row = rows[rowsStart + i];
			if (row != last + 1)
				colIO.seek(headerSize + row * rowSize);
			t[i] = readPos(colIn);
		}
		for (int i = 0; i < rowsCount; i++) {
			long pos = t[i];
			if (pos == -1L) {
				sink[sinkStart] = null;
			} else {
				datIO.seek(pos);
				sink[sinkStart] = marshaller.read(datIn);
			}
			sinkStart++;
		}
	}

	@Override
	public void removeRows(int[] toRemove) throws IOException {
		AmiHdbUtils.removeRows(this.colIO, toRemove, getBytesPerRow(), rowCount, headerSize);
		this.rowCount -= toRemove.length;
	}

	@Override
	public boolean hasDataFile() {
		return true;
	}

	@Override
	public byte determineOptimizedMode(double cutoff) throws IOException {
		boolean isString = marshaller.getType() == AmiTable.TYPE_STRING;
		long totalCurrentSize = headerSize + this.datIO.length() + this.rowCount * rowSize;
		this.datIO.seek(0);
		this.colIO.seek(headerSize);
		FastDataInput colIn = this.colIO.getInput();
		FastDataInput datIn = this.datIO.getInput();
		long[] t = new long[rowCount];
		int valSize = 0;
		for (int i = 0; i < rowCount; i++) {
			long n = readPos(colIn);
			if (n == -1)
				continue;
			t[valSize++] = n;
		}
		Set<T> uniqueValues = new HashSet<T>();
		boolean sparse = false;
		boolean canBitmap = isString;
		long totalDataSizeAfterDefrag = 0;
		for (int i = 0; i < valSize; i++) {
			long pos = t[i];
			if (datIO.getPosition() != pos) {
				datIO.seek(pos);
				sparse = true;
			}
			long start = datIO.getPosition();
			if (canBitmap) {
				T val = marshaller.read(datIn);
				if (uniqueValues.add(val) && uniqueValues.size() > Short.MAX_VALUE - 1000) {
					canBitmap = false;
					uniqueValues = null;
				}
			} else {
				marshaller.skip(datIn);
			}
			totalDataSizeAfterDefrag += datIO.getPosition() - start;
			if (!canBitmap && sparse && cutoff > 0) {
				long totalNewSize = totalDataSizeAfterDefrag + this.rowCount * getRowSizeForMode(determineMode(totalDataSizeAfterDefrag)) + headerSize;
				double pctChange = 1 - ((double) totalNewSize / totalCurrentSize);
				if (cutoff > pctChange)
					return OPTIMIZED_TO_THRESHOLD;
			}

		}
		if (canBitmap && uniqueValues.size() < rowCount / 10)
			return uniqueValues.size() < 128 ? AmiHdbUtils.MODE_BITMAP1 : AmiHdbUtils.MODE_BITMAP2;

		final byte r = determineMode(totalDataSizeAfterDefrag);
		if (cutoff > 0 && (sparse || r != this.mode)) {
			long totalNewSize = totalDataSizeAfterDefrag + this.rowCount * getRowSizeForMode(r) + headerSize;
			double pctChange = 1 - ((double) totalNewSize / totalCurrentSize);
			if (LH.isFine(log))
				LH.fine(log, this.owner.describe(), " Change in size: " + totalCurrentSize + " --> " + totalNewSize + " = " + pctChange);
			if (cutoff > pctChange)
				return OPTIMIZED_TO_THRESHOLD;
			else
				return r;
		} else
			return ALREADY_OPTIMIZED;
	}

	static private byte determineMode(long totalSize) {
		if (totalSize < 256 * 256 * 100) // 100 is to leave some padding incase of future inserts/updates
			return AmiHdbUtils.MODE_VARSIZE3;
		else if (totalSize < 256 * 256 * 256 * 100) // 100 is to leave some padding incase of future inserts/updated
			return AmiHdbUtils.MODE_VARSIZE4;
		else
			return AmiHdbUtils.MODE_VARSIZE5;
	}

	@Override
	public int getRowCount() {
		return rowCount;
	}

	@Override
	public boolean canAppendValues(Comparable[] values, int start, int count) throws IOException {
		if (mode == AmiHdbUtils.MODE_VARSIZE5)
			return true;
		int remaining = mode == AmiHdbUtils.MODE_VARSIZE3 ? Integer.MAX_VALUE / 256 : Integer.MAX_VALUE;
		remaining -= this.datIO.length();
		for (int i = 0; i < count; i++) {
			T o = marshaller.cast(values[i]);
			remaining -= marshaller.getSize(o);
			if (remaining <= 0)
				return false;
		}
		return true;

	}

	@Override
	public boolean canAppendNulls(int count) {
		return true;
	}

	@Override
	public void appendValuesPrimitive(long[] values, boolean[] isNulls, int start, int count) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean canAppendValuesPrimitive(long[] values, boolean[] isNulls, int start, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void appendValuesPrimitive(double[] values, boolean[] isNulls, int start, int count) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean canAppendValuesPrimitive(double[] values, boolean[] isNulls, int start, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean supportsPrimitive() {
		return false;
	}
}
