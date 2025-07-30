package com.f1.strategy;

import com.f1.base.Action;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.Order;
import com.f1.utils.Timer;

public interface Strategy {
	void onNewOrder(Order o);
	void onReplaceOrder(Order o, Order nuw);
	void onCancelOrder(Order o);
	void onExecOrder(Order o, Execution e);
	void onCancelledOrder(Order o);
	void onReplacedOrder(Order old, Order nuw);
	//public void onReplaceRejectedOrder()
	void onChildAcked(Order nuw);
	void onChildCancelled(Order nuw);
	void onChildRejected(Order nuw);
	void onChildReplaced(Order old, Order nuw);
	void init(OrderManager orderManager);
	OrderManager getOrderManager();
	void onOmsResponse(Action request, String text);
	void onTimerDone(Timer timer);
	void onTimer(Timer timer, long scheduledTime, long now);
}
