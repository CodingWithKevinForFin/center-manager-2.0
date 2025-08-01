package com.f1.fix.oms.clientside;

import java.util.logging.Level;

import com.f1.base.DateNanos;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.plugin.NewChildOrderPlugin;
import com.f1.fix.oms.schema.ClientOrder;
import com.f1.fix.oms.schema.FixCopyUtil;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.fix.oms.schema.Slice;
import com.f1.persist.structs.PersistableHashMap;
import com.f1.pofo.fix.ChildOrderId;
import com.f1.pofo.fix.MsgType;
import com.f1.pofo.fix.OrdStatus;
import com.f1.pofo.fix.child.FixChildOrderRequest;
import com.f1.pofo.oms.ChildNewOrderRequest;
import com.f1.pofo.oms.ChildOrderRequest;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.Order;
import com.f1.pofo.oms.SliceType;
import com.f1.utils.LH;
import com.f1.utils.SH;

/**
 * 
 * process the creation of a child for a client order
 * 
 */
public class OmsDefaultCreateChildOrderProcessor extends OmsAbstractProcessor {
	private ObjectGeneratorForClass<Slice> sg;
	private ObjectGeneratorForClass<FixChildOrderRequest> request;
	private ObjectGeneratorForClass<ChildOrderId> coid;
	private ObjectGeneratorForClass<Order> sfixg;
	private NewChildOrderPlugin plugin;

	public void start() {
		super.start();
		sg = getGenerator(Slice.class);
		request = getGenerator(FixChildOrderRequest.class);
		coid = getGenerator(ChildOrderId.class);
		sfixg = getGenerator(Order.class);
		this.plugin = super.getOmsPlugin().getNewChildOrderPlugin();
	}

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		// OrderID for the child order should be generated by the requestor
		// ClientOrder ID for the child order outbound will be a function of the
		// parent order id
		// TODO: Move destination up one and send only a FixRequestEvent
		final DateNanos now = getTools().getNowNanoDate();
		final ClientOrder order = (ClientOrder) action.getOrder();
		if (order == null) {
			action.setRejectText("Cannot find parent order for child create : ");
			return;
		}

		if (OmsUtils.isTerminal(order.getOrderStatus())) {
			action.setRejectText("Cannot create child for terminal parent order : ");
			return;
		}

		final Order parentOrder = order.getFixOrder();
		final ChildNewOrderRequest childRequest = (ChildNewOrderRequest) action.getClientMsg().getChildRequest();
		int remainingQuantity = parentOrder.getOrderQty() - order.getExchLeaves() - parentOrder.getTotalExecQty();
		if (childRequest.getOrderQty() > remainingQuantity) {
			action.setRejectText("Child quantity exceeds remaining parent quantity: " + childRequest.getOrderQty() + " > " + remainingQuantity);
			return;
		}

		final Slice slice = sg.nw();

		slice.setFixReqRevisionID(1);
		slice.setFixRevisionID(0);

		// TODO: If request is null throw a fit;
		boolean result = validateAndPopulate(childRequest, order.getFixOrder());
		if (!result) {
			// TODO: throw some kind of reject here
			return;
		}

		final FixChildOrderRequest evt = request.nw();
		evt.setOrderInfo(childRequest);

		final Order fixorder = sfixg.nw();
		evt.setSecurityID(parentOrder.getSecurityID());
		evt.setIDType(parentOrder.getIDType());
		evt.setSymbol(parentOrder.getSymbol());
		evt.setSymbolSfx(parentOrder.getSymbolSfx());
		evt.setLocateBrokerRequired(parentOrder.getLocateBrokerRequired());
		evt.setLocateBroker(parentOrder.getLocateBroker());
		evt.setSenderSubId(parentOrder.getSenderSubId());
		evt.setLocateId(parentOrder.getLocateId());
		evt.setOrderCapacity(parentOrder.getOrderCapacity());
		evt.setRule80A(parentOrder.getRule80A());
		evt.setType(MsgType.NEW_ORDER_SINGLE);

		if (fixorder.getPassThruTags() == null)
			fixorder.setPassThruTags(new PersistableHashMap<Integer, String>());
		FixCopyUtil.copy(fixorder, evt);

		String id2 = SH.trim('0', '!', SH.afterLast(getServices().getTicketGenerator("OMSOrderState").createNextId(), '-'));
		fixorder.setId(childRequest.getRequestId() + "." + id2);
		fixorder.setRevision(0);
		fixorder.setRequestId(childRequest.getRequestId());
		fixorder.setOrderGroupId(parentOrder.getOrderGroupId());
		fixorder.setCreatedTime(now);
		fixorder.setUpdatedTime(now);
		fixorder.setSliceType(SliceType.SLICE);
		fixorder.setOrderStatus(OrdStatus.PENDING_ACK.getIntMask());
		fixorder.setDestination(childRequest.getDestination());
		slice.setFixOrder(fixorder);
		slice.setOrderStatus(OmsOrderStatus.PENDING_ACK.getMask());
		slice.setExchLeaves(slice.getFixOrder().getOrderQty());

		ChildOrderId id = FixCopyUtil.createRequest(this, slice);

		fixorder.setSessionName(childRequest.getSessionName());
		evt.setSessionName(fixorder.getSessionName()); // This should be set by
														// a later processor
														// which handles all the
														// destinations etc.
		evt.setDestination(fixorder.getDestination());
		evt.setChildId(id);

		evt.setRootOrderId(state.getClientOrder().getFixOrder().getId());

		if (plugin != null) {
			try {
				plugin.onNewChildOrder(parentOrder, fixorder, evt);
			} catch (Exception e) {
				LH.log(log, Level.SEVERE, "Plugin generated error for cancel child: ", request, e);
			}
		}
		getOmsPlugin().mapNewChildOrderTags(parentOrder.getPassThruTags(), evt.getOrderInfo());
		toFixSession.send(evt, threadScope);
		// OrderID is unique across chain
		// requestID and origID are from Client on the fix order
		//

		// TODO:verify exchange leaves

		// TODO: verify price etc
		/*
		 * Verify price does not violate parent limit price Verify quantity does
		 * not exceed parent quantity Verify destination is valid
		 */
		order.setExchLeaves(order.getExchLeaves() + slice.getFixOrder().getOrderQty());
		slice.setParentOrder(order);
		state.addOrder(slice);

		OmsNotification notif = newNotification(state);
		notif.setType(OmsAction.NEW_CHILD_ORDER);
		toOMSClient.send(notif, threadScope);
	}

	private boolean validateAndPopulate(ChildOrderRequest childRequest, Order fixOrder) {

		// TODO: check for valid : conditions
		// if parent order is market and child order is limit
		// if parent order is market and child order is market
		// if parent order is market and child order is market

		if (childRequest.getOrderType() == null)
			childRequest.setOrderType(fixOrder.getOrderType());
		if (zeroPrice(childRequest.getLimitPx()))
			childRequest.setLimitPx(fixOrder.getLimitPx());
		if (SH.isnt(childRequest.getCurrency()) && SH.is(fixOrder.getCurrency()))
			childRequest.setCurrency(fixOrder.getCurrency());
		if (childRequest.getTimeInForce() == null)
			childRequest.setTimeInForce(fixOrder.getTimeInForce());
		if (childRequest.getSide() == null)
			childRequest.setSide(fixOrder.getSide());
		if (childRequest.getExecInstructions() == null)
			childRequest.setExecInstructions(fixOrder.getExecInstructions());
		return true;
	}

	private boolean zeroPrice(double price) {
		return (Math.abs(price) < 0.00001);
	}
}
