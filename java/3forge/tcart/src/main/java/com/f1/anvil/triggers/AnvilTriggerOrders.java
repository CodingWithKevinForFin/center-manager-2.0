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

public class AnvilTriggerOrders extends AmiAbstractTrigger {
	private static final String ROUTED_OUT_LIQUIDITY = "OL";
	private static final String ADDS_LIQUIDITY = "AL";

	private static final Logger log = LH.get(AnvilTriggerOrders.class);

	private static final char ORDER_STATUS_CANCEL = 'C';
	private static final char ORDER_STATUS_UPDATE = 'U';
	private static final char ORDER_STATUS_NEW = 'N';
	public static final int EX_T1_TIME_EVENT_OFFSET = 60000;
	public static final int EX_T5_TIME_EVENT_OFFSET = 300000;

	private AnvilServices service;
	private AnvilSchema schema;
	private Scheduler<AnvilTimedEvent> timerQueue;
	private ObjectPoolForClearable<AnvilTimedEvent> timedEventPool;
	private AnvilMarketDataMap marketData;

	private ArrayList<AmiRow> searchResults;
	private ArrayList<AmiRow> exSearchResults;

	private AmiPreparedQuery symQuery;
	private AmiPreparedQueryCompareClause symIndex;

	private AmiPreparedQuery orderTableQuery;
	private AmiPreparedQueryCompareClause orderTableIndex;

	private AmiPreparedQuery exTableQuery;
	private AmiPreparedQueryCompareClause exTableQuery_parentIdParam;

	private AmiPreparedQuery secMasterQuery;
	private AmiPreparedQueryCompareClause secMasterQuery_symParam;

	private AmiPreparedQuery childOrderParentIdQuery;
	private AmiPreparedQueryCompareClause childOrderParentIdQuery_parentID;

	private AmiPreparedQuery exTableChildOrderQuery;
	private AmiPreparedQueryCompareClause exTableChildOrderQuery_idParam;

	private AmiPreparedQuery ordersByAccountSideQuery;
	private AmiPreparedQueryCompareClause ordersByAccountSideQuery_accountParam;

	private AmiPreparedRow childAlertPreparedRow;
	private AmiPreparedRow preparedOrderRow;
	private AmiPreparedRow preparedExecutionRow;

	@Override
	public void onStartup(AmiImdbSession session) {
		this.service = getImdb().getAmiServiceOrThrow(AnvilServices.SERVICE_NAME, AnvilServices.class);
		this.schema = this.service.getSchema();
		this.timedEventPool = this.service.getTimedEventPool();
		this.timerQueue = service.getTimerQueue();
		this.marketData = service.getMarketData();
		initIndexes();
	}
	private void initIndexes() {
		this.symQuery = schema.symTable.createAmiPreparedQuery();
		this.symIndex = symQuery.addCompare(schema.symTable_sym, AmiPreparedQueryCompareClause.EQ);
		LH.info(log, symQuery);

		this.orderTableQuery = schema.oTable.createAmiPreparedQuery();
		this.orderTableIndex = orderTableQuery.addCompare(schema.oTable_orderID, AmiPreparedQueryCompareClause.EQ);
		LH.info(log, orderTableQuery);

		this.exTableQuery = this.schema.exTable.createAmiPreparedQuery();
		this.exTableQuery_parentIdParam = exTableQuery.addCompare(schema.exTable_parentId, AmiPreparedQueryCompareClause.EQ);
		this.searchResults = new ArrayList<AmiRow>();
		this.exSearchResults = new ArrayList<AmiRow>();
		LH.info(log, exTableQuery);

		secMasterQuery = schema.secMasterTable.createAmiPreparedQuery();
		secMasterQuery_symParam = secMasterQuery.addEq(schema.secMasterTable_sym);
		LH.info(log, secMasterQuery);

		childOrderParentIdQuery = schema.childOrdersTable.createAmiPreparedQuery();
		childOrderParentIdQuery_parentID = childOrderParentIdQuery.addEq(schema.childOrdersTable_parentId);
		LH.info(log, childOrderParentIdQuery);

		exTableChildOrderQuery = schema.exTable.createAmiPreparedQuery();
		exTableChildOrderQuery_idParam = exTableChildOrderQuery.addEq(schema.exTable_oID);
		LH.info(log, exTableChildOrderQuery);

		ordersByAccountSideQuery = schema.ordersByAccountSideTable.createAmiPreparedQuery();
		ordersByAccountSideQuery_accountParam = ordersByAccountSideQuery.addEq(schema.ordersByAccountSideTable_account);
		LH.info(log, ordersByAccountSideQuery);

		this.childAlertPreparedRow = schema.childAlertsTable.createAmiPreparedRow();
		this.preparedOrderRow = schema.oTable.createAmiPreparedRow();
		this.preparedExecutionRow = schema.exTable.createAmiPreparedRow();
	}
	private boolean checkIfExecutionAddsLiquidity(String execIndicator) {
		return (ADDS_LIQUIDITY.equals(execIndicator));
	}
	private void searchExTableByChildId(String childId) {
		exSearchResults.clear();
		exTableChildOrderQuery_idParam.setValue(childId);
		schema.exTable.query(exTableChildOrderQuery, 10000000, exSearchResults);
	}

	@Override
	public void onInserted(AmiTable table, AmiRow row, AmiImdbSession session, StackFrame sf) {
		long start = System.nanoTime();
		/*------------------------------------
		 *         INSERT ORDER
		 * -----------------------------------*/
		String parentOrderID = row.getString(schema.oTable_orderID);
		String side = row.getString(schema.oTable_side);
		String symbol = row.getString(schema.oTable_sym);
		String parentStatus = row.getString(schema.oTable_status);
		preparedOrderRow.reset();
		service.resolveCurrency(row, preparedOrderRow, schema.oTable_baseCurrency, schema.oTable_baseLimitPx, schema.oTable_limitPx);
		long time = row.getLong(schema.oTable_time);
		long startTime = row.getLong(schema.oTable_startTime);
		long endTime = row.getLong(schema.oTable_endTime);
		if (startTime == AmiTable.NULL_NUMBER) {
			preparedOrderRow.setLong(schema.oTable_startTime, startTime = time);
		}
		AnvilMarketDataSymbol marketDataForSymbol = marketData.getMarketData(symbol);
		if (endTime == AmiTable.NULL_NUMBER) {
			preparedOrderRow.setLong(schema.oTable_endTime, endTime = marketDataForSymbol.getCloseTime().getTimeForToday(startTime));
		}
		long fill = 0;
		double val = 0;
		int exCnt = 0;
		int leaves = 0;
		int processedCnt = 0;
		int processedLatency = 0;
		int pendingCnt = 0;
		int pendingLatency = 0;
		int darkCnt = 0;
		int darkVol = 0;
		double darkVal = 0;
		int liqCnt = 0;
		int liqVol = 0;
		double liqVal = 0;
		int routedOutCnt = 0;
		int routedOutVolume = 0;
		double routedOutValue = 0;
		int childCnt = 0;
		int size = (int) row.getLong(schema.oTable_size);
		String currency = row.getString(schema.oTable_baseCurrency);
		Double limitPx = row.getDouble(schema.oTable_baseLimitPx) * (double) service.getFxRate(currency);

		String account = row.getString(schema.oTable_account);
		String system = row.getString(schema.oTable_system);
		String strategy = row.getString(schema.oTable_strategy);
		String industry = null;
		String sector = null;
		Double adv = null;
		double prevClose = Double.NaN;
		long exTime = -1;

		//SEARCH SEC MASTER
		AmiRow secMasterRow = searchSecMasterForSymbol(symbol);
		if (secMasterRow != null) {
			industry = secMasterRow.getString(schema.secMasterTable_industry);
			sector = secMasterRow.getString(schema.secMasterTable_sector);
			prevClose = secMasterRow.getDouble(schema.secMasterTable_prevClose);
			adv = secMasterRow.getDouble(schema.secMasterTable_adv);
			preparedOrderRow.setString(schema.oTable_industry, industry);
			preparedOrderRow.setString(schema.oTable_sector, sector);
			preparedOrderRow.setDouble(schema.oTable_prevClose, prevClose);
		}
		//SEARCH FOR EXECUTIONS WITH NO CHILD ORDER ID
		searchExTableByParentId(parentOrderID);
		int z = searchResults.size();
		for (int i = 0; i < z; i++) {
			AmiRow exRow = searchResults.get(i);
			if (exRow.getIsNull(schema.exTable_oID)) {
				String venue = exRow.getString(schema.exTable_ex);
				preparedExecutionRow.reset();
				if (exRow.getIsNull(schema.exTable_sym)) {
					preparedExecutionRow.setString(schema.exTable_industry, industry);
					preparedExecutionRow.setString(schema.exTable_sector, sector);
					preparedExecutionRow.setDouble(schema.exTable_prevClose, prevClose);
					preparedExecutionRow.setString(schema.exTable_sym, symbol);
				}
				boolean isLit = service.isVenueLit(venue);
				String execIndicator = exRow.getString(schema.exTable_execIndicator);
				boolean addsLiquidity = checkIfExecutionAddsLiquidity(execIndicator);
				boolean routesOutLiquidity = ROUTED_OUT_LIQUIDITY.equals(execIndicator);
				int execSize = (int) exRow.getLong(schema.exTable_size);
				double execVal = exRow.getDouble(schema.exTable_px) * execSize;
				fill += execSize;
				val += execVal;
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
				if (routesOutLiquidity) {
					routedOutCnt++;
					routedOutVolume += execSize;
					routedOutValue += execVal;
				}
				exTime = exRow.getLong(schema.exTable_time);
				preparedExecutionRow.setString(schema.exTable_parentId, parentOrderID);
				preparedExecutionRow.setString(schema.exTable_side, side);
				preparedExecutionRow.setString(schema.exTable_account, account);
				preparedExecutionRow.setString(schema.exTable_system, system);
				preparedExecutionRow.setString(schema.exTable_strategy, strategy);
				schema.exTable.updateAmiRow(exRow.getAmiId(), preparedExecutionRow, session);
				exCnt++;
				timerQueue.addEvent(exTime, timedEventPool.nw().reset(exRow.getAmiId(), exTime, AnvilTimedEvent.EXECUTION, marketDataForSymbol, AnvilTimedEvent.NONE));
				timerQueue.addEvent(exTime + EX_T1_TIME_EVENT_OFFSET,
						timedEventPool.nw().reset(exRow.getAmiId(), exTime + EX_T1_TIME_EVENT_OFFSET, AnvilTimedEvent.EXECUTION_TIMER, marketDataForSymbol, AnvilTimedEvent.T1));
				timerQueue.addEvent(exTime + EX_T5_TIME_EVENT_OFFSET,
						timedEventPool.nw().reset(exRow.getAmiId(), exTime + EX_T5_TIME_EVENT_OFFSET, AnvilTimedEvent.EXECUTION_TIMER, marketDataForSymbol, AnvilTimedEvent.T5));
			}
		}
		//SEARCH CHILD ORDERS
		searchForChildOrdersByParentId(parentOrderID);
		for (AmiRow childOrder : searchResults) {
			String childOrderId = childOrder.getString(schema.childOrdersTable_clOrderId);
			leaves += childOrder.getLong(schema.childOrdersTable_openQty);
			pendingLatency += childOrder.getLong(schema.childOrdersTable_pendingLatency);
			pendingCnt += childOrder.getLong(schema.childOrdersTable_pendingCnt);
			processedLatency += childOrder.getLong(schema.childOrdersTable_processedLatency);
			processedCnt += childOrder.getLong(schema.childOrdersTable_processedCnt);
			childCnt++;
			//SEARCH EXECUTIONS
			childOrder.setString(schema.childOrdersTable_symbol, symbol, session);
			//				row.setString(schema.childOrderTable_symbol, symbol);
			searchExTableByChildId(childOrderId);
			for (AmiRow exRow : exSearchResults) {
				String status = exRow.getString(schema.exTable_status);
				if ("R".equals(status))
					continue;

				String venue = exRow.getString(schema.exTable_ex);
				preparedExecutionRow.reset();
				if (exRow.getIsNull(schema.exTable_sym)) {
					preparedExecutionRow.setString(schema.exTable_industry, industry);
					preparedExecutionRow.setString(schema.exTable_sector, sector);
					preparedExecutionRow.setDouble(schema.exTable_prevClose, prevClose);
					preparedExecutionRow.setString(schema.exTable_sym, symbol);
				}
				boolean isLit = service.isVenueLit(venue);
				String execIndicator = exRow.getString(schema.exTable_execIndicator);
				boolean addsLiquidity = ADDS_LIQUIDITY.equals(execIndicator);
				boolean routesOutLiquidity = ROUTED_OUT_LIQUIDITY.equals(execIndicator);

				//if (addsLiquidity || !isLit) {
				int execSize = (int) exRow.getLong(schema.exTable_size);
				double execVal = exRow.getDouble(schema.exTable_px) * execSize;
				fill += execSize;
				val += execVal;
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
				if (routesOutLiquidity) {
					routedOutCnt++;
					routedOutVolume += execSize;
					routedOutValue += execVal;
				}
				//}

				exTime = exRow.getLong(schema.exTable_time);
				preparedExecutionRow.setString(schema.exTable_parentId, parentOrderID);
				preparedExecutionRow.setString(schema.exTable_side, side);
				preparedExecutionRow.setString(schema.exTable_account, account);
				preparedExecutionRow.setString(schema.exTable_system, system);
				preparedExecutionRow.setString(schema.exTable_strategy, strategy);
				schema.exTable.updateAmiRow(exRow.getAmiId(), preparedExecutionRow, session);
				exCnt++;
				timerQueue.addEvent(exTime, timedEventPool.nw().reset(exRow.getAmiId(), exTime, AnvilTimedEvent.EXECUTION, marketDataForSymbol, AnvilTimedEvent.NONE));
				timerQueue.addEvent(exTime + EX_T1_TIME_EVENT_OFFSET,
						timedEventPool.nw().reset(exRow.getAmiId(), exTime + EX_T1_TIME_EVENT_OFFSET, AnvilTimedEvent.EXECUTION_TIMER, marketDataForSymbol, AnvilTimedEvent.T1));
				timerQueue.addEvent(exTime + EX_T5_TIME_EVENT_OFFSET,
						timedEventPool.nw().reset(exRow.getAmiId(), exTime + EX_T5_TIME_EVENT_OFFSET, AnvilTimedEvent.EXECUTION_TIMER, marketDataForSymbol, AnvilTimedEvent.T5));
				//Alert
				//		if (fill > size) {
				//			service.addAlert(AnvilServices.ALERT_OVERFILL, parentOrderID, sector, industry, account, symbol, system, exTime, 1, null, null, null, null);
				//		}
			}
		}
		preparedOrderRow.setLong(schema.oTable_filled, fill);
		preparedOrderRow.setDouble(schema.oTable_filledValue, val);
		preparedOrderRow.setDouble(schema.oTable_exCnt, exCnt);
		preparedOrderRow.setLong(schema.oTable_leaves, leaves);
		preparedOrderRow.setLong(schema.oTable_pendingCnt, pendingCnt);
		preparedOrderRow.setLong(schema.oTable_pendingLatency, pendingLatency);
		preparedOrderRow.setLong(schema.oTable_processedCnt, processedCnt);
		preparedOrderRow.setLong(schema.oTable_processedLatency, processedLatency);
		preparedOrderRow.setLong(schema.oTable_darkCnt, darkCnt);
		preparedOrderRow.setLong(schema.oTable_darkVol, darkVol);
		preparedOrderRow.setDouble(schema.oTable_darkVal, darkVal);
		preparedOrderRow.setLong(schema.oTable_liqVol, liqVol);
		preparedOrderRow.setLong(schema.oTable_liqCnt, liqCnt);
		preparedOrderRow.setDouble(schema.oTable_liqVal, liqVal);
		preparedOrderRow.setLong(schema.oTable_childCnt, childCnt);
		preparedOrderRow.setLong(schema.oTable_routedCnt, routedOutCnt);
		preparedOrderRow.setDouble(schema.oTable_routedVal, routedOutValue);
		preparedOrderRow.setLong(schema.oTable_routedVol, routedOutVolume);
		schema.oTable.updateAmiRow(row.getAmiId(), preparedOrderRow, session);
		updateOrderBenchmarks(row, session);
		AnvilTriggerTimer.setStatus(schema.oTable_isOpen, row, AnvilTriggerTimer.STATUS_PREOPEN, session);
		timerQueue.addEvent(startTime, timedEventPool.nw().reset(row.getAmiId(), startTime, AnvilTimedEvent.ORDER_ARRIVAL, marketDataForSymbol, AnvilTimedEvent.NONE));
		timerQueue.addEvent(endTime, timedEventPool.nw().reset(row.getAmiId(), endTime, AnvilTimedEvent.ORDER_DEPARTURE, marketDataForSymbol, AnvilTimedEvent.NONE));
		long end = System.nanoTime();
		this.service.incrementStatsForOrderProcessed(1, start, end, session);
		service.resetTimers();

		if ("N".equals(parentStatus) || "U".equals(parentStatus)) {
			//ALERT ADV
			if (adv != null && size >= (adv * AnvilServices.THRESHOLD_ADV)) {
				Map<String, Object> mapParams = new HashMap<String, Object>();
				mapParams.put("SIZE", size);
				mapParams.put("ADV", adv);
				service.addAlert(AnvilServices.ALERT_ADV, parentOrderID, sector, industry, account, symbol, system, time, 1, null, symbol, null, null, mapParams, session);
			}

			//ALERT NOTIONAL PER CLIENT
			searchOrdersByAccountSideTableByAccount(account);
			double totalNotionalValueForClient = 0;
			for (AmiRow accRow : searchResults) {
				totalNotionalValueForClient += accRow.getDouble(schema.ordersByAccountSideTable_filledValue);
				totalNotionalValueForClient += accRow.getDouble(schema.ordersByAccountSideTable_openValue);
			}
			if (totalNotionalValueForClient >= AnvilServices.THRESHOLD_NOTIONAL_PER_CLIENT) {
				Map<String, Object> mapParams = new HashMap<String, Object>();
				mapParams.put("NOTIONAL", totalNotionalValueForClient);
				service.addAlert(AnvilServices.ALERT_NOTIONAL_PER_CLIENT, parentOrderID, sector, industry, account, symbol, system, time, 1, null, symbol, null, null, mapParams,
						session);
			}

			//ALERT FARSIDE
			int nbboPos = marketDataForSymbol.getNbboPositionLe(time);
			if (nbboPos != -1) {
				double farside;
				Map<String, Object> mapParams = new HashMap<String, Object>();
				mapParams.put("PRICE", limitPx);
				if ("B".equals(side)) {
					farside = marketDataForSymbol.getNbboAskPxAtPos(nbboPos);
					if (limitPx >= farside * (1 + AnvilServices.THRESHOLD_FARSIDE)) {
						mapParams.put("SIDE", side);
						mapParams.put("FARSIDE", farside);
						service.addAlert(AnvilServices.ALERT_NOTIONAL_FARSIDE, parentOrderID, sector, industry, account, symbol, system, time, 1, null, symbol, null, null,
								mapParams, session);
					}
				} else {
					farside = marketDataForSymbol.getNbboBidPxAtPos(nbboPos);
					if (limitPx <= farside * (1 - AnvilServices.THRESHOLD_FARSIDE)) {
						mapParams.put("SIDE", side);
						mapParams.put("FARSIDE", farside);
						service.addAlert(AnvilServices.ALERT_NOTIONAL_FARSIDE, parentOrderID, sector, industry, account, symbol, system, time, 1, null, symbol, null, null,
								mapParams, session);
					}
				}
				// ALERT LAST TRADED
				double lastTraded = marketDataForSymbol.getCurrentTradePx();
				if (Math.abs(limitPx - lastTraded) >= lastTraded * AnvilServices.THRESHOLD_LAST_TRADED) {
					mapParams.put("LAST_TRADED", lastTraded);
					service.addAlert(AnvilServices.ALERT_LAST_TRADED, parentOrderID, sector, industry, account, symbol, system, time, 1, null, symbol, null, null, mapParams,
							session);
				}
			}
		}

	}
	private void searchOrdersByAccountSideTableByAccount(String account) {
		searchResults.clear();
		ordersByAccountSideQuery_accountParam.setValue(account);
		schema.ordersByAccountSideTable.query(ordersByAccountSideQuery, 10000000, searchResults);
	}
	private void searchExTableByParentId(String parentOrderID) {
		searchResults.clear();
		exTableQuery_parentIdParam.setValue(parentOrderID);
		schema.exTable.query(exTableQuery, 10000000, searchResults);
	}
	private void searchForChildOrdersByParentId(String parentId) {
		searchResults.clear();
		childOrderParentIdQuery_parentID.setValue(parentId);
		schema.childOrdersTable.query(childOrderParentIdQuery, 10000000, searchResults);
	}

	@Override
	public boolean onInserting(AmiTable table, AmiRow row, AmiImdbSession session, StackFrame sf) {
		String status = row.getString(schema.oTable_status);
		String symbol = row.getString(schema.oTable_sym);
		AnvilMarketDataSymbol marketDataForSymbol = marketData.getMarketData(symbol);
		if (status.length() != 1) {
			LH.info(log, "INVALID STATUS: " + status + " VALID STATUSES: { (N)ew, (C)ancel, (U)pdate } ON ORDER ==> " + row + "");
			return false;
		}
		char statC = status.charAt(0);
		orderTableIndex.setValue(row.getString(schema.oTable_orderID));
		AmiRow orderRow = schema.oTable.query(orderTableQuery);
		if (orderRow == null) {
			if (statC == ORDER_STATUS_UPDATE) {
				statC = ORDER_STATUS_NEW;
				LH.info(log, "INVALID UPDATE... there is no order with ID ==> " + row.getString(schema.oTable_orderID));
			} else if (statC == ORDER_STATUS_CANCEL) {
				LH.info(log, "INVALID CANCEL... there is no order with ID ==> " + row.getString(schema.oTable_orderID));
				return false;
			}
		} else {
			if (statC == ORDER_STATUS_NEW)
				statC = ORDER_STATUS_UPDATE;
		}
		switch (statC) {
			case (ORDER_STATUS_UPDATE): {

				preparedOrderRow.reset();

				long startTime = row.getLong(schema.oTable_startTime);
				if (startTime != AmiTable.NULL_NUMBER) {
					long origStartTime = orderRow.getLong(schema.oTable_startTime);
					if (startTime != origStartTime) {
						preparedOrderRow.setLong(schema.oTable_startTime, startTime);
						timerQueue.addEvent(startTime,
								timedEventPool.nw().reset(orderRow.getAmiId(), startTime, AnvilTimedEvent.ORDER_ARRIVAL, marketDataForSymbol, AnvilTimedEvent.NONE));
					}
				}

				long endTime = row.getLong(schema.oTable_endTime);
				if (endTime != AmiTable.NULL_NUMBER) {
					long origEndTime = orderRow.getLong(schema.oTable_endTime);
					if (endTime != origEndTime) {
						preparedOrderRow.setLong(schema.oTable_endTime, endTime);
						timerQueue.addEvent(endTime,
								timedEventPool.nw().reset(orderRow.getAmiId(), endTime, AnvilTimedEvent.ORDER_DEPARTURE, marketDataForSymbol, AnvilTimedEvent.NONE));
					}
				}

				preparedOrderRow.setDouble(schema.oTable_baseLimitPx, row.getDouble(schema.oTable_baseLimitPx));
				service.resolveCurrency(row, preparedOrderRow, schema.oTable_baseCurrency, schema.oTable_baseLimitPx, schema.oTable_limitPx);
				preparedOrderRow.setLong(schema.oTable_size, row.getLong(schema.oTable_size));

				schema.oTable.updateAmiRow(orderRow.getAmiId(), preparedOrderRow, session);

				return false;
			}
			case (ORDER_STATUS_CANCEL): {
				preparedOrderRow.reset();
				preparedOrderRow.setLong(schema.oTable_isOpen, AnvilTriggerTimer.STATUS_PRECANCELD);
				long canelTime = row.getLong(schema.oTable_endTime);
				preparedOrderRow.setLong(schema.oTable_endTime, canelTime);

				schema.oTable.updateAmiRow(orderRow.getAmiId(), preparedOrderRow, session);
				timerQueue.addEvent(canelTime,
						timedEventPool.nw().reset(orderRow.getAmiId(), canelTime, AnvilTimedEvent.ORDER_DEPARTURE, marketDataForSymbol, AnvilTimedEvent.NONE));
				return false;
			}
			case (ORDER_STATUS_NEW):
				return true;
			default: {
				LH.info(log, "INVALID STATUS: " + status + " VALID STATUSES: { (N)ew, (C)ancel, (U)pdate } ON ORDER ==> " + row + "");
				return false;
			}
		}

	}
	private AmiRow searchSecMasterForSymbol(String sym) {
		secMasterQuery_symParam.setValue(sym);
		return schema.secMasterTable.query(secMasterQuery);
	}

	private void updateOrderBenchmarks(AmiRow orderRow, AmiImdbSession session) {
		searchResults.clear();
		String symbol = orderRow.getString(schema.oTable_sym);
		symIndex.setValue(symbol);
		AmiRow symRow = schema.symTable.query(symQuery);
		preparedOrderRow.reset();
		if (symRow != null) {
			double open = symRow.getDouble(schema.symTable_open);
			double low = symRow.getDouble(schema.symTable_low);
			double high = symRow.getDouble(schema.symTable_high);
			if (open == open) {
				preparedOrderRow.setDouble(schema.oTable_open, symRow.getDouble(schema.symTable_open));
				preparedOrderRow.setDouble(schema.oTable_lho, (open + low + high) / 3);
			}
			preparedOrderRow.setLong(schema.oTable_dailyVolume, symRow.getLong(schema.symTable_size));
			preparedOrderRow.setDouble(schema.oTable_dailyValue, symRow.getDouble(schema.symTable_value));
			preparedOrderRow.setDouble(schema.oTable_low, low);
			preparedOrderRow.setDouble(schema.oTable_high, high);
			preparedOrderRow.setDouble(schema.oTable_ask, symRow.getDouble(schema.symTable_ask));
			preparedOrderRow.setDouble(schema.oTable_bid, symRow.getDouble(schema.symTable_bid));
		} else {
			preparedOrderRow.setLong(schema.oTable_dailyVolume, 0);
			preparedOrderRow.setDouble(schema.oTable_dailyValue, 0);
		}
		if (!orderRow.getIsNull(schema.oTable_startMidpointHL)) {
			double startingMidpointHL = orderRow.getDouble(schema.oTable_startMidpointHL);
			long status = AnvilTriggerTimer.getStatus(schema.oTable_isOpen, orderRow);
			long filled = orderRow.getLong(schema.oTable_filled);
			if (status == AnvilTriggerTimer.STATUS_OPEN) {
				long remaining = orderRow.getLong(schema.oTable_size) - filled;
				double midpointHL = (orderRow.getDouble(schema.oTable_ask) + orderRow.getDouble(schema.oTable_bid)) / 2;
				preparedOrderRow.setDouble(schema.oTable_mktImpact, orderRow.getDouble(schema.oTable_filledValue) - filled * startingMidpointHL);
				preparedOrderRow.setDouble(schema.oTable_opportunityCost, remaining * (midpointHL - startingMidpointHL));
			} else if (status == AnvilTriggerTimer.STATUS_CLOSED) {
				preparedOrderRow.setDouble(schema.oTable_mktImpact, orderRow.getDouble(schema.oTable_filledValue) - filled * startingMidpointHL);
				preparedOrderRow.setDouble(schema.oTable_opportunityCost, 0d);
			} else
				LH.info(log, "BAD ORDER STATUS: ", orderRow);
		}
		schema.oTable.updateAmiRow(orderRow.getAmiId(), preparedOrderRow, session);
	}
}
