package com.vortex.agent.messages;

import java.util.List;

import com.f1.base.Message;

public interface VortexAgentOsAdapterRequest extends Message {

	String PARTITION_DEFAULT = "OS_ADAPTER_DEFAULT";

	int INSPECT_MACHINE = 1;
	int INSPECT_PROCESSES = 2;
	int INSPECT_FILESYSTEMS = 3;
	int INSPECT_NET_CONNECTIONS = 4;
	int INSPECT_NET_LINKS = 5;
	int INSPECT_NET_ADDRESSES = 6;
	int INSPECT_CRON = 7;
	int INSPECT_AGENT_MACHINE_EVENTS = 8;
	int RUN_COMMAND = 9;
	int FILE_SEARCH = 10;
	int RUN_DEPLOYMENT = 11;
	int SEND_SIGNAL = 12;
	int DELETE_FILE = 13;

	public int getCommandType();
	public void setCommandType(int type);

	public List<String> getCommandArguments();
	public void setCommandArguments(List<String> commandArguments);

	public String getPartitionId();
	public void setPartitionId(String partitionId);

	public Message getRequestMessage();
	public void setRequestMessage(Message message);

}
