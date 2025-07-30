package com.f1.ami.web.graph;

import java.util.HashMap;
import java.util.Map;

import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebRealtimePortlet;
import com.f1.utils.OH;

public class AmiWebGraphNode_Panel implements AmiWebGraphNodeRt<AmiWebPortlet> {
	final private AmiWebGraphManager manager;
	private String id;
	private AmiWebPortlet inner;//null if missing
	private AmiWebGraphNode_Panel parentPanel;
	private Map<String, AmiWebGraphNode_Datamodel> sourceDatamodels = new HashMap<String, AmiWebGraphNode_Datamodel>();
	private Map<String, AmiWebGraphNode_Link> targetLinks = new HashMap<String, AmiWebGraphNode_Link>();
	private Map<String, AmiWebGraphNode_Link> sourceLinks = new HashMap<String, AmiWebGraphNode_Link>();
	private Map<String, AmiWebGraphNode_Panel> childPanels = new HashMap<String, AmiWebGraphNode_Panel>();
	private Map<String, AmiWebGraphNode_Datamodel> targetFilterDatamodels = new HashMap<String, AmiWebGraphNode_Datamodel>();
	//	private Map<String, AmiWebGraphNode_Panel> targetRealtimePanels = new HashMap<String, AmiWebGraphNode_Panel>();
	private Map<String, AmiWebGraphNodeRt<?>> sourceRealtimes = new HashMap<String, AmiWebGraphNodeRt<?>>();
	private Map<String, AmiWebGraphNodeRt<?>> targetRealtimes = new HashMap<String, AmiWebGraphNodeRt<?>>();
	final private long uid;

	public AmiWebGraphNode_Panel(AmiWebGraphManager manager, long uid, String id, AmiWebPortlet inner) {
		this.manager = manager;
		this.uid = uid;
		this.id = id;
		this.inner = inner;
	}

	@Override
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		if (OH.eq(this.id, id))
			return;
		String oldId = this.id;
		this.id = id;
		manager.fireIdChanged(this, oldId, this.id);
	}
	@Override
	public void setInner(AmiWebPortlet dm) {
		if (this.inner == dm)
			return;
		Object old = this.inner;
		this.inner = dm;
		manager.fireChanged(this, old, this.inner);
	}
	public boolean isDummy() {
		return this.inner == null;
	}
	public boolean hasDependencies() {
		return !this.childPanels.isEmpty();
	}
	@Override
	public AmiWebGraphManager getManager() {
		return this.manager;
	}

	public void addSourceDatamodel(AmiWebGraphNode_Datamodel dm) {
		this.sourceDatamodels.put(dm.getId(), dm);
		manager.fireOnEdgeAdded(EDGE_SOURCE_DATAMODEL, this, dm);
	}
	public void removeSourceDatamodel(String id) {
		AmiWebGraphNode_Datamodel r = this.sourceDatamodels.remove(id);
		if (r != null)
			manager.fireOnEdgeRemoved(EDGE_SOURCE_DATAMODEL, this, r);
	}
	public Map<String, AmiWebGraphNode_Datamodel> getSourceDatamodels() {
		return this.sourceDatamodels;
	}
	public void onSourceDatamodelIdChanged(String old, AmiWebGraphNode_Datamodel dm) {
		AmiWebGraphNode_Datamodel existing = this.sourceDatamodels.remove(old);
		OH.assertEqIdentity(dm, existing);
		this.sourceDatamodels.put(dm.getId(), dm);
	}

	public void addTargetLinks(AmiWebGraphNode_Link link) {
		this.targetLinks.put(link.getId(), link);
		manager.fireOnEdgeAdded(EDGE_TARGET_LINK, this, link);
	}
	public void removeTargetLinks(String id) {
		AmiWebGraphNode_Link r = this.targetLinks.remove(id);
		if (r != null)
			manager.fireOnEdgeRemoved(EDGE_TARGET_LINK, this, r);
	}
	public Map<String, AmiWebGraphNode_Link> getTargetLinks() {
		return this.targetLinks;
	}

	public void addSourceLink(AmiWebGraphNode_Link link) {
		this.sourceLinks.put(link.getId(), link);
		manager.fireOnEdgeAdded(EDGE_SOURCE_LINK, this, link);
	}
	public void removeSourceLink(String id) {
		AmiWebGraphNode_Link r = this.sourceLinks.remove(id);
		if (r != null)
			manager.fireOnEdgeRemoved(EDGE_SOURCE_LINK, this, r);
	}
	public Map<String, AmiWebGraphNode_Link> getSourceLinks() {
		return this.sourceLinks;
	}

	public void addChildPanel(AmiWebGraphNode_Panel panel) {
		this.childPanels.put(panel.getId(), panel);
		manager.fireOnEdgeAdded(EDGE_CHILD_PANEL, this, panel);
	}
	public void removeChildPanel(String id) {
		AmiWebGraphNode_Panel r = this.childPanels.remove(id);
		if (r != null)
			manager.fireOnEdgeRemoved(EDGE_CHILD_PANEL, this, r);
	}
	public Map<String, AmiWebGraphNode_Panel> getChildrenPanels() {
		return this.childPanels;
	}
	public void setParentPanel(AmiWebGraphNode_Panel parent) {
		if (this.parentPanel == parent)
			return;
		AmiWebGraphNode_Panel old = this.parentPanel;
		this.parentPanel = parent;
		this.manager.fireOnEdgeChanged(EDGE_PARENT_PANEL, this, old, this.parentPanel);
	}
	public AmiWebGraphNode_Panel getParentPanel() {
		return this.parentPanel;
	}
	public void onChildPanelIdChanged(String old, AmiWebGraphNode_Panel node) {
		AmiWebGraphNode_Panel existing = this.childPanels.remove(old);
		OH.assertEqIdentity(node, existing);
		this.childPanels.put(node.getId(), node);
	}

	public void addTargetFilterDatamodel(AmiWebGraphNode_Datamodel dm) {
		this.targetFilterDatamodels.put(dm.getId(), dm);
		manager.fireOnEdgeAdded(EDGE_TARGET_FILTER_DATAMODEL, this, dm);
	}
	public void removeTargetFilterDatamodel(String id) {
		AmiWebGraphNode_Datamodel r = this.targetFilterDatamodels.remove(id);
		if (r != null)
			manager.fireOnEdgeRemoved(EDGE_TARGET_FILTER_DATAMODEL, this, r);
	}
	public Map<String, AmiWebGraphNode_Datamodel> getTargetFilterDatamodels() {
		return this.targetFilterDatamodels;
	}
	public void onTargetFilterDatamodelIdChanged(String old, AmiWebGraphNode_Datamodel dm) {
		AmiWebGraphNode_Datamodel existing = this.targetFilterDatamodels.remove(old);
		OH.assertEqIdentity(dm, existing);
		this.targetFilterDatamodels.put(dm.getId(), dm);
	}

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
	public Map<String, AmiWebGraphNodeRt<?>> getSourceRealtimes() {
		return this.sourceRealtimes;
	}
	@Override
	public void onSourceRealtimeIdChanged(String old, AmiWebGraphNodeRt<?> dm) {
		AmiWebGraphNodeRt<?> existing = this.sourceRealtimes.remove(old);
		OH.assertEqIdentity(dm, existing);
		this.sourceRealtimes.put(dm.getId(), existing);
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
	public Map<String, AmiWebGraphNodeRt<?>> getTargetRealtimes() {
		return this.targetRealtimes;
	}
	@Override
	public void onTargetRealtimeIdChanged(String old, AmiWebGraphNodeRt<?> dm) {
		AmiWebGraphNodeRt<?> existing = this.targetRealtimes.remove(old);
		OH.assertEqIdentity(dm, existing);
		this.targetRealtimes.put(dm.getId(), existing);
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
	public long getUid() {
		return this.uid;
	}
	@Override
	public byte getType() {
		return TYPE_PANEL;
	}
	@Override
	public AmiWebPortlet getInner() {
		return this.inner;
	}

	@Override
	public String toString() {
		return AmiWebGraphManager.toString(this);
	}

	@Override
	public String getLabel() {
		return this.id;
	}

	@Override
	public String getDescription() {
		return inner == null ? null : inner.getConfigMenuTitle();
	}

	public boolean isRealtime() {
		return inner instanceof AmiWebRealtimePortlet;
	}
	@Override
	public String getRealtimeId() {
		return inner instanceof AmiWebRealtimePortlet ? ((AmiWebRealtimePortlet) inner).getRealtimeId() : null;
	}
	@Override
	public boolean isTransient() {
		return this.inner.isTransient();
	}
}
