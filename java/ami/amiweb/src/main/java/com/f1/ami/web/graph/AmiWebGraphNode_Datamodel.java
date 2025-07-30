package com.f1.ami.web.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.utils.OH;

public class AmiWebGraphNode_Datamodel implements AmiWebGraphNodeRt<AmiWebDmsImpl> {

	private AmiWebDmsImpl inner;//null if missing
	private String id;

	private Map<String, AmiWebGraphNode_Link> sourceLinks = new HashMap<String, AmiWebGraphNode_Link>();
	private Map<String, AmiWebGraphNode_Datasource> sourceDatasources = new HashMap<String, AmiWebGraphNode_Datasource>();
	private Map<String, AmiWebGraphNode_Datamodel> targetDatamodels = new HashMap<String, AmiWebGraphNode_Datamodel>();
	private Map<String, AmiWebGraphNode_Datamodel> sourceDatamodels = new HashMap<String, AmiWebGraphNode_Datamodel>();
	private Map<String, AmiWebGraphNodeRt<?>> sourceRealtimes = new HashMap<String, AmiWebGraphNodeRt<?>>();
	private Map<String, AmiWebGraphNode_Panel> sourceFilterPanels = new HashMap<String, AmiWebGraphNode_Panel>();
	private Map<String, AmiWebGraphNode_Panel> targetPanels = new HashMap<String, AmiWebGraphNode_Panel>();
	final private AmiWebGraphManager manager;
	final private long uid;

	public AmiWebGraphNode_Datamodel(AmiWebGraphManager manager, long uid, String id, AmiWebDmsImpl o) {
		this.manager = manager;
		this.id = id;
		this.uid = uid;
		this.inner = o;

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
	public void setInner(AmiWebDmsImpl dm) {
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
		return !this.targetPanels.isEmpty() || !this.targetDatamodels.isEmpty();
	}
	@Override
	public AmiWebGraphManager getManager() {
		return this.manager;
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

	public void addSourceDatasource(AmiWebGraphNode_Datasource datasource) {
		this.sourceDatasources.put(datasource.getId(), datasource);
		manager.fireOnEdgeAdded(EDGE_SOURCE_DATASOURCE, this, datasource);
	}
	public void removeSourceDatasource(String id) {
		AmiWebGraphNode_Datasource r = this.sourceDatasources.remove(id);
		if (r != null)
			manager.fireOnEdgeRemoved(EDGE_SOURCE_DATASOURCE, this, r);
	}
	public Map<String, AmiWebGraphNode_Datasource> getSourceDatasources() {
		return this.sourceDatasources;
	}

	public void addSourceDatamodel(AmiWebGraphNode_Datamodel datasource) {
		this.sourceDatamodels.put(datasource.getId(), datasource);
		manager.fireOnEdgeAdded(EDGE_SOURCE_DATAMODEL, this, datasource);
	}
	public void removeSourceDatamodel(String id) {
		AmiWebGraphNode_Datamodel r = this.sourceDatamodels.remove(id);
		if (r != null)
			manager.fireOnEdgeRemoved(EDGE_SOURCE_DATAMODEL, this, r);
	}
	public Map<String, AmiWebGraphNode_Datamodel> getSourceDatamodels() {
		return this.sourceDatamodels;
	}
	public void onSourceDatamodelIdChanged(String old, AmiWebGraphNode_Datamodel node) {
		AmiWebGraphNode_Datamodel existing = this.sourceDatamodels.remove(old);
		OH.assertEqIdentity(node, existing);
		this.sourceDatamodels.put(node.getId(), node);
	}

	public void addTargetDatamodel(AmiWebGraphNode_Datamodel datasource) {
		this.targetDatamodels.put(datasource.getId(), datasource);
		manager.fireOnEdgeAdded(EDGE_TARGET_DATAMODEL, this, datasource);
	}
	public void removeTargetDatamodel(String id) {
		AmiWebGraphNode_Datamodel r = this.targetDatamodels.remove(id);
		if (r != null)
			manager.fireOnEdgeRemoved(EDGE_TARGET_DATAMODEL, this, r);
	}
	public Map<String, AmiWebGraphNode_Datamodel> getTargetDatamodels() {
		return this.targetDatamodels;
	}

	public void addTargetPanel(AmiWebGraphNode_Panel p) {
		this.targetPanels.put(p.getId(), p);
		manager.fireOnEdgeAdded(EDGE_TARGET_PANEL, this, p);
	}
	public void removeTargetPanel(String id) {
		AmiWebGraphNode_Panel r = this.targetPanels.remove(id);
		if (r != null)
			manager.fireOnEdgeRemoved(EDGE_TARGET_PANEL, this, r);
	}
	public Map<String, AmiWebGraphNode_Panel> getTargetPanels() {
		return this.targetPanels;
	}
	public void onPanelIdChanged(String old, AmiWebGraphNode_Panel node) {
		AmiWebGraphNode_Panel existing = this.targetPanels.remove(old);
		OH.assertEqIdentity(node, existing);
		this.targetPanels.put(node.getId(), node);
	}

	public void addSourceFilterPanel(AmiWebGraphNode_Panel p) {
		this.sourceFilterPanels.put(p.getId(), p);
		manager.fireOnEdgeAdded(EDGE_SOURCE_FILTER_PANEL, this, p);
	}
	public void removeSourceFilterPanel(String id) {
		AmiWebGraphNode_Panel r = this.sourceFilterPanels.remove(id);
		if (r != null)
			manager.fireOnEdgeRemoved(EDGE_SOURCE_FILTER_PANEL, this, r);
	}
	public Map<String, AmiWebGraphNode_Panel> getSourceFilterPanels() {
		return this.sourceFilterPanels;
	}
	public void onSourceFilterPanelIdChanged(String old, AmiWebGraphNode_Panel node) {
		AmiWebGraphNode_Panel existing = this.sourceFilterPanels.remove(old);
		OH.assertEqIdentity(node, existing);
		this.sourceFilterPanels.put(node.getId(), node);
	}

	public void onTargetDatamodelIdChanged(String old, AmiWebGraphNode_Datamodel node) {
		AmiWebGraphNode_Datamodel existing = this.targetDatamodels.remove(old);
		OH.assertEqIdentity(node, existing);
		this.targetDatamodels.put(node.getId(), node);
	}
	@Override
	public long getUid() {
		return uid;
	}
	@Override
	public byte getType() {
		return TYPE_DATAMODEL;
	}
	@Override
	public AmiWebDmsImpl getInner() {
		return this.inner;
	}
	@Override
	public String toString() {
		return AmiWebGraphManager.toString(this);
	}
	public boolean isBlender() {
		return !this.sourceDatamodels.isEmpty();
	}
	@Override
	public void addSourceRealtime(AmiWebGraphNodeRt<?> datasource) {
		this.sourceRealtimes.put(datasource.getId(), datasource);
		manager.fireOnEdgeAdded(EDGE_SOURCE_REALTIME, this, datasource);
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
	public void onSourceRealtimeIdChanged(String old, AmiWebGraphNodeRt<?> dm) {
		AmiWebGraphNodeRt<?> existing = this.sourceRealtimes.remove(old);
		OH.assertEqIdentity(dm, existing);
		this.sourceRealtimes.put(dm.getId(), existing);
	}
	@Override
	public String getLabel() {
		return this.id;
	}
	@Override
	public void onTargetRealtimeIdChanged(String oldId, AmiWebGraphNodeRt<?> node) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void addTargetRealtime(AmiWebGraphNodeRt<?> dm) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void removeTargetRealtime(String id) {
		throw new UnsupportedOperationException();
	}
	@Override
	public Map<String, AmiWebGraphNodeRt<?>> getTargetRealtimes() {
		return Collections.EMPTY_MAP;
	}
	@Override
	public String getDescription() {
		return null;
	}
	@Override
	public String getRealtimeId() {
		return null;
	}
	@Override
	public boolean isTransient() {
		return this.getInner().isTransient();
	}
}
