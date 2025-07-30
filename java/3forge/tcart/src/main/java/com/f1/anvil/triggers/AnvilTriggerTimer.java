package com.f1.anvil.triggers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.ami.center.table.AmiCenterProcess;
import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiPreparedQuery;
import com.f1.ami.center.table.AmiPreparedQueryCompareClause;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.timers.AmiAbstractTimer;
import com.f1.ami.center.timers.AmiTimerBinding;
import com.f1.anvil.utils.AnvilBufferedEvent;
import com.f1.anvil.utils.AnvilMarketDataMap;
import com.f1.anvil.utils.AnvilMarketDataSymbol;
import com.f1.anvil.utils.AnvilTaqPeriodQueuesManager;
import com.f1.anvil.utils.AnvilTimedEvent;
import com.f1.container.ContainerTools;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.Scheduler;
import com.f1.utils.concurrent.ObjectPoolForClearable;

public class AnvilTriggerTimer extends AmiAbstractTimer {
	private static final Logger log = LH.get(AnvilTriggerTimer.class);

	public static final double PRECISION_DELTA = .00001;
	public static final int STATUS_PREOPEN = 0;
	public static final int STATUS_OPEN = 1;
	public static final int STATUS_CLOSED = 2;
	public static final int STATUS_PRECANCELD = 3;
	public static final int STATUS_CANCELED = 4;
	public static final int NBBO_REG_TIME = 1000;

	private AnvilServices service;
	private AnvilSchema schema;
	private Scheduler<AnvilTimedEvent> timerQueue;
	private ObjectPoolForClearable<AnvilTimedEvent> timedEventPool;
	private int updatePeriod;
	private long maxQueueSize;

	private AmiPreparedRow symTablePreparedRow;
	private AmiPreparedRow venueTable_preparedRow;
	private ArrayList<AmiRow> searchResultRows;
	private ArrayList<AmiRow> searchOrdersResultRows;
	private boolean timerSet;

	private AmiPreparedQuery symTable_query;
	private AmiPreparedQueryCompareClause symTableIndex;

	private AmiPreparedQuery orderTableQuery;
	private AmiPreparedQueryCompareClause orderTableIndex;

	private AmiPreparedQuery secMasterQuery;
	private AmiPreparedQueryCompareClause secMasterQuery_symParam;

	private AmiPreparedQuery openOrderQuery;
	private AmiPreparedQueryCompareClause openOrderQuery_isOpen;
	private AmiPreparedQueryCompareClause openOrderQuery_sym;

	private AmiPreparedQuery venuePreparedQuery;
	private AmiPreparedQueryCompareClause venueSymbolIndex;
	private AmiPreparedQueryCompareClause venueSideIndex;

	private AmiPreparedQuery orderTableIDQuery;
	private AmiPreparedQueryCompareClause orderTableID;

	private AmiPreparedQuery ordersByAccountSideQuery;
	private AmiPreparedQueryCompareClause ordersByAccountSideQuery_accountParam;

	private AnvilTaqPeriodQueuesManager taqQueueManager;

	private AmiPreparedRow childAlertPreparedRow;
	private AmiPreparedRow preparedOrderRow;
	private AmiPreparedRow preparedExecutionRow;

	private double t5AlertCutoff = 1100;
	private double t1AlertCutoff = 1000;

	@Override
	public void startup(AmiImdb imdb, AmiTimerBinding binding, AmiImdbSession session) {
		ContainerTools props = imdb.getTools();
		updatePeriod = props.getRequired("period.ms", Integer.class);
		maxQueueSize = props.getRequired("max.queue.size", Integer.class);
		t1AlertCutoff = props.getOptional("alert.cutoff.t1.bps", t1AlertCutoff);
		t5AlertCutoff = props.getOptional("alert.cutoff.t5.bps", t5AlertCutoff);
		this.timerSet = false;
		this.service = imdb.getAmiServiceOrThrow(AnvilServices.SERVICE_NAME, AnvilServices.class);
		this.schema = this.service.getSchema();
		this.taqQueueManager = this.service.getTaqQueueManager();
		this.timedEventPool = this.service.getTimedEventPool();
		this.timerQueue = service.getTimerQueue();
		initSchema();
		super.startup(imdb, binding, session);
	}
	private void initSchema() {
		this.searchResultRows = new ArrayList<AmiRow>();
		this.searchOrdersResultRows = new ArrayList<AmiRow>();

		this.symTablePreparedRow = schema.symTable.createAmiPreparedRow();
		this.venueTable_preparedRow = schema.venueTable.createAmiPreparedRow();

		this.symTable_query = schema.symTable.createAmiPreparedQuery();
		this.symTableIndex = symTable_query.addCompare(schema.symTable_sym, AmiPreparedQueryCompareClause.EQ);
		LH.info(log, "INDEX: ", symTable_query);

		this.orderTableIDQuery = schema.oTable.createAmiPreparedQuery();
		this.orderTableID = orderTableIDQuery.addCompare(schema.oTable_orderID, AmiPreparedQueryCompareClause.EQ);
		LH.info(log, "INDEX: ", orderTableIDQuery);

		this.orderTableQuery = schema.oTable.createAmiPreparedQuery();
		this.orderTableIndex = orderTableQuery.addCompare(schema.oTable_sym, AmiPreparedQueryCompareClause.EQ);
		LH.info(log, "INDEX: ", orderTableQuery);

		this.openOrderQuery = schema.oTable.createAmiPreparedQuery();
		this.openOrderQuery_isOpen = openOrderQuery.addEq(schema.oTable_isOpen);
		openOrderQuery_isOpen.setValue((long) STATUS_OPEN);
		this.openOrderQuery_sym = openOrderQuery.addEq(schema.oTable_sym);
		LH.info(log, "INDEX: ", orderTableQuery);

		this.venuePreparedQuery = schema.venueTable.createAmiPreparedQuery();
		venueSymbolIndex = venuePreparedQuery.addEq(schema.venueTable_venue);
		venueSideIndex = venuePreparedQuery.addEq(schema.venueTable_side);
		LH.info(log, "INDEX: ", venuePreparedQuery);

		secMasterQuery = schema.secMasterTable.createAmiPreparedQuery();
		secMasterQuery_symParam = secMasterQuery.addEq(schema.secMasterTable_sym);
		LH.info(log, "INDEX: ", secMasterQuery);

		ordersByAccountSideQuery = schema.ordersByAccountSideTable.createAmiPreparedQuery();
		ordersByAccountSideQuery_accountParam = ordersByAccountSideQuery.addEq(schema.ordersByAccountSideTable_account);
		LH.info(log, ordersByAccountSideQuery);

		this.childAlertPreparedRow = schema.childAlertsTable.createAmiPreparedRow();
		this.preparedOrderRow = schema.oTable.createAmiPreparedRow();
		this.preparedExecutionRow = schema.exTable.createAmiPreparedRow();
	}
	public void resetTimers() {
		if (timerSet) {
			return;
		}
		timerSet = true;
		getImdb().registerTimerFromNow(this.getBinding().getTimerName(), updatePeriod, TimeUnit.MILLISECONDS);
	}
	@Override
	public boolean onTimer(long timerId, AmiImdbSession session, AmiCenterProcess process) {
		session.lock(process, null);
		try {
			timerSet = false;
			try {
				long t1 = System.nanoTime();
				int processedCount = processTaqsOnTimer(timerId, session);
				long t2 = System.nanoTime();
				this.service.incrementStatsForTaqsProcessed(processedCount, t1, t2, session);
			} catch (Exception e) {
				LH.warning(log, "Critical problem: ", e);
			}
			try {
				long t1 = System.nanoTime();
				int processedCount = processTimedEventsOnTimer(timerId, session);
				long t2 = System.nanoTime();
				this.service.incrementStatsForTimersProcessed(processedCount, t1, t2, session);
			} catch (Exception e) {
				LH.warning(log, "Critical problem: ", e);
			}
			if (this.taqQueueManager.hasNextPeriod() && !this.taqQueueManager.hasPendingSymbols()) {
				long period = this.taqQueueManager.moveToNextPeriod();
				long t1 = System.nanoTime();
				int copyCount = this.service.copyTablesToHistory(period, session);
				long t2 = System.nanoTime();
				this.service.incrementStatsForHistoryProcessed(copyCount, t1, t2, session);
			}
			if (this.taqQueueManager.hasPendingSymbols() || this.taqQueueManager.hasNextPeriod())
				resetTimers();
		} finally {
			session.unlock();
		}
		return true;
	}
	public int processTaqsOnTimer(long timerId, AmiImdbSession session) {
		int timerCnt = 0;
		int symbolCount = 0;
		for (;;) {
			if (!this.taqQueueManager.hasPendingSymbols() || this.service.getQueueSize() > maxQueueSize || this.service.getHighPriorityQueueSize() > 0)
				break;
			this.service.captureStatsIfChanged(System.nanoTime(), session);
			AnvilBufferedEvent be = this.taqQueueManager.popNextPendingBufferedEvent();
			String symbol = be.symbol;
			long updateTime = be.time;
			symbolCount++;
			boolean timerSymbolContainsTrade = false;
			boolean timerSymbolContainsNbbo = false;
			if (be.open == be.open) {
				double open = be.open;
				double high = be.high;
				double low = be.low;
				searchOrderTableForMatchingSymbol(symbol);
				int z = searchResultRows.size();
				for (int i = 0; i < z; i++) {
					AmiRow orderRow = searchResultRows.get(i);
					preparedOrderRow.reset();
					//					schema.oTable.fireTriggerUpdating(orderRow);
					preparedOrderRow.setDouble(schema.oTable_open, open);
					preparedOrderRow.setDouble(schema.oTable_lho, (open + high + low) / 3);
					schema.oTable.updateAmiRow(orderRow.getAmiId(), preparedOrderRow, session);
					//					schema.oTable.fireTriggerUpdated(orderRow);
				}
			}
			searchSymTableForMatchingSymbol(symbol);
			final double bid, ask, open, value, high, low, px;
			final long volume;
			/* ------------------------------------
			 * ------------------------------------
			 * CREATING A NEW SYMBOL IN THE SYMBOL TABLE
			 * ------------------------------------
			 * ------------------------------------
			 */
			if (searchResultRows.size() == 0) {
				symTablePreparedRow.reset();
				symTablePreparedRow.setString(schema.symTable_sym, symbol);
				if (be.nbboEvent != null) {
					timerSymbolContainsNbbo = true;
					final AmiRow beNbboRow = be.nbboEvent.remove();
					symTablePreparedRow.setDouble(schema.symTable_bid, bid = beNbboRow.getDouble(schema.nbboTable_bid));
					symTablePreparedRow.setDouble(schema.symTable_ask, ask = beNbboRow.getDouble(schema.nbboTable_ask));
				} else
					bid = ask = Double.NaN;
				if (be.tradeEvent != null) {
					timerSymbolContainsTrade = true;
					be.tradeEvent.remove();
					if (be.open == be.open) {
						symTablePreparedRow.setDouble(schema.symTable_open, open = be.open);
					} else {
						open = Double.NaN;
					}
					symTablePreparedRow.setDouble(schema.symTable_low, low = be.low);
					symTablePreparedRow.setDouble(schema.symTable_high, high = be.high);
					symTablePreparedRow.setDouble(schema.symTable_last, px = be.px);
					symTablePreparedRow.setLong(schema.symTable_size, volume = be.volume);
					symTablePreparedRow.setDouble(schema.symTable_value, value = be.value);
					symTablePreparedRow.setLong(schema.symTable_updateTime, updateTime);
				} else {
					symTablePreparedRow.setLong(schema.symTable_size, 0);
					symTablePreparedRow.setDouble(schema.symTable_value, 0);
					volume = 0;
					value = 0d;
					high = low = open = px = Double.NaN;
				}
				schema.symTable.insertAmiRow(symTablePreparedRow, session);
			} else {
				/* ------------------------------------
				 * ------------------------------------
				 * UPDATING SYMBOL IN THE SYMBOL TABLE
				 * ------------------------------------
				 * ------------------------------------
				 */

				symTablePreparedRow.reset();
				final AmiRow row = searchResultRows.get(0);
				if (be.nbboEvent != null) {
					timerSymbolContainsNbbo = true;
					final AmiRow beNbboRow = be.nbboEvent.remove();
					timerSymbolContainsNbbo = true;
					symTablePreparedRow.setDouble(schema.symTable_bid, bid = beNbboRow.getDouble(schema.nbboTable_bid));
					symTablePreparedRow.setDouble(schema.symTable_ask, ask = beNbboRow.getDouble(schema.nbboTable_ask));
				} else
					bid = ask = Double.NaN;
				if (be.tradeEvent != null) {
					timerSymbolContainsTrade = true;
					be.tradeEvent.remove();
					symTablePreparedRow.setDouble(schema.symTable_low, low = be.low);
					symTablePreparedRow.setDouble(schema.symTable_high, high = be.high);
					symTablePreparedRow.setDouble(schema.symTable_last, px = be.px);
					if (be.open == be.open)
						symTablePreparedRow.setDouble(schema.symTable_open, open = be.open);
					else
						open = row.getDouble(schema.symTable_open);
					symTablePreparedRow.setLong(schema.symTable_size, volume = be.volume);
					symTablePreparedRow.setDouble(schema.symTable_value, value = be.value);
				} else {
					open = row.getDouble(schema.symTable_open);
					high = low = px = Double.NaN;
					volume = be.volume;
					value = be.value;
				}
				symTablePreparedRow.setLong(schema.symTable_updateTime, updateTime);
				schema.symTable.updateAmiRow(row.getAmiId(), symTablePreparedRow, session);
			}
			//			LH.info(log, "Clearing", be.symbol);
			be.clear();
			searchForOpenOrdersBySymbol(symbol);

			/* ------------------------------------
			 * ------------------------------------
			 * UPDATING OPEN ORDERS WITH SAME SYMBOL
			 * ------------------------------------
			 * ------------------------------------
			 */

			final boolean hasOpen = open == open;
			final double lho = (low + high + open) / 3;
			final double vwap = value / volume;
			double currentMidpoint = (ask + bid) / 2;
			for (int i = 0, s = searchResultRows.size(); i < s; i++) {
				final AmiRow orderRow = searchResultRows.get(i);
				if (getStatus(schema.oTable_isOpen, orderRow) != STATUS_OPEN)
					LH.info(log, "ANVIL DEBUG: SEARCH RETURNED CLOSED ORDER ==> " + orderRow);
				//				schema.oTable.fireTriggerUpdating(orderRow);
				preparedOrderRow.reset();
				if (timerSymbolContainsNbbo) {
					preparedOrderRow.setDouble(schema.oTable_bid, bid);
					preparedOrderRow.setDouble(schema.oTable_ask, ask);
				}
				if (timerSymbolContainsTrade) {
					double startVal = orderRow.getDouble(schema.oTable_startVal);
					if (hasOpen) {
						preparedOrderRow.setDouble(schema.oTable_open, open);
						preparedOrderRow.setDouble(schema.oTable_lho, lho);
					}
					preparedOrderRow.setLong(schema.oTable_dailyVolume, volume);
					preparedOrderRow.setDouble(schema.oTable_dailyValue, value);
					preparedOrderRow.setDouble(schema.oTable_dailyVWAP, vwap);
					preparedOrderRow.setDouble(schema.oTable_high, high);
					preparedOrderRow.setDouble(schema.oTable_low, low);
					preparedOrderRow.setDouble(schema.oTable_last, px);
					preparedOrderRow.setDouble(schema.oTable_intervalVWAP, (value - startVal) / (volume - orderRow.getLong(schema.oTable_startVol)));
					//preparedOrderRow.setLong(schema.oTable_time, updateTime);
				}
				if (!orderRow.getIsNull(schema.oTable_startMidpointHL)) {
					double startMidpoint = orderRow.getDouble(schema.oTable_startMidpointHL);
					long filled = orderRow.getLong(schema.oTable_filled);
					if (timerSymbolContainsNbbo)
						preparedOrderRow.setDouble(schema.oTable_opportunityCost, (orderRow.getLong(schema.oTable_size) - filled) * (currentMidpoint - startMidpoint));
					if (timerSymbolContainsTrade)
						preparedOrderRow.setDouble(schema.oTable_mktImpact, orderRow.getDouble(schema.oTable_filledValue) - filled * startMidpoint);
				}

				timerCnt++;
				schema.oTable.updateAmiRow(orderRow.getAmiId(), preparedOrderRow, session);
				//				schema.oTable.fireTriggerUpdated(orderRow);

				String parentStatus = orderRow.getString(schema.oTable_status);

				if ("N".equals(parentStatus) || "U".equals(parentStatus)) {
					//ALERT EX ADV
					AmiRow secMasterRow = searchSecMasterForSymbol(symbol);
					Double adv = secMasterRow.getDouble(schema.secMasterTable_adv);

					String industry = orderRow.getString(schema.oTable_industry);
					String sector = orderRow.getString(schema.oTable_sector);
					String parentOrderID = orderRow.getString(schema.oTable_orderID);
					String account = orderRow.getString(schema.oTable_account);
					String system = orderRow.getString(schema.oTable_system);
					String side = orderRow.getString(schema.oTable_side);
					int size = (int) orderRow.getLong(schema.oTable_size);
					long time = orderRow.getLong(schema.oTable_time);
					Double limitPx = orderRow.getDouble(schema.oTable_limitPx);
					AnvilMarketDataMap marketData = service.getMarketData();
					AnvilMarketDataSymbol marketDataForSymbol = marketData.getMarketData(symbol);

					if (adv != null && size >= (adv * AnvilServices.THRESHOLD_ADV)) {
						Map<String, Object> mapParams = new HashMap<String, Object>();
						mapParams.put("SIZE", size);
						mapParams.put("ADV", adv);
						service.addAlert(AnvilServices.ALERT_EX_ADV, parentOrderID, sector, industry, account, symbol, system, time, 1, null, symbol, null, null, mapParams,
								session);
					}

					//ALERT EX NOTIONAL PER CLIENT
					searchOrdersByAccountSideTableByAccount(account);
					double totalNotionalValueForClient = 0;
					for (AmiRow accRow : searchOrdersResultRows) {
						totalNotionalValueForClient += accRow.getDouble(schema.ordersByAccountSideTable_filledValue);
						totalNotionalValueForClient += accRow.getDouble(schema.ordersByAccountSideTable_openValue);
					}
					if (totalNotionalValueForClient >= AnvilServices.THRESHOLD_NOTIONAL_PER_CLIENT) {
						Map<String, Object> mapParams = new HashMap<String, Object>();
						mapParams.put("NOTIONAL", totalNotionalValueForClient);
						service.addAlert(AnvilServices.ALERT_EX_NOTIONAL_PER_CLIENT, parentOrderID, sector, industry, account, symbol, system, time, 1, null, symbol, null, null,
								mapParams, session);
					}

					//ALERT EX FARSIDE
					int nbboPos = marketDataForSymbol.getNbboPositionLe(time);
					Map<String, Object> mapParams = new HashMap<String, Object>();
					mapParams.put("PRICE", limitPx);
					if (nbboPos != -1) {
						double farside;
						if ("B".equals(side)) {
							farside = marketDataForSymbol.getNbboAskPxAtPos(nbboPos);
							if (limitPx >= farside * (1 + AnvilServices.THRESHOLD_FARSIDE)) {
								mapParams.put("SIDE", side);
								mapParams.put("FARSIDE", farside);
								service.addAlert(AnvilServices.ALERT_EX_NOTIONAL_FARSIDE, parentOrderID, sector, industry, account, symbol, system, time, 1, null, symbol, null,
										null, mapParams, session);
							}
						} else {
							farside = marketDataForSymbol.getNbboBidPxAtPos(nbboPos);
							if (limitPx <= farside * (1 - AnvilServices.THRESHOLD_FARSIDE)) {
								mapParams.put("SIDE", side);
								mapParams.put("FARSIDE", farside);
								service.addAlert(AnvilServices.ALERT_EX_NOTIONAL_FARSIDE, parentOrderID, sector, industry, account, symbol, system, time, 1, null, symbol, null,
										null, mapParams, session);
							}
						}
						// ALERT EX LAST TRADED
						double lastTraded = marketDataForSymbol.getCurrentTradePx();
						if (Math.abs(limitPx - lastTraded) >= lastTraded * AnvilServices.THRESHOLD_LAST_TRADED) {
							mapParams.put("LAST_TRADED", lastTraded);
							service.addAlert(AnvilServices.ALERT_EX_LAST_TRADED, parentOrderID, sector, industry, account, symbol, system, time, 1, null, symbol, null, null,
									mapParams, session);
						}
					}
				}
			}
		}
		this.service.incrementStatsForOrdersUpdated(timerCnt, session);
		return symbolCount;
	}
	public int processTimedEventsOnTimer(long timerId, AmiImdbSession session) {
		/* ------------------------------------
		 * ------------------------------------
		 * HANDLE TIMED EVENTS
		 * ------------------------------------
		 * ------------------------------------
		 */

		final long horizonTime = this.taqQueueManager.getOldestTaqTime();// Math.min(buf.getOldestTradeTime(), buf.getOldestNbboTime());

		int r = 0;
		for (;;) {
			AnvilTimedEvent timedEvent = timerQueue.next(horizonTime);
			if (timedEvent == null)
				break;
			this.service.captureStatsIfChanged(System.nanoTime(), session);
			r++;
			byte eventType = timedEvent.getType();
			switch (eventType) {
				/* ------------------------------------
				 * ------------------------------------
				 * Order Arrival
				 * ------------------------------------
				 * ------------------------------------
				 */
				case (AnvilTimedEvent.ORDER_ARRIVAL): {
					final AmiRow orderRow = schema.oTable.getAmiRowByAmiId(timedEvent.getAmiID());
					final long time = timedEvent.getTime();
					if (time != orderRow.getLong(schema.oTable_startTime))
						break;
					if (getStatus(schema.oTable_isOpen, orderRow) != STATUS_PREOPEN)
						LH.info(log, "ANVIL DEBUG: OPENING AN ALREADY OPENED ORDER ==> " + orderRow);
					//					schema.oTable.fireTriggerUpdating(orderRow);
					preparedOrderRow.reset();
					AnvilMarketDataSymbol marketData = timedEvent.getMarketData();
					int nbboPosition = marketData.getNbboPositionLe(time);
					long endtime = orderRow.getLong(schema.oTable_endTime);
					int tradePosition = marketData.getTradePositionLe(time);
					int tradeEndPosition = marketData.getTradePositionLe(endtime);
					double ask = 0;
					double bid = 0;
					double startMidpointHL;
					if (nbboPosition == -1) {
						startMidpointHL = Double.NaN;
					} else {
						ask = marketData.getNbboAskPxAtPos(nbboPosition);
						bid = marketData.getNbboBidPxAtPos(nbboPosition);
						startMidpointHL = (ask + bid) / 2;
						preparedOrderRow.setDouble(schema.oTable_startMidpointHL, startMidpointHL);
					}
					final double startValue;
					final long startVolume;
					if (tradePosition != -1) {
						startValue = marketData.getTradeAggVal(tradePosition);
						startVolume = marketData.getTradeAggVol(tradePosition);
					} else {
						startValue = 0d;
						startVolume = 0L;
					}
					preparedOrderRow.setDouble(schema.oTable_startVal, startValue);
					preparedOrderRow.setLong(schema.oTable_startVol, startVolume);
					if (tradeEndPosition != -1) {
						double high = marketData.getTradeAggHigh(tradeEndPosition);
						double low = marketData.getTradeAggLow(tradeEndPosition);
						double px = marketData.getTradePxAtPos(tradeEndPosition);
						preparedOrderRow.setDouble(schema.oTable_last, px);
						preparedOrderRow.setDouble(schema.oTable_lho, (low + high + orderRow.getDouble(schema.oTable_open)) / 3);
						long tradeAggVol = marketData.getTradeAggVol(tradeEndPosition);
						double tradeAggVal = marketData.getTradeAggVal(tradeEndPosition);
						preparedOrderRow.setLong(schema.oTable_dailyVolume, tradeAggVol);
						preparedOrderRow.setDouble(schema.oTable_dailyValue, tradeAggVal);
						preparedOrderRow.setDouble(schema.oTable_dailyVWAP, tradeAggVal / tradeAggVol);
						preparedOrderRow.setDouble(schema.oTable_intervalVWAP, (tradeAggVal - startValue) / (tradeAggVol - startVolume));
					}
					long filled = orderRow.getLong(schema.oTable_filled);
					preparedOrderRow.setDouble(schema.oTable_mktImpact, orderRow.getDouble(schema.oTable_filledValue) - filled * startMidpointHL);
					long remaining = orderRow.getLong(schema.oTable_size) - filled;
					double midpointDiff = (orderRow.getDouble(schema.oTable_ask) + orderRow.getDouble(schema.oTable_bid)) / 2 - startMidpointHL;
					double opCost = (remaining) * (midpointDiff);
					preparedOrderRow.setDouble(schema.oTable_opportunityCost, opCost);
					setStatus(schema.oTable_isOpen, orderRow, STATUS_OPEN, session);
					this.service.incrementStatsForOrderOpened(1);
					schema.oTable.updateAmiRow(orderRow.getAmiId(), preparedOrderRow, session);
					//					schema.oTable.fireTriggerUpdated(orderRow);
				}
					break;
				/* ------------------------------------
				 * ------------------------------------
				 * Order Departure
				 * ------------------------------------
				 * ------------------------------------
				 */
				case (AnvilTimedEvent.ORDER_DEPARTURE): {
					final AmiRow orderRow = schema.oTable.getAmiRowByAmiId(timedEvent.getAmiID());
					final long status = getStatus(schema.oTable_isOpen, orderRow);
					if (status == STATUS_CANCELED) {
						break;
					}
					final long time = timedEvent.getTime();
					if (time != orderRow.getLong(schema.oTable_endTime))
						break;
					//					schema.oTable.fireTriggerUpdating(orderRow);
					preparedOrderRow.reset();
					double ask = 0;
					double bid = 0;
					AnvilMarketDataSymbol marketData = timedEvent.getMarketData();
					int nbboPosition = marketData.getNbboPositionLe(time);
					int tradePosition = marketData.getTradePositionLe(time);
					if (nbboPosition != -1) {
						bid = marketData.getNbboBidPxAtPos(nbboPosition);
						ask = marketData.getNbboAskPxAtPos(nbboPosition);
						preparedOrderRow.setDouble(schema.oTable_endBid, bid);
						preparedOrderRow.setDouble(schema.oTable_endAsk, ask);
					}
					if (tradePosition != -1) {
						double startVal = orderRow.getDouble(schema.oTable_startVal);
						double value = marketData.getTradeAggVal(tradePosition);
						long volume = marketData.getTradeAggVol(tradePosition);
						float last = marketData.getTradePxAtPos(tradePosition);
						double high = marketData.getTradeAggHigh(tradePosition);
						double low = marketData.getTradeAggLow(tradePosition);
						if (startVal == startVal)
							preparedOrderRow.setDouble(schema.oTable_intervalVWAP, (value - startVal) / (volume - orderRow.getLong(schema.oTable_startVol)));
						preparedOrderRow.setDouble(schema.oTable_endVal, value);
						preparedOrderRow.setLong(schema.oTable_endVol, volume);
						preparedOrderRow.setDouble(schema.oTable_last, last);
						preparedOrderRow.setDouble(schema.oTable_dailyValue, value);
						preparedOrderRow.setLong(schema.oTable_dailyVolume, volume);
						preparedOrderRow.setDouble(schema.oTable_dailyVWAP, value / volume);
						preparedOrderRow.setDouble(schema.oTable_lho, (low + high + orderRow.getDouble(schema.oTable_open)) / 3);
					}
					if (!orderRow.getIsNull(schema.oTable_startMidpointHL)) {
						double startMidpoint = orderRow.getDouble(schema.oTable_startMidpointHL);
						preparedOrderRow.setDouble(schema.oTable_mktImpact, orderRow.getDouble(schema.oTable_filledValue) - orderRow.getLong(schema.oTable_filled) * startMidpoint);
					}
					preparedOrderRow.setDouble(schema.oTable_opportunityCost, 0d);
					if (status == STATUS_PRECANCELD)
						setStatus(schema.oTable_isOpen, orderRow, STATUS_CANCELED, session);
					else
						setStatus(schema.oTable_isOpen, orderRow, STATUS_CLOSED, session);
					if (status == STATUS_OPEN)
						this.service.incrementStatsForOrderClosed(1);
					//					schema.oTable.fireTriggerUpdated(orderRow);
					schema.oTable.updateAmiRow(orderRow.getAmiId(), preparedOrderRow, session);
					break;
				}
				/* ------------------------------------
				 * ------------------------------------
				 * Timed Execution Events
				 * ------------------------------------
				 * ------------------------------------
				 */
				case (AnvilTimedEvent.EXECUTION_TIMER): {
					byte exType = timedEvent.getTargetCol();
					AmiRow exRow = schema.exTable.getAmiRowByAmiId(timedEvent.getAmiID());
					String venue = exRow.getString(schema.exTable_ex);
					String side = exRow.getString(schema.exTable_side);
					AmiRow venueRow = searchForMatchingExchange(venue, side);
					double originalPx = exRow.getDouble(schema.exTable_px);
					final long eventTime = timedEvent.getTime();
					AnvilMarketDataSymbol marketData = timedEvent.getMarketData();
					int tradePosition = marketData.getTradePositionLe((int) eventTime);
					if (tradePosition == -1) {
					} else {
						double px = marketData.getTradePxAtPos(tradePosition);
						long size = exRow.getLong(schema.exTable_size);
						orderTableID.setValue(exRow.getString(schema.exTable_parentId));
						AmiRow orderRow = schema.oTable.query(orderTableIDQuery);
						if (orderRow == null) {
						} else {
							if (side == null)
								side = orderRow.getString(schema.oTable_side);
							String orderId = orderRow.getString(schema.oTable_orderID);
							preparedExecutionRow.reset();
							venueTable_preparedRow.reset();
							preparedOrderRow.reset();
							//							schema.oTable.fireTriggerUpdating(orderRow);
							double bps;
							switch (exType) {
								case AnvilTimedEvent.T1:
									preparedExecutionRow.setDouble(schema.exTable_t1, px);
									if (orderRow.getIsNull(schema.oTable_t1Vol)) {
										preparedOrderRow.setLong(schema.oTable_t1Vol, size);
										preparedOrderRow.setDouble(schema.oTable_t1Ev, size * px);
										preparedOrderRow.setDouble(schema.oTable_t1Rv, size * originalPx);
									} else {
										preparedOrderRow.setLong(schema.oTable_t1Vol, orderRow.getLong(schema.oTable_t1Vol) + size);
										preparedOrderRow.setDouble(schema.oTable_t1Ev, orderRow.getDouble(schema.oTable_t1Ev) + size * px);
										preparedOrderRow.setDouble(schema.oTable_t1Rv, orderRow.getDouble(schema.oTable_t1Rv) + size * originalPx);
									}
									if (venueRow.getIsNull(schema.venueTable_t1Cnt)) {
										venueTable_preparedRow.setLong(schema.venueTable_t1Cnt, 1);
										venueTable_preparedRow.setDouble(schema.venueTable_rvT1, size * originalPx);
										venueTable_preparedRow.setDouble(schema.venueTable_t1Val, size * px);
									} else {
										venueTable_preparedRow.setLong(schema.venueTable_t1Cnt, venueRow.getLong(schema.venueTable_t1Cnt) + 1);
										venueTable_preparedRow.setDouble(schema.venueTable_rvT1, venueRow.getLong(schema.venueTable_rvT1) + size * originalPx);
										venueTable_preparedRow.setDouble(schema.venueTable_t1Val, venueRow.getDouble(schema.venueTable_t1Val) + size * px);
									}
									bps = (1 - orderRow.getDouble(schema.oTable_t1Rv) / orderRow.getDouble(schema.oTable_t1Ev)) * 10000;
									boolean shouldGenAlert = side.charAt(0) == 'B' ? bps < -t1AlertCutoff : bps > t1AlertCutoff;
									//Alert
									//									if (shouldGenAlert) {
									//										long severity = 1 + (long) (Math.abs(bps / t1AlertCutoff));
									//										service.addAlert(AnvilServices.ALERT_T1, exRow.getString(schema.exTable_parentId), exRow.getString(schema.exTable_sector),
									//												exRow.getString(schema.exTable_industry), exRow.getString(schema.exTable_account), exRow.getString(schema.exTable_sym),
									//												exRow.getString(schema.exTable_system), eventTime, severity, "Bps: " + bps, orderId, venue, null, null);
									//										childAlertPreparedRow.reset();
									//										childAlertPreparedRow.setString(schema.childAlertsTable_type, "T+1");
									//										childAlertPreparedRow.setLong(schema.childAlertsTable_id, service.childAlertId++);
									//String sym = exRow.getString(schema.exTable_sym);
									//childAlertPreparedRow.setString(schema.childAlertsTable_details, orderId);
									//										childAlertPreparedRow.setString(schema.childAlertsTable_orderId, exRow.getString(schema.exTable_parentId));
									//										childAlertPreparedRow.setString(schema.childAlertsTable_account, exRow.getString(schema.exTable_account));
									//										childAlertPreparedRow.setString(schema.childAlertsTable_sector, exRow.getString(schema.exTable_sector));
									//										childAlertPreparedRow.setString(schema.childAlertsTable_industry, exRow.getString(schema.exTable_industry));
									//										childAlertPreparedRow.setString(schema.childAlertsTable_sym, sym);
									//										childAlertPreparedRow.setString(schema.childAlertsTable_system, exRow.getString(schema.exTable_system));
									//childAlertPreparedRow.setString(schema.childAlertsTable_exchange, venue);
									//										childAlertPreparedRow.setLong(schema.childAlertsTable_time, eventTime);
									//										childAlertPreparedRow.setString(schema.childAlertsTable_comment, "Bps: " + bps);
									//										childAlertPreparedRow.setLong(schema.childAlertsTable_severity, 1 + (long) (Math.abs(bps / t1AlertCutoff)));
									//										schema.childAlertsTable.fireTriggerInsert(childAlertPreparedRow);
									//										schema.childAlertsTable.insertAmiRow(childAlertPreparedRow);
									//									}
									break;
								case AnvilTimedEvent.T5:
									preparedExecutionRow.setDouble(schema.exTable_t5, px);
									if (orderRow.getIsNull(schema.oTable_t5Vol)) {
										preparedOrderRow.setLong(schema.oTable_t5Vol, size);
										preparedOrderRow.setDouble(schema.oTable_t5Ev, size * px);
										preparedOrderRow.setDouble(schema.oTable_t5Rv, size * originalPx);
									} else {
										preparedOrderRow.setLong(schema.oTable_t5Vol, orderRow.getLong(schema.oTable_t5Vol) + size);
										preparedOrderRow.setDouble(schema.oTable_t5Ev, orderRow.getDouble(schema.oTable_t5Ev) + size * px);
										preparedOrderRow.setDouble(schema.oTable_t5Rv, orderRow.getDouble(schema.oTable_t5Rv) + size * originalPx);
									}
									if (venueRow.getIsNull(schema.venueTable_t5Cnt)) {
										venueTable_preparedRow.setLong(schema.venueTable_t5Cnt, 1);
										venueTable_preparedRow.setDouble(schema.venueTable_rvT5, size * originalPx);
										venueTable_preparedRow.setDouble(schema.venueTable_t5Val, size * px);
									} else {
										venueTable_preparedRow.setLong(schema.venueTable_t5Cnt, venueRow.getLong(schema.venueTable_t5Cnt) + 1);
										venueTable_preparedRow.setDouble(schema.venueTable_rvT5, venueRow.getLong(schema.venueTable_rvT5) + size * originalPx);
										venueTable_preparedRow.setDouble(schema.venueTable_t5Val, venueRow.getDouble(schema.venueTable_t5Val) + size * px);
									}
									bps = (1 - orderRow.getDouble(schema.oTable_t5Rv) / orderRow.getDouble(schema.oTable_t5Ev)) * 10000;
									shouldGenAlert = side.charAt(0) == 'B' ? bps < -t5AlertCutoff : bps > t5AlertCutoff;
									//Alert
									//									if (shouldGenAlert) {
									//										long severity = 1 + (long) (Math.abs(bps / t5AlertCutoff));
									//										service.addAlert(AnvilServices.ALERT_T5, exRow.getString(schema.exTable_parentId), exRow.getString(schema.exTable_sector),
									//												exRow.getString(schema.exTable_industry), exRow.getString(schema.exTable_account), exRow.getString(schema.exTable_sym),
									//												exRow.getString(schema.exTable_system), eventTime, severity, "Bps: " + bps, orderId, venue, null, null);
									//										childAlertPreparedRow.reset();
									//										childAlertPreparedRow.setString(schema.childAlertsTable_type, "T+5");
									//										childAlertPreparedRow.setLong(schema.childAlertsTable_id, service.childAlertId++);
									//										String sym = exRow.getString(schema.exTable_sym);
									//										childAlertPreparedRow.setString(schema.childAlertsTable_details, orderId);
									//										childAlertPreparedRow.setString(schema.childAlertsTable_orderId, exRow.getString(schema.exTable_parentId));
									//										childAlertPreparedRow.setString(schema.childAlertsTable_account, exRow.getString(schema.exTable_account));
									//										childAlertPreparedRow.setString(schema.childAlertsTable_sector, exRow.getString(schema.exTable_sector));
									//										childAlertPreparedRow.setString(schema.childAlertsTable_industry, exRow.getString(schema.exTable_industry));
									//										childAlertPreparedRow.setString(schema.childAlertsTable_sym, sym);
									//										childAlertPreparedRow.setString(schema.childAlertsTable_system, exRow.getString(schema.exTable_system));
									//										childAlertPreparedRow.setString(schema.childAlertsTable_exchange, venue);
									//										childAlertPreparedRow.setLong(schema.childAlertsTable_time, eventTime);
									//										childAlertPreparedRow.setLong(schema.childAlertsTable_severity, 1 + (long) (Math.abs(bps / t5AlertCutoff)));
									//										schema.childAlertsTable.fireTriggerInsert(childAlertPreparedRow);
									//										schema.childAlertsTable.insertAmiRow(childAlertPreparedRow);
									//									}
									break;
							}
							schema.exTable.updateAmiRow(exRow.getAmiId(), preparedExecutionRow, session);
							schema.venueTable.updateAmiRow(venueRow.getAmiId(), venueTable_preparedRow, session);
							schema.oTable.updateAmiRow(orderRow.getAmiId(), preparedOrderRow, session);
							//							schema.oTable.fireTriggerUpdated(orderRow);
						}
					}
					break;
				}
				/* ------------------------------------
				 * ------------------------------------
				 * Comparing Execution to NBBO
				 * ------------------------------------
				 * ------------------------------------
				 */
				case (AnvilTimedEvent.BUST):
				case (AnvilTimedEvent.EXECUTION): {
					AmiRow exRow = schema.exTable.getAmiRowByAmiId(timedEvent.getAmiID());
					String execIndicator = exRow.getString(schema.exTable_execIndicator);
					String venue = exRow.getString(schema.exTable_ex);
					String side = exRow.getString(schema.exTable_side);
					boolean isLit = service.isVenueLit(venue);
					boolean addsLiquidity = "AL".equals(execIndicator);
					boolean routesLiquidity = "OL".equals(execIndicator);
					final long time = timedEvent.getTime();
					String symbol = exRow.getString(schema.exTable_sym);
					double px = exRow.getDouble(schema.exTable_px);
					long size = exRow.getLong(schema.exTable_size);
					if (eventType == AnvilTimedEvent.BUST)
						size = -size;
					String status = exRow.getString(schema.exTable_status);
					AnvilMarketDataSymbol marketData = timedEvent.getMarketData();
					int nbboEndPosition = marketData.getNbboPositionLe(time);
					if (nbboEndPosition == -1) {
						AmiRow venueRow = searchForMatchingExchange(venue, side);
						if (venueRow == null) {
							venueTable_preparedRow.reset();
							venueTable_preparedRow.setString(schema.venueTable_venue, venue);
							venueTable_preparedRow.setString(schema.venueTable_side, side);
							AmiRow secMasterRow = searchSecMasterForSymbol(symbol);
							if (secMasterRow != null) {
								double prevClose = secMasterRow.getDouble(schema.secMasterTable_prevClose);
								if (prevClose == prevClose) {
									boolean isInPrevClose = side.equals("S") ? MH.ge(px, prevClose, PRECISION_DELTA) : MH.le(px, prevClose, PRECISION_DELTA);
									venueTable_preparedRow.setLong(schema.venueTable_exCnt, 1);
									venueTable_preparedRow.setLong(schema.venueTable_spreadCnt, isInPrevClose ? 1 : 0);
									venueTable_preparedRow.setLong(schema.venueTable_regCnt, isInPrevClose ? 1 : 0);
									double value = prevClose * size;
									venueTable_preparedRow.setDouble(schema.venueTable_bidAgg, value);
									venueTable_preparedRow.setDouble(schema.venueTable_askAgg, value);
									venueTable_preparedRow.setDouble(schema.venueTable_regBidAgg, value);
									venueTable_preparedRow.setDouble(schema.venueTable_regAskAgg, value);
									venueTable_preparedRow.setLong(schema.venueTable_volume, size);
									venueTable_preparedRow.setDouble(schema.venueTable_processedValue, px * size);
									if (addsLiquidity) {
										venueTable_preparedRow.setLong(schema.venueTable_liqCnt, 1);
										venueTable_preparedRow.setLong(schema.venueTable_liqVol, size);
										venueTable_preparedRow.setDouble(schema.venueTable_liqVal, size * px);
									} else {
										venueTable_preparedRow.setLong(schema.venueTable_liqCnt, 0);
										venueTable_preparedRow.setLong(schema.venueTable_liqVol, 0);
										venueTable_preparedRow.setDouble(schema.venueTable_liqVal, 0);
									}
									if (routesLiquidity) {
										venueTable_preparedRow.setLong(schema.venueTable_routedCnt, 1);
										venueTable_preparedRow.setLong(schema.venueTable_routedVol, size);
										venueTable_preparedRow.setDouble(schema.venueTable_routedVal, size * px);
									} else {
										venueTable_preparedRow.setLong(schema.venueTable_routedCnt, 0);
										venueTable_preparedRow.setLong(schema.venueTable_routedVol, 0);
										venueTable_preparedRow.setDouble(schema.venueTable_routedVal, 0);
									}
								} else {
									populateNewVenueRowWithInvalidExecution();
								}
							} else {
								populateNewVenueRowWithInvalidExecution();
							}
							venueTable_preparedRow.setLong(schema.venueTable_lit, isLit ? 1 : 0);
							schema.venueTable.insertAmiRow(venueTable_preparedRow, session);
						} else {
							AmiRow secMasterRow = searchSecMasterForSymbol(symbol);
							if (secMasterRow != null) {
								double prevClose = secMasterRow.getDouble(schema.secMasterTable_prevClose);
								if (prevClose == prevClose) {
									boolean isInReg = side.equals("S") ? MH.ge(px, prevClose, PRECISION_DELTA) : MH.le(px, prevClose, PRECISION_DELTA);
									boolean isInSpread = isInReg;// side.equals("S") ? MH.ge(px, prevClose, PRECISION_DELTA) : MH.le(px, prevClose, PRECISION_DELTA);
									venueTable_preparedRow.reset();
									if (isInReg)
										venueTable_preparedRow.setLong(schema.venueTable_regCnt, venueRow.getLong(schema.venueTable_regCnt) + 1);
									if (isInSpread)
										venueTable_preparedRow.setLong(schema.venueTable_spreadCnt, venueRow.getLong(schema.venueTable_spreadCnt) + 1);
									double value = prevClose * size;
									venueTable_preparedRow.setDouble(schema.venueTable_bidAgg, venueRow.getDouble(schema.venueTable_bidAgg) + value);
									venueTable_preparedRow.setDouble(schema.venueTable_askAgg, venueRow.getDouble(schema.venueTable_askAgg) + value);
									venueTable_preparedRow.setDouble(schema.venueTable_regBidAgg, venueRow.getDouble(schema.venueTable_regBidAgg) + value);
									venueTable_preparedRow.setDouble(schema.venueTable_regAskAgg, venueRow.getDouble(schema.venueTable_regAskAgg) + value);
									venueTable_preparedRow.setLong(schema.venueTable_volume, venueRow.getLong(schema.venueTable_volume) + size);
									venueTable_preparedRow.setDouble(schema.venueTable_processedValue, venueRow.getDouble(schema.venueTable_processedValue) + px * size);
									venueTable_preparedRow.setLong(schema.venueTable_exCnt, venueRow.getLong(schema.venueTable_exCnt) + 1);
									if (addsLiquidity) {
										venueTable_preparedRow.setLong(schema.venueTable_liqCnt, venueRow.getLong(schema.venueTable_liqCnt) + 1);
										venueTable_preparedRow.setLong(schema.venueTable_liqVol, venueRow.getLong(schema.venueTable_liqVol) + size);
										venueTable_preparedRow.setDouble(schema.venueTable_liqVal, venueRow.getDouble(schema.venueTable_liqVal) + size * px);
									} else if (routesLiquidity) {
										venueTable_preparedRow.setLong(schema.venueTable_routedCnt, venueRow.getLong(schema.venueTable_routedCnt) + 1);
										venueTable_preparedRow.setLong(schema.venueTable_routedVol, venueRow.getLong(schema.venueTable_routedVol) + size);
										venueTable_preparedRow.setDouble(schema.venueTable_routedVal, venueRow.getDouble(schema.venueTable_routedVal) + size * px);
									}
									schema.venueTable.updateAmiRow(venueRow.getAmiId(), venueTable_preparedRow, session);
								}
							}
						}
					} else {
						int nbboStartPosition = Math.max(marketData.getNbboPositionLe(time - NBBO_REG_TIME), 0);
						boolean first = true;
						double bid = Double.NaN;
						double ask = Double.NaN;
						double minBid = Double.NaN;
						double maxAsk = Double.NaN;
						for (int i = nbboStartPosition; i <= nbboEndPosition; i++) {
							if (first) {
								minBid = bid = marketData.getNbboBidPxAtPos(i);
								maxAsk = ask = marketData.getNbboAskPxAtPos(i);
								first = false;
								continue;
							}
							bid = marketData.getNbboBidPxAtPos(i);
							if (bid < minBid)
								minBid = bid;
							ask = marketData.getNbboAskPxAtPos(i);
							if (ask > maxAsk)
								maxAsk = ask;
						}
						preparedExecutionRow.reset();
						preparedExecutionRow.setDouble(schema.exTable_ask, ask);
						preparedExecutionRow.setDouble(schema.exTable_bid, bid);
						preparedExecutionRow.setDouble(schema.exTable_maxAsk, maxAsk);
						preparedExecutionRow.setDouble(schema.exTable_minBid, minBid);
						preparedExecutionRow.setDouble(schema.exTable_evAsk, ask * size);
						preparedExecutionRow.setDouble(schema.exTable_evBid, bid * size);
						preparedExecutionRow.setDouble(schema.exTable_evMaxAsk, maxAsk * size);
						preparedExecutionRow.setDouble(schema.exTable_evMinBid, minBid * size);
						boolean isInSpread = side.equals("S") ? MH.ge(px, bid, PRECISION_DELTA) : MH.le(px, ask, PRECISION_DELTA);
						boolean isInReg = side.equals("S") ? MH.ge(px, minBid, PRECISION_DELTA) : MH.le(px, maxAsk, PRECISION_DELTA);
						if (isInSpread)
							preparedExecutionRow.setLong(schema.exTable_inSpread, 1);
						else
							preparedExecutionRow.setLong(schema.exTable_inSpread, 0);
						if (isInReg)
							preparedExecutionRow.setLong(schema.exTable_inReg, 1);
						else
							preparedExecutionRow.setLong(schema.exTable_inReg, 0);
						schema.exTable.updateAmiRow(exRow.getAmiId(), preparedExecutionRow, session);
						AmiRow venueRow = searchForMatchingExchange(venue, side);
						if (venueRow == null) {
							venueTable_preparedRow.reset();
							venueTable_preparedRow.setString(schema.venueTable_venue, venue);
							venueTable_preparedRow.setString(schema.venueTable_side, side);
							venueTable_preparedRow.setLong(schema.venueTable_exCnt, 1);
							venueTable_preparedRow.setLong(schema.venueTable_spreadCnt, isInSpread ? 1 : 0);
							venueTable_preparedRow.setLong(schema.venueTable_regCnt, isInReg ? 1 : 0);
							venueTable_preparedRow.setLong(schema.venueTable_volume, size);
							venueTable_preparedRow.setDouble(schema.venueTable_bidAgg, bid * size);
							venueTable_preparedRow.setDouble(schema.venueTable_askAgg, ask * size);
							venueTable_preparedRow.setDouble(schema.venueTable_regBidAgg, minBid * size);
							venueTable_preparedRow.setDouble(schema.venueTable_regAskAgg, maxAsk * size);
							venueTable_preparedRow.setDouble(schema.venueTable_processedValue, px * size);
							venueTable_preparedRow.setLong(schema.venueTable_lit, isLit ? 1 : 0);
							if (addsLiquidity) {
								venueTable_preparedRow.setLong(schema.venueTable_liqCnt, 1);
								venueTable_preparedRow.setLong(schema.venueTable_liqVol, size);
								venueTable_preparedRow.setDouble(schema.venueTable_liqVal, size * px);
							} else {
								venueTable_preparedRow.setLong(schema.venueTable_liqCnt, 0);
								venueTable_preparedRow.setLong(schema.venueTable_liqVol, 0);
								venueTable_preparedRow.setDouble(schema.venueTable_liqVal, 0);
							}
							if (routesLiquidity) {
								venueTable_preparedRow.setLong(schema.venueTable_routedCnt, 1);
								venueTable_preparedRow.setLong(schema.venueTable_routedVol, size);
								venueTable_preparedRow.setDouble(schema.venueTable_routedVal, size * px);
							} else {
								venueTable_preparedRow.setLong(schema.venueTable_routedCnt, 0);
								venueTable_preparedRow.setLong(schema.venueTable_routedVol, 0);
								venueTable_preparedRow.setDouble(schema.venueTable_routedVal, 0);
							}
							schema.venueTable.insertAmiRow(venueTable_preparedRow, session);

						} else {
							venueTable_preparedRow.reset();
							venueTable_preparedRow.setLong(schema.venueTable_exCnt, venueRow.getLong(schema.venueTable_exCnt) + 1);
							venueTable_preparedRow.setLong(schema.venueTable_volume, venueRow.getLong(schema.venueTable_volume) + size);
							if (isInReg) {
								venueTable_preparedRow.setLong(schema.venueTable_regCnt, venueRow.getLong(schema.venueTable_regCnt) + 1);
								if (isInSpread)
									venueTable_preparedRow.setLong(schema.venueTable_spreadCnt, venueRow.getLong(schema.venueTable_spreadCnt) + 1);
							}
							venueTable_preparedRow.setDouble(schema.venueTable_bidAgg, venueRow.getDouble(schema.venueTable_bidAgg) + bid * size);
							venueTable_preparedRow.setDouble(schema.venueTable_askAgg, venueRow.getDouble(schema.venueTable_askAgg) + ask * size);
							venueTable_preparedRow.setDouble(schema.venueTable_regBidAgg, venueRow.getDouble(schema.venueTable_regBidAgg) + minBid * size);
							venueTable_preparedRow.setDouble(schema.venueTable_regAskAgg, venueRow.getDouble(schema.venueTable_regAskAgg) + maxAsk * size);
							venueTable_preparedRow.setDouble(schema.venueTable_processedValue, venueRow.getDouble(schema.venueTable_processedValue) + px * size);
							if (addsLiquidity) {
								venueTable_preparedRow.setLong(schema.venueTable_liqCnt, venueRow.getLong(schema.venueTable_liqCnt) + 1);
								venueTable_preparedRow.setLong(schema.venueTable_liqVol, venueRow.getLong(schema.venueTable_liqVol) + size);
								venueTable_preparedRow.setDouble(schema.venueTable_liqVal, venueRow.getDouble(schema.venueTable_liqVal) + size * px);
							} else if (routesLiquidity) {
								venueTable_preparedRow.setLong(schema.venueTable_routedCnt, venueRow.getLong(schema.venueTable_routedCnt) + 1);
								venueTable_preparedRow.setLong(schema.venueTable_routedVol, venueRow.getLong(schema.venueTable_routedVol) + size);
								venueTable_preparedRow.setDouble(schema.venueTable_routedVal, venueRow.getDouble(schema.venueTable_routedVal) + size * px);
							}
							schema.venueTable.updateAmiRow(venueRow.getAmiId(), venueTable_preparedRow, session);
						}
						//Create New ChildAlert
						//Alert
						//						if (!isInReg) {
						//							String sym = exRow.getString(schema.exTable_sym);
						//							service.addAlert(AnvilServices.ALERT_NBBO_REGULATION, exRow.getString(schema.exTable_parentId), exRow.getString(schema.exTable_sector),
						//									exRow.getString(schema.exTable_industry), exRow.getString(schema.exTable_account), sym, exRow.getString(schema.exTable_system), time, 1, null,
						//									sym, venue, null);
						//							childAlertPreparedRow.reset();
						//							//childAlertPreparedRow.setString(schema.childAlertsTable_type, "NBBO Regulation");
						//							//childAlertPreparedRow.setLong(schema.childAlertsTable_id, service.childAlertId++);
						//							//childAlertPreparedRow.setString(schema.childAlertsTable_modifiedBy, "Anvil Execution Monitor");
						//							//childAlertPreparedRow.setString(schema.childAlertsTable_details, sym);
						//							//childAlertPreparedRow.setString(schema.childAlertsTable_orderId, exRow.getString(schema.exTable_parentId));
						//							//childAlertPreparedRow.setString(schema.childAlertsTable_account, exRow.getString(schema.exTable_account));
						//							//childAlertPreparedRow.setString(schema.childAlertsTable_sector, exRow.getString(schema.exTable_sector));
						//							//childAlertPreparedRow.setString(schema.childAlertsTable_industry, exRow.getString(schema.exTable_industry));
						//							//childAlertPreparedRow.setString(schema.childAlertsTable_sym, sym);
						//							//childAlertPreparedRow.setString(schema.childAlertsTable_system, exRow.getString(schema.exTable_system));
						//							//childAlertPreparedRow.setString(schema.childAlertsTable_exchange, venue);
						//							//childAlertPreparedRow.setLong(schema.childAlertsTable_time, time);
						//							//childAlertPreparedRow.setLong(schema.childAlertsTable_severity, 1);
						//							//schema.childAlertsTable.fireTriggerInsert(childAlertPreparedRow);
						//							//schema.childAlertsTable.insertAmiRow(childAlertPreparedRow);
						//						}

						orderTableID.setValue(exRow.getString(schema.exTable_parentId));
						AmiRow orderRow = schema.oTable.query(orderTableIDQuery);
						if (orderRow == null)
							LH.info(log, "THERE WAS NO ORDER TO AGG EXECUTION ONTO");
						else {
							//							schema.oTable.fireTriggerUpdating(orderRow);
							if (orderRow.getIsNull(schema.oTable_bidAgg)) {
								preparedOrderRow.reset();
								preparedOrderRow.setDouble(schema.oTable_bidAgg, bid * size);
								preparedOrderRow.setDouble(schema.oTable_askAgg, ask * size);
								preparedOrderRow.setDouble(schema.oTable_regBidAgg, minBid * size);
								preparedOrderRow.setDouble(schema.oTable_regAskAgg, maxAsk * size);
								preparedOrderRow.setDouble(schema.oTable_processedValue, px * size);
								preparedOrderRow.setDouble(schema.oTable_bidAgg, orderRow.getDouble(schema.oTable_bidAgg) + bid * size);
								preparedOrderRow.setDouble(schema.oTable_askAgg, orderRow.getDouble(schema.oTable_askAgg) + ask * size);
								preparedOrderRow.setDouble(schema.oTable_regBidAgg, orderRow.getDouble(schema.oTable_regBidAgg) + minBid * size);
								preparedOrderRow.setDouble(schema.oTable_regAskAgg, orderRow.getDouble(schema.oTable_regAskAgg) + maxAsk * size);
								preparedOrderRow.setDouble(schema.oTable_processedValue, orderRow.getDouble(schema.oTable_processedValue) + px * size);
								schema.oTable.updateAmiRow(orderRow.getAmiId(), preparedOrderRow, session);
							}
							//							schema.oTable.fireTriggerUpdated(orderRow);
						}
					}
					break;
				}
			}
			timedEventPool.recycle(timedEvent);
		}
		return r;
	}
	private void populateNewVenueRowWithInvalidExecution() {
		venueTable_preparedRow.setLong(schema.venueTable_exCnt, 0);
		venueTable_preparedRow.setLong(schema.venueTable_spreadCnt, 0);
		venueTable_preparedRow.setLong(schema.venueTable_regCnt, 0);
		venueTable_preparedRow.setDouble(schema.venueTable_bidAgg, 0);
		venueTable_preparedRow.setDouble(schema.venueTable_askAgg, 0);
		venueTable_preparedRow.setDouble(schema.venueTable_regBidAgg, 0);
		venueTable_preparedRow.setDouble(schema.venueTable_regAskAgg, 0);
		venueTable_preparedRow.setLong(schema.venueTable_volume, 0);
		venueTable_preparedRow.setDouble(schema.venueTable_processedValue, 0);
		venueTable_preparedRow.setLong(schema.venueTable_lit, 0);
		venueTable_preparedRow.setLong(schema.venueTable_routedCnt, 0);
		venueTable_preparedRow.setLong(schema.venueTable_routedVol, 0);
		venueTable_preparedRow.setDouble(schema.venueTable_routedVal, 0);
		venueTable_preparedRow.setLong(schema.venueTable_liqCnt, 0);
		venueTable_preparedRow.setLong(schema.venueTable_liqVol, 0);
		venueTable_preparedRow.setDouble(schema.venueTable_liqVal, 0);
	}

	private void searchOrdersByAccountSideTableByAccount(String account) {
		searchOrdersResultRows.clear();
		ordersByAccountSideQuery_accountParam.setValue(account);
		schema.ordersByAccountSideTable.query(ordersByAccountSideQuery, 10000000, searchOrdersResultRows);
	}
	private AmiRow searchForMatchingExchange(String venue, String side) {
		venueSymbolIndex.setValue(venue);
		venueSideIndex.setValue(side);
		return schema.venueTable.query(venuePreparedQuery);
	}
	private void searchOrderTableForMatchingSymbol(String symbol) {
		searchResultRows.clear();
		orderTableIndex.setValue(symbol);
		schema.oTable.query(orderTableQuery, Integer.MAX_VALUE, searchResultRows);
	}

	private void searchSymTableForMatchingSymbol(String symbol) {
		searchResultRows.clear();
		symTableIndex.setValue(symbol);
		schema.symTable.query(symTable_query, 1, searchResultRows);
	}
	static public long getStatus(AmiColumn isOpenColumn, AmiRow orderRow) {
		return orderRow.getLong(isOpenColumn);
	}
	static public void setStatus(AmiColumn isOpenColumn, AmiRow orderRow, int status, AmiImdbSession session) {
		long old = getStatus(isOpenColumn, orderRow);
		switch (status) {
			case STATUS_PREOPEN:
				if (old >= 0L)
					LH.info(log, "BAD STATE TRANSITION: ", old, " ==> ", status, " for ", orderRow);
				break;
			case STATUS_OPEN:
				if (old == STATUS_PRECANCELD)
					return;

				if (old != STATUS_PREOPEN)
					LH.info(log, "BAD STATE TRANSITION: ", old, " ==> ", status, " for ", orderRow);
				break;
			case STATUS_CLOSED:
				if (old != STATUS_OPEN)
					LH.info(log, "BAD STATE TRANSITION: ", old, " ==> ", status, " for ", orderRow);
				break;
			case STATUS_PRECANCELD:
				if (old != STATUS_OPEN && old != STATUS_PREOPEN)
					LH.info(log, "BAD STATE TRANSITION: ", old, " ==> ", status, " for ", orderRow);
				break;
			case STATUS_CANCELED:
				if (old != STATUS_PRECANCELD)
					LH.info(log, "BAD STATE TRANSITION: ", old, " ==> ", status, " for ", orderRow);
				break;

		}
		orderRow.setLong(isOpenColumn, status, session);
	}

	private void searchForOpenOrdersBySymbol(String sym) {
		searchResultRows.clear();
		this.openOrderQuery_sym.setValue(sym);
		schema.oTable.query(openOrderQuery, 100000000, searchResultRows);
	}
	private AmiRow searchSecMasterForSymbol(String sym) {
		secMasterQuery_symParam.setValue(sym);
		return schema.secMasterTable.query(secMasterQuery);
	}
	@Override
	public void onSchemaChanged(AmiImdb imdb, AmiImdbSession session) {
		// TODO Auto-generated method stub

	}
	@Override
	protected void onStartup(AmiImdbSession session) {
		// TODO Auto-generated method stub

	}
}
