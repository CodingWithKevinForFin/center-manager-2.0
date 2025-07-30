package com.vortex.testtrackagent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.utils.CharReader;
import com.f1.utils.GuidHelper;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;
import com.f1.vortexcommon.msg.agent.VortexAgentCron;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.agent.VortexAgentMachineEventStats;
import com.f1.vortexcommon.msg.agent.VortexAgentNetAddress;
import com.f1.vortexcommon.msg.agent.VortexAgentNetConnection;
import com.f1.vortexcommon.msg.agent.VortexAgentNetLink;
import com.f1.vortexcommon.msg.agent.VortexAgentProcess;

public class WindowsCommandRunner extends OSCommandRunner {

	private static final int[] EOFCHAR = new int[] { ',', CharReader.EOF };

	private WindowsProcessMonitor windowsProcessMonitor;

	public WindowsCommandRunner(PropertyController props) {
		this.windowsProcessMonitor = new WindowsProcessMonitor(props);
	}

	@Override
	public void stop() {
		super.stop();
		this.windowsProcessMonitor.dispose();
	};

	@Override
	public List<VortexAgentMachineEventStats> runMachineEventStats(CommandRunnerState state, long onwards, byte level) {
		List<VortexAgentMachineEventStats> r = new ArrayList<VortexAgentMachineEventStats>();
		final List<String> lines = this.windowsProcessMonitor.runMachineEventStats(onwards, level);
		if (lines.size() == 0)
			return r;

		StringBuilder sink = new StringBuilder();
		StringCharReader reader = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		Map<String, String> map = new HashMap<String, String>();

		try {
			for (String line : lines) {
				getValues(map, reader, sink, line);
				final VortexAgentMachineEventStats l = nw(VortexAgentMachineEventStats.class);
				setValues(map, l);
				r.add(l);
			}

		} catch (Exception e) {
			LH.warning(log, "Error parsing 'eventStats' ", e);
			return r;
		}

		return r;
	}

	@Override
	public List<VortexAgentNetAddress> runIpAddr(CommandRunnerState state) {
		List<VortexAgentNetAddress> r = new ArrayList<VortexAgentNetAddress>();
		final List<String> lines = this.windowsProcessMonitor.runIpAddr();

		StringBuilder sink = new StringBuilder();
		StringCharReader reader = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		Map<String, String> map = new HashMap<String, String>();

		try {
			if (lines.size() == 0) {
				LH.info(log, "not enough lines, expecting at leat 1 not: " + lines.size());
				return r;
			}

			getValues(map, reader, sink, lines.get(0));

			final VortexAgentNetAddress l = nw(VortexAgentNetAddress.class);
			setValues(map, l);
			r.add(l);

		} catch (Exception e) {
			LH.warning(log, "Error parsing 'ipAddr' ", e);
			return r;
		}

		return r;
	}

	@Override
	public List<VortexAgentNetLink> runIpLink(CommandRunnerState state) {
		List<VortexAgentNetLink> r = new ArrayList<VortexAgentNetLink>();
		StringBuilder sink = new StringBuilder();
		StringCharReader reader = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		Map<String, String> map = new HashMap<String, String>();

		final List<String> lines = this.windowsProcessMonitor.runIpLink();

		for (String line : lines) {
			try {
				getValues(map, reader, sink, line);

				final VortexAgentNetLink l = nw(VortexAgentNetLink.class);
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
	public List<VortexAgentNetConnection> runLsof(CommandRunnerState state) {
		List<VortexAgentNetConnection> r = new ArrayList<VortexAgentNetConnection>();
		StringBuilder sink = new StringBuilder();
		StringCharReader reader = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		Map<String, String> map = new HashMap<String, String>();
		final List<String> lines = this.windowsProcessMonitor.runLsof();
		for (String line : lines) {

			try {

				getValues(map, reader, sink, line);

				final VortexAgentNetConnection netConnection = nw(VortexAgentNetConnection.class);
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
	public List<VortexAgentProcess> runPs(CommandRunnerState state) {
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

				final VortexAgentProcess ap = nw(VortexAgentProcess.class);
				setValues(map, ap);
				r.add(ap);

			} catch (Exception e) {
				LH.warning(log, "Error parsing 'ps' line: ", line, e);
			}
		}

		return r;
	}

	@Override
	public long runLastReboot(CommandRunnerState state) {
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

	@Override
	public void runFree(CommandRunnerState state, VortexAgentMachine msg) {

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
	@Override
	public VortexAgentMachine runDetails(CommandRunnerState state) throws IOException {
		// TODO Auto-generated method stub

		VortexAgentMachine mach = super.runDetails(state);

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
	public String getHostName(CommandRunnerState state) throws IOException {
		return this.windowsProcessMonitor.getHostName();
	}

	@Override
	public String getMachineUid(CommandRunnerState state) {
		final String machineuid = GuidHelper.getGuid(62);
		return this.windowsProcessMonitor.getMachineUid(machineuid);
	}

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

	@Override
	public List<VortexAgentCron> runCron(CommandRunnerState state) {
		return new ArrayList<VortexAgentCron>();
	}
}
