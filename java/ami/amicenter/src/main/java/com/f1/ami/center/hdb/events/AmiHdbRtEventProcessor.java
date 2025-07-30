package com.f1.ami.center.hdb.events;

import java.util.Iterator;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiRelayObjectMessage;
import com.f1.ami.center.hdb.AmiCenterAmiUtilsForHdb;
import com.f1.ami.center.hdb.AmiHdbTable;
import com.f1.container.MultiProcessor;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.utils.LH;
import com.f1.utils.structs.table.columnar.ColumnarTable;

public class AmiHdbRtEventProcessor extends BasicProcessor<AmiRelayObjectMessage, AmiHdbTableState> implements MultiProcessor<AmiRelayObjectMessage, AmiHdbTableState> {
	private static final Logger log = LH.get();

	public AmiHdbRtEventProcessor() {
		super(AmiRelayObjectMessage.class, AmiHdbTableState.class);
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void processActions(Iterator<AmiRelayObjectMessage> actions, AmiHdbTableState state, ThreadScope threadScope) throws Exception {
		AmiHdbTable table = state.getTable();
		if (table == null) {
			while (actions.hasNext())
				LH.warning(log, "Dropping event for missing historical table: ", state.getTableName(), " --> ", actions.next());
			return;
		}
		ColumnarTable tmptable = state.getTmpTable();
		StringBuilder tmpbuf = state.getTmpBuf();
		try {
			while (actions.hasNext())
				AmiCenterAmiUtilsForHdb.updateRow(actions.next().getParams(), table, tmptable, tmpbuf);
			table.addRows(tmptable);
			table.flushPersisted();
		} catch (Exception e) {
			LH.warning(log, "Error writing realtime stream to: " + table.getName(), " rows:\n " + tmptable, e);
		} finally {
			tmptable.clear();
			tmpbuf.setLength(0);
		}
	}

	@Override
	public void processAction(AmiRelayObjectMessage action, AmiHdbTableState state, ThreadScope threadScope) throws Exception {
		AmiHdbTable table = state.getTable();
		if (table == null) {
			LH.warning(log, "Dropping event for missing historical table: ", state.getTableName(), " --> ", action);
			return;
		}
		ColumnarTable tmptable = state.getTmpTable();
		StringBuilder tmpbuf = state.getTmpBuf();
		try {
			AmiCenterAmiUtilsForHdb.updateRow(action.getParams(), table, tmptable, tmpbuf);
			table.addRows(tmptable);
			table.flushPersisted();
		} catch (Exception e) {
			LH.warning(log, "Error writing realtime stream to: " + table.getName(), " rows:\n " + tmptable, e);
		} finally {
			tmptable.clear();
			tmpbuf.setLength(0);
		}
	}

}
