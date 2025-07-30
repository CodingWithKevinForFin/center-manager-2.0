package com.f1.strategy;

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
import com.f1.fixomsclient.OmsClientSuite;
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
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OfflineConverter;
import com.f1.utils.PropertyController;
import com.f1.utils.ids.BasicNamespaceIdGenerator;
import com.f1.utils.ids.BatchIdGenerator;
import com.f1.utils.ids.FileBackedIdGenerator;
import com.f1.utils.mirror.reflect.ReflectedClassMirror;

public class OMSConnectionHandler {

	final private OfflineConverter converter;
	final private IdeableGenerator generator;
	final private MsgDirectConnection connection;
	final private String uid;
	private boolean connected;
	final private String host;
	final private int port;
	final private BasicContainer container;
	final private ContainerBootstrap bootstrap;
	private OmsClientSuite omsClientSuite;
	private RefDataClientSuite refData;
	private ObjectGeneratorForClass<RefDataRequestMessage> refreg;
	private RequestOutputPort<RefDataRequestMessage, RefDataInfoMessage> refrequest;

	public OMSConnectionHandler(ContainerBootstrap bootstrap, String host, int port) throws IOException {
		this.container = new BasicContainer();

		this.bootstrap = bootstrap;
		bootstrap.prepareContainer(container);
		bootstrap.setMessagePackagesProperty("com.f1.pofo");

		Clock clock = container.getServices().getClock();
		Locale locale = bootstrap.getLocale();
		TimeZone timeZone = bootstrap.getTimeZone();
		String systemName = bootstrap.getProperties().getOptional(StrategyWrapperMain.OPTION_SYSTEM_NAME, "ofr");
		LocaleFormatter formatter = container.getServices().getLocaleFormatterManager().getThreadSafeLocaleFormatter(locale, timeZone);

		PropertyController props = bootstrap.getProperties();
		int idFountainBatchsize = props.getOptional(StrategyWrapperMain.OPTION_IDFOUNTAIN_BATCHSIZE, 1000);
		File idDirectory = props.getRequired(StrategyWrapperMain.OPTION_IDFOUNTAIN_DIR, File.class);
		IOH.ensureDir(idDirectory);
		BatchIdGenerator.Factory<Long> fountain = new BatchIdGenerator.Factory<Long>(new FileBackedIdGenerator.Factory(idDirectory), idFountainBatchsize);
		container.getServices().setTicketGenerator(
				new DailyIdGen(formatter.getDateFormatter(LocaleFormatter.DATE), clock, systemName, new BasicNamespaceIdGenerator<Long>(fountain)));
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

		// Snapshot request and response for orders
		connection.addTopic(new MsgDirectTopicConfiguration("fe.snapshot.request", host, port));
		connection.addTopic(new MsgDirectTopicConfiguration("fe.snapshot.response", host, port));
		connection.addTopic(new MsgDirectTopicConfiguration("fe.deltas.outgoing", host, port));

		connection.addTopic(new MsgDirectTopicConfiguration("fe.admin.incoming", host, port));

		// Send and receive alerts from OMS
		connection.addTopic(new MsgDirectTopicConfiguration("fe.ofr.outgoing", host, port));
		connection.addTopic(new MsgDirectTopicConfiguration("fe.ofr.incoming", host, port));

		String refDataHost = bootstrap.getProperties().getRequired(StrategyWrapperMain.OPTION_REFDATA_HOST);
		int refDataPort = bootstrap.getProperties().getRequired(StrategyWrapperMain.OPTION_REFDATA_PORT, Integer.class);

		// Send and receive OMS actions from OMS
		connection.addTopic(new MsgDirectTopicConfiguration("ofr.oms.request", host, port));
		connection.addTopic(new MsgDirectTopicConfiguration("oms.ofr.response", host, port));
		connection.addTopic(new MsgDirectTopicConfiguration("refdata.clientToServer", refDataHost, refDataPort, "refdata.clientToServer"));
		connection.addTopic(new MsgDirectTopicConfiguration("refdata.serverToClient", refDataHost, refDataPort, "refdata.serverToClient"));

		bootstrap.registerConsoleObject("oms_connection", new MsgConsole(connection));
		final MsgSuite detlasMsgSuite = new MsgSuite("OMSCLIENT_CONNECTION", connection, "fe.deltas.outgoing", null, uid);
		final MsgSuite alertsMsgSuite = new MsgSuite("OMSCLIENT_CONNECTION", connection, "fe.ofr.outgoing", "fe.ofr.incoming", uid);
		final MsgSuite snapshotMsgSuite = new MsgSuite("TOFRONTEND", connection, "fe.snapshot.response", "fe.snapshot.request", uid);
		final MsgSuite omsCommandSuite = new MsgSuite("OMSCOMMAND", connection, "oms.ofr.response", "ofr.oms.request", uid);

		refData = new RefDataClientSuite("REFDATA");
		MsgSuite refdataSuite = new MsgSuite("REFDATA", connection, "refdata.serverToClient", "refdata.clientToServer", uid);
		Suite rootSuite = container.getRootSuite();

		omsClientSuite = new OmsClientSuite(false);
		rootSuite.addChildren(detlasMsgSuite, omsClientSuite, snapshotMsgSuite, omsCommandSuite, alertsMsgSuite, refData, refdataSuite);
		refrequest = rootSuite.exposeInputPortAsOutput(refData.requestPort, true);
		ContainerHelper.wireCast(rootSuite, alertsMsgSuite.inboundOutputPort, omsClientSuite.broadcastInputPort, true);
		ContainerHelper.wireCast(rootSuite, detlasMsgSuite.inboundOutputPort, omsClientSuite.notificationInputPort, true);
		StrategyWrapper adapter = new StrategyWrapper();
		OnTimerProcessor timerProcessor = new OnTimerProcessor();
		OnOmsResponseProcessor omsResponseProcessor = new OnOmsResponseProcessor();
		rootSuite.addChildren(adapter, timerProcessor, omsResponseProcessor);
		rootSuite.wire(adapter.timerPort, timerProcessor, true);
		rootSuite.wire(adapter.fromOmsResponsePort, omsResponseProcessor, true);
		rootSuite.wire(adapter.toOmsRequestPort, omsCommandSuite.getOutboundInputPort(), true);
		rootSuite.wire(adapter.toOms, omsCommandSuite.getOutboundInputPort(), true);
		rootSuite.wire(adapter.alertPort, alertsMsgSuite.getOutboundInputPort(), true);
		rootSuite.wire(omsClientSuite.snapshotRequestOutputPort, snapshotMsgSuite.getOutboundInputPort(), true);

		rootSuite.wire(refData.toServerPort, refdataSuite.outboundInputPort, false);
		rootSuite.wire(snapshotMsgSuite.statusPort, omsClientSuite.statusInputPort, true);

		rootSuite.wire(omsClientSuite.clientNotificationOutputPort, adapter, false);

		adapter.registerStrategyFactory(new SimpleStrategyFactory<TestStrategy>(TestStrategy.ID, rootSuite.getGenerator(TestStrategy.class)));
		adapter.registerStrategyFactory(new SimpleStrategyFactory<SlicerStrategy>(SlicerStrategy.ID, rootSuite.getGenerator(SlicerStrategy.class)));
	}

	public Container getContainer() {
		return container;
	}

	public void init() {
		refreg = container.getGenerator(RefDataRequestMessage.class);
		bootstrap.startupContainer(container);
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

	public RequestOutputPort<RefDataRequestMessage, RefDataInfoMessage> getRefDataPort() {
		return refrequest;
	}

}
