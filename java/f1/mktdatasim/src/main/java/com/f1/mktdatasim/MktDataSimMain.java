package com.f1.mktdatasim;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.f1.base.Message;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.Container;
import com.f1.container.OutputPort;
import com.f1.container.impl.BasicContainer;
import com.f1.container.impl.BasicSuite;
import com.f1.mktdata.MktDataListener;
import com.f1.msg.impl.BasicMsgConnectionConfiguration;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.msgdirect.MsgDirectTopicConfiguration;
import com.f1.pofo.mktdata.LevelOneData;
import com.f1.pofo.mktdata.LevelOneSubscribeRequest;
import com.f1.pofo.mktdata.LevelOneUnsubscribeRequest;
import com.f1.refdata.RefDataManager;
import com.f1.refdataclient.RefDataClientSuite;
import com.f1.suite.utils.ClassRoutingProcessor;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.utils.CachedFile;
import com.f1.utils.GuidHelper;
import com.f1.utils.PropertyController;

public class MktDataSimMain extends BasicSuite {

	private RefDataManager refDataManager;

	public MktDataSimMain(RefDataManager refDataManager) {
		this.refDataManager = refDataManager;
	}

	public static void main(String a[]) throws FileNotFoundException {
		ContainerBootstrap bs = new ContainerBootstrap(MktDataSimMain.class, a);
		bs.setConfigDirProperty("./src/main/config");
		MktDataSimMain suite = new MktDataSimMain(null);
		Container c = new BasicContainer();
		bs.setLoggingOverrideProperty("quiet");
		c.getRootSuite().addChild(suite);
		bs.startup();
		bs.startupContainer(c);
	}

	private final MktDataSimulatorProcessor mdsProcessor = new MktDataSimulatorProcessor();
	private final MktDataSimSubscribeProcessor mdssProcessor = new MktDataSimSubscribeProcessor();
	private final MktDataSimUnsubscribeProcessor mdusProcessor = new MktDataSimUnsubscribeProcessor();
	private final MktDataSimPublishProcessor pubProcessor = new MktDataSimPublishProcessor();
	private final MktDataSimReadConfigProcessor configProcessor = new MktDataSimReadConfigProcessor();
	private OutputPort<LevelOneSubscribeRequest> mdssPort;
	private OutputPort<LevelOneUnsubscribeRequest> mdusPort;
	private List<MktDataListener> listeners = new CopyOnWriteArrayList<MktDataListener>();
	private OutputPort<Message> configPort;

	public void init() {
		super.init();
		getServices().getGenerator().register(LevelOneData.class);
		getServices().getGenerator().register(LevelOneSubscribeRequest.class);
		getServices().getGenerator().register(LevelOneUnsubscribeRequest.class);
		PropertyController props = getTools();
		File stocksFile = props.getRequired("mktdatasim.file", File.class);
		if (!stocksFile.exists())
			throw new RuntimeException("file missing: " + stocksFile.toString());
		CachedFile cachedStocksFile = new CachedFile(stocksFile, 1000);
		mdsProcessor.bindToPartition("MKDSIM");
		mdssProcessor.bindToPartition("MKDSIM");
		mdusProcessor.bindToPartition("MKDSIM");
		pubProcessor.bindToPartition("MKDSIM");
		configProcessor.bindToPartition("MKDSIM");
		pubProcessor.setListeners(listeners);
		RefDataClientSuite refData = new RefDataClientSuite("REFDATA");
		getContainer().getPartitionController().putState("MKDSIM",
				new MktDataSimState(cachedStocksFile, getServices().getGenerator(), props, getServices().getClock().getTimeZone(), refDataManager));
		addChildren(mdsProcessor, mdssProcessor, mdusProcessor, pubProcessor, configProcessor, refData);
		wire(mdssProcessor.onSubscribe, mdsProcessor, false);
		wire(mdsProcessor.output, pubProcessor, false);
		wire(mdssProcessor.output, pubProcessor, false);
		wire(mdsProcessor.checkConfig, configProcessor, false);
		wire(configProcessor.refDataRequest, refData.requestPort, true);
		mdssPort = exposeInputPortAsOutput(mdssProcessor, true);
		mdusPort = exposeInputPortAsOutput(mdusProcessor, true);
		OutputPort<LevelOneSubscribeRequest> port = exposeInputPortAsOutput(mdssProcessor, true);

		int simPort = props.getRequired("mktdatasim.port", Integer.class);
		String refDataHost = props.getRequired("refdata.host");
		int refDataPort = props.getRequired("refdata.port", Integer.class);
		String uid = GuidHelper.getGuid();
		MsgDirectConnection connection = new MsgDirectConnection(new BasicMsgConnectionConfiguration("connection1"));
		connection.addTopic(new MsgDirectTopicConfiguration("mktdatasim.clientToSim", simPort, "mktdatasim.clientToSim"));
		connection.addTopic(new MsgDirectTopicConfiguration("mktdatasim.simToClient", simPort, "mktdatasim.simToClient"));
		connection.addTopic(new MsgDirectTopicConfiguration("refdata.clientToServer", refDataHost, refDataPort, "refdata.clientToServer"));
		connection.addTopic(new MsgDirectTopicConfiguration("refdata.serverToClient", refDataHost, refDataPort, "refdata.serverToClient"));

		MsgSuite suite = new MsgSuite("TOCLIENT", connection, "mktdatasim.clientToSim", "mktdatasim.simToClient", uid);
		MsgSuite refdataSuite = new MsgSuite("REFDATA", connection, "refdata.serverToClient", "refdata.clientToServer", uid);
		ClassRoutingProcessor<Message> router = new ClassRoutingProcessor<Message>(Message.class);
		router.bindToPartition("TOCLIENT");
		addChildren(suite, router, refdataSuite);
		wire(pubProcessor.output, suite.outboundInputPort, true);
		wire(suite.inboundOutputPort, router, false);
		wire(refData.toServerPort, refdataSuite.outboundInputPort, false);
		wire(router.newOutputPort(LevelOneSubscribeRequest.class), mdssProcessor, true);
		wire(router.newOutputPort(LevelOneUnsubscribeRequest.class), mdusProcessor, true);
		configPort = exposeInputPortAsOutput(configProcessor.getInputPort(), true);
	}

	@Override
	public void start() {
		super.start();
		configPort.send(nw(Message.class), null);
	}

	public boolean isConnected() {
		return isStarted();
	}

	private Map<Integer, Integer> subscribed = new ConcurrentHashMap<Integer, Integer>();

	public void unsubscribe(int itemName) {
		subscribed.remove(itemName);
		LevelOneUnsubscribeRequest a = nw(LevelOneUnsubscribeRequest.class);
		a.setSecurityRefId(itemName);
		mdusPort.send(a, null);
	}

	public Set<Integer> getSubscribedNames() {
		return subscribed.keySet();
	}

	public void subscribe(int itemName) {
		subscribed.put(itemName, itemName);
		LevelOneSubscribeRequest a = nw(LevelOneSubscribeRequest.class);
		a.setSecurityRefId(itemName);
		mdssPort.send(a, null);
	}

	public void addListener(MktDataListener listener) {
		listeners.add(listener);
	}

	public void removeListener(MktDataListener listener) {
		listeners.remove(listener);

	}
}
