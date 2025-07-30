package com.f1.fix.oms.adapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultActionFuture;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.container.impl.BasicState;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.OmsSnapshotRequest;
import com.f1.povo.standard.ObjectMessage;
import com.f1.utils.LH;

/**
 * 
 * receives a snapshot request and produces a snapshot in the form of an {@link OmsNotification} with an action type of {@link OmsAction#SNAPSHOT}.
 * 
 */
public class SnapshotRequestProcessor extends BasicRequestProcessor<OmsSnapshotRequest, BasicState, OmsNotification> {

	private ObjectGeneratorForClass<OmsNotification> notifgen;
	private ObjectGeneratorForClass<ObjectMessage> requestgen;
	public final RequestOutputPort<ObjectMessage, OmsNotification> toOrderRequestProcessor = newRequestOutputPort(ObjectMessage.class, OmsNotification.class);

	public SnapshotRequestProcessor() {
		super(OmsSnapshotRequest.class, BasicState.class, OmsNotification.class);
		bindToPartition("SNAPSHOT");
	}

	public void start() {
		super.start();
		notifgen = getGenerator(OmsNotification.class);
		requestgen = getGenerator(ObjectMessage.class);
	}

	@Override
	protected OmsNotification processRequest(RequestMessage<OmsSnapshotRequest> action, BasicState state, ThreadScope threadScope) {
		List<ResultActionFuture<OmsNotification>> futures = new LinkedList();
		OmsNotification notif = notifgen.nw();
		notif.setType(OmsAction.SNAPSHOT);
		notif.setSnapshotNotifications(new ArrayList<OmsNotification>());
		for (Object o : getContainer().getPartitionController().getPartitions()) {
			OmsOrderState orderState = (OmsOrderState) getContainer().getPartitionController().getState(o, OmsOrderState.class);
			if (orderState != null) {
				ObjectMessage req = requestgen.nw();
				ResultActionFuture<OmsNotification> future = toOrderRequestProcessor.requestWithFuture(req, o, threadScope);
				futures.add(future);
			}
		}
		for (ResultActionFuture<OmsNotification> future : futures) {
			OmsNotification notification = future.getResult().getAction();
			if (notification != null) {
				notif.getSnapshotNotifications().add(notification);
				//TODO: this should really go away... no need to duplicate!
			} else {
				LH.severe(log, "Could not retrieve snapshot for ", future.getResult().getRequestMessage());
			}
		}
		return notif;
	}

}
