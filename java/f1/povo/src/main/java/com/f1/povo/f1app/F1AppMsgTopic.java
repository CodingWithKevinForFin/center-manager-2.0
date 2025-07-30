package com.f1.povo.f1app;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.MT")
public interface F1AppMsgTopic extends F1AppEntity {

	byte PID_ID = 1;
	byte PID_TOPIC_NAME = 2;
	byte PID_BYTES_SENT = 4;
	byte PID_MESSAGES_SENT_COUNT = 5;
	byte PID_BYTES_RECEIVED = 6;
	byte PID_MESSAGES_RECEIVED_COUNT = 7;
	byte PID_LOCAL_PORTS = 8;
	byte PID_IS_OUTBOUND = 9;
	byte PID_CONNECTIONS_COUNT = 10;

	@PID(PID_TOPIC_NAME)
	public String getTopicName();
	public void setTopicName(String name);

	@PID(PID_BYTES_SENT)
	public long getBytesSent();
	public void setBytesSent(long bytesSent);

	@PID(PID_MESSAGES_SENT_COUNT)
	public long getMessagesSentCount();
	public void setMessagesSentCount(long messagesSent);

	@PID(PID_BYTES_RECEIVED)
	public long getBytesReceived();
	public void setBytesReceived(long bytesSent);

	@PID(PID_MESSAGES_RECEIVED_COUNT)
	public long getMessagesReceivedCount();
	public void setMessagesReceivedCount(long messagesSent);

	@PID(PID_LOCAL_PORTS)
	public void setLocalPorts(int[] localConnectionPorts);
	public int[] getLocalPorts();

	@PID(PID_IS_OUTBOUND)
	public void setIsOutbound(boolean isWrite);
	public boolean getIsOutbound();

	@PID(PID_CONNECTIONS_COUNT)
	public void setConnectionsCount(short l);
	public short getConnectionsCount();

}
