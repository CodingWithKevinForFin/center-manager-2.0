package com.f1.ami.center.table;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.AmiCenterUtils;
import com.f1.ami.center.table.index.AmiIndexMap;
import com.f1.ami.center.table.index.AmiIndexMap_Hash;
import com.f1.ami.center.table.index.AmiIndexMap_Rows;
import com.f1.ami.center.table.index.AmiIndexMap_Series;
import com.f1.ami.center.table.index.AmiIndexMap_Tree;
import com.f1.ami.center.table.index.AmiQueryFinderVisitor;
import com.f1.ami.center.table.keygen.AmiKeyGenerator;
import com.f1.ami.center.table.keygen.AmiKeyGenerator_DoubleRand;
import com.f1.ami.center.table.keygen.AmiKeyGenerator_FloatRand;
import com.f1.ami.center.table.keygen.AmiKeyGenerator_IntInc;
import com.f1.ami.center.table.keygen.AmiKeyGenerator_IntRand;
import com.f1.ami.center.table.keygen.AmiKeyGenerator_LongInc;
import com.f1.ami.center.table.keygen.AmiKeyGenerator_LongRand;
import com.f1.ami.center.table.keygen.AmiKeyGenerator_StringRand;
import com.f1.ami.center.table.keygen.AmiKeyGenerator_UUIDRand;
import com.f1.base.ToStringable;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiIndexImpl implements AmiIndex, ToStringable {

	public static final String UNIQUE_CONSTRAINT = "UNIQUE_CONSTRAINT";
	private static final Logger log = LH.get();
	final private AmiTableImpl table;
	final private AmiColumnImpl<?>[] columns;
	final private byte[] indexTypes;
	final private IntKeyMap<AmiIndexMap_Rows> rowUidsToMap = new IntKeyMap<AmiIndexMap_Rows>();
	final private AmiIndexMap[] parentsBuf;
	final private Comparable[] keysBuf;
	final private AmiIndexMap map;
	final private String indexName;
	final private boolean isUnique;
	final private int columnsCount;
	final private int columnsCountMinusOne;
	final private String description;
	final private byte defType;
	final private byte constraintType;
	final private boolean isPrimaryKey;
	private byte autogenType;

	@Override
	public long getMemorySize() {
		long pointers = (EH.ADDRESS_SIZE + EH.ESTIMATED_GC_OVERHEAD) * 8 + 12;
		long rowUidsToMapSize = rowUidsToMap.getMemorySize();
		return pointers + rowUidsToMapSize + map.getMemorySize();
	}

	public AmiIndexImpl(byte defType, AmiCenterState amiCenterState, AmiTableImpl table, String name, List<String> columns, List<Byte> sorted, byte constraintType,
			byte autogenType) {
		this.table = table;
		this.autogenType = autogenType;
		this.defType = defType;
		this.indexName = name;
		this.columnsCount = columns.size();
		this.columnsCountMinusOne = this.columnsCount - 1;
		OH.assertEq(this.columnsCount, sorted.size());
		OH.assertGt(this.columnsCount, 0);
		this.columns = new AmiColumnImpl[this.columnsCount];
		this.indexTypes = new byte[this.columnsCount + 1];
		this.indexTypes[this.columnsCount] = AmiIndexMap.TYPE_VALUES;
		for (int i = 0; i < this.columnsCount; i++) {
			AmiColumnImpl<?> col = table.getColumn(columns.get(i));
			this.columns[i] = col;
			if (col.getAmiType() == AmiTable.TYPE_BINARY)
				throw new RuntimeException("For Index " + getName() + " on table " + table.getName() + ": Can not create indexes on BINARY columns: " + col);
			if (col.getLocation() > 63)
				throw new RuntimeException("For Index " + getName() + " on table " + table.getName() + ": Only the first 64 columns can participate in an index: " + col);
			this.columnsLocationsMask |= (1L << col.getLocation());
			byte type = sorted.get(i);
			this.indexTypes[i] = type;
			assertTypeValid(col, type);
		}
		this.constraintType = constraintType;
		switch (constraintType) {
			case CONSTRAINT_TYPE_NONE:
				isUnique = false;
				isPrimaryKey = false;
				if (autogenType != AUTOGEN_NONE)
					throw new RuntimeException("For Index " + getName() + " on table " + table.getName() + ": AutoGen option only supported on PRIMARY index: "
							+ AmiTableUtils.toStringForIndexAutoGenType(autogenType));
				break;
			case CONSTRAINT_TYPE_UNIQUE:
				isUnique = true;
				isPrimaryKey = false;
				if (autogenType != AUTOGEN_NONE)
					throw new RuntimeException("For Index " + getName() + " on table " + table.getName() + ": AutoGen option only supported on PRIMARY index: "
							+ AmiTableUtils.toStringForIndexAutoGenType(autogenType));
				break;
			case CONSTRAINT_TYPE_PRIMARY:
				isUnique = true;
				isPrimaryKey = true;
				getAutoGen();
				break;
			default:
				throw new NoSuchElementException("Index Type: " + constraintType);
		}
		this.parentsBuf = new AmiIndexMap[columnsCountMinusOne];
		this.keysBuf = new Comparable[this.columnsCount];
		this.map = newMap(0);
		this.description = "[" + SH.join("+", columns) + "]";
	}
	public AmiKeyGenerator getAutoGen() {
		if (autogenType == AUTOGEN_NONE)
			return null;
		if (this.columnsCount != 1)
			throw new RuntimeException("For Index " + getName() + " on table " + table.getName() + ": AutoGen option only supported for PRIMARY index with one column");
		AmiColumnImpl<?> col = this.columns[0];
		switch (col.getAmiType()) {
			case AmiTable.TYPE_INT:
				if (autogenType == AUTOGEN_INC)
					return new AmiKeyGenerator_IntInc(col);
				else if (autogenType == AUTOGEN_RAND)
					return new AmiKeyGenerator_IntRand(col, this);
				break;
			case AmiTable.TYPE_LONG:
				if (autogenType == AUTOGEN_INC)
					return new AmiKeyGenerator_LongInc(col);
				else if (autogenType == AUTOGEN_RAND)
					return new AmiKeyGenerator_LongRand(col, this);
				break;
			case AmiTable.TYPE_STRING:
				if (autogenType == AUTOGEN_RAND)
					return new AmiKeyGenerator_StringRand(col, this);
				break;
			case AmiTable.TYPE_UUID:
				if (autogenType == AUTOGEN_RAND)
					return new AmiKeyGenerator_UUIDRand(col);
				break;
			case AmiTable.TYPE_FLOAT:
				if (autogenType == AUTOGEN_RAND)
					return new AmiKeyGenerator_FloatRand(col, this);
				break;
			case AmiTable.TYPE_DOUBLE:
				if (autogenType == AUTOGEN_RAND)
					return new AmiKeyGenerator_DoubleRand(col, this);
				break;
		}
		throw new RuntimeException("For Index " + getName() + " on table " + table.getName() + ": AutoGen " + AmiTableUtils.toStringForIndexAutoGenType(this.autogenType)
				+ " not supported for " + AmiTableUtils.toStringForDataType(col.getAmiType()));
	}

	public void clear() {
		rowUidsToMap.clear();
	}
	private void assertTypeValid(AmiColumnImpl<?> col, byte type) {
		switch (type) {
			case AmiIndexMap.TYPE_SERIES:
				if (col.getAmiType() != AmiTable.TYPE_LONG)
					throw new RuntimeException(
							"For Index " + getName() + " on table " + table.getName() + ": SERIES index only available on columns of type LONG: " + col.getName());
		}
	}

	@Override
	public AmiTableImpl getTable() {
		return this.table;
	}

	private AmiIndexMap newMap(int i) {
		switch (this.indexTypes[i]) {
			case AmiIndexMap.TYPE_HASH:
				return new AmiIndexMap_Hash();
			case AmiIndexMap.TYPE_SERIES:
				return new AmiIndexMap_Series();
			case AmiIndexMap.TYPE_SORT:
				return new AmiIndexMap_Tree();
			default:
				throw new RuntimeException("bad type: " + this.indexTypes[i]);
		}
	}

	@Override
	public int getColumnsCount() {
		return this.columnsCount;
	}

	@Override
	public AmiColumnImpl getColumn(int i) {
		return this.columns[i];
	}

	/**
	 * 
	 * @param row
	 * @param columnsChangesMask
	 * @param warningsSink
	 * @return false if unique constraint failed
	 */
	public void updateRow(AmiRowImpl row, long columnsChangesMask) {
		if (row.getLocation() == -1)
			return;
		if (MH.anyBits(this.columnsLocationsMask, columnsChangesMask)) {
			AmiIndexMap_Rows rows = rowUidsToMap.get(row.getUid());
			if (rows != addRow2(row, true))
				removeInner(rows, row);
		}
	}
	/**
	 * 
	 * @param row
	 * @param warnings
	 * @return false if unique constraint failed
	 */
	public void addRow(AmiRowImpl row) {
		addRow2(row, false);
	}
	private AmiIndexMap_Rows addRow2(AmiRowImpl row, boolean isUpdate) {
		AmiIndexMap curmap = this.map;
		for (int i = 0; i < columnsCountMinusOne; i++) {
			Comparable value = (Comparable) columns[i].getComparable(row);
			AmiIndexMap m2 = curmap.getIndex(value);
			if (m2 == null)
				curmap.putIndex(value, m2 = newMap(i + 1));
			parentsBuf[i] = m2;
			this.keysBuf[i] = value;
			curmap = m2;
		}
		Comparable value = (Comparable) columns[this.columnsCountMinusOne].getComparable(row);
		this.keysBuf[this.columnsCountMinusOne] = value;
		AmiIndexMap_Rows mp = (AmiIndexMap_Rows) curmap.getIndex(value);
		if (mp == null)
			curmap.putIndex(value, mp = new AmiIndexMap_Rows(parentsBuf.length == 0 ? parentsBuf : parentsBuf.clone(), keysBuf.clone(), row));
		else {
			if (this.isUnique) {
				if (isUpdate && this.rowUidsToMap.get(row.getUid()) == mp)
					return mp;
				else
					throw new IllegalStateException(description + " ==> " + row.toString());
			}
			mp.put(row);
		}
		rowUidsToMap.put(row.getUid(), mp);
		return mp;
	}
	public boolean canAddRow(AmiRow row, boolean produceWarning, CalcFrameStack sf) {
		if (!isUnique)
			return true;
		AmiIndexMap curmap = this.map;
		for (int i = 0; i < columnsCount; i++) {
			AmiIndexMap m2 = curmap.getIndex((Comparable) row.getComparable(columns[i]));
			if (m2 == null)
				return true;
			curmap = m2;
		}
		if (produceWarning)
			AmiCenterUtils.getSession(sf).onWarning(UNIQUE_CONSTRAINT, getTable(), getName(), "ADD_ROW", description, row, null);
		return false;
	}
	public AmiRowImpl getUniqueValue(AmiRow row) {
		OH.assertTrue(this.isUnique);
		AmiIndexMap curmap = this.map;
		for (int i = 0; i < columnsCount; i++) {
			AmiIndexMap m2 = curmap.getIndex((Comparable) row.getComparable(columns[i]));
			if (m2 == null)
				return null;
			curmap = m2;
		}
		return ((AmiIndexMap_Rows) curmap).getSingleValue();
	}
	public boolean canUpdateRow(AmiPreparedRowImpl row, AmiRowImpl existing, long columnsChangesMask, boolean produceWarning, CalcFrameStack sf) {
		if (!isUnique || !MH.anyBits(columnsLocationsMask, columnsChangesMask))
			return true;
		AmiIndexMap curmap = this.map;
		for (int i = 0; i < columnsCount; i++) {
			AmiColumnImpl<?> amiColumnImpl = columns[i];
			Comparable<?> value = !row.isSet(amiColumnImpl) ? amiColumnImpl.getComparable(existing) : row.getComparable(amiColumnImpl);
			AmiIndexMap m2 = curmap.getIndex(value);
			if (m2 == null) {
				return true;
			}
			curmap = m2;
		}
		if (this.rowUidsToMap.get(existing.getUid()) == curmap)
			return true;
		if (produceWarning)
			AmiCenterUtils.getSession(sf).onWarning(UNIQUE_CONSTRAINT, getTable(), getName(), "UPDATE_ROW", description, row, null);
		return false;
	}
	private void removeInner(AmiIndexMap_Rows rows, AmiRowImpl row) {
		if (rows.remove(row.getUid())) {
			for (int i = this.columnsCountMinusOne; i >= 0; i--) {
				AmiIndexMap m = i == 0 ? (AmiIndexMap) this.map : rows.maps[i - 1];
				Comparable key = rows.keys[i];
				m.removeIndex(key);
				if (!m.isIndexEmpty())
					break;
			}
		}
	}
	public void removeRow(AmiRowImpl row) {
		AmiIndexMap_Rows rows = rowUidsToMap.remove(row.getUid());
		if (rows == null) {
			LH.warning(log, this.toString(), " Missing row: ", row.getUid() + " ==> ", row);
			return;
		}
		removeInner(rows, row);
	}
	public String getName() {
		return this.indexName;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}
	@Override
	public StringBuilder toString(StringBuilder sb) {
		sb.append(this.indexName);
		sb.append(" ON ");
		sb.append(this.table.getName()).append("(");
		for (int i = 0; i < this.columnsCount; i++) {
			if (i > 0)
				sb.append(", ");
			sb.append(this.columns[i].getName()).append(' ');
			sb.append(AmiTableUtils.toStringForIndexType(this.indexTypes[i]));
		}
		sb.append(")");
		return sb;
	}

	public byte getIndexTypeAt(int pos) {
		return indexTypes[pos];
	}
	public AmiIndexMap getRootMap() {
		return this.map;
	}

	private long columnsLocationsMask = 0;

	public void getRows(List<AmiRow> sink, int limit, AmiPreparedQueryImpl query) {
		AmiQueryFinderVisitor fv = new AmiQueryFinderVisitor(query);
		fv.find(sink, limit);
	}

	public String debug() {
		StringBuilder sb = new StringBuilder();
		toString(sb);
		sb.append("\n----\n");
		debug(sb, (AmiIndexMap) this.map, 0);
		return sb.toString();
	}

	private void debug(StringBuilder sb, AmiIndexMap map2, int i) {
		if (map2 instanceof AmiIndexMap_Rows) {
			AmiIndexMap_Rows rows = (AmiIndexMap_Rows) map2;
			List<AmiRow> sink = new ArrayList<AmiRow>();
			rows.fill(sink, 100000, null);
			for (AmiRow t : sink) {
				SH.repeat(' ', i * 2, sb).append(t).append(SH.NEWLINE);
			}
		} else {
			for (Comparable t : map2.getKeysForDebug()) {
				SH.repeat(' ', i * 2, sb).append(t).append(SH.NEWLINE);
				AmiIndexMap idx = map2.getIndex(t);
				debug(sb, idx, i + 1);
			}
		}

	}
	public long getParticipatingColumnLocationMasks() {
		return columnsLocationsMask;
	}

	@Override
	public byte getDefType() {
		return defType;
	}
	public boolean getIsUnique() {
		return this.isUnique;
	}
	public boolean getIsPrimaryKey() {
		return isPrimaryKey;
	}
	public byte getConstraintType() {
		return constraintType;
	}

	public boolean isHigherCardinality(AmiIndexImpl bestIndex) {
		return isPrimaryKey || isUnique || map.getKeysCount() > bestIndex.map.getKeysCount();
	}

	public byte getAutoGenType() {
		return this.autogenType;
	}
}
