package com.f1.anvil.triggers;

import java.util.logging.Logger;

import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.triggers.AmiAbstractTrigger;
import com.f1.anvil.utils.AnvilBufferedEvent;
import com.f1.anvil.utils.AnvilTaqPeriodQueuesManager;
import com.f1.utils.LH;
import com.f1.utils.structs.table.derived.StackFrame;

public class AnvilTriggerTrade extends AmiAbstractTrigger {
	private static final Logger log = LH.get(AnvilTriggerTrade.class);
	private AnvilServices service;
	private AnvilTriggerTimer nbboTrigger;
	private AnvilTaqPeriodQueuesManager taqQueueManager;
	private AnvilSchema schema;

	@Override
	public void onStartup(AmiImdbSession session) {
		this.service = getImdb().getAmiServiceOrThrow(AnvilServices.SERVICE_NAME, AnvilServices.class);
		this.schema = this.service.getSchema();
		this.taqQueueManager = this.service.getTaqQueueManager();
		this.nbboTrigger = getImdb().getAmiTimerOrThrow("timerTrigger", AnvilTriggerTimer.class);
	}

	@Override
	public void onInserted(AmiTable table, AmiRow row, AmiImdbSession session, StackFrame sf) {
		this.nbboTrigger.resetTimers();
		final String symbol = row.getString(schema.tradeTable_sym);
		long time = row.getLong(schema.tradeTable_time);
		AnvilBufferedEvent be = this.taqQueueManager.onTrade(symbol, time, row);
		if (be == null)
			return;
		if (row.getIsNull(schema.tradeTable_open)) {
			be.aggregateTrade(row.getDouble(schema.tradeTable_px), row.getLong(schema.tradeTable_size), row.getDouble(schema.tradeTable_high), row.getDouble(schema.tradeTable_low),
					row.getDouble(schema.tradeTable_value), row.getLong(schema.tradeTable_volume), time);
		} else {
			be.aggregateTradeWithOpen(row.getDouble(schema.tradeTable_px), row.getLong(schema.tradeTable_size), row.getDouble(schema.tradeTable_high),
					row.getDouble(schema.tradeTable_low), row.getDouble(schema.tradeTable_open), row.getDouble(schema.tradeTable_value), row.getLong(schema.tradeTable_volume),
					time);
		}
		this.service.incrementStatsForTradeReceived(1, session);
		return;
	}

}
