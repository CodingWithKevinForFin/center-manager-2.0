package com.f1.ami.center.table.persist;

import java.io.DataOutput;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiEncrypter;
import com.f1.ami.amicommon.msg.AmiDataEntity;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiPreparedRowImpl;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.base.Bytes;
import com.f1.base.Complex;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.UUID;
import com.f1.utils.AH;
import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.CompactLongKeyMap;
import com.f1.utils.structs.CompactLongKeyMap.KeyGetter;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.table.columnar.ColumnarColumnLong;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiTablePersister_Fast implements AmiTablePersister {
	private static final String FILE_EXTENSION = ".fast";
	private static final String FILE_EXTENSION_SECURE = ".sfast";
	private static final String FILE_EXTENSION_TMP = ".fasttmp";
	private static final String FILE_EXTENSION_SECURE_TMP = ".sfasttmp";

	private static final String OPTION_ON_MISMATCH = "ami.persisiter.fast.on.schema.mismatch.assume.legacy";

	private static final double DEFAULT_COMPACT_CUTOFF_PERCENT = .95;
	private Changes[] changesPool = new Changes[100];
	private int changesPoolSize;

	private AmiEncrypter encrypter;

	public void clearChanges() {
		int s = Math.min(changesInOrder.size(), changesPool.length - changesPoolSize);
		for (int i = 0; i < s; i++) {
			Changes c = changesInOrder.get(i);
			c.reset();
			changesPool[changesPoolSize++] = c;
		}
		this.changesInOrder.clear();
		this.changes.clear();
	}
	public Changes newChanges(AmiRowImpl amiid) {
		Changes r;
		if (changesPoolSize == 0)
			r = new Changes();
		else
			r = changesPool[--changesPoolSize];
		r.row = amiid;
		this.changesInOrder.add(r);
		this.changes.put(r);
		return r;
	}

	private static final byte STATE_ADD = 1;
	private static final byte STATE_UPDATE = 2;
	private static final byte STATE_DELETE = 4;

	public static class Changes {
		private byte state;
		private long mask0, mask64;
		private AmiRowImpl row;

		public void reset() {
			this.state = 0;
			this.mask0 = 0;
			this.mask64 = 0;
			this.row = null;
		}

		public void onUpdate(long mask0, long mask64) {
			this.mask0 |= mask0;
			this.mask64 |= mask64;
			state |= STATE_UPDATE;
		}

		public void onAdd(AmiRowImpl row) {
			this.row = row;
			state |= STATE_ADD;
		}

		public void onDelete() {
			state |= STATE_DELETE;
		}

	}

	final static private Logger log = LH.get();

	//	private static final Changes ZERO = new Changes(0, 0);

	private AmiTableImpl table;

	private Map<String, Object> options;

	private File persistDirectory;

	private AmiCenterState state;

	private double compactCutoffPercent = DEFAULT_COMPACT_CUTOFF_PERCENT;

	private boolean onSchemaMismatchAssumeLegacy = false;

	public AmiTablePersister_Fast(AmiTablePersisterFactory_Fast factory, Map<String, Object> options) {
		this.options = options;
	}

	@Override
	public void init(AmiTable sink) {
		this.table = (AmiTableImpl) sink;
		this.state = ((AmiImdbImpl) this.table.getImdb()).getState();
		this.onSchemaMismatchAssumeLegacy = this.state.getTools().getOptional(OPTION_ON_MISMATCH, Boolean.FALSE);
		String pd = (String) options.get(AmiTablePersisterFactory_Fast.OPTION_PERSIST_DIR);
		Double cp = (Double) options.get(AmiTablePersisterFactory_Fast.OPTION_PERSIST_COMPACT_PERCENT);
		String encrypter = (String) options.get(AmiTablePersisterFactory_Fast.OPTION_PERSIST_ENCRYPTER);
		if (SH.is(encrypter))
			this.encrypter = this.state.getEncrypter(encrypter);
		else
			this.encrypter = null;
		if (cp != null)
			this.compactCutoffPercent = cp;
		this.persistDirectory = pd == null ? state.getPersistDirectory() : new File(pd);
		try {
			IOH.ensureDir(this.persistDirectory);
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
		this.file = new File(this.persistDirectory, this.table.getName() + getFileExtension(false));
		this.fileTmp = new File(this.persistDirectory, this.table.getName() + getFileExtension(true));
		if (encrypter != null) {
			File nonSecureFile = new File(this.persistDirectory, this.table.getName() + FILE_EXTENSION);
			if (nonSecureFile.exists() && !this.file.exists()) {
				LH.info(log, "For table ", table.getName(), ", detected encryption has been enabled for a previously unencrypted table. Auto-encrypting...");
				encryptFile(nonSecureFile, this.file, this.encrypter);
			}
		} else {
			File secureFile = new File(this.persistDirectory, this.table.getName() + FILE_EXTENSION_SECURE);
			if (secureFile.exists())
				throw new RuntimeException("For Table " + this.table.getName() + ", " + AmiTablePersisterFactory_Fast.OPTION_PERSIST_ENCRYPTER
						+ " is not set but encrypted file found. This is likely a security issue but if intentional, manually decrypt using tools.sh on "
						+ IOH.getFullPath(secureFile));
		}
	}
	static public void encryptFile(File nonSecureFile, File secureFile, AmiEncrypter encrypter) {
		InputStream in = null;
		OutputStream out = null;
		try {
			LH.info(log, "Encrypting ", nonSecureFile.length(), " bytes from file: ", IOH.getFullPath(nonSecureFile), " to ", IOH.getFullPath(secureFile));
			in = new FastBufferedInputStream(new FileInputStream(nonSecureFile));
			out = new FastBufferedOutputStream(encrypter.encryptStream(new FileOutputStream(secureFile, false)));
			IOH.pipe(in, out, new byte[8092], false);
			IOH.close(in);
			IOH.close(out);
			File f2 = new File(nonSecureFile.getParent(), nonSecureFile.getName() + ".deleteme");
			IOH.moveForce(nonSecureFile, f2);
			LH.fine(log, "Please delete file:  " + IOH.getFullPath(f2));
		} catch (Exception e) {
			IOH.close(in);
			IOH.close(out);
			throw new RuntimeException("Error encrypting file: " + IOH.getFullPath(nonSecureFile) + " ==> " + IOH.getFullPath(secureFile), e);
		}
	}

	public String getFileExtension(boolean isTmp) {
		if (encrypter == null)
			return isTmp ? FILE_EXTENSION_TMP : FILE_EXTENSION;
		else
			return isTmp ? FILE_EXTENSION_SECURE_TMP : FILE_EXTENSION_SECURE;
	}

	private static final KeyGetter<Changes> CHANGES_KEY_GETTER = new KeyGetter<Changes>() {

		@Override
		public long getKey(Changes object) {
			return object.row.getAmiId();
		}

	};

	//	private List<AmiRowImpl> added = new ArrayList<AmiRowImpl>();
	private CompactLongKeyMap<Changes> changes = new CompactLongKeyMap<Changes>("Changes", CHANGES_KEY_GETTER, 16);
	private List<Changes> changesInOrder = new ArrayList<Changes>();

	private FastBufferedOutputStream o;

	private File file;
	private File fileTmp;

	private long updateBytes, addBytes;
	private long addCount;
	private long deleteCount;
	private long updateCount;

	private long origFileSize;

	@Override
	public void onRemoveRow(AmiRowImpl row) {
		Changes c = this.changes.get(row.getAmiId());
		if (c == null)
			c = newChanges(row);
		c.onDelete();
	}

	@Override
	public void onAddRow(AmiRowImpl row) {
		Changes c = newChanges(row);
		c.onAdd(row);
	}

	@Override
	public void onRowUpdated(AmiRowImpl row, long mask0, long mask64) {
		if (mask0 == 0L && mask64 == 0)
			return;
		Changes c = this.changes.get(row.getAmiId());
		if (c != null) {
			c.onUpdate(mask0, mask64);
		} else {
			c = newChanges(row);
			c.onUpdate(mask0, mask64);
		}
	}
	@Override
	public boolean loadTableFromPersist(CalcFrameStack sf) {
		long nextFountainId = this.state.createNextId();
		LongKeyMap<Long> amiIdMapping = null;
		boolean needsCompacting = false;
		if (!file.isFile()) {
			needsCompacting = true;
		} else {
			FastBufferedInputStream in = null;
			final long totalBytes;
			final byte srcDirectives[];
			final byte srcTypes[];
			final byte tgtDirectives[];
			final byte tgtTypes[];
			final AmiColumn target[];
			final long start = System.currentTimeMillis();
			try {
				totalBytes = this.file.length();
				LH.info(log, "Restoring Table from file: ", IOH.getFullPath(file));
				in = openFileForRead(this.file);
				int columnsCount = in.readShort();
				srcTypes = new byte[columnsCount];
				srcDirectives = new byte[columnsCount];
				tgtTypes = new byte[columnsCount];
				tgtDirectives = new byte[columnsCount];
				target = new AmiColumn[columnsCount];
				for (int i = 0; i < columnsCount; i++) {
					final String name = in.readUTF();
					final byte type = in.readByte();
					if (type == AmiDataEntity.PARAM_TYPE_RESERVED) {
						srcDirectives[i] = in.readByte();
						srcTypes[i] = in.readByte();
					} else {
						srcTypes[i] = type;
						srcDirectives[i] = 0;
					}
					final AmiColumn col = this.table.getColumnNoThrow(name);
					if (col != null) {
						target[i] = col;
						tgtTypes[i] = col.getAmiType();
						tgtDirectives[i] = getDirectives(col);
						if (tgtDirectives[i] != srcDirectives[i]) {
							if (onSchemaMismatchAssumeLegacy) {
								srcDirectives[i] = tgtDirectives[i];
								needsCompacting = true;
								LH.warning(log, "For `", this.table.getName(), "`.`", col.getName(), "` Non-Compatible changes detected, '", toDirectivesString(srcDirectives[i]),
										"' became '", toDirectivesString(tgtDirectives[i]), "', " + OPTION_ON_MISMATCH + " is true, so assuming mismatch");
							} else
								LH.warning(log, "For `", this.table.getName(), "`.`", col.getName(),
										"` Non-Compatible changes detected, so data will be set to the default for this column: '", toDirectivesString(srcDirectives[i]),
										"' became '", toDirectivesString(tgtDirectives[i]), "'");
						}
					}
				}
			} catch (Exception e) {
				IOH.close(in);
				throw new RuntimeException("For '" + this.table.getName() + "': Faital exception processing header from: " + IOH.getFullPath(file), e);
			}
			final AmiColumnImpl<ColumnarColumnLong> reservedColumnAmiId = this.table.getReservedColumnAmiId();

			needsCompacting |= (!AH.eq(tgtTypes, srcTypes) || !AH.eq(tgtDirectives, srcDirectives));
			long headerSize = in.getBytesConsumed();
			Pointers pointers = new Pointers(table);

			int maxBlocks = 0;
			int blocksRead = 0;
			phase: for (int phaseCount = 0;; phaseCount++) {
				LongSet deletes = new LongSet();
				AmiPreparedRowImpl row = table.createAmiPreparedRowForRecovery();
				blocksRead = 0;
				try {
					int count = 0;
					boolean atEob = true;
					outer: for (;;) {
						if ((++count % 1000000) == 0)
							LH.info(log, "Processed ", count, " instructions from ", blocksRead, " blocks for '", this.table.getName(), "' (",
									(in.getBytesConsumed() * 100L / totalBytes), "%)");
						int code = in.read();
						switch (code) {
							case -1:
								if (atEob) {
									LH.info(log, "Processed ", count, " instructions from ", blocksRead, " blocks for '", this.table.getName(), "' (Complete)");
									break phase;
								} else
									throw new EOFException();
							case CODE_DELETE: {
								atEob = false;
								needsCompacting = true;
								long amiid = readAmiId(in, amiIdMapping);
								AmiRowImpl row2 = table.getAmiRowByAmiId(amiid);
								if (row2 != null)
									table.removeAmiRow(row2, sf);
								else
									deletes.add(amiid);

								break;
							}
							case CODE_UPDATE: {
								atEob = false;
								needsCompacting = true;
								long amiId = readAmiId(in, amiIdMapping);
								row.reset();
								row.setIgnoreWriteFailed(true);
								readRow(in, row, pointers, srcDirectives, srcTypes, tgtDirectives, tgtTypes, target);
								if (!deletes.contains(amiId)) {
									AmiRowImpl row2 = table.getAmiRowByAmiId(amiId);
									if (row2 != null) {
										table.updateAmiRow(row2, row, sf);
										pointers.apply(row2);
									} else
										LH.warning(log, "File corruption at ", count, ": Attempting to UPDATE missing record. AMIID=", amiId, " updates=", row);
								}

								break;
							}
							case CODE_INSERT: {
								atEob = false;
								row.reset();
								row.setIgnoreWriteFailed(true);
								long amiId = readAmiId(in, amiIdMapping);
								readRow(in, row, pointers, srcDirectives, srcTypes, tgtDirectives, tgtTypes, target);
								if (amiId >= nextFountainId) {
									//									phaseCount = 1;
									//									throw new IllegalStateException(
									//											"Persist file has ami-id=" + amiId + " which is ahead of id fountain(" + nextFountainId + "). See idfountain.path property");
									//
									if (amiIdMapping == null) {
										LH.warning(log, "Persist File has ami-id=" + amiId + " which is ahead of id fountain(" + nextFountainId + "). Mapping to new id fountain");
										amiIdMapping = new LongKeyMap<Long>();
										needsCompacting = true;
									}
									long newId = this.state.createNextId();
									amiIdMapping.put(amiId, newId);
									amiId = newId;
									if (reservedColumnAmiId != null)
										row.setLong(reservedColumnAmiId, newId);
								}
								if (!deletes.contains(amiId)) {
									AmiRowImpl row2 = (AmiRowImpl) table.insertAmiRow(row, amiId, sf);
									if (row2 != null)
										pointers.apply(row2);
									else
										LH.warning(log, "File corruption at ", count,
												": Attempting to add Row failed, perhaps constraints or schema changes have caused this. AMIID=", amiId, " row=", row);
								}
								break;
							}
							case CODE_DONE:
								atEob = true;
								blocksRead++;
								if (phaseCount == 1 && blocksRead == maxBlocks) {
									LH.info(log, "Processed ", count, " instructions from ", blocksRead, " blocks for '", this.table.getName(), "' (Complete up to corrupt block)");
									break phase;
								}
								break;
							default:
								throw new RuntimeException("Bad code: " + code);
						}
					}
				} catch (Exception e) {
					if (phaseCount != 0)
						throw new RuntimeException("For table' " + this.table.getName() + "': Faital exception on reprocess for: " + IOH.getFullPath(file), e);
					if (!(e instanceof EOFException)) {
						LH.warning(log, "File truncated, last transaction may have been partially written. ", e);
						try {
							File dst = new File(this.file.getParent(), this.file.getName() + ".corrupt." + DateMillis.formatMinimal(System.currentTimeMillis()));
							LH.warning(log, "Backing up corrupt file to ", IOH.getFullPath(dst), " (", file.length(), " bytes)");
							IOH.copy(this.file, dst, 1024 * 1024, true);
						} catch (Exception e2) {
							LH.warning(log, "Failed to backup file: ", IOH.getFullPath(this.file), e2);
						}
					}
				} finally {
					IOH.close(in);
				}
				LH.warning(log, "For Table '", this.table.getName(), "' Block ", blocksRead, " in ", IOH.getFullPath(file),
						" is corrupt. Will truncate and reprocess up to that block");
				maxBlocks = blocksRead;
				needsCompacting = true;
				try {
					in = openFileForRead(this.file);
					IOH.skip(in, headerSize);
					table.clearRows(sf);
				} catch (Exception e) {
					throw new RuntimeException("For table '" + this.table.getName() + "': Faital exception resetting for reprocess: " + IOH.getFullPath(file), e);
				}
			}
			long end = System.currentTimeMillis();
			LH.info(log, "Restored ", table.getRowsCount(), " row(s) to '", table.getName(), "' from file in ", end - start, " milliseconds: ", IOH.getFullPath(file));

		}
		try {
			this.o = openFileForWrite(this.file, true);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Failed to start appending to file " + IOH.getFullPath(file), e);
		}
		return needsCompacting;
	}
	private FastBufferedInputStream openFileForRead(File file) throws FileNotFoundException {
		final FastBufferedInputStream in;
		if (encrypter != null)
			in = new FastBufferedInputStream(encrypter.decryptStream(new FileInputStream(file)));
		else
			in = new FastBufferedInputStream(new FileInputStream(file));
		return in;
	}
	private FastBufferedOutputStream openFileForWrite(File file, boolean append) throws FileNotFoundException {
		final FastBufferedOutputStream in;
		if (encrypter != null)
			in = new FastBufferedOutputStream(encrypter.encryptStream(new FileOutputStream(file, append)));
		else
			in = new FastBufferedOutputStream(new FileOutputStream(file, append));
		return in;
	}

	private long readAmiId(FastBufferedInputStream in, LongKeyMap<Long> amiIdMapping) throws IOException {
		long amiId = in.readLong();
		if (amiIdMapping == null)
			return amiId;
		Long mappedId = amiIdMapping.get(amiId);
		return mappedId == null ? amiId : mappedId.longValue();
	}

	private Object toDirectivesString(byte b) {
		return b == DIRECTIVES_MASK_ONDISK ? "ONDISK" : "";
	}

	private void readRow(FastBufferedInputStream in, AmiPreparedRow row, Pointers pointers, byte[] srcDirectives, byte[] srcTypes, byte[] tgtDirectives, byte[] tgtTypes,
			AmiColumn[] target) throws IOException {
		for (;;) {
			int colPos = in.readShort();
			if (colPos == Short.MAX_VALUE)
				return;
			if (colPos < 0) {
				AmiColumnImpl t = (AmiColumnImpl) target[-1 - colPos];
				if (t != null && t.getAllowNull())
					row.setNull(t.getLocation());
			} else {
				byte srcType = srcTypes[colPos];
				AmiColumnImpl t = (AmiColumnImpl) target[colPos];
				byte srcDirective = srcDirectives[colPos];
				if (t != null && tgtDirectives[colPos] == srcDirective) {
					byte tgtType = tgtTypes[colPos];
					if (tgtType != srcType) {
						if (t.getIsOnDisk()) {
							t.setOnDiskEmptyValue(row);
							continue;
						}
						try {
							switch (srcType) {
								case AmiTable.TYPE_BOOLEAN:
									row.setComparable(t, in.readBoolean() ? 1 : 0);
									break;
								case AmiTable.TYPE_DOUBLE:
									row.setComparable(t, in.readDouble());
									break;
								case AmiTable.TYPE_ENUM:
								case AmiTable.TYPE_STRING:
									row.setComparable(t, in.readUTF());
									break;
								case AmiTable.TYPE_FLOAT:
									row.setComparable(t, in.readFloat());
									break;
								case AmiTable.TYPE_INT:
									row.setComparable(t, in.readInt());
									break;
								case AmiTable.TYPE_SHORT:
									row.setComparable(t, in.readShort());
									break;
								case AmiTable.TYPE_BYTE:
									row.setComparable(t, in.readByte());
									break;
								case AmiTable.TYPE_CHAR:
									row.setComparable(t, in.readChar());
									break;
								case AmiTable.TYPE_UTC:
									row.setComparable(t, new DateMillis(in.readLong()));
									break;
								case AmiTable.TYPE_UTCN:
									row.setComparable(t, new DateNanos(in.readLong()));
									break;
								case AmiTable.TYPE_LONG:
									row.setComparable(t, in.readLong());
									break;
								case AmiTable.TYPE_BINARY:
									byte[] data = new byte[in.readInt()];
									in.readFully(data);
									row.setComparable(t, new Bytes(data));
									break;
								default:
									throw new RuntimeException("Unknown type at " + colPos + ": " + srcType);
							}
						} catch (Exception e) {
							throw new RuntimeException(
									"Failed to coerce " + AmiTableUtils.toStringForDataType(srcType) + " to " + AmiTableUtils.toStringForDataType(tgtType) + " at " + colPos + ": ",
									e);
						}
					} else {
						if (t.getIsOnDisk()) {
							pointers.setPointerAt(t.getLocation(), in.readLong());
							t.setOnDiskEmptyValue(row);
							continue;
						}
						switch (srcType) {
							case AmiTable.TYPE_BOOLEAN:
								row.setLong(t, in.readBoolean() ? 1 : 0);
								break;
							case AmiTable.TYPE_DOUBLE:
								row.setDouble(t, in.readDouble());
								break;
							case AmiTable.TYPE_ENUM:
							case AmiTable.TYPE_STRING:
								row.setString(t, in.readUTF());
								break;
							case AmiTable.TYPE_FLOAT:
								row.setDouble(t, in.readFloat());
								break;
							case AmiTable.TYPE_INT:
								row.setLong(t, in.readInt());
								break;
							case AmiTable.TYPE_SHORT:
								row.setLong(t, in.readShort());
								break;
							case AmiTable.TYPE_BYTE:
								row.setLong(t, in.readByte());
								break;
							case AmiTable.TYPE_CHAR:
								row.setLong(t, in.readChar());
								break;
							case AmiTable.TYPE_UTC:
							case AmiTable.TYPE_UTCN:
							case AmiTable.TYPE_LONG:
								row.setLong(t, in.readLong());
								break;
							case AmiTable.TYPE_COMPLEX: {
								Complex v = new Complex(in.readDouble(), in.readDouble());
								row.setComparable(t, v);
								break;
							}
							case AmiTable.TYPE_UUID: {
								UUID v = new UUID(in.readLong(), in.readLong());
								row.setComparable(t, v);
								break;
							}
							case AmiTable.TYPE_BIGINT: {
								byte[] data = new byte[in.readInt()];
								in.readFully(data);
								BigInteger v = new BigInteger(data);
								row.setComparable(t, v);
								break;
							}
							case AmiTable.TYPE_BIGDEC: {
								int scale = in.readInt();
								byte[] data = new byte[in.readInt()];
								in.readFully(data);
								BigInteger v = new BigInteger(data);
								row.setComparable(t, new BigDecimal(v, scale));
								break;
							}
							case AmiTable.TYPE_BINARY:
								byte[] data = new byte[in.readInt()];
								in.readFully(data);
								row.setComparable(t, new Bytes(data));
								break;
							default:
								throw new RuntimeException("Unknown type at " + colPos + ": " + srcType);
						}
					}
				} else {
					switch (srcType) {
						case AmiTable.TYPE_BOOLEAN:
							in.skipBytes(1);
							break;
						case AmiTable.TYPE_DOUBLE:
							in.skipBytes(8);
							break;
						case AmiTable.TYPE_ENUM:
							row.setString(t, in.readUTF());
							break;
						case AmiTable.TYPE_STRING:
							if (srcDirective == DIRECTIVES_MASK_ONDISK)
								in.skipBytes(8);
							else
								in.readUTF();
							break;
						case AmiTable.TYPE_FLOAT:
							in.skipBytes(4);
							break;
						case AmiTable.TYPE_INT:
							in.skipBytes(4);
							break;
						case AmiTable.TYPE_SHORT:
							in.skipBytes(2);
							break;
						case AmiTable.TYPE_BYTE:
							in.skipBytes(1);
							break;
						case AmiTable.TYPE_CHAR:
							in.skipBytes(2);
							break;
						case AmiTable.TYPE_UTC:
						case AmiTable.TYPE_UTCN:
						case AmiTable.TYPE_LONG:
							in.skipBytes(8);
							break;
						case AmiTable.TYPE_COMPLEX: {
							in.skipBytes(16);
							break;
						}
						case AmiTable.TYPE_UUID: {
							in.skipBytes(16);
							break;
						}
						case AmiTable.TYPE_BIGINT: {
							in.skipBytes(in.readInt());
							break;
						}
						case AmiTable.TYPE_BIGDEC: {
							in.skip(4);
							in.skipBytes(in.readInt());
							break;
						}
						case AmiTable.TYPE_BINARY:
							if (srcDirective == DIRECTIVES_MASK_ONDISK)
								in.skipBytes(8);
							else
								in.skipBytes(in.readInt());
							break;
						default:
							throw new RuntimeException("Unknown type at " + colPos + ": " + srcType);
					}
				}
			}
		}
	}
	@Override
	public void saveTableToPersist(CalcFrameStack sf) {
		long start = System.currentTimeMillis();
		saveTable();
		long end = System.currentTimeMillis();
		LH.info(log, "Saved file snapshot: ", IOH.getFullPath(file), " (", file.length(), " bytes in ", end - start, " millis)");
	}

	private void saveTable() {
		IOH.close(this.o);
		this.o = null;
		this.updateBytes = this.addBytes = this.updateCount = this.addCount = this.deleteCount = 0;
		FastBufferedOutputStream out = null;
		try {
			this.origFileSize = 0;
			out = openFileForWrite(this.fileTmp, false);
			writeTable(out);
			out.flush();
			IOH.close(out);
			IOH.moveForce(this.fileTmp, file);
		} catch (Exception e) {
			throw new RuntimeException("Failed to write to file " + IOH.getFullPath(file), e);
		} finally {
			IOH.close(out);
		}

		try {
			this.origFileSize = this.file.length();
			this.o = openFileForWrite(this.file, true);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Failed to start appending to file " + IOH.getFullPath(file), e);
		}
	}

	private void writeTable(FastBufferedOutputStream out) {
		try {
			writeHeader(out);
			for (int i = 0, l = table.getRowsCount(); i < l; i++)
				writeAdd(out, table.getAmiRowAt(i));
			writeDone(out);
		} catch (IOException e) {
			LH.warning(log, "For Table '", this.table.getName(), "' Error writing table ", e);
		}
	}

	@Override
	public void clear(CalcFrameStack sf) {
		this.changes.clear();
		this.changesInOrder.clear();
	}

	@Override
	public void flushChanges(CalcFrameStack sf) {
		try {
			boolean wroteBytes = false;
			for (int i = 0, l = this.changesInOrder.size(); i < l; i++) {
				Changes change = this.changesInOrder.get(i);
				switch (change.state) {
					case STATE_ADD:
					case STATE_ADD | STATE_UPDATE:
						writeAdd(o, change.row);
						wroteBytes = true;
						break;
					case STATE_DELETE:
					case STATE_DELETE | STATE_UPDATE:
						writeDelete(o, change.row.getAmiId());
						wroteBytes = true;
						break;
					case STATE_DELETE | STATE_ADD:
					case STATE_DELETE | STATE_UPDATE | STATE_ADD:
						continue;
					case STATE_UPDATE:
						writeUpdate(o, change.row, change.mask0, change.mask64);
						wroteBytes = true;
						break;
					default:
						LH.warning(log, "Unknown state: " + change.state);

				}
			}
			this.clearChanges();
			if (wroteBytes) {
				o.writeByte(CODE_DONE);
				this.o.flush();
			}
			double deleteSize = addCount == 0 ? 0d : ((9 + (double) addBytes / addCount) * deleteCount);
			if ((deleteSize + updateBytes) / (this.origFileSize + o.getTotalBytesWritten()) > this.compactCutoffPercent) {
				long before = this.file.length();
				long start = System.currentTimeMillis();
				saveTable();
				long end = System.currentTimeMillis();
				long after = this.file.length();
				LH.info(log, "Compacted table ", this.table.getName(), " to file : ", IOH.getFullPath(file), " in ", (end - start), " ms. From ", before, " bytes to ", after,
						" bytes. (", 100 - (after * 100L / before), "% reduction)");
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to start appending to file " + IOH.getFullPath(file), e);
		}
	}

	private static final int CODE_INSERT = 1;
	private static final int CODE_UPDATE = 2;
	private static final int CODE_DELETE = 3;
	private static final int CODE_DONE = 4;

	private static final byte DIRECTIVES_MASK_ONDISK = 1;

	private void writeHeader(DataOutput out) throws IOException {
		out.writeShort(table.getColumnsCount());
		for (int i = 0, l = table.getColumnsCount(); i < l; i++) {
			AmiColumnImpl col = table.getColumnAt(i);
			out.writeUTF(col.getName());
			byte directives = getDirectives(col);
			if (directives != 0) {
				out.writeByte(AmiDataEntity.PARAM_TYPE_RESERVED);
				out.writeByte(directives);
				out.writeByte(col.getAmiType());
			} else
				out.writeByte(col.getAmiType());
		}
	}
	static private byte getDirectives(AmiColumn col) {
		if (col.getIsOnDisk()) {
			return DIRECTIVES_MASK_ONDISK;
		}
		return 0;
	}
	private void writeAdd(FastBufferedOutputStream out, AmiRow row) throws IOException {
		long before = out.getTotalBytesWritten();
		out.writeByte(CODE_INSERT);
		out.writeLong(row.getAmiId());
		for (int i = 0, l = table.getColumnsCount(); i < l; i++)
			writeValue(out, i, row, false);
		out.writeShort(Short.MAX_VALUE);
		addBytes += out.getTotalBytesWritten() - before;
		addCount++;
	}
	private void writeDelete(FastBufferedOutputStream out, long amiId) throws IOException {
		out.writeByte(CODE_DELETE);
		out.writeLong(amiId);
		deleteCount++;
	}
	private void writeUpdate(FastBufferedOutputStream out, AmiRow row, long mask0, long mask64) throws IOException {
		long before = o.getTotalBytesWritten();
		out.writeByte(CODE_UPDATE);
		out.writeLong(row.getAmiId());
		if (mask0 != 0) {
			int maxCol = Math.min(table.getColumnsCount(), 64);
			for (int col = MH.indexOfBitSetBefore(mask0, maxCol); col != -1; col = MH.indexOfBitSet(mask0, col + 1, maxCol))
				writeValue(out, col, row, true);
		}
		if (mask64 != 0) {
			int maxCol2 = table.getColumnsCount() - 64;
			for (int col = MH.indexOfBitSetBefore(mask64, maxCol2); col != -1; col = MH.indexOfBitSet(mask64, col + 1, maxCol2))
				writeValue(out, col + 64, row, true);
		}
		out.writeShort(Short.MAX_VALUE);
		updateBytes += out.getTotalBytesWritten() - before;
		updateCount++;
	}

	private void writeValue(FastBufferedOutputStream out, int i, AmiRow row, boolean writeNulls) throws IOException {
		AmiColumnImpl col = (AmiColumnImpl) row.getAmiTable().getColumnAt(i);
		if (col.getIsNull(row)) {
			if (writeNulls) {
				out.writeShort(-1 - i);
			}
		} else {
			if (col.getIsOnDisk()) {
				out.writeShort(i);
				out.writeLong(col.getOnDiskLong(row));
				return;
			}
			switch (col.getAmiType()) {
				case AmiTable.TYPE_BOOLEAN:
					out.writeShort(i);
					out.writeBoolean(col.getLong(row) != 0L);
					break;
				case AmiTable.TYPE_DOUBLE:
					out.writeShort(i);
					out.writeDouble(col.getDouble(row));
					break;
				case AmiTable.TYPE_ENUM:
				case AmiTable.TYPE_STRING:
					out.writeShort(i);
					out.writeUTFSupportLarge(col.getString(row));
					break;
				case AmiTable.TYPE_FLOAT:
					out.writeShort(i);
					out.writeFloat((float) col.getDouble(row));
					break;
				case AmiTable.TYPE_INT:
					out.writeShort(i);
					out.writeInt((int) col.getLong(row));
					break;
				case AmiTable.TYPE_SHORT:
					out.writeShort(i);
					out.writeShort((short) col.getLong(row));
					break;
				case AmiTable.TYPE_BYTE:
					out.writeShort(i);
					out.writeByte((byte) col.getLong(row));
					break;
				case AmiTable.TYPE_CHAR:
					out.writeShort(i);
					out.writeChar((char) col.getLong(row));
					break;
				case AmiTable.TYPE_UTC:
				case AmiTable.TYPE_UTCN:
				case AmiTable.TYPE_LONG:
					out.writeShort(i);
					out.writeLong(col.getLong(row));
					break;
				case AmiTable.TYPE_COMPLEX: {
					out.writeShort(i);
					Complex c = (Complex) col.getComparable(row);
					out.writeDouble(c.real());
					out.writeDouble(c.imaginary());
					break;
				}
				case AmiTable.TYPE_UUID: {
					out.writeShort(i);
					UUID c = (UUID) col.getComparable(row);
					out.writeLong(c.getMostSignificantBits());
					out.writeLong(c.getLeastSignificantBits());
					break;
				}
				case AmiTable.TYPE_BIGINT: {
					out.writeShort(i);
					BigInteger c = (BigInteger) col.getComparable(row);
					byte[] bytes = c.toByteArray();
					out.writeInt(bytes.length);
					out.write(bytes);
					break;
				}
				case AmiTable.TYPE_BIGDEC: {
					out.writeShort(i);
					BigDecimal c = (BigDecimal) col.getComparable(row);
					byte[] bytes = c.unscaledValue().toByteArray();
					out.writeInt(c.scale());
					out.writeInt(bytes.length);
					out.write(bytes);
					break;
				}
				case AmiTable.TYPE_BINARY:
					out.writeShort(i);
					byte[] data = ((Bytes) col.getComparable(row)).getBytes();
					out.writeInt(data.length);
					out.write(data);
					break;
				default:
					LH.warning(log, "Unknown type for ", col.getAmiTable().getName(), ".", col.getName(), ": " + col.getAmiType());
			}
		}
	}

	private void writeDone(DataOutput out) throws IOException {
		out.writeByte(CODE_DONE);
	}

	@Override
	public void drop(CalcFrameStack sf) {
		IOH.close(this.o);
		try {
			IOH.delete(this.file);
		} catch (IOException e) {
		}
		try {
			IOH.delete(this.fileTmp);
		} catch (IOException e) {
		}
	}

	@Override
	public void onTableRename(String oldName, String name, CalcFrameStack sf) {
		File newFile = new File(this.persistDirectory, name + getFileExtension(false));
		File newFileTmp = new File(this.persistDirectory, name + getFileExtension(true));
		IOH.close(o);
		if (fileTmp.exists())
			IOH.moveForce(fileTmp, newFileTmp);
		if (file.exists())
			IOH.moveForce(file, newFile);
		this.file = newFile;
		this.fileTmp = newFileTmp;
		try {
			this.o = openFileForWrite(this.file, true);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Failed to start appending to file " + IOH.getFullPath(file), e);
		}
	}

	public static class Pointers {

		final private int[] columnPos2Array;
		final private BitSet flags;
		final private long[] pointers;
		final private AmiColumnImpl[] columns;

		public Pointers(AmiTableImpl table) {
			int columnsCount = table.getColumnsCount();
			this.columnPos2Array = new int[columnsCount];
			AmiColumnImpl[] cols = new AmiColumnImpl[0];
			for (int i = 0; i < columnsCount; i++) {
				AmiColumnImpl<?> column = table.getColumnAt(i);
				if (column.getIsOnDisk()) {
					columnPos2Array[column.getLocation()] = cols.length;
					cols = AH.append(cols, column);
				} else
					columnPos2Array[column.getLocation()] = -1;
			}
			if (cols.length > 0) {
				this.flags = new BitSet(cols.length);
				this.pointers = new long[cols.length];
				this.columns = cols;
			} else {
				this.flags = null;
				this.pointers = null;
				this.columns = null;
			}
		}
		public void apply(AmiRowImpl row) {
			if (flags == null)
				return;
			for (int i = flags.nextSetBit(0); i >= 0; i = flags.nextSetBit(i + 1))
				columns[i].setOnDiskLong(row, pointers[i]);
			this.flags.clear();
		}

		public void setPointerAt(int i, long pointer) {
			i = columnPos2Array[i];
			this.flags.set(i, true);
			this.pointers[i] = pointer;
		}

		public void clear() {
			flags.clear();
		}
		public long getPointerAt(int i) {
			return pointers[i];
		}
		public int getNextSetPointer(int pos) {
			return flags.nextSetBit(pos);
		}
	}
}
