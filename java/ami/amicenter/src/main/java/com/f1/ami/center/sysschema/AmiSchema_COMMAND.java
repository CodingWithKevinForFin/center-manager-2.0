package com.f1.ami.center.sysschema;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.center.table.AmiColumnWrapper;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableDef;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchema_COMMAND {

	final public AmiTableImpl table;
	private AmiPreparedRow preparedRow;
	final public AmiColumnWrapper arguments;
	final public AmiColumnWrapper commandId;
	final public AmiColumnWrapper priority;
	final public AmiColumnWrapper name;
	final public AmiColumnWrapper filter;
	final public AmiColumnWrapper callbacks;
	final public AmiColumnWrapper i;
	final public AmiColumnWrapper p;
	final public AmiColumnWrapper v;
	final public AmiColumnWrapper m;
	final public AmiColumnWrapper d;
	final public AmiColumnWrapper c;
	final public AmiColumnWrapper connectionId;
	final public AmiColumnWrapper fields;
	final public AmiColumnWrapper level;
	final public AmiColumnWrapper help;
	final public AmiColumnWrapper where;
	final public AmiColumnWrapper selectionMode;
	final public AmiColumnWrapper connectionRelayId;
	final public AmiColumnWrapper amiScript;
	final public AmiColumnWrapper enabled;
	final public AmiColumnWrapper style;

	public AmiSchema_COMMAND(AmiImdbImpl imdb, CalcFrameStack sf) {
		AmiTableDef def = new AmiTableDef(AmiTableUtils.DEFTYPE_AMI, AmiConsts.TYPE_COMMAND);
		this.connectionId = def.addColumn(AmiConsts.PARAM_CONNECTION_ID, AmiTable.TYPE_INT);
		this.connectionRelayId = def.addColumn(AmiConsts.PARAM_CONNECTION_RELAY_ID, AmiTable.TYPE_LONG);
		this.commandId = def.addColumn(AmiConsts.PARAM_COMMAND_ID, AmiTable.TYPE_STRING);
		this.arguments = def.addColumn(AmiConsts.PARAM_COMMAND_ARGUMENTS, AmiTable.TYPE_STRING);
		this.priority = def.addColumn(AmiConsts.PARAM_COMMAND_PRIORITY, AmiTable.TYPE_INT);
		this.name = def.addColumn(AmiConsts.PARAM_COMMAND_NAME, AmiTable.TYPE_STRING);
		this.filter = def.addColumn(AmiConsts.PARAM_COMMAND_FILTER, AmiTable.TYPE_STRING);
		this.where = def.addColumn(AmiConsts.PARAM_COMMAND_WHERE, AmiTable.TYPE_STRING);
		this.help = def.addColumn(AmiConsts.PARAM_COMMAND_HELP, AmiTable.TYPE_STRING);
		this.selectionMode = def.addColumn(AmiConsts.PARAM_COMMAND_SELECT_MODE, AmiTable.TYPE_STRING);
		this.callbacks = def.addColumn(AmiConsts.PARAM_COMMAND_CALLBACKS, AmiTable.TYPE_INT);
		this.amiScript = def.addColumn(AmiConsts.PARAM_COMMAND_AMISCRIPT, AmiTable.TYPE_STRING);
		this.enabled = def.addColumn(AmiConsts.PARAM_COMMAND_ENABLED, AmiTable.TYPE_ENUM);
		this.style = def.addColumn(AmiConsts.PARAM_COMMAND_STYLE, AmiTable.TYPE_STRING);
		this.level = def.addColumn(AmiConsts.PARAM_COMMAND_LEVEL, AmiTable.TYPE_INT);
		this.fields = def.addColumn(AmiConsts.PARAM_COMMAND_FIELDS, AmiTable.TYPE_STRING);
		this.i = def.addColumn("I", AmiTable.TYPE_STRING);
		this.p = def.addColumn("P", AmiTable.TYPE_ENUM);
		this.v = def.addColumn("V", AmiTable.TYPE_INT);
		this.m = def.addColumn("M", AmiTable.TYPE_LONG);
		this.d = def.addColumn("D", AmiTable.TYPE_LONG);
		this.c = def.addColumn("C", AmiTable.TYPE_LONG);
		this.table = (AmiTableImpl) imdb.createTable(def, sf);
		this.preparedRow = this.table.createAmiPreparedRow();
	}

	public void addCommand(int connectionId, long connectionRelayId, String id, String commandDefinitionId, String app, String arguments, int priority, String name, String filter,
			String where, String help, String selectionMode, int callBacks, String amiScript, String enabled, String style, int level, String fields, CalcFrameStack sf) {
		preparedRow.reset();
		preparedRow.setLong(this.connectionId, connectionId);
		preparedRow.setLong(this.connectionRelayId, connectionRelayId);
		preparedRow.setString(this.i, id);
		preparedRow.setString(this.commandId, commandDefinitionId);
		preparedRow.setString(this.p, app);
		preparedRow.setString(this.arguments, arguments);
		preparedRow.setLong(this.priority, priority);
		preparedRow.setString(this.name, name);
		preparedRow.setString(this.filter, filter);
		preparedRow.setString(this.where, where);
		preparedRow.setString(this.help, help);
		preparedRow.setString(this.selectionMode, selectionMode);
		preparedRow.setLong(this.callbacks, callBacks);
		preparedRow.setString(this.amiScript, amiScript);
		preparedRow.setString(this.enabled, enabled);
		preparedRow.setString(this.style, style);
		preparedRow.setLong(this.level, level);
		preparedRow.setString(this.fields, fields);
		this.table.insertAmiRow(preparedRow, sf);
	}

	public void removeCommandsForConnection(int connectionId, long connectionRelayId, CalcFrameStack sf) {
		for (int i = 0; i < table.getRowsCount(); i++) {
			AmiRowImpl row = table.getAmiRowAt(i);
			if (row.getLong(this.connectionId) == connectionId && row.getLong(this.connectionRelayId) == connectionRelayId) {
				table.removeAmiRow(row, sf);
				i--;
			}
		}
	}
}
