package com.f1.fix.oms;

import java.io.IOException;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.OmsOrder;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.pofo.oms.OmsClientAction;
import com.f1.povo.standard.TextMessage;
import com.f1.utils.OH;

/**
 * receives requests, processes the contents, investigates the outcome (see
 * {@link OrderEventCtx#getRejectText()}) and replies accordingly.
 * 
 */
public class OmsClientRequestProcessor extends BasicRequestProcessor<OmsClientAction, OmsOrderState, TextMessage> {

	ObjectGeneratorForClass<OrderEventCtx> ctxgen;
	OutputPort<OrderEventCtx> output;

	public OmsClientRequestProcessor() {
		super(OmsClientAction.class, OmsOrderState.class, TextMessage.class);
		output = newOutputPort(OrderEventCtx.class);
	}

	public void start() {
		super.start();
		ctxgen = getGenerator(OrderEventCtx.class);
	}

	@Override
	protected TextMessage processRequest(RequestMessage<OmsClientAction> request, OmsOrderState state, ThreadScope threadScope) throws IOException {
		OmsClientAction action = request.getAction();
		OrderEventCtx ctx = ctxgen.nw();
		OmsOrder order = state.getOrder(action.getOrderID());
		ctx.setOrder(order);
		ctx.setOrderAction(action.getOrderAction());
		ctx.setText(action.getText());
		ctx.setFixMsgEvent(action.getEventDetails());
		ctx.setClientMsg(action);
		output.send(ctx, threadScope);
		TextMessage r = nw(TextMessage.class);
		r.setText(OH.noNull(ctx.getRejectText(), "OK"));
		return r;
	}

}
