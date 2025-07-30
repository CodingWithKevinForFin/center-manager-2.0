package com.f1.fix.oms;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.OutputPort;
import com.f1.container.impl.BasicProcessor;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.FixCopyUtil;
import com.f1.fix.oms.schema.OmsOrder;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.pofo.fix.ExecType;
import com.f1.pofo.fix.FixExecutionReport;
import com.f1.pofo.fix.FixMsg;
import com.f1.pofo.fix.FixStatusReport;
import com.f1.pofo.fix.MsgType;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsNotification;
import com.f1.utils.OH;

/**
 * An implementation should apply the supplied order event to the supplied order state and optionally generate downstream events
 * 
 */
public abstract class OmsAbstractProcessor extends BasicProcessor<OrderEventCtx, OmsOrderState> {
	ObjectGeneratorForClass<FixExecutionReport> execGen;
	ObjectGeneratorForClass<FixStatusReport> statusGen;
	ObjectGeneratorForClass<OmsNotification> notifyGen;
	private OmsPluginManager omsPlugin;

	public OmsAbstractProcessor() {
		super(OrderEventCtx.class, OmsOrderState.class);
	}

	public void init() {
		omsPlugin = OH.assertNotNull((OmsPluginManager) getServices().getService(OmsPluginManager.SERVICE_ID));
		super.init();
	}

	protected OmsPluginManager getOmsPlugin() {
		return omsPlugin;
	}
	public void start() {
		super.start();
		execGen = getGenerator(FixExecutionReport.class);
		notifyGen = getGenerator(OmsNotification.class);
		statusGen = getGenerator(FixStatusReport.class);
	}

	public FixExecutionReport newExecutionReport(OmsOrder order, ExecType e, Execution ex) {
		FixExecutionReport report = execGen.nw();
		report.setExecution(ex);
		report.setType(MsgType.EXECUTION_REPORT);
		FixCopyUtil.copy(report, order.getFixOrder());
		report.setRequestId(order.getFixOrder().getRequestId());
		report.setExecType(e);
		return report;
	}

	public FixStatusReport newStatusReport(OmsOrder order, ExecType e) {
		FixStatusReport report = statusGen.nw();
		report.setType(MsgType.EXECUTION_REPORT);
		FixCopyUtil.copy(report, order.getFixOrder());
		report.setRequestId(order.getFixOrder().getRequestId());
		if (order.getPending() != null) {
			report.setRequestId(order.getPending().getRequestId());
			report.setOrigId(order.getFixOrder().getRequestId());
		} else {
			report.setRequestId(order.getFixOrder().getRequestId());
			report.setOrigId(order.getFixOrder().getOrigRequestId());
		}
		report.setExecType(e);
		return report;
	}

	public OmsNotification newNotification(OmsOrderState state) {
		OmsNotification notif = notifyGen.nw();
		notif.setRootOrderID(state.getClientOrder().getFixOrder().getId());
		return notif;
	}

	public final OutputPort<FixMsg> toFixSession = newOutputPort(FixMsg.class);
	public final OutputPort<OmsNotification> toOMSClient = newOutputPort(OmsNotification.class);
	public final OutputPort<OrderEventCtx> toSliceStateMachine = newOutputPort(OrderEventCtx.class);
	public final OutputPort<OrderEventCtx> toRootStateMachine = newOutputPort(OrderEventCtx.class);

}
