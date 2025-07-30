/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.db;

import java.io.File;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.PartitionResolver;
import com.f1.container.RequestMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.exceptions.ProcessorException;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.utils.ToDoException;
import com.f1.utils.db.DatabaseManager;
import com.f1.utils.db.FileDbStatement;

public class DbProcessor extends BasicRequestProcessor<DbRequestMessage, State, DbResultMessage> {

	private FileDbStatement dbFileStatement;
	private DatabaseManager connectionManager;
	private ObjectGeneratorForClass<DbResultMessage> dbResultMessageGenerator;
	private File file;
	private String dbSourceId;

	public DbProcessor(File sqlFile, String datasourceId, PartitionResolver<DbRequestMessage> resolver) {
		super(DbRequestMessage.class, State.class, DbResultMessage.class, resolver);
		this.dbSourceId = datasourceId;
		this.file = sqlFile;
	}

	@Override
	protected DbResultMessage processRequest(RequestMessage<DbRequestMessage> action, State state, ThreadScope threadScope) {
		try {
			throw new ToDoException();
		} catch (Exception e) {
			throw new ProcessorException(e);
		}
	}

	@Override
	public void start() {
		super.start();
		dbResultMessageGenerator = getGenerator(DbResultMessage.class);
		if (dbSourceId == null)
			throw new NullPointerException("connectionManager");
		if (connectionManager == null)
			throw new NullPointerException("connectionManager");
		try {
			dbFileStatement = new FileDbStatement(file, getServices().getGenerator());
		} catch (Exception e) {
			throw new ProcessorException(e);
		}
	}

	public void setConnectionManager(DatabaseManager connectionManager) {
		assertNotStarted();
		this.connectionManager = connectionManager;
	}

	public DatabaseManager getConnectionManager() {
		return connectionManager;
	}

	public void setDbSourceId(String dbSourceId) {
		assertNotStarted();
		this.dbSourceId = dbSourceId;
	}

	public String getDbSourceId() {
		return dbSourceId;
	}

	public void setFile(File file) {
		assertNotStarted();
		this.file = file;
	}

	public File getFile() {
		return file;
	}
}
