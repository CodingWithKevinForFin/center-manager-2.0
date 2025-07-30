package com.f1.strategy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.OutputPort;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.fixomsclient.OmsClientNotification;
import com.f1.fixomsclient.OmsClientOrdersExecutions;
import com.f1.fixomsclient.OmsClientOrdersExecutionsManager;
import com.f1.fixomsclient.OmsClientState;
import com.f1.pofo.fix.OrdStatus;
import com.f1.pofo.oms.ChildNewOrderRequest;
import com.f1.pofo.oms.ChildOrderRequest;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsClientAction;
import com.f1.pofo.oms.Order;
import com.f1.pofo.oms.Side;
import com.f1.pofo.oms.SliceType;
import com.f1.povo.standard.MapMessage;
import com.f1.povo.standard.TextMessage;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MultiTimer;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.Timer;
import com.f1.utils.structs.Tuple2;

public class StrategyWrapper extends BasicProcessor<OmsClientNotification, StrategyState> {
	private ObjectGeneratorForClass<OmsClientAction> oa;
	private ObjectGeneratorForClass<MapMessage> mm;
	private ObjectGeneratorForClass<ChildNewOrderRequest> crg;
	private ObjectGeneratorForClass<ChildOrderRequest> creqg;
	private OMSConnectionHandler omsProxy;
	private OmsClientOrdersExecutionsManager manager;
	final private Map<String, String> childOrderRequestToIDMap = new ConcurrentHashMap<String, String>();

	final public RequestOutputPort<OmsClientAction, TextMessage> toOmsRequestPort = newRequestOutputPort(OmsClientAction.class, TextMessage.class);
	final public OutputPort<ResultMessage<TextMessage>> fromOmsResponsePort = toOmsRequestPort.getResponsePort();
	final public OutputPort<OmsClientAction> toOms = newOutputPort(OmsClientAction.class);
	final public OutputPort<MapMessage> alertPort = newOutputPort(MapMessage.class);
	final public OutputPort<TimerAction> timerPort = newOutputPort(TimerAction.class);

	public StrategyWrapper() {
		super(OmsClientNotification.class, StrategyState.class);
	}

	public void init() {
		super.init();
		oa = getGenerator(OmsClientAction.class);
		mm = getGenerator(MapMessage.class);
		crg = getGenerator(ChildNewOrderRequest.class);
		creqg = getGenerator(ChildOrderRequest.class);
		registerStrategyFactory(new SimpleStrategyFactory<DefaultStrategy>(DefaultStrategy.ID, getGenerator(DefaultStrategy.class)));
	}

	public void registerStrategyFactory(StrategyFactory<?> strategyFactory) {
		assertNotStarted();
		CH.putOrThrow(this.strategyFactories, strategyFactory.getStrategyId(), strategyFactory);
	}

	public ChildNewOrderRequest newChildRequest() {
		return crg.nw();
	}

	public ChildOrderRequest amendRequest() {
		return creqg.nw();
	}

	public void start() {
		super.start();
	}

	private Map<String, Integer> priorFilledQuantity = new HashMap<String, Integer>();
	private Map<String, String> amendRequestId = new HashMap<String, String>();

	public void onNewOrder(StrategyState state, Order order, OmsAction action) {
		Strategy strategy = state.getStrategy();
		OmsClientAction response;
		switch (action) {
			case SNAPSHOT: // TODO: filter only parent orders will come in the
							// snapshot
				// In a recovery scenario, dont re-create completed orders
				if (OrdStatus.isCompleted(order.getOrderStatus()))
					break;
				response = oa.nw();
				response.setRootOrderID(order.getId());
				response.setOrderAction(OmsAction.CANCEL_ALL_CHILD_ORDERS);
				toOms.send(response, null);
				break;
			case NEW_ORDER_RCVD:
				// inject parent order event
				// Prepare response message
				response = oa.nw();
				// Root Order ID is used to communicate what is the ultimate parent
				// ID: Useful for threading
				response.setRootOrderID(order.getId());
				// Order ID is to let the OMS know what order (parent or child) THIS
				// action is for

				response.setOrderID(order.getId());

				// MktDataManager mgr = (MktDataManager)
				// omsProxy.getContainer().getServices().getService("mktdata");
				// // Validate with ref data and get system wide security ID
				// registered with OMS
				// Security sec = omsProxy.getRefData(toSymbol(order));
				// if (sec == null) {
				// // Security is invalid according to central Security Master
				// response.setOrderAction(OmsAction.REJECT_ORDER);
				// response.setText("Invalid Symbol " + toSymbol(order));
				// LH.warning( log ,response.getText() , ", rejecting order: " , order);
				// } else if (!((order.getIDType() == 1) &&
				// cusipSame(order.getSecurityID(), sec.getCusip()))) {
				// // if NOT the order has a cusip and the cusip is valid
				// response.setOrderAction(OmsAction.REJECT_ORDER);
				// response.setText("Cusip does not match symbol: " + sec.getCusip()
				// + " != " + order.getSecurityID());
				// LH.warning( log ,response.getText() , ", rejecting order: " , order);
				// } else {
				// if (!(securityMap.containsKey(sec.getSecurityId()))) {// Setup
				// reverse mapping for market data events to use symbol to call OFR
				// securityMap.put(sec.getSecurityId(), sec);
				// // Subscribe to market data. Duplicate checking is done by market
				// data layer
				// mgr.subscribe(sec.getSecurityId());
				// }
				try {
					strategy.onNewOrder(order);
					// Successful callback means acknolwedge order
					response.setOrderAction(OmsAction.ACKNOWLEDGE_ORDER);
				} catch (StrategyException e) {
					response.setOrderAction(OmsAction.REJECT_ORDER);
					if (SH.is(e.getMessage()))
						response.setText("algo message: " + e.getMessage());
					else
						response.setText("Unknown exception from algo: " + e.getClass().getSimpleName());
					LH.warning(log, "error with order worker", e);
				}

				// }
				// Send the ack or reject to the OMS
				toOms.send(response, null);
				break;
			case NEW_CHILD_ORDER:// A new child order has been created in the OMS
				// LH.warning( log ,"Request ID: " , order.getRequestId() ,
				// " was created as slice ID : " , order.getId());
				// Store the requestID to OMS ID mapping for secondary requests
				childOrderRequestToIDMap.put(order.getRequestId(), order.getId());
				break;
			case CHILD_REJECTED:// The OMS rejected the creation of the new child
								// order
				strategy.onChildRejected(order);
				break;
		}
	}

	private String toSymbol(Order order) {
		return order.getSymbol().indexOf('.') != -1 || SH.is(order.getSymbolSfx()) ? order.getSymbol() + "." + order.getSymbolSfx() : order.getSymbol();
	}

	private boolean cusipSame(String left, String right) {
		if (SH.length(left) == 9)
			left = left.substring(0, 8);
		if (SH.length(right) == 9)
			right = right.substring(0, 8);
		return OH.eq(left, right);
	}

	public void onUpdateOrder(StrategyState state, Order old, Order nuw, OmsAction action) {
		Strategy strategy = state.getStrategy();
		switch (action) {
			case ALL_CHILDREN_CANCELLED:
				priorFilledQuantity.put(nuw.getId(), nuw.getTotalExecQty());
				OmsClientAction response = oa.nw();
				response.setRootOrderID(nuw.getId());
				response.setOrderID(nuw.getId());
				try {
					strategy.onCancelOrder(nuw);
					response.setOrderAction(OmsAction.ORDER_CANCELLED);
				} catch (StrategyException e) {
					LH.warning(log, "error with cancelling all child orders", e);
					// OFR did not accept the order
					response.setOrderAction(OmsAction.ORDER_CANCELLED);
					if (SH.is(e.getMessage()))
						response.setText("algo message: " + e.getMessage());
					else
						response.setText("unknown exception from algo: " + e.getClass().getSimpleName());
					LH.warning(log, "error with ofr adapter", e);
				}
				// Send the ack or reject to the OMS
				toOms.send(response, null);
				break;

			case CANCEL_ORDER:// Cancel request was received by the OMS for this
								// order
				try {
					strategy.onCancelOrder(nuw);
				} catch (StrategyException e) {
					LH.warning(log, "error with cancel order", e);
					response = oa.nw();
					response.setRootOrderID(nuw.getId());
					response.setOrderID(nuw.getId());
					response.setOrderAction(OmsAction.REPLACE_REJECTED);
					if (SH.is(e.getMessage()))
						response.setText("algo message: " + e.getMessage());
					else
						response.setText("unknown exception from algo: " + e.getClass().getSimpleName());
					toOms.send(response, null);
				}
				break;
			case REPLACE_ORDER:// Replace request was received by the OMS for this
								// order
				try {
					strategy.onReplaceOrder(old, nuw);
				} catch (StrategyException e) {
					LH.warning(log, "error with replace order", e);
					response = oa.nw();
					response.setRootOrderID(nuw.getId());
					response.setOrderID(nuw.getId());
					response.setOrderAction(OmsAction.REPLACE_REJECTED);
					if (SH.is(e.getMessage()))
						response.setText("algo message: " + e.getMessage());
					else
						response.setText("unknown exception from algo: " + e.getClass().getSimpleName());
					toOms.send(response, null);
				}
				break;

			// A transaction affecting a child order was completed by the OMS
			case CHILD_ORDER_ACKNOWLEDGED: // External System acked the child order
				strategy.onChildAcked(nuw);
				break;
			case CANCEL_CHILD_ORDER: // Cancel request received by OMS for this
										// child order
			case REPLACE_CHILD_ORDER: // Replace request received by OMS for this
										// child order
				break;
			case CHILD_CANCEL_SUCCEEDED: // Cancel request succeeded in OMS
				strategy.onChildCancelled(nuw);
			case CHILD_REJECTED: // Child order was rejected from FIX destination
				if (nuw.getSliceType() == SliceType.SLICE) { // Only pass on events
																// on the slice. OFR
																// is not interested
																// in the exchange
																// leaves update on
																// the
																// parent
					strategy.onChildRejected(nuw);
				}
				break;
			case CHILD_REPLACE_SUCCEEDED: // Slice was successfully replaced from
											// FIX destination
				if (nuw.getSliceType() == SliceType.SLICE) {
					childOrderRequestToIDMap.put(nuw.getRequestId(), nuw.getId()); // Store
																					// the
																					// new
																					// request
																					// ID
																					// for
																					// the
																					// lookup
					strategy.onChildReplaced(old, nuw);
				}
				break;
			case CHILD_CANCEL_REJECTED:
			case CHILD_REPLACE_REJECTED:
				break;
		}
	}

	private int getPriorFilled(Order nuw) {
		return CH.getOr(priorFilledQuantity, nuw.getId(), 0);
	}

	public void onNewExecution(StrategyState state, Execution exec, OmsAction action) {
		Strategy strategy = state.getStrategy();
		OmsClientState omsState = state.getOmsClientState();
		Order o = omsState.getOrder(exec.getSourceSystem(), exec.getOrderId());
		// Get the order associated with this Execution
		if (o != null && o.getSliceType() == SliceType.SLICE) {
			// Only report child executions. Parent executions are not interesting for the OFR
			strategy.onExecOrder(o, exec);
		}
	}

	// IEmsServices
	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.f1.strategy.OrderManager#createChildOrder(com.f1.fix.oms.client.
	 * ChildNewOrderRequest, java.lang.String)
	 */
	public String createChildOrder(Object partitionId, ChildNewOrderRequest child, String rootId) throws RequestException {
		String id = getContainer().getServices().getTicketGenerator("StrategyManager").createNextId();
		// Create a new id for this request. This will serve as a proxy for tag 11 for the OFR
		// Create the message to send
		OmsClientAction action = oa.nw();
		// Request a new child order.The type is used for routing this message
		action.setOrderAction(OmsAction.NEW_CHILD_ORDER);
		// Set the root order ID for the OMS
		action.setRootOrderID(rootId);
		// Create the details of this request
		// Attach the details object to the message to send
		action.setChildRequest(child);
		// ID of parent that needs to create the child
		action.setOrderID(rootId);
		// Downstream session associated with this child order
		// Send the command to the OMS

		child.setRequestId(id);
		toOmsRequestPort.request(action, null, partitionId, null);
		// Inform the OFR of the request ID
		return id;
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.f1.strategy.OrderManager#cancelChildOrder(java.lang.String,
	 * java.lang.String)
	 */
	public void cancelChildOrder(Object partitionId, String parentId, String childID) throws RequestException {
		String childOrderID = childOrderRequestToIDMap.get(childID);
		if (childOrderID == null) {
			throw new RequestException("Unknown child order " + childID + " for block " + parentId + ". Order may not be acked yet?");
		}
		OmsClientAction action = oa.nw();
		action.setOrderAction(OmsAction.CANCEL_CHILD_ORDER);
		action.setOrderID(childOrderID);
		action.setRootOrderID(parentId);
		toOmsRequestPort.request(action, null, partitionId, null);
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.f1.strategy.OrderManager#replaceChildOrder(com.f1.fix.oms.client.
	 * ChildOrderRequest, java.lang.String)
	 */
	public String replaceChildOrder(Object partitionId, ChildOrderRequest child, String rootId) {

		OmsClientAction action = oa.nw();
		action.setOrderAction(OmsAction.REPLACE_CHILD_ORDER);
		String childOrderID = childOrderRequestToIDMap.get(child.getOrigRequestId());
		action.setRootOrderID(rootId);
		action.setOrderID(childOrderID);

		// TODO: get this on the request
		OmsClientOrdersExecutions store = manager.borrowOrdersExecutions();
		Order order = store.getOrder(null, childOrderID);
		Side side = order.getSide();
		child.setSide(side);
		manager.freeOrderExecutions(store);
		//
		child.setRequestId(getContainer().getServices().getTicketGenerator("OFROrderState").createNextId());
		amendRequestId.put(child.getOrigRequestId(), child.getRequestId());
		action.setChildRequest(child);
		toOmsRequestPort.request(action, null, partitionId, null);
	LH.info(log,"C/R order ID:" , child.getRequestId());
		return child.getRequestId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.f1.strategy.OrderManager#orderStatusUpdate(com.f1.pofo.oms.OmsAction,
	 * java.lang.String)
	 */
	public void orderStatusUpdate(OmsAction update, String rootId) {
		OmsClientAction action = oa.nw();
		action.setRootOrderID(rootId);
		action.setOrderID(action.getRootOrderID());
		action.setOrderAction(update);
		toOms.send(action, null);
	}

	public void setOmsProxy(OMSConnectionHandler omsProxy) {
		this.omsProxy = omsProxy;
	}

	private Map<String, StrategyFactory> strategyFactories = new HashMap<String, StrategyFactory>();

	private MultiTimer timers = new MultiTimer();

	public void addTimer(Timer timer) {
		synchronized (timers) {
			timers.addTimer(timer);
		}
	}

	public boolean cancelTimer(Timer timer) {
		synchronized (timers) {
			return timers.removeTimer(timer);
		}
	}

	public void cancelAllTimers() {
		synchronized (timers) {
			timers.clear();
		}

	}

	@Override
	public void processAction(OmsClientNotification action, StrategyState state, ThreadScope threadScope) throws Exception {
		if (state.getStrategy() == null) {
			OmsClientState omsState = (OmsClientState) state.getPartition().getState(OmsClientState.class);
			state.init(omsState, this);
			Order order = action.getAddedOrders().get(0);
			String strategyId = CH.getOr(order.getPassThruTags(), 7790, DefaultStrategy.ID);
			StrategyFactory<?> factory = CH.getOrThrow(StrategyFactory.class, strategyFactories, strategyId, "strategy not found(tag 7790) ");
			Strategy strategy = factory.createStrategy(state);
			state.setStrategy(strategy);
			state.getStrategy().init(state);
		}
		if (action.getType() == null) {
			onRcvBroadcast(action.getClientBroadcast());
		} else {
			for (Order order : action.getAddedOrders())
				onNewOrder(state, order, action.getType());

			for (Tuple2<Order, Order> order : action.getUpdatedOrders())
				onUpdateOrder(state, order.getA(), order.getB(), action.getType());

			for (Execution execution : action.getAddedExecutions())
				onNewExecution(state, execution, action.getType());
		}
	}

	private void onRcvBroadcast(MapMessage clientBroadcast) {
		// TODO Auto-generated method stub

	}

	public void scheduleTimer(Object partitionId, long scheduledTime) {
		long delay = Math.max(0, scheduledTime - getServices().getClock().getNow());
		TimerAction a = nw(TimerAction.class);
		a.setScheduledTime(scheduledTime);
		timerPort.sendDelayed(a, partitionId, null, delay, TimeUnit.MILLISECONDS);
	}

}