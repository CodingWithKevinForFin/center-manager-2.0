package com.f1.anvil.triggers;

import java.util.logging.Logger;

import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.triggers.AmiAbstractTrigger;
import com.f1.utils.LH;
import com.f1.utils.structs.table.derived.StackFrame;

public class AnvilTriggerAdminCommand extends AmiAbstractTrigger {

	//	private AmiTable orderTable;
	//	private AmiTable exTable;
	//	private AmiTable nbboTable;
	//	private AmiTable tradesTable;
	//	private AmiTable symTable;
	//	private AmiTable adminCommandTable;
	//	private AmiColumn statsTable_command;
	private String command;
	private AnvilServices service;
	private AnvilSchema schema;
	private static final Logger log = LH.get(AnvilTriggerAdminCommand.class);

	@Override
	public void onStartup(AmiImdbSession session) {
		this.service = getImdb().getAmiServiceOrThrow(AnvilServices.SERVICE_NAME, AnvilServices.class);
		this.schema = this.service.getSchema();
		//		this.orderTable = imdb.getAmiTable("Orders");
		//		this.exTable = imdb.getAmiTable("Execution");
		//		this.nbboTable = imdb.getAmiTable("NBBO");
		//		this.tradesTable = imdb.getAmiTable("Trade");
		//		this.symTable = imdb.getAmiTable("Symbol");
		//		this.adminCommandTable = imdb.getAmiTable("AdminCommand");
		//		this.statsTable_command = adminCommandTable.getColumn("command");

	}

	@Override
	public void onInserted(AmiTable table, AmiRow row, AmiImdbSession session, StackFrame sf) {
		command = row.getString(schema.statsTable_command);
		if (command.equals("printAll")) {
			LH.info(log, "ORDERS: " + schema.oTable.getRowsCount() + " EXECUTIONS: " + schema.exTable.getRowsCount() + " TRADES: " + schema.tradeTable.getRowsCount() + " SYMBOL: "
					+ schema.symTable.getRowsCount() + " NBBO: " + schema.nbboTable.getRowsCount());
			LH.info(log, schema.oTable.toString());
			LH.info(log, schema.exTable.toString());
			LH.info(log, schema.tradeTable.toString());
			LH.info(log, schema.symTable.toString());
			LH.info(log, schema.nbboTable.toString());

		}
	}

	@Override
	public void onUpdated(AmiTable table, AmiRow row, AmiImdbSession session, StackFrame sf) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onDeleting(AmiTable table, AmiRow row, AmiImdbSession session, StackFrame sf) {
		return true;
	}

}
