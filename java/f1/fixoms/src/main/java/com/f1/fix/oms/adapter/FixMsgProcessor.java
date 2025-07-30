package com.f1.fix.oms.adapter;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.fix.oms.schema.ClientOrder;
import com.f1.fix.oms.schema.FixCopyUtil;
import com.f1.fix.oms.schema.OmsOrder;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.pofo.fix.ChildMessage;
import com.f1.pofo.fix.FixCancelRequest;
import com.f1.pofo.fix.FixExecutionReport;
import com.f1.pofo.fix.FixMsg;
import com.f1.pofo.fix.FixOrderReplaceReject;
import com.f1.pofo.fix.FixOrderReplaceRequest;
import com.f1.pofo.fix.FixOrderRequest;
import com.f1.pofo.fix.FixReport;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.Order;
import com.f1.pofo.oms.SliceType;

public class FixMsgProcessor extends BasicProcessor<FixMsg, OmsOrderState> {

	public final OutputPort<OrderEventCtx> toOms;
	ObjectGeneratorForClass<ClientOrder> cog;
	ObjectGeneratorForClass<Order> fog;
	ObjectGeneratorForClass<OrderEventCtx> ctxgen;

	public FixMsgProcessor() {
		super(FixMsg.class, OmsOrderState.class);
		toOms = newOutputPort(OrderEventCtx.class);
	}

	// NEW_ORDER_SINGLE:
	// PreCondition: OrderID has already been assigned as the partition ID
	// PreCondition: An empty state exists with the same ID

	public void init() {
		getContainer().getServices().getGenerator().register(ClientOrder.class, OrderEventCtx.class);
		super.init();
	}

	@Override
	public void start() {// This is fairly mechanical...perhaps a preprocessor or something can save a LOT of time
		super.start();
		cog = getGenerator(ClientOrder.class);
		fog = getGenerator(Order.class);
		ctxgen = getGenerator(OrderEventCtx.class);
	}

	@Override
	public void processAction(FixMsg action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		OrderEventCtx ctx;
		switch (action.getType()) {
			case NEW_ORDER_SINGLE:
				// Create the new order
				FixOrderRequest request = (FixOrderRequest) action;
				Order fixorder = fog.nw();
				FixCopyUtil.copy(fixorder, request);
				fixorder.setSliceType(SliceType.CLIENT_ORDER);
				ClientOrder order = cog.nw();
				order.setFixOrder(fixorder);
				order.setOrderStatus(OmsOrderStatus.UNINITIALIZED.getMask());
				fixorder.setId(state.getPartition().getPartitionId().toString());
				ctx = ctxgen.nw();
				ctx.setOrder(order);
				ctx.setFixMsgEvent(action);
				ctx.setOrderAction(OmsAction.NEW_ORDER_RCVD);
				toOms.send(ctx, threadScope);
				// call the state machine
				break;
			case CANCEL_REQUEST:
				// Pick up the original order
				FixCancelRequest event = (FixCancelRequest) action;
				ctx = ctxgen.nw();
				ctx.setOrder(state.getClientOrder());
				ctx.setFixMsgEvent(event);
				ctx.setOrderAction(OmsAction.CANCEL_ORDER);
				toOms.send(ctx, threadScope);
				break;
			// call the state machine
			case REPLACE_REQUEST:
				FixOrderReplaceRequest req = (FixOrderReplaceRequest) action;
				ctx = ctxgen.nw();
				ctx.setOrder(state.getOrder(req.getRefId()));
				ctx.setFixMsgEvent(req);
				ctx.setOrderAction(OmsAction.REPLACE_ORDER);
				toOms.send(ctx, threadScope);
				break;
			// Call the state machine
			case CANCEL_REJECT:
				FixOrderReplaceReject rej = (FixOrderReplaceReject) action;
				String orderId2;
				if (rej instanceof ChildMessage) {
					orderId2 = ((ChildMessage) rej).getChildId().getOrderId();
				} else
					orderId2 = rej.getRequestID();
				ctx = ctxgen.nw();
				ctx.setOrder(state.getOrder(orderId2));
				ctx.setFixMsgEvent(rej);
				ctx.setOrderAction(OmsAction.REPLACE_REJECTED);
				ctx.setText(rej.getText());
				toOms.send(ctx, threadScope);
				break;
			case EXECUTION_REPORT:
				// Pick up the original order
				FixReport report = (FixReport) action;
				String orderId;
				if (report instanceof ChildMessage) {
					orderId = ((ChildMessage) report).getChildId().getOrderId();
				} else
					orderId = report.getRequestId();
				OmsOrder corder = state.getOrder(orderId);
				ctx = ctxgen.nw();
				ctx.setOrder(corder);
				ctx.setFixMsgEvent(report);
				ctx.setText(report.getText());
				OmsAction oa = null;
				if (report instanceof FixExecutionReport) {
					Execution exec = ((FixExecutionReport) report).getExecution();
					int execTransType = exec.getExecTransType();

					switch (execTransType) {
						case 1:
							oa = OmsAction.CHILD_EXEC_BUST;
							break;
						case 2:
							oa = OmsAction.CHILD_EXEC_CORRECT;
							break;
					}
					//TODO: CLEAN THIS UP INTO AN ENUM OF EXECTRANSTYPE VALUES
				}
				if (oa == null)
					switch (report.getExecType()) {
						case REPLACED:
							oa = OmsAction.CHILD_REPLACE_SUCCEEDED;
							break;
						case ACKNOWLEDGED:
							oa = OmsAction.ORDER_ACKED;
							break;
						case PARTIAL:
						case FILLED:
							oa = OmsAction.FILL_RECEIVED;
							break;
						case CANCELLED:
							oa = OmsAction.ORDER_CANCELLED;
							break;
						case REJECTED:
							oa = OmsAction.REJECT_ORDER;
							break;
						case DONE_FOR_DAY:
							oa = OmsAction.DONE_FOR_DAY;
							break;
					}
				if (oa != null) {
					ctx.setOrderAction(oa);
					toOms.send(ctx, threadScope);
				}
				// Call the state machine
		}
	}
}
