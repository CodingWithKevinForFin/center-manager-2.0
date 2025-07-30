package com.f1.ami.center.sysschema;

import java.util.List;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.center.table.AmiColumnWrapper;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiIndex;
import com.f1.ami.center.table.AmiIndexImpl;
import com.f1.ami.center.table.AmiPreparedQuery;
import com.f1.ami.center.table.AmiPreparedQueryCompareClause;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableDef;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.utils.CH;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchema_RELAY {

	final public AmiTableImpl table;
	final public AmiPreparedRow preparedRow;
	final public AmiColumnWrapper machineUid;
	final public AmiColumnWrapper processUid;
	final public AmiColumnWrapper startTime;
	final public AmiColumnWrapper serverPort;
	final public AmiColumnWrapper relayId;
	final public AmiColumnWrapper hostname;
	final public AmiColumnWrapper connectTime;
	final private AmiPreparedQuery queryRelayId;
	private AmiPreparedQueryCompareClause queryRelayId_relayId;

	public BasicMultiMap.Set<String, String> relayId2ProcessUid_Threadsafe = new BasicMultiMap.Set<String, String>();

	public AmiSchema_RELAY(AmiImdbImpl imdb, CalcFrameStack sf) {
		AmiTableDef def = new AmiTableDef(AmiTableUtils.DEFTYPE_SYSTEM, AmiConsts.TYPE_RELAY);

		this.machineUid = def.addColumn(AmiConsts.PARAM_RELAY_MACHINE_UID, AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.processUid = def.addColumn(AmiConsts.PARAM_RELAY_PROCESS_UID, AmiTable.TYPE_STRING);
		this.startTime = def.addColumn(AmiConsts.PARAM_RELAY_START_TIME, AmiTable.TYPE_UTC, AmiConsts.NONULL_OPTIONS);
		this.serverPort = def.addColumn(AmiConsts.PARAM_RELAY_SERVER_PORT, AmiTable.TYPE_INT);
		this.relayId = def.addColumn(AmiConsts.PARAM_RELAY_RELAY_ID, AmiTable.TYPE_STRING);
		this.hostname = def.addColumn(AmiConsts.PARAM_RELAY_HOSTNAME, AmiTable.TYPE_STRING, null);
		this.connectTime = def.addColumn(AmiConsts.PARAM_RELAY_CONNECTION_TIME, AmiTable.TYPE_UTC, AmiConsts.NONULL_OPTIONS);

		this.table = (AmiTableImpl) imdb.createTable(def, sf);
		this.preparedRow = this.table.createAmiPreparedRow();
		this.table.addIndex(AmiTableUtils.DEFTYPE_SYSTEM, "pk", CH.l(processUid.getName()), CH.l(AmiIndexImpl.TYPE_SORT), AmiIndex.CONSTRAINT_TYPE_PRIMARY, null, sf);
		this.table.addIndex(AmiTableUtils.DEFTYPE_SYSTEM, "relayId", CH.l(relayId.getName()), CH.l(AmiIndexImpl.TYPE_HASH), AmiIndex.CONSTRAINT_TYPE_NONE, null, sf);
		this.queryRelayId = this.table.createAmiPreparedQuery();
		this.queryRelayId_relayId = this.queryRelayId.addEq(this.relayId.getInner());
	}

	public AmiRow addRow(AmiRow existing, String machineUid, String processUid, long startTime, int serverPort, String relayId, String hostname, long connectTime,
			CalcFrameStack sf) {
		this.preparedRow.reset();
		this.preparedRow.setString(this.machineUid, machineUid);
		this.preparedRow.setString(this.processUid, processUid);
		this.preparedRow.setLong(this.startTime, startTime);
		this.preparedRow.setLong(this.serverPort, serverPort);
		this.preparedRow.setString(this.relayId, relayId);
		this.preparedRow.setString(this.hostname, hostname);
		this.preparedRow.setLong(this.connectTime, connectTime);
		AmiRow r;
		if (existing != null) {
			r = this.table.updateAmiRow(existing.getAmiId(), this.preparedRow, sf);
		} else
			r = this.table.insertAmiRow(this.preparedRow, sf);
		if (relayId != null && processUid != null) {
			synchronized (this) {
				com.f1.utils.structs.BasicMultiMap.Set<String, String> t = this.relayId2ProcessUid_Threadsafe.deepClone();
				t.putMulti(relayId, processUid);
				this.relayId2ProcessUid_Threadsafe = t;
			}
		}
		return r;
	}

	public void getRelayById(String relayId, List<AmiRow> sink) {
		this.queryRelayId_relayId.setValue(relayId);
		this.table.query(this.queryRelayId, 100, sink);
	}

	public void removeRow(long amiId, CalcFrameStack sf) {
		AmiRowImpl row = this.table.getAmiRowByAmiId(amiId);
		if (row == null)
			return;
		synchronized (this) {
			com.f1.utils.structs.BasicMultiMap.Set<String, String> t = this.relayId2ProcessUid_Threadsafe.deepClone();
			t.removeMulti(row.getString(AmiConsts.PARAM_RELAY_RELAY_ID), row.getString(AmiConsts.PARAM_RELAY_PROCESS_UID));
			this.relayId2ProcessUid_Threadsafe = t;
		}
		this.table.removeAmiRow(row, sf);
	}
}
