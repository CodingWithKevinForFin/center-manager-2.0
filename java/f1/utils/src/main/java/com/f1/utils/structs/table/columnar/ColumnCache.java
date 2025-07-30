package com.f1.utils.structs.table.columnar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongKeyMap.Node;
import com.f1.utils.structs.table.columnar.ColumnCacheCachedValues.Cache;
import com.f1.utils.structs.table.columnar.ColumnCacheEmptyBlocks.EmptyBlock;

/**
 * A file backed collection of objects which must be marshable to/from a byte array. <BR>
 * {@link #add(Object)} - Add an object and get back a pointer<BR>
 * {@link #get(long)} - Retrieve the block at that pointer <BR>
 * {@link #remove(long)} and {@link #getAndRemove(long)} - frees up the memory at the pointer previously allocated for later usage<BR>
 * {@link #set(long, Object)} - update the value at a given address. It may return the same pointer supplied or a different pointer, depending on allocation details. This is more
 * efficient than calling a delete followed by add
 * <p>
 * The disk is stored and updated syncrhonisly as changes are made. and LRU cache is employed and the scheme actively manages empty blocks for reuse.
 * <P>
 * Each entry is stored with the 4 byte length, followed by the entry's data. Empty blocks are denoted with a negative size, such that -10 indicates that the following 0 byte
 * blocks of data are not allowed. <BR>
 * <P>
 * Note the special pointer -1 indicates null and -2 indicates an object made of the empty (zero length) byte array.
 * <P>
 * File system example: [Int32 15][15 bytes of data][Int32 20][20 bytes of data][-15][15 bytes of empty data[0][0 bytes of empty data][4][4 bytes of data][EOF]
 * 
 * @author rcooke
 * 
 * @param <T>
 */
public abstract class ColumnCache<T> {
	public static final int MAX_BLOCK_SIZE = Integer.MAX_VALUE;
	private static final String RW = "rw";
	public final static long POINTER_NULL = -1;
	public final static long POINTER_EMPTY = -2;
	private static final Logger log = LH.get();

	final private ColumnCacheEmptyBlocks emptyBlocks = new ColumnCacheEmptyBlocks();
	final private ColumnCacheCachedValues<T> cached;

	final private File data;
	private RandomAccessFile dataIo;

	private long usedBytes;
	private T empty;
	private boolean isClosed = true;
	private Thread shutdown = new Thread() {

		public void run() {
			try {
				if (!isClosed) {
					dataIo.getChannel().force(true);
					IOH.close(dataIo);
				}
			} catch (Exception e) {
				System.err.print("Error on shutdown for ONDISK: " + fileName);
				e.printStackTrace();
			}
		};

	};
	final private String fileName;
	private final static boolean verbose = false;

	public ColumnCache(File data, long maxInMemCacheSize) {
		this.cached = new ColumnCacheCachedValues<T>(maxInMemCacheSize);
		this.data = data;
		this.fileName = IOH.getFullPath(data);
		this.empty = valueOf(OH.EMPTY_BYTE_ARRAY);
		Runtime.getRuntime().addShutdownHook(this.shutdown);
	}

	public T get(long pointer) {
		if (pointer == POINTER_NULL)
			return null;
		if (pointer == POINTER_EMPTY)
			return empty;
		Cache<T> r = cached.get(pointer);
		if (r != null)
			return r.getValue();
		try {
			if (pointer >= dataIo.length())
				return null;
			this.dataIo.seek(pointer);
			int size = this.dataIo.readInt();
			if (size <= 0) {
				LH.warning(log, "For ", fileName, new Exception("Bad size at " + pointer + ": " + size));
				return null;
			}
			byte[] b = new byte[size];
			this.dataIo.readFully(b);

			T v = valueOf(b);
			this.cached.cache(pointer, v, size);
			return v;
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	public void remove(long pointer) {
		if (pointer == POINTER_EMPTY || pointer == POINTER_NULL)
			return;
		int size = this.cached.cache(pointer, null, 0);
		try {
			if (size == -1) {
				this.dataIo.seek(pointer);
				size = this.dataIo.readInt();
			}
			if (size <= 0) {
				LH.warning(log, "For ", fileName, new Exception("Bad size at " + pointer + ": " + size));
				return;
			}
			writeEmptyBlock(pointer, size);
			usedBytes -= size;
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}
	public T getAndRemove(long pointer) {
		if (pointer == POINTER_EMPTY)
			return empty;
		if (pointer == POINTER_NULL)
			return null;
		int size = this.cached.cache(pointer, null, 0);
		try {
			if (size == -1) {
				this.dataIo.seek(pointer);
				size = this.dataIo.readInt();
			}
			if (size <= 0) {
				LH.warning(log, "For ", fileName, new Exception("Bad size at " + pointer + ": " + size));
				return null;
			}

			byte[] b = new byte[size];
			this.dataIo.readFully(b);
			T r = valueOf(b);
			writeEmptyBlock(pointer, size);
			usedBytes -= size;
			this.cached.cache(pointer, null, 0);//TODO: this should be removed? It's redundant
			return r;
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}
	public long add(T value) {
		if (value == null)
			return POINTER_NULL;
		final byte[] bytes = valueFrom(value);
		if (bytes.length == 0)
			return POINTER_EMPTY;
		try {
			final int newSize = bytes.length;
			EmptyBlock block = this.emptyBlocks.findEq(newSize + 4);
			if (block != null) {
				long pointer = block.start;
				this.dataIo.seek(pointer);
				writeData(newSize, bytes);
				this.emptyBlocks.remove(block);
				usedBytes += newSize;
				this.cached.cache(pointer, value, newSize);
				return pointer;
			}
			block = this.emptyBlocks.findGe(newSize + 8);
			if (block != null) {
				long pointer = block.start;
				this.dataIo.seek(pointer);
				writeData(newSize, bytes);
				long remaining = block.size() - newSize - 8;
				if (remaining > MAX_BLOCK_SIZE) {
					int lb = block.getLargeBlockSubspaceRemaining();
					if (newSize + 4 < lb) {
						block.setLargeBlockSubspaceRemaining(block.getLargeBlockSubspaceRemaining() - (newSize + 4));
						writeEmpty((int) block.getLargeBlockSubspaceRemaining() - 4);
					} else {
						while (remaining > MAX_BLOCK_SIZE) {
							writeEmpty((MAX_BLOCK_SIZE - 4));
							IOH.skipBytes(this.dataIo, MAX_BLOCK_SIZE - 4);
							remaining -= MAX_BLOCK_SIZE;
						}
						writeEmpty((int) remaining);
						block.setLargeBlockSubspaceRemaining(MAX_BLOCK_SIZE);
					}
					this.emptyBlocks.updateStart(block, pointer + newSize + 4);
				} else {
					writeEmpty((int) remaining);
					this.emptyBlocks.updateStart(block, pointer + newSize + 4);
					block.setLargeBlockSubspaceRemaining(-1);
				}
				usedBytes += newSize;
				this.cached.cache(pointer, value, newSize);
				return pointer;
			}
			long pointer = this.dataIo.length();
			this.dataIo.seek(pointer);
			writeData(newSize, bytes);
			usedBytes += newSize;
			this.cached.cache(pointer, value, newSize);
			return pointer;
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	public long set(long pointer, T value) {
		if (value == null) {
			remove(pointer);
			return POINTER_NULL;
		}
		byte[] bytes = valueFrom(value);
		final int newSize = bytes.length;
		if (newSize == 0) {
			remove(pointer);
			return POINTER_EMPTY;
		}
		try {
			int oldSize;
			if (pointer == POINTER_NULL || pointer == POINTER_EMPTY) {
				oldSize = 0;
			} else {
				Cache<T> node = cached.get(pointer);
				if (node == null) {
					this.dataIo.seek(pointer);
					oldSize = this.dataIo.readInt();
				} else
					oldSize = node.getSize();
			}
			if (newSize == oldSize) {
				this.dataIo.seek(pointer + 4);
				if (verbose)
					LH.info(log, this.fileName, " Replacing block of same size: @", this.dataIo.getFilePointer(), ":", bytes.length);
				this.dataIo.write(bytes);
				cached.cache(pointer, value, newSize);
				return pointer;
			} else if (newSize + 4 <= oldSize) {//it's shrunk by at least 4, meaning we can write update inplace and mark remaining as empty
				this.dataIo.seek(pointer);
				writeData(newSize, bytes);
				int remaining = oldSize - newSize - 4;
				writeEmptyBlock(pointer + newSize + 4, remaining);
				usedBytes += newSize - oldSize;
				cached.cache(pointer, value, newSize);
				return pointer;
			}
			if (pointer != POINTER_EMPTY && pointer != POINTER_NULL) {
				int size = oldSize;
				OH.assertGt(oldSize, 0);
				writeEmptyBlock(pointer, oldSize);
				usedBytes += size;
				this.cached.cache(pointer, null, 0);
			}
			return add(value);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}

	}

	private void writeEmptyBlock(long position, int size) throws IOException {
		this.dataIo.seek(position);
		writeEmpty(size);
		this.emptyBlocks.add(position, position + 4 + size);
	}

	private void writeEmpty(int n) throws IOException {
		if (verbose)
			LH.info(log, "For ", this.fileName, " Journaling empty block: @", this.dataIo.getFilePointer(), ":", n);
		this.dataIo.writeInt(-n);
	}
	private void writeData(int n, byte[] data) throws IOException {
		if (verbose)
			LH.info(log, "For ", this.fileName, " Journaling data  block: @", this.dataIo.getFilePointer(), ":", n);
		OH.assertEq(n, data.length);
		this.dataIo.writeInt(n);
		this.dataIo.write(data);
	}

	public void clear() {
		try {
			this.dataIo.setLength(0);
			this.usedBytes = 0;
			this.cached.clear();
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	abstract protected byte[] valueFrom(T value);
	abstract protected T valueOf(byte[] r);

	public long getUsedDataBytes() {
		return usedBytes;
	}
	public long getAllocatedDataBytes() {
		try {
			return dataIo.length();
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}
	public double getFragmentation() {
		return 1 - ((double) this.usedBytes / this.getAllocatedDataBytes());
	}

	public long[] open(long[] pointers) {
		long start = System.currentTimeMillis();
		LongKeyMap<Integer> pointersToPos = new LongKeyMap<Integer>();
		String fullPath = fileName;
		long[] r = pointers.clone();
		for (int i = 0; i < pointers.length; i++) {
			long key = pointers[i];
			if (key != POINTER_EMPTY && key != POINTER_NULL)
				pointersToPos.put(key, i);
		}
		OH.assertTrue(isClosed);
		FastBufferedInputStream dataIo1 = null;
		boolean hasChange = false;
		try {
			long newLength = -1;
			this.usedBytes = 0;
			long pos = 0;
			if (data.exists()) {
				long length = this.data.length();
				if (pointers.length == 0 && length > 0) {
					LH.info(log, "Clean table, Resetting ", fullPath);
					IOH.delete(data);
					this.dataIo = new RandomAccessFile(data, RW);
					isClosed = false;
					return null;
				}
				dataIo1 = new FastBufferedInputStream(new FileInputStream(data));
				LH.info(log, "Openning ", fullPath, ": ", length, " byte(s)");
				while (pos != length) {
					if (length - pos < 4) {
						newLength = pos;
						LH.warning(log, "File '", fullPath, "' corruption resolved, trailing bytes at ", pos, " will be removed");
						break;
					}
					int blockSize = dataIo1.readInt();
					if (length < pos + 4 + Math.abs(blockSize)) {
						newLength = pos;
						LH.warning(log, "File '", fullPath, "' corruption resolved, trailing bytes at ", pos, " will be removed");
						break;
					}
					if (blockSize > 0) {
						pointersToPos.remove(pos);
						usedBytes += blockSize;
					} else
						this.emptyBlocks.add(pos, pos - blockSize + 4);
					pos += Math.abs(blockSize) + 4;
					IOH.skip(dataIo1, Math.abs(blockSize));
				}
				IOH.close(dataIo1);
			}
			if (!pointersToPos.isEmpty()) {
				hasChange = true;
				pointersToPos.keys();
				TreeMap<Integer, Long> errors = new TreeMap<Integer, Long>();
				for (Node<Integer> e : pointersToPos) {
					errors.put(e.getValue(), e.getKey());
					r[e.getValue().intValue()] = POINTER_NULL;
				}
				LH.warning(log, "File '", fullPath, "' corruption, ", errors.size(), " bad pointer(s):");
				for (Entry<Integer, Long> e : errors.entrySet())
					LH.warning(log, e.getKey(), " ==> ", e.getValue());
			}
			this.dataIo = new RandomAccessFile(data, RW);
			if (newLength != -1)
				dataIo.setLength(newLength);
		} catch (IOException e) {
			IOH.close(dataIo1);
			throw OH.toRuntime(e);
		}
		long end = System.currentTimeMillis();
		LH.info(log, "Opened ", fullPath, " in ", (end - start), "ms");
		isClosed = false;
		return hasChange ? r : null;
	}
	//takes a list of existing pointers, and returns a new list of pointers, the same length and respectively positioned
	//returns null if there is no change
	public long[] openAndDefrag(long[] pointers) {
		OH.assertTrue(isClosed);
		String fullPath = fileName;
		FastBufferedInputStream dataIo1;
		try {
			this.isClosed = false;
			if (!data.exists()) {
				this.dataIo = new RandomAccessFile(data, RW);
				this.usedBytes = getAllocatedDataBytes();
			}
			long length = data.length();
			if (pointers.length == 0 && length > 0) {
				LH.info(log, "Clean table, Resetting ", fullPath);
				IOH.delete(data);
				this.dataIo = new RandomAccessFile(data, RW);
				return null;
			}
			LH.info(log, "Defragging ", fullPath, ": ", length, " byte(s)");
			dataIo1 = new FastBufferedInputStream(new FileInputStream(data));
			long pos = 0;
			boolean needsCompacting = false;
			int n = 0;
			while (pos < length) {
				if (n == pointers.length) {
					needsCompacting = true;
					break;
				}
				long p = pointers[n++];
				if (p == POINTER_EMPTY || p == POINTER_NULL)
					continue;
				if (p != pos) {
					needsCompacting = true;
					break;
				}
				if (length - pos < 4) {
					needsCompacting = true;
					break;
				}
				int size = dataIo1.readInt();
				if (length < pos + 4 + size) {
					needsCompacting = true;
					break;
				}
				if (size < 0) {
					needsCompacting = true;
					break;
				}
				IOH.skip(dataIo1, size);
				pos += size + 4;
			}
			if (n != pointers.length)
				needsCompacting = true;
			if (!needsCompacting) {
				LH.info(log, "Already Defraged: ", fullPath);
				this.dataIo = new RandomAccessFile(data, RW);
				this.usedBytes = getAllocatedDataBytes();
				return null;
			}
		} catch (IOException e) {
			throw new RuntimeException("defragmenting files failed: " + fullPath, e);
		}
		LongKeyMap<Integer> pointersToPos = new LongKeyMap<Integer>();
		long[] r = new long[pointers.length];
		for (int i = 0; i < pointers.length; i++) {
			long key = pointers[i];
			if (key == POINTER_EMPTY || key == POINTER_NULL)
				r[i] = key;
			else
				pointersToPos.put(key, i);
		}
		File data2 = new File(data.getParent(), data.getName() + ".tmp");
		long length = -1;
		int emptyBlocksCount = 0, entriesCount = 0;
		long start = System.currentTimeMillis();
		try {
			dataIo1.close();
			dataIo1 = new FastBufferedInputStream(new FileInputStream(data));
			length = data.length();
			FastBufferedOutputStream dataIo2 = new FastBufferedOutputStream(new FileOutputStream(data2));
			byte[] buffer = new byte[1024];
			LH.info(log, "Defragmenting ", fullPath, ": ", length, " byte(s)");

			long pos = 0;
			long newPos = 0;
			while (pos < length) {
				if (length - pos < 4) {
					LH.warning(log, "File '", fullPath, "' corruption, trailing bytes at ", pos);
					break;
				}
				int size = dataIo1.readInt();
				if (length < pos + 4 + Math.abs(size)) {
					LH.warning(log, "File '", fullPath, "' corruption, eof at ", pos);
					break;
				}
				Integer existing = pointersToPos.remove(pos);
				if (existing != null) {
					if (size < 0) {
						emptyBlocksCount++;
						LH.warning(log, "File '", fullPath, "' corruption, row ", existing.intValue(), " has bad pointer ", pos);
						IOH.skip(dataIo1, Math.abs(size));
						r[existing.intValue()] = POINTER_NULL;
					} else {
						entriesCount++;
						r[existing.intValue()] = newPos;
						if (size > buffer.length)
							buffer = new byte[size * 2];
						dataIo1.readFully(buffer, 0, size);
						dataIo2.writeInt(size);
						dataIo2.write(buffer, 0, size);
						newPos += size + 4;
					}
				} else {
					emptyBlocksCount++;
					IOH.skip(dataIo1, Math.abs(size));
				}
				pos += Math.abs(size) + 4;
			}
			if (!pointersToPos.isEmpty()) {
				TreeMap<Integer, Long> errors = new TreeMap<Integer, Long>();
				for (Node<Integer> e : pointersToPos) {
					errors.put(e.getValue(), e.getKey());
					r[e.getValue().intValue()] = POINTER_NULL;
				}
				LH.warning(log, "File '", fullPath, "' corruption, ", errors.size(), " bad pointer(s):");
				for (Entry<Integer, Long> e : errors.entrySet())
					LH.warning(log, e.getKey(), " ==> ", e.getValue());
			}

			dataIo2.flush();
			IOH.close(dataIo1);
			IOH.close(dataIo2);
		} catch (IOException e) {
			throw new RuntimeException("defragmenting files failed: " + fullPath, e);
		}
		try {
			IOH.delete(data);
			IOH.moveForce(data2, data);
			this.dataIo = new RandomAccessFile(data, RW);
			this.usedBytes = getAllocatedDataBytes();
			long length2 = dataIo.length();
			long end = System.currentTimeMillis();
			LH.info(log, "Defragmented ", fullPath, " in ", (end - start), "ms: Reduced ", length, " ==> ", length2, " bytes (",
					length == 0 ? "Infinity" : (100 - (length2 * 100L / length)), "% reduction). Cleaned up ", emptyBlocksCount, " garbage blocks, ", entriesCount,
					" remaining entries");
		} catch (IOException e) {
			throw new RuntimeException("Something has gone very wrong, attempting to replace fragmented files with new, defragmented files: " + fullPath, e);
		}
		return r;
	}
	public void close(boolean removeData) {
		OH.assertFalse(isClosed);
		Runtime.getRuntime().removeShutdownHook(this.shutdown);
		if (removeData) {
			this.clear();
			this.emptyBlocks.clear();
			this.cached.clear();
			IOH.close(this.dataIo);
			this.data.delete();
		} else
			IOH.close(this.dataIo);
		this.isClosed = true;
	}

	public boolean isClosed() {
		return this.isClosed;
	}

	public T getEmptyValue() {
		return this.empty;
	}

	public long getMemorySize() {
		return this.cached.getCachedBytes();
	}

	public long getDiskSize() {
		try {
			return this.data.length();
		} catch (Exception e) {
			return -1;
		}
	}

}
