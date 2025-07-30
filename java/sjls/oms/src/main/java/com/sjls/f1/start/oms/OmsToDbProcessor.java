package com.sjls.f1.start.oms;

import java.io.File;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.f1.base.DateNanos;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fixomsclient.OmsClientNotification;
import com.f1.fixomsclient.OmsClientState;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsSnapshotRequest;
import com.f1.pofo.oms.Order;
import com.f1.pofo.oms.SliceType;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.db.FileDbStatement;
import com.f1.utils.structs.Tuple2;
import com.sjls.f1.sjlscommon.SJLSCustomTags;

public class OmsToDbProcessor extends BasicProcessor<OmsClientNotification, OmsClientState> {

	public final OutputPort<RequestMessage<OmsSnapshotRequest>> snapshotRequest = (OutputPort) newOutputPort(RequestMessage.class);

	// public final Port<MapMessage> out = newOutputPort(MapMessage.class);
	private DataSource datasource;
	private FileDbStatement insertParentOrderStatement;
	private FileDbStatement insertParentExecutionStatement;
	private FileDbStatement insertChildOrderStatement;
	private FileDbStatement insertChildExecutionStatement;

	public OmsToDbProcessor() {
		super(OmsClientNotification.class, OmsClientState.class);
	}

	@Override
	public void processAction(OmsClientNotification action, OmsClientState state, ThreadScope threadScope) throws Exception {
		if (action.getType() == OmsAction.SNAPSHOT)
			return;
		for (Order order : action.getAddedOrders()) {
			boolean isParent = order.getSliceType() == SliceType.CLIENT_ORDER;
			FileDbStatement statement = isParent ? insertParentOrderStatement : insertChildOrderStatement;
			final Map<Object, Object> params = new HashMap<Object, Object>();
			if (isParent) {
				toInsertParentOrder(params, order);
			} else {
				toInsertChildOrder(params, order);
			}
			Connection connection = null;
			try {
				connection = this.datasource.getConnection();
				statement.execute(params, connection);
			} finally {
				IOH.close(connection);
			}
		}
		for (Tuple2<Order, Order> order : action.getUpdatedOrders()) {
			boolean isParent = order.getB().getSliceType() == SliceType.CLIENT_ORDER;
			FileDbStatement statement = isParent ? insertParentOrderStatement : insertChildOrderStatement;
			final Map<Object, Object> params = new HashMap<Object, Object>();
			if (order.getA().getRevision() != order.getB().getRevision()) {
				if (isParent) {
					toInsertParentOrder(params, order.getB());
				} else {
					toInsertChildOrder(params, order.getB());
				}
				Connection connection = null;
				try {
					connection = this.datasource.getConnection();
					statement.execute(params, connection);
				} finally {
					IOH.close(connection);
				}
			}
		}
		for (Execution execution : action.getAddedExecutions()) {
			final Map<Object, Object> params = new HashMap<Object, Object>();
			Order order = state.getOrder(execution.getSourceSystem(), execution.getOrderId());
			boolean isParent = order.getSliceType() == SliceType.CLIENT_ORDER;
			FileDbStatement statement = isParent ? insertParentExecutionStatement : insertChildExecutionStatement;
			if (isParent)
				toInsertParentExecution(params, execution, order);
			else
				toInsertChildExecution(params, execution, order);
			Connection connection = null;
			try {
				connection = this.datasource.getConnection();
				statement.execute(params, connection);
			} finally {
				IOH.close(connection);
			}
		}
	}
	private Timestamp toTimestamp(DateNanos time) {
		if (time == null)
			return null;
		return time.toTimestamp();
	}

	public void start() {
		super.start();
		File sqlDir = getServices().getPropertyController().getRequired(StartOmsMain.OPTION_SQL_DIR, File.class);
		if (!sqlDir.isDirectory())
			throw new RuntimeException("not a directory: " + IOH.getFullPath(sqlDir));
		this.datasource = OmsUtils.getOmsDb(this);
		try {
			this.insertParentOrderStatement = new FileDbStatement(new File(sqlDir, "insert_parent_order.sql"), getGenerator());
			this.insertParentExecutionStatement = new FileDbStatement(new File(sqlDir, "insert_parent_execution.sql"), getGenerator());
			this.insertChildOrderStatement = new FileDbStatement(new File(sqlDir, "insert_child_order.sql"), getGenerator());
			this.insertChildExecutionStatement = new FileDbStatement(new File(sqlDir, "insert_child_execution.sql"), getGenerator());
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
		RequestMessage rm = nw(RequestMessage.class);
		OmsSnapshotRequest osr = nw(OmsSnapshotRequest.class);
		rm.setAction(osr);
		snapshotRequest.send(rm, null);
	}

	public void toInsertOrder(Map<Object, Object> params, Order fixOrder) {
		// params.put("slice_type", fixOrder.getSliceType().type);
		params.put("source_system", OH.noNull(fixOrder.getSourceSystem(), "NONE"));
		params.put("order_group_id", fixOrder.getOrderGroupId());
		params.put("order_id", fixOrder.getId());
		params.put("orig_request_id", fixOrder.getOrigRequestId());
		params.put("request_id", fixOrder.getRequestId());
		params.put("revision", fixOrder.getRevision());
		params.put("security_id", fixOrder.getSecurityID());
		params.put("id_type", fixOrder.getIDType());
		params.put("symbol", fixOrder.getSymbol());
		params.put("total_exec_qty", fixOrder.getTotalExecQty());
		params.put("total_exec_value", fixOrder.getTotalExecValue());
		params.put("destination", fixOrder.getDestination());
		params.put("session_name", fixOrder.getSessionName());
		params.put("created_time", toTimestamp(fixOrder.getCreatedTime()));
		params.put("updated_time", toTimestamp(fixOrder.getUpdatedTime()));
		params.put("order_status", fixOrder.getOrderStatus());
		params.put("account", fixOrder.getAccount());
		params.put("order_qty", fixOrder.getOrderQty());
		params.put("limit_px", fixOrder.getLimitPx());
		params.put("side", SH.toString(fixOrder.getSide().getEnumValue()));
		params.put("order_type", SH.toString(fixOrder.getOrderType().getEnumValue()));
		params.put("time_in_force", SH.toString(fixOrder.getTimeInForce().getEnumValue()));
		params.put("exec_instructions", fixOrder.getExecInstructions());
		params.put("text", fixOrder.getText());
		params.put("pass_thru_tags", fixOrder.getPassThruTags() == null ? null : SH.joinMap('|', '=', fixOrder.getPassThruTags()));
		if (fixOrder.getPassThruTags() != null) {
			Map<Integer, String> ptt = fixOrder.getPassThruTags();
			params.put("start_time", ptt.get(SJLSCustomTags.START_TIME_TAG));
			params.put("end_time", ptt.get(SJLSCustomTags.END_TIME_TAG));
			params.put("strategy", ptt.get(SJLSCustomTags.STRATEGY_TAG));
		} else {
			params.put("strategy", null);
			params.put("start_time", null);
			params.put("end_time", null);
		}
	}
	public void toInsertParentOrder(Map<Object, Object> params, Order fixOrder) {
		toInsertOrder(params, fixOrder);
	}

	public void toInsertChildOrder(Map<Object, Object> params, Order fixOrder) {
		toInsertOrder(params, fixOrder);
		params.put("external_order_id", fixOrder.getExternalOrderId());
	}

	public void toInsertChildExecution(Map<Object, Object> params, Execution execution, Order fixOrder) {
		toInsertExecution(params, execution, fixOrder);
	}
	public void toInsertParentExecution(Map<Object, Object> params, Execution execution, Order fixOrder) {
		toInsertExecution(params, execution, fixOrder);
	}
	public void toInsertExecution(Map<Object, Object> params, Execution execution, Order fixOrder) {
		params.put("source_system", OH.noNull(execution.getSourceSystem(), "NONE"));
		params.put("total_exec_qty", fixOrder.getTotalExecQty());
		params.put("total_exec_value", fixOrder.getTotalExecValue());
		params.put("order_status", fixOrder.getOrderStatus());
		params.put("exec_group_id", execution.getExecGroupID());
		params.put("exec_id", execution.getId());
		params.put("order_id", execution.getOrderId());
		params.put("order_revision", execution.getOrderRevision());
		params.put("revision", execution.getRevision());
		params.put("exec_ref_id", null);// TODO
		params.put("external_exec_id", null);// TODO
		params.put("exec_time", toTimestamp(execution.getExecTime()));
		params.put("exec_status", execution.getExecStatus());// TODO
		params.put("last_mkt", execution.getLastMkt());
		params.put("exec_broker", execution.getExecBroker());
		params.put("contra_broker", execution.getContraBroker());
		params.put("exec_qty", execution.getExecQty());
		params.put("exec_px", execution.getExecPx());
		params.put("pass_thru_tags", execution.getPassThruTags() == null ? null : SH.joinMap('|', '=', execution.getPassThruTags()));
	}
}