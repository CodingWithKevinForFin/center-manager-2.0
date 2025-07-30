package com.f1.ami.center;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiRelayMachine;
import com.f1.ami.center.table.AmiRow;
import com.f1.base.IntIterable;
import com.f1.utils.FastSafeFile;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.IntKeyMap;

public class AmiCenterRelayState {

	final private static Logger log = Logger.getLogger(AmiCenterRelayState.class.getName());
	final private AmiCenterState eyeState;
	final private String processUid;
	final private String remoteHost;
	final private int remotePort;
	final private long connectedTime;
	final private String relayId;
	private long currentSeqNum = -1;
	private AmiRelayMachine machine;
	private FastSafeFile seqFile;
	private File seqFileName;
	final private byte centerId;

	public AmiCenterRelayState(AmiCenterState eyeState, String processUid, String relayId, String remoteHost, int remotePort, long connectedTime, AmiRelayMachine machine,
			AmiRow relayRow, byte centerId, boolean guaranteedMessaging) {
		this.guaranteedMessaging = guaranteedMessaging;
		this.centerId = centerId;
		if (processUid == null)
			throw new NullPointerException("processUid");
		if (remoteHost == null)
			throw new NullPointerException("remoteHost");
		if (relayId == null)
			throw new NullPointerException("relayId");
		this.remotePort = remotePort;
		this.connectedTime = connectedTime;
		this.relayId = relayId;
		this.eyeState = eyeState;
		this.processUid = processUid;
		this.remoteHost = remoteHost;
		this.machine = machine;
		this.relayRow = relayRow;
		this.seqFileName = new File(this.eyeState.getPersistDirectory(), "relay." + this.relayId + ".recovery");
		if (guaranteedMessaging) {
			try {
				this.seqFile = new FastSafeFile(this.seqFileName, 10);
			} catch (IOException e1) {
				throw new RuntimeException("Error creating seqnum file for agent: '" + IOH.getFullPath(this.seqFileName) + "'", e1);
			}
			if (this.seqFile.getText() != null) {
				try {
					this.currentSeqNum = SH.parseInt(this.seqFile.getText(), 32);
				} catch (Exception e) {
					LH.warning(log, "Error parsing exisiting sequence file: ", this.seqFileName, e);
				}
			}
		} else
			this.seqFileName.delete();
	}
	public String getProcessUid() {
		return processUid;
	}

	public AmiCenterState getEyeState() {
		return eyeState;
	}

	public String getRemoteHost() {
		return remoteHost;
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

	final private short[] amiStringMap = new short[Short.MAX_VALUE];
	final private IntKeyMap<String> amiRelayStringPool = new IntKeyMap<String>();

	public void addAmiStringMapping(short agentKey, String agentString, short eyeKey) {
		amiStringMap[agentKey] = eyeKey;
		amiRelayStringPool.put(agentKey, agentString);
	}
	public short getAmiStringMap(short agentKey) {
		short r = amiStringMap[agentKey];
		if (agentKey == 0 || r == 0)
			throw new RuntimeException("bad agent key: " + agentKey);
		return r;
	}

	private IntKeyMap<Long> connectionIdMapping = new IntKeyMap<Long>(100);
	private final AmiRow relayRow;
	private final boolean guaranteedMessaging;

	public long getAmiConnectionIdMapping(int connectionId) {
		Long r = connectionIdMapping.get(connectionId);
		if (r == null)
			return -1;
		return r.longValue();
	}

	public void addAmiConnectionMapping(int agentConnectionId, long eyeConnectionId) {
		connectionIdMapping.put(agentConnectionId, eyeConnectionId);
	}

	public IntIterable getAmiConnectionIds() {
		return connectionIdMapping.keys();
	}

	public void clearAmi() {
		Arrays.fill(this.amiStringMap, (short) 0);
		this.connectionIdMapping.clear();
		this.amiRelayStringPool.clear();
	}

	public IntKeyMap<String> getAmiStringPool() {
		return this.amiRelayStringPool;
	}

	public AmiRelayMachine getMachineState() {
		return machine;
	}

	public String getRelayId() {
		return relayId;
	}

	public AmiRow getRelayRow() {
		return this.relayRow;
	}

	public void saveSeqnumToDisk() {
		OH.assertTrue(guaranteedMessaging);
		try {
			seqFile.setText(SH.toString(this.currentSeqNum, 32));
		} catch (IOException e) {
			LH.warning(log, "Error writing to sequence file: ", this.seqFileName, e);
		}
	}

	public byte getCenterId() {
		return this.centerId;
	}

	public boolean getGuaranteedMessaging() {
		return guaranteedMessaging;
	}

}
