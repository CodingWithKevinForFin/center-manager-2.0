package com.sjls.f1.start.oms;

import java.io.File;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;

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
import com.f1.refdata.RefDataManager;
import com.f1.refdata.impl.BasicRefDataManager;
import com.f1.refdataclient.RefDataClientSuite;
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
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.db.Database;
import com.f1.utils.ids.BasicNamespaceIdGenerator;
import com.f1.utils.ids.BatchIdGenerator;
import com.f1.utils.ids.FileBackedIdGenerator;
import com.sjls.f1.sjlscommon.SjlsConverterHelper;
import com.sjls.f1.start.SjlsIdGenerator;
import com.sjls.f1.start.SjlsRefdataReader;

/*
 * -Dlog4j.configuratorClass=com.f1.speedloggerLog4j.Log4jSpeedLoggerManager
 * -Dlog4j.configuration=com/f1/speedloggerLog4j/Log4jSpeedLoggerManager.class
 * -Djava.util.logging.manager=com.f1.speedlogger.sun.SunSpeedLoggerLogManager
 * -Dproperty.sjls.dir=<path to sjls datadir>
 *
 * Optional:
 * -D111.property.persist.dir=<path to persist directory>
 * -Dproperty.omsdb.url=<url to db>
 */
public class StartOmsMain {

	public static final String OPTION_SECMASTER_FILE = "sjls.secmaster.file";
	public static final String OPTION_PERSIST_DIR = "persist.dir";
	public static final String OPTION_PERSIST_CLEAN = "persist.clean";
	public static final String OPTION_PERSIST_BUFSIZE = "persist.bufsize";
	public static final String OPTION_IDFOUNTAIN_BATCHSIZE = "idfountan.batchsize";
	public static final String OPTION_IDFOUNTAIN_DIR = "idfountain.dir";
	public static final String OPTION_OMSDB_URL = "omsdb.url";
	public static final String OPTION_OMSDB_PASSWORD = "omsdb.password";
	public static final String OPTION_QFIX_CONFIG = "qfix.config.file";
	public static final String OPTION_EXHAUST_PORT = "exhaust.port";
	public static final String OPTION_REFDATA_PORT = "refdata.port";
	public static final String OPTION_SYSTEM_NAME = "systemname";
	public static final String OPTION_SQL_DIR = "sql.dir";

	public static void main(String... args) throws Exception {

		// /////////////////////////////////////
		// //////////////// INIT ///////////////

		// Bootstraping...
		final ContainerBootstrap cam = new ContainerBootstrap(StartOmsMain.class, args);
		cam.setConfigDirProperty("./src/main/config");
		cam.setLogLevel(Level.FINE, Level.CONFIG, StartOmsMain.class);
		final PropertyController props = cam.getProperties();
		cam.startup();

		// Container

		SjlsConverterHelper.registerConverters((ObjectToByteArrayConverter) cam.getConverter());
		Container mycontainer = new BasicContainer();
		mycontainer.setName("START_OMS");

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

		// Security Master
		RefDataManager refData = SjlsRefdataReader.read(props.getRequired(OPTION_SECMASTER_FILE, File.class), clock.getNowNanoDate(), clock.getTimeZone(), cam.getGenerator());
		if (refData.getSecuritiesCount() == 0)
			throw new RuntimeException("No securities were loaded");

		OmsUtils.setRefData(mycontainer, refData);

		RefDataClientSuite refDataSuite = new RefDataClientSuite("$$REFDATA", (BasicRefDataManager) refData);

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
			if (name.endsWith(".todays_ids") && Integer.parseInt(SH.stripSuffix(name, ".todays_ids", true)) < today)
				IOH.delete(file);
		}
		BatchIdGenerator.Factory<Long> fountain = new BatchIdGenerator.Factory<Long>(new FileBackedIdGenerator.Factory(idDirectory), idFountainBatchsize);
		// mycontainer.getServices().setTicketGenerator(new
		// BasicNamespaceIdGenerator<String>(new
		// GuidStringIdGenerator.Factory(62, false)));

		mycontainer.getServices().setTicketGenerator(
				new SjlsIdGenerator(formatter.getDateFormatter(LocaleFormatter.DATE), clock, systemName, new BasicNamespaceIdGenerator<Long>(fountain)));
		mycontainer.getServices().setUidGenerator(new BasicNamespaceIdGenerator<Long>(fountain));

		// Replication
		File persistDir = props.getOptional(OPTION_PERSIST_DIR, File.class);
		if (persistDir != null) {
			boolean persistClean = props.getOptional(OPTION_PERSIST_CLEAN, Boolean.FALSE);
			long persistMaxDeltaBufferSize = props.getOptional(OPTION_PERSIST_BUFSIZE, FilePersister.DEFAULT_MAX_DELTA_SIZE);
			if (persistClean)
				IOH.deleteForce(persistDir);
			ContainerPersister cp = new ContainerPersister(mycontainer.getPersistenceController());
			SjlsConverterHelper.registerConverters((ObjectToByteArrayConverter) cp.getFactory().getConverter());
			cp.addFileReplication(persistDir, false, persistMaxDeltaBufferSize);
			RH.setField(RH.getField(cp, "filePersist"), "maxDeltaSize", Integer.MAX_VALUE);
		}

		PartitionResolver<FixEvent> resolver = new BasicPartitionResolver<FixEvent>(FixEvent.class, "FIX");

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
		// oms.setFix();

		rootSuite.addChild(refDataSuite);

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

		// Create a tcp/ip server connection for the front end.
		String uid = GuidHelper.getGuid();
		int exhaustServerPort = props.getRequired(OPTION_EXHAUST_PORT, Integer.class);
		int refDataServerPort = props.getRequired(OPTION_REFDATA_PORT, Integer.class);
		MsgDirectConnection connection = new MsgDirectConnection(new BasicMsgConnectionConfiguration("connection1"));
		connection.addTopic(new MsgDirectTopicConfiguration("fe.snapshot.request", exhaustServerPort, "fe.snapshot.request"));
		connection.addTopic(new MsgDirectTopicConfiguration("fe.snapshot.response", exhaustServerPort, "fe.snapshot.response"));
		connection.addTopic(new MsgDirectTopicConfiguration("fe.deltas.outgoing", exhaustServerPort, "fe.deltas.outgoing"));
		connection.addTopic(new MsgDirectTopicConfiguration("fe.admin.incoming", exhaustServerPort, "fe.admin.incoming"));
		connection.addTopic(new MsgDirectTopicConfiguration("fe.ofr.outgoing", exhaustServerPort, "fe.ofr.outgoing"));
		connection.addTopic(new MsgDirectTopicConfiguration("fe.ofr.incoming", exhaustServerPort, "fe.ofr.incoming"));
		connection.addTopic(new MsgDirectTopicConfiguration("ofr.oms.request", exhaustServerPort, "ofr.oms.request"));
		connection.addTopic(new MsgDirectTopicConfiguration("oms.ofr.response", exhaustServerPort, "oms.ofr.response"));
		connection.addTopic(new MsgDirectTopicConfiguration("refdata.clientToServer", refDataServerPort, "refdata.clientToServer"));
		connection.addTopic(new MsgDirectTopicConfiguration("refdata.serverToClient", refDataServerPort, "refdata.serverToClient"));
		connection.addTopic(new MsgDirectTopicConfiguration("oms.ofr.admin.request", exhaustServerPort, "oms.ofr.admin.request"));

		MsgSuite feSnapshotSuite = new MsgSuite("TOFRONTEND", connection, "fe.snapshot.request", "fe.snapshot.response", uid);
		MsgSuite feDeltasSuite = new MsgSuite("TOFRONTEND", connection, "fe.admin.incoming", "fe.deltas.outgoing", uid);
		MsgSuite feAlertsSuite = new MsgSuite("TOFRONTEND", connection, "fe.ofr.incoming", "fe.ofr.outgoing", uid);
		MsgSuite ofrCommandSuite = new MsgSuite("TOOFR", connection, "ofr.oms.request", "oms.ofr.response", uid);
		MsgSuite refDataMsgSuite = new MsgSuite("REFDATA", connection, "refdata.clientToServer", "refdata.serverToClient", uid);

		ClassRoutingProcessor<Action> router = new ClassRoutingProcessor<Action>(Action.class);
		rootSuite.addChildren(feDeltasSuite, feSnapshotSuite, feAlertsSuite, ofrCommandSuite, refDataMsgSuite, router);
		ContainerHelper.wireCast(rootSuite, refDataMsgSuite.inboundOutputPort, refDataSuite.requestPort, true);
		rootSuite.wire(toOmsClientMulticast.newOutputPort(), feDeltasSuite.getOutboundInputPort(), true);
		ContainerHelper.wireCast(rootSuite, feSnapshotSuite.getInboundOutputPort(), oms.snapshotRequest, true);
		// Setup loopback to broadcast to GUIs
		rootSuite.wire(feAlertsSuite.getInboundOutputPort(), feAlertsSuite.getOutboundInputPort(), true);
		// Setup command channel from OFR
		ContainerHelper.wireCast(rootSuite, ofrCommandSuite.getInboundOutputPort(), router, false);
		router.bindToPartition("TOOFR");
		rootSuite.wire(router.newOutputPort(OmsClientAction.class), oms.fromOMSClient, true);
		rootSuite.wire(router.newRequestOutputPort(OmsClientAction.class, TextMessage.class), oms.fromOMSRequestClient, true);
		connection.init();
		QfixConsole fixconsole = new QfixConsole((FixMsgConnection) mycontainer.getServices().getMsgManager().getConnection("FIX"));

		// Register the Console
		cam.registerConsoleObject("oms", new OmsConsole(mycontainer));
		cam.registerConsoleObject("msg", new MsgConsole(connection));
		cam.registerConsoleObject("fix", fixconsole);
		cam.registerMessagesInPackages("com.f1.pofo");
		processPluginTranslactions(oms.getPluginManager(), fixadapter);

		// Start everything up!
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
