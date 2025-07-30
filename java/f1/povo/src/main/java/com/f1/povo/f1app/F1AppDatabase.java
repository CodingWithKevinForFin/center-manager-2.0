package com.f1.povo.f1app;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.DB")
public interface F1AppDatabase extends F1AppEntity {

	byte PID_NAME=2;
	byte PID_ACTIVE_CONNECTIONS_COUNT=3;
	byte PID_CONNECTIONS_COUNT=4;
	byte PID_SQL_SENT_COUNT=5;
	byte PID_URL=6;
	
	@PID(PID_NAME)
	public String getName();
	public void setName(String name);

	@PID(PID_ACTIVE_CONNECTIONS_COUNT)
	public int getActiveConnectionsCount();
	public void setActiveConnectionsCount(int activeConnectionsCount);

	@PID(PID_CONNECTIONS_COUNT)
	public int getConnectionsCount();
	public void setConnectionsCount(int activeConnectionsCount);

	@PID(PID_SQL_SENT_COUNT)
	public long getSqlSentCount();
	public void setSqlSentCount(long messagesSent);

	@PID(PID_URL)
	public String getUrl();
	public void setUrl(String localSocket);
}
