package com.vortex.agent.osadapter.linux;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.utils.AH;
import com.f1.utils.CharReader;
import com.f1.utils.EH;
import com.f1.utils.GuidHelper;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;
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
import com.vortex.agent.osadapter.VortexAgentAbstractOsAdapter;
import com.vortex.agent.state.VortexAgentOsAdapterState;

public class VortexAgentLinuxOsAdapter extends VortexAgentAbstractOsAdapter {

	private final boolean hasRootAccess;

	public VortexAgentLinuxOsAdapter(boolean hasRootAccess) {
		this.hasRootAccess = hasRootAccess;

	}
	private static final Logger log = LH.get(VortexAgentLinuxOsAdapter.class);

	public static final Map<String, Short> NETLINKSTATES = new HashMap<String, Short>();
	public static final Map<String, Byte> NETADDRESSSCOPES = new HashMap<String, Byte>();
	static {
		NETLINKSTATES.put("ALLMULTI", VortexAgentNetLink.STATE_ALLMULTI);
		NETLINKSTATES.put("BROADCAST", VortexAgentNetLink.STATE_BROADCAST);
		NETLINKSTATES.put("DYNAMIC", VortexAgentNetLink.STATE_DYNAMIC);
		NETLINKSTATES.put("LOOPBACK", VortexAgentNetLink.STATE_LOOPBACK);
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

	/*public static final String COMMAND_RUNNER = "COMMAND_RUNNER";

	public static LinuxCommandRunner get(AbstractContainerScope abs) {
		return (LinuxCommandRunner) abs.getServices().getService(COMMAND_RUNNER);
	}

	*/
	//static private Tuple2<String, Integer> splitHostPort(String address) {
	//String port = SH.afterLast(address, ':');
	//String host = SH.beforeLast(address, ':');
	//Tuple2<String, Integer> r = new Tuple2<String, Integer>();
	//host = SH.afterLast(host, ':');
	//r.setAB(host, "*".equals(port) ? -1 : Integer.parseInt(port));
	//return r;
	//}

	@Override
	public List<VortexAgentNetAddress> inspectNetAddresses(VortexAgentOsAdapterState state) {
		List<VortexAgentNetAddress> r = new ArrayList<VortexAgentNetAddress>();
		String stdout = new String(execute(state, sudo() + "/sbin/ip addr show").getB()).trim();

		String[] lines = SH.splitLines(stdout);
		StringBuilder sb = new StringBuilder();
		StringCharReader sr = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		for (int i = 0; i < lines.length;) {
			sr.reset(lines[i++]);// skip next line
			sr.readUntil(':', SH.clear(sb));
			sr.expect(':');
			final int idx = Integer.parseInt(sb.toString());

			sr.skip(' ');
			sr.readUntil(':', SH.clear(sb));
			sr.expect(':');
			final String name = sb.toString();

			try {
				while (i < lines.length && lines[i].startsWith(" ")) {
					final String line = lines[i++].trim();
					String[] parts = SH.splitContinous(' ', line);
					String type = parts[0], scope = null, address = null, broadcast = null;
					byte type2;
					if (type.equals("inet6")) {
						type2 = (VortexAgentNetAddress.TYPE_INET6);
					} else if (type.equals("inet")) {
						type2 = (VortexAgentNetAddress.TYPE_INET);
					} else
						continue;
					final VortexAgentNetAddress l = state.nw(VortexAgentNetAddress.class);
					address = SH.beforeFirst(parts[1], '/');
					l.setType(type2);
					int idx2 = AH.indexOf("brd", parts);
					if (idx2 != -1)
						broadcast = parts[idx2 + 1];
					idx2 = AH.indexOf("scope", parts);
					if (idx2 != -1)
						scope = parts[idx2 + 1];
					Byte scopeMask = NETADDRESSSCOPES.get(scope);
					if (scopeMask != null)
						l.setScope(scopeMask);
					if (address != null)
						l.setAddress(address);
					if (broadcast != null)
						l.setBroadcast(broadcast);

					l.setLinkName(name);
					r.add(l);

				}

			} catch (Exception e) {
				LH.warning(log, "Error with lines: ", stdout);
			}
		}
		return r;
	}

	@Override
	public List<VortexAgentNetLink> inspectNetLinks(VortexAgentOsAdapterState state) {
		List<VortexAgentNetLink> r = new ArrayList<VortexAgentNetLink>();
		String stdout = new String(execute(state, sudo() + "/sbin/ip -s link show").getB()).trim();

		String[] lines = SH.splitLines(stdout);
		if (lines.length % 6 != 0)
			throw new RuntimeException("expecting number of lines to be a multiple of 6 (not " + lines.length + "): " + stdout);
		StringBuilder sb = new StringBuilder();
		StringCharReader sr = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		for (int i = 0; i < lines.length; i += 6) {
			try {
				sr.reset(lines[i]);
				sr.readUntil(':', SH.clear(sb));
				sr.expect(':');
				final int idx = Integer.parseInt(sb.toString());

				sr.skip(' ');
				sr.readUntil(':', SH.clear(sb));
				sr.expect(':');
				final String name = sb.toString();

				sr.skip(' ');
				sr.expect('<');
				sr.readUntil('>', SH.clear(sb));
				sr.expect('>');
				final String lstate = sb.toString();

				sr.skip(' ');
				sr.expectSequence("mtu ");
				sr.skip(' ');
				sr.readUntil(' ', SH.clear(sb));
				long mtu = Long.parseLong(sb.toString());

				sr.skip(' ');
				sr.readUntil(CharReader.EOF, SH.clear(sb));
				final String transmissionDetails = sb.toString();

				sr.reset(lines[i + 1]);

				sr.skip(' ');
				sr.expectSequence("link/");
				sr.readUntil(' ', SH.clear(sb));
				final String type = sb.toString();
				String broadcast = "";
				String mac = "";

				if ("ether".equals(type) || "loopback".equals(type)) {
					sr.skip(' ');
					sr.readUntil(' ', SH.clear(sb));
					mac = sb.toString();

					sr.skip(' ');
					sr.expectSequence("brd");
					sr.skip(' ');
					sr.readUntil(' ', SH.clear(sb));
					broadcast = sb.toString();
				}
				VortexAgentNetLink l = state.nw(VortexAgentNetLink.class);

				l.setBroadcast(broadcast);
				l.setIndex(idx);
				l.setName(name);
				l.setMac(mac);
				short lstateMask = 0;
				for (String s : SH.split(',', lstate)) {
					Short mask = NETLINKSTATES.get(s);
					if (mask != null)
						lstateMask |= mask;
				}
				l.setState(lstateMask);
				l.setMtu(mtu);
				l.setTransmissionDetails(transmissionDetails);
				{
					final String[] header = SH.splitContinous(' ', lines[i + 2].trim());
					final String[] data = SH.splitContinous(' ', lines[i + 3].trim());
					OH.assertEq("RX:", header[0]);
					l.setRxPackets(Long.parseLong(data[AH.indexOf("packets", header) - 1]));
					l.setRxErrors(Long.parseLong(data[AH.indexOf("errors", header) - 1]));
					l.setRxDropped(Long.parseLong(data[AH.indexOf("dropped", header) - 1]));
					l.setRxOverrun(Long.parseLong(data[AH.indexOf("overrun", header) - 1]));
					l.setRxMulticast(Long.parseLong(data[AH.indexOf("mcast", header) - 1]));
				}
				{
					final String[] header = SH.splitContinous(' ', lines[i + 4].trim());
					final String[] data = SH.splitContinous(' ', lines[i + 5].trim());
					OH.assertEq("TX:", header[0]);
					l.setTxPackets(Long.parseLong(data[AH.indexOf("packets", header) - 1]));
					l.setTxErrors(Long.parseLong(data[AH.indexOf("errors", header) - 1]));
					l.setTxDropped(Long.parseLong(data[AH.indexOf("dropped", header) - 1]));
					l.setTxCarrier(Long.parseLong(data[AH.indexOf("carrier", header) - 1]));
					l.setTxCollsns(Long.parseLong(data[AH.indexOf("collsns", header) - 1]));
				}
				r.add(l);
			} catch (Exception e) {
				LH.warning(log, "Error with lines: ", SH.join(SH.NEWLINE, Arrays.copyOfRange((Object[]) lines, i, i + 6)), e);
			}
		}
		return r;
	}

	@Override
	public List<VortexAgentNetConnection> inspectNetConnections(VortexAgentOsAdapterState state) {
		String stdout = new String(execute(state, sudo() + "/usr/sbin/ss -a -p -n -t").getB());
		final List<VortexAgentNetConnection> r = new ArrayList<VortexAgentNetConnection>();
		String[] lines = SH.splitLines(stdout);
		String header[] = SH.splitContinous(' ', lines[0]);
		//int stateIdx = AH.indexOf("State", header);
		//int localId = AH.indexOf("Local Address", header);
		//int peerId = AH.indexOf("PPER", header);
		//int nameIdx = AH.indexOf("NAME", header);
		//int typeIdx = AH.indexOf("TYPE", header);
		for (int i = 1; i < lines.length; i++) {
			final String line = lines[i];
			try {
				if (SH.isnt(line))
					continue;
				final String parts[] = SH.splitContinous(' ', line);
				String netState = parts[0];
				String local = AH.getOr(parts, 3, "");
				String remote = AH.getOr(parts, 4, "");
				String processText = AH.getOr(parts, 5, null);
				if (netState == null)
					continue;
				netState = SH.trim('(', ')', netState);
				VortexAgentNetConnection netConnection = state.nw(VortexAgentNetConnection.class);
				if ("LISTEN".equals(netState))
					netConnection.setState(VortexAgentNetConnection.STATE_LISTEN);
				else if ("ESTAB".equals(netState))
					netConnection.setState(VortexAgentNetConnection.STATE_ESTABLISHED);
				else if ("CLOSE-WAIT".equals(netState))
					netConnection.setState(VortexAgentNetConnection.STATE_CLOSE_WAIT);
				else if ("FIN-WAIT-2".equals(netState))
					netConnection.setState(VortexAgentNetConnection.STATE_FIN_WAIT2);
				else if ("FIN-WAIT-1".equals(netState))
					netConnection.setState(VortexAgentNetConnection.STATE_FIN_WAIT1);
				else if ("CLOSING".equals(netState))
					netConnection.setState(VortexAgentNetConnection.STATE_CLOSING);
				else if ("LAST-ACK".equals(netState))
					netConnection.setState(VortexAgentNetConnection.STATE_LAST_ACK);
				else if ("SYN-RECV".equals(netState))
					netConnection.setState(VortexAgentNetConnection.STATE_SYN_RECV);
				else if ("TIME-WAIT".equals(netState)) {
					continue;//too many of these!
					//netConnection.setState(AgentNetConnection.STATE_TIME_WAIT);
				} else {
					LH.info(log, "Uknown state: " + line);
					continue;
				}
				String localAddress = SH.beforeLast(local, ":", null);
				String localPort = SH.afterLast(local, ":", null);
				String remoteAddress = SH.beforeLast(remote, ":", null);
				String remotePort = SH.afterLast(remote, ":", null);
				if (SH.isnt(localPort) || SH.isnt(remotePort)) {
					LH.warning(log, "Invalid line: ", line);
					continue;
				}
				final Tuple2<String, Integer> la = splitHostPort(local);
				final Tuple2<String, Integer> ra = splitHostPort(remote);
				netConnection.setLocalHost(la.getA());
				netConnection.setLocalPort(la.getB());
				netConnection.setForeignHost(ra.getA());
				netConnection.setForeignPort(ra.getB());

				if (SH.is(processText)) {
					String cleanedProcessText = SH.strip(processText, "users:((", "))", true);
					String[] processParts = SH.split(',', cleanedProcessText);
					netConnection.setLocalAppName(SH.trim('"', processParts[0]));
					netConnection.setLocalPid(processParts[1]);
				}
				r.add(netConnection);
			} catch (Exception e) {
				LH.warning(log, "Error with 'ss' line: ", line, e);
			}
		}
		return r;
	}

	@Override
	public List<VortexAgentProcess> inspectProcesses(VortexAgentOsAdapterState state) {
		final List<VortexAgentProcess> r = new ArrayList<VortexAgentProcess>();
		String command = "ps -eo pcpu,rss,ruser,pid,ppid,lstart,cmd";
		final String stdout = new String(execute(state, sudo() + "" + command).getB());
		final String[] lines = SH.splitLines(stdout);
		final StringBuilder sb = new StringBuilder();
		for (int i = 1; i < lines.length; i++) {
			final String line = lines[i];
			if (SH.isnt(line))
				continue;
			try {
				final VortexAgentProcess ap = state.nw(VortexAgentProcess.class);
				final StringCharReader reader = new StringCharReader(line);
				reader.skip(' ');

				reader.readUntil(' ', SH.clear(sb));
				ap.setCpuPercent(Double.parseDouble(sb.toString()) / 100);
				reader.skip(' ');

				reader.readUntil(' ', SH.clear(sb));
				ap.setMemory(1024 * Long.parseLong(sb.toString()));
				reader.skip(' ');

				reader.readUntil(' ', SH.clear(sb));
				ap.setUser(sb.toString());
				reader.skip(' ');

				reader.readUntil(' ', SH.clear(sb));
				ap.setPid(sb.toString());
				reader.skip(' ');

				reader.readUntil(' ', SH.clear(sb));
				ap.setParentPid(sb.toString());
				reader.skip(' ');

				reader.readChars(24, SH.clear(sb));
				Date date = state.parseSystemDate(sb.toString());
				ap.setStartTime(date.getTime());

				reader.skip(' ');
				reader.readUntil(CharReader.EOF, SH.clear(sb));
				ap.setCommand(sb.toString());
				if (ap.getCommand().contains(command))
					continue;
				r.add(ap);
			} catch (Exception e) {
				LH.warning(log, "Error parsing 'ps' line: ", line, e);
			}
		}

		return r;
	}

	@Override
	public long runLastReboot(VortexAgentOsAdapterState state) {
		final String stdout = new String(execute(state, sudo() + "/usr/bin/last reboot -1 -Fd -a").getB());
		String line = SH.beforeFirst(stdout, '\n');
		if (SH.isnt(line))
			return 0;
		try {
			final StringCharReader reader = new StringCharReader(line);
			reader.expectSequence("reboot".toCharArray());
			reader.skip(' ');
			reader.expectSequence("system boot".toCharArray());
			reader.skip(' ');
			final StringBuilder sb = new StringBuilder();
			reader.readChars(24, sb);
			return Date.parse(sb.toString());
		} catch (Exception e) {
			LH.warning(log, "Error parsing 'last reboot' cmd: ", stdout, e);
			return 0;
		}

	}

	public List<VortexAgentFileSystem> inspectFileSystems(VortexAgentOsAdapterState state) {
		final String stdout = new String(execute(state, sudo() + "/bin/df -B1 -T").getB());
		final String[] lines = SH.trimArray(SH.splitLines(stdout));
		if (!lines[0].startsWith("Filesystem"))
			LH.warning(log, "unexpected header for df: ", lines[0]);
		final List<VortexAgentFileSystem> fileSystems = new ArrayList<VortexAgentFileSystem>();
		for (int i = 1; i < lines.length; i++) {
			boolean twoLines = false;
			try {
				String[] parts = SH.splitContinous(' ', lines[i]);
				if (parts.length == 1) {//it wrapped to 2nd line
					String[] parts2 = SH.splitContinous(' ', lines[++i]);
					parts = AH.cat(parts, parts2);
					twoLines = true;
				}
				VortexAgentFileSystem fs = state.nw(VortexAgentFileSystem.class);
				String name = parts[0];
				String type = parts[1];
				long totalSpace = Long.parseLong(parts[2]);
				//long usedSpace = Long.parseLong(parts[3]);
				long freeSpace = Long.parseLong(parts[4]);
				fs.setFreeSpace(freeSpace);
				fs.setUsableSpace(totalSpace);
				fs.setTotalSpace(totalSpace);
				fs.setName(name);
				fs.setType(type);
				fileSystems.add(fs);
			} catch (Exception e) {
				if (twoLines)
					LH.warning(log, "Error parsing 2 lines of 'df' result: ", lines[i - 1], SH.NEWLINE, lines[i], e);
				else
					LH.warning(log, "Error parsing line of 'df' result: ", lines[i], e);
			}
		}
		return fileSystems;
	}
	public void runFree(VortexAgentOsAdapterState state, VortexAgentMachine msg) {

		//AgentMachine msg = state.nw(AgentMachine.class);
		String stdout = "";
		try {
			stdout = new String(execute(state, sudo() + "/usr/bin/free -bo").getB());
			final String[] lines = SH.splitLines(stdout);
			if (lines.length != 3)
				throw new RuntimeException("not enough lines, expecting 3 not: " + lines.length);
			String[] mem = SH.splitContinous(' ', lines[1]);
			String[] swp = SH.splitContinous(' ', lines[2]);
			msg.setTotalMemory(Long.parseLong(mem[1]));
			msg.setUsedMemory(Long.parseLong(mem[2]));
			msg.setTotalSwapMemory(Long.parseLong(swp[1]));
			msg.setUsedSwapMemory(Long.parseLong(swp[2]));
		} catch (Exception e) {
			LH.warning(log, "Error parsing 'free' cmd: ", stdout, e);
			//return null;
		}
		//OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		//double systemLoadAvg = os.getSystemLoadAverage();
		//msg.setSystemLoadAverage(systemLoadAvg);

		//final String stdout2 = new String(execute(state, sudo() + "mpstat", new File("."), "-P ALL").getB());
		//try {
		//final String[] lines = SH.splitLines(stdout2);
		//for (String line : lines) {
		//String[] parts = SH.splitContinous(' ', line);
		//if (parts.length == 12) {
		//if ("all".equals(parts[2])) {
		//double pctIdle = Double.parseDouble(parts[11]);
		//msg.setSystemLoadAverage((100 - pctIdle) / 100);
		//}
		//}
		//}
		//} catch (Exception e) {
		//LH.warning(log, "Error parsing 'mpstat' cmd: ", stdout, e);
		//}
		final String stdout2 = new String(execute(state, sudo() + "/usr/bin/uptime").getB());
		msg.setSystemLoadAverage(Double.parseDouble(SH.trim(SH.beforeFirst(SH.afterLast(stdout2, ':'), ','))));

		//state.addPing(msg, EH.currentTimeMillis());
		//return msg;
	}

	public VortexAgentMachine inspectMachine(VortexAgentOsAdapterState state) throws IOException {
		VortexAgentMachine r = super.inspectMachine(state);
		r.setAgentProcessUid(EH.getProcessUid());
		Tuple3<Process, byte[], byte[]> result = execute(state, sudo() + "/bin/cat /proc/cpuinfo");
		String stdout = new String(result.getB());
		String[] lines = SH.splitLines(stdout);
		for (String line : lines) {
			if (line.startsWith("model name")) {
				String modelName = SH.afterFirst(line, ':', null);
				if (modelName != null) {
					modelName = modelName.trim().replaceAll(" +", " ");
					r.setOsArchitecture(modelName);
				}
			}
		}
		return r;
	}

	@Override
	public String getHostName(VortexAgentOsAdapterState state) throws IOException {
		return new String(execute(state, sudo() + "/bin/hostname").getB()).trim();
	}

	@Override
	public String getMachineUid(VortexAgentOsAdapterState state) throws IOException {
		final File file = state.getPartition().getContainer().getTools().getOptional("f1.agent.uidfile", new File("/etc/3forge/machineuid.txt"));
		Tuple3<Process, byte[], byte[]> result = execute(state, sudo() + "/bin/cat " + file.getAbsolutePath());
		String r = null;
		if (result.getA().exitValue() == 1) {
			final String machineuid = GuidHelper.getGuid(62);
			final File tmpFile = File.createTempFile("machineuid", "");
			IOH.writeText(tmpFile, machineuid + SH.NEWLINE);
			assertExitCode(execute(state, sudo() + "/bin/mkdir -p " + file.getParentFile().getAbsolutePath()));
			assertExitCode(execute(state, sudo() + "cp " + tmpFile.getAbsolutePath() + " " + file.getAbsolutePath()));
			tmpFile.delete();
			assertExitCode(execute(state, sudo() + "/bin/chmod 444 " + file.getAbsolutePath()));
			result = execute(state, sudo() + "/bin/cat " + file.getAbsolutePath());
			assertExitCode(result);
			r = new String(result.getB());
		}
		r = new String(result.getB());
		for (char c : "\n\r ".toCharArray())
			r = SH.trim(c, r);
		if (SH.isnt(r))
			throw new RuntimeException("blank machine name");
		return r;

	}
	/*private Tuple3<Process, byte[], byte[]> assertExitCode(Tuple3<Process, byte[], byte[]> result) {
		if (result.getA().exitValue() != 0)
			throw new RuntimeException("exit code " + result.getA().exitValue() + ": " + new String(result.getC()));
		return result;
	}*/

	protected String sudo() {
		if (!hasRootAccess)
			return "";
		if ("root".equals(EH.getUserName()))
			return "";
		return "/usr/bin/sudo ";
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
		final String toExecute = sudo() + "/usr/bin/last -Fd";
		final String stdout = new String(execute(state, toExecute).getB());
		final StringBuilder sb = new StringBuilder();
		String[] lines = SH.splitLines(stdout);
		String line;
		for (int i = 0; i < lines.length; i++) {

			line = lines[i];
			if (SH.isnt(line))
				continue;
			try {

				final StringCharReader reader = new StringCharReader(line);
				VortexAgentMachineEventStats stat = state.nw(VortexAgentMachineEventStats.class);

				reader.skip(' ');
				reader.readUntil(' ', SH.clear(sb));
				String name = sb.toString();
				if ("wtmp".equals(name))
					continue;

				reader.skip(' ');
				reader.readUntil(' ', SH.clear(sb));
				String msg = sb.toString();

				if ("system".equals(msg)) {
					reader.skip(' ');
					reader.readUntil(' ', SH.clear(sb));
					msg += " " + sb.toString();
				}

				reader.skip(' ');
				reader.readUntil(' ', SH.clear(sb));
				String host = sb.toString();
				Date date;
				long gen = 0;
				try {
					reader.skip(' ');
					reader.readChars(24, SH.clear(sb));
					date = state.parseSystemDate(sb.toString());
					gen = date.getTime();
				} catch (ParseException pe) {
					LH.warning(log, "Error parsing date: ", sb.toString());
				}

				reader.skip(' ');
				reader.readUntil(' ', SH.clear(sb));
				String qualifier = sb.toString();
				if ("-".equals(qualifier)) {
					reader.skip(' ');
					reader.mark();
					reader.readChars(24, SH.clear(sb));
					try {
						date = state.parseSystemDate(sb.toString());
						long end = date.getTime();
						stat.setEndTime(end);
					} catch (ParseException pe) {
						reader.returnToMark();
						reader.readUntil('(', SH.clear(sb));
						stat.setNotEnded(sb.toString());
					}
					BitSet set = new BitSet();
					reader.skip(StringCharReader.WHITE_SPACE);
					reader.expect('(');
					reader.readUntil(')', SH.clear(sb));
					stat.setDuration(sb.toString());
				} else {
					reader.readUntil('\n', sb);
					qualifier = sb.toString();
					stat.setNotEnded(qualifier);
				}

				stat.setLevel((byte) 6);
				stat.setTimeGenerated(gen);
				stat.setHost(host);
				//				stat.setId(id);
				stat.setMessage(msg);
				stat.setName("UNIX");
				stat.setUserName(name);
				stat.setSource("wtmp");

				stats.add(stat);
			} catch (Exception e) {
				LH.warning(log, "Error parsing 'last' cmd: ", stdout, e);
				return stats;
			}
		}

		return stats;
	}

	@Override
	public List<VortexAgentCron> inspectCron(VortexAgentOsAdapterState state) {
		Tuple3<Process, byte[], byte[]> lsresult = execute(state, sudo() + "ls /var/spool/cron");
		if (lsresult.getA().exitValue() != 0)
			return Collections.EMPTY_LIST;
		List<VortexAgentCron> crons = new ArrayList<VortexAgentCron>();
		for (String filename : SH.splitLines(new String(lsresult.getB()))) {
			if (!SH.is(filename))
				continue;
			final String user = filename.trim();
			Tuple3<Process, byte[], byte[]> result = execute(state, sudo() + "/bin/cat /var/spool/cron/" + user);
			final String stdout = new String(result.getB());
			final StringBuilder sb = new StringBuilder();
			String[] lines = SH.splitLines(stdout);
			final StringCharReader reader = new StringCharReader();
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				line = SH.replaceAll(line, SH.CHAR_TAB, ' ');
				if (SH.isnt(line))
					continue;
				try {

					reader.reset(line);
					VortexAgentCron cron = state.nw(VortexAgentCron.class);
					cron.setUser(user);

					reader.skip(' ');
					reader.readUntil(' ', SH.clear(sb));
					cron.setMinute(sb.toString());

					reader.skip(' ');
					reader.readUntil(' ', SH.clear(sb));
					cron.setHour(sb.toString());

					reader.skip(' ');
					reader.readUntil(' ', SH.clear(sb));
					cron.setDayOfMonth(sb.toString());

					reader.skip(' ');
					reader.readUntil(' ', SH.clear(sb));
					cron.setMonth(sb.toString());

					reader.skip(' ');
					reader.readUntil(' ', SH.clear(sb));
					cron.setDayOfWeek(sb.toString());

					reader.skip(' ');
					reader.readUntil(CharReader.EOF, SH.clear(sb));
					cron.setCommand(sb.toString());

					cron.setTimeZone("" + TimeZone.getDefault().getOffset(System.currentTimeMillis()));
					cron.setSecond("0");
					//if (null != state.current.getMachine())
					//cron.setMachineInstanceId(state.current.getMachine().getMachineInstanceId());
					crons.add(cron);
				} catch (Exception e) {
					LH.warning(log, "Error parsing '~root/chkcron' cmd: ", stdout, e);
				}
			}
		}

		return crons;
	}
}
