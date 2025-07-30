package com.f1.anvil.triggers;

import java.util.ArrayList;
import java.util.List;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.center.procs.AmiAbstractStoredProc;
import com.f1.ami.center.procs.AmiStoredProcRequest;
import com.f1.ami.center.procs.AmiStoredProcResult;
import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiPreparedQuery;
import com.f1.ami.center.table.AmiPreparedQueryCompareClause;
import com.f1.ami.center.table.AmiRow;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.StackFrame;

public class AnvilCommandTriggerImpl extends AmiAbstractStoredProc {

	private AnvilServices service;
	private AnvilSchema schema;
	private AmiPreparedQuery childQuery;
	private AmiPreparedQueryCompareClause childQuery_id;
	private List<AmiRow> searchResults;
	private AmiImdb imdb;
	private List<AmiFactoryOption> argumentDefs = new ArrayList<AmiFactoryOption>();

	public AnvilCommandTriggerImpl() {
		this.argumentDefs.add(new AmiFactoryOption("username", String.class, true));
		this.argumentDefs.add(new AmiFactoryOption("hostIp", String.class, true));
		this.argumentDefs.add(new AmiFactoryOption("sessionId", String.class, true));
		this.argumentDefs.add(new AmiFactoryOption("ids", String.class, true));
		this.argumentDefs.add(new AmiFactoryOption("comment", String.class, true));
		this.argumentDefs.add(new AmiFactoryOption("change", Boolean.class, true));
		this.argumentDefs.add(new AmiFactoryOption("severity", Integer.class, true));
		this.argumentDefs.add(new AmiFactoryOption("assign", String.class, true));
	}

	@Override
	public List<AmiFactoryOption> getArguments() {
		// TODO Auto-generated method stub
		return this.argumentDefs;
	}
	@Override
	public void execute(AmiStoredProcRequest arguments, AmiStoredProcResult resultSink, AmiImdbSession session, StackFrame sf) throws Exception {
		// TODO Auto-generated method stub
		List<Object> args = arguments.getArguments();

		String username = (String) args.get(0);
		String hostIp = (String) args.get(1);
		String sessionId = (String) args.get(2);
		String amiIds = (String) args.get(3);
		String comment = (String) args.get(4);
		Boolean change = (Boolean) args.get(5);
		int severity = (Integer) args.get(6);
		String assign = (String) args.get(7);
		String amiIdsArray[] = SH.split(',', amiIds);
		BasicTable table = new BasicTable();
		table.addColumn(String.class, "text");
		table.addColumn(String.class, "amiIds");
		String text = "<div style=\"background:#061a2d;width:100%;height:100%;color:#c4c4c4;\"><center><h3>Closing <b>" + amiIdsArray.length + "</b> Alert(s)";

		table.getRows().addRow(text, amiIds);
		resultSink.setTables(CH.l((Table) table));
		//		List<String> amiIdsList = SH.splitToList(",", (String) arguments.get("ids"));
		//					long[] amiIds = AH.toArrayLong(OH.castAll(amiIdsArray, Long.class, true));
		List<AmiRow> rows = this.getChildAlertIds(amiIdsArray);
		//					String assign = (String) arguments.get("assign");
		if ("CLOSE".equals(assign)) {
			for (AmiRow amiRow : rows) {
				amiRow.setString(schema.childAlertsTable_comment, comment, session);
				amiRow.setString(schema.childAlertsTable_modifiedBy, username, session);
				amiRow.setString(schema.childAlertsTable_modifiedIp, hostIp, session);
				amiRow.setString(schema.childAlertsTable_modifiedSid, sessionId, session);
				if (change)
					amiRow.setLong(schema.childAlertsTable_severity, severity, session);
				//						schema.childAlertsTable.fireTriggerDelete(amiRow);
				schema.childAlertsTable.removeAmiRow(amiRow, session, sf);
			}
		} else {
			String assignTo = assign;
			for (AmiRow amiRow : rows) {
				//				schema.childAlertsTable.fireTriggerUpdating(amiRow, session);
				amiRow.setNull(schema.childAlertsTable_parentId, session); //ALLOW GROUP CHANGE
				amiRow.setString(schema.childAlertsTable_assignedTo, assignTo, session);
				amiRow.setString(schema.childAlertsTable_comment, comment, session);
				amiRow.setString(schema.childAlertsTable_modifiedBy, username, session);
				amiRow.setString(schema.childAlertsTable_modifiedIp, hostIp, session);
				amiRow.setString(schema.childAlertsTable_modifiedSid, sessionId, session);
				if (change)
					amiRow.setLong(schema.childAlertsTable_severity, severity, session);
				schema.childAlertsTable.fireTriggerUpdated(amiRow, session, sf);
			}
		}
		//				((AmiTableImpl) schema.parentAlertsTable).broadcastPendingChanges();
		//				((AmiTableImpl) schema.childAlertsTable).broadcastPendingChanges();
		//				((AmiImdbImpl) imdb).getState().sendPendingChangesToClients();
		//			return new AmiCommandResponse(AmiCommandResponse.STATUS_OKAY, null, "{FormPanel fp=session.getPanel(\"alertmgmt\");fp.minimize();}");
	}
	private List<AmiRow> getChildAlertIds(String[] amiIds) {
		searchResults.clear();
		int size = amiIds.length;
		for (int i = 0; i < size; i++) {
			long id = SH.parseLong(amiIds[i]);
			AmiRow row = schema.childAlertsTable.getAmiRowByAmiId(id);
			if (row == null) {
				row = schema.parentAlertsTable.getAmiRowByAmiId(id);
				childQuery_id.setValue(row.getLong(schema.parentAlerts_id));
				schema.childAlertsTable.query(childQuery, 10000000, searchResults);
			} else
				searchResults.add(row);
		}
		return searchResults;
	}

	private void initIndexes() {
		searchResults = new ArrayList<AmiRow>();
		this.imdb = this.getImdb();
		this.service = imdb.getAmiServiceOrThrow(AnvilServices.SERVICE_NAME, AnvilServices.class);
		this.schema = this.service.getSchema();
		childQuery = schema.childAlertsTable.createAmiPreparedQuery();
		childQuery_id = childQuery.addEq(schema.childAlertsTable_parentId);
	}
	@Override
	protected void onStartup(AmiImdbSession session) {
		initIndexes();
	}
}
