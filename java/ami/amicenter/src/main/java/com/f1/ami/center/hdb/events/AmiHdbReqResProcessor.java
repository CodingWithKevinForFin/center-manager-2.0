package com.f1.ami.center.hdb.events;

import java.util.Iterator;
import java.util.List;

import com.f1.ami.center.hdb.AmiHdbSqlFlowControl;
import com.f1.ami.center.hdb.AmiHdbSqlFlowControl_Insert;
import com.f1.ami.center.hdb.AmiHdbTable;
import com.f1.base.Column;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.MultiProcessor;
import com.f1.container.PartitionResolver;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.utils.sql.TableReturn;

public class AmiHdbReqResProcessor extends BasicProcessor<RequestMessage<AmiHdbRequest>, AmiHdbTableState>
		implements PartitionResolver<RequestMessage<AmiHdbRequest>>, MultiProcessor<RequestMessage<AmiHdbRequest>, AmiHdbTableState> {

	private ObjectGeneratorForClass<ResultMessage> resultGenerator;

	public AmiHdbReqResProcessor() {
		super((Class) RequestMessage.class, AmiHdbTableState.class);
		setPartitionResolver(this);

	}

	@Override
	public Object getPartitionId(RequestMessage<AmiHdbRequest> action) {
		return action.getAction().getSqlFlowControl().getTable().getF1PartitionId();
	}

	@Override
	public void processAction(RequestMessage<AmiHdbRequest> action, AmiHdbTableState state, ThreadScope threadScope) {
		processActionInner(action, state, threadScope);
		state.getTable().flushPersisted();
	}
	private void processActionInner(RequestMessage<AmiHdbRequest> action, AmiHdbTableState state, ThreadScope threadScope) {
		AmiHdbSqlFlowControl sfc = action.getAction().getSqlFlowControl();
		try {
			sfc.run();
		} catch (Throwable e) {
			sfc.processErrorResponse(e);
		}

		AmiHdbResponse r = nw(AmiHdbResponse.class);
		r.setSqlFlowControl(sfc);
		r.setOk(true);
		ResultMessage<AmiHdbResponse> result = resultGenerator.nw();
		result.setActionNoThrowable(r);
		reply(action, result, threadScope);
	}

	@Override
	public void start() {
		super.start();
		resultGenerator = getGenerator(ResultMessage.class);
	}

	@Override
	public void processActions(Iterator<RequestMessage<AmiHdbRequest>> actions, AmiHdbTableState state, ThreadScope threadScope) {
		List<RequestMessage<AmiHdbRequest>> inserts = state.getListBuffer();
		AmiHdbSqlFlowControl_Insert firstInsert = null;
		while (actions.hasNext()) {
			RequestMessage<AmiHdbRequest> action = actions.next();
			AmiHdbSqlFlowControl sqlFlowControl = action.getAction().getSqlFlowControl();
			if (sqlFlowControl instanceof AmiHdbSqlFlowControl_Insert) {
				AmiHdbSqlFlowControl_Insert sqlInsert = (AmiHdbSqlFlowControl_Insert) sqlFlowControl;
				if (firstInsert == null) {
					firstInsert = sqlInsert;
					inserts.add(action);
				} else if (firstInsert.isSameSchema(sqlInsert)) {
					inserts.add(action);
				} else {
					bulkProcessInserts(inserts, state, threadScope);
					firstInsert = sqlInsert;
					inserts.add(action);
				}
			} else {
				if (firstInsert != null) {
					bulkProcessInserts(inserts, state, threadScope);
					firstInsert = null;
				}
				processActionInner(action, state, threadScope);
			}

		}
		bulkProcessInserts(inserts, state, threadScope);
		state.getTable().flushPersisted();
	}

	private void bulkProcessInserts(List<RequestMessage<AmiHdbRequest>> inserts, AmiHdbTableState state, ThreadScope threadScope) {
		int size = inserts.size();
		if (size == 0)
			return;
		if (size == 1) {
			processAction(inserts.get(0), state, threadScope);
			inserts.clear();
			return;
		}
		AmiHdbSqlFlowControl_Insert first = (AmiHdbSqlFlowControl_Insert) inserts.get(0).getAction().getSqlFlowControl();
		AmiHdbTable historicalTable = first.getTable();
		List<Column> columns = first.getColumns();
		int[] suppliedColPositions = new int[columns.size()];
		for (int i = 0; i < suppliedColPositions.length; i++)
			suppliedColPositions[i] = columns.get(i).getLocation();

		int count = 0;
		for (int i = 0; i < size; i++) {
			count += ((AmiHdbSqlFlowControl_Insert) inserts.get(i).getAction().getSqlFlowControl()).getValues().length;
		}
		Object[][] rows2 = new Object[count][];
		int pos = 0;
		for (int i = 0; i < size; i++) {
			AmiHdbSqlFlowControl_Insert t = (AmiHdbSqlFlowControl_Insert) inserts.get(i).getAction().getSqlFlowControl();
			Object[][] vals = t.getValues();
			System.arraycopy(vals, 0, rows2, pos, vals.length);
			pos += vals.length;
		}
		historicalTable.addRows(suppliedColPositions, rows2);
		for (int i = 0; i < size; i++) {
			AmiHdbSqlFlowControl_Insert t = (AmiHdbSqlFlowControl_Insert) inserts.get(i).getAction().getSqlFlowControl();
			Object[][] vals = t.getValues();
			t.setTableReturn(new TableReturn(vals.length));
			AmiHdbResponse r = nw(AmiHdbResponse.class);
			r.setSqlFlowControl(t);
			r.setOk(true);
			ResultMessage<AmiHdbResponse> result = resultGenerator.nw();
			result.setActionNoThrowable(r);
			reply(inserts.get(i), result, threadScope);
		}
		inserts.clear();

	}
}
