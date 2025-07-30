package com.f1.ami.center.hdb.col;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.f1.ami.center.hdb.AmiHdbPartitionColumn;
import com.f1.ami.center.hdb.AmiHdbUtils;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.FastRandomAccessFile;
import com.f1.utils.MH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherSet;

public class AmiHdbColumnMarshaller_Bitmap2String implements AmiHdbColumnMarshaller {

	private static final long ROW_SIZE = 2;
	private FastRandomAccessFile colIO;
	private int rowCount;
	private FastRandomAccessFile datIO;
	final private long headerSize;
	private boolean needsCache = true;
	private String[] cache = new String[1024];
	private Map<String, Integer> cacheMap = new HashMap<String, Integer>();

	public AmiHdbColumnMarshaller_Bitmap2String(AmiHdbPartitionColumn owner) throws IOException {
		this.headerSize = owner.getHeaderSize();
	}

	@Override
	public void init(FastRandomAccessFile colIO, FastRandomAccessFile datIO) throws IOException {
		this.colIO = colIO;
		this.datIO = datIO;
		this.rowCount = (int) ((this.colIO.length() - headerSize) / ROW_SIZE);
	}

	@Override
	public void appendValues(Comparable[] values, int start, int count) throws IOException {
		cachePayload();
		colIO.seek(colIO.length());
		FastDataOutput dataOut = this.colIO.getOutput();
		for (int i = 0; i < count; i++) {
			String v = Caster_String.INSTANCE.castNoThrow(values[i + start]);
			if (v == null) {
				dataOut.writeShort(MH.MAX_UNSIGNED_SHORT);
			} else {
				int pos = getCode(v);

				dataOut.writeShort(pos);
			}
		}
		this.rowCount += count;
	}

	private int getCode(String v) throws IOException {
		Integer pos = cacheMap.get(v);
		if (pos == null) {
			int next = cacheMap.size();
			if (next == MH.MAX_UNSIGNED_SHORT)
				throw new RuntimeException("Exceeded Unique " + MH.MAX_UNSIGNED_SHORT + " values BITMAP2");
			pos = next;
			if (pos == cache.length)
				cache = Arrays.copyOf(cache, cache.length * 2);
			cache[pos] = v;
			cacheMap.put(v, pos);
			datIO.seek(datIO.length());
			datIO.getOutput().writeUTF(v);
		}
		return pos.intValue();
	}

	@Override
	public void updateRows(int toUpdate[], Comparable[] values, int count) throws IOException {
		cachePayload();
		colIO.seek(colIO.length());
		FastDataOutput dataOut = this.colIO.getOutput();
		int last = Integer.MIN_VALUE;
		for (int i = 0; i < count; i++) {
			String v = Caster_String.INSTANCE.castNoThrow(values[i]);
			int row = toUpdate[i];
			if (row != last + 1)
				this.colIO.seek(headerSize + row * ROW_SIZE);
			if (v == null) {
				dataOut.writeShort(MH.MAX_UNSIGNED_SHORT);
			} else {
				int pos = getCode(v);
				dataOut.writeShort(pos);
			}
			last = row;
		}
		this.rowCount += count;
	}
	private void cachePayload() throws IOException {
		if (!needsCache)
			return;
		datIO.seek(0);
		FastDataInput in = datIO.getInput();
		long len = datIO.length();
		int n = 0;
		while (datIO.getPosition() < len) {
			final int id = n++;
			final String str = in.readUTF();
			if (id == cache.length)
				cache = Arrays.copyOf(cache, cache.length * 2);
			cache[id] = str;
			this.cacheMap.put(str, id);
		}
		needsCache = false;
	}

	@Override
	public void appendNulls(int count) throws IOException {
		colIO.seek(colIO.length());
		FastDataOutput dataOut = this.colIO.getOutput();
		for (int i = 0; i < count; i++)
			dataOut.writeShort(MH.MAX_UNSIGNED_SHORT);
		this.rowCount += count;
	}

	@Override
	public byte getMode() {
		return AmiHdbUtils.MODE_BITMAP1;
	}

	@Override
	public byte getClassType() {
		return AmiTable.TYPE_STRING;
	}

	@Override
	public int getBytesPerRow() {
		return (int) ROW_SIZE;
	}

	@Override
	public void readValues(int[] rows, int rowsStart, int rowsCount, int target, Comparable[] sink) throws IOException {
		cachePayload();
		FastDataInput dataIn = this.colIO.getInput();
		int last = Integer.MIN_VALUE;
		for (int i = 0; i < rowsCount; i++) {
			int row = rows[rowsStart + i];
			if (row != last + 1)
				colIO.seek(headerSize + row * ROW_SIZE);
			int pos = dataIn.readUnsignedShort();
			if (pos != MH.MAX_UNSIGNED_SHORT)
				sink[target] = cache[pos];
			else
				sink[target] = null;

			target++;
			last = row;
		}
	}

	@Override
	public void readValues(int start, int count, int target, Comparable[] r) throws IOException {
		cachePayload();
		colIO.seek(headerSize + start * ROW_SIZE);
		FastDataInput dataIn = this.colIO.getInput();
		while (count-- > 0) {
			int pos = dataIn.readUnsignedShort();
			if (pos != MH.MAX_UNSIGNED_SHORT)
				r[target] = cache[pos];
			else
				r[target] = null;
			target++;
		}
	}

	@Override
	public void removeRows(int[] toRemove) throws IOException {
		AmiHdbUtils.removeRows(colIO, toRemove, getBytesPerRow(), rowCount, headerSize);
		this.rowCount -= toRemove.length;
	}

	@Override
	public boolean hasDataFile() {
		return true;
	}

	@Override
	public byte determineOptimizedMode(double cutoff) throws IOException {
		if (cutoff > .5)
			return OPTIMIZED_TO_THRESHOLD;
		cachePayload();
		return this.cacheMap.size() < 128 ? AmiHdbUtils.MODE_BITMAP1 : ALREADY_OPTIMIZED;
	}

	@Override
	public int getRowCount() {
		return this.rowCount;
	}

	@Override
	public boolean canAppendValues(Comparable[] values, int start, int count) throws IOException {
		cachePayload();
		HasherSet<String> tmp = null;
		for (int i = 0; i < count; i++) {
			String v = Caster_String.INSTANCE.castNoThrow(values[i]);
			if (v != null) {
				if (!cacheMap.containsKey(v)) {
					if (tmp == null)
						tmp = new HasherSet();
					if (tmp.add(v) && tmp.size() + this.cacheMap.size() >= MH.MAX_UNSIGNED_SHORT)
						return false;
				}
			}
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
