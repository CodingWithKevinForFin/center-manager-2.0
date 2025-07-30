
package com.f1.ami.web;

import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.structs.Tuple2;

public class AmiWebTableSchemaWrapper {

	private String name;

	private HasherMap<String, Long> amiColumnIds = new HasherMap<String, Long>();
	private com.f1.utils.structs.table.stack.BasicCalcTypes columns = new com.f1.utils.structs.table.stack.BasicCalcTypes();
	private HasherMap<String, Integer> columnPositions = new HasherMap<String, Integer>();
	private AmiWebObject tableEntity;
	private Map<String, Tuple2<Class, Class>> pendingChanges;

	private byte pendingStatus = -1;

	public AmiWebTableSchemaWrapper(AmiWebObject tableEntity, String name) {
		this.name = name;
		this.setTableEntity(tableEntity);
		this.pendingStatus = AmiWebRealtimeObjectListener.SCHEMA_ADDED;
	}

	public void addColumn(long amiId, int position, String columnName, Class clazzType) {
		amiColumnIds.put(columnName, amiId);
		Class<?> old = this.columns.putType(columnName, clazzType);
		this.columnPositions.put(columnName, position);
		if (this.pendingChanges == null)
			this.pendingChanges = new LinkedHashMap<String, Tuple2<Class, Class>>();
		this.pendingChanges.put(columnName, new Tuple2<Class, Class>(old, clazzType));
		if (pendingStatus == -1)
			pendingStatus = AmiWebRealtimeObjectListener.SCHEMA_MODIFIED;
	}

	public long getAmiId() {
		return this.getTableEntity().getId();
	}

	public void removeColumn(long amiId, String columnName) {
		Long existing = this.amiColumnIds.get(columnName);
		if (existing != null && existing.longValue() != amiId)
			return;
		this.amiColumnIds.remove(columnName);
		Class<?> type = this.columns.removeType(columnName);
		OH.assertNotNull(type);
		this.columnPositions.remove(columnName);
		if (this.pendingChanges == null)
			this.pendingChanges = new LinkedHashMap<String, Tuple2<Class, Class>>();
		this.pendingChanges.put(columnName, new Tuple2<Class, Class>(type, null));
		if (pendingStatus == -1)
			pendingStatus = AmiWebRealtimeObjectListener.SCHEMA_MODIFIED;
	}

	public com.f1.utils.structs.table.stack.BasicCalcTypes getColumns() {
		return this.columns;
	}

	public String getName() {
		return this.name;
	}

	public boolean isBroadcast() {
		return Boolean.TRUE.equals(this.getTableEntity().get("Broadcast"));
	}

	public byte getPendingStatus() {
		return this.pendingStatus;
	}

	public Integer getColumnPostion(String columnName) {
		return this.columnPositions.get(columnName);
	}

	public AmiWebObject getTableEntity() {
		return tableEntity;
	}

	public void setTableEntity(AmiWebObject tableEntity) {
		this.tableEntity = tableEntity;
	}

	public Map<String, Tuple2<Class, Class>> flushPendingChanges() {
		Map<String, Tuple2<Class, Class>> r = pendingChanges;
		this.pendingStatus = -1;
		this.pendingChanges = null;
		return r;
	}

	public void onDelete() {
		this.pendingChanges = new LinkedHashMap<String, Tuple2<Class, Class>>();
		for (String i : this.columns.getVarKeys())
			this.pendingChanges.put(i, new Tuple2<Class, Class>(this.columns.getType(i), null));
		this.pendingStatus = AmiWebRealtimeObjectListener.SCHEMA_DROPPED;
	}

}
