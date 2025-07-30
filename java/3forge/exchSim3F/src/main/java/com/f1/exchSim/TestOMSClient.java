package com.f1.exchSim;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.OutputPort;
import com.f1.container.PartitionResolver;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.pofo.fix.FixExecutionReport;
import com.f1.pofo.fix.FixOrderInfo;
import com.f1.pofo.fix.FixOrderReplaceRequest;
import com.f1.pofo.fix.FixOrderRequest;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsClientAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.Order;
import com.f1.pofo.oms.TimeInForce;
import com.f1.utils.CH;

public class TestOMSClient extends BasicProcessor<OmsNotification, OMSClientState> implements PartitionResolver<OmsNotification> {

	private static final String[] BROKERS = new String[] { "NASD", "BATS", "NITE", "NASD", "NYSE", "AMEX", "EURO" };
	ObjectGeneratorForClass<OmsClientAction> ocag;
	ObjectGeneratorForClass<FixOrderRequest> reqgen;
	ObjectGeneratorForClass<FixOrderInfo> oinfo;
	OutputPort<OmsClientAction> output;
	ObjectGeneratorForClass<FixExecutionReport> fixexec;
	ObjectGeneratorForClass<Execution> exec;
	public final OutputPort<OmsNotification> loopback = newOutputPort(OmsNotification.class);
	private Integer partitionsCount;

	public TestOMSClient() {
		super(OmsNotification.class, OMSClientState.class);
		output = newOutputPort(OmsClientAction.class);
		setPartitionResolver(this);
	}

	public void start() {
		super.start();
		ocag = getGenerator(OmsClientAction.class);
		reqgen = getGenerator(FixOrderRequest.class);
		oinfo = getGenerator(FixOrderInfo.class);
		fixexec = getGenerator(FixExecutionReport.class);
		exec = getGenerator(Execution.class);
	}

	@Override
	public void processAction(OmsNotification action, OMSClientState state, ThreadScope threadScope) throws Exception {
		OmsClientAction response = ocag.nw();
		response.setRootOrderID(action.getRootOrderID());
		if (action.getType() == null) {// was fired from timer
			Order o = action.getChangedOrders().get(0);
			response.setOrderAction(OmsAction.ATTACH_EXECUTION);
			FixExecutionReport report = fixexec.nw();
			Execution execution = genExec();
			int remaining = o.getOrderQty() - o.getTotalExecQty();
			if (o.getOrderQty() == 123)
				report.setPosResend(true);
			else if (remaining > 0) {
				if (remaining <= 100)
					execution.setExecQty(remaining);
				else {
					execution.setExecQty(100);
					OmsNotification delayedMessage = nw(OmsNotification.class);
					delayedMessage.setChangedOrders(CH.l(o));
					delayedMessage.setRootOrderID(action.getRootOrderID());
					loopback.sendDelayed(delayedMessage, state.getPartitionId(), null, 1000, TimeUnit.MICROSECONDS);
				}
			}
			o.setTotalExecQty(o.getTotalExecQty() + execution.getExecQty());
			execution.setExecPx(5.00);
			report.setExecution(execution);
			response.setEventDetails(report);
			response.setOrderID(action.getRootOrderID());
			response.setText("Created an execution!");
		} else
			switch (action.getType()) {
				case NEW_ORDER_RCVD: {
					response.setOrderID(action.getRootOrderID());
					Order order = action.getAddedOrders().get(0);
					state.addOrder(order);
					if (order.getSymbol().equals("GLW")) {
						response.setOrderAction(OmsAction.DONE_FOR_DAY);
						response.setText("GLW is done for day");
					} else if (order.getSymbol().equals("IBM")) {
						response.setOrderAction(OmsAction.REJECT_ORDER);
						response.setText("IBM orders not supported");
					} else if (order.getSymbol().equals("GE")) {
						response.setOrderAction(OmsAction.ORDER_CANCELLED);// Unsolicited cancel directly.
					} else {
						response.setOrderAction(OmsAction.ACKNOWLEDGE_ORDER);
					}
				}
					;
					break;
				case CANCEL_ORDER: {
					Order order = action.getChangedOrders().get(0);
					order = state.getOrder(order.getId());
					//Order order = action.getAddedOrders().get(0);
					response.setOrderID(action.getRootOrderID());
					if (order.getSymbol().equals("AAPL")) {
						response.setOrderAction(OmsAction.REPLACE_REJECTED);
						response.setText("Can not cancel AAPL");
					} else
						response.setOrderAction(OmsAction.ORDER_CANCELLED);
				}
					;
					break;
				case REPLACE_ORDER: {
					Order order = action.getChangedOrders().get(0);
					order = state.getOrder(order.getId());
					//Order order = action.getAddedOrders().get(0);
					if (order.getSymbol().equals("AAPL")) {
						response.setOrderAction(OmsAction.REPLACE_REJECTED);
						response.setText("Can not replace AAPL");
					}
					response.setOrderID(action.getRootOrderID());
					FixOrderReplaceRequest o = (FixOrderReplaceRequest) action.getPendingRequests().get(0);
					if (o.getOrderInfo().getOrderQty() == 543) {
						response.setOrderAction(OmsAction.REPLACE_REJECTED);
						response.setText("543 is the magic reject number");
					} else
						response.setOrderAction(OmsAction.ORDER_REPLACED);
					break;
				}
				case ATTACH_EXECUTION: {
					Order o = action.getChangedOrders().get(0);
					o = state.getOrder(o.getId());
					if (o.getSymbol().equalsIgnoreCase("MSFT")) {
						if (o.getTimeInForce() == TimeInForce.IOC & (o.getOrderQty() - o.getTotalExecQty()) > 200) {
							// if (o.getTimeInForce() == TimeInForce.IOC) {
							response.setOrderAction(OmsAction.ORDER_CANCELLED);
						}
					}
				}
					break;
				case ORDER_ACKED: {
					Order o = action.getChangedOrders().get(0);
					o = state.getOrder(o.getId());
					if (o.getSymbol().equalsIgnoreCase("MSFT")) {
						response.setOrderAction(OmsAction.ATTACH_EXECUTION);
						FixExecutionReport report = fixexec.nw();
						Execution execution = genExec();
						if (o.getOrderQty() == 123)
							report.setPosResend(true);
						if (o.getOrderQty() < 200)
							execution.setExecQty(o.getOrderQty());
						else
							execution.setExecQty(o.getOrderQty() / 2);
						execution.setExecPx(5.00);
						report.setExecution(execution);
						response.setEventDetails(report);
						response.setOrderID(action.getRootOrderID());
						report.setText("Here's your execution!");
					} else if (o.getSymbol().equalsIgnoreCase("QQQQ")) { // partial and full fill
						response.setOrderAction(OmsAction.ATTACH_EXECUTION);
						FixExecutionReport report = fixexec.nw();
						Execution execution = genExec();
						execution.setExecQty(o.getOrderQty() / 2);
						execution.setExecPx(5.00);
						report.setExecution(execution);
						response.setEventDetails(report);
						response.setOrderID(action.getRootOrderID());
						output.send(response, threadScope);
						response = ocag.nw();
						response.setOrderAction(OmsAction.ATTACH_EXECUTION);
						report = fixexec.nw();
						execution = genExec();
						execution.setExecQty(o.getOrderQty() / 2);
						execution.setExecPx(6.00);
						report.setExecution(execution);
						response.setEventDetails(report);
						response.setOrderID(action.getRootOrderID());
					} else {
						response.setOrderAction(OmsAction.ATTACH_EXECUTION);
						FixExecutionReport report = fixexec.nw();
						Execution execution = genExec();
						if (o.getOrderQty() == 123)
							report.setPosResend(true);
						if (o.getOrderQty() <= 100)
							execution.setExecQty(o.getOrderQty());
						else {
							execution.setExecQty(100);
							OmsNotification delayedMessage = nw(OmsNotification.class);
							delayedMessage.setChangedOrders(CH.l(o));
							delayedMessage.setRootOrderID(action.getRootOrderID());
							if (!o.getSymbol().equalsIgnoreCase("AAPL")) {
								loopback.sendDelayed(delayedMessage, state.getPartitionId(), null, 1000, TimeUnit.MICROSECONDS);
							}
						}
						o.setTotalExecQty(o.getTotalExecQty() + execution.getExecQty());
						execution.setExecPx(5.00);
						report.setExecution(execution);
						response.setEventDetails(report);
						response.setOrderID(action.getRootOrderID());
					}
				}
			}
		if (response.getOrderAction() != null)
			output.send(response, threadScope);
	}

	private Execution genExec() {
		Execution execution = exec.nw();
		execution.setLastMkt("SIM");
		String broker = BROKERS[new Random().nextInt(BROKERS.length)];
		execution.setExecBroker(broker);
		execution.setContraBroker("DOTHRGUY");
		execution.setExecTime(getTools().getNowNanoDate());
		return execution;
	}

	public void init() {
		super.init();
		partitionsCount = getTools().getOptional("client.partitions", 6);
	}

	@Override
	public Object getPartitionId(OmsNotification action) {
		return Integer.valueOf(action.getRootOrderID().hashCode() % partitionsCount);
	}

}
