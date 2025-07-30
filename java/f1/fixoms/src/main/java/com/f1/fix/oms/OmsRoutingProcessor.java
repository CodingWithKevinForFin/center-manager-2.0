package com.f1.fix.oms;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import com.f1.container.InputPort;
import com.f1.container.OutputPort;
import com.f1.container.Processor;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.OmsOrder;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.SliceType;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.structs.table.BasicTable;

/**
 * routes OrderEvents to the appropriate processor based on the state of the order and the action order event type
 * 
 */
public class OmsRoutingProcessor extends BasicProcessor<OrderEventCtx, OmsOrderState> {
	private OutputPort<OrderEventCtx>[][] subProcessors = new OutputPort[OmsOrderStatus.MAX_VALUE.ordinal() + 1][OmsAction.MAX_VALUE.ordinal() + 1];
	private InputPort<OrderEventCtx> rootInputPort;
	private InputPort<OrderEventCtx> sliceInputPort;
	public final OutputPort<OrderEventCtx> toSliceStateMachine = newOutputPort(OrderEventCtx.class);
	public final OutputPort<OrderEventCtx> toRootStateMachine = newOutputPort(OrderEventCtx.class);
	private SliceType type;

	public void setup(InputPort<OrderEventCtx> rootPort, InputPort<OrderEventCtx> slicePort, SliceType type) {
		this.rootInputPort = rootPort;
		this.sliceInputPort = slicePort;
		this.type = type;
	}

	public OmsRoutingProcessor() {
		super(OrderEventCtx.class, OmsOrderState.class);
	}

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		OmsOrder order = action.getOrder();
		if (order != null) {
			if (OmsUtils.getSliceType(order) != type) {
				if (OmsUtils.getSliceType(order) == SliceType.SLICE)
					toSliceStateMachine.send(action, threadScope);
				else if (OmsUtils.getSliceType(order) == SliceType.CLIENT_ORDER)
					toRootStateMachine.send(action, threadScope);
				return;
			}
		}
		int orderStatus = OmsOrderStatus.UNINITIALIZED.getMask();
		if (action.getOrder() != null) {
			orderStatus = action.getOrder().getOrderStatus();
		}
		int status = MH.indexOfLastBitSet(orderStatus);
		OutputPort<OrderEventCtx> port;
		// TODO: Check for whether or not this is connected or log error for it
		if ((port = subProcessors[status][action.getOrderAction().getEnumValue()]) != null) {
			if (port.isConnected())
				port.send(action, threadScope);
		} else {
			LH.warning(log, "Invalid action ", action.getOrderAction(), " for ", type, " with status ", OmsOrderStatus.get(status), ". Dropping: ", action.toString());
			action.setRejectText("Invalid action " + action.getOrderAction() + " for " + type + " with status " + OmsOrderStatus.get(status));
		}
	}

	@Override
	public void start() {
		super.start();
		BasicTable table = new BasicTable(String.class, "Status", String.class, "Action", String.class, "Processor");
		table.setTitle("Oms State Transitions");
		for (OmsOrderStatus status : EnumSet.allOf(OmsOrderStatus.class)) {
			if (status == OmsOrderStatus.MAX_VALUE)
				continue;
			for (OmsAction action : EnumSet.allOf(OmsAction.class)) {
				if (action == OmsAction.MAX_VALUE)
					continue;
				OutputPort<OrderEventCtx> port = subProcessors[status.getEnumValue()][action.getEnumValue()];
				if (port != null)
					table.getRows().addRow(status, action, port.getProcessor().getName());
			}
		}
		LH.info(log, "Status / Action Matrix: " + SH.NEWLINE + TableHelper.toString(table, "", TableHelper.SHOW_ALL_BUT_TYPES));
	}

	public void registerProcessor(OmsOrderStatus status, OmsAction actions, OmsAbstractProcessor processor) {
		int statusIdx = status.getEnumValue();
		int actionIdx = actions.getEnumValue();
		OutputPort<OrderEventCtx> p;
		if ((p = subProcessors[statusIdx][actionIdx]) == null) {
			p = newOutputPort(OrderEventCtx.class);
			subProcessors[statusIdx][actionIdx] = p;
		}
		p.rewire(processor.getInputPort(), false);
		processor.toRootStateMachine.wire(rootInputPort, false);
		processor.toSliceStateMachine.wire(sliceInputPort, false);
	}

	public void registerDefaultProcessor(Processor<OrderEventCtx, OmsOrderState> processor) {
		for (int i = 0; i < subProcessors.length; i++) {
			for (int j = 0; j < subProcessors[i].length; j++) {
				if (!subProcessors[i][j].isConnected())
					subProcessors[i][j].wire(processor.getInputPort(), false);
			}
		}
	}

	public void registerDefaultProcessorForAction(OmsAbstractProcessor processor, OmsAction action) {
		int actionIdx = action.getEnumValue();
		for (int statusIdx = 0; statusIdx < subProcessors.length; statusIdx++) {
			OutputPort<OrderEventCtx> p;
			if ((p = subProcessors[statusIdx][actionIdx]) == null) {
				p = newOutputPort(OrderEventCtx.class);
				subProcessors[statusIdx][actionIdx] = p;
			}
			p.wire(processor.getInputPort(), false);
			if (!(processor.toRootStateMachine.isConnected()))
				processor.toRootStateMachine.wire(rootInputPort, false);
			if (!(processor.toSliceStateMachine.isConnected()))
				processor.toSliceStateMachine.wire(sliceInputPort, false);
		}
	}

	public List<Processor> getRegisteredProcessors() {
		List<Processor> list = new LinkedList<Processor>();
		for (int i = 0; i < subProcessors.length; i++) {
			for (int j = 0; j < subProcessors[i].length; j++) {
				OutputPort<OrderEventCtx> port = subProcessors[i][j];
				if (port != null && port.isConnected()) {
					list.add(port.getProcessor());
				}
			}
		}
		return list;
	}

}
