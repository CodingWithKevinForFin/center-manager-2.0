package com.vortex.agent.osadapter.sun;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.agg.DoubleAggregator;
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

public class VortexAgentSunOsAdapter extends VortexAgentAbstractOsAdapter {
	private static final Logger log = LH.get(VortexAgentSunOsAdapter.class);

	public static final Map<String, Short> NETLINKSTATES = new HashMap<String, Short>();
	public static final Map<String, Byte> NETADDRESSSCOPES = new HashMap<String, Byte>();
	static {
		NETLINKSTATES.put("MULTICAST", VortexAgentNetLink.STATE_ALLMULTI);
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

	@Override
	public List<VortexAgentNetAddress> inspectNetAddresses(VortexAgentOsAdapterState state) {
		List<VortexAgentNetAddress> r = new ArrayList<VortexAgentNetAddress>();
		String stdout = new String(execute(state, sudo() + "ifconfig -a").getB()).trim();

		String[] lines = SH.splitLines(stdout);
		StringBuilder sb = new StringBuilder();
		StringCharReader sr = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		for (int i = 0; i < lines.length;) {
			sr.reset(lines[i++]);// skip next line
			sr.readUntil(':', SH.clear(sb));

			final String name = sb.toString();
			//	LH.warning(log, "Net name= " + name + "  "+ lines[i]);
			sr.expect(':');

			try {
				while (i < lines.length && (lines[i].startsWith(" ") || lines[i].startsWith("\t"))) {
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
					address = parts[1];
					if (address != null)
						l.setAddress(address);

					l.setType(type2);

					int idx2 = AH.indexOf("broadcast", parts);
					if (idx2 != -1)
						broadcast = parts[idx2 + 1];
					//	idx2 = AH.indexOf("scope", parts);
					//	if (idx2 != -1)
					//	scope = parts[idx2 + 1];
					//Byte scopeMask = NETADDRESSSCOPES.get(scope);
					//if (scopeMask != null)
					//	l.setScope(scopeMask);

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
		String stdout1 = new String(execute(state, sudo() + " dladm show-link -spo LINK,IPACKETS,RBYTES,IERRORS,OPACKETS,OBYTES,OERRORS ").getB()).trim();
		String stdout = new String(execute(state, sudo() + " ifconfig -a ").getB()).trim();

		Map<String, String> links = new HashMap<String, String>();

		String[] lines1 = SH.splitLines(stdout1);
		LH.warning(log, stdout1);
		for (String line : lines1) {
			String[] parts = SH.split(':', line);
			links.put(parts[0], line);
		}

		String[] lines = SH.splitLines(stdout);
		StringBuilder sb = new StringBuilder();
		StringCharReader sr = new StringCharReader(OH.EMPTY_CHAR_ARRAY);
		for (int i = 0; i < lines.length;) {
			sr.reset(lines[i++]);
			sr.readUntil(':', SH.clear(sb));

			final String name = sb.toString();
			String link = links.get(name);
			sr.readUntil('<', SH.clear(sb));
			sr.skipChars(1);
			sr.readUntil('>', SH.clear(sb));
			sr.skipChars(1);

			short lstateMask = 0;
			for (String s : SH.split(',', sb.toString())) {
				Short mask = NETLINKSTATES.get(s);
				if (mask != null)
					lstateMask |= mask;

			}
			sr.skip(' ');
			sr.expectSequence("mtu ");
			sr.skip(' ');
			sr.readUntil(' ', SH.clear(sb));
			long mtu = Long.parseLong(sb.toString());
			sr.skip(' ');
			sr.readUntil(CharReader.EOF, SH.clear(sb));
			final String transmissionDetails = sb.toString();

			String broadcast = null, mac = null;
			try {
				while (i < lines.length && (lines[i].startsWith(" ") || lines[i].startsWith("\t"))) {

					if (link == null) {
						i++;
						continue;
					}
					final String line = lines[i++].trim();
					String[] parts = SH.splitContinous(' ', line);
					String type = parts[0];

					if (type.equals("ether")) {
						mac = parts[1];
					} else
						continue;

					int idx2 = AH.indexOf("broadcast", parts);
					if (idx2 != -1)
						broadcast = parts[idx2 + 1];

				}
				if (link != null) {

					String[] parts = SH.split(':', link);
					VortexAgentNetLink l = state.nw(VortexAgentNetLink.class);
					l.setName(parts[0]);
					l.setRxPackets(SH.parseLong(parts[1]));
					l.setRxErrors(SH.parseLong(parts[3]));
					l.setTxPackets(SH.parseLong(parts[4]));
					l.setTxErrors(SH.parseLong(parts[6]));
					if (broadcast != null)
						l.setBroadcast(broadcast);
					if (mac != null)
						l.setMac(mac);

					l.setState(lstateMask);
					l.setTransmissionDetails(transmissionDetails);
					l.setMtu(mtu);
					r.add(l);

				}

			} catch (Exception e) {
				LH.warning(log, "Error with Net Links: ", sb.toString());
			}

		}
		return r;
	}

	@Override
	public List<VortexAgentNetConnection> inspectNetConnections(VortexAgentOsAdapterState state) {

		File[] dingbat = IOH.listFiles(new File("/proc"), false);

		String pids = SH.join(" ", (Object[]) dingbat);

		String stdout = new String(execute(state, sudo() + "/usr/bin/pfiles " + pids).getB());
		String stdout1 = new String(execute(state, sudo() + "/usr/bin/netstat -an -f inet -P tcp").getB());
		final List<VortexAgentNetConnection> r = new ArrayList<VortexAgentNetConnection>();
		String[] lines = SH.splitLines(stdout);
		String[] lines1 = SH.splitLines(stdout1);

		VortexAgentNetConnection netConnection = null;
		String currentPid = null, currentAppName = null;
		Map<String, String> pfiles = new HashMap<String, String>();
		Map<String, String> netstats = new HashMap<String, String>();

		for (int i = 1; i < lines.length; i++) {
			final String line = lines[i];

			try {
				if (SH.isnt(line))
					continue;
				final String parts[] = SH.split(':', line);
				if (line.charAt(0) != ' ' && line.charAt(0) != '\t') {

					//grab the pid and process path to be used by all the fd records.
					currentAppName = SH.trim(' ', parts[1]);
					currentPid = SH.trim(' ', parts[0]);
					//skip the next line
					i++;
				} else {

					int idx = line.indexOf("port");
					if (idx == -1)
						continue;
					String port = SH.trim(' ', parts[parts.length - 1]);
					pfiles.put(port, SH.join(':', currentAppName, currentPid));

				}

			} catch (Exception e) {
				LH.warning(log, "Error with 'pfiles' line: ", line, e);
			}
		}
		for (int i = 4; i < lines1.length; i++) {
			final String line1 = lines1[i];
			final String parts[] = SH.splitContinous(' ', SH.trim(line1));
			String localAddress = SH.beforeLast(parts[0], ".", null);
			String localPort = SH.afterLast(parts[0], ".", null);
			String remoteAddress = SH.beforeLast(parts[1], ".", null);
			String remotePort = SH.afterLast(parts[1], ".", null);
			String netstate = SH.trim(' ', parts[6]);
			//	LH.warning(log, localAddress," ",localPort, " ",remoteAddress," ",remotePort," ",netstate);
			netstats.put(localPort, SH.join(':', localAddress, localPort, remoteAddress, remotePort, netstate));
		}
		//	 Map<String, Tuple2<String, String>> netJoin = CH.join(netstat,pfiles);
		for (String key : netstats.keySet()) {
			netConnection = state.nw(VortexAgentNetConnection.class);
			String pfile = pfiles.get(key);
			String netstat = netstats.get(key);
			String[] parts = SH.split(':', pfile);
			String[] parts1 = SH.split(':', netstat);
			if (pfile != null) {
				netConnection.setLocalAppName(parts[0]);
				netConnection.setLocalPid(parts[1]);
			}
			netConnection.setLocalHost(parts1[0]);
			if (parts1[1].indexOf('*') == -1)
				netConnection.setLocalPort(SH.parseInt(parts1[1]));
			netConnection.setForeignHost(parts1[2]);

			if (parts1[3].indexOf('*') == -1)
				netConnection.setForeignPort(SH.parseInt(parts1[3]));
			if ("LISTEN".equals(parts1[4]))
				netConnection.setState(VortexAgentNetConnection.STATE_LISTEN);
			else if ("ESTABLISHED".equals(parts1[4]))
				netConnection.setState(VortexAgentNetConnection.STATE_ESTABLISHED);
			else if ("IDLE".equals(parts1[4]))
				netConnection.setState(VortexAgentNetConnection.STATE_CLOSE_WAIT);
			else if ("TIME_WAIT".equals(parts1[4])) {
				continue;//too many of these!
				//netConnection.setState(AgentNetConnection.STATE_TIME_WAIT);
			} else {
				LH.info(log, "Unknown state: " + parts1[4]);
				continue;
			}
			r.add(netConnection);

		}
		return r;
	}

	@Override
	public List<VortexAgentProcess> inspectProcesses(VortexAgentOsAdapterState state) {
		final List<VortexAgentProcess> r = new ArrayList<VortexAgentProcess>();
		String command = "ps -eo pcpu,rss,ruser,pid,ppid,etime,comm,args";
		long now = state.getPartition().getContainer().getTools().getNow();
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
				if ("-".equals(sb.toString()))
					continue;
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

				reader.readUntil(' ', SH.clear(sb));
				String elapsedTime = sb.toString();
				final int d, h, m, s;
				if (elapsedTime.indexOf('-') != -1) { //dd-hh:mm:ss
					final String hhmmss[] = SH.split(':', SH.afterFirst(elapsedTime, '-'));
					d = SH.parseInt(SH.beforeFirst(elapsedTime, '-'), 10);
					h = SH.parseInt(hhmmss[0], 10);
					m = SH.parseInt(hhmmss[1], 10);
					s = SH.parseInt(hhmmss[2], 10);
				} else if (elapsedTime.length() == 8) {//hh:mm:ss
					d = 0;
					final String hhmmss[] = SH.split(':', elapsedTime);
					h = SH.parseInt(hhmmss[0], 10);
					m = SH.parseInt(hhmmss[1], 10);
					s = SH.parseInt(hhmmss[2], 10);
				} else {//mm:ss
					d = 0;
					h = 0;
					final String mmss[] = SH.split(':', elapsedTime);
					m = SH.parseInt(mmss[0], 10);
					s = SH.parseInt(mmss[1], 10);
				}
				ap.setStartTime(MH.roundBy(now - ((((h * 24) + m) * 60) + s) * 1000, 2, MH.ROUND_HALF_EVEN));

				//reader.readChars(24, SH.clear(sb));
				//Date date = state.parseSystemDate(sb.toString());
				//ap.setStartTime(date.getTime());

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
		final String stdout = new String(execute(state, sudo() + "last reboot").getB());
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
			reader.readUntil(CharReader.EOF, sb);
			Date date = new SimpleDateFormat("yyyy EEE MMM dd HH:mm").parse("2013 " + sb.toString().trim());
			//reader.readChars(24, sb);
			//return Date.parse(sb.toString());
			return date.getTime();
		} catch (Exception e) {
			LH.warning(log, "Error parsing 'last reboot' cmd: ", stdout, e);
			return 0;
		}

	}

	public List<VortexAgentFileSystem> inspectFileSystems(VortexAgentOsAdapterState state) {
		final String stdout = new String(execute(state, sudo() + "df -B1 -T").getB());
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
			stdout = new String(execute(state, sudo() + "swap -s").getB());
			final String[] lines = SH.splitLines(stdout);
			if (lines.length != 1)
				throw new RuntimeException("not enough lines, expecting 1 not: " + lines.length);
			String line = lines[0];
			String parts[] = SH.splitContinous(' ', line);
			long allocated = Long.parseLong(SH.trim('k', parts[1])) * 1024;
			long reserved = Long.parseLong(SH.trim('k', parts[5])) * 1024;
			long used = Long.parseLong(SH.trim('k', parts[8])) * 1024;
			long available = Long.parseLong(SH.trim('k', parts[10])) * 1024;

			msg.setTotalMemory(available);
			msg.setUsedMemory(used);
			msg.setTotalSwapMemory(allocated + reserved);
			msg.setUsedSwapMemory(allocated);
		} catch (Exception e) {
			LH.warning(log, "Error parsing 'swap' cmd: ", stdout, e);
			//return null;
		}
		//OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
		//double systemLoadAvg = os.getSystemLoadAverage();
		//msg.setSystemLoadAverage(systemLoadAvg);

		final String stdout2 = new String(execute(state, sudo() + "mpstat").getB());
		try {
			final String[] lines = SH.splitLines(stdout2);
			DoubleAggregator da = new DoubleAggregator();
			for (String line : lines) {
				String[] parts = SH.splitContinous(' ', line.trim());
				if (parts.length == 16 && !parts[0].startsWith("CPU")) {
					double pctIdle = Double.parseDouble(parts[15]);
					da.add(pctIdle);
				}
			}
			msg.setSystemLoadAverage(1d - (da.getAverage() / 100d));
		} catch (Exception e) {
			LH.warning(log, "Error parsing 'mpstat' cmd: ", stdout, e);
		}

		//state.addPing(msg, EH.currentTimeMillis());
		//return msg;
	}

	public VortexAgentMachine inspectMachine(VortexAgentOsAdapterState state) throws IOException {
		VortexAgentMachine r = super.inspectMachine(state);
		r.setAgentProcessUid(EH.getProcessUid());
		Tuple3<Process, byte[], byte[]> result = execute(state, sudo() + "/usr/sbin/psrinfo -vp ");
		String stdout = new String(result.getB());
		//	LH.warning(log, "CPU arch= "  + stdout);
		stdout = SH.grep("!virtual", stdout);
		String[] lines = SH.splitLines(stdout);
		//		LH.warning(log, "CPU arch= "  + stdout);

		if (lines.length >= 0 && lines[0].isEmpty() == false)
			r.setOsArchitecture(lines[0]);

		return r;
	}

	@Override
	public String getHostName(VortexAgentOsAdapterState state) throws IOException {
		return new String(execute(state, sudo() + "hostname").getB()).trim();
	}

	@Override
	public String getMachineUid(VortexAgentOsAdapterState state) throws IOException {
		final File file = new File("/etc/3forge/machineuid.txt");
		Tuple3<Process, byte[], byte[]> result = execute(state, sudo() + "cat " + file.getAbsolutePath());
		String r = null;
		if (result.getA().exitValue() == 1) {
			final String machineuid = GuidHelper.getGuid(62);
			final File tmpFile = File.createTempFile("machineuid", "");
			IOH.writeText(tmpFile, machineuid + SH.NEWLINE);
			assertExitCode(execute(state, sudo() + "mkdir -p " + file.getParentFile().getAbsolutePath()));
			assertExitCode(execute(state, sudo() + "cp " + tmpFile.getAbsolutePath() + " " + file.getAbsolutePath()));
			tmpFile.delete();
			assertExitCode(execute(state, sudo() + "chmod 444 " + file.getAbsolutePath()));
			result = execute(state, sudo() + "cat " + file.getAbsolutePath());
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
		if ("root".equals(EH.getUserName()))
			return "";
		return "sudo ";
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
		final String toExecute = sudo() + "last -Fd";
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
			Tuple3<Process, byte[], byte[]> result = execute(state, sudo() + "cat /var/spool/cron/" + user);
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
