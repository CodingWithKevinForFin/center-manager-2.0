/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.db;

import java.io.File;

import com.f1.container.PartitionResolver;
import com.f1.container.RequestInputPort;
import com.f1.container.impl.BasicSuite;
import com.f1.container.impl.RequestPartitionResolver;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.suite.utils.ParamRoutingRequestProcessor;
import com.f1.utils.IOH;
import com.f1.utils.db.BasicDataSourceManager;
import com.f1.utils.db.DatabaseManager;
import com.f1.utils.db.Database;

public class DbSuite extends BasicSuite {

	public static final String SQL_FILE_SUFFIX = ".sql";
	public final ParamRoutingRequestProcessor<DbRequestMessage, DbResultMessage> router;
	final private PartitionResolver<DbRequestMessage> resolver;
	private DatabaseManager connectionManager = new BasicDataSourceManager();
	final public RequestInputPort<DbRequestMessage, DbResultMessage> inputPort;

	public DbSuite(PartitionResolver<DbRequestMessage> resolver) {
		this.resolver = resolver;
		this.router = addChild(new ParamRoutingRequestProcessor<DbRequestMessage, DbResultMessage>(DbRequestMessage.class, DbResultMessage.class));
		this.router.setPartitionResolver(new RequestPartitionResolver<DbRequestMessage>(resolver));
		this.router.setPid((byte) 1);
		inputPort = exposeInputPort(router);
	}

	public void addSqlFile(String operationId, File sqlFile, String dbSourceId) {
		DbProcessor processor = addChild(new DbProcessor(sqlFile, dbSourceId, resolver));
		processor.setConnectionManager(connectionManager);
		wire(router.addOutputPortForValue(operationId), processor.getInputPort(), false);
	}

	public void addDataSource(String dataSourceId, Database datasource) {
		this.connectionManager.addDataBase(dataSourceId, datasource);
	}

	@Override
	public void start() {
		super.start();
	}

	public void loadFromDirectory(File directory, String dataSourceId) {
		for (File file : directory.listFiles()) {
			String name = file.getName();
			if (!name.endsWith(SQL_FILE_SUFFIX)) {
				getLog().info("Skipping non-sql file: " + IOH.getFullPath(file));
				continue;
			}
			name = name.substring(0, name.length() - SQL_FILE_SUFFIX.length());
			addSqlFile(name, file, dataSourceId);
		}
	}
}
