package com.vortex.agent;

import java.io.File;
import java.io.IOException;

import com.f1.bootstrap.ContainerBootstrap;
import com.f1.bootstrap.F1Constants;
import com.f1.container.impl.BasicContainer;
import com.f1.msg.impl.MsgConsole;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.msgdirect.MsgDirectConnectionConfiguration;
import com.f1.msgdirect.MsgDirectTopicConfiguration;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.inspect.F1AppInspectionEntity;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.PropertyController;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentChangesRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileDeleteRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentGetBackupChangedFilesRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentGetBackupDestinationManifestRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentInspectDbRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentPassToF1AppRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunDeploymentRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunOsCommandRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRunSignalProcessRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentSendBackupFilesToDestinationRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentSnapshotRequest;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.vortex.agent.dbadapter.MsSqlServerDbInspector;
import com.vortex.agent.dbadapter.MysqlDbInspector;
import com.vortex.agent.dbadapter.OracleDbInspector;
import com.vortex.agent.dbadapter.SybaseDbInspector;
import com.vortex.agent.itinerary.VortexAgentEyeChangesRequestItinerary;
import com.vortex.agent.itinerary.VortexAgentEyeFileDeleteItinerary;
import com.vortex.agent.itinerary.VortexAgentEyeRunOsCommandItinerary;
import com.vortex.agent.itinerary.VortexAgentEyeSendSignalToProcessItinerary;
import com.vortex.agent.itinerary.VortexAgentEyeSnapshotRequestItinerary;
import com.vortex.agent.itinerary.VortexAgentEyeToF1AppPassthroughItinerary;
import com.vortex.agent.itinerary.VortexAgentFileSearchingItinerary;
import com.vortex.agent.itinerary.VortexAgentGetBackupChangedFilesItinerary;
import com.vortex.agent.itinerary.VortexAgentGetBackupDestinationManifestItinerary;
import com.vortex.agent.itinerary.VortexAgentInspectDbSchemaItinerary;
import com.vortex.agent.itinerary.VortexAgentRunDeploymentItinerary;
import com.vortex.agent.itinerary.VortexAgentSendBackupFilesToDestinationItinerary;
import com.vortex.agent.osadapter.VortexAgentNoopOsAdapter;
import com.vortex.agent.osadapter.linux.VortexAgentLinuxDeploymentRunner;
import com.vortex.agent.osadapter.linux.VortexAgentLinuxOsAdapter;
import com.vortex.agent.osadapter.linux.VortexAgentLinuxOsAdapterCommandRunner;
import com.vortex.agent.osadapter.linux.VortexAgentLinuxOsAdapterFileSearcher;
import com.vortex.agent.osadapter.linux.VortexAgentLinuxOsAdapterSendSignal;
import com.vortex.agent.osadapter.mac.VortexAgentMacOsAdapter;
import com.vortex.agent.osadapter.mac.VortexAgentMacOsAdapterCommandRunner;
import com.vortex.agent.osadapter.sun.VortexAgentSunOsAdapter;
import com.vortex.agent.osadapter.windows.VortexAgentWindowsDeploymentRunner;
import com.vortex.agent.osadapter.windows.VortexAgentWindowsOsAdapter;
import com.vortex.agent.osadapter.windows.VortexAgentWindowsOsAdapterCommandRunner;
import com.vortex.agent.osadapter.windows.VortexAgentWindowsOsAdapterFileSearcher;
import com.vortex.agent.processors.VortexAgentMonitorBackupProcessor;
import com.vortex.agent.processors.VortexAgentMonitoringProcessor;
import com.vortex.agent.processors.eye.VortexAgentEyeAuditTrailRuleSetProcessor;
import com.vortex.agent.processors.eye.VortexAgentEyeConnectionStatusProcessor;
import com.vortex.agent.processors.eye.VortexAgentEyeUpdateBackupsProcessor;
import com.vortex.agent.processors.eye.VortexAgentEyeUpdateDeploymentsProcessor;
import com.vortex.agent.processors.f1app.VortexAgentF1AppAuditTrailProcessor;
import com.vortex.agent.processors.f1app.VortexAgentF1AppChangesProcessor;
import com.vortex.agent.processors.f1app.VortexAgentF1AppConnectionStatusProcessor;
import com.vortex.agent.processors.f1app.VortexAgentF1AppSnapshotProcessor;
import com.vortex.agent.state.VortexAgentOsAdapterManager;
import com.vortex.agent.state.VortexAgentOsAdapterStateGenerator;
import com.vortex.agent.state.VortexAgentState;

public class VortexAgentMain {

	public static final String OPTION_VAM_PORT = "vam.port";
	public static final String OPTION_VORTEX_PORT = "vortex.eye.port";
	public static final String OPTION_VORTEX_HOST = "vortex.eye.host";
	public static final String OPTION_VORTEX_PORT2 = "vortex.eye.backup.port";
	public static final String OPTION_VORTEX_HOST2 = "vortex.eye.backup.host";
	public static final String OPTION_KEYSTORE_FILE = "keystore.file";
	public static final String OPTION_KEYSTORE_PASS = "keystore.password";
	public static final String OPTION_VORTEX_SSL_PORT = "vortex.ssl.port";
	public static final String OPTION_VORTEX_SSL_BACKUP_PORT = "vortex.ssl.backup.port";
	public static final String OPTION_VORTEX_AGENT_PORT = "vortex.agent.server.port";
	private static final int DEFAULT_VAM_PORT = 3289;

	public static void main(String a[]) throws IOException, InstantiationException, IllegalAccessException {

		//Configuration
		ContainerBootstrap cb = new ContainerBootstrap(VortexAgentMain.class, a);
		cb.setConfigDirProperty("./src/main/config");
		cb.setLoggingOverrideProperty("info");
		cb.setConsolePortProperty(3407);
		PropertyController props = cb.getProperties();
		cb.startup();

		//register messages

		cb.registerMessagesInPackages(VortexAgentRequest.class.getPackage());
		cb.registerMessagesInPackages(VortexAgentEntity.class.getPackage());
		cb.registerMessagesInPackages(VortexDeployment.class.getPackage());
		cb.registerMessagesInPackages(F1AppInstance.class.getPackage());
		cb.registerMessagesInPackages(F1AppInspectionEntity.class.getPackage());

		//Connectivity
		String host = props.getRequired(OPTION_VORTEX_HOST, String.class);
		int port = props.getRequired(OPTION_VORTEX_PORT, Integer.class);
		String host2 = props.getOptional(OPTION_VORTEX_HOST2, host);
		int port2 = props.getOptional(OPTION_VORTEX_PORT2, port);
		int agentPort = props.getOptional(OPTION_VORTEX_AGENT_PORT, F1Constants.DEFAULT_AGENT_PORT);

		int vamport = props.getOptional(OPTION_VAM_PORT, DEFAULT_VAM_PORT);

		MsgDirectConnectionConfiguration config = new MsgDirectConnectionConfiguration("vortex_agent");
		MsgDirectConnectionConfiguration appconfig = new MsgDirectConnectionConfiguration("vortex_agent_app");
		MsgDirectTopicConfiguration serverToAgentConfig = new MsgDirectTopicConfiguration("f1.server.to.agent", host, port, "f1.server.to.agent");
		MsgDirectTopicConfiguration agentToServerConfig = new MsgDirectTopicConfiguration("f1.agent.to.server", host, port, "f1.agent.to.server");

		final String store = props.getOptional(OPTION_KEYSTORE_FILE);
		final MsgDirectConnection connection = new MsgDirectConnection(config);
		connection.addTopic(serverToAgentConfig);
		connection.addTopic(agentToServerConfig);
		final MsgDirectConnection appconnection = new MsgDirectConnection(appconfig);
		appconnection.addTopic(new MsgDirectTopicConfiguration("f1.agent.to.app", agentPort));
		appconnection.addTopic(new MsgDirectTopicConfiguration("f1.app.to.agent", agentPort));

		MsgDirectConnectionConfiguration config2 = new MsgDirectConnectionConfiguration("vortex_agent_backup");
		MsgDirectTopicConfiguration serverToAgentConfig2 = new MsgDirectTopicConfiguration("f1.server.to.agent", host2, port2, "f1.server.to.agent");
		MsgDirectTopicConfiguration agentToServerConfig2 = new MsgDirectTopicConfiguration("f1.agent.to.server", host2, port2, "f1.agent.to.server");
		final MsgDirectConnection connection2 = new MsgDirectConnection(config2);
		connection2.addTopic(serverToAgentConfig2);
		connection2.addTopic(agentToServerConfig2);
		if (store != null) {
			final int sslPort = props.getRequired(OPTION_VORTEX_SSL_PORT, Integer.class);
			final int sslBakcupPort = props.getRequired(OPTION_VORTEX_SSL_BACKUP_PORT, Integer.class);
			final String pass = props.getRequired(OPTION_KEYSTORE_PASS);
			config.setKeystore(new File(store), pass);
			serverToAgentConfig.setSslPort(sslPort);
			agentToServerConfig.setSslPort(sslPort);
			serverToAgentConfig2.setSslPort(sslBakcupPort);
			agentToServerConfig2.setSslPort(sslBakcupPort);
		}

		final BasicContainer container = new BasicContainer();
		final VortexAgentOsAdapterManager osAdapterManager = getOsAdapterManager(props);
		System.out.println("Using Os Adapter: " + osAdapterManager.getClass().getSimpleName());
		container.getServices().putService(VortexAgentOsAdapterStateGenerator.SERVICE_ID_OS_ADAPTER, osAdapterManager);
		container.getPartitionController().registerStateGenerator(new VortexAgentOsAdapterStateGenerator());

		VortexAgentUtils.put(container, MysqlDbInspector.ID, new MysqlDbInspector());
		VortexAgentUtils.put(container, MsSqlServerDbInspector.ID, new MsSqlServerDbInspector());
		VortexAgentUtils.put(container, OracleDbInspector.ID, new OracleDbInspector());
		VortexAgentUtils.put(container, SybaseDbInspector.ID, new SybaseDbInspector());

		cb.prepareContainer(container);

		final VortexAgentSuite vortexAgentSuite = new VortexAgentSuite(appconnection, connection, connection2);
		container.getRootSuite().addChild(vortexAgentSuite);

		vortexAgentSuite.addVortexAgentProcessor(new VortexAgentF1AppChangesProcessor(), VortexAgentSuite.RECEIVE_FROM_F1APP);
		VortexAgentF1AppSnapshotProcessor f1AppSnapshotProcessor = vortexAgentSuite.addVortexAgentProcessor(new VortexAgentF1AppSnapshotProcessor(),
				VortexAgentSuite.RECEIVE_FROM_F1APP);
		vortexAgentSuite.addVortexAgentProcessor(new VortexAgentF1AppAuditTrailProcessor(), VortexAgentSuite.RECEIVE_FROM_F1APP);
		VortexAgentF1AppConnectionStatusProcessor f1AppConnectionStatusProcessor = vortexAgentSuite.addVortexAgentProcessor(new VortexAgentF1AppConnectionStatusProcessor(),
				VortexAgentSuite.RECEIVE_STATUS_FROM_F1APP);
		vortexAgentSuite.addVortexAgentProcessor(new VortexAgentEyeConnectionStatusProcessor(), VortexAgentSuite.RECEIVE_STATUS_FROM_EYE);
		vortexAgentSuite.addVortexAgentProcessor(new VortexAgentEyeAuditTrailRuleSetProcessor(), VortexAgentSuite.RECEIVE_FROM_EYE);
		VortexAgentEyeUpdateDeploymentsProcessor updateDeploymentsProcessor = vortexAgentSuite.addVortexAgentProcessor(new VortexAgentEyeUpdateDeploymentsProcessor(),
				VortexAgentSuite.RECEIVE_FROM_EYE);
		VortexAgentEyeUpdateBackupsProcessor updateBackupsProcessor = vortexAgentSuite.addVortexAgentProcessor(new VortexAgentEyeUpdateBackupsProcessor(),
				VortexAgentSuite.RECEIVE_FROM_EYE);

		VortexAgentMonitorBackupProcessor monitorBackupsProcessor = vortexAgentSuite.addChild(new VortexAgentMonitorBackupProcessor());
		VortexAgentMonitoringProcessor monitorDeploymentsProcessor = vortexAgentSuite.addChild(new VortexAgentMonitoringProcessor());
		vortexAgentSuite.wire(updateBackupsProcessor.toBackups, monitorBackupsProcessor, true);
		vortexAgentSuite.wire(updateDeploymentsProcessor.toDeployments, monitorDeploymentsProcessor, true);
		vortexAgentSuite.wire(f1AppSnapshotProcessor.toDeployments, monitorDeploymentsProcessor, true);
		vortexAgentSuite.wire(f1AppConnectionStatusProcessor.toDeployments, monitorDeploymentsProcessor, true);
		vortexAgentSuite.wire(monitorBackupsProcessor.loopback, monitorBackupsProcessor, true);
		vortexAgentSuite.wire(monitorBackupsProcessor.toEye, vortexAgentSuite.msgClientSuite.outboundInputPort, true);
		vortexAgentSuite.wire(monitorDeploymentsProcessor.loopback, monitorDeploymentsProcessor, true);
		vortexAgentSuite.wire(monitorDeploymentsProcessor.toEye, vortexAgentSuite.msgClientSuite.outboundInputPort, true);

		vortexAgentSuite.addItinerary(VortexAgentSnapshotRequest.class, VortexAgentEyeSnapshotRequestItinerary.class, VortexAgentSuite.RECEIVE_FROM_EYE);
		vortexAgentSuite.addItinerary(VortexAgentChangesRequest.class, VortexAgentEyeChangesRequestItinerary.class, VortexAgentSuite.RECEIVE_FROM_EYE);
		vortexAgentSuite.addItinerary(VortexAgentRunOsCommandRequest.class, VortexAgentEyeRunOsCommandItinerary.class, VortexAgentSuite.RECEIVE_FROM_EYE);
		vortexAgentSuite.addItinerary(VortexAgentFileDeleteRequest.class, VortexAgentEyeFileDeleteItinerary.class, VortexAgentSuite.RECEIVE_FROM_EYE);
		vortexAgentSuite.addItinerary(VortexAgentFileSearchRequest.class, VortexAgentFileSearchingItinerary.class, VortexAgentSuite.RECEIVE_FROM_EYE);
		vortexAgentSuite.addItinerary(VortexAgentInspectDbRequest.class, VortexAgentInspectDbSchemaItinerary.class, VortexAgentSuite.RECEIVE_FROM_EYE);
		vortexAgentSuite.addItinerary(VortexAgentRunDeploymentRequest.class, VortexAgentRunDeploymentItinerary.class, VortexAgentSuite.RECEIVE_FROM_EYE);
		vortexAgentSuite.addItinerary(VortexAgentRunSignalProcessRequest.class, VortexAgentEyeSendSignalToProcessItinerary.class, VortexAgentSuite.RECEIVE_FROM_EYE);
		//vortexAgentSuite.addItinerary(VortexAgentRunAmiCommandRequest.class, VortexAgentEyeRunAmiCommandItinerary.class, VortexAgentSuite.RECEIVE_FROM_EYE);
		vortexAgentSuite.addItinerary(VortexAgentGetBackupDestinationManifestRequest.class, VortexAgentGetBackupDestinationManifestItinerary.class,
				VortexAgentSuite.RECEIVE_FROM_EYE);
		vortexAgentSuite.addItinerary(VortexAgentGetBackupChangedFilesRequest.class, VortexAgentGetBackupChangedFilesItinerary.class, VortexAgentSuite.RECEIVE_FROM_EYE);
		vortexAgentSuite.addItinerary(VortexAgentSendBackupFilesToDestinationRequest.class, VortexAgentSendBackupFilesToDestinationItinerary.class,
				VortexAgentSuite.RECEIVE_FROM_EYE);
		vortexAgentSuite.addItinerary(VortexAgentPassToF1AppRequest.class, VortexAgentEyeToF1AppPassthroughItinerary.class, VortexAgentSuite.RECEIVE_FROM_EYE);

		//VortexAgentAmiProcessor amiProcessor = vortexAgentSuite.addVortexAgentProcessor(new VortexAgentAmiProcessor(), 0);
		//OutputPort<AgentAmiMessage> toAmiPort = vortexAgentSuite.exposeInputPortAsOutput(amiProcessor, true);
		VortexAgentState vortexAgentState = new VortexAgentState();
		//AmiServerSocket amiServer = new AmiServerSocket(vamport, new NamedThreadFactory("VAM", false), container.getServices().getGenerator(), toAmiPort);
		//vortexAgentState.setAmiServer(amiServer);
		container.getPartitionController().putState(VortexAgentSuite.PARTITIONID_VORTEX_AGENT, vortexAgentState);
		cb.startupContainer(container);
		//amiServer.start();
		//monitorPort.send(container.nw(Message.class), null);

		cb.registerConsoleObject("msg", new MsgConsole(connection));
		cb.registerConsoleObject("agent", new VortexAgentConsole(container));
		IOH.ensureDir(props.getOptional("recycling.dir", new File("/tmp/recycling")));
		cb.keepAlive();
	}
	private static VortexAgentOsAdapterManager getOsAdapterManager(PropertyController props) throws IOException {

		VortexAgentOsAdapterManager r = new VortexAgentOsAdapterManager();
		if (props.getOptional("f1.agent.nomonitor", Boolean.FALSE)) {
			VortexAgentNoopOsAdapter linuxAdapter = new VortexAgentNoopOsAdapter();
			r.setInspectCron(linuxAdapter);
			r.setInspectFileSystems(linuxAdapter);
			r.setInspectMachine(linuxAdapter);
			r.setInspectMachineEvents(linuxAdapter);
			r.setInspectNetAddresses(linuxAdapter);
			r.setInspectNetConnections(linuxAdapter);
			r.setInspectNetLinks(linuxAdapter);
			r.setInspectProcesses(linuxAdapter);
			r.setFileSearcher(new VortexAgentLinuxOsAdapterFileSearcher());
			r.setCommandRunner(new VortexAgentLinuxOsAdapterCommandRunner());
			r.setDeploymentRunner(new VortexAgentLinuxDeploymentRunner());
			r.setSendSignal(new VortexAgentLinuxOsAdapterSendSignal());
			r.setFileDeleter(new VortexAgentFileDeleter(props.getOptional("recycling.dir", new File("/tmp/recycling"))));
		} else if (EH.getOsName().startsWith("SunOS")) {
			VortexAgentSunOsAdapter linuxAdapter = new VortexAgentSunOsAdapter();
			r.setInspectCron(linuxAdapter);
			r.setInspectFileSystems(linuxAdapter);
			r.setInspectMachine(linuxAdapter);
			r.setInspectMachineEvents(linuxAdapter);
			r.setInspectNetAddresses(linuxAdapter);
			r.setInspectNetConnections(linuxAdapter);
			r.setInspectNetLinks(linuxAdapter);
			r.setInspectProcesses(linuxAdapter);
			r.setFileSearcher(new VortexAgentLinuxOsAdapterFileSearcher());
			r.setCommandRunner(new VortexAgentLinuxOsAdapterCommandRunner());
			r.setDeploymentRunner(new VortexAgentLinuxDeploymentRunner());
			r.setSendSignal(new VortexAgentLinuxOsAdapterSendSignal());
			r.setFileDeleter(new VortexAgentFileDeleter(props.getOptional("recycling.dir", new File("/tmp/recycling"))));
		} else if (EH.getOsName().startsWith("Windows")) {
			VortexAgentWindowsOsAdapter winAdapter = new VortexAgentWindowsOsAdapter(props);
			//	r.setInspectCron(winAdapter);
			r.setInspectFileSystems(winAdapter);
			r.setInspectMachine(winAdapter);
			r.setInspectMachineEvents(winAdapter);
			r.setInspectNetAddresses(winAdapter);
			r.setInspectNetConnections(winAdapter);
			r.setInspectNetLinks(winAdapter);
			r.setInspectProcesses(winAdapter);
			r.setFileSearcher(new VortexAgentWindowsOsAdapterFileSearcher());
			r.setCommandRunner(new VortexAgentWindowsOsAdapterCommandRunner());
			r.setDeploymentRunner(new VortexAgentWindowsDeploymentRunner());
			//		r.setSendSignal(new VortexAgentWindowsOsAdapterSendSignal());
			r.setFileDeleter(new VortexAgentFileDeleter(props.getOptional("recycling.dir", new File("/tmp/recycling"))));
		} else if (EH.getOsName().startsWith("Mac")) {
			VortexAgentMacOsAdapter macAdapter = new VortexAgentMacOsAdapter();
			r.setInspectCron(macAdapter);
			r.setInspectFileSystems(macAdapter);
			r.setInspectMachine(macAdapter);
			r.setInspectMachineEvents(macAdapter);
			r.setInspectNetAddresses(macAdapter);
			r.setInspectNetConnections(macAdapter);
			r.setInspectNetLinks(macAdapter);
			r.setInspectProcesses(macAdapter);
			r.setFileSearcher(new VortexAgentLinuxOsAdapterFileSearcher());
			r.setCommandRunner(new VortexAgentMacOsAdapterCommandRunner());
			r.setDeploymentRunner(new VortexAgentLinuxDeploymentRunner());
			r.setSendSignal(new VortexAgentLinuxOsAdapterSendSignal());
			r.setFileDeleter(new VortexAgentFileDeleter(props.getOptional("recycling.dir", new File("/tmp/recycling"))));
		} else {
			VortexAgentLinuxOsAdapter linuxAdapter = new VortexAgentLinuxOsAdapter(props.getOptional("f1.agent.enable.root", Boolean.TRUE));
			r.setInspectCron(linuxAdapter);
			r.setInspectFileSystems(linuxAdapter);
			r.setInspectMachine(linuxAdapter);
			r.setInspectMachineEvents(linuxAdapter);
			r.setInspectNetAddresses(linuxAdapter);
			r.setInspectNetConnections(linuxAdapter);
			r.setInspectNetLinks(linuxAdapter);
			r.setInspectProcesses(linuxAdapter);
			r.setFileSearcher(new VortexAgentLinuxOsAdapterFileSearcher());
			r.setCommandRunner(new VortexAgentLinuxOsAdapterCommandRunner());
			r.setDeploymentRunner(new VortexAgentLinuxDeploymentRunner());
			r.setSendSignal(new VortexAgentLinuxOsAdapterSendSignal());
			r.setFileDeleter(new VortexAgentFileDeleter(props.getOptional("recycling.dir", new File("/tmp/recycling"))));
		}

		if (props.getOptional("f1.agent.skip.netconnections", Boolean.FALSE))
			r.setInspectNetConnections(new VortexAgentNoopOsAdapter());
		r.lock();
		return r;
	}
}
