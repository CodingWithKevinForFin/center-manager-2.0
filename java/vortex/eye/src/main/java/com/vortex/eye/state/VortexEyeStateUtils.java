package com.vortex.eye.state;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.VH;
import com.f1.utils.converter.bytes.BasicFromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.Tuple2;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.evaluators.VortexAbstractRevisionEvaluator;
import com.vortex.eye.processors.VortexEyeProcessor;

public class VortexEyeStateUtils {
	private static final Logger log = Logger.getLogger(VortexEyeStateUtils.class.getName());

	public static void processAgentEntityAdds(VortexEyeAgentState agentState, List<VortexAgentEntity> adds, VortexEyeProcessor<?> processor, long now,
			VortexEyeChangesMessageBuilder msgBuilderSink) throws IOException {
		if (CH.isEmpty(adds))
			return;
		VortexEyeState state = agentState.getEyeState();
		LongSet machinesAdded = new LongSet();
		BasicMultiMap.List<Long, VortexAgentEntity> agentEntitiesByOrigMiid = new BasicMultiMap.List<Long, VortexAgentEntity>();
		for (VortexAgentEntity vortexEntity : adds) {
			long miid = vortexEntity.getMachineInstanceId();
			if (miid == 0 || vortexEntity.getId() == 0)
				throw new RuntimeException("Invalid miid / id: " + vortexEntity);
			if (vortexEntity instanceof VortexAgentMachine) {
				VortexAgentMachine machine = (VortexAgentMachine) vortexEntity;
				LH.info(log, "Received machine snapshot from '" + agentState.getRemoteHost() + "'. MUID: " + machine.getMachineUid() + ", host: " + machine.getHostName());
				long origMiid = machine.getId();
				VortexEyeMachineState machineState = state.getMachineByMuidNoThrow(machine.getMachineUid());
				if (machineState == null) {
					handleDiffs("", null, machine, null, state, null, now, processor);
					machineState = state.createMachineState(machine);
					msgBuilderSink.writeAdd(machine);
				} else {
					if (machineState.getAgentState() != null)
						throw new RuntimeException("Received dup machine snapshot for: " + agentState.getProcessUid() + ", machine: " + machine);
					log.info("clearing agent id mapping");
					machineState.clearAgentIdMapping();
					msgBuilderSink.writeUpdate(machineState.getMachine(), machine);
					handleDiffs("", machineState.getMachine(), machine, machineState, state, null, now, processor);
					//msgBuilderSink.writeUpdate(machineState.getMachine(), VortexAgentMachine.PID_AGENT_VERSION);
					//msgBuilderSink.writeUpdate(machineState.getMachine(), VortexAgentMachine.PID_AGENT_PATH);
				}
				machineState.setOrigMiid(origMiid);
				agentState.addMachine(machineState);
				machinesAdded.add(origMiid);
			} else {
				//is this entity a member of a just found machine (meaning its part of a machine snapshot)
				if (machinesAdded.contains(miid))
					agentEntitiesByOrigMiid.putMulti(vortexEntity.getMachineInstanceId(), vortexEntity);
				else {
					handleDiffs(VortexAgentUtils.getKeyForEntity(vortexEntity), null, vortexEntity, agentState.getMachineByOrigMiid(miid), state, null, now, processor);
					msgBuilderSink.writeAdd(vortexEntity);
				}
			}
		}

		for (long addedOrigId : machinesAdded) {
			VortexEyeMachineState machineState = agentState.getMachineByOrigMiid(addedOrigId);
			long origMiid = machineState.getOrigMiid();
			List<VortexAgentEntity> fromAgent = agentEntitiesByOrigMiid.remove(origMiid);
			if (CH.isEmpty(fromAgent)) {
				LH.warning(log, "There are no entities for machine w/ orig id ", origMiid, ".  This is most likely incorrect.  Received from ", agentState.getRemoteHost());
				fromAgent = Collections.emptyList();
			}
			final Map<String, VortexAgentEntity> fromAgentMap = new HashMap<String, VortexAgentEntity>();
			VortexAgentUtils.keyAgentMachineEvents(fromAgent, fromAgentMap);
			final Map<String, Tuple2<VortexAgentEntity, VortexAgentEntity>> joined = CH.join(machineState.getEntitiesMap(), fromAgentMap);
			for (Entry<String, Tuple2<VortexAgentEntity, VortexAgentEntity>> entry : joined.entrySet()) {
				final String key = entry.getKey();
				final VortexAgentEntity existing = (VortexAgentEntity) entry.getValue().getA();
				final VortexAgentEntity fromAgentEntity = (VortexAgentEntity) entry.getValue().getB();
				handleDiffs(key, existing, fromAgentEntity, machineState, state, null, now, processor);
				if (fromAgentEntity != null) {
					if (existing != null)
						msgBuilderSink.writeAdd(existing);
					else
						msgBuilderSink.writeAdd(fromAgentEntity);
				}
			}
		}
		if (!agentEntitiesByOrigMiid.isEmpty()) {
			LH.warning(log, "Received entities w/o machine. Miids: ", agentEntitiesByOrigMiid.keySet(), "existing miids: ", agentState.getMachineOrigMiids());
		}
	}

	static private <T extends VortexAgentEntity> void handleDiffs(String key, T existing, T fromAgent, VortexEyeMachineState agentState, VortexEyeState state,
			List<? super VortexAgentEntity> updatesSink, long now, VortexEyeProcessor<?> processor) {
		VortexAbstractRevisionEvaluator<T> evaluator = state.getEvaluator(OH.noNull(fromAgent, existing));
		if (evaluator == null) {
			LH.warning(log, "Evaluator not found for type: ", fromAgent);
			return;
		}
		if (existing == null) {
			long fromAgentId = fromAgent.getId();
			fromAgent.setNow(now);
			fromAgent.setRevision(VortexAgentUtils.REVISION_NEW);
			fromAgent.setId(state.createNextId());
			if (fromAgent instanceof VortexAgentMachine) {
				fromAgent.setMachineInstanceId(fromAgent.getId());
			} else {
				agentState.addEntity(key, fromAgentId, fromAgent);
				fromAgent.setMachineInstanceId(agentState.getMiid());
			}
			processor.sendToDb(evaluator.insertToDatabase(fromAgent));
			processor.sendToDb(evaluator.insertStatsToDatabase(fromAgent));
		} else if (fromAgent == null) {
			existing.setRevision(VortexAgentUtils.REVISION_DONE);
			processor.sendToDb(evaluator.insertToDatabase(existing));
			agentState.removeEntity(key);
		} else {
			agentState.mapAgentIdToEntity(fromAgent.getId(), existing);
			byte diffResult = evaluator.diff(fromAgent, existing);
			if (diffResult == VortexAbstractRevisionEvaluator.DIFF_FUNDAMENTALS_CHANGED) {
				final int rev = existing.getRevision();
				final long id = existing.getId();
				final long machineId = existing.getMachineInstanceId();
				VH.copyPartialFields(fromAgent, existing);
				existing.setId(id);
				existing.setMachineInstanceId(machineId);
				existing.setRevision(rev + 1);
				if (updatesSink != null)
					updatesSink.add(existing);
				existing.setNow(now);
				processor.sendToDb(evaluator.insertToDatabase(existing));
				processor.sendToDb(evaluator.insertStatsToDatabase(existing));
			} else if (diffResult == VortexAbstractRevisionEvaluator.DIFF_ONLY_STATS_CHANGED) {
				final long id = existing.getId();
				final long machineId = existing.getMachineInstanceId();
				VH.copyPartialFields(fromAgent, existing);
				existing.setId(id);
				existing.setMachineInstanceId(machineId);
				if (updatesSink != null)
					updatesSink.add(existing);
				existing.setNow(now);
				processor.sendToDb(evaluator.insertStatsToDatabase(existing));
			}
		}

	}

	public static void processF1AppEntityAdds(VortexEyeAgentState agentState, List<F1AppEntity> adds, VortexEyeProcessor<?> processor, long now,
			VortexEyeChangesMessageBuilder msgBuilderSink) {
		if (CH.isEmpty(adds))
			return;
		final VortexEyeState state = agentState.getEyeState();

		long lastId = -1;
		VortexEyeF1AppState f1AppState = null;
		for (F1AppEntity f1e : adds) {
			final long origId = f1e.getId();
			f1e.setId(state.createNextId());
			if (f1e instanceof F1AppInstance) {
				final F1AppInstance appInstance = (F1AppInstance) f1e;
				appInstance.setF1AppInstanceId(f1e.getId());
				LH.info(log, "Received f1 app snapshot from '", agentState.getRemoteHost(), "'. puid: ", appInstance.getProcessUid(), ", host: ", appInstance.getHostName(),
						", PID: ", appInstance.getPid(), ", name: ", appInstance.getAppName());
				f1AppState = state.createF1AppState(appInstance);
				f1AppState.setOrigId(origId);
				agentState.addF1App(f1AppState);
				lastId = f1e.getF1AppInstanceId();
			} else {
				if (f1e.getF1AppInstanceId() != lastId)//cache last lookup, if same f1app
					f1AppState = agentState.getF1AppByOrigAiid(lastId = f1e.getF1AppInstanceId());
				f1AppState.addEntity(origId, f1e);
			}
			msgBuilderSink.writeAdd(f1e);
		}

	}

	public static void processAgentEntityUpdates(VortexEyeAgentState agentState, byte[] updates, VortexEyeProcessor<?> processor, long now,
			VortexEyeChangesMessageBuilder msgBuilderSink) {
		if (AH.isEmpty(updates))
			return;
		try {
			final VortexEyeState state = agentState.getEyeState();
			final ObjectToByteArrayConverter converter = (ObjectToByteArrayConverter) processor.getServices().getConverter();
			final FastByteArrayDataInputStream in = new FastByteArrayDataInputStream(updates);
			final BasicFromByteArrayConverterSession inSession = new BasicFromByteArrayConverterSession(converter, in);
			while (in.available() > 0) {
				long miid = in.readLong();
				long id = in.readLong();
				VortexEyeMachineState machine = agentState.getMachineByOrigMiid(miid);
				VortexAgentEntity existing = machine.getEntityByOrigId(id);
				VortexAbstractRevisionEvaluator<VortexAgentEntity> evaluator = state.getEvaluator(existing);
				final byte[] pids = new byte[256];
				ValuedSchema<Valued> schema = existing.askSchema();
				byte diffChange = VortexAbstractRevisionEvaluator.DIFF_NO_CHANGE;
				int i = 0;
				for (;;) {
					final byte pid = in.readByte();
					if (pid == Valued.NO_PID)
						break;
					switch (evaluator.getPidType(pid)) {
						case VortexAbstractRevisionEvaluator.IGNORE_PID:
							//converter.read(inSession);//ignore the value
							//continue;
							break;
						case VortexAbstractRevisionEvaluator.FUNDAMENTAL_PID:
							diffChange = VortexAbstractRevisionEvaluator.DIFF_FUNDAMENTALS_CHANGED;
							break;
						case VortexAbstractRevisionEvaluator.STATS_PID:
							if (diffChange == VortexAbstractRevisionEvaluator.DIFF_NO_CHANGE)
								diffChange = VortexAbstractRevisionEvaluator.DIFF_ONLY_STATS_CHANGED;
							break;
					}
					pids[i++] = pid;
					ValuedParam<Valued> param = schema.askValuedParam(pid);
					Object old = param.getValue(existing);
					if (param.isPrimitive()) {
						byte basicType = in.readByte();
						if (basicType != param.getBasicType())
							throw new RuntimeException("bad param type: " + basicType + "!=" + param.getBasicType());
						param.read(existing, in);
					} else {
						Object value = converter.read(inSession);
						param.setValue(existing, value);
					}
				}
				pids[i] = Valued.NO_PID;
				msgBuilderSink.writeUpdate(existing, pids);
				switch (diffChange) {
					case VortexAbstractRevisionEvaluator.DIFF_FUNDAMENTALS_CHANGED:
						existing.setRevision(existing.getRevision() + 1);
						processor.sendToDb(evaluator.insertToDatabase(existing));
						processor.sendToDb(evaluator.insertStatsToDatabase(existing));
						break;
					case VortexAbstractRevisionEvaluator.DIFF_ONLY_STATS_CHANGED:
						processor.sendToDb(evaluator.insertStatsToDatabase(existing));
						break;
				}

			}
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	public static void processAgentEntityRemoves(VortexEyeAgentState agentState, long[] removed, VortexEyeProcessor<?> processor, long now,
			VortexEyeChangesMessageBuilder msgBuilderSink) {
		if (AH.isEmpty(removed))
			return;
		final VortexEyeState state = agentState.getEyeState();
		for (int i = 0; i < removed.length; i += 2) {
			long miid = removed[i];
			long id = removed[i + 1];
			VortexEyeMachineState machine = agentState.getMachineByOrigMiid(miid);
			VortexAgentEntity existing = machine.getEntityByOrigId(id);
			String key = VortexAgentUtils.getKeyForEntity(existing);
			machine.removeEntity(key);
			VortexAbstractRevisionEvaluator<VortexAgentEntity> evaluator = state.getEvaluator(existing);
			existing.setRevision(VortexAgentUtils.REVISION_DONE);
			processor.sendToDb(evaluator.insertToDatabase(existing));
			msgBuilderSink.writeRemove(existing);
		}
	}

	public static void processF1AppEntityUpdates(VortexEyeAgentState agentState, byte[] updated, VortexEyeProcessor<?> processor, long now,
			VortexEyeChangesMessageBuilder msgBuilderSink) {
		if (AH.isEmpty(updated))
			return;

		try {
			final VortexEyeState state = agentState.getEyeState();
			final ObjectToByteArrayConverter converter = (ObjectToByteArrayConverter) state.getPartition().getContainer().getServices().getConverter();
			final FastByteArrayDataInputStream in = new FastByteArrayDataInputStream(updated);
			final BasicFromByteArrayConverterSession inSession = new BasicFromByteArrayConverterSession(converter, in);

			byte[] pids = new byte[256];
			// [(byte)PID,(byte)BasicType,(?)value]+(byte)NO_PID]+EOF
			VortexEyeF1AppState app = null;
			long lastId = -1;
			while (in.available() > 0) {
				long appid = in.readLong();
				long id = in.readLong();
				if (appid != lastId)//cache the last look up
					app = agentState.getF1AppByOrigAiid(appid);
				F1AppEntity obj = app.getByOrigId(id);

				ValuedSchema<Valued> schema = obj.askSchema();
				for (int i = 0;; i++) {
					byte pid = in.readByte();
					pids[i] = pid;
					if (pid == Valued.NO_PID) {
						break;
					}
					ValuedParam<Valued> vp = schema.askValuedParam(pid);
					if (vp.getPid() == F1AppEntity.PID_F1_APP_INSTANCE_ID)
						throw new RuntimeException("can not update app instanceid");
					if (vp.isPrimitive()) {
						byte basicType = in.readByte();//basicType
						if (basicType != vp.getBasicType())
							throw new RuntimeException("bad param type: " + basicType + "!=" + vp);
						vp.read(obj, in);
					} else {
						Object value = converter.read(inSession);
						vp.setValue(obj, value);
					}
				}
				msgBuilderSink.writeUpdate(obj, pids);
			}
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}
	public static void processF1AppEntityRemoves(VortexEyeAgentState agentState, long[] removed, VortexEyeProcessor<?> processor, long now,
			VortexEyeChangesMessageBuilder msgBuilderSink) {
		if (AH.isEmpty(removed))
			return;
		for (int i = 0; i < removed.length; i += 2) {
			final long aiid = removed[i];
			final long id = removed[i + 1];
			final VortexEyeF1AppState app = agentState.getF1AppByOrigAiid(aiid);
			final F1AppEntity obj;
			if (aiid == id) {
				agentState.removeF1App(app);
				obj = app.getF1AppInstance();
			} else {
				obj = app.removeByOrigId(id);
			}
			msgBuilderSink.writeRemove(obj);
		}
	}

}
