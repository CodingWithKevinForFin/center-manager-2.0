package com.f1.ami.center.sysschema;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.table.AmiColumnWrapper;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiPreparedRowImpl;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableDef;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrameStack;

public class AmiSchema_CONNECTION {

	final public AmiTableImpl table;
	final public AmiCenterState state;
	final public AmiColumnWrapper appId;
	final public AmiColumnWrapper errorsCount;
	final public AmiColumnWrapper connectionId;
	final public AmiColumnWrapper machineId;
	final public AmiColumnWrapper messagesCount;
	final public AmiColumnWrapper options;
	final public AmiColumnWrapper plugins;
	final public AmiColumnWrapper relayId;
	final public AmiColumnWrapper remotePort;
	final public AmiColumnWrapper connectionTime;
	final public AmiColumnWrapper remoteHost;
	final public AmiPreparedRowImpl preparedRow;

	public AmiSchema_CONNECTION(AmiImdbImpl imdb, CalcFrameStack sf) {

		AmiTableDef def = new AmiTableDef(AmiTableUtils.DEFTYPE_AMI, AmiConsts.TYPE_CONNECTION);

		this.appId = def.addColumn(AmiConsts.PARAM_CONNECTION_APPID, AmiTable.TYPE_ENUM);
		this.errorsCount = def.addColumn(AmiConsts.PARAM_CONNECTION_ERRORSCOUNT, AmiTable.TYPE_LONG);
		this.connectionId = def.addColumn(AmiConsts.PARAM_CONNECTION_ID, AmiTable.TYPE_LONG);
		this.machineId = def.addColumn(AmiConsts.PARAM_CONNECTION_MACHINEID, AmiTable.TYPE_LONG);
		this.messagesCount = def.addColumn(AmiConsts.PARAM_CONNECTION_MESSAGESCOUNT, AmiTable.TYPE_LONG);
		this.options = def.addColumn(AmiConsts.PARAM_CONNECTION_OPTIONS, AmiTable.TYPE_STRING);
		this.plugins = def.addColumn(AmiConsts.PARAM_CONNECTION_PLUGINS, AmiTable.TYPE_STRING);
		this.relayId = def.addColumn(AmiConsts.PARAM_CONNECTION_RELAY_ID, AmiTable.TYPE_STRING);
		this.remotePort = def.addColumn(AmiConsts.PARAM_CONNECTION_REMOTE_PORT, AmiTable.TYPE_LONG);
		this.remoteHost = def.addColumn(AmiConsts.PARAM_CONNECTION_REMOTE_HOST, AmiTable.TYPE_STRING);
		this.connectionTime = def.addColumn(AmiConsts.PARAM_CONNECTION_TIME, AmiTable.TYPE_LONG);
		this.table = (AmiTableImpl) imdb.createTable(def, EmptyCalcFrameStack.INSTANCE);
		this.preparedRow = this.table.createAmiPreparedRow();
		this.state = ((AmiImdbImpl) imdb).getState();
	}

	public void addRow() {

	}

	public AmiRow addRow(long connectionId, long connectionTime, String relayId, String remoteHost, int remotePort, long machineId, CalcFrameStack sf) {
		this.preparedRow.reset();
		preparedRow.setLong(this.connectionTime, connectionTime);
		preparedRow.setLong(this.connectionId, connectionId);
		preparedRow.setString(this.relayId, relayId);
		preparedRow.setLong(this.remotePort, remotePort);
		preparedRow.setString(this.remoteHost, remoteHost);
		preparedRow.setLong(this.machineId, machineId);
		return this.table.insertAmiRow(preparedRow, sf);
	}

	public void updateRowForLogin(AmiRow existing, String appId, String options, String plugins, CalcFrameStack sf) {
		this.preparedRow.reset();
		preparedRow.setString(this.appId, appId);
		preparedRow.setString(this.options, options);
		preparedRow.setString(this.plugins, plugins);
		this.table.updateAmiRow(existing.getAmiId(), preparedRow, sf);
	}
}
