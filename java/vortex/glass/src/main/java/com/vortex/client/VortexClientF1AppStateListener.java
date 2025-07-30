package com.vortex.client;

public interface VortexClientF1AppStateListener {

	void onF1AppEntityAdded(VortexClientF1AppState.AgentWebObject<?> added);
	void onF1AppEntityUpdated(VortexClientF1AppState.AgentWebObject<?> updated);
	void onF1AppEntityRemoved(VortexClientF1AppState.AgentWebObject<?> removed);
	void onF1AppAdded(VortexClientF1AppState appState);
	void onF1AppRemoved(VortexClientF1AppState existing);

}
