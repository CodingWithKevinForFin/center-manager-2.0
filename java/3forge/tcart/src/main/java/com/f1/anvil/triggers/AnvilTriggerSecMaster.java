package com.f1.anvil.triggers;

import java.util.ArrayList;
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

public class AnvilTriggerSecMaster extends AmiAbstractTrigger {

	private static final String SELL = "S";
	private static final String BUY = "B";

	private AnvilServices service;
	private AnvilSchema schema;

	private ArrayList<AmiRow> searchResultRows;
	private AmiPreparedRow symTablePreparedRow;
	private AmiPreparedRow preparedSecMasterRow;
	private AmiPreparedRow preparedOrderRow;
	private AmiPreparedRow preparedExecutionRow;
	private AmiPreparedRow preparedSymSideAggRow;

	private AmiPreparedQuery symTable_query;
	private AmiPreparedQueryCompareClause symTableIndexSymParam;

	private AmiPreparedQuery orderTableQuery;
	private AmiPreparedQueryCompareClause orderTableIndex;

	private AmiPreparedQuery exTableQuery;
	private AmiPreparedQueryCompareClause exTableIndex;

	private AmiPreparedQuery symSideAggQuery;
	private AmiPreparedQueryCompareClause symSideAggQuery_sym;
	private AmiPreparedQueryCompareClause symSideAggQuery_side;

	private static final Logger log = LH.get(AnvilTriggerSecMaster.class);

	@Override
	public void onStartup(AmiImdbSession session) {
		this.service = getImdb().getAmiServiceOrThrow(AnvilServices.SERVICE_NAME, AnvilServices.class);
		this.schema = this.service.getSchema();
		initSchema();
	}
	private void initSchema() {
		this.symTablePreparedRow = schema.symTable.createAmiPreparedRow();
		this.preparedSecMasterRow = schema.secMasterTable.createAmiPreparedRow();
		this.preparedOrderRow = schema.oTable.createAmiPreparedRow();
		this.preparedExecutionRow = schema.exTable.createAmiPreparedRow();
		this.preparedSymSideAggRow = schema.symSideAggTable.createAmiPreparedRow();
		this.searchResultRows = new ArrayList<AmiRow>();

		this.symTable_query = schema.symTable.createAmiPreparedQuery();
		this.symTableIndexSymParam = symTable_query.addCompare(schema.symTable_sym, AmiPreparedQueryCompareClause.EQ);

		this.orderTableQuery = schema.oTable.createAmiPreparedQuery();
		this.orderTableIndex = orderTableQuery.addCompare(schema.oTable_sym, AmiPreparedQueryCompareClause.EQ);

		this.symSideAggQuery = schema.symSideAggTable.createAmiPreparedQuery();
		this.symSideAggQuery_sym = symSideAggQuery.addEq(schema.symSideAggTable_sym);
		this.symSideAggQuery_side = symSideAggQuery.addEq(schema.symSideAggTable_side);

		this.exTableQuery = this.schema.exTable.createAmiPreparedQuery();
		this.exTableIndex = exTableQuery.addCompare(schema.exTable_sym, AmiPreparedQueryCompareClause.EQ);

	}

	@Override
	public boolean onInserting(AmiTable table, AmiRow row, AmiImdbSession session, StackFrame sf) {
		double mktCap = row.getDouble(schema.secMasterTable_mktCap);
		double prevClose = row.getDouble(schema.secMasterTable_prevClose);
		double adv = mktCap / prevClose * 0.1;
		row.setDouble(schema.secMasterTable_adv, adv, session);
		//		preparedSecMasterRow.reset();
		//		preparedSecMasterRow.setDouble(schema.secMasterTable_adv, adv);
		//		schema.secMasterTable.updateAmiRow(row.getAmiId(), preparedSecMasterRow);
		return true;
	}

	@Override
	public void onInserted(AmiTable table, AmiRow row, AmiImdbSession session, StackFrame sf) {
		String sym = row.getString(schema.secMasterTable_sym);
		AmiRow symRow = searchSymbolTableForMatchingSymbol(sym);
		double prevClose = row.getDouble(schema.secMasterTable_prevClose);
		String sector = row.getString(schema.secMasterTable_sector);
		String industry = row.getString(schema.secMasterTable_industry);
		if (symRow == null) {
			symTablePreparedRow.reset();
			symTablePreparedRow.setString(schema.symTable_sym, sym);
			symTablePreparedRow.setString(schema.symTable_name, row.getString(schema.secMasterTable_name));
			symTablePreparedRow.setString(schema.symTable_sector, sector);
			symTablePreparedRow.setString(schema.symTable_industry, industry);
			symTablePreparedRow.setDouble(schema.symTable_mktCap, row.getDouble(schema.secMasterTable_mktCap));
			symTablePreparedRow.setDouble(schema.symTable_prevClose, prevClose);
			schema.symTable.insertAmiRow(symTablePreparedRow, session);
		} else {
			symTablePreparedRow.reset();
			symTablePreparedRow.setString(schema.symTable_name, row.getString(schema.secMasterTable_name));
			symTablePreparedRow.setString(schema.symTable_sector, sector);
			symTablePreparedRow.setString(schema.symTable_industry, industry);
			symTablePreparedRow.setDouble(schema.symTable_mktCap, row.getDouble(schema.secMasterTable_mktCap));
			symTablePreparedRow.setDouble(schema.symTable_prevClose, prevClose);
			schema.symTable.updateAmiRow(row.getAmiId(), symTablePreparedRow, session);
		}
		AmiRow aggRow = searchSymSideTable(sym, BUY);
		if (aggRow != null) {

			preparedSymSideAggRow.reset();
			preparedSymSideAggRow.setString(schema.symSideAggTable_sector, sector);
			preparedSymSideAggRow.setString(schema.symSideAggTable_industry, industry);
			schema.symSideAggTable.updateAmiRow(aggRow.getAmiId(), preparedSymSideAggRow, session);
		}
		aggRow = searchSymSideTable(sym, SELL);
		if (aggRow != null) {
			preparedSymSideAggRow.reset();
			preparedSymSideAggRow.setString(schema.symSideAggTable_sector, sector);
			preparedSymSideAggRow.setString(schema.symSideAggTable_industry, industry);
			schema.symSideAggTable.updateAmiRow(aggRow.getAmiId(), preparedSymSideAggRow, session);
		}
		searchOrderTableForMatchingSymbol(sym);
		if (searchResultRows.size() > 0) {
			for (int i = 0, s = searchResultRows.size(); i < s; i++) {
				AmiRow orderRow = searchResultRows.get(i);
				//				schema.oTable.fireTriggerUpdating(orderRow);
				preparedOrderRow.reset();
				preparedOrderRow.setDouble(schema.oTable_prevClose, prevClose);
				preparedOrderRow.setString(schema.oTable_industry, industry);
				preparedOrderRow.setString(schema.oTable_sector, sector);
				schema.oTable.updateAmiRow(orderRow.getAmiId(), preparedOrderRow, session);
				//				schema.oTable.fireTriggerUpdated(orderRow);
			}
		}
		searchExTableForMatchingSymbol(sym);
		if (searchResultRows.size() > 0) {
			for (int i = 0, s = searchResultRows.size(); i < s; i++) {
				AmiRow exRow = searchResultRows.get(i);
				//				schema.exTable.fireTriggerUpdating(exRow);
				preparedExecutionRow.reset();
				preparedExecutionRow.setDouble(schema.exTable_prevClose, prevClose);
				preparedExecutionRow.setString(schema.exTable_industry, industry);
				preparedExecutionRow.setString(schema.exTable_sector, sector);
				schema.exTable.updateAmiRow(exRow.getAmiId(), preparedExecutionRow, session);
				//				schema.exTable.fireTriggerUpdated(exRow);
			}
		}
	}
	private void searchExTableForMatchingSymbol(String sym) {
		searchResultRows.clear();
		exTableIndex.setValue(sym);
		schema.exTable.query(exTableQuery, 50000000, searchResultRows);
	}

	private AmiRow searchSymSideTable(String sym, String side) {
		symSideAggQuery_sym.setValue(sym);
		symSideAggQuery_side.setValue(side);
		return schema.symSideAggTable.query(symSideAggQuery);
	}

	private AmiRow searchSymbolTableForMatchingSymbol(String sym) {
		symTableIndexSymParam.setValue(sym);
		return schema.symTable.query(symTable_query);
	}
	private void searchOrderTableForMatchingSymbol(String sym) {
		searchResultRows.clear();
		orderTableIndex.setValue(sym);
		schema.oTable.query(orderTableQuery, 100000000, searchResultRows);
	}
}
