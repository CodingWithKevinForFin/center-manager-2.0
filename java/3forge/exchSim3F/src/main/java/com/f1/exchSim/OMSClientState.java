package com.f1.exchSim;

import java.util.HashMap;
import java.util.Map;

import com.f1.container.impl.BasicState;
import com.f1.pofo.oms.Order;


public class OMSClientState extends BasicState {

	Map<String,Order> orderMap=new HashMap<String,Order>();
	public void addOrder(Order order) {
		orderMap.put(order.getId(),order);		
	}
	public Order getOrder(String id){
		return orderMap.get(id);
	}

}
