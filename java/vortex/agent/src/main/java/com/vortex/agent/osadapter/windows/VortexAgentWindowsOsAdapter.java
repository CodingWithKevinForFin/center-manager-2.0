package com.vortex.agent.osadapter.windows;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.utils.CharReader;
import com.f1.utils.EH;
import com.f1.utils.GuidHelper;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.structs.Tuple3;
import com.f1.vortexcommon.msg.agent.VortexAgentCron;
import com.f1.vortexcommon.msg.agent.VortexAgentFileSystem;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.agent.VortexAgentMachineEventStats;
import com.f1.vortexcommon.msg.agent.VortexAgentNetAddress;
import com.f1.vortexcommon.msg.agent.VortexAgentNetConnection;
import com.f1.vortexcommon.msg.agent.VortexAgentNetLink;
import com.f1.vortexcommon.msg.agent.VortexAgentProcess;
import com.vortex.agent.osadapter.VortexAgentAbstractOsAdapter;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public class VortexAgentWindowsOsAdapter extends VortexAgentAbstractOsAdapter {
	private static final Logger log = LH.get(VortexAgentWindowsOsAdapter.class);
	private WindowsProcessMonitor windowsProcessMonitor;

	public static final Map<String, Short> NETLINKSTATES = new HashMap<String, Short>();
	public static final Map<String, Byte> NETADDRESSSCOPES = new HashMap<String, Byte>();
	static {
		NETLINKSTATES.put("ALLMULTI", VortexAgentNetLink.STATE_ALLMULTI);
		NETLINKSTATES.put("BROADCAST", VortexAgentNetLink.STATE_BROADCAST);
		NETLINKSTATES.put("DYNAMIC", VortexAgentNetLink.STATE_DYNAMIC);
		NETLINKSTATES.put("POOLBACK", VortexAgentNetLink.STATE_LOOPBACK);
		NETLINKSTATES.put("LOWER_UP", VortexAgentNetLink.STATE_LOWER_UP);
		NETLINKSTATES.put("MULTICAST", VortexAgentNetLink.STATE_MULTICAST);
		NETLINKSTATES.put("NOARP", VortexAgentNetLink.STATE_NOARP);
		NETLINKSTATES.put("POINTTOPOINT", VortexAgentNetLink.STATE_POINTTOPOINT);
		NETLINKSTATES.put("PROMISC", VortexAgentNetLink.STATE_PROMISC);
		NETLINKSTATES.put("SLAVE", VortexAgentNetLink.STATE_SLAVE);
		NETLINKSTATES.put("UP", VortexAgentNetLink.STATE_UP);
		NETADDRESSSCOPES.put("global", VortexAgentNetAddress.SCOPE_GLOBAL);
		NETADDRESSSCOPES.put("host", VortexAgentNetAddress.SCOPE_HOST);
		NETADDRESSSCOPES.put("link", VortexAgentNetAddress.SCOPE_LINK);
		NETADDRESSSCOPES.put("site", VortexAgentNetAddress.SCOPE_SITE);
	}

	public VortexAgentWindowsOsAdapter(PropertyController pc) throws IOException {
		Integer port = pc.getOptional("agent.dotnet.server.port", Caster_Integer.INSTANCE);
		if (port == null)
			windowsProcessMonitor = new WindowsProcessMonitor(System.in, System.out);
		else {
			//ServerSocket serversocket = new ServerSocket(port);
			//Socket socket = serversocket.accept();
			Socket socket = IOH.openClientSocketWithReason("localhost", port, "loop back to wmi");
			windowsProcessMonitor = new WindowsProcessMonitor(socket.getInputStream(), socket.getOutputStream());
			//serversocket.close();
		}
	}

	@Override
	public List<VortexAgentNetAddress> inspectNetAddresses(VortexAgentOsAdapterState state) {
		List<VortexAgentNetAddress> r = new ArrayList<VortexAgentNetAddress>();
		final List<String> lines = this.windowsProcessMonitor.runIpAddr();

		StringBuilder sink = new StringBuilder();
		StringCharReader reader = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		Map<String, String> map = new HashMap<String, String>();
		if (lines.size() == 0) {
			LH.info(log, "Not enough lines in the WMI response, expecting at least 1 not: " + lines.size());
			return r;
		}
		for (String line : lines) {
			try {

				getValues(map, reader, sink, line);

				final VortexAgentNetAddress l = state.nw(VortexAgentNetAddress.class);
				setValues(map, l);
				r.add(l);

			} catch (Exception e) {
				LH.warning(log, "Error parsing 'ipAddr' ", e);
				return r;
			}
		}
		return r;
	}

	@Override
	public List<VortexAgentNetLink> inspectNetLinks(VortexAgentOsAdapterState state) {
		List<VortexAgentNetLink> r = new ArrayList<VortexAgentNetLink>();
		StringBuilder sink = new StringBuilder();
		StringCharReader reader = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		Map<String, String> map = new HashMap<String, String>();

		final List<String> lines = this.windowsProcessMonitor.runIpLink();

		for (String line : lines) {
			try {
				getValues(map, reader, sink, line);

				final VortexAgentNetLink l = state.nw(VortexAgentNetLink.class);
				setValues(map, l);

				r.add(l);

			} catch (Exception e) {
				LH.warning(log, "Error parsing 'ipLink' ", e);
				return r;
			}
		}

		return r;
	}

	@Override
	public List<VortexAgentNetConnection> inspectNetConnections(VortexAgentOsAdapterState state) {
		List<VortexAgentNetConnection> r = new ArrayList<VortexAgentNetConnection>();
		StringBuilder sink = new StringBuilder();
		StringCharReader reader = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		Map<String, String> map = new HashMap<String, String>();
		final List<String> lines = this.windowsProcessMonitor.runLsof();
		for (String line : lines) {

			try {

				getValues(map, reader, sink, line);

				final VortexAgentNetConnection netConnection = state.nw(VortexAgentNetConnection.class);
				setValues(map, netConnection);
				r.add(netConnection);
			} catch (Exception e) {
				LH.warning(log, "Error parsing 'netStat' ", e);
				return r;
			}
		}
		return r;
	}

	@Override
	public List<VortexAgentProcess> inspectProcesses(VortexAgentOsAdapterState state) {
		final List<VortexAgentProcess> r = new ArrayList<VortexAgentProcess>();
		final List<String> lines = this.windowsProcessMonitor.runPs();

		StringBuilder sink = new StringBuilder();
		StringCharReader reader = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		Map<String, String> map = new HashMap<String, String>();

		//		final StringBuilder sb = new StringBuilder();
		for (int i = 1; i < lines.size(); i++) {
			final String line = lines.get(i);
			if (SH.isnt(line))
				continue;

			try {

				getValues(map, reader, sink, line);

				final VortexAgentProcess ap = state.nw(VortexAgentProcess.class);
				setValues(map, ap);
				r.add(ap);
			} catch (Exception e) {
				LH.warning(log, "Error parsing 'ps' line: ", line, e);
			}
		}

		return r;
	}

	@Override
	public long runLastReboot(VortexAgentOsAdapterState state) {
		final List<String> lines = this.windowsProcessMonitor.runLastReboot();
		if (lines.size() != 1)
			throw new RuntimeException("not enough lines, expecting 1 not: " + lines.size());

		try {
			return Long.parseLong(lines.get(0));
		} catch (Exception e) {
			LH.warning(log, "Error parsing 'lastReboot' ", e);
			return 0;
		}

	}

	public List<VortexAgentFileSystem> inspectFileSystems(VortexAgentOsAdapterState state) {

		final List<VortexAgentFileSystem> fileSystems = new ArrayList<VortexAgentFileSystem>();
		final List<String> lines = this.windowsProcessMonitor.runDf();
		StringBuilder sink = new StringBuilder();
		StringCharReader reader = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < lines.size(); i++) {
			final String line = lines.get(i);
			try {
				//if (lines.size() != 1)
				//	throw new RuntimeException("not enough lines, expecting 1 not: " + lines.size());
				getValues(map, reader, sink, line);
				final VortexAgentFileSystem msg = state.nw(VortexAgentFileSystem.class);
				setValues(map, msg);
				fileSystems.add(msg);

			} catch (Exception e) {
				LH.warning(log, "Error parsing 'df ' cmd", e);
			}
		}
		return fileSystems;
	}
	public void runFree(VortexAgentOsAdapterState state, VortexAgentMachine msg) {
		final List<String> lines = this.windowsProcessMonitor.runFree();

		StringBuilder sink = new StringBuilder();
		StringCharReader reader = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		Map<String, String> map = new HashMap<String, String>();

		try {
			if (lines.size() != 1)
				throw new RuntimeException("not enough lines, expecting 1 not: " + lines.size());
			getValues(map, reader, sink, lines.get(0));
			setValues(map, msg);

		} catch (Exception e) {
			LH.warning(log, "Error parsing 'free' cmd", e);
		}
	}
	public VortexAgentMachine inspectMachine(VortexAgentOsAdapterState state) throws IOException {
		VortexAgentMachine mach = super.inspectMachine(state);
		mach.setAgentProcessUid(EH.getProcessUid());
		final List<String> lines = this.windowsProcessMonitor.runDetails();

		StringBuilder sink = new StringBuilder();
		StringCharReader reader = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		Map<String, String> map = new HashMap<String, String>();

		try {
			if (lines.size() != 1)
				throw new RuntimeException("Wrong Line count..expecting 1 not: " + lines.size());

			getValues(map, reader, sink, lines.get(0));

			setValues(map, mach);

		} catch (Exception e) {
			LH.warning(log, "Error parsing 'details' cmd", e);
			return mach;
		}

		return mach;
	}

	@Override
	public String getHostName(VortexAgentOsAdapterState state) throws IOException {
		return this.windowsProcessMonitor.getHostName();
	}

	@Override
	public String getMachineUid(VortexAgentOsAdapterState state) throws IOException {
		final String machineuid = GuidHelper.getGuid(62);
		return this.windowsProcessMonitor.getMachineUid(machineuid);

	}

	/*private Tuple3<Process, byte[], byte[]> assertExitCode(Tuple3<Process, byte[], byte[]> result) {
		if (result.getA().exitValue() != 0)
			throw new RuntimeException("exit code " + result.getA().exitValue() + ": " + new String(result.getC()));
		return result;
	}*/

	protected String sudo() {

		return " ";
	}

	protected Tuple3<Process, byte[], byte[]> execute(VortexAgentOsAdapterState state, String command) {
		if (log.isLoggable(Level.FINE))
			LH.fine(log, command);
		Tuple3<Process, byte[], byte[]> process = EH.exec(state.getPartition().getContainer().getThreadPoolController(), SH.splitContinous(' ', command));
		if (log.isLoggable(Level.FINE)) {
			//		LH.fine(log,"STDOUT\n" + new String(process.getB()));
			LH.fine(log, "STDERR\n" + new String(process.getC()));
		}
		return process;
	}

	@Override
	public List<VortexAgentMachineEventStats> inspectMachineEvents(VortexAgentOsAdapterState state, long onwards, byte level) {

		//		long id = getTools().getUidLong("AgentMachineEventStats");
		final List<VortexAgentMachineEventStats> stats = new ArrayList<VortexAgentMachineEventStats>();
		final List<String> lines = this.windowsProcessMonitor.runMachineEventStats(onwards, level);

		if (lines.size() == 0)
			return stats;

		StringBuilder sink = new StringBuilder();
		StringCharReader reader = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		Map<String, String> map = new HashMap<String, String>();

		try {
			for (String line : lines) {
				getValues(map, reader, sink, line);
				final VortexAgentMachineEventStats l = state.nw(VortexAgentMachineEventStats.class);
				setValues(map, l);
				stats.add(l);
			}

		} catch (Exception e) {
			LH.warning(log, "Error parsing 'eventStats' ", e);
			return stats;
		}

		return stats;
	}

	@Override
	public List<VortexAgentCron> inspectCron(VortexAgentOsAdapterState state) {

		List<VortexAgentCron> crons = new ArrayList<VortexAgentCron>();

		return crons;
	}

	private static final int[] EOFCHAR = new int[] { ',', CharReader.EOF };

	private static Map<String, String> getValues(Map<String, String> map, StringCharReader reader, StringBuilder sink, String line) {
		if (map == null || reader == null || sink == null || line == null)
			throw new IllegalArgumentException();

		map.clear();
		reader.reset(line);

		for (;;) {
			reader.expect('\'');
			reader.readUntil('=', SH.clear(sink));
			String key = sink.toString();
			reader.expectSequence("=");
			reader.readUntil('\'', '\\', SH.clear(sink));
			reader.expect('\'');

			map.put(key, sink.toString().replace("\\n", "\n"));

			int c = reader.expectAny(EOFCHAR);
			if (c == CharReader.EOF)
				break;
		}

		return map;
	}

	private void setValues(Map<String, String> map, Valued msg) {
		ValuedParam<Valued>[] params = msg.askSchema().askValuedParams();

		for (ValuedParam<Valued> param : params) {
			String value = map.get(param.getName());
			if (value != null) {
				param.setValue(msg, param.getCaster().cast(value));
			}
		}
	}
}
