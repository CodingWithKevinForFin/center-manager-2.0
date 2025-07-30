package com.f1.anvil.triggers;

import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiPreparedQuery;
import com.f1.ami.center.table.AmiPreparedQueryCompareClause;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.triggers.AmiAbstractTrigger;
import com.f1.utils.structs.table.derived.StackFrame;

public class AnvilTriggerOrdersBySymSide extends AmiAbstractTrigger {
	private AnvilServices service;
	private AnvilSchema schema;

	private AmiPreparedQuery secMasterQuery;
	private AmiPreparedQueryCompareClause secMasterQuery_symParam;
	private AmiPreparedRow preparedSymSideAgg;

	@Override
	public void onStartup(AmiImdbSession session) {
		this.service = getImdb().getAmiServiceOrThrow(AnvilServices.SERVICE_NAME, AnvilServices.class);
		this.schema = this.service.getSchema();
		initSchema();

	}

	private void initSchema() {
		secMasterQuery = schema.secMasterTable.createAmiPreparedQuery();
		secMasterQuery_symParam = secMasterQuery.addEq(schema.secMasterTable_sym);
		this.preparedSymSideAgg = schema.symSideAggTable.createAmiPreparedRow();
	}

	@Override
	public void onInserted(AmiTable table, AmiRow row, AmiImdbSession session, StackFrame sf) {
		AmiRow secMasterRow = searchSecMaster(row.getString(schema.symSideAggTable_sym));
		if (secMasterRow != null) {
			preparedSymSideAgg.reset();
			preparedSymSideAgg.setString(schema.symSideAggTable_sector, secMasterRow.getString(schema.secMasterTable_sector));
			preparedSymSideAgg.setString(schema.symSideAggTable_industry, secMasterRow.getString(schema.secMasterTable_industry));
			schema.symSideAggTable.updateAmiRow(row.getAmiId(), preparedSymSideAgg, session);
		}

	};
	//	@Override
	//	protected void onNewAggRow(AmiRow row, AmiPreparedRow preparedRowForInsert) {
	//		AmiRow secMasterRow = searchSecMaster(preparedRowForInsert.getString(schema.symSideAggTable_sym));
	//		if (secMasterRow != null) {
	//			preparedRowForInsert.setString(schema.symSideAggTable_sector, secMasterRow.getString(schema.secMasterTable_sector));
	//			preparedRowForInsert.setString(schema.symSideAggTable_industry, secMasterRow.getString(schema.secMasterTable_industry));
	//		}
	//		super.onNewAggRow(row, preparedRowForInsert);
	//	}

	private AmiRow searchSecMaster(String sym) {
		secMasterQuery_symParam.setValue(sym);
		return schema.secMasterTable.query(secMasterQuery);
	}

}
