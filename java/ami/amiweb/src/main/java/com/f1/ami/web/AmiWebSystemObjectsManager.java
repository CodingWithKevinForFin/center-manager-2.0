package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.base.DateMillis;
import com.f1.base.IterableAndSize;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.OneToOne;
import com.f1.utils.SH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.MapInMap;
import com.f1.utils.structs.MapInMapInMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class AmiWebSystemObjectsManager implements AmiWebRealtimeObjectListener {

	private static final Logger log = LH.get();
	public static final Set<String> INTERESTED_TYPES = CH.s(AmiConsts.TYPE_COMMAND, AmiConsts.TYPE_CONNECTION, AmiConsts.TYPE_DATASOURCE, AmiConsts.TYPE_DATASOURCE_TYPE,
			AmiConsts.TYPE_PROPERTY, AmiConsts.TYPE_RESOURCE, AmiConsts.TYPE_TABLE, AmiConsts.TYPE_COLUMN, AmiConsts.TYPE_INDEX, AmiConsts.TYPE_TRIGGER, AmiConsts.TYPE_TIMER,
			AmiConsts.TYPE_PLUGIN, AmiConsts.TYPE_PROCEDURE, AmiConsts.TYPE_RELAY, AmiConsts.TYPE_STATS, AmiConsts.TYPE_CENTER, AmiConsts.TYPE_REPLICATION,
			AmiConsts.PLUGIN_TYPE_DBO);
	static private final OneToOne<String, String> DATATYPES2TYPES = new OneToOne<String, String>();
	static private final Map<String, String> SYSTEM2NAMECOLUMNS = new HashMap<String, String>();
	static {
		DATATYPES2TYPES.put(AmiConsts.TYPE_TABLE, "TABLE");
		DATATYPES2TYPES.put(AmiConsts.TYPE_TIMER, "TIMER");
		DATATYPES2TYPES.put(AmiConsts.TYPE_PROCEDURE, "PROCEDURE");
		DATATYPES2TYPES.put(AmiConsts.TYPE_TRIGGER, "TRIGGER");

		SYSTEM2NAMECOLUMNS.put(AmiConsts.TYPE_TABLE, "TableName");
		SYSTEM2NAMECOLUMNS.put(AmiConsts.TYPE_TIMER, "TimerName");
		SYSTEM2NAMECOLUMNS.put(AmiConsts.TYPE_PROCEDURE, "ProcedureName");
		SYSTEM2NAMECOLUMNS.put(AmiConsts.TYPE_TRIGGER, "TriggerName");
	}

	private List<AmiWebSystemObjectsListener> listeners = new ArrayList<AmiWebSystemObjectsListener>();
	final private LongKeyMap<AmiWebCommandWrapper> amiCommands = new LongKeyMap<AmiWebCommandWrapper>();
	final private MapInMapInMap<String, String, Long, AmiWebCommandWrapper> amiCommandsByAppNameCmdId = new MapInMapInMap<String, String, Long, AmiWebCommandWrapper>();
	final private LongKeyMap<AmiWebDatasourceWrapper> amiDatasources = new LongKeyMap<AmiWebDatasourceWrapper>();
	final private Map<String, AmiWebDatasourceWrapper> amiDatasourcesByName = new HashMap<String, AmiWebDatasourceWrapper>();
	final private MapInMap<String, String, AmiWebObject> amiSystemObjectsByTypeName = new MapInMap<String, String, AmiWebObject>();
	private Map<String, AmiWebTableSchemaWrapper> tablesByTableName = new HashMap<String, AmiWebTableSchemaWrapper>();

	final private AmiWebService service;
	final private AmiWebManager manager;
	private AmiWebSnapshotManager snapshotManager;

	public AmiWebSystemObjectsManager(AmiWebService service, AmiWebManager manager, AmiWebSnapshotManager snapshotManager) {
		this.service = service;
		this.manager = manager;
		this.snapshotManager = snapshotManager;
	}

	@Override
	public void onAmiEntityAdded(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
		String amiDataType = entity.getTypeName();
		if (AmiConsts.TYPE_COMMAND.equals(amiDataType))
			onCommandAdd(entity);
		else if (AmiConsts.TYPE_DATASOURCE.equals(amiDataType))
			onDatasourcedAdd(entity);
		else if (AmiConsts.TYPE_COLUMN.equals(amiDataType))
			onColumnAdded(entity);
		else if (AmiConsts.TYPE_TABLE.equals(amiDataType))
			onTableAdded(entity);
		else if (AmiConsts.TYPE_STATS.equals(amiDataType))
			onStatAdded(entity);
		if (SYSTEM2NAMECOLUMNS.containsKey(amiDataType))
			onSystemObjectAdded(entity);
	}

	private long latestStatsEventTime = 0;
	private AmiWebObject latestStatsEvent;

	private void onStatAdded(AmiWebObject entity) {
		long time = ((DateMillis) entity.get(AmiConsts.PARAM_STATS_TIME)).getDate();
		if (time > latestStatsEventTime) {
			latestStatsEventTime = time;
			this.latestStatsEvent = entity;
		}
	}

	public AmiWebObject getLatestStatsEvent() {
		return this.latestStatsEvent;
	}

	@Override
	public void onAmiEntityUpdated(AmiWebRealtimeObjectManager manager, AmiWebObjectFields fields, AmiWebObject entity) {
		String amiDataType = entity.getTypeName();
		if (AmiConsts.TYPE_COMMAND.equals(amiDataType)) {
			onCommandRemoved(entity);
			onCommandAdd(entity);
		} else if (AmiConsts.TYPE_DATASOURCE.equals(amiDataType)) {
			AmiWebDatasourceWrapper rem = this.amiDatasources.get(entity.getId());
			onDatasourceRemoved(entity);
			AmiWebDatasourceWrapper nuw = onDatasourcedAdd(entity);
		} else if (AmiConsts.TYPE_COLUMN.equals(amiDataType)) {
			onColumnUpdate(entity);
		} else if (AmiConsts.TYPE_TABLE.equals(amiDataType)) {
			onTableUpdated(entity);
		}

	}

	private void onTableUpdated(AmiWebObject entity) {
	}

	private void onTableRemoved(AmiWebObject entity) {
		String tableName = (String) entity.getParam("TableName");
		AmiWebTableSchemaWrapper existing = this.tablesByTableName.get(tableName);
		if (existing == null)
			return;
		if (existing.getAmiId() != entity.getId())
			return;//This table was already removed, probably from an create -> drop -> create scenario
		this.tablesByTableName.remove(tableName);
		existing.onDelete();
		this.pendingTables.put(tableName, existing);
	}

	public Map<String, AmiWebTableSchemaWrapper> getTables() {
		return this.tablesByTableName;
	}
	private void onTableAdded(AmiWebObject entity) {
		String tableName = (String) entity.getParam("TableName");
		AmiWebTableSchemaWrapper existing = this.tablesByTableName.get(tableName);
		if (existing != null) {
			if (existing.getTableEntity() == null) {
				existing.setTableEntity(entity);
				return;
			} else if (existing.getAmiId() == entity.getId()) {//probably will never get here
				return;
			} else {
				this.tablesByTableName.remove(tableName);
				fireOnTableRemoved(existing);
			}
		}
		AmiWebTableSchemaWrapper table = new AmiWebTableSchemaWrapper(entity, tableName);
		this.tablesByTableName.put(tableName, table);
		this.pendingTables.put(tableName, table);
	}

	@Override
	public void onAmiEntityRemoved(AmiWebRealtimeObjectManager manager, AmiWebObject entity) {
		String amiDataType = entity.getTypeName();
		if (AmiConsts.TYPE_COMMAND.equals(amiDataType))
			onCommandRemoved(entity);
		else if (AmiConsts.TYPE_DATASOURCE.equals(amiDataType))
			onDatasourceRemoved(entity);
		else if (AmiConsts.TYPE_COLUMN.equals(amiDataType))
			onColumnRemoved(entity);
		else if (AmiConsts.TYPE_TABLE.equals(amiDataType))
			onTableRemoved(entity);
		if (SYSTEM2NAMECOLUMNS.containsKey(amiDataType))
			onSystemObjectRemoved(entity);
	}

	private AmiWebDatasourceWrapper onDatasourcedAdd(AmiWebObject entity) {
		AmiWebDatasourceWrapper t = new AmiWebDatasourceWrapper(this.service.getDmManager(), entity);
		this.amiDatasources.put(t.getId(), t);
		this.amiDatasourcesByName.put(t.getName(), t);
		for (AmiWebSystemObjectsListener i : this.listeners)
			i.onDatasourceAdded(t);
		return t;

	}
	private void onDatasourceRemoved(AmiWebObject entity) {
		AmiWebDatasourceWrapper rem = this.amiDatasources.remove(entity.getId());
		if (rem != null) {
			this.amiDatasourcesByName.remove(rem.getName());
			for (AmiWebSystemObjectsListener i : this.listeners)
				i.onDatasourceRemoved(rem);
		}
	}

	private void onCommandAdd(AmiWebObject clientEntity) {
		final AmiWebCommandWrapper cmd = new AmiWebCommandWrapper((AmiWebObject_Feed) clientEntity, manager, this.service);
		this.amiCommands.put(cmd.getId(), cmd);
		this.amiCommandsByAppNameCmdId.putMulti(cmd.getAppName(), cmd.getCmdId(), cmd.getId(), cmd);

		if (cmd.isCallbackNow()) {
			if (this.snapshotManager.getConnectionState() == AmiWebSnapshotManager.STATE_CONNECTED) {
				try {
					AmiWebScriptManagerForLayout sm = service.getScriptManager("");
					if (cmd.matchesFilter(EmptyCalcTypes.INSTANCE, EmptyCalcFrame.INSTANCE, service)
							&& cmd.matchesWhere(EmptyCalcTypes.INSTANCE, EmptyCalcFrame.INSTANCE, service)) {
						AmiWebUtils.showRunCommandDialog(null, this.service, cmd, null, null, null);
					}
				} catch (Exception e) {
					LH.warning(log, "Error processing command", e);
				}
			}
		}
	}
	private void onCommandRemoved(AmiWebObject entity) {
		AmiWebCommandWrapper t = this.amiCommands.remove(entity.getId());
		if (t != null)
			this.amiCommandsByAppNameCmdId.removeMulti(t.getAppName(), t.getCmdId(), t.getId());
	}

	private void onColumnAdded(AmiWebObject entity) {
		String tableName = (String) entity.getParam("TableName");
		String columnName = (String) entity.getParam("ColumnName");
		int position = (Integer) entity.getParam("Position");
		String clazzName = (String) entity.getParam("DataType");
		//		this.columnsByTablenameColumnName.putMulti(tableName, columnName, entity);
		try {
			Class clazzType = this.service.getScriptManager("").forName(clazzName);
			if (clazzType == null)
				throw new NullPointerException();
			AmiWebTableSchemaWrapper table = this.tablesByTableName.get(tableName);
			if (table == null) {
				table = new AmiWebTableSchemaWrapper(null, tableName);
				this.tablesByTableName.put(tableName, table);
			}
			this.pendingTables.put(table.getName(), table);
			table.addColumn(entity.getId(), position, columnName, clazzType);

		} catch (Exception e) {
			LH.warning(log, "Unknown class type for " + tableName + "::" + columnName + " ==> " + clazzName, e);
		}
	}
	private void onColumnRemoved(AmiWebObject entity) {
		String tableName = (String) entity.getParam("TableName");
		String columnName = (String) entity.getParam("ColumnName");
		AmiWebTableSchemaWrapper table = this.tablesByTableName.get(tableName);
		if (table != null) {
			this.pendingTables.put(table.getName(), table);
			table.removeColumn(entity.getId(), columnName);
		}
	}
	private void onColumnUpdate(AmiWebObject entity) {
		String tableName = (String) entity.getParam("TableName");
		String columnName = (String) entity.getParam("ColumnName");
		int position = (Integer) entity.getParam("Position");
		String clazzName = (String) entity.getParam("DataType");
		AmiWebTableSchemaWrapper table = this.tablesByTableName.get(tableName);
		if (table != null) {
			Class clazzType = this.service.getScriptManager("").forName(clazzName);
			if (clazzType == null)
				throw new NullPointerException();
			this.pendingTables.put(table.getName(), table);
			table.addColumn(entity.getId(), position, columnName, clazzType);
		}
	}

	private void fireOnTableAdded(AmiWebTableSchemaWrapper table) {
		for (AmiWebSystemObjectsListener i : this.listeners)
			try {
				i.onTableAdded(table);
			} catch (Exception e) {
				LH.warning(log, e);
			}
	}
	private void fireOnTableRemoved(AmiWebTableSchemaWrapper table) {
		for (AmiWebSystemObjectsListener i : this.listeners)
			try {
				i.onTableRemoved(table);
			} catch (Exception e) {
				LH.warning(log, e);
			}
	}

	private void onSystemObjectAdded(AmiWebObject entity) {
		String colName = SYSTEM2NAMECOLUMNS.get(entity.getTypeName());
		String name = (String) entity.getParam(colName);
		String systemType = getSystemType(entity);
		this.amiSystemObjectsByTypeName.putMulti(systemType, name, entity);
	}
	static private String getSystemType(AmiWebObject entity) {
		return DATATYPES2TYPES.getValue(entity.getTypeName());
	}

	private void onSystemObjectRemoved(AmiWebObject entity) {
		String colName = SYSTEM2NAMECOLUMNS.get(entity.getTypeName());
		String name = (String) entity.getParam(colName);
		String systemType = getSystemType(entity);
		this.amiSystemObjectsByTypeName.removeMulti(systemType, name, entity);
	}

	///////////////////////
	//COMMANDS
	///////////////////////
	/*
	 * Gets the first command registered under the given app or cmdId, not guaranteed to return the correct command wrapper
	 * if there are multiple apps registered with the same appId
	 */
	public AmiWebCommandWrapper getFirstCommandByAppNameCmdId(String appName, String cmdId) {
		Collection<AmiWebCommandWrapper> t = this.amiCommandsByAppNameCmdId.getValues(appName, cmdId);
		return CH.first(t);
	}

	public Collection<AmiWebCommandWrapper> getCommandsByAppNameCmdId(String appName, String cmdId) {
		return this.amiCommandsByAppNameCmdId.getValues(appName, cmdId);
	}

	public AmiWebCommandWrapper getCommandByAppNameCmdIdConnectionId(String appName, String cmdId, int connectionId) {
		Collection<AmiWebCommandWrapper> cmds = this.amiCommandsByAppNameCmdId.getValues(appName, cmdId);
		for (AmiWebCommandWrapper cmd : cmds) {
			if (connectionId == cmd.getRelayConnectionId())
				return cmd;
		}
		return null;
	}

	public AmiWebCommandWrapper getAmiCommand(long id) {
		return amiCommands.get(id);
	}
	public IterableAndSize<AmiWebCommandWrapper> getAmiCommands() {
		return this.amiCommands.values();
	}

	///////////////////////
	//DATASOURCES
	///////////////////////
	public AmiWebDatasourceWrapper getDatasource(long id) {
		return amiDatasources.get(id);
	}
	public IterableAndSize<AmiWebDatasourceWrapper> getDatasources() {
		return this.amiDatasources.values();
	}
	public AmiWebDatasourceWrapper getDatasource(String name) {
		return this.amiDatasourcesByName.get(name);
	}

	///////////////////////
	//SYSTEM OBJECTS
	///////////////////////
	public Iterable<AmiWebObject> getSystemObjects() {
		return this.amiSystemObjectsByTypeName.valuesMulti();
	}

	public Map<String, AmiWebObject> getSystemObjects(String type) {
		return this.amiSystemObjectsByTypeName.get(type);
	}

	public AmiWebObject getSystemObject(String type, String name) {
		return this.amiSystemObjectsByTypeName.getMulti(type, name);
	}

	private Map<String, AmiWebTableSchemaWrapper> pendingTables = new HashMap<String, AmiWebTableSchemaWrapper>();

	@Override
	public void onAmiEntitiesReset(AmiWebRealtimeObjectManager manager) {
	}
	public void clear() {
		this.amiCommands.clear();
		this.amiDatasources.clear();
		this.amiCommandsByAppNameCmdId.clear();
		this.amiDatasourcesByName.clear();
		this.amiSystemObjectsByTypeName.clear();
		this.tablesByTableName.clear();
		this.pendingTables.clear();
		this.latestStatsEvent = null;
		this.latestStatsEventTime = 0;
		this.listeners.clear();
	}

	public void addListener(AmiWebSystemObjectsListener listener) {
		try {
			CH.addIdentityOrThrow(this.listeners, listener);
			if (LH.isFine(log))
				LH.fine(log, "SystemObjects Add Listener: ", SH.toObjectStringSimple(listener), " Count: ", this.listeners.size());
		} catch (Exception e) {
			LH.warning(log, "Add failed for listener: " + SH.toObjectStringSimple(listener), e);
		}
	}
	public void removeListener(AmiWebSystemObjectsListener listener) {
		try {
			CH.removeOrThrow(this.listeners, listener);
			if (LH.isFine(log))
				LH.fine(log, "SystemObjects Remove Listener: ", SH.toObjectStringSimple(listener), " Count: ", this.listeners.size());
		} catch (Exception e) {
			LH.warning(log, "Remove failed for listener: " + SH.toObjectStringSimple(listener), e);
		}
	}
	public com.f1.utils.structs.table.stack.BasicCalcTypes getTableSchema(String tableName) {
		AmiWebTableSchemaWrapper t = this.tablesByTableName.get(tableName);
		return (t == null) ? null : t.getColumns();
	}

	public void onInitDone() {

		for (AmiWebObjects i : this.manager.getAmiObjectsByTypes(INTERESTED_TYPES))
			i.addAmiListener(this);
	}

	public AmiWebService getService() {
		return service;
	}

	public Set<String> getTableNames() {
		return this.tablesByTableName.keySet();
	}

	@Override
	public void onLowerAriChanged(AmiWebRealtimeObjectManager manager, String oldAri, String newAri) {
		throw new UnsupportedOperationException(oldAri + " ==> " + newAri);
	}

	public void onActionsProcessed() {
		if (!this.pendingTables.isEmpty()) {
			for (AmiWebTableSchemaWrapper i : CH.l(this.pendingTables.values())) {
				try {
					if (i.getTableEntity() != null) {
						byte ps = i.getPendingStatus();
						OH.assertNe(ps, -1);
						switch (ps) {
							case AmiWebRealtimeObjectListener.SCHEMA_ADDED:
								fireOnTableAdded(i);
								break;
							case AmiWebRealtimeObjectListener.SCHEMA_DROPPED:
								fireOnTableRemoved(i);
								break;
						}
						Map<String, Tuple2<Class, Class>> columns = i.flushPendingChanges();
						if (columns != null)
							this.manager.onSchemaChanged(i, ps, columns);
					}
				} catch (Exception e) {
					LH.warning(log, "Critical error processing updates for " + i, e);
				}
				this.pendingTables.remove(i.getName());
			}
		}
	}

	@Override
	public void onSchemaChanged(AmiWebRealtimeObjectManager manager, byte status, Map<String, Tuple2<Class, Class>> columns) {
		//don't do anything here, because we are actually the one firing this!
	}

}
