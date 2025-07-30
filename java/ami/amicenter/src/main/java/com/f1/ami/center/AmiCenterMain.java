package com.f1.ami.center;

import java.io.File;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiChainedNamingServiceResolver;
import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiNamingServiceResolverHelper;
import com.f1.ami.amicommon.AmiProcessStatsLogger;
import com.f1.ami.amicommon.AmiStartup;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.customobjects.AmiScriptClassPluginWrapper;
import com.f1.ami.amicommon.messaging.SimpleMessagingServer;
import com.f1.ami.amicommon.msg.AmiCenterGetResourceRequest;
import com.f1.ami.amicommon.msg.AmiCenterGetResourceResponse;
import com.f1.ami.amicommon.msg.AmiCenterManageDatasourceRequest;
import com.f1.ami.amicommon.msg.AmiCenterManageResourcesRequest;
import com.f1.ami.amicommon.msg.AmiCenterPassToRelayRequest;
import com.f1.ami.amicommon.msg.AmiCenterRequest;
import com.f1.ami.amicommon.msg.AmiCenterResponse;
import com.f1.ami.amicommon.msg.AmiRelayObjectMessage;
import com.f1.ami.amicommon.rest.AmiRestServer;
import com.f1.ami.center.console.AmiCenterConsole;
import com.f1.ami.center.dialects.AmiDbDialectPlugin;
import com.f1.ami.center.hdb.events.AmiHdbRtEventProcessor;
import com.f1.ami.center.jdbc.AmiCenterJdbcServer;
import com.f1.ami.center.replication.AmiCenterReplicationConnectedMessage;
import com.f1.ami.center.replication.AmiCenterReplicationConnectedProcessor;
import com.f1.ami.center.replication.AmiCenterReplicationDisconnectedMessage;
import com.f1.ami.center.replication.AmiCenterReplicationDisconnectedProcessor;
import com.f1.ami.center.replication.AmiCenterReplicationObjectsMessage;
import com.f1.ami.center.replication.AmiCenterReplicationObjectsProcessor;
import com.f1.ami.center.table.AmiImdbSessionManagerService;
import com.f1.ami.client.AmiCenterClient;
import com.f1.ami.web.auth.AmiAuthenticatorPlugin;
import com.f1.base.Message;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.ContainerTools;
import com.f1.container.DispatchController;
import com.f1.container.InputPort;
import com.f1.container.OutputPort;
import com.f1.container.RequestOutputPort;
import com.f1.container.impl.BasicContainer;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.msgdirect.MsgDirectConnectionConfiguration;
import com.f1.msgdirect.MsgDirectTopicConfiguration;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.ServerSocketEntitlements;
import com.f1.utils.concurrent.FastThreadPool;
import com.f1.utils.concurrent.NamedThreadFactory;
import com.f1.utils.concurrent.SimpleExecutor;
import com.f1.utils.ids.BasicNamespaceIdGenerator;
import com.f1.utils.ids.BatchLongSequenceIdGenerator;
import com.f1.utils.ids.BatchLongSequenceIdGenerator.Factory;
import com.f1.utils.ids.FileBackedLongSequenceIdGenerator;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.stack.MutableCalcFrame;

public class AmiCenterMain {

	private static final Logger log = LH.get();

	public static void main(String[] a) throws Exception {
		ContainerBootstrap bs = new ContainerBootstrap(AmiCenterMain.class, a);
		bs.setProperty("f1.appname", "AmiCenter");
		bs.setProperty("f1.logfilename", "AmiCenter");
		bs.setProperty("f1.autocoded.disabled", "true");
		bs.setProperty("f1.threadpool.agressive", "false");
		AmiStartup.startupAmi(bs, "ami_amicenter");
		main2(bs);
	}

	public static DispatchController main2(ContainerBootstrap cb) throws Exception {

		DriverManager.getDrivers();//force init driver manager
		PropertyController props = cb.getProperties();
		BasicContainer container = new BasicContainer();
		container.setName("AmiCenter");
		container.getDispatchController().setQueueTimeoutCheckFrequency(1);
		cb.prepareContainer(container);

		int port = props.getOptional(AmiCenterProperties.PROPERTY_AMI_CENTER_PORT, Integer.class);
		String portBindAddr = props.getOptional(AmiCenterProperties.PROPERTY_AMI_CENTER_PORT_BINDADDR);
		ServerSocketEntitlements entitlements = AmiUtils.parseWhiteList(container.getTools(), props, AmiCenterProperties.PROPERTY_AMI_CENTER_PORT_WHITELIST);

		MsgDirectConnectionConfiguration config = new MsgDirectConnectionConfiguration("ami_center");
		MsgDirectTopicConfiguration centerToRelayConfig = new MsgDirectTopicConfiguration("center.to.relay", port).setServerBindAddress(portBindAddr)
				.setServerSocketEntitlements(entitlements);
		MsgDirectTopicConfiguration relayToCenterConfig = new MsgDirectTopicConfiguration("relay.to.center", port).setServerBindAddress(portBindAddr)
				.setServerSocketEntitlements(entitlements);

		final String store = props.getOptional(AmiCenterProperties.PROPERTY_AMI_CENTER_SSL_KEYSTORE_FILE);
		if (store != null) {
			final int sslPort = props.getRequired(AmiCenterProperties.PROPERTY_AMI_CENTER_SSL_PORT, Integer.class);
			final String pass = props.getRequired(AmiCenterProperties.PROPERTY_AMI_CENTER_SSL_KEYSTORE_PASS);
			config.setKeystore(new File(store), pass);
			centerToRelayConfig.setSslPort(sslPort);
			relayToCenterConfig.setSslPort(sslPort);
		}
		AmiCenterClient acc = new AmiCenterClient("!!AMI_CENTER!!", container);
		MutableCalcFrame amiScriptProperties = new MutableCalcFrame();
		getVarsFromProperties(props, amiScriptProperties);
		MsgDirectConnection connection = new MsgDirectConnection(config);

		connection.addTopic(centerToRelayConfig);
		connection.addTopic(relayToCenterConfig);
		connection.addTopic(new MsgDirectTopicConfiguration("center.to.web", port).setServerBindAddress(portBindAddr).setServerSocketEntitlements(entitlements));
		connection.addTopic(new MsgDirectTopicConfiguration("web.to.center", port).setServerBindAddress(portBindAddr).setServerSocketEntitlements(entitlements));

		File fountainDir = props.getOptional(AmiCenterProperties.PROPERTY_IDFOUNTAIN_PATH, new File("data/idfountain"));
		int fountainBatchsize = props.getOptional(AmiCenterProperties.PROPERTY_IDFOUNTAIN_BATCHSIZE, 1000000);
		IOH.ensureDir(fountainDir);
		Factory fountain = new BatchLongSequenceIdGenerator.Factory(new FileBackedLongSequenceIdGenerator.Factory(fountainDir), fountainBatchsize);
		container.getServices().setUidGenerator(new BasicNamespaceIdGenerator<Long>(fountain));
		container.getServices().getUidGenerator("EYE").createNextId();//skip first
		AmiImdbSessionManagerService sessionManager = new AmiImdbSessionManagerService(container.getTools());

		//load service resolver
		AmiNamingServiceResolverHelper.loadServiceResolver(container, props);

		AmiCenterSuite amiCenterSuite = new AmiCenterSuite(connection);
		container.getRootSuite().addChild(amiCenterSuite);
		AmiCenterResourcesManager resourceManager = new AmiCenterResourcesManager(container.getTools());
		AmiCenterTimerProcessor timerProcessor = new AmiCenterTimerProcessor();
		amiCenterSuite.addAmiCenterProcessor(timerProcessor, 0);
		OutputPort<Message> timerPort = amiCenterSuite.exposeInputPortAsOutput(timerProcessor.getInputPort(), true);

		// custom objects
		Map<String, AmiScriptClassPluginWrapper> amiScriptPlugins = new HashMap<String, AmiScriptClassPluginWrapper>();
		AmiCenterPluginHelper.initAmiScriptPlugins(container.getTools(), amiScriptPlugins);

		AmiCenterState state = new AmiCenterState(resourceManager, sessionManager, container.getTools(), timerPort, amiCenterSuite.getItineraryProcessor(), amiScriptProperties,
				amiScriptPlugins);
		resourceManager.start();
		container.getPartitionController().putState(AmiCenterSuite.PARTITIONID_AMI_CENTER, state);
		state.setNamingServiceResolver((AmiChainedNamingServiceResolver) AmiNamingServiceResolverHelper.getService(container));
		state.getDatasourceManager().setMaxConcurrentDsQueriesPerUser(props.getOptional(AmiCenterProperties.PROPERTY_AMI_DATSOURCE_CONCURRENT_QUERIES_PER_USER, 5));
		state.getPartition().setLockForWriteDefaultTimeoutMillis(props.getOptional(AmiCenterProperties.PROPERTY_AMI_DB_WRITE_LOCK_PREFERENCE, 1000));

		{
			AmiCenterReplicationObjectsProcessor replicationProcessor = new AmiCenterReplicationObjectsProcessor();
			replicationProcessor.bindToPartition(AmiCenterSuite.PARTITIONID_AMI_CENTER);
			amiCenterSuite.addChild(replicationProcessor);
			AmiCenterReplicationDisconnectedProcessor disconnectProcessor = new AmiCenterReplicationDisconnectedProcessor();
			disconnectProcessor.bindToPartition(AmiCenterSuite.PARTITIONID_AMI_CENTER);
			amiCenterSuite.addChild(disconnectProcessor);
			AmiCenterReplicationConnectedProcessor connectProcessor = new AmiCenterReplicationConnectedProcessor();
			connectProcessor.bindToPartition(AmiCenterSuite.PARTITIONID_AMI_CENTER);
			amiCenterSuite.addChild(connectProcessor);
			InputPort<AmiCenterReplicationObjectsMessage> oPort = amiCenterSuite.exposeInputPort(replicationProcessor);
			InputPort<AmiCenterReplicationDisconnectedMessage> dPort = amiCenterSuite.exposeInputPort(disconnectProcessor);
			InputPort<AmiCenterReplicationConnectedMessage> cPort = amiCenterSuite.exposeInputPort(connectProcessor);
			state.getAmiImdb().getReplicator().setToReplicationPort(oPort, dPort, cPort);
		}

		AmiCenterStartupProcessor startupProcessor;
		OutputPort<Message> startupPort = amiCenterSuite.exposeInputPortAsOutput(amiCenterSuite.addAmiCenterProcessor(startupProcessor = new AmiCenterStartupProcessor(), 0), true);
		state.setToClientsPort(startupProcessor.getToClientsPort());
		amiCenterSuite.addAmiCenterProcessor(new AmiCenterRelayConnectionStatusProcessor(), AmiCenterSuite.RECEIVE_STATUS_FROM_AGENT);
		AmiCenterRelayOnConnectResponseProcessor onConnectProcessor = amiCenterSuite.addAmiCenterProcessor(new AmiCenterRelayOnConnectResponseProcessor(),
				AmiCenterSuite.RECEIVE_FROM_AGENT);
		AmiCenterRelayGetSnapshotResponseProcessor getSnapshotProcessor = amiCenterSuite.addAmiCenterProcessor(new AmiCenterRelayGetSnapshotResponseProcessor(),
				AmiCenterSuite.RECEIVE_FROM_AGENT);
		amiCenterSuite.wire(onConnectProcessor.responsePort, getSnapshotProcessor, true);

		amiCenterSuite.addAmiCenterProcessor(new AmiCenterClientSnapshotRequestProcessor(), AmiCenterSuite.RECEIVE_FROM_CLIENT);
		amiCenterSuite.addAmiCenterProcessor(new AmiCenterClientStatusRequestProcessor(), AmiCenterSuite.RECEIVE_FROM_CLIENT);
		AmiCenterRelayChangesProcessor changesProcessor = amiCenterSuite.addAmiCenterProcessor(new AmiCenterRelayChangesProcessor(), AmiCenterSuite.RECEIVE_FROM_AGENT);

		amiCenterSuite.wire(changesProcessor.ackPort, amiCenterSuite.getPortToRelay(), true);
		amiCenterSuite.wire(getSnapshotProcessor.ackPort, amiCenterSuite.getPortToRelay(), true);

		AmiCenterResourceProcessor resourceProcessor = new AmiCenterResourceProcessor(resourceManager);
		amiCenterSuite.addChild(resourceProcessor);
		amiCenterSuite.wire(amiCenterSuite.getClientRoutingProcessor().newRequestOutputPort(AmiCenterGetResourceRequest.class, AmiCenterGetResourceResponse.class),
				resourceProcessor, true);
		amiCenterSuite.addItinerary(AmiCenterManageDatasourceRequest.class, AmiCenterManageDatasourceItinerary.class, AmiCenterSuite.RECEIVE_FROM_CLIENT);
		//		amiCenterSuite.addItinerary(AmiCenterManageAmiDataRequest.class, AmiCenterManageAmiDataItinerary.class, AmiCenterSuite.RECEIVE_FROM_CLIENT);
		//		amiCenterSuite.addItinerary(AmiCenterGetAmiSchemaRequest.class, AmiCenterGetAmiSchemaRequestItinerary.class, AmiCenterSuite.RECEIVE_FROM_CLIENT);
		amiCenterSuite.addItinerary(AmiCenterPassToRelayRequest.class, AmiCenterClientToAgentPassthroughItinerary.class, AmiCenterSuite.RECEIVE_FROM_CLIENT);

		{
			AmiCenterParseQueryProcessor preprocessor = new AmiCenterParseQueryProcessor(sessionManager);
			amiCenterSuite.addAmiCenterProcessor(preprocessor, AmiCenterSuite.RECEIVE_FROM_CLIENT);
			preprocessor.bindToPartition("AMI_PREPROCESSOR");
			AmiCenterQueryDsToItineraryProcessor processor = new AmiCenterQueryDsToItineraryProcessor();
			processor.setName("Itinerary_AmiCenterQueryDsItinerary");
			amiCenterSuite.addAmiCenterProcessor(processor, 0);
			processor.bindToPartition(AmiCenterSuite.PARTITIONID_AMI_CENTER + "RQ");
			amiCenterSuite.wire(preprocessor.getForward(), processor, true);
		}

		amiCenterSuite.addItinerary(AmiCenterManageResourcesRequest.class, AmiCenterManagerResourcesItinerary.class, AmiCenterSuite.RECEIVE_FROM_CLIENT);

		AmiCenterMonitorProcessor monitorProcessor = new AmiCenterMonitorProcessor();
		amiCenterSuite.addAmiCenterProcessor(monitorProcessor, 0);
		amiCenterSuite.wire(startupProcessor.onStartedPort, monitorProcessor, true);
		amiCenterSuite.wire(monitorProcessor.loopback, monitorProcessor, true);
		amiCenterSuite.wire(monitorProcessor.toEyePort, amiCenterSuite.getClientRoutingProcessor(), true);
		AmiHdbRtEventProcessor rtEventProcessor = new AmiHdbRtEventProcessor();
		amiCenterSuite.addChild(rtEventProcessor);
		final OutputPort<AmiRelayObjectMessage> rtEventOutputPort = amiCenterSuite.exposeInputPortAsOutput(rtEventProcessor, true);
		startupPort.send(container.nw(Message.class), null);
		AmiCenterStartupProcessor.initAmi(state, container.getTools());
		state.getHdb().setRtEventPort(rtEventOutputPort);
		OutputPort<Message> input = amiCenterSuite.exposeInputPortAsOutput(amiCenterSuite.getClientRoutingProcessor(), true);
		RequestOutputPort<AmiCenterRequest, AmiCenterResponse> itineraryPort = amiCenterSuite.getFromClientRequestPort();

		AmiAuthenticatorPlugin aa = container.getServices().getService(AmiConsts.SERVICE_AUTH, AmiAuthenticatorPlugin.class);
		AmiAuthenticatorPlugin jdbcaa = container.getServices().getService(AmiConsts.SERVICE_AUTH_JDBC, AmiAuthenticatorPlugin.class);
		Map<String, AmiDbDialectPlugin> amiDbDialects = new HashMap<String, AmiDbDialectPlugin>();
		initDbDialectPlugins(container.getTools(), amiDbDialects);
		final int jdbcPort = props.getRequired(AmiCenterProperties.PROPERTY_AMI_DB_JDBC_PORT, Integer.class);
		final int jdbcSslPort = props.getOptional(AmiCenterProperties.PROPERTY_AMI_DB_JDBC_SSL_PORT, -1);
		if (jdbcPort > 0) {
			String jdbcPortBindAddr = props.getOptional(AmiCenterProperties.PROPERTY_AMI_DB_JDBC_PORT_BINDADDR);
			int jdbcVersion = props.getOptional(AmiCenterProperties.PROPERTY_AMI_DB_JDBC_PROTOCOL_VERSION, 2);
			final ServerSocketEntitlements sse = AmiUtils.parseWhiteList(container.getTools(), props, AmiCenterProperties.PROPERTY_AMI_DB_JDBC_PORT_WHITELIST);
			FastThreadPool jdbcExecutor = new FastThreadPool(100, "AMIJDBC");
			jdbcExecutor.start();
			SimpleMessagingServer sms = new SimpleMessagingServer("AMI JDBC", jdbcPortBindAddr, sse, jdbcPort,
					new AmiCenterJdbcServer(jdbcaa, itineraryPort, amiDbDialects, jdbcVersion), container.getServices().getConverter(), jdbcExecutor, jdbcVersion);
			System.out.println("To access this AMIDB via JDBC: jdbc:amisql:" + EH.getLocalHost() + ":" + jdbcPort + "?username=...&password=...");
			sms.start();
		}
		if (jdbcSslPort > 0) {
			String jdbcSslPortBindAddr = props.getOptional(AmiCenterProperties.PROPERTY_AMI_DB_JDBC_SSL_PORT_BINDADDR);
			int jdbcVersion = props.getOptional(AmiCenterProperties.PROPERTY_AMI_DB_JDBC_PROTOCOL_VERSION, 2);
			final ServerSocketEntitlements sse = AmiUtils.parseWhiteList(container.getTools(), props, AmiCenterProperties.PROPERTY_AMI_DB_JDBC_PORT_WHITELIST);
			final File jdbcSslKeystore = new File(props.getRequired(AmiCenterProperties.PROPERTY_AMI_DB_JDBC_SSL_KEYSTORE_FILE, String.class));
			final String jdbcSslKeystorePass = props.getRequired(AmiCenterProperties.PROPERTY_AMI_DB_JDBC_SSL_KEYSTORE_PASS, String.class);
			FastThreadPool jdbcExecutor = new FastThreadPool(100, "AMIJDBC");
			jdbcExecutor.start();
			SimpleMessagingServer sms = new SimpleMessagingServer("AMI JDBC", jdbcSslPortBindAddr, sse, jdbcSslPort,
					new AmiCenterJdbcServer(jdbcaa, itineraryPort, amiDbDialects, jdbcVersion), container.getServices().getConverter(), jdbcExecutor, jdbcVersion, jdbcSslKeystore,
					jdbcSslKeystorePass);
			System.out.println("To access this AMIDB via JDBC: jdbc:amisql:" + EH.getLocalHost() + ":" + jdbcSslPort + "?username=...&password=...");
			sms.start();
		}
		final int amiConsolePort = props.getRequired(AmiCenterProperties.PROPERTY_AMI_DB_CONSOLE_PORT, Integer.class);
		if (amiConsolePort > 0) {
			final String amiConsolePortBindAddr = props.getOptional(AmiCenterProperties.PROPERTY_AMI_DB_CONSOLE_PORT_BINDADDR);
			final ServerSocketEntitlements sse = AmiUtils.parseWhiteList(container.getTools(), props, AmiCenterProperties.PROPERTY_AMI_DB_CONSOLE_PORT_WHITELIST);
			AmiCenterConsole console = new AmiCenterConsole(new SimpleExecutor(new NamedThreadFactory("AmiConsole", true)), amiConsolePortBindAddr, sse, amiConsolePort, state,
					container.getTools(), itineraryPort, aa, amiDbDialects);
			console.start();
		}
		cb.startupContainer(container);
		AmiProcessStatsLogger.INSTANCE.registerBootstrap(container);
		AmiRestServer restServer = AmiRestServer.get(container);
		if (restServer != null) {
			AmiRestPlugin_Query plugin = restServer.getPlugin(AmiRestPlugin_Query.class);
			if (plugin != null)
				plugin.setItineraryPort(itineraryPort);
		}
		return container.getDispatchController();
	}

	private static void getVarsFromProperties(PropertyController props, MutableCalcFrame amiScriptProperties) {
		PropertyController properties = props.getSubPropertyController(AmiCenterProperties.PREFIX_AMISCRIPT_VARIABLE);
		for (String varName : properties.getKeys()) {
			String value = properties.getRequired(varName);
			Tuple2<Class<?>, Object> r = AmiUtils.toAmiscriptVariable(value, "Property amiscript.db.variable.", varName);
			if (!AmiUtils.isValidVariableName(varName, false, false))
				throw new RuntimeException("Invalid variable name: " + varName);
			amiScriptProperties.putTypeValue(varName, r.getA(), r.getB());
		}
	}
	public static void initDbDialectPlugins(ContainerTools tools, Map<String, AmiDbDialectPlugin> sink) {
		String classNames = tools.getOptional(AmiCenterProperties.PROPERTY_AMI_DB_DIALECT_PLUGINS);
		if (SH.is(classNames)) {
			try {
				for (String clazz : SH.split(',', classNames)) {
					StringBuilder errorSink = new StringBuilder();
					clazz = SH.trim(clazz);
					AmiDbDialectPlugin plugin = AmiUtils.loadPlugin(clazz, "DB Dialect Plugin", tools, tools, AmiDbDialectPlugin.class, errorSink);
					if (plugin == null)
						throw new RuntimeException("Error loading " + clazz + ": " + errorSink.toString());
					CH.putOrThrow(sink, plugin.getPluginId(), plugin, "DB Dialect Plugin Class Id");
				}
			} catch (Exception e) {
				throw new RuntimeException("Error Processing property: " + AmiCenterProperties.PROPERTY_AMI_DB_DIALECT_PLUGINS + "=" + classNames, e);
			}
		}

	}
}
