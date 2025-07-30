package com.f1.ami.center.table;

import java.util.HashMap;
import java.util.Map;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiDataEntity;
import com.f1.ami.center.AmiCenterAmiUtilsForTable;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.AmiCenterUtils;
import com.f1.base.Caster;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.structs.table.columnar.ColumnarColumn;
import com.f1.utils.structs.table.columnar.ColumnarColumnObject_Cached;
import com.f1.utils.structs.table.columnar.ColumnarColumnString_BitMap;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiColumnImpl<T extends ColumnarColumn<?>> implements AmiColumn {
	static final public byte RESERVED = 1;
	static final public byte CAN_INSERT = 2;
	static final public byte CAN_UPDATE = 4;

	private T column;
	private short paramKey;
	private byte paramType;
	private String name;
	final private AmiTableImpl table;
	final private AmiCenterState state;
	private AmiTableSetterGetter<T> getterSetter;
	private Class<?> type;
	final private boolean allowNull;
	private long mask0;
	private long mask64;
	final private boolean isBroadcast;
	private boolean participatesInIndex;
	private boolean participatesInUniqueIndex;
	final private byte reservedType;
	private Map<String, String> options;
	private String optionsString;
	private boolean isOnDisk;
	final private boolean isStringBitmap;

	public AmiColumnImpl(AmiTableImpl table, T column, byte type, Map<String, String> options, boolean isBroadcast) {
		this.allowNull = column.getAllowNull();
		this.isBroadcast = isBroadcast;
		this.isStringBitmap = column instanceof ColumnarColumnString_BitMap;
		this.table = table;
		this.state = this.table.getState();
		this.column = column;
		this.paramType = type;
		this.options = options;
		this.optionsString = AmiTableUtils.toOptionsString(this.options);
		this.isOnDisk = options.containsKey(AmiConsts.ONDISK);
		//		rebuildColumn();
		this.name = (String) column.getId();
		this.paramKey = state.getAmiKeyId(this.name);
		this.type = getClassForValueType(paramType);
		if (name.length() == 1) {
			switch (name.charAt(0)) {
				case AmiConsts.RESERVED_PARAM_REVISION:
				case AmiConsts.RESERVED_PARAM_MODIFIED_ON:
				case AmiConsts.RESERVED_PARAM_AMIID:
				case AmiConsts.RESERVED_PARAM_CREATED_ON:
				case AmiConsts.RESERVED_PARAM_TYPE:
					this.reservedType = RESERVED;
					break;
				case AmiConsts.RESERVED_PARAM_APPLICATION:
				case AmiConsts.RESERVED_PARAM_ID:
					this.reservedType = RESERVED | CAN_INSERT;
					break;
				case AmiConsts.RESERVED_PARAM_EXPIRED:
					this.reservedType = RESERVED | CAN_INSERT | CAN_UPDATE;
					break;
				default:
					this.reservedType = CAN_INSERT | CAN_UPDATE;
			}
		} else
			this.reservedType = CAN_INSERT | CAN_UPDATE;
		this.getterSetter = AmiCenterAmiUtilsForTable.getSetterGetter(this);
	}
	protected void rebuildColumn() {
		this.name = (String) column.getId();
		this.paramKey = state.getAmiKeyId(this.name);
		int loc = column.getLocation();
		if (loc < 64) {
			this.mask0 = 1L << loc;
			this.mask64 = 0;
		} else {
			this.mask0 = 0;
			this.mask64 = 1L << ((long) this.table.getExtendedColumnBucketLocation(loc));
		}
		this.getterSetter = AmiCenterAmiUtilsForTable.getSetterGetter(this);
	}
	public T getColumn() {
		return column;
	}

	public short getParamKey() {
		return this.paramKey;
	}

	@Override
	public AmiTableImpl getAmiTable() {
		return table;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public byte getAmiType() {
		return paramType;
	}

	@Override
	public int getLocation() {
		return this.column.getLocation();
	}

	@Override
	public String getString(AmiRow row) {
		return getterSetter.getString(column, (AmiRowImpl) row);
	}
	@Override
	public long getLong(AmiRow row) {
		return getterSetter.getLong(column, (AmiRowImpl) row);
	}
	@Override
	public double getDouble(AmiRow row) {
		return getterSetter.getDouble(column, (AmiRowImpl) row);
	}
	@Override
	public boolean getIsNull(AmiRow row) {
		return column.isNull((AmiRowImpl) row);
	}
	@Override
	public boolean setString(AmiRow row, String value, CalcFrameStack fs) {
		try {
			if (!allowNull && value == null)
				return onNullConstraint(row, fs);
			AmiRowImpl aRow = (AmiRowImpl) row;
			if (participatesInUniqueIndex && !table.isInInsertUpdate()) {
				AmiPreparedRowImpl tmp = table.borrowPreparedRow();
				tmp.reset();
				tmp.setString(this, value, fs);
				if (!table.canUpdate(tmp, (AmiRowImpl) row, this.mask0, fs))
					return false;
			}
			if (getterSetter.setString(column, aRow, value)) {
				if (participatesInIndex)
					this.table.updateIndexes((AmiRowImpl) row, this.mask0);
				if (isBroadcast)
					aRow.applyValueChangeMask(this.mask0, this.mask64);
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new RuntimeException("Error with setting " + this.table.getName() + "::" + getName() + " to " + value, e);
		}

	}
	@Override
	public boolean setLong(AmiRow row, long value, CalcFrameStack fs) {
		try {
			if (!allowNull && value == AmiTable.NULL_NUMBER)
				return onNullConstraint(row, fs);
			AmiRowImpl aRow = (AmiRowImpl) row;
			if (participatesInUniqueIndex && !table.isInInsertUpdate()) {
				AmiPreparedRowImpl tmp = table.borrowPreparedRow();
				tmp.reset();
				tmp.setLong(this, value, fs);
				if (!table.canUpdate(tmp, (AmiRowImpl) row, this.mask0, fs))
					return false;
			}
			if (getterSetter.setLong(column, aRow, value)) {
				if (participatesInIndex)//TODO: add participating in uniqueIndex
					this.table.updateIndexes((AmiRowImpl) row, this.mask0);
				if (isBroadcast)
					aRow.applyValueChangeMask(this.mask0, this.mask64);
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new RuntimeException("Error with setting " + this.table.getName() + "::" + getName() + " to " + value, e);
		}
	}
	@Override
	public boolean setDouble(AmiRow row, double value, CalcFrameStack fs) {
		try {
			if (!allowNull && value != value)
				return onNullConstraint(row, fs);
			AmiRowImpl aRow = (AmiRowImpl) row;
			if (participatesInUniqueIndex && !table.isInInsertUpdate()) {//TODO: add participating in uniqueIndex
				AmiPreparedRowImpl tmp = table.borrowPreparedRow();
				tmp.reset();
				tmp.setDouble(this, value, fs);
				if (!table.canUpdate(tmp, (AmiRowImpl) row, this.mask0, fs))
					return false;
			}
			if (getterSetter.setDouble(column, aRow, value)) {
				if (participatesInIndex)
					this.table.updateIndexes((AmiRowImpl) row, this.mask0);
				if (isBroadcast)
					aRow.applyValueChangeMask(this.mask0, this.mask64);
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new RuntimeException("Error with setting " + this.table.getName() + "::" + getName() + " to " + value, e);
		}
	}
	@Override
	public boolean setNull(AmiRow row, CalcFrameStack fs) {
		try {
			if (!allowNull)
				return onNullConstraint(row, fs);
			if (participatesInUniqueIndex && !table.isInInsertUpdate()) {//TODO: add participating in uniqueIndex
				AmiPreparedRowImpl tmp = table.borrowPreparedRow();
				tmp.reset();
				tmp.setNull(this, fs);
				if (!table.canUpdate(tmp, (AmiRowImpl) row, this.mask0, fs))
					return false;
			}
			if (column.setNull((AmiRowImpl) row)) {
				if (participatesInIndex)
					this.table.updateIndexes((AmiRowImpl) row, this.mask0);
				if (isBroadcast)
					((AmiRowImpl) row).applyValueChangeMask(this.mask0, this.mask64);
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new RuntimeException("Error with setting " + this.table.getName() + "::" + getName() + " to null", e);
		}
	}
	private boolean onNullConstraint(AmiRow row, CalcFrameStack fs) {
		AmiCenterUtils.getSession(fs).onWarning(AmiTableImpl.NULL_CONSTRAINT, this.table, getName(), null, null, row, null);
		return false;
	}
	@Override
	public boolean copyToFrom(AmiRow toRow, AmiColumn fromCol, AmiRow fromRow, CalcFrameStack fs) {
		if (fromRow.getIsNull(fromCol))
			return toRow.setNull(this, fs);
		else {
			switch (paramType) {
				case AmiTable.TYPE_BOOLEAN:
				case AmiTable.TYPE_LONG:
				case AmiTable.TYPE_INT:
				case AmiTable.TYPE_SHORT:
				case AmiTable.TYPE_BYTE:
				case AmiTable.TYPE_UTC:
				case AmiTable.TYPE_UTCN:
				case AmiTable.TYPE_CHAR:
					return toRow.setLong(this, fromRow.getLong(fromCol), fs);
				case AmiTable.TYPE_STRING:
				case AmiTable.TYPE_ENUM:
					return toRow.setString(this, fromRow.getString(fromCol), fs);
				case AmiTable.TYPE_DOUBLE:
				case AmiTable.TYPE_FLOAT:
					return toRow.setDouble(this, fromRow.getDouble(fromCol), fs);
				case AmiTable.TYPE_BINARY:
				case AmiTable.TYPE_COMPLEX:
				case AmiTable.TYPE_UUID:
				case AmiTable.TYPE_BIGINT:
				case AmiTable.TYPE_BIGDEC:
					return toRow.setComparable(this, fromRow.getComparable(fromCol), fs);
				default:
					throw new RuntimeException("bad type: " + paramType);
			}
		}
	}
	@Override
	public boolean areEqual(AmiRow row, AmiColumn col2, AmiRow row2) {
		boolean n1 = this.getIsNull(row);
		boolean n2 = row2.getIsNull(col2);
		if (n1 || n2)
			return n1 == n2;
		switch (paramType) {
			case AmiTable.TYPE_BOOLEAN:
			case AmiTable.TYPE_LONG:
			case AmiTable.TYPE_INT:
			case AmiTable.TYPE_SHORT:
			case AmiTable.TYPE_BYTE:
			case AmiTable.TYPE_UTC:
			case AmiTable.TYPE_UTCN:
			case AmiTable.TYPE_CHAR:
				return row.getLong(this) == row2.getLong(col2);
			case AmiTable.TYPE_STRING:
			case AmiTable.TYPE_ENUM:
				return OH.eq(row.getString(this), row2.getString(col2));
			case AmiTable.TYPE_DOUBLE:
			case AmiTable.TYPE_FLOAT:
				return row.getDouble(this) == row2.getDouble(col2);
			case AmiTable.TYPE_BINARY:
				return OH.eq(row.getComparable(this), row2.getComparable(col2));
			case AmiTable.TYPE_COMPLEX:
			case AmiTable.TYPE_UUID:
			case AmiTable.TYPE_BIGINT:
			case AmiTable.TYPE_BIGDEC:
				return OH.eq(row.getComparable(this), row2.getComparable(col2));
			default:
				throw new RuntimeException("bad type: " + paramType);
		}
	}
	public Class<?> getType() {
		return type;
	}
	@Override
	public Comparable getComparable(AmiRow row) {
		return getterSetter.getComparable(column, (AmiRowImpl) row);
	}

	@Override
	public String toString() {
		return this.name;
	}
	public boolean getAllowNull() {
		return allowNull;
	}
	public long getColumnPositionMask0() {
		return this.mask0;
	}
	public long getColumnPositionMask64() {
		return this.mask64;
	}
	public Object setComparable(AmiRowImpl row, Object value, CalcFrameStack fs) {
		try {
			if (value == null)
				setNull(row, fs);
			else if (value instanceof Number) {
				if (value instanceof Double || value instanceof Float)
					setDouble(row, ((Number) value).doubleValue(), fs);
				else
					setLong(row, ((Number) value).longValue(), fs);
			} else if (value instanceof Boolean)
				setLong(row, ((Boolean) value).booleanValue() ? 1L : 0L, fs);
			else if (value instanceof Character)
				setLong(row, (Character) value, fs);
			else
				setString(row, (String) value, fs);
			return null;
		} catch (Exception e) {
			throw new RuntimeException("Error with setting " + this.table.getName() + "::" + getName() + " to " + value, e);
		}
	}

	protected void setParticipatesInIndex(boolean participatesInIndex, boolean participatesInUniqueIndex) {
		if (!participatesInIndex && participatesInUniqueIndex)
			throw new IllegalArgumentException();
		this.participatesInIndex = participatesInIndex;
		this.participatesInUniqueIndex = participatesInUniqueIndex;
	}
	//	public void rename(String newName) {
	//		int loc = this.column.getLocation();
	//		this.type = getClassForValueType(paramType);
	//		ColumnarTable t = (ColumnarTable) this.column.getTable();
	//		Object values = this.column.getValues();
	//		long[] nulls = this.column.getValueNullsMasks();
	//		t.removeColumn(loc);
	//		boolean isEnum = false;
	//		Class clazz;
	//		switch (this.paramType) {
	//			case AmiTable.TYPE_UTC:
	//			case AmiTable.TYPE_UTCN:
	//				clazz = long.class;
	//				break;
	//			case AmiTable.TYPE_ENUM:
	//				isEnum = true;
	//				clazz = int.class;
	//				break;
	//			default:
	//				clazz = getClassForValueType(paramType);
	//				break;
	//		}
	//		AmiColumnImpl col;
	//		if (isEnum)
	//			this.column = (T) t.addColumnEnum(loc, newName, null, allowNull, state.getEnumMapper());
	//		else
	//			this.column = (T) t.addColumn(loc, clazz, newName, null, allowNull);
	//		int size = t.getSize();
	//		Caster<?> caster = this.column.getTypeCaster();
	//		for (int i = 0; i < size; i++) {
	//			Object value = Array.get(values, i);
	//			t.setAt(i, loc, caster.castNoThrow(value));
	//		}
	//		this.name = newName;
	//		rebuildColumn();
	//		this.column.setValuesAndNulls(values, nulls);
	//	}
	public void setAmiType(String newName, byte newType, Map<String, String> o) {
		final Class clazz;
		boolean isEnum = false;
		switch (newType) {
			case AmiTable.TYPE_UTC:
				clazz = DateMillis.class;
				break;
			case AmiTable.TYPE_UTCN:
				clazz = DateNanos.class;
				break;
			case AmiTable.TYPE_ENUM:
				isEnum = true;
				clazz = int.class;
				break;
			case AmiTable.TYPE_SHORT:
				clazz = short.class;
				break;
			case AmiTable.TYPE_BYTE:
				clazz = byte.class;
				break;
			default:
				clazz = AmiUtils.getClassForValueType(newType);
				break;
		}
		String cname = newName != null ? newName : (String) this.column.getId();
		Map optionsFormatted = new HashMap<String, String>();
		T newColumn = (T) table.createInnerColumn(isEnum, clazz, newType, o, cname, optionsFormatted);
		this.name = newName;
		int loc = this.column.getLocation();
		this.paramType = newType;
		this.type = getClassForValueType(paramType);
		this.options = optionsFormatted;
		this.optionsString = AmiTableUtils.toOptionsString(this.options);
		this.isOnDisk = optionsFormatted.containsKey(AmiConsts.ONDISK);
		ColumnarTable t = (ColumnarTable) this.column.getTable();
		int size = t.getSize();
		Object values[] = new Object[size];
		for (int i = 0; i < size; i++)
			values[i] = this.column.getValue(i);
		this.column = newColumn;
		t.removeColumn(loc);
		t.addColumn(loc, this.column);
		Caster<?> caster = this.column.getTypeCaster();
		if (this.column.getAllowNull()) {
			for (int i = 0; i < size; i++)
				t.setAt(i, loc, caster.castNoThrow(values[i]));
		} else {
			Object dflt = this.getterSetter.getDefaultValue();
			for (int i = 0; i < size; i++) {
				Object castNoThrow = caster.castNoThrow(values[i]);
				t.setAt(i, loc, castNoThrow == null ? dflt : castNoThrow);
			}
		}
		rebuildColumn();
	}
	private Class getClassForValueType(byte type) {
		switch (type) {
			case AmiDataEntity.PARAM_TYPE_INT1:
				return Byte.class;
			case AmiDataEntity.PARAM_TYPE_INT2:
				return Short.class;
			default:
				return AmiUtils.getClassForValueType(type);
		}
	}
	public byte getReservedType() {
		return this.reservedType;
	}
	public boolean isReserved() {
		return MH.anyBits(this.reservedType, RESERVED);
	}
	public Map<String, String> getOptions() {
		return options;
	}
	public String getOptionsString() {
		return optionsString;
	}
	@Override
	public boolean setComparable(AmiRow row, Comparable value, CalcFrameStack fs) {
		try {
			if (!allowNull && value == null)
				return onNullConstraint(row, fs);
			AmiRowImpl aRow = (AmiRowImpl) row;
			if (participatesInUniqueIndex && !table.isInInsertUpdate()) {
				AmiPreparedRowImpl tmp = table.borrowPreparedRow();
				tmp.reset();
				tmp.setComparable(this, value, fs);
				if (!table.canUpdate(tmp, (AmiRowImpl) row, this.mask0, fs))
					return false;
			}
			if (getterSetter.setComparable(column, aRow, value)) {
				if (participatesInIndex)//TODO: add participating in uniqueIndex
					this.table.updateIndexes((AmiRowImpl) row, this.mask0);
				if (isBroadcast)
					aRow.applyValueChangeMask(this.mask0, this.mask64);
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new RuntimeException("Error with setting " + this.table.getName() + "::" + getName() + " to " + value, e);
		}
	}

	public void onDropping(CalcFrameStack fs) {
		this.column.onRemoved();
	}

	@Override
	public boolean getIsOnDisk() {
		return this.isOnDisk;
	}
	public long getOnDiskLong(AmiRow row) {
		ColumnarColumnObject_Cached<?> co = (ColumnarColumnObject_Cached<?>) this.getColumn();
		return co.getInnerLong(((AmiRowImpl) row));
	}
	public boolean setOnDiskLong(AmiRow row, long value) {
		ColumnarColumnObject_Cached<?> co = (ColumnarColumnObject_Cached<?>) this.getColumn();
		return co.setInnerLong(((AmiRowImpl) row), value);
	}
	public boolean onPersistenceRestoreComplete() {
		if (this.isOnDisk) {
			ColumnarColumnObject_Cached<?> co = (ColumnarColumnObject_Cached<?>) this.getColumn();
			return co.onRestoreComplete(table.getState().onStartupOnDiskDefrag());
		}
		return false;
	}
	public void onPersistenceRestoreStarting() {
		if (this.isOnDisk) {
			ColumnarColumnObject_Cached<?> co = (ColumnarColumnObject_Cached<?>) this.getColumn();
			co.onRestoreStarting();
		}
	}

	public boolean getIsBroadcast() {
		return this.isBroadcast;
	}
	public void setOnDiskEmptyValue(AmiPreparedRow row) {
		ColumnarColumnObject_Cached<?> co = (ColumnarColumnObject_Cached<?>) this.getColumn();
		row.setComparable(this, (Comparable) co.getEmptyValue());
	}
	public boolean isStringBitmap() {
		return isStringBitmap;
	}
	public Caster<?> getTypeCaster() {
		return this.column.getTypeCaster();
	}
}
