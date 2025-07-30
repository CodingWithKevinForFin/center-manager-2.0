package com.f1.ami.web.graph;

import java.util.HashMap;
import java.util.Map;

import com.f1.ami.web.AmiWebDatasourceWrapper;
import com.f1.utils.OH;

public class AmiWebGraphNode_Datasource implements AmiWebGraphNode<AmiWebDatasourceWrapper> {

	final private AmiWebGraphManager manager;
	private String id;
	private AmiWebDatasourceWrapper inner;//null if missing
	private Map<String, AmiWebGraphNode_Datamodel> targetDatamodels = new HashMap<String, AmiWebGraphNode_Datamodel>();
	private long uid;

	public AmiWebGraphNode_Datasource(AmiWebGraphManager manager, long uid, String id, AmiWebDatasourceWrapper inner) {
		this.manager = manager;
		this.uid = uid;
		this.id = id;
		this.inner = inner;
	}
	public void setId(String id) {
		if (OH.eq(this.id, id))
			return;
		String oldId = this.id;
		this.id = id;
		manager.fireIdChanged(this, oldId, this.id);
	}
	@Override
	public void setInner(AmiWebDatasourceWrapper ds) {
		if (this.inner == ds)
			return;
		Object old = this.inner;
		this.inner = ds;
		manager.fireChanged(this, old, this.inner);
	}
	public boolean isDummy() {
		return this.inner == null;
	}
	public boolean hasDependencies() {
		return !this.targetDatamodels.isEmpty();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public AmiWebGraphManager getManager() {
		return this.manager;
	}

	public void addTargetDatamodel(AmiWebGraphNode_Datamodel dm) {
		this.targetDatamodels.put(dm.getId(), dm);
		manager.fireOnEdgeAdded(EDGE_TARGET_DATAMODEL, this, dm);
	}
	public void removeTargetDatamodel(String id) {
		AmiWebGraphNode_Datamodel r = this.targetDatamodels.remove(id);
		if (r != null)
			manager.fireOnEdgeRemoved(EDGE_TARGET_DATAMODEL, this, r);
	}

	public Map<String, AmiWebGraphNode_Datamodel> getTargetDatamodels() {
		return this.targetDatamodels;
	}
	public void onTargetDatamodelIdChanged(String old, AmiWebGraphNode_Datamodel dm) {
		AmiWebGraphNode_Datamodel existing = this.targetDatamodels.remove(old);
		OH.assertEqIdentity(dm, existing);
		this.targetDatamodels.put(dm.getId(), dm);
	}
	@Override
	public long getUid() {
		return this.uid;
	}
	@Override
	public byte getType() {
		return TYPE_DATASOURCE;
	}
	@Override
	public AmiWebDatasourceWrapper getInner() {
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
		return inner != null ? inner.getAdapter() : null;
	}

	@Override
	public String getRealtimeId() {
		return null;
	}
	@Override
	public boolean isTransient() {
		return false;
	}
}
