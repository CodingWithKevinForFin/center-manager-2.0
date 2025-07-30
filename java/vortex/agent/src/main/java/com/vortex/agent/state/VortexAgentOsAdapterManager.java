package com.vortex.agent.state;

import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.vortex.agent.osadapter.VortexAgentOsAdapterCommandRunner;
import com.vortex.agent.osadapter.VortexAgentOsAdapterDeploymentRunner;
import com.vortex.agent.osadapter.VortexAgentOsAdapterFileDeleter;
import com.vortex.agent.osadapter.VortexAgentOsAdapterFileSearcher;
import com.vortex.agent.osadapter.VortexAgentOsAdapterInspectCron;
import com.vortex.agent.osadapter.VortexAgentOsAdapterInspectFileSystems;
import com.vortex.agent.osadapter.VortexAgentOsAdapterInspectMachine;
import com.vortex.agent.osadapter.VortexAgentOsAdapterInspectMachineEvents;
import com.vortex.agent.osadapter.VortexAgentOsAdapterInspectNetAddresses;
import com.vortex.agent.osadapter.VortexAgentOsAdapterInspectNetConnections;
import com.vortex.agent.osadapter.VortexAgentOsAdapterInspectNetLinks;
import com.vortex.agent.osadapter.VortexAgentOsAdapterInspectProcesses;
import com.vortex.agent.osadapter.VortexAgentOsAdapterSendSignal;

public class VortexAgentOsAdapterManager implements Lockable {

	private VortexAgentOsAdapterInspectCron osAdapterInspectCron;
	private VortexAgentOsAdapterInspectNetConnections osAdapterInspectNetConnections;
	private VortexAgentOsAdapterInspectNetLinks osAdapterInspectNetLinks;
	private VortexAgentOsAdapterInspectNetAddresses osAdapterInspectNetAddresses;
	private VortexAgentOsAdapterInspectProcesses osAdapterInspectProcesses;
	private VortexAgentOsAdapterInspectMachine osAdapterInspectMachine;
	private VortexAgentOsAdapterInspectMachineEvents osAdapterInspectMachineEvents;
	private VortexAgentOsAdapterInspectFileSystems osAdapterInspectFileSystems;
	private VortexAgentOsAdapterFileSearcher fileSearcher;
	private VortexAgentOsAdapterCommandRunner commandRunner;
	private VortexAgentOsAdapterDeploymentRunner deploymentRunner;
	private VortexAgentOsAdapterSendSignal sendSignal;
	private VortexAgentOsAdapterFileDeleter fileDeleter;
	private boolean locked;

	public VortexAgentOsAdapterInspectCron getInspectCron() {
		return osAdapterInspectCron;
	}

	public void setInspectCron(VortexAgentOsAdapterInspectCron osAdapterInspectCron) {
		LockedException.assertNotLocked(this);
		this.osAdapterInspectCron = osAdapterInspectCron;
	}

	public VortexAgentOsAdapterInspectNetConnections getInspectNetConnections() {
		return osAdapterInspectNetConnections;
	}

	public void setInspectNetConnections(VortexAgentOsAdapterInspectNetConnections osAdapterInspectNetConnections) {
		LockedException.assertNotLocked(this);
		this.osAdapterInspectNetConnections = osAdapterInspectNetConnections;
	}

	public VortexAgentOsAdapterInspectNetLinks getInspectNetLinks() {
		return osAdapterInspectNetLinks;
	}

	public void setInspectNetLinks(VortexAgentOsAdapterInspectNetLinks osAdapterInspectNetLinks) {
		LockedException.assertNotLocked(this);
		this.osAdapterInspectNetLinks = osAdapterInspectNetLinks;
	}

	public VortexAgentOsAdapterInspectNetAddresses getInspectNetAddresses() {
		return osAdapterInspectNetAddresses;
	}

	public void setInspectNetAddresses(VortexAgentOsAdapterInspectNetAddresses osAdapterInspectNetAddresses) {
		LockedException.assertNotLocked(this);
		this.osAdapterInspectNetAddresses = osAdapterInspectNetAddresses;
	}

	public VortexAgentOsAdapterInspectProcesses getInspectProcesses() {
		return osAdapterInspectProcesses;
	}

	public void setInspectProcesses(VortexAgentOsAdapterInspectProcesses osAdapterInspectProcesses) {
		LockedException.assertNotLocked(this);
		this.osAdapterInspectProcesses = osAdapterInspectProcesses;
	}

	public VortexAgentOsAdapterInspectMachine getInspectMachine() {
		return osAdapterInspectMachine;
	}

	public void setInspectMachine(VortexAgentOsAdapterInspectMachine osAdapterInspectMachine) {
		LockedException.assertNotLocked(this);
		this.osAdapterInspectMachine = osAdapterInspectMachine;
	}

	public VortexAgentOsAdapterInspectMachineEvents getInspectMachineEvents() {
		return osAdapterInspectMachineEvents;
	}

	public void setInspectMachineEvents(VortexAgentOsAdapterInspectMachineEvents osAdapterInspectMachineEvents) {
		LockedException.assertNotLocked(this);
		this.osAdapterInspectMachineEvents = osAdapterInspectMachineEvents;
	}

	public VortexAgentOsAdapterInspectFileSystems getInspectFileSystems() {
		return osAdapterInspectFileSystems;
	}

	public void setInspectFileSystems(VortexAgentOsAdapterInspectFileSystems osAdapterInspectFileSystems) {
		LockedException.assertNotLocked(this);
		this.osAdapterInspectFileSystems = osAdapterInspectFileSystems;
	}

	public void lock() {
		this.locked = true;
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	public VortexAgentOsAdapterCommandRunner getCommandRunner() {
		return commandRunner;
	}

	public void setCommandRunner(VortexAgentOsAdapterCommandRunner commandRunner) {
		LockedException.assertNotLocked(this);
		this.commandRunner = commandRunner;
	}

	public VortexAgentOsAdapterFileSearcher getFileSearcher() {
		return fileSearcher;
	}

	public void setFileSearcher(VortexAgentOsAdapterFileSearcher fileSearcher) {
		LockedException.assertNotLocked(this);
		this.fileSearcher = fileSearcher;
	}

	public VortexAgentOsAdapterDeploymentRunner getDeploymentRunner() {
		return deploymentRunner;
	}

	public void setDeploymentRunner(VortexAgentOsAdapterDeploymentRunner deploymentRunner) {
		LockedException.assertNotLocked(this);
		this.deploymentRunner = deploymentRunner;
	}

	public VortexAgentOsAdapterSendSignal getSendSignal() {
		return sendSignal;
	}

	public void setSendSignal(VortexAgentOsAdapterSendSignal sendSignal) {
		LockedException.assertNotLocked(this);
		this.sendSignal = sendSignal;
	}

	public VortexAgentOsAdapterFileDeleter getFileDeleter() {
		return this.fileDeleter;
	}
	public void setFileDeleter(VortexAgentOsAdapterFileDeleter fileDeleter) {
		LockedException.assertNotLocked(this);
		this.fileDeleter = fileDeleter;
	}
}
