package com.f1.anvil.triggers;

import java.util.ArrayList;
import java.util.List;

import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiPreparedQuery;
import com.f1.ami.center.table.AmiPreparedQueryCompareClause;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.triggers.impl.AmiCenterTableAggTrigger;
import com.f1.utils.MH;

public class AnvilTriggerParentAlertsAgg extends AmiCenterTableAggTrigger {

	private AmiPreparedQuery query;
	private AnvilServices service;
	private AnvilSchema schema;
	private List<AmiRow> searchRows;
	private long nextAlertId = 10000;
	private AmiPreparedQueryCompareClause queryParam_type;
	private AmiPreparedQueryCompareClause queryParam_details;
	private AmiPreparedQueryCompareClause queryParam_assignedTo;
	private AmiPreparedQuery idQuery;
	private AmiPreparedQueryCompareClause idQueryParam_id;

	@Override
	public void onStartup(AmiImdbSession session) {
		this.service = getImdb().getAmiServiceOrThrow(AnvilServices.SERVICE_NAME, AnvilServices.class);
		this.schema = this.service.getSchema();
		this.searchRows = new ArrayList<AmiRow>();
		initIndexes();
	}
	private void initIndexes() {
		this.query = getTargetTable().createAmiPreparedQuery();
		this.queryParam_type = query.addCompare(schema.parentAlerts_type, AmiPreparedQueryCompareClause.EQ);
		this.queryParam_details = query.addCompare(schema.parentAlerts_details, AmiPreparedQueryCompareClause.EQ);
		this.queryParam_assignedTo = query.addCompare(schema.parentAlerts_assignedTo, AmiPreparedQueryCompareClause.EQ);

		this.idQuery = getTargetTable().createAmiPreparedQuery();
		this.idQueryParam_id = idQuery.addCompare(schema.parentAlerts_id, AmiPreparedQueryCompareClause.EQ);
	}

	@Override
	protected void onNewAggRow(AmiRow row, AmiPreparedRow preparedRowForInsert, AmiImdbSession session) {
		long alertId = nextAlertId++;
		row.setLong(schema.childAlertsTable_parentId, alertId, session);
		preparedRowForInsert.setLong(schema.parentAlerts_id, alertId);
		long time = row.getLong(schema.childAlertsTable_time);
		preparedRowForInsert.setLong(schema.parentAlerts_minTime, time);
		preparedRowForInsert.setLong(schema.parentAlerts_maxTime, time);
		super.onNewAggRow(row, preparedRowForInsert, session);
	}

	@Override
	protected void onAddToExistingAggRow(AmiRow row, AmiRow existing, AmiImdbSession session) {
		long alertId = existing.getLong(schema.parentAlerts_id);
		row.setLong(schema.childAlertsTable_parentId, alertId, session);
		long time = row.getLong(schema.childAlertsTable_time);

		long minTime = existing.getLong(schema.parentAlerts_minTime);
		existing.setLong(schema.parentAlerts_minTime, Math.min(minTime, time), session);

		long maxTime = existing.getLong(schema.parentAlerts_maxTime);
		existing.setLong(schema.parentAlerts_maxTime, Math.max(maxTime, time), session);

		super.onAddToExistingAggRow(row, existing, session);
	}
	@Override
	protected AmiRow getTarget(AmiRow row) {
		if (!row.getIsNull(schema.childAlertsTable_parentId)) {
			long parentId = row.getLong(schema.childAlertsTable_parentId);
			idQueryParam_id.setValue(parentId);
			return schema.parentAlertsTable.query(idQuery);
		} else {

			String type = row.getString(schema.childAlertsTable_type);
			boolean ignoreTime = "T+1".equals(type) || "T+5".equals(type);
			long time = row.getLong(schema.childAlertsTable_time);
			queryParam_type.setValue(row.getString(schema.childAlertsTable_type));
			queryParam_assignedTo.setValue(row.getString(schema.childAlertsTable_assignedTo));
			queryParam_details.setValue(row.getString(schema.childAlertsTable_details));
			searchRows.clear();
			getTargetTable().query(query, 1000000, searchRows);
			int size = searchRows.size();
			if (size > 0) {
				AmiRow parentRow;
				for (int i = 0; i < size; i++) {
					parentRow = searchRows.get(i);
					if (ignoreTime)
						return parentRow;
					else if (MH.between(time, parentRow.getLong(schema.parentAlerts_minTime) - 60000, parentRow.getLong(schema.parentAlerts_maxTime) + 60000, 0))
						return parentRow;
				}
				return null;
			} else
				return null;
		}
	}
}
