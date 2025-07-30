package com.f1.ami.web;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.base.IterableAndSize;
import com.f1.utils.AH;
import com.f1.utils.LH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class AmiWebObjects implements AmiWebRealtimeObjectManager {

	private static final Logger log = LH.get();
	private Set<String> upperRealtimeIds = new HashSet<String>();
	private AmiWebRealtimeObjectListener[] amiListeners = AmiWebRealtimeObjectListener.EMPTY_ARRAY;
	final private String stringType;

	public AmiWebObjects(AmiWebManager manager, String stringType, AmiWebObject_FeedPositions positions) {
		this.manager = manager;
		this.stringType = stringType;
		this.realtimeId = AmiWebManagers.FEED + stringType;
		this.feedParams = positions;
	}

	final private LongKeyMap<AmiWebObject> amiObjectsById = new LongKeyMap<AmiWebObject>();
	private LongKeyMap<AmiWebObject> amiObjectsFilteredMonitoredById = null;
	final private AmiWebManager manager;
	final private String realtimeId;
	private AmiWebObject_FeedPositions feedParams;
	private boolean snapshotProcessed = false;
	private boolean hideAlwaysHit = false;

	public void fireUpdateAmi(AmiWebObject existing, AmiWebObjectFields fields) {
		for (AmiWebRealtimeObjectListener p : amiListeners)
			try {
				p.onAmiEntityUpdated(this, fields, existing);
			} catch (Exception e) {
				LH.warning(log, "Error updating ami entity: ", existing, " on portlet: ", p, e);
			}
	}

	public void fireNewAmi(AmiWebObject existingNode) {
		for (AmiWebRealtimeObjectListener p : amiListeners) {
			try {
				p.onAmiEntityAdded(this, existingNode);
			} catch (Exception e) {
				LH.warning(log, "Error adding ami entity: ", existingNode, " on portlet: ", p, e);
			}
		}

	}

	public void fireRemoveAmi(AmiWebObject existingNode) {
		for (AmiWebRealtimeObjectListener p : amiListeners) {
			try {
				p.onAmiEntityRemoved(this, existingNode);
			} catch (Exception e) {
				LH.warning(log, "Error removing ami entity: ", existingNode, " on portlet: ", p, e);
			}
		}

	}
	public void fireSchemaChanged(byte status, Map<String, Tuple2<Class, Class>> columns) {
		for (AmiWebRealtimeObjectListener p : amiListeners) {
			try {
				p.onSchemaChanged(this, status, columns);
			} catch (Exception e) {
				LH.warning(log, "Error on schema changed on portlet: " + p, e);
			}
		}
	}

	@Override
	public boolean removeAmiListener(AmiWebRealtimeObjectListener listener) {
		int i = AH.indexOf(listener, this.amiListeners);
		if (i == -1)
			return false;
		this.amiListeners = AH.remove(this.amiListeners, i);
		manager.getOwner().onListenerRemoved(this, listener);
		return true;
	}

	@Override
	public boolean addAmiListener(AmiWebRealtimeObjectListener listener) {
		int i = AH.indexOf(listener, this.amiListeners);
		if (i != -1)
			return false;
		this.amiListeners = AH.append(this.amiListeners, listener);
		manager.getOwner().onListenerAdded(this, listener);
		return true;
	}

	public boolean hasAmiListeners() {
		return this.amiListeners.length > 0;
	}

	public AmiWebObject addFilterMonitored(AmiWebObject entity) {
		if (this.amiObjectsFilteredMonitoredById == null)
			this.amiObjectsFilteredMonitoredById = new LongKeyMap<AmiWebObject>();
		AmiWebObject r = this.amiObjectsFilteredMonitoredById.put(entity.getId(), entity);
		if (r == null)
			manager.incrementMonitoredObjectsCount(1);
		return r;
	}

	public boolean isFilteredMonitored(long id) {
		return this.amiObjectsFilteredMonitoredById != null && this.amiObjectsFilteredMonitoredById.containsKey(id);
	}

	public AmiWebObject removeFilteredMonitored(long id) {
		if (this.amiObjectsFilteredMonitoredById == null)
			return null;
		AmiWebObject r = this.amiObjectsFilteredMonitoredById.remove(id);
		if (r != null)
			manager.incrementMonitoredObjectsCount(-1);
		return r;
	}
	public AmiWebObject getFilteredMonitored(long id) {
		return this.amiObjectsFilteredMonitoredById == null ? null : this.amiObjectsFilteredMonitoredById.get(id);
	}

	public AmiWebObject getAmiObject(long id) {
		return this.amiObjectsById.get(id);
	}

	public void onDisconnect() {
		clearData();
		for (AmiWebRealtimeObjectListener p : this.amiListeners) {
			try {
				p.onAmiEntitiesReset(this);
			} catch (Exception e) {
				LH.warning(log, "Error on disconnect on portlet: ", p, e);
			}
		}
	}
	public void clearData() {
		this.snapshotProcessed = false;
		this.hideAlwaysHit = false;
		this.manager.incrementObjectsCount(-size());
		this.manager.incrementMonitoredObjectsCount(-monitoredSize());
		this.manager.incrementHiddenAlwaysObjectsCount(-this.manager.getHiddenAlwaysObjectCount());
		this.amiObjectsById.clear();
		this.amiObjectsFilteredMonitoredById = null;
	}

	//	@Override
	//	public List<AmiWebRealtimeObjectListener> getListeners() {
	//		return this.amiListeners;
	//	}

	public String getTypeName() {
		return this.stringType;
	}

	public IterableAndSize<AmiWebObject> values() {
		return this.amiObjectsById.values();
	}

	//	public short getType() {
	//		return this.type;
	//	}

	public AmiWebObject remove(long id) {
		AmiWebObject r = this.amiObjectsById.remove(id);
		if (r != null)
			this.manager.incrementObjectsCount(-1);
		return r;
	}

	public AmiWebObject put(long id, AmiWebObject value) {
		AmiWebObject r = amiObjectsById.put(id, value);
		if (r == null)
			this.manager.incrementObjectsCount(1);
		return r;
	}

	public int size() {
		return amiObjectsById.size();
	}

	//	public void setType(short type) {
	//		this.type = type;
	//	}

	public int monitoredSize() {
		return this.amiObjectsFilteredMonitoredById == null ? 0 : this.amiObjectsFilteredMonitoredById.size();
	}

	@Override
	public IterableAndSize<AmiWebObject> getAmiObjects() {
		return this.amiObjectsById.values();
	}

	protected void onConnect() {
		for (AmiWebRealtimeObjectListener i : this.amiListeners) {
			i.onAmiEntitiesReset(this);
		}
	}

	@Override
	public com.f1.base.CalcTypes getRealtimeObjectschema() {
		com.f1.utils.structs.table.stack.BasicCalcTypes t = manager.getSystemObjectsManager().getTableSchema(this.stringType);
		if (t == null)
			return EmptyCalcTypes.INSTANCE;
		t = new com.f1.utils.structs.table.stack.BasicCalcTypes(t);
		t.putType(AmiConsts.TABLE_PARAM_CENTER, String.class);
		t.putType(AmiConsts.TABLE_PARAM_ID, Long.class);
		return t;
	}

	@Override
	public com.f1.base.CalcTypes getRealtimeObjectsOutputSchema() {
		return getRealtimeObjectschema();
	}

	@Override
	public String getRealtimeId() {
		return this.realtimeId;
	}

	public void clearListeners() {
		this.amiListeners = AmiWebRealtimeObjectListener.EMPTY_ARRAY;
	}

	@Override
	public Set<String> getLowerRealtimeIds() {
		return Collections.EMPTY_SET;
	}

	@Override
	public Set<String> getUpperRealtimeIds() {
		return AmiWebUtils.updateRealtimeIds(this.amiListeners, this.upperRealtimeIds);
	}

	public AmiWebObject_FeedPositions getFeedParamPositions() {
		return this.feedParams;
	}

	public boolean setFeedParamPositions(AmiWebObject_FeedPositions feedPositions) {
		if (this.feedParams == feedPositions)
			return true;
		else if (this.size() != 0 || this.monitoredSize() != 0)
			return false;
		else
			this.feedParams = feedPositions;
		return true;
	}

	public void onSnapshotProcessed() {
		this.snapshotProcessed = true;
	}

	public boolean getSnapshotProcessed() {
		return this.snapshotProcessed;
	}

	public void onHideAlwaysHit() {
		this.hideAlwaysHit = true;
	}

	public boolean getIsHideAlwaysHit() {
		return this.hideAlwaysHit;
	}

}
