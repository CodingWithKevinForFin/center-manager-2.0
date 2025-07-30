package com.vortex.eye;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.f1.base.ObjectGenerator;
import com.f1.base.Valued;
import com.f1.base.ValuedSchema;
import com.f1.container.ContainerServices;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.CharReader;
import com.f1.utils.DBH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.db.DbService;
import com.f1.utils.db.ResultSetGetter;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.structs.LongKeyMap;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;
import com.f1.vortexcommon.msg.agent.VortexAgentCron;
import com.f1.vortexcommon.msg.agent.VortexAgentDbColumn;
import com.f1.vortexcommon.msg.agent.VortexAgentDbDatabase;
import com.f1.vortexcommon.msg.agent.VortexAgentDbObject;
import com.f1.vortexcommon.msg.agent.VortexAgentDbPrivilege;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;
import com.f1.vortexcommon.msg.agent.VortexAgentDbTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentFileSystem;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.agent.VortexAgentNetAddress;
import com.f1.vortexcommon.msg.agent.VortexAgentNetConnection;
import com.f1.vortexcommon.msg.agent.VortexAgentNetLink;
import com.f1.vortexcommon.msg.agent.VortexAgentProcess;
import com.f1.vortexcommon.msg.eye.VortexBuildProcedure;
import com.f1.vortexcommon.msg.eye.VortexBuildResult;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexDeploymentSet;
import com.f1.vortexcommon.msg.eye.VortexExpectation;
import com.f1.vortexcommon.msg.eye.VortexEyeAuditTrailRule;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.f1.vortexcommon.msg.eye.VortexEyeBackupDestination;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.VortexEyeCloudInterface;
import com.f1.vortexcommon.msg.eye.VortexEyeMetadataField;
import com.f1.vortexcommon.msg.eye.VortexEyeScheduledTask;
import com.f1.vortexcommon.msg.eye.VortexVaultEntry;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryHistoryRequest;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeDbService extends DbService {

	private static final Logger log = LH.get(DbService.class);
	public static final int MAX_QUERY_BLOB_RESULT_LENGTH = 2048;

	private ValuedSchema<Valued> agentRequestSchema;
	private ValuedSchema<Valued> agentResponseSchema;
	final private ContainerTools tools;
	final private HashMap<String, ResultSetGetter<?>> customConverters;

	public VortexEyeDbService(DataSource datasource, ObjectGenerator generator, ContainerTools tools) throws IOException {
		super(datasource, generator);
		this.tools = tools;
		File sqlDir = getServices().getPropertyController().getOptional(VortexEyeMain.OPTION_SQL_DIR, new File("./src/main/scripts/sql"));
		ObjectToJsonConverter converter = getServices().getJsonConverter();
		this.customConverters = new HashMap<String, ResultSetGetter<?>>();
		customConverters.put("JSON", new ResultSetGetter.JsonResultSetGetter(getServices().getJsonConverter()));
		customConverters.put("RULES", new RuleParser(converter));
		customConverters.put("BYTEMAP", new ByteMapParser(converter));
		customConverters.put("STRINGMAP", new StringMapParser());
		customConverters.put("PARAMSMAP", new ParamsMapParser());
		super.add(sqlDir, ".sql");
	}

	public ContainerTools getTools() {
		return tools;
	}
	private ContainerServices getServices() {
		return getTools().getServices();
	}

	public LongKeyMap<VortexAgentMachine> queryMachineInstance(Connection connection) throws SQLException, Exception {
		final Map<Object, Object> params = new HashMap<Object, Object>();
		//params.put("machine_uid", machineUid);
		ResultSet result = executeQuery("query_machine_instance", params, connection);
		return mapById(DBH.toValuedList(result, getTools().getServices().getGenerator(VortexAgentMachine.class), customConverters));
	}

	public List<VortexAgentProcess> queryProcesses(Connection connection) throws Exception {
		final Map<Object, Object> params = new HashMap<Object, Object>();
		ResultSet result = executeQuery("query_process_instance_no_stats", params, connection);
		return DBH.toValuedList(result, getServices().getGenerator(VortexAgentProcess.class));
	}

	public List<VortexAgentNetConnection> queryNetConnections(Connection connection) throws Exception {
		ResultSet result = executeQuery("query_net_connection_instance", Collections.emptyMap(), connection);
		return DBH.toValuedList(result, getServices().getGenerator(VortexAgentNetConnection.class));
	}

	public List<VortexAgentFileSystem> queryFileSystems(Connection connection) throws Exception {
		ResultSet result = executeQuery("query_file_system_instance_no_stats", Collections.emptyMap(), connection);
		return DBH.toValuedList(result, getServices().getGenerator(VortexAgentFileSystem.class));
	}
	public List<VortexEyeMetadataField> queryMetadataFields(Connection connection) throws Exception {
		ResultSet result = executeQuery("query_metadata_fields", Collections.emptyMap(), connection);
		return DBH.toValuedList(result, getServices().getGenerator(VortexEyeMetadataField.class), customConverters);
	}

	public List<VortexAgentNetLink> queryNetLinks(Connection connection) throws Exception {
		ResultSet result = executeQuery("query_net_link_instance_no_stats", Collections.emptyMap(), connection);
		return DBH.toValuedList(result, getServices().getGenerator(VortexAgentNetLink.class));
	}

	public List<VortexAgentNetAddress> queryNetAddresses(Connection connection) throws Exception {
		ResultSet result = executeQuery("query_net_address_instance", Collections.emptyMap(), connection);
		return DBH.toValuedList(result, getServices().getGenerator(VortexAgentNetAddress.class));
	}

	public List<VortexAgentCron> queryCrontab(Connection connection) throws Exception {
		ResultSet result = executeQuery("query_job_schedules_instance", Collections.emptyMap(), connection);
		List<VortexAgentCron> list = new ArrayList<VortexAgentCron>();
		list = DBH.toValuedList(result, getServices().getGenerator(VortexAgentCron.class));
		return list;
	}

	public List<VortexAgentEntity> queryProcessHistory(byte type, List<String> pids, long limit) throws SQLException, Exception {
		Class c;
		String query;
		switch (type) {
			case VortexEyeQueryHistoryRequest.TYPE_PROCESS_CONNECTION:
				c = VortexAgentNetConnection.class;
				query = "query_process_net_connection_history";
				break;
			default:
				throw new IllegalStateException("Invalid History Request Type: " + type);
		}
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("pids", pids);
		params.put("lim", limit);
		ResultSet result = executeQuery(query, params, getConnection());
		return DBH.toValuedList(result, getServices().getGenerator(c));
	}

	public Map<Long, VortexEyeAuditTrailRule> queryAuditTrailRules(Connection connection) throws Exception {
		final Map<Object, Object> params = new HashMap<Object, Object>();
		ResultSet data = executeQuery("query_audit_trail_rules", params, connection);
		Map<Long, VortexEyeAuditTrailRule> r = new HashMap<Long, VortexEyeAuditTrailRule>();
		ObjectToJsonConverter converter = getServices().getJsonConverter();
		for (VortexEyeAuditTrailRule db : DBH.toValuedList(data, getServices().getGenerator(VortexEyeAuditTrailRule.class), customConverters))
			r.put(db.getId(), db);
		return r;
	}

	public static class RuleParser extends ResultSetGetter.JsonResultSetGetter {

		public RuleParser(ObjectToJsonConverter converter) {
			super(converter);
		}
		@Override
		protected Object getInner(ResultSet rs, int field) throws SQLException {
			Map<?, ?> m = (Map<?, ?>) super.getInner(rs, field);
			HashMap<Short, String> r = new HashMap<Short, String>(m.size());
			for (Entry<?, ?> e : m.entrySet())
				r.put(Short.parseShort(e.getKey().toString()), (String) e.getValue());
			return r;
		}
	}

	public Map<Long, VortexExpectation> queryExpectations(Connection connection) throws Exception {
		final Map<Object, Object> params = new HashMap<Object, Object>();
		ResultSet data = executeQuery("query_expectations", params, connection);
		Map<Long, VortexExpectation> r = new HashMap<Long, VortexExpectation>();
		for (VortexExpectation row : DBH.toValuedList(data, getServices().getGenerator(VortexExpectation.class), customConverters))
			r.put(row.getId(), row);
		return r;
	}
	public Map<Long, VortexBuildProcedure> queryBuildProcedures(Connection connection) throws Exception {
		final Map<Object, Object> params = new HashMap<Object, Object>();
		ResultSet data = executeQuery("query_build_procedures", params, connection);
		Map<Long, VortexBuildProcedure> r = new HashMap<Long, VortexBuildProcedure>();
		for (VortexBuildProcedure row : DBH.toValuedList(data, getServices().getGenerator(VortexBuildProcedure.class), customConverters))
			r.put(row.getId(), row);
		return r;
	}
	public Map<Long, VortexBuildResult> queryBuildResults(Connection connection) throws Exception {
		final Map<Object, Object> params = new HashMap<Object, Object>();
		//params.put("max_length", MAX_QUERY_BLOB_RESULT_LENGTH);
		ResultSet data = executeQuery("query_build_results", params, connection);
		Map<Long, VortexBuildResult> r = new HashMap<Long, VortexBuildResult>();
		for (VortexBuildResult row : DBH.toValuedList(data, getServices().getGenerator(VortexBuildResult.class), customConverters))
			r.put(row.getId(), row);
		return r;
	}
	public Map<Long, VortexDeployment> queryDeployments(Connection connection) throws Exception {
		final Map<Object, Object> params = new HashMap<Object, Object>();
		ResultSet data = executeQuery("query_deployments", params, connection);
		Map<Long, VortexDeployment> r = new HashMap<Long, VortexDeployment>();
		for (VortexDeployment row : DBH.toValuedList(data, getServices().getGenerator(VortexDeployment.class), customConverters))
			r.put(row.getId(), row);
		return r;
	}
	public Map<Long, VortexDeploymentSet> queryDeploymentSets(Connection connection) throws Exception {
		final Map<Object, Object> params = new HashMap<Object, Object>();
		ResultSet data = executeQuery("query_deployment_sets", params, connection);
		Map<Long, VortexDeploymentSet> r = new HashMap<Long, VortexDeploymentSet>();
		for (VortexDeploymentSet row : DBH.toValuedList(data, getServices().getGenerator(VortexDeploymentSet.class), customConverters))
			r.put(row.getId(), row);
		return r;
	}
	public Map<Long, VortexEyeScheduledTask> queryScheduledTasks(Connection connection) throws Exception {
		final Map<Object, Object> params = new HashMap<Object, Object>();
		ResultSet data = executeQuery("query_scheduled_tasks", params, connection);
		Map<Long, VortexEyeScheduledTask> r = new HashMap<Long, VortexEyeScheduledTask>();
		for (VortexEyeScheduledTask row : DBH.toValuedList(data, getServices().getGenerator(VortexEyeScheduledTask.class), customConverters))
			r.put(row.getId(), row);
		return r;
	}
	public List<VortexEyeClientEvent> queryClientEvents(Connection connection) throws Exception {
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("LIMIT", VortexEyeState.MAX_CLIENT_EVENTS);
		ResultSet data = executeQuery("query_client_events", params, connection);
		Map<Long, VortexEyeScheduledTask> r = new HashMap<Long, VortexEyeScheduledTask>();
		return DBH.toValuedList(data, getServices().getGenerator(VortexEyeClientEvent.class), customConverters);
	}

	public static class ByteMapParser extends ResultSetGetter.JsonResultSetGetter {

		public ByteMapParser(ObjectToJsonConverter converter) {
			super(converter);
		}
		@Override
		protected Object getInner(ResultSet rs, int field) throws SQLException {
			Map<?, ?> m = (Map<?, ?>) super.getInner(rs, field);
			if (m == null)
				return null;
			HashMap<Byte, String> r = new HashMap<Byte, String>(m.size());
			for (Entry<?, ?> e : m.entrySet())
				r.put(Byte.parseByte(e.getKey().toString()), (String) e.getValue());
			return r;
		}
	}

	public static class StringMapParser extends ResultSetGetter<Map<String, String>> {

		public StringMapParser() {
		}
		@Override
		protected Map<String, String> getInner(ResultSet rs, int field) throws SQLException {
			final String text = rs.getString(field);
			if (text == null)
				return null;
			try {
				return SH.splitToMap('|', '=', '\\', text);
			} catch (Exception e) {
				LH.warning(log, "error processing map text: ", text, e);
				return null;
			}

		}
		@Override
		public void set(PreparedStatement ps, int parameterIndex, Map<String, String> value) throws SQLException {
			ps.setObject(parameterIndex, value);
		}

		@Override
		public Class<Map<String, String>> getReturnType() {
			return (Class) Map.class;
		}
	}

	public static class ParamsMapParser extends ResultSetGetter<Map<String, Object>> {

		public ParamsMapParser() {
		}
		@Override
		protected Map<String, Object> getInner(ResultSet rs, int field) throws SQLException {
			final String text = rs.getString(field);
			if (text == null)
				return null;
			try {
				if (text.length() == 0)
					return new HashMap<String, Object>(0);
				final Map<String, Object> m = new HashMap<String, Object>();
				StringCharReader scr = new StringCharReader(text);
				StringBuilder sink = new StringBuilder();
				for (;;) {
					scr.readUntil('=', SH.clear(sink));
					final String key = SH.toStringAndClear(sink);
					scr.expect('=');
					final Object value;
					switch (scr.peakOrEof()) {
						case CharReader.EOF:
							value = null;
							break;
						case 't':
							scr.expectSequence("true");
							value = Boolean.TRUE;
							break;
						case 'f':
							scr.expectSequence("false");
							value = Boolean.FALSE;
							break;
						case 'n':
							scr.expectSequence("null");
							value = null;
							break;
						case '"':
							scr.expect('"');
							scr.readUntilSkipEscaped('"', '\\', sink);
							scr.expect('"');
							value = SH.toStringAndClear(sink);
							break;
						default:
							scr.readUntil('|', sink);
							int len = sink.length();
							if (len == 0)
								value = null;
							else {
								char last = sink.charAt(len - 1);
								switch (last) {
									case 'D':
									case 'd':
										sink.setLength(len - 1);
										value = SH.parseDouble(sink);
										break;
									case 'F':
									case 'f':
										sink.setLength(len - 1);
										value = SH.parseFloat(sink);
										break;
									case 'L':
										value = SH.parseLong(sink, 0, sink.length() - 1, 10);
										break;
									default:
										if (sink.indexOf(".") == -1)
											value = SH.parseInt(sink);
										else
											value = SH.parseFloat(sink);
								}
							}

					}
					m.put(key, value);
					int t = scr.peakOrEof();
					if (t == CharReader.EOF)
						break;
					scr.expect('|');
				}
				return m;
			} catch (Exception e) {
				LH.warning(log, "error processing params map text: ", text, e);
				return null;
			}

		}
		@Override
		public void set(PreparedStatement ps, int parameterIndex, Map<String, Object> value) throws SQLException {
			ps.setObject(parameterIndex, value);
		}
		@Override
		public Class<Map<String, Object>> getReturnType() {
			return (Class) Map.class;
		}
	}

	public LongKeyMap<VortexAgentDbDatabase> queryDatabases(Connection connection) throws Exception {
		ResultSet data = executeQuery("query_db_database", Collections.emptyMap(), connection);
		return mapById(DBH.toValuedList(data, getServices().getGenerator(VortexAgentDbDatabase.class)));
	}

	public LongKeyMap<VortexAgentDbServer> queryDbServers(Connection connection) throws Exception {
		ResultSet data = executeQuery("query_db_servers", Collections.emptyMap(), connection);
		return mapById(DBH.toValuedList(data, getServices().getGenerator(VortexAgentDbServer.class), customConverters));
	}
	public LongKeyMap<VortexAgentDbPrivilege> queryPrivileges(Connection connection) throws Exception {
		ResultSet data = executeQuery("query_db_privilege", Collections.emptyMap(), connection);
		return mapById(DBH.toValuedList(data, getServices().getGenerator(VortexAgentDbPrivilege.class)));
	}

	public LongKeyMap<VortexAgentDbObject> queryDbObjects(Connection connection) throws Exception {
		ResultSet data = executeQuery("query_db_object", Collections.emptyMap(), connection);
		return mapById(DBH.toValuedList(data, getServices().getGenerator(VortexAgentDbObject.class)));
	}

	public LongKeyMap<VortexAgentDbTable> queryTables(Connection connection) throws Exception {
		ResultSet data = executeQuery("query_db_table", Collections.emptyMap(), connection);
		return mapById(DBH.toValuedList(data, getServices().getGenerator(VortexAgentDbTable.class)));
	}
	public LongKeyMap<VortexAgentDbColumn> queryColumns(Connection connection) throws Exception {
		ResultSet data = executeQuery("query_db_column", Collections.emptyMap(), connection);
		return mapById(DBH.toValuedList(data, getServices().getGenerator(VortexAgentDbColumn.class)));
	}

	static private <T extends VortexEntity> LongKeyMap<T> mapById(List<T> values) {
		LongKeyMap<T> r = new LongKeyMap<T>();
		for (T value : values)
			r.put(value.getId(), value);
		return r;
	}

	public List<VortexEyeBackupDestination> queryBackupDestinations(Connection connection) throws Exception {
		ResultSet data = executeQuery("query_backup_destinations", Collections.emptyMap(), connection);
		return DBH.toValuedList(data, getServices().getGenerator(VortexEyeBackupDestination.class), customConverters);
	}

	public List<VortexEyeBackup> queryBackups(Connection connection) throws Exception {
		ResultSet data = executeQuery("query_backups", Collections.emptyMap(), connection);
		return DBH.toValuedList(data, getServices().getGenerator(VortexEyeBackup.class), customConverters);
	}

	public List<VortexEyeCloudInterface> queryCloudInterfaces(Connection connection) throws Exception {
		ResultSet data = executeQuery("query_cloud_interfaces", Collections.emptyMap(), connection);
		return DBH.toValuedList(data, getServices().getGenerator(VortexEyeCloudInterface.class), customConverters);
	}

	public List<VortexAgentBackupFile> queryBackupFiles(Connection connection) throws Exception {
		ResultSet data = executeQuery("query_backup_files", Collections.emptyMap(), connection);
		return DBH.toValuedList(data, getServices().getGenerator(VortexAgentBackupFile.class), customConverters);
	}

	public List<VortexVaultEntry> queryVortexVaultData(Connection conn, long id) throws Exception {
		ResultSet data = executeQuery("query_vortex_vault_data", CH.m("id", id), conn);
		return DBH.toValuedList(data, getServices().getGenerator(VortexVaultEntry.class), customConverters);
	}

	public void insertVortexVaultEntry(Connection conn, VortexVaultEntry entry) throws Exception {
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", true);
		params.put("now", entry.getNow());
		params.put("id", entry.getId());
		params.put("data", entry.getData());
		params.put("data_length", entry.getDataLength());
		params.put("softlink_vvid", entry.getSoftlinkVvid());
		params.put("checksum", entry.getChecksum());
		execute("insert_vortex_vault", params, conn);
	}

	public Iterable<VortexVaultEntry> queryVortexVault(Connection conn) throws Exception {
		ResultSet data = executeQuery("query_vortex_vault", Collections.emptyMap(), conn);
		return DBH.toValuedList(data, getServices().getGenerator(VortexVaultEntry.class), customConverters);
	}

}
