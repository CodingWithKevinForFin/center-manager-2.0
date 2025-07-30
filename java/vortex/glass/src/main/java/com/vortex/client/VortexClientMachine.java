package com.vortex.client;

import java.util.Map;
import java.util.logging.Logger;

import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.MapInMap;
import com.f1.vortexcommon.msg.agent.VortexAgentCron;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentFileSystem;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.agent.VortexAgentNetAddress;
import com.f1.vortexcommon.msg.agent.VortexAgentNetConnection;
import com.f1.vortexcommon.msg.agent.VortexAgentNetLink;

public class VortexClientMachine extends VortexClientMachineEntity<VortexAgentMachine> {

	public static final Logger log = LH.get(VortexClientMachine.class);

	final private MapInMap<Integer, Long, VortexClientNetConnection> connectionsByLocalPortAndId = new MapInMap<Integer, Long, VortexClientNetConnection>();
	final private MapInMap<Integer, Long, VortexClientNetConnection> connectionsByLocalPidAndId = new MapInMap<Integer, Long, VortexClientNetConnection>();
	final private IntKeyMap<VortexClientProcess> processesByPid = new IntKeyMap<VortexClientProcess>();

	final private LongKeyMap<VortexClientCron> machineCrons = new LongKeyMap<VortexClientCron>();
	final private LongKeyMap<VortexClientProcess> machineProcesses = new LongKeyMap<VortexClientProcess>();
	final private LongKeyMap<VortexClientNetConnection> machineNetConnections = new LongKeyMap<VortexClientNetConnection>();
	final private LongKeyMap<VortexClientFileSystem> machineFileSystems = new LongKeyMap<VortexClientFileSystem>();
	final private LongKeyMap<VortexClientNetLink> machineNetLinks = new LongKeyMap<VortexClientNetLink>();
	final private LongKeyMap<VortexClientNetAddress> machineNetAddresses = new LongKeyMap<VortexClientNetAddress>();

	//private LongKeyMap<WebAgentMachineNode<Agent>> machineMachineEvents ;
	//final private VortexAgentMachine machine;
	final private long id;
	private VortexClientManager manager;

	private LongKeyMap<VortexClientMachineEntity<?>> machines = new LongKeyMap<VortexClientMachineEntity<?>>();

	public VortexClientMachine(VortexClientManager webAgentManager, VortexAgentMachine machine) {
		super(VortexAgentEntity.TYPE_MACHINE, machine);
		this.manager = webAgentManager;
		//this.machine = machine;
		this.id = machine.getId();
		setMachine(this);
		this.machines.put(machine.getId(), this);
	}

	public long getId() {
		return id;
	}

	public VortexClientManager getManager() {
		return manager;
	}

	public void addCron(VortexClientCron node) {
		node.setMachine(this);
		machineCrons.put(node.getData().getId(), node);
	}
	public void addProcess(VortexClientProcess node) {
		node.setMachine(this);
		machineProcesses.put(node.getData().getId(), node);
		this.processesByPid.put(node.getPid(), node);
		Map<Long, VortexClientNetConnection> connections = this.connectionsByLocalPidAndId.get(node.getPid());
		if (connections != null)
			for (VortexClientNetConnection connection : connections.values()) {
				connection.setProcess(node);
				node.addConnection(connection);
				//TODO:notify
			}
	}
	public void addNetLink(VortexClientNetLink node) {
		node.setMachine(this);
		machineNetLinks.put(node.getData().getId(), node);
	}
	public void addNetConnection(VortexClientNetConnection node) {
		node.setMachine(this);
		VortexAgentNetConnection d = node.getData();
		machineNetConnections.put(d.getId(), node);
		connectionsByLocalPortAndId.putMulti(d.getLocalPort(), node.getId(), node);
		connectionsByLocalPidAndId.putMulti(node.getLocalPid(), node.getId(), node);
		VortexClientProcess process = this.processesByPid.get(node.getLocalPid());
		if (process != null) {
			node.setProcess(process);
			process.addConnection(node);
			//TODO:notify
		}
		if (d.getLocalPort() == 35941 || d.getForeignPort() == 35941)
			log.info("port: " + d);
		if (d.getState() != VortexAgentNetConnection.STATE_LISTEN) {
			if (node.getIsLoopback()) {
				VortexClientNetConnection remoteConnection = null;
				for (VortexClientNetConnection webConnection : CH.values(this.connectionsByLocalPortAndId.get(d.getForeignPort()))) {
					if (webConnection.getIsLoopback() && webConnection.getData().getForeignPort() == d.getLocalPort()) {
						remoteConnection = webConnection;
						break;
					}
				}
				if (remoteConnection != null) {
					remoteConnection.setRemoteConnection(node);
					node.setRemoteConnection(remoteConnection);
					//log.info("Linking: " + node.getId() + " <==> " + remoteConnection.getId());
				}
			} else {
				VortexClientNetAddress foreignMachine = manager.getNetAddressByIp(d.getForeignHost(), this);
				if (foreignMachine != null) {
					VortexClientNetConnection remoteConnection = null;
					remoteConnection = foreignMachine.getMachine().getNetConnectionByLocalPortAndForeignHostPort(d.getForeignPort(), d.getLocalHost(), d.getLocalPort());
					if (remoteConnection != null) {
						remoteConnection.setRemoteConnection(node);
						node.setRemoteConnection(remoteConnection);
						//TODO: notify
					}
				} //else
					//LH.warning(log, "Could not get machine for foreign host: ", d.getForeignHost());
			}
		}
	}
	private VortexClientNetConnection getNetConnectionByLocalPortAndForeignHostPort(int localPort, String remoteHost, int remotePort) {
		for (VortexClientNetConnection webConnection : CH.values(this.connectionsByLocalPortAndId.get(localPort))) {
			VortexAgentNetConnection connection = webConnection.getData();
			if (!webConnection.getIsLoopback() && connection.getForeignPort() == remotePort && remoteHost.equals(connection.getForeignHost()))
				return webConnection;
		}
		return null;
	}

	public void addFileSystem(VortexClientFileSystem node) {
		node.setMachine(this);
		machineFileSystems.put(node.getData().getId(), node);
	}
	//public void addNetLink(WebAgentMachineNode<AgentNetLink> node) {
	//node.setMachine(this);
	//machineNetLinks.put(node.getData().getId(), node);
	//}

	public void addNetAddress(VortexClientNetAddress node) {
		node.setMachine(this);
		machineNetAddresses.put(node.getData().getId(), node);
	}

	public VortexClientCron getCron(long id) {
		return machineCrons.get(id);
	}

	public VortexClientProcess getProcess(long id) {
		return machineProcesses.get(id);
	}
	public VortexClientNetConnection getNetConnection(long id) {
		return machineNetConnections.get(id);
	}
	public VortexClientFileSystem getFileSystem(long id) {
		return machineFileSystems.get(id);
	}
	public VortexClientNetLink getNetLink(long id) {
		return machineNetLinks.get(id);
	}
	public VortexClientNetAddress getNetAddress(long id) {
		return machineNetAddresses.get(id);
	}

	public VortexClientMachineEntity<VortexAgentCron> removeCron(long id) {
		VortexClientMachineEntity<VortexAgentCron> r = machineCrons.remove(id);
		if (r != null)
			r.unbind();
		return r;
	}
	public VortexClientProcess removeProcess(long id) {
		VortexClientProcess r = machineProcesses.remove(id);
		if (r != null) {
			processesByPid.remove(r.getPid());
			for (VortexClientNetConnection connection : CH.l(r.getConnections())) {
				connection.setProcess(null);
				r.removeConnection(connection);
				//TODO:notify
			}
			r.unbind();
		}
		return r;
	}
	public VortexClientMachineEntity<VortexAgentNetConnection> removeNetConnection(long id) {
		VortexClientNetConnection r = machineNetConnections.remove(id);
		if (r != null) {
			connectionsByLocalPidAndId.removeMulti(r.getLocalPid(), r.getId());
			connectionsByLocalPortAndId.removeMulti(r.getData().getLocalPort(), r.getId());
			r.unbind();
			VortexClientProcess process = r.getProcess();
			if (process != null) {
				process.removeConnection(r);
				r.setRemoteConnection(null);
				//TODO: notify
			}
			VortexClientNetConnection remote = r.getRemoteConnection();
			if (remote != null) {
				remote.setRemoteConnection(null);
				r.setRemoteConnection(null);
				//TODO:notify
			}
		}
		return r;
	}
	public VortexClientMachineEntity<VortexAgentFileSystem> removeFileSystem(long id) {
		VortexClientMachineEntity<VortexAgentFileSystem> r = machineFileSystems.remove(id);
		if (r != null)
			r.unbind();
		return r;
	}
	public VortexClientMachineEntity<VortexAgentNetLink> removeNetLink(long id) {
		VortexClientMachineEntity<VortexAgentNetLink> r = machineNetLinks.remove(id);
		if (r != null)
			r.unbind();
		return r;
	}
	public VortexClientMachineEntity<VortexAgentNetAddress> removeNetAddress(long id) {
		VortexClientMachineEntity<VortexAgentNetAddress> r = machineNetAddresses.remove(id);
		if (r != null)
			r.unbind();
		return r;
	}

	public Iterable<VortexClientNetAddress> getNetAddresses() {
		return this.machineNetAddresses.values();
	}

	public Iterable<VortexClientProcess> getProcesses() {
		return machineProcesses.values();
	}
	public Iterable<VortexClientNetConnection> getNetConnections() {
		return machineNetConnections.values();
	}

	//public WebAgentMachineNode<?> get(byte type, long id) {
	//return getMap(type).get(id);
	//}
	//public void add(WebAgentMachineNode<?> node) {
	//getMap(node.getType()).put(node.getId(), node);
	//node.setParent(this);
	//}

	private LongKeyMap<VortexClientMachineEntity<?>> getMap(byte type) {
		switch (type) {
			case VortexAgentEntity.TYPE_PROCESS:
				return (LongKeyMap) this.machineProcesses;
			case VortexAgentEntity.TYPE_FILE_SYSTEM:
				return (LongKeyMap) this.machineFileSystems;
			case VortexAgentEntity.TYPE_NET_ADDRESS:
				return (LongKeyMap) this.machineNetAddresses;
			case VortexAgentEntity.TYPE_NET_CONNECTION:
				return (LongKeyMap) this.machineNetConnections;
			case VortexAgentEntity.TYPE_NET_LINK:
				return (LongKeyMap) this.machineNetLinks;
			case VortexAgentEntity.TYPE_MACHINE:
				return this.machines;
				//			case AgentRevision.TYPE_DB_DATABASE:
				//				return (LongKeyMap) this.machineDbDatabases;
				//			case AgentRevision.TYPE_DB_TABLE:
				//				return (LongKeyMap) this.machineDbTables;
				//			case AgentRevision.TYPE_DB_COLUMN:
				//				return (LongKeyMap) this.machineDbColumns;
				//			case AgentRevision.TYPE_DB_PRIVILEDGE:
				//				return (LongKeyMap) this.machineDbPrivileges;
				//			case AgentRevision.TYPE_DB_OBJECT:
				//				return (LongKeyMap) this.machineDbObjects;
			case VortexAgentEntity.TYPE_DB_DATABASE:
			case VortexAgentEntity.TYPE_DB_TABLE:
			case VortexAgentEntity.TYPE_DB_COLUMN:
			case VortexAgentEntity.TYPE_DB_PRIVILEDGE:
			case VortexAgentEntity.TYPE_DB_OBJECT:
			case VortexAgentEntity.TYPE_DB_SERVER:
				return LongKeyMap.EMPTY;
			case VortexAgentEntity.TYPE_CRON:
				return (LongKeyMap) this.machineCrons;
			default:
				throw new RuntimeException("type: " + type);
		}
	}

	public Iterable<VortexClientMachineEntity<?>> getNodes(byte nodeType) {
		return getMap(nodeType).values();
	}

	public VortexClientNetAddress getLoopbackAddress() {
		for (VortexClientNetAddress address : machineNetAddresses.values())
			if (address.isLoopback())
				return address;
		LH.warning(log, "Machine does not have a loopback address: " + getHostName());
		return null;
	}
	public String getHostName() {
		return getData().getHostName();
	}

	public String getMachineUid() {
		return getData().getMachineUid();
	}

	public String getProcessUid() {
		return getData() == null ? null : getData().getAgentProcessUid();
	}

	public boolean getIsRunning() {
		return getData() != null && getData().getAgentProcessUid() != null;
	}

	@Override
	public void update(VortexAgentMachine data) {
		super.update(data);
	}
	public void onStale() {
		connectionsByLocalPortAndId.clear();
		connectionsByLocalPidAndId.clear();
		processesByPid.clear();
		machineCrons.clear();
		machineProcesses.clear();
		machineNetConnections.clear();
		machineFileSystems.clear();
		machineNetLinks.clear();
		machineNetAddresses.clear();
	}

	public VortexClientNetConnection getNetConnectionByServerPort(int serverPort) {
		Map<Long, VortexClientNetConnection> connections = this.connectionsByLocalPortAndId.get(serverPort);
		for (VortexClientNetConnection connection : CH.values(connections))
			if (connection.getData().getState() == VortexAgentNetConnection.STATE_LISTEN)
				return connection;
		return null;
	}

	public String getAgentDetail(String key) {
		Map<String, String> m = getData().getAgentDetails();
		if (m == null)
			return null;
		return m.get(key);
	}

}
