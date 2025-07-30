package com.f1.anvil.triggers;

import java.util.logging.Logger;

import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiPreparedQuery;
import com.f1.ami.center.table.AmiPreparedQueryCompareClause;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.triggers.AmiAbstractTrigger;
import com.f1.utils.LH;
import com.f1.utils.structs.table.derived.StackFrame;

public class AnvilTriggerChildModify extends AmiAbstractTrigger {
	private static final Logger log = LH.get(AnvilTriggerChildModify.class);
	private AnvilServices service;
	private AnvilSchema schema;

	private AmiPreparedQuery childMessageClOrdIdQuery;
	private AmiPreparedQueryCompareClause childMessageClOrdIdQuery_idParam;
	private AmiPreparedQuery childOrderChainIdQuery;
	private AmiPreparedQueryCompareClause childOrderChainIdQuery_chainIdParam;
	private AmiPreparedQuery orderTableQuery;
	private AmiPreparedQueryCompareClause orderTableIndex;
	private AmiPreparedRow preparedChildOrdersRow;
	private AmiPreparedRow preparedChildModifyRow;
	private AmiPreparedRow preparedOrderRow;

	@Override
	public void onStartup(AmiImdbSession session) {
		this.service = getImdb().getAmiServiceOrThrow(AnvilServices.SERVICE_NAME, AnvilServices.class);
		this.schema = this.service.getSchema();
		initSchema();
	}
	private void initSchema() {
		preparedChildOrdersRow = schema.childOrdersTable.createAmiPreparedRow();
		preparedChildModifyRow = schema.childModifyTable.createAmiPreparedRow();
		preparedOrderRow = schema.oTable.createAmiPreparedRow();
		childMessageClOrdIdQuery = schema.childOrderTable.createAmiPreparedQuery();
		childMessageClOrdIdQuery_idParam = childMessageClOrdIdQuery.addEq(schema.childOrderTable_clOrderId);
		LH.info(log, "INDEX: ", childMessageClOrdIdQuery);

		childOrderChainIdQuery = schema.childOrdersTable.createAmiPreparedQuery();
		childOrderChainIdQuery_chainIdParam = childOrderChainIdQuery.addEq(schema.childOrdersTable_chainId);
		LH.info(log, "INDEX: ", childOrderChainIdQuery);

		this.orderTableQuery = schema.oTable.createAmiPreparedQuery();
		this.orderTableIndex = orderTableQuery.addCompare(schema.oTable_orderID, AmiPreparedQueryCompareClause.EQ);
		LH.info(log, "INDEX: ", orderTableQuery);

	}
	@Override
	public void onInserted(AmiTable table, AmiRow childModifyRow, AmiImdbSession session, StackFrame sf) {
		long startTime = System.nanoTime();
		String clOrderId = childModifyRow.getString(schema.childModifyTable_clOrderId);
		AmiRow childMessageRow = searchChildMessageTableByClOrdeId(clOrderId);
		if (childMessageRow == null) {
			LH.info(log, "Child order not found for clOrderId " + clOrderId + ": => " + childMessageRow);
			return;
		}
		long chainId = childMessageRow.getLong(schema.childOrderTable_chainId);
		//		childModifyRow.setLong(schema.childModifyTable_chainId, chainId);
		preparedChildModifyRow.reset();
		preparedChildModifyRow.setLong(schema.childModifyTable_chainId, chainId);
		schema.childModifyTable.updateAmiRow(childModifyRow.getAmiId(), preparedChildModifyRow, session);

		AmiRow childOrderRow = searchForChildOrderByChainId(chainId);
		if (childOrderRow == null) {
			LH.info(log, "row not found for chainId " + chainId + ": => " + childMessageRow);
			return;
		}
		String parentId = childOrderRow.getString(schema.childOrdersTable_parentId);
		char status = childModifyRow.getString(schema.childModifyTable_status).charAt(0);
		long modTime = childModifyRow.getLong(schema.childModifyTable_time);
		long time = childMessageRow.getLong(schema.childOrderTable_time);
		long latency = modTime - time;
		boolean pendingAck = false;
		long leavesAdjusted = 0;
		preparedChildOrdersRow.reset();
		switch (status) {
			case 'P': {
				pendingAck = true;
				preparedChildOrdersRow.setLong(schema.childOrdersTable_pendingCnt, childOrderRow.getLong(schema.childOrdersTable_pendingCnt) + 1);
				preparedChildOrdersRow.setLong(schema.childOrdersTable_pendingLatency, childOrderRow.getLong(schema.childOrdersTable_pendingLatency) + latency);
				preparedChildOrdersRow.setString(schema.childOrdersTable_status, "P");
				break;
			}
			case 'A': {
				preparedChildOrdersRow.setLong(schema.childOrdersTable_processedCnt, childOrderRow.getLong(schema.childOrdersTable_processedCnt) + 1);
				preparedChildOrdersRow.setLong(schema.childOrdersTable_processedLatency, childOrderRow.getLong(schema.childOrdersTable_processedLatency) + latency);
				long qty = childOrderRow.getLong(schema.childOrdersTable_qty);
				preparedChildOrdersRow.setLong(schema.childOrdersTable_availQty, qty);
				preparedChildOrdersRow.setDouble(schema.childOrdersTable_availVal, childOrderRow.getDouble(schema.childOrdersTable_limitPx) * qty);
				preparedChildOrdersRow.setString(schema.childOrdersTable_status, "A");
				break;
			}
			case 'R': {
				String origId = childMessageRow.getString(schema.childOrderTable_origClOrderId);
				if (origId == null) {
					//TODO: CHILD ORDER REJECTED ON OPEN
					break;
				}
				childMessageRow = searchChildMessageTableByClOrdeId(origId);
				int panendAndRejectedQty = (int) childOrderRow.getLong(schema.childOrdersTable_qty);
				long lastAckedQty = childMessageRow.getLong(schema.childOrderTable_qty);
				preparedChildOrdersRow.setLong(schema.childOrdersTable_qty, lastAckedQty);
				preparedChildOrdersRow.setDouble(schema.childOrdersTable_limitPx, childMessageRow.getDouble(schema.childOrderTable_limitPx));
				preparedChildOrdersRow.setString(schema.childOrdersTable_clOrderId, origId);
				leavesAdjusted = lastAckedQty - panendAndRejectedQty;
				break;
			}
		}
		schema.childOrdersTable.updateAmiRow(childMessageRow.getAmiId(), preparedChildOrdersRow, session);

		AmiRow parentRow = searchForParentOrderById(parentId);
		if (parentRow != null) {
			//			schema.oTable.fireTriggerUpdating(parentRow);
			preparedOrderRow.reset();
			if (pendingAck) {
				preparedOrderRow.setLong(schema.oTable_pendingCnt, parentRow.getLong(schema.oTable_pendingCnt) + 1);
				preparedOrderRow.setLong(schema.oTable_pendingLatency, parentRow.getLong(schema.oTable_pendingLatency) + latency);
			} else {
				preparedOrderRow.setLong(schema.oTable_processedCnt, parentRow.getLong(schema.oTable_processedCnt) + 1);
				preparedOrderRow.setLong(schema.oTable_processedLatency, parentRow.getLong(schema.oTable_processedLatency) + latency);
			}
			if (leavesAdjusted != 0)
				preparedOrderRow.setLong(schema.oTable_leaves, parentRow.getLong(schema.oTable_leaves) + leavesAdjusted);
			//			schema.oTable.fireTriggerUpdated(parentRow);
			schema.oTable.updateAmiRow(parentRow.getAmiId(), preparedOrderRow, session);
			//			if (latency > 100) {
			//				//Generate High Latency Alert
			//				//				childAlertPreparedRow.reset();
			//			}
		}
		service.incrementStatsForChildModifyProcessed(1, startTime, System.nanoTime(), session);
	}

	private AmiRow searchForChildOrderByChainId(long chainId) {
		childOrderChainIdQuery_chainIdParam.setValue(chainId);
		return schema.childOrdersTable.query(childOrderChainIdQuery);
	}
	private AmiRow searchChildMessageTableByClOrdeId(String clOrderId) {
		childMessageClOrdIdQuery_idParam.setValue(clOrderId);
		return schema.childOrderTable.query(childMessageClOrdIdQuery);
	}
	private AmiRow searchForParentOrderById(String clOrderId) {
		orderTableIndex.setValue(clOrderId);
		return schema.oTable.query(orderTableQuery);
	}

}
