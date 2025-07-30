package com.f1.ami.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiCenterDefinition;
import com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessage;
import com.f1.ami.web.datafilter.AmiWebDataFilter;
import com.f1.base.IterableAndSize;
import com.f1.utils.CH;
import com.f1.utils.EmptyIterable;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.FastArrayList;
import com.f1.utils.structs.Tuple2;

public class AmiWebManager {

	final private static Logger log = LH.get();

	final private HasherMap<String, AmiWebObjects> amiObjectsByStringType = new HasherMap<String, AmiWebObjects>();

	final private List<AmiWebCenterListener> connectListeners = new ArrayList<AmiWebCenterListener>();

	private long objectsCount;
	private long monitoredObjectsCount;

	private boolean isEyeConnected;

	private AmiWebSnapshotManager snapshotManager;

	private AmiWebDataFilter filter;

	private final String username;

	private final byte centerId;
	private final String centerName;

	private AmiCenterDefinition center;

	private AmiWebSystemObjectsManager systemObjectsManager;

	private int pauseTestMillis = 0;

	final private AmiWebManagers owner;

	private AmiWebObject_DataFilterWrapper datafilterWrapper = new AmiWebObject_DataFilterWrapper();

	public AmiWebManager(AmiWebManagers owner, AmiCenterDefinition center, String username) {
		this.owner = owner;
		this.centerId = center.getId();
		this.center = center;
		this.centerName = center.getName();
		this.username = username;
	}

	public void setDataFilter(AmiWebDataFilter filter) {
		if (this.filter != null || isEyeConnected)
			throw new IllegalStateException();
		this.filter = filter;
	}
	public void init(AmiWebSnapshotManager snapshotManager, AmiWebSystemObjectsManager objectManager) {
		this.snapshotManager = snapshotManager;
		this.systemObjectsManager = objectManager;
		this.pauseTestMillis = objectManager.getService().getPortletManager().getTools().getOptional(AmiWebProperties.PROPERTY_AMI_SLOWDOWN_REALTIME_MILLIS, 0);
		if (this.pauseTestMillis > 0)
			LH.w(log, AmiWebProperties.PROPERTY_AMI_SLOWDOWN_REALTIME_MILLIS, "=", this.pauseTestMillis, new RuntimeException("REALTIME WILL BE ARTIFICIALLY SLOW"));
	}

	private void onAmiRemoved(AmiCenterClientObjectMessage m) {
		String type = m.getTypeName();
		long id = m.getId();
		AmiWebObjects objbyType = amiObjectsByStringType.get(type);
		if (objbyType != null) {
			AmiWebObject entity = objbyType.remove(id);
			if (entity != null)
				objbyType.fireRemoveAmi(entity);
			if (objbyType.removeFilteredMonitored(id) == null && entity == null && !objbyType.getIsHideAlwaysHit())
				LH.warning(log, logMe(), " Missing record for: ", m.describe());
		}
	}

	public void onAmiAdd(AmiWebObject_Feed entity) {
		pauseTest();
		AmiWebObjects byType = amiObjectsByStringType.get(entity.getTypeName());
		if (byType != null) {
			if (!byType.setFeedParamPositions(entity.getFeedPositions())) {
				LH.warning(log, logMe(), " FeedPositions conflict for " + byType.getTypeName(), "@", entity.getIdBoxed());
				entity.setFeedPositions(byType.getFeedParamPositions());
			}
			if (!evaluateDataFilter(entity, byType))
				return;
			if (byType.put(entity.getId(), entity) != null) {
				LH.warning(log, logMe(), " Duplicate id for add: ", entity.getTypeName(), "@", entity.getId());
			} else
				byType.fireNewAmi(entity);
		}
	}

	private void pauseTest() {
		if (pauseTestMillis > 0)
			OH.sleep(pauseTestMillis);
	}

	private void onAmiAdd(AmiCenterClientObjectMessage object, AmiWebObjects byType) {
		AmiWebObject clientEntity = null;
		clientEntity = new AmiWebObject_Feed(byType.getFeedParamPositions(), object);
		if (!evaluateDataFilter(clientEntity, byType))
			return;
		if (byType.put(object.getId(), clientEntity) != null) {
			LH.warning(log, logMe(), " Duplicate id for add: ", object.describe());
		} else
			byType.fireNewAmi(clientEntity);
	}
	private void onDisconnect() {
		LH.fine(log, logMe(), " For user disconnect, resetting curseqnum to -1");
		this.isEyeConnected = false;
		this.currentSeqNum = -1;
		for (AmiWebObjects i : this.amiObjectsByStringType.values())
			i.onDisconnect();
	}
	public void onConnectionStateChanged(byte state) {
		if (state == AmiWebSnapshotManager.STATE_DISCONNECTED)
			onDisconnect();
		else if (state == AmiWebSnapshotManager.STATE_CONNECTED)
			onConnect();
		for (AmiWebCenterListener p : connectListeners) {
			try {
				p.onCenterConnectionStateChanged(this, state);
			} catch (Exception e) {
				LH.warning(log, logMe(), " Error processing disconnect for portlet: ", p, e);
			}
		}
	}

	public List<AmiWebObjects> getAmiObjectsByTypes(Set<String> types) {
		List<AmiWebObjects> r = new ArrayList<AmiWebObjects>(types.size());
		Set<String> missingTypes = new HashSet<String>();
		for (String type : types) {
			Entry<String, AmiWebObjects> node = amiObjectsByStringType.getOrCreateEntry(type);
			AmiWebObjects awo = node.getValue();
			if (awo == null) {
				node.setValue(awo = new AmiWebObjects(this, type, new AmiWebObject_FeedPositions(type)));
				missingTypes.add(type);
			}
			r.add(awo);
		}
		if (!missingTypes.isEmpty() && isEyeConnected)
			this.snapshotManager.requestPartialSnapshot(missingTypes);
		return r;
	}
	public AmiWebObjects getAmiObjectsByTypeOrNull(String type) {
		return amiObjectsByStringType.get(type);
	}
	public AmiWebObjects getAmiObjectsByType(String type) {
		Entry<String, AmiWebObjects> node = amiObjectsByStringType.getOrCreateEntry(type);
		AmiWebObjects r = node.getValue();
		if (r == null) {
			node.setValue(r = new AmiWebObjects(this, type, new AmiWebObject_FeedPositions(type)));
			if (isEyeConnected)
				this.snapshotManager.requestPartialSnapshot(CH.s(type));
		}
		return r;
	}
	public IterableAndSize<AmiWebObject> getAmiObjects(Set<String> types) {
		IterableAndSize<AmiWebObject> r = EmptyIterable.INSTANCE;
		FastArrayList<AmiWebObject> multi = null;
		for (String type : types) {
			final AmiWebObjects t = this.amiObjectsByStringType.get(type);
			if (t == null)
				continue;
			else if (CH.isEmpty(r))
				r = t.values();
			else if (multi == null) {
				multi = new FastArrayList<AmiWebObject>(r.size() + t.size());
				multi.addAll(r);
				multi.addAll(t.values());
			} else {
				multi.addAll(t.values());
			}
		}
		return multi != null ? multi : r;
	}

	private long currentSeqNum = -1;

	private long hiddenAlwaysObjectsCount;

	public String getProcessUid() {
		return this.snapshotManager.getProcessUid();
	}

	public long getCurrentSeqNum() {
		return currentSeqNum;
	}
	public void setCurrentSeqNum(long currentSeqNum) {
		if (this.currentSeqNum != -1 && this.currentSeqNum + 1 != currentSeqNum)
			LH.warning(log, logMe(), " Bad sequence number: ", currentSeqNum);
		this.currentSeqNum = currentSeqNum;
	}

	private void onConnect() {
		this.isEyeConnected = true;
		LH.fine(log, logMe(), " Connected to ", this.centerName);
		final List<Iterable<AmiWebObject>> tmpMap = new ArrayList<Iterable<AmiWebObject>>();
		for (AmiWebObjects i : this.amiObjectsByStringType.values())
			i.onConnect();
	}

	public void addClientConnectedListener(AmiWebCenterListener listener) {
		CH.addIdentityOrThrow(this.connectListeners, listener);
	}
	public void removeClientConnectedListener(AmiWebCenterListener listener) {
		CH.removeOrThrow(this.connectListeners, listener);
	}
	public boolean getIsEyeConnected() {
		return isEyeConnected;
	}
	public Set<String> getAmiObjectTypesBeingViewed() {
		return this.amiObjectsByStringType.keySet();
	}
	public int getAmiObjectTypesBeingViewedByCount(String type) {
		AmiWebObjects r = this.amiObjectsByStringType.get(type);
		return r == null ? 0 : r.size();
	}

	public boolean isInterestedIn(String type) {
		return this.amiObjectsByStringType.containsKey(type);
	}

	private boolean evaluateDataFilter(AmiWebObject entity, AmiWebObjects byType) {
		if (this.filter == null)
			return true;
		byte status;
		try {
			status = this.filter.evaluateNewRow(entity);
		} catch (Throwable e) {
			LH.warning(log, logMe(), " DataFilter Plugin threw exception for row: ", entity, e);
			return false;
		}
		switch (status) {
			case AmiWebDataFilter.HIDE_ALWAYS:
				this.incrementHiddenAlwaysObjectsCount(1);
				byType.onHideAlwaysHit();
				return false;
			case AmiWebDataFilter.SHOW_ALWAYS:
				return true;
			case AmiWebDataFilter.HIDE:
				byType.addFilterMonitored(entity);
				return false;
			case AmiWebDataFilter.SHOW:
				byType.addFilterMonitored(entity);
				return true;
			default:
				LH.warning(log, logMe(), " row ", entity, " ", this.filter.getClass().getName(), "::evalueNewRow(...) returned invalid status: " + status);
				return false;
		}
	}
	private void onAmiChange(AmiWebObject entity, AmiWebObjects byType, boolean isCurrentlyFilteredOut, AmiWebObjectFields changes) {
		if (!byType.isFilteredMonitored(entity.getId())) {
			byType.fireUpdateAmi(entity, changes);
			return;
		}
		byte status;
		try {
			datafilterWrapper.reset(entity, changes);
			status = this.filter.evaluateUpdatedRow(datafilterWrapper, isCurrentlyFilteredOut ? AmiWebDataFilter.HIDE : AmiWebDataFilter.SHOW);
		} catch (Throwable e) {
			LH.warning(log, logMe(), " DataFilter Plugin threw exception  For row: ", entity, e);
			return;
		}
		switch (status) {
			case AmiWebDataFilter.HIDE_ALWAYS:
				byType.removeFilteredMonitored(entity.getId());
				if (!isCurrentlyFilteredOut) {
					byType.remove(entity.getId());
					byType.fireRemoveAmi(entity);
				}
				byType.onHideAlwaysHit();
				return;
			case AmiWebDataFilter.SHOW_ALWAYS:
				byType.removeFilteredMonitored(entity.getId());
				if (isCurrentlyFilteredOut) {
					byType.put(entity.getId(), entity);
					byType.fireNewAmi(entity);
				} else
					byType.fireUpdateAmi(entity, changes);
				return;
			case AmiWebDataFilter.HIDE:
				if (!isCurrentlyFilteredOut) {
					byType.remove(entity.getId());
					byType.fireRemoveAmi(entity);
				}
				return;
			case AmiWebDataFilter.SHOW:
				if (isCurrentlyFilteredOut) {
					byType.put(entity.getId(), entity);
					byType.fireNewAmi(entity);
				} else
					byType.fireUpdateAmi(entity, changes);
				return;
			default:
				LH.warning(log, logMe(), " Row ", entity, " ", this.filter.getClass().getName(), "::evalueNewRow(...) returned invalid status: " + status);
		}
	}
	public byte getCenterId() {
		return this.centerId;
	}

	public AmiCenterDefinition getCenterDef() {
		return this.center;
	}

	public String getCenterName() {
		return this.centerName;
	}

	public AmiWebObject getAmiObjectsByTypeAndId(String type, long id) {
		AmiWebObjects t = this.amiObjectsByStringType.get(type);
		return t == null ? null : t.getAmiObject(id);
	}

	public void incrementMonitoredObjectsCount(int delta) {
		this.monitoredObjectsCount += delta;
	}
	public void incrementObjectsCount(int delta) {
		this.objectsCount += delta;
	}
	public void incrementHiddenAlwaysObjectsCount(long delta) {
		this.hiddenAlwaysObjectsCount += delta;
	}

	//Must be Thread safe call (approximate)
	public long getMonitoredObjectsCount() {
		return this.monitoredObjectsCount;
	}
	public long getHiddenAlwaysObjectCount() {
		return this.hiddenAlwaysObjectsCount;
	}
	//Must be Thread safe call (approximate)
	public long getObjectsCount() {
		return this.objectsCount;
	}

	public AmiWebSystemObjectsManager getSystemObjectsManager() {
		return this.systemObjectsManager;
	}

	public void clear() {
		if (!this.amiObjectsByStringType.isEmpty())
			LH.info(log, logMe(), " Resetting  web cache user, clearing: ", this.amiObjectsByStringType.keySet());
		for (AmiWebObjects i : this.amiObjectsByStringType.values()) {
			i.clearListeners();
			i.addAmiListener(this.systemObjectsManager);
		}
		this.systemObjectsManager.clear();
		this.amiObjectsByStringType.clear();
	}

	private AmiWebObjectFieldsImpl tmpChangeBuf = new AmiWebObjectFieldsImpl();

	public void onAmiWebObjectMessage(AmiCenterClientObjectMessage m) {
		pauseTest();
		AmiWebObjects objects = amiObjectsByStringType.get(m.getTypeName());
		if (objects == null)
			return;
		if (!objects.getSnapshotProcessed()) {
			LH.info(log, logMe(), " ignoring pre-snapshot message: ", m.describe());
			return;
		}
		switch (m.getAction()) {
			case AmiCenterClientObjectMessage.ACTION_ADD:
				onAmiAdd(m, objects);
				break;
			case AmiCenterClientObjectMessage.ACTION_UPD: {
				boolean isFilteredOut = false;
				AmiWebObject_Feed current = (AmiWebObject_Feed) objects.getAmiObject(m.getId());
				if (current == null) {
					current = (AmiWebObject_Feed) objects.getFilteredMonitored(m.getId());
					if (current == null) {
						if (!objects.getIsHideAlwaysHit()) //well, we know this record wasn't just filtered out by entitlements
							LH.warning(log, logMe(), " missing record for: ", m.describe());
						return;

					}
					isFilteredOut = true;
				}
				this.tmpChangeBuf.clear();
				current.update(m, this.tmpChangeBuf);
				if (this.tmpChangeBuf.getChangesCount() > 0)
					onAmiChange(current, objects, isFilteredOut, this.tmpChangeBuf);
				break;
			}
			case AmiCenterClientObjectMessage.ACTION_DEL:
				onAmiRemoved(m);
				break;
		}
	}

	private String logMe() {
		return this.owner.getService().getPortletManager().describeUser() + "->" + centerName + " (seqnum=" + this.currentSeqNum + ")";
	}

	public String getUsername() {
		return this.username;
	}
	public void onSnapshotsProcessed(String type) {
		AmiWebObjects objects = amiObjectsByStringType.get(type);
		if (objects != null) {
			if (objects.getSnapshotProcessed())
				LH.warning(log, logMe(), " duplicate snapshot processed for type: ", type);
			else
				objects.onSnapshotProcessed();
		}
	}

	public AmiWebManagers getOwner() {
		return this.owner;
	}

	public void onSchemaChanged(AmiWebTableSchemaWrapper table, byte status, Map<String, Tuple2<Class, Class>> columns) {
		AmiWebObjects objects = amiObjectsByStringType.get(table.getName());
		if (objects != null)
			objects.fireSchemaChanged(status, columns);
	}

}
