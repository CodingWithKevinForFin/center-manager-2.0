package com.f1.ami.center;

import static com.f1.ami.center.AmiCenterProperties.PROPERTY_AMI_DATASOURCES;
import static com.f1.ami.center.AmiCenterProperties.PROPERTY_AMI_DB_DBOS;
import static com.f1.ami.center.AmiCenterProperties.PROPERTY_AMI_DB_PERSISTERS;
import static com.f1.ami.center.AmiCenterProperties.PROPERTY_AMI_DB_PROCEDURES;
import static com.f1.ami.center.AmiCenterProperties.PROPERTY_AMI_DB_SERVICES;
import static com.f1.ami.center.AmiCenterProperties.PROPERTY_AMI_DB_TIMERS;
import static com.f1.ami.center.AmiCenterProperties.PROPERTY_AMI_DB_TRIGGERS;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiDatasourcePlugin;
import com.f1.ami.amicommon.AmiEncrypter;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.ds.AmiDatasourceAdapterManager;
import com.f1.ami.center.dbo.AmiDboFactory;
import com.f1.ami.center.procs.AmiStoredProcFactory;
import com.f1.ami.center.procs.AmiStoredProcFactory_AmiScript;
import com.f1.ami.center.sysschema.AmiSchema;
import com.f1.ami.center.sysschema.AmiSchema_DATASOURCE;
import com.f1.ami.center.table.AmiCenterProcess;
import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiImdbFactoriesManager;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiPreparedRowImpl;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.ami.center.table.persist.AmiTablePersisterFactory;
import com.f1.ami.center.table.persist.AmiTablePersisterFactory_Fast;
import com.f1.ami.center.table.persist.AmiTablePersisterFactory_Text;
import com.f1.ami.center.timers.AmiTimerFactory;
import com.f1.ami.center.timers.AmiTimerFactory_AmiScript;
import com.f1.ami.center.triggers.AmiServicePlugin;
import com.f1.ami.center.triggers.AmiTriggerFactory;
import com.f1.ami.center.triggers.AmiTriggerFactory_AmiScript;
import com.f1.ami.center.triggers.AmiTriggerFactory_Decorate;
import com.f1.ami.center.triggers.AmiTriggerFactory_Join;
import com.f1.ami.center.triggers.AmiTriggerFactory_Projection;
import com.f1.ami.center.triggers.AmiTriggerFactory_Relay;
import com.f1.ami.center.triggers.agg.AmiTriggerFactory_Aggregate;
import com.f1.ami.web.auth.AmiAuthenticatorPlugin;
import com.f1.base.Message;
import com.f1.container.ContainerTools;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.povo.standard.CountMessage;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.structs.table.derived.DerivedCellTimeoutController;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiCenterStartupProcessor extends AmiCenterBasicProcessor<Message> {
	private static final String AMI_PROPERTY_PREFIX = "ami.property.";
	public static final String SYSTEM = "__SYSTEM";

	public static final Logger log = LH.get();

	OutputPort<CountMessage> onStartedPort = newOutputPort(CountMessage.class);

	public AmiCenterStartupProcessor() {
		super(Message.class);
	}

	public static void initAmi(AmiCenterState state, ContainerTools tools) {
		CalcFrameStack sf = state.getReusableTopStackFrame();
		initPlugins(state, tools);
		AmiEncrypter encrypter = AmiUtils.initCertificate(tools);
		state.setEncrypter(encrypter);
		tools.getServices().putService(AmiConsts.SERVICE_ENCRYPTER, encrypter);
		initSchemaFromScripts(state, tools, sf);
		state.getAmiImdb().onStartupComplete();
		initSystemTables(state, tools, sf);
		state.setAmiSystemApplication(state.putAmiApplication(state.getAmiKeyId(SYSTEM), SYSTEM));
	}

	private static void initSystemTables(AmiCenterState state, ContainerTools tools, CalcFrameStack sf) {
		AmiImdbImpl db = state.getAmiImdb();
		AmiSchema sysSchema = db.getSystemSchema();
		AmiDatasourceAdapterManager dsm = state.getDatasourceManager();
		for (AmiDatasourcePlugin ds : dsm.getAmiDatasourcePlugins())
			sysSchema.__DATASOURCE_TYPE.addDatasourceType(state, ds, sf);

		// Create AMI datasource row
		boolean hasAmiRow = false;
		AmiSchema_DATASOURCE __DATASOURCE = sysSchema.__DATASOURCE;
		AmiColumn col = __DATASOURCE.table.getColumn(AmiConsts.PARAM_DATASOURCE_ADAPTER);
		for (int i = 0; i < __DATASOURCE.table.getRowsCount() && !hasAmiRow; i++)
			if (col.getString(__DATASOURCE.table.getAmiRowAt(i)).equals(AmiConsts.DATASOURCE_ADAPTER_NAME_AMI))
				hasAmiRow = true;
		if (!hasAmiRow)
			__DATASOURCE.addDatasource(-1, AmiConsts.DATASOURCE_ADAPTER_NAME_AMI, "AMI", null, null, null, null, false, null, null, null, sf);

		//Encrypt passwords in datasource (backwards compatibility)
		AmiPreparedRowImpl pr = __DATASOURCE.table.createAmiPreparedRow();
		for (int i = 0; i < __DATASOURCE.table.getRowsCount(); i++) {
			AmiRowImpl row = __DATASOURCE.table.getAmiRowAt(i);
			String password = row.getString(__DATASOURCE.password);
			String pw = row.getString(__DATASOURCE.pw);
			if (password == null || pw != null) {
				password = state.encrypt(pw);
				pr.reset();
				pr.setString(__DATASOURCE.password, password);
				pr.setString(__DATASOURCE.pw, null);
				__DATASOURCE.table.updateAmiRow(row.getAmiId(), pr, sf);
			}
		}

		sysSchema.__DATASOURCE_TYPE.addDatasourceType(state, new AmiDatasourcePluginForAmi(), sf);
		PropertyController amiProperties = tools.getSubPropertyController(AMI_PROPERTY_PREFIX);
		for (String key : amiProperties.getKeys()) {
			String value = amiProperties.getRequired(key, String.class);
			sysSchema.__PROPERTY.addRow(key, value, sf);
		}
		for (AmiDatasourcePlugin s : dsm.getAmiDatasourcePlugins())
			sysSchema.__PLUGIN.addRow(null, "DATASOURCE", s.getPluginId(), OH.getClassName(s), null, sf);

		for (AmiDatasourcePlugin s : dsm.getAmiDatasourcePlugins())
			sysSchema.__PLUGIN.addRow(AmiConsts.PLUGIN_TYPE_DATASOURCE, s, sf);
		for (String name : db.getStoredProcTypes())
			sysSchema.__PLUGIN.addRow(AmiConsts.PLUGIN_TYPE_PROCEDURE, db.getStoredProcFactory(name), sf);
		for (String name : db.getTriggerTypes())
			sysSchema.__PLUGIN.addRow(AmiConsts.PLUGIN_TYPE_TRIGGER, db.getTriggerFactory(name), sf);
		for (String name : db.getTablePersisterTypes())
			sysSchema.__PLUGIN.addRow(AmiConsts.PLUGIN_TYPE_PERSISTER, db.getTablePersisterFactory(name), sf);
		for (String name : db.getTimerTypes())
			sysSchema.__PLUGIN.addRow(AmiConsts.PLUGIN_TYPE_TIMER, db.getTimerFactory(name), sf);
		for (String name : db.getDboTypes())
			sysSchema.__PLUGIN.addRow(AmiConsts.PLUGIN_TYPE_DBO, db.getDboFactory(name), sf);

	}

	private static void initSchemaFromScripts(AmiCenterState state, ContainerTools tools, CalcFrameStack sf) {
		AmiCenterGlobalProcess process = state.getAmiImdb().getGlobalProcess();
		try {
			process.setProcessStatus(AmiCenterProcess.PROCESS_RUN);
			File managedFile = state.getTools().getOptional(AmiCenterProperties.PROPERTY_AMI_SCHEMA_MANAGED_FILE, new File("data/managed_schema.amisql"));
			if (!managedFile.exists()) {
				try {
					IOH.ensureDir(managedFile.getParentFile());
					IOH.writeText(managedFile, AmiSchema.MANAGED_FILE_HEADER);
				} catch (IOException e) {
					throw new RuntimeException("Error preparing schema file: " + IOH.getFullPath(managedFile), e);
				}
			}
			String schemaFileNames = state.getTools().getOptional(AmiCenterProperties.PROPERTY_AMI_SCHEMA_SCRIPT_FILES, "config/schema.amisql");
			List<File> schemafiles = AmiUtils.findFiles(schemaFileNames, false, true);
			String preschemaFileNames = state.getTools().getOptional(AmiCenterProperties.PROPERTY_AMI_PRESCHEMA_SCRIPT_FILES, "config/preschema.amisql");
			List<File> preschemafiles = AmiUtils.findFiles(preschemaFileNames, false, true);

			DerivedCellTimeoutController timeout = new DerivedCellTimeoutController(Long.MAX_VALUE);

			int limit = SqlProcessor.NO_LIMIT;
			for (File file : preschemafiles)
				state.getAmiImdb().getScriptManager().executedSqlFile(file, AmiTableUtils.DEFTYPE_CONFIG, "__STARTUP", new AmiCenterSqlPlanListener(file.getName(), false), timeout,
						limit, process, sf);

			state.getAmiImdb().getScriptManager().executedSqlFile(managedFile, AmiTableUtils.DEFTYPE_USER, "__STARTUP", new AmiCenterSqlPlanListener("MANAGED_FILE", false),
					timeout, limit, process, sf);

			for (File file : schemafiles)
				state.getAmiImdb().getScriptManager().executedSqlFile(file, AmiTableUtils.DEFTYPE_CONFIG, "__STARTUP", new AmiCenterSqlPlanListener(file.getName(), false), timeout,
						limit, process, sf);
		} finally {
			process.setProcessStatus(AmiCenterProcess.PROCESS_IDLE);
		}
	}

	private static void initPlugins(AmiCenterState state, ContainerTools tools) {
		final Map<String, AmiDatasourcePlugin> amiDatasourcePlugins = AmiUtils.loadPlugins(tools, PROPERTY_AMI_DATASOURCES, "Ami Datasource Plugin", AmiDatasourcePlugin.class);
		final Map<String, AmiServicePlugin> amiServicePlugins = AmiUtils.loadPlugins(tools, PROPERTY_AMI_DB_SERVICES, "Ami Service Plugin", AmiServicePlugin.class);
		final Map<String, AmiTablePersisterFactory> persisterPlugins = AmiUtils.loadPlugins(tools, PROPERTY_AMI_DB_PERSISTERS, "Ami Persister Plugin",
				AmiTablePersisterFactory.class);
		final Map<String, AmiTriggerFactory> triggerFactoryPlugins = AmiUtils.loadPlugins(tools, PROPERTY_AMI_DB_TRIGGERS, "Ami Trigger Plugin", AmiTriggerFactory.class);
		final Map<String, AmiTimerFactory> timerFactoryPlugins = AmiUtils.loadPlugins(tools, PROPERTY_AMI_DB_TIMERS, "Ami Timer Plugin", AmiTimerFactory.class);
		final Map<String, AmiStoredProcFactory> storedProcFactories = AmiUtils.loadPlugins(tools, PROPERTY_AMI_DB_PROCEDURES, "Ami Procedure Plugin", AmiStoredProcFactory.class);
		final Map<String, AmiDboFactory> dboFactories = AmiUtils.loadPlugins(tools, PROPERTY_AMI_DB_DBOS, "Ami Dbo Plugin", AmiDboFactory.class);
		final AmiImdbImpl amiImdb = state.getAmiImdb();
		AmiImdbFactoriesManager fm = amiImdb.getFactoriesManager();
		fm.addTablePersisterFactory(new AmiTablePersisterFactory_Text());
		fm.addTablePersisterFactory(new AmiTablePersisterFactory_Fast());
		fm.addTriggerFactory(new AmiTriggerFactory_AmiScript());
		fm.addTriggerFactory(new AmiTriggerFactory_Aggregate());
		fm.addTriggerFactory(new AmiTriggerFactory_Projection());
		fm.addTriggerFactory(new AmiTriggerFactory_Join());
		fm.addTriggerFactory(new AmiTriggerFactory_Decorate());
		fm.addTriggerFactory(new AmiTriggerFactory_Relay());
		fm.addTimerFactory(new AmiTimerFactory_AmiScript());
		fm.addStoredProcFactory(new AmiStoredProcFactory_AmiScript());
		for (AmiTablePersisterFactory i : persisterPlugins.values())
			fm.addTablePersisterFactory(i);
		for (AmiTriggerFactory i : triggerFactoryPlugins.values())
			fm.addTriggerFactory(i);
		for (AmiTimerFactory i : timerFactoryPlugins.values())
			fm.addTimerFactory(i);
		for (AmiStoredProcFactory i : storedProcFactories.values())
			fm.addStoredProcFactory(i);
		for (AmiDboFactory i : dboFactories.values())
			fm.addDboFactory(new AmiDboFactoryWrapper(i));
		//		state.getDatasourceManager().addAmiDatasourcePlugin(new AmiDatasourceAdapterForAmiPlugin(state));
		for (AmiDatasourcePlugin i : amiDatasourcePlugins.values())
			state.getDatasourceManager().addAmiDatasourcePlugin(i);
		for (AmiServicePlugin i : amiServicePlugins.values())
			amiImdb.getObjectsManager().addAmiService(i);
		amiImdb.initSystemSchema();
		AmiAuthenticatorPlugin authenticator = AmiUtils.loadAuthenticatorPlugin(tools, AmiCenterProperties.PROPERTY_AMI_DB_AUTH_PLUGIN_CLASS, "Ami Db Authenticator Plugin");
		tools.getServices().putService(AmiConsts.SERVICE_AUTH, authenticator);
		AmiAuthenticatorPlugin jdbcAuthenticator = AmiUtils.loadAuthenticatorPlugin(tools, AmiCenterProperties.PROPERTY_AMI_JDBC_AUTH_PLUGIN_CLASS,
				"Ami Jdbc Authenticator Plugin");
		tools.getServices().putService(AmiConsts.SERVICE_AUTH_JDBC, jdbcAuthenticator);
		state.setAuthenticator(authenticator);
	}
	@Override
	public void processAction(Message action, AmiCenterState state, ThreadScope threadScope) throws Exception {
		onStartedPort.send(nw(CountMessage.class), threadScope);
	}

}
