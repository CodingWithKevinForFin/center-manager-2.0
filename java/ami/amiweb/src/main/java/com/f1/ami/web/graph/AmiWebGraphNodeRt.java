package com.f1.ami.web.graph;

import java.util.Map;

public interface AmiWebGraphNodeRt<T> extends AmiWebGraphNode<T> {

	public void addSourceRealtime(AmiWebGraphNodeRt<?> dm);
	public void removeSourceRealtime(String id);
	public Map<String, AmiWebGraphNodeRt<?>> getSourceRealtimes();
	public void addTargetRealtime(AmiWebGraphNodeRt<?> dm);
	public void removeTargetRealtime(String id);
	public void onTargetRealtimeIdChanged(String oldId, AmiWebGraphNodeRt<?> node);
	public void onSourceRealtimeIdChanged(String oldId, AmiWebGraphNodeRt<?> node);
	public Map<String, AmiWebGraphNodeRt<?>> getTargetRealtimes();
}
