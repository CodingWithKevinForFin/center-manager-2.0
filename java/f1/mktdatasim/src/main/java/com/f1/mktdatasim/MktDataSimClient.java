package com.f1.mktdatasim;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.f1.base.Message;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.OutputPort;
import com.f1.container.impl.BasicContainer;
import com.f1.container.impl.BasicSuite;
import com.f1.container.impl.ContainerHelper;
import com.f1.mktdata.MktDataListener;
import com.f1.mktdata.MktDataManager;
import com.f1.msg.MsgInputTopic;
import com.f1.msg.impl.BasicMsgConnectionConfiguration;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.msgdirect.MsgDirectTopicConfiguration;
import com.f1.pofo.mktdata.LevelOneData;
import com.f1.pofo.mktdata.LevelOneSubscribeRequest;
import com.f1.pofo.mktdata.LevelOneUnsubscribeRequest;
import com.f1.refdata.RefDataManager;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.utils.GuidHelper;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;

public class MktDataSimClient extends BasicSuite implements MktDataManager {

	private RefDataManager refDataManager;

	public MktDataSimClient(RefDataManager refDataManager) {
		this.refDataManager = refDataManager;
	}

	public static void main(String a[]) throws FileNotFoundException {
		ContainerBootstrap bs = new ContainerBootstrap(MktDataSimClient.class, a);
		bs.setConfigDirProperty("./src/main/config");
		MktDataSimClient suite = new MktDataSimClient(null);
		BasicContainer container = new BasicContainer();
		container.getRootSuite().addChild(suite);
		bs.startup();
		bs.startupContainer(container);

		suite.addListener(new MktDataListener() {

			@Override
			public void onLogin(MktDataManager manager) {

			}

			@Override
			public void onLoginFailure(MktDataManager manager) {
			}

			@Override
			public void onDisconnected(MktDataManager manager) {
			}

			@Override
			public void onLevelOneData(MktDataManager manager, LevelOneData data) {
				System.out.println("Data received: " + data);

			}
		});

		OH.sleep(1000);
		suite.subscribe(0);

	}

	private final MktDataSimPublishProcessor pubProcessor = new MktDataSimPublishProcessor();
	private OutputPort<LevelOneSubscribeRequest> mdssPort;
	private OutputPort<LevelOneUnsubscribeRequest> mdusPort;
	private List<MktDataListener> listeners = new CopyOnWriteArrayList<MktDataListener>();
	private MsgInputTopic channel;
	private OutputPort<Message> outbound;

	public void init() {
		super.init();
		getServices().getGenerator().register(LevelOneData.class);
		getServices().getGenerator().register(LevelOneSubscribeRequest.class);
		getServices().getGenerator().register(LevelOneUnsubscribeRequest.class);
		PropertyController props = getTools();
		addChildren(pubProcessor);

		int simPort = props.getRequired("mktdatasim.port", Integer.class);
		String simHost = props.getRequired("mktdatasim.host");
		String uid = GuidHelper.getGuid();
		MsgDirectConnection connection = new MsgDirectConnection(new BasicMsgConnectionConfiguration("connection1"));
		connection.addTopic(new MsgDirectTopicConfiguration("mktdatasim.clientToSim", simHost, simPort, "mktdatasim.clientToSim"));
		connection.addTopic(new MsgDirectTopicConfiguration("mktdatasim.simToClient", simHost, simPort, "mktdatasim.simToClient"));
		this.channel = connection.getInputTopic("mktdatasim.simToClient");

		MsgSuite suite = new MsgSuite("TOMKTDATASIM", connection, "mktdatasim.simToClient", "mktdatasim.clientToSim", uid);
		pubProcessor.bindToPartition("TOMKTDATASIMPUB");
		addChildren(suite);
		outbound = exposeInputPortAsOutput(suite.outboundInputPort, true);
		ContainerHelper.wireCast(this, suite.inboundOutputPort, pubProcessor, true);
		pubProcessor.setSubscribed(subscribed);
		pubProcessor.setListeners(listeners);
	}

	public boolean isConnected() {
		return channel.getExternalConnections().iterator().hasNext();
	}

	public void unsubscribe(int itemName) {
		subscribed.remove(itemName);
		LevelOneUnsubscribeRequest a = nw(LevelOneUnsubscribeRequest.class);
		a.setSecurityRefId(itemName);
		outbound.send(a, null);
	}

	private Map<Integer, Integer> subscribed = new ConcurrentHashMap<Integer, Integer>();

	public void subscribe(int itemName) {
		LevelOneSubscribeRequest a = nw(LevelOneSubscribeRequest.class);
		a.setSecurityRefId(itemName);
		outbound.send(a, null);
		subscribed.put(itemName, itemName);
	}

	public void addListener(MktDataListener listener) {
		listeners.add(listener);
	}

	public void removeListener(MktDataListener listener) {
		listeners.remove(listener);

	}

	@Override
	public Set<Integer> getSubscribedNames() {
		return subscribed.keySet();
	}
}
