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

public class AnvilTriggerExecution extends AmiAbstractTrigger {
	private static final String ROUTED_OUT_LIQUIDITY = "OL";

	private static final Logger log = LH.get(AnvilTriggerExecution.class);

	public static final int EX_T1_TIME_EVENT_OFFSET = 60000;
	public static final int EX_T5_TIME_EVENT_OFFSET = 300000;

	private AnvilServices service;
	private AnvilSchema schema;

	private ArrayList<AmiRow> searchResults;
	private AmiPreparedQuery symQuery;
	private AmiPreparedQueryCompareClause symIndex;
	private AmiPreparedQuery orderTableQuery;
	private AmiPreparedQueryCompareClause orderTableIndex;
	private AmiPreparedQuery orderTableIdQuery;
	private AmiPreparedQueryCompareClause orderTableIdQuery_orderIdParam;
	private Scheduler<AnvilTimedEvent> timerQueue;
	private ObjectPoolForClearable<AnvilTimedEvent> timedEventPool;
	private AmiPreparedQuery secMasterQuery;
	private AmiPreparedQueryCompareClause secMasterQuery_symParam;
	private AnvilMarketDataMap marketData;
	private AmiPreparedQuery childMessageClOrdIdQuery;
	private AmiPreparedQueryCompareClause childMessageClOrdIdQuery_idParam;
	private AmiPreparedQuery childOrderChainIdQuery;
	private AmiPreparedQueryCompareClause childOrderChainIdQuery_chainIdParam;
	private AmiPreparedQueryCompareClause execTableExecIdIndex;
	private AmiPreparedQueryCompareClause execTableStatus;
	private AmiPreparedQuery execTableQuery;
	private AmiPreparedQuery exTableQuery;
	private AmiPreparedQueryCompareClause exTableQuery_symParam;
	private AmiPreparedQueryCompareClause exTableQuery_timeParam;
	private AmiPreparedRow preparedExecutionRow;
	private AmiPreparedRow preparedChildOrdersRow;
	private AmiPreparedRow preparedOrderRow;

	@Override
	public void onStartup(AmiImdbSession session) {
		this.service = getImdb().getAmiServiceOrThrow(AnvilServices.SERVICE_NAME, AnvilServices.class);
		this.schema = this.service.getSchema();
		this.timedEventPool = this.service.getTimedEventPool();
		this.timerQueue = service.getTimerQueue();
		this.marketData = service.getMarketData();
		initSchema();
	}
	private void initSchema() {
		preparedExecutionRow = schema.exTable.createAmiPreparedRow();
		preparedChildOrdersRow = schema.childOrdersTable.createAmiPreparedRow();
		preparedOrderRow = schema.oTable.createAmiPreparedRow();
		this.symQuery = schema.symTable.createAmiPreparedQuery();
		this.symIndex = symQuery.addCompare(schema.symTable_sym, AmiPreparedQueryCompareClause.EQ);
		LH.info(log, symQuery);

		this.orderTableQuery = schema.oTable.createAmiPreparedQuery();
		this.orderTableIndex = orderTableQuery.addCompare(schema.oTable_orderID, AmiPreparedQueryCompareClause.EQ);
		LH.info(log, orderTableQuery);
		this.orderTableIdQuery = schema.oTable.createAmiPreparedQuery();
		orderTableIdQuery_orderIdParam = orderTableIdQuery.addEq(schema.oTable_orderID);
		LH.info(log, orderTableIdQuery);
		this.execTableQuery = schema.exTable.createAmiPreparedQuery();
		this.execTableExecIdIndex = execTableQuery.addCompare(schema.exTable_execId, AmiPreparedQueryCompareClause.EQ);
		this.execTableStatus = execTableQuery.addCompare(schema.exTable_status, AmiPreparedQueryCompareClause.EQ);
		LH.info(log, execTableQuery);
		exTableQuery = schema.exTable.createAmiPreparedQuery();
		exTableQuery_symParam = exTableQuery.addEq(schema.exTable_sym);
		exTableQuery_timeParam = exTableQuery.addLt(schema.exTable_time);
		LH.info(log, exTableQuery);

		secMasterQuery = schema.secMasterTable.createAmiPreparedQuery();
		secMasterQuery_symParam = secMasterQuery.addEq(schema.secMasterTable_sym);
		LH.info(log, secMasterQuery);

		childMessageClOrdIdQuery = schema.childOrderTable.createAmiPreparedQuery();
		childMessageClOrdIdQuery_idParam = childMessageClOrdIdQuery.addEq(schema.childOrderTable_clOrderId);
		LH.info(log, childMessageClOrdIdQuery);

		childOrderChainIdQuery = schema.childOrdersTable.createAmiPreparedQuery();
		childOrderChainIdQuery_chainIdParam = childOrderChainIdQuery.addEq(schema.childOrdersTable_chainId);
		LH.info(log, childOrderChainIdQuery);

	}
	@Override
	public boolean onInserting(AmiTable table, AmiRow row, AmiImdbSession session, StackFrame sf) {
		String execId = row.getString(schema.exTable_execId);
		if (execId != null) {
			execTableExecIdIndex.setValue(execId);
			execTableStatus.setValue("N");
			AmiRow existingExec = schema.exTable.query(execTableQuery);
			if (existingExec != null) {
				LH.info(log, "we've got an exec modify: ", existingExec);
				processExecution(table, existingExec, true, session);
				preparedExecutionRow.setString(schema.exTable_status, "R");
				schema.exTable.updateAmiRow(existingExec.getAmiId(), preparedExecutionRow, session);
				//existingExec.setString(schema.exTable_status, "R");

				//				//				existingExec.setDouble(schema.exTable_px, row.getDouble(schema.exTable_px));
				//				//				existingExec.setDouble(schema.exTable_size, row.getDouble(schema.exTable_size));
				//				existingExec.setString(schema.exTable_status, "R");
				//				//				processExecution(table, existingExec, false);
				return true;
			}
		}
		return super.onInserting(table, row, session, sf);
	}
	@Override
	public void onInserted(AmiTable table, AmiRow row, AmiImdbSession session, StackFrame sf) {
		//		row.setString(schema.exTable_status, "N");
		processExecution(table, row, false, session);
		preparedExecutionRow.setString(schema.exTable_status, "N");
		schema.exTable.updateAmiRow(row.getAmiId(), preparedExecutionRow, session);

		String parentOrderID = row.getString(schema.exTable_parentId);
		String clOrderId = row.getString(schema.exTable_oID);
		AmiRow parentOrderRow = searchForParentOrderById(parentOrderID);
		String sector = parentOrderRow.getString(schema.oTable_sector);
		String industry = parentOrderRow.getString(schema.oTable_industry);
		String system = parentOrderRow.getString(schema.oTable_system);
		String account = parentOrderRow.getString(schema.oTable_account);

		long startTime = parentOrderRow.getLong(schema.oTable_time);
		long exTime = row.getLong(schema.exTable_time);
		double exPx = row.getDouble(schema.exTable_px);
		long size = row.getLong(schema.exTable_size);

		String side = row.getString(schema.exTable_side);
		String symbol = row.getString(schema.exTable_sym);

		AnvilMarketDataSymbol marketDataForSymbol = marketData.getMarketData(symbol);

		//ALERT EX NOTIONAL PER CHILD ORDER
		double notionalVal = size * exPx;
		if (notionalVal >= AnvilServices.THRESHOLD_NOTIONAL_PER_CHILDORDER) {
			Map<String, Object> mapParams = new HashMap<String, Object>();
			mapParams.put("NOTIONAL", notionalVal);
			mapParams.put("CHILD_ID", clOrderId);
			service.addAlert(AnvilServices.ALERT_EX_NOTIONAL_PER_CHILDORDER, parentOrderID, sector, industry, account, symbol, system, exTime, 1, null, symbol, null, null,
					mapParams, session);
		}

		//ALERT MARKET MOVED
		int startPos = marketDataForSymbol.getTradePositionLe(startTime);
		int exPos = marketDataForSymbol.getTradePositionLe(exTime);
		if (startPos != -1 && exPos != -1) {
			double startPx = marketDataForSymbol.getTradePxAtPos(startPos);
			double endPx = marketDataForSymbol.getTradePxAtPos(exPos);
			if (Math.abs(startPx - endPx) >= startPx * AnvilServices.THRESHOLD_MARKET_MOVED) {
				Map<String, Object> mapParams = new HashMap<String, Object>();
				mapParams.put("START_PX", startPx);
				mapParams.put("END_PX", endPx);
				service.addAlert(AnvilServices.ALERT_MARKET_MOVED, parentOrderID, sector, industry, account, symbol, system, exTime, 1, null, symbol, null, null, mapParams,
						session);
			}
		}
		//ALERT MISHIT
		int prevPos = marketDataForSymbol.getNbboPositionLt(exTime);
		if (prevPos != -1) {
			Map<String, Object> mapParams = new HashMap<String, Object>();
			mapParams.put("PRICE", exPx);
			if ("S".equals(side)) {
				double ask = marketDataForSymbol.getNbboAskPxAtPos(prevPos);
				if (Math.abs(exPx - ask) >= ask * AnvilServices.THRESHOLD_MISHIT_PCT) {
					mapParams.put("LAST_PRICE", ask);
					service.addAlert(AnvilServices.ALERT_MISHIT_BBO_PCT, parentOrderID, sector, industry, account, symbol, system, exTime, 1, null, symbol, null, null, mapParams,
							session);
				} else if (Math.abs(exPx - ask) >= AnvilServices.THRESHOLD_MISHIT_ABS) {
					mapParams.put("LAST_PRICE", ask);
					service.addAlert(AnvilServices.ALERT_MISHIT_BBO_ABS, parentOrderID, sector, industry, account, symbol, system, exTime, 1, null, symbol, null, null, mapParams,
							session);
				}
			} else {
				double bid = marketDataForSymbol.getNbboBidPxAtPos(prevPos);
				if (Math.abs(exPx - bid) >= bid * AnvilServices.THRESHOLD_MISHIT_PCT) {
					mapParams.put("LAST_PRICE", bid);
					service.addAlert(AnvilServices.ALERT_MISHIT_BBO_PCT, parentOrderID, sector, industry, account, symbol, system, exTime, 1, null, symbol, null, null, mapParams,
							session);
				} else if (Math.abs(exPx - bid) >= AnvilServices.THRESHOLD_MISHIT_ABS) {
					mapParams.put("LAST_PRICE", bid);
					service.addAlert(AnvilServices.ALERT_MISHIT_BBO_ABS, parentOrderID, sector, industry, account, symbol, system, exTime, 1, null, symbol, null, null, mapParams,
							session);
				}
			}
		}
		AmiRow lastEx = searchForLastExecBySymBeforeTime(symbol, exTime);
		if (lastEx != null) {
			double lastPx = lastEx.getDouble(schema.exTable_px);
			Map<String, Object> mapParams = new HashMap<String, Object>();
			mapParams.put("PRICE", exPx);
			mapParams.put("LAST_PRICE", lastPx);
			if (Math.abs(exPx - lastPx) >= lastPx * AnvilServices.THRESHOLD_MISHIT_PCT) {
				service.addAlert(AnvilServices.ALERT_MISHIT_TRANS_PCT, parentOrderID, sector, industry, account, symbol, system, exTime, 1, null, symbol, null, null, mapParams,
						session);
			} else if (Math.abs(exPx - lastPx) >= AnvilServices.THRESHOLD_MISHIT_ABS) {
				service.addAlert(AnvilServices.ALERT_MISHIT_TRANS_ABS, parentOrderID, sector, industry, account, symbol, system, exTime, 1, null, symbol, null, null, mapParams,
						session);
			}
		}
	}
	public void processExecution(AmiTable table, AmiRow row, boolean bust, AmiImdbSession session) {
		//		if ("EXEID-1554307340.828".equals(row.getString(schema.exTable_execId)))
		//			System.out.println("test");
		final long start = System.nanoTime();
		final long exTime = row.getLong(schema.exTable_time);
		int countDiff = bust ? -1 : 1;
		final int size = countDiff * (int) row.getLong(schema.exTable_size);
		final String venue = row.getString(schema.exTable_ex);
		String symbol = row.getString(schema.exTable_sym);
		String industry = null;
		String sector = null;

		preparedExecutionRow.reset();
		service.resolveCurrency(row, preparedExecutionRow, schema.exTable_baseCurrency, schema.exTable_basePx, schema.exTable_px);
		schema.exTable.updateAmiRow(row.getAmiId(), preparedExecutionRow, session);
		preparedExecutionRow.reset();
		final double val = size * row.getDouble(schema.exTable_px);

		String childId = row.getString(schema.exTable_oID);
		final AmiRow parentOrderRow;
		final String parentId;
		boolean hasChild;
		if (childId == null) {
			parentId = row.getString(schema.exTable_parentId);
			orderTableIndex.setValue(parentId);
			parentOrderRow = schema.oTable.query(orderTableQuery);
			hasChild = false;
		} else {
			AmiRow childMessageRow = searchMessagsByOrderId(childId);
			if (childMessageRow == null)
				return;
			long chainId = childMessageRow.getLong(schema.childOrderTable_chainId);
			parentId = childMessageRow.getString(schema.childOrderTable_parentId);
			preparedExecutionRow.setString(schema.exTable_parentId, parentId);
			AmiRow childOrder = searchForChildOrderByChainId(chainId);
			if (childOrder == null) {
				LH.info(log, "ANVIL DEBUG: EXECUTION LINKED TO ==> " + childMessageRow + " SEARCH FOR CHILD ORDER BY CHAIN ID RETURNED NULL");
				return;
			}
			preparedChildOrdersRow.reset();
			preparedChildOrdersRow.setLong(schema.childOrdersTable_filledQty, childOrder.getLong(schema.childOrdersTable_filledQty) + size);
			preparedChildOrdersRow.setDouble(schema.childOrdersTable_filledValue, childOrder.getDouble(schema.childOrdersTable_filledValue) + val);
			preparedChildOrdersRow.setLong(schema.childOrdersTable_openQty, childOrder.getLong(schema.childOrdersTable_openQty) - size);
			preparedChildOrdersRow.setDouble(schema.childOrdersTable_openVal, childOrder.getDouble(schema.childOrdersTable_openVal) - val);
			schema.childOrdersTable.updateAmiRow(childOrder.getAmiId(), preparedChildOrdersRow, session);
			hasChild = true;
			orderTableIndex.setValue(parentId);
			parentOrderRow = schema.oTable.query(orderTableQuery);
			//DONT AGREE THIS BELONGS HERE:
			//			if (symbol != null) {
			//				if (childOrder.getIsNull(schema.childOrderTable_symbol))
			//					childOrder.setString(schema.childOrderTable_symbol, symbol);
			//			}
		}
		if (symbol == null && parentOrderRow != null) {
			symbol = parentOrderRow.getString(schema.oTable_sym);
			preparedExecutionRow.setString(schema.exTable_sym, symbol);
		}

		if (symbol != null) {
			AmiRow secMasterRow = searchSecMasterForSymbol(symbol);
			if (secMasterRow != null) {
				industry = secMasterRow.getString(schema.secMasterTable_industry);
				sector = secMasterRow.getString(schema.secMasterTable_sector);
				preparedExecutionRow.setString(schema.exTable_industry, industry);
				preparedExecutionRow.setString(schema.exTable_sector, sector);
				preparedExecutionRow.setDouble(schema.exTable_prevClose, secMasterRow.getDouble(schema.secMasterTable_prevClose));
			}
		}

		if (parentOrderRow != null) {
			preparedExecutionRow.setString(schema.exTable_account, parentOrderRow.getString(schema.oTable_account));
			preparedExecutionRow.setString(schema.exTable_system, parentOrderRow.getString(schema.oTable_system));
			preparedExecutionRow.setString(schema.exTable_strategy, parentOrderRow.getString(schema.oTable_strategy));
			preparedExecutionRow.setString(schema.exTable_side, parentOrderRow.getString(schema.oTable_side));

			boolean isLit = service.isVenueLit(venue);
			//			schema.oTable.fireTriggerUpdating(parentOrderRow);
			preparedOrderRow.reset();

			preparedOrderRow.setLong(schema.oTable_exCnt, parentOrderRow.getLong(schema.oTable_exCnt) + countDiff);
			preparedOrderRow.setLong(schema.oTable_filled, parentOrderRow.getLong(schema.oTable_filled) + size);
			preparedOrderRow.setDouble(schema.oTable_filledValue, parentOrderRow.getDouble(schema.oTable_filledValue) + val);
			if (hasChild)
				preparedOrderRow.setLong(schema.oTable_leaves, parentOrderRow.getLong(schema.oTable_leaves) - size);
			if (!isLit) {
				preparedOrderRow.setDouble(schema.oTable_darkVal, parentOrderRow.getDouble(schema.oTable_darkVal) + val);
				preparedOrderRow.setDouble(schema.oTable_darkVol, parentOrderRow.getDouble(schema.oTable_darkVol) + size);
				preparedOrderRow.setLong(schema.oTable_darkCnt, parentOrderRow.getLong(schema.oTable_darkCnt) + countDiff);
			}
			String execIndicator = row.getString(schema.exTable_execIndicator);
			boolean addsLiquidity = checkIfExecutionAddsLiquidity(execIndicator);
			if (addsLiquidity) {
				preparedOrderRow.setDouble(schema.oTable_liqVal, parentOrderRow.getDouble(schema.oTable_liqVal) + val);
				preparedOrderRow.setDouble(schema.oTable_liqVol, parentOrderRow.getDouble(schema.oTable_liqVol) + size);
				preparedOrderRow.setLong(schema.oTable_liqCnt, parentOrderRow.getLong(schema.oTable_liqCnt) + countDiff);
			}
			if (ROUTED_OUT_LIQUIDITY.equals(execIndicator)) {
				preparedOrderRow.setDouble(schema.oTable_routedVal, parentOrderRow.getDouble(schema.oTable_routedVal) + val);
				preparedOrderRow.setLong(schema.oTable_routedCnt, parentOrderRow.getLong(schema.oTable_routedCnt) + countDiff);
				preparedOrderRow.setLong(schema.oTable_routedVol, parentOrderRow.getLong(schema.oTable_routedVol) + size);
			}

			//  Alert
			//		if (parentOrderRow.getLong(schema.oTable_size) < parentOrderRow.getLong(schema.oTable_filled)) {
			//			service.addAlert(AnvilServices.ALERT_OVERFILL, parentOrderRow.getString(schema.oTable_orderID), sector, industry, parentOrderRow.getString(schema.oTable_account),
			//					parentOrderRow.getString(schema.oTable_sym), parentOrderRow.getString(schema.oTable_system), exTime, 1, null, null, null, null);
			//		}

			schema.oTable.updateAmiRow(parentOrderRow.getAmiId(), preparedOrderRow, session);
			//			schema.oTable.fireTriggerUpdated(parentOrderRow);

			if (!bust) {
				AnvilMarketDataSymbol marketDataForSymbol = marketData.getMarketData(symbol);
				timerQueue.addEvent(exTime,
						timedEventPool.nw().reset(row.getAmiId(), exTime, bust ? AnvilTimedEvent.BUST : AnvilTimedEvent.EXECUTION, marketDataForSymbol, AnvilTimedEvent.NONE));
				timerQueue.addEvent(exTime + EX_T1_TIME_EVENT_OFFSET,
						timedEventPool.nw().reset(row.getAmiId(), exTime + EX_T1_TIME_EVENT_OFFSET, AnvilTimedEvent.EXECUTION_TIMER, marketDataForSymbol, AnvilTimedEvent.T1));
				timerQueue.addEvent(exTime + EX_T5_TIME_EVENT_OFFSET,
						timedEventPool.nw().reset(row.getAmiId(), exTime + EX_T5_TIME_EVENT_OFFSET, AnvilTimedEvent.EXECUTION_TIMER, marketDataForSymbol, AnvilTimedEvent.T5));
			}
			long end = System.nanoTime();
			this.service.incrementStatsForExecProcessed(1, start, end, session);
			this.service.resetTimers();
		}
		schema.exTable.updateAmiRow(row.getAmiId(), preparedExecutionRow, session);
	}
	private boolean checkIfExecutionAddsLiquidity(String execIndicator) {
		return ("AL".equals(execIndicator));
	}
	private AmiRow searchSecMasterForSymbol(String sym) {
		secMasterQuery_symParam.setValue(sym);
		return schema.secMasterTable.query(secMasterQuery);
	}

	private AmiRow searchForChildOrderByChainId(long chainId) {
		childOrderChainIdQuery_chainIdParam.setValue(chainId);
		return schema.childOrdersTable.query(childOrderChainIdQuery);
	}
	private AmiRow searchForLastExecBySymBeforeTime(String sym, long time) {
		exTableQuery_symParam.setValue(sym);
		exTableQuery_timeParam.setValue(time);
		return schema.exTable.query(exTableQuery);
	}
	private AmiRow searchForParentOrderById(String parentId) {
		orderTableIdQuery_orderIdParam.setValue(parentId);
		return schema.oTable.query(orderTableIdQuery);
	}
	private AmiRow searchMessagsByOrderId(String origId) {
		childMessageClOrdIdQuery_idParam.setValue(origId);
		return schema.childOrderTable.query(childMessageClOrdIdQuery);
	}
}
