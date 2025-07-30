package com.f1.vortexcommon.msg.agent;

import java.util.List;
import java.util.Map;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.vortexcommon.msg.VortexEntity;

@VID("F1.VA.S")
public interface VortexAgentSnapshot extends PartialMessage {

	byte PID_PROCESSES = 1;
	byte PID_CONNECTIONS = 2;
	byte PID_FILE_SYSTEMS = 3;
	byte PID_MACHINE = 5;
	byte PID_AGENT_PROCESS_UID = 6;
	byte PID_IS_SNAPSHOT = 10;
	byte PID_NET_LINKS = 11;
	byte PID_NET_ADDRESSES = 12;
	byte PID_CRON = 13;
	byte PID_AGENT_MACHINE_EVENTS = 14;
	byte PID_F1_APP_SNAPSHOTS = 15;
	byte PID_ENTITIES = 16;

	@PID(PID_AGENT_PROCESS_UID)
	public String getAgentProcessUid();
	public void setAgentProcessUid(String processUid);

	@PID(PID_ENTITIES)
	public List<VortexEntity> getEntities();
	public void setEntities(List<VortexEntity> entities);

	@PID(PID_PROCESSES)
	Map<String, VortexAgentProcess> getProcesses();
	public void setProcesses(Map<String, VortexAgentProcess> processes);

	@PID(PID_CONNECTIONS)
	Map<String, VortexAgentNetConnection> getConnections();
	public void setConnections(Map<String, VortexAgentNetConnection> connections);

	@PID(PID_FILE_SYSTEMS)
	Map<String, VortexAgentFileSystem> getFileSystems();
	public void setFileSystems(Map<String, VortexAgentFileSystem> fileSystems);

	@PID(PID_MACHINE)
	VortexAgentMachine getMachine();
	public void setMachine(VortexAgentMachine machine);

	@PID(PID_IS_SNAPSHOT)
	boolean getIsSnapshot();
	public void setIsSnapshot(boolean isSnapshot);

	@PID(PID_NET_LINKS)
	public Map<String, VortexAgentNetLink> getNetLinks();
	public void setNetLinks(Map<String, VortexAgentNetLink> links);

	@PID(PID_NET_ADDRESSES)
	public Map<String, VortexAgentNetAddress> getNetAddresses();
	public void setNetAddresses(Map<String, VortexAgentNetAddress> links);

	@PID(PID_CRON)
	public Map<String, VortexAgentCron> getCron();
	void setCron(Map<String, VortexAgentCron> chrons);

	@PID(PID_AGENT_MACHINE_EVENTS)
	public Map<String, VortexAgentMachineEventStats> getAgentMachineEvents();
	public void setAgentMachineEvents(Map<String, VortexAgentMachineEventStats> machineEvents);

	@PID(PID_F1_APP_SNAPSHOTS)
	public Map<String, F1AppInstance> getF1AppSnapshots();
	public void setF1AppSnapshots(Map<String, F1AppInstance> f1Snapshots);

	public VortexAgentSnapshot clone();

}
