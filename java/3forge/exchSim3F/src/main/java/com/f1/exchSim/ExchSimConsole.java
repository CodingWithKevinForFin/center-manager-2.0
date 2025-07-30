package com.f1.exchSim;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.base.Console;
import com.f1.base.DateNanos;
import com.f1.container.Container;
import com.f1.container.OutputPort;
import com.f1.container.PartitionController;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.ClientOrder;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.pofo.fix.FixExecutionReport;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsClientAction;
import com.f1.pofo.oms.Order;
import com.f1.utils.Formatter;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.TextMatcher;
import com.f1.utils.structs.table.BasicTable;

@Console(help = "Inspect and create executions in the exchange simulator")
public class ExchSimConsole {
	private static final Logger log = Logger.getLogger(ExchSimConsole.class.getName());

	private Container container;

	public ExchSimConsole(Container container) {
		this.container = container;
	}

	@Console(help = "Show all orders received by this exchange simulator")
	public String showOrders() {
		Formatter f = container.getServices().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME_FULL);
		PartitionController pc = container.getPartitionController();
		BasicTable bt = new BasicTable(String.class, "ID", DateNanos.class, "Created", Integer.class, "Qty", Double.class, "LimitPx", Integer.class, "Exec Qty", Integer.class,
				"Exec Val", Integer.class, "Exec Count", String.class, "State");
		for (Object o : pc.getPartitions()) {
			OmsOrderState state = (OmsOrderState) pc.getState(o, OmsOrderState.class);
			if (state != null) {
				try {
					state.getPartition().lockForRead(1, TimeUnit.SECONDS);
					ClientOrder co = state.getClientOrder();
					if (co == null || co.getFixOrder() == null) {
						LH.severe(log, "invalid order:", state);
						continue;
					}
					Order fo = co.getFixOrder();
					int status = MH.indexOfLastBitSet(co.getOrderStatus());
					bt.getRows().addRow(fo.getRequestId(), f.format(fo.getCreatedTime()), fo.getOrderQty(), fo.getLimitPx(), fo.getTotalExecQty(), fo.getTotalExecValue(),
							co.getExecutions() == null ? 0 : co.getExecutions().size(), OmsOrderStatus.get(status));
				} finally {
					state.getPartition().unlockForRead();
				}

			}
		}
		container.getParentContainerScope();
		TableHelper.sort(bt, "Created");
		return TableHelper.toString(bt, "", TableHelper.SHOW_ALL_BUT_TYPES);
	}

	@Console(help = "Show executions received by this exchange simulator for a particular order.  supply '*' for all executions ", params = { "orderIdPattern" })
	public String showExecutions(String pattern) {

		TextMatcher matcher = SH.m(pattern);
		Formatter f = container.getServices().getLocaleFormatter().getDateFormatter(LocaleFormatter.DATETIME_FULL);
		PartitionController pc = container.getPartitionController();
		BasicTable bt = new BasicTable(String.class, "ID", String.class, "Order ID", DateNanos.class, "Created", Integer.class, "Qty", Double.class, "Px");
		for (Object o : pc.getPartitions()) {
			OmsOrderState state = (OmsOrderState) pc.getState(o, OmsOrderState.class);
			if (state != null) {
				try {
					state.getPartition().lockForRead(1, TimeUnit.SECONDS);
					ClientOrder co = state.getClientOrder();
					Order fo = co.getFixOrder();
					if (matcher.matches(fo.getRequestId())) {
						final Map<String, Execution> executions = co.getExecutions();
						for (Execution e : executions.values()) {
							bt.getRows().addRow(e.getId(), fo.getRequestId(), f.format(e.getExecTime()), e.getExecQty(), e.getExecPx());
						}
					}
				} finally {
					state.getPartition().unlockForRead();
				}

			}
		}
		container.getParentContainerScope();
		TableHelper.sort(bt, "Created");
		return TableHelper.toString(bt, "", TableHelper.SHOW_ALL_BUT_TYPES);
	}

	@Console(help = "Send an execution for a known order", params = { "orderId", "quantity", "price" })
	public String exec(String orderIdPattern, int qty, double px) {
		TextMatcher matcher = SH.m(orderIdPattern);
		PartitionController pc = container.getPartitionController();
		Set<String> ids = new HashSet<String>();
		for (Object o : pc.getPartitions()) {
			OmsOrderState state = (OmsOrderState) pc.getState(o, OmsOrderState.class);
			if (state != null) {
				try {
					state.getPartition().lockForRead(1, TimeUnit.SECONDS);
					ClientOrder co = state.getClientOrder();
					if (co == null)
						continue;
					Order fo = co.getFixOrder();
					if (fo == null)
						continue;
					if (matcher.matches(fo.getRequestId())) {
						ids.add((String) state.getPartitionId());
					}
				} finally {
					state.getPartition().unlockForRead();
				}

			}
		}

		OutputPort fmp = (OutputPort) container.getRootSuite().getChild("TestOMSClient/OmsClientActionOutputPort");

		for (String id : ids) {
			OmsClientAction response = container.nw(OmsClientAction.class);
			response.setRootOrderID(id);
			response.setOrderAction(OmsAction.ATTACH_EXECUTION);
			FixExecutionReport report = container.nw(FixExecutionReport.class);
			Execution execution = container.nw(Execution.class);
			execution.setLastMkt("SIM");
			execution.setExecBroker("EXECBRK");
			execution.setContraBroker("DOTHRGUY");
			execution.setExecTime(container.getTools().getNowNanoDate());
			execution.setExecQty(qty);
			execution.setExecPx(px);
			report.setExecution(execution);
			response.setEventDetails(report);
			response.setOrderID(id);
			fmp.send(response, null);
		}
		return "created " + ids.size() + " execution(s)";
	}
}
