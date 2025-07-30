package com.f1.oms3f.start.oms;

import java.io.File;
import java.util.Locale;
import java.util.TimeZone;

import com.f1.base.Action;
import com.f1.base.Clock;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.Container;
import com.f1.container.ContainerServices;
import com.f1.container.PartitionResolver;
import com.f1.container.Suite;
import com.f1.container.impl.BasicContainer;
import com.f1.container.impl.BasicPartitionResolver;
import com.f1.container.impl.ContainerHelper;
import com.f1.container.impl.RequestPartitionResolver;
import com.f1.containerpersist.ContainerPersister;
import com.f1.fix.oms.OmsConsole;
import com.f1.fix.oms.OmsPluginManager;
import com.f1.fix.oms.OmsSuite;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.FixMsgPartitionResolver;
import com.f1.fix.oms.adapter.FixMsgProcessor;
import com.f1.fixomsclient.OmsClientSuite;
import com.f1.msg.impl.BasicMsgConnectionConfiguration;
import com.f1.msg.impl.MsgConsole;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.msgdirect.MsgDirectTopicConfiguration;
import com.f1.oms3f.start.F1IdGenerator;
import com.f1.persist.sinks.FilePersister;
import com.f1.pofo.oms.OmsClientAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.povo.standard.TextMessage;
import com.f1.qfix.FixEvent;
import com.f1.qfix.QfixConnector;
import com.f1.qfix.QfixConsole;
import com.f1.qfix.QfixInboundProcessor;
import com.f1.qfix.QfixOutboundProcessor;
import com.f1.qfix.msg.FixMsgConnection;
import com.f1.suite.utils.ClassRoutingProcessor;
import com.f1.suite.utils.MulticastProcessor;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.utils.DBH;
import com.f1.utils.GuidHelper;
import com.f1.utils.IOH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.PropertyController;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.db.Database;
import com.f1.utils.ids.BasicNamespaceIdGenerator;
import com.f1.utils.ids.BatchIdGenerator;
import com.f1.utils.ids.FileBackedIdGenerator;

/*
 * -Djava.util.logging.manager=com.f1.speedlogger.sun.SunSpeedLoggerLogManager
 *
 * Optional:
 * -Dproperty.omsdb.url=<url to db>
 */
public class StartOms3fMain {

	private static final String REFDATA = "REFDATA";
	private static final String TOOFR = "TOOFR";
	private static final String TOFRONTEND = "TOFRONTEND";
	private static final String OMS_CLIENTFIX = "oms.clientfix";
	private static final String OMS_OFR_ADMIN_REQUEST = "oms.ofr.admin.request";
	private static final String REFDATA_SERVER_TO_CLIENT = "refdata.serverToClient";
	private static final String REFDATA_CLIENT_TO_SERVER = "refdata.clientToServer";
	private static final String OMS_OFR_RESPONSE = "oms.ofr.response";
	private static final String OFR_OMS_REQUEST = "ofr.oms.request";
	private static final String FE_OFR_INCOMING = "fe.ofr.incoming";
	private static final String FE_OFR_OUTGOING = "fe.ofr.outgoing";
	private static final String FE_ADMIN_INCOMING = "fe.admin.incoming";
	private static final String FE_DELTAS_OUTGOING = "fe.deltas.outgoing";
	private static final String FE_SNAPSHOT_RESPONSE = "fe.snapshot.response";
	private static final String FE_SNAPSHOT_REQUEST = "fe.snapshot.request";
	private static final String FIX = "FIX";
	private static final String TODAYS_IDS = ".todays_ids";
	private static final String START_OMS = "START_OMS";
	public static final String OPTION_PERSIST_DIR = "persist.dir";
	public static final String OPTION_PERSIST_CLEAN = "persist.clean";
	public static final String OPTION_PERSIST_BUFSIZE = "persist.bufsize";
	public static final String OPTION_IDFOUNTAIN_BATCHSIZE = "idfountan.batchsize";
	public static final String OPTION_IDFOUNTAIN_DIR = "idfountain.dir";
	public static final String OPTION_OMSDB_URL = "omsdb.url";
	public static final String OPTION_OMSDB_PASSWORD = "omsdb.password";
	public static final String OPTION_QFIX_CONFIG = "qfix.config.file";
	public static final String OPTION_EXHAUST_PORT = "oms.port";
	public static final String OPTION_REFDATA_PORT = "refdata.port";
	public static final String OPTION_SYSTEM_NAME = "systemname";
	public static final String OPTION_SQL_DIR = "sql.dir";

	public static void main(String... args) throws Exception {

		// /////////////////////////////////////
		// //////////////// INIT ///////////////

		// Bootstraping...
		final ContainerBootstrap cam = new ContainerBootstrap(StartOms3fMain.class, args);
		cam.setConfigDirProperty("./src/main/config");
		//cam.setLoggingOverrideProperty("warning");
		//cam.setLogPerformanceLevel(Level.INFO);
		final PropertyController props = cam.getProperties();
		cam.startup();

		// Container
		Container mycontainer = new BasicContainer();
		mycontainer.setName(START_OMS);

		// pass the container through the builder...
		cam.prepareContainer(mycontainer);

		// /////////////////////////////////////
		// ///////////// SERVICES //////////////

		// Timing
		ContainerServices services = mycontainer.getServices();
		Clock clock = services.getClock();
		Locale locale = cam.getLocale();
		TimeZone timeZone = cam.getTimeZone();
		String systemName = props.getRequired(OPTION_SYSTEM_NAME);
		LocaleFormatter formatter = mycontainer.getServices().getLocaleFormatterManager().getThreadSafeLocaleFormatter(locale, timeZone);

		String dburl = props.getRequired(OPTION_OMSDB_URL);
		boolean hasDb = SH.is(dburl);
		if (hasDb) {
			Database dbsource = DBH.createPooledDataSource(dburl, props.getRequired(OPTION_OMSDB_PASSWORD));
			OmsUtils.setOmsDb(mycontainer, dbsource);
		}

		// Id fountain
		int idFountainBatchsize = props.getOptional(OPTION_IDFOUNTAIN_BATCHSIZE, 1000);
		File idDirectory = props.getRequired(OPTION_IDFOUNTAIN_DIR, File.class);
		IOH.ensureDir(idDirectory);

		// erase old id files
		int today = Integer.parseInt(formatter.getDateFormatter(LocaleFormatter.DATE).get(clock.getNowDate()));
		for (File file : idDirectory.listFiles()) {
			String name = file.getName();
			if (name.endsWith(TODAYS_IDS) && Integer.parseInt(SH.stripSuffix(name, TODAYS_IDS, true)) < today)
				IOH.delete(file);
		}
		BatchIdGenerator.Factory<Long> fountain = new BatchIdGenerator.Factory<Long>(new FileBackedIdGenerator.Factory(idDirectory), idFountainBatchsize);

		mycontainer.getServices().setTicketGenerator(
				new F1IdGenerator(formatter.getDateFormatter(LocaleFormatter.DATE), clock, systemName, new BasicNamespaceIdGenerator<Long>(fountain)));
		mycontainer.getServices().setUidGenerator(new BasicNamespaceIdGenerator<Long>(fountain));

		// Replication
		File persistDir = props.getOptional(OPTION_PERSIST_DIR, File.class);
		if (persistDir != null) {
			boolean persistClean = props.getOptional(OPTION_PERSIST_CLEAN, Boolean.FALSE);
			long persistMaxDeltaBufferSize = props.getOptional(OPTION_PERSIST_BUFSIZE, FilePersister.DEFAULT_MAX_DELTA_SIZE);
			if (persistClean)
				IOH.deleteForce(persistDir);
			ContainerPersister cp = new ContainerPersister(mycontainer.getPersistenceController());
			cp.addFileReplication(persistDir, false, persistMaxDeltaBufferSize);
			RH.setField(RH.getField(cp, "filePersist"), "maxDeltaSize", Integer.MAX_VALUE);
		}
		PartitionResolver<FixEvent> resolver = new BasicPartitionResolver<FixEvent>(FixEvent.class, FIX);

		FixMsgPartitionResolver fixmsgresolver = new FixMsgPartitionResolver();
		mycontainer.addListener(fixmsgresolver);
		// create and add three processors
		Suite rootSuite = mycontainer.getRootSuite();
		QfixConnector processor = new QfixConnector(props);
		QfixInboundProcessor fixadapter = new QfixInboundProcessor();
		QfixOutboundProcessor fixsender = new QfixOutboundProcessor();
		FixMsgProcessor fixprocessor = new FixMsgProcessor();
		OmsSuite oms = new OmsSuite(props);
		rootSuite.addChildren(processor, fixadapter, fixsender, fixprocessor, oms);
		rootSuite.wire(processor.output, fixadapter.getInputPort(), true);
		rootSuite.wire(fixadapter.output, fixprocessor.getInputPort(), true);
		rootSuite.wire(fixprocessor.toOms, oms.fromFix, false);
		rootSuite.wire(oms.toFix, fixsender, false);
		rootSuite.wire(fixsender.reject, fixprocessor.getInputPort(), true);

		PartitionResolver<OmsClientAction> ocaResolver = new OmsClientActionResolver();
		RequestPartitionResolver<OmsClientAction> ocaRequestResolver = new RequestPartitionResolver<OmsClientAction>(ocaResolver);
		MulticastProcessor<OmsNotification> toOmsClientMulticast = new MulticastProcessor<OmsNotification>(OmsNotification.class);
		rootSuite.addChildren(toOmsClientMulticast);
		rootSuite.wire(oms.toOMSClient, toOmsClientMulticast, false);

		if (hasDb) {
			OmsClientSuite omsClientSuite = new OmsClientSuite(true);
			rootSuite.addChildren(omsClientSuite);
			OmsToDbProcessor toDbProcessor = new OmsToDbProcessor();
			rootSuite.addChildren(toDbProcessor);
			rootSuite.wire(omsClientSuite.clientNotificationOutputPort, toDbProcessor, false);
			rootSuite.wire(toDbProcessor.snapshotRequest, omsClientSuite.snapshotRequestInputPort, true);
			rootSuite.wire(toOmsClientMulticast.newOutputPort(), omsClientSuite.notificationInputPort, true);
			rootSuite.wire(omsClientSuite.snapshotRequestOutputPort, oms.snapshotRequest, true);
		}

		rootSuite.applyPartitionResolver(ocaResolver, true, true);
		rootSuite.applyPartitionResolver(resolver, true, true);
		rootSuite.applyPartitionResolver(fixmsgresolver, true, true);
		oms.omsClientRequestHandler.setPartitionResolver(new RequestPartitionResolver<OmsClientAction>(ocaResolver));
		if (props.getOptional("fastmode", false))
			fixadapter.setPartitionResolver(new BasicPartitionResolver<FixEvent>(FixEvent.class, "FIX_IN"));

		// Create a tcp/ip server connection for the front end.
		String uid = GuidHelper.getGuid();
		int exhaustServerPort = props.getRequired(OPTION_EXHAUST_PORT, Integer.class);
		int refDataServerPort = props.getRequired(OPTION_REFDATA_PORT, Integer.class);
		MsgDirectConnection connection = new MsgDirectConnection(new BasicMsgConnectionConfiguration("connection1"));
		connection.addTopic(new MsgDirectTopicConfiguration(FE_SNAPSHOT_REQUEST, exhaustServerPort, FE_SNAPSHOT_REQUEST));
		connection.addTopic(new MsgDirectTopicConfiguration(FE_SNAPSHOT_RESPONSE, exhaustServerPort, FE_SNAPSHOT_RESPONSE));
		connection.addTopic(new MsgDirectTopicConfiguration(FE_DELTAS_OUTGOING, exhaustServerPort, FE_DELTAS_OUTGOING));
		connection.addTopic(new MsgDirectTopicConfiguration(FE_ADMIN_INCOMING, exhaustServerPort, FE_ADMIN_INCOMING));
		connection.addTopic(new MsgDirectTopicConfiguration(FE_OFR_OUTGOING, exhaustServerPort, FE_OFR_OUTGOING));
		connection.addTopic(new MsgDirectTopicConfiguration(FE_OFR_INCOMING, exhaustServerPort, FE_OFR_INCOMING));
		connection.addTopic(new MsgDirectTopicConfiguration(OFR_OMS_REQUEST, exhaustServerPort, OFR_OMS_REQUEST));
		connection.addTopic(new MsgDirectTopicConfiguration(OMS_OFR_RESPONSE, exhaustServerPort, OMS_OFR_RESPONSE));
		connection.addTopic(new MsgDirectTopicConfiguration(REFDATA_CLIENT_TO_SERVER, refDataServerPort, REFDATA_CLIENT_TO_SERVER));
		connection.addTopic(new MsgDirectTopicConfiguration(REFDATA_SERVER_TO_CLIENT, refDataServerPort, REFDATA_SERVER_TO_CLIENT));
		connection.addTopic(new MsgDirectTopicConfiguration(OMS_OFR_ADMIN_REQUEST, exhaustServerPort, OMS_OFR_ADMIN_REQUEST));
		connection.addTopic(new MsgDirectTopicConfiguration(OMS_CLIENTFIX, exhaustServerPort, OMS_CLIENTFIX));

		MsgSuite feSnapshotSuite = new MsgSuite(TOFRONTEND, connection, FE_SNAPSHOT_REQUEST, FE_SNAPSHOT_RESPONSE, uid);
		MsgSuite feDeltasSuite = new MsgSuite(TOFRONTEND, connection, FE_ADMIN_INCOMING, FE_DELTAS_OUTGOING, uid);
		MsgSuite feAlertsSuite = new MsgSuite(TOFRONTEND, connection, FE_OFR_INCOMING, FE_OFR_OUTGOING, uid);
		MsgSuite ofrCommandSuite = new MsgSuite(TOOFR, connection, OFR_OMS_REQUEST, OMS_OFR_RESPONSE, uid);
		MsgSuite refDataMsgSuite = new MsgSuite(REFDATA, connection, REFDATA_CLIENT_TO_SERVER, REFDATA_SERVER_TO_CLIENT, uid);
		MsgSuite clientMsgSuite = new MsgSuite(TOFRONTEND, connection, OMS_CLIENTFIX, null, null);

		ClassRoutingProcessor<Action> router = new ClassRoutingProcessor<Action>(Action.class);
		rootSuite.addChildren(feDeltasSuite, feSnapshotSuite, feAlertsSuite, ofrCommandSuite, refDataMsgSuite, router, clientMsgSuite);
		rootSuite.wire(toOmsClientMulticast.newOutputPort(), feDeltasSuite.getOutboundInputPort(), true);
		ContainerHelper.wireCast(rootSuite, feSnapshotSuite.getInboundOutputPort(), oms.snapshotRequest, true);

		// Setup loopback to broadcast to GUIs
		rootSuite.wire(feAlertsSuite.getInboundOutputPort(), feAlertsSuite.getOutboundInputPort(), true);
		// Setup command channel from OFR
		ContainerHelper.wireCast(rootSuite, ofrCommandSuite.getInboundOutputPort(), router, false);
		ContainerHelper.wireCast(rootSuite, clientMsgSuite.inboundOutputPort, fixprocessor, true);
		router.bindToPartition(TOOFR);
		rootSuite.wire(router.newOutputPort(OmsClientAction.class), oms.fromOMSClient, true);
		rootSuite.wire(router.newRequestOutputPort(OmsClientAction.class, TextMessage.class), oms.fromOMSRequestClient, true);
		connection.init();
		QfixConsole fixconsole = new QfixConsole((FixMsgConnection) mycontainer.getServices().getMsgManager().getConnection("FIX"));

		// Register the Console
		cam.registerConsoleObject("oms", new OmsConsole(mycontainer));
		cam.registerConsoleObject("msg", new MsgConsole(connection));
		cam.registerConsoleObject("fix", fixconsole);
		cam.registerMessagesInPackages("com.f1.pofo");
		// Start everything up!

		processPluginTranslactions(oms.getPluginManager(), fixadapter);

		cam.startupContainer(mycontainer);

		// Let's keep the app alive.
		cam.keepAlive();
	}

	public static void processPluginTranslactions(OmsPluginManager pluginManager, QfixInboundProcessor in) {
		for (final int tag : pluginManager.getCancelOrderRetainFields())
			in.addCancelOrderRetainTag(tag);

		for (final int tag : pluginManager.getReplaceOrderRetainFields())
			in.addReplaceOrderRetainTag(tag);

		for (final int tag : pluginManager.getNewOrderRetainFields())
			in.addNewOrderRetainTag(tag);

		for (final int tag : pluginManager.getExecutionReportRetainFields())
			in.addExecutionReportRetainTag(tag);
	}
}
