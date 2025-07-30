package com.f1.fixomsclient;

import com.f1.base.Action;
import com.f1.base.Message;
import com.f1.container.InputPort;
import com.f1.container.OutputPort;
import com.f1.container.RequestInputPort;
import com.f1.container.RequestOutputPort;
import com.f1.container.impl.BasicPartitionResolver;
import com.f1.container.impl.BasicSuite;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.OmsSnapshotRequest;
import com.f1.povo.msg.MsgStatusMessage;
import com.f1.povo.standard.MapMessage;

public class OmsClientSuite extends BasicSuite {

	public final InputPort<OmsNotification> notificationInputPort;
	public final RequestOutputPort<OmsSnapshotRequest, OmsNotification> snapshotRequestOutputPort;
	public final OutputPort<OmsClientNotification> clientNotificationOutputPort;
	public final RequestInputPort<OmsSnapshotRequest, OmsNotification> snapshotRequestInputPort;
	public final InputPort<MapMessage> broadcastInputPort;
	public final InputPort<MsgStatusMessage> statusInputPort;
	public final RequestInputPort<Message, OmsClientNotification> getLocalSnapshotPort;
	final private OmsClientNotificationProcessor notificationProcessor;
	final private OmsClientSnapshotRequestProcessor requestSnapshotProcessor;
	final private OmsClientSnapshotResponseProcessor responseSnapshotProcessor;
	final private OmsClientBroadcastTransformProcessor broadcastProcessor;
	final private OmsClientStatusProcessor statusProcessor;
	final private OmsClientGetLocalSnapshotProcessor localSnapshotProcessor;
	private boolean singleState;

	public OmsClientSuite(boolean singleState) {
		this.singleState = singleState;
		this.notificationInputPort = newInputPort(OmsNotification.class);
		this.snapshotRequestOutputPort = newRequestOutputPort(OmsSnapshotRequest.class, OmsNotification.class);
		this.clientNotificationOutputPort = newOutputPort(OmsClientNotification.class);
		this.snapshotRequestInputPort = newRequestInputPort(OmsSnapshotRequest.class, OmsNotification.class);
		this.broadcastInputPort = newInputPort(MapMessage.class);
		this.notificationProcessor = new OmsClientNotificationProcessor();
		this.requestSnapshotProcessor = new OmsClientSnapshotRequestProcessor();
		this.responseSnapshotProcessor = new OmsClientSnapshotResponseProcessor();
		this.statusProcessor = new OmsClientStatusProcessor();
		this.broadcastProcessor = new OmsClientBroadcastTransformProcessor();
		this.localSnapshotProcessor = new OmsClientGetLocalSnapshotProcessor();
		addChildren(notificationProcessor, this.requestSnapshotProcessor, responseSnapshotProcessor, broadcastProcessor, statusProcessor, localSnapshotProcessor);

		this.exposeInputPortTo(this.notificationProcessor, this.notificationInputPort);
		this.exposeOutputPortTo(this.notificationProcessor.out, this.clientNotificationOutputPort);
		this.exposeInputPortTo(this.requestSnapshotProcessor, this.snapshotRequestInputPort);
		this.exposeOutputPortTo(this.requestSnapshotProcessor.outputPort, this.snapshotRequestOutputPort);
		this.exposeInputPortTo(this.broadcastProcessor, this.broadcastInputPort);
		this.statusInputPort = this.exposeInputPort(statusProcessor);
		this.getLocalSnapshotPort = this.exposeInputPort(localSnapshotProcessor);
		this.wire(requestSnapshotProcessor.responsePort, responseSnapshotProcessor, true);
		this.wire(responseSnapshotProcessor.output, notificationProcessor, true);
		this.wire(statusProcessor.output, requestSnapshotProcessor, false);
		applyPartitionResolver(new BasicPartitionResolver<Action>(Action.class, "OMSCLIENTSUITE"), true, true);
		if (!singleState) {
			this.notificationProcessor.setPartitionResolver(new RootOrderIdOmsClientStateResolver());
		}
	}

	public OmsClientOrdersExecutionsManager createManager(String partitionId) {
		assertNotStarted();
		SimpleOmsClientOrdersExecutionsManager r = new SimpleOmsClientOrdersExecutionsManager(partitionId);
		addChild(r);
		wire(clientNotificationOutputPort, r, false);
		wire(r.snapshotRequestPort, requestSnapshotProcessor, true);
		wire(r.snapshotResponsePort, responseSnapshotProcessor, true);
		wire(broadcastProcessor.toClient, r, true);
		return r;
	}

}
