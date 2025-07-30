package com.f1.anvil.triggers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.center.table.AmiCenterProcess;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiPreparedQuery;
import com.f1.ami.center.table.AmiPreparedQueryCompareClause;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.timers.AmiAbstractTimer;
import com.f1.anvil.utils.AnvilMarketData;
import com.f1.utils.LH;
import com.f1.utils.structs.table.derived.StackFrame;

public class AnvilMessageAlertTimer extends AmiAbstractTimer {
	private static final Logger log = LH.get(AnvilMessageAlertTimer.class);
	private AnvilServices service;
	private AnvilMarketData marketDataInstance;
	private AnvilSchema schema;
	private List<AmiRow> searchResults;
	private long lastTime = -1;
	private long elapsedM1 = 0;

	private AmiPreparedQuery ordersBySymSideTableQuery;
	private AmiPreparedQueryCompareClause ordersBySymSideTableQuery_sideParam;
	private AmiPreparedQueryCompareClause ordersBySymSideTableQuery_symParam;

	private AmiPreparedQuery ordersBySymSideTableQuerySide;
	private AmiPreparedQueryCompareClause ordersBySymSideTableQuerySide_sideParam;
	private AmiPreparedQuery secMasterQuery;
	private AmiPreparedQueryCompareClause secMasterQuery_symParam;

	//	private AmiPreparedRow preparedOrdersBySymSideRow;

	private HashMap<String, Integer> symParentOrdersBuyMessageCountTM1;
	private HashMap<String, Integer> symParentOrdersSellMessageCountTM1;
	private HashMap<String, Integer> symParentOrdersBuyMessageCountTM2;
	private HashMap<String, Integer> symParentOrdersSellMessageCountTM2;

	private HashMap<String, Integer> symExBuyMessageCountTM1;
	private HashMap<String, Integer> symExSellMessageCountTM1;
	private HashMap<String, Integer> symExBuyMessageCountTM2;
	private HashMap<String, Integer> symExSellMessageCountTM2;

	@Override
	public boolean onTimer(long scheduledTime, AmiImdbSession session, AmiCenterProcess process) {
		session.lock(process, null);
		try {
			final long cNbbo = marketDataInstance.getCurrentNbbosTime();
			final long cTrade = marketDataInstance.getCurrentTradesTime();
			long time = cNbbo < cTrade ? cNbbo : cTrade;
			long elapsed = (time - lastTime);
			long windowDuration = elapsed + elapsedM1;
			if (windowDuration > 0) {
				Map<String, Integer> symParentOrdersBuyMessageCount = new HashMap<String, Integer>();
				Map<String, Integer> symParentOrdersSellMessageCount = new HashMap<String, Integer>();
				Map<String, Integer> symExBuyMessageCount = new HashMap<String, Integer>();
				Map<String, Integer> symExSellMessageCount = new HashMap<String, Integer>();
				searchOrdersBySymSideBySide("B");
				for (AmiRow symBySideRow : searchResults) {
					String sym = symBySideRow.getString(schema.ordersBySymSideTable_sym);
					int parentOrderCount = (int) symBySideRow.getLong(schema.ordersBySymSideTable_cnt);
					int exCount = (int) symBySideRow.getLong(schema.ordersBySymSideTable_exCnt);
					symParentOrdersBuyMessageCount.put(sym, parentOrderCount);
					symExBuyMessageCount.put(sym, exCount);
				}

				searchOrdersBySymSideBySide("S");
				for (AmiRow symBySideRow : searchResults) {
					String sym = symBySideRow.getString(schema.ordersBySymSideTable_sym);
					int parentOrderCount = (int) symBySideRow.getLong(schema.ordersBySymSideTable_cnt);
					int exCount = (int) symBySideRow.getLong(schema.ordersBySymSideTable_exCnt);
					symParentOrdersSellMessageCount.put(sym, parentOrderCount);
					symExSellMessageCount.put(sym, exCount);
				}
				//Put Values 
				HashMap<String, Double> symMessageRate = new HashMap<String, Double>();
				for (String sym : symParentOrdersBuyMessageCount.keySet()) {
					Integer prevCount = symParentOrdersBuyMessageCountTM2.get(sym);
					if (prevCount != null) {
						Integer currCount = symParentOrdersBuyMessageCount.get(sym);
						double rate = (currCount - prevCount) * AnvilServices.ALERT_MESSAGE_TIMEFRAME / windowDuration;

						AmiRow ordersBySymSideRow = searchOrdersBySymSideBySym("B", sym);
						schema.ordersBySymSideTable.getAmiRowByAmiId(ordersBySymSideRow.getAmiId()).setDouble(schema.ordersBySymSideTable_messageRate, rate, session);
						//				preparedOrdersBySymSideRow.reset();
						//				preparedOrdersBySymSideRow.setDouble(schema.ordersBySymSideTable_messageRate, rate);
						//
						//				schema.ordersBySymSideTable.updateAmiRow(ordersBySymSideRow.getAmiId(), preparedOrdersBySymSideRow);
						symMessageRate.put(sym, rate);
					}
				}
				for (String sym : symParentOrdersSellMessageCount.keySet()) {
					Integer prevCount = symParentOrdersSellMessageCountTM2.get(sym);
					if (prevCount != null) {
						Integer currCount = symParentOrdersSellMessageCount.get(sym);
						double rate = (currCount - prevCount) * AnvilServices.ALERT_MESSAGE_TIMEFRAME / windowDuration;
						AmiRow ordersBySymSideRow = searchOrdersBySymSideBySym("S", sym);
						schema.ordersBySymSideTable.getAmiRowByAmiId(ordersBySymSideRow.getAmiId()).setDouble(schema.ordersBySymSideTable_messageRate, rate, session);

						Double totalRate = symMessageRate.get(sym);
						if (totalRate == null)
							totalRate = 0d;
						totalRate += rate;
						symMessageRate.put(sym, rate);
					}
				}
				HashMap<String, Double> symExMessageRate = new HashMap<String, Double>();
				for (String sym : symExBuyMessageCount.keySet()) {
					Integer prevCount = symExBuyMessageCountTM2.get(sym);
					if (prevCount != null) {
						Integer currCount = symExBuyMessageCount.get(sym);
						double rate = (currCount - prevCount) * AnvilServices.ALERT_MESSAGE_TIMEFRAME / windowDuration;

						AmiRow ordersBySymSideRow = searchOrdersBySymSideBySym("B", sym);
						schema.ordersBySymSideTable.getAmiRowByAmiId(ordersBySymSideRow.getAmiId()).setDouble(schema.ordersBySymSideTable_exMessageRate, rate, session);
						symExMessageRate.put(sym, rate);
					}
				}
				for (String sym : symExSellMessageCount.keySet()) {
					Integer prevCount = symExSellMessageCountTM2.get(sym);
					if (prevCount != null) {
						Integer currCount = symExSellMessageCount.get(sym);
						double rate = (currCount - prevCount) * AnvilServices.ALERT_MESSAGE_TIMEFRAME / windowDuration;
						AmiRow ordersBySymSideRow = searchOrdersBySymSideBySym("S", sym);
						schema.ordersBySymSideTable.getAmiRowByAmiId(ordersBySymSideRow.getAmiId()).setDouble(schema.ordersBySymSideTable_exMessageRate, rate, session);

						Double totalRate = symExMessageRate.get(sym);
						if (totalRate == null)
							totalRate = 0d;
						totalRate += rate;
						symExMessageRate.put(sym, rate);
					}
				}

				// ALERT MESSAGES
				for (String sym : symMessageRate.keySet()) {
					double rate = symMessageRate.get(sym);
					if (rate >= AnvilServices.THRESHOLD_MESSAGES) {
						AmiRow secMasterRow = searchSecMasterForSymbol(sym);
						String sector = secMasterRow.getString(schema.secMasterTable_sector);
						String industry = secMasterRow.getString(schema.secMasterTable_industry);
						Map<String, Object> mapParams = new HashMap<String, Object>();
						mapParams.put("RATE", rate);
						service.addAlert(AnvilServices.ALERT_MESSAGES, null, sector, industry, null, sym, null, time, 1, null, sym, null, null, mapParams, session);
						//					service.addAlert(AnvilServices.ALERT_MESSAGES, null, sector, industry, null, sym, null, -1, 1, null, sym, null, null);
					}
				}

				// EX ALERT MESSAGES
				for (String sym : symExMessageRate.keySet()) {
					double rate = symExMessageRate.get(sym);
					if (rate >= AnvilServices.THRESHOLD_EX_MESSAGES) {
						AmiRow secMasterRow = searchSecMasterForSymbol(sym);
						String sector = secMasterRow.getString(schema.secMasterTable_sector);
						String industry = secMasterRow.getString(schema.secMasterTable_industry);
						Map<String, Object> mapParams = new HashMap<String, Object>();
						mapParams.put("RATE", rate);
						service.addAlert(AnvilServices.ALERT_EX_MESSAGES, null, sector, industry, null, sym, null, time, 1, null, sym, null, null, mapParams, session);
					}
				}

				symParentOrdersBuyMessageCountTM2.clear();
				symParentOrdersSellMessageCountTM2.clear();
				symParentOrdersBuyMessageCountTM2.putAll(symParentOrdersBuyMessageCountTM1);
				symParentOrdersSellMessageCountTM2.putAll(symParentOrdersSellMessageCountTM1);

				symParentOrdersBuyMessageCountTM1.clear();
				symParentOrdersSellMessageCountTM1.clear();
				symParentOrdersBuyMessageCountTM1.putAll(symParentOrdersBuyMessageCount);
				symParentOrdersSellMessageCountTM1.putAll(symParentOrdersSellMessageCount);

				symExBuyMessageCountTM2.clear();
				symExSellMessageCountTM2.clear();
				symExBuyMessageCountTM2.putAll(symExBuyMessageCountTM1);
				symExSellMessageCountTM2.putAll(symExSellMessageCountTM1);

				symExBuyMessageCountTM1.clear();
				symExSellMessageCountTM1.clear();
				symExBuyMessageCountTM1.putAll(symExBuyMessageCount);
				symExSellMessageCountTM1.putAll(symExSellMessageCount);

				elapsedM1 = elapsed;
				lastTime = time;
			}
		} finally {
			session.unlock();
		}
		return true;
	}
	@Override
	protected void onStartup(AmiImdbSession session, StackFrame sf) {
		this.service = getImdb().getAmiServiceOrThrow(AnvilServices.SERVICE_NAME, AnvilServices.class);
		this.schema = this.service.getSchema();
		this.searchResults = new ArrayList<AmiRow>();
		this.symParentOrdersBuyMessageCountTM1 = new HashMap<String, Integer>();
		this.symParentOrdersSellMessageCountTM1 = new HashMap<String, Integer>();
		this.symParentOrdersBuyMessageCountTM2 = new HashMap<String, Integer>();
		this.symParentOrdersSellMessageCountTM2 = new HashMap<String, Integer>();
		this.symExBuyMessageCountTM1 = new HashMap<String, Integer>();
		this.symExSellMessageCountTM1 = new HashMap<String, Integer>();
		this.symExBuyMessageCountTM2 = new HashMap<String, Integer>();
		this.symExSellMessageCountTM2 = new HashMap<String, Integer>();

		ordersBySymSideTableQuery = schema.ordersBySymSideTable.createAmiPreparedQuery();
		ordersBySymSideTableQuery_sideParam = ordersBySymSideTableQuery.addEq(schema.ordersBySymSideTable_side);
		ordersBySymSideTableQuery_symParam = ordersBySymSideTableQuery.addEq(schema.ordersBySymSideTable_sym);
		LH.info(log, ordersBySymSideTableQuery);

		ordersBySymSideTableQuerySide = schema.ordersBySymSideTable.createAmiPreparedQuery();
		ordersBySymSideTableQuerySide_sideParam = ordersBySymSideTableQuerySide.addEq(schema.ordersBySymSideTable_side);
		LH.info(log, ordersBySymSideTableQuerySide);

		secMasterQuery = schema.secMasterTable.createAmiPreparedQuery();
		secMasterQuery_symParam = secMasterQuery.addEq(schema.secMasterTable_sym);
		//		this.preparedOrdersBySymSideRow = this.schema.ordersBySymSideTable.createAmiPreparedRow();
		this.marketDataInstance = service.getMarketDataInstance();

	}

	private AmiRow searchSecMasterForSymbol(String sym) {
		secMasterQuery_symParam.setValue(sym);
		return schema.secMasterTable.query(secMasterQuery);
	}

	private void searchOrdersBySymSideBySide(String side) {
		searchResults.clear();
		ordersBySymSideTableQuerySide_sideParam.setValue(side);
		schema.ordersBySymSideTable.query(ordersBySymSideTableQuerySide, 10000000, searchResults);
	}

	private AmiRow searchOrdersBySymSideBySym(String side, String sym) {
		ordersBySymSideTableQuery_sideParam.setValue(side);
		ordersBySymSideTableQuery_symParam.setValue(sym);
		return schema.ordersBySymSideTable.query(ordersBySymSideTableQuery);
	}
}
