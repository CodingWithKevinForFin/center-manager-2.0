package com.f1.fix.oms;

import static com.f1.fix.oms.schema.OmsOrderStatus.ACKED;
import static com.f1.fix.oms.schema.OmsOrderStatus.CANCELLED;
import static com.f1.fix.oms.schema.OmsOrderStatus.FILLED;
import static com.f1.fix.oms.schema.OmsOrderStatus.PARTIAL;
import static com.f1.fix.oms.schema.OmsOrderStatus.PAUSED;
import static com.f1.fix.oms.schema.OmsOrderStatus.PENDING_ACK;
import static com.f1.fix.oms.schema.OmsOrderStatus.PENDING_CXL;
import static com.f1.fix.oms.schema.OmsOrderStatus.PENDING_PAUSE;
import static com.f1.fix.oms.schema.OmsOrderStatus.PENDING_RPL;
import static com.f1.fix.oms.schema.OmsOrderStatus.REPLACED;
import static com.f1.fix.oms.schema.OmsOrderStatus.UNINITIALIZED;
import static com.f1.pofo.oms.OmsAction.ACKNOWLEDGE_ORDER;
import static com.f1.pofo.oms.OmsAction.ATTACH_EXECUTION;
import static com.f1.pofo.oms.OmsAction.CANCEL_CHILD_ORDER;
import static com.f1.pofo.oms.OmsAction.CANCEL_ORDER;
import static com.f1.pofo.oms.OmsAction.CHILD_FULLY_FILLED;
import static com.f1.pofo.oms.OmsAction.CHILD_PARTIALLY_FILLED;
import static com.f1.pofo.oms.OmsAction.CHILD_REPLACE_SUCCEEDED;
import static com.f1.pofo.oms.OmsAction.CUSTOM_DATA_UPDATED;
import static com.f1.pofo.oms.OmsAction.DONE_FOR_DAY;
import static com.f1.pofo.oms.OmsAction.FILL_RECEIVED;
import static com.f1.pofo.oms.OmsAction.NEW_CHILD_ORDER;
import static com.f1.pofo.oms.OmsAction.NEW_ORDER_RCVD;
import static com.f1.pofo.oms.OmsAction.ORDER_ACKED;
import static com.f1.pofo.oms.OmsAction.ORDER_CANCELLED;
import static com.f1.pofo.oms.OmsAction.ORDER_REPLACED;
import static com.f1.pofo.oms.OmsAction.REJECT_ORDER;
import static com.f1.pofo.oms.OmsAction.REPLACE_ORDER;
import static com.f1.pofo.oms.OmsAction.REPLACE_REJECTED;

import com.f1.container.InputPort;
import com.f1.container.OutputPort;
import com.f1.container.Processor;
import com.f1.container.RequestInputPort;
import com.f1.container.RequestMessage;
import com.f1.container.impl.BasicSuite;
import com.f1.fix.oms.adapter.OmsTransactionBundler;
import com.f1.fix.oms.adapter.OrderSnapshotRequestProcessor;
import com.f1.fix.oms.adapter.SnapshotRequestProcessor;
import com.f1.fix.oms.clientside.OmsDefaultAttachBustExecutionProcessor;
import com.f1.fix.oms.clientside.OmsDefaultAttachCorrectExecutionProcessor;
import com.f1.fix.oms.clientside.OmsDefaultAttachDataProcessor;
import com.f1.fix.oms.clientside.OmsDefaultAttachExecutionProcessor;
import com.f1.fix.oms.clientside.OmsDefaultCancelChildOrdersProcessor;
import com.f1.fix.oms.clientside.OmsDefaultCancelOrderProcessor;
import com.f1.fix.oms.clientside.OmsDefaultChildBustProcessor;
import com.f1.fix.oms.clientside.OmsDefaultChildCancelledProcessor;
import com.f1.fix.oms.clientside.OmsDefaultChildCorrectProcessor;
import com.f1.fix.oms.clientside.OmsDefaultChildFillProcessor;
import com.f1.fix.oms.clientside.OmsDefaultChildRejectProcessor;
import com.f1.fix.oms.clientside.OmsDefaultCreateChildOrderProcessor;
import com.f1.fix.oms.clientside.OmsDefaultDoneForDayProcessor;
import com.f1.fix.oms.clientside.OmsDefaultOrderCancelledProcessor;
import com.f1.fix.oms.clientside.OmsDefaultOrderReplacedProcessor;
import com.f1.fix.oms.clientside.OmsDefaultOrderRestatedProcessor;
import com.f1.fix.oms.clientside.OmsDefaultRejectReplaceProcessor;
import com.f1.fix.oms.clientside.OmsDefaultReplaceOrderProcessor;
import com.f1.fix.oms.clientside.OmsPendingAckAcknowledgeOrderProcessor;
import com.f1.fix.oms.clientside.OmsPendingAckRejectOrderProcesor;
import com.f1.fix.oms.clientside.OmsRejectClientActionProcessor;
import com.f1.fix.oms.clientside.OmsUninitializedNewOrderRcvdProcessor;
import com.f1.fix.oms.schema.ClientOrder;
import com.f1.fix.oms.schema.OmsOrder;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.fix.oms.schema.Slice;
import com.f1.fix.oms.sliceside.OmsDefaultBustOrderProcessor;
import com.f1.fix.oms.sliceside.OmsDefaultCOCxlOrModifyRejectedProcessor;
import com.f1.fix.oms.sliceside.OmsDefaultCancelChildProcessor;
import com.f1.fix.oms.sliceside.OmsDefaultChildOrderCancelledProcessor;
import com.f1.fix.oms.sliceside.OmsDefaultChildOrderReplacedProcessor;
import com.f1.fix.oms.sliceside.OmsDefaultCorrectOrderProcessor;
import com.f1.fix.oms.sliceside.OmsDefaultFillOrderProcessor;
import com.f1.fix.oms.sliceside.OmsDefaultReplaceChildOrderProcessor;
import com.f1.fix.oms.sliceside.OmsPendingAckOrderAckedProcesspr;
import com.f1.fix.oms.sliceside.OmsPendingAckOrderRejectedProcessor;
import com.f1.pofo.fix.FixMsg;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsClientAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.OmsSnapshotRequest;
import com.f1.pofo.oms.SliceType;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

/**
 * The 'heart' of the oms. Receives fix messages, sends fix messages, receives client messages and sends client messages
 * 
 */
public class OmsSuite extends BasicSuite {

	public static final String OPTION_REOPEN_PARENT_ORDERS = "com.f1.reopen.parent.orders.enabled";

	OmsRoutingProcessor clientOrderStateMachine;

	/**
	 * incomming messages from fix adapter
	 */
	public final InputPort<OrderEventCtx> fromFix;

	/**
	 * outgoing messages to fix adapter
	 */
	public final OutputPort<FixMsg> toFix;

	/**
	 * incomming messages from client(s)
	 */
	public final InputPort<OmsClientAction> fromOMSClient;

	/**
	 * incoming requests from client(s)
	 */
	public final InputPort<RequestMessage<OmsClientAction>> fromOMSRequestClient;

	/**
	 * incoming requests from snapshots
	 */
	public final RequestInputPort<OmsSnapshotRequest, OmsNotification> snapshotRequest;

	/**
	 * outgoing updates to clients
	 */
	public final OutputPort<OmsNotification> toOMSClient;
	OmsClientActionProcessor omsClientHandler;
	OmsTransactionBundler bundler;

	OmsRoutingProcessor sliceOrderStateMachine;

	public final OmsClientRequestProcessor omsClientRequestHandler;

	private OmsPluginManager pluginManager;

	/**
	 * Configures this oms to process client actions on a supplied processor depending on the action type and the state of the order the action is for.
	 * 
	 * @param state
	 *            the state of the order
	 * @param action
	 *            the type of action
	 * @param processor
	 *            the processor to handle the condition
	 */
	public void clientsm(OmsOrderStatus state, OmsAction action, OmsAbstractProcessor processor) {
		clientOrderStateMachine.registerProcessor(state, action, processor);
		if (processor.getParent() == null)
			addChild(processor);
		wire(processor.toOMSClient, bundler.getInputPort(), false);
		exposeOutputPortTo(processor.toFixSession, toFix);
	}

	/**
	 * Configures this oms to process client actions on a supplied processor depending on the action type regarless of the state of the order the action is for.
	 * 
	 * @param action
	 *            the type of action
	 * @param processor
	 *            the processor to handle the condition
	 */
	public void clientsm(OmsAction action, OmsAbstractProcessor processor) {
		clientOrderStateMachine.registerDefaultProcessorForAction(processor, action);
		if (processor.getParent() == null)
			addChild(processor);
		wire(processor.toOMSClient, bundler.getInputPort(), false);
		exposeOutputPortTo(processor.toFixSession, toFix);
	}

	/**
	 * Configures this oms to process street actions on a supplied processor depending on the action type and the state of the order the action is for.
	 * 
	 * @param state
	 *            the state of the order
	 * @param action
	 *            the type of action
	 * @param processor
	 *            the processor to handle the condition
	 */
	public void slicesm(OmsOrderStatus state, OmsAction action, OmsAbstractProcessor processor) {
		sliceOrderStateMachine.registerProcessor(state, action, processor);
		if (processor.getParent() == null)
			addChild(processor);
		wire(processor.toOMSClient, bundler.getInputPort(), false);
		exposeOutputPortTo(processor.toFixSession, toFix);
	}

	/**
	 * Configures this oms to process street actions on a supplied processor depending on the action type regarless of the state of the order the action is for.
	 * 
	 * @param action
	 *            the type of action
	 * @param processor
	 *            the processor to handle the condition
	 */
	public void slicesm(OmsAction action, OmsAbstractProcessor processor) {
		sliceOrderStateMachine.registerDefaultProcessorForAction(processor, action);
		if (processor.getParent() == null)
			addChild(processor);
		wire(processor.toOMSClient, bundler.getInputPort(), false);
		exposeOutputPortTo(processor.toFixSession, toFix);
	}

	public OmsSuite(PropertyController pc) {

		toFix = newOutputPort(FixMsg.class);// TODO: THIS IS A HUGE HACK. Need
											// to have some kinds of proxy plug
											// in suites
		bundler = new OmsTransactionBundler();
		clientOrderStateMachine = new OmsRoutingProcessor();
		sliceOrderStateMachine = new OmsRoutingProcessor();

		clientOrderStateMachine.setup(clientOrderStateMachine.getInputPort(), sliceOrderStateMachine.getInputPort(), SliceType.CLIENT_ORDER);
		sliceOrderStateMachine.setup(clientOrderStateMachine.getInputPort(), sliceOrderStateMachine.getInputPort(), SliceType.SLICE);
		omsClientHandler = new OmsClientActionProcessor();
		omsClientRequestHandler = new OmsClientRequestProcessor();

		addChildren(clientOrderStateMachine, sliceOrderStateMachine, omsClientHandler, omsClientRequestHandler, bundler);

		wire(clientOrderStateMachine.toRootStateMachine, clientOrderStateMachine.getInputPort(), false);
		wire(clientOrderStateMachine.toSliceStateMachine, sliceOrderStateMachine.getInputPort(), false);

		wire(sliceOrderStateMachine.toRootStateMachine, clientOrderStateMachine.getInputPort(), false);
		wire(sliceOrderStateMachine.toSliceStateMachine, sliceOrderStateMachine.getInputPort(), false);

		clientsm(UNINITIALIZED, NEW_ORDER_RCVD, new OmsUninitializedNewOrderRcvdProcessor());
		clientsm(UNINITIALIZED, REJECT_ORDER, new OmsPendingAckRejectOrderProcesor());
		clientsm(PENDING_ACK, ACKNOWLEDGE_ORDER, new OmsPendingAckAcknowledgeOrderProcessor());
		clientsm(PENDING_ACK, REJECT_ORDER, new OmsPendingAckRejectOrderProcesor());
		slicesm(ACKED, REJECT_ORDER, new OmsPendingAckOrderRejectedProcessor());
		clientsm(CANCEL_ORDER, new OmsDefaultCancelOrderProcessor());
		clientsm(ORDER_CANCELLED, new OmsDefaultOrderCancelledProcessor());
		clientsm(DONE_FOR_DAY, new OmsDefaultDoneForDayProcessor());
		clientsm(FILLED, ORDER_CANCELLED, new OmsDefaultRejectReplaceProcessor());
		clientsm(FILLED, CANCEL_ORDER, new OmsDefaultRejectReplaceProcessor());
		clientsm(UNINITIALIZED, CANCEL_ORDER, new OmsDefaultRejectReplaceProcessor());

		// TODO: Remove duplicate instantiations below...damn java
		clientsm(CUSTOM_DATA_UPDATED, new OmsDefaultAttachDataProcessor());
		clientsm(REPLACE_ORDER, new OmsDefaultRejectReplaceProcessor()); // Default
																			// option
																			// reject
																			// the
																			// replace
		clientsm(ACKED, REPLACE_ORDER, new OmsDefaultReplaceOrderProcessor());
		clientsm(PARTIAL, REPLACE_ORDER, new OmsDefaultReplaceOrderProcessor());
		clientsm(REPLACED, REPLACE_ORDER, new OmsDefaultReplaceOrderProcessor());
		clientsm(PENDING_PAUSE, REPLACE_ORDER, new OmsDefaultReplaceOrderProcessor());
		clientsm(PAUSED, REPLACE_ORDER, new OmsDefaultReplaceOrderProcessor());
		if (pc.getOptional(OPTION_REOPEN_PARENT_ORDERS, Boolean.FALSE))
			clientsm(FILLED, REPLACE_ORDER, new OmsDefaultReplaceOrderProcessor());
		clientsm(REPLACE_REJECTED, new OmsDefaultRejectReplaceProcessor());
		clientsm(ORDER_REPLACED, new OmsDefaultOrderReplacedProcessor());
		clientsm(ACKED, NEW_CHILD_ORDER, new OmsDefaultCreateChildOrderProcessor());
		clientsm(PARTIAL, NEW_CHILD_ORDER, new OmsDefaultCreateChildOrderProcessor());
		clientsm(REPLACED, NEW_CHILD_ORDER, new OmsDefaultCreateChildOrderProcessor());
		clientsm(PENDING_RPL, NEW_CHILD_ORDER, new OmsDefaultCreateChildOrderProcessor());
		// TODO:Should we allow the creation of a new child order when paused?

		clientsm(ACKED, ATTACH_EXECUTION, new OmsDefaultAttachExecutionProcessor());
		clientsm(PARTIAL, ATTACH_EXECUTION, new OmsDefaultAttachExecutionProcessor());
		clientsm(PENDING_CXL, ATTACH_EXECUTION, new OmsDefaultAttachExecutionProcessor());
		clientsm(PENDING_RPL, ATTACH_EXECUTION, new OmsDefaultAttachExecutionProcessor());
		clientsm(FILLED, ATTACH_EXECUTION, new OmsDefaultAttachExecutionProcessor());
		clientsm(CANCELLED, ATTACH_EXECUTION, new OmsDefaultAttachExecutionProcessor());// Should
																						// we
																						// allow
																						// this?
		clientsm(REPLACED, ATTACH_EXECUTION, new OmsDefaultAttachExecutionProcessor());
		clientsm(PENDING_PAUSE, ATTACH_EXECUTION, new OmsDefaultAttachExecutionProcessor());
		clientsm(PAUSED, ATTACH_EXECUTION, new OmsDefaultAttachExecutionProcessor());
		clientsm(CHILD_PARTIALLY_FILLED, new OmsDefaultChildFillProcessor());
		clientsm(CHILD_FULLY_FILLED, new OmsDefaultChildFillProcessor());
		clientsm(OmsAction.CHILD_REJECTED, new OmsDefaultChildRejectProcessor());
		clientsm(OmsAction.CHILD_CANCEL_SUCCEEDED, new OmsDefaultChildCancelledProcessor());
		clientsm(OmsAction.CANCEL_ALL_CHILD_ORDERS, new OmsDefaultCancelChildOrdersProcessor());
		clientsm(OmsAction.CHILD_ORDER_ACKNOWLEDGED, new OmsNoopProcessor());
		clientsm(OmsAction.CHILD_REPLACE_SUCCEEDED, new OmsNoopProcessor());

		clientsm(OmsAction.ATTACH_BUST_EXECUTION, new OmsDefaultAttachBustExecutionProcessor());
		clientsm(OmsAction.ATTACH_CORRECT_EXECUTION, new OmsDefaultAttachCorrectExecutionProcessor());
		clientsm(OmsAction.ORDER_RESTATED, new OmsDefaultOrderRestatedProcessor());
		clientsm(OmsAction.CHILD_EXEC_BUST, new OmsDefaultChildBustProcessor());
		clientsm(OmsAction.CHILD_EXEC_CORRECT, new OmsDefaultChildCorrectProcessor());

		slicesm(OmsAction.REPLACE_CHILD_ORDER, new OmsDefaultReplaceChildOrderProcessor());
		slicesm(CANCEL_CHILD_ORDER, new OmsDefaultCancelChildProcessor());

		slicesm(CANCELLED, OmsAction.REPLACE_CHILD_ORDER, new OmsRejectClientActionProcessor("Can't replace order in cancelled state"));
		slicesm(PENDING_ACK, ORDER_ACKED, new OmsPendingAckOrderAckedProcesspr());
		slicesm(PENDING_ACK, REJECT_ORDER, new OmsPendingAckOrderRejectedProcessor());
		slicesm(FILL_RECEIVED, new OmsDefaultFillOrderProcessor());
		slicesm(ORDER_CANCELLED, new OmsDefaultChildOrderCancelledProcessor());
		slicesm(OmsAction.DONE_FOR_DAY, new OmsDefaultChildOrderCancelledProcessor());
		slicesm(CHILD_REPLACE_SUCCEEDED, new OmsDefaultChildOrderReplacedProcessor());
		slicesm(REPLACE_REJECTED, new OmsDefaultCOCxlOrModifyRejectedProcessor());
		slicesm(PENDING_RPL, OmsAction.REPLACE_CHILD_ORDER, new OmsRejectClientActionProcessor("Can't replace order in pending replace"));
		slicesm(PENDING_RPL, OmsAction.CANCEL_CHILD_ORDER, new OmsRejectClientActionProcessor("Can't cancel order in pending replace"));
		slicesm(PENDING_CXL, OmsAction.REPLACE_CHILD_ORDER, new OmsRejectClientActionProcessor("Can't replace order in pending cancel"));
		slicesm(PENDING_CXL, OmsAction.CANCEL_CHILD_ORDER, new OmsRejectClientActionProcessor("Can't cancel order already in pending cancel"));
		slicesm(OmsAction.CHILD_EXEC_BUST, new OmsDefaultBustOrderProcessor());
		slicesm(OmsAction.CHILD_EXEC_CORRECT, new OmsDefaultCorrectOrderProcessor());
		slicesm(OmsAction.CHILD_EXEC_STATUS, new OmsNoopProcessor());
		clientsm(PENDING_ACK, NEW_CHILD_ORDER, new OmsDefaultCreateChildOrderProcessor());
		wire(omsClientHandler.output, clientOrderStateMachine, false);
		wire(omsClientRequestHandler.output, clientOrderStateMachine, false);
		for (Processor p : clientOrderStateMachine.getRegisteredProcessors()) {
			if (p.getParent() == null)
				addChild(p);
		}
		for (Processor p : sliceOrderStateMachine.getRegisteredProcessors()) {
			if (p.getParent() == null)
				addChild(p);
		}

		fromFix = exposeInputPort(clientOrderStateMachine.getInputPort());
		toOMSClient = exposeOutputPort(bundler.output);
		fromOMSClient = exposeInputPort(omsClientHandler.getInputPort()).setName("fromOMSClient");
		fromOMSRequestClient = exposeInputPort(omsClientRequestHandler.getInputPort()).setName("fromOMSClientRequest");
		SnapshotRequestProcessor snapshotRequestProcessor = new SnapshotRequestProcessor();
		OrderSnapshotRequestProcessor orderSnapshotRequestProcessor = new OrderSnapshotRequestProcessor();
		addChildren(snapshotRequestProcessor, orderSnapshotRequestProcessor);
		wire(snapshotRequestProcessor.toOrderRequestProcessor, orderSnapshotRequestProcessor, true);
		snapshotRequest = exposeInputPort(snapshotRequestProcessor);

	}

	public void init() {
		getContainer().getServices().getGenerator().register(ClientOrder.class, Slice.class, OmsOrder.class);
		pluginManager = new OmsPluginManager();
		LH.info(log, "Processing OMS Plugins and fix translations");
		pluginManager.processProperties(getContainer().getTools());
		LH.info(log, "Processed OMS Plugins and fix translations");
		getServices().putService(OmsPluginManager.SERVICE_ID, pluginManager);
		super.init();
	}

	public void start() {
		super.start();
		pluginManager.onStartup(getContainer());
	}

	public OmsPluginManager getPluginManager() {
		return pluginManager;
	}

}
