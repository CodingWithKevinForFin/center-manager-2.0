package com.f1.oms3f.start.oms;

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

public class OmsToDbProcessor extends BasicProcessor<OmsClientNotification, OmsClientState> {

	private static final String EXEC_PX = "exec_px";
	private static final String EXEC_QTY = "exec_qty";
	private static final String CONTRA_BROKER = "contra_broker";
	private static final String EXEC_BROKER = "exec_broker";
	private static final String LAST_MKT = "last_mkt";
	private static final String EXEC_STATUS = "exec_status";
	private static final String EXEC_TIME = "exec_time";
	private static final String EXTERNAL_EXEC_ID = "external_exec_id";
	private static final String EXEC_REF_ID = "exec_ref_id";
	private static final String ORDER_REVISION = "order_revision";
	private static final String EXEC_ID = "exec_id";
	private static final String EXEC_GROUP_ID = "exec_group_id";
	private static final String EXTERNAL_ORDER_ID = "external_order_id";
	private static final String STRATEGY = "strategy";
	private static final String END_TIME = "end_time";
	private static final String START_TIME = "start_time";
	private static final String NONE = "NONE";
	private static final String PASS_THRU_TAGS = "pass_thru_tags";
	private static final String TEXT = "text";
	private static final String SESSION_NAME = "session_name";
	private static final String EXEC_INSTRUCTIONS = "exec_instructions";
	private static final String TIME_IN_FORCE = "time_in_force";
	private static final String ORDER_TYPE = "order_type";
	private static final String SIDE = "side";
	private static final String LIMIT_PX = "limit_px";
	private static final String ORDER_QTY = "order_qty";
	private static final String ACCOUNT = "account";
	private static final String ORDER_STATUS = "order_status";
	private static final String UPDATED_TIME = "updated_time";
	private static final String CREATED_TIME = "created_time";
	private static final String DESTINATION = "destination";
	private static final String TOTAL_EXEC_VALUE = "total_exec_value";
	private static final String TOTAL_EXEC_QTY = "total_exec_qty";
	private static final String SYMBOL = "symbol";
	private static final String ID_TYPE = "id_type";
	private static final String SECURITY_ID = "security_id";
	private static final String REVISION = "revision";
	private static final String REQUEST_ID = "request_id";
	private static final String ORIG_REQUEST_ID = "orig_request_id";
	private static final String ORDER_ID = "order_id";
	private static final String ORDER_GROUP_ID = "order_group_id";
	private static final String SOURCE_SYSTEM = "source_system";
	private static final String INSERT_CHILD_EXECUTION_SQL = "insert_child_execution.sql";
	private static final String INSERT_CHILD_ORDER_SQL = "insert_child_order.sql";
	private static final String INSERT_PARENT_EXECUTION_SQL = "insert_parent_execution.sql";
	private static final String INSERT_PARENT_ORDER_SQL = "insert_parent_order.sql";

	public final OutputPort<RequestMessage<OmsSnapshotRequest>> snapshotRequest = (OutputPort) newOutputPort(RequestMessage.class);

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
		File sqlDir = getServices().getPropertyController().getRequired(StartOms3fMain.OPTION_SQL_DIR, File.class);
		if (!sqlDir.isDirectory())
			throw new RuntimeException("not a directory: " + IOH.getFullPath(sqlDir));
		this.datasource = OmsUtils.getOmsDb(this);
		try {
			this.insertParentOrderStatement = new FileDbStatement(new File(sqlDir, INSERT_PARENT_ORDER_SQL), getGenerator());
			this.insertParentExecutionStatement = new FileDbStatement(new File(sqlDir, INSERT_PARENT_EXECUTION_SQL), getGenerator());
			this.insertChildOrderStatement = new FileDbStatement(new File(sqlDir, INSERT_CHILD_ORDER_SQL), getGenerator());
			this.insertChildExecutionStatement = new FileDbStatement(new File(sqlDir, INSERT_CHILD_EXECUTION_SQL), getGenerator());
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
		RequestMessage rm = nw(RequestMessage.class);
		OmsSnapshotRequest osr = nw(OmsSnapshotRequest.class);
		rm.setAction(osr);
		snapshotRequest.send(rm, null);
	}

	public void toInsertOrder(Map<Object, Object> params, Order fixOrder) {
		params.put(SOURCE_SYSTEM, OH.noNull(fixOrder.getSourceSystem(), NONE));
		params.put(ORDER_GROUP_ID, fixOrder.getOrderGroupId());
		params.put(ORDER_ID, fixOrder.getId());
		params.put(ORIG_REQUEST_ID, fixOrder.getOrigRequestId());
		params.put(REQUEST_ID, fixOrder.getRequestId());
		params.put(REVISION, fixOrder.getRevision());
		params.put(SECURITY_ID, fixOrder.getSecurityID());
		params.put(ID_TYPE, fixOrder.getIDType());
		params.put(SYMBOL, fixOrder.getSymbol());
		params.put(TOTAL_EXEC_QTY, fixOrder.getTotalExecQty());
		params.put(TOTAL_EXEC_VALUE, fixOrder.getTotalExecValue());
		params.put(DESTINATION, fixOrder.getDestination());
		params.put(SESSION_NAME, fixOrder.getSessionName());
		params.put(CREATED_TIME, toTimestamp(fixOrder.getCreatedTime()));
		params.put(UPDATED_TIME, toTimestamp(fixOrder.getUpdatedTime()));
		params.put(ORDER_STATUS, fixOrder.getOrderStatus());
		params.put(ACCOUNT, fixOrder.getAccount());
		params.put(ORDER_QTY, fixOrder.getOrderQty());
		params.put(LIMIT_PX, fixOrder.getLimitPx());
		params.put(SIDE, SH.toString(fixOrder.getSide().getEnumValue()));
		params.put(ORDER_TYPE, SH.toString(fixOrder.getOrderType().getEnumValue()));
		params.put(TIME_IN_FORCE, SH.toString(fixOrder.getTimeInForce().getEnumValue()));
		params.put(EXEC_INSTRUCTIONS, fixOrder.getExecInstructions());
		params.put(TEXT, fixOrder.getText());
		params.put(PASS_THRU_TAGS, fixOrder.getPassThruTags() == null ? null : SH.joinMap('|', '=', fixOrder.getPassThruTags()));
		if (fixOrder.getPassThruTags() != null) {
			Map<Integer, String> ptt = fixOrder.getPassThruTags();
			params.put(START_TIME, ptt.get(7113));
			params.put(END_TIME, ptt.get(7114));
			params.put(STRATEGY, ptt.get(7111));
		} else {
			params.put(STRATEGY, null);
			params.put(START_TIME, null);
			params.put(END_TIME, null);
		}
	}
	public void toInsertParentOrder(Map<Object, Object> params, Order fixOrder) {
		toInsertOrder(params, fixOrder);
	}

	public void toInsertChildOrder(Map<Object, Object> params, Order fixOrder) {
		toInsertOrder(params, fixOrder);
		params.put(EXTERNAL_ORDER_ID, fixOrder.getExternalOrderId());
	}

	public void toInsertChildExecution(Map<Object, Object> params, Execution execution, Order fixOrder) {
		toInsertExecution(params, execution, fixOrder);
	}
	public void toInsertParentExecution(Map<Object, Object> params, Execution execution, Order fixOrder) {
		toInsertExecution(params, execution, fixOrder);
	}
	public void toInsertExecution(Map<Object, Object> params, Execution execution, Order fixOrder) {
		params.put(SOURCE_SYSTEM, OH.noNull(execution.getSourceSystem(), NONE));
		params.put(TOTAL_EXEC_QTY, fixOrder.getTotalExecQty());
		params.put(TOTAL_EXEC_VALUE, fixOrder.getTotalExecValue());
		params.put(ORDER_STATUS, fixOrder.getOrderStatus());
		params.put(EXEC_GROUP_ID, execution.getExecGroupID());
		params.put(EXEC_ID, execution.getId());
		params.put(ORDER_ID, execution.getOrderId());
		params.put(ORDER_REVISION, execution.getOrderRevision());
		params.put(REVISION, execution.getRevision());
		params.put(EXEC_REF_ID, null);// TODO
		params.put(EXTERNAL_EXEC_ID, null);// TODO
		params.put(EXEC_TIME, toTimestamp(execution.getExecTime()));
		params.put(EXEC_STATUS, execution.getExecStatus());// TODO
		params.put(LAST_MKT, execution.getLastMkt());
		params.put(EXEC_BROKER, execution.getExecBroker());
		params.put(CONTRA_BROKER, execution.getContraBroker());
		params.put(EXEC_QTY, execution.getExecQty());
		params.put(EXEC_PX, execution.getExecPx());
		params.put(PASS_THRU_TAGS, execution.getPassThruTags() == null ? null : SH.joinMap('|', '=', execution.getPassThruTags()));
	}
}