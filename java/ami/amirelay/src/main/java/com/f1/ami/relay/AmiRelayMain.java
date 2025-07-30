package com.f1.ami.relay;

import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiCenterDefinition;
import com.f1.ami.amicommon.AmiChainedNamingServiceResolver;
import com.f1.ami.amicommon.AmiDatasourcePlugin;
import com.f1.ami.amicommon.AmiNamingServiceResolverHelper;
import com.f1.ami.amicommon.AmiProcessStatsLogger;
import com.f1.ami.amicommon.AmiStartup;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiRelayGetSnapshotRequest;
import com.f1.ami.amicommon.msg.AmiRelayMessage;
import com.f1.ami.amicommon.msg.AmiRelayOnConnectRequest;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;
import com.f1.ami.amicommon.msg.AmiRelayRunDbRequest;
import com.f1.ami.amicommon.msg.AmiRelaySendEmailRequest;
import com.f1.base.Password;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.ContainerConstants;
import com.f1.container.OutputPort;
import com.f1.container.impl.BasicContainer;
import com.f1.container.impl.BasicPartition;
import com.f1.email.EmailClient;
import com.f1.email.EmailClientConfig;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.concurrent.NamedThreadFactory;

public class AmiRelayMain {

	private static final int DEFAULT_AMI_PORT = 3289;
	public static final String SERVICE_EMAILCLIENT = "EMAILCLIENT";
	public static final String SERVICE_EMAILCLIENTCONFIG = "EMAILCLIENTCONFIG";
	private static Logger log = LH.get();

	public static void main(String[] a) throws Exception {
		ContainerBootstrap bs = new ContainerBootstrap(AmiRelayMain.class, a);
		bs.setProperty("f1.appname", "AmiRelay");
		bs.setProperty("f1.logfilename", "AmiRelay");
		bs.setProperty("f1.autocoded.disabled", "true");
		bs.setProperty("f1.threadpool.agressive", "false");
		bs.setProperty("ami.relay.invokables", "com.f1.ami.relay.plugins.AmiRelayInvokablePlugin_LoadFile,com.f1.ami.relay.plugins.AmiRelayInvokablePlugin_Replay");
		bs.setProperty("ami.relay.fh.active", "ssocket");
		bs.setProperty("ami.relay.fh.ssocket.start", "true");
		bs.setProperty("ami.relay.fh.ssocket.class", "com.f1.ami.relay.fh.AmiServerSocketFH");
		bs.setProperty("ami.relay.fh.ssocket.props.amiId", "Server_Socket");
		AmiStartup.startupAmi(bs, "ami_amirelay");
		main2(bs);
	}

	public static void main2(ContainerBootstrap cb) throws Exception {

		PropertyController props = cb.getProperties();

		int vamport = props.getOptional(AmiRelayProperties.OPTION_AMI_PORT, DEFAULT_AMI_PORT);

		final BasicContainer container = new BasicContainer();
		container.setName("AmiRelay");
		container.getThreadPoolController().setMaximumPoolSizeForGeneralThreadPool(256);

		container.getThreadPoolController().setUseAggressiveThreadPool(false);
		cb.prepareContainer(container);
		AmiNamingServiceResolverHelper.loadServiceResolver(container, props);
		container.getPartitionController()
				.putPartition(new BasicPartition(container, AmiRelaySuite.PARTITIONID_AMI_RELAY, ContainerConstants.DEFAULT_THREAD_POOL_KEY, container.getTools().getNow(), 0));
		AmiRelayCenterDefinition[] centers = AmiRelayCenterDefinition.wrap(AmiCenterDefinition.parse(container.getTools()));
		AmiRelayState amiRelayState = new AmiRelayState(container.getTools(), centers);
		AmiRelayMonitorFilesState mfState = new AmiRelayMonitorFilesState(amiRelayState.getTransformManager(), amiRelayState.getRouter());
		AmiRelayTransformState amiRelayTransformState = new AmiRelayTransformState(amiRelayState.getTransformManager());

		final AmiRelaySuite amiRelaySuite = new AmiRelaySuite(null, centers.length, amiRelayState.getRouter());
		for (AmiRelayCenterDefinition center : centers) {
			MsgDirectConnection connection = center.newMsgDirectConnection("ami_relay", "center.to.relay", "relay.to.center");
			amiRelaySuite.addCenterConnection(connection, center.getId());
			amiRelayState.getJournal().addCenter(center.getId());
		}

		String emailHost = props.getOptional(AmiRelayProperties.OPTION_EMAIL_CLIENT_HOST);
		if (emailHost != null) {
			try {
				EmailClientConfig config = new EmailClientConfig();
				config.setEnableAuthentication(props.getOptional(AmiRelayProperties.OPTION_EMAIL_CLIENT_AUTHENTICATION_ENABLED, Boolean.TRUE));
				config.setEnableDebug(props.getOptional(AmiRelayProperties.OPTION_EMAIL_CLIENT_DEBUG_ENABLED, Boolean.FALSE));
				config.setEnableSSL(props.getOptional(AmiRelayProperties.OPTION_EMAIL_CLIENT_SSL_ENABLED, Boolean.TRUE));
				config.setEnableStartTLS(props.getOptional(AmiRelayProperties.OPTION_EMAIL_CLIENT_START_TLS_ENABLED, Boolean.TRUE));
				config.setSslProtocols(props.getOptional(AmiRelayProperties.OPTION_EMAIL_CLIENT_SSL_PROTOCOLS, ""));
				int connectionTimeout = props.getOptional(AmiRelayProperties.OPTION_EMAIL_CLIENT_CONNECTION_TIMEOUT, EmailClientConfig.DEFAULT_CONNECTION_TIMEOUT);
				config.setConnectionTimeout(connectionTimeout);
				int timeout = props.getOptional(AmiRelayProperties.OPTION_EMAIL_CLIENT_TIMEOUT,EmailClientConfig.DEFAULT_TIMEOUT);
				config.setTimeout(timeout);
				int writeTimeout = props.getOptional(AmiRelayProperties.OPTION_EMAIL_CLIENT_WRITE_TIMEOUT,EmailClientConfig.DEFAULT_WRITE_TIMEOUT);
				config.setWriteTimeout(writeTimeout);
				config.setHost(emailHost);
				config.setPort(props.getRequired(AmiRelayProperties.OPTION_EMAIL_CLIENT_PORT, Integer.class));
				config.setRetriesCount(props.getOptional(AmiRelayProperties.OPTION_EMAIL_CLIENT_RETRIES_COUNT, 3));
				config.setPassword(Password.valueOf(props.getOptional(AmiRelayProperties.OPTION_EMAIL_CLIENT_PASSWORD)));
				config.setUsername(props.getOptional(AmiRelayProperties.OPTION_EMAIL_CLIENT_USENAME));
				container.getServices().putService(SERVICE_EMAILCLIENTCONFIG, config);
				if (config.getPassword() != null || config.getUsername() != null) {
					EmailClient emailclient = new EmailClient(config, false);
					container.getServices().putService(SERVICE_EMAILCLIENT, emailclient);
					boolean exitOnError = props.getOptional(AmiRelayProperties.OPTION_EMAIL_CLIENT_EXIT_ON_ERROR, Boolean.TRUE);
					try {
						emailclient.reconnect();
					} catch (Exception e) {
						if (exitOnError)
							throw new RuntimeException(
									"Exiting because emailclient connection. Set " + AmiRelayProperties.OPTION_EMAIL_CLIENT_EXIT_ON_ERROR + "=false to continue on error", e);
						else
							LH.warning(log, "Emailclient connection error. ", e);
					}
				}
			} catch (Exception e) {
				LH.warning(log, "SMTP (Email) initalization failed: ", e);
				throw e;
			}
		}
		String relayRootFile = container.getTools().getOptional(AmiRelayProperties.OPTION_AMI_RELAY_PLUGIN_RESOURCE_ROOT_DIR);
		if (SH.is(relayRootFile))
			AmiUtils.logSecurityWarning(AmiRelayProperties.OPTION_AMI_RELAY_PLUGIN_RESOURCE_ROOT_DIR
					+ " has been set meaning files under that directory recursively can be accessed by users of relay realtime interface");

		container.getRootSuite().addChild(amiRelaySuite);

		amiRelaySuite.addAmiRelayProcessor(new AmiRelayCenterConnectionStatusProcessor(), AmiRelaySuite.RECEIVE_STATUS_FROM_EYE);
		amiRelaySuite.addAmiRelayProcessor(new AmiRelayAckMessageProcessor(), AmiRelaySuite.RECEIVE_FROM_EYE);

		amiRelaySuite.addItinerary(AmiRelayOnConnectRequest.class, AmiRelayCenterOnConnectItinerary.class, AmiRelaySuite.RECEIVE_FROM_EYE);
		amiRelaySuite.addItinerary(AmiRelayGetSnapshotRequest.class, AmiRelayCenterSnapshotItinerary.class, AmiRelaySuite.RECEIVE_FROM_EYE);
		amiRelaySuite.addItinerary(AmiRelayRunAmiCommandRequest.class, AmiRelayCenterRunAmiCommandItinerary.class, AmiRelaySuite.RECEIVE_FROM_EYE);
		amiRelaySuite.addItinerary(AmiRelayRunDbRequest.class, AmiRelayCenterRunDbItinerary.class, AmiRelaySuite.RECEIVE_FROM_EYE);
		amiRelaySuite.addItinerary(AmiRelaySendEmailRequest.class, AmiRelayCenterSendEmailItinerary.class, AmiRelaySuite.RECEIVE_FROM_EYE);

		AmiRelayAmiProcessor amiProcessor = amiRelaySuite.addAmiRelayProcessor(new AmiRelayAmiProcessor(), 0);
		AmiRelayTransformProcessor amiTransformProcessor = new AmiRelayTransformProcessor();
		amiRelaySuite.addChild(amiTransformProcessor);
		amiRelaySuite.wire(amiTransformProcessor.out, amiProcessor, true);
		amiTransformProcessor.bindToPartition("RELAY_TRANSFORM");
		OutputPort<AmiRelayMessage> toAmiPort = amiRelaySuite.exposeInputPortAsOutput(amiProcessor, true);
		OutputPort<AmiRelayMessage> toAmiTransformsPort = amiRelaySuite.exposeInputPortAsOutput(amiTransformProcessor, true);
		AmiRelayServer amiServer = new AmiRelayServer(vamport, new NamedThreadFactory("AMISOCKET", false), container.getServices().getGenerator(), toAmiTransformsPort, toAmiPort,
				amiRelayState, centers);
		amiRelayState.setAmiServer(amiServer);
		container.getPartitionController().putState(AmiRelaySuite.PARTITIONID_AMI_RELAY, amiRelayState);
		container.getPartitionController().putState("RELAY_TRANSFORM", amiRelayTransformState);
		container.getPartitionController().putState("RELAY_FILE_MONITOR", mfState);
		AmiRelayMonitorFilesProcessor mfProcessor = new AmiRelayMonitorFilesProcessor(amiRelayState.getCheckConfigFilesChangedPeriodMs());
		amiRelaySuite.addChild(mfProcessor);
		amiRelaySuite.wire(mfProcessor.self, mfProcessor.getInputPort(), true);
		mfProcessor.bindToPartition("RELAY_FILE_MONITOR");
		amiRelayState.getTransformManager().parseIfChanged(true);
		amiRelayState.getRouter().parseIfChanged(true);
		amiServer.start();
		Map<String, AmiDatasourcePlugin> amiDatasourcePlugins = AmiUtils.loadPlugins(container.getTools(), AmiRelayProperties.OPTION_AMI_DATASOURCES, "Ami Datasource Plugin",
				AmiDatasourcePlugin.class);
		for (AmiDatasourcePlugin i : amiDatasourcePlugins.values())
			amiRelayState.getDatasourceManager().addAmiDatasourcePlugin(i);
		amiRelayState.setNamingServiceResolver((AmiChainedNamingServiceResolver) AmiNamingServiceResolverHelper.getService(container));
		cb.registerConsoleObject("amiRelayServer", new AmiRelayConsole(amiRelayState));
		cb.startupContainer(container);
		AmiProcessStatsLogger.INSTANCE.registerBootstrap(container);
	}
}
