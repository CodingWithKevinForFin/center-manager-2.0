package com.f1.fix.oms.schema;

import java.util.Map;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.base.ValuedListenable;
import com.f1.pofo.fix.FixRequest;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.Order;

/**
 * 
 * provides the tree structure for associating orders to the slices. Each
 * OmsOrder has a single pointer to the order data, a single pointer to its
 * parent a collection of pointers to the children.
 * 
 */
@VID("F1.OM.OR")
public interface OmsOrder extends ValuedListenable, Message {

	/**
	 * @return the related fix order
	 */
	@PID(1)
	public Order getFixOrder();

	public void setFixOrder(Order order);

	/**
	 * @return the actual parent order
	 */
	@PID(2)
	public OmsOrder getParentOrder();

	public void setParentOrder(OmsOrder parent);

	/**
	 * @return the child slices for this order
	 */
	@PID(3)
	public Map<String, OmsOrder> getSlices();

	public void setSlices(Map<String, OmsOrder> slices);

	/**
	 * @return status of the order
	 */
	@PID(4)
	public int getOrderStatus();

	public void setOrderStatus(int status);

	/**
	 * @return total number of leaves on the exchange
	 */
	@PID(5)
	public int getExchLeaves();

	public void setExchLeaves(int qty);

	/**
	 * @return during a replace request, this will be populated. if the replace
	 *         is rejected this will be discarded, otherwise when accepted it
	 *         will be applied to the order
	 */
	@PID(6)
	public FixRequest getPending();

	public void setPending(FixRequest pending);

	/**
	 * @return executions related to this order
	 */
	@PID(7)
	public Map<String, Execution> getExecutions();

	public void setExecutions(Map<String, Execution> execs);

}
