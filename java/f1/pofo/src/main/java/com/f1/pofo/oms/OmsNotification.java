package com.f1.pofo.oms;

import java.util.List;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.pofo.fix.FixRequest;

/**
 * 
 * represents a notification for a particular order group (as indicated by the root order id)
 * 
 */
@VID("F1.OM.ON")
public interface OmsNotification extends Message {

	/**
	 * @return the id of the root order related to this notification
	 */
	@PID(1)
	public String getRootOrderID();
	public void setRootOrderID(String orderID);

	/**
	 * @return the type of action which produced this notification
	 */
	@PID(2)
	public OmsAction getType();
	public void setType(OmsAction action);

	/**
	 * @param addedOrders
	 *            a list of all the new orders created by the oms action
	 */
	@PID(3)
	public void setAddedOrders(List<Order> addedOrders);
	public List<Order> getAddedOrders();

	/**
	 * @param addedOrders
	 *            a list of all the new orders created by the oms action
	 */
	@PID(4)
	public void setChangedOrders(List<Order> changedOrders);
	public List<Order> getChangedOrders();

	/**
	 * @param addedOrders
	 *            a list of all the new orders created by the oms action
	 */
	@PID(5)
	public void setPendingRequests(List<FixRequest> pendingRequests);
	public List<FixRequest> getPendingRequests();

	/**
	 * @param addedOrders
	 *            a list of all the new orders created by the oms action
	 */
	@PID(6)
	public void setAddedExecutions(List<Execution> addedExecutions);
	public List<Execution> getAddedExecutions();

	@PID(7)
	public void setSnapshotNotifications(List<OmsNotification> addedExecutions);
	public List<OmsNotification> getSnapshotNotifications();
}
