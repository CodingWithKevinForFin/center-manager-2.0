package app.controlpanel;

import java.io.IOException;
import java.util.Collection;

import com.f1.base.Action;
import com.f1.base.IdeableGenerator;
import com.f1.base.Message;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.OutputPort;
import com.f1.container.RequestOutputPort;
import com.f1.container.Suite;
import com.f1.container.impl.BasicContainer;
import com.f1.container.impl.BasicPartitionResolver;
import com.f1.container.impl.ContainerHelper;
import com.f1.fixomsclient.OmsClientOrdersExecutionsListener;
import com.f1.fixomsclient.OmsClientOrdersExecutionsManager;
import com.f1.fixomsclient.OmsClientSuite;
import com.f1.msg.MsgConnection;
import com.f1.msg.MsgConnectionListener;
import com.f1.msg.MsgInputTopic;
import com.f1.msg.MsgOutputTopic;
import com.f1.msg.MsgTopic;
import com.f1.msg.impl.BasicMsgConnectionConfiguration;
import com.f1.msg.impl.MsgConsole;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.msgdirect.MsgDirectTopicConfiguration;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.OmsSnapshotRequest;
import com.f1.povo.standard.MapMessage;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.utils.ClassFinder;
import com.f1.utils.GuidHelper;
import com.f1.utils.OfflineConverter;
import com.f1.utils.mirror.reflect.ReflectedClassMirror;

public class GuiEventHandler implements MsgConnectionListener {

	final private OfflineConverter converter;
	final private IdeableGenerator generator;
	final private MsgDirectConnection connection;
	final private String uid;
	private boolean connected;
	final private String host;
	final private int port;
	final private OmsClientOrdersExecutionsManager manager;
	final private BasicContainer container;
	final private ContainerBootstrap bootstrap;
	private OmsClientSuite omsClientSuite;
	// private OutputPort<RequestMessage<OmsSnapshotRequest>>
	// snapshotRequestPort;
	private SjlsAlertsProcessor alertsProcessor;
	private RequestOutputPort<OmsSnapshotRequest, OmsNotification> ofrRequestPort;
	final public OutputPort<Message> alertPort;// =
												// newOutputPort(MapMessage.class);
	final MsgSuite alertsMsgSuite;

	public GuiEventHandler(ContainerBootstrap bootstrap, String host, int port) throws IOException {

		this.container = new BasicContainer();
		this.bootstrap = bootstrap;
		bootstrap.prepareContainer(container);
		bootstrap.setMessagePackagesProperty("com.f1.pofo");
		this.uid = GuidHelper.getGuid();
		this.port = port;
		this.host = host;
		this.connected = false;
		this.converter = bootstrap.getConverter();
		this.generator = this.converter.getIdeableGenerator();
		ClassFinder finder = new ClassFinder().searchClasspath(ClassFinder.TYPE_DIRECTORY | ClassFinder.TYPE_JAR);
		Collection<Class> classes = finder.filterByPackage("com.f1.pofo").toReflected().filterByExtends(ReflectedClassMirror.valueOf(Message.class)).getClasses();
		this.generator.register(classes.toArray(new Class[classes.size()]));
		final String url = host + ":" + port;
		connection = new MsgDirectConnection(new BasicMsgConnectionConfiguration("connection1"));
		connection.addMsgConnectionListener(this);
		connection.addTopic(new MsgDirectTopicConfiguration("fe.snapshot.request", host, port, "fe.snapshot.request"));
		connection.addTopic(new MsgDirectTopicConfiguration("fe.snapshot.response", host, port, "fe.snapshot.response"));
		connection.addTopic(new MsgDirectTopicConfiguration("fe.deltas.outgoing", host, port, "fe.deltas.outgoing"));
		connection.addTopic(new MsgDirectTopicConfiguration("fe.admin.incoming", host, port, "fe.admin.incoming"));
		connection.addTopic(new MsgDirectTopicConfiguration("fe.ofr.outgoing", host, port, "fe.ofr.outgoing"));
		connection.addTopic(new MsgDirectTopicConfiguration("fe.ofr.incoming", host, port, "fe.ofr.incoming"));

		bootstrap.registerConsoleObject("con", new MsgConsole(connection));
		final MsgSuite detlasMsgSuite = new MsgSuite("OMSCLIENT_CONNECTION", connection, "fe.deltas.outgoing", null, uid);
		alertsMsgSuite = new MsgSuite("OMSCLIENT_CONNECTION", connection, "fe.ofr.outgoing", "fe.ofr.incoming", uid);
		final MsgSuite snapshotMsgSuite = new MsgSuite("TOFRONTEND", connection, "fe.snapshot.response", "fe.snapshot.request", uid);
		omsClientSuite = new OmsClientSuite(true);
		omsClientSuite.applyPartitionResolver(new BasicPartitionResolver<Action>(Action.class, "OMSCLIENT_ORDEREXECS"), true, true);
		Suite rootSuite = container.getRootSuite();
		rootSuite.addChildren(detlasMsgSuite, omsClientSuite, snapshotMsgSuite, alertsMsgSuite);
		ContainerHelper.wireCast(rootSuite, detlasMsgSuite.inboundOutputPort, omsClientSuite.notificationInputPort, true);
		rootSuite.wire(omsClientSuite.snapshotRequestOutputPort, snapshotMsgSuite.getOutboundInputPort(), true);
		ofrRequestPort = rootSuite.exposeInputPortAsOutput(omsClientSuite.snapshotRequestInputPort, true);
		this.manager = omsClientSuite.createManager("OMSCLIENT_ORDEREXECS");
		alertPort = rootSuite.exposeInputPortAsOutput(alertsMsgSuite.getOutboundInputPort(), true);
		ContainerHelper.wireCast(rootSuite, alertsMsgSuite.inboundOutputPort, omsClientSuite.broadcastInputPort, true);
	}

	public void init() {
		bootstrap.startupContainer(container);
		// Suite rootSuite = getContainer().getRootSuite();
		// rootSuite.addChildren(this);
		// rootSuite.wire(alertPort, alertsMsgSuite.getOutboundInputPort(),
		// true);
	}

	@Override
	public void onDisconnect(MsgConnection connection, MsgTopic topic, String channel, String suffix, String remoteHost, boolean isWrite) {
		if (this.connected) {
			this.connected = false;
			for (OmsClientOrdersExecutionsListener e : manager.getListeners())
				e.onDisconnected(manager);
		}
	}

	@Override
	public void onConnect(MsgConnection connection, MsgTopic topic, String channel, String suffix, String remoteHost, boolean isWrite) {
		if (!this.connected) {
			this.connected = true;
			for (OmsClientOrdersExecutionsListener e : manager.getListeners())
				e.onConnected(manager);
			requestSnapshot();
		}
	}

	private void requestSnapshot() {
		manager.requestSnapshot();
		// OmsSnapshotRequest msg = generator.nw(OmsSnapshotRequest.class);
		// ofrRequestPort.request(msg, null);
		// RequestMessage ra = generator.nw(RequestMessage.class);
		// ra.setAction(req);
		// MsgMessage msg = generator.nw(MsgMessage.class);
		// msg.setMessage(req);
		// msg.setResultTopicSuffix(uid);
		// MsgBytesEvent msgEvent = new
		// MsgBytesEvent(converter.object2Bytes(msg));
		// connection.getOutputChannel("fe.snapshot.request").send(msgEvent);
	}

	public String getHost() {
		return host;
	}

	public void sendBroadCast(MapMessage m) {
		alertPort.send(m, null);
	}

	public int getPort() {
		return port;
	}

	public OmsClientOrdersExecutionsManager getManager() {
		return manager;
	}

	public BasicContainer getContainer() {
		return container;
	}

	@Override
	public void onNewInputTopic(MsgConnection connection, MsgInputTopic r) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNewOutputTopic(MsgConnection connection, MsgOutputTopic r) {
		// TODO Auto-generated method stub

	}
}
