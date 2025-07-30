package com.f1.fix.oms;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.OmsOrder;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.pofo.oms.OmsClientAction;

/**
 * 
 * responsible for processing client actions the id of this request. Generally,
 * it looks up the order and produces an OrderEventCtx action which is then sent
 * out for further processing.
 * 
 */
public class OmsClientActionProcessor extends BasicProcessor<OmsClientAction, OmsOrderState> {

	ObjectGeneratorForClass<OrderEventCtx> ctxgen;
	OutputPort<OrderEventCtx> output;

	public OmsClientActionProcessor() {
		super(OmsClientAction.class, OmsOrderState.class);
		output = newOutputPort(OrderEventCtx.class);
	}

	public void start() {
		super.start();
		ctxgen = getGenerator(OrderEventCtx.class);
	}

	@Override
	public void processAction(OmsClientAction action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		OrderEventCtx ctx = ctxgen.nw();
		OmsOrder order = state.getOrder(action.getOrderID());
		ctx.setOrder(order);
		ctx.setOrderAction(action.getOrderAction());
		ctx.setText(action.getText());
		ctx.setFixMsgEvent(action.getEventDetails());
		ctx.setClientMsg(action);
		output.send(ctx, threadScope);
	}

}
