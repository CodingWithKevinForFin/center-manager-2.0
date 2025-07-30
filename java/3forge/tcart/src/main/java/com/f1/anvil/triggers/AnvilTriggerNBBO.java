package com.f1.anvil.triggers;

import java.util.logging.Logger;

import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.triggers.AmiAbstractTrigger;
import com.f1.anvil.utils.AnvilTaqPeriodQueuesManager;
import com.f1.utils.LH;
import com.f1.utils.structs.table.derived.StackFrame;

public class AnvilTriggerNBBO extends AmiAbstractTrigger {
	private static final Logger log = LH.get(AnvilTriggerNBBO.class);

	private AnvilServices service;
	private AnvilTriggerTimer nbboTrigger;
	private AnvilTaqPeriodQueuesManager taqQueueManager;
	private AnvilSchema schema;

	private AmiPreparedRow preparedNbboRow;

	@Override
	public void onStartup(AmiImdbSession session) {
		AmiImdb imdb = getImdb();
		this.service = imdb.getAmiServiceOrThrow(AnvilServices.SERVICE_NAME, AnvilServices.class);
		this.schema = this.service.getSchema();
		this.taqQueueManager = this.service.getTaqQueueManager();
		this.nbboTrigger = imdb.getAmiTimerOrThrow("timerTrigger", AnvilTriggerTimer.class);
		initSchema();
	}

	private void initSchema() {
		preparedNbboRow = schema.nbboTable.createAmiPreparedRow();
	}

	@Override
	public boolean onInserting(AmiTable table, AmiRow row, AmiImdbSession session, StackFrame sf) {
		preparedNbboRow.reset();
		if (row.getIsNull(schema.nbboTable_bidSize))
			preparedNbboRow.setDouble(schema.nbboTable_bidSize, 0);
		if (row.getIsNull(schema.nbboTable_askSize))
			preparedNbboRow.setDouble(schema.nbboTable_askSize, 0);
		schema.nbboTable.updateAmiRow(row.getAmiId(), preparedNbboRow, session);
		return true;
	}
	@Override
	public void onInserted(AmiTable table, AmiRow row, AmiImdbSession session, StackFrame sf) {
		nbboTrigger.resetTimers();
		final String symbol = row.getString(schema.nbboTable_sym);
		long time = row.getLong(schema.nbboTable_time);
		this.taqQueueManager.onNbbo(symbol, time, row);
		this.service.incrementStatsForNbbosReceived(1, session);
		return;
	}

}
