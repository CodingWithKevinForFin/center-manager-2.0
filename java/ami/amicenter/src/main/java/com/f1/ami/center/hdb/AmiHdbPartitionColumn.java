package com.f1.ami.center.hdb;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.f1.ami.center.hdb.col.AmiHdbColumnMarshaller;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;
import com.f1.utils.FastRandomAccessFile;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class AmiHdbPartitionColumn {

	public static final int HEADER_SIZE = (AmiHdbUtils.CODE_LENGTH + 1) * 4;//header,isOptimized,type,mode

	private static final Logger log = LH.get();

	final private AmiHdbPartition owner;
	final private AmiHdbColumn historyColumn;
	private AmiHdbColumnMarshaller marshaller;
	private String name;
	private byte type;
	private byte mode;
	private boolean isOptimized;

	private File colFile;

	private File datFile;

	private FastRandomAccessFile datIO;

	private FastRandomAccessFile colIO;

	private boolean pendingFlush;

	public byte getType() {
		return this.type;
	}

	public AmiHdbPartitionColumn(AmiHdbPartition owner, AmiHdbColumn amiHistoryColumn, boolean isCreate) {
		this.owner = owner;
		this.historyColumn = amiHistoryColumn;
		this.mode = amiHistoryColumn.getMode();
		this.type = amiHistoryColumn.getAmiType();
		setFileNameInner(amiHistoryColumn.getName());
		if (colFile.exists()) {
			try {
				if (isCreate) //Writable and already exists... This is a problem
					throw new IllegalStateException("File already exists: " + IOH.getFullPath(colFile));
				readHeader();
				createMarshaller();
			} catch (Exception e) {
				IOH.close(this.datIO);
				IOH.close(this.colIO);
				LH.warning(log, "Critical Error with column, marking as bad: " + IOH.getFullPath(this.colFile), e);
				if (this.colFile.exists()) {
					File file2 = IOH.appendExtension(this.colFile, ".bad");
					LH.warning(log, "Moving " + IOH.getFullPath(this.colFile) + " to ", IOH.getFullPath(file2));
					this.colFile.renameTo(file2);
				}
				if (this.datFile.exists()) {
					File file2 = IOH.appendExtension(this.datFile, ".bad");
					LH.warning(log, "Moving " + IOH.getFullPath(this.datFile) + " to ", IOH.getFullPath(file2));
					this.datFile.renameTo(file2);
				}
				this.mode = amiHistoryColumn.getMode();
				this.type = amiHistoryColumn.getAmiType();
				this.colIO = null;
				this.datIO = null;
			}
		} else if (isCreate) {
			try {
				writeHeader();
				createMarshaller();
			} catch (Exception e) {
				IOH.close(this.datIO);
				IOH.close(this.colIO);
				throw handle(e);
			}
		} else {
			//File doesn't exist, notCreate so leave blank
		}
	}

	private void initColIO() throws IOException {
		OH.assertNull(this.colIO);
		this.colIO = getPartition().getTable().getFilePool().open(colFile, "rw", getBlockSize());
	}

	private void createMarshaller() {
		OH.assertNull(this.datIO);
		try {
			AmiHdbColumnMarshaller r = AmiHdbUtils.createMarshaller(this, type, mode);
			this.marshaller = r;
			if (marshaller.hasDataFile())
				this.datIO = getPartition().getTable().getFilePool().open(datFile, "rw", getBlockSize());
			this.marshaller.init(colIO, datIO);
		} catch (Exception e) {
			throw handle(e);
		}
	}

	private void readHeader() {
		try {
			initColIO();
			FastDataInput in = this.colIO.getInput();
			int version = AmiHdbUtils.readHeader(in, AmiHdbUtils.HEADER_COLUMN);
			OH.assertEq(in.readByte(), '\n');
			OH.assertEq(version, 1);
			this.isOptimized = AmiHdbUtils.readOptimized(in);
			OH.assertEq(in.readByte(), '\n');
			this.type = AmiHdbUtils.readType(in);
			OH.assertEq(in.readByte(), '\n');
			this.mode = AmiHdbUtils.readMode(in);
			OH.assertEq(in.readByte(), '\n');
		} catch (Exception e) {
			throw handle(e);
		}
	}

	private void writeHeader() {
		try {
			initColIO();
			FastDataOutput datOut = this.colIO.getOutput();
			AmiHdbUtils.writeHeader(datOut, AmiHdbUtils.HEADER_COLUMN, 1);
			datOut.write('\n');
			AmiHdbUtils.writeOptimized(datOut, this.isOptimized);
			datOut.write('\n');
			AmiHdbUtils.writeType(datOut, type);
			datOut.write('\n');
			AmiHdbUtils.writeMode(datOut, mode);
			datOut.write('\n');
			datOut.flush();
		} catch (Exception e) {
			throw handle(e);
		}
	}

	public String getName() {
		return this.name;
	}

	public void flush() {
		if (pendingFlush)
			return;
		this.owner.getTable().registerForFlush(this);
		this.pendingFlush = true;
	}
	public void flushPersisted() {
		flushNow();
		this.pendingFlush = false;
	}

	public void flushNow() {
		try {
			if (colIO != null)
				this.colIO.getOutput().flush();
			if (datIO != null)
				this.datIO.getOutput().flush();
		} catch (Exception e) {
			throw handle(e);
		}
	}

	public int getHeaderSize() {
		return HEADER_SIZE;
	}

	public int getBlockSize() {
		return owner.getTable().getBlockSize();
	}
	public void readValues(int[] rows, int rowsStart, int rowsCount, int sinkStart, Comparable[] sink) {
		try {
			if (isMissing()) {
				for (int i = 0; i < rowsCount; i++)
					sink[sinkStart + i] = null;
			} else
				this.marshaller.readValues(rows, rowsStart, rowsCount, sinkStart, sink);
		} catch (Exception e) {
			throw handle(e);
		}
	}
	public void readValues(int start, int count, int sinkStart, Comparable[] sink) {
		try {
			if (isMissing()) {
				for (int i = 0; i < count; i++)
					sink[sinkStart + i] = null;
			} else
				this.marshaller.readValues(start, count, sinkStart, sink);
		} catch (Exception e) {
			throw handle(e);
		}
	}

	public void appendValues(Comparable[] values, int start, int count) {
		try {
			ensureNotMissing();
			if (!this.marshaller.canAppendValues(values, start, count))
				unoptimize();
			this.marshaller.appendValues(values, start, count);
			markUnoptimized();
		} catch (Exception e) {
			throw handle(e);
		}
	}

	public boolean isPrimitive() {
		try {
			ensureNotMissing();
		} catch (Exception e) {
			throw handle(e);
		}
		return this.marshaller.supportsPrimitive();
	}
	public void appendValuesPrimitive(long[] values, boolean isNulls[], int start, int count) {
		try {
			ensureNotMissing();
			if (!this.marshaller.canAppendValuesPrimitive(values, isNulls, start, count))
				unoptimize();
			this.marshaller.appendValuesPrimitive(values, isNulls, start, count);
			markUnoptimized();
		} catch (Exception e) {
			throw handle(e);
		}
	}
	public void appendValuesPrimitive(double[] values, boolean[] isNulls, int start, int count) {
		try {
			ensureNotMissing();
			if (!this.marshaller.canAppendValuesPrimitive(values, isNulls, start, count))
				unoptimize();
			this.marshaller.appendValuesPrimitive(values, isNulls, start, count);
			markUnoptimized();
		} catch (Exception e) {
			throw handle(e);
		}
	}

	public void appendNulls(int count) {
		try {
			if (!isMissing()) {
				if (!this.marshaller.canAppendNulls(count))
					unoptimize();
				this.marshaller.appendNulls(count);
			}
			markUnoptimized();
		} catch (Exception e) {
			throw handle(e);
		}
	}

	public void updateRows(int[] toRemove, Comparable[] values, int count) {
		try {
			ensureNotMissing();
			if (!this.marshaller.canAppendValues(values, 0, count))
				unoptimize();
			this.marshaller.updateRows(toRemove, values, count);
			markUnoptimized();
		} catch (Exception e) {
			throw handle(e);
		}
	}
	public void removeRows(int[] toRemove) {
		try {
			if (isMissing())
				return;
			this.marshaller.removeRows(toRemove);
			markUnoptimized();
		} catch (Exception e) {
			throw handle(e);
		}
	}
	private void ensureNotMissing() throws IOException {
		if (isMissing()) {
			writeHeader();
			createMarshaller();
			this.marshaller.appendNulls(this.owner.getRowCount());
		}
	}

	public long getSizeOnDisk() {
		if (this.marshaller == null)
			return 0;
		long r = colFile.length();
		if (this.marshaller.hasDataFile())
			r += this.datFile.length();
		return r;
	}

	public byte getMode() {
		return this.mode;
	}

	public AmiHdbColumnMarshaller getMarshaller() {
		return this.marshaller;
	}

	public AmiHdbPartition getPartition() {
		return this.owner;
	}

	public AmiHdbColumn getHistoryColumn() {
		return this.historyColumn;
	}

	public void clear() {
		try {
			close();
			this.datFile.delete();
			this.colFile.delete();
			this.marshaller = null;
		} catch (Exception e) {
			throw handle(e);
		}
	}

	//	private void deleteBackup() {
	//		IOH.appendExtension(this.datFile, ".optimizing").delete();
	//		IOH.appendExtension(this.colFile, ".optimizing").delete();
	//	}

	public void setName(String newName) {
		try {
			if (OH.eq(this.name, newName))
				return;
			if (!isMissing())
				close();
			final File old1 = colFile;
			final File old2 = datFile;
			setFileNameInner(newName);
			if (!isMissing()) {
				IOH.renameOrThrow(old1, colFile);
				if (this.marshaller.hasDataFile())
					IOH.renameOrThrow(old2, datFile);
				initColIO();
				createMarshaller();
			}
		} catch (Exception e) {
			throw handle(e);
		}
	}

	private void setFileNameInner(String newName) {
		this.name = newName;
		this.colFile = AmiHdbUtils.newFile(owner.getDirectory(), name, AmiHdbUtils.FILE_EXT_HCOL);
		this.datFile = AmiHdbUtils.newFile(owner.getDirectory(), name, AmiHdbUtils.FILE_EXT_HDAT);
	}

	//	private void alterColumn(byte type, byte mode) {
	//		try {
	//			this.type = type;
	//			this.mode = mode;
	//			if (!isMissing()) {
	//				//STEP 1: Pull in data
	//				Comparable[] sink = new Comparable[owner.getRowCount()];
	//				marshaller.readValues(0, sink.length, 0, sink);
	//				close();
	//				
	//				//STEP 2: wrote out data to .rebuild FIles(s)
	//				this.mode = mode;
	//				this.isOptimized = false;
	//				setFileNameInner(this.historyColumn.getName() + ".rebuild");
	//				writeHeader();
	//				createMarshaller();
	//				this.marshaller.appendValues(sink, 0, sink.length);
	//				close();
	//				
	//				//STEP 3:  move .rebuild files 
	//				File oldCol = this.colFile;
	//				File oldDat = this.datFile;
	//				setFileNameInner(this.historyColumn.getName());
	//				colFile.delete();
	//				datFile.delete();
	//				oldCol.renameTo(this.colFile);
	//				if (oldDat.isFile()) {
	//					oldDat.renameTo(this.datFile);
	//				}
	//
	//				//STEP 4: Establish new marshaller
	//				createMarshaller();
	//			}
	//		} catch (Exception e) {
	//			throw handle(e);
	//		}
	//	}

	public void close() {
		flushNow();
		IOH.close(this.colIO);
		IOH.close(this.datIO);
		this.colIO = null;
		this.datIO = null;
	}

	private AmiHdbException handle(Exception e) {
		if (e instanceof AmiHdbException)
			return (AmiHdbException) e;
		return new AmiHdbException("Critical error with historical column for partition at " + describe(), e);
	}
	public String describe() {
		return IOH.getFullPath(colFile);
	}

	public boolean isMissing() {
		return this.marshaller == null;
	}
	public boolean isOptimized() {
		return this.isOptimized;
	}

	public void optimize() {
		long start = System.currentTimeMillis();
		if (isMissing())
			return;
		if (this.isOptimized)
			return;
		double cutoff = this.getPartition().getTable().getOptimizePctCutoff();
		try {
			final byte mode2 = this.marshaller.determineOptimizedMode(cutoff);
			if (mode2 != AmiHdbColumnMarshaller.ALREADY_OPTIMIZED && mode2 != AmiHdbColumnMarshaller.OPTIMIZED_TO_THRESHOLD) {
				byte oldMode = mode;

				//STEP 1:Pull in data
				Comparable[] sink = new Comparable[owner.getRowCount()];
				try {
					marshaller.readValues(0, sink.length, 0, sink);
				} catch (OutOfMemoryError e) {
					LH.warning(log, describe(), " Can not be optimized due to memory size. Marking as optimized.", e);
					this.isOptimized = true;
					this.colIO.seek(AmiHdbUtils.CODE_LENGTH + 1);
					AmiHdbUtils.writeOptimized(colIO.getOutput(), isOptimized);
					this.colIO.getOutput().flush();
					return;
				}
				long size = getSizeOnDisk();
				close();

				//STEP 2: write out data to .rebuild File(s)
				this.mode = mode2;
				this.isOptimized = true;
				this.colFile = AmiHdbUtils.newFile(owner.getDirectory(), name, AmiHdbUtils.FILE_EXT_HCOL_REBUILD);
				this.datFile = AmiHdbUtils.newFile(owner.getDirectory(), name, AmiHdbUtils.FILE_EXT_HDAT_REBUILD);
				writeHeader();
				createMarshaller();
				this.marshaller.appendValues(sink, 0, sink.length);
				close();

				//STEP 3:  move .rebuild files 
				File oldCol = this.colFile;
				File oldDat = this.datFile;
				setFileNameInner(this.historyColumn.getName());
				colFile.delete();
				datFile.delete();
				oldCol.renameTo(this.colFile);
				if (oldDat.isFile()) {
					oldDat.renameTo(this.datFile);
				}

				//STEP 4: Establish new marshaller
				initColIO();
				createMarshaller();

				long end = System.currentTimeMillis();
				long size2 = getSizeOnDisk();
				LH.info(log, "Optimized ", describe(), " FROM ", AmiHdbUtils.MODES.getValue(oldMode), " to ", AmiHdbUtils.MODES.getValue(mode2), ": " + size, "==>", size2,
						" byte(s): (", 100 - ((long) size2 * 100L / size), "% reduced in ", (end - start), " millis)");
			} else {
				long size = getSizeOnDisk();
				long end = System.currentTimeMillis();
				if (mode2 == AmiHdbColumnMarshaller.OPTIMIZED_TO_THRESHOLD)
					LH.info(log, describe(), " Marking as optimized because it's withing threshold of ", cutoff, "% (", size, " bytes checked in  ", (end - start), " millis)");
				this.isOptimized = true;
				this.colIO.seek(AmiHdbUtils.CODE_LENGTH + 1);
				AmiHdbUtils.writeOptimized(colIO.getOutput(), isOptimized);
				this.colIO.getOutput().flush();
			}
		} catch (Exception e) {
			throw handle(e);
		}
	}
	private void unoptimize() {
		byte mode2 = AmiHdbUtils.getDefaultMode(type);
		if (mode2 == this.mode)
			throw new AmiHdbException("Can not further unoptimize: " + mode2);
		LH.info(log, "Unoptimizing ", describe(), " FROM " + AmiHdbUtils.MODES.getValue(this.mode) + " to " + AmiHdbUtils.MODES.getValue(mode2));
		try {
			//STEP 1:Pull in data
			Comparable[] sink = new Comparable[owner.getRowCount()];
			marshaller.readValues(0, sink.length, 0, sink);
			close();

			//STEP 2: write out data to .rebuild File(s)
			this.mode = mode2;
			this.isOptimized = false;
			this.colFile = AmiHdbUtils.newFile(owner.getDirectory(), name, AmiHdbUtils.FILE_EXT_HCOL_REBUILD);
			this.datFile = AmiHdbUtils.newFile(owner.getDirectory(), name, AmiHdbUtils.FILE_EXT_HDAT_REBUILD);
			writeHeader();
			createMarshaller();
			this.marshaller.appendValues(sink, 0, sink.length);
			close();

			//STEP 3: move .rebuild files
			File oldCol = this.colFile;
			File oldDat = this.datFile;
			setFileNameInner(this.historyColumn.getName());
			colFile.delete();
			datFile.delete();
			oldCol.renameTo(this.colFile);
			if (oldDat.isFile()) {
				oldDat.renameTo(this.datFile);
			}

			//STEP 4: Establish new marshaller
			initColIO();
			createMarshaller();
		} catch (Exception e) {
			throw handle(e);
		}
	}
	private void markUnoptimized() {
		if (!this.isOptimized)
			return;
		try {
			this.isOptimized = false;
			this.colIO.seek(AmiHdbUtils.CODE_LENGTH + 1);
			AmiHdbUtils.writeOptimized(colIO.getOutput(), isOptimized);
		} catch (Exception e) {
			throw handle(e);
		}
	}

	public File getColFile() {
		return this.colFile;
	}

	public File getDatFile() {
		return this.datFile;
	}

	public int getRowCount() {
		return this.marshaller.getRowCount();
	}

	public void setRowCount(int nuwCount) {
		int count = this.marshaller.getRowCount();
		if (count == nuwCount)
			return;
		else if (nuwCount < count) {
			final int[] toRemove = new int[count - nuwCount];
			for (int i = 0; i < toRemove.length; i++)
				toRemove[i] = nuwCount + i;
			removeRows(toRemove);
		} else {
			appendNulls(nuwCount - count);
		}
	}

	public void backup() {
		try {
			flushNow();
			if (datFile.isFile())
				IOH.copy(this.datFile, IOH.appendExtension(this.datFile, ".backup"), getBlockSize(), false);
			if (colFile.isFile())
				IOH.copy(this.colFile, IOH.appendExtension(this.colFile, ".backup"), getBlockSize(), false);
		} catch (Exception e) {
			throw handle(e);
		}
	}

	public void markBad() {
		try {
			close();
			if (datFile.isFile())
				IOH.moveForce(this.datFile, IOH.appendExtension(this.datFile, ".bad"));
			if (colFile.isFile())
				IOH.moveForce(this.colFile, IOH.appendExtension(this.colFile, ".bad"));
			this.mode = this.historyColumn.getMode();
			this.type = this.historyColumn.getAmiType();
			this.marshaller = null;
		} catch (Exception e) {
			LH.warning(log, handle(e));
		}
	}
}
