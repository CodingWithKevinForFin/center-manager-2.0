package com.vortex.eye;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.f1.container.ContainerScope;
import com.f1.container.ResultMessage;
import com.f1.utils.CronTab;
import com.f1.utils.IntArrayList;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.encrypt.RsaEncryptUtils;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.Tuple2;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentDbColumn;
import com.f1.vortexcommon.msg.agent.VortexAgentDbDatabase;
import com.f1.vortexcommon.msg.agent.VortexAgentDbObject;
import com.f1.vortexcommon.msg.agent.VortexAgentDbPrivilege;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;
import com.f1.vortexcommon.msg.agent.VortexAgentDbTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentRequest;
import com.f1.vortexcommon.msg.agent.reqres.VortexAgentResponse;
import com.f1.vortexcommon.msg.eye.VortexEyeEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeScheduledTask;
import com.vortex.eye.messages.VortexVaultRequest;
import com.vortex.eye.state.VortexEyeDbDatabaseState;
import com.vortex.eye.state.VortexEyeDbServerState;
import com.vortex.eye.state.VortexEyeDbTableState;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeUtils {
	private static final Logger log = LH.get(VortexEyeUtils.class);

	public static final int REVISION_DONE = VortexAgentEntity.REVISION_DONE;
	public static final String DB_SERVICE = "DB_SERVICE";
	public static final String FOUNTAIN_ID = "EYE";

	public static void assertValid(VortexAgentEntity ar) {
		if (ar.getMachineInstanceId() == 0)
			throw new RuntimeException("Invalid Agent Revision, machine instance id is zero: " + ar);
		if (ar.getRevision() > VortexAgentEntity.REVISION_DONE || ar.getRevision() < 0)
			throw new RuntimeException("invalid Agent Revision, bad revision number: " + ar);
		if (ar.getNow() == 0)
			throw new RuntimeException("invalid Agent Revision, now is zero: " + ar);
	}

	public static boolean isActive(VortexAgentEntity process) {
		return process.getRevision() < VortexAgentEntity.REVISION_DONE;
	}
	public static boolean isNew(VortexAgentEntity process) {
		return process.getRevision() == 0;
	}

	public static VortexEyeDbService getVortexDb(ContainerScope cs) {
		return (VortexEyeDbService) cs.getServices().getServiceNoThrow(DB_SERVICE);
	}

	public static void setVortexDb(ContainerScope cs, VortexEyeDbService ds) {
		cs.getServices().putService(DB_SERVICE, ds);
	}

	public static LongKeyMap<VortexEyeDbServerState> buildDbServerState(Iterable<VortexAgentDbServer> dbServers, Iterable<VortexAgentDbDatabase> databases,
			Iterable<VortexAgentDbTable> tables, Iterable<VortexAgentDbColumn> columns, Iterable<VortexAgentDbObject> objects, Iterable<VortexAgentDbPrivilege> privs) {

		//lookup maps
		LongKeyMap<VortexEyeDbServerState> svStates = new LongKeyMap<VortexEyeDbServerState>();
		LongKeyMap<VortexEyeDbDatabaseState> dbStates = new LongKeyMap<VortexEyeDbDatabaseState>();
		LongKeyMap<VortexEyeDbTableState> tbStates = new LongKeyMap<VortexEyeDbTableState>();

		//servers
		for (VortexAgentDbServer server : dbServers)
			svStates.put(server.getId(), new VortexEyeDbServerState(server));

		//databases
		for (VortexAgentDbDatabase o : databases) {
			VortexEyeDbServerState existing = svStates.get(o.getDbServerId());
			if (existing == null)
				LH.warning(log, "Database for unknown server should be deleted: ", o);
			else
				dbStates.put(o.getId(), existing.addDatabase(o));
		}

		//validate tables
		for (VortexAgentDbTable o : tables) {
			VortexEyeDbDatabaseState existing = dbStates.get(o.getDatabaseId());
			if (existing == null)
				LH.warning(log, "Table for unknown database and/or server should be deleted: ", o);
			else
				tbStates.put(o.getId(), existing.addTable(o));
		}

		//validate columns
		for (VortexAgentDbColumn o : columns) {
			VortexEyeDbTableState existing = tbStates.get(o.getTableId());
			if (existing == null)
				LH.warning(log, "Column for unknown table and/or server should be deleted: ", o);
			else
				existing.addColumn(o);
		}

		//handle objects
		for (VortexAgentDbObject o : objects) {
			VortexEyeDbDatabaseState existing = dbStates.get(o.getDatabaseId());
			if (existing == null)
				LH.warning(log, "Object for unknown database and/or server should be deleted: ", o);
			else
				existing.addObject(o);
		}

		//handle privileges
		for (VortexAgentDbPrivilege o : privs) {
			VortexEyeDbDatabaseState existing = dbStates.get(o.getDatabaseId());
			if (existing == null)
				LH.warning(log, "Privilege for unknown database and/or server should be deleted: ", o);
			else
				existing.addPrivilege(o);
		}
		return svStates;
	}

	public static boolean verifyOk(Logger log, ResultMessage<? extends VortexAgentResponse> result) {
		VortexAgentResponse response = result.getAction();
		if (response == null) {
			LH.warning(log, "response is empty from agent: ", getProcessUidFromF1AppResponse(result) + ", request type: "
					+ result.getRequestMessage().getAction().getClass().getName());
			return false;
		}
		if (!response.getOk()) {
			LH.warning(log, "response error from app: ", response.getMessage(), " agent processUid: " + getProcessUidFromF1AppResponse(result));
			return false;
		}
		return true;
	}

	static public String getProcessUidFromF1AppResponse(ResultMessage<? extends VortexAgentResponse> result) {
		VortexAgentRequest request = (VortexAgentRequest) result.getRequestMessage().getAction();
		return request.getTargetAgentProcessUid();
	}

	public static <T extends VortexEyeEntity> LongKeyMap<T> mapById(Iterable<T> entities) {
		LongKeyMap<T> r = new LongKeyMap<T>();
		for (T t : entities)
			r.put(t.getId(), t);
		return r;
	}
	static public long getNextOccurence(VortexEyeScheduledTask st, long now) {
		TimeZone tz = TimeZone.getTimeZone(st.getTimezone());
		final int[] months = fromMask(st.getMonthInYears());
		final int[] days = fromMask(st.getDayOfYears());
		final int[] weekdays = fromMask(st.getWeekdays());
		final int[] hours = fromMask(st.getHours());
		final int[] minutes = fromMask(st.getMinutes());
		final int[] seconds = fromMask(st.getSeconds());
		final CronTab ct = new CronTab(months, days, weekdays, hours, minutes, seconds, tz);
		long r = ct.calculateNextOccurance(now);

		for (int count = 0;; count++) {
			if (count > 1000)
				return -1;
			boolean skip = false;
			if (st.getWeekInYears() != 0 && !MH.allBits(st.getWeekInYears(), 1 << (ct.getWeekInYear(r) - 1))) {
				skip = true;
			} else if (st.getWeekInMonths() != 0 && !MH.allBits(st.getWeekInMonths(), 1 << (ct.getWeekInMonth(r) - 1))) {
				skip = true;
			} else if (st.getDayInMonths() != 0 && !MH.allBits(st.getDayInMonths(), 1 << (ct.getDayInMonth(r) - 1))) {
				skip = true;
			} else if (st.getDayOfWeekInMonths() != 0 && !MH.allBits(st.getDayOfWeekInMonths(), 1 << (ct.getDayOfWeekInMonth(r) - 1))) {
				skip = true;
			}
			if (skip) {
				r = ct.calculateNextOccurance(ct.getNextDay(r));
			} else
				break;
		}
		return r;
	}
	static private int[] fromMask(long value) {
		if (value == 0)
			return OH.EMPTY_INT_ARRAY;
		IntArrayList r = new IntArrayList(63);
		for (int i = 0; i < 63; i++) {
			if (MH.allBits(value, (1L << i)))
				r.add(i);
		}
		return r.toIntArray();
	}

	public static String describeType(VortexEntity t) {
		return t == null ? null : t.askSchema().askOriginalType().getSimpleName();
	}

	public static long addToVortexVaultRequest(byte[] data, VortexVaultRequest vvq, int minLengthToStore, VortexEyeState state) {
		if (data == null || data.length < minLengthToStore)
			return 0;
		final long r = state.createNextId();
		if (vvq.getDataToStore() == null)
			vvq.setDataToStore(new HashMap<Long, byte[]>());
		vvq.getDataToStore().put(r, data);
		return r;
	}

	final private static Tuple2<RSAPublicKey, RSAPrivateKey> key = RsaEncryptUtils.generateKey("vortex123");

	static public byte[] encrypt(byte data[]) {
		return RsaEncryptUtils.encrypt(key.getB(), data, true);
	}

	static public byte[] decrypt(byte data[]) {
		return RsaEncryptUtils.decrypt(key.getA(), data);
	}

	public static String decryptToString(byte[] data) {
		return data == null ? null : new String(RsaEncryptUtils.decrypt(key.getA(), data));
	}

	public static String joinMap(Map<String, String> data) {
		return SH.joinMap('|', '=', '\\', data);
	}

}
