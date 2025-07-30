package com.f1.ami.amicommon.centerclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiCenterDefinition;
import com.f1.ami.amicommon.msg.AmiCenterGetSnapshotRequest;
import com.f1.base.Message;
import com.f1.container.ContainerTools;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.State;
import com.f1.container.impl.BasicState;
import com.f1.utils.CH;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.LH;
import com.f1.utils.LongArrayList;
import com.f1.utils.SH;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.Tuple2;

public class AmiCenterClientState extends BasicState {
	public static final String PARTITION_PREFIX = "AMI_CENTER_CLIENT_";
	private BasicMultiMap.List<String, Tuple2<String, String>> pendingSnapshotResponses = new BasicMultiMap.List<String, Tuple2<String, String>>();//type -> invokedBy,sessionuid

	private static final Logger log = LH.get();

	private AmiCenterClientListener listener;

	private class ByType {

		final private String type;
		final private List<AmiCenterClientObjectMessage> queue = new ArrayList<AmiCenterClientObjectMessage>();
		final private LongArrayList queueSeqnum = new LongArrayList();
		public long snapshotSeqnum = Long.MAX_VALUE;

		private ByType(String type) {
			this.type = type;
		}

		public boolean process(long seqnum, AmiCenterClientObjectMessage m) {
			if (seqnum <= this.snapshotSeqnum) {
				if (!hasSnapshot()) {//we're waiting for snapshot
					this.queue.add(m);
					this.queueSeqnum.add(seqnum);
				}
				return false;
			}

			process(m);
			return true;

		}
		public void process(AmiCenterClientObjectMessage m) {
			if (isConnectected && listener != null) {
				onProcessCalled = true;
				listener.onCenterMessage(center, m);
			}
		}

		public boolean hasSnapshot() {
			return this.snapshotSeqnum != Long.MAX_VALUE;
		}

		public void clear() {
			this.queue.clear();
			this.queueSeqnum.clear();
			this.snapshotSeqnum = Long.MAX_VALUE;
		}

		public void setSnapshotSeqnum(long seqNum) {
			boolean fine = log.isLoggable(Level.FINE);
			if (hasSnapshot()) {
				LH.warning(log, logMe(), " Invalid state, received snapshot seqnum: ", seqNum);
				return;
			}

			this.snapshotSeqnum = seqNum;
			int applied = 0, skipped = 0;
			for (int i = 0; i < queue.size(); i++) {
				long n = queueSeqnum.get(i);
				AmiCenterClientObjectMessage m = queue.get(i);
				if (n > seqNum) {
					applied++;
					if (fine)
						LH.fine(log, logMe(), " Applying queued delta at seqnum ", n, ": ", m.describe());
					process(m);
				} else {
					skipped++;
					if (fine)
						LH.fine(log, logMe(), " Skipping queued delta at seqnum ", n, ": ", m.describe());
				}
			}
			queue.clear();
			this.queueSeqnum.clear();
			LH.info(log, logMe(), " Finished snapshot, deltas skipped=", skipped, ", deltas applied=", applied);
		}

		public String logMe() {
			return AmiCenterClientState.this.centerName + " (seqnum=" + AmiCenterClientState.this.currentSeqNum + ") [" + this.type + "]";
		}

		public void toWebCached(List<Object> cached) {
			if (listener instanceof AmiCenterClientWithCacheListener)
				((AmiCenterClientWithCacheListener) listener).toWebCached(this.type, cached);
		}

	}

	final private IntKeyMap<String> amiStringValuePoolMappings = new IntKeyMap<String>();
	final private Map<String, Integer> amiStringValuePoolMappings2 = new HashMap<String, Integer>();
	private String[] amiStringKeyPool = new String[Short.MAX_VALUE];//This array is handed off to other threads, NEVER remove entries or resize this array
	final private Map<String, Short> amiStringKeyPoolMap = new HashMap<String, Short>();
	private final FastByteArrayDataInputStream buf = new FastByteArrayDataInputStream(new byte[8192]);
	private Map<String, ByType> types = new HashMap<String, ByType>();
	private ContainerTools tools;
	private boolean isConnectected;

	private final byte centerId;
	private final AmiCenterDefinition center;
	private int maxBatchSize;

	private Set<String> precachedTypes;
	private String invokedBy;

	@Override
	public Class<? extends State> getType() {
		return AmiCenterClientState.class;
	}
	public AmiCenterClientState(byte centerId, AmiCenterDefinition center, int maxBatchSize, Set<String> precachedTypes, AmiCenterClientListener amiSubscribeClientTester,
			String invokedBy) {
		this.centerId = centerId;
		this.maxBatchSize = maxBatchSize;
		this.invokedBy = invokedBy;
		this.precachedTypes = precachedTypes;
		this.listener = amiSubscribeClientTester;
		this.centerName = center.getName();
		this.center = center;
		if (listener instanceof AmiCenterClientWithCacheListener)
			((AmiCenterClientWithCacheListener) this.listener).init(this);
	}
	@Override
	public void initPersisted(boolean isRecovering) {
		super.initPersisted(isRecovering);
		this.tools = this.getPartition().getContainer().getServices().getTools();
		for (String s : precachedTypes)
			subscribe(s);
		LH.info(log, logMe(), " Precaching: ", SH.join(',', this.types));
	}
	private void subscribe(String s) {
		if (!this.types.containsKey(s)) {
			this.lastIsInterested = -1;
			this.types.put(s, new ByType(s));
			if (listener instanceof AmiCenterClientWithCacheListener)
				((AmiCenterClientWithCacheListener) listener).onSubscribe(this.center, s);
		}
	}
	private void unsubscribe(String s) {
		ByType t = this.types.remove(s);
		if (t != null) {
			this.lastIsInterested = -1;
			if (listener instanceof AmiCenterClientWithCacheListener)
				((AmiCenterClientWithCacheListener) listener).onUnsubscribe(this.center, s);
		}
	}
	public void close() {
		this.types.clear();
		this.amiStringValuePoolMappings.clear();
		this.amiStringValuePoolMappings2.clear();
		this.amiStringKeyPoolMap.clear();
		amiStringKeyPool = new String[Short.MAX_VALUE];
		this.pools.clear();
		this.precachedTypes.clear();
		this.isConnectected = false;
		this.pendingSnapshotResponses.clear();
		this.invokedBy = null;
	}
	public void onAmiCenterDisconnect() {
		LH.info(log, logMe(), " Ami Center Disconnected, clearing cache");
		this.isConnectected = false;
		this.processUid = null;
		this.amiStringValuePoolMappings.clear();
		this.amiStringValuePoolMappings2.clear();
		this.lastIsInterested = -1;
		amiStringKeyPool = new String[Short.MAX_VALUE];
		this.amiStringKeyPoolMap.clear();
		for (ByType i : this.types.values())
			i.clear();
		if (listener != null)
			this.listener.onCenterDisconnect(this.center);
		this.pendingSnapshotResponses.clear();
		this.currentSeqNum = -1;
	}
	public void onAmiCenterConnect() {
		LH.info(log, logMe(), " Ami Center Connect");
		this.isConnectected = true;
		this.snapshotProcessed = false;
		if (listener != null)
			this.listener.onCenterConnect(this.center);
		this.pendingSnapshotResponses.clear();
		this.currentSeqNum = -1;
	}

	//returns the number of messages not processed
	public List<AmiCenterClientObjectMessage> onChanges(AmiCenterClientObjectMessages sink) {
		long seqnum = sink.getSeqNum();
		List<AmiCenterClientObjectMessage> msg = sink.getMessages();
		if (!isConnectected) {
			LH.info(log, logMe(), " Not Connected, ignoring: " + seqnum);
			return Collections.EMPTY_LIST;
		}
		List<AmiCenterClientObjectMessage> r = null;
		if (snapshotProcessed)
			this.currentSeqNum = seqnum;
		for (int i = 0, l = msg.size(); i < l; i++) {
			AmiCenterClientObjectMessage m = msg.get(i);
			ByType objects = this.types.get(m.getTypeName());
			if (objects == null || !objects.process(seqnum, m)) {
				if (r == null) {
					r = new ArrayList<AmiCenterClientObjectMessage>(msg.size() - 1);
					for (int n = 0; n < i; n++)
						r.add(msg.get(n));
				}
			} else if (r != null)
				r.add(m);
		}
		if (onProcessCalled && this.listener != null) {
			onProcessCalled = false;
			this.listener.onCenterMessageBatchDone(center);
		}
		return r == null ? msg : r;

	}
	public RequestMessage<AmiCenterGetSnapshotRequest> onUserSnapshotRequest(RequestMessage<AmiCenterClientGetSnapshotRequest> action, OutputPort<Message> toUsers) {
		AmiCenterClientGetSnapshotRequest getSnapshotReqOrig = action.getAction();
		Set<String> missing = new HashSet<String>();
		Set<String> found = new HashSet<String>();
		List<Object> cached = new ArrayList<Object>();
		if (getSnapshotReqOrig.getAmiObjectTypesToStopSend() != null) {
			for (String s : getSnapshotReqOrig.getAmiObjectTypesToStopSend()) {
				ByType existing = this.types.get(s);
				if (existing != null) {
					this.unsubscribe(s);
					List<Tuple2<String, String>> removed = this.pendingSnapshotResponses.remove(s);
					if (CH.isntEmpty(removed)) {
						LH.warning(log, "Unsubscribed to pending type '", s, "' with pending subscriptions: " + removed);
						//TODO: is this okay? We might need to response here
					}
				}
			}
		}
		if (getSnapshotReqOrig.getAmiObjectTypesToSend() != null) {
			for (String s : getSnapshotReqOrig.getAmiObjectTypesToSend()) {
				ByType existing = this.types.get(s);
				if (existing == null) {
					missing.add(s);
					this.subscribe(s);
				} else if (existing.hasSnapshot()) {
					existing.toWebCached(cached);
					found.add(existing.type);
					continue;
				}
				this.pendingSnapshotResponses.putMulti(s, new Tuple2<String, String>(getSnapshotReqOrig.getInvokedBy(), getSnapshotReqOrig.getSessionUid()));
			}
		}
		AmiCenterClientSnapshot sn = nw(AmiCenterClientSnapshot.class);
		sn.setOrigRequest(getSnapshotReqOrig);
		sn.setSeqNum(this.currentSeqNum);
		sn.setCenterId(getCenterId());
		sn.setTypes(found);
		sn.setCached(cached);
		sn.setSessionUid(getSnapshotReqOrig.getSessionUid());
		sn.setProcessUid(this.processUid);
		toUsers.send(sn, null);
		RequestMessage<AmiCenterGetSnapshotRequest> r;
		if (missing.size() > 0) {
			AmiCenterGetSnapshotRequest getCenterSnapshotReq = nw(AmiCenterGetSnapshotRequest.class);
			getCenterSnapshotReq.setAmiObjectTypesToSend(missing);
			getCenterSnapshotReq.setMaxBatchSize(this.maxBatchSize);
			getCenterSnapshotReq.setInvokedBy(this.invokedBy);
			LH.info(log, logMe(), " User ", getSnapshotReqOrig.getInvokedBy(), " requested: ", getSnapshotReqOrig.getAmiObjectTypesToSend(), " of which ", missing,
					" will be requested from the Ami Center");
			r = nw(RequestMessage.class);
			r.setAction(getCenterSnapshotReq);
		} else
			r = null;
		AmiCenterClientGetSnapshotResponse getSnapshotRes = nw(AmiCenterClientGetSnapshotResponse.class);
		getSnapshotRes.setCenterId(centerId);
		getSnapshotRes.setProcessUid(this.processUid);
		getSnapshotRes.setSeqNum(this.currentSeqNum);
		ResultMessage<AmiCenterClientGetSnapshotResponse> r2 = nw(ResultMessage.class);
		r2.setAction(getSnapshotRes);
		getPartition().getContainer().getDispatchController().reply(null, action, r2, null);
		return r;

	}

	private String processUid;

	private boolean snapshotProcessed;
	private boolean onProcessCalled;

	public Set<String> getTypes() {
		return this.types.keySet();
	}

	public boolean isSnapshotProcessed() {
		return this.snapshotProcessed;
	}

	//return true if this was the final response to the initial snapshot request
	public boolean onSnapshotResponse(List<AmiCenterClientObjectMessage> msg, long seqnum, AmiCenterGetSnapshotRequest origRequest, OutputPort<Message> toUsers,
			boolean isIntermediate) {
		LH.info(log, logMe(), " Center responded with ", (isIntermediate ? "intermediate" : "final"), " snapshot, seqnum=", seqnum, ", messages=", msg.size(), ", types=",
				SH.join(',', origRequest.getAmiObjectTypesToSend()));
		for (int i = 0, l = msg.size(); i < l; i++) {
			AmiCenterClientObjectMessage m = msg.get(i);
			ByType objects = this.types.get(m.getTypeName());
			if (objects == null) {
				LH.warning(log, "Snapshot contains message of unknown type: ", m.getTypeName());
				continue;
			}
			objects.process(m);
		}
		Map<String, AmiCenterClientSnapshot> suid2snapshot = new HashMap<String, AmiCenterClientSnapshot>();
		for (String i : origRequest.getAmiObjectTypesToSend()) {
			ByType byType = this.types.get(i);
			if (byType == null) {
				LH.warning(log, "Snapshot contains unknown type: ", i);
				continue;
			}
			if (!isIntermediate) {
				byType.setSnapshotSeqnum(seqnum);
				List<Tuple2<String, String>> pendingSnapshotSessionUids = this.pendingSnapshotResponses.remove(i);
				if (CH.isntEmpty(pendingSnapshotSessionUids)) {
					for (Tuple2<String, String> userAndsessionUid : pendingSnapshotSessionUids) {
						String sessionUid = userAndsessionUid.getB();
						AmiCenterClientSnapshot sn = suid2snapshot.get(sessionUid);
						if (sn == null) {
							sn = nw(AmiCenterClientSnapshot.class);
							sn.setSeqNum(this.currentSeqNum);
							sn.setCenterId(getCenterId());
							sn.setCached(new ArrayList());
							sn.setTypes(new HashSet<String>());
							sn.setSessionUid(sessionUid);
							sn.setInvokedBy(userAndsessionUid.getA());
							sn.setProcessUid(this.processUid);
							suid2snapshot.put(sessionUid, sn);
						}
						byType.toWebCached(sn.getCached());
						sn.getTypes().add(byType.type);
					}
				}
			}
		}
		for (AmiCenterClientSnapshot messages : suid2snapshot.values()) {
			LH.info(log, logMe(), " Sending Snapshot to ", messages.getInvokedBy(), "(", messages.getSessionUid(), ")", " with ", messages.getCached().size(), " rows for types: ",
					messages.getTypes());
			toUsers.send(messages, null);
		}
		if (this.snapshotRequest != null) {
			if (this.snapshotRequest == origRequest) {
				if (!isIntermediate) {
					LH.info(log, logMe(), " Received final primary snapshot response, marking cache as snapshot processed, setting seqnum=", seqnum);
					this.currentSeqNum = seqnum;
					this.snapshotProcessed = true;
					this.snapshotRequest = null;
					if (onProcessCalled && listener != null) {
						this.onProcessCalled = false;
						listener.onCenterMessageBatchDone(center);
					}
					return true;
				}
			} else
				LH.warning(log, logMe(), " Received other snapshot response before primary snapshot response");
		} else if (!isIntermediate && listener != null) {
			if (onProcessCalled && listener != null) {
				this.onProcessCalled = false;
				listener.onCenterMessageBatchDone(center);
			}
		}

		return false;
	}

	public String getProcessUid() {
		return this.processUid;
	}

	public Set<String> getInterestedTypes() {
		return this.getTypes();
	}
	public AmiCenterClientObjectMessageImpl createObjectMessage() {
		return new AmiCenterClientObjectMessageImpl();
	}
	public FastByteArrayDataInputStream getBuffer() {
		return this.buf;
	}
	public byte getCenterId() {
		return this.centerId;
	}
	public void addAmiKeyStringPoolMapping(short key, String value) {
		this.lastIsInterested = -1;
		this.amiStringKeyPool[key] = value;
		this.amiStringKeyPoolMap.put(value, key);
	}
	public Map<String, Short> getAmiKeyStringPool() {
		return amiStringKeyPoolMap;
	}

	public Short getAmiKeyId(String key) {
		return amiStringKeyPoolMap.get(key);
	}

	public void addAmiValuesStringPoolMappings(int key, String value) {
		amiStringValuePoolMappings.put(key, value);
		amiStringValuePoolMappings2.put(value, key);
	}
	public String getAmiValuesStringPoolMapping(int key) {
		if (key == 0)
			return null;
		String r = amiStringValuePoolMappings.get(key);
		if (r != null)
			return r;
		return "<val " + SH.toString(key) + ">";
	}
	public Map<String, Integer> getAmiValueStringPool() {
		return amiStringValuePoolMappings2;
	}

	public String getAmiKeyStringFromPool(short type) {
		String r = amiStringKeyPool[type];
		if (r != null)
			return r;
		if (type == 0)
			return null;
		return "<key " + SH.toString(type) + ">";
	}

	public void addAmiKeyStringPoolMappings(Map<Short, String> amiStringPoolMap) {
		for (Entry<Short, String> e : amiStringPoolMap.entrySet()) {
			addAmiKeyStringPoolMapping(e.getKey(), e.getValue());
		}
	}
	public IntKeyMap<String> getValuesStringPoolMap() {
		return this.amiStringValuePoolMappings;
	}

	private long currentSeqNum = -1;

	private String centerName;

	public int getObjectsCount() {
		return listener instanceof AmiCenterClientWithCacheListener ? ((AmiCenterClientWithCacheListener) listener).getObjectsCount() : 0;
	}
	public String getCenterName() {
		return this.centerName;
	}

	private Map<String, AmiCenterClientObjectPool> pools = new HashMap<String, AmiCenterClientObjectPool>();

	public AmiCenterClientObjectPool getPool(String type) {
		AmiCenterClientObjectPool r = pools.get(type);
		if (r == null)
			pools.put(type, r = new AmiCenterClientObjectPool(type));
		return r;
	}

	private short lastIsInterested;
	private boolean lastIsInterestedReturn;
	private AmiCenterGetSnapshotRequest snapshotRequest;//this is the initial snapshot request (not runtime additional ones)

	public boolean isInterested(short type) {
		if (this.lastIsInterested == type)
			return lastIsInterestedReturn;
		String name = this.getAmiKeyStringFromPool(type);
		this.lastIsInterested = type;
		return this.lastIsInterestedReturn = types.containsKey(name);
	}
	public AmiCenterGetSnapshotRequest createSnapshotRequest() {
		AmiCenterGetSnapshotRequest acr = nw(AmiCenterGetSnapshotRequest.class);
		acr.setAmiObjectTypesToSend(new HashSet<String>(getTypes()));
		acr.setIncludeStringPool(true);
		acr.setInvokedBy(this.invokedBy);
		acr.setMaxBatchSize(maxBatchSize);
		acr.setRequestTime(System.currentTimeMillis());
		acr.setSupportsIntermediate(true);
		this.snapshotRequest = acr;
		return acr;
	}

	private String logMe() {
		return centerName + " (seqnum=" + this.currentSeqNum + ")";
	}
	public long getCurrentSeqNum() {
		return this.currentSeqNum;
	}
	public AmiCenterDefinition getCenterDef() {
		return this.center;
	}

	public boolean isConnected() {
		return this.isConnectected;
	}
	public String[] getStringPoolArray() {
		return this.amiStringKeyPool;
	}

}
