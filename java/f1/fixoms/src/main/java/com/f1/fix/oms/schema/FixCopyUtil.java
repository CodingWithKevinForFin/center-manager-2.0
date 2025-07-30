package com.f1.fix.oms.schema;

import java.util.Map;

import com.f1.container.impl.AbstractContainerScope;
import com.f1.persist.structs.PersistableHashMap;
import com.f1.pofo.fix.ChildOrderId;
import com.f1.pofo.fix.FixExecutionReport;
import com.f1.pofo.fix.FixOrderInfo;
import com.f1.pofo.fix.FixOrderRequest;
import com.f1.pofo.fix.FixReport;
import com.f1.pofo.fix.FixRequest;
import com.f1.pofo.fix.FixStatusReport;
import com.f1.pofo.oms.ChildOrderRequest;
import com.f1.pofo.oms.Order;

public class FixCopyUtil {
	public static void copy(Order fixorder, FixOrderRequest request) {
		fixorder.setSecurityID(request.getSecurityID());
		fixorder.setIDType(request.getIDType());
		fixorder.setSymbol(request.getSymbol());
		fixorder.setSymbolSfx(request.getSymbolSfx());
		fixorder.setOrderCapacity(request.getOrderCapacity());
		fixorder.setRule80A(request.getRule80A());
		fixorder.setLocateBrokerRequired(request.getLocateBrokerRequired());
		fixorder.setLocateBroker(request.getLocateBroker());
		fixorder.setLocateId(request.getLocateId());
		fixorder.setSenderSubId(request.getSenderSubId());
		fixorder.setOnBehalfOfCompId(request.getOnBehalfOfCompId());
		fixorder.setDestination(request.getDestination());
		fixorder.setRequestId(request.getRequestId());
		fixorder.setOrderQty(request.getOrderInfo().getOrderQty());
		fixorder.setOrderType(request.getOrderInfo().getOrderType());
		fixorder.setLimitPx(request.getOrderInfo().getLimitPx());
		fixorder.setTimeInForce(request.getOrderInfo().getTimeInForce());
		if (fixorder.getPassThruTags() == null)
			fixorder.setPassThruTags(new PersistableHashMap<Integer, String>());
		copyMap(fixorder.getPassThruTags(), request.getOrderInfo().getPassThruTags());
		fixorder.setSide(request.getOrderInfo().getSide());
		fixorder.setCurrency(request.getOrderInfo().getCurrency());
		fixorder.setAccount(request.getOrderInfo().getAccount());
		fixorder.setText(request.getOrderInfo().getText());
		fixorder.setSessionName(request.getSessionName());
		fixorder.setExecInstructions(request.getOrderInfo().getExecInstructions());
	}
	public static void copy(FixRequest request, Order fixorder) {
		request.setSymbol(fixorder.getSymbol());
		request.setSymbolSfx(fixorder.getSymbolSfx());
		request.setOrderCapacity(fixorder.getOrderCapacity());
		request.setRule80A(fixorder.getRule80A());
		request.setDestination(fixorder.getDestination());
		request.setLocateBrokerRequired(fixorder.getLocateBrokerRequired());
		request.setLocateBroker(fixorder.getLocateBroker());
		request.setLocateId(fixorder.getLocateId());
		request.setSenderSubId(fixorder.getSenderSubId());
		request.setOnBehalfOfCompId(fixorder.getOnBehalfOfCompId());
		request.getOrderInfo().setOrderQty(fixorder.getOrderQty());
		request.getOrderInfo().setLimitPx(fixorder.getLimitPx());
		request.getOrderInfo().setTimeInForce(fixorder.getTimeInForce());
		request.getOrderInfo().setOrderType(fixorder.getOrderType());
		request.getOrderInfo().setSide(fixorder.getSide());
		request.getOrderInfo().setCurrency(fixorder.getCurrency());
		request.setSessionName(fixorder.getSessionName());
		request.getOrderInfo().setExecInstructions(fixorder.getExecInstructions());
	}

	public static void copyMutable(Order fixorder, FixOrderInfo request) {
		fixorder.setOrderQty(request.getOrderQty());
		fixorder.setOrderType(request.getOrderType());
		fixorder.setLimitPx(request.getLimitPx());
		fixorder.setTimeInForce(request.getTimeInForce());
		if (fixorder.getPassThruTags() == null)
			fixorder.setPassThruTags(new PersistableHashMap<Integer, String>());
		copyMap(fixorder.getPassThruTags(), request.getPassThruTags());
		fixorder.setSide(request.getSide());
		fixorder.setCurrency(request.getCurrency());
	}

	public static void copyCommon(FixReport report, Order fixOrder) {
		report.setRequestId(fixOrder.getRequestId());
		report.setSecurityID(fixOrder.getSecurityID());
		report.setIDType(fixOrder.getIDType());
		report.setSymbol(fixOrder.getSymbol());
		report.setSymbolSfx(fixOrder.getSymbolSfx());
		report.setDestination(fixOrder.getDestination());

		// ---MODIFIABLE FIELDS

		report.setOrderQty(fixOrder.getOrderQty());
		report.setLimitPx(fixOrder.getLimitPx());
		report.setSide(fixOrder.getSide());
		report.setCurrency(fixOrder.getCurrency());
		report.setOrderType(fixOrder.getOrderType());
		report.setTimeInForce(fixOrder.getTimeInForce());
		// ---STATE---

		report.setCumQty(fixOrder.getTotalExecQty());
		report.setExecValue(fixOrder.getTotalExecValue());
		report.setOrdStatus(fixOrder.getOrderStatus());

		// TODO: Last Update Time
		report.setSessionName(fixOrder.getSessionName());
	}

	public static void copyMap(Map<Integer, String> target, Map<Integer, String> source) {
		if (source == null)
			return;
		//By default, we keep old tags around
		for (Integer i : source.keySet())
			target.put(i, source.get(i));
	}

	public static void copy(FixExecutionReport report, Order fixOrder) {
		copyCommon(report, fixOrder);
		report.getExecution().setOrderId(fixOrder.getId());
	}

	public static void copy(FixStatusReport report, Order fixOrder) {
		copyCommon(report, fixOrder);
		report.setOrderId(fixOrder.getId());
	}

	public static void copy(ChildOrderRequest from, Order to) {
		// to.setAccount(from.getAccount());
		// to.setDestination(from.getDestination());
		// to.setExecInstructions(from.getExecInstructions());
		to.setLimitPx(from.getLimitPx());
		//to.setCurrency(from.getCurrency());
		to.setOrderQty(from.getOrderQty());
		to.setOrderType(from.getOrderType());
		// to.setTimeInForce(from.getTimeInForce());
	}

	public static ChildOrderId createRequest(AbstractContainerScope c, Slice orderToModify) {
		ChildOrderId id = c.nw(ChildOrderId.class); // --IN A CHILD ORDER THE
													// "CHILD ORDER ID" OBJECT
													// IS WHAT MATTERS THE MOST
													// FOR IDS
		Order fixorder = orderToModify.getFixOrder();
		id.setOrderId(fixorder.getId());
		id.setRequestId(orderToModify.getFixReqRevisionID());
		id.setOrderRevisionId(orderToModify.getFixRevisionID());
		orderToModify.setFixReqRevisionID(orderToModify.getFixReqRevisionID() + 1);
		return id;

	}

	public static void copy(Order source, Order dest) {
		dest.setSecurityID(source.getSecurityID());
		dest.setIDType(source.getIDType());
		dest.setSymbol(source.getSymbol());
		dest.setSymbolSfx(source.getSymbolSfx());
		dest.setOrderCapacity(source.getOrderCapacity());
		dest.setRule80A(source.getRule80A());
		dest.setLocateBrokerRequired(source.getLocateBrokerRequired());
		dest.setLocateBroker(source.getLocateBroker());
		dest.setLocateId(source.getLocateId());
		dest.setSenderSubId(source.getSenderSubId());
		dest.setDestination(source.getDestination());
		dest.setRequestId(source.getRequestId());
		dest.setOrderQty(source.getOrderQty());
		dest.setOrderType(source.getOrderType());
		dest.setLimitPx(source.getLimitPx());
		dest.setTimeInForce(source.getTimeInForce());
		if (dest.getPassThruTags() == null)
			dest.setPassThruTags(new PersistableHashMap<Integer, String>());
		copyMap(dest.getPassThruTags(), source.getPassThruTags());
		dest.setSide(source.getSide());
		dest.setAccount(source.getAccount());
		dest.setText(source.getText());
		dest.setSessionName(source.getSessionName());
		dest.setExecInstructions(source.getExecInstructions());
	}

}
