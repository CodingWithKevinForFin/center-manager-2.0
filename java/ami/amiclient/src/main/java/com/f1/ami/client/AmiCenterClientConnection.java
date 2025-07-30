package com.f1.ami.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.f1.ami.amicommon.AmiCenterDefinition;
import com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotRequest;
import com.f1.ami.amicommon.centerclient.AmiCenterClientInboundProcessor;
import com.f1.ami.amicommon.centerclient.AmiCenterClientListener;
import com.f1.ami.amicommon.centerclient.AmiCenterClientOutboundProcessor;
import com.f1.ami.amicommon.centerclient.AmiCenterClientState;
import com.f1.base.Message;
import com.f1.bootstrap.Bootstrap;
import com.f1.container.InputPort;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.Suite;
import com.f1.container.impl.BasicSuite;
import com.f1.msgdirect.MsgDirectConnection;
import com.f1.suite.utils.msg.MsgSuite;
import com.f1.utils.CH;

public class AmiCenterClientConnection {
	private static final String GUI_PARTITION = "SNAPSHOT_PARTITION";
	private static AtomicInteger nextId = new AtomicInteger();

	final AmiCenterDefinition centerDef;
	final BasicSuite parentSuite;
	final InputPort<RequestMessage<? extends Message>> out;
	final private Set<String> subscribed = new HashSet<String>();
	final private AmiCenterClient client;
	final private RequestOutputPort outboundPort;
	//	final private RequestOutputPort outboundPort;
	final private AmiCenterClientInboundProcessor cacheInboundProcessor;
	final private AmiCenterClientOutboundProcessor cacheOutboundProcessor;
	final private AmiCenterClientState cache;
	private MsgDirectConnection connection;
	final private String partitionId = AmiCenterClientState.PARTITION_PREFIX + nextId.incrementAndGet();

	public AmiCenterClientConnection(AmiCenterClient amiCenterClient, AmiCenterDefinition center, AmiCenterClientListener listener) {
		this.client = amiCenterClient;
		this.centerDef = center;
		BasicSuite suite = new BasicSuite();
		byte centerId = center.getId();
		this.connection = center.newMsgDirectConnection("centerClient_" + center.getName(), "center.to.web", "web.to.center");
		MsgSuite snapshotSuite = new MsgSuite(GUI_PARTITION, connection, "center.to.web", "web.to.center", Bootstrap.getProcessUid());
		suite.addChildren(snapshotSuite);
		int maxBatchSize = 20000;
		this.cache = new AmiCenterClientState(centerId, center, maxBatchSize, Collections.EMPTY_SET, listener, client.getUsername());
		client.getContainer().getPartitionController().putState(partitionId, cache);
		this.cacheOutboundProcessor = new AmiCenterClientOutboundProcessor(centerId);
		this.cacheOutboundProcessor.bindToPartition(partitionId);
		this.cacheInboundProcessor = new AmiCenterClientInboundProcessor(centerId);
		this.cacheInboundProcessor.bindToPartition(partitionId);
		suite.addChildren(cacheOutboundProcessor, cacheInboundProcessor);
		suite.wire(cacheInboundProcessor.toCenter, snapshotSuite.outboundInputPort, true);
		suite.wire(cacheInboundProcessor.toAmi, cacheInboundProcessor, true);
		suite.wire(cacheOutboundProcessor.out, snapshotSuite.outboundInputPort, true);
		suite.wire(cacheOutboundProcessor.toAmiWeb, cacheInboundProcessor, true);
		suite.wire(snapshotSuite.inboundOutputPort, cacheInboundProcessor, true);
		suite.wire(snapshotSuite.statusPort, cacheInboundProcessor, true);
		InputPort<RequestMessage<? extends Message>> out = suite.exposeInputPort(cacheOutboundProcessor);
		cacheInboundProcessor.toUsers.setConnectionOptional(true);
		cacheOutboundProcessor.toUsers.setConnectionOptional(true);
		this.outboundPort = (RequestOutputPort) snapshotSuite.exposeInputPortAsOutput(snapshotSuite.outboundRequestInputPort, true);
		this.parentSuite = suite;
		this.out = out;

		Suite rs = client.getContainer().getSuiteController().getRootSuite();
		rs.addChild(suite);
	}
	synchronized public void unsubscribe(Set<String> types) {
		Set<String> t = CH.comm(this.subscribed, types, false, false, true);
		if (t.isEmpty())
			return;
		this.subscribed.removeAll(t);
		AmiCenterClientGetSnapshotRequest action = parentSuite.nw(AmiCenterClientGetSnapshotRequest.class);
		action.setAmiObjectTypesToStopSend(t);
		action.setInvokedBy(client.getUsername());
		parentSuite.getTools().request(out, action);
	}
	synchronized public void subscribe(Set<String> types) {
		Set<String> t = CH.comm(this.subscribed, types, false, true, false);
		if (t.isEmpty())
			return;
		this.subscribed.addAll(t);
		AmiCenterClientGetSnapshotRequest action = parentSuite.nw(AmiCenterClientGetSnapshotRequest.class);
		action.setAmiObjectTypesToSend(t);
		action.setInvokedBy(client.getUsername());
		parentSuite.getTools().request(out, action);
	}

	public RequestOutputPort getOutboundRequestPort() {
		return this.outboundPort;
	}
	public AmiCenterClientInboundProcessor getCacheInboundProcessor() {
		return this.cacheInboundProcessor;
	}
	public AmiCenterClientOutboundProcessor getCacheOutboundProcessor() {
		return this.cacheOutboundProcessor;
	}
	public AmiCenterClientState getCache() {
		return this.cache;
	}

	public AmiCenterDefinition getCenterDef() {
		return this.centerDef;
	}
	protected void close() {
		this.connection.close();
		this.cache.close();
	}

}
