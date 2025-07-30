package com.f1.ami.web.graph;

import java.util.HashMap;
import java.util.Map;

import com.f1.ami.web.AmiWebManagers;
import com.f1.ami.web.AmiWebRealtimeObjectManager;
import com.f1.ami.web.AmiWebRealtimeProcessor;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebGraphNode_Realtime implements AmiWebGraphNodeRt<AmiWebRealtimeObjectManager> {

	private AmiWebRealtimeObjectManager inner;
	private AmiWebGraphManager manager;
	private String id;
	private long uid;
	//	private Map<String, AmiWebGraphNode_Panel> targetRealtimePanels = new HashMap<String, AmiWebGraphNode_Panel>();
	private Map<String, AmiWebGraphNodeRt<?>> targetRealtimes = new HashMap<String, AmiWebGraphNodeRt<?>>();
	private Map<String, AmiWebGraphNodeRt<?>> sourceRealtimes = new HashMap<String, AmiWebGraphNodeRt<?>>();
	final private boolean isFeed;
	private String label;

	public AmiWebGraphNode_Realtime(AmiWebGraphManager manager, long uid, String id, AmiWebRealtimeObjectManager inner) {
		this.manager = manager;
		this.id = id;
		this.uid = uid;
		this.inner = inner;
		if (SH.startsWith(id, AmiWebManagers.FEED))
			this.isFeed = true;
		else if (SH.startsWith(id, AmiWebManagers.PROCESSOR))
			this.isFeed = false;
		else
			throw new IllegalStateException(id);

		this.label = SH.afterFirst(id, ':');
	}
	@Override
	public long getUid() {
		return uid;
	}

	@Override
	public byte getType() {
		return isFeed ? TYPE_FEED : TYPE_PROCESSOR;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public AmiWebGraphManager getManager() {
		return manager;
	}

	@Override
	public AmiWebRealtimeObjectManager getInner() {
		return this.inner;
	}
	//	public void addTargetRealtimePanel(AmiWebGraphNode_Panel p) {
	//		this.targetRealtimePanels.put(p.getId(), p);
	//		manager.fireOnEdgeAdded(EDGE_TARGET_PANEL, this, p);
	//	}
	//	public void removeTargetRealtimePanel(String id) {
	//		AmiWebGraphNode_Panel r = this.targetRealtimePanels.remove(id);
	//		if (r != null)
	//			manager.fireOnEdgeRemoved(EDGE_TARGET_PANEL, this, r);
	//	}
	//	public Map<String, AmiWebGraphNode_Panel> getTargetPanels() {
	//		return this.targetRealtimePanels;
	//	}
	//	public void onPanelIdChanged(String old, AmiWebGraphNode_Panel node) {
	//		AmiWebGraphNode_Panel existing = this.targetRealtimePanels.remove(old);
	//		OH.assertEqIdentity(node, existing);
	//		this.targetRealtimePanels.put(node.getId(), node);
	//	}

	@Override
	public void addSourceRealtime(AmiWebGraphNodeRt<?> dm) {
		this.sourceRealtimes.put(dm.getId(), dm);
		manager.fireOnEdgeAdded(EDGE_SOURCE_REALTIME, this, dm);
	}
	@Override
	public void removeSourceRealtime(String id) {
		AmiWebGraphNodeRt<?> r = this.sourceRealtimes.remove(id);
		if (r != null)
			manager.fireOnEdgeRemoved(EDGE_SOURCE_REALTIME, this, r);
	}
	@Override
	public void onSourceRealtimeIdChanged(String old, AmiWebGraphNodeRt<?> dm) {
		AmiWebGraphNodeRt<?> existing = this.sourceRealtimes.remove(old);
		OH.assertEqIdentity(dm, existing);
		this.sourceRealtimes.put(dm.getId(), existing);
	}
	@Override
	public Map<String, AmiWebGraphNodeRt<?>> getSourceRealtimes() {
		return this.sourceRealtimes;
	}

	@Override
	public void addTargetRealtime(AmiWebGraphNodeRt<?> dm) {
		this.targetRealtimes.put(dm.getId(), dm);
		manager.fireOnEdgeAdded(EDGE_TARGET_REALTIME, this, dm);
	}
	@Override
	public void removeTargetRealtime(String id) {
		AmiWebGraphNodeRt<?> r = this.targetRealtimes.remove(id);
		if (r != null)
			manager.fireOnEdgeRemoved(EDGE_TARGET_REALTIME, this, r);
	}
	@Override
	public void onTargetRealtimeIdChanged(String old, AmiWebGraphNodeRt<?> dm) {
		AmiWebGraphNodeRt<?> existing = this.targetRealtimes.remove(old);
		OH.assertEqIdentity(dm, existing);
		this.targetRealtimes.put(dm.getId(), existing);
	}
	@Override
	public Map<String, AmiWebGraphNodeRt<?>> getTargetRealtimes() {
		return this.targetRealtimes;
	}
	@Override
	public void setInner(AmiWebRealtimeObjectManager inner) {
		this.inner = inner;
	}
	public boolean hasDependencies() {
		return !this.targetRealtimes.isEmpty();
	}
	public boolean isFeed() {
		return this.isFeed;
	}
	@Override
	public String getLabel() {
		return this.label;
	}
	@Override
	public String getDescription() {
		if (inner instanceof AmiWebRealtimeProcessor) {
			AmiWebRealtimeProcessor proc = (AmiWebRealtimeProcessor) inner;
			return SH.uppercaseFirstChar(SH.toLowerCase(proc.getType())) + " RT Proc";
		} else if (isFeed)
			return "Feed";
		return null;
	}
	@Override
	public String toString() {
		return AmiWebGraphManager.toString(this);
	}
	public String getLabelAndDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getRealtimeId() {
		return id;
	}
	public void setId(String id) {
		if (OH.eq(this.id, id))
			return;
		String oldId = this.id;
		this.id = id;
		this.label = SH.afterFirst(id, ':');
		manager.fireIdChanged(this, oldId, this.id);
	}
	public boolean isTransient() {
		return !isFeed && inner instanceof AmiWebRealtimeProcessor && ((AmiWebRealtimeProcessor) inner).isTransient();
	}
}
