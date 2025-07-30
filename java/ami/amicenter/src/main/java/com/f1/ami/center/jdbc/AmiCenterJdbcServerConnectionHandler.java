package com.f1.ami.center.jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiJdbcConsts;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.messaging.SimpleMessagingServerConnectionHandler;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.amicommon.msg.AmiCenterUploadTable;
import com.f1.ami.center.AmiCenterUtils;
import com.f1.ami.center.dialects.AmiDbDialect;
import com.f1.ami.center.dialects.AmiDbDialectPlugin;
import com.f1.ami.web.auth.AmiAuthManager;
import com.f1.ami.web.auth.AmiAuthResponse;
import com.f1.ami.web.auth.AmiAuthenticatorPlugin;
import com.f1.ami.web.auth.BasicAmiAuthResponse;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.columnar.ColumnarColumn;
import com.f1.utils.structs.table.columnar.ColumnarRow;
import com.f1.utils.structs.table.columnar.ColumnarTable;

public class AmiCenterJdbcServerConnectionHandler implements SimpleMessagingServerConnectionHandler {

	private static final Logger log = LH.get();

	private AmiCenterJdbcServer server;

	private boolean isLoggedIn = false;
	private String username;
	private String password;
	private boolean keepOpen;
	final private String remoteAddress;

	private long querySessionId;

	private AmiDbDialect dialectPlugin;

	private byte permissions;

	private int clientVersion;
	private int negotiatedVersion;
	public static final int SERVER_VERSION = AmiJdbcConsts.VERSION_2;

	public AmiCenterJdbcServerConnectionHandler(String remoteAddress, AmiCenterJdbcServer amiCenterJdbcServer) {
		this.server = amiCenterJdbcServer;
		this.remoteAddress = remoteAddress;
	}

	@Override
	public Object processRequest(Object cmd) {
		List args = (List) cmd;
		byte instruction = (Byte) args.get(0);
		switch (instruction) {
			case AmiJdbcConsts.INSTRUCTION_LOGIN: {
				String msg = login((Map<String, String>) args.get(1));
				if (args.size() == 2) {
					this.clientVersion = AmiJdbcConsts.VERSION_1;
					this.negotiatedVersion = Math.min(this.clientVersion, SERVER_VERSION);
					return msg;
				} else {
					this.clientVersion = (int) args.get(2);
					this.negotiatedVersion = Math.min(this.clientVersion, SERVER_VERSION);
					return CH.l(msg, SERVER_VERSION);
				}
			}
			case AmiJdbcConsts.INSTRUCTION_QUERY: {
				if (args.size() == 3)
					return executeSql((String) args.get(1), (Long) args.get(2));
				else
					return executeSql((String) args.get(1), AmiCenterQueryDsRequest.USE_DEFAULT_TIMEOUT);
			}
			case AmiJdbcConsts.INSTRUCTION_INSERT: {
				String name = (String) args.get(1);
				List<String> columns = (List<String>) args.get(2);
				Table data = (Table) args.get(3);
				long timeout = (long) args.get(4);
				return executeInsert(name, columns, data, timeout);

			}
			default: {
				this.keepOpen = false;
				throw new RuntimeException("Unknown instruction");
			}
		}

	}

	private Integer timeout = AmiCenterQueryDsRequest.USE_DEFAULT_TIMEOUT;
	private int limit = AmiCenterQueryDsRequest.NO_LIMIT;

	private Map<String, Object> sessionVariables;

	private Map<String, Class> sessionVariableTypes;

	private Object executeSqlLegacy0(String cmd, long timeoutMillis) {
		if (!isLoggedIn) {
			this.keepOpen = false;
			return "007-NOT LOGGED IN";
		}
		AmiCenterQueryDsRequest request = server.getTools().nw(AmiCenterQueryDsRequest.class);
		request.setDatasourceName("AMI");
		if (this.dialectPlugin != null) {
			try {
				cmd = this.dialectPlugin.prepareQuery(cmd);
			} catch (Exception e) {
				LH.warning(log, "Dialect Error from command: ", cmd, e);
				this.keepOpen = false;
				return "008-DIALECT ERROR INTERPRETING REQUEST";
			}
		}
		request.setQuery((String) cmd);
		request.setPermissions(this.permissions);
		request.setType(AmiCenterQueryDsRequest.TYPE_QUERY);
		request.setOriginType(AmiCenterQueryDsRequest.ORIGIN_JDBC);
		request.setLimit(this.limit);//TODO:
		request.setTimeoutMs(AmiUtils.toTimeout(timeoutMillis, this.timeout));
		request.setQuerySessionKeepAlive(true);
		request.setQuerySessionId(this.querySessionId);
		request.setSessionVariableTypes(this.sessionVariableTypes);
		request.setSessionVariables(this.sessionVariables);
		Object res = server.sendToAmiState(username, request);
		List<Object> r = new ArrayList<Object>();
		if (res instanceof Exception) {
			Exception ex = (Exception) res;
			r.add(ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage());
		} else if (res instanceof AmiCenterQueryDsResponse) {
			AmiCenterQueryDsResponse action = (AmiCenterQueryDsResponse) res;
			this.querySessionId = action.getQuerySessionId();
			//Handle inserts and updates better, return number of rows affected
			if (CH.isEmpty(action.getTables())) {
				ColumnarTable t = new ColumnarTable();
				ColumnarColumn<? extends Object> colName = t.addColumn(String.class, "name");
				ColumnarColumn<? extends Object> colValue = t.addColumn(Object.class, "value");
				ColumnarRow row = t.newEmptyRow();
				row.putAt(colName.getLocation(), "returnValue");
				row.putAt(colValue.getLocation(), action.getReturnValue());
				t.getRows().add(row);
				ColumnarRow row2 = t.newEmptyRow();
				row2.putAt(colName.getLocation(), "rowsAffected");
				row2.putAt(colValue.getLocation(), action.getRowsEffected());
				t.getRows().add(row2);
				r.add(t);
			} else
				for (Table i : action.getTables()) {
					if (this.dialectPlugin != null) {
						try {
							i = this.dialectPlugin.prepareResult(i);
						} catch (Exception e) {
							LH.warning(log, "Dialect Error for ", cmd, " on results: ", i, e);
							this.keepOpen = false;
							return "008-DIALECT ERROR INTERPRETING RESULTS";
						}
					}
					r.add(i);
				}
		} else
			r.add(res);
		return r;
	}

	private Object executeSql(String cmd, long timeoutMillis) {
		if (this.server.getJdbcProtocolVersion() == 0)
			return this.executeSqlLegacy0(cmd, timeoutMillis);
		if (!isLoggedIn) {
			this.keepOpen = false;
			return CH.l(AmiJdbcConsts.RET_CODE_ERROR, "007-NOT LOGGED IN");
		}
		AmiCenterQueryDsRequest request = server.getTools().nw(AmiCenterQueryDsRequest.class);
		request.setDatasourceName("AMI");
		if (this.dialectPlugin != null) {
			try {
				cmd = this.dialectPlugin.prepareQuery(cmd);
			} catch (Exception e) {
				LH.warning(log, "Dialect Error from command: ", cmd, e);
				this.keepOpen = false;
				return CH.l(AmiJdbcConsts.RET_CODE_ERROR, "008-DIALECT ERROR INTERPRETING REQUEST");
			}
		}
		request.setQuery((String) cmd);
		request.setPermissions(this.permissions);
		request.setType(AmiCenterQueryDsRequest.TYPE_QUERY);
		request.setOriginType(AmiCenterQueryDsRequest.ORIGIN_JDBC);
		request.setLimit(this.limit);//TODO:
		request.setTimeoutMs(AmiUtils.toTimeout(timeoutMillis, this.timeout));
		request.setQuerySessionKeepAlive(true);
		request.setQuerySessionId(this.querySessionId);
		request.setSessionVariableTypes(this.sessionVariableTypes);
		request.setSessionVariables(this.sessionVariables);
		AmiCenterQueryDsResponse res = server.sendToAmiState(username, request);
		AmiCenterQueryDsResponse action = (AmiCenterQueryDsResponse) res;
		this.querySessionId = action.getQuerySessionId();
		//Handle inserts and updates better, return number of rows affected
		if (!action.getOk()) {
			Exception ex = action.getException();
			if (ex != null) {
				return CH.l(AmiJdbcConsts.RET_CODE_ERROR, "009-EXCEPTION: " + (ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage()));
			} else if (SH.is(action.getMessage()))
				return CH.l(AmiJdbcConsts.RET_CODE_ERROR, action.getMessage());
			else
				return CH.l(AmiJdbcConsts.RET_CODE_ERROR, "010-UNKNOWN_ERROR");
		} else {
			List<Table> r = new ArrayList<Table>();
			for (Table i : action.getTables()) {
				if (this.dialectPlugin != null) {
					try {
						i = this.dialectPlugin.prepareResult(i);
					} catch (Exception e) {
						LH.warning(log, "Dialect Error for ", cmd, " on results: ", i, e);
						this.keepOpen = false;
						return CH.l(AmiJdbcConsts.RET_CODE_ERROR, "008-DIALECT ERROR INTERPRETING RESULTS");
					}
				}
				r.add((Table) i);
			}
			final Object returnValue = AmiUtils.getReturnValue(action);
			final List<Object> autoIds = action.getGeneratedKeys();
			final long rowsEffected = action.getRowsEffected();
			ArrayList<Object> r2 = new ArrayList<Object>(8);
			if (rowsEffected != 0) {
				r2.add(AmiJdbcConsts.RET_CODE_ROWS_EFFECTED);
				r2.add(rowsEffected);
			}
			if (returnValue != null) {
				r2.add(AmiJdbcConsts.RET_CODE_RETURN_VALUE);
				r2.add(returnValue);
			}
			if (r != null) {
				r2.add(AmiJdbcConsts.RET_CODE_TABLES);
				r2.add(r);
			}
			if (autoIds != null) {
				r2.add(AmiJdbcConsts.RET_CODE_GENERATED_KEYS);
				r2.add(autoIds);
			}
			return r2;
		}
	}
	private Object executeInsert(String tableName, List<String> columns, Table data, long timeoutMillis) {
		AmiCenterUploadTable upload = this.server.getTools().nw(AmiCenterUploadTable.class);
		upload.setTargetTable(tableName);
		upload.setTargetColumns(columns);
		upload.setData(data);
		if (!isLoggedIn) {
			this.keepOpen = false;
			return CH.l(AmiJdbcConsts.RET_CODE_ERROR, "007-NOT LOGGED IN");
		}
		AmiCenterQueryDsRequest request = server.getTools().nw(AmiCenterQueryDsRequest.class);
		request.setDatasourceName("AMI");
		request.setUploadValues(CH.l(upload));
		request.setPermissions(this.permissions);
		request.setType(AmiCenterQueryDsRequest.TYPE_UPLOAD);
		request.setOriginType(AmiCenterQueryDsRequest.ORIGIN_JDBC);
		request.setLimit(this.limit);//TODO:
		request.setTimeoutMs(AmiUtils.toTimeout(timeoutMillis, this.timeout));
		request.setQuerySessionKeepAlive(true);
		request.setQuerySessionId(this.querySessionId);
		request.setSessionVariableTypes(this.sessionVariableTypes);
		request.setSessionVariables(this.sessionVariables);
		AmiCenterQueryDsResponse res = server.sendToAmiState(username, request);
		AmiCenterQueryDsResponse action = (AmiCenterQueryDsResponse) res;
		this.querySessionId = action.getQuerySessionId();
		//Handle inserts and updates better, return number of rows affected
		if (!action.getOk()) {
			Exception ex = action.getException();
			if (ex != null) {
				return CH.l(AmiJdbcConsts.RET_CODE_ERROR, "009-EXCEPTION: " + (ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage()));
			} else if (SH.is(action.getMessage()))
				return CH.l(AmiJdbcConsts.RET_CODE_ERROR, action.getMessage());
			else
				return CH.l(AmiJdbcConsts.RET_CODE_ERROR, "010-UNKNOWN_ERROR");
		} else {
			final Object returnValue = AmiUtils.getReturnValue(action);
			final List<Object> autoIds = action.getGeneratedKeys();
			final long rowsEffected = action.getRowsEffected();
			ArrayList<Object> r2 = new ArrayList<Object>(8);
			if (rowsEffected != 0) {
				r2.add(AmiJdbcConsts.RET_CODE_ROWS_EFFECTED);
				r2.add(rowsEffected);
			}
			if (returnValue != null) {
				r2.add(AmiJdbcConsts.RET_CODE_RETURN_VALUE);
				r2.add(returnValue);
			}
			if (autoIds != null) {
				r2.add(AmiJdbcConsts.RET_CODE_GENERATED_KEYS);
				r2.add(autoIds);
			}
			return r2;
		}
	}

	private String login(Map<String, String> map) {
		if (isLoggedIn)
			return "002-USER ALREADY LOGGED IN";
		String username = CH.getOr(map, "username", null);
		String password = CH.getOr(map, "password", null);
		if (username == null) {
			keepOpen = false;
			return "003-REQUIRED ARGUMENT MISSING: username";
		}
		if (password == null) {
			keepOpen = false;
			return "004-REQUIRED ARGUMENT MISSING: password";
		}
		String timeout = CH.getOr(map, "timeout", null);
		String limit = CH.getOr(map, "limit", null);
		Integer timeoutInt = Caster_Integer.INSTANCE.cast(timeout, false, false);
		if (timeoutInt != null)
			this.timeout = timeoutInt;
		Integer limitInt = Caster_Integer.INSTANCE.cast(limit, false, false);
		if (limitInt != null)
			this.limit = limitInt;
		username = SH.decodeUrl(username);
		password = SH.decodeUrl(password);
		AmiAuthenticatorPlugin auth = this.server.getAuthenticator();
		AmiAuthResponse response = auth.authenticate(AmiAuthenticatorPlugin.NAMESPACE_AMIDB_JDBC, remoteAddress, username, password);
		if (response.getStatus() != AmiAuthResponse.STATUS_OKAY) {
			keepOpen = false;
			return "005-AUTHENTICATION FAILED: " + BasicAmiAuthResponse.toStringForStatus(response.getStatus());
		}
		if (!AmiAuthManager.INSTANCE.addUser(this.remoteAddress, username)) {
			return "005-AUTHENTICATION FAILED: " + BasicAmiAuthResponse.toStringForStatus(AmiAuthResponse.STATUS_USER_COUNT_EXCEEDED);
		}
		this.permissions = AmiCenterUtils.getPermissions(this.server.getTools(), response.getUser());
		Map<String, Object> sessionVariables = new HashMap<String, Object>();
		Map<String, Class> sessionVariableTypes = new HashMap<String, Class>();
		for (Entry<String, Object> e : response.getUser().getAuthAttributes().entrySet()) {
			if (e.getKey().startsWith("amiscript.db.variable.")) {
				final String name = SH.stripPrefix(e.getKey(), "amiscript.db.variable.", true);
				Tuple2<Class<?>, Object> val = AmiUtils.toAmiscriptVariable(e.getValue(), "User Attribute amiscript.db.variable.", name);
				sessionVariableTypes.put(name, val.getA());
				sessionVariables.put(name, val.getB());
			}
		}
		this.sessionVariables = sessionVariables;
		this.sessionVariableTypes = sessionVariableTypes;

		String dialect = CH.getOr(map, "dialect", null);
		if (SH.is(dialect)) {
			AmiDbDialectPlugin dp = this.server.getDialects().get(dialect);
			if (dp == null) {
				keepOpen = false;
				return "006-DIALECT NOT FOUND: " + dialect;
			} else
				this.dialectPlugin = dp.createDialectInstance();
		}
		this.isLoggedIn = true;
		this.username = response.getUser().getUserName();
		this.password = password;
		keepOpen = true;
		return "001-USER LOGGED IN";
	}
	@Override
	public boolean keepOpen() {
		return keepOpen;
	}

	@Override
	public void onClosed() {
		if (this.querySessionId > 0) {
			AmiCenterQueryDsRequest request = server.getTools().nw(AmiCenterQueryDsRequest.class);
			request.setDatasourceName("AMI");
			request.setQuerySessionKeepAlive(false);
			request.setQuery(null);
			request.setPermissions(this.permissions);
			request.setType(AmiCenterQueryDsRequest.TYPE_QUERY);
			request.setOriginType(AmiCenterQueryDsRequest.ORIGIN_JDBC);
			request.setQuerySessionId(this.querySessionId);
			server.sendToAmiStateNoResponse(username, request);
		}
	}

}
