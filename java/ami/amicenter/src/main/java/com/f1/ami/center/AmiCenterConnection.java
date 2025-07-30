package com.f1.ami.center;

import com.f1.ami.center.sysschema.AmiSchema_CONNECTION;
import com.f1.ami.center.table.AmiRow;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiCenterConnection {

	private AmiRow connection;
	private long errorsCount = 0;
	private long messagesCount = 0;
	private long publishedErrorsCount = 0;
	private long publishedMessagesCount = 0;
	private int appId;
	final private long id;
	final private long amiRelayId;
	final private int relaysConnectionId;
	private AmiSchema_CONNECTION connectionTable;

	public AmiCenterConnection(AmiCenterState state, AmiRow object, long amiRelayId, int relaysConnectionid) {
		this.setConnection(object);
		this.connectionTable = state.getAmiImdb().getSystemSchema().__CONNECTION;
		this.connection = object;
		this.id = connection.getAmiId();
		this.amiRelayId = amiRelayId;
		this.relaysConnectionId = relaysConnectionid;
	}

	public long getAmiRelayId() {
		return amiRelayId;
	}

	public AmiRow getConnection() {
		return connection;
	}

	public void setConnection(AmiRow connection) {
		this.connection = connection;
	}

	public void incrementErrorsCount(int count) {
		errorsCount += count;
	}
	public void incrementMessagesCount(int count) {
		messagesCount += count;
	}

	public boolean needsStatsUpdate() {
		return errorsCount != publishedErrorsCount || messagesCount != publishedMessagesCount;
	}

	public void applyCounts(CalcFrameStack session) {
		this.connectionTable.errorsCount.setLong(this.connection, this.errorsCount, session);
		this.connectionTable.messagesCount.setLong(this.connection, this.messagesCount, session);
		this.publishedErrorsCount = errorsCount;
		this.publishedMessagesCount = messagesCount;
	}

	public int getAppId() {
		return appId;
	}

	public long getId() {
		return id;
	}

	public void setAppId(int appId) {
		this.appId = appId;
	}

	public int getRelaysConnectionId() {
		return relaysConnectionId;
	}

}
