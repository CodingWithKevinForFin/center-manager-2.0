package com.vortex.eye.processors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.f1.container.RequestMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.utils.DBH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.ToDoException;
import com.f1.utils.db.DbService;
import com.f1.utils.db.ResultSetGetter;
import com.vortex.eye.VortexEyeUtils;

public class VortexEyeDbRequestProcessor extends BasicRequestProcessor<DbRequestMessage, State, DbResultMessage> {

	private DbService dbservice;
	private Map<String, ResultSetGetter<?>> customGetters = new HashMap<String, ResultSetGetter<?>>();

	public VortexEyeDbRequestProcessor() {
		super(DbRequestMessage.class, State.class, DbResultMessage.class);
	}

	public void registerCustomGetter(String type, ResultSetGetter<?> customGetter) {
		assertNotStarted();
		this.customGetters.put(type, customGetter);
	}

	@Override
	public void init() {
		super.init();
		this.dbservice = VortexEyeUtils.getVortexDb(this);
		if (dbservice == null)
			log.info("Dropping all db transactions, no db service found");
	}

	@Override
	protected DbResultMessage processRequest(RequestMessage<DbRequestMessage> action, State state, ThreadScope threadScope) throws Exception {
		DbRequestMessage currentRequest = action.getAction();
		final DbResultMessage result = nw(DbResultMessage.class);
		DbResultMessage currentResult = result;
		Connection connection = null;
		if (dbservice == null) {
			result.setOk(true);
			return result;
		}
		try {
			for (;;) {
				try {
					if (connection == null)
						connection = dbservice.getConnection();
					if (log.isLoggable(Level.FINE))
						LH.log(log, Level.FINE, "Executing db commmand: ", currentRequest.getId());
					switch (currentRequest.getType()) {
						case DbRequestMessage.TYPE_INSERT: {
							if (log.isLoggable(Level.FINE))
								LH.log(log, Level.FINE, "Executing db insert: ", currentRequest.getId());
							dbservice.execute(currentRequest.getId(), currentRequest.getParams(), connection);
							break;
						}
						case DbRequestMessage.TYPE_QUERY_TO_TABLE: {
							if (log.isLoggable(Level.FINE))
								LH.log(log, Level.FINE, "Executing db query to table: ", currentRequest.getId());
							final ResultSet resultSet = dbservice.executeQuery(currentRequest.getId(), currentRequest.getParams(), connection);
							currentResult.setResultsTable(DBH.toTable(resultSet));
							break;
						}
						case DbRequestMessage.TYPE_QUERY_TO_VALUED: {
							if (log.isLoggable(Level.FINE))
								LH.log(log, Level.FINE, "Executing db query to valued: ", currentRequest.getId());
							final ResultSet resultSet = dbservice.executeQuery(currentRequest.getId(), currentRequest.getParams(), connection);
							currentResult.setResultsValued(DBH.toValuedList(resultSet, getGenerator(currentRequest.getResultValuedClass()), customGetters));
							break;
						}
						case DbRequestMessage.TYPE_UPDATE: {
							if (log.isLoggable(Level.FINE))
								LH.log(log, Level.FINE, "Executing db update: ", currentRequest.getId());
							throw new ToDoException("TODO:dbservice.executeUpdate()");
						}
					}
					if (log.isLoggable(Level.FINE))
						LH.log(log, Level.FINE, "Finished Executing db: ", currentRequest.getId());
					currentResult.setOk(true);
				} catch (Exception e) {
					LH.warning(log, "Executing db command failed: ", currentRequest, e);
					currentResult.setOk(false);
					currentResult.setMessage(e.getMessage());
					IOH.close(connection);
					connection = null;
				}
				if (currentRequest.getNextRequest() != null) {
					currentResult.setNextResult(nw(DbResultMessage.class));
					currentRequest = currentRequest.getNextRequest();
					currentResult = currentResult.getNextResult();
				} else
					break;
			}
		} finally {
			IOH.close(connection);
		}
		return result;
	}
}
