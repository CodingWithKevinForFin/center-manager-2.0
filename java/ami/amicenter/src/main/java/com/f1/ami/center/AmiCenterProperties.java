package com.f1.ami.center;

public interface AmiCenterProperties {

	//General
	String PROPERTY_AMI_CENTER_PORT = "ami.center.port";//TODO: move
	String PROPERTY_AMI_CENTER_PORT_BINDADDR = "ami.center.port.bindaddr";//TODO: move
	String PROPERTY_AMI_CENTER_PORT_WHITELIST = "ami.center.port.whitelist";//TODO: move
	String PROPERTY_AMI_CENTER_SSL_PORT = "ami.center.ssl.port";//TODO: move
	String PROPERTY_AMI_CENTER_SSL_KEYSTORE_FILE = "ami.center.ssl.keystore.file";//TODO:
	String PROPERTY_AMI_CENTER_SSL_KEYSTORE_PASS = "ami.center.ssl.keystore.password";//TODO:
	String PROPERTY_IDFOUNTAIN_PATH = "idfountain.path";
	String PROPERTY_IDFOUNTAIN_BATCHSIZE = "idfountain.batchsize";
	String PROPERTY_AMI_DB_AUTH_PLUGIN_CLASS = "ami.db.auth.plugin.class";
	String PROPERTY_AMI_JDBC_AUTH_PLUGIN_CLASS = "ami.jdbc.auth.plugin.class";

	//Datasources
	String PROPERTY_AMI_DATASOURCES = "ami.datasource.plugins";
	String PROPERTY_AMI_DATSOURCE_CONCURRENT_QUERIES_PER_USER = "ami.datasource.concurrent.queries.per.user";

	//Database
	String PROPERTY_AMI_PRESCHEMA_SCRIPT_FILES = "ami.db.preschema.config.files";
	String PROPERTY_AMI_SCHEMA_SCRIPT_FILES = "ami.db.schema.config.files";
	String PROPERTY_AMI_SCHEMA_MANAGED_FILE = "ami.db.schema.managed.file";
	String PROPERTY_AMI_TABLES_DEFAULT_REFRESH_PERIOD_MS = "ami.db.table.default.refresh.period.millis";
	String PROPERTY_AMI_MAX_STACK_SIZE = "ami.db.max.stack.size";
	String PROPERTY_AMI_DB_PERSIST_DIR = "ami.db.persist.dir";
	String PROPERTY_AMI_DB_PERSIST_DIR_SYSTEM_TABLES = "ami.db.persist.dir.system.tables";
	String PROPERTY_AMI_DB_PERSIST_ENCRYPTER_SYSTEM_TABLES = "ami.db.persist.encrypter.system.tables";
	String PROPERTY_AMI_DB_WRITE_LOCK_PREFERENCE = "ami.db.write.lock.wait.millis";
	String PREFIX_AMI_DB_PERSIST_DIR_SYSTEM_TABLE = "ami.db.persist.dir.system.table.";
	String PREFIX_AMI_DB_PERSIST_ENCRYPTER_SYSTEM_TABLE = "ami.db.persist.encrypter.system.table.";

	//database stuff
	String PROPERTY_AMI_DB_SERVICES = "ami.db.service.plugins";//needs document
	String PROPERTY_AMI_DB_TIMERS = "ami.db.timer.plugins";
	String PROPERTY_AMI_DB_PROCEDURES = "ami.db.procedure.plugins";
	String PROPERTY_AMI_DB_TRIGGERS = "ami.db.trigger.plugins";
	String PROPERTY_AMI_DB_DBOS = "ami.db.dbo.plugins";
	String PROPERTY_AMI_DB_PERSISTERS = "ami.db.persister.plugins";
	String PROPERTY_AMI_DB_CONSOLE_PORT = "ami.db.console.port";
	String PROPERTY_AMI_DB_CONSOLE_PORT_BINDADDR = "ami.db.console.port.bindaddr";
	String PROPERTY_AMI_DB_CONSOLE_PORT_WHITELIST = "ami.db.console.port.whitelist";
	String PROPERTY_AMI_DB_CONSOLE_POMPT = "ami.db.console.prompt";
	String PROPERTY_AMI_CONSOLE_HISTORY_DIR = "ami.db.console.history.dir";
	String PROPERTY_AMI_CONSOLE_HISTORY_MAX_LINES_COUNT = "ami.db.console.history.max.lines";
	String PROPERTY_AMI_DB_JDBC_PORT = "ami.db.jdbc.port";
	String PROPERTY_AMI_DB_JDBC_SSL_PORT = "ami.db.jdbc.ssl.port";
	String PROPERTY_AMI_DB_JDBC_PORT_BINDADDR = "ami.db.jdbc.port.bindaddr";
	String PROPERTY_AMI_DB_JDBC_SSL_PORT_BINDADDR = "ami.db.jdbc.ssl.port.bindaddr";
	String PROPERTY_AMI_DB_JDBC_PROTOCOL_VERSION = "ami.db.jdbc.protocol.version";
	String PROPERTY_AMI_DB_JDBC_PORT_WHITELIST = "ami.db.jdbc.port.whitelist";
	String PROPERTY_AMI_DB_ANONYMOUS_DATASOURCES_ENABLED = "ami.db.anonymous.datasources.enabled";
	String PROPERTY_AMI_DB_JDBC_SSL_KEYSTORE_FILE = "ami.db.jdbc.ssl.keystore.file";
	String PROPERTY_AMI_DB_JDBC_SSL_KEYSTORE_PASS = "ami.db.jdbc.ssl.keystore.password";

	//resources 
	String PROPERTY_AMI_RESOURCES_DIR = "ami.resources.dir";
	String PROPERTY_AMI_RESOURCES_MONITOR_PERIOD_MS = "ami.resources.monitor.period.millis";

	//legacy
	//	String PROPERTY_AMI_OBJECT_INDEXES = "ami.object.indexes";
	//	String PROPERTY_AMI_OBJECT_NOBROADCAST = "ami.object.nobroadcast";
	String PROPERTY_AMI_CENTER_ENABLE_CONCURRENT_QUERIES = "ami.db.enable.concurrent.queries";
	String PROPERTY_AMI_CENTER_DB_SESSION_TIMEOUT = "ami.db.session.timeout";
	String PROPERTY_AMI_CENTER_DB_SESSION_CHECK_PERIOD_SECONDS = "ami.db.session.check.period.seconds";
	String PROPERTY_AMI_UNKNOWN_REALTIME_TABLE_BEHAVIOR = "ami.unknown.realtime.table.behavior";
	String PROPERTY_AMI_TIMER_LOGGING_ENABLED = "ami.db.timer.logging.enabled";
	String PROPERTY_AMI_DB_DIALECT_PLUGINS = "ami.db.dialect.plugins";

	String PROPERTY_AMI_DB_DISABLE_FUNCTIONS = "ami.db.disable.functions";
	String PROPERTY_AMI_DB_DEFAULT_PERMISSIONS = "ami.db.default.permissions";

	String PROPERTY_AMI_CENTER_PUBLISH_CHANGES_PERIOD_MS = "ami.center.publish.changes.period.millis";
	String PROPERTY_AMI_CENTER_LOG_STATS_PERIOD_MS = "ami.center.log.stats.period.millis";
	String PREFIX_AMISCRIPT_VARIABLE = "amiscript.db.variable.";

	String PROPERTY_AMI_DB_ONSTARTUP_ONDISK_DEFRAG = "ami.db.onstartup.ondisk.defrag";

	// custom objects
	String PROPERTY_AMI_CENTER_AMISCRIPT_CUSTOM_CLASSES = "ami.center.amiscript.custom.classes";
	String PROPERTY_AMI_HDB_ROOT_DIR = "ami.hdb.root.dir";
	String PROPERTY_AMI_HDB_BLOCKSIZE = "ami.hdb.blocksize";
	String PROPERTY_AMI_HDB_FILEHANDLES_MAX = "ami.hdb.filehandles.max";
	String PROPERTY_AMI_CENTER_RELAY_BATCH_MESSAGES_MAX = "ami.center.relay.batch.messages.max";
}
