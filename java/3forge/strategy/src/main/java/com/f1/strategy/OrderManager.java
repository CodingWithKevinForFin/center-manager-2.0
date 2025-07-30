package com.f1.strategy;

import com.f1.container.ContainerTools;
import com.f1.fixomsclient.OmsClientState;
import com.f1.pofo.oms.ChildNewOrderRequest;
import com.f1.pofo.oms.ChildOrderRequest;
import com.f1.pofo.oms.OmsAction;
import com.f1.utils.Timer;

public interface OrderManager {

	String createChildOrder(ChildNewOrderRequest child, String rootId) throws RequestException;

	void cancelChildOrder(String parentId, String childID) throws RequestException;

	String replaceChildOrder(ChildOrderRequest child, String rootId);

	void orderStatusUpdate(OmsAction update, String rootId);

	ChildOrderRequest amendRequest();

	ChildNewOrderRequest newChildRequest();

	public void addTimer(Timer cronTab);

	public void cancelAllTimers();

	boolean cancelTimer(Timer timer);

	OmsClientState getOmsClientState();

	ContainerTools getTools();

}