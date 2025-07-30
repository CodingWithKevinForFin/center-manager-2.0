package com.f1.ami.center;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiDataEntity;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amicommon.msg.AmiRelayCommandDefMessage;
import com.f1.ami.amicommon.msg.AmiRelayConnectionMessage;
import com.f1.ami.amicommon.msg.AmiRelayErrorMessage;
import com.f1.ami.amicommon.msg.AmiRelayLoginMessage;
import com.f1.ami.amicommon.msg.AmiRelayLogoutMessage;
import com.f1.ami.amicommon.msg.AmiRelayMessage;
import com.f1.ami.amicommon.msg.AmiRelayObjectDeleteMessage;
import com.f1.ami.amicommon.msg.AmiRelayObjectMessage;
import com.f1.ami.amicommon.msg.AmiRelayStatusMessage;
import com.f1.ami.amicommon.msg.SingleParamMessage;
import com.f1.ami.center.sysschema.AmiSchema;
import com.f1.ami.center.sysschema.AmiSchema_CONNECTION;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTableDef;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.container.ContainerTools;
import com.f1.utils.ByteHelper;
import com.f1.utils.CH;
import com.f1.utils.DetailedException;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiCenterAmiUtils {

	private static final Logger log = LH.get();

	private static Map<Class, Byte> TYPES = new HashMap<Class, Byte>();

	public static final byte TYPE_STATUS = 2;
	public static final byte TYPE_LOGOUT = 4;
	public static final byte TYPE_LOGIN = 5;
	public static final byte TYPE_OBJECT = 6;
	public static final byte TYPE_COMMAND_DEF = 8;
	public static final byte TYPE_OBJECT_DELETE = 9;
	public static final byte TYPE_ERROR = 10;

	public static final byte TYPE_CONNECTION = 11;

	private static final byte STATUS_TYPE_LOGOUT = 0;
	static {
		TYPES.put(AmiRelayStatusMessage.class, TYPE_STATUS);
		TYPES.put(AmiRelayLogoutMessage.class, TYPE_LOGOUT);
		TYPES.put(AmiRelayLoginMessage.class, TYPE_LOGIN);
		TYPES.put(AmiRelayObjectMessage.class, TYPE_OBJECT);
		TYPES.put(AmiRelayCommandDefMessage.class, TYPE_COMMAND_DEF);
		TYPES.put(AmiRelayObjectDeleteMessage.class, TYPE_OBJECT_DELETE);
		TYPES.put(AmiRelayConnectionMessage.class, TYPE_CONNECTION);
		TYPES.put(AmiRelayErrorMessage.class, TYPE_ERROR);

	}

	public static void processAgentAmiDisconnect(AmiCenterState state, AmiCenterRelayState agentState, ContainerTools tools, long now,
			AmiCenterChangesMessageBuilder msgBuilderSink, CalcFrameStack sf) {

		final AmiSchema systemSchema = state.getAmiImdb().getSystemSchema();
		for (int id : agentState.getAmiConnectionIds()) {
			long eyeId = agentState.getAmiConnectionIdMapping(id);
			AmiCenterConnection connection = state.getAmiConnection(eyeId);
			if (connection == null)
				continue;
			state.removeAmiConnection(connection.getId());
			systemSchema.__CONNECTION.table.removeAmiRow(connection.getConnection(), sf);
			systemSchema.__COMMAND.removeCommandsForConnection(connection.getRelaysConnectionId(), connection.getAmiRelayId(), sf);
			//TODO: remove commands
		}
		systemSchema.__RELAY.removeRow(agentState.getRelayRow().getAmiId(), sf);
	}

	public static void processAgentAmiEvents(AmiCenterState state, AmiCenterRelayState agentState, List<AmiRelayMessage> amiEvents, Map<Short, String> agentStringMap,
			ContainerTools tools, long now, AmiCenterChangesMessageBuilder msgBuilderSink, CalcFrameStack sf) {
		processRelayNewKeys(agentStringMap, state, agentState);
		if (CH.isntEmpty(amiEvents)) {
			int l = amiEvents.size();
			state.incrementAmiMessageStats(AmiCenterState.STATUS_TYPE_PROCESS_RELAY_EVENT);
			state.incrementAmiMessageStats(AmiCenterState.STATUS_TYPE_PROCESS_EVENT, l);
			for (int i = 0; i < l; i++)
				mapRelayKeysToCenterKeys(amiEvents.get(i), agentState);

			for (int i = 0; i < l;)
				i = processRelayAmiEvent(amiEvents, i, state, agentState, tools, now, msgBuilderSink, sf);
		}
	}
	public static void processRelayNewKeys(Map<Short, String> agentStringMap, AmiCenterState state, AmiCenterRelayState agentState) {
		if (CH.isntEmpty(agentStringMap))
			for (Entry<Short, String> e : agentStringMap.entrySet())
				agentState.addAmiStringMapping(e.getKey().shortValue(), e.getValue(), state.getAmiKeyId(e.getValue()));
	}

	public static boolean mapRelayKeysToCenterKeys(SingleParamMessage m, AmiCenterRelayState agentState) {
		return mapRelayKeysToCenterKeys(m.getParams(), agentState);
	}
	public static boolean mapRelayKeysToCenterKeys(AmiRelayMessage m, AmiCenterRelayState agentState) {
		if (m instanceof SingleParamMessage)
			return mapRelayKeysToCenterKeys((SingleParamMessage) m, agentState);
		return false;
	}
	public static boolean mapRelayKeysToCenterKeys(byte[] params, AmiCenterRelayState agentState) {
		if (params != null) {
			int end = ByteHelper.readShort(params, 0) * 2 + 2;
			for (int pos = 2; pos < end; pos += 2) {
				short agentKey = ByteHelper.readShort(params, pos);
				ByteHelper.writeShort(agentState.getAmiStringMap(agentKey), params, pos);
			}
		}
		return true;
	}
	public static int processRelayAmiEvent(List<AmiRelayMessage> amiEvents, int position, AmiCenterState state, AmiCenterRelayState agentState, ContainerTools tools, long now,
			AmiCenterChangesMessageBuilder msgBuilderSink, CalcFrameStack sf) {
		AmiImdbImpl db = state.getAmiImdb();
		AmiSchema sysSchema = db.getSystemSchema();
		while (position < amiEvents.size()) {
			final AmiRelayMessage amiEvent = amiEvents.get(position++);
			try {
				long cnt = state.incrementAmiMessageCount(1);
				if (cnt % 100000 == 0)
					LH.info(log, "Received ", cnt, " AMI message(s)");
				long eyeConnectionId = agentState.getAmiConnectionIdMapping(amiEvent.getConnectionId());
				AmiCenterConnection connection = state.getAmiConnection(eyeConnectionId);
				short amiKeyId = amiEvent.getAppIdStringKey();
				amiKeyId = agentState.getAmiStringMap(amiKeyId);
				AmiCenterApplication eApp = state.getAmiAppByAppId(amiKeyId);
				if (eApp == null) {
					eApp = state.putAmiApplication(amiKeyId, state.getAmiKeyString(amiKeyId));
				}

				boolean error = false;
				final byte type;
				if (amiEvent instanceof AmiRelayObjectMessage) {
					type = TYPE_OBJECT;
				} else
					type = CH.getOrThrow(TYPES, amiEvent.askSchema().askOriginalType()).byteValue();
				switch (type) {
					case TYPE_CONNECTION: {
						if (agentState.getAmiConnectionIdMapping(amiEvent.getConnectionId()) == -1) {
							AmiRelayConnectionMessage event = (AmiRelayConnectionMessage) amiEvent;
							AmiSchema_CONNECTION connTable = sysSchema.__CONNECTION;
							AmiRow row = connTable.addRow(event.getConnectionId(), event.getConnectionTime(), agentState.getRelayId(), event.getRemoteIp(), event.getRemotePort(),
									agentState.getMachineState().getId(), sf);
							agentState.addAmiConnectionMapping(amiEvent.getConnectionId(), row.getAmiId());
							state.putAmiConnection(row, agentState.getMachineState().getId(), event.getConnectionId());
						}
						break;
					}
					case TYPE_ERROR: {
						error = true;
						break;
					}
					case TYPE_LOGIN: {
						if (connection == null) {
							LH.warning(log, "message from unknown agent connection: ", amiEvent);
							break;
						}
						state.incrementAmiMessageStats(AmiCenterState.STATUS_TYPE_LOGIN);
						AmiRelayLoginMessage event = (AmiRelayLoginMessage) amiEvent;
						String appId = event.getAppId();
						eApp = state.getAmiAppByAppId(amiKeyId);
						if (eApp == null)
							eApp = state.putAmiApplication(amiKeyId, appId);

						AmiSchema_CONNECTION connTable = sysSchema.__CONNECTION;
						AmiRow row = connection.getConnection();
						connTable.updateRowForLogin(row, appId, event.getOptions(), event.getPlugin(), sf);
						connection.setAppId(amiKeyId);
						break;
					}
					case TYPE_LOGOUT: {
						if (connection == null) {
							LH.warning(log, "message from unknown agent connection: ", amiEvent);
							break;
						}
						state.incrementAmiMessageStats(STATUS_TYPE_LOGOUT);
						AmiRelayLogoutMessage event = (AmiRelayLogoutMessage) amiEvent;
						state.removeAmiConnection(connection.getId());
						AmiSchema_CONNECTION connTable = sysSchema.__CONNECTION;
						sysSchema.__COMMAND.removeCommandsForConnection(connection.getRelaysConnectionId(), connection.getAmiRelayId(), sf);
						connTable.table.removeAmiRow(connection.getConnection(), sf);
						break;
					}
					case TYPE_OBJECT_DELETE: {

						AmiRelayObjectDeleteMessage event = (AmiRelayObjectDeleteMessage) amiEvent;
						if (SH.startsWith(event.getType(), "__")) {
							LH.info(log, "Can not use reserved type: " + event.getType());
							break;
						}
						short typeId = state.getAmiKeyId(event.getType());
						final AmiTableImpl table = db.getAmiTable(typeId);
						final String id = event.getId();
						final byte[] params = event.getParams();
						if (table != null) {
							AmiCenterAmiUtilsForTable.onDelete(state, typeId, table, id, params, eApp, now, sf);
							break;
						} else {
							if (state.getHdb().getTablesSorted().contains(event.getType())) {
								LH.info(log, "Delete (D) not supported for historical tables: ", event.getType());
							} else {
								switch (state.getUnknownTypeBehaviour()) {
									case AmiCenterState.UNKNOWN_TYPE_BEHAVIOUR_IGNORE:
										break;
									case AmiCenterState.UNKNOWN_TYPE_BEHAVIOUR_LOG_ERROR:
										LH.info(log, "Unknown Type '" + event.getType(), "': " + event);
										break;
									case AmiCenterState.UNKNOWN_TYPE_BEHAVIOUR_CREATE_TABLE:
										break;
								}
							}
						}
						break;
					}
					case TYPE_OBJECT: {
						final AmiRelayObjectMessage event = (AmiRelayObjectMessage) amiEvent;
						short typeId = state.getAmiKeyId(event.getType());
						if (SH.startsWith(event.getType(), "__")) {
							LH.info(log, "Can not use reserved type: " + event.getType());
							break;
						}
						AmiTableImpl table = db.getAmiTable(typeId);
						if (table != null) {
							position = addToPublicTable(amiEvents, position, state, now, cnt, eApp, event, typeId, table, sf);
						} else {
							if (!state.getHdb().onRealtimeEvent(event)) {
								switch (state.getUnknownTypeBehaviour()) {
									case AmiCenterState.UNKNOWN_TYPE_BEHAVIOUR_IGNORE:
										break;
									case AmiCenterState.UNKNOWN_TYPE_BEHAVIOUR_LOG_ERROR:
										LH.info(log, "Unknown Type '" + event.getType(), "': " + event);
										break;
									case AmiCenterState.UNKNOWN_TYPE_BEHAVIOUR_CREATE_TABLE: {
										table = createPublicTable(state, event, sf);
										if (table == null)
											position++;
										else
											position = addToPublicTable(amiEvents, position, state, now, cnt, eApp, event, typeId, table, sf);
										break;
									}
								}
							}
						}
						break;
					}
					case TYPE_STATUS: {
						LH.info(log, "Status Events (S) not supported");
						break;
					}
					case TYPE_COMMAND_DEF: {
						if (connection == null) {
							LH.warning(log, "message from unknown agent connection: ", amiEvent);
							break;
						}
						AmiRelayCommandDefMessage event = (AmiRelayCommandDefMessage) amiEvent;
						String commandI = connection.getId() + ":" + event.getCommandId();
						String appId = eApp.getAppName();
						if (event.getLevel() == 0) {
							AmiRowImpl existing = sysSchema.__COMMAND.table.getAmiRow(appId, commandI, null);
							if (existing != null) {
								sysSchema.__COMMAND.table.removeAmiRow(existing, sf);
							}
						} else {
							sysSchema.__COMMAND.addCommand(connection.getRelaysConnectionId(), connection.getAmiRelayId(), commandI, event.getCommandId(), appId,
									event.getArgumentsJson(), event.getPriority(), event.getTitle(), event.getFilterClause(), event.getWhereClause(), event.getHelp(),
									event.getSelectMode(), event.getCallbacksMask(), event.getAmiScript(), event.getEnabledExpression(), event.getStyle(), event.getLevel(),
									event.getFields(), sf);
						}
					}
				}
				if (connection != null) {
					if (error)
						connection.incrementErrorsCount(1);
					else
						connection.incrementMessagesCount(1);
				}
			} catch (Exception e) {
				DetailedException de = new DetailedException(e);
				de.set("AmiEvent", amiEvent);
				LH.warning(log, "Error processing ami event", de);
			}
		}
		return position;

	}
	private static int addToPublicTable(List<AmiRelayMessage> amiEvents, int position, AmiCenterState state, long now, long cnt, AmiCenterApplication eApp,
			AmiRelayObjectMessage event, short typeId, AmiTableImpl table, CalcFrameStack sf) {
		AmiCenterAmiUtilsForTable.onObject(state, typeId, table, event, eApp, now, sf);
		while (position < amiEvents.size()) {
			final AmiRelayMessage amiEvent2 = amiEvents.get(position);
			if (!(amiEvent2 instanceof AmiRelayObjectMessage))
				break;
			final AmiRelayObjectMessage event2 = (AmiRelayObjectMessage) amiEvent2;
			if (!event.getType().equals(event2.getType()))
				break;
			cnt = state.incrementAmiMessageCount(1);
			if (cnt % 100000 == 0)
				LH.info(log, "Received ", cnt, " AMI message(s)");
			AmiCenterAmiUtilsForTable.onObject(state, typeId, table, event2, eApp, now, sf);
			position++;
		}
		return position;
	}
	private static AmiTableImpl createPublicTable(AmiCenterState state, AmiRelayObjectMessage event, CalcFrameStack sf) {
		AmiImdbImpl db = state.getAmiImdb();
		List<String> columnNames = new ArrayList<String>();
		List<Byte> columnTypes = new ArrayList<Byte>();
		List<Map<String, String>> columnOptions = new ArrayList<Map<String, String>>();
		byte[] data = event.getParams();
		final int keysLength = ByteHelper.readShort(data, 0);
		for (int i = 0, valPos = (keysLength << 1) + 2, len; i < keysLength; i++, valPos += len) {
			len = AmiUtils.getDataLength(data, valPos);
			byte type = data[valPos];
			if (type == AmiDataEntity.PARAM_TYPE_NULL)
				continue;
			final short key = ByteHelper.readShort(data, (i << 1) + 2);
			String name = state.getAmiKeyString(key);
			columnNames.add(name);
			if (type == AmiDataEntity.PARAM_TYPE_INT1 || type == AmiDataEntity.PARAM_TYPE_INT2 || type == AmiDataEntity.PARAM_TYPE_INT3)
				type = AmiDatasourceColumn.TYPE_INT;
			columnTypes.add(type);
			columnOptions.add(Collections.EMPTY_MAP);
		}
		if (event.getId() != null) {
			columnNames.add(SH.toString(AmiConsts.RESERVED_PARAM_ID));
			columnTypes.add(AmiDatasourceColumn.TYPE_STRING);
			columnOptions.add(Collections.EMPTY_MAP);
		}
		if (event.getExpires() != 0) {
			columnNames.add(SH.toString(AmiConsts.RESERVED_PARAM_EXPIRED));
			columnTypes.add(AmiDatasourceColumn.TYPE_LONG);
			columnOptions.add(Collections.EMPTY_MAP);
		}
		if (columnNames.size() == 0) {
			LH.warning(log, "Auto-create table '", event.getType(), "' failed: No fields specified, at least one column is required");
			return null;
		}
		LH.info(log, "Table '", event.getType(), "' does not exist and ", AmiCenterProperties.PROPERTY_AMI_UNKNOWN_REALTIME_TABLE_BEHAVIOR,
				"=CREATE_TABLE so auto-creating table with columns: ", columnNames);
		AmiTableDef tableDef = new AmiTableDef(AmiTableUtils.DEFTYPE_USER, event.getType(), columnNames, columnTypes, columnOptions, 100, 1000, AmiTableDef.ON_UNDEFINED_COLUMN_ADD,
				null);
		return (AmiTableImpl) db.createTable(tableDef, sf);
	}

}
