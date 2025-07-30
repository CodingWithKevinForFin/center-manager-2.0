package com.vortex.eye;

import java.io.File;

import com.f1.base.Message;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.OutputPort;
import com.f1.container.impl.BasicContainer;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.msgdirect.MsgDirectConnectionConfiguration;
import com.f1.msgdirect.MsgDirectTopicConfiguration;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.inspect.F1AppInspectionEntity;
import com.f1.utils.DBH;
import com.f1.utils.IOH;
import com.f1.utils.PropertiesHelper;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.db.Database;
import com.f1.utils.db.ResultSetGetter;
import com.f1.utils.db.ResultSetGetter.JsonResultSetGetter;
import com.f1.utils.ids.BasicNamespaceIdGenerator;
import com.f1.utils.ids.BatchIdGenerator;
import com.f1.utils.ids.DbBackedIdGenerator;
import com.f1.utils.ids.FileBackedIdGenerator;
import com.f1.vortexcommon.msg.agent.VortexAgentSnapshot;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentSnapshotRequest;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeCIMachineOPRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeCreateDeploymentEnvironmentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeGetEyeInfoRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeInstallAgentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageAuditTrailRuleRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBackupDestinationRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBackupRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBuildProcedureRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBuildResultRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageCloudInterfaceRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDbServerRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDeploymentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDeploymentSetRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageExpectationRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageMachineRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageMetadataFieldRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageScheduledTaskRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToAgentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToF1AppRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunBackupRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunBuildProcedureRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunCloudInterfaceActionRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunDbInspectionRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunDeploymentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunNetworkScanRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunScheduledTaskRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunShellCommandRequest;
import com.vortex.eye.itinerary.VortexEyeCIMachineOPItinerary;
import com.vortex.eye.itinerary.VortexEyeClientToAgentForF1AppPassthroughItinerary;
import com.vortex.eye.itinerary.VortexEyeClientToAgentPassthroughItinerary;
import com.vortex.eye.itinerary.VortexEyeCreateDeploymentEnvironmentItinerary;
import com.vortex.eye.itinerary.VortexEyeGetEyeInfoItinerary;
import com.vortex.eye.itinerary.VortexEyeInspectDbSchemaItinerary;
import com.vortex.eye.itinerary.VortexEyeInstallAgentItinerary;
import com.vortex.eye.itinerary.VortexEyeManageAuditRulesItinerary;
import com.vortex.eye.itinerary.VortexEyeManageBackupDestinationItinerary;
import com.vortex.eye.itinerary.VortexEyeManageBackupItinerary;
import com.vortex.eye.itinerary.VortexEyeManageBuildProcedureItinerary;
import com.vortex.eye.itinerary.VortexEyeManageBuildResultItinerary;
import com.vortex.eye.itinerary.VortexEyeManageCloudInterfaceItinerary;
import com.vortex.eye.itinerary.VortexEyeManageDbServerItinerary;
import com.vortex.eye.itinerary.VortexEyeManageDeploymentItinerary;
import com.vortex.eye.itinerary.VortexEyeManageDeploymentSetItinerary;
import com.vortex.eye.itinerary.VortexEyeManageExpectationItinerary;
import com.vortex.eye.itinerary.VortexEyeManageMachineItinerary;
import com.vortex.eye.itinerary.VortexEyeManageMetadataFieldItinerary;
import com.vortex.eye.itinerary.VortexEyeManageScheduledTaskItinerary;
import com.vortex.eye.itinerary.VortexEyeQueryDataRequestItinerary;
import com.vortex.eye.itinerary.VortexEyeRunBackupItinerary;
import com.vortex.eye.itinerary.VortexEyeRunBuildProcedureItinerary;
import com.vortex.eye.itinerary.VortexEyeRunCloudInterfaceActionItinerary;
import com.vortex.eye.itinerary.VortexEyeRunDeploymentItinerary;
import com.vortex.eye.itinerary.VortexEyeRunNetworkScanItinerary;
import com.vortex.eye.itinerary.VortexEyeRunScheduledTaskItinerary;
import com.vortex.eye.itinerary.VortexEyeRunShellCommandItinerary;
import com.vortex.eye.processors.VortexEyeCloudMainProcessor;
import com.vortex.eye.processors.VortexEyeCloudProcessor;
import com.vortex.eye.processors.VortexEyeStartupProcessor;
import com.vortex.eye.processors.VortexEyeVaultResponseProcessor;
import com.vortex.eye.processors.agent.VortexEyeAgentAuditEventsProcessor;
import com.vortex.eye.processors.agent.VortexEyeAgentBackupChangesProcessor;
import com.vortex.eye.processors.agent.VortexEyeAgentChangesProcessor;
import com.vortex.eye.processors.agent.VortexEyeAgentConnectionStatusProcessor;
import com.vortex.eye.processors.agent.VortexEyeAgentDeploymentChangesProcessor;
import com.vortex.eye.processors.agent.VortexEyeAgentF1ChangesProcessor;
import com.vortex.eye.processors.agent.VortexEyeAgentSnapshotResponseProcessor;
import com.vortex.eye.processors.client.VortexEyeClientSnapshotRequestProcessor;
import com.vortex.eye.processors.client.VortexEyeClientStatusRequestProcessor;
import com.vortex.eye.processors.eye.VortexEyeMonitorProcessor;

public class VortexEyeMain {

	public static final String OPTION_VORTEXEYE_AGENT_PORT = "vortex.eye.agent.port";
	public static final String OPTION_VORTEXEYE_AGENT_SSL_KEYSTORE_FILE = "vortex.eye.agent.ssl.keystore.file";
	public static final String OPTION_VORTEXEYE_AGENT_SSL_KEYSTORE_PASS = "vortex.eye.agent.ssl.keystore.password";
	public static final String OPTION_VORTEXEYE_AGENT_SSL_PORT = "vortex.eye.agent.ssl.port";

	public static final String OPTION_VORTEXEYE_GLASS_PORT = "vortex.eye.glass.port";
	public static final String OPTION_VORTEXEYE_GLASS_SSL_KEYSTORE_FILE = "vortex.eye.glass.ssl.keystore.file";
	public static final String OPTION_VORTEXEYE_GLASS_SSL_KEYSTORE_PASS = "vortex.eye.glass.ssl.keystore.password";
	public static final String OPTION_VORTEXEYE_GLASS_SSL_PORT = "vortex.eye.glass.ssl.port";

	public static final String OPTION_SQL_DIR = "sql.dir";
	public static final String OPTION_DBURL = "db.url";
	public static final String OPTION_DBPASSWORD = "db.password";
	public static final String OPTION_IDFOUNTAIN_BATCHSIZE = "idfountan.batchsize";

	public static final String OPTION_VORTEXEYE_PORT = "vortex.eye.port"; //deprecated
	public static final String OPTION_KEYSTORE_FILE = "keystore.file";//deprecated
	public static final String OPTION_KEYSTORE_PASS = "keystore.password";//deprecated
	public static final String OPTION_VORTEX_SSL_PORT = "vortex.ssl.port";//deprecated

	public static void main(String a[]) throws Exception {
		ContainerBootstrap cb = new ContainerBootstrap(VortexEyeMain.class, a);
		cb.setConsolePortProperty(4445);

		//cb.setLoggingOverrideProperty("info");
		cb.startup();
		cb.setProperty("journal.file", "./data/.journal.txt");
		cb.registerMessagesInPackages(F1AppInstance.class.getPackage());
		cb.registerMessagesInPackages(VortexAgentSnapshot.class.getPackage());
		cb.registerMessagesInPackages(VortexDeployment.class.getPackage());
		cb.registerMessagesInPackages(VortexAgentSnapshotRequest.class.getPackage());
		cb.registerMessagesInPackages(F1AppInspectionEntity.class.getPackage());
		cb.registerMessagesInPackages(VortexEyeManageExpectationRequest.class.getPackage());
		//cb.registerMessagesInPackages(VortexAmiEntity.class.getPackage());
		cb.keepAlive();
		PropertyController props = cb.getProperties();

		int glassPort = PropertiesHelper.getRequiredForAny(props, Caster_Integer.INSTANCE, OPTION_VORTEXEYE_GLASS_PORT, OPTION_VORTEXEYE_PORT);
		int agentPort = PropertiesHelper.getRequiredForAny(props, Caster_Integer.INSTANCE, OPTION_VORTEXEYE_AGENT_PORT, OPTION_VORTEXEYE_PORT);

		String dburl = props.getOptional(OPTION_DBURL);

		// Id fountain
		int idFountainBatchsize = props.getOptional(OPTION_IDFOUNTAIN_BATCHSIZE, 1000);

		MsgDirectConnection agentConnection;
		MsgDirectConnection glassConnection;
		if (glassPort == agentPort) {
			MsgDirectTopicConfiguration serverToAgentConfig = new MsgDirectTopicConfiguration("f1.server.to.agent", agentPort, "f1.server.to.agent");
			MsgDirectTopicConfiguration agentToServerConfig = new MsgDirectTopicConfiguration("f1.agent.to.server", agentPort, "f1.agent.to.server");
			MsgDirectTopicConfiguration serverToGlassConfig = new MsgDirectTopicConfiguration("server.to.gui", glassPort, "server.to.gui");
			MsgDirectTopicConfiguration glassToServerConfig = new MsgDirectTopicConfiguration("gui.to.server", glassPort, "gui.to.server");
			MsgDirectConnectionConfiguration agentConfig = new MsgDirectConnectionConfiguration("vortex_eye_agent");

			final String store = props.getOptional(OPTION_VORTEXEYE_AGENT_SSL_KEYSTORE_FILE);
			final Integer sslPort = props.getOptional(OPTION_VORTEXEYE_AGENT_SSL_PORT, Caster_Integer.INSTANCE);
			final String pass = props.getOptional(OPTION_VORTEXEYE_AGENT_SSL_KEYSTORE_PASS);
			if (store != null && sslPort != null && pass != null) {
				agentConfig.setKeystore(new File(store), pass);
				serverToAgentConfig.setSslPort(sslPort);
				agentToServerConfig.setSslPort(sslPort);
				serverToGlassConfig.setSslPort(sslPort);
				glassToServerConfig.setSslPort(sslPort);
			}
			agentConnection = new MsgDirectConnection(agentConfig);
			agentConnection.addTopic(serverToAgentConfig);
			agentConnection.addTopic(agentToServerConfig);
			agentConnection.addTopic(serverToGlassConfig);
			agentConnection.addTopic(glassToServerConfig);
			glassConnection = null;
		} else {

			{
				MsgDirectTopicConfiguration serverToAgentConfig = new MsgDirectTopicConfiguration("f1.server.to.agent", agentPort, "f1.server.to.agent");
				MsgDirectTopicConfiguration agentToServerConfig = new MsgDirectTopicConfiguration("f1.agent.to.server", agentPort, "f1.agent.to.server");
				MsgDirectConnectionConfiguration agentConfig = new MsgDirectConnectionConfiguration("vortex_eye_agent");

				final String store = props.getOptional(OPTION_VORTEXEYE_AGENT_SSL_KEYSTORE_FILE);
				final Integer sslPort = props.getOptional(OPTION_VORTEXEYE_AGENT_SSL_PORT, Caster_Integer.INSTANCE);
				final String pass = props.getOptional(OPTION_VORTEXEYE_AGENT_SSL_KEYSTORE_PASS);
				if (store != null && sslPort != null && pass != null) {
					agentConfig.setKeystore(new File(store), pass);
					serverToAgentConfig.setSslPort(sslPort);
					agentToServerConfig.setSslPort(sslPort);
				}
				agentConnection = new MsgDirectConnection(agentConfig);
				agentConnection.addTopic(serverToAgentConfig);
				agentConnection.addTopic(agentToServerConfig);
			}

			{

				MsgDirectTopicConfiguration serverToGlassConfig = new MsgDirectTopicConfiguration("server.to.gui", glassPort, "server.to.gui");
				MsgDirectTopicConfiguration glassToServerConfig = new MsgDirectTopicConfiguration("gui.to.server", glassPort, "gui.to.server");
				MsgDirectConnectionConfiguration glassConfig = new MsgDirectConnectionConfiguration("vortex_eye_glass");

				final String store = props.getOptional(OPTION_VORTEXEYE_GLASS_SSL_KEYSTORE_FILE);
				final Integer sslPort = props.getOptional(OPTION_VORTEXEYE_GLASS_SSL_PORT, Caster_Integer.INSTANCE);
				final String pass = props.getOptional(OPTION_VORTEXEYE_GLASS_SSL_KEYSTORE_PASS);
				if (store != null && sslPort != null && pass != null) {
					glassConfig.setKeystore(new File(store), pass);
					serverToGlassConfig.setSslPort(sslPort);
					glassToServerConfig.setSslPort(sslPort);
				}
				glassConnection = new MsgDirectConnection(glassConfig);
				glassConnection.addTopic(serverToGlassConfig);
				glassConnection.addTopic(glassToServerConfig);
			}
		}

		//MsgDirectConnection connection = new MsgDirectConnection(config);

		BasicContainer container = new BasicContainer();
		cb.prepareContainer(container);
		if (SH.isnt(dburl)) {
			System.out.println("No database specified, running in demonstration mode. Please supply property: " + OPTION_DBURL);
			//BatchIdGenerator.Factory<Long> fountain = new BatchIdGenerator.Factory<Long>(new DbBackedIdGenerator.Factory(dbsource, "Id_Fountains", "next_id", "namespace", 100000),
			//idFountainBatchsize);
			File fountainDir = props.getOptional("idfountain.path", new File("data/idfountain"));
			IOH.ensureDir(fountainDir);
			BatchIdGenerator.Factory<Long> fountain = new BatchIdGenerator.Factory<Long>(new FileBackedIdGenerator.Factory(fountainDir), 1000000);
			container.getServices().setUidGenerator(new BasicNamespaceIdGenerator<Long>(fountain));
		} else {
			Database dbsource = DBH.createPooledDataSource(dburl, props.getRequired(OPTION_DBPASSWORD));
			VortexEyeDbService dbservice = new VortexEyeDbService(dbsource, container.getGenerator(), container.getTools());
			VortexEyeUtils.setVortexDb(container, dbservice);
			BatchIdGenerator.Factory<Long> fountain = new BatchIdGenerator.Factory<Long>(new DbBackedIdGenerator.Factory(dbsource, "Id_Fountains", "next_id", "namespace", 100000),
					idFountainBatchsize);
			container.getServices().setUidGenerator(new BasicNamespaceIdGenerator<Long>(fountain));
			container.getServices().addDatabase("VORTEX", dbsource);
		}
		container.getServices().getUidGenerator("EYE").createNextId();//skip first

		VortexEyeSuite vortexSuite = new VortexEyeSuite(agentConnection, glassConnection);
		container.getRootSuite().addChild(vortexSuite);

		VortexEyeStartupProcessor startupProcessor;
		OutputPort<Message> startupPort = vortexSuite.exposeInputPortAsOutput(vortexSuite.addVortexEyeProcessor(startupProcessor = new VortexEyeStartupProcessor(), 0), true);
		vortexSuite.addVortexEyeProcessor(new VortexEyeAgentConnectionStatusProcessor(), VortexEyeSuite.RECEIVE_STATUS_FROM_AGENT);
		vortexSuite.addVortexEyeProcessor(new VortexEyeAgentSnapshotResponseProcessor(), VortexEyeSuite.RECEIVE_FROM_AGENT);
		vortexSuite.addVortexEyeProcessor(new VortexEyeAgentChangesProcessor(), VortexEyeSuite.RECEIVE_FROM_AGENT);
		vortexSuite.addVortexEyeProcessor(new VortexEyeAgentDeploymentChangesProcessor(), VortexEyeSuite.RECEIVE_FROM_AGENT);
		vortexSuite.addVortexEyeProcessor(new VortexEyeClientStatusRequestProcessor(), VortexEyeSuite.RECEIVE_FROM_CLIENT);
		VortexEyeAgentBackupChangesProcessor backupChagesProcessor = vortexSuite.addVortexEyeProcessor(new VortexEyeAgentBackupChangesProcessor(),
				VortexEyeSuite.RECEIVE_FROM_AGENT);
		VortexEyeVaultResponseProcessor vaultResponseProcessor = vortexSuite.addChild(new VortexEyeVaultResponseProcessor());
		vaultResponseProcessor.bindToPartition(VortexEyeSuite.PARTITIONID_VORTEX_EYE);
		vortexSuite.wire(backupChagesProcessor.responsePort, vaultResponseProcessor, true);

		vortexSuite.addVortexEyeProcessor(new VortexEyeAgentF1ChangesProcessor(), VortexEyeSuite.RECEIVE_FROM_AGENT);
		vortexSuite.addVortexEyeProcessor(new VortexEyeAgentAuditEventsProcessor(), VortexEyeSuite.RECEIVE_FROM_AGENT);

		vortexSuite.addVortexEyeProcessor(new VortexEyeClientSnapshotRequestProcessor(), VortexEyeSuite.RECEIVE_FROM_CLIENT);

		//setup ping pong for the cloud :)
		VortexEyeCloudMainProcessor cloudMainProcessor = new VortexEyeCloudMainProcessor(props.getOptional("cloud.refresh.interval", 1000 * 60));
		VortexEyeCloudProcessor cloudProcessor = new VortexEyeCloudProcessor();
		cloudMainProcessor.bindToPartition(VortexEyeSuite.PARTITIONID_VORTEX_EYE);
		cloudProcessor.bindToPartition(VortexEyeSuite.PARTITIONID_CLOUD);
		vortexSuite.addVortexEyeProcessor(cloudMainProcessor, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addChild(cloudProcessor);
		vortexSuite.wire(cloudProcessor.output, cloudMainProcessor, true);
		vortexSuite.wire(cloudMainProcessor.output, cloudProcessor, true);
		//start up ping pong
		cloudProcessor.output.send(null, null);

		vortexSuite.addItinerary(VortexEyeRunShellCommandRequest.class, VortexEyeRunShellCommandItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeRunNetworkScanRequest.class, VortexEyeRunNetworkScanItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeInstallAgentRequest.class, VortexEyeInstallAgentItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeCIMachineOPRequest.class, VortexEyeCIMachineOPItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeManageMetadataFieldRequest.class, VortexEyeManageMetadataFieldItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeManageBuildProcedureRequest.class, VortexEyeManageBuildProcedureItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeManageDeploymentSetRequest.class, VortexEyeManageDeploymentSetItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		//vortexSuite.addItinerary(VortexEyeManageAmiAlertRequest.class, VortexEyeManageAmiAlertItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		//vortexSuite.addItinerary(VortexEyeManageAmiDataRequest.class, VortexEyeManageAmiDataItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeManageAuditTrailRuleRequest.class, VortexEyeManageAuditRulesItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeManageDeploymentRequest.class, VortexEyeManageDeploymentItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeManageExpectationRequest.class, VortexEyeManageExpectationItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeManageDbServerRequest.class, VortexEyeManageDbServerItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeManageBackupDestinationRequest.class, VortexEyeManageBackupDestinationItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeManageBackupRequest.class, VortexEyeManageBackupItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeManageBuildResultRequest.class, VortexEyeManageBuildResultItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeManageScheduledTaskRequest.class, VortexEyeManageScheduledTaskItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeManageMachineRequest.class, VortexEyeManageMachineItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeManageCloudInterfaceRequest.class, VortexEyeManageCloudInterfaceItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeCreateDeploymentEnvironmentRequest.class, VortexEyeCreateDeploymentEnvironmentItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeGetEyeInfoRequest.class, VortexEyeGetEyeInfoItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeRunBuildProcedureRequest.class, VortexEyeRunBuildProcedureItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeRunCloudInterfaceActionRequest.class, VortexEyeRunCloudInterfaceActionItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeRunDeploymentRequest.class, VortexEyeRunDeploymentItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeRunDbInspectionRequest.class, VortexEyeInspectDbSchemaItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		//vortexSuite.addItinerary(VortexEyeGetAmiSchemaRequest.class, VortexEyeGetAmiSchemaRequestItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		//vortexSuite.addItinerary(VortexEyeGetAmiObjectsRequest.class, VortexEyeGetAmiObjectsRequestItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeRunBackupRequest.class, VortexEyeRunBackupItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeRunScheduledTaskRequest.class, VortexEyeRunScheduledTaskItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyeQueryDataRequest.class, VortexEyeQueryDataRequestItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyePassToAgentRequest.class, VortexEyeClientToAgentPassthroughItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);
		vortexSuite.addItinerary(VortexEyePassToF1AppRequest.class, VortexEyeClientToAgentForF1AppPassthroughItinerary.class, VortexEyeSuite.RECEIVE_FROM_CLIENT);

		VortexEyeMonitorProcessor monitorProcessor = new VortexEyeMonitorProcessor();
		vortexSuite.addVortexEyeProcessor(monitorProcessor, 0);
		vortexSuite.wire(startupProcessor.onStartedPort, monitorProcessor, true);
		vortexSuite.wire(monitorProcessor.loopback, monitorProcessor, true);
		vortexSuite.wire(monitorProcessor.toEyePort, vortexSuite.clientRoutingProcessor, true);

		JsonResultSetGetter jsonConverter = new ResultSetGetter.JsonResultSetGetter(container.getServices().getJsonConverter());
		vortexSuite.getDbProcessor().registerCustomGetter("JSON", jsonConverter);
		vortexSuite.getDbProcessor().registerCustomGetter("STRINGMAP", new VortexEyeDbService.StringMapParser());

		startupPort.send(container.nw(Message.class), null);
		cb.registerConsoleObject("eye", new VortexEyeConsole(container));

		//		final boolean UPDATE_DATABASE = false;
		//		if (UPDATE_DATABASE) {
		//			System.out.println("Going to update db.press Enter 3 times");
		//			System.in.read();
		//			System.in.read();
		//			System.in.read();
		//			System.out.println("Ok");
		//			Connection con2 = dbservice.getConnection();
		//			Table<Row> ids = DBH.toTable(con2.prepareStatement("select id,now,revision from BuildResults").executeQuery());
		//			con2.close();
		//
		//			String vault = container.getTools().getRequired("vortex.vault.dir") + "/";
		//			IdGenerator<Long> idgen = container.getServices().getUidGenerator(VortexEyeUtils.FOUNTAIN_ID);
		//			int count = 0;
		//			for (Row row : ids.getRows()) {
		//				Connection con3 = dbservice.getConnection();
		//				System.out.println(" progress: " + (count++) + " / " + ids.getSize());
		//				long now = row.get("now", long.class);
		//				long id = row.get("id", long.class);
		//				PreparedStatement ps = con3.prepareStatement("select data,verify_data,build_stderr,build_stdout from BuildResults where id=? and now=?");
		//				ps.setLong(1, id);
		//				ps.setLong(2, now);
		//				ResultSet results = ps.executeQuery();
		//				results.next();
		//				byte[] data = results.getBytes("data");
		//				byte[] verify_data = results.getBytes("verify_data");
		//				byte[] build_stdout = results.getBytes("build_stdout");
		//				byte[] build_stderr = results.getBytes("build_stderr");
		//
		//				if (data == null && verify_data == null && build_stdout == null && build_stderr == null) {
		//					IOH.close(con3);
		//					continue;
		//				}
		//				Long data_id = null, verify_data_id = null, build_stdout_id = null, build_stderr_id = null;
		//
		//				if (data != null) {
		//					if (data.length == 0)
		//						data_id = 0L;
		//					else
		//						IOH.writeData(new File(vault + (data_id = idgen.createNextId()) + VortexVaultState.SUFFIX), data);
		//				}
		//				if (verify_data != null) {
		//					if (verify_data.length == 0)
		//						verify_data_id = 0L;
		//					else
		//						IOH.writeData(new File(vault + (verify_data_id = idgen.createNextId()) + VortexVaultState.SUFFIX), verify_data);
		//				}
		//				if (build_stdout != null) {
		//					if (build_stdout.length == 0)
		//						build_stdout_id = 0L;
		//					else
		//						IOH.writeData(new File(vault + (build_stdout_id = idgen.createNextId()) + VortexVaultState.SUFFIX), build_stdout);
		//				}
		//				if (build_stderr != null) {
		//					if (build_stderr.length == 0)
		//						build_stderr_id = 0L;
		//					else
		//						IOH.writeData(new File(vault + (build_stderr_id = idgen.createNextId()) + VortexVaultState.SUFFIX), build_stderr);
		//				}
		//
		//				System.out.println(row.get("id") + "," + row.get("now") + ": " + AH.length(data) + ", " + AH.length(verify_data) + ", " + AH.length(build_stdout) + ", "
		//						+ AH.length(build_stderr) + ", ");
		//				results.close();
		//				ps.close();
		//				ps = con3.prepareStatement("update BuildResults set data_vvid=?, verify_data_vvid=?, build_stderr_vvid=?, build_stdout_vvid=? "
		//						+ ", data_length=?, verify_data_length=?, build_stderr_length=?, build_stdout_length=? where id=? and now=?");
		//				ps.setObject(1, data_id);
		//				ps.setObject(2, verify_data_id);
		//				ps.setObject(3, build_stderr_id);
		//				ps.setObject(4, build_stdout_id);
		//				ps.setObject(5, length(data));
		//				ps.setObject(6, length(verify_data));
		//				ps.setObject(7, length(build_stderr));
		//				ps.setObject(8, length(build_stdout));
		//				ps.setLong(9, id);
		//				ps.setLong(10, now);
		//				ps.executeUpdate();
		//				ps.close();
		//				if (AH.length(data) > 0) {
		//					VortexVaultEntry v = container.nw(VortexVaultEntry.class);
		//					v.setChecksum(IOH.checkSumBsdLong(data));
		//					v.setData(data);
		//					v.setDataLength(data.length);
		//					v.setId(data_id);
		//					v.setNow(now);
		//					dbservice.insertVortexVaultEntry(con3, v);
		//				}
		//				if (AH.length(verify_data) > 0) {
		//					VortexVaultEntry v = container.nw(VortexVaultEntry.class);
		//					v.setChecksum(IOH.checkSumBsdLong(verify_data));
		//					v.setData(verify_data);
		//					v.setDataLength(verify_data.length);
		//					v.setId(verify_data_id);
		//					v.setNow(now);
		//					dbservice.insertVortexVaultEntry(con3, v);
		//				}
		//				IOH.close(con3);
		//			}
		//			System.exit(0);
		//		}
		cb.startupContainer(container);

	}
	private static Object length(byte[] data) {
		return data == null ? null : data.length;
	}

}
