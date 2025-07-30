package com.sjls.f1.start.ofradapter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.f1.base.Clock;
import com.f1.base.IdeableGenerator;
import com.f1.base.Message;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.Container;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultActionFuture;
import com.f1.container.Suite;
import com.f1.container.impl.BasicContainer;
import com.f1.container.impl.ContainerHelper;
import com.f1.fixomsclient.OmsClientOrdersExecutionsListener;
import com.f1.fixomsclient.OmsClientOrdersExecutionsManager;
import com.f1.fixomsclient.OmsClientSuite;
import com.f1.msg.MsgConnection;
import com.f1.msg.MsgConnectionListener;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.MsgInputTopic;
import com.f1.msg.MsgOutputTopic;
import com.f1.msg.MsgTopic;
import com.f1.msg.impl.BasicMsgConnectionConfiguration;
import com.f1.msg.impl.MsgConsole;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.msgdirect.MsgDirectTopicConfiguration;
import com.f1.pofo.refdata.RefDataInfoMessage;
import com.f1.pofo.refdata.RefDataRequestMessage;
import com.f1.pofo.refdata.Security;
import com.f1.refdataclient.RefDataClientSuite;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.utils.ClassFinder;
import com.f1.utils.GuidHelper;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OfflineConverter;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.ids.BasicNamespaceIdGenerator;
import com.f1.utils.ids.BatchIdGenerator;
import com.f1.utils.ids.FileBackedIdGenerator;
import com.f1.utils.mirror.reflect.ReflectedClassMirror;

public class OMSConnectionHandler implements MsgConnectionListener {

	private static final String FE_SNAPSHOT_RESPONSE = "fe.snapshot.response";
	private static final String FE_SNAPSHOT_REQUEST = "fe.snapshot.request";
	private static final java.util.logging.Logger log = LH.get(OMSConnectionHandler.class);
	public static final long SNAPSHOT_CHECKER_PERIOD = 30000;
	private boolean requestTopicConnected = false, responseTopicConnected = false, privateTopicConnected = false;

	final private OfflineConverter converter;
	final private IdeableGenerator generator;
	final private MsgDirectConnection connection;
	final private String uid;
	private volatile boolean connected;
	final private String host;
	final private int port;
	final private OmsClientOrdersExecutionsManager manager;
	final private BasicContainer container;
	final private ContainerBootstrap bootstrap;
	private OmsClientSuite omsClientSuite;
	private RefDataClientSuite refData;
	private ObjectGeneratorForClass<RefDataRequestMessage> refreg;
	private RequestOutputPort<RefDataRequestMessage, RefDataInfoMessage> refrequest;
	private OfrAdapter adapter;

	// private OutputPort<RequestMessage<OmsSnapshotRequest>> snapshotRequestPort;
	// private SjlsAlertsProcessor alertsProcessor;

	public OMSConnectionHandler(ContainerBootstrap bootstrap, OfrAdapter adapter, String host, int port) throws IOException {
		this.adapter = adapter;
		OnOmsResponseProcessor onOmsResponseProcessor = new OnOmsResponseProcessor(adapter);
		this.container = new BasicContainer();

		this.bootstrap = bootstrap;
		bootstrap.prepareContainer(container);
		bootstrap.setMessagePackagesProperty("com.f1.pofo");

		Clock clock = container.getServices().getClock();
		Locale locale = bootstrap.getLocale();
		TimeZone timeZone = bootstrap.getTimeZone();
		String systemName = bootstrap.getProperties().getOptional(StartOFRMain.OPTION_SYSTEM_NAME, "ofr");
		LocaleFormatter formatter = container.getServices().getLocaleFormatterManager().getThreadSafeLocaleFormatter(locale, timeZone);

		PropertyController props = bootstrap.getProperties();
		int idFountainBatchsize = props.getOptional(StartOFRMain.OPTION_IDFOUNTAIN_BATCHSIZE, 1000);
		File idDirectory = props.getRequired(StartOFRMain.OPTION_IDFOUNTAIN_DIR, File.class);
		IOH.ensureDir(idDirectory);
		BatchIdGenerator.Factory<Long> fountain = new BatchIdGenerator.Factory<Long>(new FileBackedIdGenerator.Factory(idDirectory), idFountainBatchsize);
		container.getServices().setTicketGenerator(
				new SjlsOfrIdGenerator(formatter.getDateFormatter(LocaleFormatter.DATE), clock, systemName, new BasicNamespaceIdGenerator<Long>(fountain)));
		this.uid = GuidHelper.getGuid();
		this.port = port;
		this.host = host;
		this.connected = false;
		this.converter = bootstrap.getConverter();
		this.generator = this.converter.getIdeableGenerator();
		ClassFinder finder = new ClassFinder().searchClasspath(ClassFinder.TYPE_DIRECTORY | ClassFinder.TYPE_JAR);
		Collection<Class> classes = finder.filterByPackage("com.f1.pofo").toReflected().filterByExtends(ReflectedClassMirror.valueOf(Message.class)).getClasses();
		this.generator.register(classes.toArray(new Class[classes.size()]));
		connection = new MsgDirectConnection(new BasicMsgConnectionConfiguration("oms_connection1"));
		connection.addMsgConnectionListener(this);

		// Snapshot request and response for orders
		connection.addTopic(new MsgDirectTopicConfiguration(FE_SNAPSHOT_REQUEST, host, port));
		connection.addTopic(new MsgDirectTopicConfiguration(FE_SNAPSHOT_RESPONSE, host, port));
		connection.addTopic(new MsgDirectTopicConfiguration("fe.deltas.outgoing", host, port));

		connection.addTopic(new MsgDirectTopicConfiguration("fe.admin.incoming", host, port));

		// Send and receive alerts from OMS
		connection.addTopic(new MsgDirectTopicConfiguration("fe.ofr.outgoing", host, port));
		connection.addTopic(new MsgDirectTopicConfiguration("fe.ofr.incoming", host, port));

		String refDataHost = bootstrap.getProperties().getRequired(StartOFRMain.OPTION_REFDATA_HOST);
		int refDataPort = bootstrap.getProperties().getRequired(StartOFRMain.OPTION_REFDATA_PORT, Integer.class);

		// Send and receive OMS actions from OMS
		connection.addTopic(new MsgDirectTopicConfiguration("ofr.oms.request", host, port));
		connection.addTopic(new MsgDirectTopicConfiguration("oms.ofr.response", host, port));
		connection.addTopic(new MsgDirectTopicConfiguration("refdata.clientToServer", refDataHost, refDataPort, "refdata.clientToServer"));
		connection.addTopic(new MsgDirectTopicConfiguration("refdata.serverToClient", refDataHost, refDataPort, "refdata.serverToClient"));

		bootstrap.registerConsoleObject("oms_connection", new MsgConsole(connection));
		final MsgSuite detlasMsgSuite = new MsgSuite("OMSCLIENT_CONNECTION", connection, "fe.deltas.outgoing", null, uid);
		final MsgSuite alertsMsgSuite = new MsgSuite("OMSCLIENT_CONNECTION", connection, "fe.ofr.outgoing", "fe.ofr.incoming", uid);
		final MsgSuite snapshotMsgSuite = new MsgSuite("TOFRONTEND", connection, FE_SNAPSHOT_RESPONSE, FE_SNAPSHOT_REQUEST, uid);
		final MsgSuite omsCommandSuite = new MsgSuite("OMSCOMMAND", connection, "oms.ofr.response", "ofr.oms.request", uid);

		refData = new RefDataClientSuite("REFDATA");
		MsgSuite refdataSuite = new MsgSuite("REFDATA", connection, "refdata.serverToClient", "refdata.clientToServer", uid);
		Suite rootSuite = container.getRootSuite();

		// alertsProcessor = new SjlsAlertsProcessor();
		// alertsProcessor.bindToPartition("OMSCLIENT_ORDEREXECS");
		omsClientSuite = new OmsClientSuite(true);
		//omsClientSuite.applyPartitionResolver(new BasicPartitionResolver<Action>(Action.class, "OMSCLIENT_ORDEREXECS"), true, true);
		onOmsResponseProcessor.bindToPartition("OMSCLIENTSUITE");
		rootSuite.addChildren(detlasMsgSuite, omsClientSuite, snapshotMsgSuite, omsCommandSuite, alertsMsgSuite, adapter, refData, refdataSuite, onOmsResponseProcessor);
		refrequest = rootSuite.exposeInputPortAsOutput(refData.requestPort, true);
		ContainerHelper.wireCast(rootSuite, alertsMsgSuite.inboundOutputPort, omsClientSuite.broadcastInputPort, true);
		ContainerHelper.wireCast(rootSuite, detlasMsgSuite.inboundOutputPort, omsClientSuite.notificationInputPort, true);
		rootSuite.wire(adapter.toOmsRequestPort, omsCommandSuite.getOutboundInputPort(), true);
		rootSuite.wire(adapter.toOmsRequestPort.getResponsePort(), onOmsResponseProcessor, true);
		rootSuite.wire(adapter.toOms, omsCommandSuite.getOutboundInputPort(), true);
		//rootSuite.wire(adapter.alertPort, alertsMsgSuite.getOutboundInputPort(), true);
		rootSuite.wire(omsClientSuite.snapshotRequestOutputPort, snapshotMsgSuite.getOutboundInputPort(), true);

		rootSuite.wire(refData.toServerPort, refdataSuite.outboundInputPort, false);

		this.manager = omsClientSuite.createManager("OMSCLIENTSUITE");
		manager.addGuiManagerListener(adapter);
	}

	public Container getContainer() {
		return container;
	}

	public void init() {
		adapter.setOmsProxy(this);
		refreg = container.getGenerator(RefDataRequestMessage.class);
		bootstrap.startupContainer(container);
	}

	@Override
	synchronized public void onDisconnect(MsgConnection connection, MsgTopic topic, String channel, String suffix, String remoteHost, boolean isWrite, MsgExternalConnection conn) {
		if (FE_SNAPSHOT_REQUEST.equals(topic.getName()))
			requestTopicConnected = false;
		else if (FE_SNAPSHOT_RESPONSE.equals(topic.getName())) {
			if (SH.is(suffix))
				privateTopicConnected = false;
			else
				responseTopicConnected = false;
		} else
			return;
		LH.info(log, "DISCONNECT from ", topic.getName(), "+", suffix, " ==> REQ: ", requestTopicConnected, ", RES: ", responseTopicConnected, ", PRI: ", privateTopicConnected,
				" CON: ", connected);
		if (!requestTopicConnected && !responseTopicConnected && !privateTopicConnected && connected) {
			this.connected = false;
			LH.info(log, "SENDING DISCONNECT");
			for (OmsClientOrdersExecutionsListener e : manager.getListeners())
				e.onDisconnected(manager);
		}
	}

	@Override
	synchronized public void onConnect(MsgConnection connection, MsgTopic topic, String channel, String suffix, String remoteHost, boolean isWrite, MsgExternalConnection conn) {
		LH.info(log, "Connection from: ", topic.getName(), "+", suffix);
		if (FE_SNAPSHOT_REQUEST.equals(topic.getName()))
			requestTopicConnected = true;
		else if (FE_SNAPSHOT_RESPONSE.equals(topic.getName())) {
			if (SH.is(suffix))
				privateTopicConnected = true;
			else
				responseTopicConnected = true;
		} else
			return;
		LH.info(log, "CONNECT from ", topic.getName(), "+", suffix, " ==> REQ: ", requestTopicConnected, ", RES: ", responseTopicConnected, ", PRI: ", privateTopicConnected,
				" CON: ", connected);
		if (requestTopicConnected && responseTopicConnected && privateTopicConnected && !connected) {
			LH.info(log, "REQUESTING SNAPSHOT");
			this.connected = true;
			for (OmsClientOrdersExecutionsListener e : manager.getListeners())
				e.onConnected(manager);
			requestSnapshot();
		}
	}

	private void requestSnapshot() {
		LH.info(log, "send Snapshot Request");
		manager.requestSnapshot();
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public Security getRefData(String symbol) {
		RefDataRequestMessage msg = refreg.nw();
		msg.setSymbol(symbol);
		ResultActionFuture<RefDataInfoMessage> ret = refrequest.requestWithFuture(msg, null);
		Map<Integer, Security> map = ret.getResult(50000).getAction().getSecurities();
		if (map == null)
			return null;
		Iterator<Map.Entry<Integer, Security>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			return iter.next().getValue();
		}
		return null;
	}

	public OmsClientOrdersExecutionsManager getManager() {
		return manager;
	}

	public RequestOutputPort<RefDataRequestMessage, RefDataInfoMessage> getRefDataPort() {
		return refrequest;
	}

	@Override
	public void onNewInputTopic(MsgConnection connection, MsgInputTopic r) {

	}

	@Override
	public void onNewOutputTopic(MsgConnection connection, MsgOutputTopic r) {

	}
}
