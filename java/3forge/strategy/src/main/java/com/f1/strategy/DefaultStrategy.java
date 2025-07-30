package com.f1.strategy;

import com.f1.base.Action;
import com.f1.fixomsclient.OmsClientState;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.Order;
import com.f1.utils.Timer;

public class DefaultStrategy implements Strategy {
	public static String ID = "DFLT";

	private OrderManager orderManager;

	@Override
	public void onNewOrder(Order o) {
	}

	@Override
	public void onReplaceOrder(Order o, Order nuw) {
		getOrderManager().orderStatusUpdate(OmsAction.ORDER_REPLACED, getOmsClientState().getRootOrderId());
	}
	@Override
	public void onCancelOrder(Order o) {
		getOrderManager().orderStatusUpdate(OmsAction.ORDER_CANCELLED, getOmsClientState().getRootOrderId());
	}

	@Override
	public void onExecOrder(Order o, Execution e) {
	}

	@Override
	public void onCancelledOrder(Order o) {
	}

	@Override
	public void onReplacedOrder(Order old, Order nuw) {
	}

	@Override
	public void onChildAcked(Order nuw) {
	}

	@Override
	public void onChildCancelled(Order nuw) {
	}

	@Override
	public void onChildRejected(Order nuw) {
	}

	@Override
	public void onChildReplaced(Order old, Order nuw) {
	}

	@Override
	public void init(OrderManager orderManager) {
		if (this.orderManager != null)
			throw new IllegalStateException();
		this.orderManager = orderManager;
	}

	@Override
	public OrderManager getOrderManager() {
		return orderManager;
	}

	@Override
	public void onTimer(Timer timer, long scheduledTime, long now) {
	}
	@Override
	public void onTimerDone(Timer timer) {
	}

	protected OmsClientState getOmsClientState() {
		return orderManager.getOmsClientState();
	}

	@Override
	public void onOmsResponse(Action request, String text) {

	}

}
