package com.vortex.agent.osadapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.f1.container.impl.AbstractContainerScope;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.Tuple3;
import com.f1.vortexcommon.msg.agent.VortexAgentCron;
import com.f1.vortexcommon.msg.agent.VortexAgentFileSystem;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.agent.VortexAgentMachineEventStats;
import com.f1.vortexcommon.msg.agent.VortexAgentNetAddress;
import com.f1.vortexcommon.msg.agent.VortexAgentNetConnection;
import com.f1.vortexcommon.msg.agent.VortexAgentNetLink;
import com.f1.vortexcommon.msg.agent.VortexAgentProcess;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public abstract class VortexAgentAbstractOsAdapter implements VortexAgentOsAdapterInspectCron, VortexAgentOsAdapterInspectMachineEvents, VortexAgentOsAdapterInspectFileSystems,
		VortexAgentOsAdapterInspectMachine, VortexAgentOsAdapterInspectNetAddresses, VortexAgentOsAdapterInspectNetConnections, VortexAgentOsAdapterInspectNetLinks,
		VortexAgentOsAdapterInspectProcesses {

	private static final Logger log = LH.get(VortexAgentAbstractOsAdapter.class);
	public static final String COMMAND_RUNNER = "COMMAND_RUNNER";

	protected static VortexAgentAbstractOsAdapter get(AbstractContainerScope abs) {
		return (VortexAgentAbstractOsAdapter) abs.getServices().getService(COMMAND_RUNNER);
	}

	static protected Tuple2<String, Integer> splitHostPort(String address) {
		address = SH.stripPrefix(address, "::ffff:", false);
		String port = SH.afterLast(address, ':');
		String host = SH.beforeLast(address, ':');
		Tuple2<String, Integer> r = new Tuple2<String, Integer>();
		//if (!host.startsWith("::"))
		//host = SH.afterLast(host, ':');
		r.setAB(host, "*".equals(port) ? 0 : Integer.parseInt(port));
		return r;
	}

	public PropertyController getProperties(VortexAgentOsAdapterState state) {
		return state.getPartition().getContainer().getServices().getPropertyController();
	}
	public abstract List<VortexAgentCron> inspectCron(VortexAgentOsAdapterState state);

	public abstract List<VortexAgentMachineEventStats> inspectMachineEvents(VortexAgentOsAdapterState state, long onwards, byte level);

	public abstract List<VortexAgentNetAddress> inspectNetAddresses(VortexAgentOsAdapterState state);

	public abstract List<VortexAgentNetLink> inspectNetLinks(VortexAgentOsAdapterState state);

	public abstract List<VortexAgentNetConnection> inspectNetConnections(VortexAgentOsAdapterState state);

	public abstract List<VortexAgentProcess> inspectProcesses(VortexAgentOsAdapterState state);

	public abstract long runLastReboot(VortexAgentOsAdapterState state);

	public List<VortexAgentFileSystem> inspectFileSystems(VortexAgentOsAdapterState state) {
		final List<VortexAgentFileSystem> fileSystems = new ArrayList<VortexAgentFileSystem>();
		final File[] roots = File.listRoots();
		for (File root : roots) {
			final String name = root.getAbsolutePath();
			final long freeSpace = root.getFreeSpace();
			final long usableSpace = root.getUsableSpace();
			final long totalSpace = root.getTotalSpace();
			VortexAgentFileSystem fs = state.nw(VortexAgentFileSystem.class);
			fs.setFreeSpace(freeSpace);
			fs.setUsableSpace(usableSpace);
			fs.setTotalSpace(totalSpace);
			fs.setName(name);
			fileSystems.add(fs);
		}
		return fileSystems;
	}

	public abstract void runFree(VortexAgentOsAdapterState state, VortexAgentMachine sink);

	private String tmpMachineUid;
	public VortexAgentMachine inspectMachine(VortexAgentOsAdapterState state) throws IOException {
		VortexAgentMachine details = state.nw(VortexAgentMachine.class);
		details.setCpuCount(Runtime.getRuntime().availableProcessors());
		details.setOsArchitecture(EH.getOsArchitecture());
		details.setOsVersion(EH.getOsVersion());
		details.setOsName(EH.getOsName());
		details.setStartTime(EH.getStartTime());
		try {
			long startTime = runLastReboot(state);
			if (startTime > 0)
				details.setSystemStartTime(startTime);
		} catch (Exception e) {
			LH.warning(log, "Error getting last reboot time", e);
		}
		if (tmpMachineUid != null) {
			details.setMachineUid(tmpMachineUid);
		} else {
			try {
				String machineUid = getMachineUid(state);
				details.setMachineUid(machineUid);
			} catch (Exception e) {
				LH.warning(log, "Error getting last reboot time", e);
				tmpMachineUid = "UID_" + EH.getLocalHost();
				details.setMachineUid(tmpMachineUid);
			}
		}
		try {
			details.setHostName(getHostName(state));
		} catch (Exception e) {
			LH.warning(log, "Error getting host", e);
			details.setHostName(EH.getLocalHost());
		}
		try {
			runFree(state, details);
		} catch (Exception e) {
			LH.warning(log, "Error getting free memory", e);
		}
		return details;
	}

	public abstract String getMachineUid(VortexAgentOsAdapterState state) throws IOException;
	public abstract String getHostName(VortexAgentOsAdapterState state) throws IOException;

	protected Tuple3<Process, byte[], byte[]> assertExitCode(Tuple3<Process, byte[], byte[]> result) {
		if (result.getA().exitValue() != 0)
			throw new RuntimeException("exit code " + result.getA().exitValue() + ": " + new String(result.getC()));
		return result;
	}

}
