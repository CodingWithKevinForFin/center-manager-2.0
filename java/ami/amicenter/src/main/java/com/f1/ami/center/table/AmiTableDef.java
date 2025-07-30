package com.f1.ami.center.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.base.Lockable;
import com.f1.base.LockedException;

public class AmiTableDef implements Lockable {

	private static final long DEFAULT_REFRESH_PERIOD = 250;
	private static final int DEFAULT_INITIAL_CAPACITY = 100;
	public static final byte ON_UNDEFINED_COLUMN_IGNORE = 1;
	public static final byte ON_UNDEFINED_COLUMN_REJECT = 2;
	public static final byte ON_UNDEFINED_COLUMN_ADD = 3;

	final private String name;
	final private List<String> columnNames;
	final private List<Byte> columnTypes;

	private long refreshPeriodMs = DEFAULT_REFRESH_PERIOD;
	private int initialCapacity = DEFAULT_INITIAL_CAPACITY;
	private List<AmiColumnWrapper> columns = new ArrayList<AmiColumnWrapper>();

	private boolean isLocked = false;
	private AmiTableImpl table;
	final private byte deftype;
	private boolean neverBroadcast;
	final private byte onUndefinedColumn;
	private List<Map<String, String>> columnOptions;
	private String persisterEngineName;

	public AmiTableDef(byte deftype, String name) {
		this.deftype = deftype;
		this.name = name;
		this.columnNames = new ArrayList<String>();
		this.columnTypes = new ArrayList<Byte>();
		this.columnOptions = new ArrayList<Map<String, String>>();
		this.onUndefinedColumn = ON_UNDEFINED_COLUMN_REJECT;
	}

	public void bindToTable(AmiTableImpl table) {
		LockedException.assertNotLocked(this);
		this.table = table;
		for (int i = 0; i < table.getColumnsCount(); i++)
			this.columns.get(i).setInner(table.getColumnAt(i));
		lock();
	}
	public AmiTableDef(byte defType, String name, List<String> columnNames, List<Byte> columnTypes, List<Map<String, String>> columnOptions, long refreshPeriod,
			int initialCapacity, byte onUndefinedColumn, String persisterEngineName) {
		this.deftype = defType;
		this.name = name;
		this.columnNames = columnNames;
		this.columnTypes = columnTypes;
		this.persisterEngineName = persisterEngineName;
		for (int i = 0; i < columnNames.size(); i++)
			this.columns.add(new AmiColumnWrapper());
		this.refreshPeriodMs = refreshPeriod;
		this.initialCapacity = initialCapacity;
		this.onUndefinedColumn = onUndefinedColumn;
		this.columnOptions = columnOptions;
	}
	public String getName() {
		return name;
	}
	public List<String> getColumnNames() {
		return columnNames;
	}
	public List<Byte> getColumnTypes() {
		return columnTypes;
	}
	public List<Map<String, String>> getColumnOptions() {
		return columnOptions;
	}
	@Override
	public String toString() {
		return "AmiTableDef [name=" + name + ", columnNames=" + columnNames + ", columnTypes=" + columnTypes + "]";
	}
	public long getRefershPeriod() {
		return this.refreshPeriodMs;
	}
	public int getInitialCapacity() {
		return initialCapacity;
	}

	public AmiColumnWrapper addColumn(String name, byte type, Map<String, String> options) {
		LockedException.assertNotLocked(this);
		this.columnNames.add(name);
		this.columnTypes.add(type);
		this.columnOptions.add(options);
		AmiColumnWrapper r = new AmiColumnWrapper();
		this.columns.add(r);
		return r;
	}
	public AmiColumnWrapper addColumn(String name, byte type) {
		return addColumn(name, type, null);
	}
	@Override
	public void lock() {
		this.isLocked = true;
	}
	@Override
	public boolean isLocked() {
		return isLocked;
	}
	public long getRefreshPeriodMs() {
		return refreshPeriodMs;
	}
	public void setRefreshPeriodMs(long refreshPeriodMs) {
		LockedException.assertNotLocked(this);
		this.refreshPeriodMs = refreshPeriodMs;
	}
	public void setInitialCapacity(int initialCapacity) {
		LockedException.assertNotLocked(this);
		this.initialCapacity = initialCapacity;
	}
	public AmiTable getTable() {
		return this.table;
	}

	public byte getDefType() {
		return this.deftype;
	}

	public boolean getIsNeverBroadcast() {
		return this.neverBroadcast;
	}

	public void setNeverBroadcast(boolean neverBroadcast) {
		this.neverBroadcast = neverBroadcast;
	}

	public byte getOnUndefinedColumn() {
		return onUndefinedColumn;
	}

	public String getPersisterEngineName() {
		return this.persisterEngineName;
	}

}
