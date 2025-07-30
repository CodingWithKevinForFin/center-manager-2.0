package com.f1.ami.web.graph;

import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.utils.OH;

public class AmiWebGraphNode_Link implements AmiWebGraphNode<AmiWebDmLink> {
	final private AmiWebGraphManager manager;
	final private long uid;
	private String id;
	private AmiWebDmLink inner;//null if missing
	private AmiWebGraphNode_Panel sourcePanel;
	private AmiWebGraphNode_Panel targetPanel;
	private AmiWebGraphNode_Datamodel targetDm;

	public AmiWebGraphNode_Link(AmiWebGraphManager manager, long uid, String id, AmiWebDmLink inner) {
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
	public void setInner(AmiWebDmLink ln) {
		if (this.inner == ln)
			return;
		Object old = this.inner;
		this.inner = ln;
		manager.fireChanged(this, old, this.inner);
	}
	public boolean isDummy() {
		return this.inner == null;
	}
	public boolean hasDependencies() {
		return this.sourcePanel == null;
	}

	@Override
	public AmiWebGraphManager getManager() {
		return this.manager;
	}

	public AmiWebGraphNode_Panel getSourcePanel() {
		return sourcePanel;
	}

	public void setSourcePanel(AmiWebGraphNode_Panel source) {
		if (this.sourcePanel == source)
			return;
		AmiWebGraphNode_Panel old = this.sourcePanel;
		this.sourcePanel = source;
		manager.fireOnEdgeChanged(EDGE_SOURCE_PANEL, this, old, source);
	}

	public AmiWebGraphNode_Panel getTargetPanel() {
		return targetPanel;
	}

	public void setTargetPanel(AmiWebGraphNode_Panel targetPanel) {
		if (this.targetPanel == targetPanel)
			return;
		AmiWebGraphNode_Panel old = this.targetPanel;
		this.targetPanel = targetPanel;
		manager.fireOnEdgeChanged(EDGE_TARGET_PANEL, this, old, targetPanel);
	}

	public AmiWebGraphNode_Datamodel getTargetDm() {
		return targetDm;
	}

	public void setTargetDm(AmiWebGraphNode_Datamodel targetDm) {
		if (this.targetDm == targetDm)
			return;
		AmiWebGraphNode_Datamodel old = this.targetDm;
		this.targetDm = targetDm;
		manager.fireOnEdgeChanged(EDGE_TARGET_DATAMODEL, this, old, this.targetDm);
	}

	@Override
	public long getUid() {
		return this.uid;
	}

	@Override
	public byte getType() {
		return TYPE_LINK;
	}
	@Override
	public String toString() {
		return AmiWebGraphManager.toString(this);
	}

	@Override
	public AmiWebDmLink getInner() {
		return this.inner;
	}
	@Override
	public String getLabel() {
		return this.id;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getRealtimeId() {
		return null;
	}
	@Override
	public boolean isTransient() {
		return this.inner.isTransient();
	}
}
