package com.vortex.eye.itinerary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.LoggingProgressCounter;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentDbColumn;
import com.f1.vortexcommon.msg.agent.VortexAgentDbDatabase;
import com.f1.vortexcommon.msg.agent.VortexAgentDbObject;
import com.f1.vortexcommon.msg.agent.VortexAgentDbPrivilege;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;
import com.f1.vortexcommon.msg.agent.VortexAgentDbTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentInspectDbRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentInspectDbResponse;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.VortexEyeEntity;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunDbInspectionRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunDbInspectionResponse;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;
import com.vortex.eye.state.VortexEyeMachineState;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeInspectDbSchemaItinerary extends AbstractVortexEyeItinerary<VortexEyeRunDbInspectionRequest> {
	private static final Logger log = LH.get(VortexEyeInspectDbSchemaItinerary.class);
	private static final int STEP1_UPDATE_DB_STATUS = 1;
	private static final int STEP2_INSPECTING_DB = 2;
	private static final int STEP3_INSERTING_DB_DATA = 3;
	private int step;
	private VortexEyeRunDbInspectionResponse response;
	private VortexAgentDbServer old;
	private VortexAgentDbServer nuw;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		response = getTools().nw(VortexEyeRunDbInspectionResponse.class);
		old = getState().getDbServer(getInitialRequest().getAction().getDbServerId()).clone();
		old.setStatus(VortexAgentDbServer.STATUS_INSPECTING);
		old.setInvokedBy(getInitialRequest().getAction().getInvokedBy());
		old.setNow(getTools().getNow());
		insertDbServerStatus(old, worker);
		step = STEP1_UPDATE_DB_STATUS;
		return STATUS_ACTIVE;
	}
	private void sendStatusToClients(VortexAgentDbServer dbs, VortexEyeItineraryWorker worker) {
		//VortexAgentDbServer tmp = dbs.clone();
		//tmp.removeValue(VortexAgentDbServer.PID_DATABASES);
		VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
		cmb.writeUpdate(dbs, VortexAgentDbServer.PID_STATUS, VortexAgentDbServer.PID_MESSAGE, VortexAgentDbServer.PID_NOW, VortexAgentDbServer.PID_INSPECTED_TIME,
				VortexAgentDbServer.PID_INVOKED_BY);
		worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));

	}

	private List<Tuple2<VortexAgentDbDatabase, VortexAgentDbDatabase>> changedDatabases = new ArrayList<Tuple2<VortexAgentDbDatabase, VortexAgentDbDatabase>>();
	private List<Tuple2<VortexAgentDbObject, VortexAgentDbObject>> changedObjects = new ArrayList<Tuple2<VortexAgentDbObject, VortexAgentDbObject>>();
	private List<Tuple2<VortexAgentDbTable, VortexAgentDbTable>> changedTables = new ArrayList<Tuple2<VortexAgentDbTable, VortexAgentDbTable>>();
	private List<Tuple2<VortexAgentDbColumn, VortexAgentDbColumn>> changedColumns = new ArrayList<Tuple2<VortexAgentDbColumn, VortexAgentDbColumn>>();
	//private List<VortexAgentDbDatabase> changedDatabases=new ArrayList<VortexAgentDbDatabase>();
	private long inspectTime;

	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		VortexEyeState state = getState();
		switch (step) {
			case STEP1_UPDATE_DB_STATUS: {
				DbResultMessage dbResult = (DbResultMessage) result.getAction();
				if (!dbResult.getOk()) {
					response.setMessage(dbResult.getMessage());
					return STATUS_COMPLETE;
				}
				sendStatusToClients(old, worker);
				VortexAgentInspectDbRequest agentRequest = getTools().nw(VortexAgentInspectDbRequest.class);
				agentRequest.setDbType(old.getDbType());
				agentRequest.setInvokedBy(getInitialRequest().getAction().getInvokedBy());
				agentRequest.setPassword(old.getPassword());
				agentRequest.setServerId(old.getId());
				agentRequest.setUrl(old.getUrl());
				VortexEyeMachineState machine = getState().getMachineByMuidNoThrow(old.getMachineUid());
				if (machine == null || machine.getAgentState() == null) {
					response.setMessage("Agent not running for dbserver: " + old.getUrl());
					return STATUS_COMPLETE;
				}
				worker.sendRequestToAgent(this, agentRequest, machine.getAgentState().getProcessUid());
				step = STEP2_INSPECTING_DB;
				return STATUS_ACTIVE;

			}
			case STEP2_INSPECTING_DB: {
				VortexAgentInspectDbResponse response = (VortexAgentInspectDbResponse) result.getAction();
				nuw = response.getDbServer();
				if (!response.getOk()) {
					this.response.setOk(false);
					this.response.setMessage(response.getMessage());
					if (nuw != null) {
						if (nuw.getStatus() == VortexAgentDbServer.STATUS_CONNECTION_ERROR || nuw.getStatus() == VortexAgentDbServer.STATUS_GENERAL_ERROR)
							old.setStatus(nuw.getStatus());
						else
							old.setStatus(VortexAgentDbServer.STATUS_GENERAL_ERROR);
						if (SH.is(nuw.getMessage())) {
							old.setMessage(nuw.getMessage());
						} else if (SH.is(this.response.getMessage())) {
							old.setMessage(this.response.getMessage());
						} else {
							old.setMessage("Unknown error");
						}
					} else {
						old.setStatus(VortexAgentDbServer.STATUS_GENERAL_ERROR);
						if (SH.is(this.response.getMessage()))
							old.setMessage(this.response.getMessage());
						else
							old.setMessage("Unknown error from agent");

					}
					old.setInvokedBy(getInitialRequest().getAction().getInvokedBy());
					old.setNow(getTools().getNow());
					sendStatusToClients(old, worker);
					return STATUS_COMPLETE;
				}
				nuw.setDescription(old.getDescription());
				nuw.setServerPort(old.getServerPort());
				nuw.setHints(old.getHints());
				long dbServerId = getInitialRequest().getAction().getDbServerId();
				VortexAgentDbServer existingDbServer = state.getDbServer(dbServerId);
				String machineUid = response.getMachineUid();
				if (machineUid == null) {
					LH.warning(log, "Unknown machineUid: ", machineUid);
					return STATUS_COMPLETE;
				}
				if (existingDbServer == null) {
					LH.warning(log, "unknown dbServerId: ", dbServerId);
					return STATUS_COMPLETE;
				}
				VortexEyeMachineState machine = getState().getMachineByMuidNoThrow(nuw.getMachineUid());
				if (machine == null) {
					LH.warning(log, "Unknown agent for machineUid: ", machineUid);
					return STATUS_COMPLETE;
				}
				if (response.getDbServer() == null) {
					LH.warning(log, "response missing db server: ", response);
					return STATUS_COMPLETE;
				}

				nuw.setId(dbServerId);
				nuw.setRevision(old.getRevision());
				applyDbServerId(nuw);

				//final List<VortexAgentDbDatabase> dbAdds = new ArrayList<VortexAgentDbDatabase>();
				//final List<VortexAgentDbDatabase> dbRemoves = new ArrayList<VortexAgentDbDatabase>();
				//final List<Tuple2<Long, VortexAgentDbTable>> tbRemoves = new ArrayList<Tuple2<Long, VortexAgentDbTable>>();
				//final List<Tuple2<Long, VortexAgentDbColumn>> clRemoves = new ArrayList<Tuple2<Long, VortexAgentDbColumn>>();
				//final List<Tuple2<Long, VortexAgentDbObject>> obRemoves = new ArrayList<Tuple2<Long, VortexAgentDbObject>>();
				//final List<Tuple2<Long, VortexAgentDbPrivilege>> pvRemoves = new ArrayList<Tuple2<Long, VortexAgentDbPrivilege>>();
				//List<String> dbNames = new ArrayList<String>();

				final Map<String, VortexAgentDbDatabase> existingDbs = existingDbServer.getDatabases();

				this.inspectTime = getTools().getNow();
				Map<String, VortexAgentDbDatabase> databases = processDatabases(dbServerId, existingDbs, nuw.getDatabases());

				int changesCnt = changedDatabases.size() + changedColumns.size() + changedTables.size() + changedObjects.size();
				if (changesCnt == 0) {
					nuw.setMessage("No changes");
				} else {
					nuw.setMessage("Processed " + changesCnt + " changes.");
				}
				nuw.setStatus(VortexAgentDbServer.STATUS_OKAY);
				nuw.setInvokedBy(getInitialRequest().getAction().getInvokedBy());
				nuw.setNow(inspectTime);
				nuw.setInspectedTime(inspectTime);
				VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
				writeTransition(cmb, removeChildren(old), removeChildren(nuw));
				if (changesCnt > 0) {
					for (Tuple2<VortexAgentDbDatabase, VortexAgentDbDatabase> i : changedDatabases) {
						writeTransition(cmb, removeChildren(i.getA()), removeChildren(i.getB()));
						insertDbDatabase(i.getB(), worker);
					}
					for (Tuple2<VortexAgentDbTable, VortexAgentDbTable> i : changedTables) {
						writeTransition(cmb, removeChildren(i.getA()), removeChildren(i.getB()));
						insertDbTable(i.getB(), worker);
					}
					for (Tuple2<VortexAgentDbColumn, VortexAgentDbColumn> i : changedColumns) {
						writeTransition(cmb, i.getA(), i.getB());
						insertDbColumn(i.getB(), worker);
					}
					for (Tuple2<VortexAgentDbObject, VortexAgentDbObject> i : changedObjects) {
						writeTransition(cmb, i.getA(), i.getB());
						insertDbObject(i.getB(), worker);
					}
					assertIds(nuw);
					nuw.setDatabases(databases);
				} else {
					assertIds(nuw);
					nuw.setDatabases(old.getDatabases());
				}
				insertDbServerStatus(nuw, worker);
				worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
				LoggingProgressCounter lpc = new LoggingProgressCounter(log, Level.INFO, "inserting files", 100, 2, TimeUnit.SECONDS);
				state.addDbServer(nuw);
				this.response.setOk(true);
				step = STEP3_INSERTING_DB_DATA;
				break;
			}
			case STEP3_INSERTING_DB_DATA: {
				//nothing to do but wait
				break;
			}
		}
		if (getPendingRequests().isEmpty()) {
			response.setOk(true);
			return STATUS_COMPLETE;
		}
		return STATUS_ACTIVE;
	}
	private void writeTransition(VortexEyeChangesMessageBuilder cmb, VortexEyeEntity old, VortexEyeEntity nuw) {
		if (old != null && nuw != null && nuw.getRevision()!=VortexEntity.REVISION_DONE)
			nuw.setRevision(old.getRevision() + 1);
		cmb.writeTransition(old, nuw);
	}
	public static void assertIds(VortexAgentDbServer dbServer) {
		long dbsid = dbServer.getId();
		for (Entry<String, VortexAgentDbDatabase> e : dbServer.getDatabases().entrySet()) {
			VortexAgentDbDatabase db = e.getValue();
			OH.assertEq(e.getKey(), db.getName());
			OH.assertEq(db.getDbServerId(), dbsid);
			long dbid = db.getId();
			OH.assertNe(dbid, 0L);
			for (VortexAgentDbPrivilege pv : db.getPrivileges()) {
				OH.assertEq(pv.getDbServerId(), dbsid);
				OH.assertEq(pv.getDatabaseId(), dbid);
				OH.assertNe(pv.getId(), 0L);
			}
			for (VortexAgentDbObject ob : db.getObjects()) {
				OH.assertEq(ob.getDbServerId(), dbsid);
				OH.assertEq(ob.getDatabaseId(), dbid);
				OH.assertNe(ob.getId(), 0L);
			}
			for (Entry<String, VortexAgentDbTable> e2 : db.getTables().entrySet()) {
				VortexAgentDbTable tb = e2.getValue();
				OH.assertEq(e2.getKey(), tb.getName());
				OH.assertEq(tb.getDbServerId(), dbsid);
				OH.assertEq(tb.getDatabaseId(), dbid);
				OH.assertNe(tb.getId(), 0L);
				for (Entry<String, VortexAgentDbColumn> e3 : tb.getColumns().entrySet()) {
					VortexAgentDbColumn cl = e3.getValue();
					OH.assertEq(e3.getKey(), cl.getName());
					OH.assertEq(cl.getDbServerId(), dbsid);
					OH.assertEq(cl.getTableId(), tb.getId());
					OH.assertNe(cl.getId(), 0L);
				}
			}
		}
	}
	private VortexEyeEntity removeChildren(VortexAgentDbServer t) {
		if (t == null)
			return null;
		VortexEyeEntity r = t.clone();
		r.removeValue(VortexAgentDbServer.PID_DATABASES);
		return r;
	}
	private VortexEyeEntity removeChildren(VortexAgentDbDatabase t) {
		if (t == null)
			return null;
		VortexEyeEntity r = t.clone();
		r.removeValue(VortexAgentDbDatabase.PID_OBJECTS);
		r.removeValue(VortexAgentDbDatabase.PID_PRIVILEGES);
		r.removeValue(VortexAgentDbDatabase.PID_TABLES);
		return r;
	}
	private VortexEyeEntity removeChildren(VortexAgentDbTable t) {
		if (t == null)
			return null;
		VortexEyeEntity r = t.clone();
		r.removeValue(VortexAgentDbTable.PID_COLUMNS);
		return r;
	}
	private Map<String, VortexAgentDbDatabase> processDatabases(long dbServerId, Map<String, VortexAgentDbDatabase> existing, Map<String, VortexAgentDbDatabase> actual) {
		Map<String, VortexAgentDbDatabase> r = new HashMap<String, VortexAgentDbDatabase>();
		for (Tuple2<VortexAgentDbDatabase, VortexAgentDbDatabase> curNuw : CH.join(existing, actual).values()) {
			final VortexAgentDbDatabase cur = curNuw.getA();
			VortexAgentDbDatabase nuw = curNuw.getB();
			if (nuw != null) {//TODO: temporary hack
				nuw.getPrivileges().clear();
				//nuw.getObjects().clear();
			}
			if (cur == null) {//add
				nuw.setId(getState().createNextId());
				nuw.setNow(inspectTime);
				this.changedDatabases.add(new Tuple2<VortexAgentDbDatabase, VortexAgentDbDatabase>(cur, nuw));
				nuw.setObjects(processObjects(nuw.getId(), Collections.EMPTY_LIST, nuw.getObjects()));
				nuw.setTables(processTables(nuw.getId(), Collections.EMPTY_MAP, nuw.getTables()));
				//nuw.setPrivileges(privileges)
				r.put(nuw.getName(), nuw);
			} else if (nuw == null) {//delete
				nuw = (VortexAgentDbDatabase) cur.clone();
				nuw.setRevision(VortexEntity.REVISION_DONE);
				this.changedDatabases.add(new Tuple2<VortexAgentDbDatabase, VortexAgentDbDatabase>(cur, nuw));
				processObjects(cur.getId(), cur.getObjects(), Collections.EMPTY_LIST);
				processTables(cur.getId(), cur.getTables(), Collections.EMPTY_MAP);
			} else { //update
				nuw.setId(cur.getId());
				nuw.setRevision(cur.getRevision());
				nuw.setObjects(processObjects(cur.getId(), cur.getObjects(), nuw.getObjects()));
				nuw.setTables(processTables(cur.getId(), cur.getTables(), nuw.getTables()));
				r.put(nuw.getName(), nuw);
			}
		}
		return r;
	}
	private List<VortexAgentDbObject> processObjects(long dbId, List<VortexAgentDbObject> existing, List<VortexAgentDbObject> actual) {
		List<VortexAgentDbObject> r = new ArrayList<VortexAgentDbObject>();

		for (Tuple2<VortexAgentDbObject, VortexAgentDbObject> curNuw : CH.join(buildKey(existing), buildKey(actual)).values()) {
			final VortexAgentDbObject cur = curNuw.getA();
			VortexAgentDbObject nuw = curNuw.getB();
			if (cur == null) {//add
				nuw.setId(getState().createNextId());
				nuw.setNow(inspectTime);
				this.changedObjects.add(new Tuple2<VortexAgentDbObject, VortexAgentDbObject>(cur, nuw));
				nuw.setDatabaseId(dbId);
				r.add(nuw);
			} else if (nuw == null) {//delete
				nuw = (VortexAgentDbObject) cur.clone();
				nuw.setRevision(VortexEntity.REVISION_DONE);
				this.changedObjects.add(new Tuple2<VortexAgentDbObject, VortexAgentDbObject>(cur, nuw));
			} else {//update
				nuw.setId(cur.getId());
				if (changed(cur, nuw)) {
					this.changedObjects.add(new Tuple2<VortexAgentDbObject, VortexAgentDbObject>(cur, nuw));
					nuw.setRevision(cur.getRevision() + 1);
				} else
					nuw.setRevision(cur.getRevision());
				nuw.setDatabaseId(dbId);
				nuw.setRevision(cur.getRevision());
				r.add(nuw);
			}
		}
		return r;
	}
	private Map<String, VortexAgentDbObject> buildKey(List<VortexAgentDbObject> vals) {
		Map<String, VortexAgentDbObject> r = new HashMap<String, VortexAgentDbObject>(vals.size());
		for (VortexAgentDbObject e : vals) {
			r.put(buildKey(e), e);
		}
		return r;
	}
	private String buildKey(VortexAgentDbObject e) {
		return SH.toString(e.getType()) + "." + e.getName();
	}
	private Map<String, VortexAgentDbTable> processTables(long dbId, Map<String, VortexAgentDbTable> existing, Map<String, VortexAgentDbTable> actual) {
		Map<String, VortexAgentDbTable> r = new HashMap<String, VortexAgentDbTable>();
		for (Tuple2<VortexAgentDbTable, VortexAgentDbTable> curNuw : CH.join(existing, actual).values()) {
			final VortexAgentDbTable cur = curNuw.getA();
			VortexAgentDbTable nuw = curNuw.getB();
			if (cur == null) {//add
				nuw.setId(getState().createNextId());
				nuw.setNow(inspectTime);
				this.changedTables.add(new Tuple2<VortexAgentDbTable, VortexAgentDbTable>(cur, nuw));
				nuw.setDatabaseId(dbId);
				nuw.setColumns(processColumns(nuw.getId(), Collections.EMPTY_MAP, nuw.getColumns()));
				r.put(nuw.getName(), nuw);
			} else if (nuw == null) {//delete
				nuw = (VortexAgentDbTable) cur.clone();
				nuw.setRevision(VortexEntity.REVISION_DONE);
				this.changedTables.add(new Tuple2<VortexAgentDbTable, VortexAgentDbTable>(cur, nuw));
				processColumns(cur.getId(), cur.getColumns(), Collections.EMPTY_MAP);
			} else {//update
				nuw.setId(cur.getId());
				if (changed(cur, nuw)) {
					this.changedTables.add(new Tuple2<VortexAgentDbTable, VortexAgentDbTable>(cur, nuw));
					nuw.setRevision(cur.getRevision() + 1);
				} else
					nuw.setRevision(cur.getRevision());
				nuw.setDatabaseId(dbId);
				nuw.setRevision(cur.getRevision());
				nuw.setColumns(processColumns(cur.getId(), cur.getColumns(), nuw.getColumns()));
				r.put(nuw.getName(), nuw);
			}
		}
		return r;
	}
	private Map<String, VortexAgentDbColumn> processColumns(long tableId, Map<String, VortexAgentDbColumn> existing, Map<String, VortexAgentDbColumn> actual) {
		Map<String, VortexAgentDbColumn> r = new HashMap<String, VortexAgentDbColumn>();
		for (Tuple2<VortexAgentDbColumn, VortexAgentDbColumn> curNuw : CH.join(existing, actual).values()) {
			final VortexAgentDbColumn cur = curNuw.getA();
			VortexAgentDbColumn nuw = curNuw.getB();
			if (cur == null) {//add
				nuw.setId(getState().createNextId());
				nuw.setNow(inspectTime);
				this.changedColumns.add(new Tuple2<VortexAgentDbColumn, VortexAgentDbColumn>(cur, nuw));
				nuw.setTableId(tableId);
				r.put(nuw.getName(), nuw);
			} else if (nuw == null) {//delete
				nuw = (VortexAgentDbColumn) cur.clone();
				nuw.setRevision(VortexEntity.REVISION_DONE);
				this.changedColumns.add(new Tuple2<VortexAgentDbColumn, VortexAgentDbColumn>(cur, nuw));
			} else {//update
				nuw.setId(cur.getId());
				if (changed(cur, nuw)) {
					this.changedColumns.add(new Tuple2<VortexAgentDbColumn, VortexAgentDbColumn>(cur, nuw));
					nuw.setRevision(cur.getRevision() + 1);
				} else
					nuw.setRevision(cur.getRevision());
				nuw.setTableId(tableId);
				nuw.setRevision(cur.getRevision());
				r.put(nuw.getName(), nuw);
			}
		}
		return r;
	}

	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {

		//cheating a bit, sending everything as an update
		if (response.getOk()) {
			VortexEyeChangesMessageBuilder cmb = getState().getChangesMessageBuilder();
			cmb.writeUpdate(nuw, nuw.askSchema().askPids());
			worker.sendToClients(this, cmb.popToChangesMsg(getState().nextSequenceNumber()));
		}
		return response;
	}

	public static void applyDbServerId(VortexAgentDbServer existingDbServer) {
		final long dbServerId = existingDbServer.getId();
		for (VortexAgentDbDatabase db : CH.values(existingDbServer.getDatabases())) {
			db.setDbServerId(dbServerId);
			for (VortexAgentDbTable table : CH.values(db.getTables())) {
				table.setDbServerId(dbServerId);
				for (VortexAgentDbColumn column : CH.values(table.getColumns())) {
					column.setDbServerId(dbServerId);
				}
			}
			for (VortexAgentDbObject object : db.getObjects())
				object.setDbServerId(dbServerId);
			for (VortexAgentDbPrivilege privilege : db.getPrivileges())
				privilege.setDbServerId(dbServerId);
		}

	}

	private VortexAgentDbPrivilege findPrivilege(List<VortexAgentDbPrivilege> privileges, String user, String tableName) {
		for (VortexAgentDbPrivilege pv : privileges)
			if (OH.eq(pv.getUser(), user) && OH.eq(pv.getTableName(), tableName)) {
				privileges.remove(pv);
				return pv;
			}
		return null;
	}

	private VortexAgentDbObject findObject(List<VortexAgentDbObject> objects, byte type, String name) {
		for (VortexAgentDbObject ob : objects)
			if (OH.eq(ob.getType(), type) && OH.eq(ob.getName(), name)) {
				objects.remove(ob);
				return ob;
			}
		return null;
	}

	private boolean changed(VortexAgentDbPrivilege existingPv, VortexAgentDbPrivilege pv) {
		if (existingPv.getType() != pv.getType())
			return true;
		return false;
	}

	private boolean changed(VortexAgentDbObject existingObject, VortexAgentDbObject ob) {
		if (OH.ne(existingObject.getDefinition(), ob.getDefinition()))
			return true;
		return false;
	}

	private boolean changed(VortexAgentDbColumn existingColumn, VortexAgentDbColumn cl) {
		if (OH.ne(existingColumn.getMask(), cl.getMask()))
			return true;
		if (OH.ne(existingColumn.getDescription(), cl.getDescription()))
			return true;
		if (OH.ne(existingColumn.getComments(), cl.getComments()))
			return true;
		if (OH.ne(existingColumn.getPermissibleValues(), cl.getPermissibleValues()))
			return true;
		if (OH.ne(existingColumn.getPosition(), cl.getPosition()))
			return true;
		if (OH.ne(existingColumn.getPrecision(), cl.getPrecision()))
			return true;
		if (OH.ne(existingColumn.getScale(), cl.getScale()))
			return true;
		if (OH.ne(existingColumn.getSize(), cl.getSize()))
			return true;
		if (OH.ne(existingColumn.getType(), cl.getType()))
			return true;
		return false;
	}

	private boolean changed(VortexAgentDbTable existingTable, VortexAgentDbTable tb) {
		if (OH.ne(existingTable.getDescription(), tb.getDescription()))
			return true;
		if (OH.ne(existingTable.getComments(), tb.getComments()))
			return true;
		return false;
	}

	public void insertDbDatabase(VortexAgentDbDatabase db, VortexEyeItineraryWorker worker) {
		boolean active = db.getRevision() != VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("db_server_id", db.getDbServerId());
		params.put("id", db.getId());
		params.put("revision", db.getRevision());
		params.put("now", db.getNow());
		params.put("name", db.getName());
		execute("insert_db_database", params, worker);
	}

	public void insertDbServerStatus(VortexAgentDbServer db, VortexEyeItineraryWorker worker) {
		boolean active = db.getRevision() != VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("id", db.getId());
		params.put("now", db.getNow());
		params.put("status", db.getStatus());
		params.put("message", db.getMessage());
		params.put("inspected_time", db.getInspectedTime());
		execute("insert_db_server_status", params, worker);
	}

	public void insertDbTable(VortexAgentDbTable table, VortexEyeItineraryWorker worker) {
		boolean active = table.getRevision() != VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("id", table.getId());
		params.put("revision", table.getRevision());
		params.put("now", table.getNow());
		params.put("name", table.getName());
		params.put("db_database_id", table.getDatabaseId());
		params.put("description", table.getDescription());
		params.put("comments", table.getComments());
		params.put("create_time", table.getCreateTime());
		execute("insert_db_table", params, worker);
	}

	public void insertDbColumn(VortexAgentDbColumn column, VortexEyeItineraryWorker worker) {
		boolean active = column.getRevision() != VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("id", column.getId());
		params.put("revision", column.getRevision());
		params.put("now", column.getNow());
		params.put("name", column.getName());
		params.put("db_table_id", column.getTableId());
		params.put("description", column.getDescription());
		params.put("comments", column.getComments());
		params.put("mask", column.getMask());
		params.put("size", column.getSize());
		params.put("numeric_precision", column.getPrecision());
		params.put("numeric_scale", column.getScale());
		params.put("permissible_values", column.getPermissibleValues());
		params.put("position", column.getPosition());
		params.put("data_type", column.getType());
		execute("insert_db_column", params, worker);
	}

	public void insertDbObject(VortexAgentDbObject object, VortexEyeItineraryWorker worker) {
		boolean active = object.getRevision() != VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("id", object.getId());
		params.put("revision", object.getRevision());
		params.put("now", object.getNow());
		params.put("name", object.getName());
		params.put("db_database_id", object.getDatabaseId());
		params.put("definition", object.getDefinition());
		params.put("object_type", object.getType());
		execute("insert_db_object", params, worker);
	}

	public void insertDbPrivilege(VortexAgentDbPrivilege priv, VortexEyeItineraryWorker worker) {
		boolean active = priv.getRevision() != VortexAgentEntity.REVISION_DONE;
		final Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("active", active);
		params.put("id", priv.getId());
		params.put("db_database_id", priv.getDatabaseId());
		params.put("revision", priv.getRevision());
		params.put("now", priv.getNow());
		params.put("user_name", priv.getUser());
		params.put("table_name", priv.getTableName());
		params.put("privilege_type", priv.getType());
		execute("insert_db_privilege", params, worker);
	}
	private void execute(String sqlId, Map<Object, Object> params, VortexEyeItineraryWorker worker) {
		DbRequestMessage msg = getTools().nw(DbRequestMessage.class);
		msg.setId(sqlId);
		msg.setParams(params);
		worker.sendToDb(this, msg);
	}
	@Override
	protected void populateAuditEvent(VortexEyeRunDbInspectionRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_RUN_INSPECT_DATASERVER);
		sink.getParams().put("DSID", SH.toString(action.getDbServerId()));
	}
}
