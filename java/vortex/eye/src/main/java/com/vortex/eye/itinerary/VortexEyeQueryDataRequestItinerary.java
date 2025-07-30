package com.vortex.eye.itinerary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.povo.db.DbRequestMessage;
import com.f1.povo.db.DbResultMessage;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.VH;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.Tuple2;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentFileSystem;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.agent.VortexAgentProcess;
import com.f1.vortexcommon.msg.eye.VortexBuildResult;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.VortexVaultEntry;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataResponse;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.messages.VortexVaultRequest;
import com.vortex.eye.messages.VortexVaultResponse;
import com.vortex.eye.processors.VortexEyeItineraryProcessor;

public class VortexEyeQueryDataRequestItinerary extends AbstractVortexEyeItinerary<VortexEyeQueryDataRequest> {

	private VortexEyeQueryDataResponse r;

	@Override
	public byte startJourney(VortexEyeItineraryWorker worker) {
		VortexEyeQueryDataRequest request = getInitialRequest().getAction();
		r = getState().nw(VortexEyeQueryDataResponse.class);
		List<VortexAgentEntity> rows = new ArrayList<VortexAgentEntity>();
		if (VortexEyeUtils.getVortexDb(getState().getPartition().getContainer()) == null) {
			r = getState().nw(VortexEyeQueryDataResponse.class);
			r.setMessage("Running in sample mode, no database for running history");
			return STATUS_COMPLETE;
		}
		switch (request.getType()) {
			case VortexAgentEntity.TYPE_BUILD_RESULT: {
				VortexVaultRequest req = getState().nw(VortexVaultRequest.class);
				HashSet<Long> ids = new HashSet<Long>();
				for (long id : request.getIds()) {
					VortexBuildResult br = getState().getBuildResult(id);
					if (br.getBuildStderrVvid() > VortexEyeRunBuildProcedureItinerary.MAX_LENGTH)
						ids.add(br.getBuildStderrVvid());
					if (br.getBuildStdoutVvid() > VortexEyeRunBuildProcedureItinerary.MAX_LENGTH)
						ids.add(br.getBuildStdoutVvid());
					if (br.getDataVvid() > VortexEyeRunBuildProcedureItinerary.MAX_LENGTH)
						ids.add(br.getDataVvid());
					if (br.getVerifyDataVvid() > VortexEyeRunBuildProcedureItinerary.MAX_LENGTH)
						ids.add(br.getVerifyDataVvid());
				}
				req.setVvidsToRetrieve(ids);
				worker.sendToVault(this, req);
				//DbRequestMessage dbMsg = getState().nw(DbRequestMessage.class);
				//final Map<Object, Object> params = new HashMap<Object, Object>();
				//HashSet<Long> ids = new HashSet<Long>();
				//for (long id : request.getIds())
				//ids.add(id);
				//params.put("ids", ids);
				//dbMsg.setId("query_build_result_detailed");
				//dbMsg.setParams(params);
				//dbMsg.setType(DbRequestMessage.TYPE_QUERY_TO_VALUED);
				//dbMsg.setResultValuedClass(VortexBuildResult.class);
				//worker.sendToDb(this, dbMsg);
				break;
			}
			case VortexAgentEntity.TYPE_FILE_SYSTEM: {
				DbRequestMessage dbMsg = getState().nw(DbRequestMessage.class);
				final Map<Object, Object> params = new HashMap<Object, Object>();
				HashSet<Long> ids = new HashSet<Long>();
				for (long id : request.getIds())
					ids.add(id);
				params.put("ids", ids);
				params.put("lim", 10000);//TODO:MAKE PROPERTY
				dbMsg.setId("query_file_system_history");
				dbMsg.setParams(params);
				dbMsg.setResultValuedClass(VortexAgentFileSystem.class);
				dbMsg.setType(DbRequestMessage.TYPE_QUERY_TO_VALUED);
				DbRequestMessage dbMsg2 = getState().nw(DbRequestMessage.class);
				dbMsg2.setId("query_file_system_stats_history");
				dbMsg2.setParams(params);
				dbMsg2.setType(DbRequestMessage.TYPE_QUERY_TO_VALUED);
				dbMsg2.setResultValuedClass(VortexAgentFileSystem.class);
				dbMsg.setNextRequest(dbMsg2);
				worker.sendToDb(this, dbMsg);
				break;
			}
			case VortexAgentEntity.TYPE_MACHINE: {
				DbRequestMessage dbMsg = getState().nw(DbRequestMessage.class);
				final Map<Object, Object> params = new HashMap<Object, Object>();
				HashSet<Long> ids = new HashSet<Long>();
				for (long id : request.getIds())
					ids.add(id);
				params.put("ids", ids);
				params.put("lim", 10000);//TODO:MAKE PROPERTY
				dbMsg.setId("query_machine_instance_by_id");
				dbMsg.setParams(params);
				dbMsg.setResultValuedClass(VortexAgentMachine.class);
				dbMsg.setType(DbRequestMessage.TYPE_QUERY_TO_VALUED);
				DbRequestMessage dbMsg2 = getState().nw(DbRequestMessage.class);
				dbMsg2.setId("query_machine_stats_history");
				dbMsg2.setParams(params);
				dbMsg2.setType(DbRequestMessage.TYPE_QUERY_TO_VALUED);
				dbMsg2.setResultValuedClass(VortexAgentMachine.class);
				dbMsg.setNextRequest(dbMsg2);
				worker.sendToDb(this, dbMsg);
				break;
			}
			case VortexAgentEntity.TYPE_PROCESS: {
				DbRequestMessage dbMsg = getState().nw(DbRequestMessage.class);
				final Map<Object, Object> params = new HashMap<Object, Object>();
				HashSet<Long> ids = new HashSet<Long>();
				for (long id : request.getIds())
					ids.add(id);
				params.put("ids", ids);
				params.put("lim", 10000);//TODO:MAKE PROPERTY
				dbMsg.setId("query_process_instance_history");
				dbMsg.setParams(params);
				dbMsg.setResultValuedClass(VortexAgentProcess.class);
				dbMsg.setType(DbRequestMessage.TYPE_QUERY_TO_VALUED);
				DbRequestMessage dbMsg2 = getState().nw(DbRequestMessage.class);
				dbMsg2.setId("query_process_history");
				dbMsg2.setParams(params);
				dbMsg2.setType(DbRequestMessage.TYPE_QUERY_TO_VALUED);
				dbMsg2.setResultValuedClass(VortexAgentProcess.class);
				dbMsg.setNextRequest(dbMsg2);
				worker.sendToDb(this, dbMsg);
				break;
			}
			case VortexAgentEntity.TYPE_DEPLOYMENT: {
				DbRequestMessage dbMsg = getState().nw(DbRequestMessage.class);
				final Map<Object, Object> params = new HashMap<Object, Object>();
				HashSet<Long> ids = new HashSet<Long>();
				for (long id : request.getIds())
					ids.add(id);
				params.put("ids", ids);
				params.put("lim", 10000);//TODO:MAKE PROPERTY
				dbMsg.setId("query_deployment_history");
				dbMsg.setParams(params);
				dbMsg.setResultValuedClass(VortexDeployment.class);
				dbMsg.setType(DbRequestMessage.TYPE_QUERY_TO_VALUED);
				DbRequestMessage dbMsg2 = getState().nw(DbRequestMessage.class);
				dbMsg2.setId("query_deployment_stats_history");
				dbMsg2.setParams(params);
				dbMsg2.setType(DbRequestMessage.TYPE_QUERY_TO_VALUED);
				dbMsg2.setResultValuedClass(VortexDeployment.class);
				dbMsg.setNextRequest(dbMsg2);
				worker.sendToDb(this, dbMsg);
				break;
			}
			case VortexAgentEntity.TYPE_BACKUP_FILE: {
				DbRequestMessage dbMsg = getState().nw(DbRequestMessage.class);
				final Map<Object, Object> params = new HashMap<Object, Object>();
				HashSet<Long> ids = new HashSet<Long>();
				for (long id : request.getIds())
					ids.add(id);
				params.put("ids", ids);
				params.put("lim", 10000);//TODO:MAKE PROPERTY
				if (request.getSearchDeleted()) {
					params.put("path", OH.noNull(request.getSearchExpression(), "%"));
					dbMsg.setId("query_backup_files_deleted");
				} else {
					dbMsg.setId("query_backup_files_history");
				}
				dbMsg.setParams(params);
				dbMsg.setResultValuedClass(VortexAgentBackupFile.class);
				dbMsg.setType(DbRequestMessage.TYPE_QUERY_TO_VALUED);
				worker.sendToDb(this, dbMsg);
				break;
			}
			case VortexAgentEntity.TYPE_VAULT_ENTRY: {
				VortexVaultRequest req = getState().nw(VortexVaultRequest.class);
				HashSet<Long> ids = new HashSet<Long>();
				for (long id : request.getIds())
					ids.add(id);
				req.setVvidsToRetrieve(ids);
				worker.sendToVault(this, req);
				break;
			}
			default: {
				r.setMessage("Unknown type: " + request.getType());
				return STATUS_COMPLETE;
			}
		}
		return STATUS_ACTIVE;
	}

	@Override
	public byte onResponse(ResultMessage<?> result, VortexEyeItineraryWorker worker) {
		VortexEyeQueryDataRequest request = getInitialRequest().getAction();
		switch (request.getType()) {
			case VortexAgentEntity.TYPE_BACKUP_FILE: {
				List<VortexAgentBackupFile> rows = (List<VortexAgentBackupFile>) ((DbResultMessage) result.getAction()).getResultsValued();
				r.setOk(true);
				r.setData(rows);
				break;
			}
			case VortexAgentEntity.TYPE_VAULT_ENTRY: {
				Map<Long, byte[]> results = ((VortexVaultResponse) result.getAction()).getData();
				List<VortexEntity> rows = new ArrayList<VortexEntity>(results.size());
				for (Entry<Long, byte[]> ve : results.entrySet()) {
					final VortexVaultEntry entry = getState().nw(VortexVaultEntry.class);
					entry.setId(ve.getKey());
					entry.setData(ve.getValue());
					rows.add(entry);
				}
				r.setData(rows);
				r.setOk(true);
				break;
			}
			case VortexAgentEntity.TYPE_BUILD_RESULT: {
				Map<Long, byte[]> results = ((VortexVaultResponse) result.getAction()).getData();
				Map<Long, VortexBuildResult> r = new HashMap<Long, VortexBuildResult>();
				for (long id : request.getIds()) {
					VortexBuildResult br = getState().getBuildResult(id).clone();
					r.put(id, br);
					if (br.getBuildStderrLength() > VortexEyeRunBuildProcedureItinerary.MAX_LENGTH)
						br.setBuildStderr(results.get(br.getBuildStderrVvid()));
					if (br.getBuildStdoutLength() > VortexEyeRunBuildProcedureItinerary.MAX_LENGTH)
						br.setBuildStdout(results.get(br.getBuildStdoutVvid()));
					if (br.getDataLength() > VortexEyeRunBuildProcedureItinerary.MAX_LENGTH)
						br.setData(results.get(br.getDataVvid()));
					if (br.getVerifyDataLength() > VortexEyeRunBuildProcedureItinerary.MAX_LENGTH)
						br.setVerifyData(results.get(br.getVerifyDataVvid()));
				}
				this.r.setOk(true);
				this.r.setData(CH.l(r.values()));
				break;
			}
			case VortexAgentEntity.TYPE_MACHINE: {
				List<VortexAgentMachine> out = new ArrayList<VortexAgentMachine>();
				LongKeyMap<VortexAgentMachine> fileSystemInstances = new LongKeyMap<VortexAgentMachine>();
				for (VortexAgentMachine row : (List<VortexAgentMachine>) ((DbResultMessage) result.getAction()).getResultsValued())
					fileSystemInstances.put(row.getId(), row);
				List<VortexAgentMachine> stats = (List<VortexAgentMachine>) ((DbResultMessage) result.getAction()).getNextResult().getResultsValued();
				VH.sort(stats, VortexAgentMachine.PID_NOW);
				final LongSet found = new LongSet();
				for (VortexAgentMachine row : stats) {
					final VortexAgentMachine i2;
					if (found.add(row.getId())) {//is this the first
						i2 = fileSystemInstances.get(row.getId());
						if (i2 == null)
							throw new RuntimeException("stats for missing machine: " + row.getId());
					} else {
						i2 = getTools().nw(VortexAgentMachine.class);
						i2.setId(row.getId());
					}
					i2.setNow(row.getNow());
					i2.setSystemLoadAverage(row.getSystemLoadAverage());
					i2.setTotalMemory(row.getTotalMemory());
					i2.setTotalSwapMemory(row.getTotalSwapMemory());
					i2.setUsedMemory(row.getUsedMemory());
					i2.setUsedSwapMemory(row.getUsedSwapMemory());
					out.add(i2);
				}

				this.r.setOk(true);
				this.r.setData(out);
				break;
			}
			case VortexAgentEntity.TYPE_FILE_SYSTEM: {
				List<VortexAgentFileSystem> out = new ArrayList<VortexAgentFileSystem>();
				LongKeyMap<VortexAgentFileSystem> fileSystemInstances = new LongKeyMap<VortexAgentFileSystem>();
				for (VortexAgentFileSystem row : (List<VortexAgentFileSystem>) ((DbResultMessage) result.getAction()).getResultsValued())
					fileSystemInstances.put(row.getId(), row);
				List<VortexAgentFileSystem> stats = (List<VortexAgentFileSystem>) ((DbResultMessage) result.getAction()).getNextResult().getResultsValued();
				VH.sort(stats, VortexAgentFileSystem.PID_NOW);
				final LongSet found = new LongSet();
				for (VortexAgentFileSystem row : stats) {
					final VortexAgentFileSystem i2;
					if (found.add(row.getId())) {//is this the first
						i2 = fileSystemInstances.get(row.getId());
						if (i2 == null)
							throw new RuntimeException("stats for missing process: " + row.getId());
					} else {
						i2 = getTools().nw(VortexAgentFileSystem.class);
						i2.setId(row.getId());
					}
					i2.setNow(row.getNow());
					i2.setFreeSpace(row.getFreeSpace());
					i2.setTotalSpace(row.getTotalSpace());
					i2.setUsableSpace(row.getUsableSpace());
					out.add(i2);
				}

				this.r.setOk(true);
				this.r.setData(out);
				break;
			}
			case VortexAgentEntity.TYPE_PROCESS: {
				List<VortexAgentProcess> out = new ArrayList<VortexAgentProcess>();
				LongKeyMap<VortexAgentProcess> processInstances = new LongKeyMap<VortexAgentProcess>();
				for (VortexAgentProcess row : (List<VortexAgentProcess>) ((DbResultMessage) result.getAction()).getResultsValued())
					processInstances.put(row.getId(), row);
				List<VortexAgentProcess> stats = (List<VortexAgentProcess>) ((DbResultMessage) result.getAction()).getNextResult().getResultsValued();
				VH.sort(stats, VortexAgentProcess.PID_NOW);
				final LongSet found = new LongSet();
				for (VortexAgentProcess row : stats) {
					final VortexAgentProcess i2;
					if (found.add(row.getId())) {//is this the first
						i2 = processInstances.get(row.getId());
						if (i2 == null)
							throw new RuntimeException("stats for missing process: " + row.getId());
					} else {
						i2 = getTools().nw(VortexAgentProcess.class);
						i2.setId(row.getId());
					}
					i2.setNow(row.getNow());
					i2.setCpuPercent(row.getCpuPercent());
					i2.setMemory(row.getMemory());
					out.add(i2);
				}

				this.r.setOk(true);
				this.r.setData(out);
				break;
			}
			case VortexAgentEntity.TYPE_DEPLOYMENT: {
				List<VortexDeployment> instances = (List<VortexDeployment>) ((DbResultMessage) result.getAction()).getResultsValued();
				List<VortexDeployment> stats = (List<VortexDeployment>) ((DbResultMessage) result.getAction()).getNextResult().getResultsValued();
				VH.sort(instances, VortexDeployment.PID_NOW);
				VH.sort(stats, VortexDeployment.PID_NOW);
				BasicMultiMap.List<Long, VortexDeployment> instancesById = new BasicMultiMap.List<Long, VortexDeployment>();
				BasicMultiMap.List<Long, VortexDeployment> statsById = new BasicMultiMap.List<Long, VortexDeployment>();
				for (VortexDeployment t : instances)
					instancesById.putMulti(t.getId(), t);
				for (VortexDeployment t : stats)
					statsById.putMulti(t.getId(), t);
				Map<Long, Tuple2<List<VortexDeployment>, List<VortexDeployment>>> joined = CH.join(instancesById, statsById);
				final List<VortexDeployment> out = new ArrayList<VortexDeployment>();
				for (Entry<Long, Tuple2<List<VortexDeployment>, List<VortexDeployment>>> pair : joined.entrySet()) {
					List<VortexDeployment> instanceList = pair.getValue().getA();
					List<VortexDeployment> statsList = pair.getValue().getB();
					if (CH.isEmpty(instanceList))
						throw new NullPointerException("no instance for deployment: " + pair.getKey());
					if (CH.isEmpty(statsList))
						throw new NullPointerException("no stats for deployment: " + pair.getKey());
					Iterator<VortexDeployment> instanceIterator = instanceList.iterator();
					Iterator<VortexDeployment> statsIterator = statsList.iterator();
					VortexDeployment instance = instanceIterator.next();
					VortexDeployment nextInstance = CH.nextOr(instanceIterator, null);
					while (statsIterator.hasNext()) {
						VortexDeployment stat = statsIterator.next();
						if (nextInstance != null && stat.getNow() >= nextInstance.getNow()) {
							instance = nextInstance;
							nextInstance = CH.nextOr(instanceIterator, null);
							if (nextInstance != null && stat.getNow() >= nextInstance.getNow())
								throw new IllegalStateException("bad state: " + stat + " , " + nextInstance);
						}
						final VortexDeployment i = instance.clone();
						i.setStatus(stat.getStatus());
						i.setNow(stat.getNow());
						i.setRunningPid(stat.getRunningPid());
						i.setCurrentBuildResultId(stat.getCurrentBuildResultId());
						i.setRunningProcessUid(stat.getRunningProcessUid());
						i.setDeployedInstanceId(stat.getDeployedInstanceId());
						i.setMessage(stat.getMessage());
						out.add(i);
					}
				}

				this.r.setOk(true);
				this.r.setData(out);
				break;
			}
		}
		return STATUS_COMPLETE;
	}
	@Override
	public Message endJourney(VortexEyeItineraryWorker worker) {
		return r;
	}

	@Override
	protected void populateAuditEvent(VortexEyeQueryDataRequest action, VortexEyeItineraryProcessor worker, VortexEyeClientEvent sink) {
		sink.setEventType(VortexEyeClientEvent.TYPE_QUERY_DATA);
		sink.getParams().put("TYPE", SH.toString(action.getType()));
		auditList(sink, "IDS", action.getIds());
	}

}
