package com.f1.fixomsclient;

import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.Order;

/**
 * Contains the current state of all orders and executions
 * 
 */
public interface OmsClientOrdersExecutions {

	/**
	 * 
	 * @param systemName
	 *            the system name which the order originated from
	 * @param orderId
	 *            the id of the order. See {@link Order#getId()}
	 * @return the existing order, or null if not found
	 */
	public Order getOrder(String systemName, String orderId);

	/**
	 * 
	 * @param systemName
	 *            the system name which the order originated from
	 * @param executionIdj
	 *            the id of the order. See {@link Execution#getId()}
	 * @return the existing execution, or null if not found
	 */
	public Execution getExecution(String systemName, String executionId);

	/**
	 * @return collection of current state of all orders subscribed to
	 */
	public Iterable<Order> getOrders();

	/**
	 * @return collection of current state of all executions subscribed to
	 */
	public Iterable<Execution> getExecutions();
}
