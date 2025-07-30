package com.vortex.eye.state;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.LongIterable;
import com.f1.utils.CH;
import com.f1.utils.structs.LongKeyMap;

public class VortexEyeAgentState {
	private long lastProcessCheckMs;
	private long lastNetLinkCheckMs;
	private long lastNetConnCheckMs;
	private long lastNetAddrCheckMs;
	private long lastMachineCheckMs;
	private long lastFilesysCheckMs;
	private long lastChrntabCheckMs;
	private long lastMchnevtCheckMs;

	final private static Logger log = Logger.getLogger(VortexEyeAgentState.class.getName());
	final private VortexEyeState eyeState;
	final private String processUid;
	final private String remoteHost;
	final private int remotePort;
	final private long connectedTime;
	private long currentSeqNum = -1;

	public VortexEyeAgentState(VortexEyeState eyeState, String processUid, String remoteHost, int remotePort, long connectedTime) {
		if (processUid == null)
			throw new NullPointerException("processUid");
		if (remoteHost == null)
			throw new NullPointerException("remoteHost");
		this.remotePort = remotePort;
		this.connectedTime = connectedTime;
		this.eyeState = eyeState;
		this.processUid = processUid;
		this.remoteHost = remoteHost;
	}

	public String getProcessUid() {
		return processUid;
	}

	public VortexEyeState getEyeState() {
		return eyeState;
	}

	//f1 applications
	final private Map<String, VortexEyeF1AppState> f1AppsByPuid = new HashMap<String, VortexEyeF1AppState>();
	final private LongKeyMap<VortexEyeF1AppState> f1AppsByOrigId = new LongKeyMap<VortexEyeF1AppState>();

	public Collection<VortexEyeF1AppState> getF1Apps() {
		return f1AppsByPuid.values();
	}

	public void addF1App(VortexEyeF1AppState f1App) {
		if (f1App.getPuid() == null)
			throw new NullPointerException("processUid: " + f1App);
		if (f1App.getAgentState() != null)
			throw new IllegalStateException("already member of another state: " + f1App);
		f1App.setAgentState(this);
		CH.putOrThrow(f1AppsByPuid, f1App.getPuid(), f1App);
		if (f1App.getOrigId() == 0)
			throw new IllegalArgumentException("origId not set: " + f1App);
		f1AppsByOrigId.putOrThrow(f1App.getOrigId(), f1App);
	}

	public VortexEyeF1AppState getF1AppByProcessUid(String processUid) {
		return CH.getOrThrow(f1AppsByPuid, processUid);
	}

	public VortexEyeF1AppState getF1AppByOrigAiid(long f1AppInstanceId) {
		return f1AppsByOrigId.getOrThrow(f1AppInstanceId);
	}

	public void removeF1App(VortexEyeF1AppState app) {
		CH.removeOrThrow(f1AppsByPuid, app.getF1AppInstance().getProcessUid());
		f1AppsByOrigId.removeOrThrow(app.getOrigId());
		app.setAgentState(null);
	}

	//Machines
	final private Map<String, VortexEyeMachineState> machinesByMuid = new HashMap<String, VortexEyeMachineState>();
	final private LongKeyMap<VortexEyeMachineState> machinesByOrigMiid = new LongKeyMap<VortexEyeMachineState>();

	public Collection<VortexEyeMachineState> getMachines() {
		return machinesByMuid.values();
	}

	public void addMachine(VortexEyeMachineState machine) {
		if (machine.getMuid() == null)
			throw new NullPointerException("MachineUid: " + machine);
		if (machine.getAgentState() != null)
			throw new IllegalStateException("already member of another state: " + machine);
		machine.setAgentState(this);
		CH.putOrThrow(machinesByMuid, machine.getMuid(), machine);
		if (machine.getOrigMiid() == 0)
			throw new IllegalArgumentException("origId not set: " + machine);
		machinesByOrigMiid.putOrThrow(machine.getOrigMiid(), machine);
	}

	public VortexEyeMachineState getMachineByMachineUid(String muid) {
		return CH.getOrThrow(machinesByMuid, muid);
	}

	public VortexEyeMachineState getMachineByOrigMiid(long origMiid) {
		return this.machinesByOrigMiid.getOrThrow(origMiid);
	}

	public void removeMachine(VortexEyeMachineState machine) {
		CH.removeOrThrow(machinesByMuid, machine.getMuid());
		machinesByOrigMiid.remove(machine.getOrigMiid());
		machine.setAgentState(null);
	}

	public Set<String> getF1AppPuids() {
		return this.f1AppsByPuid.keySet();
	}

	public Set<String> getMachineMuids() {
		return this.machinesByMuid.keySet();
	}
	public String getRemoteHost() {
		return remoteHost;
	}

	public LongIterable getMachineOrigMiids() {
		return machinesByOrigMiid.keys();
	}

	public int getRemotePort() {
		return remotePort;
	}

	public long getConnectedTime() {
		return connectedTime;
	}
	public long getCurrentSeqNum() {
		return currentSeqNum;
	}
	public void setCurrentSeqNum(long currentSeqNum) {
		this.currentSeqNum = currentSeqNum;
	}

	public long getLastProcessCheckMs() {
		return lastProcessCheckMs;
	}

	public void setLastProcessCheckMs(long lastProcessCheckMs) {
		this.lastProcessCheckMs = lastProcessCheckMs;
	}

	public long getLastNetLinkCheckMs() {
		return lastNetLinkCheckMs;
	}

	public void setLastNetLinkCheckMs(long lastNetLinkCheckMs) {
		this.lastNetLinkCheckMs = lastNetLinkCheckMs;
	}

	public long getLastNetConnCheckMs() {
		return lastNetConnCheckMs;
	}

	public void setLastNetConnCheckMs(long lastNetConnCheckMs) {
		this.lastNetConnCheckMs = lastNetConnCheckMs;
	}

	public long getLastNetAddrCheckMs() {
		return lastNetAddrCheckMs;
	}

	public void setLastNetAddrCheckMs(long lastNetAddrCheckMs) {
		this.lastNetAddrCheckMs = lastNetAddrCheckMs;
	}

	public long getLastMachineCheckMs() {
		return lastMachineCheckMs;
	}

	public void setLastMachineCheckMs(long lastMachineCheckMs) {
		this.lastMachineCheckMs = lastMachineCheckMs;
	}

	public long getLastFilesysCheckMs() {
		return lastFilesysCheckMs;
	}

	public void setLastFilesysCheckMs(long lastFilesysCheckMs) {
		this.lastFilesysCheckMs = lastFilesysCheckMs;
	}

	public long getLastChrntabCheckMs() {
		return lastChrntabCheckMs;
	}

	public void setLastChrntabCheckMs(long lastChrntabCheckMs) {
		this.lastChrntabCheckMs = lastChrntabCheckMs;
	}

	public long getLastMchnevtCheckMs() {
		return lastMchnevtCheckMs;
	}

	public void setLastMchnevtCheckMs(long lastMchnevtCheckMs) {
		this.lastMchnevtCheckMs = lastMchnevtCheckMs;
	}

	//final private short[] amiStringMap = new short[Short.MAX_VALUE];
	//final private IntKeyMap<String> agentStringPool = new IntKeyMap<String>();

	//public void addAmiStringMapping(short agentKey, String agentString, short eyeKey) {
	//amiStringMap[agentKey] = eyeKey;
	//agentStringPool.put(agentKey, agentString);
	//}
	//public short getAmiStringMap(short agentKey) {
	//short r = amiStringMap[agentKey];
	//if (agentKey == 0 || r == 0)
	//throw new RuntimeException("bad agent key: " + agentKey);
	//return r;
	////return amiStringMap[agentKey];
	//}

	//private IntKeyMap<Long> connectionIdMapping = new IntKeyMap<Long>(100);
	//public long getAmiConnectionIdMapping(int connectionId) {
	//Long r = connectionIdMapping.get(connectionId);
	//if (r == null)
	//return -1;
	//return r;
	//}

	//	public void addAmiConnectionMapping(int agentConnectionId, long eyeConnectionId) {
	//		connectionIdMapping.put(agentConnectionId, eyeConnectionId);
	//	}
	//
	//	public IntIterable getAmiConnectionIds() {
	//		return connectionIdMapping.keys();
	//	}
	//
	//	public void clearAmi() {
	//		Arrays.fill(this.amiStringMap, (short) 0);
	//		this.connectionIdMapping.clear();
	//		this.agentStringPool.clear();
	//	}
	//
	//	public IntKeyMap<String> getAmiStringPool() {
	//		return this.agentStringPool;
	//	}

}
