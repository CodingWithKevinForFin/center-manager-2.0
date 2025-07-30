package com.f1.fixomsclient;

import com.f1.container.ResultMessage;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.Order;
import com.f1.povo.standard.MapMessage;
import com.f1.povo.standard.TextMessage;

/**
 * Is used in conjunction with an {@link OmsClientOrdersExecutionsManager} manager. Classes implementing this interface, and added to said manager will receive notifications about
 * changes to orders and executions. In addition, it will also notify of messages broadcast from another client.
 * 
 * @see OmsClientOrdersExecutionsManager#addGuiManagerListener(OmsClientOrdersExecutionsListener)
 */
public interface OmsClientOrdersExecutionsListener {

	/**
	 * Called when a new order is created.
	 * 
	 * @param manager
	 *            contains a snapshot of all subscribed orders and executions.
	 * @param order
	 *            the order aded.
	 * @param action
	 *            the action which invoked the creation of the order
	 */
	public void onNewOrder(OmsClientOrdersExecutions manager, Order order, OmsAction action);

	/**
	 * Called when an order is updated
	 * 
	 * @param manager
	 *            contains a snapshot of all subscribed orders and executions.
	 * @param old
	 *            the old state of the order.
	 * @param nuw
	 *            the new state of the order.
	 * @param action
	 *            the action which invoked the change in the order
	 */
	public void onUpdateOrder(OmsClientOrdersExecutions manager, Order old, Order nuw, OmsAction action);

	/**
	 * Called when a new execution is created.
	 * 
	 * @param manager
	 *            contains a snapshot of all subscribed orders and executions.
	 * @param execution
	 *            the execution which was added.
	 * @param action
	 *            the action which invoked the change in the order
	 */
	public void onNewExecution(OmsClientOrdersExecutions manager, Execution execution, OmsAction action);

	/**
	 * Called when a connection has been made and the subscription for orders and executions has been sent. Please note, that following this a snapshot of all orders and executions
	 * sent.
	 * 
	 * @param manager
	 *            contains a snapshot of all subscribed orders and executions.
	 */
	public void onConnected(OmsClientOrdersExecutionsManager manager);

	/**
	 * Called when a connection has been made and the subscription for orders and executions has been sent. Please note, that following this the manager will continue to hold a
	 * reference to last know state of the orders and executions, but there will be no further updates
	 * 
	 * @param manager
	 *            contains a snapshot of all subscribed orders and executions.
	 */
	public void onDisconnected(OmsClientOrdersExecutionsManager manager);

	/**
	 * called after the manager has been 'started',meaning that it can now connect.
	 * 
	 * @param manager
	 */
	public void onStarted(OmsClientOrdersExecutionsManager manager);

	/**
	 * called when a client broadcast has been sent. Broadcasts allow for various clients to <i>broadcast</i> data to all other clients.
	 * 
	 * @param manager
	 *            contains a snapshot of all subscribed orders and executions.
	 * @param clientBroadcast
	 *            a custom message containing user defined data which should be well-known by both the sending and receiving parties.
	 */
	public void onRcvBroadcast(OmsClientOrdersExecutionsManager manager, MapMessage clientBroadcast);

	/**
	 * Called after a batch of messages has finished processing
	 * 
	 * @param action
	 */
	public void onTransactionComplete(OmsClientNotification action);

	/**
	 * gets called when a response is avaible for a prior request to the assocationed {@link OmsClientOrdersExecutionsManager}
	 * 
	 * @param response
	 *            the response, note you can get to the original request by calling {@link ResultMessage#getRequestMessage()}
	 */
	public void onResponse(OmsClientOrdersExecutionsManager manager, ResultMessage<TextMessage> response);

}
