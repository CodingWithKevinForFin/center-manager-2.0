package com.vortex.client;

public interface VortexClientMachineListener {

	public void onMachineAdded(VortexClientMachine machine);
	public void onMachineUpdated(VortexClientMachine machine);
	public void onMachineStale(VortexClientMachine machine);
	public void onMachineActive(VortexClientMachine machine);
	public void onMachineRemoved(VortexClientMachine machine);

	public void onMachineEntityAdded(VortexClientEntity<?> node);
	public void onMachineEntityUpdated(VortexClientEntity<?> node);
	public void onMachineEntityRemoved(VortexClientEntity<?> node);

}
