package com.f1.fixomsclient;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.f1.container.OutputPort;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.OmsSnapshotRequest;
import com.f1.pofo.oms.Order;
import com.f1.utils.structs.Tuple2;

/**
 * 
 * The interface for Receiving updates from a
 * 
 */
public class SimpleOmsClientOrdersExecutionsManager extends BasicProcessor<OmsClientNotification, OmsClientState> implements OmsClientOrdersExecutionsManager {

	private final List<OmsClientOrdersExecutionsListener> listeners = new CopyOnWriteArrayList<OmsClientOrdersExecutionsListener>();
	final public RequestOutputPort<OmsSnapshotRequest, OmsNotification> snapshotRequestPort = newRequestOutputPort(OmsSnapshotRequest.class, OmsNotification.class);
	final public OutputPort<ResultMessage<OmsNotification>> snapshotResponsePort = snapshotRequestPort.getResponsePort();
	final private String partitionId;
	public SimpleOmsClientOrdersExecutionsManager(String partitionId) {
		super(OmsClientNotification.class, OmsClientState.class);
		this.partitionId = partitionId;
		bindToPartition(partitionId);
	}

	/**
	 * Processes incoming actions... Do not call directly
	 */
	@Override
	public void processAction(OmsClientNotification action, OmsClientState state, ThreadScope threadScope) throws Exception {
		for (OmsClientOrdersExecutionsListener listener : listeners) {
			if (action.getType() == null) {
				listener.onRcvBroadcast(this, action.getClientBroadcast());
			} else {
				for (Order order : action.getAddedOrders())
					listener.onNewOrder(state, order, action.getType());

				for (Tuple2<Order, Order> order : action.getUpdatedOrders())
					listener.onUpdateOrder(state, order.getA(), order.getB(), action.getType());

				for (Execution execution : action.getAddedExecutions())
					listener.onNewExecution(state, execution, action.getType());
			}
			listener.onTransactionComplete(action);
		}
	}

	@Override
	public void addGuiManagerListener(OmsClientOrdersExecutionsListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeManagerListener(OmsClientOrdersExecutionsListener listener) {
		listeners.remove(listener);
	}

	public void fireOnConnected() {
		for (OmsClientOrdersExecutionsListener listener : listeners) {
			listener.onConnected(this);
		}
	}

	@Override
	public OmsClientOrdersExecutions borrowOrdersExecutions() {
		OmsClientState r = (OmsClientState) getContainer().getPartitionController().getState(partitionId, OmsClientState.class);
		if (!r.getPartition().lockForRead(2, TimeUnit.SECONDS))
			throw new RuntimeException("could not borrow connection, timeout");
		return r;

	}

	@Override
	public void freeOrderExecutions(OmsClientOrdersExecutions orderExecutions) {
		OmsClientState r = (OmsClientState) orderExecutions;
		r.getPartition().unlockForRead();
	}

	public void fireOnDisconnected() {
		for (OmsClientOrdersExecutionsListener listener : listeners) {
			listener.onDisconnected(this);
		}
	}

	public void requestSnapshot() {
		OmsSnapshotRequest req = nw(OmsSnapshotRequest.class);
		snapshotRequestPort.requestWithFuture(req, null);
	}

	@Override
	public Iterable<OmsClientOrdersExecutionsListener> getListeners() {
		return listeners;
	}

	@Override
	public void start() {
		super.start();
		for (OmsClientOrdersExecutionsListener listener : listeners) {
			listener.onStarted(this);
		}
	}


}
