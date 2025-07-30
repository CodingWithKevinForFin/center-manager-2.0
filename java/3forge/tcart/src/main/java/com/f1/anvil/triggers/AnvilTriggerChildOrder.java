package com.f1.anvil.triggers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiPreparedQuery;
import com.f1.ami.center.table.AmiPreparedQueryCompareClause;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.triggers.AmiAbstractTrigger;
import com.f1.anvil.utils.AnvilMarketDataMap;
import com.f1.anvil.utils.AnvilMarketDataSymbol;
import com.f1.anvil.utils.AnvilTimedEvent;
import com.f1.utils.LH;
import com.f1.utils.Scheduler;
import com.f1.utils.concurrent.ObjectPoolForClearable;
import com.f1.utils.structs.table.derived.StackFrame;

public class AnvilTriggerChildOrder extends AmiAbstractTrigger {
	private static final String ROUTES_OUT_LIQUIDITY = "OL";

	private static final Logger log = LH.get(AnvilTriggerChildOrder.class);

	private AnvilServices service;
	private AnvilSchema schema;
	private ObjectPoolForClearable<AnvilTimedEvent> timedEventPool;
	private Scheduler<AnvilTimedEvent> timerQueue;
	private AnvilMarketDataMap marketData;

	private long chainId;

	private ArrayList<AmiRow> searchResults;

	private AmiPreparedQuery childMessageClOrdIdQuery;
	private AmiPreparedQueryCompareClause childMessageClOrdIdQuery_idParam;

	private AmiPreparedQuery childOrderChainIdQuery;
	private AmiPreparedQueryCompareClause childOrderChainIdQuery_chainIdParam;

	private AmiPreparedQuery childModifyClOrderIdQuery;
	private AmiPreparedQueryCompareClause childModifyClOrderIdQuery_idParam;

	private AmiPreparedQuery executionsByIdQuery;
	private AmiPreparedQueryCompareClause executionsByIdQuery_idParam;

	private AmiPreparedQuery orderTableQuery;
	private AmiPreparedQueryCompareClause orderTableIndex;

	private AmiPreparedRow preparedChildOrdersRow;
	private AmiPreparedRow preparedChildOrderRow;
	private AmiPreparedRow preparedOrderRow;
	private AmiPreparedRow preparedExecutionRow;

	@Override
	public void onStartup(AmiImdbSession session) {
		this.service = getImdb().getAmiServiceOrThrow(AnvilServices.SERVICE_NAME, AnvilServices.class);
		this.schema = this.service.getSchema();
		this.timedEventPool = this.service.getTimedEventPool();
		this.timerQueue = service.getTimerQueue();
		this.marketData = service.getMarketData();
		this.chainId = 0;
		initSchema();
	}

	private void initSchema() {
		preparedChildOrdersRow = schema.childOrdersTable.createAmiPreparedRow();
		preparedChildOrderRow = schema.childOrderTable.createAmiPreparedRow();
		preparedOrderRow = schema.oTable.createAmiPreparedRow();
		preparedExecutionRow = schema.exTable.createAmiPreparedRow();
		this.searchResults = new ArrayList<AmiRow>();

		childMessageClOrdIdQuery = schema.childOrderTable.createAmiPreparedQuery();
		childMessageClOrdIdQuery.addNe(schema.childOrderTable_chainId).setValue(null);
		childMessageClOrdIdQuery_idParam = childMessageClOrdIdQuery.addEq(schema.childOrderTable_clOrderId);
		LH.info(log, "INDEX: ", childMessageClOrdIdQuery);

		childOrderChainIdQuery = schema.childOrdersTable.createAmiPreparedQuery();
		childOrderChainIdQuery_chainIdParam = childOrderChainIdQuery.addEq(schema.childOrdersTable_chainId);
		LH.info(log, "INDEX: ", childOrderChainIdQuery);

		childModifyClOrderIdQuery = schema.childModifyTable.createAmiPreparedQuery();
		childModifyClOrderIdQuery_idParam = childModifyClOrderIdQuery.addEq(schema.childModifyTable_clOrderId);
		LH.info(log, "INDEX: ", childModifyClOrderIdQuery);

		executionsByIdQuery = schema.exTable.createAmiPreparedQuery();
		executionsByIdQuery_idParam = executionsByIdQuery.addEq(schema.exTable_oID);
		LH.info(log, "INDEX: ", executionsByIdQuery);

		this.orderTableQuery = schema.oTable.createAmiPreparedQuery();
		this.orderTableIndex = orderTableQuery.addCompare(schema.oTable_orderID, AmiPreparedQueryCompareClause.EQ);
		LH.info(log, "INDEX: ", orderTableQuery);
	}

	@Override
	public void onInserted(AmiTable table, AmiRow childOrderRow, AmiImdbSession session, StackFrame sf) {
		long startTime = System.nanoTime();
		String clOrderId = childOrderRow.getString(schema.childOrderTable_clOrderId);
		String parentId = childOrderRow.getString(schema.childOrderTable_parentId);
		String symbol = childOrderRow.getString(schema.childOrderTable_symbol);
		long time = childOrderRow.getLong(schema.childOrderTable_time);
		String childStatus = childOrderRow.getString(schema.childOrderTable_status);
		searchModifysByOrderId(clOrderId);
		int pendingCnt = 0;
		int pendingLatency = 0;
		int processedCnt = 0;
		int processedLatency = 0;
		for (AmiRow modRow : searchResults) {
			char status = modRow.getString(schema.childModifyTable_status).charAt(0);
			long latency = modRow.getLong(schema.childModifyTable_time) - time;
			if (status == 'P') {
				pendingCnt++;
				pendingLatency += latency;
			} else {
				processedCnt++;
				processedLatency += latency;
			}
		}

		AmiRow parentOrder = searchForParentOrderById(parentId);
		boolean parentOrderNull = parentOrder == null;
		preparedChildOrderRow.reset();
		if (!parentOrderNull && symbol == null) {
			symbol = parentOrder.getString(schema.oTable_sym);
			preparedChildOrderRow.setString(schema.childOrderTable_symbol, symbol);

		}
		service.resolveCurrency(childOrderRow, preparedChildOrderRow, schema.childOrderTable_baseCurrency, schema.childOrderTable_baseLimitPx, schema.childOrderTable_limitPx);
		schema.childOrderTable.updateAmiRow(childOrderRow.getAmiId(), preparedChildOrderRow, session);
		//SEARCH FOR EXECUTIONS
		searchForExecutionsById(clOrderId);
		int totExecSize = 0;
		double totExecValue = 0;
		int exCnt = 0;
		int darkCnt = 0;
		int darkVol = 0;
		double darkVal = 0;
		int liqCnt = 0;
		int liqVol = 0;
		double liqVal = 0;
		int routedOutCnt = 0;
		int routedOutVolume = 0;
		double routedOutValue = 0;
		boolean newOrder = false;
		for (AmiRow exRow : searchResults) {
			preparedExecutionRow.reset();
			if (!parentOrderNull) {
				if (exRow.getIsNull(schema.exTable_sym)) {
					preparedExecutionRow.setString(schema.exTable_sym, symbol);
					preparedExecutionRow.setString(schema.exTable_industry, parentOrder.getString(schema.oTable_industry));
					preparedExecutionRow.setString(schema.exTable_sector, parentOrder.getString(schema.oTable_sector));
					preparedExecutionRow.setDouble(schema.exTable_prevClose, parentOrder.getDouble(schema.oTable_prevClose));
				}
				if (exRow.getIsNull(schema.exTable_side))
					preparedExecutionRow.setString(schema.exTable_side, parentOrder.getString(schema.oTable_side));
			}
			String venue = exRow.getString(schema.exTable_ex);
			boolean isLit = service.isVenueLit(venue);
			String execIndicator = exRow.getString(schema.exTable_execIndicator);
			boolean addsLiquidity = checkIfExecutionAddsLiquidity(execIndicator);
			if (ROUTES_OUT_LIQUIDITY.equals(execIndicator)) {
				routedOutCnt++;
				routedOutVolume += totExecSize;
				routedOutValue += totExecValue;
			}

			if (addsLiquidity || !isLit) {
				int execSize = (int) exRow.getLong(schema.exTable_size);
				double execVal = exRow.getDouble(schema.exTable_px) * execSize;
				if (addsLiquidity) {
					liqCnt++;
					liqVol += execSize;
					liqVal += execVal;
				}
				if (!isLit) {
					darkCnt++;
					darkVol += execSize;
					execVal += execVal;
				}
			}

			long exTime = exRow.getLong(schema.exTable_time);
			int execSize = (int) exRow.getLong(schema.exTable_size);
			totExecSize += execSize;
			totExecValue = totExecValue + execSize * exRow.getDouble(schema.exTable_px);
			exCnt++;
			preparedExecutionRow.setString(schema.exTable_parentId, parentId);
			schema.exTable.updateAmiRow(exRow.getAmiId(), preparedExecutionRow, session);
			if (!parentOrderNull) {
				AnvilMarketDataSymbol marketDataForSymbol = marketData.getMarketData(symbol);
				timerQueue.addEvent(exTime, timedEventPool.nw().reset(exRow.getAmiId(), exTime, AnvilTimedEvent.EXECUTION, marketDataForSymbol, AnvilTimedEvent.NONE));
				timerQueue.addEvent(exTime + AnvilTriggerOrders.EX_T1_TIME_EVENT_OFFSET, timedEventPool.nw().reset(exRow.getAmiId(),
						exTime + AnvilTriggerOrders.EX_T1_TIME_EVENT_OFFSET, AnvilTimedEvent.EXECUTION_TIMER, marketDataForSymbol, AnvilTimedEvent.T1));
				timerQueue.addEvent(exTime + AnvilTriggerOrders.EX_T5_TIME_EVENT_OFFSET, timedEventPool.nw().reset(exRow.getAmiId(),
						exTime + AnvilTriggerOrders.EX_T5_TIME_EVENT_OFFSET, AnvilTimedEvent.EXECUTION_TIMER, marketDataForSymbol, AnvilTimedEvent.T5));
			}
		}
		this.service.resetTimers();
		long targetQty = childOrderRow.getLong(schema.childOrderTable_qty);
		double limitPx = childOrderRow.getDouble(schema.childOrderTable_limitPx);
		double baseLimitPx = childOrderRow.getDouble(schema.childOrderTable_baseLimitPx);
		double size = childOrderRow.getDouble(schema.childOrderTable_qty);
		//		double filledValue = childOrderRow.getDouble(schema.childOrdersTable_filledValue);
		//		double openVal = childOrderRow.getDouble(schema.childOrdersTable_openVal);
		String baseCurrency = childOrderRow.getString(schema.childOrderTable_baseCurrency);
		int leaves = 0;
		if (childOrderRow.getIsNull(schema.childOrderTable_origClOrderId)) {
			/*----------------------
			 * NEW CHILD ORDER
			 * ---------------------
			 */
			preparedChildOrdersRow.reset();
			preparedChildOrdersRow.setString(schema.childOrdersTable_clOrderId, clOrderId);
			preparedChildOrdersRow.setString(schema.childOrdersTable_parentId, parentId);
			preparedChildOrdersRow.setString(schema.childOrdersTable_symbol, childOrderRow.getString(schema.childOrderTable_symbol));
			preparedChildOrdersRow.setLong(schema.childOrdersTable_qty, targetQty);
			preparedChildOrdersRow.setDouble(schema.childOrdersTable_limitPx, limitPx);
			preparedChildOrdersRow.setDouble(schema.childOrdersTable_baseLimitPx, baseLimitPx);
			preparedChildOrdersRow.setString(schema.childOrdersTable_baseCurrency, baseCurrency);

			preparedChildOrderRow.reset();
			preparedChildOrderRow.setLong(schema.childOrderTable_chainId, chainId);
			schema.childOrderTable.updateAmiRow(childOrderRow.getAmiId(), preparedChildOrderRow, session);

			preparedChildOrdersRow.setLong(schema.childOrdersTable_chainId, chainId++);
			preparedChildOrdersRow.setLong(schema.childOrdersTable_pendingCnt, pendingCnt);
			preparedChildOrdersRow.setLong(schema.childOrdersTable_pendingLatency, pendingLatency);
			preparedChildOrdersRow.setLong(schema.childOrdersTable_processedCnt, processedCnt);
			preparedChildOrdersRow.setLong(schema.childOrdersTable_processedLatency, processedLatency);
			preparedChildOrdersRow.setLong(schema.childOrdersTable_filledQty, totExecSize);
			preparedChildOrdersRow.setDouble(schema.childOrdersTable_filledValue, totExecValue);
			leaves = (int) (targetQty - totExecSize);
			preparedChildOrdersRow.setLong(schema.childOrdersTable_openQty, leaves);
			preparedChildOrdersRow.setLong(schema.childOrdersTable_time, time);
			preparedChildOrdersRow.setLong(schema.childOrdersTable_updateTime, time);
			preparedChildOrdersRow.setString(schema.childOrdersTable_status, childStatus);
			preparedChildOrdersRow.setDouble(schema.childOrdersTable_openVal, limitPx * targetQty - totExecValue);
			preparedChildOrdersRow.setLong(schema.childOrdersTable_replaced, 0);
			schema.childOrdersTable.insertAmiRow(preparedChildOrdersRow, session);
			newOrder = true;
		} else {
			/*----------------------
			 *UPDATE CHILD ORDER
			 * ---------------------
			 */
			String origId = childOrderRow.getString(schema.childOrderTable_origClOrderId);
			AmiRow previousMessage = searchMessagsByOrderId(origId);
			if (previousMessage == null) {
				//LH.info(log, "ANVIL DEBUG: CHILD MESSAGE (U) ==> " + row + " THERE IS NO CHILD MESSAGE WITH clOrderId = " + clOrderId);
				return;
			}
			long chainId = previousMessage.getLong(schema.childOrderTable_chainId);
			preparedChildOrderRow.reset();
			preparedChildOrderRow.setLong(schema.childOrderTable_chainId, chainId);
			schema.childOrderTable.updateAmiRow(childOrderRow.getAmiId(), preparedChildOrderRow, session);
			AmiRow childOrders = searchForChildOrderByChainId(chainId);
			if (childOrders == null) {
				//LH.info(log, "ANVIL DEBUG: CHILD MESSAGE (U) ==> " + row + " PREVIOUS MESSAGE ==> " + previousMessage + " THERE ARE NO CHILD ORDERS WITH CHAIN ID ==> " + chainId);
				return;
			}
			int origLeaves = (int) childOrders.getLong(schema.childOrdersTable_openQty);
			long filledQty = childOrders.getLong(schema.childOrdersTable_filledQty);
			preparedChildOrdersRow.setLong(schema.childOrdersTable_qty, targetQty);
			preparedChildOrdersRow.setDouble(schema.childOrdersTable_limitPx, limitPx);
			preparedChildOrdersRow.setLong(schema.childOrdersTable_updateTime, time);
			preparedChildOrdersRow.setString(schema.childOrdersTable_status, childStatus);
			preparedChildOrdersRow.setLong(schema.childOrdersTable_pendingCnt, childOrders.getLong(schema.childOrdersTable_pendingCnt) + pendingCnt);
			preparedChildOrdersRow.setLong(schema.childOrdersTable_pendingLatency, childOrders.getLong(schema.childOrdersTable_pendingLatency) + pendingLatency);
			preparedChildOrdersRow.setLong(schema.childOrdersTable_processedCnt, childOrders.getLong(schema.childOrdersTable_processedCnt) + processedCnt);
			preparedChildOrdersRow.setLong(schema.childOrdersTable_processedLatency, childOrders.getLong(schema.childOrdersTable_processedLatency) + processedLatency);
			leaves = (int) (targetQty - (filledQty));
			preparedChildOrdersRow.setLong(schema.childOrdersTable_openQty, leaves);
			preparedChildOrdersRow.setLong(schema.childOrdersTable_replaced, 1);
			schema.childOrdersTable.updateAmiRow(childOrders.getAmiId(), preparedChildOrdersRow, session);
			leaves = leaves - origLeaves;
		}
		/*----------------------
		 *UPDATE PARENT ORDER
		 * ---------------------
		 */
		if (!parentOrderNull) {
			//			schema.oTable.fireTriggerUpdating(parentOrder);
			preparedOrderRow.reset();
			preparedOrderRow.setLong(schema.oTable_leaves, parentOrder.getLong(schema.oTable_leaves) + leaves);

			preparedOrderRow.setLong(schema.oTable_pendingCnt, parentOrder.getLong(schema.oTable_pendingCnt) + pendingCnt);
			preparedOrderRow.setLong(schema.oTable_pendingLatency, parentOrder.getLong(schema.oTable_pendingLatency) + pendingLatency);
			preparedOrderRow.setLong(schema.oTable_processedCnt, parentOrder.getLong(schema.oTable_processedCnt) + processedCnt);
			preparedOrderRow.setLong(schema.oTable_processedLatency, parentOrder.getLong(schema.oTable_processedLatency) + processedLatency);
			preparedOrderRow.setDouble(schema.oTable_liqVal, parentOrder.getDouble(schema.oTable_liqVal) + liqVal);
			preparedOrderRow.setLong(schema.oTable_liqVol, parentOrder.getLong(schema.oTable_liqVol) + liqVol);
			preparedOrderRow.setLong(schema.oTable_liqCnt, parentOrder.getLong(schema.oTable_liqCnt) + liqCnt);
			preparedOrderRow.setDouble(schema.oTable_darkVal, parentOrder.getDouble(schema.oTable_darkVal) + darkVal);
			preparedOrderRow.setLong(schema.oTable_darkVol, parentOrder.getLong(schema.oTable_darkVol) + darkVol);
			preparedOrderRow.setLong(schema.oTable_darkCnt, parentOrder.getLong(schema.oTable_darkCnt) + darkCnt);
			preparedOrderRow.setDouble(schema.oTable_routedVal, parentOrder.getDouble(schema.oTable_routedVal) + routedOutValue);
			preparedOrderRow.setLong(schema.oTable_routedVol, parentOrder.getLong(schema.oTable_routedVol) + routedOutVolume);
			preparedOrderRow.setLong(schema.oTable_routedCnt, parentOrder.getLong(schema.oTable_liqCnt) + routedOutCnt);
			if (newOrder) {
				preparedOrderRow.setDouble(schema.oTable_filledValue, parentOrder.getDouble(schema.oTable_filledValue) + totExecValue);
				preparedOrderRow.setLong(schema.oTable_childCnt, parentOrder.getLong(schema.oTable_childCnt) + 1);
				preparedOrderRow.setLong(schema.oTable_exCnt, parentOrder.getLong(schema.oTable_exCnt) + exCnt);
				preparedOrderRow.setLong(schema.oTable_filled, parentOrder.getLong(schema.oTable_filled) + totExecSize);
			}
			schema.oTable.updateAmiRow(parentOrder.getAmiId(), preparedOrderRow, session);
			//			schema.oTable.fireTriggerUpdated(parentOrder);
			if (newOrder)
				service.incrementStatsForChildMessageProcessed(1, startTime, System.nanoTime(), session);

		}
		if ("N".equals(childStatus) || "U".equals(childStatus)) {
			//ALERT NOTIONAL PER CHILD ORDER
			double notionalVal = size * baseLimitPx;
			if (notionalVal >= AnvilServices.THRESHOLD_NOTIONAL_PER_CHILDORDER) {
				AmiRow parentRow = searchForParentOrderById(parentId);
				String sector = null;
				String industry = null;
				String account = null;
				String system = null;
				if (parentRow != null) {
					sector = parentRow.getString(schema.oTable_sector);
					industry = parentRow.getString(schema.oTable_industry);
					account = parentRow.getString(schema.oTable_account);
					system = parentRow.getString(schema.oTable_system);
				}
				Map<String, Object> mapParams = new HashMap<String, Object>();
				mapParams.put("NOTIONAL", notionalVal);
				mapParams.put("CHILD_ID", clOrderId);
				service.addAlert(AnvilServices.ALERT_NOTIONAL_PER_CHILDORDER, parentId, sector, industry, account, symbol, system, time, 1, null, symbol, null, null, mapParams,
						session);
			}
		}

	}
	private AmiRow searchForParentOrderById(String clOrderId) {
		orderTableIndex.setValue(clOrderId);
		return schema.oTable.query(orderTableQuery);
	}

	private void searchForExecutionsById(String clOrderId) {
		searchResults.clear();
		executionsByIdQuery_idParam.setValue(clOrderId);
		schema.exTable.query(executionsByIdQuery, 10000000, searchResults);
	}

	private AmiRow searchMessagsByOrderId(String origId) {
		childMessageClOrdIdQuery_idParam.setValue(origId);
		return schema.childOrderTable.query(childMessageClOrdIdQuery);
	}

	private void searchModifysByOrderId(String clOrderId) {
		searchResults.clear();
		childModifyClOrderIdQuery_idParam.setValue(clOrderId);
		schema.childModifyTable.query(childModifyClOrderIdQuery, 10000000, searchResults);
	}

	private AmiRow searchForChildOrderByChainId(long chainId) {
		childOrderChainIdQuery_chainIdParam.setValue(chainId);
		return schema.childOrdersTable.query(childOrderChainIdQuery);
	}
	private boolean checkIfExecutionAddsLiquidity(String execIndicator) {
		return ("AL".equals(execIndicator));
	}

}
