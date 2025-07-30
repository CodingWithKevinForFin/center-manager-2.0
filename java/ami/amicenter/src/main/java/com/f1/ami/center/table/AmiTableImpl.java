package com.f1.ami.center.table;

import java.io.File;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.msg.AmiCenterChanges;
import com.f1.ami.amicommon.msg.AmiDataEntity;
import com.f1.ami.center.AmiCenterAmiUtilsForTable;
import com.f1.ami.center.AmiCenterChangesMessageBuilder;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.AmiCenterUtils;
import com.f1.ami.center.table.keygen.AmiKeyGenerator;
import com.f1.ami.center.table.persist.AmiTablePersister;
import com.f1.ami.center.table.persist.AmiTablePersisterBinding;
import com.f1.ami.center.table.prepared.AmiPreparedCell;
import com.f1.ami.center.triggers.AmiTimedRunnable;
import com.f1.ami.center.triggers.AmiTrigger;
import com.f1.ami.center.triggers.AmiTriggerBindingImpl;
import com.f1.base.Bytes;
import com.f1.base.LongIterable;
import com.f1.base.Row;
import com.f1.utils.AH;
import com.f1.utils.ByteHelper;
import com.f1.utils.CH;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.LH;
import com.f1.utils.LongArrayList;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.CompactLongKeyMap;
import com.f1.utils.structs.CompactLongKeyMap.KeyGetter;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntKeyMap.Node;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.MapInMap;
import com.f1.utils.structs.table.columnar.ColumnCache_Bytes;
import com.f1.utils.structs.table.columnar.ColumnCache_String;
import com.f1.utils.structs.table.columnar.ColumnarColumn;
import com.f1.utils.structs.table.columnar.ColumnarColumnEnum;
import com.f1.utils.structs.table.columnar.ColumnarColumnInt;
import com.f1.utils.structs.table.columnar.ColumnarColumnLong;
import com.f1.utils.structs.table.columnar.ColumnarColumnObject;
import com.f1.utils.structs.table.columnar.ColumnarColumnObject_Cached;
import com.f1.utils.structs.table.columnar.ColumnarColumnString_BitMap;
import com.f1.utils.structs.table.columnar.ColumnarColumnString_CompactAscii;
import com.f1.utils.structs.table.columnar.ColumnarColumnString_CompactChars;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarRowFactory;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.columnar.ColumnarTableList;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiTableImpl implements AmiImdbFlushable, AmiTable, ColumnarRowFactory, KeyGetter<AmiRowImpl> {

	public static final String NULL_CONSTRAINT = "NULL_CONSTRAINT";
	final private static Logger log = LH.get();
	final private static boolean logIsFine = log.isLoggable(Level.FINE);
	private static final long DEFAULT_COLUMN_CACHE_SIZE = 1000 * 1000 * 100;
	private AmiColumnImpl<ColumnarColumnLong> reservedColumnD;
	private AmiColumnImpl<ColumnarColumnObject<String>> reservedColumnP;
	private AmiColumnImpl<ColumnarColumnObject<String>> reservedColumnI;
	private AmiColumnImpl<ColumnarColumnLong> reservedColumnE;
	private AmiColumnImpl<ColumnarColumnInt> reservedColumnV;
	private AmiColumnImpl<ColumnarColumnLong> reservedColumnM;
	private AmiColumnImpl<ColumnarColumnLong> reservedColumnC;
	private AmiColumnImpl<ColumnarColumnEnum> reservedColumnT;

	final private ColumnarTable table;
	final private AmiCenterState state;
	private short type, origType;

	private MapInMap<String, String, AmiRowImpl> indexPI;
	final private StringBuilder tmpbuf = new StringBuilder();
	private HashMap<String, AmiRowImpl> indexI;

	//Indexing
	private Map<String, AmiIndexImpl> indexesByName = new HashMap<String, AmiIndexImpl>();
	private AmiIndexImpl[] indexes = new AmiIndexImpl[0];
	private long columnsParticipatingInIndexMask = 0;
	private long columnsParticipatingInUniqueIndexMask = 0;

	//columns
	final private IntKeyMap<AmiColumnImpl<?>> columns = new IntKeyMap<AmiColumnImpl<?>>();
	private AmiColumnImpl[] columnsByPos;
	private AmiColumnImpl[] nonReservedColumns;
	private int columnsCount;
	private AmiColumnImpl<?>[] requiredColumns;
	final private Map<String, AmiColumnImpl> columnsByName = new HashMap<String, AmiColumnImpl>();
	private boolean hasReservedColumns;

	final private boolean neverBroadcast;
	final private CompactLongKeyMap<AmiRowImpl> rows;

	final private List<AmiRow> queryBuf = new ArrayList<AmiRow>(1);

	final private IdList pendingRowsForUpdate = new IdList();
	final private IdList pendingRowsForInsert = new IdList();
	final private IdList pendingRowsForDelete = new IdList();
	private boolean pendingRebuildAll;
	final private AmiImdbImpl imdb;
	private AmiPreparedRowImpl addRowBuf;
	private AmiPreparedRowImpl tmpPreparedRow;

	final private Map<String, AmiTriggerBindingImpl> triggerBindingsByName = new HashMap<String, AmiTriggerBindingImpl>();
	private AmiTrigger[] triggers;
	private long refreshPeriod = 100;
	private boolean hasPending = false;
	private boolean persisterNeedsFlush;
	private final PendingChangesRunnable pendingChangesRunnable = new PendingChangesRunnable();
	private AmiTablePersister persister;
	private AmiTablePersisterBinding persisterBinding;
	private boolean isStarted = false;
	private byte defType;
	private AmiIndexImpl primaryKeyIndex;
	private AmiKeyGenerator autoGenColumn = null;
	private AmiTriggerBindingImpl[] triggerBindings;
	private boolean supportedInserting;
	private boolean supportedInserted;
	private boolean supportedUpdating;
	private boolean supportedUpdated;
	private boolean supportedDeleting;
	final private byte onUndefinedColumn;
	private int initialCapacity;
	private final String persisterEngineName;

	//strategy: first 64 columns always get there own bit, remaining are broken in (up to) 64.
	//For examples:
	//164 total columns results in 100 extended columns, first 28 buckets represent 1 columns,remaining 36 will represent 2 columns
	//500 total columns results in 436 extended columns, first 12 buckets represent 6 columns,remaining 52 will represent 7 columns

	private int extendedColumnSplit;//number of "smaller" first buckets
	private int extendedColumnBucketSize1;//size of smaller buckets
	private int extendedColumnBucketSize2;//size of larger buckets, which is one greater than smaller buckets

	public AmiTableImpl(AmiImdbImpl db, AmiTableDef def) {
		this.imdb = db;
		this.state = db.getState();
		this.onUndefinedColumn = def.getOnUndefinedColumn();
		this.persisterEngineName = def.getPersisterEngineName();
		this.initialCapacity = def.getInitialCapacity();
		List<String> cnames = def.getColumnNames();
		List<Byte> ctypes = def.getColumnTypes();
		List<Map<String, String>> coptions = def.getColumnOptions();
		List<AmiColumnImpl> requiredColumns = new ArrayList<AmiColumnImpl>();
		this.defType = def.getDefType();
		AmiTableUtils.toStringForDefType(defType);
		this.columnsCount = cnames.size();
		this.table = new ColumnarTable();
		this.table.setTitle(def.getName());
		this.table.setRowFactory(this);
		this.type = this.origType = state.getAmiKeyId(def.getName());
		this.neverBroadcast = def.getIsNeverBroadcast();
		AmiColumnImpl[] cols = new AmiColumnImpl[cnames.size()];
		List<AmiColumnImpl> nonReservedColumns = new ArrayList<AmiColumnImpl>();
		AmiColumnImpl<ColumnarColumnLong> reservedColumnD = null;
		AmiColumnImpl<ColumnarColumnObject<String>> reservedColumnP = null;
		AmiColumnImpl<ColumnarColumnObject<String>> reservedColumnI = null;
		AmiColumnImpl<ColumnarColumnLong> reservedColumnE = null;
		AmiColumnImpl<ColumnarColumnInt> reservedColumnV = null;
		AmiColumnImpl<ColumnarColumnLong> reservedColumnM = null;
		AmiColumnImpl<ColumnarColumnLong> reservedColumnC = null;
		AmiColumnImpl<ColumnarColumnEnum> reservedColumnT = null;
		boolean hasReserved = false;
		for (int i = 0; i < cnames.size(); i++) {
			String cname = cnames.get(i);
			final Map<String, String> coption = coptions == null ? Collections.EMPTY_MAP : OH.noNull(coptions.get(i), Collections.EMPTY_MAP);
			byte ctype = ctypes.get(i);
			if ("T".equals(cname) && ctype == AmiTable.TYPE_STRING)
				ctype = AmiTable.TYPE_ENUM;//this is a hack to make the create public table as select * from some_legacy_table work
			AmiColumnImpl col = createColumn(i, ctype, cname, coption);
			cols[i] = col;
		}
		this.refreshPeriod = def.getRefershPeriod();
		table.ensureCapacity(def.getInitialCapacity());
		rows = new CompactLongKeyMap<AmiRowImpl>(getName(), this, Math.max(1024, this.initialCapacity));
		rebuildColumns(cols);
		if (neverBroadcast)
			LH.info(log, "BROADCAST PERMANENTLY DISABLED FOR " + getName());
		def.bindToTable(this);//I think this whole concept can go away
	}

	@Override
	public boolean addIndex(byte defType, String name, List<String> columns, List<Byte> sorted, byte indexType, StringBuilder errorSink, CalcFrameStack sf) {
		return addIndex(defType, name, columns, sorted, indexType, errorSink, AmiIndex.AUTOGEN_NONE, sf);
	}

	public boolean addIndex(byte defType, String name, List<String> columns, List<Byte> sorted, byte indexType, StringBuilder errorSink, byte autogenType, CalcFrameStack sf) {
		AmiIndexImpl index = new AmiIndexImpl(defType, state, this, name, columns, sorted, indexType, autogenType);
		if (index.getIsPrimaryKey()) {
			if (reservedColumnI != null) {
				if (errorSink != null)
					errorSink.append("Primary Key can not exist if there is an 'I' (identifier) column defined");
				return false;
			}
			if (this.primaryKeyIndex != null) {
				if (errorSink != null)
					errorSink.append("Primary Key already defined");
				return false;
			}
		}
		CH.putOrThrow(indexesByName, index.getName(), index);
		for (int i = 0; i < this.getRowsCount(); i++) {
			AmiRowImpl row = this.getAmiRowAt(i);
			if (!index.canAddRow(row, false, sf)) {
				index.clear();
				this.indexesByName.remove(index.getName());
				if (errorSink != null)
					errorSink.append("Unique key constraint violation");
				return false;
			}
			index.addRow(row);
		}
		this.indexes = AH.append(this.indexes, index);
		rebuildIndexMasks();
		this.imdb.onSchemaChanged(sf);
		return true;
	}
	@Override
	public AmiIndexImpl removeIndex(String name, CalcFrameStack sf) {
		AmiIndexImpl r = CH.removeOrThrow(indexesByName, name);
		this.indexes = AH.remove(this.indexes, AH.indexOf(r, this.indexes));
		rebuildIndexMasks();
		r.clear();
		this.imdb.onSchemaChanged(sf);
		return r;
	}

	private void rebuildIndexMasks() {
		this.columnsParticipatingInIndexMask = 0;
		this.columnsParticipatingInUniqueIndexMask = 0;
		this.primaryKeyIndex = null;
		this.autoGenColumn = null;
		for (AmiIndexImpl i : this.indexes) {
			this.columnsParticipatingInIndexMask |= i.getParticipatingColumnLocationMasks();
			if (i.getIsUnique())
				this.columnsParticipatingInUniqueIndexMask |= i.getParticipatingColumnLocationMasks();
			if (i.getIsPrimaryKey()) {
				this.primaryKeyIndex = i;
				this.autoGenColumn = i.getAutoGen();
				if (this.autoGenColumn != null)
					this.autoGenColumn.onValues((List) this.table.getRows());

			}
		}
		for (int i = 0; i < Math.min(this.columnsCount, 64); i++) {
			AmiColumnImpl col = this.columnsByPos[i];
			long mask0 = col.getColumnPositionMask0();
			col.setParticipatesInIndex(MH.anyBits(mask0, this.columnsParticipatingInIndexMask), MH.anyBits(mask0, this.columnsParticipatingInUniqueIndexMask));
		}
	}

	public void resolvedTriggers() {
	}

	public AmiColumnImpl getColumn(short type) {
		return columns.get(type);
	}

	final public short getType() {
		return this.type;
	}

	public AmiColumnImpl<ColumnarColumnObject<String>> getReservedColumnApplicationId() {
		return reservedColumnP;
	}
	public AmiColumnImpl<ColumnarColumnObject<String>> getReservedColumnId() {
		return reservedColumnI;
	}
	public AmiColumnImpl<ColumnarColumnLong> getReservedColumnExpires() {
		return reservedColumnE;
	}
	public AmiColumnImpl<ColumnarColumnInt> getReservedColumnRevision() {
		return reservedColumnV;
	}
	public AmiColumnImpl<ColumnarColumnLong> getReservedColumnModifiedOn() {
		return reservedColumnM;
	}
	public AmiColumnImpl<ColumnarColumnLong> getReservedColumnCreatedOn() {
		return reservedColumnC;
	}
	public AmiColumnImpl<ColumnarColumnEnum> getReservedColumnType() {
		return reservedColumnT;
	}
	public AmiColumnImpl<ColumnarColumnLong> getReservedColumnAmiId() {
		return reservedColumnD;
	}

	@Override
	public AmiRowImpl getAmiRowByAmiId(long amiId) {
		return rows.get(amiId);
	}

	public AmiRowImpl getAmiRow(String application, String id, AmiPreparedRow row) {
		if (this.primaryKeyIndex != null && row != null) {
			if (this.autoGenColumn == null || autoGenColumn.hasValue(row))
				return this.primaryKeyIndex.getUniqueValue(row);
		}
		if (id == null)
			return null;
		if (indexI != null)
			return indexI.get(id);
		if (indexPI != null)
			return indexPI.getMulti(application, id);
		return null;
	}

	public AmiPreparedRowImpl borrowPreparedRow() {
		return this.tmpPreparedRow;
	}

	public AmiRow addRowFromEntity(String applicationName, int appId, String objectId, long expires, long now, byte[] data, CalcFrameStack sf) {
		AmiPreparedRowImpl rowBuf = addRowBuf;
		rowBuf.reset();
		if (this.onUndefinedColumn != AmiTableDef.ON_UNDEFINED_COLUMN_IGNORE) {
			if (objectId != null && this.reservedColumnI == null) {
				if (this.onUndefinedColumn == AmiTableDef.ON_UNDEFINED_COLUMN_REJECT)
					LH.info(log, "Table '", getName(), "' has OnUndefColumn='REJECT' so dropping record with unknown column: 'I'");
				else if (this.onUndefinedColumn == AmiTableDef.ON_UNDEFINED_COLUMN_ADD)
					LH.info(log, "Table '", getName(), "' has OnUndefColumn='ADD' but reseved columns can not be added dynamically so dropping record with unknown column: 'I'");
				return null;
			} else if (expires != 0 && this.reservedColumnE == null) {
				if (this.onUndefinedColumn == AmiTableDef.ON_UNDEFINED_COLUMN_REJECT)
					LH.info(log, "Table '", getName(), "' has OnUndefColumn='REJECT' so dropping record with unknown column: 'E'");
				else if (this.onUndefinedColumn == AmiTableDef.ON_UNDEFINED_COLUMN_ADD)
					LH.info(log, "Table '", getName(), "' has OnUndefColumn='ADD' but reseved columns can not be added dynamically so dropping record with unknown column: 'E'");
				return null;
			}
		}

		if (data != null) {
			while (!AmiCenterAmiUtilsForTable.updateRow(rowBuf, data, SH.clear(tmpbuf), this, this.onUndefinedColumn, sf)) {
				if (this.onUndefinedColumn == AmiTableDef.ON_UNDEFINED_COLUMN_REJECT)
					return null;
				else if (this.onUndefinedColumn == AmiTableDef.ON_UNDEFINED_COLUMN_ADD) {
					rowBuf = addRowBuf;
					rowBuf.reset();
					continue;//this means a row was column was added, lets try again...
				} else
					throw new IllegalStateException();

			}
		} else
			LH.warning(log, "Data in ", this.getName(), " null for ", objectId);
		if (this.primaryKeyIndex != null)
			if (!fireTriggerInserting(rowBuf, sf))
				return null;
		AmiRowImpl existing = getAmiRow(applicationName, objectId, rowBuf);
		if (existing == null) {
			AmiRowImpl r = insertRowInner(rowBuf, objectId, applicationName, state.getAmiImdb().getNow(), expires, sf);
			if (logIsFine) {
				if (r == null)
					LH.fine(log, "INSERT_REJECTED ", this.getName(), ": ", rowBuf);
				else
					LH.fine(log, "INSERT ", this.getName(), "+", r.getAmiId(), ": ", rowBuf);
			}
			return r;
		} else {
			if (expires > 0L && this.reservedColumnE != null)
				rowBuf.setLong(this.reservedColumnE, expires);
			if (updateRowInner(existing, state.getAmiImdb().getNow(), rowBuf, sf)) {
				if (logIsFine)
					LH.fine(log, "UPDATE ", this.getName(), "+", existing.getAmiId(), ": ", rowBuf);
				return existing;
			} else {
				if (logIsFine)
					LH.fine(log, "IGNORE ", this.getName(), "+", existing.getAmiId(), ": ", rowBuf);
				return null;
			}
		}
	}
	public boolean removeRowFromEntity(String applicationName, int appId, String objectId, long now, byte[] data, CalcFrameStack sf) {
		AmiRowImpl row;
		if (primaryKeyIndex != null) {
			AmiPreparedRowImpl rowBuf = addRowBuf;
			rowBuf.reset();
			if (data != null) {
				if (!AmiCenterAmiUtilsForTable.updateRow(rowBuf, data, tmpbuf, this, AmiTableDef.ON_UNDEFINED_COLUMN_IGNORE, sf))
					return false;
			}
			row = getAmiRow(applicationName, objectId, rowBuf);
		} else {

			row = getAmiRow(applicationName, objectId, null);
		}
		if (row == null)
			return false;
		removeAmiRow(row, sf);
		return true;
	}
	private boolean broadcasting(AmiRowImpl row) {
		if (neverBroadcast)
			return false;
		return true;
	}

	public void writeEntity(FastByteArrayDataOutputStream sink, AmiRowImpl row) {
		int start = sink.size();
		sink.writeInt(0);//place holder for total size
		sink.writeShort(this.type);
		sink.writeLong(row.getAmiId());
		int maskPos = sink.size();
		sink.writeByte(0);//place holder for mask
		byte mask = 0;
		if (hasReservedColumns) {
			if (reservedColumnC != null) {
				mask |= AmiDataEntity.MASK_CREATED_ON;
				sink.writeLong(reservedColumnC.getLong(row));
			}

			if (reservedColumnM != null) {
				sink.writeLong(reservedColumnM.getLong(row));
				mask |= AmiDataEntity.MASK_MODIFIED_ON;
			}
			if (reservedColumnV != null) {
				sink.writeInt((int) reservedColumnV.getLong(row));
				mask |= AmiDataEntity.MASK_REVISION;
			}
			if (reservedColumnE != null) {
				long val = reservedColumnE.getLong(row);
				if (AmiTable.NULL_NUMBER != val) {
					sink.writeLong(val);
					mask |= AmiDataEntity.MASK_EXPIRES_IN_MILLIS;
				}
			}

			if (reservedColumnP != null) {
				String value = reservedColumnP.getString(row);
				if (value != null) {
					sink.writeShort(state.getAmiKeyId(value));
					mask |= AmiDataEntity.MASK_APPLICATION_ID;
				}
			}

			if (reservedColumnI != null) {
				String id = reservedColumnI.getString(row);
				if (id != null) {
					try {
						sink.writeUTF(id);
					} catch (UTFDataFormatException e) {
						throw OH.toRuntime(e);
					}
					mask |= AmiDataEntity.MASK_OBJECT_ID;
				}
			}
		}
		int cntPos = sink.size();
		sink.writeShort(0);//place holder for records count
		int cnt = 0;
		for (AmiColumnImpl<?> pos : nonReservedColumns) {
			if (!pos.getIsBroadcast() || pos.getIsNull(row))
				continue;
			cnt++;
			sink.writeShort(pos.getParamKey());
			AmiCenterAmiUtilsForTable.writeField(sink, pos, row);
		}
		ByteHelper.writeShort(cnt, sink.getBuffer(), cntPos);
		mask |= AmiDataEntity.MASK_PARAMS;
		int end = sink.size();
		ByteHelper.writeByte(mask, sink.getBuffer(), maskPos);
		ByteHelper.writeInt(end - start - 4, sink.getBuffer(), start);
	}
	public boolean isColumnLocationParticipatingInIndex(int location) {
		return location < 64 && MH.anyBits(columnsParticipatingInIndexMask, 1L << location);
	}

	private AmiRowImpl insertRowInner(AmiPreparedRowImpl row, String objectId, String applicationName, long now, long expires, CalcFrameStack sf) {

		if (hasReservedColumns) {
			if (reservedColumnE != null && expires < now && expires != 0L && !isPersistenceRestoring)
				return null;
			if (reservedColumnI != null)
				row.setString(reservedColumnI, objectId, sf);
			if (reservedColumnP != null)
				row.setString(reservedColumnP, applicationName, sf);
		}
		for (AmiColumnImpl<?> i : this.requiredColumns)
			if (row.getIsNull(i)) {
				AmiCenterUtils.getSession(sf).onWarning(NULL_CONSTRAINT, this, i.getName(), "INSERT_ROW", null, row, null);
				return null;
			}
		if (columnsParticipatingInUniqueIndexMask != 0)
			for (int i = 0; i < indexes.length; i++)
				if (!indexes[i].canAddRow(row, true, sf))
					return null;
		if (this.primaryKeyIndex == null)
			if (!fireTriggerInserting(row, sf))
				return null;
		AmiRowImpl r = newEmptyRowInner(objectId, applicationName, now, expires, sf);
		if (autoGenColumn != null) {
			if (autoGenColumn.hasValue(row))
				autoGenColumn.onValue(row);
			else
				autoGenColumn.getNextValue(row);
		}

		this.isInInsertUpdate = true;
		if (this.reservedColumnE != null) {
			for (int n = 0; n < row.getFieldSetCount(); n++) {
				AmiPreparedCell pc = row.getFieldSetAt(n);
				if (pc.getColumn() != this.reservedColumnE)
					pc.setOn(r, sf);
			}
		} else {
			for (int n = 0; n < row.getFieldSetCount(); n++) {
				AmiPreparedCell pc = row.getFieldSetAt(n);
				pc.setOn(r, sf);
			}
		}
		this.isInInsertUpdate = false;
		if (columnsParticipatingInIndexMask != 0) {
			for (int i = 0; i < indexes.length; i++)
				indexes[i].addRow(r);
		}
		if (objectId != null) {
			if (indexI != null)
				indexI.put(objectId, r);
			if (indexPI != null)
				indexPI.putMulti(applicationName, objectId, r);
		}
		this.table.getRows().add(r);
		this.rows.put(r);
		if (persister != null) {
			persister.onAddRow(r);
			onPersistNeedsFlush();
		}
		if (broadcasting(r))
			r.setVisible(true);
		fireTriggerInsert(r, sf);
		return r;
	}

	private AmiRowImpl newEmptyRowInner(String objectId, String applicationName, long now, long expires, CalcFrameStack fs) {
		AmiRowImpl r = (AmiRowImpl) getTable().newEmptyRow();
		if (hasReservedColumns) {
			if (reservedColumnI != null)
				r.setString(reservedColumnI, objectId, fs);
			if (reservedColumnP != null)
				r.setString(reservedColumnP, applicationName, fs);
			if (reservedColumnV != null)
				r.setLong(reservedColumnV, 0, fs);
			if (reservedColumnC != null)
				r.setLong(reservedColumnC, now, fs);
			if (reservedColumnT != null)
				r.setString(reservedColumnT, this.getName(), fs);
			if (reservedColumnM != null)
				r.setLong(reservedColumnM, now, fs);
			if (reservedColumnE != null && expires != 0) {
				r.setLong(reservedColumnE, expires, fs);
				onExpiresTimeChanged(r, 0, expires);
			}
			if (reservedColumnD != null)
				r.setLong(reservedColumnD, r.getAmiId(), fs);
		}
		return r;
	}
	private boolean updateRowInner(AmiRowImpl sink, long now, AmiPreparedRowImpl row, CalcFrameStack sf) {
		long columnsChangesMask0 = 0;
		long columnsChangesMask64 = 0;
		boolean hasChanges = false;
		for (int n = 0; n < row.getFieldSetCount(); n++) {
			AmiPreparedCell pc = row.getFieldSetAt(n);
			AmiColumn col = pc.getColumn();
			if (!col.getAllowNull() && row.getIsNull(col)) {
				AmiCenterUtils.getSession(sf).onWarning(NULL_CONSTRAINT, this, col.getName(), "UPDATE_ROW", null, row, null);
				return false;
			}
			if (!pc.isEqual(sink)) {
				hasChanges = true;
				columnsChangesMask0 |= col.getColumnPositionMask0();
				columnsChangesMask64 |= col.getColumnPositionMask64();
			}
		}
		if (!hasChanges && (reservedColumnE == null || !row.isSet(reservedColumnE)))
			return false;
		if (!canUpdate(row, sink, columnsChangesMask0, sf))
			return false;
		if (supportedUpdating) {
			if (!fireTriggerUpdating(sink, row, sf))
				return false;
			columnsChangesMask0 = 0;
			columnsChangesMask64 = 0;
			hasChanges = false;
			for (int n = 0; n < row.getFieldSetCount(); n++) {
				AmiPreparedCell pc = row.getFieldSetAt(n);
				AmiColumn col = pc.getColumn();
				if (!pc.isEqual(sink)) {
					hasChanges = true;
					columnsChangesMask0 |= col.getColumnPositionMask0();
					columnsChangesMask64 |= col.getColumnPositionMask64();
				}
			}
			if (!hasChanges)
				return false;
		}
		this.isInInsertUpdate = true;
		for (int n = 0; n < row.getFieldSetCount(); n++) {
			AmiPreparedCell pc = row.getFieldSetAt(n);
			AmiColumn col = pc.getColumn();
			int i = col.getLocation();
			if (i < 64) {
				if ((columnsChangesMask0 & col.getColumnPositionMask0()) == 0)
					continue;
			} else if (i < 128) {
				if ((columnsChangesMask64 & col.getColumnPositionMask64()) == 0)
					continue;
			}
			if (pc.setOn(sink, sf))
				table.fireCellChanged(sink, col.getLocation(), null, null);
		}
		this.isInInsertUpdate = false;
		if (hasReservedColumns) {
			if (reservedColumnV != null && !row.isSet(reservedColumnV)) {
				sink.setLong(reservedColumnV, sink.getLong(reservedColumnV) + 1, sf);
				columnsChangesMask0 |= reservedColumnV.getColumnPositionMask0();
				columnsChangesMask64 |= reservedColumnV.getColumnPositionMask64();
			}
			if (reservedColumnM != null && !row.isSet(reservedColumnM)) {
				sink.setLong(reservedColumnM, now, sf);
				columnsChangesMask0 |= reservedColumnM.getColumnPositionMask0();
				columnsChangesMask64 |= reservedColumnM.getColumnPositionMask64();
			}
			if (reservedColumnE != null && row.isSet(reservedColumnE)) {
				long expires = row.getIsNull(reservedColumnE) ? 0 : row.getLong(reservedColumnE);
				if (expires < 0L)
					expires = this.getImdb().getNow() - expires;
				long before = sink.getIsNull(reservedColumnE) ? 0 : sink.getLong(reservedColumnE);
				if (expires == 0L)
					sink.setNull(reservedColumnE, sf);
				else
					sink.setLong(reservedColumnE, expires, sf);
				onExpiresTimeChanged(sink, before, expires);
				columnsChangesMask0 |= reservedColumnE.getColumnPositionMask0();
				columnsChangesMask64 |= reservedColumnE.getColumnPositionMask64();
			}
		}
		if (autoGenColumn != null) {
			autoGenColumn.onValue(sink);
		}
		updateIndexes(sink, columnsChangesMask0);
		if (this.persister != null) {
			this.persister.onRowUpdated(sink, columnsChangesMask0, columnsChangesMask64);
			onPersistNeedsFlush();
		}
		fireTriggerUpdated(sink, sf);
		return true;
	}

	private long soonestExpiresTime = -1L;
	private int rowsWithExpiresTimeCount = 0;
	private long pushedAmiId = -1L;
	private boolean isInInsertUpdate = false;
	private boolean isPersistenceRestoring = false;

	public void removeExpired(long now, CalcFrameStack sf) {
		if (now < soonestExpiresTime || rowsWithExpiresTimeCount == 0)
			return;
		long soonestExpiresTime = Long.MAX_VALUE;
		ColumnarTableList rows = table.getRows();
		int expiredCount = 0;
		for (int i = getRowsCount() - 1; i >= 0; i--) {
			AmiRowImpl row = (AmiRowImpl) rows.get(i);
			long expires = this.reservedColumnE.getLong(row);
			if (expires <= 0)
				continue;
			if (expires <= now) {
				if (!removeAmiRow(row, sf)) {//this will muck with soonestExpiresTime, so we need to set at the end of for loop
					this.reservedColumnE.setNull(row, sf);
				}
				expiredCount++;
				if (rowsWithExpiresTimeCount == 0)
					break;
			} else if (expires < soonestExpiresTime)
				soonestExpiresTime = expires;
		}
		state.incrementAmiMessageStats(state.STATUS_TYPE_OBJECT_EXPIRED, expiredCount);
		this.soonestExpiresTime = soonestExpiresTime;
	}
	private void onExpiresTimeChanged(AmiRowImpl sink, long old, long nuw) {
		if (old == nuw)
			return;
		if (nuw == 0) {//was set, now its not
			rowsWithExpiresTimeCount--;
			if (old == soonestExpiresTime)
				soonestExpiresTime = -1L;
		} else if (old == 0) {//wasn't set, now it is
			rowsWithExpiresTimeCount++;
			if (nuw < soonestExpiresTime || soonestExpiresTime == -1L)
				soonestExpiresTime = nuw;
		} else {//just changed
			if (nuw < soonestExpiresTime)
				soonestExpiresTime = nuw;
		}
	}

	protected boolean canUpdate(AmiPreparedRowImpl row, AmiRowImpl sink, long columnsChangesMask, CalcFrameStack fs) {
		if (MH.anyBits(columnsChangesMask, columnsParticipatingInUniqueIndexMask))
			for (int i = 0; i < indexes.length; i++)
				if (!indexes[i].canUpdateRow(row, sink, columnsChangesMask, true, fs))
					return false;
		return true;
	}

	protected void updateIndexes(AmiRowImpl row, long columnsChangesMask) {
		if (MH.anyBits(columnsChangesMask, columnsParticipatingInIndexMask))
			for (int i = 0; i < indexes.length; i++)
				indexes[i].updateRow(row, columnsChangesMask);
	}

	@Override
	public boolean removeAmiRow(final AmiRow row2, CalcFrameStack sf) {
		if (!fireTriggerDelete(row2, sf))
			return false;
		final AmiRowImpl row = (AmiRowImpl) row2;
		if (columnsParticipatingInIndexMask != 0)
			for (int i = 0; i < indexes.length; i++)
				indexes[i].removeRow(row);
		if (indexI != null) {
			String id = reservedColumnI.getString(row);
			if (id != null)
				indexI.remove(id);
		} else if (indexPI != null) {
			String id = reservedColumnI.getString(row);
			String p = reservedColumnP.getString(row);
			if (id != null)
				indexPI.removeMulti(p, id);
		}
		row.setVisible(false);

		this.rows.remove(row.getAmiId());
		if (persister != null) {
			persister.onRemoveRow(row);
			onPersistNeedsFlush();
		}
		if (reservedColumnE != null && !reservedColumnE.getIsNull(row)) {
			onExpiresTimeChanged(row, reservedColumnE.getLong(row), 0);
		}
		table.removeRow(row);
		return true;
	}

	private void onPersistNeedsFlush() {
		if (persisterNeedsFlush)
			return;
		this.persisterNeedsFlush = true;
		this.imdb.registerNeedsflush(this);
	}
	public String toString() {
		return table.toString();
	}

	public ColumnarTable getTable() {
		return this.table;
	}

	@Override
	public int getRowsCount() {
		return table.getSize();
	}

	@Override
	public int getColumnsCount() {
		return this.columnsByPos.length;
	}

	@Override
	public String getColunNameAt(int position) {
		return this.columnsByPos[position].getName();
	}

	@Override
	public byte getColumnTypeAt(int position) {
		return this.columnsByPos[position].getAmiType();
	}

	@Override
	public byte getColumnType(String name) {
		final AmiColumn t = getColumn(name);
		return t == null ? AmiTable.TYPE_NONE : t.getAmiType();
	}

	@Override
	public int getColumnLocation(String name) {
		final AmiColumn t = getColumnNoThrow(name);
		return t == null ? -1 : t.getLocation();
	}

	@Override
	public AmiColumnImpl<?> getColumnAt(int position) {
		return this.columnsByPos[position];
	}
	@Override
	public AmiColumnImpl<?> getColumn(String name) {
		return CH.getOrThrow(this.columnsByName, name, "Column name not found on Table ", this.getName());
	}
	@Override
	public AmiColumnImpl<?> getColumnAtNoThrow(int position) {
		return AH.getOr(this.columnsByPos, position, null);
	}
	@Override
	public AmiColumnImpl<?> getColumnNoThrow(String name) {
		return this.columnsByName.get(name);
	}

	@Override
	public AmiRowImpl getAmiRowAt(int position) {
		return (AmiRowImpl) table.getRows().get(position);
	}

	@Override
	public boolean updateAmiRow(AmiRowImpl existing, AmiPreparedRow row, CalcFrameStack sf) {
		OH.assertEqIdentity(row.getAmiTable(), this, "Prepared Row not a member of this table");
		OH.assertEqIdentity(existing.getAmiTable(), this, "Prepared Row not a member of this table");
		return updateRowInner(existing, state.getAmiImdb().getNow(), (AmiPreparedRowImpl) row, sf);
	}
	@Override
	public AmiRow updateAmiRow(long amiId, AmiPreparedRow row, CalcFrameStack sf) {
		OH.assertEqIdentity(row.getAmiTable(), this, "Prepared Row not a member of this table");
		AmiRowImpl existing = getAmiRowByAmiId(amiId);
		if (existing == null)
			return null;
		if (!updateRowInner(existing, state.getAmiImdb().getNow(), (AmiPreparedRowImpl) row, sf))
			return null;
		return existing;
	}
	@Override
	public AmiRow updateAmiRow(AmiPreparedRow row, CalcFrameStack sf) {
		OH.assertEqIdentity(row.getAmiTable(), this, "Prepared Row not a member of this table");
		String applicationName = reservedColumnP == null ? null : row.getString(reservedColumnP);
		String objectId = reservedColumnI == null ? null : row.getString(reservedColumnI);
		AmiRowImpl existing = getAmiRow(applicationName, objectId, row);
		if (existing == null)
			return null;
		if (!updateRowInner(existing, state.getAmiImdb().getNow(), (AmiPreparedRowImpl) row, sf))
			return null;
		return existing;
	}
	public AmiRowImpl insertAmiRow(AmiPreparedRow row, long amiId, CalcFrameStack sf) {
		this.pushedAmiId = amiId;
		AmiRowImpl r = insertAmiRow(row, false, true, sf);
		if (this.pushedAmiId == amiId)
			this.pushedAmiId = -1L;
		return r;
	}
	public AmiRowImpl insertAmiRow(AmiPreparedRow row, CalcFrameStack sf) {
		return insertAmiRow(row, false, true, sf);
	}
	public AmiRowImpl insertAmiRow(AmiPreparedRow row, boolean returnRowUpsert, boolean allowUpsert, CalcFrameStack sf) {
		OH.assertEqIdentity(row.getAmiTable(), this, "Prepared Row not a member of this table");
		if (primaryKeyIndex != null)
			if (!fireTriggerInserting(row, sf))
				return null;
		String applicationName = reservedColumnP == null ? null : row.getString(reservedColumnP);
		String objectId = reservedColumnI == null ? null : row.getString(reservedColumnI);
		AmiRowImpl existing = getAmiRow(applicationName, objectId, row);
		if (existing == null) {
			long expires = reservedColumnE == null || !row.isSet(reservedColumnE) || row.getIsNull(reservedColumnE) ? 0 : row.getLong(reservedColumnE);
			if (expires < 0L)
				expires = this.getImdb().getNow() - expires;
			AmiRowImpl r = insertRowInner((AmiPreparedRowImpl) row, objectId, applicationName, state.getAmiImdb().getNow(), expires, sf);
			return r;
		} else {
			if (allowUpsert)
				updateRowInner(existing, state.getAmiImdb().getNow(), (AmiPreparedRowImpl) row, sf);
			return returnRowUpsert ? existing : null;
		}
	}
	private boolean fireTriggerInserting(AmiRow row, CalcFrameStack sf) {
		if (supportedInserting)
			for (int i = 0; i < triggers.length; i++)
				if (!triggerBindings[i].onInserting(this, row, sf))
					return false;
		return true;
	}
	private void fireTriggerInsert(AmiRow row, CalcFrameStack sf) {
		if (supportedInserted)
			for (int i = 0; i < triggers.length; i++)
				triggerBindings[i].onInserted(this, row, sf);
	}

	//	@Override
	//	public boolean fireTriggerUpdating(AmiRow row, AmiImdbSession session) {
	//		if (supportedUpdating)
	//			for (int i = 0; i < triggers.length; i++)
	//				if (!triggerBindings[i].onUpdating(this, row, session))
	//					return false;
	//		return true;
	//	}
	private boolean fireTriggerUpdating(AmiRow row, AmiPreparedRow newValues, CalcFrameStack sf) {
		if (supportedUpdating)
			for (int i = 0; i < triggers.length; i++)
				if (!triggerBindings[i].onUpdating(this, row, newValues, sf)) {
					for (int j = 0; j < i; j++) {
						triggerBindings[j].onUpdatingRejected(this, row, newValues, sf);
					}
					return false;
				}
		return true;
	}
	@Override
	public void fireTriggerUpdated(AmiRow row, CalcFrameStack sf) {
		if (supportedUpdated)
			for (int i = 0; i < triggers.length; i++)
				triggerBindings[i].onUpdated(this, row, sf);
	}

	private boolean fireTriggerDelete(AmiRow row, CalcFrameStack sf) {
		if (supportedDeleting)
			for (int i = 0; i < triggers.length; i++)
				if (!triggerBindings[i].onDeleting(this, row, sf))
					return false;
		return true;

	}

	public boolean hasTriggers() {
		return triggers != null;
	}

	@Override
	public ColumnarRow newColumnarRow(ColumnarTable columnarTable, int i, int index) {
		long amiId;
		if (this.pushedAmiId != -1L) {
			amiId = this.pushedAmiId;
			this.pushedAmiId = -1L;
		} else
			amiId = this.state.createNextId();
		return new AmiRowImpl(this, i, index, amiId, !neverBroadcast);
	}
	public AmiIndexImpl getIndex(String name) {
		return this.indexesByName.get(name);
	}
	public Collection<AmiIndexImpl> getIndexes() {
		return this.indexesByName.values();
	}

	@Override
	public String getName() {
		return table.getTitle();
	}
	@Override
	public String getFlushableName() {
		return "TABLE:" + table.getTitle();
	}

	@Override
	public AmiIndex getAmiIndex(String name) {
		return CH.getOrThrow(this.indexesByName, name, "Index name not found on table ", this.getName());
	}
	@Override
	public AmiIndex getAmiIndexNoThrow(String name) {
		return this.indexesByName.get(name);
	}
	@Override
	public Set<String> getAmiIndexNames() {
		return this.indexesByName.keySet();
	}

	public AmiCenterState getState() {
		return this.state;
	}
	@Override
	public AmiPreparedRowImpl createAmiPreparedRow() {
		return new AmiPreparedRowImpl(this, false);
	}
	@Override
	public AmiPreparedRowImpl createAmiPreparedRowForRecovery() {
		return new AmiPreparedRowImpl(this, true);
	}
	@Override
	public void query(AmiPreparedQuery query, int limit, List<AmiRow> sink) {
		if (limit == 0)
			return;
		AmiPreparedQueryImpl queryImpl = (AmiPreparedQueryImpl) query;
		AmiIndexImpl index = queryImpl.getIndex();
		OH.assertEqIdentity(query.getAmiTable(), this);
		if (index == null) {
			for (Row row : this.table.getRows()) {
				if (queryImpl.matches((AmiRowImpl) row))
					sink.add((AmiRow) row);
				if (sink.size() >= limit)
					break;
			}
		} else {
			index.getRows(sink, limit, queryImpl);
		}
	}
	@Override
	public AmiRow query(AmiPreparedQuery query) {
		queryBuf.clear();
		query(query, 1, queryBuf);
		if (queryBuf.size() == 0)
			return null;
		AmiRow r = queryBuf.get(0);
		queryBuf.clear();
		return r;
	}

	@Override
	public AmiPreparedQuery createAmiPreparedQuery() {
		return new AmiPreparedQueryImpl(this);
	}
	@Override
	public long getKey(AmiRowImpl object) {
		return object.getAmiId();
	}
	public void getBroadcastableRows(List<Row> sink) {
		if (neverBroadcast)
			return;
		LongSet set = this.pendingRowsForInsert.toSet();
		for (Row r : this.getTable().getRows()) {
			AmiRowImpl r2 = (AmiRowImpl) r;
			if (r2.getVisible() && !set.contains(r2.getAmiId()))
				sink.add(r);
		}
	}
	public void broadcastPendingChanges() {
		AmiCenterChangesMessageBuilder mb = state.getChangesMessageBuilderNoReset();
		this.hasPending = false;
		final long deleteSize = pendingRowsForDelete.size();
		if (deleteSize > 0) {
			for (int i = 0; i < deleteSize; i++) {
				final long id = pendingRowsForDelete.getLong(i);
				if (id == -1L)
					continue;
				mb.writeRemoveAmiEntity(origType, id);
				AmiRowImpl row = getAmiRowByAmiId(id);
				if (row != null)
					row.flushPendingBroadcast();
				sendIfFull(mb);
			}
			pendingRowsForDelete.clear();
		}
		if (this.origType != type) {
			for (AmiRowImpl i : this.rows) {
				mb.writeRemoveAmiEntity(origType, i.getAmiId());
				mb.writeAdd(i);
				sendIfFull(mb);
			}
			this.origType = type;
		}
		if (pendingRebuildAll) {
			for (AmiRowImpl i : this.rows) {
				mb.writeRemoveAmiEntity(origType, i.getAmiId());
				sendIfFull(mb);
			}
			AmiCenterChanges toClient = mb.popToChangesMsg(state.nextSequenceNumber());
			this.imdb.getState().getItineraryProcessor().sendToClients(toClient);
			for (AmiRowImpl i : this.rows) {
				mb.writeAdd(i);
				sendIfFull(mb);
			}
			pendingRebuildAll = false;
			this.origType = type;
			this.pendingRowsForInsert.clear();
			this.pendingRowsForUpdate.clear();
		}
		final long insertSize = pendingRowsForInsert.size();
		final long updateSize = pendingRowsForUpdate.size();
		if (insertSize > 0) {
			for (int i = 0; i < insertSize; i++) {
				final long id = pendingRowsForInsert.getLong(i);
				if (id == -1L)
					continue;
				AmiRowImpl row = getAmiRowByAmiId(id);
				mb.writeAdd(row);
				row.flushPendingBroadcast();
				sendIfFull(mb);
			}
			pendingRowsForInsert.clear();
		}
		if (updateSize > 0) {
			int maxCol = Math.min(this.columnsCount, 64);
			int maxCol2 = this.columnsCount - 64;
			for (int i = 0; i < updateSize; i++) {
				final long id = pendingRowsForUpdate.getLong(i);
				if (id == -1L)
					continue;
				AmiRowImpl row = getAmiRowByAmiId(id);
				long mask0 = row.getChangedColumnsMask0();
				long mask64 = row.getChangedColumnsMask64();
				if (mask0 != 0)
					for (int col = MH.indexOfBitSetBefore(mask0, maxCol); col != -1; col = MH.indexOfBitSet(mask0, col + 1, maxCol))
						mb.writeUpdateRevision(row, ((AmiColumnImpl) this.columnsByPos[col]));
				if (mask64 != 0) {
					if (this.extendedColumnBucketSize2 == 1) {//trivial case
						for (int col = MH.indexOfBitSetBefore(mask64, maxCol2); col != -1; col = MH.indexOfBitSet(mask64, col + 1, maxCol2))
							mb.writeUpdateRevision(row, ((AmiColumnImpl) this.columnsByPos[col + 64]));
					} else
						for (int col = MH.indexOfBitSetBefore(mask64, maxCol2); col != -1; col = MH.indexOfBitSet(mask64, col + 1, maxCol2)) {
							int c = col;
							if (c < this.extendedColumnSplit) {
								if (this.extendedColumnBucketSize1 == 1) {
									mb.writeUpdateRevision(row, ((AmiColumnImpl) this.columnsByPos[col + 64]));
								} else {
									c *= this.extendedColumnBucketSize1;
									for (int j = 0; j < this.extendedColumnBucketSize1; j++) {
										AmiColumnImpl amiColumnImpl = (AmiColumnImpl) this.columnsByPos[c + 64 + j];
										if (amiColumnImpl.getIsBroadcast())
											mb.writeUpdateRevision(row, amiColumnImpl);
									}
								}
							} else {
								c = (c - this.extendedColumnSplit) * extendedColumnBucketSize2 + this.extendedColumnSplit * this.extendedColumnBucketSize1;
								for (int j = 0; j < this.extendedColumnBucketSize2; j++) {
									AmiColumnImpl amiColumnImpl = (AmiColumnImpl) this.columnsByPos[c + 64 + j];
									if (amiColumnImpl.getIsBroadcast())
										mb.writeUpdateRevision(row, amiColumnImpl);
								}
							}

						}
				}
				row.flushPendingBroadcast();
				sendIfFull(mb);
			}
			mb.writeUpdateRevision(null, null);
			pendingRowsForUpdate.clear();
		}
	}

	private void sendIfFull(AmiCenterChangesMessageBuilder mb) {
		if (mb.gettingFull()) {
			LH.info(log, "Large Broadcast transaction, send partial message of ", mb.getTotalMessageSize(), " bytes");
			AmiCenterChanges toClient = mb.popToChangesMsg(state.nextSequenceNumber());
			this.imdb.getState().getItineraryProcessor().sendToClients(toClient);
		}
	}

	public void onRowBroadcastStatusChange(AmiRowImpl amiRowImpl, int oldStatus, int nuwStatus) {
		if (neverBroadcast)
			return;
		switch (oldStatus << 8 | nuwStatus) {
			case AmiRowImpl.STATUS_NO_CHANGE << 8 | AmiRowImpl.STATUS_NO_CHANGE:
			case AmiRowImpl.STATUS_NOBROADCAST << 8 | AmiRowImpl.STATUS_NOBROADCAST:
			case AmiRowImpl.STATUS_NOBROADCAST_NEEDS_DELETE << 8 | AmiRowImpl.STATUS_NOBROADCAST_NEEDS_DELETE:
			case AmiRowImpl.STATUS_NEEDS_INSERT << 8 | AmiRowImpl.STATUS_NEEDS_INSERT:
			case AmiRowImpl.STATUS_NEEDS_UPDATE << 8 | AmiRowImpl.STATUS_NEEDS_UPDATE:
				break;

			case AmiRowImpl.STATUS_NO_CHANGE << 8 | AmiRowImpl.STATUS_NOBROADCAST:
			case AmiRowImpl.STATUS_NO_CHANGE << 8 | AmiRowImpl.STATUS_NEEDS_INSERT:
			case AmiRowImpl.STATUS_NOBROADCAST << 8 | AmiRowImpl.STATUS_NO_CHANGE:
			case AmiRowImpl.STATUS_NOBROADCAST << 8 | AmiRowImpl.STATUS_NOBROADCAST_NEEDS_DELETE:
			case AmiRowImpl.STATUS_NOBROADCAST << 8 | AmiRowImpl.STATUS_NEEDS_UPDATE:
			case AmiRowImpl.STATUS_NOBROADCAST_NEEDS_DELETE << 8 | AmiRowImpl.STATUS_NEEDS_INSERT:
			case AmiRowImpl.STATUS_NEEDS_INSERT << 8 | AmiRowImpl.STATUS_NOBROADCAST_NEEDS_DELETE:
			case AmiRowImpl.STATUS_NEEDS_INSERT << 8 | AmiRowImpl.STATUS_NEEDS_UPDATE:
			case AmiRowImpl.STATUS_NEEDS_UPDATE << 8 | AmiRowImpl.STATUS_NOBROADCAST:
			case AmiRowImpl.STATUS_NEEDS_UPDATE << 8 | AmiRowImpl.STATUS_NEEDS_INSERT:
				throw new IllegalStateException(oldStatus + " => " + nuwStatus);

			case AmiRowImpl.STATUS_NEEDS_INSERT << 8 | AmiRowImpl.STATUS_NO_CHANGE:
			case AmiRowImpl.STATUS_NEEDS_UPDATE << 8 | AmiRowImpl.STATUS_NO_CHANGE:
			case AmiRowImpl.STATUS_NOBROADCAST_NEEDS_DELETE << 8 | AmiRowImpl.STATUS_NOBROADCAST:
				//Pending-Flushed
				break;

			case AmiRowImpl.STATUS_NO_CHANGE << 8 | AmiRowImpl.STATUS_NEEDS_UPDATE:
				addLong(this.pendingRowsForUpdate, amiRowImpl.getAmiId());
				break;
			case AmiRowImpl.STATUS_NOBROADCAST << 8 | AmiRowImpl.STATUS_NEEDS_INSERT:
				addLong(this.pendingRowsForInsert, amiRowImpl.getAmiId());
				break;

			case AmiRowImpl.STATUS_NOBROADCAST_NEEDS_DELETE << 8 | AmiRowImpl.STATUS_NEEDS_UPDATE:
				removeLong(this.pendingRowsForDelete, amiRowImpl.getAmiId());
				addLong(this.pendingRowsForUpdate, amiRowImpl.getAmiId());
				break;

			case AmiRowImpl.STATUS_NO_CHANGE << 8 | AmiRowImpl.STATUS_NOBROADCAST_NEEDS_DELETE:
				addLong(this.pendingRowsForDelete, amiRowImpl.getAmiId());
				break;
			case AmiRowImpl.STATUS_NEEDS_UPDATE << 8 | AmiRowImpl.STATUS_NOBROADCAST_NEEDS_DELETE:
				removeLong(this.pendingRowsForUpdate, amiRowImpl.getAmiId());
				addLong(this.pendingRowsForDelete, amiRowImpl.getAmiId());
				break;

			case AmiRowImpl.STATUS_NOBROADCAST_NEEDS_DELETE << 8 | AmiRowImpl.STATUS_NO_CHANGE:
				removeLong(this.pendingRowsForDelete, amiRowImpl.getAmiId());
				break;

			case AmiRowImpl.STATUS_NEEDS_INSERT << 8 | AmiRowImpl.STATUS_NOBROADCAST:
				removeLong(this.pendingRowsForInsert, amiRowImpl.getAmiId());
				break;
		}

		/*   STATE TRANSITIONS:             NEEDS_INSERT                NEEDS_UPDATE                NO_CHANGE                   NO_BROADCAST                NO_BROADCAST_NEEDS_DELETE
		 *   --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
		 *   NEEDS_INSERT                   No-Change                   Invalid                     Pending-Flushed             REM_FROM_PENDING_ADD**      Invalid               
		 *   NEEDS_UPDATE                   Invalid                     No-Change                   Pending-Flushed             Invalid                     MOVE_TO_PENDING_DEL**  
		 *   NO_CHANGE                      Invalid                     ADD_TO_PENDING_UPD**        No-Change                   Invalid                     ADD_TO_PENDING_DEL**  
		 *   NO_BROADCAST                   ADD_TO_PENDING_INS**        Invalid                     Invalid                     No-Change                   Invalid
		 *   NO_BROADCAST_NEEDS_DELETE      Invalid                     MOVE_TO_PENDING_UPD**       REM_FROM_PENDING_DEL**      Pending-Flushed             No-Change
		 *   
		 *   
		 *   ** if the changed column mask is empty, then no change
		 */

	}
	private void addLong(IdList sink, long amiId) {
		sink.add(amiId);
		flagPendingChanges();
	}

	private void flagPendingChanges() {
		if (!hasPending) {
			hasPending = true;
			this.imdb.registerTimer(this.pendingChangesRunnable, -getRefreshPeriod(), 0, null);
		}
	}
	private void removeLong(IdList list, long id) {
		list.removeLong(id);
	}
	@Override
	public AmiImdbImpl getImdb() {
		return this.imdb;
	}

	public long getRefreshPeriod() {
		return this.refreshPeriod;
	}
	public void setRefreshPeriod(long refreshPeriod) {
		this.refreshPeriod = refreshPeriod;
	}

	public class PendingChangesRunnable implements AmiTimedRunnable {

		@Override
		public void onTimer(long timerId, Object correlationId) {
			try {
				broadcastPendingChanges();
			} catch (RuntimeException e) {
				LH.warning(log, "Error broadcasting changes for table ", getName(), e);
				throw e;
			}
		}

	}

	@Override
	public void clearRows(CalcFrameStack sf) {
		for (int i = this.table.getSize() - 1; i >= 0; i--)
			this.removeAmiRow((AmiRow) this.table.getRows().get(i), sf);
	}

	@Override
	public void addTrigger(AmiTriggerBindingImpl trigger, CalcFrameStack sf) {
		OH.assertNotNull(trigger.getTriggerName(), "trigger name");
		CH.putOrThrow(triggerBindingsByName, trigger.getTriggerName(), trigger);
		rebuildTriggersList();
	}
	@Override
	public void removeTrigger(String triggerName, CalcFrameStack sf) {
		CH.removeOrThrow(triggerBindingsByName, triggerName);
		rebuildTriggersList();
		this.imdb.onSchemaChanged(sf);
	}

	public void onSchemaChanged() {
		rebuildTriggersList();
	}

	private void rebuildTriggersList() {
		this.supportedInserting = this.supportedInserted = this.supportedUpdating = this.supportedUpdated = this.supportedDeleting = false;
		if (triggerBindingsByName.isEmpty()) {
			this.triggers = null;
			this.triggerBindings = null;
			return;
		}
		this.triggerBindings = AH.toArray(this.triggerBindingsByName.values(), AmiTriggerBindingImpl.class);
		Arrays.sort(triggerBindings, AmiTableUtils.TRIGGER_PRIORITY_COMPARATOR);
		this.triggers = new AmiTrigger[triggerBindings.length];
		for (int i = 0; i < triggerBindings.length; i++) {
			AmiTriggerBindingImpl t = triggerBindings[i];
			this.triggers[i] = t.getTrigger();
			this.supportedInserting |= t.isSupported(AmiTrigger.INSERTING);
			this.supportedInserted |= t.isSupported(AmiTrigger.INSERTED);
			this.supportedUpdating |= t.isSupported(AmiTrigger.UPDATING);
			this.supportedUpdated |= t.isSupported(AmiTrigger.UPDATED);
			this.supportedDeleting |= t.isSupported(AmiTrigger.DELETING);
		}
	}

	public void setPersister(AmiTablePersisterBinding persisterBinding) {
		if (isStarted)
			throw new IllegalStateException();
		this.persisterBinding = persisterBinding;
		this.persister = persisterBinding == null ? null : persisterBinding.getPersister();
	}
	@Override
	public AmiTablePersisterBinding getPersister() {
		return this.persisterBinding;
	}
	@Override
	public void startup(AmiImdbImpl db, CalcFrameStack sf) {
		if (this.isStarted)
			throw new IllegalStateException();
		this.isStarted = true;
		if (this.persister != null) {
			this.persister.init(this);
			AmiTablePersister p = this.persister;
			boolean t1 = this.supportedInserting;
			boolean t2 = this.supportedInserted;
			boolean t3 = this.supportedUpdating;
			boolean t4 = this.supportedUpdated;
			boolean t5 = this.supportedDeleting;
			try {
				this.persister = null;
				this.supportedInserting = this.supportedInserted = this.supportedUpdating = this.supportedUpdated = this.supportedDeleting = false;
				LH.info(log, "Loading table: " + this.getName());
				for (Node<AmiColumnImpl<?>> i : this.columns)
					i.getValue().onPersistenceRestoreStarting();
				this.isPersistenceRestoring = true;
				boolean needsCompacting = p.loadTableFromPersist(sf);
				for (Node<AmiColumnImpl<?>> i : this.columns)
					if (i.getValue().onPersistenceRestoreComplete())
						needsCompacting = true;
				LH.info(log, "Done loading table: " + this.getName(), " needs compacting=", needsCompacting);
				this.isPersistenceRestoring = false;
				if (needsCompacting) {
					p.saveTableToPersist(sf);
					LH.info(log, "Done Compacting table: " + this.getName());
				}
			} finally {
				this.isPersistenceRestoring = false;
				this.persister = p;
				this.supportedInserting = t1;
				this.supportedInserted = t2;
				this.supportedUpdating = t3;
				this.supportedUpdated = t4;
				this.supportedDeleting = t5;
			}
		} else {
			for (Node<AmiColumnImpl<?>> i : this.columns)
				i.getValue().onPersistenceRestoreStarting();
			for (Node<AmiColumnImpl<?>> i : this.columns)
				i.getValue().onPersistenceRestoreComplete();
		}

	}
	@Override
	public void flushPersister(CalcFrameStack sf) {
		if (!this.persisterNeedsFlush)
			throw new IllegalStateException();
		this.persisterNeedsFlush = false;
		if (this.persister != null)
			this.persister.flushChanges(sf);
	}
	@Override
	public boolean getIsBroadCast() {
		return !this.neverBroadcast;
	}

	@Override
	public byte getDefType() {
		return this.defType;
	}

	public String getPersisterTypeName() {
		return this.persister == null ? null : this.persisterBinding.getPersisterType();
	}

	@Override
	public int getTriggersCount() {
		return this.triggers == null ? 0 : this.triggers.length;
	}

	@Override
	public AmiTriggerBindingImpl getTriggerAt(int position) {
		return this.triggerBindings[position];
	}

	@Override
	public AmiTriggerBindingImpl getTriggerNoThrow(String triggerName) {
		return triggerBindingsByName.get(triggerName);
	}

	public void onDropping(CalcFrameStack sf) {
		if (this.persister != null)
			this.persister.drop(sf);
		this.persister = null;
		this.persisterBinding = null;
		for (AmiColumnImpl i : this.columnsByPos)
			i.onDropping(sf);
		clearRows(sf);
	}

	@Override
	public void rename(String name, CalcFrameStack sf) {
		if (OH.eq(this.getName(), name))
			return;
		String oldName = this.getName();
		if (this.persisterBinding != null)
			this.persisterBinding.onTableRename(oldName, name, sf);
		this.table.setTitle(name);
		this.type = state.getAmiKeyId(name);
		if (this.triggerBindings != null)
			for (AmiTriggerBindingImpl i : this.triggerBindings)
				i.onTableNameChanged(oldName, name);
		for (int i = 0; i < this.columnsCount; i++) {
			AmiColumnImpl<?> col = this.getColumnAt(i);
			if (col.getIsOnDisk())
				col.setAmiType(col.getName(), col.getAmiType(), col.getOptions());//lazy way to force renaming of files

		}
		if (!neverBroadcast) {
			this.flagPendingChanges();
		}
	}
	public boolean changeColumn(int location, byte newAmiType, String newName, Map<String, String> optionsMap, CalcFrameStack sf) {
		AmiColumnImpl<?> col = getColumnAt(location);
		if (col.isReserved())
			throw new RuntimeException("Can not modify reserved column: " + col.getName());
		if (newName != null && newName.length() == 1) {
			switch (newName.charAt(0)) {
				case AmiConsts.RESERVED_PARAM_APPLICATION:
				case AmiConsts.RESERVED_PARAM_ID:
				case AmiConsts.RESERVED_PARAM_EXPIRED:
				case AmiConsts.RESERVED_PARAM_REVISION:
				case AmiConsts.RESERVED_PARAM_MODIFIED_ON:
				case AmiConsts.RESERVED_PARAM_AMIID:
				case AmiConsts.RESERVED_PARAM_CREATED_ON:
				case AmiConsts.RESERVED_PARAM_TYPE:
					throw new RuntimeException("Can not rename column " + col.getName() + " to reserved column name " + newName);
			}
		}
		boolean changeColumn = newAmiType != -1 && col.getAmiType() != newAmiType;
		if (!changeColumn) {
			if (!col.getOptions().isEmpty() || !optionsMap.isEmpty())
				if (OH.ne(col.getOptions(), optionsMap))
					changeColumn = true;
		}
		boolean changeName = newName != null && OH.ne(newName, col.getName());
		if (!changeColumn && !changeName)
			return false;
		if (this.indexes != null)
			for (AmiIndexImpl idx : this.indexes)
				for (int i = 0; i < idx.getColumnsCount(); i++)
					if (idx.getColumn(i) == col)
						throw new RuntimeException("Index '" + idx.getName() + "' depends on column: " + col.getName());
		col.setAmiType(changeName ? newName : col.getName(), newAmiType != -1 ? newAmiType : col.getAmiType(), newAmiType != -1 ? optionsMap : col.getOptions());
		rebuildColumns(this.columnsByPos.clone());
		flagForRebroadcastAll();
		if (this.persister != null && isStarted)
			this.persister.saveTableToPersist(sf);
		return true;
	}
	private void flagForRebroadcastAll() {
		if (neverBroadcast)
			return;
		this.pendingRebuildAll = true;
		flagPendingChanges();
	}

	public void removeColumn(String cname, CalcFrameStack sf) {
		AmiColumnImpl<?> col = getColumn(cname);
		if (columnsCount == 1)
			throw new RuntimeException("must have atleast one column");
		if (this.indexes != null)
			for (AmiIndexImpl idx : this.indexes)
				for (int i = 0; i < idx.getColumnsCount(); i++)
					if (idx.getColumn(i) == col)
						throw new RuntimeException("Index '" + idx.getName() + "' depends on column: " + cname);
		AmiColumnImpl[] t = AH.remove(columnsByPos, AH.indexOf(col, columnsByPos));
		this.table.removeColumn(col.getLocation());
		rebuildColumns(t);
		flagForRebroadcastAll();
		if (this.persister != null && isStarted)
			this.persister.saveTableToPersist(sf);
	}
	public AmiColumnImpl addColumn(int position, final byte type, final String cname, Map<String, String> options, CalcFrameStack sf) {
		if (position < 64) {
			for (AmiIndexImpl i : this.indexes) {
				for (int n = 0; n < i.getColumnsCount(); n++) {
					AmiColumnImpl col = i.getColumn(n);
					if (col.getLocation() == 63)
						throw new RuntimeException(
								"For Index " + i.getName() + " on table " + getName() + ": Only the first 64 columns can participate in an index: " + col.getName());
				}
			}
		}
		if (getColumnNoThrow(cname) != null)
			throw new RuntimeException("duplicate column name: " + cname);
		final Class clazz;
		AmiColumnImpl col = createColumn(position, type, cname, options);
		AmiColumnImpl[] t = AH.insert(columnsByPos, position, col);
		rebuildColumns(t);
		if (this.persister != null && isStarted)
			this.persister.saveTableToPersist(sf);
		return col;
	}

	private AmiColumnImpl createColumn(int position, byte type, String cname, Map<String, String> options) {
		final Class clazz;
		final boolean isEnum;
		type = AmiTableUtils.toAmiTableColumnType(type);
		if (type == AmiTable.TYPE_ENUM) {
			isEnum = true;
			clazz = int.class;
		} else {
			isEnum = false;
			clazz = AmiTableUtils.getClassForValueType(type);
		}
		if (cname.length() == 1) {
			boolean isReserved;
			switch (cname.charAt(0)) {
				case AmiConsts.RESERVED_PARAM_APPLICATION:
					if (type != AmiTable.TYPE_ENUM && type != AmiTable.TYPE_STRING)
						throw new RuntimeException("Reserved column " + cname + " must be of type String");
					isReserved = true;
					break;
				case AmiConsts.RESERVED_PARAM_ID:
					if (clazz != String.class)
						throw new RuntimeException("Reserved column " + cname + " must be of type String");
					isReserved = true;
					break;
				case AmiConsts.RESERVED_PARAM_EXPIRED:
					if (clazz != Long.class)
						throw new RuntimeException("Reserved column " + cname + " must be of type LONG");
					isReserved = true;
					break;
				case AmiConsts.RESERVED_PARAM_REVISION:
					if (clazz != Integer.class)
						throw new RuntimeException("Reserved column " + cname + " must be of type INT");
					isReserved = true;
					break;
				case AmiConsts.RESERVED_PARAM_MODIFIED_ON:
					if (clazz != Long.class)
						throw new RuntimeException("Reserved column " + cname + " must be of type LONG");
					isReserved = true;
					break;
				case AmiConsts.RESERVED_PARAM_AMIID:
					if (clazz != Long.class)
						throw new RuntimeException("Reserved column " + cname + " must be of type LONG");
					isReserved = true;
					break;
				case AmiConsts.RESERVED_PARAM_CREATED_ON:
					if (clazz != Long.class)
						throw new RuntimeException("Reserved column " + cname + " must be of type LONG");
					isReserved = true;
					break;
				case AmiConsts.RESERVED_PARAM_TYPE:
					if (type != AmiTable.TYPE_ENUM)
						throw new RuntimeException("Reserved column " + cname + " must be of type ENUM");
					isReserved = true;
					break;
				default:
					isReserved = false;
			}
			if (isReserved && CH.isntEmpty(options))
				throw new RuntimeException("options not allowed on reserved column " + cname + ": " + options);
		}
		Map optionsFormatted = new HashMap<String, String>();
		ColumnarColumn col2 = createInnerColumn(isEnum, clazz, type, options, cname, optionsFormatted);
		table.addColumn(position, col2);
		return new AmiColumnImpl(this, col2, type, optionsFormatted, !this.neverBroadcast);
	}
	public ColumnarColumn createInnerColumn(boolean isEnum, Class clazz, byte type, Map<String, String> options, String cname, Map<String, String> optionsFormattedSink) {
		boolean isCompact = false, allowNull = true, isAscii = false, isBitmap = false, isOndisk = false, isNobroadcast = false;
		String cache = null;
		for (Entry<String, String> s : options.entrySet()) {
			String key = s.getKey();
			try {
				if (SH.equalsIgnoreCase(AmiConsts.NONULL, key)) {
					allowNull = !Caster_Boolean.INSTANCE.cast(s.getValue());
					optionsFormattedSink.put(AmiConsts.NONULL, s.getValue());
				} else if (SH.equalsIgnoreCase(AmiConsts.COMPACT, key)) {
					if (type != AmiTable.TYPE_STRING)
						throw new RuntimeException("COMPACT directive only supported for STRING columns");
					isCompact = Caster_Boolean.INSTANCE.cast(s.getValue());
					optionsFormattedSink.put(AmiConsts.COMPACT, s.getValue());
				} else if (SH.equalsIgnoreCase(AmiConsts.NOBROADCAST, key)) {
					isNobroadcast = Caster_Boolean.INSTANCE.cast(s.getValue());
					optionsFormattedSink.put(AmiConsts.NOBROADCAST, s.getValue());
				} else if (SH.equalsIgnoreCase(AmiConsts.ASCII, key)) {
					if (type != AmiTable.TYPE_STRING)
						throw new RuntimeException("ASCII directive only supported for STRING columns");
					isAscii = Caster_Boolean.INSTANCE.cast(s.getValue());
					optionsFormattedSink.put(AmiConsts.ASCII, s.getValue());
				} else if (SH.equalsIgnoreCase(AmiConsts.BITMAP, key)) {
					if (type != AmiTable.TYPE_STRING)
						throw new RuntimeException("BITMAP directive only supported for STRING columns");
					isBitmap = Caster_Boolean.INSTANCE.cast(s.getValue());
					optionsFormattedSink.put(AmiConsts.BITMAP, s.getValue());
				} else if (SH.equalsIgnoreCase(AmiConsts.ONDISK, key)) {
					isOndisk = Caster_Boolean.INSTANCE.cast(s.getValue());
					optionsFormattedSink.put(AmiConsts.ONDISK, s.getValue());
				} else if (SH.equalsIgnoreCase(AmiConsts.CACHE, key)) {
					cache = Caster_String.INSTANCE.cast(s.getValue());
					optionsFormattedSink.put(AmiConsts.CACHE, s.getValue());
				} else
					throw new RuntimeException("unknown directive: " + key);
			} catch (Exception e) {
				throw new RuntimeException("Column " + cname + " has invalid directive " + key + "=" + s.getValue() + " (" + e.getMessage() + ")", e);
			}
		}
		ColumnarColumn col2;
		if (cache != null && !isOndisk)
			if (isOndisk && (isAscii || isBitmap || isEnum))
				throw new RuntimeException("CACHE must be used in conjunction with ONDISK");
		if (isOndisk && (isAscii || isBitmap || isEnum))
			throw new RuntimeException("ONDISK can not be used in conjunction with other supplied directives");
		if (isCompact && isBitmap)
			throw new RuntimeException("BITMAP and COMPACT directive are mutually exclusive");
		if (isAscii && !isCompact)
			throw new RuntimeException("ASCII directive only supported for STRING columns with COMPACT option");
		if (isEnum) {
			col2 = new ColumnarColumnEnum(state.getEnumMapper(), this.table, 0, cname, 0, allowNull);
		} else if (isBitmap) {
			col2 = new ColumnarColumnString_BitMap(this.table, 0, cname, 0, allowNull);
		} else if (isOndisk) {
			try {
				final File path = this.getImdb().getState().getPersistDirectory();
				final File data = new File(path, getName() + "." + cname + ".oddat");
				if (this.getImdb().isStartupComplete() || SH.isnt(this.persisterEngineName)) {
					if (data.exists())
						data.delete();
				}
				long maxCacheSize = SH.is(cache) ? SH.parseMemoryToBytes(cache) : DEFAULT_COLUMN_CACHE_SIZE;
				if (type == AmiTable.TYPE_BINARY) {
					ColumnCache_Bytes c = new ColumnCache_Bytes(data, maxCacheSize);
					if (isStarted)
						c.open(OH.EMPTY_LONG_ARRAY);
					col2 = new ColumnarColumnObject_Cached<Bytes>(this.table, 0, Bytes.class, cname, 0, allowNull, c);
				} else if (type == AmiTable.TYPE_STRING) {
					ColumnCache_String c = new ColumnCache_String(data, maxCacheSize);
					if (isStarted)
						c.open(OH.EMPTY_LONG_ARRAY);
					col2 = new ColumnarColumnObject_Cached<String>(this.table, 0, String.class, cname, 0, allowNull, c);
				} else
					throw new RuntimeException("ONDISK directive only supported for STRING and BINARY columns");
			} catch (IOException e) {
				throw OH.toRuntime(e);
			}
		} else if (isCompact) {
			if (isAscii)
				col2 = new ColumnarColumnString_CompactAscii(this.table, 0, cname, 0, allowNull);
			else
				col2 = new ColumnarColumnString_CompactChars(this.table, 0, cname, 0, allowNull);
		} else {
			col2 = ColumnarTable.newColumnarColumnObject(this.table, clazz, cname, 0, allowNull);
		}
		if (!allowNull && getRowsCount() > 0) {
			AmiTableSetterGetter<ColumnarColumn<?>> sg = AmiCenterAmiUtilsForTable.getSetterGetter(type);
			if (!sg.isPrimitive())
				for (int i = 0; i < getRowsCount(); i++)
					col2.setValue(i, sg.getDefaultValue());
		}
		return col2;
	}

	private void rebuildColumns(AmiColumnImpl<?>[] columns) {
		List<AmiColumnImpl> nonReservedColumns = new ArrayList<AmiColumnImpl>();
		AmiColumnImpl<ColumnarColumnLong> reservedColumnD = null;
		AmiColumnImpl<ColumnarColumnObject<String>> reservedColumnP = null;
		AmiColumnImpl<ColumnarColumnObject<String>> reservedColumnI = null;
		AmiColumnImpl<ColumnarColumnLong> reservedColumnE = null;
		AmiColumnImpl<ColumnarColumnInt> reservedColumnV = null;
		AmiColumnImpl<ColumnarColumnLong> reservedColumnM = null;
		AmiColumnImpl<ColumnarColumnLong> reservedColumnC = null;
		AmiColumnImpl<ColumnarColumnEnum> reservedColumnT = null;
		boolean hasReserved = false;
		for (int i = 0; i < columns.length; i++) {
			AmiColumnImpl col = columns[i];
			if (col.isReserved()) {
				hasReserved = true;
				switch (col.getName().charAt(0)) {
					case AmiConsts.RESERVED_PARAM_APPLICATION:
						reservedColumnP = col;
						break;
					case AmiConsts.RESERVED_PARAM_ID:
						reservedColumnI = col;
						break;
					case AmiConsts.RESERVED_PARAM_EXPIRED:
						reservedColumnE = col;
						break;
					case AmiConsts.RESERVED_PARAM_REVISION:
						reservedColumnV = col;
						break;
					case AmiConsts.RESERVED_PARAM_MODIFIED_ON:
						reservedColumnM = col;
						break;
					case AmiConsts.RESERVED_PARAM_AMIID:
						reservedColumnD = col;
						break;
					case AmiConsts.RESERVED_PARAM_CREATED_ON:
						reservedColumnC = col;
						break;
					case AmiConsts.RESERVED_PARAM_TYPE:
						reservedColumnT = col;
						break;
				}
			} else
				nonReservedColumns.add(col);
		}
		int requiredCount = 0;
		int nonReservedCount = 0;
		for (AmiColumnImpl<?> i : columns) {
			if (!i.getAllowNull())
				requiredCount++;
			if (!i.isReserved())
				nonReservedCount++;
		}
		columnsCount = columns.length;
		if (columnsCount <= 128) {
			this.extendedColumnSplit = this.columnsCount;
			this.extendedColumnBucketSize1 = 1;
			this.extendedColumnBucketSize2 = 1;
		} else {
			int c = columnsCount - 64;
			this.extendedColumnBucketSize1 = c / 64;
			this.extendedColumnBucketSize2 = this.extendedColumnBucketSize1 + 1;
			this.extendedColumnSplit = 64 - (c % 64);
		}
		this.requiredColumns = new AmiColumnImpl[requiredCount];
		this.nonReservedColumns = new AmiColumnImpl[nonReservedCount];
		this.columnsByName.clear();
		this.columns.clear();
		for (int i = 0, j = 0, k = 0; i < columnsCount; i++) {
			AmiColumnImpl<?> col = columns[i];
			col.rebuildColumn();
			OH.assertEq(i, col.getLocation());
			if (!col.getAllowNull()) {
				this.requiredColumns[j++] = col;
			}
			this.columnsByName.put(col.getName(), col);
			this.columns.put(col.getParamKey(), col);
			if (!col.isReserved())
				this.nonReservedColumns[k++] = col;
		}
		this.columnsByPos = columns.clone();
		setReservedColumnD(reservedColumnD);
		setReservedColumnE(reservedColumnE);
		setReservedColumnV(reservedColumnV);
		setReservedColumnT(reservedColumnT);
		setReservedColumnPI(reservedColumnP, reservedColumnI);
		this.reservedColumnM = reservedColumnM;
		this.reservedColumnC = reservedColumnC;

		this.hasReservedColumns = hasReserved;
		this.nonReservedColumns = AH.toArray(nonReservedColumns, AmiColumnImpl.class);
		this.addRowBuf = createAmiPreparedRow();
		this.tmpPreparedRow = createAmiPreparedRow();
	}

	private void setReservedColumnPI(AmiColumnImpl<ColumnarColumnObject<String>> p, AmiColumnImpl<ColumnarColumnObject<String>> i) {
		if (isRem(this.reservedColumnI, i)) {
			indexI = null;
			indexPI = null;
		} else if (isAdd(reservedColumnI, i)) {
			if (p != null) {
				indexPI = new MapInMap<String, String, AmiRowImpl>();
				indexI = null;
			} else {
				indexPI = null;
				indexI = new HashMap<String, AmiRowImpl>();
			}
		} else if (this.reservedColumnI != null) {
			if (isAdd(reservedColumnP, p)) {
				indexPI = new MapInMap<String, String, AmiRowImpl>();
				this.indexPI.put(null, indexI);
				indexI = null;
			} else if (isRem(reservedColumnP, p)) {
				indexI = new HashMap<String, AmiRowImpl>();
				for (Map<String, AmiRowImpl> e : indexPI.values())
					indexI.putAll(e);//crazy edge condition, wh
				indexPI = null;
			}
		}
		this.reservedColumnP = p;
		this.reservedColumnI = i;
	}

	private void setReservedColumnD(AmiColumnImpl<ColumnarColumnLong> c) {
		if (isAdd(this.reservedColumnD, c)) {
			ColumnarColumnLong cc = c.getColumn();
			for (AmiRowImpl row : this.rows)
				cc.setLong(row, row.getAmiId());
		}
		this.reservedColumnD = c;
	}

	private void setReservedColumnT(AmiColumnImpl<ColumnarColumnEnum> c) {
		if (isAdd(this.reservedColumnT, c)) {
			ColumnarColumnEnum cc = c.getColumn();
			String name = this.getName();
			for (AmiRowImpl row : this.rows)
				cc.setValue(row, name);
		}
		this.reservedColumnT = c;
	}

	private void setReservedColumnV(AmiColumnImpl<ColumnarColumnInt> c) {
		if (isAdd(this.reservedColumnV, c)) {
			ColumnarColumnInt cc = c.getColumn();
			for (AmiRowImpl row : this.rows)
				cc.setInt(row, 0);
		}
		this.reservedColumnV = c;
	}

	private void setReservedColumnE(AmiColumnImpl<ColumnarColumnLong> c) {
		if (isAdd(this.reservedColumnE, c)) {
			this.rowsWithExpiresTimeCount = 0;
			this.soonestExpiresTime = 0;
			if (this.imdb.getObjectsManager().getAmiTable(this.getName()) == this)
				this.imdb.getObjectsManager().registerTableWithExpiry(this);
		} else if (isRem(this.reservedColumnE, c)) {
			this.rowsWithExpiresTimeCount = 0;
			this.soonestExpiresTime = 0;
			if (this.imdb.getObjectsManager().getAmiTable(this.getName()) == this)
				this.imdb.getObjectsManager().unregisterTableWithExpiry(this);
		}
		this.reservedColumnE = c;
	}

	private static boolean isAdd(AmiColumnImpl<?> existing, AmiColumnImpl<?> nuw) {
		return existing == null && nuw != null;
	}
	private static boolean isRem(AmiColumnImpl<?> existing, AmiColumnImpl<?> nuw) {
		return existing != null && nuw == null;
	}

	public void onTriggerRenamed(String oldName, String newName) {
		AmiTriggerBindingImpl tr = this.triggerBindingsByName.remove(oldName);
		this.triggerBindingsByName.put(newName, tr);
		rebuildTriggersList();
	}
	public void onIndexRenamed(String oldName, String newName) {
		AmiIndexImpl tr = this.indexesByName.remove(oldName);
		this.indexesByName.put(newName, tr);
		rebuildIndexMasks();
	}

	private static class IdList {
		private LongArrayList ids = new LongArrayList();
		private LongSet removed = new LongSet();

		public void add(long id) {
			ids.add(id);
			if (!removed.isEmpty())
				removed.remove(id);
		}
		public void removeLong(long id) {
			final int size = ids.size();
			if (size > 0 && ids.getLong(size - 1) == id) {
				ids.removeAt(size - 1);
				return;
			}
			this.removed.add(id);
		}
		public void clear() {
			ids.clear();
			removed.clear();
		}
		public LongSet toSet() {
			if (ids.isEmpty())
				return LongSet.EMPTY;
			LongSet r = new LongSet();
			r.addAll(ids);
			r.removeAll((LongIterable) removed);
			return r;
		}

		public int size() {
			return ids.size();
		}
		public long getLong(int i) {
			long r = ids.getLong(i);
			if (removed.contains(r))
				return -1;
			return r;

		}
	}

	public boolean hasReservedColumns() {
		return this.hasReservedColumns;
	}

	public byte getOnUndefinedColumn() {
		return this.onUndefinedColumn;
	}

	public int getInitialCapacity() {
		return this.initialCapacity;
	}

	public AmiColumnImpl<?>[] getRequiredColumns() {
		return this.requiredColumns;
	}

	public boolean isInInsertUpdate() {
		return isInInsertUpdate;
	}

	public int getExtendedColumnBucketLocation(int loc) {
		loc -= 64;
		if (loc < this.extendedColumnSplit * this.extendedColumnBucketSize1)
			return loc / this.extendedColumnBucketSize1;
		else {
			int r = this.extendedColumnSplit + (loc - this.extendedColumnSplit * this.extendedColumnBucketSize1) / this.extendedColumnBucketSize2;
			return r;
		}
	}

	public static void main(String a[]) {
		StringBuilder sb = new StringBuilder();
		int n = 500;
		sb.append("CREATE PUBLIC TABLE T").append(n).append("(");
		for (int i = 0; i < n; i++) {
			if (i > 0)
				sb.append(", ");
			sb.append("c").append(i).append(" int");
		}
		sb.append(");");
		sb.append(SH.NEWLINE);
		sb.append("INSERT INTO T").append(n).append(" values(");
		for (int i = 0; i < n; i++) {
			if (i > 0)
				sb.append(", ");
			sb.append(i);
		}
		sb.append(");");
		sb.append(SH.NEWLINE);
		System.out.println(sb);
	}

	public AmiIndexImpl getPrimaryIndex() {
		return this.primaryKeyIndex;
	}

	public int getIndexesCount() {
		return this.indexes.length;
	}

	public AmiIndexImpl getIndexAt(int i) {
		return this.indexes[i];
	}
}
