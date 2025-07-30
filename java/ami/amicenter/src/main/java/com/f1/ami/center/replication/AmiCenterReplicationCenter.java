package com.f1.ami.center.replication;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessage;
import com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessageImpl;
import com.f1.ami.client.AmiCenterClientConnection;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.LH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.MapInMap;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiCenterReplicationCenter {

	static final private Logger log = LH.get();
	final private LongKeyMap<Row> amiId2SchemaTableAndColumns = new LongKeyMap<Row>();
	final private Set<String> changedTableSchemas = new HashSet<String>();

	private Map<String, AmiCenterReplication> replicationsBySource = new HashMap<String, AmiCenterReplication>();
	private Map<String, Object> tmp = new HashMap<String, Object>();

	private MapInMap<String, String, Row> tname2cname2column = new MapInMap<String, String, Row>();

	final private byte centerId;
	private Table tableSchema;
	private Table columnSchema;
	private Table indexSchema;
	private AmiCenterClientConnection clientConnection;
	private AmiCenterReplicator owner;
	private String centerName;

	public AmiCenterReplicationCenter(AmiCenterReplicator owner, AmiCenterClientConnection c) {
		this.owner = owner;
		this.clientConnection = c;
		this.centerId = this.clientConnection.getCenterDef().getId();
		this.centerName = this.clientConnection.getCenterDef().getName();
		this.tableSchema = new BasicTable(owner.getImdb().getSystemSchema().__TABLE.table.getTable().getColumns());
		this.columnSchema = new BasicTable(owner.getImdb().getSystemSchema().__COLUMN.table.getTable().getColumns());
		this.indexSchema = new BasicTable(owner.getImdb().getSystemSchema().__INDEX.table.getTable().getColumns());
		this.tableSchema.setTitle("TABLES FROM " + centerName);
		this.columnSchema.setTitle("COLUMNS FROM " + centerName);
		this.indexSchema.setTitle("INDEXES FROM " + centerName);
	}

	public AmiCenterReplication getReplication(String sourceTable) {
		return replicationsBySource.get(sourceTable);
	}

	public Collection<AmiCenterReplication> getReplications() {
		return replicationsBySource.values();
	}

	public void putReplication(String sourceTable, AmiCenterReplication replication) {
		this.replicationsBySource.put(sourceTable, replication);
		if (!changedTableSchemas.contains(sourceTable)) {
			Map<String, Row> schema = tname2cname2column.get(sourceTable);
			if (schema != null)
				replication.compile(schema);
		}
	}

	public AmiCenterReplication removeReplication(String sourceTable) {
		return this.replicationsBySource.remove(sourceTable);
	}

	public void onSchema(AmiCenterClientObjectMessageImpl msg) {

		String typeName = msg.getTypeName();
		if (AmiConsts.TYPE_COLUMN.equals(typeName)) {
			Row row = handleRow(msg, columnSchema);
			if (row != null) {
				String tname = row.get("TableName", String.class);
				String cname = row.get("ColumnName", String.class);
				this.changedTableSchemas.add(tname);
				if (msg.getAction() == AmiCenterClientObjectMessage.ACTION_DEL) {
					this.tname2cname2column.removeMulti(tname, cname, row);
				} else if (msg.getAction() == AmiCenterClientObjectMessage.ACTION_ADD) {
					this.tname2cname2column.putMulti(tname, cname, row);
				}
			}
		} else if (AmiConsts.TYPE_TABLE.equals(typeName)) {
			handleRow(msg, tableSchema);
		} else if (AmiConsts.TYPE_INDEX.equals(typeName)) {
			handleRow(msg, indexSchema);
		}
	}

	private Row handleRow(AmiCenterClientObjectMessageImpl msg, Table sink) {
		if (msg.getAction() == AmiCenterClientObjectMessage.ACTION_ADD) {
			Object values[] = new Object[sink.getColumnsCount()];
			Map<String, Column> cols = sink.getColumnsMap();
			for (int i = 0; i < msg.getParamsCount(); i++) {
				Column col = cols.get(msg.getParamName(i));
				if (col != null)
					values[col.getLocation()] = col.getTypeCaster().cast(msg.getParamValue(i));
			}
			Row row = sink.getRows().addRow(values);
			amiId2SchemaTableAndColumns.put(msg.getId(), row);
			return row;
		} else if (msg.getAction() == AmiCenterClientObjectMessage.ACTION_UPD) {
			Row row = amiId2SchemaTableAndColumns.get(msg.getId());
			if (row != null) {
				Map<String, Column> cols = sink.getColumnsMap();
				for (int i = 0; i < msg.getParamsCount(); i++) {
					Column col = cols.get(msg.getParamName(i));
					if (col != null)
						row.putAt(col.getLocation(), col.getTypeCaster().cast(msg.getParamValue(i)));
				}
			}
			return row;
		} else if (msg.getAction() == AmiCenterClientObjectMessage.ACTION_DEL) {
			Row row = amiId2SchemaTableAndColumns.remove(msg.getId());
			if (row != null) {
				row.getTable().removeRow(row);
			}
			return row;
		}
		return null;
	}

	public void onSchemaChanged(CalcFrameStack sf) {
		for (String schema : this.changedTableSchemas) {
			AmiCenterReplication replication = this.replicationsBySource.get(schema);
			if (replication != null) {
				replication.onSchemaChanged(false, sf);
			}
		}
		this.changedTableSchemas.clear();
	}

	public void onDisconnect() {
		this.amiId2SchemaTableAndColumns.clear();
		this.indexSchema.clear();
		this.columnSchema.clear();
		this.tableSchema.clear();
		for (AmiCenterReplication replication : this.getReplications())
			replication.onDisconnect();
	}

	public AmiCenterClientConnection getClientConnection() {
		return this.clientConnection;
	}

	public Map<String, Row> getSchema(String sourceTable) {
		return this.tname2cname2column.get(sourceTable);
	}

	public Table getTableSchema() {
		return this.tableSchema;
	}
	public Table getColumnSchema() {
		return this.columnSchema;
	}
	public Table getIndexSchema() {
		return this.indexSchema;
	}
	public String getCenterName() {
		return this.centerName;
	}
	public byte getCenterId() {
		return this.centerId;
	}

}
