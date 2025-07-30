package com.f1.exchSim;

import java.io.File;

import com.f1.base.Action;
import com.f1.base.Factory;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.Container;
import com.f1.container.PartitionResolver;
import com.f1.container.Suite;
import com.f1.container.impl.BasicContainer;
import com.f1.container.impl.BasicPartitionResolver;
import com.f1.container.impl.ContainerHelper;
import com.f1.container.impl.RequestPartitionResolver;
import com.f1.fix.oms.OmsConsole;
import com.f1.fix.oms.OmsPluginManager;
import com.f1.fix.oms.OmsSuite;
import com.f1.fix.oms.adapter.FixMsgPartitionResolver;
import com.f1.fix.oms.adapter.FixMsgProcessor;
import com.f1.mktdatasim.MktDataSimClient;
import com.f1.mktdatasim.MktDataSimulator;
import com.f1.msg.impl.BasicMsgConnectionConfiguration;
import com.f1.msg.impl.MsgConsole;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.msgdirect.MsgDirectTopicConfiguration;
import com.f1.pofo.fix.FixMsg;
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
import com.f1.utils.GuidHelper;
import com.f1.utils.IOH;
import com.f1.utils.PropertyController;
import com.f1.utils.ids.BasicNamespaceIdGenerator;
import com.f1.utils.ids.BatchIdGenerator;
import com.f1.utils.ids.FileBackedIdGenerator;
import com.f1.utils.ids.IdGenerator;

public class ExchSimMain {

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
		ContainerBootstrap cam = new ContainerBootstrap(ExchSimMain.class, args);

		// at this point default values have been set...

		// 3 different ways to override a property, for example these are all
		// equivalent:
		cam.setConfigDirProperty("./src/main/config");
		//cam.setLoggingOverrideProperty("normal");
		cam.setConsolePortProperty(3333);
		//cam.setLogPerformanceLevel(Level.INFO);
		// cam.setMessagePackagesProperty("com.f1.msg.fix,com.f1.fix.oms");

		// cam.setProperty("f1.conf.dir", "myconfig");
		// System.setProperty("property.f1.conf.dir", "myconfig");

		// now that the configuration dir / file have been set, lets init the
		// properties
		cam.readProperties();

		// lets override a setting or two...
		// cam.setLogLevel(Level.FINE, Level.CONFIG, ExchSimMain.class);
		// cam.setAutocodedDirectoryCleanProperty(true);
		// cam.setAutocodedDirectoryProperty("com.f1.msf.fix");

		// how that we are happy with the properties, lets consume them.
		cam.processProperties();

		PropertyController props = cam.getProperties();

		// now the properties are locked, but we can override other things, like
		// the converter
		// cam.setConverter(new ObjectToJsonConverter());

		// all set, now lets startup
		cam.startup();

		// DataSource dbsource = new PooledDataSource(DBH.createDataSource(props.getRequired("omsdb.url"), props.getRequired("omsdb.password")));
		int idFountainBatchsize = props.getOptional("idfountain.batchsize", 1000);
		MktDataSimClient exchangeSim = new MktDataSimClient(null);

		// create the container
		Container mycontainer = new BasicContainer();
		mycontainer.setName("TestContainer");
		// mycontainer.getServices().addDataSource("omsdb", dbsource);

		// Factory<String, ? extends IdGenerator<Long>> generator = new DbBackedIdGenerator.Factory(dbsource, "Id_Fountains", "next_id", "namespace");
		File idDirectory = props.getRequired("idfountain.dir", File.class);
		IOH.ensureDir(idDirectory);
		Factory<String, ? extends IdGenerator<Long>> generator = new FileBackedIdGenerator.Factory(idDirectory);
		cam.setIdGenerator(new BasicNamespaceIdGenerator<Long>(new BatchIdGenerator.Factory<Long>(generator, idFountainBatchsize)));

		for (int i = 0; i < 10000; i++)
			cam.getIdGenerator().getIdGenerator("O-").createNextId();
		// pass the container through the builder...
		cam.prepareContainer(mycontainer);
		PartitionResolver<FixEvent> resolver = new BasicPartitionResolver<FixEvent>(FixEvent.class, "FIX");

		PartitionResolver<FixMsg> fixmsgresolver = new FixMsgPartitionResolver();
		// create and add three processors
		Suite rootSuite = mycontainer.getRootSuite();
		rootSuite.addChild(exchangeSim);
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
		// oms.setFix();

		PartitionResolver<OmsClientAction> garbage = new TestOCAResolver();

		//	PartitionResolver<OmsClientAction> ocaResolver = new OmsClientActionResolver();
		//	RequestPartitionResolver<OmsClientAction> ocaRequestResolver = new RequestPartitionResolver<OmsClientAction>(ocaResolver);
		MulticastProcessor<OmsNotification> toOmsClientMulticast = new MulticastProcessor<OmsNotification>(OmsNotification.class);
		rootSuite.addChildren(toOmsClientMulticast);
		rootSuite.wire(oms.toOMSClient, toOmsClientMulticast, false);

		//rootSuite.applyPartitionResolver(ocaResolver, true, true);
		rootSuite.applyPartitionResolver(resolver, true, true);
		rootSuite.applyPartitionResolver(fixmsgresolver, true, true);
		if (props.getOptional("fastmode", false))
			fixadapter.setPartitionResolver(new BasicPartitionResolver<FixEvent>(FixEvent.class, "FIX_IN"));

		TestOMSClient omsClient = new TestOMSClient();
		rootSuite.addChild(omsClient);
		rootSuite.wire(omsClient.loopback, omsClient, true);

		//MulticastProcessor<OmsNotification> toOmsClientMulticast = new MulticastProcessor<OmsNotification>(OmsNotification.class);
		// OmsToFrontEndDeltaProcessor toFrontEndProcessor = new OmsToFrontEndDeltaProcessor();
		// OmsGetOrderStateProcessor getOrderStateProcessor = new OmsGetOrderStateProcessor();
		//rootSuite.addChildren(toOmsClientMulticast);
		/*
		 * rootSuite.wire(toFrontEndSnapshotProcessor.getOrderStatePort, getOrderStateProcessor, true); rootSuite.wire(toOmsClientMulticast.newOutputPort(OMSNotification.class),
		 * toFrontEndProcessor, true); rootSuite.wire(toOmsClientMulticast.newOutputPort(OMSNotification.class), toDbProcessor, true);
		 */
		rootSuite.wire(toOmsClientMulticast.newOutputPort(), omsClient, true);
		rootSuite.wire(omsClient.output, oms.fromOMSClient, true);
		rootSuite.applyPartitionResolver(garbage, true, true);

		// rootSuite.addChildren(chainingProcessor, endofChainProcessor,
		// replyProcessor, responseProcessor);
		fixadapter.setPartitionResolver(new BasicPartitionResolver<FixEvent>(FixEvent.class, "FIX"));

		//omsClient.setPartitionResolver(garbage2);

		// wire the processors together
		// Grab a port to send messages into the chainProcessor.
		String uid = GuidHelper.getGuid();
		/*
		 * MsgDirectConnection connection = new MsgDirectConnection(new BasicMsgConnectionConfiguration("connection1", ":4567")); connection.addChannel(new
		 * MsgDirectChannelConfiguration("fe.snapshot.request", ":4567:fe.snapshot.request")); connection.addChannel(new MsgDirectChannelConfiguration("fe.snapshot.response",
		 * ":4567:fe.snapshot.response")); connection.addChannel(new MsgDirectChannelConfiguration("fe.deltas.outgoing", ":4567:fe.deltas.outgoing")); connection.addChannel(new
		 * MsgDirectChannelConfiguration("fe.admin.incoming", ":4567:fe.admin.incoming")); MsgSuite feSnapshotSuite = new MsgSuite("TOFRONTEND", connection, "fe.snapshot.request",
		 * "fe.snapshot.response", uid); MsgSuite feDeltasSuite = new MsgSuite("TOFRONTEND", connection, "fe.admin.incoming", "fe.deltas.outgoing", uid);
		 * rootSuite.addChildren(feDeltasSuite, feSnapshotSuite); rootSuite.wire(toFrontEndProcessor.out, feDeltasSuite.getOutboundInputPort(), true);
		 * rootSuite.wire(feSnapshotSuite.inboundOutputPort, toFrontEndSnapshotProcessor, true); connection.init();
		 */

		/*************************************************************************************/
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
		rootSuite.addChildren(feDeltasSuite, feSnapshotSuite, feAlertsSuite, ofrCommandSuite, router, clientMsgSuite);
		rootSuite.wire(toOmsClientMulticast.newOutputPort(), feDeltasSuite.getOutboundInputPort(), true);
		ContainerHelper.wireCast(rootSuite, feSnapshotSuite.getInboundOutputPort(), oms.snapshotRequest, true);

		oms.omsClientRequestHandler.setPartitionResolver(new RequestPartitionResolver<OmsClientAction>(garbage));

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

		/***********************************************************************************************/
		mycontainer.getServices().putService("mktdata", new MktDataSimulator(exchangeSim));
		processPluginTranslactions(oms.getPluginManager(), fixadapter);
		cam.startupContainer(mycontainer);
		cam.registerConsoleObject("sim", new ExchSimConsole(mycontainer));

		// Send a message into the chaining processor

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
