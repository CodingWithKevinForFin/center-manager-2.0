package com.f1.fixomsclient;

import java.util.List;

import com.f1.base.Message;
import com.f1.base.VID;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.Order;
import com.f1.povo.standard.MapMessage;
import com.f1.utils.structs.Tuple2;

/**
 * contains a batch of all changes for a particular oms transaction. This is sent by the oms client
 * 
 * @author rcooke
 * 
 */
@VID("F1.OC.CN")
public interface OmsClientNotification extends Message {

	/**
	 * @return orders that are added
	 */
	public List<Order> getAddedOrders();
	public void setAddedOrders(List<Order> orders);

	/**
	 * @return orders that were updated. getA() contains the old order state, getB() contains the new order state
	 */
	public List<Tuple2<Order, Order>> getUpdatedOrders();
	public void setUpdatedOrders(List<Tuple2<Order, Order>> orders);

	/**
	 * @return orders that were removed (purged)
	 */
	public List<Order> getDeletedOrders();
	public void setDeletedOrders(List<Order> orders);

	/**
	 * 
	 * @return executions that were added
	 */
	public List<Execution> getAddedExecutions();
	public void setAddedExecutions(List<Execution> orders);

	/**
	 * 
	 * @return executions that were deleted (purged)
	 */
	public List<Execution> getDeletedExecutions();
	public void setDeletedExecutions(List<Execution> orders);

	/**
	 * 
	 * @return the corresponding client broadcast which invoked this notification (will be null if this notification was not caused by a client broadcast)
	 */
	public MapMessage getClientBroadcast();
	public void setClientBroadcast(MapMessage msg);

	/**
	 * @return the type of action which caused this notification
	 */
	public OmsAction getType();
	public void setType(OmsAction action);
}
