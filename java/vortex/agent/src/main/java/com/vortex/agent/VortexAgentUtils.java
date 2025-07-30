package com.vortex.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.base.ValuedParam;
import com.f1.container.ContainerScope;
import com.f1.container.ResultMessage;
import com.f1.povo.f1app.reqres.F1AppRequest;
import com.f1.povo.f1app.reqres.F1AppResponse;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentCron;
import com.f1.vortexcommon.msg.agent.VortexAgentDbColumn;
import com.f1.vortexcommon.msg.agent.VortexAgentDbDatabase;
import com.f1.vortexcommon.msg.agent.VortexAgentDbObject;
import com.f1.vortexcommon.msg.agent.VortexAgentDbPrivilege;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;
import com.f1.vortexcommon.msg.agent.VortexAgentDbTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentEvent;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.agent.VortexAgentFileSystem;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.agent.VortexAgentMachineEventStats;
import com.f1.vortexcommon.msg.agent.VortexAgentNetAddress;
import com.f1.vortexcommon.msg.agent.VortexAgentNetConnection;
import com.f1.vortexcommon.msg.agent.VortexAgentNetLink;
import com.f1.vortexcommon.msg.agent.VortexAgentProcess;
import com.f1.vortexcommon.msg.agent.VortexAgentSnapshot;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentChangesRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentFileSearchRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentInspectCronRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentInspectDbRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentSnapshotRequest;
import com.f1.vortexcommon.msg.eye.VortexBuildProcedure;
import com.f1.vortexcommon.msg.eye.VortexBuildResult;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexDeploymentSet;
import com.f1.vortexcommon.msg.eye.VortexExpectation;
import com.vortex.agent.dbadapter.DbInspector;

public class VortexAgentUtils {
	private static final Logger log = LH.get(VortexAgentUtils.class);

	public static final int REVISION_DONE = VortexAgentEntity.REVISION_DONE;
	public static final int REVISION_NEW = VortexAgentEntity.REVISION_NEW;

	public static String toSudo(String owner, String rawCommand) {
		String thisUser = EH.getUserName();
		if (OH.ne(thisUser, owner)) {
			if (thisUser.equals("root")) {
				return "/usr/bin/sudo -u " + owner + " " + rawCommand;
			} else if (owner.equals("root")) {
				return "/usr/bin/sudo -u root " + rawCommand;
			} else
				return "/usr/bin/sudo -u root sudo -u " + owner + " " + rawCommand;
		} else
			return rawCommand;
	}
	public static byte[] trim(byte[] data, int maxLen) {
		if (data == null || data.length <= maxLen)
			return data;
		return Arrays.copyOf(data, maxLen);
	}

	public static Map<String, VortexAgentNetLink> keyNetLinks(Collection<VortexAgentNetLink> values) {
		final Map<String, VortexAgentNetLink> r = new HashMap<String, VortexAgentNetLink>();
		for (VortexAgentNetLink value : values) {
			if (SH.isnt(value.getName()))
				LH.warning(log, "skipping invalid net link:", value);
			else
				CH.putOrLog(r, getKey(value), value, log, Level.FINE);
		}
		return r;
	}

	public static Map<String, VortexAgentNetAddress> keyNetAddresses(Collection<VortexAgentNetAddress> values) {
		final Map<String, VortexAgentNetAddress> r = new HashMap<String, VortexAgentNetAddress>();
		for (VortexAgentNetAddress value : values) {
			if (SH.isnt(value.getLinkName()) || SH.isnt(value.getAddress()))
				LH.warning(log, "skipping invalid net address:", value);
			else
				CH.putOrLog(r, getKey(value), value, log, Level.FINE);
		}
		return r;
	}

	public static Map<String, VortexAgentMachineEventStats> keyAgentMachineEvents(List<VortexAgentMachineEventStats> values) {
		final Map<String, VortexAgentMachineEventStats> r = new HashMap<String, VortexAgentMachineEventStats>();
		for (VortexAgentMachineEventStats value : values) {
			if (SH.isnt(value.getHost()) || SH.isnt(value.getSource()) || SH.isnt(value.getMessage()))
				LH.warning(log, "skipping invalid agentmachine event:", value);
			else
				CH.putOrLog(r, getKey(value), value, log, Level.FINE);
		}
		return r;
	}

	public static Map<String, VortexAgentProcess> keyProcesses(Collection<VortexAgentProcess> values) {
		final Map<String, VortexAgentProcess> r = new HashMap<String, VortexAgentProcess>();
		for (VortexAgentProcess value : values) {
			if (value.getStartTime() == 0 || SH.isnt(value.getPid()) || SH.isnt(value.getCommand()))
				LH.warning(log, "skipping invalid process: ", value);
			else
				CH.putOrLog(r, getKey(value), value, log, Level.FINE);
		}
		return r;
	}

	public static Map<String, VortexAgentNetConnection> keyConnections(Collection<VortexAgentNetConnection> values) {
		final Map<String, VortexAgentNetConnection> r = new HashMap<String, VortexAgentNetConnection>();
		for (VortexAgentNetConnection value : values) {
			if (SH.isnt(value.getLocalHost()) || value.getState() == 0)
				LH.warning(log, "skipping invalid net connection:", value);
			else
				CH.putOrLog(r, getKey(value), value, log, Level.FINE);
		}
		return r;
	}

	public static Map<String, VortexAgentFileSystem> keyFileSystems(Collection<VortexAgentFileSystem> values) {
		final Map<String, VortexAgentFileSystem> r = new HashMap<String, VortexAgentFileSystem>();
		for (VortexAgentFileSystem value : values) {
			if (SH.isnt(value.getName()))
				LH.info(log, "skipping invalid file system: " + value);
			else
				CH.putOrLog(r, getKey(value), value, log, Level.FINE);
		}
		return r;
	}

	public static Map<String, List<VortexAgentCron>> keyCrons(Collection<VortexAgentCron> values) {
		final Map<String, List<VortexAgentCron>> r = new HashMap<String, List<VortexAgentCron>>();
		for (VortexAgentCron value : values) {
			if (SH.isnt(value.getUser()) || SH.isnt(SH.beforeFirst(value.getCommand(), " ")))
				LH.warning(log, "skipping invalid Cron:", value);
			else {
				String key = getKey(value);
				if (r.containsKey(key))
					r.get(key).add(value);
				else {
					List<VortexAgentCron> list = new ArrayList<VortexAgentCron>();
					list.add(value);
					r.put(getKey(value), list);
				}
			}
		}
		return r;
	}

	public static String getKey(VortexAgentMachine machine) {
		return "";
	}
	public static String getKey(VortexAgentProcess process) {
		return SH.toString(VortexAgentEntity.TYPE_PROCESS) + ":" + process.getStartTime() + ":" + process.getPid();
	}

	public static String getKey(VortexAgentNetLink link) {
		return SH.toString(VortexAgentEntity.TYPE_NET_LINK) + ":" + link.getName();
	}

	public static String getKey(VortexAgentNetAddress address) {
		return SH.toString(VortexAgentEntity.TYPE_NET_ADDRESS) + ":" + address.getLinkName() + ":" + address.getAddress() + ":" + address.getBroadcast();
	}

	public static String getKey(VortexAgentNetConnection connection) {
		return SH.toString(VortexAgentEntity.TYPE_NET_CONNECTION) + ":" + formatConnectionState(connection.getState()) + ":" + connection.getLocalHost() + ":"
				+ connection.getLocalPort() + ":" + connection.getForeignHost() + ":" + connection.getForeignPort() + ":" + connection.getLocalPid();
	}

	public static String getKey(VortexAgentFileSystem fileSystem) {
		return SH.toString(VortexAgentEntity.TYPE_FILE_SYSTEM) + ":" + fileSystem.getName();
	}

	public static String getKey(VortexAgentMachineEventStats machineEvent) {
		return SH.toString(VortexAgentEntity.TYPE_MACHINE_EVENT) + ":" + machineEvent.getHost() + ":" + machineEvent.getSource() + ":" + machineEvent.getMessage() + ":"
				+ machineEvent.getTimeGenerated() + ":" + machineEvent.getUserName();
	}

	public static String getKey(VortexAgentCron cron) {
		return SH.toString(VortexAgentEntity.TYPE_CRON) + ":" + cron.getUser() + ":" + cron.getDayOfMonth() + ":" + cron.getDayOfWeek() + ":" + cron.getMonth() + ":"
				+ cron.getHour() + ":" + cron.getMinute() + ":" + cron.getSecond() + ":" + cron.getTimeZone() + ":" + cron.getCommand();
	}

	public static String formatConnectionState(byte state) {
		return SH.toString(state);
	}
	static public VortexAgentSnapshot diff(VortexAgentSnapshot old, VortexAgentSnapshot nuw) {
		final VortexAgentSnapshot r = (VortexAgentSnapshot) old.nw();
		//final AgentMachineStats memory = (diff(old.getMachineStats(), nuw.getMachineStats(), generator));
		final VortexAgentMachine host = (diff(old.getMachine(), nuw.getMachine()));
		final Map<String, VortexAgentNetConnection> connections = (diff(old.getConnections(), nuw.getConnections()));
		final Map<String, VortexAgentFileSystem> fileSystems = (diff(old.getFileSystems(), nuw.getFileSystems()));
		final Map<String, VortexAgentProcess> processes = (diff(old.getProcesses(), nuw.getProcesses()));
		final Map<String, VortexAgentNetLink> netLinks = (diff(old.getNetLinks(), nuw.getNetLinks()));
		final Map<String, VortexAgentNetAddress> netAddresses = (diff(old.getNetAddresses(), nuw.getNetAddresses()));
		final Map<String, VortexAgentMachineEventStats> machineEvents = (diff(old.getAgentMachineEvents(), nuw.getAgentMachineEvents()));
		//TODO:final Map<String, List<AgentCron>> Crons = (diff(old.getCron(), nuw.getCron(), generator, true));

		//if (memory != null)
		//r.setMachineStats(memory);
		if (host != null)
			r.setMachine(host);
		if (connections != null)
			r.setConnections(connections);
		if (fileSystems != null)
			r.setFileSystems(fileSystems);
		if (processes != null)
			r.setProcesses(processes);
		if (netAddresses != null)
			r.setNetAddresses(netAddresses);
		if (netLinks != null)
			r.setNetLinks(netLinks);
		//if (Crons != null)
		//r.setCron(Crons);
		if (machineEvents != null)
			r.setAgentMachineEvents(machineEvents);
		return r.askExistingPids().hasNext() ? r : null;
	}

	/**
	 * 
	 * @param old
	 *            known entry (existing)
	 * @param nuw
	 *            new entry
	 * @param generator
	 * @return
	 */
	static public <T extends VortexEntity> T diff(T old, T nuw) {
		if (old == nuw || nuw == null)
			return null;
		if (old == null)
			return nuw;
		T r = null;
		for (ValuedParam v : old.askSchema().askValuedParams()) {
			if (!v.areEqual(old, nuw)) {
				if (r == null) {
					r = (T) old.nw();
					r.setId(old.getId());
				}
				//r = (T) generator.nw(old.getClass());
				v.copy(nuw, r);
			}
		}
		return r;
	}
	static public <T extends VortexEntity> Map<String, T> diff(Map<String, T> old, Map<String, T> nuw) {
		if (nuw == null)// this means that we are not capturing
			return null;
		if (old == null)
			return nuw;
		Set<String> removed = CH.comm(old.keySet(), nuw.keySet(), true, false, false);
		Set<String> added = CH.comm(old.keySet(), nuw.keySet(), false, true, false);
		Set<String> changed = CH.comm(old.keySet(), nuw.keySet(), false, false, true);
		Map<String, T> r = new HashMap<String, T>();
		for (String key : added)
			r.put(key, nuw.get(key));
		for (String key : removed)
			r.put(key, null);
		for (String key : changed) {
			final T changes = diff(old.get(key), nuw.get(key));
			if (changes != null)
				r.put(key, changes);
		}
		return r.isEmpty() ? null : r;
	}
	private static final Map<Class<?>, Integer> REQ_TYPES = new HashMap<Class<?>, Integer>();

	private static final Map<Class<?>, Byte> ENTITY_TYPES = new HashMap<Class<?>, Byte>();

	private static final int MIN_COMPLRESS_SIZE = 1024 * 10;
	static {
		REQ_TYPES.put(VortexAgentSnapshotRequest.class, 1);
		REQ_TYPES.put(VortexAgentChangesRequest.class, 2);
		REQ_TYPES.put(VortexAgentInspectDbRequest.class, 3);
		REQ_TYPES.put(VortexAgentFileSearchRequest.class, 4);
		REQ_TYPES.put(VortexAgentInspectCronRequest.class, 7);
		REQ_TYPES.put(VortexAgentMachineEventStats.class, 9);
		ENTITY_TYPES.put(VortexAgentCron.class, VortexAgentEntity.TYPE_CRON);
		ENTITY_TYPES.put(VortexAgentEvent.class, VortexAgentEntity.TYPE_EVENT);
		ENTITY_TYPES.put(VortexAgentFile.class, VortexAgentEntity.TYPE_FILE);
		ENTITY_TYPES.put(VortexAgentFileSystem.class, VortexAgentEntity.TYPE_FILE_SYSTEM);
		ENTITY_TYPES.put(VortexAgentMachine.class, VortexAgentEntity.TYPE_MACHINE);
		ENTITY_TYPES.put(VortexAgentMachineEventStats.class, VortexAgentEntity.TYPE_MACHINE_EVENT);
		ENTITY_TYPES.put(VortexAgentNetAddress.class, VortexAgentEntity.TYPE_NET_ADDRESS);
		ENTITY_TYPES.put(VortexAgentNetLink.class, VortexAgentEntity.TYPE_NET_LINK);
		ENTITY_TYPES.put(VortexAgentNetConnection.class, VortexAgentEntity.TYPE_NET_CONNECTION);
		ENTITY_TYPES.put(VortexAgentProcess.class, VortexAgentEntity.TYPE_PROCESS);
		ENTITY_TYPES.put(VortexAgentDbDatabase.class, VortexAgentEntity.TYPE_DB_DATABASE);
		ENTITY_TYPES.put(VortexAgentDbColumn.class, VortexAgentEntity.TYPE_DB_COLUMN);
		ENTITY_TYPES.put(VortexAgentDbObject.class, VortexAgentEntity.TYPE_DB_OBJECT);
		ENTITY_TYPES.put(VortexAgentDbPrivilege.class, VortexAgentEntity.TYPE_DB_PRIVILEDGE);
		ENTITY_TYPES.put(VortexAgentDbServer.class, VortexAgentEntity.TYPE_DB_SERVER);
		ENTITY_TYPES.put(VortexAgentDbTable.class, VortexAgentEntity.TYPE_DB_TABLE);
		ENTITY_TYPES.put(VortexExpectation.class, VortexAgentEntity.TYPE_EXPECTATION);
		ENTITY_TYPES.put(VortexBuildProcedure.class, VortexAgentEntity.TYPE_BUILD_PROCEDURE);
		ENTITY_TYPES.put(VortexBuildResult.class, VortexAgentEntity.TYPE_BUILD_RESULT);
		ENTITY_TYPES.put(VortexDeploymentSet.class, VortexAgentEntity.TYPE_DEPLOYMENT_SET);
		ENTITY_TYPES.put(VortexDeployment.class, VortexAgentEntity.TYPE_DEPLOYMENT);
		//TODO: ENTITY_TYPES.put(VortexAgentEntity.class, VortexAgentEntity.TYPE_DEVICE);
		//TODO: ENTITY_TYPES.put(VortexAgent.class, VortexAgentEntity.TYPE_AUDIT_EVENT_RULE);
		//TODO: ENTITY_TYPES.put(Vortex.class, VortexAgentEntity.TYPE_MACHINE_EVENT);
	}

	public static Integer getRequestType(VortexAgentRequest request) {
		return CH.getOrThrow(REQ_TYPES, request.askSchema().askOriginalType());
	}

	public static DbInspector get(ContainerScope cs, String string) {
		return (DbInspector) cs.getServices().getService("DBINSPECTOR_" + string);
	}

	public static void put(ContainerScope cs, String string, DbInspector dbi) {
		cs.getServices().putService("DBINSPECTOR_" + string, dbi);
	}
	public static <T extends VortexEntity> void keyAgentMachineEvents(List<T> entities, Map<String, ? super T> sink) {
		for (T entity : entities) {
			String key = getKeyForEntity(entity);
			if (key == null)
				log.warning("Could get get key, so skipping: " + entity);
			sink.put(key, entity);

		}
	}
	public static String getKeyForEntity(VortexEntity entity) {
		switch (getEntityType(entity)) {
			case VortexAgentEntity.TYPE_MACHINE:
				return getKey((VortexAgentMachine) entity);
			case VortexAgentEntity.TYPE_NET_CONNECTION:
				return getKey((VortexAgentNetConnection) entity);
			case VortexAgentEntity.TYPE_NET_ADDRESS:
				return getKey((VortexAgentNetAddress) entity);
			case VortexAgentEntity.TYPE_NET_LINK:
				return getKey((VortexAgentNetLink) entity);
			case VortexAgentEntity.TYPE_FILE_SYSTEM:
				return getKey((VortexAgentFileSystem) entity);
			case VortexAgentEntity.TYPE_PROCESS:
				return getKey((VortexAgentProcess) entity);
			case VortexAgentEntity.TYPE_CRON:
				return getKey((VortexAgentCron) entity);

		}
		return null;
	}
	public static byte getEntityType(VortexEntity entity) {
		return CH.getOrThrow(ENTITY_TYPES, entity.askSchema().askOriginalType()).byteValue();
	}
	public static byte getEntityType(Class<? extends VortexEntity> clazz) {
		return CH.getOrThrow(ENTITY_TYPES, clazz);
	}
	public static boolean verifyOk(Logger log, ResultMessage<? extends F1AppResponse> result) {
		F1AppResponse response = result.getAction();
		if (response == null) {
			LH.warning(log, "response is empty from agent: ", getProcessUidFromF1AppResponse(result) + ", request type: "
					+ result.getRequestMessage().getAction().getClass().getName());
			return false;
		}
		if (!response.getOk()) {
			LH.warning(log, "response error from app: ", response.getMessage(), " app processUid: " + getProcessUidFromF1AppResponse(result));
			return false;
		}
		return true;
	}

	static public String getProcessUidFromF1AppResponse(ResultMessage<? extends F1AppResponse> result) {
		F1AppRequest request = (F1AppRequest) result.getRequestMessage().getAction();
		return request.getTargetF1AppProcessUid();
	}
	public static VortexAgentFile compressFile(VortexAgentFile f) {
		if (f == null)
			return null;
		if (f.getData() != null && !MH.anyBits(f.getMask(), VortexAgentFile.DATA_DEFPLATED) && f.getData().length > MIN_COMPLRESS_SIZE) {
			byte[] data = IOH.compressFastest(f.getData());
			if (data.length < f.getData().length) {//don't bother if it can't be compressed
				f.setData(data);
				f.setMask((short) (f.getMask() | VortexAgentFile.DATA_DEFPLATED));
			}
		}
		return f;
	}
	public static VortexAgentFile decompressFile(VortexAgentFile f) {
		if (f == null)
			return null;
		if (f.getData() != null && MH.anyBits(f.getMask(), VortexAgentFile.DATA_DEFPLATED)) {
			f.setData(IOH.decompress(f.getData()));
			f.setMask(MH.clearBits(f.getMask(), VortexAgentFile.DATA_DEFPLATED));
		}
		return f;
	}

}
