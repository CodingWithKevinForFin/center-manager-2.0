package com.f1.ami.amicommon.ds;

import java.util.ArrayList;
import java.util.List;

import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsTrackerEvent;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.BasicTable;

public class AmiDatasourceTrackerImpl implements AmiDatasourceTracker {

	private ContainerTools tools;
	private List<AmiCenterQueryDsTrackerEvent> trackedEvents;
	private int sampleLimitSize = 10;

	public AmiDatasourceTrackerImpl(ContainerTools tools) {
		this.tools = tools;
		this.trackedEvents = new ArrayList<AmiCenterQueryDsTrackerEvent>();
	}

	@Override
	public void onQuery(String query) {
		if (tools == null)
			return;
		trackedEvents.add(newTrackedEvent(AmiCenterQueryDsTrackerEvent.TYPE_QUERY, query));
	}

	private AmiCenterQueryDsTrackerEvent newTrackedEvent(byte type, String event) {
		final AmiCenterQueryDsTrackerEvent r = tools.nw(AmiCenterQueryDsTrackerEvent.class);
		long timestamp = tools.getNow();
		r.setType(type);
		r.setTimestamp(timestamp);
		r.setString(event);
		return r;
	}

	@Override
	public void onError(String string) {
		if (tools == null)
			return;
		trackedEvents.add(newTrackedEvent(AmiCenterQueryDsTrackerEvent.TYPE_ERROR, string));
	}

	@Override
	public void onQueryResult(String select, Table table) {
		if (tools == null)
			return;
		AmiCenterQueryDsTrackerEvent last = CH.last(trackedEvents);
		if (last != null && last.getType() == AmiCenterQueryDsTrackerEvent.TYPE_QUERY && OH.eq(select, last.getString())) {
			last.setDuration(tools.getNow() - last.getTimestamp());
		} else {
			last = newTrackedEvent(AmiCenterQueryDsTrackerEvent.TYPE_QUERY, select);
			trackedEvents.add(last);
			last.setDuration(-1);
		}
		if (table != null) {
			last.setResultTableSize(table.getSize());
			last.setResultTableName(table.getTitle());
			BasicTable sampleTable = new BasicTable(table.getColumns(), sampleLimitSize);
			for (int i = 0, l = Math.min(sampleLimitSize, table.getSize()); i < l; i++)
				sampleTable.getRows().addRow(table.getRows().get(i).getValuesCloned());
			last.setResultTableSample(sampleTable);
		} else
			last.setResultTableSize(-1);
	}

	@Override
	public List<AmiCenterQueryDsTrackerEvent> createTrackedEvents() {
		return trackedEvents;
	}

	@Override
	public String getMessage() {
		return null;
	}

	@Override
	public void onStart(String query) {
		if (tools == null)
			return;
		trackedEvents.add(newTrackedEvent(AmiCenterQueryDsTrackerEvent.TYPE_QUERY_START, query));
	}
	@Override
	public void onStep(String step, String msg) {
		if (tools == null)
			return;
		trackedEvents.add(newTrackedEvent(AmiCenterQueryDsTrackerEvent.TYPE_QUERY_STEP, step + " ==> " + msg));

	}

	@Override
	public void onEnd(Object result) {
		if (tools == null)
			return;
		trackedEvents.add(newTrackedEvent(AmiCenterQueryDsTrackerEvent.TYPE_QUERY_END, ""));

	}

	@Override
	public void onEndWithError(Exception e) {
		if (tools == null)
			return;
		if (e instanceof ExpressionParserException)
			trackedEvents.add(newTrackedEvent(AmiCenterQueryDsTrackerEvent.TYPE_QUERY_END_ERROR, ((ExpressionParserException) e).toLegibleStringException(false)));
		else
			trackedEvents.add(newTrackedEvent(AmiCenterQueryDsTrackerEvent.TYPE_QUERY_END_ERROR, SH.printStackTrace("", "", e)));

	}

}
