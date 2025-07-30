package com.vortex.testtrackagent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.f1.container.impl.AbstractContainerScope;
import com.f1.utils.EH;
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

public abstract class OSCommandRunner extends AbstractContainerScope {

	public static final String COMMAND_RUNNER = "COMMAND_RUNNER";

	protected static OSCommandRunner get(AbstractContainerScope abs) {
		return (OSCommandRunner) abs.getServices().getService(COMMAND_RUNNER);
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

	public abstract List<VortexAgentCron> runCron(CommandRunnerState state);

	public abstract List<VortexAgentMachineEventStats> runMachineEventStats(CommandRunnerState state, long onwards, byte level);

	public abstract List<VortexAgentNetAddress> runIpAddr(CommandRunnerState state);

	public abstract List<VortexAgentNetLink> runIpLink(CommandRunnerState state);

	public abstract List<VortexAgentNetConnection> runLsof(CommandRunnerState state);

	public abstract List<VortexAgentProcess> runPs(CommandRunnerState state);

	public abstract long runLastReboot(CommandRunnerState state);

	public List<VortexAgentFileSystem> runDf(CommandRunnerState state) {
		final List<VortexAgentFileSystem> fileSystems = new ArrayList<VortexAgentFileSystem>();
		final File[] roots = File.listRoots();
		for (File root : roots) {
			final String name = root.getAbsolutePath();
			final long freeSpace = root.getFreeSpace();
			final long usableSpace = root.getUsableSpace();
			final long totalSpace = root.getTotalSpace();
			VortexAgentFileSystem fs = nw(VortexAgentFileSystem.class);
			fs.setFreeSpace(freeSpace);
			fs.setUsableSpace(usableSpace);
			fs.setTotalSpace(totalSpace);
			fs.setName(name);
			fileSystems.add(fs);
		}
		return fileSystems;
	}

	public abstract void runFree(CommandRunnerState state, VortexAgentMachine sink);

	public VortexAgentMachine runDetails(CommandRunnerState state) throws IOException {
		VortexAgentMachine details = nw(VortexAgentMachine.class);
		details.setCpuCount(Runtime.getRuntime().availableProcessors());
		details.setOsArchitecture(EH.getOsArchitecture());
		details.setOsVersion(EH.getOsVersion());
		details.setOsName(EH.getOsName());
		//TODO: details.setProcessUid(Bootstrap.getProcessUid());
		//TODO: details.setAgentVersion(TestTrackAgentMain.VERSION);
		details.setStartTime(EH.getStartTime());
		long startTime = runLastReboot(state);
		if (startTime > 0)
			details.setSystemStartTime(startTime);
		String machineUid = null;
		//try {
		//machineUid = state.getMachineUid();
		//} catch (Exception e) {
		//LH.warning( log ,e.getMessage());
		//}
		if (machineUid == null)
			machineUid = getMachineUid(state);
		details.setMachineUid(machineUid);
		details.setHostName(getHostName(state));
		//TODO: details.setF1LicenseEndDate(F1LicenseInfo.getLicenseEndDate());

		runFree(state, details);
		return details;
	}

	public abstract String getMachineUid(CommandRunnerState state) throws IOException;
	public abstract String getHostName(CommandRunnerState state) throws IOException;

	protected Tuple3<Process, byte[], byte[]> assertExitCode(Tuple3<Process, byte[], byte[]> result) {
		if (result.getA().exitValue() != 0)
			throw new RuntimeException("exit code " + result.getA().exitValue() + ": " + new String(result.getC()));
		return result;
	}

}
